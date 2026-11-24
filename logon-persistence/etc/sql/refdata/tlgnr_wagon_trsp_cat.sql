-- $Revision$
-- $Date$
--
-- 29.9.2009:
-- Based on list /share/doc/crh/pegasus/input/Bahn/RailcarMasterData_V0.5.5_20090807.xls, and made similar to truck/trailer transport category.

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_wagon_trsp_cat.sql ----------'

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
-- tlgnr_wagon_trsp_cat_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_wagon_trsp_cat_cd         in number,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_wagon_trsp_cat_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_wagon_trsp_cat_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.wagon_trsp_cat_cd)
    into v_exists_flag
    from tlgnr_wagon_trsp_cat_bs bs
    where bs.wagon_trsp_cat_cd = p_wagon_trsp_cat_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_wagon_trsp_cat_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_wagon_trsp_cat_bs set
/* wagon_trsp_cat_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          wagon_trsp_cat_cd = p_wagon_trsp_cat_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_wagon_trsp_cat_cd || ').');
      end if;
    else
      insert into tlgnr_wagon_trsp_cat_bs (
        wagon_trsp_cat_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_wagon_trsp_cat_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_wagon_trsp_cat_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_wagon_trsp_cat_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_wagon_trsp_cat_cd       in number,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_wagon_trsp_cat_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_wagon_trsp_cat_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.wagon_trsp_cat_cd) into v_exists_bs_flag
    from tlgnr_wagon_trsp_cat_bs bs
    where bs.wagon_trsp_cat_cd = p_wagon_trsp_cat_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.wagon_trsp_cat_cd) into v_exists_flag
      from tlgnr_wagon_trsp_cat_tx tx
      where tx.wagon_trsp_cat_cd = p_wagon_trsp_cat_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_wagon_trsp_cat_tx set
/* p_wagon_trsp_cat_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          wagon_trsp_cat_cd = p_wagon_trsp_cat_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_wagon_trsp_cat_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_wagon_trsp_cat_tx (
          wagon_trsp_cat_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_wagon_trsp_cat_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_wagon_trsp_cat_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_wagon_trsp_cat_cd || ').');
    end if;
  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
-- skip 3, because this is used for "Trailer" in truck_trsp_cat !! The rest, i.e. 1,2,4 are comparable to truck/trailer
execute upsert_bs(4, 1);
execute upsert_bs(5, 1);
execute upsert_bs(6, 1);
execute upsert_bs(7, 1);

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - EN
execute upsert_tx(1, 'en', 'Silo (Bulk cement)');
execute upsert_tx(2, 'en', 'Container');
execute upsert_tx(4, 'en', 'Sliding walls (Pallets)');
execute upsert_tx(5, 'en', 'Opening roof/gravity discharge (Clinker, Slag)');
execute upsert_tx(6, 'en', 'Open/gravity discharge (Coal, Slag, Clinker, Gravel)');
execute upsert_tx(7, 'en', 'Open/Side-tipping (Gipsum, Clinker)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - DE
execute upsert_tx(1, 'de', 'Silo (Bulk cement)');
execute upsert_tx(2, 'de', 'Container');
execute upsert_tx(4, 'de', 'Schiebetüren (Stückgut, Paletten)');
execute upsert_tx(5, 'de', 'Opening roof/gravity discharge (Klinker/Schlacke)');
execute upsert_tx(6, 'de', 'Open/gravity discharge (Kohle, Schlacke, Klinker, Kies)');
execute upsert_tx(7, 'de', 'Open/Side-tipping (Gips, Klinker)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - FR
execute upsert_tx(1, 'fr', 'Silo (Ciment vrac)');
execute upsert_tx(2, 'fr', 'Container');
execute upsert_tx(4, 'fr', 'Parois coulissantes (Palettes)');
execute upsert_tx(5, 'fr', 'Toit ouvrant/décharge gravitaire (Clinker, Scories)');
execute upsert_tx(6, 'fr', 'Ouvert/décharge gravitaire (Charbon, Scories, Clinker, Gravier)');
execute upsert_tx(7, 'fr', 'Ouvert/Latérale-basculant (Gypse, Clinker)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - IT
execute upsert_tx(1, 'it', 'Silo (cemento sfuso)');
execute upsert_tx(2, 'it', 'Container');
execute upsert_tx(4, 'it', 'Pareti scorrevoli (Bancali)');
execute upsert_tx(5, 'it', 'Tetto apribile/scarico a gravità (Clinker, Scoria)');
execute upsert_tx(6, 'it', 'Aperto/scarico a gravità (Carbone, Scoria, Clinker, Ghiaia)');
execute upsert_tx(7, 'it', 'Aperto/Ribaltamento laterale (Gesso, Clinker)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - ES
execute upsert_tx(1, 'es', 'Silo (cemento a granel)');
execute upsert_tx(2, 'es', 'Contenedor');
execute upsert_tx(4, 'es', 'Puertas corredizas (Palés)');
execute upsert_tx(5, 'es', 'Techo abatible/descarga gavitatoria (Clínker, Escoria)');
execute upsert_tx(6, 'es', 'Abierto/descarga gravitatoria (Carbón, Escoria, Clínker, Grava)');
execute upsert_tx(7, 'es', 'Abierto/basculante lateral (Yeso, Clínker)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - CS
execute upsert_tx(1, 'cs', 'Cisterna (VLC)');
execute upsert_tx(2, 'cs', 'Kontejner');
execute upsert_tx(4, 'cs', 'Vagón s posuvnými dveřmi (palety)');
execute upsert_tx(5, 'cs', 'Otvírací střecha/vykládka spodem (Slinek, struska)');
execute upsert_tx(6, 'cs', 'Otevřený vagón/vykládka spodem (uhlí, struska, slinek, štěrk) ');
execute upsert_tx(7, 'cs', 'Otevřený vagón/sklápěcí (sádrovec, slinek)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - SK
execute upsert_tx(1, 'sk', 'Silo (voľne ložený cement)');
execute upsert_tx(2, 'sk', 'Kontajner');
execute upsert_tx(4, 'sk', 'Posuvné steny (Palety)');
execute upsert_tx(5, 'sk', 'Posuvná strecha/gravitačné plnenie (slinok, troska)');
execute upsert_tx(6, 'sk', 'Otvorené/gravitačné plnenie (uhlie, slinok, troska, štrk)');
execute upsert_tx(7, 'sk', 'Otvorené/bočné vyklápanie (sádra, slinok)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - HU
execute upsert_tx(1, 'hu', 'Siló (ömlesztett cement)');
execute upsert_tx(2, 'hu', 'Konténer');
execute upsert_tx(4, 'hu', 'Eltolható fal (raklapok)');
execute upsert_tx(5, 'hu', 'Nyitott tetejű / gravitáció mentes (klinker, salak)');
execute upsert_tx(6, 'hu', 'Nyitott / gravitáció mentes (szén, salak, klinker, kavics)');
execute upsert_tx(7, 'hu', 'Nyitott / oldal kiegészítés (gipsz, klinker)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - RO
execute upsert_tx(1, 'ro', 'Siloz (ciment vrac)');
execute upsert_tx(2, 'ro', 'Container');
execute upsert_tx(4, 'ro', 'Pereti glisanti (Paleti)');
execute upsert_tx(5, 'ro', 'Trapa superioara/descarcare gravitationala (Clinker, Slag)');
execute upsert_tx(6, 'ro', 'Deschis/descarcare gravitationala (Carbune, Slag, Clinker, Pietris)');
execute upsert_tx(7, 'ro', 'Deschis/basculare laterala (gips, clinker)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - BG
execute upsert_tx(1, 'bg', 'Силоз ( Насипен цимент )');
execute upsert_tx(2, 'bg', 'Контейнер');
execute upsert_tx(4, 'bg', 'Плъзкащи стени (Палети)');
execute upsert_tx(5, 'bg', 'Отворен покрив/Освобождаване на тежестта (Клинкер, Шлака)');
execute upsert_tx(6, 'bg', 'Отворен/Освобождаване на тежестта (Въглища, Шлака, Клинкер, Чакъл)');
execute upsert_tx(7, 'bg', 'Отворен/Странично разтоварване (Гипсокартон, Клинкер)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'Силос (навальный цемент)');
execute upsert_tx(2, 'ru', 'контейнер');
execute upsert_tx(4, 'ru', 'Крытые вагоны (Паллеты)');
execute upsert_tx(5, 'ru', 'Хоппер\разгрузка самотеком (клинкер, шлак)');
execute upsert_tx(6, 'ru', 'Открыть\разгрузка самотеком (клинкер, шлак)');
execute upsert_tx(7, 'ru', 'Открыть\боковая разгузка (гипс, клинкер)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - PL
execute upsert_tx(1, 'pl', 'Silos (Cement luzem)');
execute upsert_tx(2, 'pl', 'Pojemnik');
execute upsert_tx(4, 'pl', 'Przesuwane ściany (Palety)');
execute upsert_tx(5, 'pl', 'Otwieranie dachu/grawitacja rozładowania (klinkier, żużel)');
execute upsert_tx(6, 'pl', 'Otwórz/grawitacja rozładowania (węgiel, żużel , klinkier, żwir)');
execute upsert_tx(7, 'pl', 'Otwórz/stronne (Gipsum , klinkier)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'Bunker (Tutumu sement)');
execute upsert_tx(2, 'az', 'Konteyner');
execute upsert_tx(4, 'az', 'Sürüşən divarlar (altlıqlar)');
execute upsert_tx(5, 'az', 'Açılan dam / öz-özünə boşalma (Klinker, Şlak)');
execute upsert_tx(6, 'az', 'Açıq/öz-özünə boşalma (Kömür, Şlak, Klinker, Çınqıl)');
execute upsert_tx(7, 'az', 'Açıq/yan tərəfə aşma (Gips, Klinker)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - HR
execute upsert_tx(1, 'hr', 'Silos(rasuti cement)');
execute upsert_tx(2, 'hr', 'Kontejner');
execute upsert_tx(4, 'hr', 'Klizne stijene(palete)');
execute upsert_tx(5, 'hr', 'Otvaranje krova/gravitacijsko pražnjenje(klinkera,Troska)');
execute upsert_tx(6, 'hr', 'Otvoreno/gravitacijsko pražnjenje(ugljen,troska,klinkera,šljunčana)');
execute upsert_tx(7, 'hr', 'Otvoreno/bočno-prevrtanja(Gipsum,klinkera)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - SR
execute upsert_tx(1, 'sr', 'Silos(rinfuzni cement)');
execute upsert_tx(2, 'sr', 'Kontejner');
execute upsert_tx(4, 'sr', 'Pokretni zidovi(palete)');
execute upsert_tx(5, 'sr', 'Otvaranje krova/gravitacijsko pražnjenje(klinkera,Troške)');
execute upsert_tx(6, 'sr', 'Otvoreno/gravitacijsko pražnjenje(ugljen,troške,klinkera,šljunka)');
execute upsert_tx(7, 'sr', 'Otvoreno/bočno-prevrtanje(Gipsa,klinkera)');

prompt INSERTING into TLGNR_WAGON_TRSP_CAT_TX - TR
execute upsert_tx(1, 'tr', 'Silo (Bulk cement)');
execute upsert_tx(2, 'tr', 'Container');
execute upsert_tx(4, 'tr', 'Sliding walls (Pallets)');
execute upsert_tx(5, 'tr', 'Opening roof/gravity discharge (Clinker, Slag)');
execute upsert_tx(6, 'tr', 'Open/gravity discharge (Coal, Slag, Clinker, Gravel)');
execute upsert_tx(7, 'tr', 'Open/Side-tipping (Gipsum, Clinker)');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
