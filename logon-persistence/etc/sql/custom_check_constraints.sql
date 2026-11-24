/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */


/* 
 * It's checking the existence of the given constraint. If it extists then return value greater than 
 * 0, otherwise it's 0.
 */
CREATE OR REPLACE FUNCTION existsConstraint(i_constraint_name IN VARCHAR2) RETURN NUMBER 
IS 
  l_count NUMBER;
BEGIN
  SELECT COUNT(*)
  INTO l_count
  FROM user_constraints
  WHERE constraint_name = UPPER( i_constraint_name) ;

  RETURN l_count;
END;
/


BEGIN
	
  IF existsConstraint('ck_planning_sap_materials') = 0 THEN
    execute immediate 'ALTER TABLE tlgns_sap_material 
    ADD CONSTRAINT ck_planning_sap_materials
    CHECK((sap_material_id = 2 and sap_material_no = ''LOGON00002'') or
    (sap_material_id = 1 and sap_material_no = ''LOGON00001'') or 
    (sap_material_id not in (1,2) and sap_material_no not in (''LOGON00001'', ''LOGON00002'')))';
  END IF;
    
  IF existsConstraint('ck_planning_sap_contract_items') = 0 THEN
    execute immediate 'ALTER TABLE tlgns_sap_contract_item 
    ADD CONSTRAINT ck_planning_sap_contract_items
    CHECK((sap_material_id = 2 and contract_item_no = -3) or
    (sap_material_id = 1 and contract_item_no = -2) or 
    (sap_material_id not in (1,2) and contract_item_no not in (-2,-3)))';
  END IF;
	
  /* Constraints for tlgnc_order_item */ 
  
  IF existsConstraint('ck_tlgnc_order_item_ord_qtty') = 0 THEN
    execute immediate 'ALTER TABLE tlgnc_order_item 
    ADD CONSTRAINT ck_tlgnc_order_item_ord_qtty 
    CHECK( ordered_quantity IS NULL OR ordered_uom_cd IS NOT NULL)';
  END IF;


  IF existsConstraint('ck_tlgnc_order_item_uoms_equal') = 0 THEN
    execute immediate 'ALTER TABLE tlgnc_order_item 
    ADD CONSTRAINT ck_tlgnc_order_item_uoms_equal
    CHECK(
        returned_uom_cd is null or
        loaded_uom_cd is null or
        coalesce(returned_uom_cd, ''comparing_value_in_case_of_null'')
         =
        coalesce(loaded_uom_cd  , ''comparing_value_in_case_of_null'')
    )';
  END IF;


  IF existsConstraint('ck_tlgnc_order_item_load_qtty') = 0 THEN
    execute immediate 'ALTER TABLE tlgnc_order_item 
    ADD CONSTRAINT ck_tlgnc_order_item_load_qtty 
    CHECK( loaded_quantity IS NULL OR loaded_uom_cd IS NOT NULL )';
  END IF;


  IF existsConstraint('ck_tlgnc_order_item_ret_qtty') = 0 THEN
    execute immediate 'ALTER TABLE tlgnc_order_item 
    ADD CONSTRAINT ck_tlgnc_order_item_ret_qtty 
    CHECK( returned_quantity IS NULL OR returned_uom_cd IS NOT NULL )';
  END IF;


  IF existsConstraint('ck_tlgnc_order_item_deli_qtty') = 0 THEN
    execute immediate 'ALTER TABLE tlgnc_order_item 
    ADD CONSTRAINT ck_tlgnc_order_item_deli_qtty 
     CHECK( delivered_quantity IS NULL OR delivered_uom_cd IS NOT NULL )'; 
  END IF;

  /* Constraints for tlgnc_load */ 
  
  IF existsConstraint('ck_tlgnc_load_net_qtty') = 0 THEN
     execute immediate 'ALTER TABLE tlgnc_load 
     ADD CONSTRAINT ck_tlgnc_load_net_qtty 
     CHECK( net_quantity IS NULL OR net_quantity_uom_cd IS NOT NULL )';
  END IF;


  /* Constraints for tlgnc_order */ 

  IF existsConstraint('ck_tlgnc_order_shippoint') = 0 THEN
    execute immediate 'ALTER TABLE tlgnc_order 
    ADD CONSTRAINT ck_tlgnc_order_shippoint 
    CHECK( delivery_type_cd = 8 OR shippingpoint_id IS NOT NULL )';
  END IF;


  /* Constraints for tlgnc_assignment_part */

  IF existsConstraint('ck_tlgnc_assignment_part_load') = 0 THEN
    execute immediate 'ALTER TABLE tlgnc_assignment_part 
    ADD CONSTRAINT ck_tlgnc_assignment_part_load 
    CHECK( assignment_part_type_cd = 1 OR load_id IS NULL )';
  END IF;

  IF existsConstraint('ck_tlgnc_assignment_part_order') = 0 THEN
    execute immediate 'ALTER TABLE tlgnc_assignment_part 
    ADD CONSTRAINT ck_tlgnc_assignment_part_order 
    CHECK( assignment_part_type_cd = 3 OR order_id IS NULL )';
  END IF;

  /* This constraint disables to generate Cash Sales invoice 
   * and Export invoice for the same    */
  IF existsConstraint('ck_tlgnr_map_extra_dn_flags') = 0 THEN
  	execute immediate 'ALTER TABLE tlgnr_map_extra_dn_type_bs
	ADD CONSTRAINT ck_tlgnr_map_extra_dn_flags
	CHECK( (print_cash_sales = 0 AND print_export_invoice = 0) 
		    OR
		   (print_cash_sales = 1 AND print_export_invoice = 0)
		    OR
		   (print_cash_sales = 0 AND print_export_invoice = 1)
		  )';
  END IF;
  
  IF existsConstraint('ck_user_types') = 0 THEN
    execute immediate 'ALTER TABLE tlgnu_user 
    ADD CONSTRAINT ck_user_types
    CHECK(
      (user_type_cd = 1 and sap_bp_id is not null) or
      (user_type_cd = 2 and sap_vendor_id is not null) or
      (user_type_cd = 0)
    )';
  END IF;

END;
/
DROP FUNCTION existsConstraint;
