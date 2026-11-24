COLUMN spool_file_name NEW_VALUE spool_file_name NOPRINT

--log file name like [connected user]__[SID fullname]__[patch_name]__[timestamp].log
select  
'/temp/' 
    || &&patch_file_name    || '__' 
    || user || '__' 
    ||  global_name || '__' 
    || to_char(sysdate, 'yyyymmddHH24MISS') || '.log'
  as spool_file_name from (select global_name from global_name), dual;

SPOOL &spool_file_name
prompt Spooling into file &&spool_file_name
