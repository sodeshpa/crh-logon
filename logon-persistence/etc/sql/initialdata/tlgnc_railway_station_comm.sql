-- $Revision$
-- $Date$
--
-- Initial data created manually based on /share/doc/crh/pegasus/input/Bahn/RailwayStation_Operational_to_Commercial_20090622.xls

prompt '---------- initialdata/tlgnc_railway_station_comm.sql ----------'

prompt INSERTING into TLGNC_RAILWAY_STATION_COMM

-- 85 172148  Lausanne-Triage SALT           85 011189   Renens VD
-- 85 090415  Zizers SBB                     85 090019   Zizers SBB
-- 85 035469  Zürich RB Limmattal Wagenrep.  85 035089   Dietikon
-- INV! 85 082941  Gettnau                        85 174193   Gettnau
-- 85 033365  Werk Bäretswil (Hinwil)        85 031302   Hinwil
-- DUP 85 172148  Lausanne-Triage SALT           85 011189   Renens VD
-- DUP 85 090415  Zizers SBB                     85 090019   Zizers SBB
-- 85 074989  Leissigbad                     85 074955   Leissigen
-- 85 052043  Centrale Faido (Lavorgo)       85 052050   Lavorgo
-- 85 043406  Biel/Bienne Rangierbhf.        85 043000   Biel/Bienne
-- 85 030254  Zürich Vorbahnhof              85 030148   Zürich HB Cargo
-- Added 21.11.2009: But inverse the other "Gettnau"
-- 85 174193  Gettnau                        85 082941    Gettnau

/* Data from CHE
insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '172148' and r2.railway_station_no = '011189');

insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '090415' and r2.railway_station_no = '090019');

insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '035469' and r2.railway_station_no = '035089');
*/
/* Was wrong, just inversed...
insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '082941' and r2.railway_station_no = '174193');
*/
/* Data from CHE
insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '033365' and r2.railway_station_no = '031302');

insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '074989' and r2.railway_station_no = '074955');

insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '052043' and r2.railway_station_no = '052050');

insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '043406' and r2.railway_station_no = '043000');

insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '030254' and r2.railway_station_no = '030148');

insert into tlgnc_railway_station_comm (railway_station_comm_id, op_railway_station_id, comm_railway_station_id, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
( select slgnc_railway_station_comm.nextval, r1.railway_station_id, r2.railway_station_id, 1, systimestamp, 1, systimestamp, 0
  from   tlgnc_railway_station r1, tlgnc_railway_station r2
  where  r1.uic_country_cd = 85 and r2.uic_country_cd = 85
  and    r1.railway_station_no = '174193' and r2.railway_station_no = '082941');
*/
commit;
