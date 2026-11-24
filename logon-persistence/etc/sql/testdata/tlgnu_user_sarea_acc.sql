prompt INSERTING into tlgnu_user_companycode_acc and tlgnu_user_sarea_acc - AdNovum Testdata only!

/*
In LOGON 1.6, a user could be restricted to a sales area directly. Example: only on AD02/10/10.
With LOGON 2.0, a sales area restriction is now always part within a COMPANY Code.
I.e, if restricted on a sales area, that must be within the restriction of a COMPANY Code.
This is a similar concept as soldto->Shipto user_soldto/shipto_acc tables: Shipto is a sub-selection of an existing soldto_acc

Therefore, we first create an entry in user_companycode_acc before we can add one in user_sarea_acc_id
*/

insert into tlgnu_user_companycode_acc (
    user_companycode_acc_id,
    user_id,
    companycode_cd,
    ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
) values (
    slgnu_user_companycode_acc.nextval,
    -14,
    'AD02',
    1, systimestamp, 1, systimestamp, 0
);

insert into tlgnu_user_sarea_acc (
    user_sarea_acc_id,
    user_id,
    sarea_id,
    user_companycode_acc_id,
    ctl_cre_uid, ctl_cre_ts , ctl_mod_uid, ctl_mod_ts , ctl_tcn
) values (
    slgnu_user_sarea_acc.nextval,
    -14,
    (select sarea_id from tlgnc_sarea where sorg_cd='AD02' and distribution_channel=10 and division_cd=10),
    slgnu_user_companycode_acc.currval,
    1, systimestamp, 1, systimestamp, 0
);
    
commit;
