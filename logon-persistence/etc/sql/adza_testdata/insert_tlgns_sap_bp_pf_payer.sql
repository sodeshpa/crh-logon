prompt Inserting tlgns_sap_bp_pf_payer: required Partner Function PAYER for test-contracts: Identical to BILLTO to start with. (even same IDs)
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-32132         , -32132               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-33522         , -33522               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-101725        , -101725              , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-103826        , -103826              , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-121           , -121                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'ZA01-10-10') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-122           , -122                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-122           , -222                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
-- Customer order block
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-124           , -124                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-125           , -125                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-126           , -126                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-127           , -127                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-128           , -128                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-129           , -129                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );

--insert payer partner function for the new rail capable shipto + soldto pair
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-203826        , -203826              , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );

-- Container terminal customer (billto, shipto and payer in one)
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-202723        , -202723              , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );

insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-12            , -12                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-13            , -13                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD01-20-11') );
insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                       ) values
(-142           , -142                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-30') );

insert into tlgns_sap_bp_pf_payer
(sap_bp_sarea_id, payer_sap_bp_sarea_id, sarea_id                                                                      ) values
(-146           , -146                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD03-20-20'));
