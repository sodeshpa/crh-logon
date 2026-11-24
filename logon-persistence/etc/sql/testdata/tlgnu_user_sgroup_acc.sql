prompt INSERTING into tlgnu_user_sgroup_acc - AdNovum Testdata only!

prompt Testuser -14 (markus) with sgroup restriction to 'AD0'.
insert into tlgnu_user_sgroup_acc (
  user_sgroup_acc_id, user_id, sgroup_cd,
  ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
) values (
  slgnu_user_sgroup_acc.nextval, -14, 'AD0',
  1, systimestamp, 1, systimestamp, 0
);

commit;
