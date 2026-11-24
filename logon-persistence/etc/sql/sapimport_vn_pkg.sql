--------------------------------------------------------------------------------
--
-- PROJECT      : $Project: LOGON$
--
-- AUTHOR       : $Author: pdavid $
-- CREATION DATE: $Date$
-- DESCRIPTION  : Create VN loading package.
--
--------------------------------------------------------------------------------


CREATE OR REPLACE PACKAGE SAPIMPORT_VN AS

  -- phase 1 (general data and purchasing organisation data)

  PROCEDURE MAIN(i_min_loglevel IN VARCHAR2, i_days_try_import IN NUMBER);
  PROCEDURE check_partner_function(i_vendor_hjid IN NUMBER, i_sap_id IN VARCHAR, io_comm_carrier IN OUT NUMBER, io_exec_carrier IN OUT NUMBER);

  PROCEDURE load_tlgns_sap_vendor_porg(i_vendor_hjid IN NUMBER, i_sap_vendor_id IN NUMBER);
  
  PROCEDURE mark_missing_porg_as_deleted(i_vendor_hjid IN NUMBER, i_sap_vendor_id IN NUMBER);
  
  -- phase 2 (partner functions)
  
  PROCEDURE process_partner_functions;
  PROCEDURE get_sap_vendor_id(i_sap_id IN VARCHAR2, o_sap_vendor_id OUT NUMBER);
  PROCEDURE get_sap_vendor_porg_id(i_sap_vendor_id IN NUMBER, i_purchasingorg_cd IN VARCHAR2, o_sap_vendor_porg_id OUT NUMBER);
  PROCEDURE load_partner_functions(i_sap_vendor_porg_id_owner IN NUMBER, i_sap_vendor_porg_id_pf IN NUMBER, i_purchasingorg_cd IN VARCHAR2, i_partner_function IN VARCHAR);
  PROCEDURE delete_missing_pf(i_sap_vendor_porg_id IN NUMBER, i_porg_hjid IN NUMBER);

  -- cleanup: partner function flags
  PROCEDURE update_pf_flags;

END SAPIMPORT_VN;
/

SHOW ERRORS;

