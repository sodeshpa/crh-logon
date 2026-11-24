-- $Revision$
-- $Date$
--
-- Technical reference data, only extend together with application change!
-- Any value here must match a specific use case/rule in the java code.
--
-- Business references:
-- - LRQ 279.4 "Sequential Numbering for Delivery Notes & Export Invoices", LOGON-4618
-- - LRQ 077.0 "AGG Cash Sales", LOGON-4777

-- Default usage type
insert into tlgnr_seq_usage_bs (seq_usage_cd, seq_usage_desc, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (0, 'Default usage type', 1, 1, systimestamp, 1, systimestamp);

-- Cash sales usage type
insert into tlgnr_seq_usage_bs (seq_usage_cd, seq_usage_desc, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (1, 'Cash sales usage type', 1, 1, systimestamp, 1, systimestamp);

-- Export invoice usage type
insert into tlgnr_seq_usage_bs (seq_usage_cd, seq_usage_desc, ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (2, 'Export invoice usage type: If the order is based on an export invoice contract (contract type = ZCEX) then at the time the order gets loaded the next number is retrieved and persisted on the order.', 1, 1, systimestamp, 1, systimestamp);

commit;
