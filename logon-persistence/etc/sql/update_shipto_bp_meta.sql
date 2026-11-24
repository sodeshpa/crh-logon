--
--  Author : AdNovum Informatik AG
--

----------------------------------------------------
-- Update cem & agg valid until dates for shiptos --
----------------------------------------------------

create or replace procedure update_shipto_bp_meta(i_min_loglevel IN VARCHAR2) is
	l_log_text tlgnc_log.log_text%type;
begin
    logon_log.g_min_loglevel := i_min_loglevel;
	for sap_bp_to_update in (
		select * from (
		    SELECT
		   		sap_bp_id,
			   	cem_valid_until_dat as old_cem_valid_until_dat,
			   	agg_valid_until_dat as old_agg_valid_until_dat,
			   	MAX(new_cem_valid_until_dat) AS new_cem_valid_until_dat,
			   	MAX(new_agg_valid_until_dat) AS new_agg_valid_until_dat
			FROM
			   (
				   SELECT
					   sap_bp.sap_bp_id,
					   sap_bp.cem_valid_until_dat as cem_valid_until_dat,
					   sap_bp.agg_valid_until_dat AS agg_valid_until_dat,
					   CASE
						   WHEN sap_material.material_is_cem = 1 THEN
							   sap_contract.valid_until_dat
						   ELSE
							   null
						   END AS new_cem_valid_until_dat,
					   CASE
						   WHEN sap_material.material_is_agg = 1 THEN
							   sap_contract.valid_until_dat
						   ELSE
							   null
						   END AS new_agg_valid_until_dat
				   FROM
					   tlgns_sap_bp sap_bp
						   JOIN tlgns_sap_contract sap_contract ON ( sap_contract.shipto_sap_bp_id = sap_bp.sap_bp_id )
						   JOIN tlgns_sap_contract_item sap_contract_item ON ( sap_contract_item.sap_contract_id = sap_contract.sap_contract_id )
						   JOIN tlgns_sap_material sap_material ON ( sap_material.sap_material_id = sap_contract_item.sap_material_id )
				   WHERE
					   ( sap_bp.bp_is_shipto = 1
						   AND sap_contract.valid_until_ignore_flag = 0
						   AND sap_contract.valid_until_dat IS NOT NULL
						   AND ( sap_material.material_is_agg = 1 OR sap_material.material_is_cem = 1 )
						)
			   )
			GROUP BY sap_bp_id, cem_valid_until_dat, agg_valid_until_dat)
		where
			COALESCE(old_cem_valid_until_dat, to_date('1970-08-02', 'YYYY-MM-DD'))<>COALESCE(new_cem_valid_until_dat, to_date('1970-08-02', 'YYYY-MM-DD')) or
			COALESCE(old_agg_valid_until_dat, to_date('1970-08-02', 'YYYY-MM-DD'))<>COALESCE(new_agg_valid_until_dat, to_date('1970-08-02', 'YYYY-MM-DD'))
	) loop
		update tlgns_sap_bp
		set
			cem_valid_until_dat = sap_bp_to_update.new_cem_valid_until_dat,
			agg_valid_until_dat = sap_bp_to_update.new_agg_valid_until_dat,
			ctl_tcn=ctl_tcn + 1,
			ctl_mod_uid = 1,
			ctl_mod_ts = systimestamp
		where
			SAP_BP_ID = sap_bp_to_update.SAP_BP_ID;
		if sql%rowcount > 0 then
			l_log_text := 'Sap bp id: ' || sap_bp_to_update.SAP_BP_ID;
			l_log_text := l_log_text || ' | ' || 'Rows updated: ' || sql%rowcount;
			logon_log.info('update shipto bp meta / update cem and agg valid until dates for shipto sap bps', l_log_text);
		end if;
	end loop;
end;
/

show errors;