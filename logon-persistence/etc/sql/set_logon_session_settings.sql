-- Set the required session parameters so that refdata and testdata is loaded correctly.
--
-- Those settings must correspond to the SYSTEM settings, which can be checked with
--   show parameter nls
-- SYSTEM parameters can be changed as sys with
--   alter system set nls_length_semantics = 'CHAR' scope=both;
-- SESSION parameters are configured here in this file.
--
-- This file must be called by each SQL file which may be called independently.
-- If the SQL file re-connects, set_logon_session_settings.sql must be called again, as settings are lost by a re-connect.

alter session set nls_language=AMERICAN;
alter session set nls_territory=AMERICA;

-- length semantics is important for UTF-8, when diacretics etc take two bytes per 1 character.
alter session set nls_length_semantics=CHAR;


-- interesting for explicitely written SQL script
-- To query Oracle Parameters, execute as sys (Example)

-- Example 1: Parameters which can be modified with 'alter session':
-- SQL> select name,value,isdefault,ISSES_MODIFIABLE from v$parameter where ISSES_MODIFIABLE ='TRUE';

-- Example 2: Query for a certain oracle parameter
-- SQL> select name,value,isdefault,ISSES_MODIFIABLE from v$parameter where name ='cursor_sharing'; 
ALTER SESSION SET CURSOR_SHARING=SIMILAR;

-- more readable output for selects etc.
set linesize 1000
set trimout on
set trimspool on

-- To have nice date/time during output.
alter session set nls_date_format = 'YYYY-MM-DD HH24:MI:SS';

