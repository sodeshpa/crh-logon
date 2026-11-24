--------------------------------------------------------------------------------
--
-- PROJECT      : $Project: LOGON $
--
-- AUTHOR       : $Author: stich $
-- CREATION DATE: $Date$
-- DESCRIPTION  : Material Plant Norms Notification import package.
--

CREATE OR REPLACE PACKAGE sapimport_mnn AS

	PROCEDURE main(i_min_loglevel IN VARCHAR2, i_days_to_import IN NUMBER);
	FUNCTION get_sap_material_id(i_sap_material_no IN VARCHAR2) RETURN VARCHAR2;
	FUNCTION get_plant_cd(i_plant_cd IN VARCHAR2) RETURN VARCHAR2;
	FUNCTION get_sap_material_norm_cd(i_sap_mat_plant_norm_cd IN VARCHAR2) RETURN VARCHAR2;
	FUNCTION get_sap_language_cd(i_sap_language_cd IN VARCHAR2) RETURN VARCHAR2;
	FUNCTION get_sorg_cd(i_sorg_cd IN VARCHAR2) RETURN VARCHAR2;
	FUNCTION get_sap_mat_plant_norm_id(i_sap_material_id IN VARCHAR2, i_plant_cd IN VARCHAR2, i_sap_material_norm_cd IN VARCHAR2, i_sap_language_cd IN VARCHAR2, i_sorg_cd IN VARCHAR2, i_dest_country IN VARCHAR2) RETURN NUMBER;
	PROCEDURE handle_error(i_sapimpimporteddatelement IN VARCHAR2, i_notif_hjid IN VARCHAR2);
	FUNCTION get_hjid_info(i_notif_hjid IN VARCHAR2, i_data_hjid IN VARCHAR2) RETURN VARCHAR2;

END sapimport_mnn;
/

