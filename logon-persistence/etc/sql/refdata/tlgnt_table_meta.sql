-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnt_column_meta.sql ----------'

prompt Overriding generated tlgnt_table_meta entries with special restrictions.

-- This table contains all audit of tablebrowser actions. Prevent that a tablebrowser user can modify and remove his traces.
delete from tlgnt_table_meta where table_name='TLGNC_AUDIT';
update tlgnt_table_meta set delete_allowed=0, ctl_mod_ts = systimestamp, ctl_mod_uid=1 where table_name='TLGNR_CONFIG';

-- For the lookup of wagon -> wagon types we do not display the usual 'wagon_type_tx_cd (wagon_type_tx.txt)'
-- but just the value WAGON_TYPE_BS.WAGON_TYPE_NAME:
update tlgnt_table_meta set referenced_field = 'WAGON_TYPE_NAME', ctl_mod_ts = systimestamp, ctl_mod_uid=1 where table_name = 'TLGNR_WAGON_TYPE_BS';
-- Because of a minor bug in tablebrowser (metadata not correctly respected in edit, when rendering the dropdown), simply set to 'edit as popup':
update tlgnt_table_meta set use_dropdown = 0, ctl_mod_ts = systimestamp, ctl_mod_uid=1 where table_name = 'TLGNR_WAGON_TYPE_BS';

commit;
