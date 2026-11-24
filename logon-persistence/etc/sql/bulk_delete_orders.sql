/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 *
 * Warning!!! Run this script only from sqlPlus on a Windows machine!
 *
 */
------------------------
-- Bulk delete orders --
------------------------

set define on;
set serveroutput on;
set trimspool on;
set linesize 1000;

/**
 * Custom type for the list of order numbers
 */
create or replace type order_numbers is table of number;
/

show errors;

----------------
-- Parameters --
----------------
/**
 * Path of the log directory. Please make sure that is really exists!
 */
var log_directory_path varchar2(500);
exec :log_directory_path :='D:\ProgramData\AdNovum\adnjboss7win\instances\LOGONLAN'
/**
 * Please enter the order number list into the following function!
 *
 * You should only edit this section of the script!
 */
create or replace function get_orders_to_delete return order_numbers as
begin
  return order_numbers(
    1,
    2,
    3
  );
end;
/

show errors;

------------------
-- Declarations --
------------------
/**
 * Create temporary tables for storing reference IDs
 */
create global temporary table tlgnt_assignments_to_delete (assignment_id number) on commit delete rows;
create global temporary table tlgnt_ext_orders_to_delete (order_id number) on commit delete rows;
create global temporary table tlgnt_loads_to_delete (load_id number) on commit delete rows;
create global temporary table tlgnt_order_reps_to_delete (order_rep_id number) on commit delete rows;
create global temporary table tlgnt_order_items_to_delete (order_item_id number) on commit delete rows;
create global temporary table tlgnt_bizevents_to_delete (bizevent_id number) on commit delete rows;

/**
 * Log the IDs to delete for later analyzis
 */
create or replace procedure log_ids_to_delete(i_order_number in number)
is
  l_id_list_concatenated varchar(1000);
  l_log_message varchar(5000);
begin
  logon_log.g_min_loglevel := 'INFO';
  l_log_message := 'Order number: "' || i_order_number || '", ';

  select
    listagg(order_item_id, ',') within group (order by order_item_id) into l_id_list_concatenated
  from tlgnt_order_items_to_delete;
  l_log_message := l_log_message || 'order item IDs: "' || l_id_list_concatenated || '", ';

  select
    listagg(bizevent_id, ',') within group (order by bizevent_id) into l_id_list_concatenated
  from tlgnt_bizevents_to_delete;
  l_log_message := l_log_message || 'business event IDs: "' || l_id_list_concatenated || '", ';

  select
    listagg(assignment_id, ',') within group (order by assignment_id) into l_id_list_concatenated
  from tlgnt_assignments_to_delete;
  l_log_message := l_log_message || 'assignment IDs: "' || l_id_list_concatenated || '", ';

  select
    listagg(load_id, ',') within group (order by load_id) into l_id_list_concatenated
  from tlgnt_loads_to_delete;
  l_log_message := l_log_message || 'load IDs: "' || l_id_list_concatenated || '", ';

  select
    listagg(order_id, ',') within group (order by order_id) into l_id_list_concatenated
  from tlgnt_ext_orders_to_delete;
  l_log_message := l_log_message || 'external order IDs: "' || l_id_list_concatenated || '", ';

  select
    listagg(order_rep_id, ',') within group (order by order_rep_id) into l_id_list_concatenated
  from tlgnt_order_reps_to_delete;
  l_log_message := l_log_message || 'order repair IDs: "' || l_id_list_concatenated || '"';

  logon_log.info('bulk delete orders', l_log_message);
end;
/

show errors;

/**
 * Deletes one order and all it's references without any further checks.
 *
 * @param: i_order_number
 */
create or replace procedure delete_order_and_dependencies(i_order_number in number)
is
  l_custorder_id number;
  l_deliverynote_signature_id number;
  l_order_id number;
  l_nondeleted_order_count number;
