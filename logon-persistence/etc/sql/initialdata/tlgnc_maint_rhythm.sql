-- $Revision$
-- $Date$
--
prompt '---------- initialdata/tlgnc_maint_rhythm.sql ----------'

prompt INSERTING into TLGNC_MAINT_RHYTHM

-- These are the repetition rules for the 3 initial rhythm types
-- 0 = manual
-- 1 = 12-4-4
-- 2 = 12-6
-- See spec: /share/doc/crh/pegasus/input/Bahn/Concept_for_wagon_maintenance_rules_V1.0_20091215.xls

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 1, 'G3.0', 14, 1, 1, 1, 0, 1, 1, 1, 0, 1, 1, 1, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 1, 'G4.0', 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 1, 'G4.8', 14, 0, 0, 0, 1, 0, 0, 0, 1, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 1, 'P5', 14, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 1, 'C', 14, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');


insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 2, 'G2.0', 14, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 2, 'G4.0', 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 2, 'G4.2', 14, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 2, 'P5', 14, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'P5');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 2, 'C', 14, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'P5');


insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 3, 'G2.0', 14, 1, 1, 1, 1, 1, 0, 1, 1, 1, 1, 1, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 3, 'G4.0', 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 3, 'G4.2', 14, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 3, 'P5', 14, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'P5');


insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 4, 'C', 14, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'C');
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'P5');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 4, 'G1.0', 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G1.0');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 4, 'G2.0', 14, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G2.0');
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.2');
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'P5');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 4, 'G4.0', 14, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 1);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.2');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 4, 'G4.2', 14, 0, 0, 0, 0, 0, 1, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.0');
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G4.2');

insert into tlgnc_maint_rhythm (maint_rhythm_id, maint_rhythm_type_cd, maint_type_cd, activation_period_d, year_1_flag, year_2_flag, year_3_flag, year_4_flag, year_5_flag, year_6_flag, year_7_flag, year_8_flag, year_9_flag, year_10_flag, year_11_flag, year_12_flag)
		 values (slgnc_maint_rhythm.nextval, 4, 'P5', 14, 1, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0);
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'G2.0');
insert into tlgnc_maint_rhythm_depend (maint_rhythm_depend_id, maint_rhythm_cd, depend_maint_type_cd)
		 values (slgnc_maint_rhythm_depend.nextval, slgnc_maint_rhythm.currval, 'P5');

commit;
