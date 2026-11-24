-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_region_bs.sql ----------'

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
-- tlgnr_region_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_region_bs (
    p_region_cd          in varchar2,
    p_region_name          in varchar2,
    p_ctl_act_cd              in number

) is
    v_exists_flag                   number;        -- number of existing records of that key (0 or 1)
    v_ctl_act_cd                    number := p_ctl_act_cd;

begin
  dbms_output.enable(100000);

  dbms_output.put_line('Insert or Update tlgnr_region_bs (' || p_region_cd || ', ' || 'ctl_act_cd=' || p_ctl_act_cd || ')');
  
  -- Be tolerant against bad initial data input: treat null as "LOGON standard", i.e. our meaningful defaults.
  if (v_ctl_act_cd is null) then
    v_ctl_act_cd := 1 /* active */;
  end if;

  ----------------------------------------------------------------------------
  -- Lookup tlgnr_region_bs
  -------------------------------------------------------------------------------
  select count(bs.region_cd) into v_exists_flag
    from tlgnr_region_bs bs
  where bs.region_cd = p_region_cd;

  if (v_exists_flag > 0) then
  
    update tlgnr_region_bs set
      /* region_cd: cannot update because this is the key */
      region_name 					 = p_region_name,
      ctl_act_cd                     = v_ctl_act_cd,
      ctl_mod_uid                    = 1,
      ctl_mod_ts                     = systimestamp
    where
      region_cd = p_region_cd and
      1 = is_different_number   (ctl_act_cd, v_ctl_act_cd)
    ;
    dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_region_bs (CD=' || p_region_cd || ').');
  
  else
    insert into tlgnr_region_bs (
      region_cd,
      region_name,
      ctl_act_cd,
      ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
    ) values (
      p_region_cd,
      p_region_name,
      v_ctl_act_cd,
      1, systimestamp, 1, systimestamp
    );
    dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_region_bs (CD=' || p_region_cd || ').');
  end if;

end upsert_region_bs;
/
show errors;

prompt INSERTING into tlgnr_region_bs - ACTIVE

execute upsert_region_bs('AD', null, 1);
execute upsert_region_bs('AE', null, 1);
execute upsert_region_bs('AT', null, 1);
execute upsert_region_bs('AU', null, 1);
execute upsert_region_bs('AZ', null, 1);
execute upsert_region_bs('BE', null, 1);
execute upsert_region_bs('BG', null, 1);
execute upsert_region_bs('CA', null, 1);
execute upsert_region_bs('CH', null, 1);
execute upsert_region_bs('CL', null, 1);
execute upsert_region_bs('CS', null, 1);
execute upsert_region_bs('CZ', null, 1);
execute upsert_region_bs('DE', null, 1);
execute upsert_region_bs('DK', null, 1);
execute upsert_region_bs('ES', null, 1);
execute upsert_region_bs('FI', null, 1);
execute upsert_region_bs('FR', null, 1);
execute upsert_region_bs('GB', null, 1);
execute upsert_region_bs('HK', null, 1);
execute upsert_region_bs('HR', null, 1);
execute upsert_region_bs('HU', null, 1);
execute upsert_region_bs('ID', null, 1);
execute upsert_region_bs('IE', null, 1);
execute upsert_region_bs('IN', null, 1);
execute upsert_region_bs('IT', null, 1);
execute upsert_region_bs('KW', null, 1);
execute upsert_region_bs('LB', null, 1);
execute upsert_region_bs('LI', null, 1);
execute upsert_region_bs('LU', null, 1);
execute upsert_region_bs('MA', null, 1);
execute upsert_region_bs('MC', null, 1);
execute upsert_region_bs('MG', null, 1);
execute upsert_region_bs('MX', null, 1);
execute upsert_region_bs('NL', null, 1);
execute upsert_region_bs('NO', null, 1);
execute upsert_region_bs('NZ', null, 1);
execute upsert_region_bs('OM', null, 1);
execute upsert_region_bs('PE', null, 1);
execute upsert_region_bs('PL', null, 1);
execute upsert_region_bs('PR', null, 1);
execute upsert_region_bs('PT', null, 1);
execute upsert_region_bs('RE', null, 1);
execute upsert_region_bs('RO', null, 1);
execute upsert_region_bs('RS', null, 1);
execute upsert_region_bs('RU', null, 1);
execute upsert_region_bs('SE', null, 1);
execute upsert_region_bs('SK', null, 1);
execute upsert_region_bs('SM', null, 1);
execute upsert_region_bs('TR', null, 1);
execute upsert_region_bs('US', null, 1);
execute upsert_region_bs('ZA', null, 1);

prompt INSERTING into tlgnr_region_bs - INACTIVE

