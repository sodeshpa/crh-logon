-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_rfidobject_type.sql ----------'

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
-- tlgnr_rfidobject_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_rfidobject_type_cd   in number,
  p_ctl_act_cd           in number)
is
  v_table_name    varchar2(48) := 'tlgnr_rfidobject_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_rfidobject_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.rfidobject_type_cd)
    into v_exists_flag
    from tlgnr_rfidobject_type_bs bs
    where bs.rfidobject_type_cd = p_rfidobject_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_rfidobject_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_rfidobject_type_bs set
/* rfidobject_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          rfidobject_type_cd = p_rfidobject_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_rfidobject_type_cd || ').');
      end if;
    else
      insert into tlgnr_rfidobject_type_bs (
        rfidobject_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_rfidobject_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_rfidobject_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_rfidobject_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_rfidobject_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_rfidobject_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_rfidobject_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.rfidobject_type_cd) into v_exists_bs_flag
    from tlgnr_rfidobject_type_bs bs
    where bs.rfidobject_type_cd = p_rfidobject_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.rfidobject_type_cd) into v_exists_flag
      from tlgnr_rfidobject_type_tx tx
      where tx.rfidobject_type_cd = p_rfidobject_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_rfidobject_type_tx set
/* p_rfidobject_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          rfidobject_type_cd = p_rfidobject_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_rfidobject_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_rfidobject_type_tx (
          rfidobject_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_rfidobject_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_rfidobject_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_rfidobject_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
-- ADN42445: decision with CDU 22.6.2009: no distinction between parallel terminal and 'normal' loadingpoint silo
execute upsert_bs(3, 0);
execute upsert_bs(4, 1);
execute upsert_bs(5, 1);
execute upsert_bs(6, 1);
execute upsert_bs(7, 1);
execute upsert_bs(8, 1);
-- Mail "RFID Objekt Typen" 24.6. Nina -> Nufatron: not needed
execute upsert_bs(9, 0);

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - EN
execute upsert_tx(1, 'en', 'PDA User');
execute upsert_tx(2, 'en', 'Loadingpoint Silo');
execute upsert_tx(3, 'en', 'LDS Terminal (not used)');
execute upsert_tx(4, 'en', 'Truck');
execute upsert_tx(5, 'en', 'Trailer');
execute upsert_tx(6, 'en', 'Container');
execute upsert_tx(7, 'en', 'Wagon');
execute upsert_tx(8, 'en', 'ShipTo Silo');
execute upsert_tx(9, 'en', 'Forklift (not used)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - DE
execute upsert_tx(1, 'de', 'PDA Benutzer');
execute upsert_tx(2, 'de', 'Silo an Ladestrasse');
execute upsert_tx(3, 'de', 'LDS Terminal (unbenutzt)');
execute upsert_tx(4, 'de', 'LKW');
execute upsert_tx(5, 'de', 'Anhänger/Auflieger');
execute upsert_tx(6, 'de', 'Container');
execute upsert_tx(7, 'de', 'Bahnwagen');
execute upsert_tx(8, 'de', 'Silo am ShipTo');
execute upsert_tx(9, 'de', 'Stapler (unbenutzt)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - FR
execute upsert_tx(1, 'fr', 'Utilisateur PDA');
execute upsert_tx(2, 'fr', 'Lieu de chargement silo');
execute upsert_tx(3, 'fr', 'LDS Terminal (not used)');
execute upsert_tx(4, 'fr', 'Camion');
execute upsert_tx(5, 'fr', 'Semi-remorque');
execute upsert_tx(6, 'fr', 'Container');
execute upsert_tx(7, 'fr', 'Wagon');
execute upsert_tx(8, 'fr', 'Destination Silo');
execute upsert_tx(9, 'fr', 'Chariot élévateur');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - IT
execute upsert_tx(1, 'it', 'Utilizzatore PDA');
execute upsert_tx(2, 'it', 'Luogo di carico silo');
execute upsert_tx(3, 'it', 'LDS Terminal (not used)');
execute upsert_tx(4, 'it', 'Camion');
execute upsert_tx(5, 'it', 'Semi-rimorchio');
execute upsert_tx(6, 'it', 'Container');
execute upsert_tx(7, 'it', 'Vagone');
execute upsert_tx(8, 'it', 'Destinazione Silo');
execute upsert_tx(9, 'it', 'Carello elevatore');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - ES
execute upsert_tx(1, 'es', 'Usuario PDA');
execute upsert_tx(2, 'es', 'Punto de carga Silo');
execute upsert_tx(3, 'es', 'Terminal LDS(sin uso)');
execute upsert_tx(4, 'es', 'Camión');
execute upsert_tx(5, 'es', 'Tráiler');
execute upsert_tx(6, 'es', 'Contenedor');
execute upsert_tx(7, 'es', 'Vagón');
execute upsert_tx(8, 'es', 'Silo Destinatario');
execute upsert_tx(9, 'es', 'Carretilla elevadora (sin uso)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - CS
execute upsert_tx(1, 'cs', 'PDA uživatel');
execute upsert_tx(2, 'cs', 'Vykládka - silo');
execute upsert_tx(3, 'cs', 'LDS Terminal (not used)');
execute upsert_tx(4, 'cs', 'Vozidlo');
execute upsert_tx(5, 'cs', 'Návěs');
execute upsert_tx(6, 'cs', 'Kontejner');
execute upsert_tx(7, 'cs', 'Vagón');
execute upsert_tx(8, 'cs', 'Silo zákazníka');
execute upsert_tx(9, 'cs', 'Vysokozdvižný vozík (nepoužit)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - SK
execute upsert_tx(1, 'sk', 'PDA užívateľ');
execute upsert_tx(2, 'sk', 'Miesto nakládky silo');
execute upsert_tx(3, 'sk', 'LDS Terminal (not used)');
execute upsert_tx(4, 'sk', 'Ťahač');
execute upsert_tx(5, 'sk', 'Príves');
execute upsert_tx(6, 'sk', 'Kontajner');
execute upsert_tx(7, 'sk', 'Vagón');
execute upsert_tx(8, 'sk', 'Na ceste k silu');
execute upsert_tx(9, 'sk', 'Vysokozdvižný (nepoužíva sa)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - HU
execute upsert_tx(1, 'hu', 'PDA felhasználó');
execute upsert_tx(2, 'hu', 'Siló felrakó hely');
execute upsert_tx(3, 'hu', 'LDS Terminal (not used)');
execute upsert_tx(4, 'hu', 'Kamion');
execute upsert_tx(5, 'hu', 'Utánfutó');
execute upsert_tx(6, 'hu', 'Tartály');
execute upsert_tx(7, 'hu', 'Vagon');
execute upsert_tx(8, 'hu', 'Siló felrakó');
execute upsert_tx(9, 'hu', 'Targonca (nem használt)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - RO
execute upsert_tx(1, 'ro', 'Utilizator PDA');
execute upsert_tx(2, 'ro', 'Siloz Punct de Incarcare');
execute upsert_tx(3, 'ro', 'LDS Terminal (not used)');
execute upsert_tx(4, 'ro', 'Camion');
execute upsert_tx(5, 'ro', 'Remorca');
execute upsert_tx(6, 'ro', 'Container');
execute upsert_tx(7, 'ro', 'Vagon');
execute upsert_tx(8, 'ro', 'Siloz Destinatar');
execute upsert_tx(9, 'ro', 'Motostivuitor (not used)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - BG
execute upsert_tx(1, 'bg', 'PDA потребител');
execute upsert_tx(2, 'bg', 'Място на товарене Силоз');
execute upsert_tx(3, 'bg', 'LDS Terminal (not used)');
execute upsert_tx(4, 'bg', 'Камион');
execute upsert_tx(5, 'bg', 'Ремарке');
execute upsert_tx(6, 'bg', 'Контейнер');
execute upsert_tx(7, 'bg', 'Вагон');
execute upsert_tx(8, 'bg', 'Обект Силоз');
execute upsert_tx(9, 'bg', 'Мотокар (не се използва)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'Пользователь КПК');
execute upsert_tx(2, 'ru', 'погрузочная точка силоса');
execute upsert_tx(3, 'ru', 'терминал LDS (не используется)');
execute upsert_tx(4, 'ru', 'автомобиль');
execute upsert_tx(5, 'ru', 'прицеп');
execute upsert_tx(6, 'ru', 'контейнер');
execute upsert_tx(7, 'ru', 'вагон');
execute upsert_tx(8, 'ru', 'силос грузополучателя');
execute upsert_tx(9, 'ru', 'Погрузчик (не используется)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - PL
execute upsert_tx(1, 'pl', 'Użytkownik PDA');
execute upsert_tx(2, 'pl', 'Silos - punkt załadunku');
execute upsert_tx(3, 'pl', 'LDS Terminal (nie używane)');
execute upsert_tx(4, 'pl', 'Ciężarówka');
execute upsert_tx(5, 'pl', 'Naczepa');
execute upsert_tx(6, 'pl', 'Pojemnik');
execute upsert_tx(7, 'pl', 'Wagon');
execute upsert_tx(8, 'pl', 'Silos docelowy');
execute upsert_tx(9, 'pl', 'Wózek widłowy (nie używane)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'PDA İstifadəçi');
execute upsert_tx(2, 'az', 'Yükləmə məntəqəsi bunkeri');
execute upsert_tx(3, 'az', 'LDS Terminalı (istifadəsiz)');
execute upsert_tx(4, 'az', 'Yük maşını');
execute upsert_tx(5, 'az', 'Treyler');
execute upsert_tx(6, 'az', 'Konteyner');
execute upsert_tx(7, 'az', 'Vaqon');
execute upsert_tx(8, 'az', 'Hara göndərilir Bunker');
execute upsert_tx(9, 'az', 'Forklift (istifadəsiz)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - HR
execute upsert_tx(1, 'hr', 'Upute za PDA');
execute upsert_tx(2, 'hr', 'Utovar silos');
execute upsert_tx(3, 'hr', 'LDS Terminal (ne koristi)');
execute upsert_tx(4, 'hr', 'Tegljač');
execute upsert_tx(5, 'hr', 'Prikolica');
execute upsert_tx(6, 'hr', 'Kontejner');
execute upsert_tx(7, 'hr', 'Vagon');
execute upsert_tx(8, 'hr', 'Silos isporuke');
execute upsert_tx(9, 'hr', 'Viličar (ne koristi)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - SR
execute upsert_tx(1, 'sr', 'PDA korisnik');
execute upsert_tx(2, 'sr', 'Utovarna stanica - silos');
execute upsert_tx(3, 'sr', 'LDS Terminal (ne koristi)');
execute upsert_tx(4, 'sr', 'Kamion');
execute upsert_tx(5, 'sr', 'Prikolica');
execute upsert_tx(6, 'sr', 'Kontejner');
execute upsert_tx(7, 'sr', 'Vagon');
execute upsert_tx(8, 'sr', 'Kupčev silos');
execute upsert_tx(9, 'sr', 'Viljuškar (ne koristiti)');

prompt INSERTING into TLGNR_RFIDOBJECT_TYPE_TX - TR
execute upsert_tx(1, 'tr', 'PDA User');
execute upsert_tx(2, 'tr', 'Loadingpoint Silo');
execute upsert_tx(3, 'tr', 'LDS Terminal (not used)');
execute upsert_tx(4, 'tr', 'Truck');
execute upsert_tx(5, 'tr', 'Trailer');
execute upsert_tx(6, 'tr', 'Container');
execute upsert_tx(7, 'tr', 'Wagon');
execute upsert_tx(8, 'tr', 'ShipTo Silo');
execute upsert_tx(9, 'tr', 'Forklift (not used)');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

