-- $Revision$
-- $Date$
--
-- List based on additional "new" wagons from Crh:
--   /share/doc/crh/pegasus/input/Bahn:
--   - Containertragwagen.xls -> RHB - most seem to be in the DB already???
--   - Stammdaten_Containerwagon_ADN47233_20091121.xls
-- These lists are minimal in the number of columns, therefore not done as CSV/GDI "upsert" Scripts.
-- The real data (all attributes) is maintained by Crh via tablebrowser export/import.

prompt '---------- initialdata/tlgnc_wagon_1.2.sql ----------'

prompt INSERTING initial 1.2 Wagons into TLGNC_WAGON

-- insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts)
-- values (slgnc_wagon.nextval, lpad('7716', 12, '0'), 13 /* RhB_Sb-v */, 1, 18 /* RhB */, 14.4, 24, 0, 0, 1, systimestamp, 1, systimestamp);

/* There were already in the DB?? 
7716    RhB_Sb-v    RhB     1   14.4    24  TO  0
7717    RhB_Sb-v    RhB     1   14.4    24  TO  0
7718    RhB_Sb-v    RhB     1   14.4    24  TO  0
7719    RhB_Sb-v    RhB     1   14.4    24  TO  0
7720    RhB_Sb-v    RhB     1   14.4        TO  0
7721    RhB_Sb-v    RhB     1   14.4        TO  0
7722    RhB_Sb-v    RhB     1   14.4        TO  0
7723    RhB_Sb-v    RhB     1   14.4        TO  0
7724    RhB_Sb-v    RhB     1   14.4        TO  0
7725    RhB_Sb-v    RhB     1   14.4        TO  0
7726    RhB_Sb-v    RhB     1   14.4        TO  0
7727    RhB_Sb-v    RhB     1   14.4        TO  0
7728    RhB_Sb-v    RhB     1   14.4        TO  0
7729    RhB_Sb-v    RhB     1   14.4        TO  0
7730    RhB_Sb-v    RhB     1   14.4        TO  0
*/

prompt Some of those are duplicates - Ignore those errors (or better: Clean script before going productive!)

--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65675', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65676', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65677', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65678', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65679', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65680', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65681', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65682', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65683', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65684', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65685', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65686', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65687', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65688', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);
--insert into tlgnc_wagon (wagon_id, wagon_no, wagon_type_cd, active_flag, wagon_keeper_id, tare_weight_t, max_gross_weight_t, volume_m3, maint_rhythm_type_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts) values (slgnc_wagon.nextval, lpad('65689', 12, '0'), 13 /* RhB_Sb-v - 'RhB_Sb' does not exist. */, 1, 18 /* RhB */, 15.5, 24, 0, 0, 1, systimestamp, 1, systimestamp);

commit;

-- SBB Container:

-- 3185 4552 000-5 Sgns    SBB Cargo       0   20  90  TO  0  44  52  62  70      14.2    19.74
-- 3185 4552 001-3 Sgns    SBB Cargo       0   20  90  TO  0  44  52  62  70      14.2    19.74
-- ..

set serveroutput on

create or replace procedure insert_cont_wagon (
  v_wagon_no in varchar2
) is
  v_wagon_no_logon  varchar2(12);
  v_rfidobject_id   number;
begin
  dbms_output.enable(100000);
  v_wagon_no_logon := regexp_replace (v_wagon_no, '[[:punct:][:blank:]]');

  -- Insert RFID object
  v_rfidobject_id := slgnc_rfidobject.nextval;
  insert into tlgnc_rfidobject(
    rfidobject_id,
    rfidobject_type_cd,
    object_label,
    ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
  ) values (
    v_rfidobject_id,
    7 /* type wagon */,
    v_wagon_no_logon,
    1, systimestamp, 1, systimestamp, 0
  );

  insert into tlgnc_wagon (
    wagon_id,
    wagon_no,
    wagon_type_cd,
    active_flag,
    wagon_keeper_id,
    tare_weight_t,
    max_gross_weight_t,
    volume_m3,
    loading_limit_a_t,
    loading_limit_b_t,
    loading_limit_c_t,
    loading_limit_d_t,
    wheelbase_bpp_mm,
    buffer_length_mm,
    maint_rhythm_type_cd,
    rfidobject_id,
    ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
  ) values (
    slgnc_wagon.nextval,
    v_wagon_no_logon,
    12 /* Sgns */,
    0 /*inactive*/,
    19 /* SBB Cargo */,
    20,
    90 /*gross*/,
    0 /*vol*/,
    44, 52, 62, 70,
    14.2, 19.74,
    0,
    v_rfidobject_id,
    1, systimestamp, 1, systimestamp
  );
  dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnc_wagon with new id=' || slgnc_wagon.currval || '; rfidobject_id=' || v_rfidobject_id || '; wagon_no=' || v_wagon_no_logon || '.');
end insert_cont_wagon;
/
show errors;

