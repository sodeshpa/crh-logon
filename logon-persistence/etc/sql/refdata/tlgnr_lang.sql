-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_lang.sql ----------'

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
-- tlgnr_lang_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_lang_bs (
    p_lang_cd     in varchar2,
    p_ctl_act_cd  in number

) is
    v_exists_flag number;        -- number of existing records of that key (0 or 1)

begin
  dbms_output.enable(100000);

  dbms_output.put_line('Insert or Update tlgnr_lang_bs (' || p_lang_cd || ', ' || 'ctl_act_cd=' || p_ctl_act_cd || ')');

  ----------------------------------------------------------------------------
  -- Lookup tlgnr_lang_bs
  ----------------------------------------------------------------------------
  select count(bs.lang_cd) into v_exists_flag
    from tlgnr_lang_bs bs
  where bs.lang_cd = p_lang_cd;

  if (v_exists_flag > 0) then
    if (p_ctl_act_cd != 1) then
      dbms_output.put_line ('NOT updating tlgnr_lang_bs with lang_cd=' || p_lang_cd || ' because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
    else
      update tlgnr_lang_bs set
        /* lang_cd: cannot update because this is the key */
        ctl_act_cd = p_ctl_act_cd,
        ctl_mod_uid = 1,
        ctl_mod_ts = systimestamp
      where
        lang_cd = p_lang_cd and
        ctl_act_cd != p_ctl_act_cd  /* not nullable */
      ;
      dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_lang_bs (CD=' || p_lang_cd || ').');
    end if;
  else
    insert into tlgnr_lang_bs (
      lang_cd,
      ctl_act_cd,
      ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
    ) values (
      p_lang_cd,
      p_ctl_act_cd,
      1, systimestamp, 1, systimestamp
    );
    dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_lang_bs (CD=' || p_lang_cd || ').');
  end if;

end upsert_lang_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_lang_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_lang_tx (
    p_lang_cd   in varchar2,
    p_lang1_cd  in varchar2,
    p_text      in varchar2

) is
    v_exists_flag    number;        -- number of existing records of that key (0 or 1)
    v_exists_bs_flag number;        -- number of existing BASE records

begin
  dbms_output.enable(100000);

  dbms_output.put_line('Insert or Update tlgnr_lang_tx with (' || p_lang_cd || ', ' || p_lang1_cd || ', ''' || p_text || ''')');

  ----------------------------------------------------------------------------
  -- Lookup tlgnr_lang_bs entry first
  ----------------------------------------------------------------------------
  select count(bs.lang_cd) into v_exists_bs_flag
    from tlgnr_lang_bs bs
  where bs.lang_cd = p_lang_cd;

  if (v_exists_bs_flag > 0) then
    ----------------------------------------------------------------------------
    -- Lookup tlgnr_lang_tx
    ----------------------------------------------------------------------------
    select count(tx.lang_cd) into v_exists_flag
      from tlgnr_lang_tx tx
    where tx.lang_cd = p_lang_cd and tx.lang1_cd = p_lang1_cd;

    if (v_exists_flag > 0) then
      update tlgnr_lang_tx set
        /* p_lang_cd and p_lang1_cd: cannot update because these are the keys */
        txt         = p_text,
        ctl_mod_uid = 1,
        ctl_mod_ts  = systimestamp
      where
        lang_cd = p_lang_cd     and
        lang1_cd = p_lang1_cd   and
        1 = is_different_varchar2(txt, p_text)
      ;
      dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_lang_tx (CD=' || p_lang_cd || ', LANG1=' || p_lang1_cd || ').');

    else
      insert into tlgnr_lang_tx (
        lang_cd,
        lang1_cd,
        txt,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_lang_cd,
        p_lang1_cd,
        p_text,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_lang_tx (CD=' || p_lang_cd || ', LANG1=' || p_lang1_cd || ').');
    end if;
  else
    dbms_output.put_line ('ERROR: Missing entry in tlgnr_lang_bs (CD=' || p_lang_cd || ').');
  end if;

end upsert_lang_tx;
/
show errors;

-- SAP's code Z1 must NOT be inserted into this table. The importer can map Z1 and DE to de. When sending back to SAP, de->Z1, always.

execute upsert_lang_bs('de', 1);
execute upsert_lang_bs('en', 1);
execute upsert_lang_bs('fr', 1);
execute upsert_lang_bs('it', 1);
execute upsert_lang_bs('es', 1);
execute upsert_lang_bs('cs', 1);
execute upsert_lang_bs('sk', 1);
execute upsert_lang_bs('hu', 1);
execute upsert_lang_bs('ro', 1);
execute upsert_lang_bs('bg', 1);
execute upsert_lang_bs('ru', 1);
execute upsert_lang_bs('pl', 1);
execute upsert_lang_bs('az', 1);
execute upsert_lang_bs('hr', 1);
execute upsert_lang_bs('sr', 1);
execute upsert_lang_bs('tr', 1);
execute upsert_lang_bs('nl', 1);

-- EN
execute upsert_lang_tx('en', 'en', 'English');
execute upsert_lang_tx('de', 'en', 'German');
execute upsert_lang_tx('fr', 'en', 'French');
execute upsert_lang_tx('it', 'en', 'Italian');
execute upsert_lang_tx('es', 'en', 'Spanish');
execute upsert_lang_tx('cs', 'en', 'Czech');
execute upsert_lang_tx('sk', 'en', 'Slovak');
execute upsert_lang_tx('hu', 'en', 'Hungarian');
execute upsert_lang_tx('ro', 'en', 'Romanian');
execute upsert_lang_tx('bg', 'en', 'Bulgarian');
execute upsert_lang_tx('ru', 'en', 'Russian');
execute upsert_lang_tx('pl', 'en', 'Polish');
execute upsert_lang_tx('az', 'en', 'Azerbaijani');
execute upsert_lang_tx('hr', 'en', 'Croatian');
execute upsert_lang_tx('sr', 'en', 'Serbian');
execute upsert_lang_tx('tr', 'en', 'Turkish');
execute upsert_lang_tx('nl', 'en', 'Dutch');

-- DE
execute upsert_lang_tx('en', 'de', 'Englisch');
execute upsert_lang_tx('de', 'de', 'Deutsch');
execute upsert_lang_tx('fr', 'de', 'Französisch');
execute upsert_lang_tx('it', 'de', 'Italienisch');
execute upsert_lang_tx('es', 'de', 'Spanisch');
execute upsert_lang_tx('cs', 'de', 'Tschechisch');
execute upsert_lang_tx('sk', 'de', 'Slovakisch');
execute upsert_lang_tx('hu', 'de', 'Ungarisch');
execute upsert_lang_tx('ro', 'de', 'Rumänisch');
execute upsert_lang_tx('bg', 'de', 'Bulgarisch');
execute upsert_lang_tx('ru', 'de', 'Russisch');
execute upsert_lang_tx('pl', 'de', 'Polnisch');
execute upsert_lang_tx('az', 'de', 'Aserbaidschanisch');
execute upsert_lang_tx('hr', 'de', 'Kroatisch');
execute upsert_lang_tx('sr', 'de', 'Serbisch');
execute upsert_lang_tx('tr', 'de', 'Turkish');
execute upsert_lang_tx('nl', 'de', 'Dutch');

-- FR
execute upsert_lang_tx('en', 'fr', 'Anglais');
execute upsert_lang_tx('de', 'fr', 'Allemand');
execute upsert_lang_tx('fr', 'fr', 'Français');
execute upsert_lang_tx('it', 'fr', 'Italien');
execute upsert_lang_tx('es', 'fr', 'Espagnol');
execute upsert_lang_tx('cs', 'fr', 'Tchèque');
execute upsert_lang_tx('sk', 'fr', 'Slovaque');
execute upsert_lang_tx('hu', 'fr', 'Hongrois');
execute upsert_lang_tx('ro', 'fr', 'Roumain');
execute upsert_lang_tx('bg', 'fr', 'Bulgare');
execute upsert_lang_tx('ru', 'fr', 'Russe');
execute upsert_lang_tx('pl', 'fr', 'Polonais');
execute upsert_lang_tx('az', 'fr', 'Azerbaïdjanais');
execute upsert_lang_tx('hr', 'fr', 'Croate');
execute upsert_lang_tx('sr', 'fr', 'Serbe');
execute upsert_lang_tx('tr', 'fr', 'Turkish');
execute upsert_lang_tx('nl', 'fr', 'Dutch');

-- IT
execute upsert_lang_tx('en', 'it', 'Inglese');
execute upsert_lang_tx('de', 'it', 'Tedesco');
execute upsert_lang_tx('fr', 'it', 'Francese');
execute upsert_lang_tx('it', 'it', 'Italiano');
execute upsert_lang_tx('es', 'it', 'Spagnolo');
execute upsert_lang_tx('cs', 'it', 'Ceco');
execute upsert_lang_tx('sk', 'it', 'Slovacco');
execute upsert_lang_tx('hu', 'it', 'Ungherese');
execute upsert_lang_tx('ro', 'it', 'Rumeno');
execute upsert_lang_tx('bg', 'it', 'Bulgaro');
execute upsert_lang_tx('ru', 'it', 'Russo');
execute upsert_lang_tx('pl', 'it', 'Polacco');
execute upsert_lang_tx('az', 'it', 'Azero');
execute upsert_lang_tx('hr', 'it', 'Croato');
execute upsert_lang_tx('sr', 'it', 'Serbo');
execute upsert_lang_tx('tr', 'it', 'Turkish');
execute upsert_lang_tx('nl', 'it', 'Dutch');

-- ES
execute upsert_lang_tx('en', 'es', 'Inglés');
execute upsert_lang_tx('de', 'es', 'Alemán');
execute upsert_lang_tx('fr', 'es', 'Francés');
execute upsert_lang_tx('it', 'es', 'Italiano');
execute upsert_lang_tx('es', 'es', 'Español');
execute upsert_lang_tx('cs', 'es', 'Checo');
execute upsert_lang_tx('sk', 'es', 'Eslovaco');
execute upsert_lang_tx('hu', 'es', 'Húngaro');
execute upsert_lang_tx('ro', 'es', 'Rumano');
execute upsert_lang_tx('bg', 'es', 'Búlgaro');
execute upsert_lang_tx('ru', 'es', 'Ruso');
execute upsert_lang_tx('pl', 'es', 'Polaco');
execute upsert_lang_tx('az', 'es', 'Azerbaiyano');
execute upsert_lang_tx('hr', 'es', 'Croata');
execute upsert_lang_tx('sr', 'es', 'Serbio');
execute upsert_lang_tx('tr', 'es', 'Turkish');
execute upsert_lang_tx('nl', 'es', 'Dutch');

-- CS
execute upsert_lang_tx('en', 'cs', 'Anglicky');
execute upsert_lang_tx('de', 'cs', 'Nemecky');
execute upsert_lang_tx('fr', 'cs', 'Francouzsky');
execute upsert_lang_tx('it', 'cs', 'Italsky');
execute upsert_lang_tx('es', 'cs', 'Španělsky');
execute upsert_lang_tx('cs', 'cs', 'Cesky');
execute upsert_lang_tx('sk', 'cs', 'Slovensky');
execute upsert_lang_tx('hu', 'cs', 'Madarsky');
execute upsert_lang_tx('ro', 'cs', 'Rumunsky');
execute upsert_lang_tx('bg', 'cs', 'Bulharsky');
execute upsert_lang_tx('ru', 'cs', 'Russian');
execute upsert_lang_tx('pl', 'cs', 'Polish');
execute upsert_lang_tx('az', 'cs', 'Azerbaijani');
execute upsert_lang_tx('hr', 'cs', 'Croatian');
execute upsert_lang_tx('sr', 'cs', 'Serbian');
execute upsert_lang_tx('tr', 'cs', 'Turkish');
execute upsert_lang_tx('nl', 'cs', 'Dutch');

-- SK
execute upsert_lang_tx('en', 'sk', 'Anglictina');
execute upsert_lang_tx('de', 'sk', 'Nemcina');
execute upsert_lang_tx('fr', 'sk', 'Francúzština');
execute upsert_lang_tx('it', 'sk', 'Taliancina');
execute upsert_lang_tx('es', 'sk', 'Španielčina');
execute upsert_lang_tx('cs', 'sk', 'Čeština');
execute upsert_lang_tx('sk', 'sk', 'Slovencina');
execute upsert_lang_tx('hu', 'sk', 'Madarcina');
execute upsert_lang_tx('ro', 'sk', 'Rumuncina');
execute upsert_lang_tx('bg', 'sk', 'Bulharcina');
execute upsert_lang_tx('ru', 'sk', 'Rusky');
execute upsert_lang_tx('pl', 'sk', 'Poľsky');
execute upsert_lang_tx('az', 'sk', 'Azerbajdžansky');
execute upsert_lang_tx('hr', 'sk', 'Chorvátsky');
execute upsert_lang_tx('sr', 'sk', 'Srbsky');
execute upsert_lang_tx('tr', 'sk', 'Turkish');
execute upsert_lang_tx('nl', 'sk', 'Dutch');

-- HU
execute upsert_lang_tx('en', 'hu', 'Angol');
execute upsert_lang_tx('de', 'hu', 'Német');
execute upsert_lang_tx('fr', 'hu', 'Francia');
execute upsert_lang_tx('it', 'hu', 'Olasz');
execute upsert_lang_tx('es', 'hu', 'Spanyol');
execute upsert_lang_tx('cs', 'hu', 'Cseh');
execute upsert_lang_tx('sk', 'hu', 'Szlovák');
execute upsert_lang_tx('hu', 'hu', 'Magyar');
execute upsert_lang_tx('ro', 'hu', 'Román');
execute upsert_lang_tx('bg', 'hu', 'Bolgár');
execute upsert_lang_tx('ru', 'hu', 'Orosz');
execute upsert_lang_tx('pl', 'hu', 'Lengyel');
execute upsert_lang_tx('az', 'hu', 'Azerbajdzsán');
execute upsert_lang_tx('hr', 'hu', 'Horváth');
execute upsert_lang_tx('sr', 'hu', 'Szerb');
execute upsert_lang_tx('tr', 'hu', 'Turkish');
execute upsert_lang_tx('nl', 'hu', 'Dutch');

-- RO
execute upsert_lang_tx('en', 'ro', 'Engleza');
execute upsert_lang_tx('de', 'ro', 'Germana');
execute upsert_lang_tx('fr', 'ro', 'Franceza');
execute upsert_lang_tx('it', 'ro', 'Italiana');
execute upsert_lang_tx('es', 'ro', 'Spaniola');
execute upsert_lang_tx('cs', 'ro', 'Ceha');
execute upsert_lang_tx('sk', 'ro', 'Slovaca');
execute upsert_lang_tx('hu', 'ro', 'Maghiara');
execute upsert_lang_tx('ro', 'ro', 'Romana');
execute upsert_lang_tx('bg', 'ro', 'Bulgara');
execute upsert_lang_tx('ru', 'ro', 'Rus');
execute upsert_lang_tx('pl', 'ro', 'Polonez');
execute upsert_lang_tx('az', 'ro', 'Azer');
execute upsert_lang_tx('hr', 'ro', 'Croat');
execute upsert_lang_tx('sr', 'ro', 'Sarb');
execute upsert_lang_tx('tr', 'ro', 'Turkish');
execute upsert_lang_tx('nl', 'ro', 'Dutch');

-- BG
execute upsert_lang_tx('en', 'bg', 'Английски');
execute upsert_lang_tx('de', 'bg', 'Немски');
execute upsert_lang_tx('fr', 'bg', 'Френски');
execute upsert_lang_tx('it', 'bg', 'Италиански');
execute upsert_lang_tx('es', 'bg', 'Испански');
execute upsert_lang_tx('cs', 'bg', 'Чешки');
execute upsert_lang_tx('sk', 'bg', 'Словашки');
execute upsert_lang_tx('hu', 'bg', 'Унгарски');
execute upsert_lang_tx('ro', 'bg', 'Румънски');
execute upsert_lang_tx('bg', 'bg', 'Български');
execute upsert_lang_tx('ru', 'bg', 'Русия');
execute upsert_lang_tx('pl', 'bg', 'Полша');
execute upsert_lang_tx('az', 'bg', 'Азербайджан');
execute upsert_lang_tx('hr', 'bg', 'Хърватска');
execute upsert_lang_tx('sr', 'bg', 'Сърбия');
execute upsert_lang_tx('tr', 'bg', 'Turkish');
execute upsert_lang_tx('nl', 'bg', 'Dutch');

-- RU (ENGLISH COPIED)
execute upsert_lang_tx('en', 'ru', 'английский');
execute upsert_lang_tx('de', 'ru', 'немецкий');
execute upsert_lang_tx('fr', 'ru', 'французский');
execute upsert_lang_tx('it', 'ru', 'итальянский');
execute upsert_lang_tx('es', 'ru', 'испанский');
execute upsert_lang_tx('cs', 'ru', 'чешский');
execute upsert_lang_tx('sk', 'ru', 'словацкий');
execute upsert_lang_tx('hu', 'ru', 'венгерский');
execute upsert_lang_tx('ro', 'ru', 'румынский');
execute upsert_lang_tx('bg', 'ru', 'болгарский');
execute upsert_lang_tx('ru', 'ru', 'Русский');
execute upsert_lang_tx('pl', 'ru', 'Польский');
execute upsert_lang_tx('az', 'ru', 'Азербайджанский');
execute upsert_lang_tx('hr', 'ru', 'Ховатский');
execute upsert_lang_tx('sr', 'ru', 'Сербский');
execute upsert_lang_tx('tr', 'ru', 'Turkish');
execute upsert_lang_tx('nl', 'ru', 'Dutch');

-- PL
execute upsert_lang_tx('en', 'pl', 'Angielski');
execute upsert_lang_tx('de', 'pl', 'Niemiecki');
execute upsert_lang_tx('fr', 'pl', 'Francuski');
execute upsert_lang_tx('it', 'pl', 'Włoski');
execute upsert_lang_tx('es', 'pl', 'Hiszpański');
execute upsert_lang_tx('cs', 'pl', 'Czeski');
execute upsert_lang_tx('sk', 'pl', 'Słowacki');
execute upsert_lang_tx('hu', 'pl', 'Węgierski');
execute upsert_lang_tx('ro', 'pl', 'Rumuński');
execute upsert_lang_tx('bg', 'pl', 'Bułgarski');
execute upsert_lang_tx('ru', 'pl', 'Rosyjski');
execute upsert_lang_tx('pl', 'pl', 'Polski');
execute upsert_lang_tx('az', 'pl', 'Azerbejdżański');
execute upsert_lang_tx('hr', 'pl', 'Chorwacki');
execute upsert_lang_tx('sr', 'pl', 'Serbski');
execute upsert_lang_tx('tr', 'pl', 'Turkish');
execute upsert_lang_tx('nl', 'pl', 'Dutch');

-- AZ (ENGLISH COPIED)
execute upsert_lang_tx('en', 'az', 'English');
execute upsert_lang_tx('de', 'az', 'German');
execute upsert_lang_tx('fr', 'az', 'French');
execute upsert_lang_tx('it', 'az', 'Italian');
execute upsert_lang_tx('es', 'az', 'Spanish');
execute upsert_lang_tx('cs', 'az', 'Czech');
execute upsert_lang_tx('sk', 'az', 'Slovak');
execute upsert_lang_tx('hu', 'az', 'Hungarian');
execute upsert_lang_tx('ro', 'az', 'Romanian');
execute upsert_lang_tx('bg', 'az', 'Bulgarian');
execute upsert_lang_tx('ru', 'az', 'Russian');
execute upsert_lang_tx('pl', 'az', 'Polish');
execute upsert_lang_tx('az', 'az', 'Azerbaijani');
execute upsert_lang_tx('hr', 'az', 'Croatian');
execute upsert_lang_tx('sr', 'az', 'Serbian');
execute upsert_lang_tx('tr', 'az', 'Turkish');
execute upsert_lang_tx('nl', 'az', 'Dutch');

-- HR
execute upsert_lang_tx('en', 'hr', 'Engleski');
execute upsert_lang_tx('de', 'hr', 'Njemački');
execute upsert_lang_tx('fr', 'hr', 'Francusk');
execute upsert_lang_tx('it', 'hr', 'Talijanski');
execute upsert_lang_tx('es', 'hr', 'Španjolski');
execute upsert_lang_tx('cs', 'hr', 'Češki');
execute upsert_lang_tx('sk', 'hr', 'Slovački');
execute upsert_lang_tx('hu', 'hr', 'Mađarski');
execute upsert_lang_tx('ro', 'hr', 'Rumunjski');
execute upsert_lang_tx('bg', 'hr', 'Bugarski');
execute upsert_lang_tx('ru', 'hr', 'Ruski');
execute upsert_lang_tx('pl', 'hr', 'Poljski');
execute upsert_lang_tx('az', 'hr', 'Azerbadžanski');
execute upsert_lang_tx('hr', 'hr', 'Hrvatski');
execute upsert_lang_tx('sr', 'hr', 'Srpski');
execute upsert_lang_tx('tr', 'hr', 'Turkish');
execute upsert_lang_tx('nl', 'hr', 'Dutch');

-- SR
execute upsert_lang_tx('en', 'sr', 'Engleski');
execute upsert_lang_tx('de', 'sr', 'Nemački');
execute upsert_lang_tx('fr', 'sr', 'Francuski');
execute upsert_lang_tx('it', 'sr', 'Italijanski');
execute upsert_lang_tx('es', 'sr', 'Španski');
execute upsert_lang_tx('cs', 'sr', 'Češki');
execute upsert_lang_tx('sk', 'sr', 'Slovački');
execute upsert_lang_tx('hu', 'sr', 'Mađarski');
execute upsert_lang_tx('ro', 'sr', 'Rumunski');
execute upsert_lang_tx('bg', 'sr', 'Bugarski');
execute upsert_lang_tx('ru', 'sr', 'Ruski');
execute upsert_lang_tx('pl', 'sr', 'Poljski');
execute upsert_lang_tx('az', 'sr', 'Azerbejdžanski');
execute upsert_lang_tx('hr', 'sr', 'Hrvatski');
execute upsert_lang_tx('sr', 'sr', 'Srpski');
execute upsert_lang_tx('tr', 'sr', 'Turkish');
execute upsert_lang_tx('nl', 'sr', 'Dutch');

-- TR (ENGLISH COPIED)
execute upsert_lang_tx('en', 'tr', 'English');
execute upsert_lang_tx('de', 'tr', 'German');
execute upsert_lang_tx('fr', 'tr', 'French');
execute upsert_lang_tx('it', 'tr', 'Italian');
execute upsert_lang_tx('es', 'tr', 'Spanish');
execute upsert_lang_tx('cs', 'tr', 'Czech');
execute upsert_lang_tx('sk', 'tr', 'Slovak');
execute upsert_lang_tx('hu', 'tr', 'Hungarian');
execute upsert_lang_tx('ro', 'tr', 'Romanian');
execute upsert_lang_tx('bg', 'tr', 'Bulgarian');
execute upsert_lang_tx('ru', 'tr', 'Russian');
execute upsert_lang_tx('pl', 'tr', 'Polish');
execute upsert_lang_tx('az', 'tr', 'Azerbaijani');
execute upsert_lang_tx('hr', 'tr', 'Croatian');
execute upsert_lang_tx('sr', 'tr', 'Serbian');
execute upsert_lang_tx('tr', 'tr', 'Turkish');
execute upsert_lang_tx('nl', 'tr', 'Dutch');

-- NL (ENGLISH COPIED)
execute upsert_lang_tx('en', 'nl', 'English');
execute upsert_lang_tx('de', 'nl', 'German');
execute upsert_lang_tx('fr', 'nl', 'French');
execute upsert_lang_tx('it', 'nl', 'Italian');
execute upsert_lang_tx('es', 'nl', 'Spanish');
execute upsert_lang_tx('cs', 'nl', 'Czech');
execute upsert_lang_tx('sk', 'nl', 'Slovak');
execute upsert_lang_tx('hu', 'nl', 'Hungarian');
execute upsert_lang_tx('ro', 'nl', 'Romanian');
execute upsert_lang_tx('bg', 'nl', 'Bulgarian');
execute upsert_lang_tx('ru', 'nl', 'Russian');
execute upsert_lang_tx('pl', 'nl', 'Polish');
execute upsert_lang_tx('az', 'nl', 'Azerbaijani');
execute upsert_lang_tx('hr', 'nl', 'Croatian');
execute upsert_lang_tx('sr', 'nl', 'Serbian');
execute upsert_lang_tx('tr', 'nl', 'Turkish');
execute upsert_lang_tx('nl', 'nl', 'Dutch');

commit;

drop procedure upsert_lang_bs;
drop procedure upsert_lang_tx;
drop function is_different_number;
drop function is_different_varchar2;
