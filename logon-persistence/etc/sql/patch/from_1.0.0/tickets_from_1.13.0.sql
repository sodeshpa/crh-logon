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
-- tlgnr_order_creation_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_order_creation_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_order_creation_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_order_creation_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.order_creation_type_cd)
    into v_exists_flag
    from tlgnr_order_creation_type_bs bs
    where bs.order_creation_type_cd = p_order_creation_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_order_creation_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_order_creation_type_bs set
/* order_creation_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          order_creation_type_cd = p_order_creation_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_order_creation_type_cd || ').');
      end if;
    else
      insert into tlgnr_order_creation_type_bs (
        order_creation_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_order_creation_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_order_creation_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_order_creation_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_order_creation_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_order_creation_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_order_creation_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.order_creation_type_cd) into v_exists_bs_flag
    from tlgnr_order_creation_type_bs bs
    where bs.order_creation_type_cd = p_order_creation_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.order_creation_type_cd) into v_exists_flag
      from tlgnr_order_creation_type_tx tx
      where tx.order_creation_type_cd = p_order_creation_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_order_creation_type_tx set
/* p_order_creation_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          order_creation_type_cd = p_order_creation_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_order_creation_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_order_creation_type_tx (
          order_creation_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_order_creation_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_order_creation_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_order_creation_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;


prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_BS
execute upsert_bs(7, 1);

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - EN
execute upsert_tx(7, 'en', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - DE
execute upsert_tx(7, 'de', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - FR
execute upsert_tx(7, 'fr', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - IT
execute upsert_tx(7, 'it', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - ES
execute upsert_tx(7, 'es', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - CS
execute upsert_tx(7, 'cs', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - SK
execute upsert_tx(7, 'sk', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - HU
execute upsert_tx(7, 'hu', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - RO
execute upsert_tx(7, 'ro', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - BG
execute upsert_tx(7, 'bg', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - RU
execute upsert_tx(7, 'ru', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - PL
execute upsert_tx(7, 'pl', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - AZ
execute upsert_tx(7, 'az', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - HR
execute upsert_tx(7, 'hr', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - SR
execute upsert_tx(7, 'sr', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - TR
execute upsert_tx(7, 'tr', 'Customer Portal');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - NL
execute upsert_tx(7, 'nl', 'Customer Portal');


prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_BS
execute upsert_bs(8, 1);

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - EN
execute upsert_tx(8, 'en', 'External System (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - DE
execute upsert_tx(8, 'de', 'External System (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - FR
execute upsert_tx(8, 'fr', 'Système Externe (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - IT
execute upsert_tx(8, 'it', 'Sistemo Esterno (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - ES
execute upsert_tx(8, 'es', 'Sistema Externo (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - CS
execute upsert_tx(8, 'cs', 'Externí systém (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - SK
execute upsert_tx(8, 'sk', 'Externý systém (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - HU
execute upsert_tx(8, 'hu', 'Külső rendszer (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - RO
execute upsert_tx(8, 'ro', 'Sistem extern (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - BG
execute upsert_tx(8, 'bg', 'Външна система (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - RU
execute upsert_tx(8, 'ru', 'внешняя система (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - PL
execute upsert_tx(8, 'pl', 'Zewnętrzny system (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - AZ
execute upsert_tx(8, 'az', 'External System (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - HR
execute upsert_tx(8, 'hr', 'Vanjski sustav (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - SR
execute upsert_tx(8, 'sr', 'Eksterni sistem (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - TR
execute upsert_tx(8, 'tr', 'External System (OC)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - NL
execute upsert_tx(8, 'nl', 'External System (OC)');


-- change external system to LDS because it's not anymore straightforwards

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - EN
execute upsert_tx(6, 'en', 'External System (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - DE
execute upsert_tx(6, 'de', 'External System (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - FR
execute upsert_tx(6, 'fr', 'Système Externe (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - IT
execute upsert_tx(6, 'it', 'Sistemo Esterno (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - ES
execute upsert_tx(6, 'es', 'Sistema Externo (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - CS
execute upsert_tx(6, 'cs', 'Externí systém (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - SK
execute upsert_tx(6, 'sk', 'Externý systém (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - HU
execute upsert_tx(6, 'hu', 'Külső rendszer (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - RO
execute upsert_tx(6, 'ro', 'Sistem extern (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - BG
execute upsert_tx(6, 'bg', 'Външна система (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - RU
execute upsert_tx(6, 'ru', 'внешняя система (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - PL
execute upsert_tx(6, 'pl', 'Zewnętrzny system (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - AZ
execute upsert_tx(6, 'az', 'External System (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - HR
execute upsert_tx(6, 'hr', 'Vanjski sustav (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - SR
execute upsert_tx(6, 'sr', 'Eksterni sistem (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - TR
execute upsert_tx(6, 'tr', 'External System (LDS)');

prompt INSERTING into TLGNR_ORDER_CREATION_TYPE_TX - NL
execute upsert_tx(6, 'nl', 'External System (LDS)');
commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;