/* Wagons from Switzerland
exec insert_cont_wagon('3185 4552 000-5');
exec insert_cont_wagon('3185 4552 001-3');
exec insert_cont_wagon('3185 4552 002-1');
exec insert_cont_wagon('3185 4552 003-9');
exec insert_cont_wagon('3185 4552 004-7');
exec insert_cont_wagon('3185 4552 005-4');
exec insert_cont_wagon('3185 4552 006-2');
exec insert_cont_wagon('3185 4552 007-0');
exec insert_cont_wagon('3185 4552 008-8');
exec insert_cont_wagon('3185 4552 009-6');
exec insert_cont_wagon('3185 4552 010-4');
exec insert_cont_wagon('3185 4552 011-2');
exec insert_cont_wagon('3185 4552 012-0');
exec insert_cont_wagon('3185 4552 013-8');
exec insert_cont_wagon('3185 4552 014-6');
exec insert_cont_wagon('3185 4552 015-3');
exec insert_cont_wagon('3185 4552 016-1');
exec insert_cont_wagon('3185 4552 017-9');
exec insert_cont_wagon('3185 4552 018-7');
exec insert_cont_wagon('3185 4552 019-5');
exec insert_cont_wagon('3185 4552 020-3');
exec insert_cont_wagon('3185 4552 021-1');
exec insert_cont_wagon('3185 4552 022-9');
exec insert_cont_wagon('3185 4552 023-7');
exec insert_cont_wagon('3185 4552 024-5');
exec insert_cont_wagon('3185 4552 025-2');
exec insert_cont_wagon('3185 4552 026-0');
exec insert_cont_wagon('3185 4552 027-8');
exec insert_cont_wagon('3185 4552 028-6');
exec insert_cont_wagon('3185 4552 029-4');
exec insert_cont_wagon('3185 4552 030-2');
exec insert_cont_wagon('3185 4552 031-0');
exec insert_cont_wagon('3185 4552 032-8');
exec insert_cont_wagon('3185 4552 033-6');
exec insert_cont_wagon('3185 4552 034-4');
exec insert_cont_wagon('3185 4552 035-1');
exec insert_cont_wagon('3185 4552 036-9');
exec insert_cont_wagon('3185 4552 037-7');
exec insert_cont_wagon('3185 4552 038-5');
exec insert_cont_wagon('3185 4552 039-3');
exec insert_cont_wagon('3185 4552 040-1');
exec insert_cont_wagon('3185 4552 041-9');
exec insert_cont_wagon('3185 4552 042-7');
exec insert_cont_wagon('3185 4552 043-5');
exec insert_cont_wagon('3185 4552 044-3');
exec insert_cont_wagon('3185 4552 045-0');
exec insert_cont_wagon('3185 4552 046-8');
exec insert_cont_wagon('3185 4552 047-6');
exec insert_cont_wagon('3185 4552 048-4');
exec insert_cont_wagon('3185 4552 049-2');
exec insert_cont_wagon('3185 4552 050-0');
exec insert_cont_wagon('3185 4552 051-8');
exec insert_cont_wagon('3185 4552 052-6');
exec insert_cont_wagon('3185 4552 053-4');
exec insert_cont_wagon('3185 4552 054-2');
exec insert_cont_wagon('3185 4552 055-9');
exec insert_cont_wagon('3185 4552 056-7');
exec insert_cont_wagon('3185 4552 057-5');
exec insert_cont_wagon('3185 4552 058-3');
exec insert_cont_wagon('3185 4552 059-1');
exec insert_cont_wagon('3185 4552 060-9');
exec insert_cont_wagon('3185 4552 061-7');
exec insert_cont_wagon('3185 4552 062-5');
exec insert_cont_wagon('3185 4552 063-3');
exec insert_cont_wagon('3185 4552 064-1');
exec insert_cont_wagon('3185 4552 065-8');
exec insert_cont_wagon('3185 4552 066-6');
exec insert_cont_wagon('3185 4552 067-4');
exec insert_cont_wagon('3185 4552 068-2');
exec insert_cont_wagon('3185 4552 069-0');
exec insert_cont_wagon('3185 4552 070-8');
exec insert_cont_wagon('3185 4552 071-6');
exec insert_cont_wagon('3185 4552 072-4');
exec insert_cont_wagon('3185 4552 073-2');
exec insert_cont_wagon('3185 4552 074-0');
exec insert_cont_wagon('3185 4552 075-7');
exec insert_cont_wagon('3185 4552 076-5');
exec insert_cont_wagon('3185 4552 077-3');
exec insert_cont_wagon('3185 4552 078-1');
exec insert_cont_wagon('3185 4552 079-9');
exec insert_cont_wagon('3185 4552 080-7');
exec insert_cont_wagon('3185 4552 081-5');
exec insert_cont_wagon('3185 4552 082-3');
exec insert_cont_wagon('3185 4552 083-1');
exec insert_cont_wagon('3185 4552 084-9');
exec insert_cont_wagon('3185 4552 085-6');
exec insert_cont_wagon('3185 4552 086-4');
exec insert_cont_wagon('3185 4552 087-2');
exec insert_cont_wagon('3185 4552 088-0');
exec insert_cont_wagon('3185 4552 089-8');
exec insert_cont_wagon('3185 4552 090-6');
exec insert_cont_wagon('3185 4552 091-4');
exec insert_cont_wagon('3185 4552 092-2');
exec insert_cont_wagon('3185 4552 093-0');
exec insert_cont_wagon('3185 4552 094-8');
exec insert_cont_wagon('3185 4552 095-5');
exec insert_cont_wagon('3185 4552 096-3');
exec insert_cont_wagon('3185 4552 098-9');
exec insert_cont_wagon('3185 4552 099-7');
exec insert_cont_wagon('3185 4552 100-3');
exec insert_cont_wagon('3185 4552 101-1');
exec insert_cont_wagon('3185 4552 102-9');
exec insert_cont_wagon('3185 4552 103-7');
exec insert_cont_wagon('3185 4552 104-5');
exec insert_cont_wagon('3185 4552 105-2');
exec insert_cont_wagon('3185 4552 106-0');
exec insert_cont_wagon('3185 4552 107-8');
exec insert_cont_wagon('3185 4552 108-6');
exec insert_cont_wagon('3185 4552 109-4');
exec insert_cont_wagon('3185 4552 110-2');
exec insert_cont_wagon('3185 4552 111-0');
exec insert_cont_wagon('3185 4552 112-8');
exec insert_cont_wagon('3185 4552 113-6');
exec insert_cont_wagon('3185 4552 114-4');
exec insert_cont_wagon('3185 4552 115-1');
exec insert_cont_wagon('3185 4552 116-9');
exec insert_cont_wagon('3185 4552 117-7');
exec insert_cont_wagon('3185 4552 118-5');
exec insert_cont_wagon('3185 4552 119-3');
exec insert_cont_wagon('3185 4552 120-1');
exec insert_cont_wagon('3185 4552 121-9');
exec insert_cont_wagon('3185 4552 122-7');
exec insert_cont_wagon('3185 4552 123-5');
exec insert_cont_wagon('3185 4552 124-3');
exec insert_cont_wagon('3185 4552 125-0');
exec insert_cont_wagon('3185 4552 126-8');
exec insert_cont_wagon('3185 4552 127-6');
exec insert_cont_wagon('3185 4552 128-4');
exec insert_cont_wagon('3185 4552 129-2');
exec insert_cont_wagon('3185 4552 130-0');
exec insert_cont_wagon('3185 4552 131-8');
exec insert_cont_wagon('3185 4552 132-6');
exec insert_cont_wagon('3185 4552 133-4');
exec insert_cont_wagon('3185 4552 134-2');
exec insert_cont_wagon('3185 4552 135-9');
exec insert_cont_wagon('3185 4552 136-7');
exec insert_cont_wagon('3185 4552 137-5');
exec insert_cont_wagon('3185 4552 138-3');
exec insert_cont_wagon('3185 4552 139-1');
exec insert_cont_wagon('3185 4552 140-9');
exec insert_cont_wagon('3185 4552 141-7');
exec insert_cont_wagon('3185 4552 142-5');
exec insert_cont_wagon('3185 4552 143-3');
exec insert_cont_wagon('3185 4552 144-1');
exec insert_cont_wagon('3185 4552 145-8');
exec insert_cont_wagon('3185 4552 146-6');
exec insert_cont_wagon('3185 4552 147-4');
exec insert_cont_wagon('3185 4552 148-2');
exec insert_cont_wagon('3185 4552 149-0');
exec insert_cont_wagon('3185 4552 150-8');
exec insert_cont_wagon('3185 4552 151-6');
exec insert_cont_wagon('3185 4552 152-4');
exec insert_cont_wagon('3185 4552 153-2');
exec insert_cont_wagon('3185 4552 154-0');
exec insert_cont_wagon('3185 4552 155-7');
exec insert_cont_wagon('3185 4552 156-5');
exec insert_cont_wagon('3185 4552 157-3');
exec insert_cont_wagon('3185 4552 158-1');
exec insert_cont_wagon('3185 4552 159-9');
exec insert_cont_wagon('3185 4552 160-7');
exec insert_cont_wagon('3185 4552 161-5');
exec insert_cont_wagon('3185 4552 162-3');
exec insert_cont_wagon('3185 4552 163-1');
exec insert_cont_wagon('3185 4552 164-9');
exec insert_cont_wagon('3185 4552 165-6');
exec insert_cont_wagon('3185 4552 166-4');
exec insert_cont_wagon('3185 4552 167-2');
exec insert_cont_wagon('3185 4552 168-0');
exec insert_cont_wagon('3185 4552 169-8');
exec insert_cont_wagon('3185 4552 170-6');
exec insert_cont_wagon('3185 4552 171-4');
exec insert_cont_wagon('3185 4552 172-2');
exec insert_cont_wagon('3185 4552 173-0');
exec insert_cont_wagon('3185 4552 174-8');
exec insert_cont_wagon('3185 4552 175-5');
exec insert_cont_wagon('3185 4552 176-3');
exec insert_cont_wagon('3185 4552 177-1');
exec insert_cont_wagon('3185 4552 178-9');
exec insert_cont_wagon('3185 4552 179-7');
exec insert_cont_wagon('3185 4552 180-5');
exec insert_cont_wagon('3185 4552 181-3');
exec insert_cont_wagon('3185 4552 182-1');
exec insert_cont_wagon('3185 4552 183-9');
exec insert_cont_wagon('3185 4552 184-7');
exec insert_cont_wagon('3185 4552 185-4');
exec insert_cont_wagon('3185 4552 186-2');
exec insert_cont_wagon('3185 4552 187-0');
exec insert_cont_wagon('3185 4552 188-8');
exec insert_cont_wagon('3185 4552 189-6');
exec insert_cont_wagon('3185 4552 190-4');
exec insert_cont_wagon('3185 4552 191-2');
exec insert_cont_wagon('3185 4552 192-0');
exec insert_cont_wagon('3185 4552 193-8');
exec insert_cont_wagon('3185 4552 194-6');
exec insert_cont_wagon('3185 4552 195-3');
exec insert_cont_wagon('3185 4552 196-1');
exec insert_cont_wagon('3185 4552 197-9');
exec insert_cont_wagon('3185 4552 198-7');
exec insert_cont_wagon('3185 4552 199-5');
exec insert_cont_wagon('3185 4552 200-1');
exec insert_cont_wagon('3185 4552 202-7');
exec insert_cont_wagon('3185 4552 203-5');
exec insert_cont_wagon('3185 4552 204-3');
exec insert_cont_wagon('3185 4552 205-0');
exec insert_cont_wagon('3185 4552 206-8');
exec insert_cont_wagon('3185 4552 207-6');
exec insert_cont_wagon('3185 4552 208-4');
exec insert_cont_wagon('3185 4552 209-2');
exec insert_cont_wagon('3185 4552 210-0');
exec insert_cont_wagon('3185 4552 211-8');
exec insert_cont_wagon('3185 4552 212-6');
exec insert_cont_wagon('3185 4552 213-4');
exec insert_cont_wagon('3185 4552 214-2');
exec insert_cont_wagon('3185 4552 215-9');
exec insert_cont_wagon('3185 4552 216-7');
exec insert_cont_wagon('3185 4552 217-5');
exec insert_cont_wagon('3185 4552 218-3');
exec insert_cont_wagon('3185 4552 219-1');
exec insert_cont_wagon('3185 4552 220-9');
exec insert_cont_wagon('3185 4552 221-7');
exec insert_cont_wagon('3185 4552 222-5');
exec insert_cont_wagon('3185 4552 223-3');
exec insert_cont_wagon('3185 4552 224-1');
exec insert_cont_wagon('3185 4552 225-8');
exec insert_cont_wagon('3185 4552 226-6');
exec insert_cont_wagon('3185 4552 227-4');
exec insert_cont_wagon('3185 4552 228-2');
exec insert_cont_wagon('3185 4552 229-0');
exec insert_cont_wagon('3185 4552 230-8');
exec insert_cont_wagon('3185 4552 231-6');
exec insert_cont_wagon('3185 4552 232-4');
exec insert_cont_wagon('3185 4552 233-2');
exec insert_cont_wagon('3185 4552 234-0');
exec insert_cont_wagon('3185 4552 235-7');
exec insert_cont_wagon('3185 4552 236-5');
exec insert_cont_wagon('3185 4552 237-3');
exec insert_cont_wagon('3185 4552 238-1');
exec insert_cont_wagon('3185 4552 239-9');
exec insert_cont_wagon('3185 4552 240-7');
exec insert_cont_wagon('3185 4552 241-5');
exec insert_cont_wagon('3185 4552 242-3');
exec insert_cont_wagon('3185 4552 243-1');
exec insert_cont_wagon('3185 4552 244-9');
exec insert_cont_wagon('3185 4552 245-6');
exec insert_cont_wagon('3185 4552 246-4');
exec insert_cont_wagon('3185 4552 247-2');
exec insert_cont_wagon('3185 4552 248-0');
exec insert_cont_wagon('3185 4552 249-8');
exec insert_cont_wagon('3185 4552 250-6');
exec insert_cont_wagon('3185 4552 251-4');
exec insert_cont_wagon('3185 4552 252-2');
exec insert_cont_wagon('3185 4552 253-0');
exec insert_cont_wagon('3185 4552 254-8');
exec insert_cont_wagon('3185 4552 255-5');
exec insert_cont_wagon('3185 4552 256-3');
exec insert_cont_wagon('3185 4552 257-1');
exec insert_cont_wagon('3185 4552 258-9');
exec insert_cont_wagon('3185 4552 259-7');
exec insert_cont_wagon('3185 4552 260-5');
exec insert_cont_wagon('3185 4552 261-3');
exec insert_cont_wagon('3185 4552 262-1');
exec insert_cont_wagon('3185 4552 263-9');
exec insert_cont_wagon('3185 4552 264-7');
exec insert_cont_wagon('3185 4552 265-4');
exec insert_cont_wagon('3185 4552 266-2');
exec insert_cont_wagon('3185 4552 267-0');
exec insert_cont_wagon('3185 4552 268-8');
exec insert_cont_wagon('3185 4552 269-6');
exec insert_cont_wagon('3185 4552 270-4');
exec insert_cont_wagon('3185 4552 271-2');
exec insert_cont_wagon('3185 4552 272-0');
exec insert_cont_wagon('3185 4552 273-8');
exec insert_cont_wagon('3185 4552 274-6');
exec insert_cont_wagon('3185 4552 275-3');
exec insert_cont_wagon('3185 4552 276-1');
exec insert_cont_wagon('3185 4552 277-9');
exec insert_cont_wagon('3185 4552 278-7');
exec insert_cont_wagon('3185 4552 279-5');
exec insert_cont_wagon('3185 4552 280-3');
exec insert_cont_wagon('3185 4552 281-1');
exec insert_cont_wagon('3185 4552 282-9');
exec insert_cont_wagon('3185 4552 283-7');
exec insert_cont_wagon('3185 4552 284-5');
exec insert_cont_wagon('3185 4552 285-2');
exec insert_cont_wagon('3185 4552 286-0');
exec insert_cont_wagon('3185 4552 287-8');
exec insert_cont_wagon('3185 4552 288-6');
exec insert_cont_wagon('3185 4552 289-4');
exec insert_cont_wagon('3185 4552 290-2');
exec insert_cont_wagon('3185 4552 291-0');
exec insert_cont_wagon('3185 4552 292-8');
exec insert_cont_wagon('3185 4552 293-6');
exec insert_cont_wagon('3185 4552 294-4');
exec insert_cont_wagon('3185 4552 295-1');
exec insert_cont_wagon('3185 4552 296-9');
exec insert_cont_wagon('3185 4552 297-7');
exec insert_cont_wagon('3185 4552 298-5');
exec insert_cont_wagon('3185 4552 299-3');
exec insert_cont_wagon('3185 4552 300-9');
exec insert_cont_wagon('3185 4552 301-7');
exec insert_cont_wagon('3185 4552 302-5');
exec insert_cont_wagon('3185 4552 303-3');
exec insert_cont_wagon('3185 4552 304-1');
exec insert_cont_wagon('3185 4552 305-8');
exec insert_cont_wagon('3185 4552 306-6');
exec insert_cont_wagon('3185 4552 307-4');
exec insert_cont_wagon('3185 4552 308-2');
exec insert_cont_wagon('3185 4552 309-0');
exec insert_cont_wagon('3185 4552 310-8');
exec insert_cont_wagon('3185 4552 311-6');
exec insert_cont_wagon('3185 4552 312-4');
exec insert_cont_wagon('3185 4552 313-2');
exec insert_cont_wagon('3185 4552 314-0');
exec insert_cont_wagon('3185 4552 315-7');
exec insert_cont_wagon('3185 4552 316-5');
exec insert_cont_wagon('3185 4552 317-3');
exec insert_cont_wagon('3185 4552 318-1');
exec insert_cont_wagon('3185 4552 319-9');
exec insert_cont_wagon('3185 4552 320-7');
exec insert_cont_wagon('3185 4552 321-5');
exec insert_cont_wagon('3185 4552 322-3');
exec insert_cont_wagon('3185 4552 323-1');
exec insert_cont_wagon('3185 4552 324-9');
exec insert_cont_wagon('3185 4552 325-6');
exec insert_cont_wagon('3185 4552 326-4');
exec insert_cont_wagon('3185 4552 327-2');
exec insert_cont_wagon('3185 4552 328-0');
exec insert_cont_wagon('3185 4552 329-8');
exec insert_cont_wagon('3185 4552 330-6');
exec insert_cont_wagon('3185 4552 331-4');
exec insert_cont_wagon('3185 4552 332-2');
exec insert_cont_wagon('3185 4552 333-0');
exec insert_cont_wagon('3185 4552 334-8');
exec insert_cont_wagon('3185 4552 335-5');
exec insert_cont_wagon('3185 4552 336-3');
exec insert_cont_wagon('3185 4552 337-1');
exec insert_cont_wagon('3185 4552 338-9');
exec insert_cont_wagon('3185 4552 339-7');
exec insert_cont_wagon('3185 4552 340-5');
exec insert_cont_wagon('3185 4552 341-3');
exec insert_cont_wagon('3185 4552 342-1');
exec insert_cont_wagon('3185 4552 343-9');
exec insert_cont_wagon('3185 4552 344-7');
exec insert_cont_wagon('3185 4552 345-4');
exec insert_cont_wagon('3185 4552 346-2');
exec insert_cont_wagon('3185 4552 347-0');
exec insert_cont_wagon('3185 4552 348-8');
exec insert_cont_wagon('3185 4552 349-6');
exec insert_cont_wagon('3185 4552 350-4');
exec insert_cont_wagon('3185 4552 351-2');
exec insert_cont_wagon('3185 4552 352-0');
exec insert_cont_wagon('3185 4552 353-8');
exec insert_cont_wagon('3185 4552 354-6');
exec insert_cont_wagon('3185 4552 355-3');
exec insert_cont_wagon('3185 4552 356-1');
exec insert_cont_wagon('3185 4552 357-9');
exec insert_cont_wagon('3185 4552 358-7');
exec insert_cont_wagon('3185 4552 359-5');
exec insert_cont_wagon('3185 4552 360-3');
exec insert_cont_wagon('3185 4552 361-1');
exec insert_cont_wagon('3185 4552 362-9');
exec insert_cont_wagon('3185 4552 363-7');
exec insert_cont_wagon('3185 4552 364-5');
exec insert_cont_wagon('3185 4552 365-2');
exec insert_cont_wagon('3185 4552 366-0');
exec insert_cont_wagon('3185 4552 367-8');
exec insert_cont_wagon('3185 4552 368-6');
exec insert_cont_wagon('3185 4552 369-4');
exec insert_cont_wagon('3185 4552 370-2');
exec insert_cont_wagon('3185 4552 371-0');
exec insert_cont_wagon('3185 4552 372-8');
exec insert_cont_wagon('3185 4552 373-6');
exec insert_cont_wagon('3185 4552 374-4');
exec insert_cont_wagon('3185 4552 375-1');
exec insert_cont_wagon('3185 4552 376-9');
exec insert_cont_wagon('3185 4552 377-7');
exec insert_cont_wagon('3185 4552 378-5');
exec insert_cont_wagon('3185 4552 379-3');
exec insert_cont_wagon('3185 4552 380-1');
exec insert_cont_wagon('3185 4552 381-9');
exec insert_cont_wagon('3185 4552 382-7');
exec insert_cont_wagon('3185 4552 383-5');
exec insert_cont_wagon('3185 4552 384-3');
exec insert_cont_wagon('3185 4552 385-0');
exec insert_cont_wagon('3185 4552 386-8');
exec insert_cont_wagon('3185 4552 387-6');
exec insert_cont_wagon('3185 4552 388-4');
exec insert_cont_wagon('3185 4552 389-2');
exec insert_cont_wagon('3185 4552 390-0');
exec insert_cont_wagon('3185 4552 391-8');
exec insert_cont_wagon('3185 4552 392-6');
exec insert_cont_wagon('3185 4552 393-4');
exec insert_cont_wagon('3185 4552 394-2');
exec insert_cont_wagon('3185 4552 395-9');
exec insert_cont_wagon('3185 4552 396-7');
exec insert_cont_wagon('3185 4552 397-5');
exec insert_cont_wagon('3185 4552 398-3');
exec insert_cont_wagon('3185 4552 399-1');
exec insert_cont_wagon('3185 4557 607-2');
exec insert_cont_wagon('3185 4557 610-6');
exec insert_cont_wagon('3185 4557 611-4');
exec insert_cont_wagon('3185 4557 612-2');
exec insert_cont_wagon('3185 4557 618-9');
exec insert_cont_wagon('3185 4557 622-1');
exec insert_cont_wagon('3185 4557 631-2');
exec insert_cont_wagon('3185 4557 636-1');
exec insert_cont_wagon('3185 4557 637-9');
exec insert_cont_wagon('3185 4557 641-1');
exec insert_cont_wagon('3185 4557 646-0');
exec insert_cont_wagon('3185 4557 649-4');
exec insert_cont_wagon('3185 4557 651-0');
exec insert_cont_wagon('3185 4557 652-8');
exec insert_cont_wagon('3185 4557 653-6');
exec insert_cont_wagon('3185 4557 654-4');
exec insert_cont_wagon('3185 4557 655-1');
exec insert_cont_wagon('3185 4557 656-9');
exec insert_cont_wagon('3185 4557 657-7');
exec insert_cont_wagon('3185 4557 658-5');
exec insert_cont_wagon('3185 4557 659-3');
exec insert_cont_wagon('3185 4557 660-1');
exec insert_cont_wagon('3185 4557 661-9');
exec insert_cont_wagon('3185 4557 662-7');
exec insert_cont_wagon('3185 4557 663-5');
exec insert_cont_wagon('3185 4557 664-3');
exec insert_cont_wagon('3185 4557 665-0');
exec insert_cont_wagon('3185 4557 666-8');
exec insert_cont_wagon('3185 4557 667-6');
exec insert_cont_wagon('3185 4557 668-4');
exec insert_cont_wagon('3185 4557 669-2');
exec insert_cont_wagon('3185 4557 670-0');
exec insert_cont_wagon('3185 4557 671-8');
exec insert_cont_wagon('3185 4557 672-6');
exec insert_cont_wagon('3185 4557 673-4');
exec insert_cont_wagon('3185 4557 674-2');
exec insert_cont_wagon('3185 4557 675-9');
exec insert_cont_wagon('3185 4557 676-7');
exec insert_cont_wagon('3185 4557 677-5');
exec insert_cont_wagon('3185 4557 678-3');
exec insert_cont_wagon('3185 4557 679-1');
exec insert_cont_wagon('3185 4557 680-9');
exec insert_cont_wagon('3185 4557 681-7');
exec insert_cont_wagon('3185 4557 682-5');
exec insert_cont_wagon('3185 4557 683-3');
exec insert_cont_wagon('3185 4557 684-1');
exec insert_cont_wagon('3185 4557 685-8');
exec insert_cont_wagon('3185 4557 686-6');
exec insert_cont_wagon('3185 4557 687-4');
exec insert_cont_wagon('3185 4557 688-2');
exec insert_cont_wagon('3185 4557 689-0');
exec insert_cont_wagon('3185 4557 690-8');
exec insert_cont_wagon('3185 4557 691-6');
exec insert_cont_wagon('3185 4557 692-4');
exec insert_cont_wagon('3185 4557 693-2');
exec insert_cont_wagon('3185 4557 694-0');
exec insert_cont_wagon('3185 4557 695-7');
exec insert_cont_wagon('3185 4557 696-5');
exec insert_cont_wagon('3185 4557 697-3');
exec insert_cont_wagon('3185 4557 698-1');
exec insert_cont_wagon('3185 4557 699-9');
exec insert_cont_wagon('3185 4557 704-7');
exec insert_cont_wagon('3185 4557 709-6');
exec insert_cont_wagon('3185 4557 717-9');
exec insert_cont_wagon('3185 4557 744-3');
exec insert_cont_wagon('3185 4557 750-0');
exec insert_cont_wagon('3185 4557 751-8');
exec insert_cont_wagon('3185 4557 752-6');
exec insert_cont_wagon('3185 4557 753-4');
exec insert_cont_wagon('3185 4557 754-2');
exec insert_cont_wagon('3185 4557 755-9');
exec insert_cont_wagon('3185 4557 756-7');
exec insert_cont_wagon('3185 4557 757-5');
exec insert_cont_wagon('3185 4557 758-3');
exec insert_cont_wagon('3185 4557 759-1');
exec insert_cont_wagon('3185 4557 760-9');
exec insert_cont_wagon('3185 4557 761-7');
exec insert_cont_wagon('3185 4557 762-5');
exec insert_cont_wagon('3185 4557 763-3');
exec insert_cont_wagon('3185 4557 764-1');
exec insert_cont_wagon('3185 4557 765-8');
exec insert_cont_wagon('3185 4557 766-6');
exec insert_cont_wagon('3185 4557 767-4');
exec insert_cont_wagon('3185 4557 768-2');
exec insert_cont_wagon('3185 4557 769-0');
exec insert_cont_wagon('3185 4557 770-8');
exec insert_cont_wagon('3185 4557 771-6');
exec insert_cont_wagon('3185 4557 772-4');
exec insert_cont_wagon('3185 4557 773-2');
exec insert_cont_wagon('3185 4557 774-0');
exec insert_cont_wagon('3185 4557 775-7');
exec insert_cont_wagon('3185 4557 776-5');
exec insert_cont_wagon('3185 4557 777-3');
exec insert_cont_wagon('3185 4557 778-1');
exec insert_cont_wagon('3185 4557 779-9');
exec insert_cont_wagon('3185 4557 780-7');
exec insert_cont_wagon('3185 4557 781-5');
exec insert_cont_wagon('3185 4557 782-3');
exec insert_cont_wagon('3185 4557 783-1');
exec insert_cont_wagon('3185 4557 784-9');
exec insert_cont_wagon('3185 4557 785-6');
exec insert_cont_wagon('3185 4557 786-4');
exec insert_cont_wagon('3185 4557 787-2');
exec insert_cont_wagon('3185 4557 788-0');
exec insert_cont_wagon('3185 4557 789-8');
exec insert_cont_wagon('3185 4557 790-6');
exec insert_cont_wagon('3185 4557 791-4');
exec insert_cont_wagon('3185 4557 792-2');
exec insert_cont_wagon('3185 4557 793-0');
exec insert_cont_wagon('3185 4557 794-8');
exec insert_cont_wagon('3185 4557 795-5');
exec insert_cont_wagon('3185 4557 796-3');
exec insert_cont_wagon('3185 4557 797-1');
exec insert_cont_wagon('3185 4557 798-9');
exec insert_cont_wagon('3185 4557 799-7');
exec insert_cont_wagon('3185 4557 800-3');
exec insert_cont_wagon('3185 4557 801-1');
exec insert_cont_wagon('3185 4557 802-9');
exec insert_cont_wagon('3185 4557 803-7');
exec insert_cont_wagon('3185 4557 804-5');
exec insert_cont_wagon('3185 4557 805-2');
exec insert_cont_wagon('3185 4557 806-0');
exec insert_cont_wagon('3185 4557 807-8');
exec insert_cont_wagon('3185 4557 808-6');
exec insert_cont_wagon('3185 4557 809-4');
exec insert_cont_wagon('3185 4557 810-2');
exec insert_cont_wagon('3185 4557 811-0');
exec insert_cont_wagon('3185 4557 812-8');
exec insert_cont_wagon('3185 4557 813-6');
exec insert_cont_wagon('3185 4557 814-4');
exec insert_cont_wagon('3185 4557 815-1');
exec insert_cont_wagon('3185 4557 816-9');
exec insert_cont_wagon('3185 4557 817-7');
exec insert_cont_wagon('3185 4557 818-5');
exec insert_cont_wagon('3185 4557 819-3');
exec insert_cont_wagon('3185 4557 900-1');
exec insert_cont_wagon('3185 4557 901-9');
exec insert_cont_wagon('3185 4557 902-7');
exec insert_cont_wagon('3185 4557 903-5');
exec insert_cont_wagon('3185 4557 904-3');
exec insert_cont_wagon('3185 4557 905-0');
exec insert_cont_wagon('3185 4557 906-8');
exec insert_cont_wagon('3185 4557 907-6');
exec insert_cont_wagon('3185 4557 908-4');
exec insert_cont_wagon('3185 4557 909-2');
exec insert_cont_wagon('3185 4557 910-0');
exec insert_cont_wagon('3185 4557 911-8');
exec insert_cont_wagon('3185 4557 912-6');
exec insert_cont_wagon('3185 4557 913-4');
exec insert_cont_wagon('3185 4557 914-2');
exec insert_cont_wagon('3185 4557 915-9');
exec insert_cont_wagon('3185 4557 916-7');
exec insert_cont_wagon('3185 4557 917-5');
exec insert_cont_wagon('3185 4557 918-3');
exec insert_cont_wagon('3185 4557 919-1');
exec insert_cont_wagon('3185 4557 920-9');
exec insert_cont_wagon('3185 4557 921-7');
exec insert_cont_wagon('3185 4557 922-5');
exec insert_cont_wagon('3185 4557 923-3');
exec insert_cont_wagon('3185 4557 924-1');
exec insert_cont_wagon('3185 4557 925-8');
exec insert_cont_wagon('3185 4557 926-6');
exec insert_cont_wagon('3185 4557 927-4');
exec insert_cont_wagon('3185 4557 928-2');
exec insert_cont_wagon('3185 4557 929-0');
exec insert_cont_wagon('3185 4557 930-8');
exec insert_cont_wagon('3185 4557 931-6');
exec insert_cont_wagon('3185 4557 932-4');
exec insert_cont_wagon('3185 4557 933-2');
exec insert_cont_wagon('3185 4557 934-0');
exec insert_cont_wagon('3185 4557 935-7');
exec insert_cont_wagon('3185 4557 936-5');
exec insert_cont_wagon('3185 4557 937-3');
exec insert_cont_wagon('3185 4557 938-1');
exec insert_cont_wagon('3185 4557 939-9');
exec insert_cont_wagon('3185 4563 000-2');
exec insert_cont_wagon('3185 4563 001-0');
exec insert_cont_wagon('3185 4563 002-8');
exec insert_cont_wagon('3185 4563 003-6');
exec insert_cont_wagon('3185 4563 004-4');
exec insert_cont_wagon('3185 4563 005-1');
exec insert_cont_wagon('3185 4563 006-9');
exec insert_cont_wagon('3185 4563 007-7');
exec insert_cont_wagon('3185 4563 008-5');
exec insert_cont_wagon('3185 4563 009-3');
exec insert_cont_wagon('3185 4563 010-1');
exec insert_cont_wagon('3185 4563 011-9');
exec insert_cont_wagon('3185 4563 012-7');
exec insert_cont_wagon('3185 4563 013-5');
exec insert_cont_wagon('3185 4563 014-3');
exec insert_cont_wagon('3185 4563 015-0');
exec insert_cont_wagon('3185 4563 016-8');
exec insert_cont_wagon('3185 4563 017-6');
exec insert_cont_wagon('3185 4563 018-4');
exec insert_cont_wagon('3185 4563 019-2');
*/

commit;

drop procedure insert_cont_wagon;