CREATE OR REPLACE PACKAGE BODY sapimport_mnn AS

	----------------------------------------------------------------
	----------------------------------------------------------------
	-----------MATERIAL-PLANT-NORM-NOTIFICATION-IMPORT-------------
	----------------------------------------------------------------
	----------------------------------------------------------------
	
			-- CONSTANTS --
	c_import_name						constant	VARCHAR(30)		:= 'sapimport_mnn';
	
			-- TRACING --
	c_info_package_name					constant	VARCHAR(5)		:= 'MNN: ';
	c_info_package_main_log				constant	VARCHAR(40)		:= 'MNN: load_tlgns_sap_mat_plant_norm';
	c_info_notif_hjid					constant	VARCHAR(40)		:= 'mnn_materialnormnotification.hjid';
	c_info_data_hjid					constant	VARCHAR(40)		:= 'mnn_materialnormdata.hjid';
	
		-- staus vars
	c_imported_from_sap_status			constant	NUMBER := 0;								-- not imported
	c_imported_to_logon_status			constant	NUMBER := 20;								-- imported successfully
	c_skipped_status					constant	NUMBER := 2;								-- skipped newer available
	c_failed_abandoned					constant	NUMBER := 9;								-- failed abandoned
	
		-- Action constants --
	c_action_insert						constant	VARCHAR(1)		:= 'I';
	c_action_update						constant	VARCHAR(1)		:= 'U';
	c_action_delete						constant	VARCHAR(1)		:= 'D';
	
	c_show_logo_flag_true				constant	VARCHAR(1)		:= 'X';
		
			--GLOBAL VARIABLES --
	g_days_to_import								NUMBER := 0;
	g_err_msg										VARCHAR2(200);
			
			-- CUSTOM EXCEPTIONS --
	incorrect_action								EXCEPTION;
	already_logged									EXCEPTION;
	
	PROCEDURE main(i_min_loglevel IN VARCHAR2, i_days_to_import IN NUMBER) IS
		
		-- control vars for cursor status
		l_ins_total 								NUMBER := 0;
		l_upd_total 								NUMBER := 0;
		
		l_hjid_info									VARCHAR2(200);
		
		-- local status variables
		l_sapimpstatuscd							NUMBER := NULL;
		l_sap_mat_plant_norm_id						NUMBER := NULL;
		l_sap_material_no						VARCHAR2(18) := NULL;
		l_sap_material_id							VARCHAR2(12) := NULL;
		l_plant_cd									VARCHAR2(4) := NULL;
		l_sap_material_norm_cd						VARCHAR2(10) := NULL;
		l_sap_language_cd							VARCHAR2(2) := NULL;
		l_dest_country								VARCHAR2(3) := NULL;
		l_sorg_cd									VARCHAR2(4) := NULL;
		l_marked_for_deletion						NUMBER := NULL;
		l_show_logo_flag							NUMBER := NULL;
			
		-- main query to select not imported records, starting with the newest
		CURSOR cur_sap_material_norm IS
			SELECT 
				notif.hjid notif_hjid,
				normdata.hjid data_hjid,
				LTRIM(normdata.matnr,'0') matnr,
				normdata.werks,
				normdata.znorm,
				normdata.sprasiso,
				normdata.vkorg,
				normdata.zlogo,
				normdata.zaction,
				normdata.zcode1,
				normdata.zcode2,
				normdata.text1,
				normdata.text2,
				normdata.text3,
				normdata.land1,
				normdata.zidlogo,
				normdata.zarchivdocid,
				notif.sapimpimporteddatelement
			FROM ulgnsap.mnn_materialnormnotification notif
				INNER JOIN ulgnsap.mnn_materialnormnotification_0 notif_data ON (notif_data.parent_mnn_materialnormnotif_0 = notif.hjid)
				INNER JOIN ulgnsap.mnn_materialnormdata normdata ON (normdata.hjid = notif_data.child_mnn_materialnormdata_id)
			WHERE notif.sapimpstatuscd = c_imported_from_sap_status
			ORDER BY notif.sapimpimporteddatelement;
	
	BEGIN
		
		g_days_to_import := i_days_to_import;
		
		-- set the time of the start of the import and logging parameters
		LOGON_LOG.start_import(c_import_name);
		LOGON_LOG.g_min_loglevel := i_min_loglevel;
		
		--LOGON_LOG.g_min_loglevel := 'DEBUG';
		
		LOGON_LOG.INFO(c_info_package_main_log, 'START loading data into ''TLGNS_SAP_MAT_PLANT_NORM'' table');
		
		FOR cur_record IN cur_sap_material_norm
		LOOP
			BEGIN
				
				-- 0. INIT LOCAL VARS
				g_err_msg := NULL;
				
				l_sap_mat_plant_norm_id:= NULL;
				l_sap_material_no := NULL;
				l_sap_material_id := NULL;
				l_plant_cd := NULL;
				l_sap_material_norm_cd := NULL;
				l_sap_language_cd := NULL;
				l_dest_country := NULL;
				l_sorg_cd := NULL;
				l_show_logo_flag := NULL;
				l_marked_for_deletion := NULL;
				
				l_hjid_info := get_hjid_info(cur_record.notif_hjid, cur_record.data_hjid);
							
				LOGON_LOG.INFO(c_info_package_main_log, ' * Checking material plant norm  material number = ' || cur_record.matnr || ' ... ('  || l_hjid_info || ')');
				
				-- 1. As the sapimpstatuscd is updated in the meanwhile, we have to check here if the currently pocessed notification was marked to be skipped already.
		        SELECT sapimpstatuscd 
		        INTO l_sapimpstatuscd 
		        FROM ulgnsap.mnn_materialnormnotification 
		        WHERE hjid = cur_record.notif_hjid;
		        
		        -- if contract was skipped there is no need to import
		        IF (l_sapimpstatuscd = c_skipped_status) THEN
		          LOGON_LOG.INFO(c_info_package_main_log, 'Skip because a newer version of the material norm was imported.');
		          GOTO next_mat_norm_record;
		        END IF;
				
				-- 2. Determinig the deletion flag, there is no real difference between 'U', 'I', NULL (empty), as we do not delete only set a flag.
				IF (cur_record.zaction IS NULL) THEN
					l_marked_for_deletion := 0;
					LOGON_LOG.INFO(c_info_package_main_log, 'Action is empty, meaning INSERT/UPDATE and marked_for_deletion = 0');
				ELSE
					CASE cur_record.zaction							
						WHEN c_action_insert THEN	
							l_marked_for_deletion := 0;
							LOGON_LOG.INFO(c_info_package_main_log, 'Action is ''I'', meaning INSERT/UPDATE and marked_for_deletion = 0');
						WHEN c_action_update THEN		
							l_marked_for_deletion := 0;
							LOGON_LOG.INFO(c_info_package_main_log, 'Action is ''U'', meaning INSERT/UPDATE and marked_for_deletion = 0');
						WHEN c_action_delete THEN
							l_marked_for_deletion := 1;
							LOGON_LOG.INFO(c_info_package_main_log, 'Action is ''D'', meaning INSERT/UPDATE and marked_for_deletion = 1');
						ELSE 
							RAISE incorrect_action;	
					END CASE;
				END IF;
				
				-- 3. Checking the FK field values if they are correct. Exceptions can happen only in this area.
				-- If any error occures in the meanwhile, we jump to the Exception block and log the error.
				
				l_sap_material_no := cur_record.matnr;
				LOGON_LOG.DEBUG(c_info_package_main_log, 'l_sap_material_no = ' || l_sap_material_no);
				
				l_sap_material_id := get_sap_material_id(l_sap_material_no);
				LOGON_LOG.DEBUG(c_info_package_main_log, 'l_sap_material_id = ' || l_sap_material_id);
				
				l_plant_cd := get_plant_cd(cur_record.werks);
				LOGON_LOG.DEBUG(c_info_package_main_log, 'l_plant_cd = ' || l_plant_cd);
				
				l_sap_material_norm_cd := get_sap_material_norm_cd(cur_record.znorm);
				LOGON_LOG.DEBUG(c_info_package_main_log, 'l_sap_material_norm_cd = ' || l_sap_material_norm_cd);
				
				l_sap_language_cd := get_sap_language_cd(cur_record.sprasiso);
				LOGON_LOG.DEBUG(c_info_package_main_log, 'l_sap_language_cd = ' || l_sap_language_cd);
				
				l_dest_country := cur_record.land1;
				LOGON_LOG.DEBUG(c_info_package_main_log, 'l_dest_country = ' || l_dest_country);
				
				IF (cur_record.vkorg IS NOT NULL) THEN
					l_sorg_cd := get_sorg_cd(cur_record.vkorg);
				END IF;
				
				LOGON_LOG.DEBUG(c_info_package_main_log, 'l_sorg_cd = ' || l_sorg_cd);
				
				IF (cur_record.zlogo = c_show_logo_flag_true) THEN
					l_show_logo_flag := 1;
				ELSE
					l_show_logo_flag := 0;
				END IF;
				
				-- PK ID to check if insert or update case is required
				l_sap_mat_plant_norm_id := get_sap_mat_plant_norm_id(l_sap_material_id, l_plant_cd, l_sap_material_norm_cd, l_sap_language_cd, l_sorg_cd, l_dest_country);
				
				-- 4. INSERT/UPDATE -> All preconditions were met, changing TLGNS_SAP_MAT_PLANT_NORM
				IF (l_sap_mat_plant_norm_id = -1) THEN
					LOGON_LOG.INFO(c_info_package_main_log, 'l_sap_mat_plant_norm_id = -1 (INSERT CASE)');
					
					SELECT slgns_sap_mat_plant_norm.nextval
					INTO l_sap_mat_plant_norm_id
					FROM dual;
	
					INSERT INTO TLGNS_SAP_MAT_PLANT_NORM (
						sap_mat_plant_norm_id,
						sap_material_id,
						plant_cd,
						sap_material_norm_cd,
						sap_language_cd,
						sorg_cd,
						show_logo_flag,
						zcode1,
						zcode2,
						text1,
						text2,
						text3,
						marked_for_deletion,
						dest_country,
						dn_logo_image_name,
						dn_logo_archivdocid,
						ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
					) VALUES (
						l_sap_mat_plant_norm_id,
						l_sap_material_id,
						l_plant_cd,
						l_sap_material_norm_cd,
						l_sap_language_cd,
						l_sorg_cd,
						l_show_logo_flag,
						cur_record.zcode1,
						cur_record.zcode2,
						cur_record.text1,
						cur_record.text2,
						cur_record.text3,
						l_marked_for_deletion,
						l_dest_country,
						cur_record.zidlogo,
						cur_record.zarchivdocid,
						1, systimestamp, 1, systimestamp, 0
					);
					l_ins_total := l_ins_total + 1;
					LOGON_LOG.DEBUG(c_info_package_main_log, '+ Succesful Insert');
					
				ELSE
					LOGON_LOG.INFO(c_info_package_main_log, 'l_sap_mat_plant_norm_id = ' || l_sap_mat_plant_norm_id || ' (UPDATE CASE)');
					
					UPDATE TLGNS_SAP_MAT_PLANT_NORM set 
						show_logo_flag = l_show_logo_flag,
						zcode1 = cur_record.zcode1,
						zcode2 = cur_record.zcode2,
						text1 = cur_record.text1,
						text2 = cur_record.text2,
						text3 = cur_record.text3,
						marked_for_deletion = l_marked_for_deletion,
						dest_country = l_dest_country,
						dn_logo_image_name = cur_record.zidlogo,
						dn_logo_archivdocid = cur_record.zarchivdocid,
						ctl_mod_uid = 1,
						ctl_mod_ts = systimestamp,
						ctl_tcn = ctl_tcn + 1
					where sap_mat_plant_norm_id = l_sap_mat_plant_norm_id;
					
					l_upd_total := l_upd_total + 1;
					LOGON_LOG.DEBUG(c_info_package_main_log, '+ Succesful Update');
					
				END IF;
				
				-- 5.1 update controlling attributes
				UPDATE ulgnsap.mnn_materialnormnotification
				SET sapimpstatuscd = c_imported_to_logon_status,
					sapimppropagateddatelement = systimestamp
				WHERE hjid = cur_record.notif_hjid;			
				
				-- 5.2 update older entries
				UPDATE ulgnsap.mnn_materialnormnotification notif_outer
				SET sapimpstatuscd = c_skipped_status,
					sapimppropagateddatelement = systimestamp
				WHERE notif_outer.sapimpstatuscd <> c_imported_to_logon_status
					AND notif_outer.sapimpimporteddatelement < cur_record.sapimpimporteddatelement
					AND notif_outer.hjid IN (
						SELECT notif.hjid
						FROM ulgnsap.mnn_materialnormnotification notif
							INNER JOIN ulgnsap.mnn_materialnormnotification_0 notif_data ON (notif_data.parent_mnn_materialnormnotif_0 = notif.hjid)
							INNER JOIN ulgnsap.mnn_materialnormdata normdata ON (normdata.hjid = notif_data.child_mnn_materialnormdata_id)
						WHERE normdata.matnr = l_sap_material_no
							AND normdata.werks = l_plant_cd
							AND normdata.znorm = l_sap_material_norm_cd
							AND normdata.sprasiso = l_sap_language_cd
							AND normdata.vkorg = l_sorg_cd
							AND ((l_dest_country is null and normdata.land1 is null) or normdata.land1 = l_dest_country)
					);
				COMMIT;
				
				LOGON_LOG.INFO(c_info_package_main_log, 'Material plant norm has been loaded/updated.');
				
				-- This is the point to jump on in case of records which were set to skipped after inserting/updating a newer notification for the same material norm.
				<<next_mat_norm_record>>
          		NULL;
          		
				-- 5. Exceptions block If any error occures during precondition checking, we jump to the Exception block and log the error.
				EXCEPTION  
					WHEN incorrect_action THEN
						g_err_msg := 'Invalid action. (mnn_materialnormdata.zaction) = ' || cur_record.zaction || '. This field can only have the following values: ''I'', ''D'', ''U'', ''''.';
						LOGON_LOG.ERROR(c_info_package_main_log, '', '', g_err_msg);
						handle_error(cur_record.sapimpimporteddatelement, cur_record.notif_hjid);
					WHEN already_logged THEN
						-- By raising "already_logged EXCEPTION" in all the functions will force the code to jump here directly if any error occurred and skip the status updates of the succesful cases.
						-- Error is already loggeg here but we still need to update the notification row with the cause of the error. 
						handle_error(cur_record.sapimpimporteddatelement, cur_record.notif_hjid);
					WHEN others THEN
						g_err_msg := 'Unexpected exception.';
						LOGON_LOG.ERROR(c_info_package_main_log, SQLCODE, SQLERRM, g_err_msg);
						handle_error(cur_record.sapimpimporteddatelement, cur_record.notif_hjid);
			
			
          	
			END;
		END LOOP;
		LOGON_LOG.INFO(c_info_package_main_log, 'FINISH! Updated ' || l_upd_total || ' and inserted ' || l_ins_total || ' records into/from ''TLGNS_SAP_MAT_PLANT_NORM'' table!');
		
    -- set the run number and time of the end of the import
    LOGON_LOG.finish_import(c_import_name);

	END;
	
	-- Checks if the material exists withthe given IFC Number.  Throws exception if it does not exist or if the DB is not consistent.
	FUNCTION get_sap_material_id(i_sap_material_no IN VARCHAR2) 
	RETURN VARCHAR2 IS
	o_get_sap_material_id VARCHAR2(12) := NULL;
	
	BEGIN
		SELECT sap_material_id
		INTO o_get_sap_material_id
		FROM tlgns_sap_material
		WHERE sap_material_no = i_sap_material_no;
		
		RETURN o_get_sap_material_id;
		
		EXCEPTION
			WHEN no_data_found THEN
				g_err_msg := 'Invalid Material IFC Number. No rows were found in table ''TLGNS_SAP_MATERIAL'' where TLGNS_SAP_MATERIAL.SAP_MATERIAL_IFC_NO = ' || i_sap_material_no;
				LOGON_LOG.ERROR(c_info_package_name || 'get_sap_material_id', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
			WHEN too_many_rows THEN
				g_err_msg := 'Database consistency error, more then one material have the same the Material IFC Number. Too many rows were found in table ''TLGNS_SAP_MATERIAL'' where TLGNS_SAP_MATERIAL.SAP_MATERIAL_IFC_NO = ' || i_sap_material_no;
				LOGON_LOG.ERROR(c_info_package_name || 'get_sap_material_id', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
			WHEN others THEN
				g_err_msg := 'Unexpected exception in get_sap_material_id()';
				LOGON_LOG.ERROR(c_info_package_name || 'get_sap_material_id', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
	END;
	
	-- Checks if the plant exists. Throws exception if it does not exist.
	FUNCTION get_plant_cd(i_plant_cd IN VARCHAR2) 
	RETURN VARCHAR2 IS
	o_get_plant_cd VARCHAR2(4) := NULL;
	
	BEGIN
		SELECT plant_cd
		INTO o_get_plant_cd
		FROM tlgnr_plant_bs
		WHERE plant_cd = i_plant_cd;
		
		RETURN o_get_plant_cd;
		
		EXCEPTION
			WHEN no_data_found THEN
				g_err_msg := 'Invalid Plant code. No rows were found in table ''TLGNR_PLANT_BS'' where TLGNR_PLANT_BS.PLANT_CD = ' || i_plant_cd;
				LOGON_LOG.ERROR(c_info_package_name || 'get_plant_cd', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
			WHEN others THEN
				g_err_msg := 'Unexpected exception in get_plant_cd()';
				LOGON_LOG.ERROR(c_info_package_name || 'get_plant_cd', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
	END;
	
	-- Checks if the material norm type exists. Throws exception if it does not exist.
	FUNCTION get_sap_material_norm_cd(i_sap_mat_plant_norm_cd IN VARCHAR2) 
	RETURN VARCHAR2 IS
	o_get_sap_material_norm_cd VARCHAR(10) := NULL;
	
	BEGIN
		SELECT sap_material_norm_cd
		INTO o_get_sap_material_norm_cd
		FROM tlgnr_sap_material_norm_bs
		WHERE sap_material_norm_cd = i_sap_mat_plant_norm_cd;
		
		RETURN o_get_sap_material_norm_cd;
		
		EXCEPTION
			WHEN no_data_found THEN
				g_err_msg := 'Invalid material norm code. No rows were found in table ''TLGNR_SAP_MATERIAL_NORM_BS'' where TLGNR_SAP_MATERIAL_NORM_BS.SAP_MATERIAL_NORM_CD = ' || i_sap_mat_plant_norm_cd;
				LOGON_LOG.ERROR(c_info_package_name || 'get_sap_material_norm_cd', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
			WHEN others THEN
				g_err_msg := 'Unexpected exception in get_sap_material_norm_cd()';
				LOGON_LOG.ERROR(c_info_package_name || 'get_sap_material_norm_cd', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
	END;
	
	-- Checks if the SAP language exists. Throws exception if it does not exist.
	FUNCTION get_sap_language_cd(i_sap_language_cd IN VARCHAR2) 
	RETURN VARCHAR2 IS
	o_get_sap_language_cd VARCHAR(2) := NULL;
	
	BEGIN
		SELECT sap_language_cd
		INTO o_get_sap_language_cd
		FROM tlgnr_sap_language_bs
		WHERE sap_language_cd = i_sap_language_cd;
		
		RETURN o_get_sap_language_cd;
		
		EXCEPTION
			WHEN no_data_found THEN
				g_err_msg := 'Invalid language code. No rows were found in table ''TLGNR_SAP_LANGUAGE_BS'' where TLGNR_SAP_LANGUAGE_BS.SAP_LANGUAGE_CD = ' || i_sap_language_cd;
				LOGON_LOG.ERROR(c_info_package_name || 'get_sap_language_cd', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
			WHEN others THEN
				g_err_msg := 'Unexpected exception in get_sap_language_cd()';
				LOGON_LOG.ERROR(c_info_package_name || 'get_sap_language_cd', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
	END;
	
	-- Checks if the Sales Organisation exists. Throws exception if it does not exist.
	FUNCTION get_sorg_cd(i_sorg_cd IN VARCHAR2) 
	RETURN VARCHAR2 IS
	o_get_sorg_cd VARCHAR(4) := NULL;
	
	BEGIN
		SELECT sorg_cd
		INTO o_get_sorg_cd
		FROM tlgnr_sorg_bs
		WHERE sorg_cd = i_sorg_cd;
		
		RETURN o_get_sorg_cd;
		
		EXCEPTION
			WHEN no_data_found THEN
				g_err_msg := 'Invalid Sales Organisation code. No rows were found in table ''TLGNR_SORG_BS'' where TLGNR_SORG_BS.SORG_CD = ' || i_sorg_cd;
				LOGON_LOG.ERROR(c_info_package_name || 'get_sorg_cd', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
			WHEN others THEN
				g_err_msg := 'Unexpected exception in get_sorg_cd()';
				LOGON_LOG.ERROR(c_info_package_name || 'get_sorg_cd', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
	END;
	
	-- This function determines, if the unique norm is existing or not. Returns -1 if the norm was not found.
	FUNCTION get_sap_mat_plant_norm_id(i_sap_material_id IN VARCHAR2, i_plant_cd IN VARCHAR2, i_sap_material_norm_cd IN VARCHAR2, i_sap_language_cd IN VARCHAR2, i_sorg_cd IN VARCHAR2, i_dest_country IN VARCHAR2) 
	RETURN NUMBER IS
	o_get_sap_mat_plant_norm_id NUMBER := NULL;
	
	BEGIN
		SELECT sap_mat_plant_norm_id
		INTO o_get_sap_mat_plant_norm_id
		FROM tlgns_sap_mat_plant_norm
		WHERE sap_material_id = i_sap_material_id
			AND plant_cd = i_plant_cd
			AND sap_material_norm_cd = i_sap_material_norm_cd
			AND sap_language_cd = i_sap_language_cd
			AND sorg_cd = i_sorg_cd
			AND ((i_dest_country is null and dest_country is null) or dest_country = i_dest_country);
		
		RETURN o_get_sap_mat_plant_norm_id;
		
		EXCEPTION
			WHEN no_data_found THEN
				o_get_sap_mat_plant_norm_id := -1;
				RETURN o_get_sap_mat_plant_norm_id;
			WHEN others THEN
				g_err_msg := 'Unexpected exception in get_sap_mat_plant_norm_id()';
				LOGON_LOG.ERROR(c_info_package_name || 'get_sap_mat_plant_norm_id', SQLCODE, SQLERRM, g_err_msg);
				RAISE already_logged;
	END;

	-- Responsible to update the status variables of the failed notification
	PROCEDURE handle_error(i_sapimpimporteddatelement IN VARCHAR2, i_notif_hjid IN VARCHAR2) AS
	BEGIN

		IF (EXTRACT(DAY FROM systimestamp - i_sapimpimporteddatelement) > g_days_to_import) THEN
			-- If record cannot be imported for certain period of time then it gets aborted and gets the aborted status, will not ever be processed again.  
			BEGIN
				-- update controlling attributes
				UPDATE ulgnsap.mnn_materialnormnotification
				SET sapimpstatuscd = c_failed_abandoned,
					sapimperrmsg = g_err_msg,
					sapimppropagateddatelement = systimestamp
				WHERE hjid = i_notif_hjid;
				
				LOGON_LOG.INFO(c_info_package_main_log, 'SAP material plant norm has been abandoned. Days for successful import = ' || g_days_to_import);
			END;
		ELSE
			-- If the record cannot be imported the reason of the failure is added as an error message.
			-- As the reason might be missing reference data, it will be retried later on('sapimpstatuscd' is not updated).
			BEGIN
				-- update controlling attributes
				UPDATE ulgnsap.mnn_materialnormnotification
				SET sapimperrmsg = g_err_msg,
					sapimppropagateddatelement = systimestamp
				WHERE hjid = i_notif_hjid;
			END;
		END IF;
		
		LOGON_LOG.INFO(c_info_package_main_log, 'SAP material plant norm load/update was skipped due errors.');
		COMMIT;
	
	END handle_error;
	
	-- Logging function
	FUNCTION get_hjid_info(i_notif_hjid IN VARCHAR2, i_data_hjid IN VARCHAR2)
	RETURN VARCHAR2 IS
	
	BEGIN
		RETURN c_info_notif_hjid || ' = ' || i_notif_hjid || ', ' || c_info_data_hjid || ' = ' || i_data_hjid;
	END get_hjid_info;

END sapimport_mnn;
/

SHOW ERRORS;
--------------------------------------------------------------------------------
-- END OF THE SCRIPT
--------------------------------------------------------------------------------