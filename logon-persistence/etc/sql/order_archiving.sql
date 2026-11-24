-------------------------------------------------------------------------------
-- archiving_delete_data - Used during Order Archiving
-------------------------------------------------------------------------------
-- Stored procedure for deleting rows associated with archived orders added.
-- View VLGNC_ARCHIVING_DELETABLE: contains keys (PK or FK) of tables where data can be deleted.
-- Procedure ARCHIVING_DELETE_DATA: procedure for order data deletion. Requires two temporary tables tlgnt_archiving_del_id_list and tlgnt_archiving_del_id_list_2.
-- Temporary tables TLGNT_ARCHIVING_DEL_ID_LIST and TLGNT_ARCHIVING_DEL_ID_LIST_2: Required for holding keys of not yet deleted data which dissapear from view when referencing entries are deleted.
-- Example: Tables TLGNC_SIGNATURE and TLGNC_CUSTORDER are referenced by TLGNC_ORDER
-- -> entries of first two tables cannot be deleted until referencing entries in last table have been deleted
-- -> signature_id and custorder_id are also removed from vlgnc_archiving_deletable when order entry is deleted from tlgnc_order
-- -> signature_id and custorder_id of deleted entry have to be kept in temporary table for subsequent deletion

-- required views created in custom_views.sql

execute drop_table_if_exists('tlgnt_archiving_assignmentids');
create global temporary table tlgnt_archiving_assignmentids (assignment_id number) on commit delete rows;

execute drop_table_if_exists('tlgnt_archiving_orderids');
create global temporary table tlgnt_archiving_orderids (order_id number) on commit delete rows;

execute drop_table_if_exists('tlgnt_archiving_loadids');
create global temporary table tlgnt_archiving_loadids (load_id number) on commit delete rows;

execute drop_table_if_exists('tlgnt_archiving_documentids');
create global temporary table tlgnt_archiving_documentids (document_id number) on commit delete rows;

create or replace function is_order_removable (i_order_id in number) return number is l_count NUMBER;
begin
  SELECT COUNT(*)
  INTO l_count
  FROM VLGNC_DELETABLE_ORDERS
  WHERE order_id = i_order_id ;

  RETURN l_count;
end;
/

create or replace procedure archiving_remove_order (i_order_id in number)
is
	l_custorder_id NUMBER;
	l_deliverynote_signature_id NUMBER;
	l_nondeleted_order_count NUMBER;
	ORDER_REP_MAINT_TYPE_EVENT CONSTANT NUMBER := 1003;
	ORDER_REP_ATT_EVENT CONSTANT NUMBER := 1004;
	ORDER_REP_DMGCODE_TYPE_EVENT CONSTANT NUMBER := 1005;
	ORDER_REP_EVENT CONSTANT NUMBER := 1006;
	ORDER_OUTPUTDOC_EVENT CONSTANT NUMBER := 1007;
	OUTPUTDOC_EVENT CONSTANT NUMBER := 1008;
	CUSTORDER_OUTPUTDOC_EVENT CONSTANT NUMBER := 1009;
	CUSTORDER_ITEM_EVENT CONSTANT NUMBER := 1010;
	BIZEVENT_AUDIT_EVENT CONSTANT NUMBER := 1011;
	BIZEVENT_EVENT CONSTANT NUMBER := 1012;
	ASSIGNMENT_PART_EVENT CONSTANT NUMBER := 1013;
	ASSIGNMENT_EVENT CONSTANT NUMBER := 1014;
--	EXT_ORDER_EVENT CONSTANT NUMBER := 1015;
	CREDIT_NOTE_EVENT CONSTANT NUMBER := 1016;
	PRICING_CALL_EVENT CONSTANT NUMBER := 1017;
	BIMODAL_RAIL_TO_NULL_EVENT CONSTANT NUMBER := 1018;
	ORDER_ITEM_EVENT CONSTANT NUMBER := 1019;
	GOODS_MOVEMENT_EVENT CONSTANT NUMBER := 1020;
	LOAD_EVENT CONSTANT NUMBER := 1021;
	ORDER_PRESCHEDULE_EVENT CONSTANT NUMBER := 1022;
	ORDER_TRUCK_TYPE_EVENT CONSTANT NUMBER := 1023;
	ORDER_EVENT CONSTANT NUMBER := 1024;
	CUSTORDER_EVENT CONSTANT NUMBER := 1025;
	SIGNATURE_EVENT CONSTANT NUMBER := 1026;
