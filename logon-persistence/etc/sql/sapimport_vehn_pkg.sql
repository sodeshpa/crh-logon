--------------------------------------------------------------------------------
--
-- PROJECT      : $Project: LOGON $
--
-- AUTHOR       : $Author: nemethze $
-- CREATION DATE: $Date$
-- DESCRIPTION  : Vehicle Notification import package.
--
-- FIXME: Throwing own exceptions would simplify even more.

CREATE OR REPLACE PACKAGE sapimport_vehn AS

	PROCEDURE main(i_min_loglevel IN VARCHAR2, i_days_to_import IN NUMBER);
	FUNCTION get_sap_vehicle_id(i_sap_vehicle_no IN VARCHAR2) RETURN NUMBER;
	FUNCTION get_sapimport_status_cd(i_notif_hjid IN NUMBER) RETURN NUMBER;
	FUNCTION is_request_ex_carrier(i_master_hjid IN NUMBER) RETURN NUMBER;
	FUNCTION get_sap_vendor_id(i_master_hjid IN NUMBER) RETURN NUMBER;
	FUNCTION get_sap_vehicle_type(i_master_hjid IN NUMBER) RETURN VARCHAR2;
	FUNCTION is_sap_vehicle_type_valid(i_sap_vehicle_type_cd IN VARCHAR2) RETURN NUMBER;
	FUNCTION get_vehicle_characteristic(i_vehicle_characteristic IN VARCHAR2) RETURN NUMBER;
	FUNCTION get_logon_vehicle_type(i_sap_vehicle_type_cd IN VARCHAR2) RETURN NUMBER;
	FUNCTION is_vehicle_plateno_ok(i_plate_number IN VARCHAR2, i_sap_truck_no IN VARCHAR2, i_vehicle_characteristic IN VARCHAR2) RETURN NUMBER;
	FUNCTION get_vehicle_truck_eqp_no(i_master_hjid IN VARCHAR2) RETURN VARCHAR2;
	FUNCTION get_vehicle_trailer_eqp_no(i_master_hjid IN VARCHAR2) RETURN VARCHAR2;
	PROCEDURE handle_error(var_sqlerrm IN VARCHAR2, var_sapimpimporteddatelement IN VARCHAR2, i_notif_hjid IN VARCHAR2);
	PROCEDURE set_error(var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2);
	PROCEDURE set_info(var_source IN VARCHAR2, var_text IN VARCHAR2);
	PROCEDURE set_debug(var_text IN VARCHAR2);

END sapimport_vehn;
/

