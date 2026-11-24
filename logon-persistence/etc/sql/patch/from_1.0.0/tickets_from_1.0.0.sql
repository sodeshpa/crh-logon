PROMPT ************************************************************************
PROMPT *** BEGIN CRHLOGON-53: Only one replacement SAP contract is supported by the automatic maintenance
PROMPT ************************************************************************

ALTER TABLE tlgnc_shipto_sarea_rest_maint
  DROP CONSTRAINT uq_sap_contract_item_id DROP INDEX;
ALTER TABLE tlgnc_shipto_sarea_rest_maint
  DROP CONSTRAINT uq_match_sap_contract_item_id DROP INDEX;

ALTER TABLE tlgnc_shipto_sarea_rest_maint
  ADD CONSTRAINT uq_match_sap_contract_item_id UNIQUE (sap_contract_item_id, match_sap_contract_item_id)
  USING INDEX TABLESPACE index_lgn_a;

PROMPT ************************************************************************
PROMPT *** BEGIN CRHLOGON-53: Only one replacement SAP contract is supported by the automatic maintenance
PROMPT ************************************************************************