CREATE OR REPLACE PACKAGE BODY SAPIMPORT_VN AS

  ----------------------------------------------------------------
  ----------------------------------------------------------------
  --------------------VN-IMPORT-----------------------------------
  ----------------------------------------------------------------
  ----------------------------------------------------------------

  c_import_name             constant VARCHAR(30) := 'sapimport_vn';
  
  ----------------------------------------------------------------
  -- Statuses
  ----------------------------------------------------------------

  c_gen_initial             constant NUMBER :=  0; -- The notification has not been processed yet.
  c_gen_skipped             constant NUMBER :=  1; -- The notification has been skipped because a newer full update is available
  c_gen_failed              constant NUMBER :=  2; -- The notification could not be imported because an error occurred but it will be processed again during the next run.
  c_gen_blocked             constant NUMBER :=  3; -- The notification is blocked by an older notification that had failed.
  c_gen_blocked_final       constant NUMBER :=  8; -- The notification is blocked by an older notification that had failed and processing has been abandoned.
  c_gen_failed_final        constant NUMBER :=  9; -- The notification could not be imported because an error occurred and procesing has been abandoned.
  c_pf_initial              constant NUMBER := 10; -- The root element and the segments have been imported, ready for processing partner functions.
  c_pf_skipped              constant NUMBER := 11; -- The notification has been skipped because a newer update exists for all sales areas sent in this notification.
  c_pf_failed               constant NUMBER := 12; -- The notification could not be imported because an error occurred but it will be processed again during the next run.
  c_pf_failed_final         constant NUMBER := 19; -- The notification could not be imported because an error occurred and it won't be processed again.
  c_imported_to_logon       constant NUMBER := 20; -- The notification has been imported completely.

  c_function_code_delete    constant VARCHAR(4) := '003';
  c_function_code_change    constant VARCHAR(4) := '004';
  c_function_code_replace   constant VARCHAR(4) := '005';
  c_function_code_original  constant VARCHAR(4) := '009';
  c_function_code_resend    constant VARCHAR(4) := '018';
  c_function_code_wait      constant VARCHAR(4) := '023';

  c_pf_comm_carrier         constant VARCHAR(3) := 'SP';
  c_pf_exec_carrier         constant VARCHAR(3) := 'ZC';
  
  -- days for successful import. After that record is abandoned (status 8, 9 or 19).
  g_days_try_import NUMBER;
  g_err_msg VARCHAR2(200);

  --------------------------------------------
  --------------------------------------------
  -----tlgns_sap_bp---------------------------
  --------------------------------------------
  --------------------------------------------
  PROCEDURE main(i_min_loglevel IN VARCHAR2, i_days_try_import IN NUMBER) AS

    l_full_update BOOLEAN := FALSE;
    l_insert BOOLEAN := FALSE;
    l_bb_hjid NUMBER;
    l_final BOOLEAN := FALSE;
    l_new_state NUMBER := NULL;
    l_sqlerrm VARCHAR2(200);

    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;

    l_sap_vendor_id NUMBER;
    l_carrier_id NUMBER;
    l_internal NUMBER;
    l_comm_carrier NUMBER;
    l_exec_carrier NUMBER;
    l_lang_cd VARCHAR2(3);

    -- main query to select not imported records
    CURSOR cur_sap_vendor IS
    SELECT req.hjid vendor_hjid,
      gen.msgfn msgfn,
      gen.ktokk account_group,
      gen.land1 country_cd,
      LTRIM(gen.lifnr, '0') sap_id,
      gen.lifnr sap_ifc_id,
      DECODE(gen.loevm, 'X', '1', '0') marked_for_deletion,
      gen.name1 name1,
      gen.name2 name2,
      gen.stras street,
      gen.ort01 city,
      gen.ort02 location_district,
      gen.pstlz postal_code,
      gen.regio region,
      gen.sprasiso lang_cd,
      gen.stceg vat_registration_number,
      gen.telf1 telf1,
      gen.stcd3 tax_number3,
      req.sapimpimporteddatelement
    FROM ULGNSAP.VN_VENDORNOTIFICATION req
    INNER JOIN ULGNSAP.VN_VENDORGENERALDATA gen ON(gen.hjid = req.VENDORGENERALDATA_VN_VENDORG_0)
    WHERE req.sapimpstatuscd IN (c_gen_initial, c_gen_failed, c_gen_blocked)
    ORDER BY req.sapimpimporteddatelement asc;

  BEGIN

    g_days_try_import := i_days_try_import;

    -- set the time of the start of the import
    LOGON_LOG.start_import(c_import_name);

    -- set logging parameters
    LOGON_LOG.g_min_loglevel := i_min_loglevel;

    LOGON_LOG.INFO('VN: load_tlgns_sap_vendor', 'START loading data into ''tlgns_sap_vendor'' table ...');

    FOR cur_vendor_record IN cur_sap_vendor
    LOOP

      LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor', ' Checking sap_id (' || cur_vendor_record.sap_id || ') and hjid (' || cur_vendor_record.vendor_hjid || ') ...');
      
      -- check whether maximum age has already been reached
      l_final := EXTRACT(DAY FROM systimestamp - cur_vendor_record.sapimpimporteddatelement) > g_days_try_import;
      
      g_err_msg := NULL;

      -- check whether full or partial update (or invalid)      
      CASE cur_vendor_record.msgfn
        WHEN c_function_code_change THEN
          l_full_update := FALSE;
        WHEN c_function_code_replace THEN
          l_full_update := TRUE;
        WHEN c_function_code_original THEN
          l_full_update := TRUE;
        WHEN c_function_code_resend THEN
          l_full_update := FALSE;
        WHEN c_function_code_wait THEN
          BEGIN
            LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor', ' SKIP notification (function code ''' || c_function_code_wait || ''')');
            UPDATE ULGNSAP.VN_VENDORNOTIFICATION
            SET sapimpstatuscd = c_gen_skipped,
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_vendor_record.vendor_hjid;
            
            COMMIT;
            --CONTINUE;
            GOTO next_vendor_record;
          END;
        ELSE
          BEGIN
            LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor', ' Invalid function code ''' || cur_vendor_record.msgfn || ''', setting to failed final.');
            UPDATE ULGNSAP.VN_VENDORNOTIFICATION
            SET sapimpstatuscd = c_gen_failed_final,
              sapimperrmsg = 'Invalid message function code ''' || cur_vendor_record.msgfn || '''',
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_vendor_record.vendor_hjid;
            
            COMMIT;
            --CONTINUE;
            GOTO next_vendor_record;
          END;
      END CASE;
        
      BEGIN
      
        l_sap_vendor_id := NULL;
        l_carrier_id := NULL;

        -- check whether sap_vendor_id exists
        BEGIN
  
          SELECT vendor.sap_vendor_id
          INTO l_sap_vendor_id
          FROM tlgns_sap_vendor vendor
          WHERE vendor.sap_id = cur_vendor_record.sap_id;
  
          EXCEPTION
          WHEN no_data_found THEN
            -- bp will be loaded
            l_sap_vendor_id := -1;
          WHEN too_many_rows THEN
             LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor', SQLCODE, SQLERRM, ' More than one vendor found for sap_id = ' || cur_vendor_record.sap_id || ' and hjid = ' || cur_vendor_record.vendor_hjid || '.');
             g_err_msg := 'More than one vendor found for sap_id ''' || cur_vendor_record.sap_id || '''';
             RAISE;
          WHEN others THEN
             LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor', SQLCODE, SQLERRM, ' Unknown exception in selecting sap_vendor_id from tlgns_sap_vendor for sap_id (' || cur_vendor_record.sap_id || ') and hjid (' || cur_vendor_record.vendor_hjid || ')');
             RAISE;
        
        END;
        
        -- check whether to skip this notification
        IF NOT l_full_update THEN
        
          IF l_sap_vendor_id = -1 THEN
          
            LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor', ' Partial update and vendor ''' || cur_vendor_record.sap_id || ''' does not exist. Setting to failed final.');
          
            UPDATE ULGNSAP.VN_VENDORNOTIFICATION
            SET sapimpstatuscd = c_gen_failed,
              sapimperrmsg = 'Partial update and vendor does not exist',
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_vendor_record.vendor_hjid;
            
            COMMIT;
            --CONTINUE;
            GOTO next_vendor_record;
            
          ELSE
          
            SELECT vendor.update_blocked_by_hjid
            INTO l_bb_hjid
            FROM TLGNS_SAP_VENDOR vendor
            WHERE vendor.sap_vendor_id = l_sap_vendor_id;
            
            IF NOT (l_bb_hjid IS NULL OR l_bb_hjid = cur_vendor_record.vendor_hjid) THEN
            
              LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor', ' Update of sap_id = ' || cur_vendor_record.sap_id || ' and hjid = ' || cur_vendor_record.vendor_hjid || ' is blocked by hjid = ' || l_bb_hjid || '.');

              IF l_final THEN
                l_new_state := c_gen_blocked_final;
              ELSE
                l_new_state := c_gen_blocked;
              END IF;

              UPDATE ULGNSAP.VN_VENDORNOTIFICATION
              SET sapimpstatuscd = l_new_state,
                sapimperrmsg = 'Blocked by ' || l_bb_hjid,
                sapimppropagateddatelement = systimestamp
              WHERE hjid = cur_vendor_record.vendor_hjid;
              
              COMMIT;
              --CONTINUE;
              GOTO next_vendor_record;

            END IF;
            
          END IF;
          
        END IF;

        -- check whether language exists, set default 'en' otherwise
        BEGIN
  
          l_lang_cd := NULL;
          SELECT lang.lang_cd
          INTO l_lang_cd
          FROM tlgnr_lang_bs lang
          WHERE lang.lang_cd = LOWER(DECODE(cur_vendor_record.lang_cd, 'Z1', 'de', cur_vendor_record.lang_cd));
  
          EXCEPTION
          WHEN no_data_found THEN
            -- default to EN
            l_lang_cd := 'en';
            LOGON_LOG.INFO('VN: load_tlgns_sap_vendor', ' Cannot find lang (' || cur_vendor_record.lang_cd || ') for sap_id (' || cur_vendor_record.sap_id || '), using (' || l_lang_cd || ') as default');
          WHEN others THEN
            LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor', SQLCODE, SQLERRM, ' Unknown Exception in selecting lang_cd from tlgnr_lang_bs for lang_cd (' || cur_vendor_record.lang_cd || ') and sap_id (' || cur_vendor_record.sap_id || ')');
            RAISE;
        
        END;

        --check whether vendor is internal
        IF (cur_vendor_record.account_group = 'ZGRP') THEN
          l_internal := 1;
        ELSE
          l_internal := 0;
        END IF;
  
        -- check (self-referencing) partner functions
        l_comm_carrier := 0;
        l_exec_carrier := 0;
        check_partner_function(cur_vendor_record.vendor_hjid, cur_vendor_record.sap_id, l_comm_carrier, l_exec_carrier);

        l_insert := l_sap_vendor_id = -1;
        
        -- insert a new vendor
        IF l_insert THEN

          SELECT slgns_sap_vendor.nextval
          INTO l_sap_vendor_id
          FROM dual;
  
          LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor', ' Loading new sap vendor (sap_id = ' || cur_vendor_record.sap_id  || ' and sap_vendor_id = ' || l_sap_vendor_id || ') ...');
  
          INSERT
          INTO tlgns_sap_vendor (sap_vendor_id,
            sap_id,
            sap_ifc_id,
            account_group,
            marked_for_deletion,
            name1,
            name2,
            country_cd,
            postal_code,
            city,
            location_district,
            region, street,
            lang_cd,
            vat_registration_number,
            tax_number3,
            phone_number,
            vn_is_internal, vn_is_comm_carrier, vn_is_exec_carrier,
            ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
          VALUES (l_sap_vendor_id,
            cur_vendor_record.sap_id,
            cur_vendor_record.sap_ifc_id,
            cur_vendor_record.account_group,
            cur_vendor_record.marked_for_deletion,
            cur_vendor_record.name1,
            cur_vendor_record.name2,
            cur_vendor_record.country_cd,
            cur_vendor_record.postal_code,
            cur_vendor_record.city,
            cur_vendor_record.location_district,
            cur_vendor_record.region,
            cur_vendor_record.street,
            l_lang_cd,
            cur_vendor_record.vat_registration_number,
            cur_vendor_record.tax_number3,
            cur_vendor_record.telf1,
            l_internal, l_comm_carrier, l_exec_carrier,
            1, systimestamp, 1, systimestamp, 0);
    
        -- update vendor
        ELSE

          LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor', ' Updating sap vendor (sap_id = ' || cur_vendor_record.sap_id  || ' and sap_vendor_id = ' || l_sap_vendor_id || ') ...');

          UPDATE tlgns_sap_vendor
          SET sap_ifc_id = cur_vendor_record.sap_ifc_id,
            update_blocked_by_hjid = NULL,
            account_group = cur_vendor_record.account_group,
            marked_for_deletion = cur_vendor_record.marked_for_deletion,
            name1 = cur_vendor_record.name1,
            name2 = cur_vendor_record.name2,
            country_cd = cur_vendor_record.country_cd,
            postal_code = cur_vendor_record.postal_code,
            city = cur_vendor_record.city,
            location_district = cur_vendor_record.location_district,
            region = cur_vendor_record.region,
            street = cur_vendor_record.street,
            house_number = NULL, -- House number is in 'street'
            lang_cd = l_lang_cd,
            vat_registration_number = cur_vendor_record.vat_registration_number,
            tax_number3 = cur_vendor_record.tax_number3,
            phone_number = cur_vendor_record.telf1,
            vn_is_internal = l_internal,
            -- vn_is_comm_carrier/vn_is_exec_carrier flags are not updated here! It will be done in procedure update_pf_flags because we also want to check PF in the current DB (not only staging area!).
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_vendor_id = l_sap_vendor_id;

        END IF;
        
        -- check whether a carrier exists
        BEGIN
        
          SELECT carrier_id
          INTO l_carrier_id
          FROM TLGNC_CARRIER
          WHERE sap_vendor_id = l_sap_vendor_id;
          
          EXCEPTION
          WHEN no_data_found THEN
            l_carrier_id := -1;
          
        END;
        
        -- insert a new carrier
        IF l_carrier_id = -1 THEN
        
          -- only commercial and executing carriers are inserted into the carrier table
          IF (l_comm_carrier = 1 OR l_exec_carrier = 1) THEN
          
            SELECT slgnc_carrier.nextval
            INTO l_carrier_id
            FROM dual;
            
            LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor', ' Loading new carrier (carrier_id = ' || l_carrier_id || ') into tlgnc_carrier table ...');
            
            INSERT
            INTO tlgnc_carrier (carrier_id,
              display_name,
              active_flag,
              sap_vendor_id,
              transmit_to_carrier_flag,
              carrier_requires_planning_flag,
              ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
            VALUES (l_carrier_id,
              cur_vendor_record.name1,
              DECODE(cur_vendor_record.marked_for_deletion, '1', 0, 1),
              l_sap_vendor_id,
              0,
              0,
              1, systimestamp, 1, systimestamp, 0);
              
          END IF;
          
        -- update carrier
        ELSE
        
          LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor', ' Updating carrier (carrier_id = ' || l_carrier_id || ' and sap_vendor_id = ' || l_sap_vendor_id || ') ...');
          
          UPDATE TLGNC_CARRIER
          SET active_flag = DECODE(cur_vendor_record.marked_for_deletion, '1', 0, 1),
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE carrier_id = l_carrier_id;
          
        END IF;
  
        -- update sales area table
        load_tlgns_sap_vendor_porg(cur_vendor_record.vendor_hjid, l_sap_vendor_id);
        
        -- mark missing sales areas as deleted
        IF l_full_update THEN
          mark_missing_porg_as_deleted(cur_vendor_record.vendor_hjid, l_sap_vendor_id);
          NULL;
        END IF;

        --update controlling attributes
        UPDATE ulgnsap.VN_VENDORNOTIFICATION
        SET sapimpstatuscd = c_pf_initial,
          sapimperrmsg = NULL,
          sapimppropagateddatelement = systimestamp
        WHERE hjid = cur_vendor_record.vendor_hjid;

        COMMIT;
        LOGON_LOG.INFO('VN: load_tlgns_sap_vendor', ' Sap vendor (sap_id = ' || cur_vendor_record.sap_id  || ' and sap_vendor_id = ' || l_sap_vendor_id || ') has been loaded/updated.');

        IF l_insert THEN
          l_ins_total := l_ins_total + 1;
        ELSE
          l_upd_total := l_upd_total + 1;
        END IF;

        EXCEPTION

          WHEN others THEN
          BEGIN

            ROLLBACK;
            LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor', SQLCODE, SQLERRM, ' ROLLBACK on sap vendor (sap_id = ' || cur_vendor_record.sap_id  || ', hjid = ' || cur_vendor_record.vendor_hjid || ' and sap_vendor_id = ' || l_sap_vendor_id || ').');

            l_sqlerrm := SQLERRM;
            
            IF l_final THEN
              l_new_state := c_gen_failed_final;
            ELSE
              l_new_state := c_gen_failed;
            END IF;

            -- update controlling attributes
            UPDATE ulgnsap.VN_VENDORNOTIFICATION
            SET sapimpstatuscd = l_new_state,
              sapimperrmsg = COALESCE(g_err_msg, l_sqlerrm),
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_vendor_record.vendor_hjid;
            
            -- update 'blocked by' field
            UPDATE TLGNS_SAP_VENDOR vendor
            SET vendor.update_blocked_by_hjid = cur_vendor_record.vendor_hjid
            WHERE vendor.sap_id = cur_vendor_record.sap_id;

            COMMIT;

            IF l_final THEN
              LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor', ' Sap vendor (sap_id = ' || cur_vendor_record.sap_id  || ' and sap_vendor_id = ' || l_sap_vendor_id || ') has been abandoned. Days for successful import = ' || g_days_try_import || '.');
            END IF;

          END;
      
      END;
      
      --update older entries
      IF l_full_update THEN
      
        UPDATE (SELECT req.sapimpstatuscd, req.sapimppropagateddatelement, req.sapimpimporteddatelement
                FROM ulgnsap.VN_VENDORNOTIFICATION req
                INNER JOIN ulgnsap.VN_VENDORGENERALDATA gen ON (req.vendorgeneraldata_vn_vendorg_0 = gen.hjid)
                WHERE LTRIM(gen.lifnr, '0') = cur_vendor_record.sap_id) t
        SET t.sapimpstatuscd = c_gen_skipped,
          t.sapimppropagateddatelement = systimestamp
        WHERE t.sapimpstatuscd IN (c_gen_blocked, c_gen_failed)
          AND t.sapimpimporteddatelement < cur_vendor_record.sapimpimporteddatelement;

        COMMIT;
        
      END IF;
      
      <<next_vendor_record>>
      NULL;

    END LOOP; -- cur_vendor_record

    LOGON_LOG.INFO('VN: load_tlgns_sap_vendor', 'FINISH! Updated ' || l_upd_total || ' and inserted ' || l_ins_total || ' records into ''tlgns_sap_vendor'' table!');

    -- Load partner functions
    process_partner_functions();

    -- update vn_is_comm/exec_carrier flags according to db state
    update_pf_flags();

    -- set the run number and the time of the end of import
    LOGON_LOG.FINISH_IMPORT(c_import_name);

  END main;

  --------------------------------------------
  --------------------------------------------
  -----check partner function (staging area)--
  --------------------------------------------
  --------------------------------------------
  PROCEDURE check_partner_function(i_vendor_hjid IN NUMBER, i_sap_id IN VARCHAR, io_comm_carrier IN OUT NUMBER, io_exec_carrier IN OUT NUMBER) AS

    -- query to select partner functions
    CURSOR cur_partner IS
    SELECT LTRIM(pf.lifn2, '0') sap_id,
      pf.parvw partner_function
    FROM ulgnsap.VN_VENDORNOTIFICATION req
    INNER JOIN ulgnsap.VN_VENDORGENERALDATA gen ON (req.VENDORGENERALDATA_VN_VENDORG_0  = gen.hjid)
    INNER JOIN ulgnsap.VN_VENDORGENERALDATA_VENDORP_0 rel0 ON (rel0.PARENT_VN_VENDORGENERALDATA__0 = gen.hjid)
    INNER JOIN ulgnsap.VN_VENDORPURCHASINGORGANIZAT_0 porg ON (rel0.CHILD_VN_VENDORPURCHASINGORG_0 = porg.hjid)
    INNER JOIN ulgnsap.VN_VENDORPURCHASINGORGANIZAT_2 rel1 ON (rel1.PARENT_VN_VENDORPURCHASINGOR_0 = porg.hjid)
    INNER JOIN ulgnsap.vn_VENDORADDRESSESMMSSMD pf ON (rel1.CHILD_VN_VENDORADDRESSESMMSS_0 = pf.hjid)
    WHERE req.hjid = i_vendor_hjid
      AND porg.loevm IS NULL
      AND pf.msgfn <> c_function_code_delete;

  BEGIN

    LOGON_LOG.DEBUG('VN: check_partner_function', ' Checking partner function for sap_id = ' || i_sap_id || ' ...');

    FOR cur_partner_record IN cur_partner
    LOOP

      -- Check self-referencing partner functions (LIFN2 = LIFNR)
      -- Set the vn_is_comm_carrier / vn_is_exec_carrier flags accordingly
      IF (cur_partner_record.sap_id = i_sap_id) THEN

        IF (cur_partner_record.partner_function = c_pf_comm_carrier) THEN
          io_comm_carrier := 1;
        ELSIF (cur_partner_record.partner_function = c_pf_exec_carrier) THEN
          io_exec_carrier := 1;
        END IF;

      END IF;

    END LOOP;

    LOGON_LOG.DEBUG('VN: check_partner_function', ' Partner functions for sap_id = ' || i_sap_id || ': comm_carrier = ' || io_comm_carrier || ' and exec_carrier = ' || io_exec_carrier || '.');

  END check_partner_function;

  --------------------------------------------
  --------------------------------------------
  -----tlgns_sap_vendor_porg------------------
  --------------------------------------------
  --------------------------------------------
  PROCEDURE load_tlgns_sap_vendor_porg(i_vendor_hjid IN NUMBER, i_sap_vendor_id IN NUMBER) AS

    CURSOR cur_porg IS
    SELECT req.hjid vendor_hjid,
      DECODE(porg.loevm, 'X', '1', '0') marked_for_deletion,
      porg.ekorg purchasingorg_cd
    FROM ulgnsap.VN_VENDORNOTIFICATION req
    INNER JOIN ulgnsap.VN_VENDORGENERALDATA gen ON (req.VENDORGENERALDATA_VN_VENDORG_0  = gen.hjid)
    INNER JOIN ulgnsap.VN_VENDORGENERALDATA_VENDORP_0 rel ON (rel.PARENT_VN_VENDORGENERALDATA__0 = gen.hjid)
    INNER JOIN ulgnsap.VN_VENDORPURCHASINGORGANIZAT_0 porg ON (rel.CHILD_VN_VENDORPURCHASINGORG_0 = porg.hjid)
    WHERE req.hjid = i_vendor_hjid;

    l_purchasingorg_exists NUMBER := NULL;
    l_sap_vendor_porg_id NUMBER := NULL;
    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;

  BEGIN

    LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor_porg', ' Loading data into ''tlgns_sap_vendor_porg'' table (hjid = ' || i_vendor_hjid || ') ...');

    FOR cur_porg_record IN cur_porg
    LOOP

      l_purchasingorg_exists := NULL;
      
      LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor_porg', '  Checking purchasingorg_cd = ' || cur_porg_record.purchasingorg_cd || ' ...');

      --check whether purchasing organisation exists
      BEGIN

        SELECT ROWNUM
        INTO l_purchasingorg_exists
        FROM TLGNR_PURCHASINGORG_BS
        WHERE purchasingorg_cd = cur_porg_record.purchasingorg_cd;

        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor_porg', sqlcode, sqlerrm, '  Invalid purchasing organisation ''' || cur_porg_record.purchasingorg_cd || '''.');
          g_err_msg := 'Invalid purchasing organisation ''' || cur_porg_record.purchasingorg_cd || '''';
          RAISE;

      END;

      --check whether sap vendor purchasing organisation exists
      BEGIN

        SELECT sap_vendor_porg_id
        INTO l_sap_vendor_porg_id
        FROM TLGNS_SAP_VENDOR_PORG
        WHERE sap_vendor_id = i_sap_vendor_id
          AND purchasingorg_cd = cur_porg_record.purchasingorg_cd;

        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          -- new sap vendor purchasing organisation will be loaded
          l_sap_vendor_porg_id := -1;
        WHEN OTHERS THEN
          LOGON_LOG.ERROR('VN: load_tlgns_sap_vendor_porg', SQLCODE, SQLERRM, '  Unknown exception in selecting sap_vendor_porg_id from tlgns_sap_vendor_porg for sap_vendor_id = ' || i_sap_vendor_id|| ' and purchasingorg_cd = ' ||  cur_porg_record.purchasingorg_cd || '.');
          RAISE;

      END;

      -- insert a new sap vendor purchasing organisation
      IF (l_sap_vendor_porg_id = -1) THEN

        SELECT slgns_sap_vendor_porg.nextval
        INTO l_sap_vendor_porg_id
        FROM dual;

        LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor_porg', '  Inserting a new sap_vendor_porg (sap_vendor_id = ' || i_sap_vendor_id|| ', purchasingorg_cd = ' ||  cur_porg_record.purchasingorg_cd || ', hjid = ' || i_vendor_hjid || ') ...');

        INSERT
        INTO tlgns_sap_vendor_porg (sap_vendor_porg_id,
          sap_vendor_id,
          purchasingorg_cd,
          marked_for_deletion,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (l_sap_vendor_porg_id,
          i_sap_vendor_id,
          cur_porg_record.purchasingorg_cd,
          cur_porg_record.marked_for_deletion,
          1, systimestamp, 1, systimestamp, 0);

        LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor_porg', '  Sap_vendor_porg (sap_vendor_porg_id = ' || l_sap_vendor_porg_id || ') was successfully inserted.');
        l_ins_total := l_ins_total + 1;

      -- update sap vendor purchasing organisation
      ELSE

        LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor_porg', '  Updating sap_vendor_porg (sap_vendor_porg_id = ' || l_sap_vendor_porg_id || ', sap_vendor_id = ' || i_sap_vendor_id|| ', purchasingorg_cd = ' ||  cur_porg_record.purchasingorg_cd || ', hjid = ' || i_vendor_hjid || ') ...');

        UPDATE tlgns_sap_vendor_porg porg
        SET porg.marked_for_deletion = cur_porg_record.marked_for_deletion,
          ctl_mod_uid = 1,
          ctl_mod_ts = systimestamp,
          ctl_tcn = ctl_tcn + 1
        WHERE porg.sap_vendor_porg_id = l_sap_vendor_porg_id;

        LOGON_LOG.DEBUG('VN: load_tlgns_sap_vendor_porg', '  Sap_vendor_porg  (sap_vendor_porg_id = ' || l_sap_vendor_porg_id || ') was successfully updated.');
        l_upd_total := l_upd_total + 1;
          
      END IF;

    END LOOP;
    
    LOGON_LOG.INFO('VN: load_tlgns_sap_vendor_porg', ' Inserted ' || l_ins_total || ' and updated ' || l_upd_total || ' records in tlgns_sap_vendor_porg table.');

  END load_tlgns_sap_vendor_porg;

  ---------------------------------------------
  ---------------------------------------------
  -----Sap_vendor_porg entries which are not---
  -----present in the staging area anymore-----
  -----must be marked as deleted---------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE mark_missing_porg_as_deleted(i_vendor_hjid IN NUMBER, i_sap_vendor_id IN NUMBER) AS

    --find tlgns_sap_vendor_porg entries which are not present in the request anymore
    CURSOR porgs_to_mark_as_deleted IS
    SELECT sap_vendor_porg_id
    FROM TLGNS_SAP_VENDOR_PORG
    WHERE sap_vendor_porg_id NOT IN (
      SELECT db.sap_vendor_porg_id
      FROM ulgnsap.VN_VENDORNOTIFICATION req
      INNER JOIN ulgnsap.VN_VENDORGENERALDATA gen ON (req.VENDORGENERALDATA_VN_VENDORG_0  = gen.hjid)
      INNER JOIN ulgnsap.VN_VENDORGENERALDATA_VENDORP_0 rel ON (rel.PARENT_VN_VENDORGENERALDATA__0 = gen.hjid)
      INNER JOIN ulgnsap.VN_VENDORPURCHASINGORGANIZAT_0 porg ON (rel.CHILD_VN_VENDORPURCHASINGORG_0 = porg.hjid)
      INNER JOIN TLGNS_SAP_VENDOR_PORG db ON (porg.ekorg = db.purchasingorg_cd)
      WHERE req.hjid = i_vendor_hjid
    )
      AND sap_vendor_id = i_sap_vendor_id
      AND marked_for_deletion = '0';
  
  BEGIN
  
    LOGON_LOG.DEBUG('VN: mark_missing_porg_as_deleted', ' Marking missing purchasing organisations as deleted ...');
	
    FOR cur_porg_to_delete IN porgs_to_mark_as_deleted
    LOOP

	    BEGIN

          LOGON_LOG.INFO('VN: mark_missing_porg_as_deleted', '  Marking porg (sap_vendor_porg_id = ' || cur_porg_to_delete.sap_vendor_porg_id || ') as deleted.');
          UPDATE TLGNS_SAP_VENDOR_PORG SET 
            marked_for_deletion = '1',
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_vendor_porg_id = cur_porg_to_delete.sap_vendor_porg_id;
          
          LOGON_LOG.INFO('VN: mark_missing_porg_as_deleted', '  Delete all PFs of that purchasing organisation.');
          
          DELETE FROM TLGNS_SAP_VN_PF_COMM_CARRIER
          WHERE sap_vendor_porg_id = cur_porg_to_delete.sap_vendor_porg_id;
          
          DELETE FROM TLGNS_SAP_VN_PF_EXEC_CARRIER
          WHERE sap_vendor_porg_id = cur_porg_to_delete.sap_vendor_porg_id;
          
      END;

    END LOOP;
    
    LOGON_LOG.DEBUG('VN: mark_missing_porg_as_deleted', ' Done marking missing purchasing organisations as deleted.');     
    
  END mark_missing_porg_as_deleted;
	
---

  ---------------------------------------------
  ---------------------------------------------
  -----load partner functions------------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE process_partner_functions AS

    -- requests
    CURSOR cur_sap_vendor IS
    SELECT req.hjid vendor_hjid,
      req.sapimpimporteddatelement,
      gen.msgfn msgfn,
      LTRIM(gen.lifnr, '0') sap_id
    FROM ulgnsap.VN_VENDORNOTIFICATION req
    INNER JOIN ulgnsap.VN_VENDORGENERALDATA gen ON (req.vendorgeneraldata_vn_vendorg_0  = gen.hjid)
    WHERE req.sapimpstatuscd IN (c_pf_initial, c_pf_failed)
    ORDER BY req.sapimpimporteddatelement;

    -- purchasing organisations
    CURSOR cur_porg(v_vendor_hjid IN NUMBER) IS
    SELECT porg.hjid vendor_hjid,
      porg.msgfn msgfn,
      porg.ekorg purchasingorg_cd
    FROM ulgnsap.VN_VENDORNOTIFICATION req
    INNER JOIN ulgnsap.VN_VENDORGENERALDATA gen ON (req.vendorgeneraldata_vn_vendorg_0  = gen.hjid)
    INNER JOIN ulgnsap.VN_VENDORGENERALDATA_VENDORP_0 rel ON (rel.parent_vn_vendorgeneraldata__0 = gen.hjid)
    INNER JOIN ulgnsap.VN_VENDORPURCHASINGORGANIZAT_0 porg ON (rel.child_vn_vendorpurchasingorg_0 = porg.hjid)
    WHERE req.hjid = v_vendor_hjid;

    -- partner functions
    CURSOR cur_partner(v_vendor_hjid IN NUMBER) IS
    SELECT pf.msgfn,
      LTRIM(pf.lifn2, '0') sap_id,
      pf.parvw partner_function
    FROM ulgnsap.VN_VENDORPURCHASINGORGANIZAT_0 porg
    INNER JOIN ulgnsap.VN_VENDORPURCHASINGORGANIZAT_2 rel ON (rel.parent_vn_vendorpurchasingor_0 = porg.hjid)
    INNER JOIN ulgnsap.vn_VENDORADDRESSESMMSSMD pf ON (rel.child_vn_vendoraddressesmmss_0 = pf.hjid)
    WHERE porg.hjid = v_vendor_hjid;

    l_final BOOLEAN := FALSE;
    l_new_state NUMBER := NULL;
    l_sqlerrm VARCHAR2(200) := NULL;

    l_full_update     BOOLEAN := FALSE;
    l_pf_received     BOOLEAN := FALSE;  -- needed to decide whether to delete missing pf's
    l_porg_received   BOOLEAN := FALSE;  -- needed to determine the next notification state
    
    l_last_successful NUMBER := NULL;
    l_last_failed NUMBER := NULL;
    
    l_min_ls NUMBER := NULL;  -- minimum hjid of last successfull notification
    l_max_ls NUMBER := NULL;  -- maximum hjid of last successfull notification
    l_min_lf NUMBER := NULL;  -- minimum hjid of last failed notification

    l_purchasingorg_cd VARCHAR2(4) := NULL;
    
    -- notation: owner --> pf
    l_sap_vendor_id_owner NUMBER := NULL;
    l_sap_vendor_id_pf NUMBER := NULL;
    l_sap_vendor_porg_id_owner NUMBER:= NULL;
    l_sap_vendor_porg_id_pf NUMBER:= NULL;
    
    invalid_partner_record  EXCEPTION;

  BEGIN

    LOGON_LOG.INFO('VN: PF: process_partner_functions', 'START loading partner functions ...');

    FOR cur_vendor_record IN cur_sap_vendor
    LOOP
    
      BEGIN
      
        LOGON_LOG.DEBUG('VN: PF: process_partner_functions', ' Processing sap_id = ' || cur_vendor_record.sap_id || ' and hjid = ' || cur_vendor_record.vendor_hjid || ' ...');

        l_final := EXTRACT(DAY FROM systimestamp - cur_vendor_record.sapimpimporteddatelement) > g_days_try_import;
        
        g_err_msg := NULL;
      
        -- select owner sap_vendor_id from tlgns_sap_vendor
        l_sap_vendor_id_owner := NULL;
        get_sap_vendor_id(cur_vendor_record.sap_id, l_sap_vendor_id_owner);
        
        IF (l_sap_vendor_id_owner IS NULL) THEN
          
          LOGON_LOG.ERROR('VN: PF: process_partner_functions',   '  Skipping sap vendor (sap_id = ' || cur_vendor_record.sap_id || ' and hjid = ' || cur_vendor_record.vendor_hjid || ') because no sap_vendor_id found for sap_id ''' || cur_vendor_record.sap_id || '''.');
          
          IF l_final THEN
            l_new_state := c_pf_failed_final;
          ELSE
            l_new_state := c_pf_failed;
          END IF;
          
          UPDATE ULGNSAP.VN_VENDORNOTIFICATION
          SET sapimpstatuscd = l_new_state,
            sapimperrmsg = 'Vendor does not exist',
            sapimppropagateddatelement = systimestamp
          WHERE hjid = cur_vendor_record.vendor_hjid;
          
          COMMIT;
          --CONTINUE;
          GOTO next_vendor_record;
        
        END IF;
        
        l_min_ls := NULL;
        l_max_ls := -1;
        l_min_lf := NULL;
        
        l_porg_received := FALSE;
        
        FOR cur_porg_record IN cur_porg(cur_vendor_record.vendor_hjid)
        LOOP
        
          BEGIN
          
            LOGON_LOG.DEBUG('VN: PF: process_partner_functions', '   Processing purchasingorg_cd = ' || cur_porg_record.purchasingorg_cd || ' ...');

            l_porg_received := TRUE;
            
            -- check whether full or partial update (or invalid)     
            CASE cur_porg_record.msgfn
              WHEN c_function_code_change THEN
                l_full_update := FALSE;
              WHEN c_function_code_replace THEN
                l_full_update := TRUE;
              WHEN c_function_code_original THEN
                l_full_update := TRUE;
              WHEN c_function_code_resend THEN
                l_full_update := FALSE;
              ELSE
                BEGIN

                  LOGON_LOG.ERROR('VN: PF: process_partner_functions', '   Invalid function code ''' || cur_porg_record.msgfn || ''', skipping purchasing organisation.');

--------------------------------------------------------------------------------
                  l_min_ls := -1;
                  IF (l_min_lf IS NULL) OR (cur_vendor_record.vendor_hjid < l_min_lf) THEN
                    l_min_lf := cur_vendor_record.vendor_hjid;
                  END IF;
--------------------------------------------------------------------------------

                  --CONTINUE;
                  GOTO next_porg_record;

                END;
            END CASE;
    
            --check whether porg area exists
            l_purchasingorg_cd := NULL;

            BEGIN

              SELECT purchasingorg_cd
              INTO l_purchasingorg_cd
              FROM TLGNR_PURCHASINGORG_BS
              WHERE purchasingorg_cd = cur_porg_record.purchasingorg_cd;

              EXCEPTION
              WHEN no_data_found THEN
                LOGON_LOG.ERROR('VN: PF: process_partner_functions', '   No purchasing organisation found for purchasingorg_cd = ' || cur_porg_record.purchasingorg_cd || '.');
              WHEN others THEN
                LOGON_LOG.ERROR('VN: PF: process_partner_functions', SQLCODE, SQLERRM, '   Unknown exception in selecting purchasingorg_cd from TLGNR_PURCHASINGORG_BS for purchasingorg_cd = ' || cur_porg_record.purchasingorg_cd || '.');

            END;
            
            IF l_purchasingorg_cd IS NULL THEN
              
              LOGON_LOG.ERROR('VN: PF: process_partner_functions', '   Skipping purchasing organisation (purchasingorg_cd = ' || cur_porg_record.purchasingorg_cd || ' and hjid = ' || cur_vendor_record.vendor_hjid || ') because no purchasingorg_cd found.');
              g_err_msg := 'Purchasing organisation ''' || cur_porg_record.purchasingorg_cd || ' does not exist';
              
