-- $Revision$
-- $Date$
--

prompt '---------- refdata/tlgnr_country_region_tz_map.sql ----------'

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
-- They are identical either if a) both are '', b) they are equal by "="
-- They are different if a) either is '' and the other not, b) different by "="
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
-- upsert_tz_country_region_map
-------------------------------------------------------------------------------
create or replace
procedure upsert_tz_country_region_map (
    p_timezone_cd             in varchar2,
    p_sap_country_cd          in varchar2,
    p_sap_region_code         in varchar2,
    p_sap_timezone_desc       in varchar2,
    p_ctl_act_cd              in number

) is
    v_exists_flag                   number;        -- number of existing records of that key (0 or 1)
    v_exists_country_flag           number;        -- number of existing country records of that key (0 or 1)
    v_exists_timezone_flag          number;        -- number of existing timezone records of that key (0 or 1)

begin
  dbms_output.enable(100000);

  dbms_output.put_line('Insert or Update tlgnr_country_region_tz_map (' || p_timezone_cd || ', ''' || p_sap_country_cd || ''', ''' || p_sap_region_code || ''', ''' || p_sap_timezone_desc || ''', ctl_act_cd=' || p_ctl_act_cd || ')');

  -- (Precondition: must exist): Lookup tlgnr_sap_country_bs
  select count(bs.sap_country_cd) into v_exists_country_flag
  from tlgnr_sap_country_bs bs
  where bs.sap_country_cd = p_sap_country_cd;

  if (v_exists_country_flag > 0) then
    -- (Precondition: must exist): Lookup tlgnr_timezone_bs
    select count(bs.timezone_cd) into v_exists_timezone_flag
    from tlgnr_timezone_bs bs
    where bs.timezone_cd = p_timezone_cd;

    if (v_exists_timezone_flag > 0) then
      -- Now lookup record to insert or update
      select count(m.country_region_tz_map_id) into v_exists_flag
      from tlgnr_country_region_tz_map m
      where m.sap_country_cd = p_sap_country_cd and
            ((p_sap_region_code is not null and m.sap_region_code = p_sap_region_code) or
             (p_sap_region_code is null and m.sap_region_code is null));

      if (v_exists_flag > 0) then
        if (p_ctl_act_cd != 1) then
          dbms_output.put_line ('NOT updating tlgnr_country_region_tz_map with sap_country_cd = ' || p_sap_country_cd || ' and sap_region_code = ' || p_sap_region_code || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
        else
          update tlgnr_country_region_tz_map set
            /* sap_country_cd/region: cannot update because these are the keys */
            timezone_cd                    = p_timezone_cd,
            sap_timezone_description       = p_sap_timezone_desc,
            ctl_act_cd                     = p_ctl_act_cd,
            ctl_mod_uid                    = 1,
            ctl_mod_ts                     = systimestamp
          where
            sap_country_cd = p_sap_country_cd and
            sap_region_code = p_sap_region_code and
            (ctl_act_cd != p_ctl_act_cd  /* not nullable */ or
             1 = is_different_varchar2 (sap_timezone_description, p_sap_timezone_desc))
          ;
          dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_country_region_tz_map (CD=' || p_sap_country_cd || '/' || p_sap_region_code || ')');
        end if;
      else
        insert into tlgnr_country_region_tz_map (
          country_region_tz_map_id,
          timezone_cd,
          sap_country_cd,
          sap_region_code,
          sap_timezone_description,
          ctl_act_cd,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          slgnr_country_region_tz_map.nextval,
          p_timezone_cd,
          p_sap_country_cd,
          p_sap_region_code,
          p_sap_timezone_desc,
          p_ctl_act_cd,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_country_region_tz_map (CD=' || p_sap_country_cd || '/' || p_sap_region_code || ')');
      end if;

    else
      dbms_output.put_line ('ERROR: Missing entry in tlgnr_timezone_bs (CD=' || p_timezone_cd || ').');
    end if;

  else
    dbms_output.put_line ('ERROR: Missing entry in tlgnr_sap_country_bs (CD=' || p_sap_country_cd || ').');
  end if;

end upsert_tz_country_region_map;
/
show errors;


prompt INSERTING into TLGNR_TZ_COUNTRY_REGION_MAP

execute upsert_tz_country_region_map('Africa/Casablanca', 'MA', '', 'UTC+0', 1);
execute upsert_tz_country_region_map('Atlantic/Canary', 'ES', '35', 'UTC+0', 1);
execute upsert_tz_country_region_map('Atlantic/Canary', 'ES', '38', 'UTC+0', 1);

execute upsert_tz_country_region_map('Asia/Beirut', 'LB', '', 'UTC+2', 1);

execute upsert_tz_country_region_map('Europe/Amsterdam', 'NL', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Belgrade', 'RS', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Berlin', 'DE', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Bratislava', 'SK', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Brussels', 'BE', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Bucharest', 'RO', '', 'UTC+2', 1);
execute upsert_tz_country_region_map('Europe/Budapest', 'HU', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Chisinau', 'MD', '', 'UTC+2', 1);
execute upsert_tz_country_region_map('Europe/Copenhagen', 'DK', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Kiev', 'UA', '', 'UTC+2', 1);
execute upsert_tz_country_region_map('Europe/Ljubljana', 'SI', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Luxembourg', 'LU', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Madrid', 'ES', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Paris', 'FR', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Prague', 'CZ', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Pristina', 'XK', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Riga', 'LV', '', 'UTC+2', 1);
execute upsert_tz_country_region_map('Europe/Rome', 'IT', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Sarajevo', 'BA', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Sofia', 'BG', '', 'UTC+2', 1);
execute upsert_tz_country_region_map('Europe/Vienna', 'AT', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Warsaw', 'PL', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Zagreb', 'HR', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Zurich', 'CH', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Istanbul', 'TR', '', 'UTC+2', 1);
execute upsert_tz_country_region_map('Europe/Stockholm','SE','','UTC+1',1);
execute upsert_tz_country_region_map('America/Chicago','US','','UTC-6',1);
execute upsert_tz_country_region_map('Pacific/Noumea','NC','','UTC+11',1);
execute upsert_tz_country_region_map('Africa/Abidjan','CI','','UTC+0',1);
execute upsert_tz_country_region_map('Asia/Qatar','QA','','UTC+3',1);
execute upsert_tz_country_region_map('Asia/Nicosia','CY','','UTC+2',1);
execute upsert_tz_country_region_map('Europe/London','GB','','UTC+0',1);
execute upsert_tz_country_region_map('Asia/Almaty','KZ','','UTC+6',1);
execute upsert_tz_country_region_map('Asia/Damascus','SY','','UTC+2',1);
execute upsert_tz_country_region_map('Asia/Jerusalen','IL','','UTC+2',1);

execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '01', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '05', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '06', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '07', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '08', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '09', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '10', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '11', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '12', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '13', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '15', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '16', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '18', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '19', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '20', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '21', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '23', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '26', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '29', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '30', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '31', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '32', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '33', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '34', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '35', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '36', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '37', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '40', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '43', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '44', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '46', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '47', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '48', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '50', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '51', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '52', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '53', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '57', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '58', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '60', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '61', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '62', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '64', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '67', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '68', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '69', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '71', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '75', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '76', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '77', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '78', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '73', 'UTC+3', 1);
execute upsert_tz_country_region_map('Europe/Kaliningrad', 'RU', '83', 'UTC+3', 1);

execute upsert_tz_country_region_map('Asia/Baku', 'AZ', '', 'UTC+4', 1);
execute upsert_tz_country_region_map('Europe/Moscow', 'RU', '39', 'UTC+4', 1);
execute upsert_tz_country_region_map('Europe/Moscow', 'RU', '63', 'UTC+4', 1);

execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '02', 'UTC+6', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '03', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '22', 'UTC+6', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '45', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '54', 'UTC+6', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '55', 'UTC+6', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '56', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '59', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '66', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '70', 'UTC+6', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '72', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '74', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '81', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '85', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '86', 'UTC+5', 1);
execute upsert_tz_country_region_map('Asia/Yekaterinburg', 'RU', '89', 'UTC+5', 1);

execute upsert_tz_country_region_map('Asia/Omsk', 'RU', '17', 'UTC+7', 1);
execute upsert_tz_country_region_map('Asia/Omsk', 'RU', '24', 'UTC+7', 1);
execute upsert_tz_country_region_map('Asia/Omsk', 'RU', '42', 'UTC+7', 1);
execute upsert_tz_country_region_map('Asia/Omsk', 'RU', '84', 'UTC+7', 1);

execute upsert_tz_country_region_map('Asia/Krasnoyarsk', 'RU', '04', 'UTC+8', 1);
execute upsert_tz_country_region_map('Asia/Krasnoyarsk', 'RU', '38', 'UTC+8', 1);
execute upsert_tz_country_region_map('Asia/Krasnoyarsk', 'RU', '80', 'UTC+8', 1);

execute upsert_tz_country_region_map('Asia/Irkutsk', 'RU', '28', 'UTC+9', 1);

execute upsert_tz_country_region_map('Asia/Yakutsk', 'RU', '14', 'UTC+10', 1);
execute upsert_tz_country_region_map('Asia/Yakutsk', 'RU', '25', 'UTC+10', 1);
execute upsert_tz_country_region_map('Asia/Yakutsk', 'RU', '27', 'UTC+10', 1);
execute upsert_tz_country_region_map('Asia/Yakutsk', 'RU', '65', 'UTC+10', 1);
execute upsert_tz_country_region_map('Asia/Yakutsk', 'RU', '79', 'UTC+10', 1);

execute upsert_tz_country_region_map('Asia/Vladivostok', 'RU', '49', 'UTC+11', 1);
execute upsert_tz_country_region_map('Asia/Vladivostok', 'RU', '87', 'UTC+11', 1);
execute upsert_tz_country_region_map('Asia/Vladivostok', 'RU', '88', 'UTC+11', 1);

execute upsert_tz_country_region_map('Asia/Magadan', 'RU', '41', 'UTC+12', 1);
execute upsert_tz_country_region_map('Asia/Magadan', 'RU', '82', 'UTC+12', 1);

-- Fer changes
execute upsert_tz_country_region_map('Europe/Copenhagen', 'DK', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Riga', 'LV', '', 'UTC+1', 1);
execute upsert_tz_country_region_map('Europe/Kiev', 'UA', '', 'UTC+2', 1);
execute upsert_tz_country_region_map('Europe/Chisinau', 'MD', '', 'UTC+2', 1);
-- Fer changes

commit;


drop procedure upsert_tz_country_region_map;
drop function is_different_number;
drop function is_different_varchar2;

