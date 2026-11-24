-- $Revision$
-- $Date$
--
--LOGON-6613: Configuration Table for Mapping Material Packaging Types

prompt '---------- initialdata/tlgns_matpkg_map.sql ----------'


prompt INSERTING into TLGNS_MATPKG_MAP_RULE and TLGNS_MATPKG_MAP_COND

-- RULE 1 - Cement Bulk Powder + special case buled dump (LOGON-6281 Adjust material import logic for clinker materials)

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 11, 'S006', 'clinker materials');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZCEM');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MATKL', '010801');

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 12, 'S006', 'Clinker materials');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZCEM');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MATKL', '010802');

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 13, 'S006', 'Clinker materials');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZCEM');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MATKL', '010899');

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 14, 'S001', 'Cement Bulk Powder');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZCEM');

-- RULE 2 - Landfill

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 21, 'S006', 'Landfill');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZNST');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'KONDM', '32');

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 22, 'S006', 'Landfill');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZNST');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'KONDM', '33');

-- RULE 3 - Fly-Ash

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 31, 'S001', 'Fly-Ash');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZRAW');
INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 32, 'S001', 'By Pass Dust');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZSMF');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MATKL', '010702');
INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 33, 'S001', 'Limestone GC');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZSMF');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MATKL', '010702');
INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 34, 'S001', 'Raw Meal');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZNVL');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MATKL', '059999');

-- RULE 4 - Clinker (a semi-finished cement product)

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 41, 'S006', 'Clinker (a semi-finished cement product)');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZSMF');

-- RULE 5 - Trading material Bulk

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 51, 'S001', 'Trading material Bulk');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZTRA');

-- RULE 6 - Cement Bag

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 61, 'S002', 'Cement Bag');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BAG');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZCEM');

-- RULE 7 - Aggregate Bag

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 71, 'S002', 'Aggregate Bag');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BAG');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZAGG');

-- RULE 8 - Trading material Bag

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 81, 'S002', 'Trading material Bag');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BAG');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZTRA');

-- RULE 9 - Used in Italy "drymix" bag product (cement + sand + aggregate). Technically "Concrete Products" (ZCPR)

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 91, 'S002', 'Used in Italy "drymix" bag product (cement + sand + aggregate). Technically "Concrete Products" (ZCPR)');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BAG');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZCPR');

-- RULE 10 - BAF is used for cement packaed in bags, with plastic film cover(non-returnable) pallet included (LOGON-5847)

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 101, 'S002', 'BAF is used for cement packaed in bags, with plastic film cover(non-returnable) pallet included');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BAF');

-- RULE 11 - Filler (If material product hierarchy level 5 = A10AG30OA100010040the material is a filler materialshould therefore be classified as S001

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 111, 'S001', 'Filler');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZAGG');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'PRDHA', 'A10AG30OA100010040');

-- RULE 12 - Aggregate Bulk (LOGON-5848)

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 121, 'S006', 'Aggregate Bulk');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZAGG');

-- RULE 13 - Service material rule

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 131, 'S005', 'Service material rule');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZSRV');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTPOS', 'ZSER');
INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 132, 'S005', 'Consignment Service material');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZSRV');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTPOS', 'ZCHS');

-- RULE 14 - Empty pallet rule

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 141, 'S003', 'Empty pallet rule');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZPAC');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTPOS', 'ZPAC');

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 142, 'S003', 'Empty pallet rule');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZPAC');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTPOS', 'ZPAL');

-- RULE 15 - Other Raw materials (Bulk dumped)

INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 151, 'S006', 'Coal, Graphite (ZFUE)');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZFUE');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MATKL', '020101');
INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 152, 'S006', 'Petrol Coke');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZFUE');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MATKL', '020102');
INSERT INTO TLGNS_MATPKG_MAP_RULE (MATPKG_MAP_RULE_ID, MATPKG_MAP_RULE_NO, MATERIAL_PACKAGE_CD, RULE_COMMENT) VALUES (slgns_matpkg_map_rule.nextval, 153, 'S006', 'Scrap');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MVGR2', 'BLK');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MTART', 'ZNVL');
INSERT INTO TLGNS_MATPKG_MAP_COND (MATPKG_MAP_COND_ID, MATPKG_MAP_RULE_ID, FIELD_NAME, FIELD_VALUE) VALUES (slgns_matpkg_map_cond.nextval, slgns_matpkg_map_rule.currval, 'MATKL', '059999');

commit;