--------------------------------------------------------------------------------
              l_min_ls := -1;
              IF (l_min_lf IS NULL) OR (cur_vendor_record.vendor_hjid < l_min_lf) THEN
                l_min_lf := cur_vendor_record.vendor_hjid;
              END IF;
--------------------------------------------------------------------------------

              --CONTINUE;
              GOTO next_porg_record;
              
            END IF;
            
            -- select sap_vendor_porg_id owner from tlgns_sap_vendor_porg
            l_sap_vendor_porg_id_owner := NULL;
            get_sap_vendor_porg_id(l_sap_vendor_id_owner, l_purchasingorg_cd, l_sap_vendor_porg_id_owner);

            IF l_sap_vendor_porg_id_owner IS NULL THEN
              
              LOGON_LOG.ERROR('VN: PF: process_partner_functions', '   Skipping purchasing organisation (sap_id = ' || cur_vendor_record.sap_id || ', purchasingorg_cd = ' || l_purchasingorg_cd || ' and hjid = ' || cur_vendor_record.vendor_hjid || ') because no sap_vendor_porg_id found.');
              g_err_msg := 'No vendor purchasing organisation found for sap_id = ' || cur_vendor_record.sap_id || ' and purchasingorg_cd = ' || l_purchasingorg_cd;
              