execute upsert_region_bs('AF', null, 0);
execute upsert_region_bs('AG', null, 0);
execute upsert_region_bs('AI', null, 0);
execute upsert_region_bs('AL', null, 0);
execute upsert_region_bs('AM', null, 0);
execute upsert_region_bs('AN', null, 0);
execute upsert_region_bs('AO', null, 0);
execute upsert_region_bs('AQ', null, 0);
execute upsert_region_bs('AR', null, 0);
execute upsert_region_bs('AS', null, 0);
execute upsert_region_bs('AW', null, 0);
execute upsert_region_bs('BA', null, 0);
execute upsert_region_bs('BB', null, 0);
execute upsert_region_bs('BD', null, 0);
execute upsert_region_bs('BF', null, 0);
execute upsert_region_bs('BH', null, 0);
execute upsert_region_bs('BI', null, 0);
execute upsert_region_bs('BJ', null, 0);
execute upsert_region_bs('BL', null, 0);
execute upsert_region_bs('BM', null, 0);
execute upsert_region_bs('BN', null, 0);
execute upsert_region_bs('BO', null, 0);
execute upsert_region_bs('BR', null, 0);
execute upsert_region_bs('BS', null, 0);
execute upsert_region_bs('BT', null, 0);
execute upsert_region_bs('BV', null, 0);
execute upsert_region_bs('BW', null, 0);
execute upsert_region_bs('BY', null, 0);
execute upsert_region_bs('BZ', null, 0);
execute upsert_region_bs('CC', null, 0);
execute upsert_region_bs('CD', null, 0);
execute upsert_region_bs('CF', null, 0);
execute upsert_region_bs('CG', null, 0);
execute upsert_region_bs('CI', null, 0);
execute upsert_region_bs('CK', null, 0);
execute upsert_region_bs('CM', null, 0);
execute upsert_region_bs('CN', null, 0);
execute upsert_region_bs('CO', null, 0);
execute upsert_region_bs('CR', null, 0);
execute upsert_region_bs('CU', null, 0);
execute upsert_region_bs('CV', null, 0);
execute upsert_region_bs('CX', null, 0);
execute upsert_region_bs('CY', null, 0);
execute upsert_region_bs('DJ', null, 0);
execute upsert_region_bs('DM', null, 0);
execute upsert_region_bs('DO', null, 0);
execute upsert_region_bs('DZ', null, 0);
execute upsert_region_bs('EC', null, 0);
execute upsert_region_bs('EE', null, 0);
execute upsert_region_bs('EG', null, 0);
execute upsert_region_bs('EH', null, 0);
execute upsert_region_bs('ER', null, 0);
execute upsert_region_bs('ET', null, 0);
execute upsert_region_bs('EU', null, 0);
execute upsert_region_bs('FJ', null, 0);
execute upsert_region_bs('FK', null, 0);
execute upsert_region_bs('FM', null, 0);
execute upsert_region_bs('FO', null, 0);
execute upsert_region_bs('GA', null, 0);
execute upsert_region_bs('GD', null, 0);
execute upsert_region_bs('GE', null, 0);
execute upsert_region_bs('GF', null, 0);
execute upsert_region_bs('GH', null, 0);
execute upsert_region_bs('GI', null, 0);
execute upsert_region_bs('GL', null, 0);
execute upsert_region_bs('GM', null, 0);
execute upsert_region_bs('GN', null, 0);
execute upsert_region_bs('GP', null, 0);
execute upsert_region_bs('GQ', null, 0);
execute upsert_region_bs('GR', null, 0);
execute upsert_region_bs('GS', null, 0);
execute upsert_region_bs('GT', null, 0);
execute upsert_region_bs('GU', null, 0);
execute upsert_region_bs('GW', null, 0);
execute upsert_region_bs('GY', null, 0);
execute upsert_region_bs('HM', null, 0);
execute upsert_region_bs('HN', null, 0);
execute upsert_region_bs('HT', null, 0);
execute upsert_region_bs('IL', null, 0);
execute upsert_region_bs('IO', null, 0);
execute upsert_region_bs('IQ', null, 0);
execute upsert_region_bs('IR', null, 0);
execute upsert_region_bs('IS', null, 0);
execute upsert_region_bs('JM', null, 0);
execute upsert_region_bs('JO', null, 0);
execute upsert_region_bs('JP', null, 0);
execute upsert_region_bs('KE', null, 0);
execute upsert_region_bs('KG', null, 0);
execute upsert_region_bs('KH', null, 0);
execute upsert_region_bs('KI', null, 0);
execute upsert_region_bs('KM', null, 0);
execute upsert_region_bs('KN', null, 0);
execute upsert_region_bs('KP', null, 0);
execute upsert_region_bs('KR', null, 0);
execute upsert_region_bs('KY', null, 0);
execute upsert_region_bs('KZ', null, 0);
execute upsert_region_bs('LA', null, 0);
execute upsert_region_bs('LC', null, 0);
execute upsert_region_bs('LK', null, 0);
execute upsert_region_bs('LR', null, 0);
execute upsert_region_bs('LS', null, 0);
execute upsert_region_bs('LT', null, 0);
execute upsert_region_bs('LV', null, 0);
execute upsert_region_bs('LY', null, 0);
execute upsert_region_bs('MD', null, 0);
execute upsert_region_bs('ME', null, 0);
execute upsert_region_bs('MH', null, 0);
execute upsert_region_bs('MK', null, 0);
execute upsert_region_bs('ML', null, 0);
execute upsert_region_bs('MM', null, 0);
execute upsert_region_bs('MN', null, 0);
execute upsert_region_bs('MO', null, 0);
execute upsert_region_bs('MP', null, 0);
execute upsert_region_bs('MQ', null, 0);
execute upsert_region_bs('MR', null, 0);
execute upsert_region_bs('MS', null, 0);
execute upsert_region_bs('MT', null, 0);
execute upsert_region_bs('MU', null, 0);
execute upsert_region_bs('MV', null, 0);
execute upsert_region_bs('MW', null, 0);
execute upsert_region_bs('MY', null, 0);
execute upsert_region_bs('MZ', null, 0);
execute upsert_region_bs('NA', null, 0);
execute upsert_region_bs('NC', null, 0);
execute upsert_region_bs('NE', null, 0);
execute upsert_region_bs('NF', null, 0);
execute upsert_region_bs('NG', null, 0);
execute upsert_region_bs('NI', null, 0);
execute upsert_region_bs('NP', null, 0);
execute upsert_region_bs('NR', null, 0);
execute upsert_region_bs('NT', null, 0);
execute upsert_region_bs('NU', null, 0);
execute upsert_region_bs('OR', null, 0);
execute upsert_region_bs('PA', null, 0);
execute upsert_region_bs('PF', null, 0);
execute upsert_region_bs('PG', null, 0);
execute upsert_region_bs('PH', null, 0);
execute upsert_region_bs('PK', null, 0);
execute upsert_region_bs('PM', null, 0);
execute upsert_region_bs('PN', null, 0);
execute upsert_region_bs('PS', null, 0);
execute upsert_region_bs('PW', null, 0);
execute upsert_region_bs('PY', null, 0);
execute upsert_region_bs('QA', null, 0);
execute upsert_region_bs('RW', null, 0);
execute upsert_region_bs('SA', null, 0);
execute upsert_region_bs('SB', null, 0);
execute upsert_region_bs('SC', null, 0);
execute upsert_region_bs('SD', null, 0);
execute upsert_region_bs('SG', null, 0);
execute upsert_region_bs('SH', null, 0);
execute upsert_region_bs('SI', null, 0);
execute upsert_region_bs('SJ', null, 0);
execute upsert_region_bs('SL', null, 0);
execute upsert_region_bs('SN', null, 0);
execute upsert_region_bs('SO', null, 0);
execute upsert_region_bs('SR', null, 0);
execute upsert_region_bs('ST', null, 0);
execute upsert_region_bs('SV', null, 0);
execute upsert_region_bs('SY', null, 0);
execute upsert_region_bs('SZ', null, 0);
execute upsert_region_bs('TC', null, 0);
execute upsert_region_bs('TD', null, 0);
execute upsert_region_bs('TF', null, 0);
execute upsert_region_bs('TG', null, 0);
execute upsert_region_bs('TH', null, 0);
execute upsert_region_bs('TJ', null, 0);
execute upsert_region_bs('TK', null, 0);
execute upsert_region_bs('TL', null, 0);
execute upsert_region_bs('TM', null, 0);
execute upsert_region_bs('TN', null, 0);
execute upsert_region_bs('TO', null, 0);
execute upsert_region_bs('TP', null, 0);
execute upsert_region_bs('TT', null, 0);
execute upsert_region_bs('TV', null, 0);
execute upsert_region_bs('TW', null, 0);
execute upsert_region_bs('TZ', null, 0);
execute upsert_region_bs('UA', null, 0);
execute upsert_region_bs('UG', null, 0);
execute upsert_region_bs('UM', null, 0);
execute upsert_region_bs('UN', null, 0);
execute upsert_region_bs('UY', null, 0);
execute upsert_region_bs('UZ', null, 0);
execute upsert_region_bs('VA', null, 0);
execute upsert_region_bs('VC', null, 0);
execute upsert_region_bs('VE', null, 0);
execute upsert_region_bs('VG', null, 0);
execute upsert_region_bs('VI', null, 0);
execute upsert_region_bs('VN', null, 0);
execute upsert_region_bs('VU', null, 0);
execute upsert_region_bs('WF', null, 0);
execute upsert_region_bs('WS', null, 0);
execute upsert_region_bs('XK', null, 0);
execute upsert_region_bs('XS', null, 0);
execute upsert_region_bs('YE', null, 0);
execute upsert_region_bs('YT', null, 0);
execute upsert_region_bs('YU', null, 0);
execute upsert_region_bs('ZM', null, 0);
execute upsert_region_bs('ZW', null, 0);

commit;

drop procedure upsert_region_bs;
drop function is_different_number;
drop function is_different_varchar2;

