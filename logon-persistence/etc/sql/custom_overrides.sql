-- Manual overrides / corrections / extensions to generated SQL which cannot (yet) be done in the mapping files
-- This file MUST be save against repeated execution on any database and is called by each patch,
-- as well (originally) for first time DB creation.
--
-- Note: If "overrides" would be needed which cannot be executed again and again,
-- those are probably WRONG to be place here and should rather go into the patch
-- script of that version directly (or, a seperate file like custom_create_move_blobs.sql)

prompt '----- custom_overrides.sql -----'

prompt *** i_bizevent_1: include ctl_cre_ts desc.
drop index i_bizevent_1;
create index i_bizevent_1 on tlgnc_bizevent (truck_id, ctl_cre_ts desc) tablespace index_lgn_a;

prompt *** i_bizevent_2: include ctl_cre_ts desc.
drop index i_bizevent_2;
create index i_bizevent_2 on tlgnc_bizevent (trailer_id, ctl_cre_ts desc) tablespace index_lgn_a;

-- dave, 21.9.2012: This is needed for the dispokarte truck selection update, LOGON-5187
prompt *** tlgnc_shsarea_restr_truck_type.UQ_TLGNC_SHSAREA_RESTR_TRUCK_T : make deferred.
alter table TLGNC_SHSAREA_RESTR_TRUCK_TYPE drop constraint UQ_TLGNC_SHSAREA_RESTR_TRUCK_T;
alter table TLGNC_SHSAREA_RESTR_TRUCK_TYPE add constraint UQ_TLGNC_SHSAREA_RESTR_TRUCK_T unique (SHIPTO_SAREA_RESTR_ID, TRUCK_TYPE_CD) deferrable initially deferred using index tablespace index_lgn_a;
