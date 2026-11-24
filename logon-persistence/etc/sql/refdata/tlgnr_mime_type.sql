-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_mime_type.sql ----------'

-- Extend list as needed.
prompt INSERTING into TLGNR_MIME_TYPE_BS
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'image/gif',              'gif', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'image/jpeg',             'jpg', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (3, 'image/png',              'png', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (4, 'application/pdf',        'pdf', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (5, 'application/postscript', 'ps',  1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (6, 'image/x-png',            'png', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (7, 'text/csv',               'csv', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (8, 'application/vnd.ms-excel','xls', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (9, 'application/msword',		'doc', 1, 1, systimestamp, 1, systimestamp);
insert into tlgnr_mime_type_bs(mime_type_cd, mime_type, file_extension, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (10, 'text/plain',				'txt', 1, 1, systimestamp, 1, systimestamp);


commit;
