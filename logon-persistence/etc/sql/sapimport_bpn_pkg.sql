--------------------------------------------------------------------------------
--
-- PROJECT      : $Project: LOGON$
--
-- AUTHOR       : $Author: stich $
-- CREATION DATE: $Date$
-- DESCRIPTION  : Create BP loading package.
--
--------------------------------------------------------------------------------


CREATE OR REPLACE PACKAGE SAPIMPORT_BPN AS

  parent_key_not_found EXCEPTION;
  PRAGMA EXCEPTION_INIT(parent_key_not_found, -2291);

  -- phase 1 (general data, sales area data and company code data)

  PROCEDURE main(i_min_loglevel IN VARCHAR2, i_days_try_import IN NUMBER);
  PROCEDURE check_partner_function(i_bp_hjid IN NUMBER, i_sap_id IN VARCHAR, io_shipto IN OUT NUMBER, io_soldto IN OUT NUMBER, io_billto IN OUT NUMBER, io_payer IN OUT NUMBER);

  PROCEDURE load_tlgns_sap_bp_sarea(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER, i_shipto IN NUMBER);
  FUNCTION get_sales_office(i_sales_office IN VARCHAR2) RETURN VARCHAR2;
  FUNCTION get_sales_group(i_sales_group IN VARCHAR2) RETURN VARCHAR2;

  PROCEDURE load_tlgns_sap_bp_companycode(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER);

  PROCEDURE mark_missing_sarea_as_deleted(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER);
  PROCEDURE delete_all_pfs(i_sap_bp_sarea_id IN NUMBER);
  PROCEDURE delete_all_ssrs(i_sap_bp_id IN NUMBER);
  PROCEDURE delete_all_ssrs_by_sarea(i_sarea_id IN NUMBER, i_sap_bp_id IN NUMBER);
  
  PROCEDURE mark_missing_compcd_as_deleted(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER);

  -- phase 2 (partner functions)

  PROCEDURE process_partner_functions;
  PROCEDURE get_sap_bp_id(i_sap_id IN VARCHAR2, o_sap_bp_id OUT NUMBER);
  PROCEDURE get_sap_bp_sarea_id(i_sap_bp_id IN NUMBER, i_sarea_id IN NUMBER, o_sap_bp_sarea_id OUT NUMBER);
  PROCEDURE load_partner_functions(i_sap_bp_sarea_id_owner IN NUMBER, i_sap_bp_sarea_id_pf IN NUMBER, i_sarea_id IN NUMBER, i_partner_function IN VARCHAR);
  PROCEDURE delete_missing_pf(i_sap_bp_sarea_id IN NUMBER, i_sarea_hjid IN NUMBER);
  PROCEDURE invent_soldto_billto_payer_pf(i_account_group_cd IN VARCHAR, i_sap_bp_sarea_id IN NUMBER);

  -- cleanup: partner function flags
  PROCEDURE update_pf_flags;

  -- common division and distribution channel related things
  PROCEDURE update_dependent_bp_sareas(i_bp_hjid IN NUMBER, i_sap_bp_sarea_id IN NUMBER, i_sap_bp_id IN NUMBER, i_sarea_id IN NUMBER);
  PROCEDURE sync_dependent_bp_sarea(i_sap_bp_sarea_id IN NUMBER, i_dep_sap_bp_sarea_id IN NUMBER, i_dep_sarea_id IN NUMBER, x_ins_total IN OUT NUMBER, x_upd_total IN OUT NUMBER);
  PROCEDURE delete_dependent_sareas(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER, i_sarea_id IN NUMBER);
  PROCEDURE sync_dependent_pfs(i_sap_bp_sarea_id IN NUMBER, i_sap_bp_id IN NUMBER, i_sarea_id IN NUMBER);

END SAPIMPORT_BPN;
/

SHOW ERRORS;

