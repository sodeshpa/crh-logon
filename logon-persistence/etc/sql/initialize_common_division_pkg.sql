--------------------------------------------------------------------------------
--
-- PROJECT      : $Project: LOGON$
--
-- AUTHOR       : $Author: $
-- CREATION DATE: $Date$
-- DESCRIPTION  : Create package for initializing common divison related things [LOGON-6658]
--                Must be called after every update of tlgnr_common_sarea_map_bs
--------------------------------------------------------------------------------


CREATE OR REPLACE PACKAGE INITIALIZE_COMMON_DIVISION AS

  PROCEDURE main(i_min_loglevel IN VARCHAR2);
  PROCEDURE delete_all_pfs_referring(i_pf_sap_bp_sarea_id IN NUMBER);

END INITIALIZE_COMMON_DIVISION;
/

SHOW ERRORS;

CREATE OR REPLACE PACKAGE BODY INITIALIZE_COMMON_DIVISION AS

  ----------------------------------------------------------------
  ----------------------------------------------------------------
  ------------------INITIALIZE_COMMON_DIVISION--------------------
  ----------------------------------------------------------------
  ----------------------------------------------------------------

  --------------------------------------------
  --------------------------------------------
  -----tlgns_sap_bp---------------------------
  --------------------------------------------
  --------------------------------------------
  PROCEDURE main(i_min_loglevel IN VARCHAR2) AS

    -- query to identify records to be deleted
    CURSOR cur_delete_bp_sarea IS
    select dep_bp_sarea.sap_bp_sarea_id, map.composite_id
    from TLGNS_SAP_BP_SAREA dep_bp_sarea
    inner join TLGNS_SAP_BP_SAREA bp_sarea on (dep_bp_sarea.ref_sap_bp_sarea_id = bp_sarea.sap_bp_sarea_id)
    left outer join VLGNC_COMMON_SAREA_MAP map on (dep_bp_sarea.sarea_id = map.sarea_id and bp_sarea.sarea_id = map.ref_sarea_id);
    
    CURSOR cur_insert_bp_sarea IS
    select bp_sarea.sap_bp_sarea_id, dep_bp_sarea.sap_bp_sarea_id dep_sap_bp_sarea_id, map.sarea_id dep_sarea_id
    from TLGNS_SAP_BP_SAREA bp_sarea
    inner join VLGNC_COMMON_SAREA_MAP map on (bp_sarea.sarea_id = map.ref_sarea_id)
    left outer join TLGNS_SAP_BP_SAREA dep_bp_sarea on (bp_sarea.sap_bp_id = dep_bp_sarea.sap_bp_id and map.sarea_id = dep_bp_sarea.sarea_id);
    
    CURSOR cur_explicit_bp_sarea IS
    select bp_sarea.sap_bp_sarea_id, bp_sarea.sap_bp_id, bp_sarea.sarea_id
    from TLGNS_SAP_BP_SAREA bp_sarea
    where bp_sarea.ref_sap_bp_sarea_id is null;

    l_del_total NUMBER;
    l_ins_total NUMBER;
    l_upd_total NUMBER;

  BEGIN

    -- set logging parameters
    LOGON_LOG.g_min_loglevel := i_min_loglevel;

    LOGON_LOG.INFO('ICD: main', 'START deleting dependent bp sales areas which not backed by a mapping anymore ...');
    
    l_del_total := 0;

    FOR delete_bp_sarea IN cur_delete_bp_sarea
    LOOP
    
      IF delete_bp_sarea.composite_id is null THEN
      
        sapimport_bpn.delete_all_pfs(delete_bp_sarea.sap_bp_sarea_id);
        delete_all_pfs_referring(delete_bp_sarea.sap_bp_sarea_id);
        
        delete from TLGNS_SAP_BP_SAREA
        where sap_bp_sarea_id = delete_bp_sarea.sap_bp_sarea_id;
        
        l_del_total := l_del_total + 1;
        
      END IF;

    END LOOP; -- cur_bp_record

    LOGON_LOG.INFO('ICD: main', 'FINISH! Deleted ' || l_del_total || ' records from ''tlgns_sap_bp_sarea'' table!');
    
    LOGON_LOG.INFO('ICD: main', 'START Inserting / updating dependent bp sales areas ...');

    l_ins_total := 0;
    l_upd_total := 0;
    
    FOR insert_bp_sarea IN cur_insert_bp_sarea
    LOOP
    
      sapimport_bpn.sync_dependent_bp_sarea(insert_bp_sarea.sap_bp_sarea_id, insert_bp_sarea.dep_sap_bp_sarea_id, insert_bp_sarea.dep_sarea_id, l_ins_total, l_upd_total);
    
    END LOOP; -- cur_insert_bp_sarea

    LOGON_LOG.INFO('ICD: main', 'FINISH! Inserted ' || l_ins_total || ' and updated ' || l_upd_total || ' records from ''tlgns_sap_bp_sarea'' table!');
    
    FOR explicit_bp_sarea IN cur_explicit_bp_sarea
    LOOP
    
      sapimport_bpn.sync_dependent_pfs(explicit_bp_sarea.sap_bp_sarea_id, explicit_bp_sarea.sap_bp_id, explicit_bp_sarea.sarea_id);
    
    END LOOP; -- cur_explicit_bp_sarea
    
    COMMIT;

  END main;

  ---------------------------------------------
  ---------------------------------------------
  -----delete all partner functions------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE delete_all_pfs_referring(i_pf_sap_bp_sarea_id IN NUMBER) AS
  
  BEGIN
  
    delete from tlgns_sap_bp_pf_soldto
    where soldto_sap_bp_sarea_id = i_pf_sap_bp_sarea_id;
  
    delete from tlgns_sap_bp_pf_shipto
    where shipto_sap_bp_sarea_id = i_pf_sap_bp_sarea_id;
  
    delete from tlgns_sap_bp_pf_billto
    where billto_sap_bp_sarea_id = i_pf_sap_bp_sarea_id;
  
    delete from tlgns_sap_bp_pf_payer
    where payer_sap_bp_sarea_id = i_pf_sap_bp_sarea_id;
  
  END delete_all_pfs_referring;

END INITIALIZE_COMMON_DIVISION;
/
 
SHOW ERRORS;
--------------------------------------------------------------------------------
-- END OF THE SCRIPT
--------------------------------------------------------------------------------
