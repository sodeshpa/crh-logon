-- $Revision$
-- $Date$
--
-- based on ticket ADN71680, ADN72108

-- Based on the list of initial test/master data for the Andorra/South Africa tests
-- /share/doc/crh/logon_2_0_ebm/input/initialdata/20110622\ Customizing\ master\ data\ for\ ITC1.xlsx

prompt '---------- adza_initialdata/tlgnc_sarea.sql ----------'

set define off
set serveroutput on

-------------------------------------------------------------------------------
-- is_different_varchar2
-------------------------------------------------------------------------------
-- compares two varchars.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_varchar2 (
  i_a in varchar2,
  i_b in varchar2)
  return number is
  begin
    if ((i_a != i_b)
        or (i_a is null and i_b is not null)
        or (i_a is not null and i_b is null)) then
      return 1;
    end if;
    return 0;
  end;
/
show errors;

-------------------------------------------------------------------------------
-- is_different_number
-------------------------------------------------------------------------------
-- compares two numbers.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_number (
  i_a in number,
  i_b in number)
  return number is
  begin
    if ((i_a != i_b)
        or (i_a is null and i_b is not null)
        or (i_a is not null and i_b is null)) then
      return 1;
    end if;
    return 0;
  end;
/
show errors;

-------------------------------------------------------------------------------
-- upsert_sarea
-------------------------------------------------------------------------------
create or replace
procedure upsert_sarea (
  p_sorg_cd                         in varchar2,
  p_distribution_channel            in varchar2,
  p_division_cd                     in varchar2,
  p_sarea_display_longname          in varchar2,
  p_delnote_uses_ws_number_flag     in number,
  p_document_del_time_win_flag      in number,
  p_document_distance_flag          in number,
  p_document_comm_carrier_flag      in number,
  p_document_exec_carrier_flag      in number,
  p_cust_comm_flag                  in number,
  p_order_taking_clevel3_flag       in number,
  p_order_taking_clevel2_flag       in number,
  p_exact_qty_order_tolerance       in number,
  p_allow_planning_material_flag    in number,
  p_auto_payer_determ_flag          in number,
  p_cs_show_payment_mth_flag        in number,
  p_cs_price_type                   in number,
  p_cs_show_totals_text_flag        in number,
  p_cs_show_cust_tax_code_flag      in number,
  p_cs_enable_one_time_cust_flag    in number,
  p_dn_show_trailer_flag            in number,
  p_dn_show_seals_info_flag         in number,
  p_dn_show_silo_no_flag            in number,
  p_dn_show_plant_cycle_flag        in number,
  p_dn_show_fix_text                in number,
  p_dn_show_disp_driver_flag        in number,
  p_dn_show_carrier_crm_flag        in number,
  p_exp_show_total_txt_flag         in number,
  p_exp_show_legal_text_1_flag      in number,
  p_exp_show_legal_text_2_flag      in number,
  p_exp_show_issue_dat_loc_flag     in number,
  p_exp_show_ad_invoice_no_flag     in number,
  p_exp_show_ctrl_no_flag           in number,
  p_exp_mat_price_type              in number,
  p_cs_show_contract_curr_flag      in number,
  p_exp_show_zcode1_2_flag          in number,
  p_ctl_act_cd                      in number,
  p_enable_pricing_call_flag        in number,
  p_show_price_in_web_order_flag    in number,
  p_rail_return_sarea_flag          in number,
  p_force_to_sap_flag               in number,
  p_enable_demand_plan_flag         in number)
is
  v_exists_flag       number;   -- number of existing records of that key (0 or 1)
  v_existing_id       number;   -- ID of existing record of that key, if exist
  v_sorg_dist_div_cd  varchar2(16) := p_sorg_cd || '-' || p_distribution_channel || '-' || p_division_cd;
  c_default_show_delplant_flag constant number := 0;

