
/**
 * Conditionally executing './subpatch___FROM_VERSION___to___TO_VERSION__.sql'
 */

set define on

column sub_patch_script_name new_value sub_patch_script_name noprint;

select case
  when
    regexp_replace(version, '(rc)?.(.)?$','') in ('__FROM_VERSION__')
  then
    './subpatch___FROM_VERSION___to___TO_VERSION__.sql'
  else
    './subpatch___FROM_VERSION___to___TO_VERSION___skipped.sql'
  end as sub_patch_script_name
from tlgnc_db_version_log
where install_end_ts = (select max(install_end_ts) from tlgnc_db_version_log);

@&sub_patch_script_name

spool &&spool_file_name append

set define off
