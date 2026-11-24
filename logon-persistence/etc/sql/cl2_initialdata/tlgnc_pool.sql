-- $Revision$
-- $Date$

prompt '--------- cl2_initialdata/tlgnc_pool.sql ----------'

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
-- tlgnc_pool
-------------------------------------------------------------------------------
create or replace
procedure upsert_pool (
  p_pool_id           in number,
  p_sap_trsp_mode_cd  in varchar2,
  p_ctl_act_cd        in number)
is
  v_table_name    varchar2(48) := 'tlgnc_pool';
  v_exists_flag   number;        -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_pool_id || ', ''' || p_sap_trsp_mode_cd || ''', ctl_act_cd=' || p_ctl_act_cd || ')');

    ----------------------------------------------------------------------------
    -- Lookup tlgnc_pool
    ----------------------------------------------------------------------------
    select count(pool.pool_id) into v_exists_flag
    from tlgnc_pool pool
    where pool.pool_id = p_pool_id;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with ID=' || p_pool_id || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnc_pool set
          /* pool_id: cannot update because this is the key */
          sap_trsp_mode_cd               = p_sap_trsp_mode_cd,
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp,
          ctl_tcn                        = ctl_tcn+1
        where
          pool_id = p_pool_id and
          (1 = is_different_number   (ctl_act_cd, p_ctl_act_cd) or
           1 = is_different_varchar2 (sap_trsp_mode_cd, p_sap_trsp_mode_cd))
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (ID=' || p_pool_id || ').');
      end if;
    else
      insert into tlgnc_pool (
        pool_id,
        sap_trsp_mode_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
      ) values (
        p_pool_id,
        p_sap_trsp_mode_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp, 0
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (ID=' || p_pool_id || ').');
    end if;

  end upsert_pool;
/
show errors;


-------------------------------------------------------------------------------
-- tlgnc_pool_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_pool_tx (
  p_pool_id IN NUMBER,
  p_lang_cd IN VARCHAR2,
  p_name    IN VARCHAR2) 
is
  v_exists_flag                   number;        -- number of existing records of that key (0 or 1)
  v_exists_pool_flag              number;      -- number of existing BASE records

  begin
    dbms_output.enable(100000);

    dbms_output.put_line('Insert or Update tlgnc_pool_tx with (' || p_pool_id || ', ' || p_lang_cd || ', ''' || p_name || ''')');

    ----------------------------------------------------------------------------
    -- Lookup tlgnc_pool entry first
    ----------------------------------------------------------------------------
    select count(pool.pool_id) into v_exists_pool_flag
    from tlgnc_pool pool
    where pool.pool_id = p_pool_id;

    if (v_exists_pool_flag > 0) then
      ----------------------------------------------------------------------------
      -- Lookup tlgnc_pool_tx
      ----------------------------------------------------------------------------
      select count(tx.pool_id) into v_exists_flag
      from tlgnc_pool_tx tx
      where tx.pool_id = p_pool_id and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        
        update tlgnc_pool_tx set
          /* p_pool_id and lang_cd: cannot update because these are the keys */
          name                           = p_name,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp,
          ctl_tcn                        = ctl_tcn+1
        where
          pool_id = p_pool_id and
          lang_cd = p_lang_cd and
          1 = is_different_varchar2 (name, p_name)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnc_pool_tx (ID=' || p_pool_id || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnc_pool_tx (
          pool_id,
          lang_cd,
          name,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn)
        values (
          p_pool_id,
          p_lang_cd,
          p_name,
          1, systimestamp, 1, systimestamp, 0
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnc_pool_tx (ID=' || p_pool_id || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in tlgnc_pool (ID=' || p_pool_id || ').');
    end if;

  end upsert_pool_tx;
/
show errors;


prompt INSERTING into TLGNC_POOL
execute upsert_pool(10, '00', 1);
execute upsert_pool(2051, '10', 1);
execute upsert_pool(2052, '10', 1);
execute upsert_pool(2053, '10', 1);
execute upsert_pool(2054, '50', 1);
execute upsert_pool(2055, '30', 1);
execute upsert_pool(2056, '10', 1);
execute upsert_pool(2057, '10', 1);
execute upsert_pool(2058, '30', 1);
execute upsert_pool(2059, '10', 1);
execute upsert_pool(2060, '10', 1);
execute upsert_pool(2061, '10', 1);
execute upsert_pool(2062, '10', 1);
execute upsert_pool(2063, '50', 1);
execute upsert_pool(2064, '10', 1);
execute upsert_pool(2065, '30', 1);
execute upsert_pool(2066, '10', 1);
execute upsert_pool(2067, '10', 1);
execute upsert_pool(2068, '30', 1);
execute upsert_pool(2069, '10', 1);
execute upsert_pool(2070, '10', 1);
execute upsert_pool(2071, '50', 1);
execute upsert_pool(2072, '10', 1);
execute upsert_pool(2073, '10', 1);

prompt INSERTING into TLGNC_POOL_TX - EN
execute upsert_pool_tx(10, 'en', '_Pickup');
execute upsert_pool_tx(2051, 'en', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'en', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'en', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'en', 'BG - Rail');
execute upsert_pool_tx(2055, 'en', 'BG - Ship');
execute upsert_pool_tx(2056, 'en', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'en', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'en', 'HR - Ship');
execute upsert_pool_tx(2059, 'en', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'en', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'en', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'en', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'en', 'RO - Rail');
execute upsert_pool_tx(2064, 'en', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'en', 'RO - Ship');
execute upsert_pool_tx(2066, 'en', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'en', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'en', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'en', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'en', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'en', 'SK - Rail');
execute upsert_pool_tx(2072, 'en', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'en', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - DE
execute upsert_pool_tx(10, 'de', '_Pickup');
execute upsert_pool_tx(2051, 'de', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'de', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'de', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'de', 'BG - Rail');
execute upsert_pool_tx(2055, 'de', 'BG - Ship');
execute upsert_pool_tx(2056, 'de', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'de', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'de', 'HR - Ship');
execute upsert_pool_tx(2059, 'de', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'de', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'de', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'de', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'de', 'RO - Rail');
execute upsert_pool_tx(2064, 'de', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'de', 'RO - Ship');
execute upsert_pool_tx(2066, 'de', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'de', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'de', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'de', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'de', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'de', 'SK - Rail');
execute upsert_pool_tx(2072, 'de', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'de', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - FR
execute upsert_pool_tx(10, 'fr', '_Pickup');
execute upsert_pool_tx(2051, 'fr', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'fr', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'fr', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'fr', 'BG - Rail');
execute upsert_pool_tx(2055, 'fr', 'BG - Ship');
execute upsert_pool_tx(2056, 'fr', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'fr', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'fr', 'HR - Ship');
execute upsert_pool_tx(2059, 'fr', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'fr', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'fr', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'fr', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'fr', 'RO - Rail');
execute upsert_pool_tx(2064, 'fr', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'fr', 'RO - Ship');
execute upsert_pool_tx(2066, 'fr', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'fr', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'fr', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'fr', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'fr', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'fr', 'SK - Rail');
execute upsert_pool_tx(2072, 'fr', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'fr', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - IT
execute upsert_pool_tx(10, 'it', '_Pickup');
execute upsert_pool_tx(2051, 'it', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'it', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'it', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'it', 'BG - Rail');
execute upsert_pool_tx(2055, 'it', 'BG - Ship');
execute upsert_pool_tx(2056, 'it', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'it', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'it', 'HR - Ship');
execute upsert_pool_tx(2059, 'it', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'it', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'it', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'it', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'it', 'RO - Rail');
execute upsert_pool_tx(2064, 'it', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'it', 'RO - Ship');
execute upsert_pool_tx(2066, 'it', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'it', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'it', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'it', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'it', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'it', 'SK - Rail');
execute upsert_pool_tx(2072, 'it', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'it', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - ES
execute upsert_pool_tx(10, 'es', '_Pickup');
execute upsert_pool_tx(2051, 'es', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'es', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'es', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'es', 'BG - Rail');
execute upsert_pool_tx(2055, 'es', 'BG - Ship');
execute upsert_pool_tx(2056, 'es', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'es', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'es', 'HR - Ship');
execute upsert_pool_tx(2059, 'es', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'es', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'es', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'es', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'es', 'RO - Rail');
execute upsert_pool_tx(2064, 'es', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'es', 'RO - Ship');
execute upsert_pool_tx(2066, 'es', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'es', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'es', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'es', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'es', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'es', 'SK - Rail');
execute upsert_pool_tx(2072, 'es', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'es', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - CS
execute upsert_pool_tx(10, 'cs', '_Pickup');
execute upsert_pool_tx(2051, 'cs', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'cs', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'cs', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'cs', 'BG - Rail');
execute upsert_pool_tx(2055, 'cs', 'BG - Ship');
execute upsert_pool_tx(2056, 'cs', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'cs', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'cs', 'HR - Ship');
execute upsert_pool_tx(2059, 'cs', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'cs', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'cs', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'cs', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'cs', 'RO - Rail');
execute upsert_pool_tx(2064, 'cs', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'cs', 'RO - Ship');
execute upsert_pool_tx(2066, 'cs', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'cs', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'cs', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'cs', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'cs', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'cs', 'SK - Rail');
execute upsert_pool_tx(2072, 'cs', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'cs', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - SK
execute upsert_pool_tx(10, 'sk', '_Pickup');
execute upsert_pool_tx(2051, 'sk', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'sk', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'sk', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'sk', 'BG - Rail');
execute upsert_pool_tx(2055, 'sk', 'BG - Ship');
execute upsert_pool_tx(2056, 'sk', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'sk', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'sk', 'HR - Ship');
execute upsert_pool_tx(2059, 'sk', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'sk', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'sk', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'sk', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'sk', 'RO - Rail');
execute upsert_pool_tx(2064, 'sk', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'sk', 'RO - Ship');
execute upsert_pool_tx(2066, 'sk', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'sk', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'sk', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'sk', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'sk', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'sk', 'SK - Rail');
execute upsert_pool_tx(2072, 'sk', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'sk', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - HU
execute upsert_pool_tx(10, 'hu', '_Pickup');
execute upsert_pool_tx(2051, 'hu', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'hu', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'hu', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'hu', 'BG - Rail');
execute upsert_pool_tx(2055, 'hu', 'BG - Ship');
execute upsert_pool_tx(2056, 'hu', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'hu', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'hu', 'HR - Ship');
execute upsert_pool_tx(2059, 'hu', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'hu', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'hu', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'hu', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'hu', 'RO - Rail');
execute upsert_pool_tx(2064, 'hu', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'hu', 'RO - Ship');
execute upsert_pool_tx(2066, 'hu', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'hu', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'hu', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'hu', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'hu', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'hu', 'SK - Rail');
execute upsert_pool_tx(2072, 'hu', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'hu', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - RO
execute upsert_pool_tx(10, 'ro', '_Pickup');
execute upsert_pool_tx(2051, 'ro', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'ro', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'ro', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'ro', 'BG - Rail');
execute upsert_pool_tx(2055, 'ro', 'BG - Ship');
execute upsert_pool_tx(2056, 'ro', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'ro', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'ro', 'HR - Ship');
execute upsert_pool_tx(2059, 'ro', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'ro', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'ro', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'ro', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'ro', 'RO - Rail');
execute upsert_pool_tx(2064, 'ro', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'ro', 'RO - Ship');
execute upsert_pool_tx(2066, 'ro', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'ro', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'ro', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'ro', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'ro', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'ro', 'SK - Rail');
execute upsert_pool_tx(2072, 'ro', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'ro', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - BG
execute upsert_pool_tx(10, 'bg', '_Pickup');
execute upsert_pool_tx(2051, 'bg', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'bg', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'bg', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'bg', 'BG - Rail');
execute upsert_pool_tx(2055, 'bg', 'BG - Ship');
execute upsert_pool_tx(2056, 'bg', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'bg', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'bg', 'HR - Ship');
execute upsert_pool_tx(2059, 'bg', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'bg', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'bg', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'bg', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'bg', 'RO - Rail');
execute upsert_pool_tx(2064, 'bg', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'bg', 'RO - Ship');
execute upsert_pool_tx(2066, 'bg', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'bg', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'bg', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'bg', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'bg', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'bg', 'SK - Rail');
execute upsert_pool_tx(2072, 'bg', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'bg', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - RU
execute upsert_pool_tx(10, 'ru', '_Pickup');
execute upsert_pool_tx(2051, 'ru', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'ru', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'ru', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'ru', 'BG - Rail');
execute upsert_pool_tx(2055, 'ru', 'BG - Ship');
execute upsert_pool_tx(2056, 'ru', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'ru', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'ru', 'HR - Ship');
execute upsert_pool_tx(2059, 'ru', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'ru', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'ru', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'ru', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'ru', 'RO - Rail');
execute upsert_pool_tx(2064, 'ru', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'ru', 'RO - Ship');
execute upsert_pool_tx(2066, 'ru', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'ru', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'ru', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'ru', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'ru', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'ru', 'SK - Rail');
execute upsert_pool_tx(2072, 'ru', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'ru', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - PL
execute upsert_pool_tx(10, 'pl', '_Pickup');
execute upsert_pool_tx(2051, 'pl', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'pl', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'pl', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'pl', 'BG - Rail');
execute upsert_pool_tx(2055, 'pl', 'BG - Ship');
execute upsert_pool_tx(2056, 'pl', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'pl', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'pl', 'HR - Ship');
execute upsert_pool_tx(2059, 'pl', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'pl', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'pl', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'pl', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'pl', 'RO - Rail');
execute upsert_pool_tx(2064, 'pl', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'pl', 'RO - Ship');
execute upsert_pool_tx(2066, 'pl', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'pl', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'pl', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'pl', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'pl', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'pl', 'SK - Rail');
execute upsert_pool_tx(2072, 'pl', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'pl', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - AZ
execute upsert_pool_tx(10, 'az', '_Pickup');
execute upsert_pool_tx(2051, 'az', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'az', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'az', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'az', 'BG - Rail');
execute upsert_pool_tx(2055, 'az', 'BG - Ship');
execute upsert_pool_tx(2056, 'az', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'az', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'az', 'HR - Ship');
execute upsert_pool_tx(2059, 'az', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'az', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'az', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'az', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'az', 'RO - Rail');
execute upsert_pool_tx(2064, 'az', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'az', 'RO - Ship');
execute upsert_pool_tx(2066, 'az', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'az', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'az', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'az', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'az', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'az', 'SK - Rail');
execute upsert_pool_tx(2072, 'az', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'az', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - HR
execute upsert_pool_tx(10, 'hr', '_Pickup');
execute upsert_pool_tx(2051, 'hr', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'hr', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'hr', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'hr', 'BG - Rail');
execute upsert_pool_tx(2055, 'hr', 'BG - Ship');
execute upsert_pool_tx(2056, 'hr', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'hr', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'hr', 'HR - Ship');
execute upsert_pool_tx(2059, 'hr', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'hr', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'hr', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'hr', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'hr', 'RO - Rail');
execute upsert_pool_tx(2064, 'hr', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'hr', 'RO - Ship');
execute upsert_pool_tx(2066, 'hr', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'hr', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'hr', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'hr', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'hr', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'hr', 'SK - Rail');
execute upsert_pool_tx(2072, 'hr', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'hr', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - SR
execute upsert_pool_tx(10, 'sr', '_Pickup');
execute upsert_pool_tx(2051, 'sr', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'sr', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'sr', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'sr', 'BG - Rail');
execute upsert_pool_tx(2055, 'sr', 'BG - Ship');
execute upsert_pool_tx(2056, 'sr', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'sr', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'sr', 'HR - Ship');
execute upsert_pool_tx(2059, 'sr', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'sr', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'sr', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'sr', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'sr', 'RO - Rail');
execute upsert_pool_tx(2064, 'sr', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'sr', 'RO - Ship');
execute upsert_pool_tx(2066, 'sr', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'sr', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'sr', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'sr', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'sr', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'sr', 'SK - Rail');
execute upsert_pool_tx(2072, 'sr', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'sr', 'SK - HU CAR Bulk');

prompt INSERTING into TLGNC_POOL_TX - TR
execute upsert_pool_tx(10, 'tr', '_Pickup');
execute upsert_pool_tx(2051, 'tr', 'AT - CEM Bulk');
execute upsert_pool_tx(2052, 'tr', 'BG - CEM Bag');
execute upsert_pool_tx(2053, 'tr', 'BG - CEM Bulk');
execute upsert_pool_tx(2054, 'tr', 'BG - Rail');
execute upsert_pool_tx(2055, 'tr', 'BG - Ship');
execute upsert_pool_tx(2056, 'tr', 'HR - CEM Bag');
execute upsert_pool_tx(2057, 'tr', 'HR - CEM Bulk');
execute upsert_pool_tx(2058, 'tr', 'HR - Ship');
execute upsert_pool_tx(2059, 'tr', 'HU - CEM Bag');
execute upsert_pool_tx(2060, 'tr', 'HU - CEM Bulk');
execute upsert_pool_tx(2061, 'tr', 'RO - CEM Bag');
execute upsert_pool_tx(2062, 'tr', 'RO - CEM Bulk');
execute upsert_pool_tx(2063, 'tr', 'RO - Rail');
execute upsert_pool_tx(2064, 'tr', 'RO - Pallet Return');
execute upsert_pool_tx(2065, 'tr', 'RO - Ship');
execute upsert_pool_tx(2066, 'tr', 'RS - CEM Bag');
execute upsert_pool_tx(2067, 'tr', 'RS - CEM Bulk');
execute upsert_pool_tx(2068, 'tr', 'SK - Block-Train');
execute upsert_pool_tx(2069, 'tr', 'SK - CEM Bag');
execute upsert_pool_tx(2070, 'tr', 'SK - CEM Bulk');
execute upsert_pool_tx(2071, 'tr', 'SK - Rail');
execute upsert_pool_tx(2072, 'tr', 'SK - HU CAR Bag');
execute upsert_pool_tx(2073, 'tr', 'SK - HU CAR Bulk');

commit;

drop procedure upsert_pool;
drop procedure upsert_pool_tx;
drop function is_different_number;
drop function is_different_varchar2;
