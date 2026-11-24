/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */


create table tmp_indexes (
  table_name varchar2(30) null,
  constraint_name varchar2(30) null,
  index_name varchar2(30) null
);

create table tmp_unique_indexes (  
  index_name varchar2(30) null
);


-- Store remaining constraints and related indexes
insert into tmp_indexes ( table_name, constraint_name, index_name)
select uc.table_name, uc.constraint_name, ui.index_name  
from user_constraints uc 
inner join user_indexes ui on 
	uc.constraint_name != ui.index_name 
	and uc.index_name = ui.index_name
	and uc.table_name like 'TLGN%'
  and uc.constraint_type = 'U';

-- Store remaining constraints and related indexes
insert into tmp_unique_indexes ( index_name)
select ui.index_name  
from user_indexes ui 
where ui.index_name like 'UQ%';


set serveroutput on;

declare
  cursor fks is
  select table_name,
  		 constraint_name,
  		 index_name
  from tmp_indexes;

begin

  dbms_output.enable;

  for cur_fk in fks
  loop
  begin
   dbms_output.put_line('alter table ' || cur_fk.table_name || ' drop constraint ' || cur_fk.constraint_name);
    execute immediate 'alter table ' || cur_fk.table_name || ' drop constraint ' || cur_fk.constraint_name;
  exception
  when others then
    null;
  end;
  begin
    dbms_output.put_line('drop index ' || cur_fk.index_name);
    execute immediate 'drop index ' || cur_fk.index_name ;
  exception
  when others then
    null;
  end;
  end loop;

end;
/

declare
  cursor fksi is
  select index_name
  from tmp_unique_indexes;

begin

  dbms_output.enable;

  for cur_fk in fksi
  loop
  begin
    dbms_output.put_line('drop index ' || cur_fk.index_name);
    execute immediate 'drop index ' || cur_fk.index_name ;
  exception
  when others then
    null;
  end;
  end loop;

end;
/

drop table tmp_indexes;
drop table tmp_unique_indexes;
