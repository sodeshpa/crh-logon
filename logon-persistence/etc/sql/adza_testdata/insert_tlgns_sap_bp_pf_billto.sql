
-- Only the relevant soldto-billto PF - restrict to the billto which can also be soldto, and extend in case some BP is not useable.
prompt Inserting tlgns_sap_bp_pf_billto: required Partner Function BILLTO for test-contracts based from CHE prod data

insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-32132         , -32132                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10'));
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-33522         , -33522                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10'));
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-101725        , -101725               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10'));
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-103826        , -103826               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10'));
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-121           , -121                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'ZA01-10-10'));
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-122           , -122                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11'));
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-122           , -222                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11'));

insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                                                                                      ) values
(-32132         , -31966                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                                                                                      ) values
(-33522         , -31966                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );

-- Customer order block
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-124           , -124                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-125           , -125                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-126           , -126                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-127           , -127                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-128           , -128                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-129           , -129                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );

--insert billto partner function for the new rail capable shipto+soldto pair
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                                                                                      ) values
(-203826        , -203826               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );

-- Container terminal customer (billto, shipto and payer in one)
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                                                                                      ) values
(-202723        , -202723               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );

insert into tlgns_sap_bp_pf_billto
(sap_bp_sarea_id, billto_sap_bp_sarea_id, sarea_id                                                                       ) values
(-12            , -12                   , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
-- payer/billto for Albula in AD01-20-11
insert into tlgns_sap_bp_pf_billto
(sap_bp_sarea_id, billto_sap_bp_sarea_id, sarea_id                                                                       ) values
(-13            , -13                   , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD01-20-11') );
insert into tlgns_sap_bp_pf_billto
(sap_bp_sarea_id, billto_sap_bp_sarea_id, sarea_id                                                                       ) values
(-142           , -142                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-30') );

-- Payer + billto for one time shipto
insert into tlgns_sap_bp_pf_billto
(sap_bp_sarea_id, billto_sap_bp_sarea_id, sarea_id                                                                       ) values
(-130           , -222                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );


-- Terminal Applicaion Test Soldto's billto partner functions
insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-146           , -146                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD03-20-20'));

insert into tlgns_sap_bp_pf_billto
(SAP_BP_SAREA_ID, BILLTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-146           , -31966                , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD03-20-20'));