begin
			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ORDER_REP_MAINT_TYPE_EVENT, i_order_id, order_rep_maint_type_id
						from tlgnc_order_rep_maint_type
						where order_rep_id in (select order_rep_id
								   			   from tlgnc_order_rep rep
								  			   where order_id = i_order_id);

			delete from tlgnc_order_rep_maint_type
			where order_rep_id in (select order_rep_id
								   from tlgnc_order_rep rep
								   where order_id = i_order_id);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ORDER_REP_ATT_EVENT, i_order_id, attachment_id
						from tlgnc_order_rep_att
						where order_rep_id in (select order_rep_id
								  			from tlgnc_order_rep rep
								  			   where order_id = i_order_id);
			delete from tlgnc_order_rep_att
			where order_rep_id in (select order_rep_id
								   from tlgnc_order_rep rep
								   where order_id = i_order_id);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ORDER_REP_DMGCODE_TYPE_EVENT, i_order_id, order_rep_dmgcode_type_id
						from tlgnc_order_rep_dmgcode_type
						where order_rep_id in (select order_rep_id
								  			   from tlgnc_order_rep rep
								   			   where order_id = i_order_id);
			delete from tlgnc_order_rep_dmgcode_type
			where order_rep_id in (select order_rep_id
								   from tlgnc_order_rep rep
								   where order_id = i_order_id);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ORDER_REP_EVENT, i_order_id, order_rep_id
						from tlgnc_order_rep where order_id = i_order_id;
			delete from tlgnc_order_rep where order_id = i_order_id;
			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ORDER_OUTPUTDOC_EVENT, i_order_id, order_outputdoc_id
						from tlgnc_order_outputdoc where order_id = i_order_id;

			--------------------------------------------- Deleting documents START
			delete from tlgnt_archiving_documentids;
			insert into tlgnt_archiving_documentids select distinct pdf_outputdoc_id
   													from tlgnc_order_outputdoc
                            where order_id = i_order_id and
                                  (pdf_outputdoc_id is not null);
            -- get the documentids from tlgnc_order_outputdoc
			insert into tlgnt_archiving_documentids select distinct ps_outputdoc_id
   													from tlgnc_order_outputdoc
                            						where order_id = i_order_id and (ps_outputdoc_id is not null);
			insert into tlgnt_archiving_documentids select distinct pdf_outputdoc_id
   													from tlgnc_order_outputdoc
                            						where order_id = i_order_id and (pdf_outputdoc_id is not null);
			-- get the documentids from tlgnc_custorder_outputdoc
			insert into tlgnt_archiving_documentids select distinct ps_outputdoc_id
   													from tlgnc_custorder_outputdoc
                            						where (ps_outputdoc_id is not null) and
                            								custorder_id = (select custorder_id
																			from tlgnc_order 
																			where order_id = i_order_id);
			insert into tlgnt_archiving_documentids select distinct pdf_outputdoc_id
   													from tlgnc_custorder_outputdoc
                            						where (pdf_outputdoc_id is not null) and
                            								custorder_id = (select custorder_id
																			from tlgnc_order 
																			where order_id = i_order_id);
			------ Got all documents in tlgnt_archiving_documentids
            insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, OUTPUTDOC_EVENT, i_order_id, outputdoc_id
						from tlgnc_outputdoc where outputdoc_id in (select distinct document_id
																	 from tlgnt_archiving_documentids);
			-- first delete from the switchtables
			delete from tlgnc_order_outputdoc where order_id = i_order_id;
			delete from tlgnc_custorder_outputdoc where custorder_id = (select custorder_id
																		  from tlgnc_order
																		  where order_id = i_order_id);
			-- then delete the documents themselves
			delete from tlgnc_outputdoc where outputdoc_id in (select distinct document_id
																 from tlgnt_archiving_documentids);
			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, CUSTORDER_OUTPUTDOC_EVENT, i_order_id, custorder_outputdoc_id
						from tlgnc_custorder_outputdoc where custorder_id = (select custorder_id
																		  from tlgnc_order
																		  where order_id = i_order_id);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, CUSTORDER_ITEM_EVENT, i_order_id, custorder_item_id
						from tlgnc_custorder_item where custorder_id = (select custorder_id
																		  from tlgnc_order
																		  where order_id = i_order_id);
			--------------------------------------------- Deleting documents END
			delete from tlgnc_custorder_item where custorder_id = (select custorder_id
																		  from tlgnc_order
																		  where order_id = i_order_id);
			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, BIZEVENT_AUDIT_EVENT, i_order_id, bizevent_audit_id
						from tlgnc_bizevent_audit where bizevent_id in (select bizevent_id
																		  from tlgnc_bizevent
																		  where order_id = i_order_id);
			delete from tlgnc_bizevent_audit where bizevent_id in (select bizevent_id
																		  from tlgnc_bizevent
																		  where order_id = i_order_id);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, BIZEVENT_EVENT, i_order_id, bizevent_id
						from tlgnc_bizevent where order_id = i_order_id;
			delete from tlgnc_bizevent where order_id = i_order_id;
			delete from tlgnt_archiving_assignmentids;
			insert into tlgnt_archiving_assignmentids select distinct assignmentpart.assignment_id
													  from tlgnc_order_item orderitem
													  inner join tlgnc_assignment_part assignmentpart on (orderitem.load_id = assignmentpart.load_id)
									 				  where orderitem.order_id = i_order_id;

			delete from tlgnt_archiving_orderids;
