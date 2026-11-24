--------------------------------------------------------------------------------
--
-- PROJECT      : $Project: LOGON$
--
-- AUTHOR       : $Author: nemethze $
-- CREATION DATE: $Date$
-- DESCRIPTION  : Create MN loading package.
--
--------------------------------------------------------------------------------


CREATE OR REPLACE PACKAGE SAPIMPORT_MN AS

  -- phase 1 (general data, plant data, etc.)

  PROCEDURE MAIN(i_min_loglevel IN VARCHAR2, i_days_try_import IN NUMBER);
  FUNCTION  get_converted_uom_cd(i_uom_cd IN VARCHAR2) RETURN VARCHAR2;

  PROCEDURE load_tlgns_sap_material_plant(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER);
  FUNCTION  get_plant_cd(i_plant_cd IN VARCHAR2) RETURN VARCHAR2;
  PROCEDURE load_tlgnc_mat_plant(i_sap_material_id IN NUMBER, i_plant_cd IN VARCHAR2);
  PROCEDURE load_tlgnc_mat_plant_tx(i_mat_plant_id IN NUMBER);

  PROCEDURE upd_tlgns_sap_material_plant(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER);

  PROCEDURE load_tlgns_sap_material_sarea(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER);

  PROCEDURE load_tlgns_sap_material_alt(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER, i_full_update IN BOOLEAN);

  PROCEDURE load_tlgns_sap_material_desc(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER, i_full_update IN BOOLEAN);
  PROCEDURE upd_default_description(i_lang_cd IN VARCHAR2, i_description IN VARCHAR2, o_default_description IN OUT VARCHAR2, o_default_lang_cd IN OUT VARCHAR2);
  PROCEDURE insert_missing_descriptions(i_sap_material_id NUMBER, i_default_description VARCHAR2, i_default_lang_cd VARCHAR2, o_ins_total IN OUT NUMBER);

  PROCEDURE mark_missing_sarea_as_deleted(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER);
  PROCEDURE mark_missing_plant_as_deleted(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER);


END SAPIMPORT_MN;
/

SHOW ERRORS;