--------------------------------------------------------------------------------
              l_min_ls := -1;
              IF (l_min_lf IS NULL) OR (cur_vendor_record.vendor_hjid < l_min_lf) THEN
                l_min_lf := cur_vendor_record.vendor_hjid;
              END IF;
--------------------------------------------------------------------------------

              --CONTINUE;
              GOTO next_porg_record;
            
            END IF;
            
            -- retrieve 'last successful' and 'last failed' values
            SELECT porg.last_successful_hjid,
              porg.last_failed_hjid
            INTO l_last_successful,
              l_last_failed
            FROM TLGNS_SAP_VENDOR_PORG porg
            WHERE porg.sap_vendor_porg_id = l_sap_vendor_porg_id_owner;
            
            IF (l_last_successful IS NOT NULL OR l_last_failed IS NOT NULL)
              AND (l_last_successful IS NULL OR l_last_successful >= cur_vendor_record.vendor_hjid)
              AND (l_last_failed IS NULL OR l_last_failed > cur_vendor_record.vendor_hjid) THEN
              
              LOGON_LOG.DEBUG('VN: PF: process_partner_functions', '   Skipping purchasing organisation since a newer version is available or it has already been imported.');
              
--------------------------------------------------------------------------------
              IF l_last_successful IS NULL THEN
                l_min_ls := -1;
              ELSIF l_last_successful < l_min_ls THEN
                l_min_ls := l_last_successful;
              END IF;

              IF (l_last_failed IS NOT NULL) AND ((l_min_lf IS NULL) OR (l_last_failed < l_min_lf)) THEN
                l_min_lf := l_last_failed;
              END IF;