CREATE OR REPLACE PACKAGE BODY SAPIMPORT_BPN AS

  ----------------------------------------------------------------
  ----------------------------------------------------------------
  --------------------BP-IMPORT-----------------------------------
  ----------------------------------------------------------------
  ----------------------------------------------------------------

  c_import_name             constant VARCHAR(30) := 'sapimport_bpn';

  ----------------------------------------------------------------
  -- Statuses
  ----------------------------------------------------------------

  c_notification_initial             constant NUMBER :=  0; -- The notification has not been processed yet.
  c_notification_skipped             constant NUMBER :=  1; -- The notification has been skipped because a newer full update is available
  c_notification_failed              constant NUMBER :=  2; -- The notification could not be imported because an error occured but it will be processed again during the next run.
  c_notification_blocked             constant NUMBER :=  3; -- The notification is blocked by an older notification that had failed.
  c_notification_blocked_final       constant NUMBER :=  8; -- The notification is blocked by an older notification that had failed and processing has been abandoned.
  c_notification_failed_final        constant NUMBER :=  9; -- The notification could not be imported because an error occured and procesing has been abandoned.
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

  c_pf_billto               constant VARCHAR(3) := 'RE';
  c_pf_shipto               constant VARCHAR(3) := 'WE';
  c_pf_soldto               constant VARCHAR(3) := 'AG';
  c_pf_payer                constant VARCHAR(3) := 'RG';

  -- used to substitute invalid sales offices and sales groups. Must exist both in TLGNR_SOFFICE_BS and TLGNR_SGROUP_BS.
  c_invalid                 constant VARCHAR(4) := 'INV';

  e_no_companycode_cd       EXCEPTION;

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

    l_sap_bp_id NUMBER;
    l_lang_cd VARCHAR2(3);
    l_sap_lang_cd VARCHAR2(3);
    l_uic_country_cd VARCHAR2(2);
    l_railway_station_no VARCHAR2(6);
    l_railway_station_id NUMBER := NULL;
    l_treat_as_onetime_flag NUMBER := NULL;
    l_chromat_reduction_months NUMBER := NULL;

    l_bp_is_driver NUMBER := NULL;
    l_shipto NUMBER;
    l_soldto NUMBER;
    l_billto NUMBER;
    l_payer NUMBER;

    l_timezone_cd VARCHAR2(32) := NULL;

    -- main query to select not imported records
    cursor cur_sap_bp is
    select business_partner_notification.hjid bp_hjid,
      master_customer_basic_data.msgfn msgfn,
      master_customer_basic_data.land1 country_cd,
      master_customer_basic_data.kukla customer_classification,
      ltrim(master_customer_basic_data.kunnr,'0') sap_id,
      master_customer_basic_data.kunnr sap_ifc_id,
      decode(UPPER(master_customer_basic_data.loevm), 'X', '1', '0') marked_for_deletion,
      master_customer_basic_data.name1 name1,
      master_customer_basic_data.name2 name2,
      master_customer_basic_data.ort01 city,
      master_customer_basic_data.pstlz postal_code,
      master_customer_basic_data.regio region,
      master_customer_basic_data.stras street,
      trim(master_customer_basic_data.bahns) railway_station,
      master_customer_basic_data.sprasiso sap_lang_cd,
      decode(master_customer_basic_data.sperr, null, '0', '1') central_posting_block,
      decode(master_customer_basic_data.lifsd, null, '0', '1') central_delivery_block,
      decode(master_customer_basic_data.aufsd, null, '0', '1') central_order_block,
      master_customer_basic_data.ktokd account_group_cd,
      master_customer_basic_data.katr1 chromat_reduction_months,
      master_customer_basic_data.telf1 telf1,
      master_customer_basic_data.stceg vat_reg_number,
      master_customer_basic_data.stcd1 tax_number_1,
      business_partner_notification.sapimpimporteddatelement,
      master_contact_person.name1 || ', ' || master_contact_person.namev contact_name,
      master_contact_person.telf1 contact_phone
    from ulgnsap.bpn_businesspartnernotificat_0 business_partner_notification
    inner join ulgnsap.bpn_mastercustomerbasicdata master_customer_basic_data on (
      business_partner_notification.mastercustomerbasicdata_bpn__0  = master_customer_basic_data.hjid
    )
    left join (
      select
        master_contact_persons.name1, master_contact_persons.namev, master_contact_persons.telf1,
        master_contact_person_links.parent_bpn_mastercustomerbas_0 parent_basic_data,
        (row_number() over(partition by master_contact_person_links.parent_bpn_mastercustomerbas_0
         order by master_contact_person_links.child_bpn_mastercustomermast_0 asc)) rank
      from
        ulgnsap.bpn_mastercustomerbasicdata__4 master_contact_person_links
      join ulgnsap.bpn_mastercustomermastercont_0 master_contact_persons on (
        master_contact_person_links.child_bpn_mastercustomermast_0 = master_contact_persons.hjid and
        master_contact_persons.abtnr = 'Z002'
      )
    ) master_contact_person on (
      master_contact_person.parent_basic_data = master_customer_basic_data.hjid and
      master_contact_person.rank = 1
    )
    where business_partner_notification.sapimpstatuscd in (
      c_notification_initial,
      c_notification_failed,
      c_notification_blocked
    )
    order by business_partner_notification.sapimpimporteddatelement asc;
  BEGIN

    g_days_try_import := i_days_try_import;

    -- set the time of the start of the import
    LOGON_LOG.start_import(c_import_name);

    -- set logging parameters
    LOGON_LOG.g_min_loglevel := i_min_loglevel;

    LOGON_LOG.INFO('BPN: load_tlgns_sap_bp', 'START loading data into ''tlgns_sap_bp'' table ...');

    FOR cur_bp_record IN cur_sap_bp
    LOOP

      LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp', ' Checking sap_id (' || cur_bp_record.sap_id || ') and hjid (' || cur_bp_record.bp_hjid || ') ...');

      -- check whether maximum age has already been reached
      l_final := EXTRACT(DAY FROM systimestamp - cur_bp_record.sapimpimporteddatelement) > g_days_try_import;

      g_err_msg := NULL;

      -- check whether full or partial update (or invalid)
      CASE cur_bp_record.msgfn
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
            LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp', ' SKIP notification (function code ''' || c_function_code_wait || ''')');
            UPDATE ULGNSAP.BPN_BUSINESSPARTNERNOTIFICAT_0
            SET sapimpstatuscd = c_notification_skipped,
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_bp_record.bp_hjid;

            COMMIT;
            GOTO next_bp_record;
          END;
        ELSE
          BEGIN
            LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp', ' Invalid function code ''' || cur_bp_record.msgfn || ''', setting to failed final.');
            UPDATE ULGNSAP.BPN_BUSINESSPARTNERNOTIFICAT_0
            SET sapimpstatuscd = c_notification_failed_final,
              sapimperrmsg = 'Invalid message function code ''' || cur_bp_record.msgfn || '''',
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_bp_record.bp_hjid;

            COMMIT;
            GOTO next_bp_record;
          END;
      END CASE;

      BEGIN

        l_sap_bp_id := NULL;

        -- check whether sap_bp_id exists
        BEGIN

          SELECT bp.sap_bp_id
          INTO l_sap_bp_id
          FROM tlgns_sap_bp bp
          WHERE bp.sap_id = cur_bp_record.sap_id;

          EXCEPTION
          WHEN no_data_found THEN
            -- bp will be loaded
            l_sap_bp_id := -1;
          WHEN too_many_rows THEN
            LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp',
              ' More than one business partner found for sap_id = ' || cur_bp_record.sap_id || ' and hjid = ' || cur_bp_record.bp_hjid || '.');
            g_err_msg := 'More than one business partner found for sap_id ''' || cur_bp_record.sap_id || '''';
            RAISE;
          WHEN others THEN
            LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp', SQLCODE, SQLERRM,
              ' Unknown exception in selecting sap_bp_id from TLGNS_SAP_BP for sap_id = ' || cur_bp_record.sap_id || ' and hjid = ' || cur_bp_record.bp_hjid || '.');
            RAISE;

        END;

        -- check whether to skip this notification
        IF NOT l_full_update THEN

          IF l_sap_bp_id = -1 THEN

            LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp',
              ' Partial update and business partner ''' || cur_bp_record.sap_id || ''' does not exist. Setting to failed final.');

            UPDATE ULGNSAP.BPN_BUSINESSPARTNERNOTIFICAT_0
            SET sapimpstatuscd = c_notification_failed_final,
              sapimperrmsg = 'Partial update and business partner does not exist',
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_bp_record.bp_hjid;

            COMMIT;
            GOTO next_bp_record;

          ELSE

            SELECT bp.update_blocked_by_hjid
            INTO l_bb_hjid
            FROM TLGNS_SAP_BP bp
            WHERE bp.sap_bp_id = l_sap_bp_id;

            IF NOT (l_bb_hjid IS NULL OR l_bb_hjid = cur_bp_record.bp_hjid) THEN

              LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp',
                ' Update of sap_id = ' || cur_bp_record.sap_id || ' and hjid = ' || cur_bp_record.bp_hjid || ' is blocked by hjid = ' || l_bb_hjid || '.');

              IF l_final THEN
                l_new_state := c_notification_blocked_final;
              ELSE
                l_new_state := c_notification_blocked;
              END IF;

              UPDATE ULGNSAP.BPN_BUSINESSPARTNERNOTIFICAT_0
              SET sapimpstatuscd = l_new_state,
                sapimperrmsg = 'Blocked by ' || l_bb_hjid,
                sapimppropagateddatelement = systimestamp
              WHERE hjid = cur_bp_record.bp_hjid;

              COMMIT;
              GOTO next_bp_record;

            END IF;

          END IF;

        END IF;

        -- check whether language exists, set default 'en' otherwise
        BEGIN

          l_sap_lang_cd := cur_bp_record.sap_lang_cd;
          l_lang_cd := NULL;

          SELECT tlgnr_sap_language_bs.map_to_application_lang_cd
          INTO l_lang_cd
          FROM tlgnr_sap_language_bs
          WHERE tlgnr_sap_language_bs.sap_language_cd = l_sap_lang_cd;

          EXCEPTION
          WHEN no_data_found THEN
            -- default to EN
            l_lang_cd := 'en';
            LOGON_LOG.INFO('BPN: load_tlgns_sap_bp',
              ' Cannot find lang (' || l_sap_lang_cd || ') for sap bp_id (' || cur_bp_record.sap_id || '), using (' || l_lang_cd || ') as default');
          WHEN others THEN
            LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp', SQLCODE, SQLERRM,
              ' Unknown exception in selecting lang_cd from TLGNR_SAP_LANGUAGE_BS for sap_language_cd = ' || l_sap_lang_cd || ' and sap_id = ' || cur_bp_record.sap_id || '.');
            RAISE;

        END;

        -- check whether account group exists
        BEGIN

          SELECT bp_is_driver, treat_as_onetime_flag
          INTO l_bp_is_driver, l_treat_as_onetime_flag
          FROM TLGNR_SAP_ACCOUNT_GROUP_BS
          WHERE sap_account_group_cd = cur_bp_record.account_group_cd;

          EXCEPTION
          WHEN no_data_found THEN
            LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp', 'Account group ''' || cur_bp_record.account_group_cd || ''' not found.');
            g_err_msg := 'Account group ''' || cur_bp_record.account_group_cd || ''' not found.';
            RAISE;

        END;

        -- validate railway station and railway station country
        IF (cur_bp_record.railway_station IS NOT NULL AND length(cur_bp_record.railway_station) = 8) THEN

          l_uic_country_cd := SUBSTR(cur_bp_record.railway_station, 1, 2);
          l_railway_station_no := SUBSTR(cur_bp_record.railway_station, 3, 6);

          LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp',
            ' Checking railway station (railway_station_no = ' || l_railway_station_no || ' and uic_country_cd = ' || l_uic_country_cd || ') ...');

          BEGIN

            SELECT rail.railway_station_id
            INTO l_railway_station_id
            FROM tlgnc_railway_station rail
            WHERE rail.railway_station_no =  l_railway_station_no
              AND rail.uic_country_cd = l_uic_country_cd;

            LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp', ' Selected railway station id is ' || l_railway_station_id);

            EXCEPTION
            WHEN no_data_found THEN
              l_railway_station_id := NULL;
              LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp',
                ' Cannot find railway station (railway_station_no = ' || l_railway_station_no || ' and uic_country_cd = ' || l_uic_country_cd || ')');

          END;

        ELSE

          l_railway_station_id := NULL;

        END IF;

        -- check (self-referencing) partner functions
        l_shipto := 0;
        l_soldto := 0;
        l_billto := 0;
        l_payer := 0;
        check_partner_function(cur_bp_record.bp_hjid, cur_bp_record.sap_id, l_shipto, l_soldto, l_billto, l_payer);

        IF cur_bp_record.chromat_reduction_months IS NULL THEN
          l_chromat_reduction_months := -1;
        ELSE
          l_chromat_reduction_months := cur_bp_record.chromat_reduction_months;
        END IF;

        -- lookup timezone
        BEGIN
          -- try first with both country and region code
          SELECT timezone_cd
          INTO l_timezone_cd
          FROM TLGNR_COUNTRY_REGION_TZ_MAP
          WHERE sap_country_cd = cur_bp_record.country_cd
          AND sap_region_code = cur_bp_record.region;

          LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp', 'Found timezone=' || l_timezone_cd || ' for country_cd=' || cur_bp_record.country_cd || ' and region=' || cur_bp_record.region);

        EXCEPTION
          WHEN no_data_found THEN
            LOGON_LOG.INFO('BPN: load_tlgns_sap_bp', 'No timezone found for country_cd=' || cur_bp_record.country_cd || ' and region=' || cur_bp_record.region || '. Lookup up without region code...');

            -- ok, it looks like we only have the mapping without region code. try again.
            BEGIN
              SELECT timezone_cd
              INTO l_timezone_cd
              FROM TLGNR_COUNTRY_REGION_TZ_MAP
              WHERE sap_country_cd = cur_bp_record.country_cd
              AND sap_region_code is NULL;

              LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp', 'Found timezone=' || l_timezone_cd || ' for country_cd=' || cur_bp_record.country_cd);

            EXCEPTION
              WHEN no_data_found THEN
                LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp', 'No timezone found for country_cd=' || cur_bp_record.country_cd);
                g_err_msg := 'No timezone found for country_cd=' || cur_bp_record.country_cd || ' and region=' || cur_bp_record.region;
                RAISE;
            END;
        END;

        l_insert := l_sap_bp_id = -1;

        -- insert a new bp
        IF l_insert THEN

          SELECT slgns_sap_bp.nextval
          INTO l_sap_bp_id
          FROM dual;

          LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp', ' Loading new sap bp (sap_id = ' || cur_bp_record.sap_id  || ' and sap_bp_id = ' || l_sap_bp_id || ') ...');

          INSERT INTO tlgns_sap_bp (
            sap_bp_id,
            sap_id,
            sap_ifc_id,
            marked_for_deletion,
            name1,
            name2,
            postal_code,
            city,
            region,
            street,
            customer_classification,
            bp_is_shipto,
            bp_is_soldto,
            bp_is_billto,
            bp_is_payer,
            bp_is_driver,
            lang_cd,
            sap_language_cd,
            account_group_cd,
            railway_station_id,
            country_cd,
            central_posting_block,
            central_delivery_block,
            central_order_block,
            treat_as_onetime_flag,
            chromat_reduction_months,
            phone_number,
            vat_registration_number,
            tax_number_1,
            timezone_cd,
            contact_name,
            contact_phone,
            ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
          VALUES (
            l_sap_bp_id,
            cur_bp_record.sap_id,
            cur_bp_record.sap_ifc_id,
            cur_bp_record.marked_for_deletion,
            cur_bp_record.name1,
            cur_bp_record.name2,
            cur_bp_record.postal_code,
            cur_bp_record.city,
            cur_bp_record.region,
            cur_bp_record.street,
            cur_bp_record.customer_classification,
            l_shipto,
            l_soldto,
            l_billto,
            l_payer,
            l_bp_is_driver,
            l_lang_cd,
            l_sap_lang_cd,
            cur_bp_record.account_group_cd,
            l_railway_station_id,
            cur_bp_record.country_cd,
            cur_bp_record.central_posting_block,
            cur_bp_record.central_delivery_block,
            cur_bp_record.central_order_block,
            l_treat_as_onetime_flag,
            l_chromat_reduction_months,
            cur_bp_record.telf1,
            cur_bp_record.vat_reg_number,
            cur_bp_record.tax_number_1,
            l_timezone_cd,
            cur_bp_record.contact_name,
            cur_bp_record.contact_phone,
            1, systimestamp, 1, systimestamp, 0);

        -- update bp
        ELSE

          LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp', ' Updating sap bp (sap_id = ' || cur_bp_record.sap_id  || ' and sap_bp_id = ' || l_sap_bp_id || ' ) ...');

          UPDATE tlgns_sap_bp
          SET sap_ifc_id = cur_bp_record.sap_ifc_id,
            update_blocked_by_hjid = NULL,
            marked_for_deletion = cur_bp_record.marked_for_deletion,
            name1 = cur_bp_record.name1,
            name2 = cur_bp_record.name2,
            postal_code = cur_bp_record.postal_code,
            city = cur_bp_record.city,
            region = cur_bp_record.region,
            street = cur_bp_record.street,
            customer_classification = cur_bp_record.customer_classification,
            bp_is_driver = l_bp_is_driver,
            lang_cd = l_lang_cd,
            sap_language_cd = l_sap_lang_cd,
            account_group_cd = cur_bp_record.account_group_cd,
            railway_station_id = l_railway_station_id,
            country_cd = cur_bp_record.country_cd,
            central_posting_block = cur_bp_record.central_posting_block,
            central_delivery_block = cur_bp_record.central_delivery_block,
            central_order_block = cur_bp_record.central_order_block,
            treat_as_onetime_flag = l_treat_as_onetime_flag,
            chromat_reduction_months = l_chromat_reduction_months,
            phone_number = cur_bp_record.telf1,
            vat_registration_number = cur_bp_record.vat_reg_number,
            tax_number_1 = cur_bp_record.tax_number_1,
            timezone_cd = l_timezone_cd,
            contact_name = cur_bp_record.contact_name,
            contact_phone = cur_bp_record.contact_phone,
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_id = cur_bp_record.sap_id;

        END IF;

        -- update sales area table
        load_tlgns_sap_bp_sarea(cur_bp_record.bp_hjid, l_sap_bp_id, l_shipto);
        -- update tlgns_sap_bp_companycode table
        load_tlgns_sap_bp_companycode(cur_bp_record.bp_hjid, l_sap_bp_id);

        -- mark missing sales areas as deleted
        IF l_full_update THEN
          mark_missing_sarea_as_deleted(cur_bp_record.bp_hjid, l_sap_bp_id);
          mark_missing_compcd_as_deleted(cur_bp_record.bp_hjid, l_sap_bp_id);
        END IF;
        
        -- delete related shipto sales area restrictions data by sap_bp_id
        IF cur_bp_record.marked_for_deletion = '1' THEN
          delete_all_ssrs(l_sap_bp_id);
        END IF;
        
        --update controlling attributes
        UPDATE ulgnsap.BPN_BUSINESSPARTNERNOTIFICAT_0
        SET sapimpstatuscd = c_pf_initial,
          sapimperrmsg = NULL,
          sapimppropagateddatelement = systimestamp
        WHERE hjid = cur_bp_record.bp_hjid;

        COMMIT;
        LOGON_LOG.INFO('BPN: load_tlgns_sap_bp',
          ' Sap BP (sap_id = ' || cur_bp_record.sap_id  || ' and sap_bp_id = ' || l_sap_bp_id || ') has been loaded/updated.');

        IF l_insert THEN
          l_ins_total := l_ins_total + 1;
        ELSE
          l_upd_total := l_upd_total + 1;
        END IF;

        EXCEPTION

          WHEN others THEN
          BEGIN

            ROLLBACK;
            LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp', SQLCODE, SQLERRM,
              ' ROLLBACK on sap bp (sap_id = ' || cur_bp_record.sap_id  || ', hjid = ' || cur_bp_record.bp_hjid || ' and sap_bp_id = ' || l_sap_bp_id || ').');

            l_sqlerrm := SQLERRM;

            IF l_final THEN
              l_new_state := c_notification_failed_final;
            ELSE
              l_new_state := c_notification_failed;
            END IF;

            -- update controlling attributes
            UPDATE ulgnsap.BPN_BUSINESSPARTNERNOTIFICAT_0
            SET sapimpstatuscd = l_new_state,
              sapimperrmsg = COALESCE(g_err_msg, l_sqlerrm),
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_bp_record.bp_hjid;

            -- update 'blocked by' field
            UPDATE TLGNS_SAP_BP bp
            SET bp.update_blocked_by_hjid = cur_bp_record.bp_hjid
            WHERE bp.sap_id = cur_bp_record.sap_id;

            COMMIT;

            IF l_final THEN
              LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp', ' Sap bp (sap_id = ' || cur_bp_record.sap_id  || ' and sap_bp_id = ' || l_sap_bp_id || ') has been abandoned. Days for successful import = ' || g_days_try_import || '.');
            END IF;

          END;

      END;

      --update older entries
      IF l_full_update THEN

        UPDATE (SELECT req.sapimpstatuscd, req.sapimppropagateddatelement, req.sapimpimporteddatelement
                FROM ulgnsap.BPN_BUSINESSPARTNERNOTIFICAT_0 req
                INNER JOIN ulgnsap.BPN_MASTERCUSTOMERBASICDATA master_customer_basic_data ON (req.MASTERCUSTOMERBASICDATA_BPN__0 = master_customer_basic_data.hjid)
                WHERE LTRIM(master_customer_basic_data.kunnr,'0') = cur_bp_record.sap_id) t
        SET t.sapimpstatuscd = c_notification_skipped,
          t.sapimppropagateddatelement = systimestamp
        WHERE t.sapimpstatuscd IN (c_notification_blocked, c_notification_failed)
          AND t.sapimpimporteddatelement < cur_bp_record.sapimpimporteddatelement;

        COMMIT;

      END IF;

      <<next_bp_record>>
      NULL;

    END LOOP; -- cur_bp_record

    LOGON_LOG.INFO('BPN: load_tlgns_sap_bp', 'FINISH! Updated ' || l_upd_total || ' and inserted ' || l_ins_total || ' records into ''tlgns_sap_bp'' table!');

    -- Load partner functions
    process_partner_functions();

    -- update bp_is_shipto/soldto/billto/payer flags according to db state
    update_pf_flags();

    -- set the run number and the time of the end of import
    LOGON_LOG.FINISH_IMPORT(c_import_name);

  END main;

  --------------------------------------------
  --------------------------------------------
  -----check partner function (staging area)--
  --------------------------------------------
  --------------------------------------------
  PROCEDURE check_partner_function(i_bp_hjid IN NUMBER, i_sap_id IN VARCHAR, io_shipto IN OUT NUMBER, io_soldto IN OUT NUMBER, io_billto IN OUT NUMBER, io_payer IN OUT NUMBER) AS

    -- query to select partner functions
    CURSOR cur_partner IS
    SELECT LTRIM(par0.kunn2, '0') sap_id,
      par0.parvw partner_function
    FROM ulgnsap.BPN_BUSINESSPARTNERNOTIFICAT_0 req
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERBASICDATA master_customer_basic_data ON (req.MASTERCUSTOMERBASICDATA_BPN__0  = master_customer_basic_data.hjid)
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERBASICDATA__1 srel ON (srel.PARENT_BPN_MASTERCUSTOMERBAS_0 = master_customer_basic_data.hjid)
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERSALESDATA sales ON (srel.CHILD_BPN_MASTERCUSTOMERSALE_0 = sales.hjid)
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERSALESDATA__0 rel ON (rel.PARENT_BPN_MASTERCUSTOMERSAL_0 = sales.hjid)
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERPARTNERROL_0 par0 ON (rel.CHILD_BPN_MASTERCUSTOMERPART_0 = par0.hjid)
    WHERE req.hjid = i_bp_hjid
      AND sales.loevm IS NULL
      AND par0.msgfn <> c_function_code_delete;

  BEGIN

    LOGON_LOG.DEBUG('BPN: check_partner_function', ' Checking partner function for sap_id = ' || i_sap_id || ' ...');

    FOR cur_partner_record IN cur_partner
    LOOP

      -- Check self-referencing partner functions (KNVP_KUNN2 = KNA1_KUNNR)
      -- Set the BP_IS_SHIPTO / BP_IS_SOLDTO / BP_IS_BILLTO / BP_IS_PAYER accordingly
      IF (cur_partner_record.sap_id = i_sap_id) THEN

        IF (cur_partner_record.partner_function = c_pf_shipto) THEN
          io_shipto := 1;
        ELSIF (cur_partner_record.partner_function = c_pf_soldto) THEN
          io_soldto := 1;
        ELSIF (cur_partner_record.partner_function = c_pf_billto) THEN
          io_billto := 1;
        ELSIF (cur_partner_record.partner_function = c_pf_payer) THEN
          io_payer := 1;
        END IF;

      END IF;

    END LOOP;

    LOGON_LOG.DEBUG('BPN: check_partner_function', ' Partner functions for sap_id = ' || i_sap_id || ': shipto = ' || io_shipto || ', soldto = ' || io_soldto || ', billto = ' || io_billto || ', payer = ' || io_payer || '.');

  END check_partner_function;

  --------------------------------------------
  --------------------------------------------
  -----tlgns_sap_bp_sarea---------------------
  --------------------------------------------
  --------------------------------------------
  PROCEDURE load_tlgns_sap_bp_sarea(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER, i_shipto IN NUMBER) AS

    CURSOR cur_sarea IS
    SELECT req.hjid bp_hjid,
      DECODE(sal0.aufsd, null, '0', '1') customer_order_block,
      sal0.kdgrp customer_group,
      'OUT' distribution_dest_cd,
      DECODE(sal0.lifsd, null, '0', '1') customer_delivery_block,
      DECODE(sal0.faksd, null, '0', '1') customer_posting_block,
      DECODE(sal0.loevm, 'X', '1', '0') marked_for_deletion,
      sal0.vkbur sales_office,
      sal0.vwerk default_plant_cd,
      sal0.waers currency_cd,
      /*sal0.zzazoll FIXME*/'' custom_office,
      sal0.bzirk sales_district,
      sal0.klabc abc_class,
      sal0.vkgrp sales_group,
      sal0.vkorg sorg_cd,
      sal0.vtweg distribution_channel,
      sal0.spart division_cd,
      sal0.kvgr4 customer_group_4
    FROM ulgnsap.BPN_BUSINESSPARTNERNOTIFICAT_0 req
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERBASICDATA master_customer_basic_data ON(req.MASTERCUSTOMERBASICDATA_BPN__0  = master_customer_basic_data.hjid)
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERBASICDATA__1 rel ON(rel.PARENT_BPN_MASTERCUSTOMERBAS_0 = master_customer_basic_data.hjid)
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERSALESDATA sal0 ON(rel.CHILD_BPN_MASTERCUSTOMERSALE_0 = sal0.hjid)
    WHERE req.hjid = i_bp_hjid;

    l_sarea_id NUMBER := NULL;
    l_sap_bp_sarea_id NUMBER := NULL;
    l_sap_bp_id NUMBER := NULL;
    l_ctl_act_cd NUMBER := NULL;

    -- common division: reference bp_sarea
    l_ref_sap_bp_sarea_id NUMBER := NULL;

    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;

  BEGIN

    LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_sarea', ' Loading data into ''tlgns_sap_bp_sarea'' table (hjid = ' || i_bp_hjid || ') ...');

    FOR cur_sarea_record IN cur_sarea
    LOOP

      l_sarea_id := NULL;
      l_ctl_act_cd := NULL;

      LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_sarea', '  Checking sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' || cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || ' ...');

      -- check whether sales area exists
      BEGIN

        SELECT sarea_id, ctl_act_cd
        INTO l_sarea_id, l_ctl_act_cd
        FROM TLGNC_SAREA
        WHERE sorg_cd = cur_sarea_record.sorg_cd
          AND distribution_channel = cur_sarea_record.distribution_channel
          AND division_cd = cur_sarea_record.division_cd;
          
        IF l_ctl_act_cd = 0 THEN
            -- no need to insert/update for inactive sales area
        	LOGON_LOG.DEBUG('BPN: PF: load_tlgns_sap_bp_sarea', 'i_bp_hjid: ' || i_bp_hjid || ' ,Skipping sales area because sales area is inactive for sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' ||  cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || '.');
        	CONTINUE;
              	
        END IF;

        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp_sarea', '  No sales area data found for sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' ||  cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || '.');
          g_err_msg := 'No sales area found for sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' ||  cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd;
          RAISE;

      END;

      -- check whether sap bp sales area exists
      BEGIN

        SELECT sap_bp_sarea_id, ref_sap_bp_sarea_id, sap_bp_id
        INTO l_sap_bp_sarea_id, l_ref_sap_bp_sarea_id, l_sap_bp_id
        FROM tlgns_sap_bp_sarea
        WHERE sap_bp_id = i_sap_bp_id
          AND sarea_id = l_sarea_id;

        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          -- new sap bp sales area will be loaded
          l_sap_bp_sarea_id := -1;
        WHEN OTHERS THEN
          LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp_sarea', SQLCODE, SQLERRM, '  Unknown Exception in selecting sap_bp_sarea_id from tlgns_spa_bp_sarea for sap_bp_id = ' || i_sap_bp_id|| ' and  sarea_id = ' ||  l_sarea_id || '.');
          RAISE;

      END;

      -- common division: delete all partner functions in case of a change from dependent to explicit bp sales area
      IF l_ref_sap_bp_sarea_id is not null and cur_sarea_record.marked_for_deletion = '0' THEN
        delete_all_pfs(l_sap_bp_sarea_id);
      END IF;
      
       
        -- delete related shipto sales area restrictions data by sap_bp_sarea_id
        IF cur_sarea_record.marked_for_deletion = '1' THEN
          delete_all_ssrs_by_sarea(l_sarea_id, l_sap_bp_id);
        END IF;

      -- insert a new sap bp sales area
      IF (l_sap_bp_sarea_id = -1) THEN

        SELECT slgns_sap_bp_sarea.nextval
        INTO l_sap_bp_sarea_id
        FROM dual;

        LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_sarea', '  Inserting a new sap_bp_sarea (sap_bp_id = ' || i_sap_bp_id|| ', sarea_id = ' ||  l_sarea_id || ', hjid = ' || i_bp_hjid || ', sales_office = ' || cur_sarea_record.sales_office || 'and sales_group = ' || cur_sarea_record.sales_group || ') ...');

        INSERT
        INTO tlgns_sap_bp_sarea (sap_bp_sarea_id,
          sap_bp_id,
          sarea_id,
          marked_for_deletion,
          sales_district,
          soffice_cd,
          sgroup_cd,
          customer_group,
          abc_class,
          currency_cd,
          distribution_dest_cd,
          customer_order_block,
          customer_delivery_block,
          customer_posting_block,
          custom_office,
          default_plant_cd,
          customer_group_4,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (l_sap_bp_sarea_id,
          i_sap_bp_id,
          l_sarea_id,
          cur_sarea_record.marked_for_deletion,
          cur_sarea_record.sales_district,
          get_sales_office(cur_sarea_record.sales_office),
          get_sales_group(cur_sarea_record.sales_group),
          cur_sarea_record.customer_group,
          cur_sarea_record.abc_class,
          cur_sarea_record.currency_cd,
          cur_sarea_record.distribution_dest_cd,
          cur_sarea_record.customer_order_block,
          cur_sarea_record.customer_delivery_block,
          cur_sarea_record.customer_posting_block,
          cur_sarea_record.custom_office,
          cur_sarea_record.default_plant_cd,
          cur_sarea_record.customer_group_4,
          1, systimestamp, 1, systimestamp, 0);

        LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_sarea', '  Sap_bp_sarea  (sap_bp_sarea_id = ' || l_sap_bp_sarea_id || ') was successfully inserted.');
        l_ins_total := l_ins_total + 1;

      -- update sap bp sales area
      ELSE

        LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_sarea', '  Updating sap_bp_sarea (sap_bp_sarea_id = ' || l_sap_bp_sarea_id || ', sap_bp_id = ' || i_sap_bp_id|| ', sarea_id = ' ||  l_sarea_id || ', hjid = ' || i_bp_hjid || ', sales_office = ' || cur_sarea_record.sales_office || ' and sales_group = ' || cur_sarea_record.sales_group || ') ...');

        UPDATE tlgns_sap_bp_sarea area
        SET area.ref_sap_bp_sarea_id = null,
          area.marked_for_deletion = cur_sarea_record.marked_for_deletion,
          area.sales_district = cur_sarea_record.sales_district,
          area.soffice_cd = get_sales_office(cur_sarea_record.sales_office),
          area.sgroup_cd = get_sales_group(cur_sarea_record.sales_group),
          area.customer_group = cur_sarea_record.customer_group,
          area.abc_class = cur_sarea_record.abc_class,
          area.currency_cd = cur_sarea_record.currency_cd,
          area.distribution_dest_cd = cur_sarea_record.distribution_dest_cd,
          area.customer_order_block = cur_sarea_record.customer_order_block,
          area.customer_delivery_block = cur_sarea_record.customer_delivery_block,
          area.customer_posting_block = cur_sarea_record.customer_posting_block,
          area.custom_office = cur_sarea_record.custom_office,
          area.default_plant_cd = cur_sarea_record.default_plant_cd,
          area.customer_group_4 = cur_sarea_record.customer_group_4,
          ctl_mod_uid = 1,
          ctl_mod_ts = systimestamp,
          ctl_tcn = ctl_tcn + 1
        WHERE area.sap_bp_sarea_id = l_sap_bp_sarea_id;

        LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_sarea', '  Sap_bp_sarea  (sap_bp_sarea_id = ' || l_sap_bp_sarea_id || ') was successfully updated.');
        l_upd_total := l_upd_total + 1;

      END IF;

      -- common division: insert/update dependent bp sales areas
      update_dependent_bp_sareas(i_bp_hjid, l_sap_bp_sarea_id, i_sap_bp_id, l_sarea_id);
		
	  
    END LOOP;

    LOGON_LOG.INFO('BPN: load_tlgns_sap_bp_sarea', ' Inserted ' || l_ins_total || ' and updated ' || l_upd_total || ' records in tlgns_sap_bp_sarea table.');

  END load_tlgns_sap_bp_sarea;

  -----------------------------------------------------------------------
  -----------------------------------------------------------------------
  -----get sales_office. if sales_group cannot be found then return INV--
  -----------------------------------------------------------------------
  -----------------------------------------------------------------------
  FUNCTION get_sales_office(i_sales_office IN VARCHAR2)
  RETURN VARCHAR2 IS

      o_sales_office VARCHAR2(4) := NULL;

  BEGIN

      IF (i_sales_office IS NULL) THEN
        RETURN NULL;
      END IF;

      SELECT sof.soffice_cd
      INTO o_sales_office
      FROM tlgnr_soffice_bs sof
      WHERE sof.soffice_cd = i_sales_office;

      RETURN o_sales_office;

      EXCEPTION
        WHEN no_data_found THEN
          LOGON_LOG.ERROR('BPN: get_sales_office', '  No sales office found for soffice_cd = ' || i_sales_office || '.');
          -- Returning c_invalid, which must exist in TLGNR_SOFFICE_BASE. This is a workaround needed to import BPs even when an invalid sales office is received.
          RETURN c_invalid;
        WHEN others THEN
          LOGON_LOG.ERROR('BPN: get_sales_office', SQLCODE, SQLERRM, '  Unknown exception in selecting soffice_cd from tlgnr_soffice_bs for soffice_cd = ' || i_sales_office);
          RAISE;

  END get_sales_office;

  -----------------------------------------------------------------------
  -----------------------------------------------------------------------
  -----get sales_group. if sales_group cannot be found then return INV---
  -----------------------------------------------------------------------
  -----------------------------------------------------------------------
  FUNCTION get_sales_group(i_sales_group IN VARCHAR2)
  RETURN VARCHAR2 IS

      o_sales_group VARCHAR2(4) := NULL;

  BEGIN

      IF (i_sales_group IS NULL) THEN
        RETURN NULL;
      END IF;

      SELECT sgr.sgroup_cd
      INTO o_sales_group
      FROM tlgnr_sgroup_bs sgr
      WHERE sgr.sgroup_cd = i_sales_group;

      RETURN o_sales_group;

      EXCEPTION
        WHEN no_data_found THEN
          LOGON_LOG.INFO('BPN: get_sales_group', '  No sales group found for sgroup_cd = ' || i_sales_group);
          -- Returning c_invalid, which must exist in TLGNR_SGROUP_BASE. This is a workaround needed to import BPs even when an invalid sales group is received.
          RETURN c_invalid;
        WHEN others THEN
          LOGON_LOG.ERROR('BPN: get_sales_group', SQLCODE, SQLERRM, '  Exception in selecting sgroup_cd from tlgnr_sgroup_bs for sgroup_cd = ' || i_sales_group);
          RAISE;

  END get_sales_group;

  --------------------------------------------
  --------------------------------------------
  -----tlgns_sap_bp_companycode---------------
  --------------------------------------------
  --------------------------------------------
  PROCEDURE load_tlgns_sap_bp_companycode(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER) AS

    CURSOR cur_bp_companycode IS
    SELECT notif.hjid bp_hjid,
      compcd.bukrs companycode_cd,
      decode(compcd.sperr, null, '0', '1') posting_block,
      decode(compcd.loevm, 'X', 1, 0) marked_for_deletion
    FROM ulgnsap.bpn_businesspartnernotificat_0 notif
    INNER JOIN ulgnsap.bpn_mastercustomerbasicdata master ON (notif.mastercustomerbasicdata_bpn__0 = master.hjid)
    INNER JOIN ulgnsap.bpn_mastercustomerbasicdata__3 master_compcd ON (master.hjid = master_compcd.parent_bpn_mastercustomerbas_0)
    INNER JOIN ulgnsap.bpn_mastercustomercompaniesc_0 compcd ON (master_compcd.child_bpn_mastercustomercomp_0 = compcd.hjid)
    WHERE notif.hjid = i_bp_hjid;

    l_compcd_exists NUMBER := NULL;
    l_sap_bp_compcd NUMBER := NULL;

    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;

  BEGIN

    LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_companycode', ' Loading data into TLGNS_SAP_BP_COMPANYCODE table (hjid = ' || i_bp_hjid || ') ...');

    FOR cur_bp_companycode_record IN cur_bp_companycode
    LOOP

      l_compcd_exists := NULL;

      LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_companycode', '  Checking companycode_cd = ' || cur_bp_companycode_record.companycode_cd || ' ...');

      -- check whether company code exists
      BEGIN

        SELECT ROWNUM
        INTO l_compcd_exists
        FROM TLGNR_COMPANYCODE_BS
        WHERE companycode_cd = cur_bp_companycode_record.companycode_cd;

        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp_companycode', '  Invalid company code ''' || cur_bp_companycode_record.companycode_cd || '''.');
          g_err_msg := 'Invalid company code ''' || cur_bp_companycode_record.companycode_cd || '''';
          RAISE;

      END;

      -- check whether sap bp companycode exists
      BEGIN

        SELECT sap_bp_compcd
        INTO l_sap_bp_compcd
        FROM tlgns_sap_bp_companycode
        WHERE sap_bp_id = i_sap_bp_id
          AND companycode_cd = cur_bp_companycode_record.companycode_cd;

        EXCEPTION
          WHEN no_data_found THEN
            -- new sap bp companycode will be loaded
            l_sap_bp_compcd := -1;
          WHEN others THEN
            LOGON_LOG.ERROR('BPN: load_tlgns_sap_bp_companycode', SQLCODE, SQLERRM, '  Unknown exception while selecting sap_bp_compcd from tlgns_sap_bp_companycode for sap_bp_id = ' || i_sap_bp_id || '.');
            RAISE;

      END;

      -- insert a new sap bp companycode
      IF (l_sap_bp_compcd = -1) THEN

        SELECT slgns_sap_bp_companycode.nextval
        INTO l_sap_bp_compcd
        FROM dual;

        LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_companycode', '  Inserting a new sap_bp_companycode (sap_bp_id = ' || i_sap_bp_id || ' and hjid = ' || i_bp_hjid || ') ...');

        INSERT
        INTO tlgns_sap_bp_companycode (sap_bp_compcd,
          sap_bp_id,
          companycode_cd,
          posting_block,
          marked_for_deletion,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (l_sap_bp_compcd,
          i_sap_bp_id,
          cur_bp_companycode_record.companycode_cd,
          cur_bp_companycode_record.posting_block,
          cur_bp_companycode_record.marked_for_deletion,
          1, systimestamp, 1, systimestamp, 0);

        LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_companycode', '  sap_bp_companycode (sap_bp_compcd = ' || l_sap_bp_compcd || ') was successfully inserted.');
        l_ins_total := l_ins_total + 1;

      -- update sap bp companycode
      ELSE

        LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_companycode', '  Updating sap_bp_companycode (sap_bp_compcd = ' || l_sap_bp_compcd || ', sap_bp_id = ' || i_sap_bp_id || ' and hjid = ' || i_bp_hjid || ') ...');

        UPDATE tlgns_sap_bp_companycode companycode
        SET companycode.companycode_cd = cur_bp_companycode_record.companycode_cd,
          companycode.posting_block = cur_bp_companycode_record.posting_block,
          companycode.marked_for_deletion = cur_bp_companycode_record.marked_for_deletion,
          ctl_mod_uid = 1,
          ctl_mod_ts = systimestamp,
          ctl_tcn = ctl_tcn + 1
        WHERE companycode.sap_bp_compcd = l_sap_bp_compcd;

        LOGON_LOG.DEBUG('BPN: load_tlgns_sap_bp_companycode', '  sap_bp_companycode (sap_bp_compcd = ' || l_sap_bp_compcd || ') was successfully updated.');
        l_upd_total := l_upd_total + 1;

      END IF;

    END LOOP;

    LOGON_LOG.INFO('BPN: load_tlgns_sap_bp_companycode', ' Inserted ' || l_ins_total || ' and updated ' || l_upd_total || ' records in tlgns_sap_bp_companycode table.');

  END load_tlgns_sap_bp_companycode;

  ---------------------------------------------
  ---------------------------------------------
  -----Sap_bp_sarea entries which are not------
  -----present in the staging area anymore-----
  -----must be marked as deleted---------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE mark_missing_sarea_as_deleted(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER) AS

    --find tlgns_sap_bp_sarea entries which are not present in the request anymore
    CURSOR sareas_to_mark_as_deleted IS
    SELECT sap_bp_sarea_id, sarea_id
    FROM tlgns_sap_bp_sarea
    WHERE sarea_id NOT IN
  		(SELECT bs.sarea_id FROM TLGNC_SAREA bs
       INNER JOIN ulgnsap.BPN_MASTERCUSTOMERSALESDATA sal0 ON (sorg_cd = sal0.vkorg AND distribution_channel = sal0.vtweg AND division_cd = sal0.spart)
       INNER JOIN ulgnsap.bpn_mastercustomerbasicdata__1 rel ON (rel.child_bpn_mastercustomersale_0 = sal0.hjid)
       INNER JOIN ulgnsap.BPN_MASTERCUSTOMERBASICDATA master_customer_basic_data ON (rel.PARENT_BPN_MASTERCUSTOMERBAS_0  = master_customer_basic_data.hjid)
       INNER JOIN ulgnsap.BPN_BUSINESSPARTNERNOTIFICAT_0 req ON (req.MASTERCUSTOMERBASICDATA_BPN__0  = master_customer_basic_data.hjid)
       WHERE req.hjid = i_bp_hjid)
    AND sap_bp_id = i_sap_bp_id
    AND ref_sap_bp_sarea_id is null
    AND marked_for_deletion = '0';

  BEGIN

    LOGON_LOG.DEBUG('BPN: mark_missing_sarea_as_deleted', ' Marking missing sales areas as deleted ...');

    FOR cur_sarea_to_delete IN sareas_to_mark_as_deleted
    LOOP

	    BEGIN

          LOGON_LOG.INFO('BPN: mark_missing_sarea_as_deleted', '  Marking sarea (sap_bp_sarea_id = ' || cur_sarea_to_delete.sap_bp_sarea_id || ') as deleted.');
          UPDATE tlgns_sap_bp_sarea SET
            marked_for_deletion = '1',
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_bp_sarea_id = cur_sarea_to_delete.sap_bp_sarea_id;

          LOGON_LOG.INFO('BPN: mark_missing_sarea_as_deleted', '  Delete all PFs of that sales area.');

          delete_all_pfs(cur_sarea_to_delete.sap_bp_sarea_id);

      END;

      -- common division: mark dependent bp sales areas
      delete_dependent_sareas(i_bp_hjid, i_sap_bp_id, cur_sarea_to_delete.sarea_id);

    END LOOP;

    LOGON_LOG.DEBUG('BPN: mark_missing_sarea_as_deleted', ' Done marking missing sales areas as deleted.');

  END mark_missing_sarea_as_deleted;

  ---------------------------------------------
  ---------------------------------------------
  -----Sap_bp_companycode entries which are----
  -----not present in the staging area---------
  -----anymore must be marked as deleted-------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE mark_missing_compcd_as_deleted(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER) AS

    --find tlgns_sap_bp_companycode entries which are not present in the request anymore
    CURSOR compcds_to_mark_as_deleted IS
    SELECT sap_bp_compcd
    FROM TLGNS_SAP_BP_COMPANYCODE
    WHERE sap_bp_compcd NOT IN (
      SELECT db.sap_bp_compcd
      FROM ULGNSAP.BPN_BUSINESSPARTNERNOTIFICAT_0 req
      INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERBASICDATA master_customer_basic_data ON (req.mastercustomerbasicdata_bpn__0 = master_customer_basic_data.hjid)
      INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERBASICDATA__3 rel ON (master_customer_basic_data.hjid = rel.parent_bpn_mastercustomerbas_0)
      INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERCOMPANIESC_0 compcd ON (rel.child_bpn_mastercustomercomp_0 = compcd.hjid)
      INNER JOIN TLGNS_SAP_BP_COMPANYCODE db ON (compcd.bukrs = db.companycode_cd)
      WHERE req.hjid = i_bp_hjid
    )
      AND sap_bp_id = i_sap_bp_id
      AND marked_for_deletion = '0';

  BEGIN

    LOGON_LOG.DEBUG('BPN: mark_missing_compcd_as_deleted', ' Marking missing company codes as deleted ...');

    FOR cur_compcd_to_delete IN compcds_to_mark_as_deleted
    LOOP

	    BEGIN

          LOGON_LOG.INFO('MN: mark_missing_compcd_as_deleted', '  Marking company code (sap_bp_compcd = ' || cur_compcd_to_delete.sap_bp_compcd || ') as deleted.');

          UPDATE TLGNS_SAP_BP_COMPANYCODE SET
            marked_for_deletion = '1',
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_bp_compcd = cur_compcd_to_delete.sap_bp_compcd;

      END;

    END LOOP;

    LOGON_LOG.DEBUG('BPN: mark_missing_compcd_as_deleted', ' Done marking missing company codes as deleted.');

  END mark_missing_compcd_as_deleted;

---

  ---------------------------------------------
  ---------------------------------------------
  -----load partner functions------------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE process_partner_functions AS

    -- requests
    CURSOR cur_sap_bp IS
    SELECT req.hjid bp_hjid,
      req.sapimpimporteddatelement,
      master_customer_basic_data.msgfn msgfn,
      master_customer_basic_data.ktokd account_group_cd,
      LTRIM(master_customer_basic_data.kunnr,'0') sap_id
    FROM ulgnsap.BPN_BUSINESSPARTNERNOTIFICAT_0 req
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERBASICDATA master_customer_basic_data ON(req.MASTERCUSTOMERBASICDATA_BPN__0  = master_customer_basic_data.hjid)
    WHERE req.sapimpstatuscd IN (c_pf_initial, c_pf_failed)
    ORDER BY req.sapimpimporteddatelement;

    -- sales areas
    CURSOR cur_sarea(v_bp_hjid IN NUMBER) IS
    SELECT sales.hjid sarea_hjid,
      sales.msgfn msgfn,
      sales.vkorg sorg_cd,
      sales.vtweg distribution_channel,
      sales.spart division_cd,
      DECODE(sales.loevm, 'X', '1', '0') marked_for_deletion
    FROM ulgnsap.BPN_BUSINESSPARTNERNOTIFICAT_0 req
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERBASICDATA master_customer_basic_data ON(req.MASTERCUSTOMERBASICDATA_BPN__0  = master_customer_basic_data.hjid)
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERBASICDATA__1 rel ON(rel.PARENT_BPN_MASTERCUSTOMERBAS_0 = master_customer_basic_data.hjid)
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERSALESDATA sales ON(rel.CHILD_BPN_MASTERCUSTOMERSALE_0 = sales.hjid)
    WHERE req.hjid = v_bp_hjid;

    -- partner functions
    CURSOR cur_partner(v_sarea_hjid IN NUMBER) IS
    SELECT par0.msgfn,
      LTRIM(par0.kunn2,'0') sap_id,
      par0.parvw partner_function
    FROM ulgnsap.BPN_MASTERCUSTOMERSALESDATA sales
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERSALESDATA__0 salesrel ON(salesrel.PARENT_BPN_MASTERCUSTOMERSAL_0 = sales.hjid)
    INNER JOIN ulgnsap.BPN_MASTERCUSTOMERPARTNERROL_0 par0 ON(salesrel.CHILD_BPN_MASTERCUSTOMERPART_0 = par0.hjid)
    WHERE SALES.HJID = V_SAREA_HJID
    	AND par0.parvw in ('AG', 'WE', 'RE', 'RG'); -- LOGON-6521

    l_final BOOLEAN := FALSE;
    l_new_state NUMBER := NULL;
    l_sqlerrm VARCHAR2(200) := NULL;

    l_full_update     BOOLEAN := FALSE;
    l_pf_received     BOOLEAN := FALSE;  -- needed to decide whether to delete missing pf's
    l_sarea_received  BOOLEAN := FALSE;  -- needed to determine the next notification state

    l_last_successful NUMBER := NULL;
    l_last_failed NUMBER := NULL;

    l_min_ls NUMBER := NULL;  -- minimum hjid of last successfull notification
    l_max_ls NUMBER := NULL;  -- maximum hjid of last successfull notification
    l_min_lf NUMBER := NULL;  -- minimum hjid of last failed notification

    l_sarea_id NUMBER := NULL;
    l_ctl_act_cd NUMBER := NULL;

    -- notation: owner --> pf
    l_sap_bp_id_owner NUMBER := NULL;
    l_sap_bp_id_pf NUMBER := NULL;
    l_sap_bp_sarea_id_owner NUMBER:= NULL;
    l_sap_bp_sarea_id_pf NUMBER:= NULL;

    l_invent_soldto_for_shipto NUMBER := NULL;
    l_invent_billto_for_shipto NUMBER := NULL;
    l_invent_payer_for_shipto NUMBER := NULL;

    invalid_partner_record  EXCEPTION;

  BEGIN

    LOGON_LOG.INFO('BPN: PF: process_partner_functions', 'START loading partner functions ...');

    FOR cur_bp_record IN cur_sap_bp
    LOOP

      BEGIN

        LOGON_LOG.DEBUG('BPN: PF: process_partner_functions', ' Processing sap_id = ' || cur_bp_record.sap_id || ' and hjid = ' || cur_bp_record.bp_hjid || ' ...');

        l_final := EXTRACT(DAY FROM systimestamp - cur_bp_record.sapimpimporteddatelement) > g_days_try_import;

        g_err_msg := NULL;

        -- select owner sap_bp_id from tlgns_sap_bp
        l_sap_bp_id_owner := NULL;
        get_sap_bp_id(cur_bp_record.sap_id, l_sap_bp_id_owner);

        IF (l_sap_bp_id_owner IS NULL) THEN

          LOGON_LOG.ERROR('BPN: PF: process_partner_functions',   '  Skipping sap bp (sap_id = ' || cur_bp_record.sap_id || ' and hjid = ' || cur_bp_record.bp_hjid || ') because no sap_bp_id found for sap_id ''' || cur_bp_record.sap_id || '''.');

          IF l_final THEN
            l_new_state := c_pf_failed_final;
          ELSE
            l_new_state := c_pf_failed;
          END IF;

          UPDATE ULGNSAP.BPN_BUSINESSPARTNERNOTIFICAT_0
          SET sapimpstatuscd = l_new_state,
            sapimperrmsg = 'Business partner does not exist',
            sapimppropagateddatelement = systimestamp
          WHERE hjid = cur_bp_record.bp_hjid;

          COMMIT;
          --CONTINUE;
          GOTO next_bp_record;

        END IF;

        l_min_ls := NULL;
        l_max_ls := -1;
        l_min_lf := NULL;

        l_sarea_received := FALSE;

        FOR cur_sarea_record IN cur_sarea(cur_bp_record.bp_hjid)
        LOOP

          BEGIN

            LOGON_LOG.DEBUG('BPN: PF: process_partner_functions', '   Processing sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' || cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || ' ...');

            l_sarea_received := TRUE;

            -- check whether full or partial update (or invalid)
            CASE cur_sarea_record.msgfn
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

                  LOGON_LOG.ERROR('BPN: PF: process_partner_functions', '   Invalid function code ''' || cur_sarea_record.msgfn || ''', skipping sales area.');

--------------------------------------------------------------------------------
                  l_min_ls := -1;
                  IF (l_min_lf IS NULL) OR (cur_bp_record.bp_hjid < l_min_lf) THEN
                    l_min_lf := cur_bp_record.bp_hjid;
                  END IF;
--------------------------------------------------------------------------------

                  --CONTINUE;
                  GOTO next_sarea_record;

                END;
            END CASE;

            -- do not do anything for deleted sales areas
            IF cur_sarea_record.marked_for_deletion = '1' THEN
              GOTO next_sarea_record;
            END IF;

            --check whether sales area exists
            l_sarea_id := NULL;
            l_ctl_act_cd := NULL;

            BEGIN

              SELECT sarea_id, ctl_act_cd
              INTO l_sarea_id, l_ctl_act_cd
              FROM TLGNC_SAREA
              WHERE sorg_cd = cur_sarea_record.sorg_cd
               AND distribution_channel = cur_sarea_record.distribution_channel
               AND division_cd = cur_sarea_record.division_cd;
				
              IF l_ctl_act_cd = 0 THEN
              
              	LOGON_LOG.INFO('BPN: PF: process_partner_functions', 'Business partner hjid: ' || cur_bp_record.bp_hjid || '  ,Skipping sales area because sales area is inactive for sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' ||  cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || '.');
              	GOTO skip_inactive_sarea;
              	
              END IF;
               
              EXCEPTION
              WHEN no_data_found THEN
                LOGON_LOG.ERROR('BPN: PF: process_partner_functions', '   No sales area data found for sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' ||  cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || '.');
              WHEN too_many_rows THEN
                LOGON_LOG.ERROR('BPN: PF: process_partner_functions', SQLCODE, SQLERRM, '   Too many sales areas for sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' ||  cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || '.');
              WHEN others THEN
                LOGON_LOG.ERROR('BPN: PF: process_partner_functions', SQLCODE, SQLERRM, '   Unknown exception in selecting sarea_id from TLGNC_SAREA for sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' ||  cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || '.');

            END;

            IF l_sarea_id IS NULL THEN

              LOGON_LOG.ERROR('BPN: PF: process_partner_functions', '   Skipping sales area (sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' ||  cur_sarea_record.distribution_channel || ', division_cd = ' || cur_sarea_record.division_cd || ' and hjid = ' || cur_bp_record.bp_hjid || ') because no sarea_id found.');
              g_err_msg := 'Sales area with sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' ||  cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || ' does not exist';

--------------------------------------------------------------------------------
              l_min_ls := -1;
              IF (l_min_lf IS NULL) OR (cur_bp_record.bp_hjid < l_min_lf) THEN
                l_min_lf := cur_bp_record.bp_hjid;
              END IF;
--------------------------------------------------------------------------------

              --CONTINUE;
              GOTO next_sarea_record;

            END IF;

            -- select sap_bp_sarea_id owner from tlgns_sap_bp_sarea
            l_sap_bp_sarea_id_owner := NULL;
            get_sap_bp_sarea_id(l_sap_bp_id_owner, l_sarea_id, l_sap_bp_sarea_id_owner);

            IF l_sap_bp_sarea_id_owner IS NULL THEN

              LOGON_LOG.ERROR('BPN: PF: process_partner_functions', '   Skipping sales area (sap_id = ' || cur_bp_record.sap_id || ', sarea_id = ' || l_sarea_id || ' and hjid = ' || cur_bp_record.bp_hjid || ') because no sap_bp_sarea_id found.');
              g_err_msg := 'No bp sales area found for sap_id = ' || cur_bp_record.sap_id || ' and sarea_id = ' || l_sarea_id;

--------------------------------------------------------------------------------
              l_min_ls := -1;
              IF (l_min_lf IS NULL) OR (cur_bp_record.bp_hjid < l_min_lf) THEN
                l_min_lf := cur_bp_record.bp_hjid;
              END IF;
--------------------------------------------------------------------------------

              --CONTINUE;
              GOTO next_sarea_record;

            END IF;

            -- retrieve 'last successful' and 'last failed' values
            SELECT sarea.last_successful_hjid,
              sarea.last_failed_hjid
            INTO l_last_successful,
              l_last_failed
            FROM TLGNS_SAP_BP_SAREA sarea
            WHERE sarea.sap_bp_sarea_id = l_sap_bp_sarea_id_owner;

            IF (l_last_successful IS NOT NULL OR l_last_failed IS NOT NULL)
              AND (l_last_successful IS NULL OR l_last_successful >= cur_bp_record.bp_hjid)
              AND (l_last_failed IS NULL OR l_last_failed > cur_bp_record.bp_hjid) THEN

              LOGON_LOG.DEBUG('BPN: PF: process_partner_functions', '   Skipping sales area since a newer version is available or it has already been imported.');

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
              GOTO next_sarea_record;

            END IF;

            l_pf_received := FALSE;
            FOR cur_partner_record IN cur_partner(cur_sarea_record.sarea_hjid)
            LOOP

              BEGIN

                LOGON_LOG.DEBUG('BPN: PF: process_partner_functions', '    Processing parvw = ' || cur_partner_record.partner_function || ' and kunn2 = ' || cur_partner_record.sap_id || ' ...');

                l_pf_received := TRUE;

                l_sap_bp_id_pf := NULL;
                get_sap_bp_id(cur_partner_record.sap_id, l_sap_bp_id_pf);

                IF (l_sap_bp_id_pf IS NULL) THEN

                  LOGON_LOG.ERROR('BPN: PF: process_partner_functions', '    Referenced bp not found (kunn2 = ' || cur_partner_record.sap_id || ' and hjid = ' || cur_bp_record.bp_hjid || ').');
                  g_err_msg := 'Referenced bp ''' || cur_partner_record.sap_id || ''' not found';
                  RAISE invalid_partner_record;

                END IF;

                l_sap_bp_sarea_id_pf := NULL;
                get_sap_bp_sarea_id(l_sap_bp_id_pf, l_sarea_id, l_sap_bp_sarea_id_pf);

                IF (l_sap_bp_sarea_id_pf IS NULL) THEN

                  LOGON_LOG.ERROR('BPN: PF: process_partner_functions', '    Referenced bp sales area not found (kunn2 = ' || cur_partner_record.sap_id || ', sarea_id = ' || l_sarea_id || ' and hjid = ' || cur_bp_record.bp_hjid || ').');
                  g_err_msg := 'Referenced bp sales area not found for kunn2 = ' || cur_partner_record.sap_id || ' and sarea_id = ' || l_sarea_id;
                  RAISE invalid_partner_record;

                END IF;

                IF cur_partner_record.msgfn IN (c_function_code_change, c_function_code_resend, c_function_code_replace, c_function_code_original) THEN

                  -- load data into relation tables (tlgns_sap_bp_pf_shipto, tlgns_sap_bp_pf_soldto, tlgns_sap_bp_pf_billto and tlgns_sap_bp_pf_payer)
                  load_partner_functions(l_sap_bp_sarea_id_owner, l_sap_bp_sarea_id_pf, l_sarea_id, cur_partner_record.partner_function);

                ELSIF cur_partner_record.msgfn = c_function_code_delete THEN

                  LOGON_LOG.DEBUG('BPN: PF: load_parner_functions', '    Deleting partner function (kunn2 = ' || cur_partner_record.sap_id || ' and partner_function = ' || cur_partner_record.partner_function || ') ...');

                  -- delete partner function
                  CASE cur_partner_record.partner_function
                  WHEN c_pf_shipto THEN

                    DELETE FROM TLGNS_SAP_BP_PF_SHIPTO st
                    WHERE st.sap_bp_sarea_id = l_sap_bp_sarea_id_owner
                      AND st.shipto_sap_bp_sarea_id = l_sap_bp_sarea_id_pf
                      AND st.sarea_id = l_sarea_id;

                    COMMIT;

                  WHEN c_pf_soldto THEN

                    DELETE FROM TLGNS_SAP_BP_PF_SOLDTO st
                    WHERE st.sap_bp_sarea_id = l_sap_bp_sarea_id_owner
                      AND st.soldto_sap_bp_sarea_id = l_sap_bp_sarea_id_pf
                      AND st.sarea_id = l_sarea_id;

                    COMMIT;

                  WHEN c_pf_billto THEN

                    DELETE FROM TLGNS_SAP_BP_PF_BILLTO bt
                    WHERE bt.sap_bp_sarea_id = l_sap_bp_sarea_id_owner
                      AND bt.billto_sap_bp_sarea_id = l_sap_bp_sarea_id_pf
                      AND bt.sarea_id = l_sarea_id;

                    COMMIT;

                  WHEN c_pf_payer THEN

                    DELETE FROM TLGNS_SAP_BP_PF_PAYER payer
                    WHERE payer.sap_bp_sarea_id = l_sap_bp_sarea_id_owner
                      AND payer.payer_sap_bp_sarea_id = l_sap_bp_sarea_id_pf
                      AND payer.sarea_id = l_sarea_id;

                    COMMIT;

                  ELSE

                    LOGON_LOG.DEBUG('BPN: PF: process_partner_functions', '    Nothing to be deleted (partner function ''' || cur_partner_record.partner_function || ''').');

                  END CASE;

                ELSE

                  LOGON_LOG.ERROR('BPN: PF: process_partner_functions', '    Invalid function code ''' || cur_partner_record.msgfn || '''.');
                  RAISE invalid_partner_record;

                END IF;

              END;

            END LOOP; -- cur_partner_record

            -- common division: sync dependent sales areas
            sync_dependent_pfs(l_sap_bp_sarea_id_owner, l_sap_bp_id_owner, l_sarea_id);

            -- update 'last successful' and 'last_failed' fields
            UPDATE TLGNS_SAP_BP_SAREA
            SET last_successful_hjid = cur_bp_record.bp_hjid,
              last_failed_hjid = NULL
            WHERE sap_bp_sarea_id = l_sap_bp_sarea_id_owner;

            COMMIT;
            
            -- delete missing partner functions
            IF l_pf_received OR l_full_update THEN
              delete_missing_pf(l_sap_bp_sarea_id_owner, cur_sarea_record.sarea_hjid);
            END IF;

            -- invent sold-to/bill-to/payer partner functions according to account group
            invent_soldto_billto_payer_pf(cur_bp_record.account_group_cd, l_sap_bp_sarea_id_owner);

--------------------------------------------------------------------------------
			--LOGON-9335: In case there was an inactive sales area we have to skip processing the partner function, 
			--we take it as if it was successfull to avoid processing again and again for an inactive sales area.
            <<skip_inactive_sarea>>
            
            IF (l_min_ls IS NULL) OR (cur_bp_record.bp_hjid < l_min_ls) THEN
              l_min_ls := cur_bp_record.bp_hjid;
            END IF;
            IF cur_bp_record.bp_hjid > l_max_ls THEN
              l_max_ls := cur_bp_record.bp_hjid;
            END IF;
--------------------------------------------------------------------------------

            EXCEPTION
            WHEN others THEN
            BEGIN

              ROLLBACK;
              LOGON_LOG.ERROR('BPN: PF: process_partner_functions', SQLCODE, SQLERRM, '   ROLLBACK on sorg_cd = ' || cur_sarea_record.sorg_cd || ', distribution_channel = ' || cur_sarea_record.distribution_channel || ' and division_cd = ' || cur_sarea_record.division_cd || '.');

              l_sqlerrm := SQLERRM;

              UPDATE TLGNS_SAP_BP_SAREA
              SET last_successful_hjid = NULL,
                last_failed_hjid = cur_bp_record.bp_hjid
              WHERE sap_bp_sarea_id = l_sap_bp_sarea_id_owner;

              COMMIT;

--------------------------------------------------------------------------------
              l_min_ls := -1;
              IF (l_min_lf IS NULL) OR (cur_bp_record.bp_hjid < l_min_lf) THEN
                l_min_lf := cur_bp_record.bp_hjid;
              END IF;
--------------------------------------------------------------------------------

            END;

          END;

          <<next_sarea_record>>
          NULL;

        END LOOP; -- cur_sarea_record

        -- implementation of decision table for next state
        l_new_state := NULL;
        IF NOT l_sarea_received THEN

          l_new_state := c_imported_to_logon;
          g_err_msg := NULL;

        ELSIF l_min_lf = cur_bp_record.bp_hjid THEN

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

        ELSIF l_min_ls = cur_bp_record.bp_hjid THEN

          l_new_state := c_imported_to_logon;
          g_err_msg := NULL;

        ELSE

          l_new_state := c_pf_skipped;
          g_err_msg := NULL;

        END IF;

        -- apply new state
        UPDATE ULGNSAP.BPN_BUSINESSPARTNERNOTIFICAT_0
        SET sapimpstatuscd = l_new_state,
          sapimperrmsg = g_err_msg,
          sapimppropagateddatelement = SYSTIMESTAMP
        WHERE hjid = cur_bp_record.bp_hjid;

        COMMIT;

      END;

      <<next_bp_record>>
      NULL;

    END LOOP; -- cur_bp_record

    LOGON_LOG.INFO('BPN: PF: process_partner_functions', 'FINISH loading Partner Functions data!');

  END process_partner_functions;

  ---------------------------------------------
  ---------------------------------------------
  -----get sap_bp_id---------------------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE get_sap_bp_id(i_sap_id IN VARCHAR2, o_sap_bp_id OUT NUMBER) AS

  BEGIN

      SELECT bp.sap_bp_id
      INTO o_sap_bp_id
      FROM tlgns_sap_bp bp
      WHERE bp.sap_id = i_sap_id;

      EXCEPTION
        WHEN others THEN
        LOGON_LOG.ERROR('BPN: PF: get_sap_bp_id', SQLCODE, SQLERRM, '    Cannot find sap_bp_id for a given sap_id = ' || i_sap_id || ' because it has not been imported!');

  END get_sap_bp_id;

  ---------------------------------------------
  ---------------------------------------------
  -----get sap_bp_sarea_id---------------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE get_sap_bp_sarea_id(i_sap_bp_id IN NUMBER, i_sarea_id IN NUMBER, o_sap_bp_sarea_id OUT NUMBER) AS

  BEGIN

      SELECT area.sap_bp_sarea_id
      INTO o_sap_bp_sarea_id
      FROM tlgns_sap_bp_sarea area
      WHERE area.sap_bp_id = i_sap_bp_id
        AND area.sarea_id = i_sarea_id;

      EXCEPTION
        WHEN others THEN
        LOGON_LOG.ERROR('BPN: PF: get_sap_bp_sarea_id', SQLCODE, SQLERRM, '    Cannot find sap_bp_sarea_id for a given sap_bp_id = ' || i_sap_bp_id || ' and sarea_id = ' || i_sarea_id || ' because it has not been imported!');

  END get_sap_bp_sarea_id;

  ---------------------------------------------
  ---------------------------------------------
  -----load data into relation tables----------
  ----- - tlgns_sap_bp_pf_shipto---------------
  ----- - tlgns_sap_bp_pf_soldto---------------
  ----- - tlgns_sap_bp_pf_billto---------------
  ----- - tlgns_sap_bp_pf_payer----------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE load_partner_functions(i_sap_bp_sarea_id_owner IN NUMBER, i_sap_bp_sarea_id_pf IN NUMBER, i_sarea_id IN NUMBER, i_partner_function IN VARCHAR) AS

    l_rel_exists NUMBER := NULL;

  BEGIN

    LOGON_LOG.DEBUG('BPN: PF: load_partner_functions', '    Checking partner function (' || i_partner_function || ') where sap_bp_sarea_id_owner = ' || i_sap_bp_sarea_id_owner || ', sap_bp_sarea_id_pf = '|| i_sap_bp_sarea_id_pf || ' and sarea_id = ' || i_sarea_id || ' ...');

    -- shipto
    IF (i_partner_function = c_pf_shipto) THEN

      -- check whether relation exists
      BEGIN

        SELECT rownum
        INTO l_rel_exists
        FROM tlgns_sap_bp_pf_shipto ship
        WHERE ship.sap_bp_sarea_id = i_sap_bp_sarea_id_owner
          AND ship.shipto_sap_bp_sarea_id = i_sap_bp_sarea_id_pf
          AND ship.sarea_id = i_sarea_id;

        EXCEPTION
        WHEN no_data_found THEN
          -- new relation will be loaded
          l_rel_exists := -1;
        WHEN others THEN
          LOGON_LOG.ERROR('BPN: PF: load_partner_functions', SQLCODE, SQLERRM, '    Unknown exception in selecting sap_bp_sarea_id from tlgns_sap_bp_pf_shipto where sap_bp_sarea_id_owner = ' || i_sap_bp_sarea_id_owner || ' sap_bp_sarea_id_pf = '|| i_sap_bp_sarea_id_pf || ' and sarea_id = ' || i_sarea_id);
          RAISE;

      END;

      --insert new PF
      IF (l_rel_exists = -1) THEN

        LOGON_LOG.DEBUG('BPN: PF: load_partner_functions', '    Inserting a new sap_bp_pf_shipto (sap_bp_sarea_id = ' || i_sap_bp_sarea_id_owner || ', shipto_sap_bp_sarea_id = ' || i_sap_bp_sarea_id_pf || ' and sarea_id = ' || i_sarea_id || ') ...');

        INSERT
        INTO TLGNS_SAP_BP_PF_SHIPTO(sap_bp_sarea_id,
          shipto_sap_bp_sarea_id,
          sarea_id,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (i_sap_bp_sarea_id_owner,
          i_sap_bp_sarea_id_pf,
          i_sarea_id,
          1, systimestamp, 1, systimestamp, 0);

      END IF;

    -- soldto
    ELSIF (i_partner_function = c_pf_soldto) THEN

      -- check whether relation exists
      BEGIN

        SELECT rownum
        INTO l_rel_exists
        FROM tlgns_sap_bp_pf_soldto sold
        WHERE sold.sap_bp_sarea_id = i_sap_bp_sarea_id_owner
          AND sold.soldto_sap_bp_sarea_id = i_sap_bp_sarea_id_pf
          AND sold.sarea_id = i_sarea_id;

        EXCEPTION
        WHEN no_data_found THEN
          -- new relation will be loaded
          l_rel_exists := -1;
        WHEN others THEN
          LOGON_LOG.ERROR('BPN: PF: load_partner_functions', SQLCODE, SQLERRM, '    Unknown exception in selecting sap_bp_sarea_id from tlgns_sap_bp_pf_soldto where sap_bp_sarea_id_owner = ' || i_sap_bp_sarea_id_owner || ' sap_bp_sarea_id_pf = '|| i_sap_bp_sarea_id_pf || ' and sarea_id = ' || i_sarea_id);
          RAISE;

      END;

      --insert new PF
      IF (l_rel_exists = -1) THEN

        LOGON_LOG.DEBUG('BPN: PF: load_partner_functions', '    Inserting a new sap_bp_pf_soldto (sap_bp_sarea_id = ' || i_sap_bp_sarea_id_owner || ', soldto_sap_bp_sarea_id = ' || i_sap_bp_sarea_id_pf || ' and sarea_id = ' || i_sarea_id || ') ...');

        INSERT
        INTO TLGNS_SAP_BP_PF_SOLDTO (sap_bp_sarea_id,
          soldto_sap_bp_sarea_id,
          sarea_id,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (i_sap_bp_sarea_id_owner,
          i_sap_bp_sarea_id_pf,
          i_sarea_id,
          1, systimestamp, 1, systimestamp, 0);

      END IF;

    -- billto
    ELSIF (i_partner_function = c_pf_billto) THEN

      -- check whether relation exists
      BEGIN

        SELECT rownum
        INTO l_rel_exists
        FROM tlgns_sap_bp_pf_billto bill
        WHERE bill.sap_bp_sarea_id = i_sap_bp_sarea_id_owner
          AND bill.billto_sap_bp_sarea_id = i_sap_bp_sarea_id_pf
          AND bill.sarea_id = i_sarea_id;

        EXCEPTION
        WHEN no_data_found THEN
          --new relation will be loaded
          l_rel_exists := -1;
        WHEN others THEN
          LOGON_LOG.ERROR('BPN: PF: load_partner_functions', SQLCODE, SQLERRM, '    Unknown exception in selecting sap_bp_sarea_id from tlgns_sap_bp_pf_billto for sap_bp_sarea_id_owner = ' || i_sap_bp_sarea_id_owner || ' sap_bp_sarea_id_pf = '|| i_sap_bp_sarea_id_pf || ' and sarea_id = ' || i_sarea_id);
          RAISE;

      END;

      -- insert new PF
      IF (l_rel_exists = -1) THEN

        LOGON_LOG.DEBUG('BPN: PF: load_partner_functions', '    Inserting a new sap_bp_pf_billto (sap_bp_sarea_id = ' || i_sap_bp_sarea_id_owner || ', billto_sap_bp_sarea_id = ' || i_sap_bp_sarea_id_pf || ' and sarea_id = ' || i_sarea_id || ') ...');

        INSERT
        INTO TLGNS_SAP_BP_PF_BILLTO(sap_bp_sarea_id,
          billto_sap_bp_sarea_id,
          sarea_id,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (i_sap_bp_sarea_id_owner,
          i_sap_bp_sarea_id_pf,
          i_sarea_id,
          1, systimestamp, 1, systimestamp, 0);

      END IF;

     -- payer
    ELSIF (i_partner_function = c_pf_payer) THEN

      -- check whether relation exists
      BEGIN

        SELECT rownum
        INTO l_rel_exists
        FROM tlgns_sap_bp_pf_payer payer
        WHERE payer.sap_bp_sarea_id = i_sap_bp_sarea_id_owner
          AND payer.payer_sap_bp_sarea_id = i_sap_bp_sarea_id_pf
          AND payer.sarea_id = i_sarea_id;

        EXCEPTION
        WHEN no_data_found THEN
          --new relation will be loaded
          l_rel_exists := -1;
        WHEN others THEN
          LOGON_LOG.ERROR('BPN: PF: load_partner_functions', SQLCODE, SQLERRM, '    Unknown exception in selecting sap_bp_sarea_id from tlgns_sap_bp_pf_payer for sap_bp_sarea_id_owner = ' || i_sap_bp_sarea_id_owner || ' sap_bp_sarea_id_pf = '|| i_sap_bp_sarea_id_pf || ' and sarea_id = ' || i_sarea_id);
          RAISE;

      END;

      -- insert new PF
      IF (l_rel_exists = -1) THEN

        LOGON_LOG.DEBUG('BPN: PF: load_partner_functions', '    Inserting a new sap_bp_pf_payer (sap_bp_sarea_id = ' || i_sap_bp_sarea_id_owner || ', payer_sap_bp_sarea_id = ' || i_sap_bp_sarea_id_pf || ' and sarea_id = ' || i_sarea_id || ') ...');

        INSERT
        INTO TLGNS_SAP_BP_PF_PAYER(sap_bp_sarea_id,
          payer_sap_bp_sarea_id,
          sarea_id,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (i_sap_bp_sarea_id_owner,
          i_sap_bp_sarea_id_pf,
          i_sarea_id,
          1, systimestamp, 1, systimestamp, 0);

      END IF;

    END IF;

  END load_partner_functions;

  ---------------------------------------------
  ---------------------------------------------
  -----delete missing partner functions--------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE delete_missing_pf(i_sap_bp_sarea_id IN NUMBER, i_sarea_hjid IN NUMBER) AS

  CURSOR cur_shipto_sarea IS
  SELECT shipto.shipto_sap_bp_sarea_id AS db_sap_bp_sarea_id_pf,
    request.req_sap_bp_sarea_id_pf AS req_sap_bp_sarea_id_pf
  FROM TLGNS_SAP_BP_PF_SHIPTO shipto
  LEFT JOIN (
    SELECT req_sarea_pf.sap_bp_sarea_id as req_sap_bp_sarea_id_pf
    FROM ULGNSAP.BPN_MASTERCUSTOMERSALESDATA req_sarea_owner
    INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERSALESDATA__0 rel ON (rel.parent_bpn_mastercustomersal_0 = req_sarea_owner.hjid)
    INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERPARTNERROL_0 req_pf ON (req_pf.hjid = rel.child_bpn_mastercustomerpart_0)
    INNER JOIN TLGNS_SAP_BP req_bp_pf ON (req_bp_pf.sap_id = LTRIM(req_pf.kunn2, '0'))
    INNER JOIN TLGNC_SAREA req_sarea_bs ON (req_sarea_bs.sorg_cd = req_sarea_owner.vkorg AND req_sarea_bs.distribution_channel = req_sarea_owner.vtweg AND req_sarea_bs.division_cd = req_sarea_owner.spart)
    INNER JOIN TLGNS_SAP_BP_SAREA req_sarea_pf ON (req_sarea_pf.sap_bp_id = req_bp_pf.sap_bp_id AND req_sarea_pf.sarea_id = req_sarea_bs.sarea_id)
    WHERE req_sarea_owner.hjid = i_sarea_hjid
      AND req_pf.parvw = c_pf_shipto
    ) request ON (request.req_sap_bp_sarea_id_pf = shipto.shipto_sap_bp_sarea_id)
  WHERE shipto.sap_bp_sarea_id = i_sap_bp_sarea_id;

  CURSOR cur_soldto_sarea IS
  SELECT soldto.soldto_sap_bp_sarea_id AS db_sap_bp_sarea_id_pf,
    request.req_sap_bp_sarea_id_pf AS req_sap_bp_sarea_id_pf
  FROM TLGNS_SAP_BP_PF_SOLDTO soldto
  LEFT JOIN (
    SELECT req_sarea_pf.sap_bp_sarea_id as req_sap_bp_sarea_id_pf
    FROM ULGNSAP.BPN_MASTERCUSTOMERSALESDATA req_sarea_owner
    INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERSALESDATA__0 rel ON (rel.parent_bpn_mastercustomersal_0 = req_sarea_owner.hjid)
    INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERPARTNERROL_0 req_pf ON (req_pf.hjid = rel.child_bpn_mastercustomerpart_0)
    INNER JOIN TLGNS_SAP_BP req_bp_pf ON (req_bp_pf.sap_id = LTRIM(req_pf.kunn2, '0'))
    INNER JOIN TLGNC_SAREA req_sarea_bs ON (req_sarea_bs.sorg_cd = req_sarea_owner.vkorg AND req_sarea_bs.distribution_channel = req_sarea_owner.vtweg AND req_sarea_bs.division_cd = req_sarea_owner.spart)
    INNER JOIN TLGNS_SAP_BP_SAREA req_sarea_pf ON (req_sarea_pf.sap_bp_id = req_bp_pf.sap_bp_id AND req_sarea_pf.sarea_id = req_sarea_bs.sarea_id)
    WHERE req_sarea_owner.hjid = i_sarea_hjid
      AND req_pf.parvw = c_pf_soldto
    ) request ON (request.req_sap_bp_sarea_id_pf = soldto.soldto_sap_bp_sarea_id)
  WHERE soldto.sap_bp_sarea_id = i_sap_bp_sarea_id;

  CURSOR cur_billto_sarea IS
  SELECT billto.billto_sap_bp_sarea_id AS db_sap_bp_sarea_id_pf,
    request.req_sap_bp_sarea_id_pf AS req_sap_bp_sarea_id_pf
  FROM TLGNS_SAP_BP_PF_BILLTO billto
  LEFT JOIN (
    SELECT req_sarea_pf.sap_bp_sarea_id as req_sap_bp_sarea_id_pf
    FROM ULGNSAP.BPN_MASTERCUSTOMERSALESDATA req_sarea_owner
    INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERSALESDATA__0 rel ON (rel.parent_bpn_mastercustomersal_0 = req_sarea_owner.hjid)
    INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERPARTNERROL_0 req_pf ON (req_pf.hjid = rel.child_bpn_mastercustomerpart_0)
    INNER JOIN TLGNS_SAP_BP req_bp_pf ON (req_bp_pf.sap_id = LTRIM(req_pf.kunn2, '0'))
    INNER JOIN TLGNC_SAREA req_sarea_bs ON (req_sarea_bs.sorg_cd = req_sarea_owner.vkorg AND req_sarea_bs.distribution_channel = req_sarea_owner.vtweg AND req_sarea_bs.division_cd = req_sarea_owner.spart)
    INNER JOIN TLGNS_SAP_BP_SAREA req_sarea_pf ON (req_sarea_pf.sap_bp_id = req_bp_pf.sap_bp_id AND req_sarea_pf.sarea_id = req_sarea_bs.sarea_id)
    WHERE req_sarea_owner.hjid = i_sarea_hjid
      AND req_pf.parvw = c_pf_billto
    ) request ON (request.req_sap_bp_sarea_id_pf = billto.billto_sap_bp_sarea_id)
  WHERE billto.sap_bp_sarea_id = i_sap_bp_sarea_id;

  CURSOR cur_payer_sarea IS
  SELECT payer.payer_sap_bp_sarea_id AS db_sap_bp_sarea_id_pf,
    request.req_sap_bp_sarea_id_pf AS req_sap_bp_sarea_id_pf
  FROM TLGNS_SAP_BP_PF_PAYER payer
  LEFT JOIN (
    SELECT req_sarea_pf.sap_bp_sarea_id as req_sap_bp_sarea_id_pf
    FROM ULGNSAP.BPN_MASTERCUSTOMERSALESDATA req_sarea_owner
    INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERSALESDATA__0 rel ON (rel.parent_bpn_mastercustomersal_0 = req_sarea_owner.hjid)
    INNER JOIN ULGNSAP.BPN_MASTERCUSTOMERPARTNERROL_0 req_pf ON (req_pf.hjid = rel.child_bpn_mastercustomerpart_0)
    INNER JOIN TLGNS_SAP_BP req_bp_pf ON (req_bp_pf.sap_id = LTRIM(req_pf.kunn2, '0'))
    INNER JOIN TLGNC_SAREA req_sarea_bs ON (req_sarea_bs.sorg_cd = req_sarea_owner.vkorg AND req_sarea_bs.distribution_channel = req_sarea_owner.vtweg AND req_sarea_bs.division_cd = req_sarea_owner.spart)
    INNER JOIN TLGNS_SAP_BP_SAREA req_sarea_pf ON (req_sarea_pf.sap_bp_id = req_bp_pf.sap_bp_id AND req_sarea_pf.sarea_id = req_sarea_bs.sarea_id)
    WHERE req_sarea_owner.hjid = i_sarea_hjid
      AND req_pf.parvw = c_pf_payer
    ) request ON (request.req_sap_bp_sarea_id_pf = payer.payer_sap_bp_sarea_id)
  WHERE payer.sap_bp_sarea_id = i_sap_bp_sarea_id;

  BEGIN

    LOGON_LOG.DEBUG('BPN: PF: delete_missing_pf', '   Deleting missing partner functions (sap_bp_sarea_id = ' || i_sap_bp_sarea_id || ' and BPN_MASTERCUSTOMERSALESDATA.hjid = ' || i_sarea_hjid || ') ...');

    FOR cur_shipto_sarea_record IN cur_shipto_sarea
    LOOP

      IF cur_shipto_sarea_record.req_sap_bp_sarea_id_pf IS NULL THEN

        DELETE FROM TLGNS_SAP_BP_PF_SHIPTO
        WHERE sap_bp_sarea_id = i_sap_bp_sarea_id
          AND shipto_sap_bp_sarea_id = cur_shipto_sarea_record.db_sap_bp_sarea_id_pf;
        LOGON_LOG.DEBUG('BPN: PF: delete_missing_pf', '    Partner function ' || c_pf_shipto || ' (sap_bp_sarea_id = ' || i_sap_bp_sarea_id || ' and shipto_sap_bp_sarea_id = ' || cur_shipto_sarea_record.db_sap_bp_sarea_id_pf || ') deleted.');

      END IF;

    END LOOP;

    FOR cur_soldto_sarea_record IN cur_soldto_sarea
    LOOP

      IF cur_soldto_sarea_record.req_sap_bp_sarea_id_pf IS NULL THEN

        DELETE FROM TLGNS_SAP_BP_PF_SOLDTO
        WHERE sap_bp_sarea_id = i_sap_bp_sarea_id
          AND soldto_sap_bp_sarea_id = cur_soldto_sarea_record.db_sap_bp_sarea_id_pf;
        LOGON_LOG.DEBUG('BPN: PF: delete_missing_pf', '    Partner function ' || c_pf_soldto || ' (sap_bp_sarea_id = ' || i_sap_bp_sarea_id || ' and soldto_sap_bp_sarea_id = ' || cur_soldto_sarea_record.db_sap_bp_sarea_id_pf || ') deleted.');

      END IF;

    END LOOP;

    FOR cur_billto_sarea_record IN cur_billto_sarea
    LOOP

      IF cur_billto_sarea_record.req_sap_bp_sarea_id_pf IS NULL THEN

        DELETE FROM TLGNS_SAP_BP_PF_BILLTO
        WHERE sap_bp_sarea_id = i_sap_bp_sarea_id
          AND billto_sap_bp_sarea_id = cur_billto_sarea_record.db_sap_bp_sarea_id_pf;
        LOGON_LOG.DEBUG('BPN: PF: delete_missing_pf', '    Partner function ' || c_pf_billto || ' (sap_bp_sarea_id = ' || i_sap_bp_sarea_id || ' and billto_sap_bp_sarea_id = ' || cur_billto_sarea_record.db_sap_bp_sarea_id_pf || ') deleted.');

      END IF;

    END LOOP;

    FOR cur_payer_sarea_record IN cur_payer_sarea
    LOOP

      IF cur_payer_sarea_record.req_sap_bp_sarea_id_pf IS NULL THEN

        DELETE FROM TLGNS_SAP_BP_PF_PAYER
        WHERE sap_bp_sarea_id = i_sap_bp_sarea_id
          AND payer_sap_bp_sarea_id = cur_payer_sarea_record.db_sap_bp_sarea_id_pf;
        LOGON_LOG.DEBUG('BPN: PF: delete_missing_pf', '    Partner function ' || c_pf_payer || ' (sap_bp_sarea_id = ' || i_sap_bp_sarea_id || ' and payer_sap_bp_sarea_id = ' || cur_payer_sarea_record.db_sap_bp_sarea_id_pf || ') deleted.');

      END IF;

    END LOOP;

    COMMIT;

    LOGON_LOG.DEBUG('BPN: PF: delete_missing_pf', '   Done deleting missing partner functions.');

  END delete_missing_pf;

  ---------------------------------------------
  ---------------------------------------------
  -----invent partner functions----------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE invent_soldto_billto_payer_pf(i_account_group_cd IN VARCHAR, i_sap_bp_sarea_id IN NUMBER) AS

    l_invent_soldto_for_shipto NUMBER := NULL;
    l_invent_billto_for_shipto NUMBER := NULL;
    l_invent_payer_for_shipto NUMBER := NULL;

  BEGIN

    LOGON_LOG.DEBUG('BPN: PF: invent_soldto_billto_payer_pf', '   Inventing partner functions (account_group_cd = ' || i_account_group_cd || ' and sap_bp_sarea_id = ' || i_sap_bp_sarea_id || ') ...');

    SELECT invent_soldto_for_shipto, invent_billto_for_shipto, invent_payer_for_shipto
    INTO l_invent_soldto_for_shipto, l_invent_billto_for_shipto, l_invent_payer_for_shipto
    FROM TLGNR_SAP_ACCOUNT_GROUP_BS
    WHERE sap_account_group_cd = i_account_group_cd;

    IF l_invent_soldto_for_shipto = 1 THEN

      INSERT INTO TLGNS_SAP_BP_PF_SOLDTO (sap_bp_sarea_id, soldto_sap_bp_sarea_id, sarea_id)
      (
        SELECT sap_bp_sarea_id, shipto_sap_bp_sarea_id, sarea_id
        FROM TLGNS_SAP_BP_PF_SHIPTO
        WHERE sap_bp_sarea_id = i_sap_bp_sarea_id
          AND shipto_sap_bp_sarea_id = i_sap_bp_sarea_id
          AND (sap_bp_sarea_id, shipto_sap_bp_sarea_id) NOT IN
          (
            SELECT sap_bp_sarea_id, soldto_sap_bp_sarea_id
            FROM TLGNS_SAP_BP_PF_SOLDTO
          )
      );

      IF SQL%ROWCOUNT = 1 THEN

        LOGON_LOG.INFO('BPN: PF: invent_soldto_billto_payer_pf', '   Self-referencing sold-to partner function invented for sap_bp_sarea_id ''' || i_sap_bp_sarea_id || '''.');

      END IF;

    END IF;

    IF l_invent_billto_for_shipto = 1 THEN

      INSERT INTO TLGNS_SAP_BP_PF_BILLTO (sap_bp_sarea_id, billto_sap_bp_sarea_id, sarea_id)
      (
        SELECT sap_bp_sarea_id, shipto_sap_bp_sarea_id, sarea_id
        FROM TLGNS_SAP_BP_PF_SHIPTO
        WHERE sap_bp_sarea_id = i_sap_bp_sarea_id
          AND shipto_sap_bp_sarea_id = i_sap_bp_sarea_id
          AND (sap_bp_sarea_id, shipto_sap_bp_sarea_id) NOT IN
          (
            SELECT sap_bp_sarea_id, billto_sap_bp_sarea_id
            FROM TLGNS_SAP_BP_PF_BILLTO
          )
      );

      IF SQL%ROWCOUNT = 1 THEN

        LOGON_LOG.INFO('BPN: PF: invent_soldto_billto_payer_pf', '   Self-referencing bill-to partner function invented for sap_bp_sarea_id ''' || i_sap_bp_sarea_id || '''.');

      END IF;

    END IF;

    IF l_invent_payer_for_shipto = 1 THEN

      INSERT INTO TLGNS_SAP_BP_PF_PAYER (sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id)
      (
        SELECT sap_bp_sarea_id, shipto_sap_bp_sarea_id, sarea_id
        FROM TLGNS_SAP_BP_PF_SHIPTO
        WHERE sap_bp_sarea_id = i_sap_bp_sarea_id
          AND shipto_sap_bp_sarea_id = i_sap_bp_sarea_id
          AND (sap_bp_sarea_id, shipto_sap_bp_sarea_id) NOT IN
          (
            SELECT sap_bp_sarea_id, payer_sap_bp_sarea_id
            FROM TLGNS_SAP_BP_PF_PAYER
          )
      );

      IF SQL%ROWCOUNT = 1 THEN

        LOGON_LOG.INFO('BPN: PF: invent_soldto_billto_payer_pf', '   Self-referencing payer partner function invented for sap_bp_sarea_id ''' || i_sap_bp_sarea_id || '''.');

      END IF;

    END IF;

  COMMIT;

  END invent_soldto_billto_payer_pf;

  ---------------------------------------------
  ---------------------------------------------
  -----update partner function flags-----------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE update_pf_flags AS

    l_upd_total NUMBER := 0;

  BEGIN

    LOGON_LOG.INFO('BPN: update_pf_flags', 'START updating bp_is_shipto/soldto/billto/payer flags ...');

    -- shipto
    UPDATE TLGNS_SAP_BP bp
    SET bp.bp_is_shipto = 1
    WHERE bp.bp_is_shipto = 0
      AND bp.sap_bp_id IN (
        SELECT DISTINCT bp.sap_bp_id
        FROM TLGNS_SAP_BP bp
        LEFT JOIN TLGNS_SAP_BP_SAREA sarea ON (bp.sap_bp_id = sarea.sap_bp_id)
        LEFT JOIN TLGNS_SAP_BP_PF_SHIPTO st ON (sarea.sap_bp_sarea_id = st.sap_bp_sarea_id AND sarea.sap_bp_sarea_id = st.shipto_sap_bp_sarea_id)
        WHERE st.shipto_sap_bp_sarea_id IS NOT NULL
      );

    l_upd_total := SQL%ROWCOUNT;

    IF l_upd_total > 0 THEN
      LOGON_LOG.INFO('BPN: update_pf_flags', ' Updated ' || l_upd_total || ' bp_is_shipto flags from 0 to 1.');
    END IF;

    UPDATE TLGNS_SAP_BP bp
    SET bp.bp_is_shipto = 0
    WHERE bp.bp_is_shipto = 1
      AND bp.sap_bp_id NOT IN (
        SELECT DISTINCT bp.sap_bp_id
        FROM TLGNS_SAP_BP bp
        LEFT JOIN TLGNS_SAP_BP_SAREA sarea ON (bp.sap_bp_id = sarea.sap_bp_id)
        LEFT JOIN TLGNS_SAP_BP_PF_SHIPTO st ON (sarea.sap_bp_sarea_id = st.sap_bp_sarea_id AND sarea.sap_bp_sarea_id = st.shipto_sap_bp_sarea_id)
        WHERE st.shipto_sap_bp_sarea_id IS NOT NULL
      );

    l_upd_total := SQL%ROWCOUNT;

    IF l_upd_total > 0 THEN
      LOGON_LOG.INFO('BPN: update_pf_flags', ' Updated ' || l_upd_total || ' bp_is_shipto flags from 1 to 0.');
    END IF;

    -- soldto
    UPDATE TLGNS_SAP_BP bp
    SET bp.bp_is_soldto = 1
    WHERE bp.bp_is_soldto = 0
      AND bp.sap_bp_id IN (
        SELECT DISTINCT bp.sap_bp_id
        FROM TLGNS_SAP_BP bp
        LEFT JOIN TLGNS_SAP_BP_SAREA sarea ON (bp.sap_bp_id = sarea.sap_bp_id)
        LEFT JOIN TLGNS_SAP_BP_PF_SOLDTO st ON (sarea.sap_bp_sarea_id = st.sap_bp_sarea_id AND sarea.sap_bp_sarea_id = st.soldto_sap_bp_sarea_id)
        WHERE st.soldto_sap_bp_sarea_id IS NOT NULL
      );

    l_upd_total := SQL%ROWCOUNT;

    IF l_upd_total > 0 THEN
      LOGON_LOG.INFO('BPN: update_pf_flags', ' Updated ' || l_upd_total || ' bp_is_soldto flags from 0 to 1.');
    END IF;

    UPDATE TLGNS_SAP_BP bp
    SET bp.bp_is_soldto = 0
    WHERE bp.bp_is_soldto = 1
      AND bp.sap_bp_id NOT IN (
        SELECT DISTINCT bp.sap_bp_id
        FROM TLGNS_SAP_BP bp
        LEFT JOIN TLGNS_SAP_BP_SAREA sarea ON (bp.sap_bp_id = sarea.sap_bp_id)
        LEFT JOIN TLGNS_SAP_BP_PF_SOLDTO st ON (sarea.sap_bp_sarea_id = st.sap_bp_sarea_id AND sarea.sap_bp_sarea_id = st.soldto_sap_bp_sarea_id)
        WHERE st.soldto_sap_bp_sarea_id IS NOT NULL
      );

    l_upd_total := SQL%ROWCOUNT;

    IF l_upd_total > 0 THEN
      LOGON_LOG.INFO('BPN: update_pf_flags', ' Updated ' || l_upd_total || ' bp_is_soldto flags from 1 to 0.');
    END IF;

    -- billto
    UPDATE TLGNS_SAP_BP bp
    SET bp.bp_is_billto = 1
    WHERE bp.bp_is_billto = 0
      AND bp.sap_bp_id IN (
        SELECT DISTINCT bp.sap_bp_id
        FROM TLGNS_SAP_BP bp
        LEFT JOIN TLGNS_SAP_BP_SAREA sarea ON (bp.sap_bp_id = sarea.sap_bp_id)
        LEFT JOIN TLGNS_SAP_BP_PF_BILLTO st ON (sarea.sap_bp_sarea_id = st.sap_bp_sarea_id AND sarea.sap_bp_sarea_id = st.billto_sap_bp_sarea_id)
        WHERE st.billto_sap_bp_sarea_id IS NOT NULL
      );

    l_upd_total := SQL%ROWCOUNT;

    IF l_upd_total > 0 THEN
      LOGON_LOG.INFO('BPN: update_pf_flags', ' Updated ' || l_upd_total || ' bp_is_billto flags from 0 to 1.');
    END IF;

    UPDATE TLGNS_SAP_BP bp
    SET bp.bp_is_billto = 0
    WHERE bp.bp_is_billto = 1
      AND bp.sap_bp_id NOT IN (
        SELECT DISTINCT bp.sap_bp_id
        FROM TLGNS_SAP_BP bp
        LEFT JOIN TLGNS_SAP_BP_SAREA sarea ON (bp.sap_bp_id = sarea.sap_bp_id)
        LEFT JOIN TLGNS_SAP_BP_PF_BILLTO st ON (sarea.sap_bp_sarea_id = st.sap_bp_sarea_id AND sarea.sap_bp_sarea_id = st.billto_sap_bp_sarea_id)
        WHERE st.billto_sap_bp_sarea_id IS NOT NULL
      );

    l_upd_total := SQL%ROWCOUNT;

    IF l_upd_total > 0 THEN
      LOGON_LOG.INFO('BPN: update_pf_flags', ' Updated ' || l_upd_total || ' bp_is_billto flags from 1 to 0.');
    END IF;

    -- payer
    UPDATE TLGNS_SAP_BP bp
    SET bp.bp_is_payer = 1
    WHERE bp.bp_is_payer = 0
      AND bp.sap_bp_id IN (
        SELECT DISTINCT bp.sap_bp_id
        FROM TLGNS_SAP_BP bp
        LEFT JOIN TLGNS_SAP_BP_SAREA sarea ON (bp.sap_bp_id = sarea.sap_bp_id)
        LEFT JOIN TLGNS_SAP_BP_PF_PAYER st ON (sarea.sap_bp_sarea_id = st.sap_bp_sarea_id AND sarea.sap_bp_sarea_id = st.payer_sap_bp_sarea_id)
        WHERE st.payer_sap_bp_sarea_id IS NOT NULL
      );

    l_upd_total := SQL%ROWCOUNT;

    IF l_upd_total > 0 THEN
      LOGON_LOG.INFO('BPN: update_pf_flags', ' Updated ' || l_upd_total || ' bp_is_payer flags from 0 to 1.');
    END IF;

    UPDATE TLGNS_SAP_BP bp
    SET bp.bp_is_payer = 0
    WHERE bp.bp_is_payer = 1
      AND bp.sap_bp_id NOT IN (
        SELECT DISTINCT bp.sap_bp_id
        FROM TLGNS_SAP_BP bp
        LEFT JOIN TLGNS_SAP_BP_SAREA sarea ON (bp.sap_bp_id = sarea.sap_bp_id)
        LEFT JOIN TLGNS_SAP_BP_PF_PAYER st ON (sarea.sap_bp_sarea_id = st.sap_bp_sarea_id AND sarea.sap_bp_sarea_id = st.payer_sap_bp_sarea_id)
        WHERE st.payer_sap_bp_sarea_id IS NOT NULL
      );

    IF l_upd_total > 0 THEN
      LOGON_LOG.INFO('BPN: update_pf_flags', ' Updated ' || l_upd_total || ' bp_is_payer flags from 1 to 0.');
    END IF;

    COMMIT;

    LOGON_LOG.INFO('BPN: update_pf_flags', 'FINISH updating bp_is_shipto/soldto/billto/payer flags.');

  END update_pf_flags;

  ---------------------------------------------
  ---------------------------------------------
  -----delete all partner functions------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE delete_all_pfs(i_sap_bp_sarea_id IN NUMBER) AS

  BEGIN

    delete from tlgns_sap_bp_pf_soldto
    where sap_bp_sarea_id = i_sap_bp_sarea_id;

    delete from tlgns_sap_bp_pf_shipto
    where sap_bp_sarea_id = i_sap_bp_sarea_id;

    delete from tlgns_sap_bp_pf_billto
    where sap_bp_sarea_id = i_sap_bp_sarea_id;

    delete from tlgns_sap_bp_pf_payer
    where sap_bp_sarea_id = i_sap_bp_sarea_id;

  END delete_all_pfs;

  ---------------------------------------------
  ---------------------------------------------
  -----delete all shipto sarea restrictions ------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE delete_all_ssrs(i_sap_bp_id IN NUMBER) AS

  CURSOR cur_shipto_sarea_restr IS
	select ssr.SHIPTO_SAREA_RESTR_ID from TLGNC_SHIPTO_SAREA_RESTR ssr
	join TLGNC_SHIPTO_SAREA ss on ssr.SHIPTO_SAREA_ID = ss.SHIPTO_SAREA_ID
	join TLGNC_SHIPTO shipto on ss.SHIPTO_ID = shipto.SHIPTO_ID
	where shipto.SHIPTO_SAP_BP_ID = i_sap_bp_id;
	
  BEGIN

    LOGON_LOG.DEBUG('BPN: delete_all_ssrs', ' Deletion shipto sarea restrictions by sap business partner id ' || i_sap_bp_id);
	  
	FOR shipto_sarea_restr IN cur_shipto_sarea_restr
    LOOP
              
	    delete from TLGNC_SHSAREA_RESTR_TRUCK_TYPE
	    where SHIPTO_SAREA_RESTR_ID = shipto_sarea_restr.SHIPTO_SAREA_RESTR_ID;
		  
	    delete from TLGNC_SHIPTO_SAREA_RESTR_BTRUL
	    where SHIPTO_SAREA_RESTR_ID = shipto_sarea_restr.SHIPTO_SAREA_RESTR_ID;
	    
	    delete from TLGNC_SHIPTO_SAREA_RESTR
	    where SHIPTO_SAREA_RESTR_ID = shipto_sarea_restr.SHIPTO_SAREA_RESTR_ID;
	
        LOGON_LOG.INFO('BPN: delete_all_ssrs', '  Deleted shipto sarea restrictions ' || shipto_sarea_restr.SHIPTO_SAREA_RESTR_ID || ' by sap business partner id ' || i_sap_bp_id);
	    
	END LOOP;

    LOGON_LOG.DEBUG('BPN: delete_all_ssrs', ' Done deletion of all shipto sarea restrictions by sap business partner id ' || i_sap_bp_id);
	
  END delete_all_ssrs;
  
  ---------------------------------------------
  ---------------------------------------------
  -----delete all shipto sarea restrictions by sarea ------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE delete_all_ssrs_by_sarea(i_sarea_id IN NUMBER, i_sap_bp_id IN NUMBER) AS

  CURSOR cur_shipto_sarea_restr IS
	select ssr.SHIPTO_SAREA_RESTR_ID from TLGNC_SHIPTO_SAREA_RESTR ssr
	join TLGNC_SHIPTO_SAREA ss on ssr.SHIPTO_SAREA_ID = ss.SHIPTO_SAREA_ID
	join TLGNC_SHIPTO shipto on ss.SHIPTO_ID = shipto.SHIPTO_ID
	where shipto.SHIPTO_SAP_BP_ID = i_sap_bp_id
	and ss.sarea_id = i_sarea_id;
	
  BEGIN

    LOGON_LOG.DEBUG('BPN: delete_all_ssrs_by_sarea', ' Deletion shipto sarea restrictions by sap business partner id ' || i_sap_bp_id || ' and sarea id ' || i_sarea_id);
	  
	FOR shipto_sarea_restr IN cur_shipto_sarea_restr
    LOOP
              
	    delete from TLGNC_SHSAREA_RESTR_TRUCK_TYPE
	    where SHIPTO_SAREA_RESTR_ID = shipto_sarea_restr.SHIPTO_SAREA_RESTR_ID;
		  
	    delete from TLGNC_SHIPTO_SAREA_RESTR_BTRUL
	    where SHIPTO_SAREA_RESTR_ID = shipto_sarea_restr.SHIPTO_SAREA_RESTR_ID;
	    
	    delete from TLGNC_SHIPTO_SAREA_RESTR
	    where SHIPTO_SAREA_RESTR_ID = shipto_sarea_restr.SHIPTO_SAREA_RESTR_ID;
	
        LOGON_LOG.INFO('BPN: delete_all_ssrs_by_sarea', '  Deleted shipto sarea restrictions ' || shipto_sarea_restr.SHIPTO_SAREA_RESTR_ID || ' by sap business partner id ' || i_sap_bp_id || ' and sarea id ' || i_sarea_id);
	    
	END LOOP;

    LOGON_LOG.DEBUG('BPN: delete_all_ssrs_by_sarea', ' Done deletion of all shipto sarea restrictions by sap business partner id ' || i_sap_bp_id || ' and sarea id ' || i_sarea_id);
	
  END delete_all_ssrs_by_sarea;

  
  ---------------------------------------------
  ---------------------------------------------
  -----update all dependent sales areas--------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE update_dependent_bp_sareas(i_bp_hjid IN NUMBER, i_sap_bp_sarea_id IN NUMBER, i_sap_bp_id IN NUMBER, i_sarea_id IN NUMBER) AS

  CURSOR cur_dep_sarea IS
  SELECT map.sarea_id
  FROM VLGNC_COMMON_SAREA_MAP map
  WHERE map.ref_sarea_id = i_sarea_id;

  l_dep_sap_bp_sarea_id NUMBER;

  l_ins_total NUMBER;
  l_upd_total NUMBER;

  BEGIN

    LOGON_LOG.DEBUG('BPN: update_dependent_bp_sareas', '  Updating dependent sales areas for sap_bp_sarea_id ' || i_sap_bp_sarea_id || ' ...');

    l_ins_total := 0;
    l_upd_total := 0;

    FOR dep_sarea IN cur_dep_sarea
    LOOP

      LOGON_LOG.DEBUG('BPN: update_dependent_bp_sareas', '   Checking dependent sales area with sarea_id ' || dep_sarea.sarea_id || ' ...');

      BEGIN

        l_dep_sap_bp_sarea_id := null;

        SELECT sap_bp_sarea_id
        INTO l_dep_sap_bp_sarea_id
        FROM TLGNS_SAP_BP_SAREA
        WHERE sap_bp_id = i_sap_bp_id AND sarea_id = dep_sarea.sarea_id;

        EXCEPTION
        WHEN no_data_found THEN
          NULL;

      END;

      sync_dependent_bp_sarea(i_sap_bp_sarea_id, l_dep_sap_bp_sarea_id, dep_sarea.sarea_id, l_ins_total, l_upd_total);

    END LOOP; -- cur_dep_sarea

    LOGON_LOG.DEBUG('BPN: update_dependent_bp_sareas', '  Inserted ' || l_ins_total || ' and updated ' || l_upd_total || ' records from ''tlgns_sap_bp_sarea'' table! for sap_bp_sarea_id ' || i_sap_bp_sarea_id || '.');

  END update_dependent_bp_sareas;

  ---------------------------------------------
  ---------------------------------------------
  -----common division:                  ------
  -----sync reference and dependent sarea------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE sync_dependent_bp_sarea(i_sap_bp_sarea_id IN NUMBER, i_dep_sap_bp_sarea_id IN NUMBER, i_dep_sarea_id IN NUMBER, x_ins_total IN OUT NUMBER, x_upd_total IN OUT NUMBER) AS

    l_src_bp_sarea TLGNS_SAP_BP_SAREA%ROWTYPE := null;
    l_dst_bp_sarea TLGNS_SAP_BP_SAREA%ROWTYPE := null;

  BEGIN

    LOGON_LOG.DEBUG('BPN: sync_dependent_bp_sarea', '   Syncing sap_bp_sarea_id ' || i_sap_bp_sarea_id || ' to dependent sarea_id ' || i_dep_sarea_id || ' (target sap_bp_sarea_id = ' || i_dep_sap_bp_sarea_id || ').');

    SELECT *
    INTO l_src_bp_sarea
    FROM TLGNS_SAP_BP_SAREA
    WHERE sap_bp_sarea_id = i_sap_bp_sarea_id;

    IF i_dep_sap_bp_sarea_id is not null THEN

      SELECT *
      INTO l_dst_bp_sarea
      FROM TLGNS_SAP_BP_SAREA
      WHERE sap_bp_sarea_id = i_dep_sap_bp_sarea_id;

    END IF;

    IF l_dst_bp_sarea.sap_bp_sarea_id is not null THEN

      IF l_dst_bp_sarea.marked_for_deletion = 1 or l_dst_bp_sarea.ref_sap_bp_sarea_id is not null THEN

        LOGON_LOG.DEBUG('BPN: sync_dependent_bp_sarea', '   Updating sap_bp_sarea_id ' || i_dep_sap_bp_sarea_id || ' ...');

        IF l_dst_bp_sarea.marked_for_deletion = 1 or l_src_bp_sarea.marked_for_deletion = 1 THEN
          LOGON_LOG.DEBUG('BPN: sync_dependent_bp_sarea', '   Deleting all partner functions of sap_bp_sarea_id = ' || i_dep_sap_bp_sarea_id || ' ...');
          delete_all_pfs(l_dst_bp_sarea.sap_bp_sarea_id);
        END IF;

        l_src_bp_sarea.sap_bp_sarea_id     := l_dst_bp_sarea.sap_bp_sarea_id;
        l_src_bp_sarea.sarea_id            := l_dst_bp_sarea.sarea_id;
        l_src_bp_sarea.ref_sap_bp_sarea_id := i_sap_bp_sarea_id;
        l_src_bp_sarea.ctl_mod_uid         := 1;
        l_src_bp_sarea.ctl_mod_ts          := systimestamp;
        l_src_bp_sarea.ctl_tcn             := l_dst_bp_sarea.ctl_tcn + 1;

        UPDATE TLGNS_SAP_BP_SAREA SET ROW = l_src_bp_sarea WHERE sap_bp_sarea_id = l_dst_bp_sarea.sap_bp_sarea_id;

        x_upd_total := x_upd_total + 1;

      END IF;

    ELSE

      LOGON_LOG.DEBUG('BPN: sync_dependent_bp_sarea', '   Inserting ...');

      SELECT slgns_sap_bp_sarea.nextval
      INTO l_src_bp_sarea.sap_bp_sarea_id
      FROM dual;

      l_src_bp_sarea.sarea_id            := i_dep_sarea_id;
      l_src_bp_sarea.ref_sap_bp_sarea_id := i_sap_bp_sarea_id;
      l_src_bp_sarea.ctl_cre_uid         := 1;
      l_src_bp_sarea.ctl_cre_ts          := systimestamp;
      l_src_bp_sarea.ctl_mod_uid         := 1;
      l_src_bp_sarea.ctl_mod_ts          := systimestamp;
      l_src_bp_sarea.ctl_tcn             := 0;

      INSERT INTO TLGNS_SAP_BP_SAREA VALUES l_src_bp_sarea;

      x_ins_total := x_ins_total + 1;

    END IF;

  END sync_dependent_bp_sarea;

  ---------------------------------------------
  ---------------------------------------------
  -----common division:               ---------
  -----delete dependent bp sales areas---------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE delete_dependent_sareas(i_bp_hjid IN NUMBER, i_sap_bp_id IN NUMBER, i_sarea_id IN NUMBER) AS

  CURSOR cur_dep_bp_sarea IS
  SELECT bp_sarea.sap_bp_sarea_id
  FROM VLGNC_COMMON_SAREA_MAP map
  INNER JOIN TLGNS_SAP_BP_SAREA bp_sarea ON (map.sarea_id = bp_sarea.sarea_id)
  WHERE bp_sarea.sap_bp_id = i_sap_bp_id
    AND map.ref_sarea_id = i_sarea_id
    AND bp_sarea.marked_for_deletion != '1';

  BEGIN

    LOGON_LOG.DEBUG('BPN: delete_dependent_sareas', '  Deleting dependent bp sales areas for sap_bp_id ' || i_sap_bp_id || ' and sarea_id ' || i_sarea_id || ' ...');

    FOR dep_bp_sarea IN cur_dep_bp_sarea
    LOOP

      LOGON_LOG.DEBUG('BPN: delete_dependent_sareas', '   Deleting dependent bp sales area with sap_bp_sarea_id ' || dep_bp_sarea.sap_bp_sarea_id || ' ...');

      UPDATE tlgns_sap_bp_sarea SET
        marked_for_deletion = '1',
        ctl_mod_uid = 1,
        ctl_mod_ts = systimestamp,
        ctl_tcn = ctl_tcn + 1
      WHERE sap_bp_sarea_id = dep_bp_sarea.sap_bp_sarea_id;

      delete_all_pfs(dep_bp_sarea.sap_bp_sarea_id);

    END LOOP; -- cur_dep_bp_sarea

  END delete_dependent_sareas;

  ---------------------------------------------
  ---------------------------------------------
  -----common division:                --------
  -----sync dependent partner functions--------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE sync_dependent_pfs(i_sap_bp_sarea_id IN NUMBER, i_sap_bp_id IN NUMBER, i_sarea_id IN NUMBER) AS

    CURSOR cur_dep_bp_sarea IS
    SELECT bp_sarea.sap_bp_sarea_id, bp_sarea.sarea_id
    FROM TLGNS_SAP_BP_SAREA bp_sarea
    WHERE bp_sarea.ref_sap_bp_sarea_id = i_sap_bp_sarea_id;

    CURSOR cur_delete_soldto(v_dep_sap_bp_sarea_id IN NUMBER) IS
    SELECT dep_pf_bp_sarea.sap_bp_sarea_id dep_pf_sap_bp_sarea_id, pf.sap_bp_sarea_id existing_pf
    FROM TLGNS_SAP_BP_PF_SOLDTO dep_pf
    INNER JOIN TLGNS_SAP_BP_SAREA dep_pf_bp_sarea ON (dep_pf.soldto_sap_bp_sarea_id = dep_pf_bp_sarea.sap_bp_sarea_id)
    INNER JOIN TLGNS_SAP_BP_SAREA pf_bp_sarea ON (dep_pf_bp_sarea.sap_bp_id = pf_bp_sarea.sap_bp_id and i_sarea_id = pf_bp_sarea.sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_PF_SOLDTO pf ON (pf_bp_sarea.sap_bp_sarea_id = pf.soldto_sap_bp_sarea_id and i_sap_bp_sarea_id = pf.sap_bp_sarea_id)
    WHERE dep_pf.sap_bp_sarea_id = v_dep_sap_bp_sarea_id;

    CURSOR cur_insert_soldto(v_dep_sap_bp_sarea_id IN NUMBER, v_dep_sarea_id IN NUMBER) IS
    SELECT dep_pf_bp_sarea.sap_bp_sarea_id dep_pf_sap_bp_sarea_id, dep_pf.sap_bp_sarea_id existing_dep_pf
    FROM TLGNS_SAP_BP_PF_SOLDTO pf
    INNER JOIN TLGNS_SAP_BP_SAREA pf_bp_sarea ON (pf.soldto_sap_bp_sarea_id = pf_bp_sarea.sap_bp_sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_SAREA dep_pf_bp_sarea ON (pf_bp_sarea.sap_bp_id = dep_pf_bp_sarea.sap_bp_id and dep_pf_bp_sarea.sarea_id = v_dep_sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_PF_SOLDTO dep_pf ON (v_dep_sap_bp_sarea_id = dep_pf.sap_bp_sarea_id and dep_pf_bp_sarea.sap_bp_sarea_id = dep_pf.soldto_sap_bp_sarea_id)
    WHERE pf.sap_bp_sarea_id = i_sap_bp_sarea_id;

    CURSOR cur_delete_shipto(v_dep_sap_bp_sarea_id IN NUMBER) IS
    SELECT dep_pf_bp_sarea.sap_bp_sarea_id dep_pf_sap_bp_sarea_id, pf.sap_bp_sarea_id existing_pf
    FROM TLGNS_SAP_BP_PF_SHIPTO dep_pf
    INNER JOIN TLGNS_SAP_BP_SAREA dep_pf_bp_sarea ON (dep_pf.shipto_sap_bp_sarea_id = dep_pf_bp_sarea.sap_bp_sarea_id)
    INNER JOIN TLGNS_SAP_BP_SAREA pf_bp_sarea ON (dep_pf_bp_sarea.sap_bp_id = pf_bp_sarea.sap_bp_id and i_sarea_id = pf_bp_sarea.sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_PF_SHIPTO pf ON (pf_bp_sarea.sap_bp_sarea_id = pf.shipto_sap_bp_sarea_id and i_sap_bp_sarea_id = pf.sap_bp_sarea_id)
    WHERE dep_pf.sap_bp_sarea_id = v_dep_sap_bp_sarea_id;

    CURSOR cur_insert_shipto(v_dep_sap_bp_sarea_id IN NUMBER, v_dep_sarea_id IN NUMBER) IS
    SELECT dep_pf_bp_sarea.sap_bp_sarea_id dep_pf_sap_bp_sarea_id, dep_pf.sap_bp_sarea_id existing_dep_pf
    FROM TLGNS_SAP_BP_PF_SHIPTO pf
    INNER JOIN TLGNS_SAP_BP_SAREA pf_bp_sarea ON (pf.shipto_sap_bp_sarea_id = pf_bp_sarea.sap_bp_sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_SAREA dep_pf_bp_sarea ON (pf_bp_sarea.sap_bp_id = dep_pf_bp_sarea.sap_bp_id and dep_pf_bp_sarea.sarea_id = v_dep_sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_PF_SHIPTO dep_pf ON (v_dep_sap_bp_sarea_id = dep_pf.sap_bp_sarea_id and dep_pf_bp_sarea.sap_bp_sarea_id = dep_pf.shipto_sap_bp_sarea_id)
    WHERE pf.sap_bp_sarea_id = i_sap_bp_sarea_id;

    CURSOR cur_delete_billto(v_dep_sap_bp_sarea_id IN NUMBER) IS
    SELECT dep_pf_bp_sarea.sap_bp_sarea_id dep_pf_sap_bp_sarea_id, pf.sap_bp_sarea_id existing_pf
    FROM TLGNS_SAP_BP_PF_BILLTO dep_pf
    INNER JOIN TLGNS_SAP_BP_SAREA dep_pf_bp_sarea ON (dep_pf.billto_sap_bp_sarea_id = dep_pf_bp_sarea.sap_bp_sarea_id)
    INNER JOIN TLGNS_SAP_BP_SAREA pf_bp_sarea ON (dep_pf_bp_sarea.sap_bp_id = pf_bp_sarea.sap_bp_id and i_sarea_id = pf_bp_sarea.sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_PF_BILLTO pf ON (pf_bp_sarea.sap_bp_sarea_id = pf.billto_sap_bp_sarea_id and i_sap_bp_sarea_id = pf.sap_bp_sarea_id)
    WHERE dep_pf.sap_bp_sarea_id = v_dep_sap_bp_sarea_id;

    CURSOR cur_insert_billto(v_dep_sap_bp_sarea_id IN NUMBER, v_dep_sarea_id IN NUMBER) IS
    SELECT dep_pf_bp_sarea.sap_bp_sarea_id dep_pf_sap_bp_sarea_id, dep_pf.sap_bp_sarea_id existing_dep_pf
    FROM TLGNS_SAP_BP_PF_BILLTO pf
    INNER JOIN TLGNS_SAP_BP_SAREA pf_bp_sarea ON (pf.billto_sap_bp_sarea_id = pf_bp_sarea.sap_bp_sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_SAREA dep_pf_bp_sarea ON (pf_bp_sarea.sap_bp_id = dep_pf_bp_sarea.sap_bp_id and dep_pf_bp_sarea.sarea_id = v_dep_sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_PF_BILLTO dep_pf ON (v_dep_sap_bp_sarea_id = dep_pf.sap_bp_sarea_id and dep_pf_bp_sarea.sap_bp_sarea_id = dep_pf.billto_sap_bp_sarea_id)
    WHERE pf.sap_bp_sarea_id = i_sap_bp_sarea_id;

    CURSOR cur_delete_payer(v_dep_sap_bp_sarea_id IN NUMBER) IS
    SELECT dep_pf_bp_sarea.sap_bp_sarea_id dep_pf_sap_bp_sarea_id, pf.sap_bp_sarea_id existing_pf
    FROM TLGNS_SAP_BP_PF_PAYER dep_pf
    INNER JOIN TLGNS_SAP_BP_SAREA dep_pf_bp_sarea ON (dep_pf.payer_sap_bp_sarea_id = dep_pf_bp_sarea.sap_bp_sarea_id)
    INNER JOIN TLGNS_SAP_BP_SAREA pf_bp_sarea ON (dep_pf_bp_sarea.sap_bp_id = pf_bp_sarea.sap_bp_id and i_sarea_id = pf_bp_sarea.sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_PF_PAYER pf ON (pf_bp_sarea.sap_bp_sarea_id = pf.payer_sap_bp_sarea_id and i_sap_bp_sarea_id = pf.sap_bp_sarea_id)
    WHERE dep_pf.sap_bp_sarea_id = v_dep_sap_bp_sarea_id;

    CURSOR cur_insert_payer(v_dep_sap_bp_sarea_id IN NUMBER, v_dep_sarea_id IN NUMBER) IS
    SELECT dep_pf_bp_sarea.sap_bp_sarea_id dep_pf_sap_bp_sarea_id, dep_pf.sap_bp_sarea_id existing_dep_pf
    FROM TLGNS_SAP_BP_PF_PAYER pf
    INNER JOIN TLGNS_SAP_BP_SAREA pf_bp_sarea ON (pf.payer_sap_bp_sarea_id = pf_bp_sarea.sap_bp_sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_SAREA dep_pf_bp_sarea ON (pf_bp_sarea.sap_bp_id = dep_pf_bp_sarea.sap_bp_id and dep_pf_bp_sarea.sarea_id = v_dep_sarea_id)
    LEFT OUTER JOIN TLGNS_SAP_BP_PF_PAYER dep_pf ON (v_dep_sap_bp_sarea_id = dep_pf.sap_bp_sarea_id and dep_pf_bp_sarea.sap_bp_sarea_id = dep_pf.payer_sap_bp_sarea_id)
    WHERE pf.sap_bp_sarea_id = i_sap_bp_sarea_id;

  BEGIN

    FOR dep_bp_sarea IN cur_dep_bp_sarea
    LOOP

      LOGON_LOG.DEBUG('BPN: sync_dependent_pfs', '    Synchronizing partner functions of dependent sales areas (i_sap_bp_sarea_id =' || i_sap_bp_sarea_id || ', i_sap_bp_id = ' || i_sap_bp_id || ' and i_sarea_id = ' || i_sarea_id || ') ...');

      FOR delete_soldto_record IN cur_delete_soldto(dep_bp_sarea.sap_bp_sarea_id)
      LOOP

        LOGON_LOG.DEBUG('BPN: sync_dependent_pfs', '     Validating dep_pf_sap_bp_sarea_id = ' || delete_soldto_record.dep_pf_sap_bp_sarea_id || ' and existing_pf = ' || delete_soldto_record.existing_pf || '.');

        IF delete_soldto_record.existing_pf is null THEN

          delete from TLGNS_SAP_BP_PF_SOLDTO
          where sap_bp_sarea_id = dep_bp_sarea.sap_bp_sarea_id
            and soldto_sap_bp_sarea_id = delete_soldto_record.dep_pf_sap_bp_sarea_id;

        END IF;

      END LOOP; -- cur_delete_soldto

      FOR insert_soldto_record IN cur_insert_soldto(dep_bp_sarea.sap_bp_sarea_id, dep_bp_sarea.sarea_id)
      LOOP

        LOGON_LOG.DEBUG('BPN: sync_dependent_pfs', '     Inserting (if not exists) dep_pf_sap_bp_sarea_id = ' || insert_soldto_record.dep_pf_sap_bp_sarea_id || ' and existing_dep_pf = ' || insert_soldto_record.existing_dep_pf || '.');

        IF insert_soldto_record.dep_pf_sap_bp_sarea_id is not null THEN

          IF insert_soldto_record.existing_dep_pf is null THEN

            insert into TLGNS_SAP_BP_PF_SOLDTO (
              sap_bp_sarea_id,
              soldto_sap_bp_sarea_id,
              sarea_id,
              ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
            values (
              dep_bp_sarea.sap_bp_sarea_id,
              insert_soldto_record.dep_pf_sap_bp_sarea_id,
              dep_bp_sarea.sarea_id,
              1, systimestamp, 1, systimestamp, 0);

          END IF;

        ELSE

          -- TODO 11-Jul-2013/stich: should raise an error, but not doing due to time constraint and risk
          NULL;

        END IF;

      END LOOP; -- cur_insert_soldto

      FOR delete_shipto_record IN cur_delete_shipto(dep_bp_sarea.sap_bp_sarea_id)
      LOOP

        LOGON_LOG.DEBUG('BPN: sync_dependent_pfs', '     Validating dep_pf_sap_bp_sarea_id = ' || delete_shipto_record.dep_pf_sap_bp_sarea_id || ' and existing_pf = ' || delete_shipto_record.existing_pf || '.');

        IF delete_shipto_record.existing_pf is null THEN

          delete from TLGNS_SAP_BP_PF_SHIPTO
          where sap_bp_sarea_id = dep_bp_sarea.sap_bp_sarea_id
            and shipto_sap_bp_sarea_id = delete_shipto_record.dep_pf_sap_bp_sarea_id;

        END IF;

      END LOOP; -- cur_delete_shipto

      FOR insert_shipto_record IN cur_insert_shipto(dep_bp_sarea.sap_bp_sarea_id, dep_bp_sarea.sarea_id)
      LOOP

        LOGON_LOG.DEBUG('BPN: sync_dependent_pfs', '     Inserting (if not exists) dep_pf_sap_bp_sarea_id = ' || insert_shipto_record.dep_pf_sap_bp_sarea_id || ' and existing_dep_pf = ' || insert_shipto_record.existing_dep_pf || '.');

        IF insert_shipto_record.dep_pf_sap_bp_sarea_id is not null THEN

          IF insert_shipto_record.existing_dep_pf is null THEN

            insert into TLGNS_SAP_BP_PF_SHIPTO (
              sap_bp_sarea_id,
              shipto_sap_bp_sarea_id,
              sarea_id,
              ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
            values (
              dep_bp_sarea.sap_bp_sarea_id,
              insert_shipto_record.dep_pf_sap_bp_sarea_id,
              dep_bp_sarea.sarea_id,
              1, systimestamp, 1, systimestamp, 0);

          END IF;

        ELSE

          -- TODO 11-Jul-2013/stich: should raise an error, but not doing due to time constraint and risk
          NULL;

        END IF;

      END LOOP; -- cur_insert_shipto

      FOR delete_billto_record IN cur_delete_billto(dep_bp_sarea.sap_bp_sarea_id)
      LOOP

        LOGON_LOG.DEBUG('BPN: sync_dependent_pfs', '     Validating dep_pf_sap_bp_sarea_id = ' || delete_billto_record.dep_pf_sap_bp_sarea_id || ' and existing_pf = ' || delete_billto_record.existing_pf || '.');

        IF delete_billto_record.existing_pf is null THEN

          delete from TLGNS_SAP_BP_PF_BILLTO
          where sap_bp_sarea_id = dep_bp_sarea.sap_bp_sarea_id
            and billto_sap_bp_sarea_id = delete_billto_record.dep_pf_sap_bp_sarea_id;

        END IF;

      END LOOP; -- cur_delete_billto

      FOR insert_billto_record IN cur_insert_billto(dep_bp_sarea.sap_bp_sarea_id, dep_bp_sarea.sarea_id)
      LOOP

        LOGON_LOG.DEBUG('BPN: sync_dependent_pfs', '     Inserting (if not exists) dep_pf_sap_bp_sarea_id = ' || insert_billto_record.dep_pf_sap_bp_sarea_id || ' and existing_dep_pf = ' || insert_billto_record.existing_dep_pf || '.');

        IF insert_billto_record.dep_pf_sap_bp_sarea_id is not null THEN

          IF insert_billto_record.existing_dep_pf is null THEN

            insert into TLGNS_SAP_BP_PF_BILLTO (
              sap_bp_sarea_id,
              billto_sap_bp_sarea_id,
              sarea_id,
              ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
            values (
              dep_bp_sarea.sap_bp_sarea_id,
              insert_billto_record.dep_pf_sap_bp_sarea_id,
              dep_bp_sarea.sarea_id,
              1, systimestamp, 1, systimestamp, 0);

          END IF;

        ELSE

          -- TODO 11-Jul-2013/stich: should raise an error, but not doing due to time constraint and risk
          NULL;

        END IF;

      END LOOP; -- cur_insert_billto

      FOR delete_payer_record IN cur_delete_payer(dep_bp_sarea.sap_bp_sarea_id)
      LOOP

        LOGON_LOG.DEBUG('BPN: sync_dependent_pfs', '     Validating dep_pf_sap_bp_sarea_id = ' || delete_payer_record.dep_pf_sap_bp_sarea_id || ' and existing_pf = ' || delete_payer_record.existing_pf || '.');

        IF delete_payer_record.existing_pf is null THEN

          delete from TLGNS_SAP_BP_PF_PAYER
          where sap_bp_sarea_id = dep_bp_sarea.sap_bp_sarea_id
            and payer_sap_bp_sarea_id = delete_payer_record.dep_pf_sap_bp_sarea_id;

        END IF;

      END LOOP; -- cur_delete_payer

      FOR insert_payer_record IN cur_insert_payer(dep_bp_sarea.sap_bp_sarea_id, dep_bp_sarea.sarea_id)
      LOOP

        LOGON_LOG.DEBUG('BPN: sync_dependent_pfs', '     Inserting (if not exists) dep_pf_sap_bp_sarea_id = ' || insert_payer_record.dep_pf_sap_bp_sarea_id || ' and existing_dep_pf = ' || insert_payer_record.existing_dep_pf || '.');

        IF insert_payer_record.dep_pf_sap_bp_sarea_id is not null THEN

          IF insert_payer_record.existing_dep_pf is null THEN

            insert into TLGNS_SAP_BP_PF_PAYER (
              sap_bp_sarea_id,
              payer_sap_bp_sarea_id,
              sarea_id,
              ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
            values (
              dep_bp_sarea.sap_bp_sarea_id,
              insert_payer_record.dep_pf_sap_bp_sarea_id,
              dep_bp_sarea.sarea_id,
              1, systimestamp, 1, systimestamp, 0);

          END IF;

        ELSE

          -- TODO 11-Jul-2013/stich: should raise an error, but not doing due to time constraint and risk
          NULL;

        END IF;

      END LOOP; -- cur_insert_payer

    END LOOP; -- cur_dep_bp_sarea

  END sync_dependent_pfs;

END SAPIMPORT_BPN;
/

SHOW ERRORS;
--------------------------------------------------------------------------------
-- END OF THE SCRIPT
--------------------------------------------------------------------------------