--			insert into tlgnt_archiving_orderids select distinct assignmentpart.ext_order_id
--													  from tlgnc_order_item orderitem
--													  inner join tlgnc_assignment_part assignmentpart on (orderitem.load_id = assignmentpart.load_id)
--									 				  where orderitem.order_id = i_order_id;

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, BIZEVENT_AUDIT_EVENT, i_order_id, bizevent_audit_id
						from tlgnc_bizevent_audit where bizevent_id in (select bizevent_id
																		from tlgnc_bizevent
																		where assignment_id in (select assignment_id
																	  							from tlgnt_archiving_assignmentids));
			delete from tlgnc_bizevent_audit where bizevent_id in (select bizevent_id
																		  from tlgnc_bizevent
																		  where assignment_id in ( select assignment_id
																	  from tlgnt_archiving_assignmentids));

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, BIZEVENT_EVENT, i_order_id, bizevent_id
						from tlgnc_bizevent where assignment_id in ( select assignment_id
																	  from tlgnt_archiving_assignmentids);
			delete from tlgnc_bizevent where assignment_id in ( select assignment_id
																	  from tlgnt_archiving_assignmentids);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ASSIGNMENT_PART_EVENT, i_order_id, assignment_part_id
						from tlgnc_assignment_part where assignment_id in (select assignment_id
																	  from tlgnt_archiving_assignmentids);
			delete from tlgnc_assignment_part where assignment_id in (select assignment_id
																	  from tlgnt_archiving_assignmentids);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ASSIGNMENT_EVENT, i_order_id, assignment_id
						from tlgnc_assignment where assignment_id in (select assignment_id
																 from tlgnt_archiving_assignmentids);
			delete from tlgnc_assignment where assignment_id in (select assignment_id
																 from tlgnt_archiving_assignmentids);

