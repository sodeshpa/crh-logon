-- Fake costs to test basic functionality of IUD cost element.
-- TODO: Replace with real data from Crh production, as soon as possible (to get comparable numbers)

prompt INSERTING into TLGNC_IUD_COST

insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (-12, '8101', '8104', 'TO', 'S002' /*Bag*/ , 50, 70, to_date('20100101', 'YYYYMMDD'), 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (-13, '8104', '8101', 'TO', 'NULL' /*Bag*/ , 50, 70, to_date('20100101', 'YYYYMMDD'), 'CHF');

insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (-14, '8101', '8VAD', 'TO', 'S002' /*Bag*/ , 50, 70, to_date('20100101', 'YYYYMMDD'), 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (-15, '8VAD', '8101', 'TO', 'NULL' /*Bag*/ , 50, 70, to_date('20100101', 'YYYYMMDD'), 'CHF');

insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (-16, '9VZA', '9101', 'TO', 'S002' /*Bag*/ , 50, 70, to_date('20100101', 'YYYYMMDD'), 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (-17, '9101', '9VZA', 'TO', 'NULL' /*Bag*/ , 50, 70, to_date('20100101', 'YYYYMMDD'), 'CHF');


-- Data from Crh.

-- The relevant entries were replaced by real data in prod database.
-- Therefore, remove all, unless there are certain tests that require them (add again, if necessary, but only those)
/*
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (100, 'S101', 'S106', 'TO', 'S001', 0, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (101, 'S101', 'S104', 'TO', 'S001', 22.22, 22.22, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (102, 'S101', 'S112', 'TO', 'S002', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (103, 'S101', 'S117', 'TO', 'S002', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (104, 'S101', 'S134', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (105, 'S101', 'S128', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (106, 'S108', 'S106', 'TO', 'S001', 0, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (107, 'S108', 'S104', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (108, 'S108', 'S111', 'TO', 'S002', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (109, 'S108', 'S112', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (110, 'S108', 'S118', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (111, 'S130', 'S106', 'TO', 'S001', 0, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (112, 'S130', 'S132', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (113, 'S130', 'S150', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (114, 'S130', 'S142', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (115, 'S131', 'S150', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (116, 'S165', 'S106', 'TO', 'S001', 0, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (117, 'S165', 'S164', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (118, 'S997', 'S106', 'TO', 'S001', 0, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (119, 'S996', 'S106', 'TO', 'S001', 0, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (120, 'S996', 'S162', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (121, 'S998', 'S106', 'TO', 'S001', 0, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (122, 'S998', 'S162', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (123, 'S999', 'S106', 'TO', 'S001', 0, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (124, 'S999', 'S162', 'TO', 'S001', 22.22, null, null, 'CHF');
insert into tlgnc_iud_cost (iud_cost_id, from_plant_cd, to_plant_cd, uom_cd, material_package_cd, var_iud_cost, future_var_iud_cost, future_valid_from_dat, currency_cd) values
  (125, 'S140', 'S106', 'TO', 'S001', 0, null, null, 'CHF');
*/

commit;