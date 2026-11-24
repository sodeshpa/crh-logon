--------------------------------------------------------------------------------
--
-- PROJECT      : $Project: LOGON $
--
-- AUTHOR       : $Author: stich $
-- CREATION DATE: $Date$
-- DESCRIPTION  : Sales Document Notification import package.
--
--------------------------------------------------------------------------------

CREATE OR REPLACE PACKAGE sapimport_sdn AS

  PROCEDURE main(i_min_loglevel IN VARCHAR2, i_days_to_import IN NUMBER);

  PROCEDURE load_tlgns_sap_contract_item(
    i_contract_hjid           IN NUMBER,
    i_sap_contract_id         IN NUMBER,
    i_header_supply_plant_cd  IN VARCHAR2,
    i_contract_type_is_stpo   IN NUMBER,
    i_item_grouping_type      IN NUMBER);

  FUNCTION get_partner_function(i_partner_function IN VARCHAR2) RETURN VARCHAR2;

  FUNCTION get_item_grouping_type(
    quantity_control_flag   IN NUMBER,
    advance_payment_flag    IN NUMBER)
  RETURN NUMBER;
  
  FUNCTION is_valid_item_grouping_type(
    i_sap_contract_id    IN NUMBER,
    item_grouping_type   IN NUMBER)
  RETURN BOOLEAN;
  
  FUNCTION ap_cre_upd_group_leader(
    i_sap_contract_id           IN NUMBER,
    i_sap_material_id           IN NUMBER,
    i_supply_plant_cd           IN VARCHAR2,
    i_target_quantity_uom_cd    IN VARCHAR2,
    i_vat_percentage            IN NUMBER,
    i_transport_costs_included  IN NUMBER,
    i_sap_contract_item_id      IN NUMBER,
    i_marked_for_deletion       IN NUMBER,
    i_new_target_quantity       IN NUMBER,
    o_delta_target_quantity     OUT NUMBER)
    RETURN NUMBER;

  FUNCTION qr_cre_upd_group_leader(
    i_sap_contract_item_id      IN NUMBER,
    i_sap_contract_id           IN NUMBER,
    i_sap_material_id           IN NUMBER,
    i_supply_plant_cd           IN VARCHAR2,
    i_material_group_1          IN VARCHAR2,
    i_material_group_2          IN VARCHAR2,
    i_material_group_3          IN VARCHAR2,
    i_material_group_4          IN VARCHAR2,
    i_material_group_5          IN VARCHAR2,
    i_item_category             IN VARCHAR2,
    i_target_quantity           IN NUMBER,
    i_target_quantity_uom_cd    IN VARCHAR2,
    i_net_value                 IN NUMBER,
    i_vat_percentage            IN NUMBER,
    i_marked_for_deletion       IN NUMBER,
    i_transport_costs_included  IN NUMBER)
    RETURN NUMBER;

  FUNCTION create_group_leader(
    i_sap_contract_id           IN NUMBER,
    i_sap_material_id           IN NUMBER,
    i_supply_plant_cd           IN VARCHAR2,
    i_target_quantity_uom_cd    IN VARCHAR2,
    i_vat_percentage            IN NUMBER,
    i_transport_costs_included  IN NUMBER,
    i_target_quantity           IN NUMBER)
    RETURN NUMBER;
    
  PROCEDURE update_gl_qty_and_del_flag(
    i_sap_contract_id    IN NUMBER);

  FUNCTION quantity_restricted (
    i_contr_type                IN VARCHAR2,
    i_augru                     IN VARCHAR2)
    RETURN VARCHAR2;

  FUNCTION is_advance_payment_contract(
    i_contract_type_cd IN VARCHAR2,
    i_augru IN VARCHAR2) RETURN NUMBER;

  FUNCTION contract_controller_no (
    i_shipto_id IN VARCHAR2,
    i_contract_hjid IN NUMBER) RETURN VARCHAR2;
  
  FUNCTION parent_offer_number (i_item_hjid IN NUMBER) RETURN VARCHAR2;

  PROCEDURE takeover_excess_quantity(
    i_sap_contract_item_id    IN NUMBER,
    i_ap_group_leader_id      IN NUMBER,
    i_delta_target_quantity   IN NUMBER);
  
END sapimport_sdn;
/

