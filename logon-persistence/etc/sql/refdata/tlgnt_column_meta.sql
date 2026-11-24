-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnt_column_meta.sql ----------'

prompt INSERTING into tlgnt_column_meta

-- add here if needed (update restrictions, blob handling, etc.)
-------------------------------------------------------------------------------
-- tlgnt_column_meta
-------------------------------------------------------------------------------
create or replace procedure insert_column_meta (
  p_table_name   in varchar2,
  p_column_name in varchar2)
is
  v_table_meta_id    number;
  v_column_meta_id   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert ' || p_column_name || ' column from table ' || p_table_name || ' to restrict column editability ');

    select table_meta_id 
    into v_table_meta_id 
    from tlgnt_table_meta 
    where table_name = p_table_name;
  
    select internal_column_id 
    into v_column_meta_id 
    from all_tab_cols 
    where table_name = p_table_name
    and column_name = p_column_name;
  
    insert into tlgnt_column_meta (
    column_meta_id, 
    column_name,
    table_meta_id,
    update_allowed
    ) values (
    v_table_meta_id || v_column_meta_id,
    p_column_name,
    v_table_meta_id,
    0
    );
    

  end insert_column_meta;
/
show errors;

prompt INSERTING SHOW_PRICE_IN_WEB_ORDER_FLAG from TLGNC_SAREA into TLGNT_COLUMN_META with UPDATE NOT ALLOWED
execute insert_column_meta('TLGNC_SAREA', 'SHOW_PRICE_IN_WEB_ORDER_FLAG');

drop procedure insert_column_meta;

commit;

    