begin
  logon_log.g_min_loglevel := 'INFO';

  /**
   * Store dependant IDs to delete them afterwards
   */
  select   custorder_id,   deliverynote_signature_id,   order_id
    into l_custorder_id, l_deliverynote_signature_id, l_order_id
  from tlgnc_order
    where order_number = i_order_number;
  insert into tlgnt_order_reps_to_delete
  select distinct order_rep_id from tlgnc_order_rep rep
    where order_id = l_order_id;
  insert into tlgnt_assignments_to_delete
    select distinct assignmentpart.assignment_id from tlgnc_order_item orderitem
      inner join tlgnc_assignment_part assignmentpart on (orderitem.load_id = assignmentpart.load_id)
      where orderitem.order_id = l_order_id
        union
    select distinct assignmentpart.assignment_id from tlgnc_order logon_order
      inner join tlgnc_assignment_part assignmentpart
        on (logon_order.order_id = assignmentpart.order_id and logon_order.order_id = l_order_id);
  insert into tlgnt_loads_to_delete
    select distinct load_id from tlgnc_order_item
    where order_id = l_order_id;
  insert into tlgnt_order_items_to_delete
    select distinct order_item_id from tlgnc_order_item
    where order_id = l_order_id;
  insert into tlgnt_bizevents_to_delete
    select bizevent_id
    from
      tlgnc_bizevent bizevent
    join
      tlgnc_assignment_part assignment_part
      on (
        assignment_part.order_id = l_order_id and
        assignment_part.assignment_id = bizevent.assignment_id)
    union
    select bizevent_id
    from
      tlgnc_bizevent
    where order_id = l_order_id;

  log_ids_to_delete(i_order_number);

  /**
   * Repair/maintenance
   */
  delete from tlgnc_order_rep_maint_type
  where order_rep_id in (
    select order_rep_id from tlgnt_order_reps_to_delete
  );

  delete from tlgnc_order_rep_att
  where order_rep_id in (
    select order_rep_id from tlgnt_order_reps_to_delete
  );

  delete from tlgnc_order_rep_dmgcode_type
  where order_rep_id in (
    select order_rep_id from tlgnt_order_reps_to_delete
  );

  delete from tlgnc_order_rep where order_id = l_order_id;

  /**
   * Output documents
   */
  delete from tlgnc_order_outputdoc where order_id = l_order_id;

  delete from tlgnc_outputdoc where outputdoc_id in (
    select distinct pdf_outputdoc_id
      from tlgnc_order_outputdoc
      where order_id = l_order_id and
      pdf_outputdoc_id is not null
    union
      select distinct ps_outputdoc_id
        from tlgnc_order_outputdoc
        where order_id = l_order_id and
        ps_outputdoc_id is not null
  );

  /**
   * Business events
   */
  delete from tlgnc_bizevent_audit where bizevent_id in (
    select bizevent_id from tlgnt_bizevents_to_delete
  );
  delete from tlgnc_bizevent where bizevent_id in (
    select bizevent_id from tlgnt_bizevents_to_delete
  );

  /**
   * Assignments
   */
  delete from tlgnc_ext_order where ext_order_id in (
    select distinct assignment_part.ext_order_id
    from tlgnc_assignment_part assignment_part
    where assignment_id in (
      select assignment_id from tlgnt_assignments_to_delete
    )
  );

  delete from tlgnc_assignment_part where assignment_id in (
    select assignment_id from tlgnt_assignments_to_delete
  );

  delete from tlgnc_assignment where assignment_id in (
    select assignment_id from tlgnt_assignments_to_delete
  );

  /**
   * Order items
   */
  update tlgnc_order_item set bimodal_rail_order_item_id = null
    where order_id = l_order_id;

  delete from tlgnc_credit_note where order_item_id in (
    select order_item_id from tlgnt_order_items_to_delete
  );

  delete from tlgnc_pricing_call where order_item_id in (
    select order_item_id from tlgnt_order_items_to_delete
  );

  delete from tlgnc_order_item where order_id = l_order_id;

  /**
   * Loads
   */
  delete from tlgnc_goods_movement where load_id in (
    select load_id from tlgnt_loads_to_delete
  );

  delete from tlgnc_load where load_id in (
    select load_id from tlgnt_loads_to_delete
  );

  /**
   * Orders
   */
  delete from tlgnc_order_preschedule where order_id = l_order_id;

  delete from tlgnc_order_truck_type where order_id = l_order_id;

  delete from tlgnc_order where order_id = l_order_id;

  /**
   * Customer order - only delete if this is the last child order to delete
   */
  select count(*) into l_nondeleted_order_count
    from tlgnc_order
    where custorder_id = l_custorder_id;

  if (l_nondeleted_order_count = 0) then
    delete from tlgnc_custorder_outputdoc
      where custorder_id = l_custorder_id;

    delete from tlgnc_custorder_item
      where custorder_id = l_custorder_id;

    delete from tlgnc_custorder
      where custorder_id = l_custorder_id;
  end if;

  delete from tlgnc_signature where signature_id = l_deliverynote_signature_id;

  dbms_output.put_line(to_char(systimestamp, 'yyyy.mm.dd. hh24:mi:ss ')
    || i_order_number || '      OK: Order deleted.');
  logon_log.info('bulk delete orders', i_order_number || ' Order deleted.');
  commit;
exception
when no_data_found then
  rollback;
  dbms_output.put_line(to_char(systimestamp,'yyyy.mm.dd. hh24:mi:ss ')
    || i_order_number || ' WARNING: Order number not found.');
  logon_log.error('bulk delete orders', i_order_number || ' Order number not found.');
  commit;
when others then
  rollback;
  dbms_output.put_line(to_char(systimestamp,'yyyy.mm.dd. hh24:mi:ss ')
    || i_order_number || '   ERROR: "' || sqlerrm || '"');
  logon_log.error('bulk delete orders', i_order_number || ' "' || sqlerrm || '"');
  commit;
end;
/

show errors;

/**
 * Main procedure for bulk deleting orders
 *
 * Example invocation: execute bulk_delete_orders(order_numbers(1,2,3));
 */
create or replace procedure bulk_delete_orders(i_order_numbers in order_numbers) as
begin
  for v_order_number_index in i_order_numbers.first .. i_order_numbers.last
  loop
    delete_order_and_dependencies(i_order_numbers(v_order_number_index));
  end loop;
end;
/

show errors;

-------------
-- Logging --
-------------
/**
 * Setup file based logging
 */
column the_current_date new_value the_current_date noprint
column log_directory new_value log_directory noprint
select to_char(systimestamp, 'YYYY-MM-DD_hh24-mi') as the_current_date from dual;
select :log_directory_path as log_directory from dual;

----------------------------
-- Run bulk_delete_orders --
----------------------------
set autocommit off;
spool &log_directory\logon_bulk_delete_orders_&the_current_date..log
execute bulk_delete_orders(get_orders_to_delete);
spool off;

--------------
-- Clean up --
--------------
drop type order_numbers;
drop procedure bulk_delete_orders;
drop procedure delete_order_and_dependencies;
drop function get_orders_to_delete;
drop table tlgnt_assignments_to_delete;
drop table tlgnt_loads_to_delete;
drop table tlgnt_ext_orders_to_delete;
drop table tlgnt_order_reps_to_delete;
drop table tlgnt_order_items_to_delete;
drop table tlgnt_bizevents_to_delete;


select to_char(systimestamp, 'YYYY-MM-DD_hh24-mi') as the_current_date from dual;
select :log_directory_path as log_directory from dual;

prompt Please find the logs at &log_directory\logon_bulk_delete_orders_&the_current_date..log
prompt

set define off;
exit
