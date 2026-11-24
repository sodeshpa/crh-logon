-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_itrack_status_bs.sql ----------'

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
-- tlgnr_itrack_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_itrack_status_bs (
    p_itrack_status_cd          in number,
    p_itrack_status_name          in varchar2,
    p_ctl_act_cd              in number

) is
    v_exists_flag                   number;        -- number of existing records of that key (0 or 1)
    v_ctl_act_cd                    number := p_ctl_act_cd;

begin
  dbms_output.enable(100000);

  dbms_output.put_line('Insert or Update tlgnr_itrack_status_bs (' || p_itrack_status_cd || ', ' || 'ctl_act_cd=' || p_ctl_act_cd || ')');
  
  -- Be tolerant against bad initial data input: treat null as "LOGON standard", i.e. our meaningful defaults.
  if (v_ctl_act_cd is null) then
    v_ctl_act_cd := 1 /* active */;
  end if;

  ----------------------------------------------------------------------------
  -- Lookup tlgnr_itrack_status_bs
  -------------------------------------------------------------------------------
  select count(bs.itrack_status_cd) into v_exists_flag
    from tlgnr_itrack_status_bs bs
  where bs.itrack_status_cd = p_itrack_status_cd;

  if (v_exists_flag > 0) then
  
    update tlgnr_itrack_status_bs set
      /* itrack_status_cd: cannot update because this is the key */
      itrack_status_name 					 = p_itrack_status_name,
      ctl_act_cd                     = v_ctl_act_cd,
      ctl_mod_uid                    = 1,
      ctl_mod_ts                     = systimestamp
    where
      itrack_status_cd = p_itrack_status_cd and
      1 = is_different_number   (ctl_act_cd, v_ctl_act_cd)
    ;
    dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_itrack_status_bs (CD=' || p_itrack_status_cd || ').');
  
  else
    insert into tlgnr_itrack_status_bs (
      itrack_status_cd,
      itrack_status_name,
      ctl_act_cd,
      ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
    ) values (
      p_itrack_status_cd,
      p_itrack_status_name,
      v_ctl_act_cd,
      1, systimestamp, 1, systimestamp
    );
    dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_itrack_status_bs (CD=' || p_itrack_status_cd || ').');
  end if;

end upsert_itrack_status_bs;
/
show errors;

prompt INSERTING into tlgnr_itrack_status_bs - ACTIVE

execute upsert_itrack_status_bs(1, 'OK', 1);
execute upsert_itrack_status_bs(2, 'Delivered Wrong Location', 1);
execute upsert_itrack_status_bs(3, 'Delivered Location Approved', 1);
execute upsert_itrack_status_bs(4, 'Shipping Unload Information Missing', 1);
execute upsert_itrack_status_bs(5, 'Delivered Unload Information Missing', 1)
execute upsert_itrack_status_bs(6, 'Delivered Parameter Missing', 1)

commit;

drop procedure upsert_itrack_status_bs;
drop function is_different_number;
drop function is_different_varchar2;

