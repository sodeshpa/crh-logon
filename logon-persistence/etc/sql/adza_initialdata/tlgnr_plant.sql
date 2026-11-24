-- $Revision$
-- $Date$
--
-- based on ticket ADN71680, ADN72108

-- Based on the list of initial test/master data for the Andorra/South Africa tests
-- /share/doc/crh/logon_2_0_ebm/input/initialdata/20110622\ Customizing\ master\ data\ for\ ITC1.xlsx

prompt '---------- adza_initialdata/tlgnr_plant.sql ----------'

set define off
set serveroutput on

-------------------------------------------------------------------------------
-- is_different_varchar2
-------------------------------------------------------------------------------
-- compares two varchars.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_varchar2 (
  i_a in varchar2,
  i_b in varchar2)
  return number is
  begin
    if ((i_a != i_b)
        or (i_a is null and i_b is not null)
        or (i_a is not null and i_b is null)) then
      return 1;
    end if;
    return 0;
  end;
/
show errors;

-------------------------------------------------------------------------------
-- is_different_number
-------------------------------------------------------------------------------
-- compares two numbers.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_number (
  i_a in number,
  i_b in number)
  return number is
  begin
    if ((i_a != i_b)
        or (i_a is null and i_b is not null)
        or (i_a is not null and i_b is null)) then
      return 1;
    end if;
    return 0;
  end;
/
show errors;

-------------------------------------------------------------------------------
-- upsert_plant_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_plant_bs (
  p_plant_cd                in varchar2,
  p_plant_name              in varchar2,
  p_production_plant        in number,
  p_sap_inventory_managed   in number,
  p_coord_lat               in number,
  p_coord_lon               in number,
  p_ctl_act_cd              in number)
is
  v_exists_flag             number;        -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);

    dbms_output.put_line('Insert or Update tlgnr_plant_bs (' || p_plant_cd || ', ''' || p_plant_name || ''', ' || p_production_plant || ', ' || p_sap_inventory_managed || ', coord_lat=' || p_coord_lat || ', coord_lon=' || p_coord_lon || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.plant_cd) into v_exists_flag
    from tlgnr_plant_bs bs
    where bs.plant_cd = p_plant_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating tlgnr_plant_bs with plant_cd = ' || p_plant_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_plant_bs set
          /* plant_cd: cannot update because this is the key */
          plant_name            = p_plant_name,
          production_plant      = p_production_plant,
          sap_inventory_managed = p_sap_inventory_managed,
          coord_lat             = p_coord_lat,
          coord_lon             = p_coord_lon,
          ctl_act_cd            = p_ctl_act_cd,
          ctl_mod_uid           = 1,
          ctl_mod_ts            = systimestamp
        where
          plant_cd = p_plant_cd                                                       and
          ( 1 = is_different_varchar2(plant_name, p_plant_name)                       or
            1 = is_different_number(production_plant, p_production_plant)             or
            1 = is_different_number(sap_inventory_managed, p_sap_inventory_managed)   or
            1 = is_different_number(coord_lat, p_coord_lat)                           or
            1 = is_different_number(coord_lon, p_coord_lon)                           or
            ctl_act_cd != p_ctl_act_cd  /* not nullable */
          )
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_plant_bs (CD=' || p_plant_cd || ').');
      end if;
    else
      insert into tlgnr_plant_bs (
        plant_cd,
        plant_name,
        production_plant,
        sap_inventory_managed,
        coord_lat,
        coord_lon,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_plant_cd,
        p_plant_name,
        p_production_plant,
        p_sap_inventory_managed,
        p_coord_lat,
        p_coord_lon,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_plant_bs (CD=' || p_plant_cd || ').');
    end if;

  end upsert_plant_bs;
/
show errors;


prompt INSERTING into table TLGNR_PLANT_BS

execute upsert_plant_bs('8101', 'AD CEM Plant 8101'   , 1, 1, null, null, 1);
execute upsert_plant_bs('8102', 'AD AFR Plant 8102'   , 1, 1, null, null, 1);
execute upsert_plant_bs('8103', 'AD AFR Plant 8103'   , 1, 1, null, null, 1);
execute upsert_plant_bs('8104', 'AD CEM Plant 8104'   , 1, 1, null, null, 1);
execute upsert_plant_bs('8105', 'AD CEM Plant BIS'    , 1, 1, null, null, 1);
execute upsert_plant_bs('8201', 'AD MIC Plant'        , 1, 1, null, null, 1);
execute upsert_plant_bs('8301', 'AD AGG Plant 8301'   , 1, 1, null, null, 1);
execute upsert_plant_bs('8401', 'AD RMX Plant'        , 1, 1, null, null, 1);
execute upsert_plant_bs('8VAD', 'AD Virtual Plant'    , 1, 1, null, null, 1);
execute upsert_plant_bs('9101', 'ZA CEM Plant'        , 1, 1, null, null, 1);
execute upsert_plant_bs('9301', 'ZA AGG Plant'        , 1, 1, null, null, 1);
execute upsert_plant_bs('9401', 'ZA RMX Plant'        , 1, 1, null, null, 1);
execute upsert_plant_bs('9801', 'ZA TRD Plant'        , 1, 1, null, null, 1);
execute upsert_plant_bs('9VZA', 'ZA Virtual Plant'    , 1, 1, null, null, 1);
execute upsert_plant_bs('QMPL', 'Virtual QMPlant'     , 1, 1, null, null, 1);
execute upsert_plant_bs('C102', 'Siggenthal'          , 1, 1, 47.523105, 8.240625, 1);


prompt INSERTING artificial plants into table TLGNR_PLANT_BS
-- These are logon-only production plants, artificial. The are only needed for the artificial Logon-Only Shippingpoints, needed for Rail (Return, Repair, ...)
-- The prefix "." was chosen to avoid any conflicts with other keys from other countries. Nobody should use . as a prefix...
-- The numbers (4, 5, 6) are identical to the shippingpoint type numbers (4=rail supplier, 5=Rail Repair, 6=Rail Extern/Return).
-- I am not 100% sure, if the application has a hardcoded reference to the Swiss plants .SP4, .SP5, .SP6)
--
execute upsert_plant_bs('.AZ6', 'Logon ADZA Rail Extern/Return', 0, 0, null, null, 1);
execute upsert_plant_bs('.AZ4', 'Logon ADZA Rail Supplier'     , 0, 0, null, null, 1);
execute upsert_plant_bs('.AZ5', 'Logon ADZA Rail Repair'       , 0, 0, null, null, 1);


commit;


drop procedure upsert_plant_bs;
drop function is_different_number;
drop function is_different_varchar2;