begin
  dbms_output.enable(100000);

  dbms_output.put_line('Insert or Update tlgnc_sarea (' || v_sorg_dist_div_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

  select count(sa.sarea_id) into v_exists_flag
  from tlgnc_sarea sa
  where sa.sorg_cd = p_sorg_cd and
        sa.distribution_channel = p_distribution_channel and
        sa.division_cd = p_division_cd;

  if (v_exists_flag > 0) then
    select sa.sarea_id into v_existing_id
    from tlgnc_sarea sa
    where sa.sorg_cd = p_sorg_cd and
          sa.distribution_channel = p_distribution_channel and
          sa.division_cd = p_division_cd;

    if (p_ctl_act_cd != 1) then
      dbms_output.put_line ('NOT updating tlgnc_sarea with sarea_id = ' || v_existing_id || ' (' || v_sorg_dist_div_cd || ') because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
    else
      update tlgnc_sarea set
        /* sorg_cd, distribution_channel, division_cd: cannot update because these form the unique key */
        sarea_display_longname          = p_sarea_display_longname,
        delnote_uses_ws_number_flag     = p_delnote_uses_ws_number_flag,
        document_del_time_window_flag   = p_document_del_time_win_flag,
        document_distance_flag          = p_document_distance_flag,
        document_comm_carrier_flag      = p_document_comm_carrier_flag,
        document_exec_carrier_flag      = p_document_exec_carrier_flag,
        cust_comm_flag                  = p_cust_comm_flag,
        order_taking_clevel3_flag       = p_order_taking_clevel3_flag,
        order_taking_clevel2_flag       = p_order_taking_clevel2_flag,
        exact_quantity_order_tolerance  = p_exact_qty_order_tolerance,
        allow_planning_material_flag    = p_allow_planning_material_flag,
        auto_payer_determ_flag          = p_auto_payer_determ_flag,
        cs_show_payment_mth_flag        = p_cs_show_payment_mth_flag,
        cs_price_type                   = p_cs_price_type,
        cs_show_totals_text_flag        = p_cs_show_totals_text_flag,
        cs_show_cust_tax_code_flag      = p_cs_show_cust_tax_code_flag,
        cs_enable_one_time_cust_flag    = p_cs_enable_one_time_cust_flag,
        dn_show_trailer_flag            = p_dn_show_trailer_flag,
        dn_show_seals_info_flag         = p_dn_show_seals_info_flag,
        dn_show_silo_no_flag            = p_dn_show_silo_no_flag,
        dn_show_plant_cycle_flag        = p_dn_show_plant_cycle_flag,
        dn_show_fix_text                = p_dn_show_fix_text,
        dn_show_dispatcher_driver_flag  = p_dn_show_disp_driver_flag,
        dn_show_carrier_crm_flag        = p_dn_show_carrier_crm_flag,
        exp_show_total_txt_flag         = p_exp_show_total_txt_flag,
        exp_show_legal_text_1_flag      = p_exp_show_legal_text_1_flag,
        exp_show_legal_text_2_flag      = p_exp_show_legal_text_2_flag,
        exp_show_issue_dat_loc_flag     = p_exp_show_issue_dat_loc_flag,
        exp_show_ad_invoice_no_flag     = p_exp_show_ad_invoice_no_flag,
        exp_show_ctrl_no_flag           = p_exp_show_ctrl_no_flag,
        exp_mat_price_type              = p_exp_mat_price_type,
        cs_show_contract_currency_flag  = p_cs_show_contract_curr_flag,
        exp_show_zcode1_2_flag          = p_exp_show_zcode1_2_flag,
        enable_pricing_call_flag        = p_enable_pricing_call_flag,
        show_price_in_web_order_flag    = p_show_price_in_web_order_flag,
        rail_return_sarea_flag          = p_rail_return_sarea_flag,
        force_to_sap_flag               = p_force_to_sap_flag,
        enable_demand_planning_flag     = p_enable_demand_plan_flag,
        ctl_act_cd                      = p_ctl_act_cd,
        ctl_mod_uid                     = 1,
        ctl_mod_ts                      = systimestamp,
        ctl_tcn                         = ctl_tcn+1
      where
        sorg_cd = p_sorg_cd                                                                           and
        distribution_channel = p_distribution_channel                                                 and
        division_cd = p_division_cd                                                                   and
        ( 1 = is_different_varchar2(sarea_display_longname, p_sarea_display_longname)                 or
          1 = is_different_number(delnote_uses_ws_number_flag, p_delnote_uses_ws_number_flag)         or
          1 = is_different_number(document_del_time_window_flag, p_document_del_time_win_flag)        or
          1 = is_different_number(document_distance_flag, p_document_distance_flag)                   or
          1 = is_different_number(document_comm_carrier_flag, p_document_comm_carrier_flag)           or
          1 = is_different_number(document_exec_carrier_flag, p_document_exec_carrier_flag)           or
          1 = is_different_number(cust_comm_flag, p_cust_comm_flag)                                   or
          1 = is_different_number(order_taking_clevel3_flag, p_order_taking_clevel3_flag)             or
          1 = is_different_number(order_taking_clevel2_flag, p_order_taking_clevel2_flag)             or
          1 = is_different_number(exact_quantity_order_tolerance, p_exact_qty_order_tolerance)        or
          1 = is_different_number(allow_planning_material_flag, p_allow_planning_material_flag)       or
          1 = is_different_number(auto_payer_determ_flag, p_auto_payer_determ_flag)                   or
          1 = is_different_number(cs_show_payment_mth_flag, p_cs_show_payment_mth_flag)               or
          1 = is_different_number(cs_price_type, p_cs_price_type)                                     or
          1 = is_different_number(cs_show_totals_text_flag, p_cs_show_totals_text_flag)               or
          1 = is_different_number(cs_show_cust_tax_code_flag, p_cs_show_cust_tax_code_flag)           or
          1 = is_different_number(cs_enable_one_time_cust_flag, p_cs_enable_one_time_cust_flag)       or
          1 = is_different_number(dn_show_trailer_flag, p_dn_show_trailer_flag)                       or
          1 = is_different_number(dn_show_seals_info_flag, p_dn_show_seals_info_flag)                 or
          1 = is_different_number(dn_show_silo_no_flag, p_dn_show_silo_no_flag)                       or
          1 = is_different_number(dn_show_plant_cycle_flag, p_dn_show_plant_cycle_flag)               or
          1 = is_different_number(dn_show_fix_text, p_dn_show_fix_text)                               or
          1 = is_different_number(dn_show_dispatcher_driver_flag, p_dn_show_disp_driver_flag)         or
          1 = is_different_number(dn_show_carrier_crm_flag, p_dn_show_carrier_crm_flag)               or
          1 = is_different_number(exp_show_total_txt_flag, p_exp_show_total_txt_flag)                 or
          1 = is_different_number(exp_show_legal_text_1_flag, p_exp_show_legal_text_1_flag)           or
          1 = is_different_number(exp_show_legal_text_2_flag, p_exp_show_legal_text_2_flag)           or
          1 = is_different_number(exp_show_issue_dat_loc_flag, p_exp_show_issue_dat_loc_flag)         or
          1 = is_different_number(exp_show_ad_invoice_no_flag, p_exp_show_ad_invoice_no_flag)         or
          1 = is_different_number(exp_show_ctrl_no_flag, p_exp_show_ctrl_no_flag)                     or
          1 = is_different_number(exp_mat_price_type, p_exp_mat_price_type)                           or
          1 = is_different_number(cs_show_contract_currency_flag, p_cs_show_contract_curr_flag)       or
          1 = is_different_number(exp_show_zcode1_2_flag, p_exp_show_zcode1_2_flag)                   or
          1 = is_different_number(enable_pricing_call_flag, p_enable_pricing_call_flag)               or
          1 = is_different_number(show_price_in_web_order_flag, p_show_price_in_web_order_flag)       or
          1 = is_different_number(rail_return_sarea_flag, p_rail_return_sarea_flag)                   or
          1 = is_different_number(force_to_sap_flag, p_force_to_sap_flag)                             or
          1 = is_different_number(enable_demand_planning_flag, p_enable_demand_plan_flag)             or
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        );
      dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnc_sarea (ID=' || v_existing_id || ', ' || v_sorg_dist_div_cd || ').');
    end if;
  else
    insert into tlgnc_sarea (
      sarea_id,
      sorg_cd,
      distribution_channel,
      division_cd,
      sarea_display_longname,
      delnote_uses_ws_number_flag,
      document_del_time_window_flag,
      document_distance_flag,
      document_comm_carrier_flag,
      document_exec_carrier_flag,
      cust_comm_flag,
      order_taking_clevel3_flag,
      order_taking_clevel2_flag,
      exact_quantity_order_tolerance,
      allow_planning_material_flag,
      auto_payer_determ_flag,
      cs_show_payment_mth_flag,
      cs_price_type,
      cs_show_totals_text_flag,
      cs_show_cust_tax_code_flag,
      cs_enable_one_time_cust_flag,
      dn_show_trailer_flag,
      dn_show_seals_info_flag,
      dn_show_silo_no_flag,
      dn_show_plant_cycle_flag,
      dn_show_fix_text,
      dn_show_dispatcher_driver_flag,
      dn_show_carrier_crm_flag,
      exp_show_total_txt_flag,
      exp_show_legal_text_1_flag,
      exp_show_legal_text_2_flag,
      exp_show_issue_dat_loc_flag,
      exp_show_ad_invoice_no_flag,
      exp_show_ctrl_no_flag,
      exp_mat_price_type,
      cs_show_contract_currency_flag,
      exp_show_zcode1_2_flag,
      enable_pricing_call_flag,
      show_delplant_web_order_flag,
      show_price_in_web_order_flag,
      rail_return_sarea_flag,
      force_to_sap_flag,
      enable_demand_planning_flag,
      ctl_act_cd,
      ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
    values (
      slgnc_sarea.nextval,
      p_sorg_cd,
      p_distribution_channel,
      p_division_cd,
      p_sarea_display_longname,
      p_delnote_uses_ws_number_flag,
      p_document_del_time_win_flag,
      p_document_distance_flag,
      p_document_comm_carrier_flag,
      p_document_exec_carrier_flag,
      p_cust_comm_flag,
      p_order_taking_clevel3_flag,
      p_order_taking_clevel2_flag,
      p_exact_qty_order_tolerance,
      p_allow_planning_material_flag,
      p_auto_payer_determ_flag,
      p_cs_show_payment_mth_flag,
      p_cs_price_type,
      p_cs_show_totals_text_flag,
      p_cs_show_cust_tax_code_flag,
      p_cs_enable_one_time_cust_flag,
      p_dn_show_trailer_flag,
      p_dn_show_seals_info_flag,
      p_dn_show_silo_no_flag,
      p_dn_show_plant_cycle_flag,
      p_dn_show_fix_text,
      p_dn_show_disp_driver_flag,
      p_dn_show_carrier_crm_flag,
      p_exp_show_total_txt_flag,
      p_exp_show_legal_text_1_flag,
      p_exp_show_legal_text_2_flag,
      p_exp_show_issue_dat_loc_flag,
      p_exp_show_ad_invoice_no_flag,
      p_exp_show_ctrl_no_flag,
      p_exp_mat_price_type,
      p_cs_show_contract_curr_flag,
      p_exp_show_zcode1_2_flag,
      p_enable_pricing_call_flag,
      c_default_show_delplant_flag,
      p_show_price_in_web_order_flag,
      p_rail_return_sarea_flag,
      p_force_to_sap_flag,
      p_enable_demand_plan_flag,
      p_ctl_act_cd,
      1, systimestamp, 1, systimestamp, 0
    );
    dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnc_sarea (ID=' || slgnc_sarea.currval || ', ' || v_sorg_dist_div_cd || ').');
  end if;

end upsert_sarea;
/
show errors;


prompt INSERTING into TLGNC_SAREA

execute upsert_sarea('AD01', '10', '11', 'AD01-10-11', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD01', '20', '11', 'AD01-20-11', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);

execute upsert_sarea('AD02', '10', '10', 'AD02-10-10', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD02', '10', '11', 'AD02-10-11', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD02', '10', '30', 'AD02-10-30', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD02', '10', '40', 'AD02-10-40', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD02', '10', '90', 'AD02-10-90', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD02', '20', '10', 'AD02-20-10', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD02', '20', '11', 'AD02-20-11', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD02', '20', '30', 'AD02-20-30', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD02', '20', '40', 'AD02-20-40', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD02', '20', '90', 'AD02-20-90', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);

execute upsert_sarea('AD03', '10', '10', 'AD03-10-10', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD03', '10', '20', 'AD03-10-20', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD03', '20', '10', 'AD03-20-10', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD03', '20', '20', 'AD03-20-20', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('AD03', '20', '30', 'AD03-20-30', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);

execute upsert_sarea('ZA01', '10', '10', 'ZA01-10-10', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('ZA01', '10', '30', 'ZA01-10-30', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('ZA01', '20', '10', 'ZA01-20-10', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('ZA01', '20', '30', 'ZA01-20-30', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);

execute upsert_sarea('ZA02', '10', '40', 'ZA02-10-40', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('ZA02', '20', '40', 'ZA02-20-40', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);

execute upsert_sarea('ZA03', '10', '80', 'ZA03-10-40', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);
execute upsert_sarea('ZA03', '20', '80', 'ZA03-20-40', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,0,0, 0);

-- special sales area required for rail
execute upsert_sarea('CH90', '10', '10', 'AD90-10-10', 0, 1, 1, 1, 1, 0, 0, 0, 1000, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1,0,0,1,0, 0);


commit;

drop procedure upsert_sarea;
drop function is_different_number;
drop function is_different_varchar2;