CREATE OR REPLACE PACKAGE BODY sapimport_sdn AS

  ----------------------------------------------------------------
  ----------------------------------------------------------------
  ---------------SALES-DOCUMENT-NOTIFICATION-IMPORT---------------
  ----------------------------------------------------------------
  ----------------------------------------------------------------

  c_import_name              constant VARCHAR(30) := 'sapimport_sdn';
  c_imported_from_sap_status constant NUMBER :=  0; -- not imported
  c_imported_to_logon_status constant NUMBER := 20; -- imported successfully
  c_skipped_status           constant NUMBER :=  1; -- skipped newer available
  c_failed_abandoned         constant NUMBER :=  9; -- failed abandoned

  c_soldto                   constant VARCHAR(2) := 'AG';
  c_shipto                   constant VARCHAR(2) := 'WE';
  c_billto                   constant VARCHAR(2) := 'RE';
  c_payer                    constant VARCHAR(2) := 'RG';
  c_vendor                   constant VARCHAR(2) := 'LF';

  c_max_length_prod_plant_cd constant NUMBER := 4;

  -- days for successful import. After that record is abandoned.
  g_days_try_import NUMBER;
  g_err_msg VARCHAR2(200);
  
  ---------------------------------
  ---------------------------------
  --------tlgns_sap_contract-------
  ---------------------------------
  ---------------------------------
  PROCEDURE main(i_min_loglevel IN VARCHAR2, i_days_to_import IN NUMBER) IS

    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;

    l_final BOOLEAN := FALSE;
    l_new_state NUMBER := NULL;
    l_sqlerrm VARCHAR2(200);

    l_sap_contract_id NUMBER := NULL;
    l_sap_contract_type_cd VARCHAR2(4) := NULL;
    l_sarea_id NUMBER := NULL;
    l_soldto_sap_bp_id NUMBER := NULL;
    l_shipto_sap_bp_id NUMBER := NULL;
    l_billto_sap_bp_id NUMBER := NULL;
    l_payer_sap_bp_id NUMBER := NULL;
    l_valid_from_dat DATE := NULL;
    l_partner_function VARCHAR(2):= NULL;
    l_valid_until_dat DATE := NULL;
    l_sap_shipping_cond_cd VARCHAR2(2) := NULL;
    l_cust_purchase_order_no VARCHAR2(35) := NULL;
    l_sales_district_cd VARCHAR2(6) := NULL;
    l_incoterms_cd VARCHAR2(3) := NULL;
    l_sapimpstatuscd NUMBER := NULL;
    l_vendor_sap_no VARCHAR2(10) := NULL;

    l_sap_bp_id NUMBER := NULL;
    l_soldto_sap_bp_id_count NUMBER := NULL;
    l_shipto_sap_bp_id_count NUMBER := NULL;
    
    l_contract_is_stpo NUMBER := NULL;
    l_invent_soldto NUMBER := NULL;
    l_supply_plant_cd VARCHAR2(4) := NULL;
    l_item_grouping_type NUMBER := NULL;
    l_valid_grouping_type BOOLEAN;

    l_controller_no VARCHAR2(70) := NULL;
    l_advance_invoice_no VARCHAR2(60) := NULL;
      
    e_no_soldto_sap_bp_id EXCEPTION;
    e_too_many_soldto_sap_bp_id EXCEPTION;
    e_no_shipto_sap_bp_id EXCEPTION;
    e_too_many_shipto_sap_bp_id EXCEPTION;
    e_no_sap_shipping_cond_cd EXCEPTION;

    -- main query to select not imported newest records
    CURSOR cur_sap_contract IS
    SELECT notif.hjid contract_hjid,
      gen.belnr sap_contract_no, /* FIXME: trimming ??? */
      /* FIXME: sap_contract_ifc_no ??? */
      /* FIXME: Origin: TBD */ 0 valid_until_ignore_flag,
      gen.vsart sap_shipping_cond_cd,
      quantity_restricted(org.bsart, gen.augru) quantity_control_flag,
      is_advance_payment_contract(org.bsart, gen.augru) advance_payment_flag,
      gen.curcy currency_cd,
      gen.zterm payment_method_cd,
      credit.zkkber credit_control_area_cd,
      credit.zknkli credit_limit_account_no,
      credit.zgrupp customer_credit_group,
      credit.zsbgrp credit_management_group,
      credit.zctlpc credit_risk_category,
      credit.zcmwae credit_currency_cd,
      pricing.pincode validation_code,
      DECODE(pricing.zvatm, 'X', 1, 0) vat_no_mandatory_flag,
      notif.sapimpimporteddatelement
    FROM ulgnsap.sdn_salesdocumentnotification notif
    INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_0 notif0 ON (notif.salesdocumentnotification_sd_0 = notif0.hjid)
    INNER JOIN ulgnsap.sdn_documentheadergeneraldata gen ON (notif0.documentheadergeneraldata_sd_0 = gen.hjid)
    INNER JOIN ulgnsap.sdn_documentheaderorganizati_0 org ON (notif0.documentheaderorganizational_1 = org.hjid)
    LEFT OUTER JOIN ulgnsap.sdn_creditrelatedinfo credit ON (gen.creditrelatedinfo_sdn_credit_0 = credit.hjid)
    LEFT OUTER JOIN ulgnsap.sdn_pricingrelatedinfo pricing ON (gen.pricingrelatedinfo_sdn_prici_0 = pricing.hjid)
    WHERE notif.sapimpstatuscd = c_imported_from_sap_status
    ORDER BY notif.sapimpimporteddatelement DESC;

    CURSOR cur_sap_contract_terms(v_contract_hjid IN NUMBER) IS
    SELECT terms.inco1 incoterms_cd /* FIXME: this is only a temporary solution, waiting for clarification */
    FROM ulgnsap.sdn_salesdocumentnotification notif
    INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_0 notif0 ON (notif.salesdocumentnotification_sd_0 = notif0.hjid)
    INNER JOIN ulgnsap.sdn_documentheadertermsofdel_0 terms ON (notif0.documentheadertermsofdeliver_0 = terms.hjid)
    WHERE notif.hjid = v_contract_hjid;

    CURSOR cur_sap_contract_org(v_contract_hjid IN NUMBER) IS
    SELECT RTRIM(org.bsart) sap_contract_type_cd,
      org.vkorg sorg_cd,
      org.vtweg distribution_channel,
      org.spart division_cd,
      org.bzirk sales_district_cd,
      org.reswk supply_plant_cd
    FROM ulgnsap.sdn_salesdocumentnotification notif
    INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_0 notif0 ON (notif.salesdocumentnotification_sd_0 = notif0.hjid)
    INNER JOIN ulgnsap.sdn_documentheaderorganizati_0 org ON (notif0.documentheaderorganizational_1 = org.hjid)
    WHERE notif.hjid = v_contract_hjid;

    CURSOR cur_sap_contract_dat(v_contract_hjid IN NUMBER) IS
    SELECT DECODE(dat.kdatb, null, null, '', null, '00000000', null, dat.kdatb) valid_from_dat,
      DECODE(dat.kdate, null, null, '', null, '00000000', null, dat.kdate) valid_until_dat
    FROM ulgnsap.sdn_salesdocumentnotification notif
    INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_0 notif0 ON (notif.salesdocumentnotification_sd_0 = notif0.hjid)
    INNER JOIN ulgnsap.sdn_documentheaderdate dat ON (notif0.documentheaderdate_sdn_docum_0 = dat.hjid)
    WHERE notif.hjid = v_contract_hjid;

    CURSOR cur_sap_contract_partner(v_contract_hjid IN NUMBER) IS
    SELECT partner.parvw partner_function,
      LTRIM(partner.partn, '0') partner_number
    FROM ulgnsap.sdn_salesdocumentnotification notif
    INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_1 notif_partner ON (notif.salesdocumentnotification_sd_0 = notif_partner.parent_sdn_salesdocumentnoti_0)
    INNER JOIN ulgnsap.sdn_documentheaderpartnerinf_0 partner ON (notif_partner.child_sdn_documentheaderpart_0 = partner.hjid)
    WHERE notif.hjid = v_contract_hjid;

    CURSOR cur_sap_contract_ref(v_contract_hjid IN NUMBER) IS
    SELECT ref.bstnk cust_purchase_order_no
    FROM ulgnsap.sdn_salesdocumentnotification notif
    INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_0 notif0 ON (notif.salesdocumentnotification_sd_0 = notif0.hjid)
    INNER JOIN ulgnsap.sdn_documentheaderreferenced_0 ref ON (notif0.documentheaderreferencedata__0 = ref.hjid)
    WHERE notif.hjid = v_contract_hjid;

    CURSOR cur_sap_contract_item(v_contract_hjid IN NUMBER) IS
    SELECT orderitemadd.zvsbed sap_shipping_cond_cd
    FROM ulgnsap.sdn_salesdocumentnotification notif
    INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_4 notif_item ON (notif.salesdocumentnotification_sd_0 = notif_item.parent_sdn_salesdocumentnoti_0)
    INNER JOIN ulgnsap.sdn_documentitemgeneraldata item ON (notif_item.child_sdn_documentitemgenera_0 = item.hjid)
    LEFT OUTER JOIN ulgnsap.sdn_orderitemadditionaldata orderitemadd ON (item.orderitemadditionaldata_sdn__0 = orderitemadd.hjid)
    WHERE notif.hjid = v_contract_hjid;

  BEGIN

    g_days_try_import := i_days_to_import;

    -- set the time of the start of the import
    LOGON_LOG.start_import(c_import_name);

    -- set logging parameters
    LOGON_LOG.g_min_loglevel := i_min_loglevel;

    LOGON_LOG.INFO('SDN: load_tlgns_sap_contract', 'START loading data into ''tlgns_sap_contract'' table ...');

    FOR cur_sap_contract_record IN cur_sap_contract
    LOOP
    
      l_sap_contract_id := NULL;
      l_sap_contract_type_cd := NULL;
      l_sarea_id := NULL;

      l_soldto_sap_bp_id := NULL;
      l_shipto_sap_bp_id := NULL;
      l_billto_sap_bp_id := NULL;
      l_payer_sap_bp_id := NULL;
      
      l_soldto_sap_bp_id_count := 0;
      l_shipto_sap_bp_id_count := 0;

      l_valid_from_dat := NULL;
      l_valid_until_dat := NULL;
      l_sap_shipping_cond_cd := NULL;
      l_cust_purchase_order_no := NULL;
      l_sales_district_cd := NULL;
      l_incoterms_cd := NULL;
      l_vendor_sap_no := NULL;
      
      l_supply_plant_cd := NULL;
      
      l_controller_no := NULL;
      l_advance_invoice_no := NULL;
      
      LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract', ' Checking (sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ') ...');

      -- check whether maximum age has already been reached
      l_final := EXTRACT(DAY FROM SYSTIMESTAMP - cur_sap_contract_record.sapimpimporteddatelement) > g_days_try_import;

      g_err_msg := NULL;

      -- check if sap_contract_no exists
      BEGIN

        SELECT contr.sap_contract_id
        INTO l_sap_contract_id
        FROM tlgns_sap_contract contr
        WHERE contr.sap_contract_no = cur_sap_contract_record.sap_contract_no;

        EXCEPTION
          WHEN no_data_found THEN
            -- contract will be loaded
            l_sap_contract_id := -1;
          WHEN too_many_rows THEN
            LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', SQLCODE, SQLERRM, 'Too many SAP contracts for sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid);
            g_err_msg := 'More than one contract found for sap_contract_no ''' || cur_sap_contract_record.sap_contract_no || '''';
            RAISE;
          WHEN others THEN
            LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', SQLCODE, SQLERRM, 'Unknown exception while selecting sap_contract_id from tlgns_sap_contract for sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid);
            RAISE;

      END;

      BEGIN

        -- check if contract was already skipped (code 2) because a newer one was imported
        SELECT sapimpstatuscd 
        INTO l_sapimpstatuscd 
        FROM ulgnsap.sdn_salesdocumentnotification 
        WHERE hjid = cur_sap_contract_record.contract_hjid;

        -- if contract was skipped there is no need to import
        IF (l_sapimpstatuscd = c_skipped_status) THEN
          logon_log.info('SDN: load_tlgns_sap_contract', 'Skip (hjid = ' || cur_sap_contract_record.contract_hjid || ') because a newer version of contract (sap_contract_no = ' || cur_sap_contract_record.sap_contract_no ||' ) was imported.');
          GOTO next_contract_record;
        END IF;

        logon_log.debug('SDN: load_tlgns_sap_contract', '(sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ', quantity_control_flag: ' || cur_sap_contract_record.quantity_control_flag|| ', advance_payment_flag: ' || cur_sap_contract_record.advance_payment_flag);
        
        l_item_grouping_type := get_item_grouping_type(cur_sap_contract_record.quantity_control_flag, cur_sap_contract_record.advance_payment_flag);
        
        logon_log.debug('SDN: load_tlgns_sap_contract', '(l_sap_contract_id = ' || l_sap_contract_id || ', l_item_grouping_type: ' || l_item_grouping_type);
        
        l_valid_grouping_type := is_valid_item_grouping_type(l_sap_contract_id, l_item_grouping_type);
        
        IF NOT l_valid_grouping_type THEN
          logon_log.info('SDN: load_tlgns_sap_contract', 'Skip contract (sap_contract_id = ' || l_sap_contract_id || ', sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || '), because of invalid changing of item_grouping_type. new item_grouping_type: ' || l_item_grouping_type);
          GOTO next_contract_record;
        END IF;

        -- get all the needed additional data
        FOR cur_sap_contract_terms_record IN cur_sap_contract_terms(cur_sap_contract_record.contract_hjid)
        LOOP

          l_incoterms_cd := cur_sap_contract_terms_record.incoterms_cd;

        END LOOP;

        FOR cur_sap_contract_org_record IN cur_sap_contract_org(cur_sap_contract_record.contract_hjid)
        LOOP

          l_sap_contract_type_cd := cur_sap_contract_org_record.sap_contract_type_cd;
          l_sales_district_cd := cur_sap_contract_org_record.sales_district_cd;
          l_supply_plant_cd := cur_sap_contract_org_record.supply_plant_cd;

          -- searching corresponding sarea
          BEGIN

            SELECT sarea.sarea_id
            INTO l_sarea_id
            FROM tlgnc_sarea sarea
            WHERE sarea.sorg_cd = cur_sap_contract_org_record.sorg_cd
              AND sarea.distribution_channel = cur_sap_contract_org_record.distribution_channel
              AND sarea.division_cd = cur_sap_contract_org_record.division_cd;

            EXCEPTION
              WHEN no_data_found THEN
                LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', SQLCODE, SQLERRM, '  No sarea for sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ' (sorg_cd = ' || cur_sap_contract_org_record.sorg_cd || ' and distribution_channel = ' || cur_sap_contract_org_record.distribution_channel || ' and division_cd = ' || cur_sap_contract_org_record.division_cd || ')');
                g_err_msg := 'Sales area (sorg_cd = ' || cur_sap_contract_org_record.sorg_cd || ', distribution_channel = ' || cur_sap_contract_org_record.distribution_channel || ' and division_cd = ' || cur_sap_contract_org_record.division_cd || ' not found.';
                RAISE;
              WHEN too_many_rows THEN
                LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', SQLCODE, SQLERRM, '  Too many sarea for sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ' (sorg_cd = ' || cur_sap_contract_org_record.sorg_cd || ' and distribution_channel = ' || cur_sap_contract_org_record.distribution_channel || ' and division_cd = ' || cur_sap_contract_org_record.division_cd || ')');
                g_err_msg := 'More than one sales area found for sorg_cd = ' || cur_sap_contract_org_record.sorg_cd || ', distribution_channel = ' || cur_sap_contract_org_record.distribution_channel || ' and division_cd = ' || cur_sap_contract_org_record.division_cd || '.';
                RAISE;
              WHEN others THEN
                LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', SQLCODE, SQLERRM, '  Unknown exception while selecting sarea_id from tlgnc_sarea for sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ' (sorg_cd = ' || cur_sap_contract_org_record.sorg_cd || ' and distribution_channel = ' || cur_sap_contract_org_record.distribution_channel || ' and division_cd = ' || cur_sap_contract_org_record.division_cd || ')');
                RAISE;

          END;

        END LOOP;

        FOR cur_sap_contract_dat_record IN cur_sap_contract_dat(cur_sap_contract_record.contract_hjid)
        LOOP

          IF cur_sap_contract_dat_record.valid_from_dat IS NOT NULL THEN
            l_valid_from_dat := TO_DATE(cur_sap_contract_dat_record.valid_from_dat, 'YYYYMMDD');
          ELSE
            l_valid_from_dat := TO_DATE('19700101', 'YYYYMMDD');
          END IF;

          IF cur_sap_contract_dat_record.valid_until_dat IS NOT NULL THEN
            l_valid_until_dat := TO_DATE(cur_sap_contract_dat_record.valid_until_dat, 'YYYYMMDD');
          ELSE
            l_valid_until_dat := TO_DATE('29991231', 'YYYYMMDD');
          END IF;

        END LOOP;
        
        LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract', '  Contract valid from ' || l_valid_from_dat || ' until ' || l_valid_until_dat || '.');

        FOR cur_sap_contract_partner_rec IN cur_sap_contract_partner(cur_sap_contract_record.contract_hjid)
        LOOP
          
          l_partner_function := get_partner_function(cur_sap_contract_partner_rec.partner_function);
          
          IF l_partner_function is NULL THEN
          
            LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract', '  Skipping unknown partner function ''' || cur_sap_contract_partner_rec.partner_function || ''' (sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ', sap_contract_type = ' || l_sap_contract_type_cd || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ')');
            
          ELSE
            
            l_sap_bp_id := NULL;
            
            IF l_partner_function <> c_vendor THEN
            
              -- searching sap_bp_id of business partner
              BEGIN
                
                SELECT bp.sap_bp_id
                INTO l_sap_bp_id
                FROM tlgns_sap_bp bp
                WHERE bp.sap_id = cur_sap_contract_partner_rec.partner_number;
                
                EXCEPTION
                  WHEN no_data_found THEN
                    LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', SQLCODE, SQLERRM, '  Referenced BP ''' || cur_sap_contract_partner_rec.partner_number || ''' for partner function ''' || l_partner_function || ''' not found.');
                    -- g_err_msg := 'Referenced BP ''' || cur_sap_contract_partner_rec.partner_number || ''' for partner function ''' || l_partner_function || ''' not found.';
                    -- RAISE;
                    GOTO next_partner_record;
                  WHEN too_many_rows THEN
                    LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', SQLCODE, SQLERRM, '  Too many BPs found for referenced sap BP id ''' || cur_sap_contract_partner_rec.partner_number || ''' and partner function ''' || l_partner_function || '''.');
                    g_err_msg := 'Too many BPs found for referenced sap BP id ''' || cur_sap_contract_partner_rec.partner_number || ''' and partner function ''' || l_partner_function || '''.';
                    RAISE;
                
              END;
              
            END IF;
            
            IF  l_partner_function = c_soldto THEN
              
              l_soldto_sap_bp_id := l_sap_bp_id;
              l_soldto_sap_bp_id_count := l_soldto_sap_bp_id_count + 1;
              
            ELSIF l_partner_function = c_shipto THEN
              
              l_shipto_sap_bp_id := l_sap_bp_id;
              l_shipto_sap_bp_id_count := l_shipto_sap_bp_id_count + 1;
              
            ELSIF l_partner_function = c_billto THEN
              
              l_billto_sap_bp_id := l_sap_bp_id;
              
            ELSIF l_partner_function = c_payer THEN
              
              l_payer_sap_bp_id := l_sap_bp_id;
              
            ELSIF l_partner_function = c_vendor THEN
              
              l_vendor_sap_no := cur_sap_contract_partner_rec.partner_number;
              
            END IF;
            
          END IF;
          
          <<next_partner_record>>
          NULL;
          
        END LOOP;

        IF l_shipto_sap_bp_id_count = 0 THEN

          LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', ' SAP contract does not contain a shipto (sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ')');
          g_err_msg := 'SAP contract ''' || cur_sap_contract_record.sap_contract_no || ''' does not contain a shipto.';
          RAISE e_no_shipto_sap_bp_id;

        ELSIF l_shipto_sap_bp_id_count > 1 THEN

          LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', ' SAP contract contains more than one shipto (sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ')');
          g_err_msg := 'SAP contract ''' || cur_sap_contract_record.sap_contract_no || ''' contains more than one shipto.';
          RAISE e_too_many_shipto_sap_bp_id;

        END IF;

        -- check whether soldto must be invented (and possibly overwritten)
        l_contract_is_stpo := NULL;
        l_invent_soldto := NULL;
            
        -- read out contract type flags (STPO)
        BEGIN
        
          SELECT contract_type_is_stpo_flag
          INTO l_contract_is_stpo
          FROM TLGNR_SAP_CONTRACT_TYPE_BS
          WHERE sap_contract_type_cd = l_sap_contract_type_cd;
          
          EXCEPTION
          WHEN no_data_found THEN
            LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', 'SAP contract type ''' || l_sap_contract_type_cd || ''' not found.');
            g_err_msg := 'SAP contract type ''' || l_sap_contract_type_cd || ''' not found.';
            RAISE;
            
        END;

        l_controller_no := contract_controller_no(
            l_shipto_sap_bp_id,
            cur_sap_contract_record.contract_hjid);

        BEGIN
        
          SELECT base.invent_soldto_for_shipto
          INTO l_invent_soldto
          FROM TLGNS_SAP_BP bp
          INNER JOIN TLGNR_SAP_ACCOUNT_GROUP_BS base ON (bp.account_group_cd = base.sap_account_group_cd)
          WHERE bp.sap_bp_id = l_shipto_sap_bp_id;
          
        END;
            
        IF (l_contract_is_stpo = 1) AND (l_invent_soldto = 1) THEN
          
          LOGON_LOG.INFO('SDN: load_tlgns_sap_contract', ' Using the shipto ''' || l_shipto_sap_bp_id || ''' as soldto.');
          l_soldto_sap_bp_id := l_shipto_sap_bp_id;
          
        ELSIF l_soldto_sap_bp_id_count = 0 THEN

          LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', ' SAP contract does not contain a soldto (sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ')');
          g_err_msg := 'SAP contract ''' || cur_sap_contract_record.sap_contract_no || ''' does not contain a soldto.';
          RAISE e_no_soldto_sap_bp_id;
          
        ELSIF l_soldto_sap_bp_id_count > 1 THEN

          LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', ' SAP contract contains more than one soldto (sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ')');
          g_err_msg := 'SAP contract ''' || cur_sap_contract_record.sap_contract_no || ''' contains more than one soldto.';
          RAISE e_too_many_soldto_sap_bp_id;

        END IF;
        
        FOR cur_sap_contract_ref_record IN cur_sap_contract_ref(cur_sap_contract_record.contract_hjid)
        LOOP

          l_cust_purchase_order_no := cur_sap_contract_ref_record.cust_purchase_order_no;

        END LOOP;

        l_sap_shipping_cond_cd := cur_sap_contract_record.sap_shipping_cond_cd;
        IF (l_sap_shipping_cond_cd IS NULL OR LENGTH(l_sap_shipping_cond_cd) = 0) THEN

          FOR cur_sap_contract_item_record IN cur_sap_contract_item(cur_sap_contract_record.contract_hjid)
          LOOP

            IF (l_sap_shipping_cond_cd IS NULL OR LENGTH(l_sap_shipping_cond_cd) = 0) THEN

              l_sap_shipping_cond_cd := cur_sap_contract_item_record.sap_shipping_cond_cd;

            END IF;

          END LOOP;

          IF (l_sap_shipping_cond_cd IS NULL OR LENGTH(l_sap_shipping_cond_cd) = 0) THEN

            LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', ' SAP contract does not contain a shipping condition (sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ')');
            g_err_msg := 'SAP contract ''' || cur_sap_contract_record.sap_contract_no || ''' does not contain a shipping condition.';
            RAISE e_no_sap_shipping_cond_cd;

          END IF;

        END IF;

        -- insert a new contract
        IF l_sap_contract_id = -1 THEN
 
          SELECT slgns_sap_contract.nextval
          INTO l_sap_contract_id
          FROM dual;

          LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract', ' Loading new SAP contract (sap_contract_id = ' || l_sap_contract_id || ' and sap_contract_type = ' || l_sap_contract_type_cd || ')...');

          -- a new entry in tlgns_sap_contract table
          INSERT
          INTO tlgns_sap_contract (sap_contract_id,
            sap_contract_no,
            sap_contract_type_cd,
            sarea_id,
            soldto_sap_bp_id,
            shipto_sap_bp_id,
            billto_sap_bp_id,
            payer_sap_bp_id,
            valid_from_dat,
            valid_until_dat,
            valid_until_ignore_flag,
            sap_shipping_cond_cd,
            cust_purchase_order_no,
            item_grouping_type,
            currency_cd,
            credit_control_area_cd,
            credit_limit_account_no,
            customer_credit_group,
            credit_management_group,
            credit_risk_category,
            credit_currency_cd,
            sales_district_cd,
            incoterms_cd,
            validation_code,
            vendor_sap_no,
            vat_no_mandatory_flag,
            payment_method_cd,
            controller_no,
            ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
          VALUES (l_sap_contract_id,
            cur_sap_contract_record.sap_contract_no,
            l_sap_contract_type_cd,
            l_sarea_id,
            l_soldto_sap_bp_id,
            l_shipto_sap_bp_id,
            l_billto_sap_bp_id,
            l_payer_sap_bp_id,
            l_valid_from_dat,
            l_valid_until_dat,
            cur_sap_contract_record.valid_until_ignore_flag,
            l_sap_shipping_cond_cd,
            l_cust_purchase_order_no,
            l_item_grouping_type,
            cur_sap_contract_record.currency_cd,
            cur_sap_contract_record.credit_control_area_cd,
            cur_sap_contract_record.credit_limit_account_no,
            cur_sap_contract_record.customer_credit_group,
            cur_sap_contract_record.credit_management_group,
            cur_sap_contract_record.credit_risk_category,
            cur_sap_contract_record.credit_currency_cd,
            l_sales_district_cd,
            l_incoterms_cd,
            cur_sap_contract_record.validation_code,
            l_vendor_sap_no,
            cur_sap_contract_record.vat_no_mandatory_flag,
            cur_sap_contract_record.payment_method_cd,
            l_controller_no,
            1, systimestamp, 1, systimestamp, 0);

          l_ins_total := l_ins_total + 1;

        -- update contract
        ELSE

          LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract', ' Updating SAP contract (sap_contract_id = ' || l_sap_contract_id || ' and sap_contract_type = ' || l_sap_contract_type_cd || ')...');

          UPDATE tlgns_sap_contract
          SET sap_contract_type_cd = l_sap_contract_type_cd,
            sarea_id = l_sarea_id,
            soldto_sap_bp_id = l_soldto_sap_bp_id,
            shipto_sap_bp_id = l_shipto_sap_bp_id,
            billto_sap_bp_id = l_billto_sap_bp_id,
            payer_sap_bp_id = l_payer_sap_bp_id,
            valid_from_dat = l_valid_from_dat,
            valid_until_dat = l_valid_until_dat,
            valid_until_ignore_flag = cur_sap_contract_record.valid_until_ignore_flag,
            sap_shipping_cond_cd = l_sap_shipping_cond_cd,
            cust_purchase_order_no = l_cust_purchase_order_no,
            item_grouping_type = l_item_grouping_type,
            currency_cd = cur_sap_contract_record.currency_cd,
            credit_control_area_cd = cur_sap_contract_record.credit_control_area_cd,
            credit_limit_account_no = cur_sap_contract_record.credit_limit_account_no,
            customer_credit_group = cur_sap_contract_record.customer_credit_group,
            credit_management_group = cur_sap_contract_record.credit_management_group,
            credit_risk_category = cur_sap_contract_record.credit_risk_category,
            credit_currency_cd = cur_sap_contract_record.credit_currency_cd,
            sales_district_cd = l_sales_district_cd,
            incoterms_cd = l_incoterms_cd,
            validation_code = cur_sap_contract_record.validation_code,
            vendor_sap_no = l_vendor_sap_no,
            vat_no_mandatory_flag = cur_sap_contract_record.vat_no_mandatory_flag,
            payment_method_cd = cur_sap_contract_record.payment_method_cd,
            controller_no = l_controller_no,
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_contract_id = l_sap_contract_id;

          l_upd_total := l_upd_total + 1;

        END IF;

        -- overwrite item supply plant only for LU / UB contracts (see LOGON-5484)
        IF l_sap_contract_type_cd NOT IN ('LU', 'UB') THEN
          l_supply_plant_cd := NULL;
        END IF;
        
        -- update other tables
        load_tlgns_sap_contract_item(
            cur_sap_contract_record.contract_hjid,
            l_sap_contract_id,
            l_supply_plant_cd,
            l_contract_is_stpo,
            l_item_grouping_type);

        -- update controlling attributes
        UPDATE ulgnsap.sdn_salesdocumentnotification
        SET sapimpstatuscd = c_imported_to_logon_status,
          sapimperrmsg = null,
          sapimppropagateddatelement = systimestamp
        WHERE hjid = cur_sap_contract_record.contract_hjid;

        -- update older entries
        UPDATE ulgnsap.sdn_salesdocumentnotification notif
        SET notif.sapimpstatuscd = c_skipped_status,
          notif.sapimppropagateddatelement = systimestamp
        WHERE notif.sapimpstatuscd <> c_imported_to_logon_status
          AND notif.sapimpimporteddatelement < cur_sap_contract_record.sapimpimporteddatelement
          AND notif.hjid IN (
                  SELECT notif_inner.hjid
                    FROM ulgnsap.sdn_salesdocumentnotification notif_inner
              INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_0 notif0 ON (notif_inner.salesdocumentnotification_sd_0 = notif0.hjid)
              INNER JOIN ulgnsap.sdn_documentheadergeneraldata gen ON (notif0.documentheadergeneraldata_sd_0 = gen.hjid)
                   WHERE gen.belnr = cur_sap_contract_record.sap_contract_no);

        COMMIT;
        
        LOGON_LOG.INFO('SDN: load_tlgns_sap_contract', ' SAP contract (sap_contract_id = ' || l_sap_contract_id || ') has been loaded/updated.');

        EXCEPTION
        
          WHEN others THEN
          BEGIN
          
            ROLLBACK;
            LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', SQLCODE, SQLERRM, 'ROLLBACK on SAP contract (sap_contract_id = ' || l_sap_contract_id || ' and sap_contract_no = ' || cur_sap_contract_record.sap_contract_no || ' and hjid = ' || cur_sap_contract_record.contract_hjid || ').');
  
            l_sqlerrm := SQLERRM;
            
            IF l_final THEN
              l_new_state := c_failed_abandoned;
            ELSE
              l_new_state := c_imported_from_sap_status;
            END IF;
  
            -- update controlling attributes
            UPDATE ulgnsap.sdn_salesdocumentnotification
            SET sapimpstatuscd = l_new_state,
              sapimperrmsg = COALESCE(g_err_msg, l_sqlerrm),
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_sap_contract_record.contract_hjid;
  
            COMMIT;
            
            IF l_final THEN
              LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract', NULL, NULL, 'SAP contract (sap_contract_id = ' || l_sap_contract_id || ') has been abandoned. Days for successful import = ' || i_days_to_import);
            END IF;
            
          END;
  
      END;

      <<next_contract_record>>
      NULL;
      
    END LOOP;

	LOGON_LOG.INFO('SDN: load_tlgns_sap_contract', 'FINISH! Updated ' || l_upd_total || ' and inserted ' || l_ins_total || ' records into ''tlgns_sap_contract'' table!');
	
	BEGIN
		LOGON_LOG.INFO('SDN: load_tlgns_sap_contract', 'SDN: Start maintaining planning materials' );
	    
		ulgn01.upsert_planning_contract_items();
		
		LOGON_LOG.INFO('SDN: load_tlgns_sap_contract', 'SDN: End maintaining planning materials');
		
		COMMIT;

	EXCEPTION
        WHEN others THEN
        	BEGIN
	        	ROLLBACK;
          		LOGON_LOG.ERROR('SDN: maintaining planning materials failed', SQLCODE, SQLERRM, '    Unknown exception while maintaining planning material contract items');
          	END;
	END;

    -- set the cem & agg valid until dates on tlgns_sap_bp for the shiptos
	BEGIN
		LOGON_LOG.INFO('SDN: load_tlgns_sap_contract', 'SDN: Start updating max validity date for shipto business partner' );

		ulgn01.update_shipto_bp_meta(i_min_loglevel);

		LOGON_LOG.INFO('SDN: load_tlgns_sap_contract', 'SDN: End updating max validity date for shipto business partner');

		commit;

	EXCEPTION
		WHEN others THEN
			BEGIN
				ROLLBACK;
				LOGON_LOG.ERROR('SDN: Start updating max validity date for shipto business partner failed', SQLCODE, SQLERRM, '' ||
				                                                                                                              '    Unknown exception while updating max validity date for shipto business partner');
			END;
	END;
    -- set the run number and time of the end of the import
    LOGON_LOG.finish_import(c_import_name);
    
  END;

  ---------------------------------------------------
  ---------------------------------------------------
  --------------tlgns_sap_contract_item--------------
  ---------------------------------------------------
  ---------------------------------------------------
  PROCEDURE load_tlgns_sap_contract_item(
    i_contract_hjid           IN NUMBER,
    i_sap_contract_id         IN NUMBER,
    i_header_supply_plant_cd  IN VARCHAR2,
    i_contract_type_is_stpo   IN NUMBER,
    i_item_grouping_type      IN NUMBER)
  IS
    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;

    l_sap_contract_item_id NUMBER := NULL;
    l_sap_material_id NUMBER := NULL;
    l_supply_plant_cd VARCHAR2(4) := NULL;
    l_material_group_1 VARCHAR2(3) := NULL;
    l_material_group_2 VARCHAR2(3) := NULL;
    l_material_group_3 VARCHAR2(3) := NULL;
    l_material_group_4 VARCHAR2(3) := NULL;
    l_material_group_5 VARCHAR2(3) := NULL;
    l_target_quantity_uom_cd VARCHAR2(3) := NULL;
    l_marked_for_deletion NUMBER := NULL;
    l_transport_costs_included NUMBER := NULL;
    l_ap_group_leader_id NUMBER := NULL;
    l_delta_target_quantity NUMBER := NULL;

    l_input_params_str VARCHAR2(4000) := '    Loading data into tlgns_sap_contract_item (sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ', i_sap_contract_id = ' || i_sap_contract_id || ', i_header_supply_plant_cd = ' || i_header_supply_plant_cd || ', i_contract_type_is_stpo = ' || i_contract_type_is_stpo || ', i_item_grouping_type = ' || i_item_grouping_type || ')';
	l_advance_invoice_no VARCHAR2(60) := NULL;
    l_parent_offer_num VARCHAR2(70) := NULL;
    l_orig_parent_offer_num VARCHAR2(70) := NULL;
	
    e_no_sap_material EXCEPTION;
    e_no_target_quantity_uom_cd EXCEPTION;
    e_invalid_uom_cd EXCEPTION;
    e_parent_offer_no_is_not_null EXCEPTION;

    CURSOR cur_sap_contract_item(v_contract_hjid IN NUMBER) IS
      SELECT item.hjid contract_item_hjid,
        item.posex contract_item_no,
        item.werks supply_plant_cd,
        item.pstyv item_category,
        item.abgru rejection_reason,
        orderitemadd.zptentr target_quantity,
        orderitemadd.zbmeins target_quantity_uom_cd,
        orderitemadd.zpvat vat_percentage,
        orderitemadd.zloekz stpo_deletion_indicator,
        item.netwr net_value,
        extra.zqualz,
        extra.zcusadd
      FROM ulgnsap.sdn_salesdocumentnotification notif
      INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_0 notif_item_0 ON (notif.salesdocumentnotification_sd_0 = notif_item_0.hjid)
      INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_4 notif_item_4 ON (notif.salesdocumentnotification_sd_0 = notif_item_4.parent_sdn_salesdocumentnoti_0)
      INNER JOIN ulgnsap.sdn_documentitemgeneraldata item ON (notif_item_4.child_sdn_documentitemgenera_0 = item.hjid)
      LEFT OUTER JOIN ulgnsap.sdn_orderitemadditionaldata orderitemadd ON (item.orderitemadditionaldata_sdn__0 = orderitemadd.hjid)
      LEFT OUTER JOIN ULGNSAP.sdn_documentitemgeneraldata__5 extra_5 ON (extra_5.parent_sdn_documentitemgener_0 = item.hjid)
      LEFT OUTER JOIN ULGNSAP.sdn_orderitemextradata extra ON (extra_5.child_sdn_orderitemextradata_0 = extra.hjid)
      WHERE notif.hjid = v_contract_hjid;

    CURSOR cur_sap_contract_object(v_contract_item_hjid IN NUMBER) IS
      SELECT LTRIM(object.matnr, '0') sap_material_no
      FROM ulgnsap.sdn_documentitemgeneraldata item
      INNER JOIN ulgnsap.sdn_documentitemobjectidenti_0 object ON (item.documentitemobjectidentifica_1 = object.hjid)
      WHERE item.hjid = v_contract_item_hjid;

    CURSOR cur_sap_contract_docitemad(v_contract_item_hjid IN NUMBER) IS
      SELECT docitemad.mvgr1 material_group_1,
        docitemad.mvgr2 material_group_2,
        docitemad.mvgr3 material_group_3,
        docitemad.mvgr4 material_group_4,
        docitemad.mvgr5 material_group_5,
        docitemad.kzwi3 transport_costs_included
      FROM ulgnsap.sdn_documentitemgeneraldata item
      INNER JOIN ulgnsap.sdn_documentitemadditionalda_0 docitemad ON (item.documentitemadditionaldata_s_0 = docitemad.hjid)
      WHERE item.hjid = v_contract_item_hjid;

  BEGIN
    LOGON_LOG.INFO('SDN: load_tlgns_sap_contract_item', l_input_params_str);

    FOR cur_sap_contract_item_record IN cur_sap_contract_item(i_contract_hjid)
    LOOP

      l_sap_contract_item_id := NULL;
      l_sap_material_id := NULL;
      l_supply_plant_cd := NULL;
      l_material_group_1 := NULL;
      l_material_group_2 := NULL;
      l_material_group_3 := NULL;
      l_material_group_4 := NULL;
      l_material_group_5 := NULL;
      l_target_quantity_uom_cd := NULL;
      l_marked_for_deletion := NULL;
      l_transport_costs_included := NULL;
      l_ap_group_leader_id := NULL;
      l_advance_invoice_no := NULL;
      l_delta_target_quantity := 0;
      l_parent_offer_num := NULL;
      l_orig_parent_offer_num := NULL;

      -- check whether contract item exists
      BEGIN

        SELECT sapitem.sap_contract_item_id, sapitem.parent_offer_no
        INTO l_sap_contract_item_id, l_orig_parent_offer_num
        FROM tlgns_sap_contract_item sapitem
        WHERE sapitem.sap_contract_id = i_sap_contract_id
          AND sapitem.contract_item_no = cur_sap_contract_item_record.contract_item_no;

        EXCEPTION
          WHEN no_data_found THEN
            -- contract item will be loaded
            l_sap_contract_item_id := -1;
          WHEN others THEN
            LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract_item', SQLCODE, SQLERRM, '    Unknown exception while selecting sap_contract_item_id from tlgns_sap_contract_item (sap_contract_id = ' || i_sap_contract_id || ' and contract_item_no = ' || cur_sap_contract_item_record.contract_item_no || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ')');

      END;

      -- check whether contract item exists
      IF (l_sap_contract_item_id IS NOT NULL) THEN

      	-- check whether contract item is rejected ("normal" contract) or deleted (STPO)
      	l_marked_for_deletion := 0;
      	IF (i_contract_type_is_stpo = 1) THEN
      		IF (cur_sap_contract_item_record.stpo_deletion_indicator IS NOT NULL) THEN
      			l_marked_for_deletion := 1;
      		END IF;
      	ELSE
      	    IF (cur_sap_contract_item_record.rejection_reason IS NOT NULL) THEN
      			l_marked_for_deletion := 1;
      		END IF;
      	END IF;

      	-- get the advance invoice number
      	IF (cur_sap_contract_item_record.zqualz = '002') THEN
      		l_advance_invoice_no := cur_sap_contract_item_record.zcusadd;
      	END IF;
      	
      	-- get the parent offer reference
      	l_parent_offer_num := parent_offer_number(cur_sap_contract_item_record.contract_item_hjid);
      	
        -- get all the needed additional data
        FOR cur_sap_contract_object_record IN cur_sap_contract_object(cur_sap_contract_item_record.contract_item_hjid)
        LOOP

          l_sap_material_id := NULL;

          -- searching sap_material_id of material
          BEGIN

            SELECT mat.sap_material_id
            INTO l_sap_material_id
            FROM tlgns_sap_material mat
            WHERE mat.sap_material_no = cur_sap_contract_object_record.sap_material_no;

            EXCEPTION
              WHEN no_data_found THEN
                LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract_item', SQLCODE, SQLERRM, '    No SAP material for sap_contract_id = ' || i_sap_contract_id || ' and sdn_documentitemgeneraldata.hjid = ' || cur_sap_contract_item_record.contract_item_hjid || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ' (sap_material_no = ' || cur_sap_contract_object_record.sap_material_no || ')');
                RAISE e_no_sap_material;
              WHEN too_many_rows THEN
                LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract_item', SQLCODE, SQLERRM, '    Too many SAP material for sap_contract_id = ' || i_sap_contract_id || ' and sdn_documentitemgeneraldata.hjid = ' || cur_sap_contract_item_record.contract_item_hjid || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ' (sap_material_no = ' || cur_sap_contract_object_record.sap_material_no || ')');
                RAISE e_no_sap_material;
              WHEN others THEN
                LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract_item', SQLCODE, SQLERRM, '    Unknown exception while selecting sap_material_id from tlgns_sap_material for sap_contract_id = ' || i_sap_contract_id || ' and sdn_documentitemgeneraldata.hjid = ' || cur_sap_contract_item_record.contract_item_hjid || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ' (sap_material_no = ' || cur_sap_contract_object_record.sap_material_no || ')');
                RAISE e_no_sap_material;

          END;

        END LOOP;

        -- Default value
        l_transport_costs_included := 0;

        FOR cur_sap_contract_docitemad_rec IN cur_sap_contract_docitemad(cur_sap_contract_item_record.contract_item_hjid)
        LOOP

          l_material_group_1 := cur_sap_contract_docitemad_rec.material_group_1;
          l_material_group_2 := cur_sap_contract_docitemad_rec.material_group_2;
          l_material_group_3 := cur_sap_contract_docitemad_rec.material_group_3;
          l_material_group_4 := cur_sap_contract_docitemad_rec.material_group_4;
          l_material_group_5 := cur_sap_contract_docitemad_rec.material_group_5;          
          
          -- If the cost exists AND greater than zero 
          IF (cur_sap_contract_docitemad_rec.transport_costs_included IS NOT NULL) AND (cur_sap_contract_docitemad_rec.transport_costs_included > 0) THEN          
            l_transport_costs_included := 1;
          END IF;

        END LOOP;

        IF cur_sap_contract_item_record.target_quantity IS NOT NULL AND (cur_sap_contract_item_record.target_quantity_uom_cd IS NULL OR cur_sap_contract_item_record.target_quantity_uom_cd = '') THEN

          LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract_item', NULL, NULL, '    tlgns_sap_contract_item.target_quantity_uom_cd cannot be empty if tlgns_sap_contract_item.target_quantity is set (sap_contract_id = ' || i_sap_contract_id || ' and contract_item_no = ' || cur_sap_contract_item_record.contract_item_no || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ')');
          RAISE e_no_target_quantity_uom_cd;

        END IF;

        -- converting UoM
        IF (cur_sap_contract_item_record.target_quantity_uom_cd IS NOT NULL AND LENGTH(cur_sap_contract_item_record.target_quantity_uom_cd) > 0) THEN
          BEGIN

            SELECT map.uom_cd
            INTO l_target_quantity_uom_cd
            FROM tlgnr_map_sap_uom_bs map
            WHERE map.map_sap_uom_cd = cur_sap_contract_item_record.target_quantity_uom_cd;

            EXCEPTION
              WHEN no_data_found THEN
                LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract_item', SQLCODE, SQLERRM, '    No UoM mapping in tlgnr_map_sap_uom_bs for sap_contract_id = ' || i_sap_contract_id || ' and sdn_documentitemgeneraldata.hjid = ' || cur_sap_contract_item_record.contract_item_hjid || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ' (target_quantity_uom_cd = ' || cur_sap_contract_item_record.target_quantity_uom_cd || ')');
                RAISE e_invalid_uom_cd;
              WHEN others THEN
                LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract_item', SQLCODE, SQLERRM, '    Unknown exception while selecting uom_cd from tlgnr_map_sap_uom_bs for sap_contract_id = ' || i_sap_contract_id || ' and sdn_documentitemgeneraldata.hjid = ' || cur_sap_contract_item_record.contract_item_hjid || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ' (target_quantity_uom_cd = ' || cur_sap_contract_item_record.target_quantity_uom_cd || ')');
                RAISE e_invalid_uom_cd;

          END;
        END IF;
        
        -- overwrite supply_plant with the one from the header if given (see LOGON-5484)
        IF i_header_supply_plant_cd IS NOT NULL THEN
          l_supply_plant_cd := i_header_supply_plant_cd;
        ELSE
          l_supply_plant_cd := cur_sap_contract_item_record.supply_plant_cd;
        END IF;

        IF (i_item_grouping_type = 3) THEN
          l_ap_group_leader_id := ap_cre_upd_group_leader(
              i_sap_contract_id,
              l_sap_material_id,
              l_supply_plant_cd,
              l_target_quantity_uom_cd,
              cur_sap_contract_item_record.vat_percentage,
              l_transport_costs_included,
              l_sap_contract_item_id,
              l_marked_for_deletion,
              cur_sap_contract_item_record.target_quantity,
              l_delta_target_quantity);

        ELSIF (i_item_grouping_type = 2) THEN
          l_ap_group_leader_id := qr_cre_upd_group_leader(
              l_sap_contract_item_id,
              i_sap_contract_id,
              l_sap_material_id,
              l_supply_plant_cd,
              l_material_group_1,
              l_material_group_2,
              l_material_group_3,
              l_material_group_4,
              l_material_group_5,
              cur_sap_contract_item_record.item_category,
              cur_sap_contract_item_record.target_quantity,
              l_target_quantity_uom_cd,
              cur_sap_contract_item_record.net_value,
              cur_sap_contract_item_record.vat_percentage,
              l_marked_for_deletion,
              l_transport_costs_included);
        END IF;
          
        IF (l_sap_contract_item_id = -1) THEN          
          -- insert a new contract item

          SELECT slgns_sap_contract_item.nextval
          INTO l_sap_contract_item_id
          FROM dual;

          LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract_item', '    Inserting new SAP contract item sap_contract_item_id = ' || l_sap_contract_item_id || ' (sap_contract_id = ' || i_sap_contract_id || ' and contract_item_no = ' || cur_sap_contract_item_record.contract_item_no || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ' and l_ap_group_leader_id = ' || l_ap_group_leader_id || ' and l_advance_invoice_no = ' || l_advance_invoice_no || ')...');

          INSERT
          INTO tlgns_sap_contract_item (sap_contract_item_id,
            sap_contract_id,
            contract_item_no,
            sap_material_id,
            supply_plant_cd,
            material_group_1,
            material_group_2,
            material_group_3,
            material_group_4,
            material_group_5,
            item_category,
            target_quantity,
            target_quantity_uom_cd,
            net_value,
            vat_percentage,
            marked_for_deletion,
            transport_cost_included_flag,
            is_ap_group_leader,
            ap_group_leader_id,
            total_sent_quantity,
            advance_invoice_no,
            parent_offer_no,
            ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
          VALUES (l_sap_contract_item_id,
            i_sap_contract_id,
            cur_sap_contract_item_record.contract_item_no,
            l_sap_material_id,
            l_supply_plant_cd,
            l_material_group_1,
            l_material_group_2,
            l_material_group_3,
            l_material_group_4,
            l_material_group_5,
            cur_sap_contract_item_record.item_category,
            cur_sap_contract_item_record.target_quantity,
            l_target_quantity_uom_cd,
            cur_sap_contract_item_record.net_value,
            cur_sap_contract_item_record.vat_percentage,
            l_marked_for_deletion,
            l_transport_costs_included,
            0,  /* always a group member, never a leader */
            l_ap_group_leader_id,
            0,  /* nothing has been sent out yet - only on FINISH */
            l_advance_invoice_no,
            l_parent_offer_num,
            1, systimestamp, 1, systimestamp, 0);
			
          INSERT INTO TLGNC_SHIPTO_SAREA_REST_MAINT(SHIPTO_SAREA_REST_MAINT_ID, SAP_CONTRACT_ITEM_ID) values (SLGNC_SHIPTO_SAREA_REST_MAINT.nextval, l_sap_contract_item_id);
            
          LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract_item', '    SAP contract item sap_contract_item_id = ' || l_sap_contract_item_id || ' (sap_contract_id = ' || i_sap_contract_id || ' and contract_item_no = ' || cur_sap_contract_item_record.contract_item_no || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ') was successfully inserted.');

          l_ins_total := l_ins_total + 1;

        ELSIF (l_orig_parent_offer_num IS NULL OR l_orig_parent_offer_num = l_parent_offer_num) THEN
          -- update contract item

          LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract_item', '    Updating SAP contract item sap_contract_item_id = ' || l_sap_contract_item_id || ' (sap_contract_id = ' || i_sap_contract_id || ' and contract_item_no = ' || cur_sap_contract_item_record.contract_item_no || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ' and l_ap_group_leader_id = ' || l_ap_group_leader_id || ' and l_advance_invoice_no = ' || l_advance_invoice_no || ')...');

          UPDATE tlgns_sap_contract_item
          SET sap_material_id = l_sap_material_id,
            supply_plant_cd = l_supply_plant_cd,
            material_group_1 = l_material_group_1,
            material_group_2 = l_material_group_2,
            material_group_3 = l_material_group_3,
            material_group_4 = l_material_group_4,
            material_group_5 = l_material_group_5,
            item_category = cur_sap_contract_item_record.item_category,
            target_quantity = cur_sap_contract_item_record.target_quantity,
            target_quantity_uom_cd = l_target_quantity_uom_cd,
            net_value = cur_sap_contract_item_record.net_value,
            vat_percentage = cur_sap_contract_item_record.vat_percentage,
            marked_for_deletion = l_marked_for_deletion,
            transport_cost_included_flag = l_transport_costs_included,
            is_ap_group_leader = 0,  /* always a group member, never a leader */
            ap_group_leader_id = l_ap_group_leader_id, 
            advance_invoice_no = l_advance_invoice_no,
            parent_offer_no = l_parent_offer_num,
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_contract_item_id = l_sap_contract_item_id;

          LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract_item', '    SAP contract item sap_contract_item_id = ' || l_sap_contract_item_id || ' (sap_contract_id = ' || i_sap_contract_id || ' and contract_item_no = ' || cur_sap_contract_item_record.contract_item_no || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ') was successfully updated.');

          l_upd_total := l_upd_total + 1;
		
        ELSE
        	
        	LOGON_LOG.ERROR('SDN: load_tlgns_sap_contract_item', '    Updating SAP contract item DENIED because parent_offer_no is not null, sap_contract_item_id = ' || l_sap_contract_item_id || ' (sap_contract_id = ' || i_sap_contract_id || ' and contract_item_no = ' || cur_sap_contract_item_record.contract_item_no || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ' and l_ap_group_leader_id = ' || l_ap_group_leader_id || ' and l_advance_invoice_no = ' || l_advance_invoice_no || ')...');
        	RAISE e_parent_offer_no_is_not_null;
        END IF;

        IF (i_item_grouping_type = 3 and l_delta_target_quantity > 0) THEN
          takeover_excess_quantity(l_sap_contract_item_id, l_ap_group_leader_id, l_delta_target_quantity);
        END IF;

      END IF;

    END LOOP;

    LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract_item', '    Deleting from tlgns_sap_contract_item (sap_contract_id = ' || i_sap_contract_id || ' and sdn_salesdocumentnotification.hjid = ' || i_contract_hjid || ')...');

    DELETE FROM tlgns_sap_contract_item
    WHERE sap_contract_id = i_sap_contract_id
      AND contract_item_no NOT IN
      (SELECT item.posex contract_item_no
       FROM ulgnsap.sdn_salesdocumentnotification notif
       INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_4 notif_item ON (notif.salesdocumentnotification_sd_0 = notif_item.parent_sdn_salesdocumentnoti_0)
       INNER JOIN ulgnsap.sdn_documentitemgeneraldata item ON (notif_item.child_sdn_documentitemgenera_0 = item.hjid)
       WHERE notif.hjid = i_contract_hjid)
      AND ap_group_leader_id IS NOT NULL;

    LOGON_LOG.DEBUG('SDN: load_tlgns_sap_contract_item', '    Updating group leaders for advance payments and quantity controlled contracts (sap_contract_id = ' || i_sap_contract_id || ', i_item_grouping_type = ' || i_item_grouping_type);
    IF i_item_grouping_type = 3 or i_item_grouping_type = 2 THEN
      update_gl_qty_and_del_flag(i_sap_contract_id);
	END IF;
    
    LOGON_LOG.INFO('SDN: load_tlgns_sap_contract_item', '    Inserted ' || l_ins_total || ' and updated ' || l_upd_total || ' records in tlgns_sap_contract_item table.');
  END load_tlgns_sap_contract_item;

  -----------------------------------------------------------------------
  --   get_item_grouping_type
  --
  --Calculates item_grouping_type to be stored in tlgns_sap_contract by--
  --quantity control flag and advance payment flag.----------------------
  -----------------------------------------------------------------------
  FUNCTION get_item_grouping_type(
    quantity_control_flag   IN NUMBER,
    advance_payment_flag    IN NUMBER)
  RETURN NUMBER
  IS
    item_grouping_type NUMBER := 0;
  BEGIN
    IF advance_payment_flag = 1 THEN
      item_grouping_type := 3;
    ELSIF quantity_control_flag = 1 AND advance_payment_flag = 0 THEN
      item_grouping_type := 2;
    ELSE
      item_grouping_type := 1;
    END IF;
    RETURN item_grouping_type;
    
  END get_item_grouping_type;
  
  -------------------------------------------------------------------------------------------------
  --   is_valid_item_grouping_type 
  --
  --Compares grouping type of a contract by comparing old value and new value. For a new contract
  --(to be inserted) it is always valid; for a contract to be updated it mustn't be changed.
  --Exceptional case: if item_grouping_type was unknown (0), both quantity restricted and
  --advance payment groupint type is accepted (unknown is because of historical reasons).
  -------------------------------------------------------------------------------------------------
  FUNCTION is_valid_item_grouping_type(
    i_sap_contract_id    IN NUMBER,
    item_grouping_type   IN NUMBER)
  RETURN BOOLEAN
  IS
    valid_item_grouping_type  BOOLEAN := FALSE;
    old_item_grouping_type    NUMBER  := NULL;
  BEGIN
    logon_log.debug('SDN: is_valid_item_grouping_type', '(i_sap_contract_id = ' || i_sap_contract_id || ', item_grouping_type = ' || item_grouping_type);
    
    IF i_sap_contract_id = -1 THEN
      valid_item_grouping_type := TRUE;
    ELSE
      SELECT contr.item_grouping_type
      INTO old_item_grouping_type
      FROM tlgns_sap_contract contr
      WHERE contr.sap_contract_id = i_sap_contract_id;
      
      logon_log.debug('SDN: is_valid_item_grouping_type', '(i_sap_contract_id = ' || i_sap_contract_id || ', old_item_grouping_type = ' || old_item_grouping_type);
    
      IF (old_item_grouping_type = item_grouping_type
          OR (old_item_grouping_type = 0 AND item_grouping_type = 2)
          OR (old_item_grouping_type = 0 AND item_grouping_type = 3) ) THEN
        valid_item_grouping_type := TRUE;
      END IF;
    END IF;
    
    RETURN valid_item_grouping_type;
    
  END is_valid_item_grouping_type;
  
  -----------------------------------------------------------------------------
  -- ap_cre_upd_group_leader
  --
  -- Create or Update the Group Leader for the Advance Payment contract
  --
  -- Group leaders are nothing more than a dummy contract item which other
  -- contract items reference and serve as the common point for a set of
  -- contract item members. For performance reasons, they also maintain two
  -- cached values:
  --
  -- TARGET_QUANTITY    : The sum of all target quantities for all contract members
  -- TOTAL_SENT_QUANTITY: The sum of all delivered quantities for all associated
  --                      ordered items
  --
  -- Input:
  --  i_sap_contract_id         : PK tlgns_sap_contract (Search criteria)
  --  i_sap_material_id         : PK tlgns_sap_material (Search criteria)
  --  i_supply_plant_cd         : Search criteria
  --  i_target_quantity_uom_cd  : Search criteria
  --  i_vat_percentage          : Search criteria
  --  i_transport_costs_included: Search criteria
  --  i_sap_contract_item_id    : id of SAP_CONTRACT_ITEM currently being added (group member), -1 new
  --  i_marked_for_deletion     : marked for deletion flag has influence on calculated target quantity,
  --  i_new_target_quantity     : the target quantity for the new or to-be-updated SAP_CONTRACT_ITEM
  -----------------------------------------------------------------------------
  FUNCTION ap_cre_upd_group_leader(
    i_sap_contract_id           IN NUMBER,
    i_sap_material_id           IN NUMBER,
    i_supply_plant_cd           IN VARCHAR2,
    i_target_quantity_uom_cd    IN VARCHAR2,
    i_vat_percentage            IN NUMBER,
    i_transport_costs_included  IN NUMBER,
    i_sap_contract_item_id      IN NUMBER,
    i_marked_for_deletion       IN NUMBER,
    i_new_target_quantity       IN NUMBER,
    o_delta_target_quantity     OUT NUMBER)
  RETURN NUMBER
  IS
    r_group_leader_id       NUMBER := NULL;
    l_old_target_quantity   NUMBER := 0;
    l_total_target_quantity NUMBER := 0;
    l_new_target_quantity   NUMBER := 0;

    l_input_params_str VARCHAR2(4000) := '        Input params: (i_sap_contract_id = ' || i_sap_contract_id || ', i_sap_material_id = ' || i_sap_material_id || ', i_supply_plant_cd = ' || i_supply_plant_cd || ', i_target_quantity_uom_cd = ' || i_target_quantity_uom_cd || ', i_vat_percentage = ' || i_vat_percentage || ', i_transport_costs_included = ' || i_transport_costs_included || ', i_sap_contract_item_id = ' || i_sap_contract_item_id || ', i_marked_for_deletion = ' || i_marked_for_deletion || ', i_new_target_quantity = ' || i_new_target_quantity || ')';
  BEGIN
    LOGON_LOG.INFO('SDN: ap_cre_upd_group_leader', l_input_params_str);

    -- find the group leader based on the search criteria outlined in
    -- LRQ 303.0 Advance Payment Solution, Chapter 4.3.1.2
    BEGIN
      SELECT sapitem.sap_contract_item_id
      INTO r_group_leader_id
      FROM tlgns_sap_contract_item sapitem
      WHERE sapitem.sap_contract_id = i_sap_contract_id
            AND sapitem.sap_material_id = i_sap_material_id
            AND sapitem.supply_plant_cd = i_supply_plant_cd
            AND sapitem.target_quantity_uom_cd = i_target_quantity_uom_cd
            AND sapitem.vat_percentage = i_vat_percentage
            AND sapitem.transport_cost_included_flag = i_transport_costs_included
            AND sapitem.ap_group_leader_id IS NULL
            AND sapitem.is_ap_group_leader = 1;

      LOGON_LOG.INFO('SDN: ap_cre_upd_group_leader', '        Found group_leader_id = ' || r_group_leader_id || ' / current_target_quantity = ' || l_total_target_quantity);

      EXCEPTION
        WHEN no_data_found THEN
          l_total_target_quantity := 0;

          r_group_leader_id := create_group_leader(
              i_sap_contract_id,
              i_sap_material_id,
              i_supply_plant_cd,
              i_target_quantity_uom_cd,
              i_vat_percentage,
              i_transport_costs_included,
              l_total_target_quantity);
    END;

    -- the group leader keeps track of the total quantity across all contract items
    IF (i_sap_contract_item_id = -1) THEN
      -- we are dealing with a new SAP CONTRACT ITEM (group member)
      l_old_target_quantity := 0;
    ELSE
      -- for an existing SAP_CONTRACT_ITEM (group member), we are performing an update
      -- of course we only retrieve the SAP_CONTRACT_ITEM's current target_quantity to
      -- subtract if it is already assigned to the Group Leader. otherwise, it could be 
      -- an update to an existing SAP_CONTRACT_ITEM which hasn't been assigned yet 
      -- (that is, the CONTRACT ITEM predates our Advance Payment Solution fix).
      BEGIN
        SELECT COALESCE(sapitem.target_quantity, 0)
        INTO l_old_target_quantity
        FROM tlgns_sap_contract_item sapitem
        WHERE sapitem.sap_contract_item_id = i_sap_contract_item_id
        AND sapitem.ap_group_leader_id = r_group_leader_id
        AND marked_for_deletion = 0;
          
        EXCEPTION
          WHEN no_data_found THEN
            l_old_target_quantity := 0;
      END;
    END IF;
    
    LOGON_LOG.DEBUG('SDN: ap_cre_upd_group_leader', '        l_old_target_quantity = ' || l_old_target_quantity);
    IF i_marked_for_deletion = 1 THEN
      l_new_target_quantity := 0;
    ELSE
      l_new_target_quantity := i_new_target_quantity;
    END IF;
    
    LOGON_LOG.DEBUG('SDN: ap_cre_upd_group_leader', '        l_new_target_quantity = ' || l_new_target_quantity);
    o_delta_target_quantity := l_new_target_quantity - l_old_target_quantity;
    
    RETURN r_group_leader_id;

  END ap_cre_upd_group_leader;


  -----------------------------------------------------------------------------
  -- qr_cre_upd_group_leader
  --
  -- Create or Update the Group Leader for a Quantity Restricted contract
  --
  -- Group leaders are nothing more than a dummy contract item which other
  -- contract items reference and serve as the common point for a set of
  -- contract item members. For performance reasons, they also maintain two
  -- cached values:
  --
  -- Unlike Advance Payment contracts, a Quantity Restricted contract
  -- Group Leader only has 1 member. As such, the only search criteria for
  -- looking up a QR Group Leader is to check whether the current contract
  -- item in question has its AP_GROUP_LEADER_ID field set.
  --
  -- TARGET_QUANTITY    : The sum of all target quantities for all contract members
  -- TOTAL_SENT_QUANTITY: The sum of all delivered quantities for all associated
  --                      ordered items
  --
  -- Input:
  --  i_sap_contract_id         : PK tlgns_sap_contract (Search criteria)
  --  i_sap_material_id         : PK tlgns_sap_material (Search criteria)
  --  i_supply_plant_cd         : data
  --  i_target_quantity_uom_cd  : data
  --  i_vat_percentage          : data
  --  i_transport_costs_included: data
  --  i_sap_contract_item_id    : id of SAP_CONTRACT_ITEM currently being added (group member), -1 if new
  --  i_new_target_quantity     : the target quantity for the new or to-be-updated SAP_CONTRACT_ITEM
  --
  -- NAMING NOTE: The group leader was originally added as part of the Advance Payment
  -- Solution (LRQ 303.0). However it was later extended to include Quantity-Restricted
  -- contracts (LR 002.1) as well, but the naming of the column remained AP_GROUP_LEADER
  -- (Advance Payement Group Leader). When we get a chance, we can clean this up, but for
  -- now, keep in mind that this Group Leader is created for all contract types which are
  -- quantity controlled (see TLGNR_MAP_QTY_RESTR_BS)
  -----------------------------------------------------------------------------
  FUNCTION qr_cre_upd_group_leader(
    i_sap_contract_item_id      IN NUMBER,
    i_sap_contract_id           IN NUMBER,
    i_sap_material_id           IN NUMBER,
    i_supply_plant_cd           IN VARCHAR2,
    i_material_group_1          IN VARCHAR2,
    i_material_group_2          IN VARCHAR2,
    i_material_group_3          IN VARCHAR2,
    i_material_group_4          IN VARCHAR2,
    i_material_group_5          IN VARCHAR2,
    i_item_category             IN VARCHAR2,
    i_target_quantity           IN NUMBER,
    i_target_quantity_uom_cd    IN VARCHAR2,
    i_net_value                 IN NUMBER,
    i_vat_percentage            IN NUMBER,
    i_marked_for_deletion       IN NUMBER,
    i_transport_costs_included  IN NUMBER)
    RETURN NUMBER
  IS
    r_group_leader_id       NUMBER := NULL;
    l_current_target_quantity NUMBER := 0;

    l_input_params_str VARCHAR2(4000) := '        Input params: (i_sap_contract_id = ' || i_sap_contract_id || ', i_sap_material_id = ' || i_sap_material_id || ', i_supply_plant_cd = ' || i_supply_plant_cd || ', i_target_quantity_uom_cd = ' || i_target_quantity_uom_cd || ', i_vat_percentage = ' || i_vat_percentage || ', i_transport_costs_included = ' || i_transport_costs_included || ', i_sap_contract_item_id = ' || i_sap_contract_item_id || ', i_target_quantity = ' || i_target_quantity || ')';
  BEGIN
    LOGON_LOG.INFO('SDN: qr_cre_upd_group_leader', l_input_params_str);

    -- Quantity Restricted contract group leaders only ever have 1 member
    IF (i_sap_contract_item_id = -1) THEN
      r_group_leader_id := create_group_leader(
          i_sap_contract_id,
          i_sap_material_id,
          i_supply_plant_cd,
          i_target_quantity_uom_cd,
          i_vat_percentage,
          i_transport_costs_included,
          i_target_quantity);
    ELSE
      SELECT sapitem.ap_group_leader_id, COALESCE(sapitem.target_quantity, 0)
      INTO r_group_leader_id, l_current_target_quantity
      FROM tlgns_sap_contract_item sapitem
      WHERE sapitem.sap_contract_item_id = i_sap_contract_item_id;

      LOGON_LOG.INFO('SDN: qr_cre_upd_group_leader', '        Found group_leader_id = ' || r_group_leader_id || ' / current_target_quantity = ' || l_current_target_quantity);
    END IF;

    -- since a QR group leader only ever has 1 member, we also update the group member
    -- with the member's fields since they will always match 1 to 1 (with the
    -- obvious exception of those fields which distinguish a group leader from
    -- a normal group member)
    UPDATE tlgns_sap_contract_item
    SET sap_material_id = i_sap_material_id,
      supply_plant_cd = i_supply_plant_cd,
      material_group_1 = i_material_group_1,
      material_group_2 = i_material_group_2,
      material_group_3 = i_material_group_3,
      material_group_4 = i_material_group_4,
      material_group_5 = i_material_group_5,
      item_category = i_item_category,
      target_quantity = i_target_quantity,
      target_quantity_uom_cd = i_target_quantity_uom_cd,
      net_value = i_net_value,
      vat_percentage = i_vat_percentage,
      marked_for_deletion = i_marked_for_deletion,
      transport_cost_included_flag = i_transport_costs_included,
      ctl_mod_uid = 1,
      ctl_mod_ts = systimestamp,
      ctl_tcn = ctl_tcn + 1
    WHERE sap_contract_item_id = r_group_leader_id;

    LOGON_LOG.INFO('SDN: qr_cre_upd_group_leader', '        Updated group_leader_id = ' || r_group_leader_id || ' / target_quantity = ' || i_target_quantity || ' (sap_contract_item_id = ' || i_sap_contract_item_id || ')');

    RETURN r_group_leader_id;

  END qr_cre_upd_group_leader;


  FUNCTION create_group_leader(
    i_sap_contract_id           IN NUMBER,
    i_sap_material_id           IN NUMBER,
    i_supply_plant_cd           IN VARCHAR2,
    i_target_quantity_uom_cd    IN VARCHAR2,
    i_vat_percentage            IN NUMBER,
    i_transport_costs_included  IN NUMBER,
    i_target_quantity           IN NUMBER)
    RETURN NUMBER
  IS
    r_group_leader_id  NUMBER := NULL;
    l_input_params_str VARCHAR2(4000) := '            Input params: (i_sap_contract_id = ' || i_sap_contract_id || ', i_sap_material_id = ' || i_sap_material_id || ', i_supply_plant_cd = ' || i_supply_plant_cd || ', i_target_quantity_uom_cd = ' || i_target_quantity_uom_cd || ', i_vat_percentage = ' || i_vat_percentage || ', i_transport_costs_included = ' || i_transport_costs_included || ', i_target_quantity = ' || i_target_quantity || ')';
    BEGIN
      LOGON_LOG.INFO('SDN: create_group_leader', l_input_params_str);

      SELECT slgns_sap_contract_item.nextval
      INTO r_group_leader_id
      FROM dual;

      INSERT INTO tlgns_sap_contract_item (
        sap_contract_item_id,
        sap_contract_id,
        contract_item_no,
        sap_material_id,
        supply_plant_cd,
        target_quantity,
        target_quantity_uom_cd,
        vat_percentage,
        transport_cost_included_flag,
        is_ap_group_leader,
        ap_group_leader_id,
        total_sent_quantity,
        marked_for_deletion,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (
          r_group_leader_id,
          i_sap_contract_id,
          -1, /* dummy value for item no - does not need to be unique */
          i_sap_material_id,
          i_supply_plant_cd,
          i_target_quantity,
          i_target_quantity_uom_cd,
          i_vat_percentage,
          i_transport_costs_included,
          1,     /* leader flag - always 1 */
          NULL,  /* leaders never reference anyone else, even themselves */
          0,     /* nothing has been sent out yet */
          0,     /* not yet deleted */
          1, systimestamp, 1, systimestamp, 0);

      LOGON_LOG.INFO('SDN: create_group_leader', '            Inserted new group_leader_id = ' || r_group_leader_id);

      RETURN r_group_leader_id;

  END create_group_leader;



-----------------------------------------------------------------------
-----------------------------------------------------------------------
---get partner function. if invalid then return NULL and log as error--
-----------------------------------------------------------------------
-----------------------------------------------------------------------
  FUNCTION get_partner_function(i_partner_function IN VARCHAR2)
  RETURN VARCHAR2 IS
      o_partner_function VARCHAR2(2) := NULL;

  BEGIN

    IF i_partner_function = c_soldto THEN
      o_partner_function := c_soldto;
    ELSIF i_partner_function = c_shipto THEN
      o_partner_function := c_shipto;
    ELSIF i_partner_function = c_billto THEN
      o_partner_function := c_billto;
    ELSIF i_partner_function = c_payer THEN
      o_partner_function := c_payer;
    ELSIF i_partner_function = c_vendor THEN
      o_partner_function := c_vendor;
    ELSE
      LOGON_LOG.ERROR('SDN: get_partner_function', ' Unknown partner function ''' || i_partner_function || '''');
      RETURN NULL;
      
    END IF;
    
    RETURN o_partner_function;

  END;
  

  -------------------------------------------------------------------------------
  -------------------------------------------------------------------------------
  ---Quantity restricted function. returns 1 if contract is quantity restricted--
  -------------------------------------------------------------------------------
  -------------------------------------------------------------------------------
  
  FUNCTION quantity_restricted (
                     i_contr_type in varchar2,
                     i_augru in varchar2
                    )
                    return varchar2 as
	o_final_restricted   number(1,0);

  BEGIN

    select
    case count(quantity_control_flag)
      when 0 then 0
      else 1
    end as qty_restr
    into o_final_restricted
    from tlgnr_map_qty_restr_bs
    where sap_contract_type_cd = i_contr_type
    and (augru = i_augru or (augru is null and i_augru is null))
    and quantity_control_flag = 1
    and ctl_act_cd = 1;

    RETURN o_final_restricted;

  END quantity_restricted;

  
  -------------------------------------------------------------------------------
  -------------------------------------------------------------------------------
  ---Advance payment function. Returns 1 if contract has advance payment---------
  -------------------------------------------------------------------------------
  -------------------------------------------------------------------------------

  FUNCTION is_advance_payment_contract(
    i_contract_type_cd IN VARCHAR2,
    i_augru IN VARCHAR2) RETURN NUMBER
  IS
    r_is_advance_payment_flag NUMBER;
  BEGIN

    BEGIN
      SELECT advance_payment_flag
      INTO r_is_advance_payment_flag
      FROM tlgnr_map_qty_restr_bs bs
      WHERE bs.sap_contract_type_cd=i_contract_type_cd
        AND (augru = i_augru or (augru is null and i_augru is null))
        AND ctl_act_cd = 1;
    EXCEPTION
      WHEN no_data_found THEN
        r_is_advance_payment_flag := 0;
    END;

    LOGON_LOG.DEBUG('SDN: is_advance_payment_contract', '    (contract_type_cd = ' || i_contract_type_cd || ', augru = ' || i_augru || '): ' || r_is_advance_payment_flag);
    RETURN r_is_advance_payment_flag;
  END is_advance_payment_contract;
 
  
  -----------------------------------------------------------------------------------------------------
  -----------------------------------------------------------------------------------------------------
  ---Contract controller number function. Returns the value for tlgns_sap_contract.CONTROLLER_NO-------
  -----------------------------------------------------------------------------------------------------
  -----------------------------------------------------------------------------------------------------

  FUNCTION contract_controller_no (
    i_shipto_id IN VARCHAR2,
    i_contract_hjid IN NUMBER) RETURN VARCHAR2
  AS
    CURSOR cur_sap_contract_controlr IS
      SELECT texts.tdline, 
             identi0.tssprasiso lang_cd
    FROM ulgnsap.sdn_salesdocumentnotification notif
    INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_0 notif0 ON (notif.salesdocumentnotification_sd_0 = notif0.hjid)
    INNER JOIN ulgnsap.sdn_salesdocumentnotificatio_3 notif3 ON (notif0.hjid = notif3.parent_sdn_salesdocumentnoti_0)
    INNER JOIN ulgnsap.sdn_documentheadertextidenti_0 identi0 ON (identi0.hjid = notif3.child_sdn_documentheadertext_1)
    INNER JOIN ulgnsap.sdn_documentheadertextidenti_1 identi1 ON (identi1.parent_sdn_documentheadertex_0 = identi0.hjid)
    INNER JOIN ulgnsap.sdn_documentheadertexts texts ON (texts.hjid = identi1.child_sdn_documentheadertext_0)
    WHERE notif.hjid = i_contract_hjid -- we only need to process the relevant request
        AND identi0.tdid = '0003'
        AND identi0.tssprasiso IS NOT NULL
        AND texts.tdline IS NOT NULL
    ORDER BY texts.hjid ASC; -- we need to find the first document_header_texts node
  
    l_shipto_lang_cd varchar2(2) := NULL;
 
  BEGIN
	  
	LOGON_LOG.DEBUG('SDN:  contract_controller_no', '    params (shipto_id = ' || i_shipto_id || ', contract_hjid = ' || i_contract_hjid || ').');
  
    BEGIN
      -- get the lang code for the given sap_shipto
      SELECT sap_language_cd
      INTO l_shipto_lang_cd
      FROM tlgns_sap_bp
      where sap_bp_id = i_shipto_id
        and bp_is_shipto = 1;
        EXCEPTION
          WHEN no_data_found THEN
            LOGON_LOG.ERROR('SDN:    contract_controller_no', SQLCODE, SQLERRM ,'No language code for shipto sap_bp_id: ' || i_shipto_id );
            RETURN NULL;
    END;
    
    FOR cur_sap_contract_controlr_rec IN cur_sap_contract_controlr
    LOOP
      EXIT WHEN cur_sap_contract_controlr%notfound;
      IF cur_sap_contract_controlr_rec.lang_cd = l_shipto_lang_cd
      THEN
        LOGON_LOG.DEBUG('SDN:    contract_controller_no', '    Returning with tdline: ' || cur_sap_contract_controlr_rec.tdline);
        RETURN cur_sap_contract_controlr_rec.tdline;
      END IF;
    END LOOP;
  
    LOGON_LOG.DEBUG('SDN:    contract_controller_no', '    End of function but no tdline found. Returning with NULL.');
    RETURN NULL;

  END contract_controller_no;


  -----------------------------------------------------------------------------------------------------
  -----------------------------------------------------------------------------------------------------
  ---Parent offer number function. Returns the value for tlgns_sap_contract_item.PARENT_OFFER_NO-------
  -----------------------------------------------------------------------------------------------------
  -----------------------------------------------------------------------------------------------------

  FUNCTION parent_offer_number (i_item_hjid IN NUMBER) RETURN VARCHAR2
  AS
    l_tdline varchar2(70) := NULL;
 
  BEGIN
	  
	LOGON_LOG.DEBUG('SDN:  parent_offer_number', '    params (item_hjid = ' || i_item_hjid || ').');
  
    BEGIN
      SELECT texts.tdline
      INTO l_tdline
	  FROM ulgnsap.SDN_DOCUMENTITEMGENERALDATA itemgendata
	  INNER JOIN ulgnsap.SDN_DOCUMENTITEMGENERALDATA__2 itemgendata2 ON (itemgendata.hjid = itemgendata2.parent_sdn_documentitemgener_0)
	  INNER JOIN ulgnsap.sdn_documentitemtextidentifi_0 identi0 ON (identi0.hjid = itemgendata2.CHILD_SDN_DOCUMENTITEMTEXTID_0 )
	  INNER JOIN ulgnsap.sdn_documentitemtextidentifi_1 identi1 ON (identi1.parent_sdn_documentitemtexti_0 = identi0.hjid)
	  INNER JOIN ulgnsap.sdn_documentitemtexts texts ON (texts.hjid = identi1.child_sdn_documentitemtexts__0)
	  WHERE itemgendata.hjid = i_item_hjid -- we only need to process the relevant request
	        AND identi0.tdid = 'Z010'
	        AND identi0.tssprasiso = 'EN'
	        AND texts.tdline IS NOT NULL;
        EXCEPTION
          WHEN no_data_found THEN
            LOGON_LOG.ERROR('SDN:    parent_offer_number', SQLCODE, SQLERRM ,'No parent offer number for i_item_hjid: ' || i_item_hjid );
            RETURN NULL;
    END;
   
  
    LOGON_LOG.DEBUG('SDN:    parent_offer_number', '    Returning with tdline: ' || l_tdline);
    RETURN l_tdline;

  END parent_offer_number;
  
--------------------------------------------------------------------------------
--
-- The task of this procedure is to level out excess quantities in
-- tlgnc_order_item_contract_item. In order to achieve this, dummy entries
-- without order item reference with a negative delivered quantity are intro-
-- duced for the contract item with the excess, while the equivalent positive
-- delivered quantity is again introduced as a dummy for the new/modified
-- contract item.
--
--------------------------------------------------------------------------------
  PROCEDURE takeover_excess_quantity(
    i_sap_contract_item_id    IN NUMBER,
    i_ap_group_leader_id      IN NUMBER,
    i_delta_target_quantity   IN NUMBER)
  IS

  CURSOR excesses IS
    select
      ci2.sap_contract_item_id as sap_contract_item_id,
      total_delivered_quantity - COALESCE(ci2.target_quantity, 0) quantity
    from (select
            ci.sap_contract_item_id,
            sum(COALESCE(oici.delivered_quantity, 0)) as total_delivered_quantity
          from
            tlgns_sap_contract_item ci
          left join tlgnc_order_item_contract_item oici
                on (oici.sap_contract_item_id = ci.sap_contract_item_id and COALESCE(oici.deleted_flag, 0)=0)
          where
            ci.ap_group_leader_id is not null
          group by ci.sap_contract_item_id
          ) totals
    join tlgns_sap_contract_item ci2 on (ci2.sap_contract_item_id = totals.sap_contract_item_id)
    where ci2.sap_contract_item_id != i_sap_contract_item_id
      and ci2.ap_group_leader_id = i_ap_group_leader_id
      and total_delivered_quantity - COALESCE(ci2.target_quantity, 0) > 0;
  
  l_used_delta NUMBER := 0;
  l_correction NUMBER := NULL;

  BEGIN

    LOGON_LOG.INFO('SDN: takeover_excess_quantity', '    Start checking for excesses.');

    FOR excess IN excesses
    LOOP

      LOGON_LOG.INFO('SDN: takeover_excess_quantity', '     Found excess: ' || excess.sap_contract_item_id || ' / ' || excess.quantity);

      IF i_delta_target_quantity > l_used_delta THEN
        
        LOGON_LOG.DEBUG('SDN: takeover_excess_quantity', '     Delta: ' || i_delta_target_quantity || ', used: ' || l_used_delta);
        l_correction := 0;
        IF i_delta_target_quantity - l_used_delta >= excess.quantity THEN
          l_correction := excess.quantity;
        ELSE
          l_correction := i_delta_target_quantity - l_used_delta;
        END IF;
        
        l_used_delta := l_used_delta + l_correction;
        
        LOGON_LOG.DEBUG('SDN: takeover_excess_quantity', '     Delta after correction: ' || l_used_delta);
        insert
        into tlgnc_order_item_contract_item (order_item_contract_item_id,
          order_item_id,
          sap_contract_item_id,
          sent_order_item_no,
          delivered_quantity,
          deleted_flag,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        values (slgnc_orderitemcontractitem.nextval,
          null,
          excess.sap_contract_item_id,
          0,
          -l_correction,
          0,
          1, systimestamp, 1, systimestamp, 0);
        
      END IF;

    END LOOP;
    
    IF l_used_delta > 0 THEN
      LOGON_LOG.DEBUG('SDN: takeover_excess_quantity', '     l_used_delta is > 0: ' || l_used_delta);
      insert
      into tlgnc_order_item_contract_item (order_item_contract_item_id,
        order_item_id,
        sap_contract_item_id,
        sent_order_item_no,
        delivered_quantity,
        deleted_flag,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
      values (slgnc_orderitemcontractitem.nextval,
        null,
        i_sap_contract_item_id,
        0,
        l_used_delta,
        0,
        1, systimestamp, 1, systimestamp, 0);
    
    END IF;

    LOGON_LOG.INFO('SDN: takeover_excess_quantity', '    Finished checking for excesses.');

  END takeover_excess_quantity;

  --------------------------------------------------------------------------------
--
-- This will update target_quantity and marked_for_deletion attributes in all group 
-- leader (GL) contract items withing the contract.
-- If all belonging contract items in the group is marked for deletion, then GL's 
-- flag is also marked_for_deletion; otherwise it isn't.
-- target_quantity: sum of target_quantities in the group, which are not marked for
-- deletion.
--
--------------------------------------------------------------------------------
  PROCEDURE update_gl_qty_and_del_flag(i_sap_contract_id IN NUMBER) IS
    CURSOR cur_group_leader_id(i_sap_contract_id IN NUMBER) IS
      SELECT ap_group_leader_id 
      FROM tlgns_sap_contract_item
      WHERE ap_group_leader_id is not null
      AND sap_contract_id = i_sap_contract_id
      GROUP BY ap_group_leader_id;
    
    l_total_target_quantity           NUMBER := 0;
    l_nr_of_active_contract_items NUMBER := NULL;
    l_gl_marked_for_deletion      NUMBER := NULL;
    
  BEGIN
      FOR cur_group_leader_id_record IN cur_group_leader_id(i_sap_contract_id)
      LOOP
        LOGON_LOG.DEBUG('SDN: update_gl_qty_and_del_flag', '    (sap_contract_id = ' || 
          i_sap_contract_id || ', ap_group_leader_id = ' || cur_group_leader_id_record.ap_group_leader_id || ')');
        
        SELECT
          COALESCE(
            SUM(
              CASE WHEN marked_for_deletion = 0 
              THEN target_quantity END), 
            0),
          COUNT(
            CASE WHEN marked_for_deletion = 0 
            THEN 1 END)
        INTO l_total_target_quantity, l_nr_of_active_contract_items
        FROM tlgns_sap_contract_item
        WHERE ap_group_leader_id = cur_group_leader_id_record.ap_group_leader_id;

        IF l_nr_of_active_contract_items = 0 
          THEN l_gl_marked_for_deletion := 1;
          ELSE l_gl_marked_for_deletion := 0;
        END IF;
    
        LOGON_LOG.DEBUG('SDN: update_gl_qty_and_del_flag', '    (sap_contract_id = ' ||
          i_sap_contract_id || ', l_nr_of_active_contract_items = ' || l_nr_of_active_contract_items || 
          ', l_gl_marked_for_deletion = ' || l_gl_marked_for_deletion);
          
        UPDATE tlgns_sap_contract_item
        SET
          target_quantity = l_total_target_quantity,
          marked_for_deletion = l_gl_marked_for_deletion,
          ctl_mod_uid = 1,
          ctl_mod_ts = systimestamp,
          ctl_tcn = ctl_tcn + 1
        WHERE sap_contract_item_id = cur_group_leader_id_record.ap_group_leader_id;
        
        LOGON_LOG.DEBUG('SDN: update_gl_qty_and_del_flag', '        Updated ap_group_leader_id = ' || 
          cur_group_leader_id_record.ap_group_leader_id || ' / AP total_target_quantity = ' || l_total_target_quantity || 
          '/ marked_for_deletion = ' || l_gl_marked_for_deletion || ')');
      END LOOP;

  END update_gl_qty_and_del_flag;
  
END sapimport_sdn;
/

SHOW ERRORS;

--------------------------------------------------------------------------------
-- END OF THE SCRIPT
--------------------------------------------------------------------------------

