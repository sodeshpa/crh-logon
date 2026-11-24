/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

/* 
 * There are situations where custom indexes ('I_%') happen to be used for enforcing primary key
 * constraints instead of a system generated index. as a consequence, these indexes cannot be
 * dropped anymore.
 * In order to fix this, we need to do the following:
 *  - Drop foreign keys referencing the primary keys being enforced by custom indexes
 *  - Drop primary keys being enforced by custom indexes
 *  - Drop custom indexes which contain the primary key column of the dropped primary keys
 *  - Recreate the primary keys. This will cause a system index being generated.
 *  - Recreate the custom indexes
 *  - Recreate the foreign keys
 * See also LOGON-7441.
 */

create table tmp_fk (
  table_name varchar2(30) null,
  constraint_name varchar2(30) null,
  column_name varchar2(30) null,
  r_table_name varchar2(30) null,
  r_column_name varchar2(30) null
);

create table tmp_pk (
  table_name varchar2(30) null,
  constraint_name varchar2(30) null,
  column_list varchar2(300) null
);

create table tmp_ind (
  table_name varchar2(30) null,
  index_name varchar2(30) null,
  column_list varchar2(300) null
);

-- Store the foreign keys and all information required for recreation
insert into tmp_fk (table_name, constraint_name, column_name, r_table_name, r_column_name)
select f_con.table_name, f_con.constraint_name, f_col.column_name, p_con.table_name, p_col.column_name
from user_constraints p_con
inner join user_cons_columns p_col on (p_con.constraint_name = p_col.constraint_name)
inner join user_constraints f_con on (f_con.constraint_type = 'R' and p_con.constraint_name = f_con.r_constraint_name)
inner join user_cons_columns f_col on (f_con.constraint_name = f_col.constraint_name)
where p_con.constraint_type = 'P'
  and p_con.index_name like 'I_%';
  
-- Store the primary keys and all information required for recreation
insert into tmp_pk (table_name, constraint_name, column_list)
select p_con.table_name, p_con.constraint_name,
  listagg(p_col.column_name, ', ') within group (order by p_col.position) column_list
from user_constraints p_con
inner join user_cons_columns p_col on (p_con.constraint_name = p_col.constraint_name)
where p_con.constraint_type = 'P'
  and p_con.index_name like 'I_%'
group by p_con.table_name, p_con.constraint_name;

-- Store the indexes and all information required for recreation
insert into tmp_ind (table_name, index_name, column_list)
select ind.table_name, ind.index_name,
  listagg(i_col2.column_name, ', ') within group (order by i_col2.column_position) column_list
from user_indexes ind
inner join user_ind_columns i_col on (ind.index_name = i_col.index_name) -- to check whether the PK is on an index column
inner join user_ind_columns i_col2 on (i_col.index_name = i_col2.index_name) -- for the column_list
where ind.index_name like 'I_%'
  and exists (select 1 from tmp_pk where tmp_pk.table_name = ind.table_name and instr(tmp_pk.column_list, i_col.column_name) > 0)
group by ind.table_name, ind.index_name;

set serveroutput on;

declare
  cursor fks is
  select table_name,
    constraint_name,
    column_name,
    r_table_name,
    r_column_name
  from tmp_fk;
  
  cursor pks is
  select table_name,
    constraint_name,
    column_list
  from tmp_pk;
  
  cursor inds is
  select table_name,
    index_name,
    column_list
  from tmp_ind;

begin

  dbms_output.enable;

  for cur_fk in fks
  loop
  begin
    execute immediate 'alter table ' || cur_fk.table_name || ' drop constraint ' || cur_fk.constraint_name;
  exception
  when others then
    null;
  end;
  end loop;

  for cur_pk in pks
  loop
  begin
     execute immediate 'alter table ' || cur_pk.table_name || ' drop constraint ' || cur_pk.constraint_name;
  exception
  when others then
    null;
  end;
  end loop;
  
  for cur_ind in inds
  loop
  begin
     dbms_output.put_line('drop index ' || cur_ind.index_name);
     execute immediate 'drop index ' || cur_ind.index_name;
  exception
  when others then
    null;
  end;
  end loop;
  
  for cur_pk in pks
  loop
  begin
     execute immediate 'alter table ' || cur_pk.table_name || ' add primary key (' || cur_pk.column_list || ') using index tablespace index_lgn_a';
  exception
  when others then
    null;
  end;
  end loop;
  
  for cur_ind in inds
  loop
  begin
     dbms_output.put_line('create index ' || cur_ind.index_name || ' on ' || cur_ind.table_name || ' (' || cur_ind.column_list || ') tablespace index_lgn_a');
     execute immediate 'create index ' || cur_ind.index_name || ' on ' || cur_ind.table_name || ' (' || cur_ind.column_list || ') tablespace index_lgn_a';
  exception
  when others then
    null;
  end;
  end loop;

  for cur_fk in fks
  loop
  begin
     execute immediate 'alter table ' || cur_fk.table_name || ' add constraint ' || cur_fk.constraint_name || ' foreign key (' || cur_fk.column_name || ') references ' || cur_fk.r_table_name || ' (' || cur_fk.r_column_name || ')';
  exception
  when others then
    null;
  end;
  end loop;

end;
/

drop table tmp_ind;
drop table tmp_pk;
drop table tmp_fk;
