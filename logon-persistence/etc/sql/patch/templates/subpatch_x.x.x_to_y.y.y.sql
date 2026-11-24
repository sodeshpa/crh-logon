/**
 * WARNING!!! DO NOT RUN THIS FILE IN ITSELF. ALWAYS RUN THE MASTER SCRIPT __ORIGINAL_PATCHFILE__
 */

COLUMN subpatch_spool_file_name NEW_VALUE subpatch_spool_file_name NOPRINT

--log file name like [connected user]__[SID fullname]__[patch_name]__[timestamp].log
select  
'/temp/' 
    || user || '__' 
    ||  global_name || '__' 
    || 'logon_subpatch____FROM_VERSION___to___TO_VERSION__'    || '__' 
    || to_char(sysdate, 'yyyymmddHH24MISS') || '.log'
  as subpatch_spool_file_name from (select global_name from global_name), dual;

SPOOL &subpatch_spool_file_name
prompt Patch log available at &&subpatch_spool_file_name

prompt
prompt ************************************************************************
prompt *** Patching Logon Database __FROM_VERSION__ -> __TO_VERSION__ (patch in production)
prompt ************************************************************************
prompt
prompt This patch script is to be applied to logon production databases in LP0, LP1 and
prompt LP2 with DB version __FROM_VERSION__. It will upgrade the DB to version __TO_VERSION__.


-- ^^^ Add Patches HERE ^^^, COPY the "template" first from below. Be careful with dependencies amongst individual patches!

-- BEGIN TEMPLATE
----------------------------------------------------------------------------------------------------------------|
-- LOGON-XXXX                                                                                                   |
-- LRQ xyz.u : Name / Description                                                                               |
-- Summary of the things your are patching, etc,                                                                |
----------------------------------------------------------------------------------------------------------------|

-- ... patch

-- END TEMPLATE

commit;
COLUMN subpatch_spool_file_name CLEAR 