CREATE OR REPLACE PACKAGE BODY SAPIMPORT_MN AS

  ----------------------------------------------------------------
  ----------------------------------------------------------------
  --------------------BP-IMPORT-----------------------------------
  ----------------------------------------------------------------
  ----------------------------------------------------------------

  c_import_name             constant VARCHAR(30) := 'sapimport_mn';
  
  ----------------------------------------------------------------
  -- Statuses
  ----------------------------------------------------------------

  c_gen_initial             constant NUMBER :=  0; -- The notification has not been processed yet.
  c_gen_skipped             constant NUMBER :=  1; -- The notification has been skipped because a newer full update is available
  c_gen_failed              constant NUMBER :=  2; -- The notification could not be imported because an error occurred but it will be processed again during the next run.
  c_gen_blocked             constant NUMBER :=  3; -- The notification is blocked by an older notification that had failed.
  c_gen_blocked_final       constant NUMBER :=  8; -- The notification is blocked by an older notification that had failed and processing has been abandoned.
  c_gen_failed_final        constant NUMBER :=  9; -- The notification could not be imported because an error occurred and procesing has been abandoned.
  c_imported_to_logon       constant NUMBER := 20; -- The notification has been imported completely.

  c_function_code_delete    constant VARCHAR(4) := '003';
  c_function_code_change    constant VARCHAR(4) := '004';
  c_function_code_replace   constant VARCHAR(4) := '005';
  c_function_code_original  constant VARCHAR(4) := '009';
  c_function_code_resend    constant VARCHAR(4) := '018';
  c_function_code_wait      constant VARCHAR(4) := '023';

  c_service                 constant VARCHAR2(4) := 'S005';
  c_pallet                  constant VARCHAR2(4) := 'S003';
  c_bulk_powder             constant VARCHAR2(4) := 'S001';
  c_bulk_dumped             constant VARCHAR2(4) := 'S006';
  c_bag                     constant VARCHAR2(4) := 'S002';
  
  c_clinker_type_I			constant VARCHAR2(6) := '010801';
  c_clinker_type_II			constant VARCHAR2(6) := '010802';
  c_other_clinker			constant VARCHAR2(6) := '010899';
  
  e_invalid_material_package_cd EXCEPTION;
  e_no_sales_areas              EXCEPTION;
  e_invalid_matpkg_map_cond     EXCEPTION;

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

    l_sap_material_id NUMBER;
    l_material_package_cd VARCHAR2(4);
    l_rule_result NUMBER;
    l_base_uom_cd VARCHAR2(3);
    
    l_material_is_agg NUMBER := 0;
    l_material_is_cem NUMBER := 0;
    l_material_is_lf NUMBER := 0;
    
    l_material_marked_for_deletion NUMBER := 0;
    
    -- cursor to validate all MATPKG_MAP rules
	CURSOR rule_validation IS
	  select 
	    rule.MATPKG_MAP_RULE_ID,
	    rule.MATPKG_MAP_RULE_NO,
	    cond.MATPKG_MAP_COND_ID,
	    cond.field_name,
	    table_names
	  from 
	    TLGNS_MATPKG_MAP_RULE rule
	  join
	    TLGNS_MATPKG_MAP_COND cond on (rule.MATPKG_MAP_RULE_ID = cond.MATPKG_MAP_RULE_ID)
	  left outer join
	    (
	      select
	        cols.column_name,
	        count(table_name) as table_num,
	        LISTAGG(cols.table_name, ', ') WITHIN GROUP (ORDER BY cols.table_name) as table_names    
	      from 
	        ALL_TAB_COLUMNS cols
	      where (cols.owner = 'ULGNSAP' and cols.table_name in ('MN_MATERIALGENERALDATA', 'MN_MATERIALSALESDATA'))
	      group by cols.column_name
	    ) cols_name on (cols_name.column_name = cond.field_name)
	  where ((table_num is null) or (table_num > 1));

    -- main query to select not imported records
    CURSOR cur_material IS
    SELECT req.hjid mat_hjid,
      gen.msgfn msgfn,
      LTRIM(gen.matnr, '0') sap_id,
      gen.matnr sap_ifc_id,
      gen.meins base_uom_cd,
      DECODE(gen.lvorm, 'X', 1, 0) marked_for_deletion,
      gen.spart division_cd,
      gen.normt industry_standard_desc,
      gen.mtart mtart,
      gen.prdha prdha,
      gen.matkl material_group_desc,
      req.sapimpimporteddatelement,
      gen.hjid gen_hjid
    FROM ULGNSAP.MN_MATERIALNOTIFICATION req
    INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel ON (req.hjid = rel.parent_mn_materialnotificati_0)
    INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA gen ON (rel.child_mn_materialgeneraldata_0 = gen.hjid)
    WHERE req.sapimpstatuscd IN (c_gen_initial, c_gen_failed, c_gen_blocked)
    ORDER BY req.sapimpimporteddatelement asc;
    
    CURSOR cur_sarea (v_mat_hjid IN NUMBER) IS
    SELECT sales.mtpos mtpos,
      sales.mvgr2 mvgr2,
      sales.kondm pricing_group,
      sales.hjid sales_hjid
    FROM ULGNSAP.MN_MATERIALNOTIFICATION req
    INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
    INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_6 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
    INNER JOIN ULGNSAP.MN_MATERIALSALESDATA sales ON (rel1.child_mn_materialsalesdata_id = sales.hjid)
    WHERE req.hjid = v_mat_hjid;

    -- cursor of all MATPKG_MAP rules   
	CURSOR cur_rule IS
		SELECT 
		  rule.MATPKG_MAP_RULE_ID,
		  rule.MATPKG_MAP_RULE_NO,
		  rule.MATERIAL_PACKAGE_CD,
		  rule.RULE_COMMENT,
		  conds.conds_sql
		FROM
		  TLGNS_MATPKG_MAP_RULE rule
		join (
		    select
		      cond.MATPKG_MAP_RULE_ID,
		      '(' || 
		        LISTAGG(
		          cols.table_name || '.' || cond.field_name || ' = ''' || cond.field_value || '''',
		          ') AND ('
		        ) WITHIN GROUP (ORDER BY cond.MATPKG_MAP_RULE_ID) 
		      || ')' as conds_sql
		    from 
		      TLGNS_MATPKG_MAP_COND cond 
		    join
		      ALL_TAB_COLUMNS cols ON (cols.owner = 'ULGNSAP' and cols.table_name in ('MN_MATERIALGENERALDATA', 'MN_MATERIALSALESDATA') and cols.column_name = cond.field_name)      
		    GROUP BY 
		      cond.MATPKG_MAP_RULE_ID
		  ) conds on (conds.MATPKG_MAP_RULE_ID = rule.MATPKG_MAP_RULE_ID)
		ORDER BY
		  rule.MATPKG_MAP_RULE_NO asc;
  BEGIN

    g_days_try_import := i_days_try_import;

    -- set the time of the start of the import
    LOGON_LOG.start_import(c_import_name);

    -- set logging parameters
    LOGON_LOG.g_min_loglevel := i_min_loglevel;
	
	LOGON_LOG.DEBUG('MN: load_tlgns_sap_material', ' Validating all MATPKG_MAP rules.');		  
	FOR rule_validation_record IN rule_validation
	LOOP
	  g_err_msg := ' Rule ' || rule_validation_record.MATPKG_MAP_RULE_NO || '(id: ' || rule_validation_record.MATPKG_MAP_RULE_ID || ') is invalid. Misconfigured condition (id: ' || rule_validation_record.MATPKG_MAP_COND_ID || '). ';
	  IF rule_validation_record.table_names IS NULL THEN
	    g_err_msg := g_err_msg || 'The field ''' || rule_validation_record.field_name || ''' does not map to any table.';
	  ELSE
	    g_err_msg := g_err_msg || 'The field ''' || rule_validation_record.field_name || ''' maps to multiple tables (' || rule_validation_record.table_names || ').';
	  END IF;
	  LOGON_LOG.ERROR('MN: load_tlgns_sap_material', g_err_msg);
	  RAISE e_invalid_matpkg_map_cond;
	END LOOP;
	LOGON_LOG.DEBUG('MN: load_tlgns_sap_material', ' All MATPKG_MAP rules are valid.');

    LOGON_LOG.INFO('MN: load_tlgns_sap_material', 'START loading data into ''tlgns_sap_material'' table ...');

    FOR cur_material_record IN cur_material
    LOOP

      LOGON_LOG.DEBUG('MN: load_tlgns_sap_material', ' Checking sap_id (' || cur_material_record.sap_id || ') and hjid (' || cur_material_record.mat_hjid || ') ...');
      
      -- check whether maximum age has already been reached
      l_final := EXTRACT(DAY FROM systimestamp - cur_material_record.sapimpimporteddatelement) > g_days_try_import;

      g_err_msg := NULL;

      -- check whether full or partial update (or invalid)      
      CASE cur_material_record.msgfn
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
            LOGON_LOG.DEBUG('MN: load_tlgns_sap_material', ' SKIP notification (function code ''' || c_function_code_wait || ''')');
            UPDATE ULGNSAP.MN_MATERIALNOTIFICATION
            SET sapimpstatuscd = c_gen_skipped,
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_material_record.mat_hjid;
            
            COMMIT;
            --CONTINUE;
            GOTO next_material_record;
          END;
        ELSE
          BEGIN
            LOGON_LOG.ERROR('MN: load_tlgns_sap_material', ' Invalid function code ''' || cur_material_record.msgfn || ''', setting to failed final.');
            UPDATE ULGNSAP.MN_MATERIALNOTIFICATION
            SET sapimpstatuscd = c_gen_failed_final,
              sapimperrmsg = 'Invalid message function code ''' || cur_material_record.msgfn || '''',
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_material_record.mat_hjid;
            
            COMMIT;
            --CONTINUE;
            GOTO next_material_record;
          END;
      END CASE;
        
      BEGIN
      
        l_sap_material_id := NULL;
        l_material_package_cd := NULL;
        l_rule_result := 0;
        l_base_uom_cd := NULL;
        
        l_material_is_agg := 0;
        l_material_is_cem := 0;
        l_material_is_lf := 0;
        
        l_material_marked_for_deletion := 0;

        -- check whether sap_material_id exists
        BEGIN
  
          SELECT mat.sap_material_id
          INTO l_sap_material_id
          FROM tlgns_sap_material mat
          WHERE mat.sap_material_no = cur_material_record.sap_id;
  
          EXCEPTION
          WHEN no_data_found THEN
            -- bp will be loaded
            l_sap_material_id := -1;
          WHEN too_many_rows THEN
             LOGON_LOG.ERROR('MN: load_tlgns_sap_material', ' More than one material found for sap_id (' || cur_material_record.sap_id || ') and hjid (' || cur_material_record.mat_hjid || ')');
             g_err_msg := 'More than one material found for sap_id ''' || cur_material_record.sap_id || '''';
             RAISE;
          WHEN others THEN
             LOGON_LOG.ERROR('MN: load_tlgns_sap_material', SQLCODE, SQLERRM, ' Unknown Exception in selecting sap_material_id from tlgns_sap_material for sap bp_id (' || cur_material_record.sap_id || ') and hjid (' || cur_material_record.mat_hjid || ')');
             RAISE;
        
        END;
        
        l_insert := l_sap_material_id = -1;
            
        -- check whether to skip this notification
        IF NOT l_full_update THEN
        
          IF l_sap_material_id = -1 THEN
          
            LOGON_LOG.ERROR('MN: load_tlgns_sap_material', ' Partial update and material ''' || cur_material_record.sap_id || ''' does not exist. Setting to failed final.');
          
            UPDATE ULGNSAP.MN_MATERIALNOTIFICATION
            SET sapimpstatuscd = c_gen_failed,
              sapimperrmsg = 'Partial update and material does not exist',
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_material_record.mat_hjid;
            
            COMMIT;
            --CONTINUE;
            GOTO next_material_record;
            
          ELSE
          
            SELECT mat.update_blocked_by_hjid
            INTO l_bb_hjid
            FROM TLGNS_SAP_MATERIAL mat
            WHERE mat.sap_material_id = l_sap_material_id;
            
            IF NOT (l_bb_hjid IS NULL OR l_bb_hjid = cur_material_record.mat_hjid) THEN
            
              LOGON_LOG.ERROR('MN: load_tlgns_sap_material', ' Update of sap_id = ' || cur_material_record.sap_id || ' and hjid = ' || cur_material_record.mat_hjid || ' is blocked by hjid = ' || l_bb_hjid || '.');

              IF l_final THEN
                l_new_state := c_gen_blocked_final;
              ELSE
                l_new_state := c_gen_blocked;
              END IF;

              UPDATE ULGNSAP.MN_MATERIALNOTIFICATION
              SET sapimpstatuscd = l_new_state,
                sapimperrmsg = 'Blocked by ' || l_bb_hjid,
                sapimppropagateddatelement = systimestamp
              WHERE hjid = cur_material_record.mat_hjid;
              
              COMMIT;
              --CONTINUE;
              GOTO next_material_record;

            END IF;
            
          END IF;
          
        END IF;
        
        -- determine material type and material package code
		l_material_package_cd := NULL;

        FOR cur_sarea_record IN cur_sarea(cur_material_record.mat_hjid)
        LOOP
        
          -- determine material type
          IF (cur_material_record.mtart = 'ZAGG') THEN
            l_material_is_agg := 1;
          ELSE
          	l_material_is_agg := 0;
          END IF;
          
          IF (cur_material_record.mtart = 'ZCEM') THEN
            l_material_is_cem := 1;
          ELSE
          	l_material_is_cem := 0;
          END IF;
          
          IF (cur_material_record.mtart = 'ZNST' AND cur_sarea_record.pricing_group IN ('32', '33')) THEN
            l_material_is_lf := 1; 
          ELSE
          	l_material_is_lf := 0;
          END IF;
          
           -- determine material package code
             
          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material', ' Material package code determination starting.');
                  
          FOR cur_rule_record IN cur_rule
          LOOP  
            LOGON_LOG.DEBUG('MN: load_tlgns_sap_material', ' Processing Rule ' || cur_rule_record.MATPKG_MAP_RULE_NO || ' (id: ' || cur_rule_record.MATPKG_MAP_RULE_ID || ', executing SQL with parameters ''' || cur_material_record.mat_hjid || ''', and ''' || cur_rule_record.conds_sql || ''').');


            EXECUTE IMMEDIATE 
              'select count(1) 
              from ULGNSAP.MN_MATERIALNOTIFICATION req 
              inner join ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel ON (req.hjid = rel.parent_mn_materialnotificati_0) 
              inner join ULGNSAP.MN_MATERIALGENERALDATA MN_MATERIALGENERALDATA ON (rel.child_mn_materialgeneraldata_0 = MN_MATERIALGENERALDATA.hjid) 
              inner join ULGNSAP.MN_MATERIALGENERALDATA_MATER_6 rel1 ON (rel.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0) 
              inner join ULGNSAP.MN_MATERIALSALESDATA MN_MATERIALSALESDATA ON (rel1.child_mn_materialsalesdata_id = MN_MATERIALSALESDATA.hjid) 
              where req.HJID = :1 and MN_MATERIALGENERALDATA.HJID = :2 and MN_MATERIALSALESDATA.hjid = :3 AND (' || cur_rule_record.conds_sql || ')'
            INTO l_rule_result
            USING cur_material_record.mat_hjid, cur_material_record.gen_hjid, cur_sarea_record.sales_hjid;
                           
            IF (l_rule_result > 0) THEN
           
              LOGON_LOG.INFO('MN: load_tlgns_sap_material', ' Rule ' || cur_rule_record.MATPKG_MAP_RULE_NO || ' (Result: ''' || l_rule_result || ''', for ''' || cur_rule_record.conds_sql || ''') is true, resultting in package type to ''' || cur_rule_record.MATERIAL_PACKAGE_CD || '''.');    
              l_material_package_cd := cur_rule_record.MATERIAL_PACKAGE_CD;
              EXIT;
             
            END IF;
           
          END LOOP;
			
		END LOOP;
		
		IF l_material_package_cd IS NULL THEN
		  
		  IF l_insert THEN
		    
		    LOGON_LOG.ERROR('MN: load_tlgns_sap_material', ' The material package code cannot be determined because no matching condition has been found for a new material (sap_id = ' || cur_material_record.sap_id || ' and hjid = ' || cur_material_record.mat_hjid || '.');
		    g_err_msg := 'The material package code cannot be determined because no matching condition has been found for a new material';
		    RAISE e_no_sales_areas;
		  
		  ELSE
		  
		    SELECT material_package_cd
		    INTO l_material_package_cd
		    FROM TLGNS_SAP_MATERIAL
		    WHERE sap_material_id = l_sap_material_id;
		  
		  END IF;
		END IF;
			
		LOGON_LOG.DEBUG('MN: load_tlgns_sap_material', ' Material package code determination finished.');
        
        -- Initialize the l_material_marked_for_deletion variable from the material data
        l_material_marked_for_deletion := cur_material_record.marked_for_deletion;
        
        -- We could not determine the type of the material
        IF (l_material_package_cd IS NULL OR (l_material_package_cd != c_service AND l_material_package_cd != c_pallet 
          	AND l_material_is_agg = 0 AND l_material_is_cem = 0 AND l_material_is_lf = 0)) THEN
          	
          	IF l_insert THEN
          		
          		-- This material did not exist before, but we cannot determine the material type --> inactivate it.
          		LOGON_LOG.ERROR('MN: load_tlgns_sap_material', ' Material is not service and not pallet but the material type could not be identified (sap_id = ' || cur_material_record.sap_id || ' and hjid = ' || cur_material_record.mat_hjid || '. Setting it to inactive.');
          		l_material_marked_for_deletion := 1;
          	
        	ELSE
        	
        		SELECT material_is_agg
            	INTO l_material_is_agg
            	FROM TLGNS_SAP_MATERIAL
            	WHERE sap_material_id = l_sap_material_id;
            	
            	SELECT material_is_cem
            	INTO l_material_is_cem
            	FROM TLGNS_SAP_MATERIAL
            	WHERE sap_material_id = l_sap_material_id;
            	
            	SELECT material_is_lf
            	INTO l_material_is_lf
            	FROM TLGNS_SAP_MATERIAL
            	WHERE sap_material_id = l_sap_material_id;
        		
        	END IF;	
          	
        END IF;
        
        l_base_uom_cd := get_converted_uom_cd(cur_material_record.base_uom_cd);
                
        -- insert a new material
        IF l_insert THEN

          SELECT slgns_sap_material.nextval
          INTO l_sap_material_id
          FROM dual;
  
          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material', ' Loading new sap material (sap_id = ' || cur_material_record.sap_id  || ' and sap_material_id = ' || l_sap_material_id || ') ...');
  
          INSERT
          INTO tlgns_sap_material (sap_material_id,
            sap_material_no,
            sap_material_ifc_no,
            marked_for_deletion,
            base_uom_cd,
            division_cd,
            industry_standard_desc,
            material_package_cd,
            material_group_desc,
            material_is_agg, material_is_cem, material_is_lf,
            ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
          VALUES (l_sap_material_id,
            cur_material_record.sap_id,
            cur_material_record.sap_ifc_id,
            l_material_marked_for_deletion,
            l_base_uom_cd,
            cur_material_record.division_cd,
            cur_material_record.industry_standard_desc,
            l_material_package_cd,
            cur_material_record.material_group_desc,
            l_material_is_agg, l_material_is_cem, l_material_is_lf,
            1, systimestamp, 1, systimestamp, 0);
    
        -- update material
        ELSE

          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material', ' Updating sap material (sap_id = ' || cur_material_record.sap_id  || ' and sap_material_id = ' || l_sap_material_id || ' ) ...');

          UPDATE tlgns_sap_material
          SET sap_material_ifc_no = cur_material_record.sap_ifc_id,
            update_blocked_by_hjid = NULL,
            marked_for_deletion = l_material_marked_for_deletion,
            base_uom_cd = l_base_uom_cd,
            material_package_cd = l_material_package_cd,
            division_cd = cur_material_record.division_cd,
            industry_standard_desc = cur_material_record.industry_standard_desc,
            material_group_desc = cur_material_record.material_group_desc,
            material_is_agg = l_material_is_agg,
            material_is_cem = l_material_is_cem,
            material_is_lf = l_material_is_lf,
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_material_no = cur_material_record.sap_id;

        END IF;
  
        -- update other tables
        load_tlgns_sap_material_plant(cur_material_record.mat_hjid, l_sap_material_id);
        upd_tlgns_sap_material_plant (cur_material_record.mat_hjid, l_sap_material_id);
        load_tlgns_sap_material_sarea(cur_material_record.mat_hjid, l_sap_material_id);
        load_tlgns_sap_material_alt  (cur_material_record.mat_hjid, l_sap_material_id, l_full_update);
        load_tlgns_sap_material_desc (cur_material_record.mat_hjid, l_sap_material_id, l_full_update);
        
        -- mark missing sales areas and plants as deleted
        IF l_full_update THEN
          mark_missing_sarea_as_deleted(cur_material_record.mat_hjid, l_sap_material_id);
          mark_missing_plant_as_deleted(cur_material_record.mat_hjid, l_sap_material_id);
        END IF;

        --update controlling attributes
        UPDATE ulgnsap.MN_MATERIALNOTIFICATION
        SET sapimpstatuscd = c_imported_to_logon,
          sapimperrmsg = NULL,
          sapimppropagateddatelement = systimestamp
        WHERE hjid = cur_material_record.mat_hjid;

        COMMIT;
        LOGON_LOG.INFO('MN: load_tlgns_sap_material', ' Sap material (sap_id = ' || cur_material_record.sap_id  || ' and sap_material_id = ' || l_sap_material_id || ') has been loaded/updated.');

        IF l_insert THEN
          l_ins_total := l_ins_total + 1;
        ELSE
          l_upd_total := l_upd_total + 1;
        END IF;

        EXCEPTION

          WHEN others THEN
          BEGIN

            ROLLBACK;
            LOGON_LOG.ERROR('MN: load_tlgns_sap_material', SQLCODE, SQLERRM, ' ROLLBACK on sap material (sap_id = ' || cur_material_record.sap_id  || ', hjid = ' || cur_material_record.mat_hjid || ' and sap_material_id = ' || l_sap_material_id || ').');

            l_sqlerrm := SQLERRM;

            IF l_final THEN
              l_new_state := c_gen_failed_final;
            ELSE
              l_new_state := c_gen_failed;
            END IF;

            -- update controlling attributes
            UPDATE ulgnsap.MN_MATERIALNOTIFICATION
            SET sapimpstatuscd = l_new_state,
              sapimperrmsg = COALESCE(g_err_msg, l_sqlerrm),
              sapimppropagateddatelement = systimestamp
            WHERE hjid = cur_material_record.mat_hjid;
            
            -- update 'blocked by' field
            UPDATE TLGNS_SAP_MATERIAL mat
            SET mat.update_blocked_by_hjid = cur_material_record.mat_hjid
            WHERE mat.sap_material_no = cur_material_record.sap_id;

            COMMIT;

            IF l_final THEN
              LOGON_LOG.ERROR('MN: load_tlgns_sap_material', ' Sap material (sap_id = ' || cur_material_record.sap_id  || ' and sap_material_id = ' || l_sap_material_id || ') has been abandoned. Days for successful import = ' || g_days_try_import || '.');
            END IF;

          END;
      
      END;
      
      --update older entries
      IF l_full_update THEN
      
        UPDATE ULGNSAP.MN_MATERIALNOTIFICATION req
        SET req.sapimpstatuscd = c_gen_skipped,
          req.sapimppropagateddatelement = SYSTIMESTAMP
        WHERE req.sapimpstatuscd IN (c_gen_blocked, c_gen_failed)
          AND req.sapimpimporteddatelement < cur_material_record.sapimpimporteddatelement
          AND req.hjid IN (
            SELECT req_inner.hjid
            FROM ulgnsap.MN_MATERIALNOTIFICATION req_inner
            INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel ON (req_inner.hjid = rel.parent_mn_materialnotificati_0)
            INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA gen ON (rel.child_mn_materialgeneraldata_0 = gen.hjid)
            WHERE LTRIM(gen.matnr, '0') = cur_material_record.sap_id);

        COMMIT;
        
      END IF;
      
      <<next_material_record>>
      NULL;

    END LOOP; -- cur_material_record

    LOGON_LOG.INFO('MN: load_tlgns_sap_material', 'FINISH! Updated ' || l_upd_total || ' and inserted ' || l_ins_total || ' records into ''tlgns_sap_material'' table!');

    -- set the run number and the time of the end of import
    LOGON_LOG.FINISH_IMPORT(c_import_name);

  END main;

  --------------------------------------------
  --------------------------------------------
  -----tlgns_sap_material_plant---------------
  --------------------------------------------
  --------------------------------------------
  FUNCTION get_converted_uom_cd(i_uom_cd IN VARCHAR2) RETURN VARCHAR2 IS
  
    o_uom_cd VARCHAR2(3) := NULL;
    
  BEGIN
  
    SELECT map.uom_cd
    INTO o_uom_cd
    FROM TLGNR_MAP_SAP_UOM_BS map
    WHERE map.map_sap_uom_cd = i_uom_cd;
    
    RETURN o_uom_cd;
    
    EXCEPTION
    WHEN NO_DATA_FOUND THEN
      LOGON_LOG.ERROR('MN: get_converted_uom_cd', 'No UoM mapping found in TLGNR_MAP_SAP_UOM_BS for uom_cd ''' || i_uom_cd || '''.');
      g_err_msg := 'No UoM mapping found in TLGNR_MAP_SAP_UOM_BS for uom_cd ''' || i_uom_cd || '''';
      RAISE;

  END get_converted_uom_cd;

  --------------------------------------------
  --------------------------------------------
  -----tlgns_sap_material_plant---------------
  --------------------------------------------
  --------------------------------------------
  PROCEDURE load_tlgns_sap_material_plant(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER) IS

    CURSOR cur_plant IS
    SELECT plant.werks plant_cd,
      DECODE(plant.lvorm, 'X', '1', '0') marked_for_deletion,
      plant.stawn nhm_code
    FROM ULGNSAP.MN_MATERIALNOTIFICATION req
    INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
    INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_2 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
    INNER JOIN ULGNSAP.MN_MATERIALPLANTDATA plant ON (rel1.child_mn_materialplantdata_id = plant.hjid)
    WHERE req.hjid = i_mat_hjid;
    
    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;
    
    l_sap_material_plant_id NUMBER := NULL;
    l_plant_cd VARCHAR2(4) := NULL;
    l_production_plant NUMBER := NULL;
    
  BEGIN
  
    LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_plant', ' Loading data into TLGNS_SAP_MATERIAL_PLANT table (hjid = ' || i_mat_hjid || ') ...');
    
    FOR cur_plant_record IN cur_plant
    LOOP

      l_sap_material_plant_id := NULL;
      l_plant_cd := get_plant_cd(cur_plant_record.plant_cd);
      
      LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_plant', '  Checking plant_cd = ' || l_plant_cd || ' ...');

      -- check whether sap material plant exists
      BEGIN

        SELECT sap_material_plant_id
        INTO l_sap_material_plant_id
        FROM TLGNS_SAP_MATERIAL_PLANT
        WHERE sap_material_id = i_sap_material_id
         AND plant_cd = l_plant_cd;

        EXCEPTION
        WHEN NO_DATA_FOUND THEN
          -- new sap material plant will be loaded
          l_sap_material_plant_id := -1;
        WHEN OTHERS THEN
           LOGON_LOG.ERROR('MN: load_tlgns_sap_material_plant', SQLCODE, SQLERRM, '  Unknown exception in selecting sap_material_plant_id from tlgns_sap_material_plant for sap_material_id = ' || i_sap_material_id || ' and  plant_cd = ' ||  l_plant_cd || '.');
           RAISE;

      END;

      -- insert a new sap material plant
      IF (l_sap_material_plant_id = -1) THEN
      
        SELECT slgns_sap_material_plant.nextval
        INTO l_sap_material_plant_id
        FROM dual;

        LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_plant', '  Inserting a new sap_material_plant (sap_material_id = ' || i_sap_material_id || ', plant_cd = ' || l_plant_cd || ' and hjid = ' || i_mat_hjid || ') ...');

        INSERT
        INTO TLGNS_SAP_MATERIAL_PLANT (sap_material_plant_id,
          sap_material_id,
          plant_cd,
          marked_for_deletion,
          val_price_control_indicator,
          val_standard_price,
          val_moving_avg_price,
          val_future_price,
          val_future_valid_from_dat,
          nhm_code,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (l_sap_material_plant_id,
          i_sap_material_id,
          l_plant_cd,
          cur_plant_record.marked_for_deletion,
          NULL,
          NULL,
          NULL,
          NULL,
          NULL,
          cur_plant_record.nhm_code,
          1, systimestamp, 1, systimestamp, 0);

        LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_plant', '  Sap material plant successfully inserted.');
        l_ins_total := l_ins_total + 1;
        
        SELECT production_plant
        INTO l_production_plant
        FROM TLGNR_PLANT_BS
        WHERE plant_cd = l_plant_cd;
        
        IF l_production_plant = 1 THEN
        
          load_tlgnc_mat_plant(i_sap_material_id, l_plant_cd);
          
        END IF;

      -- update sap material plant
      ELSE
      
        LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_plant', '  Updating sap_material_plant (sap_material_plant_id = ' || l_sap_material_plant_id || ', sap_material_id = ' || i_sap_material_id|| ', plant_cd = ' ||  l_plant_cd || ' and hjid = ' || i_mat_hjid || ') ...');
  
        UPDATE TLGNS_SAP_MATERIAL_PLANT
        SET marked_for_deletion = cur_plant_record.marked_for_deletion,
          nhm_code = cur_plant_record.nhm_code,
          ctl_mod_uid = 1,
          ctl_mod_ts = systimestamp,
          ctl_tcn = ctl_tcn + 1
        WHERE sap_material_plant_id = l_sap_material_plant_id;
  
        LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_plant', '  Sap material plant successfully updated.');
        l_upd_total := l_upd_total + 1;
          
      END IF;
      
      <<next_plant_record>>
      NULL;

    END LOOP;
    
    LOGON_LOG.INFO('MN: load_tlgns_sap_material_plant', ' Inserted ' || l_ins_total || ' and updated ' || l_upd_total || ' records in tlgns_sap_material_plant table.');

  END load_tlgns_sap_material_plant;
  
  --------------------------------------------
  --------------------------------------------
  -----get plant_cd---------------------------
  --------------------------------------------
  --------------------------------------------
  FUNCTION get_plant_cd(i_plant_cd IN VARCHAR2) RETURN VARCHAR2 IS

    o_plant_cd VARCHAR2(4) := NULL;
    l_plant_cd VARCHAR2(4) := NULL;

  BEGIN
	    
		l_plant_cd := i_plant_cd;

--***************************************************************************************************************************************		
--* Business:                                                                                                                           *
--*   Plant 25A1 and 25A3 are both in Salzgitter, where NGE has 3 ovens to burn "slag" from a near-by located steel-mill. The outcome   *
--*   of the oven is fine-granular slag which can be used as an ingredient in some cements. The outcome is stored on two seperate       *
--*   places, because the ovens are not all owned by just Crh, but one is owned as part of a joint-venture with a competitor of      *
--*   Crh. I.e. the material is the same, but it is owned by somebody else.                                                          *
--*     25A1 is "Crh-only", and is used to deliver to a set of Crh ship-tos.                                                      *
--*     25A3 is the plant code of the Joint-venture, and is used for only a few ship-tos (but it could be the same ship-tos).           *
--*   There are only 2 materials in use for these ovens.                                                                                *
--*   LOGON can treat them as one plant, identically to how WeWaS only knows 1 plant today.                                             *
--*   The material + transport conditions are still distinct, even after Logon maps 25A3 -> 25A1:                                       *
--*     Materials: The same material does only exist on 25A1 OR 25A3.                                                                   *
--*     Transport: 25A1 has only sales org DE01, 25A3 only DE50.                                                                        *
--*   The only consequence: For a LOGON order of plant 25A1 you do not DIRECTLY see if it was from that plant, but you would have to    *
--*   check the sales area in addition for example (=> may only be relevant in reporting, but it's possible to find out).               *
--*                                                                                                                                     *
--* Technical:                                                                                                                          *
--*   The mapping (25A1, 25A3) => (25A1) is a workaround because we stick with the 1:1 mapping of WeWaS plant : SAP Plant               *
--*   If there would be more cases like this, a possible change of DB+Weighinsystem-code would be:                                      *
--*     - remove the column tlgnr_plant_wewas_bs.plant_cd                                                                               *
--*     - add a mapping table plant : plant_wewas                                                                                       *
--*     - change all application logic which checks for "plant exist in plant_wewas_cd" by some other criteria                          *
--*     - change the weighing sytem code in "getTruckLoadOrderList(shippingpointAltNo)" to return orders from SEVERAL shippingpoints!   *
--***************************************************************************************************************************************
		
    IF (i_plant_cd = '25A3') THEN
      l_plant_cd := '25A1';
      LOGON_LOG.DEBUG('MN: get_plant_cd', '  Mapping ' || i_plant_cd || ' to ' || l_plant_cd);
    END IF;
    
    SELECT bs.plant_cd
    INTO o_plant_cd
    FROM tlgnr_plant_bs bs
    WHERE bs.plant_cd = l_plant_cd;

    RETURN o_plant_cd;

    EXCEPTION
    WHEN no_data_found THEN
        LOGON_LOG.ERROR('MN: get_plant_cd', '  plant_cd ''' || l_plant_cd || ''' not found.');
        g_err_msg := 'plant_cd ''' || l_plant_cd || ''' not found.';
        RAISE;

  END get_plant_cd;

  --------------------------------------------
  --------------------------------------------
  -----load tlgnc_mat_plant------------------- 
  --------------------------------------------
  --------------------------------------------
  PROCEDURE load_tlgnc_mat_plant(i_sap_material_id IN NUMBER, i_plant_cd IN VARCHAR2) IS
    
    l_mat_plant_id NUMBER := NULL;
    
  BEGIN

    LOGON_LOG.DEBUG('MN: load_tlgnc_mat_plant', '  Checking sap_material_id = ' || i_sap_material_id || ' and plant_cd = ' || i_plant_cd || ') ...');
    
    --check whether an entry in tlgnc_mat_plant exists
    BEGIN
      
      SELECT mat_plant_id
      INTO l_mat_plant_id
      FROM TLGNC_MAT_PLANT
      WHERE sap_material_id = i_sap_material_id
        AND plant_cd = i_plant_cd;
      
    EXCEPTION
      WHEN no_data_found THEN
        -- new tlgnc_mat_plant entry will be created
        l_mat_plant_id := -1;
      WHEN others THEN
        LOGON_LOG.ERROR('MN: load_tlgnc_mat_plant', SQLCODE, SQLERRM, '  Unknown Exception in selecting mat_plant_id from tlgnc_mat_plant for sap_material_id = ' || i_sap_material_id || ' and plant_cd = ' || i_plant_cd || '.');
        RAISE;
      
    END;
    
    -- insert a new tlgnc_mat_plant entry
    IF l_mat_plant_id = -1 THEN
      
      BEGIN
      
        SELECT slgnc_mat_plant.nextval
        INTO l_mat_plant_id
        FROM dual;
        
        LOGON_LOG.DEBUG('MN: load_tlgnc_mat_plant', '  Loading new mat_plant (mat_plant_id = ' || l_mat_plant_id || ' ) ...');
        
        INSERT INTO TLGNC_MAT_PLANT (
          mat_plant_id,
          sap_material_id,
          plant_cd,
          prio_over_sap_mat_norm_flag,
          norm_desc,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (
          l_mat_plant_id,
          i_sap_material_id,
          i_plant_cd,
          0,
          'Maintain data!',
          1, systimestamp, 1, systimestamp, 0);
        
        LOGON_LOG.DEBUG('MN: load_tlgnc_mat_plant', '  Material plant successfully inserted.');
        
        load_tlgnc_mat_plant_tx(l_mat_plant_id);
        
      END;
    
    END IF;
    
  END load_tlgnc_mat_plant;
  

  -------------------------------------
  -------------------------------------
  -------load tlgnc_mat_plant_tx-------
  -------------------------------------
  -------------------------------------
  PROCEDURE load_tlgnc_mat_plant_tx(i_mat_plant_id IN NUMBER) IS
    
    l_lang_exists NUMBER := NULL;
    l_ins_total NUMBER := 0;

    CURSOR cur_lang IS
    SELECT lang_cd
    FROM TLGNR_LANG_BS;
    
  BEGIN
      
    LOGON_LOG.DEBUG('MN: load_tlgnc_mat_plant_tx', '  Creating language dependent data for new tlgnc_mat_plant if missing (mat_plant_id = ' || i_mat_plant_id || ').');

    FOR cur_lang_record IN cur_lang
    LOOP

      l_lang_exists := NULL;

      BEGIN
	  	
        SELECT ROWNUM
        INTO l_lang_exists
        FROM TLGNC_MAT_PLANT_TX
        WHERE mat_plant_id = i_mat_plant_id
          AND lang_cd = cur_lang_record.lang_cd;
      
        EXCEPTION
        WHEN no_data_found THEN
          -- new tlgnc_mat_plant_tx entry will be created
          l_lang_exists := -1;
        WHEN others THEN
          LOGON_LOG.ERROR('MN: load_tlgnc_mat_plant', SQLCODE, SQLERRM,
          	'Unknown exception in selecting mat_plant_id from tlgnc_mat_plant_tx for i_mat_plant_id = '
            || i_mat_plant_id || ' and lang_cd = ' || cur_lang_record.lang_cd);
          RAISE;
      
      END;
      
      IF l_lang_exists = -1 THEN

        LOGON_LOG.DEBUG('MN: load_tlgnc_mat_plant_tx', '   Inserting new tlgnc_mat_plant_tx (mat_plant_id = ' || i_mat_plant_id || ' lang_cd = ' || cur_lang_record.lang_cd || ') ...');
          
        INSERT
        INTO tlgnc_mat_plant_tx (mat_plant_id,
          lang_cd,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (i_mat_plant_id,
          cur_lang_record.lang_cd,
          1, systimestamp, 1, systimestamp, 0);
            
        l_ins_total := l_ins_total + 1;
        
      END IF;
            
    END LOOP;

    LOGON_LOG.INFO('MN: load_tlgnc_mat_plant_tx', '  Inserted ' || l_ins_total || ' entries into tlgnc_mat_plant_tx for mat_plant_id ''' || i_mat_plant_id || '''.');
    
  END load_tlgnc_mat_plant_tx;

  --------------------------------------------
  --------------------------------------------
  -----update tlgns_sap_material_plant-------- 
  -----with data from valuation area----------
  --------------------------------------------
  --------------------------------------------
  PROCEDURE upd_tlgns_sap_material_plant(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER) IS

    CURSOR cur_val IS
    SELECT val.bwkey plant_cd,
      val.vprsv val_price_control_indicator,
      val.stprs val_standard_price,
      val.verpr val_moving_avg_price,
      val.zkprs val_future_price,
      val.zkdat val_future_valid_from_dat
    FROM ULGNSAP.MN_MATERIALNOTIFICATION req
    INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
    INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_4 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
    INNER JOIN ULGNSAP.MN_MATERIALVALUATION val ON (rel1.child_mn_materialvaluation_id = val.hjid)
    WHERE req.hjid = i_mat_hjid;

    l_plant_cd VARCHAR2(4) := NULL;

  BEGIN
  
    LOGON_LOG.DEBUG('MN: upd_tlgns_sap_material_plant', ' Updating data in tlgns_sap_material_plant table (hjid = ' || i_mat_hjid || ') ...');

    FOR cur_val_record IN cur_val
    LOOP
    
      l_plant_cd := get_plant_cd(cur_val_record.plant_cd);
      
      LOGON_LOG.DEBUG('MN: upd_tlgns_sap_material_plant', '  Updating sap_material_plant (sap_material_id = ' || i_sap_material_id || ' and plant_cd = ' || l_plant_cd || ') ...');

      -- Plant data controls insertion and deletion. In material valuation only updates!
      UPDATE TLGNS_SAP_MATERIAL_PLANT
      SET val_price_control_indicator = cur_val_record.val_price_control_indicator,
        val_standard_price = cur_val_record.val_standard_price,
        val_moving_avg_price = cur_val_record.val_moving_avg_price,
        val_future_price = cur_val_record.val_future_price,
        val_future_valid_from_dat = NVL2(DECODE(cur_val_record.val_future_valid_from_dat, '00000000', NULL, cur_val_record.val_future_valid_from_dat), TO_DATE(DECODE(cur_val_record.val_future_valid_from_dat, '00000000', NULL, cur_val_record.val_future_valid_from_dat), 'YYYYMMDD'), NULL),
        ctl_mod_uid = 1,
        ctl_mod_ts = systimestamp,
        ctl_tcn = ctl_tcn + 1
      WHERE sap_material_id = i_sap_material_id
        AND plant_cd = l_plant_cd;
          
    END LOOP;

    LOGON_LOG.INFO('MN: upd_tlgns_sap_material_plant', ' tlgns_sap_material_plant table has been updated.');

  END upd_tlgns_sap_material_plant;

  --------------------------------------------
  --------------------------------------------
  -----load tlgns_sap_material_sarea---------- 
  --------------------------------------------
  --------------------------------------------
  PROCEDURE load_tlgns_sap_material_sarea(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER) IS

    CURSOR cur_sarea IS
    SELECT sales.vkorg sorg_cd,
      sales.vrkme sales_uom_cd,
      DECODE(sales.lvorm, 'X', '1', '0') marked_for_deletion,
      sales.vtweg distribution_channel,
      sales.kondm pricing_group,
      sales.mvgr4 material_group4
    FROM ULGNSAP.MN_MATERIALNOTIFICATION req
    INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
    INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_6 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
    INNER JOIN ULGNSAP.MN_MATERIALSALESDATA sales ON (rel1.child_mn_materialsalesdata_id = sales.hjid)
    WHERE req.hjid = i_mat_hjid;

    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;
    
    l_sap_material_sarea_id NUMBER := NULL;
    l_is_generic_flag NUMBER := NULL;
    l_sales_uom_cd VARCHAR2(3) := NULL;

  BEGIN
  
    LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_sarea',
      ' Loading data into TLGNS_SAP_MATERIAL_SAREA table (hjid = ' || i_mat_hjid || ') ...');

    FOR cur_sarea_record IN cur_sarea
    LOOP

      l_sap_material_sarea_id := NULL;
      l_is_generic_flag := NULL;
      l_sales_uom_cd := NULL;

      --check whether material sarea data already exists for this l_sap_material_sarea_id
      BEGIN

        SELECT sap_material_sarea_id
        INTO l_sap_material_sarea_id
        FROM tlgns_sap_material_sarea
        WHERE sap_material_id = i_sap_material_id
          AND sorg_cd = cur_sarea_record.sorg_cd
          AND distribution_channel = cur_sarea_record.distribution_channel;

        EXCEPTION
        WHEN no_data_found THEN
          --new material sarea will be loaded
          l_sap_material_sarea_id := -1;
        WHEN others THEN
          LOGON_LOG.ERROR('MN: load_tlgns_sap_material_sarea', SQLCODE, SQLERRM,
            '  Unknown exception while selecting sap_material_sarea_id from TLGNS_SAP_MATERIAL_SAREA for sap_material_id = ' || i_sap_material_id ||
            ', sorg_cd = ' || cur_sarea_record.sorg_cd ||
            ', distribution_channel = ' || cur_sarea_record.distribution_channel ||
            ' and hjid = ' || i_mat_hjid || '.');
          RAISE;

      END;

      BEGIN

        IF (cur_sarea_record.material_group4 = '1' OR cur_sarea_record.material_group4 = '2') THEN
            l_is_generic_flag := 1;
        ELSE
            l_is_generic_flag := 0;
        END IF;
  
        -- convert UoM
        IF (cur_sarea_record.sales_uom_cd IS NOT NULL AND LENGTH(cur_sarea_record.sales_uom_cd) > 0) THEN
          l_sales_uom_cd := get_converted_uom_cd(cur_sarea_record.sales_uom_cd);
        END IF;
  
        -- insert a new material sarea
        IF (l_sap_material_sarea_id = -1) THEN
  
          SELECT slgns_sap_material_sarea.nextval
          INTO l_sap_material_sarea_id
          FROM dual;
  
          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_sarea',
            '  Inserting a new sap_material_sarea (sap_material_id = ' || i_sap_material_id
            || ', sorg_cd = ' || cur_sarea_record.sorg_cd
            || ', distribution_channel = ' || cur_sarea_record.distribution_channel
            || ' and hjid = ' || i_mat_hjid || ') ...');
  
          INSERT
          INTO tlgns_sap_material_sarea (sap_material_sarea_id,
            sap_material_id,
            sorg_cd,
            distribution_channel,
            marked_for_deletion,
            sales_uom_cd,
            pricing_group,
            material_group4,
            is_generic_flag,
            ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
          VALUES (l_sap_material_sarea_id,
            i_sap_material_id,
            cur_sarea_record.sorg_cd,
            cur_sarea_record.distribution_channel,
            cur_sarea_record.marked_for_deletion,
            l_sales_uom_cd,
            cur_sarea_record.pricing_group,
            cur_sarea_record.material_group4,
            l_is_generic_flag,
            1, systimestamp, 1, systimestamp, 0);
  
          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_sarea',
            '  Sap_material_sarea (sap_material_id = ' || i_sap_material_id
            || ', sorg_cd = ' || cur_sarea_record.sorg_cd
            || ', distribution_channel = ' || cur_sarea_record.distribution_channel
            || ' and hjid = ' || i_mat_hjid || ') was successfully inserted.');
  
          l_ins_total := l_ins_total + 1;
  
        -- update material sarea
        ELSE
  
          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_sarea',
              '  Updating sap_material_sarea (sap_material_id = ' || i_sap_material_id
              || ', sorg_cd = ' || cur_sarea_record.sorg_cd
              || ', distribution_channel = ' || cur_sarea_record.distribution_channel
              || ' and hjid = ' || i_mat_hjid || ') ...');

          UPDATE tlgns_sap_material_sarea
          SET marked_for_deletion = cur_sarea_record.marked_for_deletion,
              sales_uom_cd = l_sales_uom_cd,
              pricing_group = cur_sarea_record.pricing_group,
              material_group4 = cur_sarea_record.material_group4,
              is_generic_flag = l_is_generic_flag,
              ctl_mod_uid = 1,
              ctl_mod_ts = systimestamp,
              ctl_tcn = ctl_tcn + 1
          WHERE sap_material_sarea_id = l_sap_material_sarea_id;

          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_sarea',
              '  Sap_material_sarea  (sap_material_id = ' || i_sap_material_id
              || ', sorg_cd = ' || cur_sarea_record.sorg_cd
              || ', distribution_channel = ' || cur_sarea_record.distribution_channel
              || ' and hjid = ' || i_mat_hjid || ') was successfully updated.');

          l_upd_total := l_upd_total + 1;
              
        END IF;
        
      END;
      
      <<next_sarea_record>>
      NULL;

    END LOOP;

    LOGON_LOG.INFO('MN: load_tlgns_sap_material_sarea',
      ' Inserted ' || l_ins_total || ' and updated ' || l_upd_total || ' records from TLGNS_SAP_MATERIAL_SAREA table.');

  END load_tlgns_sap_material_sarea;

  --------------------------------------------
  --------------------------------------------
  -----load tlgns_sap_material_alt------------ 
  --------------------------------------------
  --------------------------------------------
  PROCEDURE load_tlgns_sap_material_alt(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER, i_full_update IN BOOLEAN) IS

    CURSOR cur_alt IS
    SELECT alt.msgfn msgfn,
      alt.meinh uom_cd,
      alt.umrez numerator_for_conversion,
      alt.umren denominator_for_conversion
    FROM ULGNSAP.MN_MATERIALNOTIFICATION req
    INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
    INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_3 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
    INNER JOIN ULGNSAP.MN_MATERIALUNITMEASURE alt ON (rel1.child_mn_materialunitmeasure_0 = alt.hjid)
    WHERE req.hjid = i_mat_hjid;

    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;
    l_del_total NUMBER := 0;
    
    l_uom_cd VARCHAR2(3) := NULL;
    l_sap_material_alt_uom_id NUMBER := NULL;
    
    e_invalid_uom_cd EXCEPTION;
    
  BEGIN
  
    LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_alt',
      ' Loading data into TLGNS_SAP_MATERIAL_ALT_UOM table (hjid = ' || i_mat_hjid || ') ...');

    FOR cur_alt_record IN cur_alt
    LOOP
    
      l_uom_cd := NULL;
      l_sap_material_alt_uom_id := NULL;

      l_uom_cd := get_converted_uom_cd(cur_alt_record.uom_cd);

      --check whether alternative UoM exists
      BEGIN

        SELECT altuom.sap_material_alt_uom_id
        INTO l_sap_material_alt_uom_id
        FROM tlgns_sap_material_alt_uom altuom
        WHERE altuom.sap_material_id = i_sap_material_id
          AND altuom.uom_cd = l_uom_cd;

        EXCEPTION
        WHEN no_data_found THEN
          --material will be loaded
          l_sap_material_alt_uom_id := -1;
        WHEN others THEN
           LOGON_LOG.ERROR('MN: load_tlgns_sap_material_alt', SQLCODE, SQLERRM, 'Unknown exception while selecting sap_material_alt_uom_id from tlgns_sap_material_alt_uom for uom_cd = ' || l_uom_cd || ' and hjid = ' || i_mat_hjid || '.');
           RAISE;

      END;

        BEGIN

          -- insert a new sap material alternative UoM
          IF (l_sap_material_alt_uom_id = -1 AND cur_alt_record.msgfn != c_function_code_delete) THEN

              SELECT slgns_sap_material_alt_uom.nextval
              INTO l_sap_material_alt_uom_id
              FROM dual;

              LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_alt', '  Inserting new sap_material_alt (sap_material_id = ' || i_sap_material_id || ', uom_cd = ' || l_uom_cd || ' and hjid = ' || i_mat_hjid || ') ...');

              INSERT
              INTO tlgns_sap_material_alt_uom (sap_material_alt_uom_id,
                sap_material_id,
                uom_cd,
                numerator_for_conversion,
                denominator_for_conversion,
                ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
              VALUES (l_sap_material_alt_uom_id,
                i_sap_material_id,
                l_uom_cd,
                cur_alt_record.numerator_for_conversion,
                cur_alt_record.denominator_for_conversion,
                1, systimestamp, 1, systimestamp, 0);

              LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_alt', '  Alternative UoM (sap_material_id = ' || i_sap_material_id || ', uom_cd = ' || l_uom_cd || ' and hjid = ' || i_mat_hjid || ') successfully inserted.');
              l_ins_total := l_ins_total + 1;

            -- update or delete material
            ELSIF (l_sap_material_alt_uom_id != -1) THEN
            
              IF (cur_alt_record.msgfn = c_function_code_delete) THEN
              
                LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_alt', '  Deleting sap_material_alt (sap_material_id = ' || i_sap_material_id || ', uom_cd = ' || l_uom_cd || ' and hjid = ' || i_mat_hjid || ') ...');
                
                DELETE FROM tlgns_sap_material_alt_uom alt
                WHERE alt.sap_material_alt_uom_id = l_sap_material_alt_uom_id;
                
                LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_alt', '  Deleted sap_material_alt (sap_material_id = ' || i_sap_material_id || ', uom_cd = ' || l_uom_cd || ' and hjid = ' || i_mat_hjid || ').');
                l_del_total := l_del_total + 1;
                
              ELSE
              
                LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_alt', '  Updating sap_material_alt (sap_material_id = ' || i_sap_material_id || ', uom_cd = ' || l_uom_cd || ' and hjid = ' || i_mat_hjid || ') ...');
                
                UPDATE tlgns_sap_material_alt_uom altuom
                SET altuom.numerator_for_conversion = cur_alt_record.numerator_for_conversion,
                  altuom.denominator_for_conversion = cur_alt_record.denominator_for_conversion,
                  ctl_mod_uid = 1,
                  ctl_mod_ts = systimestamp,
                  ctl_tcn = ctl_tcn + 1
                WHERE sap_material_alt_uom_id = l_sap_material_alt_uom_id;

                LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_alt', '  Alternative UoM (sap_material_id = ' || i_sap_material_id || ', uom_cd = ' || l_uom_cd || ' and hjid = ' || i_mat_hjid || ') successfully updated.');
                l_upd_total := l_upd_total + 1;
                
              END IF;
            END IF;
        END;

    END LOOP;
    
    IF i_full_update THEN

      LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_alt', '  Deleting from sap_material_alt_uom (sap_material_id = ' || i_sap_material_id || ' and hjid = ' || i_mat_hjid || ') ...');
    
      DELETE FROM tlgns_sap_material_alt_uom altuom
      WHERE altuom.sap_material_id = i_sap_material_id
        AND altuom.uom_cd NOT IN (
          SELECT (
            SELECT map.uom_cd
            FROM tlgnr_map_sap_uom_bs map
            WHERE map.map_sap_uom_cd = alt.meinh) uom_cd
          FROM ULGNSAP.MN_MATERIALNOTIFICATION req
          INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
          INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_3 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
          INNER JOIN ULGNSAP.MN_MATERIALUNITMEASURE alt ON (rel1.child_mn_materialunitmeasure_0 = alt.hjid)
          WHERE req.hjid = i_mat_hjid AND alt.msgfn != c_function_code_delete);
          
      l_del_total := SQL%ROWCOUNT;
      
    END IF;

    LOGON_LOG.INFO('MN: load_tlgns_sap_material_alt',
      ' Inserted ' || l_ins_total || ', updated ' || l_upd_total || ' and deleted ' || l_del_total || ' records in tlgns_sap_material_alt table.');

  END load_tlgns_sap_material_alt;

  --------------------------------------------
  --------------------------------------------
  -----load tlgns_sap_material_desc----------- 
  --------------------------------------------
  --------------------------------------------
  PROCEDURE load_tlgns_sap_material_desc(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER, i_full_update IN BOOLEAN) IS

    CURSOR cur_desc IS
    SELECT tex.sprasiso lang_cd,
      tex.maktx txt
    FROM ULGNSAP.MN_MATERIALNOTIFICATION req
    INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
    INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_1 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
    INNER JOIN ULGNSAP.MN_MATERIALSHORTTEXTS tex ON (rel1.child_mn_materialshorttexts__0 = tex.hjid)
    WHERE req.hjid = i_mat_hjid;

    l_ins_total NUMBER := 0;
    l_upd_total NUMBER := 0;
    l_del_total NUMBER := 0;
    
    l_sap_material_desc NUMBER := NULL;
    l_lang_cd VARCHAR2(3) := NULL;
    l_default_desc VARCHAR2(40) := NULL;
    l_default_lang_cd VARCHAR2(2) := NULL;

  BEGIN

    LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_desc',
      ' Loading data into tlgns_sap_material_description table (hjid = ' || i_mat_hjid || ') ...');

    FOR cur_desc_record IN cur_desc
    LOOP

      --check whether language exists
      BEGIN

        SELECT lang_cd
        INTO l_lang_cd
        FROM tlgnr_lang_bs
        WHERE lang_cd = LOWER(DECODE(cur_desc_record.lang_cd, 'Z1', 'de', cur_desc_record.lang_cd));

        EXCEPTION
        WHEN others THEN
           LOGON_LOG.INFO('MN: load_tlgns_sap_material_desc', '  Ignoring language ''' || cur_desc_record.lang_cd  || ''' for hjid = ' || i_mat_hjid);
           GOTO next_desc_record;

      END;

      upd_default_description(l_lang_cd, cur_desc_record.txt, l_default_desc, l_default_lang_cd);

      l_sap_material_desc := NULL;

      --check whether sap material description exists
      BEGIN

        SELECT ROWNUM
        INTO l_sap_material_desc
        FROM tlgns_sap_material_description des
        WHERE des.sap_material_id = i_sap_material_id
          AND des.lang_cd = l_lang_cd;

        EXCEPTION
        WHEN no_data_found THEN
          --material will be loaded
          l_sap_material_desc := -1;
        WHEN others THEN
          LOGON_LOG.ERROR('MN: load_tlgns_sap_material_desc', SQLCODE, SQLERRM,
            '  Unknown Exception selecting from tlgns_sap_material_description for lang_cd = ' || cur_desc_record.lang_cd || ' and hjid = ' || i_mat_hjid);
          RAISE;

      END;

      -- insert a new sap_material_plant
      IF l_sap_material_desc = -1 THEN

          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_desc',
            '  Inserting new sap_material_description (sap_material_id = ' || i_sap_material_id || ', lang_cd = ' || l_lang_cd || ' and hjid = ' || i_mat_hjid || ') ...');

          INSERT
          INTO tlgns_sap_material_description (sap_material_id,
            lang_cd,
            txt,
            ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
          VALUES (i_sap_material_id,
            l_lang_cd,
            cur_desc_record.txt,
            1, systimestamp, 1, systimestamp, 0);

          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_desc',
            '  Sap_material_description (sap_material_id = ' || i_sap_material_id || ', lang_cd = ' || l_lang_cd || ' and hjid = ' || i_mat_hjid || ') successfully inserted.');
          l_ins_total := l_ins_total + 1;

      -- update material
      ELSE

          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_desc',
            '  Updating sap_material_description (sap_material_id = ' || i_sap_material_id || ', lang_cd = ' || l_lang_cd || ' and hjid = ' || i_mat_hjid || ') ...');

          UPDATE tlgns_sap_material_description
          SET txt = cur_desc_record.txt,
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_material_id = i_sap_material_id
            AND lang_cd = l_lang_cd;

          LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_desc',
            '  Sap_material_description (sap_material_id = ' || i_sap_material_id || ', lang_cd = ' || l_lang_cd || ' and hjid = ' || i_mat_hjid || ') successfully updated.');
          l_upd_total := l_upd_total + 1;

      END IF;

      <<next_desc_record>>
      NULL;

    END LOOP;
    
    IF i_full_update THEN

      LOGON_LOG.DEBUG('MN: load_tlgns_sap_material_desc',
        ' Deleting from sap_material_description (sap_material_id = ' || i_sap_material_id || ' and hjid = ' || i_mat_hjid || ') ...');
    
      DELETE FROM tlgns_sap_material_description des
      WHERE des.sap_material_id = i_sap_material_id
      AND des.lang_cd NOT IN (
        SELECT LOWER(DECODE(tex.sprasiso, 'Z1', 'de', tex.sprasiso)) lang_cd
        FROM ULGNSAP.MN_MATERIALNOTIFICATION req
        INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
        INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_1 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
        INNER JOIN ULGNSAP.MN_MATERIALSHORTTEXTS tex ON (rel1.child_mn_materialshorttexts__0 = tex.hjid)
        WHERE req.hjid = i_mat_hjid);
         
      l_del_total := SQL%ROWCOUNT;
      
    END IF;
         
    insert_missing_descriptions(i_sap_material_id, l_default_desc, l_default_lang_cd, l_ins_total);

    LOGON_LOG.INFO('MN: load_tlgns_sap_material_desc',
      ' Inserted ' || l_ins_total || ', updated ' || l_upd_total || ' and deleted ' || l_del_total || ' records in tlgns_sap_material_description table.');

  END load_tlgns_sap_material_desc;

  --------------------------------------------
  --------------------------------------------
  -----Overwrites the parameters o_default_description and o_default_lang_cd in case the input language has higher preference
  -----The value is going to be used to insert the missing material descriptions in other languages in case they are not sent by SAP
  --------------------------------------------
  --------------------------------------------
  PROCEDURE upd_default_description(i_lang_cd IN VARCHAR2, i_description IN VARCHAR2, o_default_description IN OUT VARCHAR2, o_default_lang_cd IN OUT VARCHAR2) AS
  BEGIN	    

    IF (i_lang_cd = 'de') THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);
        o_default_description := i_description;
        o_default_lang_cd := 'de';

    ELSIF (i_lang_cd = 'en' AND (o_default_lang_cd is NULL OR o_default_lang_cd != 'de')) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);	                	
        o_default_description := i_description;
        o_default_lang_cd := 'en';

    ELSIF (i_lang_cd = 'fr' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);	                	
        o_default_description := i_description;
        o_default_lang_cd := 'fr';

    ELSIF (i_lang_cd = 'it' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);	                	
        o_default_description := i_description;
        o_default_lang_cd := 'it';
        
    ELSIF (i_lang_cd = 'es' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);	                	
        o_default_description := i_description;
        o_default_lang_cd := 'es';
    
	ELSIF (i_lang_cd = 'cs' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);	                	
        o_default_description := i_description;
        o_default_lang_cd := 'cs';
    
    ELSIF (i_lang_cd = 'sk' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);	                	
        o_default_description := i_description;
        o_default_lang_cd := 'sk';

	ELSIF (i_lang_cd = 'hu' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);	                	
        o_default_description := i_description;
        o_default_lang_cd := 'hu';
        
   ELSIF (i_lang_cd = 'ro' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk' AND o_default_lang_cd != 'hu'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);	                	
        o_default_description := i_description;
        o_default_lang_cd := 'ro';

   ELSIF (i_lang_cd = 'bg' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk' AND o_default_lang_cd != 'hu' AND o_default_lang_cd != 'ro'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);	                	
        o_default_description := i_description;
        o_default_lang_cd := 'bg';

   ELSIF (i_lang_cd = 'ru' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk' AND o_default_lang_cd != 'hu' AND o_default_lang_cd != 'ro' AND o_default_lang_cd != 'bg'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);
        o_default_description := i_description;
        o_default_lang_cd := 'ru';

   ELSIF (i_lang_cd = 'pl' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk' AND o_default_lang_cd != 'hu' AND o_default_lang_cd != 'ro' AND o_default_lang_cd != 'bg' AND o_default_lang_cd != 'ru'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);
        o_default_description := i_description;
        o_default_lang_cd := 'pl';

   ELSIF (i_lang_cd = 'az' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk' AND o_default_lang_cd != 'hu' AND o_default_lang_cd != 'ro' AND o_default_lang_cd != 'bg' AND o_default_lang_cd != 'ru' AND o_default_lang_cd != 'pl'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);
        o_default_description := i_description;
        o_default_lang_cd := 'az';

   ELSIF (i_lang_cd = 'hr' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk' AND o_default_lang_cd != 'hu' AND o_default_lang_cd != 'ro' AND o_default_lang_cd != 'bg' AND o_default_lang_cd != 'ru' AND o_default_lang_cd != 'pl' AND o_default_lang_cd != 'az'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);
        o_default_description := i_description;
        o_default_lang_cd := 'hr';

   ELSIF (i_lang_cd = 'sr' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk' AND o_default_lang_cd != 'hu' AND o_default_lang_cd != 'ro' AND o_default_lang_cd != 'bg' AND o_default_lang_cd != 'ru' AND o_default_lang_cd != 'pl' AND o_default_lang_cd != 'az' AND o_default_lang_cd != 'hr'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);
        o_default_description := i_description;
        o_default_lang_cd := 'sr';

   ELSIF (i_lang_cd = 'tr' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk' AND o_default_lang_cd != 'hu' AND o_default_lang_cd != 'ro' AND o_default_lang_cd != 'bg' AND o_default_lang_cd != 'ru' AND o_default_lang_cd != 'pl' AND o_default_lang_cd != 'az' AND o_default_lang_cd != 'hr' AND o_default_lang_cd != 'sr'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);
        o_default_description := i_description;
        o_default_lang_cd := 'tr';

   ELSIF (i_lang_cd = 'nl' AND (o_default_lang_cd is NULL OR (o_default_lang_cd != 'de' AND o_default_lang_cd != 'en' AND o_default_lang_cd != 'fr' AND o_default_lang_cd != 'it' AND o_default_lang_cd != 'es' AND o_default_lang_cd != 'cs' AND o_default_lang_cd != 'sk' AND o_default_lang_cd != 'hu' AND o_default_lang_cd != 'ro' AND o_default_lang_cd != 'bg' AND o_default_lang_cd != 'ru' AND o_default_lang_cd != 'pl' AND o_default_lang_cd != 'az' AND o_default_lang_cd != 'hr' AND o_default_lang_cd != 'sr' AND o_default_lang_cd != 'tr'))) THEN

        LOGON_LOG.DEBUG('MN: upd_default_description',
          '  Current default: lang_cd - ' || o_default_lang_cd || ' text - ' || o_default_description || ' is going to be replaced by lang_cd - ' || i_lang_cd || ' text - ' || i_description);
        o_default_description := i_description;
        o_default_lang_cd := 'nl';

    END IF;

  END upd_default_description;
    
  --------------------------------------------
  --------------------------------------------
  -----Inserts missing description entries to tlgns_sap_material_description
  --------------------------------------------
  --------------------------------------------
  PROCEDURE insert_missing_descriptions(i_sap_material_id NUMBER, i_default_description VARCHAR2, i_default_lang_cd VARCHAR2, o_ins_total IN OUT NUMBER) IS

    CURSOR empty_material_desc IS
    SELECT lang_cd
    FROM tlgnr_lang_bs
    WHERE lang_cd NOT IN (
      SELECT lang.lang_cd
      FROM tlgnr_lang_bs lang
      INNER JOIN tlgns_sap_material_description descr ON (lang.lang_cd = descr.lang_cd)
      WHERE descr.sap_material_id = i_sap_material_id);

  BEGIN
    
    LOGON_LOG.DEBUG('MN: insert_missing_descriptions',
      ' Start inserting missing descriptions (sap_material_id = ' || i_sap_material_id || ', default_description = ' || i_default_description || ' and default_lang_cd = ' || i_default_lang_cd || ') ...');
    
    FOR cur_empty_material_desc IN empty_material_desc
    LOOP

      IF (i_default_description IS NOT NULL) THEN

        LOGON_LOG.DEBUG('MN: insert_missing_descriptions',
          '  Material description is missing for lang_cd ' || cur_empty_material_desc.lang_cd || '. Inserting material description taken from default ' || i_default_lang_cd || ' value ' || i_default_description ||' (sap_material_id = ' || i_sap_material_id || ')');      
    
        INSERT INTO tlgns_sap_material_description (sap_material_id,
          lang_cd,
          txt, 
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        VALUES (i_sap_material_id,
          cur_empty_material_desc.lang_cd,
          i_default_description,
          1, SYSTIMESTAMP, 1, SYSTIMESTAMP, 0);
          
        o_ins_total := o_ins_total + 1;
      ELSE

        LOGON_LOG.ERROR('MN: insert_missing_descriptions',
          '  Material description is missing for lang_cd ' || cur_empty_material_desc.lang_cd || ' and no default value received either (sap_material_id = ' || i_sap_material_id || ')');            
      
      END IF;
      
    END LOOP;
      
  END insert_missing_descriptions;

  ----------------------------------------------
  ----------------------------------------------
  -----sap_material_sarea entries which are ----
  -----not present in the staging area ---------
  -----anymore must be marked as deleted--------
  ----------------------------------------------
  ----------------------------------------------
  PROCEDURE mark_missing_sarea_as_deleted(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER) AS

    -- find tlgns_sap_material_sarea entries which are not present in the request anymore
    CURSOR sareas_to_mark_as_deleted IS
    SELECT sap_material_sarea_id
    FROM tlgns_sap_material_sarea
    WHERE sap_material_sarea_id NOT IN (
      SELECT db.sap_material_sarea_id
      FROM ULGNSAP.MN_MATERIALNOTIFICATION req
      INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
      INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_6 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
      INNER JOIN ULGNSAP.MN_MATERIALSALESDATA sales ON (rel1.child_mn_materialsalesdata_id = sales.hjid)
      INNER JOIN TLGNS_SAP_MATERIAL_SAREA db ON (sales.vkorg = db.sorg_cd AND sales.vtweg = db.distribution_channel)
      WHERE req.hjid = i_mat_hjid
    )
      AND sap_material_id = i_sap_material_id
      AND marked_for_deletion = '0';
    
  BEGIN
	
    LOGON_LOG.DEBUG('MN: mark_missing_sarea_as_deleted', ' Marking missing sales areas as deleted ...');
	
    FOR cur_sarea_to_delete IN sareas_to_mark_as_deleted
    LOOP

	    BEGIN

          LOGON_LOG.INFO('MN: mark_missing_sarea_as_deleted', '  Marking sarea (sap_material_sarea_id = ' || cur_sarea_to_delete.sap_material_sarea_id || ') as deleted.');
          UPDATE tlgns_sap_material_sarea SET 
            marked_for_deletion = '1',
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_material_sarea_id = cur_sarea_to_delete.sap_material_sarea_id;
          
      END;

    END LOOP;
    
	LOGON_LOG.DEBUG('MN: mark_missing_sarea_as_deleted', ' Done marking missing sales areas as deleted.');     
    
  END mark_missing_sarea_as_deleted;

  ----------------------------------------------
  ----------------------------------------------
  -----sap_material_plant entries which are ----
  -----not present in the staging area ---------
  -----anymore must be marked as deleted--------
  ----------------------------------------------
  ----------------------------------------------
  PROCEDURE mark_missing_plant_as_deleted(i_mat_hjid IN NUMBER, i_sap_material_id IN NUMBER) AS

    -- find tlgns_sap_material_plant entries which are not present in the request anymore
    CURSOR plants_to_mark_as_deleted IS
    SELECT sap_material_plant_id
    FROM tlgns_sap_material_plant
    WHERE sap_material_plant_id NOT IN (
      SELECT db.sap_material_plant_id
      FROM ULGNSAP.MN_MATERIALNOTIFICATION req
      INNER JOIN ULGNSAP.MN_MATERIALNOTIFICATION_MATE_0 rel0 ON (req.hjid = rel0.parent_mn_materialnotificati_0)
      INNER JOIN ULGNSAP.MN_MATERIALGENERALDATA_MATER_2 rel1 ON (rel0.child_mn_materialgeneraldata_0 = rel1.parent_mn_materialgeneraldat_0)
      INNER JOIN ULGNSAP.MN_MATERIALPLANTDATA plant ON (rel1.child_mn_materialplantdata_id = plant.hjid)
      INNER JOIN TLGNS_SAP_MATERIAL_PLANT db ON (plant.werks = db.plant_cd)
      WHERE req.hjid = i_mat_hjid
    )
      AND sap_material_id = i_sap_material_id
      AND marked_for_deletion = '0';
    
  BEGIN
	
    LOGON_LOG.DEBUG('MN: mark_missing_plant_as_deleted', ' Marking missing plants as deleted ...');
	
    FOR cur_plant_to_delete IN plants_to_mark_as_deleted
    LOOP

	    BEGIN

          LOGON_LOG.INFO('MN: mark_missing_plant_as_deleted', '  Marking plant (sap_material_plant_id = ' || cur_plant_to_delete.sap_material_plant_id || ') as deleted.');
          UPDATE tlgns_sap_material_plant SET 
            marked_for_deletion = '1',
            ctl_mod_uid = 1,
            ctl_mod_ts = systimestamp,
            ctl_tcn = ctl_tcn + 1
          WHERE sap_material_plant_id = cur_plant_to_delete.sap_material_plant_id;
          
      END;

    END LOOP;
    
	LOGON_LOG.DEBUG('MN: mark_missing_plant_as_deleted', ' Done marking missing plants as deleted.');     
    
  END mark_missing_plant_as_deleted;

END SAPIMPORT_MN;
/
 
SHOW ERRORS;
--------------------------------------------------------------------------------
-- END OF THE SCRIPT
--------------------------------------------------------------------------------