--------------------------------------------------------------------------------
              
              --CONTINUE;
              GOTO next_porg_record;
              
            END IF;

            l_pf_received := FALSE;
            FOR cur_partner_record IN cur_partner(cur_porg_record.vendor_hjid)
            LOOP
            
              BEGIN
    
                LOGON_LOG.DEBUG('VN: PF: process_partner_functions', '    Processing parvw = ' || cur_partner_record.partner_function || ' and lifn2 = ' || cur_partner_record.sap_id || ' ...');
                
                l_pf_received := TRUE;
                
                l_sap_vendor_id_pf := NULL;
                get_sap_vendor_id(cur_partner_record.sap_id, l_sap_vendor_id_pf);
                
                IF (l_sap_vendor_id_pf IS NULL) THEN
                
                  LOGON_LOG.ERROR('VN: PF: process_partner_functions', '    Referenced vendor not found (lifn2 = ' || cur_partner_record.sap_id || ' and hjid = ' || cur_vendor_record.vendor_hjid || ').');
                  g_err_msg := 'Referenced vendor ''' || cur_partner_record.sap_id || ''' not found';
                  RAISE invalid_partner_record;
                  
                END IF;
                
                l_sap_vendor_porg_id_pf := NULL;
                get_sap_vendor_porg_id(l_sap_vendor_id_pf, l_purchasingorg_cd, l_sap_vendor_porg_id_pf);
                
                IF (l_sap_vendor_porg_id_pf IS NULL) THEN
                
                  LOGON_LOG.ERROR('VN: PF: process_partner_functions', '    Referenced vendor purchasing organisation not found (lifn2 = ' || cur_partner_record.sap_id || ', purchasingorg_cd = ' || l_purchasingorg_cd || ' and hjid = ' || cur_vendor_record.vendor_hjid || ').');
                  g_err_msg := 'Referenced vendor purchasing organisation not found for kunn2 = ' || cur_partner_record.sap_id || ' and purchasingorg_cd = ' || l_purchasingorg_cd;
                  RAISE invalid_partner_record;
                  
                END IF;
                
                IF cur_partner_record.msgfn IN (c_function_code_change, c_function_code_resend, c_function_code_replace, c_function_code_original) THEN

                  -- load data into relation table tlgns_sap_vn_pf_exec_carrier
                  load_partner_functions(l_sap_vendor_porg_id_owner, l_sap_vendor_porg_id_pf, l_purchasingorg_cd, cur_partner_record.partner_function);

                ELSIF cur_partner_record.msgfn = c_function_code_delete THEN

                  -- delete partner function
                  IF cur_partner_record.partner_function = c_pf_exec_carrier THEN
                  
                    DELETE FROM TLGNS_SAP_VN_PF_EXEC_CARRIER pf
                    WHERE pf.sap_vendor_porg_id = l_sap_vendor_porg_id_owner
                      AND pf.exec_sap_vendor_porg_id = l_sap_vendor_porg_id_pf
                      AND pf.purchasingorg_cd = l_purchasingorg_cd;
                      
                    COMMIT;
                  
                  ELSIF cur_partner_record.partner_function = c_pf_comm_carrier THEN
                  
                    DELETE FROM TLGNS_SAP_VN_PF_COMM_CARRIER pf
                    WHERE pf.sap_vendor_porg_id = l_sap_vendor_porg_id_owner
                      AND pf.comm_sap_vendor_porg_id = l_sap_vendor_porg_id_pf
                      AND pf.purchasingorg_cd = l_purchasingorg_cd;
                    
                  ELSE
                  
                    LOGON_LOG.DEBUG('VN: PF: process_partner_functions', '    Nothing to be deleted (partner function ''' || cur_partner_record.partner_function || ''').');
                      
                  END IF;

                ELSE

                  LOGON_LOG.ERROR('VN: PF: process_partner_functions', '    Invalid function code ''' || cur_partner_record.msgfn || '''.');
                  RAISE invalid_partner_record;

                END IF;
                                  
              END;
              
            END LOOP; -- cur_partner_record
            
            -- update 'last successful' and 'last_failed' fields
            UPDATE TLGNS_SAP_VENDOR_PORG
            SET last_successful_hjid = cur_vendor_record.vendor_hjid,
              last_failed_hjid = NULL
            WHERE sap_vendor_porg_id = l_sap_vendor_porg_id_owner;
            
            COMMIT;
            
--------------------------------------------------------------------------------
            IF (l_min_ls IS NULL) OR (cur_vendor_record.vendor_hjid < l_min_ls) THEN
              l_min_ls := cur_vendor_record.vendor_hjid;
            END IF;
            IF cur_vendor_record.vendor_hjid > l_max_ls THEN
              l_max_ls := cur_vendor_record.vendor_hjid;
            END IF;
--------------------------------------------------------------------------------

            -- delete missing partner functions
            IF l_pf_received OR l_full_update THEN
              delete_missing_pf(l_sap_vendor_porg_id_owner, cur_porg_record.vendor_hjid);
              NULL;
            END IF;
            
            EXCEPTION
            WHEN others THEN
            BEGIN
              
              ROLLBACK;
              LOGON_LOG.ERROR('VN: PF: process_partner_functions', SQLCODE, SQLERRM, '   ROLLBACK on purchasingorg_cd = ' || cur_porg_record.purchasingorg_cd || '.');
              
              l_sqlerrm := SQLERRM;
              
              UPDATE TLGNS_SAP_VENDOR_PORG
              SET last_successful_hjid = NULL,
                last_failed_hjid = cur_vendor_record.vendor_hjid
              WHERE sap_vendor_porg_id = l_sap_vendor_porg_id_owner;
              
              COMMIT;
              
--------------------------------------------------------------------------------
              l_min_ls := -1;
              IF (l_min_lf IS NULL) OR (cur_vendor_record.vendor_hjid < l_min_lf) THEN
                l_min_lf := cur_vendor_record.vendor_hjid;
              END IF;
--------------------------------------------------------------------------------
              
            END;
            
          END;
          
          <<next_porg_record>>
          NULL;
          
        END LOOP; -- cur_porg_record
        
        -- implementation of decision table for next state
        l_new_state := NULL;
        IF NOT l_porg_received THEN
        
          l_new_state := c_imported_to_logon;
          g_err_msg := NULL;
          
        ELSIF l_min_lf = cur_vendor_record.vendor_hjid THEN
        
          IF l_final THEN
            l_new_state := c_pf_failed_final;
          ELSE
            l_new_state := c_pf_failed;
          END IF;
          
          g_err_msg := COALESCE(g_err_msg, l_sqlerrm);
          
        ELSIF l_min_ls = -1 THEN
        
          IF l_max_ls = -1 THEN
            l_new_state := c_pf_skipped;
          ELSE
            l_new_state := c_imported_to_logon;
          END IF;
          
          g_err_msg := NULL;
          
        ELSIF l_min_ls = cur_vendor_record.vendor_hjid THEN
          
          l_new_state := c_imported_to_logon;
          g_err_msg := NULL;
          
        ELSE
        
          l_new_state := c_pf_skipped;
          g_err_msg := NULL;
          
        END IF;
        
        -- apply new state
        UPDATE ULGNSAP.VN_VENDORNOTIFICATION
        SET sapimpstatuscd = l_new_state,
          sapimperrmsg = g_err_msg,
          sapimppropagateddatelement = SYSTIMESTAMP
        WHERE hjid = cur_vendor_record.vendor_hjid;
        
        COMMIT;
        
      END;
      
      <<next_vendor_record>>
      NULL;
        
    END LOOP; -- cur_vendor_record

    LOGON_LOG.INFO('VN: PF: process_partner_functions', 'FINISH loading Partner Functions data!');
    
  END process_partner_functions;

  ---------------------------------------------
  ---------------------------------------------
  -----get sap_vendor_id-----------------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE get_sap_vendor_id(i_sap_id IN VARCHAR2, o_sap_vendor_id OUT NUMBER) AS

  BEGIN

      SELECT vendor.sap_vendor_id
      INTO o_sap_vendor_id
      FROM tlgns_sap_vendor vendor
      WHERE vendor.sap_id = i_sap_id;

      EXCEPTION
        WHEN others THEN
        LOGON_LOG.ERROR('VN: PF: get_sap_vendor_id', SQLCODE, SQLERRM, '    Cannot find sap_vendor_id for a given sap_id = ' || i_sap_id || ' because it has not been imported!');
  
  END get_sap_vendor_id;

  ---------------------------------------------
  ---------------------------------------------
  -----get sap_vendor_porg_id------------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE get_sap_vendor_porg_id(i_sap_vendor_id IN NUMBER, i_purchasingorg_cd IN VARCHAR2, o_sap_vendor_porg_id OUT NUMBER) AS

  BEGIN

      SELECT porg.sap_vendor_porg_id
      INTO o_sap_vendor_porg_id
      FROM tlgns_sap_vendor_porg porg
      WHERE porg.sap_vendor_id = i_sap_vendor_id
        AND porg.purchasingorg_cd = i_purchasingorg_cd;

      EXCEPTION
        WHEN others THEN
        LOGON_LOG.ERROR('VN: PF: get_sap_vendor_porg_id', SQLCODE, SQLERRM, '    Cannot find sap_vendor_porg_id for a given sap_vendor_id = ' || i_sap_vendor_id || ' and purchasingorg_cd = ' || i_purchasingorg_cd || ' because it has not been imported!');

  END get_sap_vendor_porg_id;

  ---------------------------------------------
  ---------------------------------------------
  -----load data into relation table-----------
  -----tlgns_sap_vn_pf_exec_carrier------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE load_partner_functions(i_sap_vendor_porg_id_owner IN NUMBER, i_sap_vendor_porg_id_pf IN NUMBER, i_purchasingorg_cd IN VARCHAR2, i_partner_function IN VARCHAR) AS

    l_rel_exists NUMBER := NULL;

  BEGIN

    LOGON_LOG.DEBUG('VN: PF: load_partner_functions', '    Checking partner function (' || i_partner_function || ') where sap_vendor_porg_id_owner = ' || i_sap_vendor_porg_id_owner || ', sap_vendor_porg_id_pf = '|| i_sap_vendor_porg_id_pf || ' and purchasingorg_cd = ' || i_purchasingorg_cd || ' ...');

    IF i_partner_function = c_pf_exec_carrier THEN

      -- check whether relation exists
      BEGIN

        SELECT rownum
        INTO l_rel_exists
        FROM TLGNS_SAP_VN_PF_EXEC_CARRIER exec
        WHERE exec.sap_vendor_porg_id = i_sap_vendor_porg_id_owner
          AND exec.exec_sap_vendor_porg_id = i_sap_vendor_porg_id_pf
          AND exec.purchasingorg_cd = i_purchasingorg_cd;

        EXCEPTION
        WHEN no_data_found THEN
          -- new relation will be loaded
          l_rel_exists := -1;

      END;

      -- insert new PF
      IF (l_rel_exists = -1) THEN
      
        LOGON_LOG.DEBUG('VN: PF: load_partner_functions', '    Inserting a new sap_vn_pf_exec_carrier (sap_vendor_porg_id_owner = ' || i_sap_vendor_porg_id_owner || ', sap_vendor_porg_id_pf = ' || i_sap_vendor_porg_id_pf || ' and purchasingorg_cd = ' || i_purchasingorg_cd || ') ...');
        
        INSERT
        INTO TLGNS_SAP_VN_PF_EXEC_CARRIER (sap_vendor_porg_id,
          exec_sap_vendor_porg_id,
          purchasingorg_cd,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (i_sap_vendor_porg_id_owner,
          i_sap_vendor_porg_id_pf,
          i_purchasingorg_cd,
          1, systimestamp, 1, systimestamp, 0);
          
      END IF;
      
    ELSIF i_partner_function = c_pf_comm_carrier THEN
    
      -- check whether relation exists
      BEGIN
      
        SELECT ROWNUM
        INTO l_rel_exists
        FROM TLGNS_SAP_VN_PF_COMM_CARRIER comm
        WHERE comm.sap_vendor_porg_id = i_sap_vendor_porg_id_owner
          AND comm.comm_sap_vendor_porg_id = i_sap_vendor_porg_id_pf
          AND comm.purchasingorg_cd = i_purchasingorg_cd;
          
        EXCEPTION
        WHEN no_data_found THEN
          -- new relation will be loaded
          l_rel_exists := -1;
          
      END;
      
      -- insert new PF
      IF (l_rel_exists = -1) THEN
      
        LOGON_LOG.DEBUG('VN: PF: load_partner_functions', '    Inserting a new sap_vn_pf_comm_carrier (sap_vendor_porg_id_owner = ' || i_sap_vendor_porg_id_owner || ', sap_vendor_porg_id_pf = ' || i_sap_vendor_porg_id_pf || ' and purchasingorg_cd = ' || i_purchasingorg_cd || ') ...');
        
        INSERT
        INTO TLGNS_SAP_VN_PF_COMM_CARRIER (sap_vendor_porg_id,
          comm_sap_vendor_porg_id,
          purchasingorg_cd,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (i_sap_vendor_porg_id_owner,
          i_sap_vendor_porg_id_pf,
          i_purchasingorg_cd,
          1, SYSTIMESTAMP, 1, SYSTIMESTAMP, 0);
          
      END IF;

    END IF;

  END load_partner_functions;

  ---------------------------------------------
  ---------------------------------------------
  -----delete missing partner functions--------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE delete_missing_pf(i_sap_vendor_porg_id IN NUMBER, i_porg_hjid IN NUMBER) AS

  CURSOR cur_exec_porg IS
  SELECT exec.sap_vendor_porg_id,
    exec.exec_sap_vendor_porg_id
  FROM TLGNS_SAP_VN_PF_EXEC_CARRIER exec
  WHERE exec.exec_sap_vendor_porg_id NOT IN (
    SELECT db.sap_vendor_porg_id
    FROM ULGNSAP.VN_VENDORPURCHASINGORGANIZAT_0 porg
    INNER JOIN ULGNSAP.VN_VENDORPURCHASINGORGANIZAT_2 rel ON (rel.parent_vn_vendorpurchasingor_0 = porg.hjid)
    INNER JOIN ULGNSAP.VN_VENDORADDRESSESMMSSMD pf ON (rel.child_vn_vendoraddressesmmss_0 = pf.hjid)
    INNER JOIN TLGNS_SAP_VENDOR vn ON (LTRIM(pf.lifn2, '0') = vn.sap_id)
    INNER JOIN TLGNS_SAP_VENDOR_PORG db ON (vn.sap_vendor_id = db.sap_vendor_id AND porg.ekorg = db.purchasingorg_cd)
    WHERE porg.hjid = i_porg_hjid
      AND pf.parvw = c_pf_exec_carrier
  )
   AND exec.sap_vendor_porg_id = i_sap_vendor_porg_id;

  CURSOR cur_comm_porg IS
  SELECT comm.sap_vendor_porg_id,
    comm.comm_sap_vendor_porg_id
  FROM TLGNS_SAP_VN_PF_COMM_CARRIER comm
  WHERE comm.comm_sap_vendor_porg_id NOT IN (
    SELECT db.sap_vendor_porg_id
    FROM ULGNSAP.VN_VENDORPURCHASINGORGANIZAT_0 porg
    INNER JOIN ULGNSAP.VN_VENDORPURCHASINGORGANIZAT_2 rel ON (rel.parent_vn_vendorpurchasingor_0 = porg.hjid)
    INNER JOIN ULGNSAP.VN_VENDORADDRESSESMMSSMD pf ON (rel.child_vn_vendoraddressesmmss_0 = pf.hjid)
    INNER JOIN TLGNS_SAP_VENDOR vn ON (LTRIM(pf.lifn2, '0') = vn.sap_id)
    INNER JOIN TLGNS_SAP_VENDOR_PORG db ON (vn.sap_vendor_id = db.sap_vendor_id AND porg.ekorg = db.purchasingorg_cd)
    WHERE porg.hjid = i_porg_hjid
      AND pf.parvw = c_pf_comm_carrier
  )
   AND comm.sap_vendor_porg_id = i_sap_vendor_porg_id;

  BEGIN
  
    LOGON_LOG.DEBUG('VN: PF: delete_missing_pf', '   Deleting missing partner functions (sap_vendor_porg_id = ' || i_sap_vendor_porg_id || ' and hjid = ' || i_porg_hjid || ') ...');
    
    FOR cur_exec_porg_record IN cur_exec_porg
    LOOP
    
      DELETE FROM TLGNS_SAP_VN_PF_EXEC_CARRIER
      WHERE sap_vendor_porg_id = i_sap_vendor_porg_id
        AND exec_sap_vendor_porg_id = cur_exec_porg_record.exec_sap_vendor_porg_id;
      
      LOGON_LOG.DEBUG('VN: PF: delete_missing_pf', '    Partner function ' || c_pf_exec_carrier || ' (sap_vendor_porg_id = ' || i_sap_vendor_porg_id || ' and exec_sap_vendor_porg_id = ' || cur_exec_porg_record.exec_sap_vendor_porg_id || ') deleted.');
        
    END LOOP;

    FOR cur_comm_porg_record IN cur_comm_porg
    LOOP
    
      DELETE FROM TLGNS_SAP_VN_PF_COMM_CARRIER
      WHERE sap_vendor_porg_id = i_sap_vendor_porg_id
        AND comm_sap_vendor_porg_id = cur_comm_porg_record.comm_sap_vendor_porg_id;
      
      LOGON_LOG.DEBUG('VN: PF: delete_missing_pf', '    Partner function ' || c_pf_comm_carrier || ' (sap_vendor_porg_id = ' || i_sap_vendor_porg_id || ' and comm_sap_vendor_porg_id = ' || cur_comm_porg_record.comm_sap_vendor_porg_id || ') deleted.');
        
    END LOOP;
    
    COMMIT;

    LOGON_LOG.DEBUG('VN: PF: delete_missing_pf', '   Done deleting missing partner functions.');
  
  END delete_missing_pf;
  
  ---------------------------------------------
  ---------------------------------------------
  -----update partner function flags-----------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE update_pf_flags AS

    l_upd_total NUMBER := 0;
  
  BEGIN
  
    LOGON_LOG.INFO('VN: update_pf_flags', 'START updating vn_is_comm/exec_carrier flags ...');
  
    -- commercial carrier
    UPDATE TLGNS_SAP_VENDOR vn
    SET vn.vn_is_comm_carrier = 1
    WHERE vn.vn_is_comm_carrier = 0
      AND vn.sap_vendor_id IN (
        SELECT DISTINCT vn.sap_vendor_id
        FROM TLGNS_SAP_VENDOR vn
        LEFT JOIN TLGNS_SAP_VENDOR_PORG porg ON (vn.sap_vendor_id = porg.sap_vendor_id)
        LEFT JOIN TLGNS_SAP_VN_PF_COMM_CARRIER comm ON (porg.sap_vendor_porg_id = comm.sap_vendor_porg_id AND porg.sap_vendor_porg_id = comm.comm_sap_vendor_porg_id)
        WHERE comm.comm_sap_vendor_porg_id IS NOT NULL
      );
      
    l_upd_total := SQL%ROWCOUNT;
    
    LOGON_LOG.INFO('VN: update_pf_flags', ' Updated ' || l_upd_total || ' vn_is_comm_carrier flags from 0 to 1.');
      
    UPDATE TLGNS_SAP_VENDOR vn
    SET vn.vn_is_comm_carrier = 0
    WHERE vn.vn_is_comm_carrier = 1
      AND vn.sap_vendor_id NOT IN (
        SELECT DISTINCT vn.sap_vendor_id
        FROM TLGNS_SAP_VENDOR vn
        LEFT JOIN TLGNS_SAP_VENDOR_PORG porg ON (vn.sap_vendor_id = porg.sap_vendor_id)
        LEFT JOIN TLGNS_SAP_VN_PF_COMM_CARRIER comm ON (porg.sap_vendor_porg_id = comm.sap_vendor_porg_id AND porg.sap_vendor_porg_id = comm.comm_sap_vendor_porg_id)
        WHERE comm.comm_sap_vendor_porg_id IS NOT NULL
      );
      
    l_upd_total := SQL%ROWCOUNT;
    
    LOGON_LOG.INFO('VN: update_pf_flags', ' Updated ' || l_upd_total || ' vn_is_comm_carrier flags from 1 to 0.');

    -- executing carrier
    UPDATE TLGNS_SAP_VENDOR vn
    SET vn.vn_is_exec_carrier = 1
    WHERE vn.vn_is_exec_carrier = 0
      AND vn.sap_vendor_id IN (
        SELECT DISTINCT vn.sap_vendor_id
        FROM TLGNS_SAP_VENDOR vn
        LEFT JOIN TLGNS_SAP_VENDOR_PORG porg ON (vn.sap_vendor_id = porg.sap_vendor_id)
        LEFT JOIN TLGNS_SAP_VN_PF_EXEC_CARRIER exec ON (porg.sap_vendor_porg_id = exec.sap_vendor_porg_id AND porg.sap_vendor_porg_id = exec.exec_sap_vendor_porg_id)
        WHERE exec.exec_sap_vendor_porg_id IS NOT NULL
      );
      
    l_upd_total := SQL%ROWCOUNT;
    
    LOGON_LOG.INFO('VN: update_pf_flags', ' Updated ' || l_upd_total || ' vn_is_exec_carrier flags from 0 to 1.');
      
    UPDATE TLGNS_SAP_VENDOR vn
    SET vn.vn_is_exec_carrier = 0
    WHERE vn.vn_is_exec_carrier = 1
      AND vn.sap_vendor_id NOT IN (
        SELECT DISTINCT vn.sap_vendor_id
        FROM TLGNS_SAP_VENDOR vn
        LEFT JOIN TLGNS_SAP_VENDOR_PORG porg ON (vn.sap_vendor_id = porg.sap_vendor_id)
        LEFT JOIN TLGNS_SAP_VN_PF_EXEC_CARRIER exec ON (porg.sap_vendor_porg_id = exec.sap_vendor_porg_id AND porg.sap_vendor_porg_id = exec.exec_sap_vendor_porg_id)
        WHERE exec.exec_sap_vendor_porg_id IS NOT NULL
      );
      
    l_upd_total := SQL%ROWCOUNT;
    
    LOGON_LOG.INFO('VN: update_pf_flags', ' Updated ' || l_upd_total || ' vn_is_exec_carrier flags from 1 to 0.');

    COMMIT;

    LOGON_LOG.INFO('VN: update_pf_flags', 'FINISH updating bp_is_shipto/soldto/billto/payer flags.');
    
  END update_pf_flags;

END SAPIMPORT_VN;
/
 
SHOW ERRORS;
--------------------------------------------------------------------------------
-- END OF THE SCRIPT
--------------------------------------------------------------------------------
