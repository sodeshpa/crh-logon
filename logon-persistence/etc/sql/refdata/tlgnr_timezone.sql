-- $Revision$
-- $Date$
--
-- LOGON-6681: Timezone Handling / Support
--
-- The timezone ids used here are the Olson ids and are only used internal for technical purposes

prompt '---------- refdata/tlgnr_timezone.sql ----------'

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
-- tlgnr_timezone_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_timezone_bs (
    p_timezone_cd             in varchar2,
    p_sap_country_cd          in varchar2,
    p_ctl_act_cd              in number

) is
    v_exists_flag                   number;        -- number of existing records of that key (0 or 1)

begin
  dbms_output.enable(100000);

  dbms_output.put_line('Insert or Update tlgnr_timezone_bs (' || p_timezone_cd || ', ''' || p_sap_country_cd || ''', ctl_act_cd=' || p_ctl_act_cd || ')');

  ----------------------------------------------------------------------------
  -- Lookup tlgnr_timezone_bs
  ----------------------------------------------------------------------------
  select count(bs.timezone_cd) into v_exists_flag
    from tlgnr_timezone_bs bs
  where bs.timezone_cd = p_timezone_cd;

  if (v_exists_flag > 0) then
    if (p_ctl_act_cd != 1) then
      dbms_output.put_line ('NOT updating tlgnr_timezone_bs with timezone_cd = ' || p_timezone_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
    else
      update tlgnr_timezone_bs set
        /* timezone_cd: cannot update because this is the key */
        sap_country_cd                 = p_sap_country_cd,
        ctl_act_cd                     = p_ctl_act_cd,
        ctl_mod_uid                    = 1,
        ctl_mod_ts                     = systimestamp
      where
        timezone_cd = p_timezone_cd and
        ctl_act_cd != p_ctl_act_cd  /* not nullable */
      ;
      dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_timezone_bs (CD=' || p_timezone_cd || ').');
    end if;
  else
    insert into tlgnr_timezone_bs (
      timezone_cd,
      sap_country_cd,
      ctl_act_cd,
      ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
    ) values (
      p_timezone_cd,
      p_sap_country_cd,
      p_ctl_act_cd,
      1, systimestamp, 1, systimestamp
    );
    dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_timezone_bs (CD=' || p_timezone_cd || ').');
  end if;

end upsert_timezone_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_timezone_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_timezone_tx (
    p_timezone_cd             in varchar2,
    p_lang_cd                 in varchar2,
    p_text                    in varchar2

) is
    v_exists_flag                   number;        -- number of existing records of that key (0 or 1)
    v_exists_bs_flag                number;        -- number of existing BASE records

begin
  dbms_output.enable(100000);

  dbms_output.put_line('Insert or Update tlgnr_timezone_tx with (' || p_timezone_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

  ----------------------------------------------------------------------------
  -- Lookup tlgnr_timezone_bs entry first
  ----------------------------------------------------------------------------
  select count(bs.timezone_cd) into v_exists_bs_flag
    from tlgnr_timezone_bs bs
  where bs.timezone_cd = p_timezone_cd;

  if (v_exists_bs_flag > 0) then
    ----------------------------------------------------------------------------
    -- Lookup tlgnr_timezone_tx
    ----------------------------------------------------------------------------
    select count(tx.timezone_cd) into v_exists_flag
      from tlgnr_timezone_tx tx
    where tx.timezone_cd = p_timezone_cd and tx.lang_cd = p_lang_cd;

    if (v_exists_flag > 0) then

      update tlgnr_timezone_tx set
        /* p_timezone_cd and lang_cd: cannot update because these are the keys */
        txt                            = p_text,
        ctl_mod_uid                    = 1,
        ctl_mod_ts                     = systimestamp
      where
        timezone_cd = p_timezone_cd and
        lang_cd = p_lang_cd         and
        1 = is_different_varchar2(txt, p_text)
      ;
      dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_timezone_tx (CD=' || p_timezone_cd || ', LANG=' || p_lang_cd || ').');

    else
      insert into tlgnr_timezone_tx (
        timezone_cd,
        lang_cd,
        txt,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_timezone_cd,
        p_lang_cd,
        p_text,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_timezone_tx (CD=' || p_timezone_cd || ', LANG=' || p_lang_cd || ').');
    end if;
  else
    dbms_output.put_line ('ERROR: Missing entry in tlgnr_timezone_bs (CD=' || p_timezone_cd || ').');
  end if;

end upsert_timezone_tx;
/
show errors;

prompt INSERTING into TLGNR_TIMEZONE_BS
execute upsert_timezone_bs('Europe/Paris', 'FR', 1);
execute upsert_timezone_bs('Europe/Brussels', 'BE', 1);
execute upsert_timezone_bs('Europe/Luxembourg', 'LU', 1);
execute upsert_timezone_bs('Europe/Amsterdam', 'NL', 1);
execute upsert_timezone_bs('Europe/Zurich', 'CH', 1);
execute upsert_timezone_bs('Europe/Rome', 'IT', 1);
execute upsert_timezone_bs('Europe/Berlin', 'DE', 1);
execute upsert_timezone_bs('Europe/Sofia', 'BG', 1);
execute upsert_timezone_bs('Europe/Prague', 'CZ', 1);
execute upsert_timezone_bs('Europe/Zagreb', 'HR', 1);
execute upsert_timezone_bs('Europe/Budapest', 'HU', 1);
execute upsert_timezone_bs('Europe/Bucharest', 'RO', 1);
execute upsert_timezone_bs('Europe/Belgrade', 'RS', 1);
execute upsert_timezone_bs('Europe/Bratislava', 'SK', 1);
execute upsert_timezone_bs('Europe/Madrid', 'ES', 1);
execute upsert_timezone_bs('Atlantic/Canary', 'ES', 1);
execute upsert_timezone_bs('Asia/Baku', 'AZ', 1);
execute upsert_timezone_bs('Asia/Beirut', 'LB', 1);
execute upsert_timezone_bs('Africa/Casablanca', 'MA', 1);
execute upsert_timezone_bs('Europe/Kaliningrad', 'RU', 1);
execute upsert_timezone_bs('Europe/Moscow', 'RU', 1);
execute upsert_timezone_bs('Asia/Yekaterinburg', 'RU', 1);
execute upsert_timezone_bs('Asia/Omsk', 'RU', 1);
execute upsert_timezone_bs('Asia/Krasnoyarsk', 'RU', 1);
execute upsert_timezone_bs('Asia/Irkutsk', 'RU', 1);
execute upsert_timezone_bs('Asia/Yakutsk', 'RU', 1);
execute upsert_timezone_bs('Asia/Vladivostok', 'RU', 1);
execute upsert_timezone_bs('Asia/Magadan', 'RU', 1);
execute upsert_timezone_bs('Europe/Sarajevo', 'BA', 1);
execute upsert_timezone_bs('Europe/Pristina', 'XK', 1);
execute upsert_timezone_bs('Europe/Ljubljana', 'SI', 1);
execute upsert_timezone_bs('Europe/Vienna', 'AT', 1);
execute upsert_timezone_bs('Europe/Warsaw', 'PL', 1);
execute upsert_timezone_bs('Europe/Chisinau', 'MD', 1);
execute upsert_timezone_bs('Europe/Copenhagen', 'DK', 1);
execute upsert_timezone_bs('Europe/Kiev', 'UA', 1);
execute upsert_timezone_bs('Europe/Riga', 'LV', 1);
-- Fer changes
execute upsert_timezone_bs('Europe/Chisinau', 'MD', 1);
execute upsert_timezone_bs('Europe/Istanbul', 'TR', 1);
execute upsert_timezone_bs('Europe/Stockholm','SE',1);
execute upsert_timezone_bs('America/Chicago','US',1);
execute upsert_timezone_bs('Pacific/Noumea','NC',1);
execute upsert_timezone_bs('Africa/Abidjan','CI',1);
execute upsert_timezone_bs('Asia/Qatar','QA',1);
execute upsert_timezone_bs('Asia/Nicosia','CY',1);
execute upsert_timezone_bs('Europe/London','GB',1);
execute upsert_timezone_bs('Asia/Almaty','KZ',1);
execute upsert_timezone_bs('Asia/Damascus','SY',1);
execute upsert_timezone_bs('Asia/Jerusalen','IL',1);
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - EN
execute upsert_timezone_tx('Europe/Paris', 'en', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'en', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'en', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'en', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'en', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'en', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'en', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'en', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'en', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'en', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'en', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'en', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'en', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'en', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'en', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'en', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'en', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'en', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'en', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'en', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'en', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'en', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'en', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'en', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'en', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'en', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'en', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'en', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'en', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'en', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'en', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'en', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'en', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'en', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'en', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'en', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'en', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'en', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'en', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','en','Stockholm');
execute upsert_timezone_tx('America/Chicago','en','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','en','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','en','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','en','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','en','Nicosia');
execute upsert_timezone_tx('Europe/London','en','London');
execute upsert_timezone_tx('Asia/Almaty','en','Almaty');
execute upsert_timezone_tx('Asia/Damascus','en','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','en','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - DE
execute upsert_timezone_tx('Europe/Paris', 'de', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'de', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'de', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'de', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'de', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'de', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'de', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'de', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'de', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'de', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'de', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'de', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'de', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'de', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'de', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'de', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'de', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'de', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'de', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'de', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'de', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'de', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'de', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'de', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'de', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'de', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'de', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'de', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'de', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'de', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'de', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'de', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'de', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'de', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'de', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'de', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'de', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'de', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'de', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','de','Stockholm');
execute upsert_timezone_tx('America/Chicago','de','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','de','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','de','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','de','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','de','Nicosia');
execute upsert_timezone_tx('Europe/London','de','London');
execute upsert_timezone_tx('Asia/Almaty','de','Almaty');
execute upsert_timezone_tx('Asia/Damascus','de','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','de','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - FR
execute upsert_timezone_tx('Europe/Paris', 'fr', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'fr', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'fr', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'fr', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'fr', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'fr', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'fr', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'fr', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'fr', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'fr', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'fr', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'fr', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'fr', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'fr', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'fr', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'fr', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'fr', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'fr', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'fr', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'fr', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'fr', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'fr', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'fr', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'fr', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'fr', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'fr', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'fr', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'fr', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'fr', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'fr', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'fr', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'fr', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'fr', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'fr', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'fr', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'fr', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'fr', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'fr', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'fr', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','fr','Stockholm');
execute upsert_timezone_tx('America/Chicago','fr','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','fr','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','fr','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','fr','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','fr','Nicosia');
execute upsert_timezone_tx('Europe/London','fr','London');
execute upsert_timezone_tx('Asia/Almaty','fr','Almaty');
execute upsert_timezone_tx('Asia/Damascus','fr','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','fr','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - IT
execute upsert_timezone_tx('Europe/Paris', 'it', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'it', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'it', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'it', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'it', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'it', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'it', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'it', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'it', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'it', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'it', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'it', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'it', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'it', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'it', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'it', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'it', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'it', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'it', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'it', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'it', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'it', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'it', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'it', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'it', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'it', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'it', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'it', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'it', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'it', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'it', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'it', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'it', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'it', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'it', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'it', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'it', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'it', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'it', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','it','Stockholm');
execute upsert_timezone_tx('America/Chicago','it','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','it','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','it','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','it','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','it','Nicosia');
execute upsert_timezone_tx('Europe/London','it','London');
execute upsert_timezone_tx('Asia/Almaty','it','Almaty');
execute upsert_timezone_tx('Asia/Damascus','it','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','it','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - ES
execute upsert_timezone_tx('Europe/Paris', 'es', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'es', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'es', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'es', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'es', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'es', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'es', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'es', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'es', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'es', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'es', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'es', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'es', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'es', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'es', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'es', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'es', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'es', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'es', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'es', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'es', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'es', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'es', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'es', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'es', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'es', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'es', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'es', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'es', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'es', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'es', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'es', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'es', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'es', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'es', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'es', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'es', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'es', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'es', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','es','Stockholm');
execute upsert_timezone_tx('America/Chicago','es','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','es','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','es','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','es','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','es','Nicosia');
execute upsert_timezone_tx('Europe/London','es','London');
execute upsert_timezone_tx('Asia/Almaty','es','Almaty');
execute upsert_timezone_tx('Asia/Damascus','es','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','es','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - CS
execute upsert_timezone_tx('Europe/Paris', 'cs', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'cs', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'cs', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'cs', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'cs', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'cs', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'cs', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'cs', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'cs', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'cs', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'cs', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'cs', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'cs', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'cs', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'cs', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'cs', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'cs', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'cs', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'cs', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'cs', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'cs', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'cs', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'cs', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'cs', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'cs', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'cs', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'cs', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'cs', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'cs', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'cs', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'cs', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'cs', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'cs', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'cs', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'cs', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'cs', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'cs', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'cs', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'cs', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','cs','Stockholm');
execute upsert_timezone_tx('America/Chicago','cs','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','cs','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','cs','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','cs','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','cs','Nicosia');
execute upsert_timezone_tx('Europe/London','cs','London');
execute upsert_timezone_tx('Asia/Almaty','cs','Almaty');
execute upsert_timezone_tx('Asia/Damascus','cs','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','cs','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - SK
execute upsert_timezone_tx('Europe/Paris', 'sk', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'sk', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'sk', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'sk', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'sk', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'sk', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'sk', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'sk', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'sk', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'sk', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'sk', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'sk', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'sk', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'sk', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'sk', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'sk', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'sk', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'sk', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'sk', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'sk', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'sk', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'sk', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'sk', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'sk', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'sk', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'sk', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'sk', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'sk', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'sk', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'sk', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'sk', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'sk', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'sk', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'sk', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'sk', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'sk', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'sk', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'sk', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'sk', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','sk','Stockholm');
execute upsert_timezone_tx('America/Chicago','sk','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','sk','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','sk','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','sk','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','sk','Nicosia');
execute upsert_timezone_tx('Europe/London','sk','London');
execute upsert_timezone_tx('Asia/Almaty','sk','Almaty');
execute upsert_timezone_tx('Asia/Damascus','sk','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','sk','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - HU
execute upsert_timezone_tx('Europe/Paris', 'hu', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'hu', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'hu', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'hu', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'hu', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'hu', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'hu', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'hu', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'hu', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'hu', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'hu', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'hu', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'hu', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'hu', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'hu', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'hu', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'hu', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'hu', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'hu', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'hu', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'hu', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'hu', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'hu', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'hu', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'hu', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'hu', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'hu', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'hu', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'hu', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'hu', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'hu', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'hu', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'hu', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'hu', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'hu', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'hu', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'hu', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'hu', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'hu', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','hu','Stockholm');
execute upsert_timezone_tx('America/Chicago','hu','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','hu','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','hu','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','hu','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','hu','Nicosia');
execute upsert_timezone_tx('Europe/London','hu','London');
execute upsert_timezone_tx('Asia/Almaty','hu','Almaty');
execute upsert_timezone_tx('Asia/Damascus','hu','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','hu','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - RO
execute upsert_timezone_tx('Europe/Paris', 'ro', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'ro', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'ro', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'ro', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'ro', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'ro', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'ro', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'ro', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'ro', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'ro', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'ro', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'ro', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'ro', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'ro', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'ro', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'ro', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'ro', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'ro', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'ro', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'ro', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'ro', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'ro', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'ro', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'ro', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'ro', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'ro', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'ro', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'ro', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'ro', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'ro', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'ro', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'ro', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'ro', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'ro', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'ro', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'ro', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'ro', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'ro', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'ro', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','ro','Stockholm');
execute upsert_timezone_tx('America/Chicago','ro','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','ro','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','ro','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','ro','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','ro','Nicosia');
execute upsert_timezone_tx('Europe/London','ro','London');
execute upsert_timezone_tx('Asia/Almaty','ro','Almaty');
execute upsert_timezone_tx('Asia/Damascus','ro','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','ro','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - BG
execute upsert_timezone_tx('Europe/Paris', 'bg', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'bg', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'bg', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'bg', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'bg', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'bg', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'bg', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'bg', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'bg', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'bg', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'bg', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'bg', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'bg', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'bg', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'bg', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'bg', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'bg', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'bg', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'bg', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'bg', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'bg', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'bg', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'bg', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'bg', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'bg', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'bg', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'bg', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'bg', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'bg', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'bg', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'bg', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'bg', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'bg', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'bg', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'bg', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'bg', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'bg', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'bg', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'bg', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','bg','Stockholm');
execute upsert_timezone_tx('America/Chicago','bg','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','bg','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','bg','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','bg','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','bg','Nicosia');
execute upsert_timezone_tx('Europe/London','bg','London');
execute upsert_timezone_tx('Asia/Almaty','bg','Almaty');
execute upsert_timezone_tx('Asia/Damascus','bg','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','bg','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - RU (ENGLISH COPY)
execute upsert_timezone_tx('Europe/Paris', 'ru', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'ru', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'ru', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'ru', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'ru', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'ru', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'ru', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'ru', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'ru', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'ru', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'ru', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'ru', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'ru', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'ru', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'ru', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'ru', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'ru', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'ru', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'ru', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'ru', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'ru', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'ru', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'ru', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'ru', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'ru', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'ru', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'ru', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'ru', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'ru', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'ru', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'ru', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'ru', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'ru', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'ru', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'ru', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'ru', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'ru', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'ru', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'ru', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','ru','Stockholm');
execute upsert_timezone_tx('America/Chicago','ru','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','ru','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','ru','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','ru','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','ru','Nicosia');
execute upsert_timezone_tx('Europe/London','ru','London');
execute upsert_timezone_tx('Asia/Almaty','ru','Almaty');
execute upsert_timezone_tx('Asia/Damascus','ru','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','ru','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - PL (ENGLISH COPY)
execute upsert_timezone_tx('Europe/Paris', 'pl', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'pl', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'pl', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'pl', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'pl', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'pl', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'pl', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'pl', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'pl', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'pl', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'pl', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'pl', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'pl', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'pl', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'pl', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'pl', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'pl', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'pl', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'pl', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'pl', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'pl', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'pl', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'pl', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'pl', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'pl', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'pl', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'pl', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'pl', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'pl', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'pl', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'pl', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'pl', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'pl', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'pl', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'pl', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'pl', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'pl', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'pl', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'pl', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','pl','Stockholm');
execute upsert_timezone_tx('America/Chicago','pl','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','pl','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','pl','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','pl','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','pl','Nicosia');
execute upsert_timezone_tx('Europe/London','pl','London');
execute upsert_timezone_tx('Asia/Almaty','pl','Almaty');
execute upsert_timezone_tx('Asia/Damascus','pl','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','pl','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - AZ (ENGLISH COPY)
execute upsert_timezone_tx('Europe/Paris', 'az', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'az', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'az', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'az', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'az', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'az', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'az', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'az', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'az', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'az', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'az', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'az', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'az', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'az', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'az', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'az', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'az', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'az', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'az', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'az', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'az', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'az', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'az', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'az', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'az', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'az', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'az', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'az', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'az', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'az', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'az', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'az', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'az', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'az', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'az', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'az', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'az', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'az', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'az', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','az','Stockholm');
execute upsert_timezone_tx('America/Chicago','az','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','az','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','az','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','az','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','az','Nicosia');
execute upsert_timezone_tx('Europe/London','az','London');
execute upsert_timezone_tx('Asia/Almaty','az','Almaty');
execute upsert_timezone_tx('Asia/Damascus','az','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','az','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - HR (ENGLISH COPY)
execute upsert_timezone_tx('Europe/Paris', 'hr', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'hr', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'hr', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'hr', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'hr', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'hr', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'hr', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'hr', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'hr', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'hr', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'hr', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'hr', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'hr', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'hr', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'hr', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'hr', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'hr', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'hr', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'hr', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'hr', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'hr', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'hr', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'hr', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'hr', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'hr', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'hr', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'hr', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'hr', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'hr', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'hr', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'hr', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'hr', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'hr', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'hr', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'hr', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'hr', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'hr', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'hr', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'hr', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','hr','Stockholm');
execute upsert_timezone_tx('America/Chicago','hr','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','hr','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','hr','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','hr','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','hr','Nicosia');
execute upsert_timezone_tx('Europe/London','hr','London');
execute upsert_timezone_tx('Asia/Almaty','hr','Almaty');
execute upsert_timezone_tx('Asia/Damascus','hr','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','hr','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - SR (ENGLISH COPY)
execute upsert_timezone_tx('Europe/Paris', 'sr', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'sr', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'sr', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'sr', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'sr', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'sr', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'sr', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'sr', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'sr', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'sr', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'sr', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'sr', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'sr', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'sr', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'sr', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'sr', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'sr', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'sr', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'sr', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'sr', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'sr', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'sr', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'sr', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'sr', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'sr', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'sr', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'sr', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'sr', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'sr', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'sr', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'sr', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'sr', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'sr', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'sr', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'sr', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'sr', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'sr', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'sr', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'sr', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','sr','Stockholm');
execute upsert_timezone_tx('America/Chicago','sr','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','sr','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','sr','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','sr','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','sr','Nicosia');
execute upsert_timezone_tx('Europe/London','sr','London');
execute upsert_timezone_tx('Asia/Almaty','sr','Almaty');
execute upsert_timezone_tx('Asia/Damascus','sr','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','sr','Jerusalen');
-- Fer changes

prompt INSERTING into TLGNR_TIMEZONE_TX - TR
execute upsert_timezone_tx('Europe/Paris', 'tr', 'Paris');
execute upsert_timezone_tx('Europe/Brussels', 'tr', 'Brussels');
execute upsert_timezone_tx('Europe/Luxembourg', 'tr', 'Luxembourg');
execute upsert_timezone_tx('Europe/Amsterdam', 'tr', 'Amsterdam');
execute upsert_timezone_tx('Europe/Zurich', 'tr', 'Zürich');
execute upsert_timezone_tx('Europe/Rome', 'tr', 'Rome');
execute upsert_timezone_tx('Europe/Berlin', 'tr', 'Berlin');
execute upsert_timezone_tx('Europe/Sofia', 'tr', 'Sofia');
execute upsert_timezone_tx('Europe/Prague', 'tr', 'Prague');
execute upsert_timezone_tx('Europe/Zagreb', 'tr', 'Zagreb');
execute upsert_timezone_tx('Europe/Budapest', 'tr', 'Budapest');
execute upsert_timezone_tx('Europe/Bucharest', 'tr', 'Bucharest');
execute upsert_timezone_tx('Europe/Belgrade', 'tr', 'Belgrade');
execute upsert_timezone_tx('Europe/Bratislava', 'tr', 'Bratislava');
execute upsert_timezone_tx('Europe/Madrid', 'tr', 'Madrid');
execute upsert_timezone_tx('Atlantic/Canary', 'tr', 'Canary Islands');
execute upsert_timezone_tx('Asia/Baku', 'tr', 'Baku');
execute upsert_timezone_tx('Asia/Beirut', 'tr', 'Beirut');
execute upsert_timezone_tx('Africa/Casablanca', 'tr', 'Casablanca');
execute upsert_timezone_tx('Europe/Kaliningrad', 'tr', 'Kaliningrad');
execute upsert_timezone_tx('Europe/Moscow', 'tr', 'Moscow');
execute upsert_timezone_tx('Asia/Yekaterinburg', 'tr', 'Yekaterinburg');
execute upsert_timezone_tx('Asia/Omsk', 'tr', 'Omsk');
execute upsert_timezone_tx('Asia/Krasnoyarsk', 'tr', 'Krasnoyarsk');
execute upsert_timezone_tx('Asia/Irkutsk', 'tr', 'Irkutsk');
execute upsert_timezone_tx('Asia/Yakutsk', 'tr', 'Yakutsk');
execute upsert_timezone_tx('Asia/Vladivostok', 'tr', 'Vladivostok');
execute upsert_timezone_tx('Asia/Magadan', 'tr', 'Magadan');
execute upsert_timezone_tx('Europe/Sarajevo', 'tr', 'Sarajevo');
execute upsert_timezone_tx('Europe/Pristina', 'tr', 'Pristina');
execute upsert_timezone_tx('Europe/Ljubljana', 'tr', 'Ljubljana');
execute upsert_timezone_tx('Europe/Vienna', 'tr', 'Vienna');
execute upsert_timezone_tx('Europe/Warsaw', 'tr', 'Warsaw');
execute upsert_timezone_tx('Europe/Chisinau', 'tr', 'Chisinau');
execute upsert_timezone_tx('Europe/Copenhagen', 'tr', 'Copenhagen');
execute upsert_timezone_tx('Europe/Kiev', 'tr', 'Kiev');
execute upsert_timezone_tx('Europe/Riga', 'tr', 'Riga');
-- Fer changes
execute upsert_timezone_tx('Europe/Chisinau', 'tr', 'Chisinau');
execute upsert_timezone_tx('Europe/Istanbul', 'tr', 'Istanbul');
execute upsert_timezone_tx('Europe/Stockholm','tr','Stockholm');
execute upsert_timezone_tx('America/Chicago','tr','Chicago');
execute upsert_timezone_tx('Pacific/Noumea','tr','Noumea');
execute upsert_timezone_tx('Africa/Abidjan','tr','Abidjan');
execute upsert_timezone_tx('Asia/Qatar','tr','Qatar');
execute upsert_timezone_tx('Asia/Nicosia','tr','Nicosia');
execute upsert_timezone_tx('Europe/London','tr','London');
execute upsert_timezone_tx('Asia/Almaty','tr','Almaty');
execute upsert_timezone_tx('Asia/Damascus','tr','Damascus');
execute upsert_timezone_tx('Asia/Jerusalen','tr','Jerusalen');
-- Fer changes

commit;

drop procedure upsert_timezone_bs;
drop procedure upsert_timezone_tx;
drop function is_different_number;
drop function is_different_varchar2;

