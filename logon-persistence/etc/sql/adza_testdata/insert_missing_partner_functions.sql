prompt '---------- insert_missing_partner_functions.sql ----------'

-- This script inserts self-referencing partner functions in all sales areas / purchasing
-- organisations for business partners / vendors which have the corresponding flag bp_is /
-- vn_is set to 1. This is required to avoid that the flag is set to 0 in the first run of
-- the corresponding SAP import.
-- Note that this is not a long-term solution. Corresponding partner functions should be
-- inserted manually along with the insertion of the business partners / vendors.

prompt Inserting self-referecing shipto partner functions into TLGNS_SAP_BP_PF_SHIPTO
INSERT INTO TLGNS_SAP_BP_PF_SHIPTO (SAP_BP_SAREA_ID,SHIPTO_SAP_BP_SAREA_ID,SAREA_ID)
(
  SELECT s.sap_bp_sarea_id, s.sap_bp_sarea_id, s.sarea_id
  FROM TLGNS_SAP_BP_SAREA s
  INNER JOIN TLGNS_SAP_BP b ON (s.sap_bp_id = b.sap_bp_id)
  WHERE b.bp_is_shipto = 1
    AND b.sap_bp_id < 0
    AND (s.sap_bp_sarea_id, s.sap_bp_sarea_id) NOT IN (
      SELECT p.sap_bp_sarea_id, p.shipto_sap_bp_sarea_id
      FROM TLGNS_SAP_BP_PF_SHIPTO p
    )
);

prompt Inserting self-referecing soldto partner functions into TLGNS_SAP_BP_PF_SOLDTO
INSERT INTO TLGNS_SAP_BP_PF_SOLDTO (SAP_BP_SAREA_ID,SOLDTO_SAP_BP_SAREA_ID,SAREA_ID)
(
  SELECT s.sap_bp_sarea_id, s.sap_bp_sarea_id, s.sarea_id
  FROM TLGNS_SAP_BP_SAREA s
  INNER JOIN TLGNS_SAP_BP b ON (s.sap_bp_id = b.sap_bp_id)
  WHERE b.bp_is_soldto = 1
    AND b.sap_bp_id < 0
    AND (s.sap_bp_sarea_id, s.sap_bp_sarea_id) NOT IN (
      SELECT p.sap_bp_sarea_id, p.soldto_sap_bp_sarea_id
      FROM TLGNS_SAP_BP_PF_SOLDTO p
    )
);

prompt Inserting self-referecing billto partner functions into TLGNS_SAP_BP_PF_BILLTO
INSERT INTO TLGNS_SAP_BP_PF_BILLTO (SAP_BP_SAREA_ID,BILLTO_SAP_BP_SAREA_ID,SAREA_ID)
(
  SELECT s.sap_bp_sarea_id, s.sap_bp_sarea_id, s.sarea_id
  FROM TLGNS_SAP_BP_SAREA s
  INNER JOIN TLGNS_SAP_BP b ON (s.sap_bp_id = b.sap_bp_id)
  WHERE b.bp_is_billto = 1
    AND b.sap_bp_id < 0
    AND (s.sap_bp_sarea_id, s.sap_bp_sarea_id) NOT IN (
      SELECT p.sap_bp_sarea_id, p.billto_sap_bp_sarea_id
      FROM TLGNS_SAP_BP_PF_BILLTO p
    )
);

prompt Inserting self-referecing payer partner functions into TLGNS_SAP_BP_PF_PAYER
INSERT INTO TLGNS_SAP_BP_PF_PAYER (SAP_BP_SAREA_ID,PAYER_SAP_BP_SAREA_ID,SAREA_ID)
(
  SELECT s.sap_bp_sarea_id, s.sap_bp_sarea_id, s.sarea_id
  FROM TLGNS_SAP_BP_SAREA s
  INNER JOIN TLGNS_SAP_BP b ON (s.sap_bp_id = b.sap_bp_id)
  WHERE b.bp_is_payer = 1
    AND b.sap_bp_id < 0
    AND (s.sap_bp_sarea_id, s.sap_bp_sarea_id) NOT IN (
      SELECT p.sap_bp_sarea_id, p.payer_sap_bp_sarea_id
      FROM TLGNS_SAP_BP_PF_PAYER p
    )
);

prompt Inserting self-referecing commercial carrier partner functions into TLGNS_SAP_BP_PF_COMM_CARRIER
INSERT INTO TLGNS_SAP_VN_PF_COMM_CARRIER (SAP_VENDOR_PORG_ID,COMM_SAP_VENDOR_PORG_ID,PURCHASINGORG_CD)
(
  SELECT porg.sap_vendor_porg_id, porg.sap_vendor_porg_id, porg.purchasingorg_cd
  FROM TLGNS_SAP_VENDOR_PORG porg
  INNER JOIN TLGNS_SAP_VENDOR v ON (porg.sap_vendor_id = v.sap_vendor_id)
  WHERE v.vn_is_comm_carrier = 1
    AND v.sap_vendor_id < 0
    AND (porg.sap_vendor_porg_id, porg.sap_vendor_porg_id) NOT IN (
      SELECT pf.sap_vendor_porg_id, pf.comm_sap_vendor_porg_id
      FROM TLGNS_SAP_VN_PF_COMM_CARRIER pf
    )
);

prompt Inserting self-referecing executing carrier partner functions into TLGNS_SAP_BP_PF_EXEC_CARRIER
INSERT INTO TLGNS_SAP_VN_PF_EXEC_CARRIER (SAP_VENDOR_PORG_ID,EXEC_SAP_VENDOR_PORG_ID,PURCHASINGORG_CD)
(
  SELECT porg.sap_vendor_porg_id, porg.sap_vendor_porg_id, porg.purchasingorg_cd
  FROM TLGNS_SAP_VENDOR_PORG porg
  INNER JOIN TLGNS_SAP_VENDOR v ON (porg.sap_vendor_id = v.sap_vendor_id)
  WHERE v.vn_is_exec_carrier = 1
    AND v.sap_vendor_id < 0
    AND (porg.sap_vendor_porg_id, porg.sap_vendor_porg_id) NOT IN (
      SELECT pf.sap_vendor_porg_id, pf.exec_sap_vendor_porg_id
      FROM TLGNS_SAP_VN_PF_EXEC_CARRIER pf
    )
);
