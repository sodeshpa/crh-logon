-- credbposttask.sql
-- Script is called when creating the db instance. It is independent of the schema owner/users.
-- Variables:
--  1: data pump directory, for example R:\orabackup\PLGN01\dpdump or /u00/app/adnoracle/admin/DCHLGN01/dpdump
--  2: images directory, for example C:\adnoracle\admin\PLGN01\scripts\images or /u00/app/adnoracle/admin/DCHLGN01/scripts/images

drop directory DATA_PUMP_DIR;
create directory DATA_PUMP_DIR as '&1';
grant read,write on directory DATA_PUMP_DIR to dba;

create directory IMAGES_DIR as '&2';
grant read,write on directory IMAGES_DIR to ulgn01;

alter profile default limit FAILED_LOGIN_ATTEMPTS unlimited;
alter profile default limit PASSWORD_GRACE_TIME unlimited;
alter profile default limit PASSWORD_LOCK_TIME unlimited;
alter profile default limit PASSWORD_LIFE_TIME unlimited;
alter profile default limit PASSWORD_LIFE_TIME unlimited;

alter system set standby_recovery_dest='' scope=both;


