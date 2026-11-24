-- $Revision$
-- $Date$
--
-- List based on clarification in https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/ShiptoOrderSAPCleanup#Clarification_of_Pickup_Transpor

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_delivery_type.sql ----------'

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
-- tlgnr_delivery_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_delivery_type_cd              in varchar2,
  p_sap_trsp_mode_cd              in varchar2,
  p_depends_on_delivery_type_cd   in varchar2,
  p_ctl_act_cd                    in number,
  p_apply_bp_block_check    in number)
is
  v_table_name    varchar2(48) := 'tlgnr_delivery_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_delivery_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.delivery_type_cd)
    into v_exists_flag
    from tlgnr_delivery_type_bs bs
    where bs.delivery_type_cd = p_delivery_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_delivery_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_delivery_type_bs set
          /* delivery_type_cd: cannot update because this is the key */
          sap_trsp_mode_cd               = p_sap_trsp_mode_cd,
          depends_on_delivery_type_cd    = p_depends_on_delivery_type_cd,
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          delivery_type_cd = p_delivery_type_cd and
          (ctl_act_cd != p_ctl_act_cd  /* not nullable */                   or
           1 = is_different_varchar2(sap_trsp_mode_cd, p_sap_trsp_mode_cd)  or
           1 = is_different_number(depends_on_delivery_type_cd, p_depends_on_delivery_type_cd))
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_delivery_type_cd || ').');
      end if;
    else
      insert into tlgnr_delivery_type_bs (
        delivery_type_cd,
        sap_trsp_mode_cd,
        depends_on_delivery_type_cd,
        apply_bp_block_check,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_delivery_type_cd,
        p_sap_trsp_mode_cd,
        p_depends_on_delivery_type_cd,
        p_apply_bp_block_check,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_delivery_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_delivery_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_delivery_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_delivery_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_delivery_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.delivery_type_cd) into v_exists_bs_flag
    from tlgnr_delivery_type_bs bs
    where bs.delivery_type_cd = p_delivery_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.delivery_type_cd) into v_exists_flag
      from tlgnr_delivery_type_tx tx
      where tx.delivery_type_cd = p_delivery_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_delivery_type_tx set
          /* p_delivery_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          delivery_type_cd = p_delivery_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_delivery_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_delivery_type_tx (
          delivery_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_delivery_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_delivery_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_delivery_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_DELIVERY_TYPE_BS
execute upsert_bs( 1, '10', null, 1, 1);
execute upsert_bs( 2, '50', null, 1, 1);
execute upsert_bs( 3, '50', null, 1, 1);
execute upsert_bs( 4, '10', 3,    1, 1);
execute upsert_bs( 5, '00', null, 1, 1);
execute upsert_bs( 6, '20', null, 1, 1);
execute upsert_bs( 7, '30', null, 1, 1);
execute upsert_bs( 8, '00', null, 1, 1);
execute upsert_bs( 9, '00', null, 1, 0);
execute upsert_bs(10, '10', null, 1, 0);
execute upsert_bs(11, '00', null, 1, 1);
execute upsert_bs(12, '00', null, 1, 1);

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - EN
execute upsert_tx( 1, 'en', 'Road direct');
execute upsert_tx( 2, 'en', 'Rail direct (single wagon)');
execute upsert_tx( 3, 'en', 'Bimodal Rail-Part');
execute upsert_tx( 4, 'en', 'Bimodal Road-Part');
execute upsert_tx( 5, 'en', 'Pickup');
execute upsert_tx( 6, 'en', 'Block train');
execute upsert_tx( 7, 'en', 'Ship');
execute upsert_tx( 8, 'en', 'Service');
execute upsert_tx( 9, 'en', 'Pallet Return');
execute upsert_tx(10, 'en', 'Pallet Return (w/transport)');
execute upsert_tx(11, 'en', 'Ship Pickup');
execute upsert_tx(12, 'en', 'Blocktrain Pickup');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - DE
execute upsert_tx( 1, 'de', 'Strasse direkt');
execute upsert_tx( 2, 'de', 'Bahnwagen direkt');
execute upsert_tx( 3, 'de', 'Kombi Bahn-Teil');
execute upsert_tx( 4, 'de', 'Kombi Strasse-Teil');
execute upsert_tx( 5, 'de', 'Selbstabholer');
execute upsert_tx( 6, 'de', 'Blockzug');
execute upsert_tx( 7, 'de', 'Schiff');
execute upsert_tx( 8, 'de', 'Service');
execute upsert_tx( 9, 'de', 'Retour Selbstabholer');
execute upsert_tx(10, 'de', 'Retour Geliefert');
execute upsert_tx(11, 'de', 'Schiff Selbstabholer');
execute upsert_tx(12, 'de', 'Blockzug Selbstabholer');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - FR
execute upsert_tx( 1, 'fr', 'Route direct');
execute upsert_tx( 2, 'fr', 'Rail direct');
execute upsert_tx( 3, 'fr', 'Bimodal partie rail');
execute upsert_tx( 4, 'fr', 'Bimodal partie route');
execute upsert_tx( 5, 'fr', 'Enlèvement');
execute upsert_tx( 6, 'fr', 'Train entier');
execute upsert_tx( 7, 'fr', 'Bateau');
execute upsert_tx( 8, 'fr', 'Service');
execute upsert_tx( 9, 'fr', 'Pallet Return');
execute upsert_tx(10, 'fr', 'Pallet Return (w/transport)');
execute upsert_tx(11, 'fr', 'Bateau Enlèvement');
execute upsert_tx(12, 'fr', 'Train Entier Enlèvement');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - IT
execute upsert_tx( 1, 'it', 'F. Destino');
execute upsert_tx( 2, 'it', 'Ferrovia Diretto');
execute upsert_tx( 3, 'it', 'Bimodale p. Ferrovia');
execute upsert_tx( 4, 'it', 'Bimodale p. Strada');
execute upsert_tx( 5, 'it', 'F. Partenza');
execute upsert_tx( 6, 'it', 'Treno');
execute upsert_tx( 7, 'it', 'Nave');
execute upsert_tx( 8, 'it', 'Servizio');
execute upsert_tx( 9, 'it', 'Reso Pickup');
execute upsert_tx(10, 'it', 'Consegna di Reso');
execute upsert_tx(11, 'it', 'Nave F. Partenza');
execute upsert_tx(12, 'it', 'Treno F. Partenza');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - ES
execute upsert_tx( 1, 'es', 'Camión');
execute upsert_tx( 2, 'es', 'Tren');
execute upsert_tx( 3, 'es', 'Bimodal Parte Tren');
execute upsert_tx( 4, 'es', 'Bimodal Parte Carretera');
execute upsert_tx( 5, 'es', 'Pickup');
execute upsert_tx( 6, 'es', 'Tren completo');
execute upsert_tx( 7, 'es', 'Barco');
execute upsert_tx( 8, 'es', 'Servicios');
execute upsert_tx( 9, 'es', 'Devolución Pickup');
execute upsert_tx(10, 'es', 'Devolución Planificada');
execute upsert_tx(11, 'es', 'Barco Pickup');
execute upsert_tx(12, 'es', 'Tren Completo Pickup');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - CS
execute upsert_tx( 1, 'cs', 'Silniční doprava');
execute upsert_tx( 2, 'cs', '6lezniční přeprava (jednotlivé vagóny)');
execute upsert_tx( 3, 'cs', 'Bimodální přeprava (železnice)');
execute upsert_tx( 4, 'cs', 'Bimodální přeprava (silnice)');
execute upsert_tx( 5, 'cs', 'Vlastní doprava');
execute upsert_tx( 6, 'cs', 'Ucelený vlak');
execute upsert_tx( 7, 'cs', 'Loď');
execute upsert_tx( 8, 'cs', 'Servisní jízda');
execute upsert_tx( 9, 'cs', 'Vrácení - Vlastní doprava');
execute upsert_tx(10, 'cs', 'Vrácení - DDP');
execute upsert_tx(11, 'cs', 'Ship Pickup');
execute upsert_tx(12, 'cs', 'Blocktrain Pickup');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - SK
execute upsert_tx( 1, 'sk', 'Cestné priame');
execute upsert_tx( 2, 'sk', 'Železničné priame (vagón)');
execute upsert_tx( 3, 'sk', 'Bimodálna železničná časť');
execute upsert_tx( 4, 'sk', 'Bimodálna cestná časť');
execute upsert_tx( 5, 'sk', 'Pickup - EXW');
execute upsert_tx( 6, 'sk', 'Ucelený vlak');
execute upsert_tx( 7, 'sk', 'Loď');
execute upsert_tx( 8, 'sk', 'Servis');
execute upsert_tx( 9, 'sk', 'Spätne EXW');
execute upsert_tx(10, 'sk', 'Spätne doručené');
execute upsert_tx(11, 'sk', 'Loď EXW');
execute upsert_tx(12, 'sk', 'Železničná súprava EXW');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - HU
execute upsert_tx( 1, 'hu', 'Útirány');
execute upsert_tx( 2, 'hu', 'Vasúti irány (egy vagon)');
execute upsert_tx( 3, 'hu', 'Bimodális vasúti rész');
execute upsert_tx( 4, 'hu', 'Bimodális közúti rész');
execute upsert_tx( 5, 'hu', 'Felvesz');
execute upsert_tx( 6, 'hu', 'Szerelvény');
execute upsert_tx( 7, 'hu', 'Szállítás');
execute upsert_tx( 8, 'hu', 'Szolgáltatás');
execute upsert_tx( 9, 'hu', 'Felvétel visszavonása');
execute upsert_tx(10, 'hu', 'Kiszállítás visszavonása');
execute upsert_tx(11, 'hu', 'Saját fuvaros kiszállítás');
execute upsert_tx(12, 'hu', 'Saját fuvaros irányvonat');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - RO
execute upsert_tx( 1, 'ro', 'Rutier');
execute upsert_tx( 2, 'ro', 'Feroviar (vagon unic)');
execute upsert_tx( 3, 'ro', 'Bimodal feroviar - parte');
execute upsert_tx( 4, 'ro', 'Bimodal rutier - parte');
execute upsert_tx( 5, 'ro', 'Ridicare');
execute upsert_tx( 6, 'ro', 'Tren');
execute upsert_tx( 7, 'ro', 'Vapor');
execute upsert_tx( 8, 'ro', 'Service');
execute upsert_tx( 9, 'ro', 'Ridicare la retur');
execute upsert_tx(10, 'ro', 'Retur livrat');
execute upsert_tx(11, 'ro', 'Nave EXW');
execute upsert_tx(12, 'ro', 'Tren complet EXW');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - BG
execute upsert_tx( 1, 'bg', 'Автомобилна директна');
execute upsert_tx( 2, 'bg', 'ЖП директна (един вагон)');
execute upsert_tx( 3, 'bg', 'Бимодална жп-част');
execute upsert_tx( 4, 'bg', 'Бимодална авто-част');
execute upsert_tx( 5, 'bg', 'Трансфер');
execute upsert_tx( 6, 'bg', 'Блок влак');
execute upsert_tx( 7, 'bg', 'Кораб');
execute upsert_tx( 8, 'bg', 'Услуга');
execute upsert_tx( 9, 'bg', 'Върнат трансфер');
execute upsert_tx(10, 'bg', 'Върната доставка');
execute upsert_tx(11, 'bg', 'Собствен кораб');
execute upsert_tx(12, 'bg', 'Собствен блок-влак');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx( 1, 'ru', 'Автодоставка');
execute upsert_tx( 2, 'ru', 'Доставка ЖД');
execute upsert_tx( 3, 'ru', 'Бимодальная поставка: ЖД часть');
execute upsert_tx( 4, 'ru', 'Бимодальная поставка: Авто часть');
execute upsert_tx( 5, 'ru', 'самовывоз');
execute upsert_tx( 6, 'ru', 'Block train');
execute upsert_tx( 7, 'ru', 'судно');
execute upsert_tx( 8, 'ru', 'сервис');
execute upsert_tx( 9, 'ru', 'возврат самовывозом');
execute upsert_tx(10, 'ru', 'возврат доставкой');
execute upsert_tx(11, 'ru', 'Самовывоз: баржа');
execute upsert_tx(12, 'ru', 'Blocktrain Pickup');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - PL (ENGLISH COPY)
execute upsert_tx( 1, 'pl', 'Droga bezpośrednia');
execute upsert_tx( 2, 'pl', 'Kolejowy bezpośredni(pojedyńczy wagon)');
execute upsert_tx( 3, 'pl', 'Bimodalny transport część kolejowa');
execute upsert_tx( 4, 'pl', 'Bimodalny transport część drogowa');
execute upsert_tx( 5, 'pl', 'Odbiór');
execute upsert_tx( 6, 'pl', 'Zablokować pociąg');
execute upsert_tx( 7, 'pl', 'Statek');
execute upsert_tx( 8, 'pl', 'Serwis');
execute upsert_tx( 9, 'pl', 'Zwrot odbioru');
execute upsert_tx(10, 'pl', 'Zwrot dostawy');
execute upsert_tx(11, 'pl', 'Odbiór statkiem');
execute upsert_tx(12, 'pl', 'Odbiór pociągiem');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx( 1, 'az', 'Road direct');
execute upsert_tx( 2, 'az', 'Rail direct (single wagon)');
execute upsert_tx( 3, 'az', 'Bimodal Rail-Part');
execute upsert_tx( 4, 'az', 'Bimodal Road-Part');
execute upsert_tx( 5, 'az', 'Pickup');
execute upsert_tx( 6, 'az', 'Block train');
execute upsert_tx( 7, 'az', 'Ship');
execute upsert_tx( 8, 'az', 'Service');
execute upsert_tx( 9, 'az', 'Pallet Return');
execute upsert_tx(10, 'az', 'Pallet Return (w/transport)');
execute upsert_tx(11, 'az', 'Ship Pickup');
execute upsert_tx(12, 'az', 'Blocktrain Pickup');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - HR (ENGLISH COPY)
execute upsert_tx( 1, 'hr', 'Cestom direktno ');
execute upsert_tx( 2, 'hr', 'Željeznicom direktno (jedan vagon)');
execute upsert_tx( 3, 'hr', 'Bimodal Rail-Part');
execute upsert_tx( 4, 'hr', 'Bimodal Road-Part');
execute upsert_tx( 5, 'hr', 'FCA');
execute upsert_tx( 6, 'hr', 'Blokiraj vlak');
execute upsert_tx( 7, 'hr', 'Brod');
execute upsert_tx( 8, 'hr', 'Usluga');
execute upsert_tx( 9, 'hr', 'Povrat FCA');
execute upsert_tx(10, 'hr', 'Povrat isporučenog');
execute upsert_tx(11, 'hr', 'Brod FOB');
execute upsert_tx(12, 'hr', 'Vlak Pickup');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - SR (ENGLISH COPY)
execute upsert_tx( 1, 'sr', 'Drumski prevoz');
execute upsert_tx( 2, 'sr', 'Prevoz železnicom');
execute upsert_tx( 3, 'sr', 'Bimodalni tr. Železnica');
execute upsert_tx( 4, 'sr', 'Bimodalni tr.  Drum');
execute upsert_tx( 5, 'sr', 'EXW');
execute upsert_tx( 6, 'sr', 'Blok vagona');
execute upsert_tx( 7, 'sr', 'Brod');
execute upsert_tx( 8, 'sr', 'Usluga');
execute upsert_tx( 9, 'sr', 'Povraćaj EXW naloga');
execute upsert_tx(10, 'sr', 'Povraćaj DDP naloga');
execute upsert_tx(11, 'sr', 'EXW brod');
execute upsert_tx(12, 'sr', 'EXW blok voz');

prompt INSERTING into TLGNR_DELIVERY_TYPE_TX - TR
execute upsert_tx( 1, 'tr', 'Road direct');
execute upsert_tx( 2, 'tr', 'Rail direct (single wagon)');
execute upsert_tx( 3, 'tr', 'Bimodal Rail-Part');
execute upsert_tx( 4, 'tr', 'Bimodal Road-Part');
execute upsert_tx( 5, 'tr', 'Pickup');
execute upsert_tx( 6, 'tr', 'Block train');
execute upsert_tx( 7, 'tr', 'Ship');
execute upsert_tx( 8, 'tr', 'Service');
execute upsert_tx( 9, 'tr', 'Pallet Return');
execute upsert_tx(10, 'tr', 'Pallet Return (w/transport)');
execute upsert_tx(11, 'tr', 'Ship Pickup');
execute upsert_tx(12, 'tr', 'Blocktrain Pickup');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

