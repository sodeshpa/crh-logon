prompt Inserting tlgns_sap_bp_pf_shipto: required Partner Function SHIPTO for test-contracts based from CHE prod data

insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-32132         , -1544                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-33522         , -1743                 , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
-- Soldto = 'ADN Kieswerk Albula AG' in AD02-10-11, Shipto = 'ADN Betonanlage Albula' in AD02-10-11
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-12            , -11                   , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );

-- Shipto = Soldto = 'ADN Kieswerk Albula AG' in AD01-20-11
-- This is a shipto=soldto case. Also, the shipto is in another sales area (AD01-20-11 than the other shipto for this soldto ('ADN Kieswerk Albula AG' in AD02-10-10 and AD02-10-11)).
-- The contracts 1209037-03/1209037-04 apply to AD02-20-10, 1209037-05 to AD01-20-11 and 1209037-06 (soldto=shipto!) to AD01-20-11
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-13            , -13                   , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD01-20-11') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-101725        , -105148               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-101725        , -105165               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-103826        , -103829               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );

--fake return sales area for ADN Untervaz
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-103827        , -103830               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD90-10-10') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-121           , -120                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'ZA01-10-10') );

-- Soldto = 'ADN One-time Soldto' in AD02-10-11, Shipto = 'ADN Shipto for One-Time Soldto' in AD02-10-11
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-122           , -130                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11')  );

-- Soldto = 'ADN Kieswerk Albula AG' in AD02-10-11, Shipto = 'ADN One-time Shipto (LNF)' in AD02-10-11
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-12            , -123                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11')  );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-142           , -144                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-30') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-144           , -144                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-30') );

-- Soldto = 'ADN One-time Soldto' in AD02-10-11, Shipto = 'ADN One-time Shipto 2 (LNF)' in AD02-10-11
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-122           , -137                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11')  );

-- Customer order block / Soldtos in AD02-10-11 connected to their own shipto
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-124           , -131                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11')  );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-125           , -132                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11')  );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-126           , -133                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11')  );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-127           , -134                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11')  );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-128           , -135                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11')  );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-129           , -136                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11')  );

-- ADN Container Terminal Truck + Rail
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-101725        , -106986               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10')  );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-101725        , -102724               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10')  );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                        ) values
(-202723        , -102723               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10')  );

--ADN Contract Hierarchies
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-33522         , -138                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-12            , -139                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-11') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-13            , -140                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD01-20-11') );

--ADN Contract hierarchies for landfill and agg AD02-10-30
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-142           , -141                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-30') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-142           , -142                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-30') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-141           , -141                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-30') );

insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-143           , -143                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-30') );
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-142           , -143                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-30') );

--insert shipto pf between the new rail capable shipto+soldto
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                       ) values
(-203826        , -203829               , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD02-10-10') );

-- Terminal Applicaion Test Shipto's partner function
insert into tlgns_sap_bp_pf_shipto
(SAP_BP_SAREA_ID, SHIPTO_SAP_BP_SAREA_ID, SAREA_ID                                                                      ) values
(-146           , -145                  , (select sarea_id from tlgnc_sarea where sarea_display_longname = 'AD03-20-20'));