CREATE OR REPLACE PACKAGE BODY sapimport_vehn AS

	----------------------------------------------------------------
	----------------------------------------------------------------
	-------------------VEHICLE-NOTIFICATION-IMPORT------------------
	----------------------------------------------------------------
	----------------------------------------------------------------
	
	-- CONSTANTS --
	c_import_name						constant	VARCHAR(30) := 'sapimport_vehn';
	
	-- staus vars
	c_imported_from_sap_status			constant	NUMBER := 0;								-- not imported
	c_imported_to_logon_status			constant	NUMBER := 20;								-- imported successfully
	c_skipped_status					constant	NUMBER := 2;								-- skipped newer available
	c_failed_abandoned					constant	NUMBER := 9;								-- failed abandoned
	
	c_executional_carrier				constant	VARCHAR2(2) := 'ZC';							-- vehn_vehiclepartnerfunctions.parvw
	c_vehicle_type						constant	VARCHAR2(12) := 'VEHICLE_TYPE';				-- vehn_vehicleclassification.atnam
	c_veh_char							constant	VARCHAR2(22) := 'VEHICLE_CHARACTERISTIC';		-- vehn_vehicleclassification.atnam
	
	c_veh_char_individual				constant	VARCHAR2(18) 	:= 'INDIVIDUAL VEHICLE';		-- vehn_vehicleclassification.atwrt
	c_veh_char_generic					constant	VARCHAR2(18) 	:= 'GENERIC VEHICLE';			-- vehn_vehicleclassification.atwrt
	
	c_veh_char_individual_value			constant	NUMBER := 0;									--stored value in sap_vehicle
	c_veh_char_generic_value			constant	NUMBER := 1;									--stored value in sap_vehicle
	
	c_vehicle_trailer_attached 			constant	NUMBER := 1;									-- vehn_vehicletrailer.status
	
	c_logon_vehicle_type_truck			constant	NUMBER := 0;
	c_logon_vehicle_type_trailer 		constant	NUMBER := 1;
	
	c_internal_vehicle					constant	VARCHAR(1) := 'V';							--vehn_vehiclemasterdata.eqtyp
	
	-- conversion constraints
	
	--vehn_vehiclemasterdata.gewei
	c_weight_tonn_a						constant	VARCHAR(2) := 'TO';
	c_weight_tonn_b						constant	VARCHAR(3) := 'TNE';
	
	c_weight_kilo_a						constant	VARCHAR(2) := 'KG';
	c_weight_kilo_b						constant	VARCHAR(3) := 'KGM';
	
	--vehn_vehiclemasterdata.volunit
	c_volume_cubicmeter					constant	VARCHAR(2) := 'M3';
	c_volume_cubicdecimeter				constant	VARCHAR(2) := 'DM';
	
	-- debug 
	c_runned_from_sqldeveloper			constant	NUMBER := 0; -- Only for debugging, set to 1 and then package can be runned from SQL Developer and debug level messages will be logged also
	
	-- GLOBAL VARIABLES --
	g_err_raised									NUMBER := 0;
	g_err_msg										VARCHAR2(200);
	g_days_to_import								NUMBER := 0;
	
	e_no_companycode_cd								EXCEPTION;
	
	PROCEDURE main(i_min_loglevel IN VARCHAR2, i_days_to_import IN NUMBER) IS
		
		--control vars for cursor status
		l_ins_total 								NUMBER := 0;
		l_upd_total 								NUMBER := 0;
		l_succesful_import							NUMBER := 0;
		
		l_sap_vehicle_id 							NUMBER := NULL;
		l_sapimpstatuscd 							NUMBER := NULL;
		l_is_request_ex_carrier 					NUMBER := NULL;
		l_sap_vendor_id 							NUMBER := NULL;
		l_logon_vehicle_type						NUMBER := NULL;
		l_vehicle_plateno_ok						NUMBER := NULL; -- true -> when ther is no other truck with same sap_no but different platenumber
		l_previous_characteristic					NUMBER := NULL; -- used to prevent characteristic update
		l_vehicle_characteristic					NUMBER := NULL;
		l_sap_vehicle_internal_flag 				NUMBER := NULL;
		l_companycode_cd							VARCHAR2(4)  := NULL;
		l_max_gross_weight							NUMBER := NULL;
		l_tare_weight								NUMBER := NULL;
		l_volume_m3									NUMBER := NULL;
				
		l_sap_vehicle_type 							VARCHAR2(30) := NULL;
		l_vehicle_trailer_eqp_no					VARCHAR2(18) := NULL;
		l_vehicle_truck_eqp_no						VARCHAR2(18) := NULL;
		
		l_hjid_info									VARCHAR2(200);
		
			-- main query to select not imported newest records
		CURSOR cur_sap_vehicle IS
			SELECT notif.hjid notif_hjid,
			notif.sapimpimporteddatelement, 
			LTRIM(master.equnr, '0') sap_vehicle_no,
			master.equnr sap_vehicle_ifc_no,
			master.bukrs companycode_cd,
			master.fleetnum vehicle_name,
			master.hjid master_hjid,
			master.licensenum plate_number,
			master.numaxle axis_count,
			master.grosswgt max_gross_weight,
			master.brgew tare_weight,
			master.loadvol vehicle_volume,
			master.eqtyp master_eqtyp,
			master.equnr master_equnr,
			master.sttxt master_sttxt,
			master.gewei master_gewei,
			master.volunit master_volunit,
			vehicleclassification_vt.atwrt sap_vehicle_type,
			vehicleclassification_char.atwrt vehicle_characteristic
		FROM ulgnsap.vehn_vehiclenotification notif
			INNER JOIN ulgnsap.vehn_vehiclenotification_veh_0 notif_master ON (notif.hjid = notif_master.parent_vehn_vehiclenotificat_0)
			INNER JOIN ulgnsap.vehn_vehiclemasterdata master ON (notif_master.child_vehn_vehiclemasterdata_0 = master.hjid)
			INNER JOIN ulgnsap.vehn_vehiclemasterdata_vehic_3 vc_master_vt ON (master.hjid = vc_master_vt.parent_vehn_vehiclemasterdat_0) -- vc vehicle type
			INNER JOIN ulgnsap.vehn_vehicleclassification vehicleclassification_vt ON (vehicleclassification_vt.hjid = vc_master_vt.child_vehn_vehicleclassifica_0)
			INNER JOIN ulgnsap.vehn_vehiclemasterdata_vehic_3 vc_master_char ON (master.hjid = vc_master_char.parent_vehn_vehiclemasterdat_0) -- vc vehicle type
			INNER JOIN ulgnsap.vehn_vehicleclassification vehicleclassification_char ON (vehicleclassification_char.hjid = vc_master_char.child_vehn_vehicleclassifica_0)
		WHERE notif.sapimpstatuscd = c_imported_from_sap_status
			AND vehicleclassification_vt.atnam = c_vehicle_type
			AND vehicleclassification_char.atnam = c_veh_char
		ORDER BY notif.sapimpimporteddatelement desc;
	
	BEGIN
		g_days_to_import := i_days_to_import;
		
		-- set the time of the start of the import
		LOGON_LOG.start_import(c_import_name);
		-- set logging parameters
		LOGON_LOG.g_min_loglevel := i_min_loglevel;
	
		IF (c_runned_from_sqldeveloper = 1) THEN
			LOGON_LOG.g_min_loglevel := 'DEBUG';
		END IF;
		
		set_info('VEHN: load_tlgns_sap_vehicle', 'START loading data into ''TLGNS_SAP_VEHICLE'' table...');
		
		FOR cur_sap_vehicle_record IN cur_sap_vehicle
		LOOP
			-- 0. INIT LOCAL VARS
			l_sap_vehicle_id := NULL;
			l_is_request_ex_carrier := NULL;
			l_sap_vendor_id := NULL;
			l_sap_vehicle_type := NULL;
			l_logon_vehicle_type  := NULL; 
			l_vehicle_plateno_ok := NULL;
			l_previous_characteristic  := NULL;
			l_vehicle_characteristic  := NULL;
			l_sap_vehicle_internal_flag := NULL;
			l_companycode_cd := NULL;
			l_vehicle_trailer_eqp_no := NULL;
			l_vehicle_truck_eqp_no := NULL;
			l_max_gross_weight := NULL;
			l_tare_weight := NULL;
			l_volume_m3 := NULL;
			l_succesful_import := 0;
			
			g_err_msg := NULL;
			g_err_raised := 0;
			l_hjid_info := ' (vehn_vehiclenotification.hjid = ' || cur_sap_vehicle_record.notif_hjid || ' , vehn_vehiclemasterdata.hjid = ' || cur_sap_vehicle_record.master_hjid || ')';
			
		BEGIN			
			-- 1. check if sap_vehicle_no exists
			IF (cur_sap_vehicle_record.sap_vehicle_no IS NOT NULL) THEN
				l_sap_vehicle_id := get_sap_vehicle_id(cur_sap_vehicle_record.sap_vehicle_no);
				l_hjid_info := ' (SAP Vehicle ID (vehn_vehiclemasterdata.equnr) = ' || cur_sap_vehicle_record.sap_vehicle_no || ', vehn_vehiclenotification.hjid = ' || cur_sap_vehicle_record.notif_hjid || ' , vehn_vehiclemasterdata.hjid = ' || cur_sap_vehicle_record.master_hjid || ')';
			ELSE
				set_error('VEHN: load_tlgns_sap_vehicle', NULL, NULL, 'SAP Vehicle ID (vehn_vehiclemasterdata.equnr) = NULL.');
			END IF;
			
			set_info('VEHN: load_tlgns_sap_vehicle', ' * Checking vehicle ...'  || l_hjid_info);
			
			l_sapimpstatuscd := get_sapimport_status_cd(cur_sap_vehicle_record.notif_hjid);
			
			IF (l_sapimpstatuscd = c_skipped_status) THEN
				set_info('VEHN: load_tlgns_sap_vehicle', 'Skip of vehicle import because a newer version of vehicle (sap_vehicle_no = ' || cur_sap_vehicle_record.sap_vehicle_no ||' ) was already imported.');
			ELSE
					-- 3. Basic conditions check if import is possible
					l_is_request_ex_carrier := is_request_ex_carrier(cur_sap_vehicle_record.master_hjid);
					set_debug('l_is_request_ex_carrier = ' || l_is_request_ex_carrier);
					l_sap_vendor_id := get_sap_vendor_id(cur_sap_vehicle_record.master_hjid);
					set_debug('l_sap_vendor_id = ' || l_sap_vendor_id);
					l_sap_vehicle_type := get_sap_vehicle_type(cur_sap_vehicle_record.master_hjid);
					set_debug('l_sap_vehicle_type = ' || l_sap_vehicle_type);
					l_logon_vehicle_type := get_logon_vehicle_type(l_sap_vehicle_type);
					set_debug('l_logon_vehicle_type = ' || l_logon_vehicle_type);
					l_vehicle_characteristic := get_vehicle_characteristic(cur_sap_vehicle_record.vehicle_characteristic);
					set_debug('l_vehicle_characteristic = ' || l_vehicle_characteristic);
					
					-- 4. check truck and trailer properties (if there was no error)
					IF (g_err_raised = 0) THEN
						IF (l_logon_vehicle_type = c_logon_vehicle_type_truck) THEN
							set_debug('Vehicle is TRUCK.');
						END IF;
						
						IF (l_logon_vehicle_type = c_logon_vehicle_type_trailer) THEN
							-- check trailer properties (eqp no)
							set_debug('Vehicle is TRAILER.');
							l_vehicle_truck_eqp_no := get_vehicle_truck_eqp_no(cur_sap_vehicle_record.master_hjid);
							l_vehicle_trailer_eqp_no := get_vehicle_trailer_eqp_no(cur_sap_vehicle_record.master_hjid);
							set_debug('l_vehicle_truck_eqp_no = ' || l_vehicle_truck_eqp_no);
							set_debug('l_vehicle_trailer_eqp_no = ' || l_vehicle_trailer_eqp_no);
						END IF;
						
						l_vehicle_plateno_ok := is_vehicle_plateno_ok(cur_sap_vehicle_record.plate_number, cur_sap_vehicle_record.sap_vehicle_no, l_vehicle_characteristic);
						
						set_debug('l_vehicle_plateno_ok = ' || l_vehicle_plateno_ok);
					END IF;
					
					-- 5. All conditions were met, Characteristic change check was not done yet as that only raises at update, checked at 6.B.
					-- Gathering additional necessary information
					IF (l_is_request_ex_carrier = 1 AND l_vehicle_plateno_ok = 1 AND g_err_raised = 0) THEN
						set_debug('NO ERRORS: l_is_request_ex_carrier = 1 AND l_vehicle_plateno_ok = 1 AND g_err_raised = 0');
						
						-- get vehicle is internal flag
						IF (cur_sap_vehicle_record.master_eqtyp = c_internal_vehicle) THEN
							l_sap_vehicle_internal_flag := 1;
						ELSE
							l_sap_vehicle_internal_flag := 0;
						END IF; 
						
						set_debug('l_sap_vehicle_internal_flag = ' || l_sap_vehicle_internal_flag);
						
						-- company code validation
						IF cur_sap_vehicle_record.companycode_cd is not null THEN
							
							BEGIN
								
								SELECT companycode_cd
								INTO l_companycode_cd
								FROM TLGNR_COMPANYCODE_BS bs
								WHERE bs.companycode_cd = cur_sap_vehicle_record.companycode_cd;
								
								EXCEPTION
								WHEN no_data_found THEN
									set_error('VEHN: load_tlgns_sap_vehicle', SQLCODE, SQLERRM, 'Unknown companycode ''' || cur_sap_vehicle_record.companycode_cd || '''.');
							    raise;
              
							END;
							
						ELSIF l_sap_vehicle_internal_flag = 1 THEN
							
							set_error('VEHN: load_tlgns_sap_vehicle', NULL, NULL, 'Companycode is mandatory for internal vehicles.');
							raise e_no_companycode_cd;
						
						END IF;
						
						-- vehicle weight conversions
						IF (cur_sap_vehicle_record.master_gewei = c_weight_tonn_a OR cur_sap_vehicle_record.master_gewei = c_weight_tonn_b OR cur_sap_vehicle_record.master_gewei is null) THEN
							l_max_gross_weight := cur_sap_vehicle_record.max_gross_weight;
							l_tare_weight := cur_sap_vehicle_record.tare_weight;
						END IF;
						
						IF (cur_sap_vehicle_record.master_gewei = c_weight_kilo_a OR cur_sap_vehicle_record.master_gewei = c_weight_kilo_b) THEN
							l_max_gross_weight := cur_sap_vehicle_record.max_gross_weight / 1000;
							l_tare_weight := cur_sap_vehicle_record.tare_weight / 1000;
						END IF;
						
						set_debug('l_max_gross_weight = ' || l_max_gross_weight);
						set_debug('l_tare_weight = ' || l_tare_weight);
						
						-- vehicle volume conversions
						
						IF (cur_sap_vehicle_record.master_volunit = c_volume_cubicmeter OR cur_sap_vehicle_record.master_volunit is null) THEN
							l_volume_m3 := cur_sap_vehicle_record.vehicle_volume;
						END IF;
						
						IF (cur_sap_vehicle_record.master_volunit = c_volume_cubicdecimeter) THEN
							l_volume_m3 := cur_sap_vehicle_record.vehicle_volume / 1000;
						END IF;
						
						IF (l_sap_vehicle_id = -1) THEN
							-- 6.A Insert case
							
							SELECT slgns_sap_vehicle.nextval
							INTO l_sap_vehicle_id
							FROM dual;
							
							set_info('VEHN: load_tlgns_sap_vehicle', 'Adding new SAP vehicle ...');
							
							INSERT INTO tlgns_sap_vehicle (
								sap_vehicle_id, sap_vehicle_no, sap_vehicle_ifc_no, 
								sap_vehicle_active_flag, sap_vehicle_name, 
								sap_vendor_id, sap_vehicle_plate_no, sap_vehicle_type_cd,
								sap_vehicle_axis_count, sap_vehicle_max_gross_weight_t, sap_vehcile_tare_weight_t,
								sap_vehicle_volume_m3, sap_vehicle_char_cd, sap_vehicle_is_internal_flag, companycode_cd,
								sap_vehicle_truck_eqp_no, sap_vehicle_trailer_eqp_no,
								sap_vehicle_is_processed_flag, sap_vehicle_is_imported_flag,
								ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
							) VALUES (
								l_sap_vehicle_id, cur_sap_vehicle_record.sap_vehicle_no, cur_sap_vehicle_record.sap_vehicle_ifc_no, 
								cur_sap_vehicle_record.master_sttxt, cur_sap_vehicle_record.vehicle_name,
								l_sap_vendor_id, cur_sap_vehicle_record.plate_number, cur_sap_vehicle_record.sap_vehicle_type,
								cur_sap_vehicle_record.axis_count, l_max_gross_weight, l_tare_weight,
								l_volume_m3, l_vehicle_characteristic, l_sap_vehicle_internal_flag, l_companycode_cd,
								LTRIM(l_vehicle_truck_eqp_no, '0'), LTRIM(l_vehicle_trailer_eqp_no, '0'),
								0, 0, 
								1, systimestamp, 1, systimestamp, 0
							);
							
							l_ins_total := l_ins_total + 1;
							l_succesful_import := 1;
							
						ELSE
							-- 6.B Characteristic change check, update
							
							SELECT veh.sap_vehicle_char_cd
							INTO l_previous_characteristic
							FROM tlgns_sap_vehicle veh
							WHERE veh.sap_vehicle_no = cur_sap_vehicle_record.sap_vehicle_no;
							
							IF (l_previous_characteristic = l_vehicle_characteristic) THEN
							
								set_info('VEHN: load_tlgns_sap_vehicle', 'Updating SAP vehicle ...');
								
								UPDATE tlgns_sap_vehicle SET 
									sap_vehicle_no = cur_sap_vehicle_record.sap_vehicle_no,
									sap_vehicle_ifc_no = cur_sap_vehicle_record.sap_vehicle_ifc_no,
									sap_vehicle_active_flag = cur_sap_vehicle_record.master_sttxt,
									sap_vehicle_name = cur_sap_vehicle_record.vehicle_name,
									sap_vendor_id = l_sap_vendor_id,
									sap_vehicle_plate_no = cur_sap_vehicle_record.plate_number,
									sap_vehicle_type_cd = cur_sap_vehicle_record.sap_vehicle_type,
									sap_vehicle_axis_count = cur_sap_vehicle_record.axis_count,
									sap_vehicle_max_gross_weight_t = l_max_gross_weight,
									sap_vehcile_tare_weight_t = l_tare_weight,
									sap_vehicle_volume_m3 = l_volume_m3,
									sap_vehicle_is_internal_flag = l_sap_vehicle_internal_flag,
									companycode_cd = l_companycode_cd,
									sap_vehicle_truck_eqp_no = LTRIM(l_vehicle_truck_eqp_no, '0'),
									sap_vehicle_trailer_eqp_no = LTRIM(l_vehicle_trailer_eqp_no, '0'),
									sap_vehicle_is_processed_flag = 0,
									sap_vehicle_is_imported_flag = 0,
									ctl_mod_uid = 1,
									ctl_mod_ts = systimestamp,
									ctl_tcn = ctl_tcn + 1
								WHERE sap_vehicle_id = l_sap_vehicle_id;
								
								l_upd_total := l_upd_total + 1;
								l_succesful_import := 1;
							
							ELSE
								set_error('VEHN: load_tlgns_sap_vehicle', NULL, NULL, 'Characteristic change is not allowed [new characteristic of truck being imported = ' || cur_sap_vehicle_record.vehicle_characteristic || '].');
							END IF;
						END IF;
					END IF;
					
					IF (l_succesful_import = 1) THEN
						-- 7.A Succesful import, update necessary control fields
						BEGIN
							
							-- update controlling attributes
							UPDATE ulgnsap.vehn_vehiclenotification
							SET sapimpstatuscd = c_imported_to_logon_status,
								sapimppropagateddatelement = systimestamp
							WHERE hjid = cur_sap_vehicle_record.notif_hjid;
							
							-- update older entries
							UPDATE ulgnsap.vehn_vehiclenotification notif
							SET notif.sapimpstatuscd = c_skipped_status,
								notif.sapimppropagateddatelement = systimestamp
							WHERE notif.sapimpstatuscd <> c_imported_to_logon_status
								AND notif.sapimpimporteddatelement < cur_sap_vehicle_record.sapimpimporteddatelement
								AND notif.hjid IN (
									SELECT notif_inner.hjid
									FROM ulgnsap.vehn_vehiclenotification notif_inner
										INNER JOIN ulgnsap.vehn_vehiclenotification_veh_0 notif_master ON (notif_inner.hjid = notif_master.parent_vehn_vehiclenotificat_0)
										INNER JOIN ulgnsap.vehn_vehiclemasterdata master ON (notif_master.child_vehn_vehiclemasterdata_0 = master.hjid)
									WHERE LTRIM(master.equnr, '0') = cur_sap_vehicle_record.sap_vehicle_no);
							COMMIT;
							
							set_info('VEHN: load_tlgns_sap_vehicle', 'SAP vehicle has been loaded/updated.');
						END;
					ELSE
						IF (g_err_raised = 1) THEN
							-- 7.B Failed import, update necessary control fields
							handle_error(SQLERRM, cur_sap_vehicle_record.sapimpimporteddatelement, cur_sap_vehicle_record.notif_hjid);
						END IF;
					END IF;
					
			END IF;
		EXCEPTION
			WHEN others THEN
			-- 7.B Failed import, update necessary control fields
			handle_error(SQLERRM, cur_sap_vehicle_record.sapimpimporteddatelement, cur_sap_vehicle_record.notif_hjid);
		END;
		END LOOP;
		
		set_info('VEHN: load_tlgns_sap_vehicle', 'FINISH! Updated ' || l_upd_total || ' and inserted ' || l_ins_total || ' records into ''TLGNS_SAP_VEHICLE'' table!');
		
    	-- set the run number and the time of the end of import
    	LOGON_LOG.FINISH_IMPORT(c_import_name);		
	END;
	
	-- Returns the tlgns_sap_vehicle.sap_vehicle_id if it exists based on tlgns_sap_vehicle.sap_vehicle_no if it exists, -1 otherwise.
	FUNCTION get_sap_vehicle_id(i_sap_vehicle_no IN VARCHAR2)
	RETURN NUMBER IS
		o_sap_vehicle_id NUMBER(12) := -1;
		l_sap_vehicle_no NUMBER(12) := -1;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				l_sap_vehicle_no := cast (i_sap_vehicle_no as number);
				
				SELECT veh.sap_vehicle_id
				INTO o_sap_vehicle_id
				FROM tlgns_sap_vehicle veh
				WHERE veh.sap_vehicle_no = i_sap_vehicle_no;
				
				set_info('VEHN: load_tlgns_sap_vehicle', 'The vehicle already exists in tlgns_sap_vehicle. sap_vehicle_id=' || o_sap_vehicle_id);
				return o_sap_vehicle_id;
				
				EXCEPTION
					WHEN no_data_found THEN
						-- vehicle will be loaded
						return o_sap_vehicle_id;
					WHEN too_many_rows THEN
						set_error('VEHN: load_tlgns_sap_vehicle', SQLCODE, SQLERRM, 'Too many SAP vehicles for sap_vehicle_no = ' || i_sap_vehicle_no);
						RAISE;
					WHEN others THEN
						set_error('VEHN: load_tlgns_sap_vehicle', SQLCODE, SQLERRM, 'Unknown exception for sap_vehicle_no = ' || i_sap_vehicle_no);
						RAISE;
			END;
	END IF;
	END;
	
	-- Returns the ulgnsap.vehn_vehiclenotification.sapimpstatuscd.
	FUNCTION get_sapimport_status_cd(i_notif_hjid IN NUMBER)
	RETURN NUMBER IS
		o_sapimport_status_cd NUMBER(2) := NULL;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				SELECT sapimpstatuscd 
				INTO o_sapimport_status_cd 
				FROM ulgnsap.vehn_vehiclenotification
				WHERE hjid = i_notif_hjid;
				
				RETURN o_sapimport_status_cd;
				
				EXCEPTION
					WHEN others THEN
					set_error('VEHN: get_sapimpstatuscd', SQLCODE, SQLERRM, 'Unknown exception.');
					RAISE;
			END;
		END IF;
	END;
	
	-- Returns 1 if the incoming request contains an executional carrier, otherwise returns 0.
	FUNCTION is_request_ex_carrier(i_master_hjid IN NUMBER)
	RETURN NUMBER IS
		o_is_request_ex_carrier NUMBER(1) := 0;
	
	l_hjid NUMBER(19) := NULL;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				SELECT pf_master.parent_vehn_vehiclemasterdat_0
				INTO l_hjid
				FROM ulgnsap.vehn_vehiclemasterdata_vehic_1 pf_master
					INNER JOIN ulgnsap.vehn_vehiclepartnerfunctions partnerfunction ON (partnerfunction.hjid = pf_master.child_vehn_vehiclepartnerfun_0)
				WHERE pf_master.parent_vehn_vehiclemasterdat_0 = i_master_hjid
					AND partnerfunction.parvw = c_executional_carrier;
				
				o_is_request_ex_carrier := 1;
				RETURN o_is_request_ex_carrier;
				
				EXCEPTION
					WHEN no_data_found THEN
						set_error('VEHN: is_request_ex_carrier', SQLCODE, SQLERRM, 'Non-executional carrier in request.');
						RAISE;
					WHEN others THEN
						set_error('VEHN: is_request_ex_carrier', SQLCODE, SQLERRM, 'Unknown Exception.');
						RAISE;
			END;
		END IF;
	END;
	
	-- Returns the vendor.sap_vendor_id by tlgns_sap_vendor.sap_ifc_id which has a valid carrier reference and vendor is executing-carrier if there is one, otherwise returns -1.
	FUNCTION get_sap_vendor_id(i_master_hjid IN NUMBER)
	RETURN NUMBER IS
		o_sap_vendor_id NUMBER(12) := -1;
	
	l_sap_ifc_id VARCHAR2(11) := '';
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				SELECT partnerfunction.iparnr
				INTO l_sap_ifc_id
				FROM ulgnsap.vehn_vehiclemasterdata_vehic_1 pf_master
					INNER JOIN ulgnsap.vehn_vehiclepartnerfunctions partnerfunction ON (partnerfunction.hjid = pf_master.child_vehn_vehiclepartnerfun_0)
				WHERE pf_master.parent_vehn_vehiclemasterdat_0 = i_master_hjid
					AND partnerfunction.parvw = c_executional_carrier;
				
				SELECT vendor.sap_vendor_id
				INTO o_sap_vendor_id
				FROM ulgnsap.vehn_vehiclemasterdata_vehic_1 pf_master
					INNER JOIN ulgnsap.vehn_vehiclepartnerfunctions partnerfunction ON (partnerfunction.hjid = pf_master.child_vehn_vehiclepartnerfun_0)
					INNER JOIN tlgns_sap_vendor vendor on (partnerfunction.iparnr = vendor.sap_ifc_id)
					INNER JOIN tlgnc_carrier carrier on (carrier.sap_vendor_id = vendor.sap_vendor_id)
				WHERE pf_master.parent_vehn_vehiclemasterdat_0 = i_master_hjid
					AND partnerfunction.parvw = c_executional_carrier
					AND vendor.vn_is_exec_carrier = 1;
				
				RETURN o_sap_vendor_id;
				
				EXCEPTION
					WHEN no_data_found THEN
						set_error('VEHN: get_sap_vendor_id', SQLCODE, SQLERRM, 'No executional carrier in logon DB for SAP Vendor ID = ' || l_sap_ifc_id || '.');
						RAISE;
					WHEN others THEN
						set_error('VEHN: get_sap_vendor_id', SQLCODE, SQLERRM, 'Unknown Exception.');
						RAISE;
			END;
		END IF;
	END;
	
	-- Returns the sap_vehicle_type_cd of the vehicle if it is present, otherwise returns null.
	FUNCTION get_sap_vehicle_type(i_master_hjid IN NUMBER)
	RETURN VARCHAR2 IS
		o_get_sap_vehicle_type VARCHAR2(30) := NULL;
	
	l_sap_vehicle_type_valid NUMBER(1) := 1;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				SELECT vehicleclassification.atwrt
				INTO o_get_sap_vehicle_type
				FROM ulgnsap.vehn_vehiclemasterdata_vehic_3 vc_master
					INNER JOIN ulgnsap.vehn_vehicleclassification vehicleclassification ON (vehicleclassification.hjid = vc_master.child_vehn_vehicleclassifica_0)
				WHERE vc_master.parent_vehn_vehiclemasterdat_0 = i_master_hjid
					AND vehicleclassification.atnam = c_vehicle_type;
				
				IF (is_sap_vehicle_type_valid(o_get_sap_vehicle_type) <> 1) THEN
					l_sap_vehicle_type_valid := 0;
					o_get_sap_vehicle_type := NULL;
				END IF;
				
				RETURN o_get_sap_vehicle_type;
				
				EXCEPTION
					WHEN no_data_found THEN
						set_error('VEHN: get_sap_vehicle_type', SQLCODE, SQLERRM, 'No SAP vehicle type found in request.');
						RAISE;
					WHEN others THEN
						set_error('VEHN: get_sap_vehicle_type', SQLCODE, SQLERRM, 'Unknown Exception.');
						RAISE;
			END;
		END IF;
	END;
	
	-- Returns 1 if the i_sap_vehicle_type_cd is valid as tlgnr_sap_vehicle_type_bs.sap_vehicle_type_cd, otherwise returns 0.
	FUNCTION is_sap_vehicle_type_valid(i_sap_vehicle_type_cd IN VARCHAR2)
	RETURN NUMBER IS
		o_is_sap_vehicle_type_valid NUMBER(1) := 0;
	
	l_sap_vehicle_type_cd VARCHAR2(30) := NULL;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				SELECT sap_vehicle_type_cd
				INTO l_sap_vehicle_type_cd
				FROM tlgnr_sap_vehicle_type_bs vehicle_type
				WHERE sap_vehicle_type_cd = i_sap_vehicle_type_cd;
				
				o_is_sap_vehicle_type_valid := 1;
				RETURN o_is_sap_vehicle_type_valid;
				
				EXCEPTION
					WHEN no_data_found THEN
						set_error('VEHN: is_sap_vehicle_type_valid', SQLCODE, SQLERRM, 'Not valid sap vehicle type for sap_vehicle_type_cd = ' || i_sap_vehicle_type_cd || '.');
						RAISE;
					WHEN others THEN
						set_error('VEHN: is_sap_vehicle_type_valid', SQLCODE, SQLERRM, 'Unknown Exception for sap_vehicle_type_cd = ' || i_sap_vehicle_type_cd || '.');
						RAISE;
			END;
		END IF;
	END;
	
	-- Returns the sap_vehicle_type_cd of the vehicle if it is present, otherwise returns null.
	FUNCTION get_vehicle_characteristic(i_vehicle_characteristic IN VARCHAR2) 
	RETURN NUMBER IS
		o_vehicle_characteristic NUMBER(1) := NULL;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				
				IF (i_vehicle_characteristic = c_veh_char_individual) THEN
					o_vehicle_characteristic := c_veh_char_individual_value;
				END IF;
				
				IF (i_vehicle_characteristic = c_veh_char_generic) THEN
					o_vehicle_characteristic := c_veh_char_generic_value;
				END IF;
				
				IF (o_vehicle_characteristic IS NULL) THEN
					set_error('VEHN: get_vehicle_characteristic', NULL, NULL, 'Not valid sap vehicle characteristic = ' || i_vehicle_characteristic || '.');
				END IF;
				
				RETURN o_vehicle_characteristic;
				
			END;
		END IF;
	END;
	
	-- Returns tlgnr_sap_vehicle_type_bs.logon_vehicle_type_cd, which has the incoming sap-vehicle-type if it exists, otherwise returns null.
	FUNCTION get_logon_vehicle_type(i_sap_vehicle_type_cd IN VARCHAR2)
	RETURN NUMBER IS
		o_get_logon_vehicle_type NUMBER(2) := NULL;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				SELECT logon_vehicle_type_cd
				INTO o_get_logon_vehicle_type
				FROM tlgnr_sap_vehicle_type_bs vehicle_type
				WHERE sap_vehicle_type_cd = i_sap_vehicle_type_cd;
				
				RETURN o_get_logon_vehicle_type;
				
				EXCEPTION
					WHEN no_data_found THEN
						set_error('VEHN: get_logon_vehicle_type', SQLCODE, SQLERRM, 'No LOGON vehicle type found for sap_vehicle_type_cd = ' || i_sap_vehicle_type_cd || '.');
						RAISE;
					WHEN others THEN
						set_error('VEHN: get_logon_vehicle_type', SQLCODE, SQLERRM, 'Unknown Exception for sap_vehicle_type_cd = ' || i_sap_vehicle_type_cd || '.');
						RAISE;
			END;
		END IF;
	END;
	
	-- Returns 1 if there is no truck is exiting with the same SAP_NO and different plate number, returns 0 otherwise.
	FUNCTION is_vehicle_plateno_ok(i_plate_number IN VARCHAR2, i_sap_truck_no IN VARCHAR2, i_vehicle_characteristic IN VARCHAR2) 
	RETURN NUMBER IS
		o_is_truck_ok NUMBER(1) := 0;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				IF (i_vehicle_characteristic = c_veh_char_individual) THEN
					IF (i_plate_number IS NULL) THEN
						set_error('VEHN: is_vehicle_plateno_ok', null, null, 'Individual vehicle must have a plate number.');
					ELSE
						o_is_truck_ok := 1;
					END IF;
				ELSE
					o_is_truck_ok := 1;
				END IF;
				
				RETURN o_is_truck_ok;
			END;
		END IF;
	END;
	
	-- Returns the truck's SAP_NO if its single and present, returns null otherwise.
	FUNCTION get_vehicle_truck_eqp_no(i_master_hjid IN VARCHAR2) 
	RETURN VARCHAR2 IS
	o_get_vehicle_truck_eqp_no VARCHAR2(18) := NULL;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				SELECT vehicletrailer.eqvon
				INTO o_get_vehicle_truck_eqp_no
				FROM ulgnsap.vehn_vehiclemasterdata_vehic_2 vt_master
					INNER JOIN ulgnsap.vehn_vehicletrailer vehicletrailer ON (vehicletrailer.hjid = vt_master.child_vehn_vehicletrailer_id)
				WHERE vt_master.parent_vehn_vehiclemasterdat_0 = i_master_hjid
					AND vehicletrailer.status = c_vehicle_trailer_attached;
				
				RETURN o_get_vehicle_truck_eqp_no;
				
				EXCEPTION
					WHEN no_data_found THEN
						set_info('VEHN: get_vehicle_truck_eqp_no', 'Trailer is not attached to any truck, ''TLGNS_SAP_VEHICLE.SAP_VEHICLE_TRUCK_EQP_NO'' will be null after succesful import. hjid = ' || i_master_hjid);
						RETURN NULL;
					WHEN too_many_rows THEN
						set_info('VEHN: get_vehicle_truck_eqp_no', 'Trailer is attached to multiple active trucks, ''TLGNS_SAP_VEHICLE.SAP_VEHICLE_TRUCK_EQP_NO'' will be null after succesful import. hjid was = ' || i_master_hjid);
						RETURN NULL;
					WHEN others THEN
						set_error('VEHN: get_vehicle_truck_eqp_no', SQLCODE, SQLERRM, 'Unknown Exception. hjid was = ' || i_master_hjid);
						RAISE;
			END;
		END IF;
	END;
	
	-- Returns the trailer's SAP_NO if its single and present, returns null otherwise.
	FUNCTION get_vehicle_trailer_eqp_no(i_master_hjid IN VARCHAR2) 
	RETURN VARCHAR2 IS
	o_get_vehicle_trailer_eqp_no VARCHAR2(18) := NULL;
	
	BEGIN
		IF (g_err_raised = 1) THEN
			RETURN NULL;
		ELSE
			BEGIN
				SELECT vehicletrailer.eqnach
				INTO o_get_vehicle_trailer_eqp_no
				FROM ulgnsap.vehn_vehiclemasterdata_vehic_2 vt_master
					INNER JOIN ulgnsap.vehn_vehicletrailer vehicletrailer ON (vehicletrailer.hjid = vt_master.child_vehn_vehicletrailer_id)
				WHERE vt_master.parent_vehn_vehiclemasterdat_0 = i_master_hjid
					AND vehicletrailer.status = c_vehicle_trailer_attached;
				
				RETURN o_get_vehicle_trailer_eqp_no;
				
				EXCEPTION
					WHEN no_data_found THEN
						set_info('VEHN: get_vehicle_truck_eqp_no', 'Trailer did not contain its own equipment number in vehn_vehicletrailer.eqnach, ''TLGNS_SAP_VEHICLE.SAP_VEHICLE_TRAILER_EQP_NO'' will be null after succesful import.');
						RETURN NULL;
					WHEN too_many_rows THEN
						set_info('VEHN: get_vehicle_trailer_eqp_no', 'Trailer is is attached to multiple active trucks in vehn_vehicletrailer.eqnach, ''TLGNS_SAP_VEHICLE.SAP_VEHICLE_TRAILER_EQP_NO'' will be null after succesful import.');
						RETURN NULL;
					WHEN others THEN
						set_error('VEHN: get_vehicle_trailer_eqp_no', SQLCODE, SQLERRM, 'Unknown Exception.');
						RAISE;
			END;
		END IF;
	END;
	
	-- Logs the error for the processed vehicle. 
	PROCEDURE handle_error(var_sqlerrm IN VARCHAR2, var_sapimpimporteddatelement IN VARCHAR2, i_notif_hjid IN VARCHAR2) AS
	BEGIN
		ROLLBACK;
		set_info('VEHN: load_tlgns_sap_vehicle', 'ROLLBACK on SAP vehicle. hjid = ' || i_notif_hjid);
		-- if record cannot be imported for certain period of time then abort
		IF (EXTRACT(DAY FROM systimestamp - var_sapimpimporteddatelement) > g_days_to_import) THEN
			BEGIN
				-- update controlling attributes
				UPDATE ulgnsap.vehn_vehiclenotification
				SET sapimpstatuscd = c_failed_abandoned,
					sapimperrmsg = COALESCE(g_err_msg, var_sqlerrm),
					sapimppropagateddatelement = systimestamp
				WHERE hjid = i_notif_hjid;
				
				set_info('VEHN: load_tlgns_sap_vehicle', 'SAP vehicle has been abandoned. Days for successful import = ' || g_days_to_import);
			END;
		ELSE
			BEGIN
				-- update controlling attributes
				UPDATE ulgnsap.vehn_vehiclenotification
				SET sapimperrmsg = COALESCE(g_err_msg, var_sqlerrm),
					sapimppropagateddatelement = systimestamp
				WHERE hjid = i_notif_hjid;
			END;
		END IF;
		
		set_info('VEHN: load_tlgns_sap_vehicle', 'SAP vehicle load/update was skipped due errors. hjid = ' || i_notif_hjid);
		COMMIT;
	
	END handle_error;
	
	-- Called when an error pops up to prevent further processing
	PROCEDURE set_error(var_source IN VARCHAR2, var_sqlcode IN NUMBER, var_sqlerrm IN VARCHAR2, var_text IN VARCHAR2) AS
	
	BEGIN
		IF (g_err_raised <> 1) THEN 
			g_err_raised := 1;
			g_err_msg := var_text;
			LOGON_LOG.ERROR(var_source, var_sqlcode, var_sqlerrm, var_text);
		END IF;
	END set_error;
	
	-- Adds the info to output
	PROCEDURE set_info(var_source IN VARCHAR2, var_text IN VARCHAR2) AS
	
	BEGIN	
		LOGON_LOG.INFO(var_source, var_text);
	END set_info;
	
	-- !!! Only used in development mode !!!
	-- for debugging from SQL Developer set c_runned_from_sqldeveloper constant NUMBER := 1;
	PROCEDURE set_debug(var_text IN VARCHAR2) AS
	
	BEGIN	
		LOGON_LOG.DEBUG('VEHN: load_tlgns_sap_vehicle', var_text);
	END set_debug;
	
END sapimport_vehn;
/

SHOW ERRORS;

--------------------------------------------------------------------------------
-- END OF THE SCRIPT
--------------------------------------------------------------------------------