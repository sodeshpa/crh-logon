prompt Spooling into file /temp/logon_drop_db_01_user.log
spool /temp/logon_drop_db_01_user.log

set define off
set echo on
set feedback off
set verify off
set serveroutput on

begin
   for i in (select sid,serial# from v$session where username = upper('ulgn03')) loop
      execute immediate 'alter system kill session '''||i.sid||','||i.serial#||'''';
   end loop;
end;
/
exec dbms_lock.sleep(2);
prompt
prompt drop user ulgn03;
prompt
drop user ulgn03;

begin
   for i in (select sid,serial# from v$session where username = upper('ulgn02')) loop
      execute immediate 'alter system kill session '''||i.sid||','||i.serial#||'''';
   end loop;
end;
/
exec dbms_lock.sleep(2);
prompt
prompt drop user ulgn02;
prompt
drop user ulgn02;

begin
   for i in (select sid,serial# from v$session where username = upper('ulgn01')) loop
      execute immediate 'alter system kill session '''||i.sid||','||i.serial#||'''';
   end loop;
end;
/
exec dbms_lock.sleep(2);
prompt
prompt drop user ulgn01 cascade;
prompt
drop user ulgn01 cascade;

begin
   for i in (select sid,serial# from v$session where username = upper('ulgnsap')) loop
      execute immediate 'alter system kill session '''||i.sid||','||i.serial#||'''';
   end loop;
end;
/
exec dbms_lock.sleep(2);
prompt
prompt drop user ulgnsap cascade;
prompt
drop user ulgnsap cascade;


drop role rl_lgn_login;
drop role rl_lgn_user;
drop role rl_lgn_owner;
drop role rl_lgn_etl;
drop role rl_lgn_db_analizer;