--			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
--						select slgnc_archiving_log.nextval, EXT_ORDER_EVENT, i_order_id, ext_order_id
--						from tlgnc_ext_order where ext_order_id in (select order_id
--															   from tlgnt_archiving_orderids);
--			delete from tlgnc_ext_order where ext_order_id in (select order_id
--															   from tlgnt_archiving_orderids);

			delete from tlgnt_archiving_loadids;
			insert into tlgnt_archiving_loadids select distinct load_id
												from tlgnc_order_item
												where order_id = i_order_id;

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, CREDIT_NOTE_EVENT, i_order_id, credit_note_id
						from tlgnc_credit_note where order_item_id in (select order_item_id
																  from tlgnc_order_item
																  where order_id = i_order_id);
			delete from tlgnc_credit_note where order_item_id in (select order_item_id
																  from tlgnc_order_item
																  where order_id = i_order_id);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, PRICING_CALL_EVENT, i_order_id, pricing_call_id
						from tlgnc_pricing_call where order_item_id in (select order_item_id
																  from tlgnc_order_item
																  where order_id = i_order_id);
			delete from tlgnc_pricing_call where order_item_id in (select order_item_id
																  from tlgnc_order_item
																  where order_id = i_order_id);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id, log_text)
						select slgnc_archiving_log.nextval, BIMODAL_RAIL_TO_NULL_EVENT, i_order_id, order_item_id, to_char(bimodal_rail_order_item_id)
						from tlgnc_order_item
						where order_id = i_order_id;
						
			-- LOGON-9260 in case this is a bimodal railorder, the road parts might still reference this order_item.
			-- it was needed to detach the order_items referencing this order_item (set the FK to null in those records).
			-- howeverm it's not needed since the introduction of the new query with "order by delivery_type_cd desc", line 353
						
			--update tlgnc_order_item oi set bimodal_rail_order_item_id = null where oi.order_item_id in (
			--	select oi2.order_item_id 
			--	from tlgnc_order_item oi1 inner join tlgnc_order_item oi2 on oi2.bimodal_rail_order_item_id = oi1.order_item_id
			--	where oi1.order_id = i_order_id
			--);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ORDER_ITEM_EVENT, i_order_id, order_item_id
						from tlgnc_order_item where order_id = i_order_id;
			delete from tlgnc_order_item where order_id = i_order_id;
			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, GOODS_MOVEMENT_EVENT, i_order_id, goods_movement_id
						from tlgnc_goods_movement where load_id in (select load_id
															   		from tlgnt_archiving_loadids);
			delete from tlgnc_goods_movement where load_id in (select load_id
															   from tlgnt_archiving_loadids);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, LOAD_EVENT, i_order_id, load_id
						from tlgnc_load where load_id in (select load_id
															 from tlgnt_archiving_loadids);
			delete from tlgnc_load where load_id in (select load_id
													 from tlgnt_archiving_loadids);

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ORDER_PRESCHEDULE_EVENT, i_order_id, order_preschedule_id
						from tlgnc_order_preschedule where order_id = i_order_id;
			delete from tlgnc_order_preschedule where order_id = i_order_id;

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						select slgnc_archiving_log.nextval, ORDER_TRUCK_TYPE_EVENT, i_order_id, order_truck_type_id
						from tlgnc_order_truck_type where order_id = i_order_id;
			delete from tlgnc_order_truck_type where order_id = i_order_id;

			select custorder_id, deliverynote_signature_id
			into l_custorder_id,l_deliverynote_signature_id
			from tlgnc_order
			where order_id = i_order_id;

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
						values(slgnc_archiving_log.nextval, ORDER_EVENT, i_order_id, i_order_id);
			delete from tlgnc_order where order_id = i_order_id;

			SELECT COUNT(*)
  			INTO l_nondeleted_order_count
  			FROM tlgnc_order
  			WHERE custorder_id = l_custorder_id ;

			IF (l_nondeleted_order_count = 0) THEN
				insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
							values(slgnc_archiving_log.nextval, CUSTORDER_EVENT, i_order_id, l_custorder_id);
				delete from tlgnc_custorder where custorder_id = l_custorder_id;
			END IF;

			insert into tlgnc_archiving_log(archiving_log_id, archiving_log_event_cd, order_id, deleted_entry_id)
							values(slgnc_archiving_log.nextval, SIGNATURE_EVENT, i_order_id, l_deliverynote_signature_id);
			delete from tlgnc_signature where signature_id = l_deliverynote_signature_id;
end;
/

create or replace procedure archiving_delete_data (i_order_id in number)
is
	l_removable_order_id NUMBER;
begin
	 if is_order_removable(i_order_id) > 0 then
	    for removable_order in (
		  select distinct order_id, delivery_type_cd from (
		    -- gets orders for same custorder. Note that this always returns the same i_order_id as well.
		      select order1.order_id, order1.delivery_type_cd
		      from tlgnc_order order1
		      inner join tlgnc_order order2 on order1.custorder_id = order2.custorder_id
		      where order2.order_id = i_order_id
		    union -- this part gets the bimodal rail order for a given truck order (there is always only one rail order)
		      select bimodal_rail_order_item.order_id, ord.delivery_type_cd
		      from tlgnc_order_item bimodal_rail_order_item
		        inner join tlgnc_order_item bimodal_truck_order_item
		            on bimodal_rail_order_item.order_item_id = bimodal_truck_order_item.bimodal_rail_order_item_id
		        inner join tlgnc_order ord on ord.order_id = bimodal_rail_order_item.order_id
		      where bimodal_truck_order_item.order_id = i_order_id
		    union -- this gets the bimodal truck orders for a given rail order
		      select bimodal_truck_order_item.order_id, ord.delivery_type_cd
		      from tlgnc_order_item bimodal_truck_order_item
		        inner join tlgnc_order_item bimodal_rail_order_item
		            on bimodal_rail_order_item.order_item_id = bimodal_truck_order_item.bimodal_rail_order_item_id
		        inner join tlgnc_order ord on ord.order_id = bimodal_truck_order_item.order_id
		      where bimodal_rail_order_item.order_id = i_order_id
		    union -- this query gets all of the bimodal truck orders belonging to a rail order for a given truck order
		      select bimodal_truck_order_item.order_id, ord.delivery_type_cd
		      from tlgnc_order_item bimodal_truck_order_item
		        inner join tlgnc_order_item oi2
		            on oi2.bimodal_rail_order_item_id = bimodal_truck_order_item.bimodal_rail_order_item_id
		        inner join tlgnc_order ord on ord.order_id = bimodal_truck_order_item.order_id
		      where oi2.order_id = i_order_id)
		  order by delivery_type_cd desc)
		 loop
     		   archiving_remove_order(removable_order.order_id);
 		 end loop;
   end if;
end archiving_delete_data;
/
show errors;
