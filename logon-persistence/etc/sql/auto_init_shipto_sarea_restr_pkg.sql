CREATE OR REPLACE PACKAGE auto_init_shipto_sarea_restr AS

c_import_name             constant VARCHAR(30) := 'auto_init_shipto_sarea_restr';
  
copy_restr_failed EXCEPTION;

PROCEDURE main(i_min_loglevel IN VARCHAR2);

PROCEDURE copy_restrictions(from_contract_item_id IN NUMBER, to_contract_item_id IN NUMBER);

PROCEDURE copy_truck_type_restrictions(old_shipto_sarea_restr_id IN NUMBER);

PROCEDURE copy_btrul_restrictions(old_shipto_sarea_restr_id IN NUMBER);

END auto_init_shipto_sarea_restr;
/

SHOW ERRORS;

CREATE OR REPLACE PACKAGE BODY auto_init_shipto_sarea_restr AS
  ----------------------------------------------------------------
  ----------------------------------------------------------------
  --------------------auto_init_shipto_sarea_restr---------
  ----------------------------------------------------------------
  ----------------------------------------------------------------

  PROCEDURE main (i_min_loglevel IN VARCHAR2) AS
  
	l_sap_contract_item_id NUMBER := NULL;
	l_sap_contract_type_cd VARCHAR2(4) := NULL;
	l_sarea_id NUMBER := NULL;
	l_shipto_sap_bp_id NUMBER := NULL;
	l_soldto_sap_bp_id NUMBER := NULL;
	l_incoterms_cd VARCHAR2(3) := NULL;
    l_sap_contract_no VARCHAR2(10) := NULL;
	l_sap_material_id NUMBER := NULL;
	l_supply_plant_cd VARCHAR2(4 CHAR) := NULL;
    l_matching_contract_item_id NUMBER := NULL;
    l_already_existing_restr_id NUMBER := NULL;
    l_contract_valid BOOLEAN := TRUE;
    l_parent_offer_no VARCHAR2(70) := NULL;
    l_valid_until_dat DATE := NULL;
    l_ap_group_leader_id NUMBER := NULL;
    l_material_deleted NUMBER := NULL;
    l_contract_item_deleted NUMBER := NULL;
    l_item_grouping_type NUMBER := NULL;
	
    -- potential new records which are missing restrictions
    cursor cur_dispos_for_import_restr is
    select rmt.SHIPTO_SAREA_REST_MAINT_ID, 
           rmt.SAP_CONTRACT_ITEM_ID
    from TLGNC_SHIPTO_SAREA_REST_MAINT rmt where rmt.MATCH_SAP_CONTRACT_ITEM_ID is NULL and rmt.PROCESSING_TS is NULL;
    
    BEGIN
      
	  -- set logging parameters
      LOGON_LOG.g_min_loglevel := i_min_loglevel;
      
	  -- set the time of the start of the import
	  LOGON_LOG.start_import(c_import_name);      
      
	    
      -- set the time of the start of the import
      LOGON_LOG.INFO('ADM: main','Started automatic maintenance of dispocard restrictions');
      
	  FOR dispo_for_import_rest IN cur_dispos_for_import_restr
      LOOP
      -- reset temp values holding values to be matched.
	  l_sap_contract_type_cd := NULL;
	  l_sarea_id := NULL;
	  l_shipto_sap_bp_id := NULL;
	  l_soldto_sap_bp_id := NULL;
	  l_incoterms_cd := NULL;
      l_sap_contract_no := NULL;
	  l_sap_material_id := NULL;
	  l_supply_plant_cd := NULL;
      l_matching_contract_item_id := NULL;
      l_already_existing_restr_id := NULL;
      l_contract_valid := TRUE;
      l_parent_offer_no := NULL;
      l_valid_until_dat := NULL;
      l_ap_group_leader_id := NULL;
      l_sap_contract_item_id := NULL;
      l_material_deleted := NULL;
      l_contract_item_deleted := NULL;
      l_item_grouping_type := NULL;
            
      --check if contract item already has restriction
      BEGIN
      	select SHIPTO_SAREA_RESTR_ID
      	into l_already_existing_restr_id
      	from TLGNC_SHIPTO_SAREA_RESTR
      	where SAP_CONTRACT_ITEM_ID = dispo_for_import_rest.sap_contract_item_id;
      	
      	EXCEPTION
           WHEN no_data_found THEN
            	LOGON_LOG.INFO('ADM: main','Contract item : ' || dispo_for_import_rest.sap_contract_item_id || ' has no restricion, available for import');
           WHEN too_many_rows THEN
           		LOGON_LOG.INFO('ADM: main','Contract item : ' || dispo_for_import_rest.sap_contract_item_id || ' has already restricions, import will be skipped');
            	GOTO update_auto_maintanance_table;
           WHEN others THEN
	            LOGON_LOG.ERROR('ADM: main', SQLCODE, SQLERRM,
	              ' Unknown exception in selecting matching record for sap contract item id : ' || l_sap_contract_item_id);
	            RAISE;
       END;
       
       IF l_already_existing_restr_id is not null THEN
       		LOGON_LOG.INFO('ADM: main','Contract item : ' || dispo_for_import_rest.sap_contract_item_id || ' has already restricions, import will be skipped');
       		GOTO update_auto_maintanance_table;
       END IF;
      	
       -- find contract item and its filters to be matched and fields to check validity.
       BEGIN
	   select 
	          cri.sap_contract_item_id,
			  cr.sap_contract_type_cd,
			  cr.soldto_sap_bp_id,
			  cr.shipto_sap_bp_id, 
			  cr.sarea_id, 
			  cr.incoterms_cd,
			  cri.sap_material_id, 
			  cri.supply_plant_cd,
			  cri.parent_offer_no,
			  cri.ap_group_leader_id,
			  cr.valid_until_dat,
			  cr.sap_contract_no,
			  m.marked_for_deletion,
			  cri.marked_for_deletion,
			  cr.item_grouping_type
		into  l_sap_contract_item_id,
		      l_sap_contract_type_cd,
		      l_soldto_sap_bp_id,
		      l_shipto_sap_bp_id,
		  	  l_sarea_id,
		  	  l_incoterms_cd,
		  	  l_sap_material_id,
		  	  l_supply_plant_cd,
		  	  l_parent_offer_no,
		  	  l_ap_group_leader_id,
		  	  l_valid_until_dat,
		  	  l_sap_contract_no,
		  	  l_material_deleted,
		  	  l_contract_item_deleted,
		  	  l_item_grouping_type
	    from tlgns_sap_contract cr
	    join tlgns_sap_contract_item cri on cr.sap_contract_id = cri.sap_contract_id and cri.sap_contract_item_id = dispo_for_import_rest.sap_contract_item_id
	    join tlgns_sap_material m on m.sap_material_id = cri.sap_material_id;
	    	
	   	EXCEPTION
           WHEN no_data_found THEN
            	LOGON_LOG.INFO('ADM: main','Contract item : ' || dispo_for_import_rest.sap_contract_item_id || ' could not be found in contract item table');
            	GOTO update_auto_maintanance_table;
           WHEN others THEN
	            LOGON_LOG.ERROR('ADM: main', SQLCODE, SQLERRM,
	              ' Unknown exception in selecting matching record for sap contract item id : ' || l_sap_contract_item_id);
	            GOTO update_auto_maintanance_table;
	    END;
	       
	    --CHECK FOR CONTRACT VALIDITY
	    
	    --SAP Contract is valid or will be valid in the future (valid_until_dat >= today)
	    IF l_valid_until_dat < SYSDATE THEN
	    	l_contract_valid := FALSE;	    
	    --SAP Material is not deleted (marked_for_deletion = ‘0’)
	    ELSIF l_material_deleted != 0 THEN
	    	l_contract_valid := FALSE;
		--SAP Contract item is not deleted (marked_for_deletion=’0’)
		ELSIF l_contract_item_deleted != 0 THEN
	    	l_contract_valid := FALSE;	    
		--SAP Contract item has a PO number
		ELSIF l_parent_offer_no IS NULL THEN
			l_contract_valid := FALSE;
		--this value must be null
		ELSIF l_ap_group_leader_id IS NOT NULL THEN
		    l_contract_valid := FALSE;
		END IF;
      
      	--if contract is no longer valid we skip importing and we set the timestamp so it wont be tried again
        IF NOT l_contract_valid THEN
      		LOGON_LOG.INFO('ADM: main','Contract item : ' || dispo_for_import_rest.sap_contract_item_id || ' is no longer valid, skipping import');
      		GOTO update_auto_maintanance_table;
        END IF;
	    
	    -- results are ranked based on valid until dat and sap_contract_no
        BEGIN
		  select sap_contract_item_id 
		  into l_matching_contract_item_id 
		  from 
		  (
		  	SELECT *
			FROM (
			  SELECT t.*,
			         RANK() OVER ( PARTITION BY sap_contract_type_cd, SOLDTO_SAP_BP_ID, shipto_sap_bp_id, sarea_id,	incoterms_cd, sap_material_id, supply_plant_cd, parent_offer_no ORDER BY VALID_UNTIL_DAT DESC, SAP_CONTRACT_NO) AS rnk
			  FROM   (select cri.SAP_CONTRACT_ITEM_ID, cr.item_grouping_type, cr.sap_contract_type_cd, cr.SOLDTO_SAP_BP_ID, cr.shipto_sap_bp_id, cr.sarea_id,	cr.incoterms_cd, cri.sap_material_id, cri.supply_plant_cd,cri.parent_offer_no, cr.VALID_UNTIL_DAT, to_number(cr.SAP_CONTRACT_NO) as SAP_CONTRACT_NO 
			  		   FROM tlgns_sap_contract cr 
					   join tlgns_sap_contract_item cri on cr.sap_contract_id = cri.sap_contract_id and cri.ap_group_leader_id is null
					   left outer join tlgnc_shipto_sarea_rest_maint ssrm on ssrm.SAP_CONTRACT_ITEM_ID = cri.SAP_CONTRACT_ITEM_ID
					   where REGEXP_LIKE(cr.sap_contract_no,'^[0-9]+$') and (ssrm.SAP_CONTRACT_ITEM_ID != dispo_for_import_rest.sap_contract_item_id or ssrm.SAP_CONTRACT_ITEM_ID is null)) t
			)
			WHERE rnk = 1
		  )
		  where sap_contract_type_cd = l_sap_contract_type_cd
		  and ((item_grouping_type = l_item_grouping_type) OR (item_grouping_type IS NULL AND l_item_grouping_type IS NULL))
		  and soldto_sap_bp_id = l_soldto_sap_bp_id
		  and shipto_sap_bp_id = l_shipto_sap_bp_id
		  and sarea_id = l_sarea_id
		  and ((incoterms_cd = l_incoterms_cd) OR (incoterms_cd IS NULL AND l_incoterms_cd IS NULL))
		  and sap_material_id = l_sap_material_id
		  and ((supply_plant_cd = l_supply_plant_cd) OR (supply_plant_cd IS NULL AND l_supply_plant_cd IS NULL))
		  and ((parent_offer_no = l_parent_offer_no));
	        
	      EXCEPTION
            WHEN no_data_found THEN
            	LOGON_LOG.ERROR('ADM: main','No matching found for contract item: ' || l_sap_contract_item_id || 'with sap_contract_type_cd = ' || l_sap_contract_type_cd || ' sarea_id = '
            	|| l_sarea_id || ' shipto_sap_bp_id = ' || l_shipto_sap_bp_id || 'soldto_sap_bp_id = ' || l_soldto_sap_bp_id || ' incoterms_cd = ' || l_incoterms_cd || ' sap_contract_no = '
            	|| l_sap_contract_no || ' sap_material_id = ' || l_sap_material_id || ' supply_plant_cd = ' || l_supply_plant_cd);
            	GOTO update_auto_maintanance_table;
            WHEN too_many_rows THEN
            	LOGON_LOG.ERROR('ADM: main','Too many matching found for contract item: ' || l_sap_contract_item_id || 'with sap_contract_type_cd = ' || l_sap_contract_type_cd || ' sarea_id = '
            	|| l_sarea_id || ' shipto_sap_bp_id = ' || l_shipto_sap_bp_id || 'soldto_sap_bp_id = ' || l_soldto_sap_bp_id || ' incoterms_cd = ' || l_incoterms_cd || ' sap_contract_no = '
            	|| l_sap_contract_no || ' sap_material_id = ' || l_sap_material_id || ' supply_plant_cd = ' || l_supply_plant_cd);
            	GOTO update_auto_maintanance_table;
            WHEN others THEN
	            LOGON_LOG.ERROR('ADM: main', SQLCODE, SQLERRM,
	              ' Unknown exception in selecting matching record for sap contract item id : ' || l_sap_contract_item_id);
	            RAISE;
        END;
        
        -- copy restrictions of found match
        BEGIN
        	copy_restrictions(l_matching_contract_item_id, dispo_for_import_rest.sap_contract_item_id);			
        
	        EXCEPTION
	        	WHEN copy_restr_failed THEN
	        		LOGON_LOG.ERROR('ADM: main', SQLCODE, SQLERRM,' Copy of restrictions failed for contract item because of bill to rule or trucktype copy exception: ' || l_sap_contract_item_id);
					ROLLBACK;
		   		WHEN others THEN  -- does not handle RAISEd exception
		   			LOGON_LOG.ERROR('ADM: main', SQLCODE, SQLERRM,' Unknown exception in copy of restrictions : ' || l_sap_contract_item_id);
		   			ROLLBACK;
	   	END;
        
        <<update_auto_maintanance_table>>
        
        BEGIN
			UPDATE TLGNC_SHIPTO_SAREA_REST_MAINT
			SET MATCH_SAP_CONTRACT_ITEM_ID = l_matching_contract_item_id,
				PROCESSING_TS = SYSDATE,
				ctl_mod_uid = 1,
				ctl_mod_ts = systimestamp,
				ctl_tcn = ctl_tcn + 1
			WHERE SHIPTO_SAREA_REST_MAINT_ID = dispo_for_import_rest.SHIPTO_SAREA_REST_MAINT_ID;
			COMMIT;
		
		EXCEPTION
			WHEN DUP_VAL_ON_INDEX THEN
				LOGON_LOG.ERROR('ADM: main', SQLCODE, SQLERRM,'Exception in updating matching record for sap contract item id : ' || l_sap_contract_item_id);
		   		ROLLBACK;
		   	WHEN others THEN  -- does not handle RAISEd exception
		   		LOGON_LOG.ERROR('ADM: main', SQLCODE, SQLERRM,' Unknown exception in updating matching record for sap contract item id : ' || l_sap_contract_item_id);
		   		ROLLBACK;
	   	END;
			
        
	  END LOOP;  
      LOGON_LOG.INFO('ADM: main','End of automatic maintenance of dispocard restrictions');
      
      -- set the run number and the time of the end of import
      LOGON_LOG.FINISH_IMPORT(c_import_name);
      
	END main;
	
	PROCEDURE copy_restrictions(from_contract_item_id IN NUMBER, to_contract_item_id IN NUMBER) AS
	
	
	CURSOR DEFAULTS_TO_COPY IS
	  SELECT *
	  FROM tlgnc_shipto_sarea_restr rs
	  WHERE rs.SAP_CONTRACT_ITEM_ID = from_contract_item_id;
	
	BEGIN
		LOGON_LOG.INFO('	ADM: copy_restrictions','Started copy shipto defaults of contract item: ' || from_contract_item_id || ' to contract item: ' || to_contract_item_id);
		
		 	  
		FOR DEFAULT_TO_COPY IN DEFAULTS_TO_COPY
	    LOOP
	    
	    
	    LOGON_LOG.INFO('		ADM: copy_restrictions', 'Copy shipto default: ' ||  DEFAULT_TO_COPY.shipto_sarea_restr_id);
	      
		INSERT INTO tlgnc_shipto_sarea_restr
		(
		 shipto_sarea_restr_id,
		 shipto_sarea_id,
		 enabled_flag,
		 available_in_cust_web_flag,
		 available_in_pickup_app_flag,
		 sap_material_id,
		 mix_design_id,
		 production_plant_cd,
		 shipto_silo_id,
		 shippingpoint_id,
		 loadingpoint_id,
		 quantity,
		 uom_cd,
		 per_sdorder_quantity,
		 per_sdorder_uom_cd,
		 exact_custorder_qty_flag,
		 exact_sdorder_qty_flag,
		 on_hold_order_and_locked_flag,
		 open_custorder_flag,
		 delivery_freq_every_time,
		 delivery_freq_every_time_uom,
		 delivery_freq_qtyperhre,
		 delivery_freq_qtyperhre_uom_cd,
		 delivery_time_tolerance,
		 delivery_time_tolerance_uom,
		 delivery_type_cd,
		 pool_id,
		 auto_pool_assignment_flag,
		 auto_release_flag,
		 comm_carrier_id,
		 auto_carrier_assignment_flag,
		 billto_sap_bp_id,
		 payer_sap_bp_id,
		 billto_ruletype_cd,
		 free_delivery_flag,
		 release_sap_flag,
		 sap_suppress_min_charge_flag,
		 note_order_mgmt,
		 note_delivery,
		 note_dispatching,
		 note_truck_driver,
		 note_railway_disp,
		 package_type_cd,
		 sap_contract_item_id,
		 ctl_cre_uid,
		 ctl_cre_ts,
		 ctl_mod_uid,
		 ctl_mod_ts,
		 ctl_tcn,
		 spi_cd,
		 exec_carrier_id
		)
		VALUES 
		(
		 slgnc_shipto_sarea_restr.nextval,
		 default_to_copy.shipto_sarea_id,
		 default_to_copy.enabled_flag,
		 default_to_copy.available_in_cust_web_flag,
		 default_to_copy.available_in_pickup_app_flag,
		 default_to_copy.sap_material_id,
		 default_to_copy.mix_design_id,
		 default_to_copy.production_plant_cd,
		 default_to_copy.shipto_silo_id,
		 default_to_copy.shippingpoint_id,
		 default_to_copy.loadingpoint_id,
		 default_to_copy.quantity,
		 default_to_copy.uom_cd,
		 default_to_copy.per_sdorder_quantity,
		 default_to_copy.per_sdorder_uom_cd,
		 default_to_copy.exact_custorder_qty_flag,
		 default_to_copy.exact_sdorder_qty_flag,
		 default_to_copy.on_hold_order_and_locked_flag,
		 default_to_copy.open_custorder_flag,
		 default_to_copy.delivery_freq_every_time,
		 default_to_copy.delivery_freq_every_time_uom,
		 default_to_copy.delivery_freq_qtyperhre,
		 default_to_copy.delivery_freq_qtyperhre_uom_cd,
		 default_to_copy.delivery_time_tolerance,
		 default_to_copy.delivery_time_tolerance_uom,
		 default_to_copy.delivery_type_cd,
		 default_to_copy.pool_id,
		 default_to_copy.auto_pool_assignment_flag,
		 default_to_copy.auto_release_flag,
		 default_to_copy.comm_carrier_id,
		 default_to_copy.auto_carrier_assignment_flag,
		 default_to_copy.billto_sap_bp_id,
		 default_to_copy.payer_sap_bp_id,
		 default_to_copy.billto_ruletype_cd,
		 default_to_copy.free_delivery_flag,
		 default_to_copy.release_sap_flag,
		 default_to_copy.sap_suppress_min_charge_flag,
		 default_to_copy.note_order_mgmt,
		 default_to_copy.note_delivery,
		 default_to_copy.note_dispatching,
		 default_to_copy.note_truck_driver,
		 default_to_copy.note_railway_disp,
		 default_to_copy.package_type_cd,
		 to_contract_item_id,
		 1,
		 systimestamp,
		 1,
		 systimestamp,
		 0,
		 default_to_copy.spi_cd,
		 default_to_copy.exec_carrier_id
		);
		
		LOGON_LOG.INFO('		ADM: copy_restrictions', 'Copied shipto default(restriction): ' ||  DEFAULT_TO_COPY.shipto_sarea_restr_id || 'to new shipto default(restriction): ' || slgnc_shipto_sarea_restr.currval);
		
		
		IF DEFAULT_TO_COPY.shipto_sarea_restr_id is not null then
				-- copy BTRUL restrictions
				copy_btrul_restrictions(DEFAULT_TO_COPY.shipto_sarea_restr_id);
				-- copy trucktype restriction
				copy_truck_type_restrictions(DEFAULT_TO_COPY.shipto_sarea_restr_id);
		END IF;

		END LOOP;
		
	END copy_restrictions;
	
	PROCEDURE copy_btrul_restrictions(old_shipto_sarea_restr_id IN NUMBER) AS
	
	CURSOR BTRUL_DEFAULTS_TO_COPY IS
	  SELECT *
	  FROM TLGNC_SHIPTO_SAREA_RESTR_BTRUL ssrb
	  WHERE ssrb.SHIPTO_SAREA_RESTR_ID = old_shipto_sarea_restr_id;
	
	BEGIN
		
		LOGON_LOG.INFO('	ADM: copy_btrul_restrictions','Started copy shipto btrul defaults of shipto rest: ' || old_shipto_sarea_restr_id);
		
		FOR BTRUL_DEFAULT_TO_COPY IN BTRUL_DEFAULTS_TO_COPY
	    LOOP
	    	BEGIN
		    	LOGON_LOG.DEBUG('	ADM: copy_btrul_restrictions ',' Copy shipto btrul defaults of shipto rest with shipto_sarea_restr_id: ' || slgnc_shipto_sarea_restr.currval || ' billto_ruletype_cd: ' || btrul_default_to_copy.billto_ruletype_cd || ' value: ' || btrul_default_to_copy.value);
				INSERT INTO TLGNC_SHIPTO_SAREA_RESTR_BTRUL
				(
				 shipto_sarea_restr_btrul_id,
				 shipto_sarea_restr_id,
				 billto_sap_bp_id,
				 billto_ruletype_cd,
				 value,
				 ctl_cre_uid,
				 ctl_cre_ts,
				 ctl_mod_uid,
				 ctl_mod_ts,
				 ctl_tcn
				)
				VALUES
				(
				 slgnc_shiptosarearestrbtrul.nextval,
				 slgnc_shipto_sarea_restr.currval,
				 btrul_default_to_copy.billto_sap_bp_id,
				 btrul_default_to_copy.billto_ruletype_cd,
				 btrul_default_to_copy.value,
				 1,
				 systimestamp,
				 1,
				 systimestamp,
				 0
				);
			EXCEPTION
				WHEN DUP_VAL_ON_INDEX THEN
					LOGON_LOG.ERROR('ADM: copy_btrul_restrictions', SQLCODE, SQLERRM,' Exception in copy of bill to rules, unique constraint violated');
			   		RAISE copy_restr_failed;
			   	WHEN others THEN  -- does not handle RAISEd exception
			   		LOGON_LOG.ERROR('ADM: copy_btrul_restrictions', SQLCODE, SQLERRM,' Unknown exception in copy of bill to rules');
			   		RAISE copy_restr_failed;
			END;
		
		END LOOP;
		
		LOGON_LOG.INFO('	ADM: copy_btrul_restrictions','End of copy shipto btrul defaults of shipto rest: ' || old_shipto_sarea_restr_id);
		
	END copy_btrul_restrictions;
	
	
	PROCEDURE copy_truck_type_restrictions(old_shipto_sarea_restr_id IN NUMBER) AS
	
	CURSOR TRUCK_TYPE_DEFAULTS_TO_COPY IS
	  SELECT *
	  FROM TLGNC_SHSAREA_RESTR_TRUCK_TYPE srtt
	  WHERE srtt.SHIPTO_SAREA_RESTR_ID = old_shipto_sarea_restr_id;
	
	BEGIN
		
		LOGON_LOG.INFO('	ADM: copy_truck_type_restrictions','Started copy shipto truck type defaults of shipto rest: ' || old_shipto_sarea_restr_id);
		
		FOR TRUCK_TYPE_DEFAULT_TO_COPY IN TRUCK_TYPE_DEFAULTS_TO_COPY
	    LOOP
	    	BEGIN
				INSERT INTO TLGNC_SHSAREA_RESTR_TRUCK_TYPE
				(
				 shsarea_restr_truck_type_id,
				 shipto_sarea_restr_id,
				 truck_type_cd,
				 evaluation_priority,
		         ctl_cre_uid,
				 ctl_cre_ts,
				 ctl_mod_uid,
				 ctl_mod_ts,
				 ctl_tcn
				)
				VALUES
				(
				 slgnc_shsarearestrtrucktype.nextval,
				 slgnc_shipto_sarea_restr.currval,
				 TRUCK_TYPE_DEFAULT_TO_COPY.truck_type_cd,
				 TRUCK_TYPE_DEFAULT_TO_COPY.evaluation_priority,
				 1,
				 systimestamp,
				 1,
				 systimestamp,
				 0
				);
			EXCEPTION
				WHEN DUP_VAL_ON_INDEX THEN
					LOGON_LOG.ERROR('ADM: copy_btrul_restrictions', SQLCODE, SQLERRM,' Exception in copy of trucktypes, unique constraint violated');
			   		RAISE copy_restr_failed;
			   	WHEN others THEN  -- does not handle RAISEd exception
			   		LOGON_LOG.ERROR('ADM: copy_truck_type_restrictions', SQLCODE, SQLERRM,' Unknown exception in copy of truck type restriction');
			   		RAISE copy_restr_failed;		
			END;
		END LOOP;
		LOGON_LOG.INFO('	ADM: copy_truck_type_restrictions','End of copy shipto truck type defaults of shipto rest: ' || old_shipto_sarea_restr_id);
		
	END copy_truck_type_restrictions;

END auto_init_shipto_sarea_restr;
/

SHOW ERRORS;


