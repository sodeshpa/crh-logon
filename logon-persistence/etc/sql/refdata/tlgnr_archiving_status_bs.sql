-- $Revision$
-- $Date$
--
-- LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_archiving_status_bs.sql ----------'

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
-- tlgnr_archiving_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_archiving_status_cd     in number,
  p_description                in varchar2,
  p_display_flag	in number)
is
  v_table_name    varchar2(48) := 'tlgnr_archiving_status_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_archiving_status_cd || ', description=' || p_description || ', display_flag=' || p_display_flag || ')');

    select count(bs.archiving_status_cd)
    into v_exists_flag
    from tlgnr_archiving_status_bs bs
    where bs.archiving_status_cd = p_archiving_status_cd;

    if (v_exists_flag > 0) then
        update tlgnr_archiving_status_bs set
/* archiving_status_cd: cannot update because this is the key */
          description                     = p_description,
          display_flag					 = p_display_flag,	
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          archiving_status_cd = p_archiving_status_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_archiving_status_cd || ').');
   
    else
      insert into tlgnr_archiving_status_bs (
        archiving_status_cd,
        description,
        display_flag,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_archiving_status_cd,
        p_description,
        p_display_flag,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_archiving_status_cd || ').');
    end if;

  end upsert_bs;
/
show errors;





prompt INSERTING into TLGNR_ARCHIVING_STATUS_BS
execute upsert_bs(1,'NEW', 1);
execute upsert_bs(2,'ARCHIVING FAILED', 1);
execute upsert_bs(3,'DELETABLE', 0);
execute upsert_bs(4,'DELETION FAILED', 0);




commit;

drop procedure upsert_bs;
drop function is_different_number;
drop function is_different_varchar2;
