-- $Revision$
-- $Date$
--
-- based on ticket ADN71680

-- FIXME Crh: collect data

prompt '---------- adza_initialdata/tlgnr_sorg.sql ----------'

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
-- upsert_sorg_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_sorg_bs (
  p_sorg_cd                     in varchar2,
  p_companycode_cd              in varchar2,
  p_sorg_name                   in varchar2,
  p_logo_image_name             in varchar2,
  p_address_street              in varchar2,
  p_address_countrycity         in varchar2,
  p_skip_send_distance_sap_sd   in number,
  p_salesdoc_return_auart       in varchar2,
  p_ctl_act_cd                  in number)
is
  v_exists_flag             number;        -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);

    dbms_output.put_line('Insert or Update tlgnr_sorg_bs (' || p_sorg_cd || ', ' || p_companycode_cd || ', ''' || p_sorg_name || ''', ''' || p_logo_image_name || ''', ''' || p_address_street || ''', ''' || p_address_countrycity || ', ' || p_skip_send_distance_sap_sd || ', ''' || p_salesdoc_return_auart || ''', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.sorg_cd) into v_exists_flag
    from tlgnr_sorg_bs bs
    where bs.sorg_cd = p_sorg_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating tlgnr_sorg_bs with sorg_cd = ' || p_sorg_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_sorg_bs set
          /* sorg_cd: cannot update because this is the key */
          companycode_cd            = p_companycode_cd,
          sorg_name                 = p_sorg_name,
          logo_image_name           = p_logo_image_name,
          address_street            = p_address_street,
          address_countrycity       = p_address_countrycity,
          skip_send_distance_sap_sd = p_skip_send_distance_sap_sd,
          salesdoc_return_auart     = p_salesdoc_return_auart,
          ctl_act_cd                = p_ctl_act_cd,
          ctl_mod_uid               = 1,
          ctl_mod_ts                = systimestamp
        where
          sorg_cd = p_sorg_cd                                                               and
          ( 1 = is_different_varchar2(companycode_cd, p_companycode_cd)                     or
            1 = is_different_varchar2(sorg_name, p_sorg_name)                               or
            1 = is_different_varchar2(logo_image_name, p_logo_image_name)                   or
            1 = is_different_varchar2(address_street, p_address_street)                     or
            1 = is_different_varchar2(address_countrycity, p_address_countrycity)           or
            1 = is_different_number(skip_send_distance_sap_sd, p_skip_send_distance_sap_sd) or
            1 = is_different_varchar2(salesdoc_return_auart, p_salesdoc_return_auart)       or
            ctl_act_cd != p_ctl_act_cd  /* not nullable */
          )
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_sorg_bs (CD=' || p_sorg_cd || ').');
      end if;
    else
      insert into tlgnr_sorg_bs (
        sorg_cd,
        companycode_cd,
        sorg_name,
        logo_image_name,
        address_street,
        address_countrycity,
        skip_send_distance_sap_sd,
        salesdoc_return_auart,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_sorg_cd,
        p_companycode_cd,
        p_sorg_name,
        p_logo_image_name,
        p_address_street,
        p_address_countrycity,
        p_skip_send_distance_sap_sd,
        p_salesdoc_return_auart,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_sorg_bs (CD=' || p_sorg_cd || ').');
    end if;

  end upsert_sorg_bs;
/
show errors;

-------------------------------------------------------------------------------
-- upsert_sorg_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_sorg_tx (
  p_sorg_cd             in varchar2,
  p_lang_cd             in varchar2,
  p_address_name        in varchar2,
  p_line_phone          in varchar2,
  p_line_fax            in varchar2) 
is
  v_exists_flag                   number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag                number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);

    dbms_output.put_line('Insert or Update tlgnr_sorg_tx with (' || p_sorg_cd || ', ' || p_lang_cd || ', ''' || p_address_name || ''', ' || p_address_name || ''', ' || p_line_phone || ''', ' || p_line_fax || ''')');

    -- Lookup tlgnr_sorg_bs entry first
    select count(bs.sorg_cd) into v_exists_bs_flag
    from tlgnr_sorg_bs bs
    where bs.sorg_cd = p_sorg_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.sorg_cd) into v_exists_flag
      from tlgnr_sorg_tx tx
      where tx.sorg_cd = p_sorg_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_sorg_tx set
          /* sorg_cd and lang_cd: cannot update because these are the keys */
          address_name    = p_address_name,
          line_phone      = p_line_phone,
          line_fax        = p_line_fax,
          ctl_mod_uid     = 1,
          ctl_mod_ts      = systimestamp
        where
          sorg_cd = p_sorg_cd and lang_cd = p_lang_cd               and
          ( 1 = is_different_varchar2(address_name, p_address_name) or
            1 = is_different_varchar2(line_phone, p_line_phone)     or
            1 = is_different_varchar2(line_fax, p_line_fax)
          )
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_sorg_tx (CD=' || p_sorg_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_sorg_tx (
          sorg_cd,
          lang_cd,
          address_name,
          line_phone,
          line_fax,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_sorg_cd,
          p_lang_cd,
          p_address_name,
          p_line_phone,
          p_line_fax,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_sorg_tx (CD=' || p_sorg_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in tlgnr_plant_bs (CD=' || p_sorg_cd || ').');
    end if;

  end upsert_sorg_tx;
/
show errors;

prompt INSERTING into TLGNR_SORG_BS (incomplete)
execute upsert_sorg_bs('AD01', 'AD01', 'Sales Org. AD01', 'pdf_crh_logo', 'AD01 address_street', 'AD01 address_countrycity', 0, 'ZRET', 1);
execute upsert_sorg_bs('AD02', 'AD02', 'Sales Org. AD02', 'pdf_crh_logo', 'AD02 address_street', 'AD02 address_countrycity', 0, 'ZRET', 1);
execute upsert_sorg_bs('AD03', 'AD03', 'Sales Org. AD03', 'pdf_crh_logo', 'AD03 address_street', 'AD03 address_countrycity', 0, 'ZRET', 1);
execute upsert_sorg_bs('ZA01', 'ZA01', 'Sales Org. ZA01', 'pdf_crh_logo', 'ZA01 address_street', 'ZA01 address_countrycity', 0, 'ZRET', 1);
execute upsert_sorg_bs('ZA02', 'ZA02', 'Sales Org. ZA02', 'pdf_crh_logo', 'ZA02 address_street', 'ZA02 address_countrycity', 0, 'ZRET', 1);
execute upsert_sorg_bs('ZA03', 'ZA03', 'Sales Org. ZA03', 'pdf_crh_logo', 'ZA03 address_street', 'ZA03 address_countrycity', 0, 'ZRET', 1);
execute upsert_sorg_bs('CH90', 'CH90', 'Sales Org. CH90', 'pdf_crh_logo', 'CH90 address_street', 'CH90 address_countrycity', 0, 'ZRET', 1);

prompt INSERTING into TLGNR_SORG_TX - EN (incomplete)
execute upsert_sorg_tx('AD01', 'en', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'en', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'en', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'en', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'en', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'en', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'en', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - DE (incomplete)
execute upsert_sorg_tx('AD01', 'de', 'AD01 address_name (de)', 'AD01 line_phone (de)', 'AD01 line_fax (de)');
execute upsert_sorg_tx('AD02', 'de', 'AD02 address_name (de)', 'AD02 line_phone (de)', 'AD02 line_fax (de)');
execute upsert_sorg_tx('AD03', 'de', 'AD03 address_name (de)', 'AD03 line_phone (de)', 'AD03 line_fax (de)');
execute upsert_sorg_tx('ZA01', 'de', 'ZA01 address_name (de)', 'ZA01 line_phone (de)', 'ZA01 line_fax (de)');
execute upsert_sorg_tx('ZA02', 'de', 'ZA02 address_name (de)', 'ZA02 line_phone (de)', 'ZA02 line_fax (de)');
execute upsert_sorg_tx('ZA03', 'de', 'ZA03 address_name (de)', 'ZA03 line_phone (de)', 'ZA03 line_fax (de)');
execute upsert_sorg_tx('CH90', 'de', 'CH90 address_name (de)', 'CH90 line_phone (de)', 'CH90 line_fax (de)');

prompt INSERTING into TLGNR_SORG_TX - FR (incomplete)
execute upsert_sorg_tx('AD01', 'fr', 'AD01 address_name (fr)', 'AD01 line_phone (fr)', 'AD01 line_fax (fr)');
execute upsert_sorg_tx('AD02', 'fr', 'AD02 address_name (fr)', 'AD02 line_phone (fr)', 'AD02 line_fax (fr)');
execute upsert_sorg_tx('AD03', 'fr', 'AD03 address_name (fr)', 'AD03 line_phone (fr)', 'AD03 line_fax (fr)');
execute upsert_sorg_tx('ZA01', 'fr', 'ZA01 address_name (fr)', 'ZA01 line_phone (fr)', 'ZA01 line_fax (fr)');
execute upsert_sorg_tx('ZA02', 'fr', 'ZA02 address_name (fr)', 'ZA02 line_phone (fr)', 'ZA02 line_fax (fr)');
execute upsert_sorg_tx('ZA03', 'fr', 'ZA03 address_name (fr)', 'ZA03 line_phone (fr)', 'ZA03 line_fax (fr)');
execute upsert_sorg_tx('CH90', 'fr', 'CH90 address_name (fr)', 'CH90 line_phone (fr)', 'CH90 line_fax (fr)');

prompt INSERTING into TLGNR_SORG_TX - IT (incomplete)
execute upsert_sorg_tx('AD01', 'it', 'AD01 address_name (it)', 'AD01 line_phone (it)', 'AD01 line_fax (it)');
execute upsert_sorg_tx('AD02', 'it', 'AD02 address_name (it)', 'AD02 line_phone (it)', 'AD02 line_fax (it)');
execute upsert_sorg_tx('AD03', 'it', 'AD03 address_name (it)', 'AD03 line_phone (it)', 'AD03 line_fax (it)');
execute upsert_sorg_tx('ZA01', 'it', 'ZA01 address_name (it)', 'ZA01 line_phone (it)', 'ZA01 line_fax (it)');
execute upsert_sorg_tx('ZA02', 'it', 'ZA02 address_name (it)', 'ZA02 line_phone (it)', 'ZA02 line_fax (it)');
execute upsert_sorg_tx('ZA03', 'it', 'ZA03 address_name (it)', 'ZA03 line_phone (it)', 'ZA03 line_fax (it)');
execute upsert_sorg_tx('CH90', 'it', 'CH90 address_name (it)', 'CH90 line_phone (it)', 'CH90 line_fax (it)');

prompt INSERTING into TLGNR_SORG_TX - ES (incomplete)
execute upsert_sorg_tx('AD01', 'es', 'AD01 address_name (es)', 'AD01 line_phone (es)', 'AD01 line_fax (es)');
execute upsert_sorg_tx('AD02', 'es', 'AD02 address_name (es)', 'AD02 line_phone (es)', 'AD02 line_fax (es)');
execute upsert_sorg_tx('AD03', 'es', 'AD03 address_name (es)', 'AD03 line_phone (es)', 'AD03 line_fax (es)');
execute upsert_sorg_tx('ZA01', 'es', 'ZA01 address_name (es)', 'ZA01 line_phone (es)', 'ZA01 line_fax (es)');
execute upsert_sorg_tx('ZA02', 'es', 'ZA02 address_name (es)', 'ZA02 line_phone (es)', 'ZA02 line_fax (es)');
execute upsert_sorg_tx('ZA03', 'es', 'ZA03 address_name (es)', 'ZA03 line_phone (es)', 'ZA03 line_fax (es)');
execute upsert_sorg_tx('CH90', 'es', 'CH90 address_name (es)', 'CH90 line_phone (es)', 'CH90 line_fax (es)');

prompt INSERTING into TLGNR_SORG_TX - CS (incomplete)
execute upsert_sorg_tx('AD01', 'cs', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'cs', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'cs', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'cs', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'cs', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'cs', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'cs', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - SK (incomplete)
execute upsert_sorg_tx('AD01', 'sk', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'sk', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'sk', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'sk', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'sk', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'sk', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'sk', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - HU (incomplete)
execute upsert_sorg_tx('AD01', 'hu', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'hu', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'hu', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'hu', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'hu', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'hu', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'hu', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - RO (incomplete)
execute upsert_sorg_tx('AD01', 'ro', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'ro', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'ro', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'ro', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'ro', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'ro', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'ro', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - BG (incomplete)
execute upsert_sorg_tx('AD01', 'bg', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'bg', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'bg', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'bg', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'bg', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'bg', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'bg', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - RU (incomplete)
execute upsert_sorg_tx('AD01', 'ru', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'ru', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'ru', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'ru', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'ru', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'ru', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'ru', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - PL (incomplete)
execute upsert_sorg_tx('AD01', 'pl', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'pl', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'pl', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'pl', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'pl', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'pl', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'pl', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - AZ (incomplete)
execute upsert_sorg_tx('AD01', 'az', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'az', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'az', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'az', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'az', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'az', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'az', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - HR (incomplete)
execute upsert_sorg_tx('AD01', 'hr', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'hr', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'hr', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'hr', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'hr', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'hr', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'hr', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - SR (incomplete)
execute upsert_sorg_tx('AD01', 'sr', 'AD01 address_name (en)', 'AD01 line_phone (en)', 'AD01 line_fax (en)');
execute upsert_sorg_tx('AD02', 'sr', 'AD02 address_name (en)', 'AD02 line_phone (en)', 'AD02 line_fax (en)');
execute upsert_sorg_tx('AD03', 'sr', 'AD03 address_name (en)', 'AD03 line_phone (en)', 'AD03 line_fax (en)');
execute upsert_sorg_tx('ZA01', 'sr', 'ZA01 address_name (en)', 'ZA01 line_phone (en)', 'ZA01 line_fax (en)');
execute upsert_sorg_tx('ZA02', 'sr', 'ZA02 address_name (en)', 'ZA02 line_phone (en)', 'ZA02 line_fax (en)');
execute upsert_sorg_tx('ZA03', 'sr', 'ZA03 address_name (en)', 'ZA03 line_phone (en)', 'ZA03 line_fax (en)');
execute upsert_sorg_tx('CH90', 'sr', 'CH90 address_name (en)', 'CH90 line_phone (en)', 'CH90 line_fax (en)');

prompt INSERTING into TLGNR_SORG_TX - TR (incomplete)
execute upsert_sorg_tx('AD01', 'tr', 'AD01 address_name (tr)', 'AD01 line_phone (tr)', 'AD01 line_fax (tr)');
execute upsert_sorg_tx('AD02', 'tr', 'AD02 address_name (tr)', 'AD02 line_phone (tr)', 'AD02 line_fax (tr)');
execute upsert_sorg_tx('AD03', 'tr', 'AD03 address_name (tr)', 'AD03 line_phone (tr)', 'AD03 line_fax (tr)');
execute upsert_sorg_tx('ZA01', 'tr', 'ZA01 address_name (tr)', 'ZA01 line_phone (tr)', 'ZA01 line_fax (tr)');
execute upsert_sorg_tx('ZA02', 'tr', 'ZA02 address_name (tr)', 'ZA02 line_phone (tr)', 'ZA02 line_fax (tr)');
execute upsert_sorg_tx('ZA03', 'tr', 'ZA03 address_name (tr)', 'ZA03 line_phone (tr)', 'ZA03 line_fax (tr)');
execute upsert_sorg_tx('CH90', 'tr', 'CH90 address_name (tr)', 'CH90 line_phone (tr)', 'CH90 line_fax (tr)');

commit;

drop procedure upsert_sorg_bs;
drop procedure upsert_sorg_tx;
drop function is_different_number;
drop function is_different_varchar2;

