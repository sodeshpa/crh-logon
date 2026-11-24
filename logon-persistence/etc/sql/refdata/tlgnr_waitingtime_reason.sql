-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_waitingtime_reason.sql ----------'


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
-- tlgnr_waitingtime_reason_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_waitingtime_reason_cd     in number,
  p_at_shipto_flag            in number,
  p_at_shippingpoint_flag     in number,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_waitingtime_reason_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_waitingtime_reason_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.waitingtime_reason_cd)
    into v_exists_flag
    from tlgnr_waitingtime_reason_bs bs
    where bs.waitingtime_reason_cd = p_waitingtime_reason_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_waitingtime_reason_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_waitingtime_reason_bs set
/* waitingtime_reason_cd: cannot update because this is the key */
          at_shipto_flag                 = p_at_shipto_flag,
          at_shippingpoint_flag          = p_at_shippingpoint_flag,
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          waitingtime_reason_cd = p_waitingtime_reason_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_waitingtime_reason_cd || ').');
      end if;
    else
      insert into tlgnr_waitingtime_reason_bs (
        waitingtime_reason_cd,
        at_shipto_flag,
        at_shippingpoint_flag,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_waitingtime_reason_cd,
        p_at_shipto_flag,
        p_at_shippingpoint_flag,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_waitingtime_reason_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_waitingtime_reason_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_waitingtime_reason_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_waitingtime_reason_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_waitingtime_reason_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.waitingtime_reason_cd) into v_exists_bs_flag
    from tlgnr_waitingtime_reason_bs bs
    where bs.waitingtime_reason_cd = p_waitingtime_reason_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.waitingtime_reason_cd) into v_exists_flag
      from tlgnr_waitingtime_reason_tx tx
      where tx.waitingtime_reason_cd = p_waitingtime_reason_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_waitingtime_reason_tx set
/* p_waitingtime_reason_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          waitingtime_reason_cd = p_waitingtime_reason_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_waitingtime_reason_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_waitingtime_reason_tx (
          waitingtime_reason_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_waitingtime_reason_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_waitingtime_reason_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_waitingtime_reason_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_WAITINGTIME_REASON_BS
execute upsert_bs(1,1,0,1);
execute upsert_bs(2,1,0,1);
execute upsert_bs(3,1,1,1);
execute upsert_bs(4,0,1,1);
execute upsert_bs(5,0,1,1);
execute upsert_bs(6,1,0,1);
execute upsert_bs(7,1,0,1);
execute upsert_bs(8,1,0,1);
execute upsert_bs(9,1,1,1);
execute upsert_bs(10,1,1,1);
execute upsert_bs(11,1,1,1);

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - EN
execute upsert_tx(1, 'en', 'Arrival too early');
execute upsert_tx(2, 'en', 'Arrival out of opening hours');
execute upsert_tx(3, 'en', 'Other vehicle at loading/unloading Point');
execute upsert_tx(4, 'en', 'Loading point out of order');
execute upsert_tx(5, 'en', 'No order available');
execute upsert_tx(6, 'en', 'Customer is not present');
execute upsert_tx(7, 'en', 'Not enough space left in silo');
execute upsert_tx(8, 'en', 'Unloading silo defect');
execute upsert_tx(9, 'en', 'Issue with vehicle');
execute upsert_tx(10, 'en', 'Break (button not pressed)');
execute upsert_tx(11, 'en', 'Other reason');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - DE
execute upsert_tx(1, 'de', 'Ankunft zu früh');
execute upsert_tx(2, 'de', 'Ankunft ausserhalb Öffnungszeit');
execute upsert_tx(3, 'de', 'Anderer LKW an Ent-/Beladestelle');
execute upsert_tx(4, 'de', 'Ladestelle ausser Betrieb');
execute upsert_tx(5, 'de', 'Kein Auftrag vorhanden');
execute upsert_tx(6, 'de', 'Kunde nicht anwesend');
execute upsert_tx(7, 'de', 'Nicht genügend Platz im Silo');
execute upsert_tx(8, 'de', 'Entladesilo defekt');
execute upsert_tx(9, 'de', 'Problem mit LKW');
execute upsert_tx(10, 'de', 'Pause (Knopf nicht gedrückt)');
execute upsert_tx(11, 'de', 'Andere Ursache');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - FR
execute upsert_tx(1, 'fr', 'Arrivée trop tôt');
execute upsert_tx(2, 'fr', 'Arrive hors horaire d''ouverture');
execute upsert_tx(3, 'fr', 'Un autre véhicule au point de chargement/déchargement');
execute upsert_tx(4, 'fr', 'Point de chargement hors service');
execute upsert_tx(5, 'fr', 'Aucune commande disponible');
execute upsert_tx(6, 'fr', 'Client n''est pas présent');
execute upsert_tx(7, 'fr', 'Pas assez d''espace dans le silo');
execute upsert_tx(8, 'fr', 'Défaut de déchargement du silo ');
execute upsert_tx(9, 'fr', 'Problème avec véhicule');
execute upsert_tx(10, 'fr', 'Pause (bouton pas appuyé)');
execute upsert_tx(11, 'fr', 'Autre raison');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - IT
execute upsert_tx(1, 'it', 'Arrivo in anticipo');
execute upsert_tx(2, 'it', 'Arrivo al di fuori dell''orario di apertura');
execute upsert_tx(3, 'it', 'Altro veicolo al punto di carico/scarico');
execute upsert_tx(4, 'it', 'Punto di carico fuori servizio');
execute upsert_tx(5, 'it', 'Nessun ordine disponibile');
execute upsert_tx(6, 'it', 'Cliente assente');
execute upsert_tx(7, 'it', 'Insufficiente spazio lasciato nel silo');
execute upsert_tx(8, 'it', 'Scarico del silo difettoso');
execute upsert_tx(9, 'it', 'Problema col veicolo');
execute upsert_tx(10, 'it', 'Break (pulsante non attivato)');
execute upsert_tx(11, 'it', 'Altre ragioni');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - ES
execute upsert_tx(1, 'es', 'Llegada temprano');
execute upsert_tx(2, 'es', 'Llegada fuera de horario');
execute upsert_tx(3, 'es', 'Otro vehículo en el lugar de carga/descarga');
execute upsert_tx(4, 'es', 'Lugar de carga fuera de servicio');
execute upsert_tx(5, 'es', 'Ningún pedido disponible');
execute upsert_tx(6, 'es', 'El cliente no está presente');
execute upsert_tx(7, 'es', 'No hay espacio suficiente en el silo');
execute upsert_tx(8, 'es', 'Descarga de silo defectuosa');
execute upsert_tx(9, 'es', 'Problemas con el camión');
execute upsert_tx(10, 'es', 'Pausa (botón no pulsado)');
execute upsert_tx(11, 'es', 'Otra causa');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - CS (ENGLISH COPY)
execute upsert_tx(1, 'cs', 'Příjezd moc brzy');
execute upsert_tx(2, 'cs', 'Příjezd mimo otvírací dobu');
execute upsert_tx(3, 'cs', 'Jiné vozidlo je na místě nakládky/vykládky');
execute upsert_tx(4, 'cs', 'Místo nakládky je mimo Objednávku');
execute upsert_tx(5, 'cs', 'Žádné objednávky nejsou dostupné');
execute upsert_tx(6, 'cs', 'Zákazník není přítomen');
execute upsert_tx(7, 'cs', 'V sile nezbývá místo');
execute upsert_tx(8, 'cs', 'Porucha sila pro vykládku');
execute upsert_tx(9, 'cs', 'Problém s vozidlem');
execute upsert_tx(10, 'cs', 'Přestávka (tlačítko nestisknuto)');
execute upsert_tx(11, 'cs', 'Jiný důvod');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - SK (ENGLISH COPY)
execute upsert_tx(1, 'sk', 'Predčasný príchod');
execute upsert_tx(2, 'sk', 'Príchod mimo otváracích hodín');
execute upsert_tx(3, 'sk', 'Iné vozidlo na nakládke/vykládke');
execute upsert_tx(4, 'sk', 'Nakládka vyradená z prevádzky');
execute upsert_tx(5, 'sk', 'Nie je dostupná žiadna objednávka');
execute upsert_tx(6, 'sk', 'Neprítomný zákazník');
execute upsert_tx(7, 'sk', 'Nedostatok mieste v sile');
execute upsert_tx(8, 'sk', 'Pokazené silo vykládky');
execute upsert_tx(9, 'sk', 'Problém s vozidlom');
execute upsert_tx(10, 'sk', 'Brzda (nestlačené tlačidlo)');
execute upsert_tx(11, 'sk', 'Iný dôvod');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - HU (ENGLISH COPY)
execute upsert_tx(1, 'hu', 'Túl korai érkezés');
execute upsert_tx(2, 'hu', 'Nyitvatartási időn kívüli érkezés');
execute upsert_tx(3, 'hu', 'Más jármű töltési / letöltési hely');
execute upsert_tx(4, 'hu', 'Felrakó hely rendelésből');
execute upsert_tx(5, 'hu', 'Nem áll rendelkezésre');
execute upsert_tx(6, 'hu', 'Vevő nem áll rendelkezésre');
execute upsert_tx(7, 'hu', 'Nem marad elég hely a silóban');
execute upsert_tx(8, 'hu', 'Kirakodásnál siló hiba');
execute upsert_tx(9, 'hu', 'Kérdéses jármű');
execute upsert_tx(10, 'hu', 'Szünet (gomb lenyomásával)');
execute upsert_tx(11, 'hu', 'Egyéb ok');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - RO (ENGLISH COPY)
execute upsert_tx(1, 'ro', 'Sosire prea devreme');
execute upsert_tx(2, 'ro', 'Sosire in afara programului');
execute upsert_tx(3, 'ro', 'Alt vehicul la Punctul incarcare/descarcare');
execute upsert_tx(4, 'ro', 'Punct incarcare inchis');
execute upsert_tx(5, 'ro', 'Nicio comanda disponibila');
execute upsert_tx(6, 'ro', 'Clientul nu este prezent');
execute upsert_tx(7, 'ro', 'Nu este spatiu suficient in siloz');
execute upsert_tx(8, 'ro', 'Siloz descarcare defect');
execute upsert_tx(9, 'ro', 'Problema cu vehicul');
execute upsert_tx(10, 'ro', 'Pauza (butonul nu a fost apasat)');
execute upsert_tx(11, 'ro', 'Alt motiv');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - BG (ENGLISH COPY)
execute upsert_tx(1, 'bg', 'Пристигнал твърде рано');
execute upsert_tx(2, 'bg', 'Пристигане извън работно време');
execute upsert_tx(3, 'bg', 'Друго превозно средство на мястото за товарене/разтоварване');
execute upsert_tx(4, 'bg', 'Мястото на товарене е повредено');
execute upsert_tx(5, 'bg', 'Няма налична поръчка');
execute upsert_tx(6, 'bg', 'Клиентът не е настоящ');
execute upsert_tx(7, 'bg', 'Няма достатъчно място в силоза');
execute upsert_tx(8, 'bg', 'Дефектен силоз');
execute upsert_tx(9, 'bg', 'Издаване на превозно средство');
execute upsert_tx(10, 'bg', 'Почивка (Бутонът не е натиснат)');
execute upsert_tx(11, 'bg', 'Друга причин');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'ранее прибытие');
execute upsert_tx(2, 'ru', 'заезд в часы работы');
execute upsert_tx(3, 'ru', 'Другое транспортное средство в точке погрузки / разгрузки');
execute upsert_tx(4, 'ru', 'Точка погрузки вышла из строя');
execute upsert_tx(5, 'ru', 'нет доступного заказ');
execute upsert_tx(6, 'ru', 'Клиент отсутствует');
execute upsert_tx(7, 'ru', 'Не хватает свободного места в силосе');
execute upsert_tx(8, 'ru', 'Силос поврежден');
execute upsert_tx(9, 'ru', 'Проблема с автомобилем');
execute upsert_tx(10, 'ru', 'Перерыв (кнопка не нажата)');
execute upsert_tx(11, 'ru', 'Другие причины');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - PL
execute upsert_tx(1, 'pl', 'Przyjazd zbyt wcześnie');
execute upsert_tx(2, 'pl', 'Przyjazd poza godzinami pracy');
execute upsert_tx(3, 'pl', 'Inny pojazd przy załadunku / rozładunku');
execute upsert_tx(4, 'pl', 'Ładowanie punkt z porządku');
execute upsert_tx(5, 'pl', 'No order dostępny');
execute upsert_tx(6, 'pl', 'Klient nie jest obecny');
execute upsert_tx(7, 'pl', 'Za mało wolnego miejsca w silosie');
execute upsert_tx(8, 'pl', 'Rozładunek silos wada');
execute upsert_tx(9, 'pl', 'Problem z pojazdu');
execute upsert_tx(10, 'pl', 'Przerwa ( przycisk nie został naciśnięty)');
execute upsert_tx(11, 'pl', 'Inny powód');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'Çox tez gəlmə');
execute upsert_tx(2, 'az', 'Açılış saatı xaricində gəlmə');
execute upsert_tx(3, 'az', 'Yükləmə/boşaltma məntəqəsində digər nəqliyyat vasitəsi');
execute upsert_tx(4, 'az', 'Sifarişdən kənar yükləmə məntəqəsi');
execute upsert_tx(5, 'az', 'Heç bir sifariş yoxdur');
execute upsert_tx(6, 'az', 'Müştəri yerində yoxdur');
execute upsert_tx(7, 'az', 'Bunkerdə kifayət qədər yer saxlanmayıb');
execute upsert_tx(8, 'az', 'Yükboşaltma bunkerində nöqsan');
execute upsert_tx(9, 'az', 'Nəqliyyat vasitəsi ilə bağlı problem');
execute upsert_tx(10, 'az', 'Fasilə (düymə basılmayıb)');
execute upsert_tx(11, 'az', 'Digər səbəb');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - HR
execute upsert_tx(1, 'hr', 'Dolazak prerano');
execute upsert_tx(2, 'hr', 'Dolazak izvan radnog vremena');
execute upsert_tx(3, 'hr', 'Ostala vozila na utovaru / istovaru');
execute upsert_tx(4, 'hr', 'Utovarna točka izvan funkcije');
execute upsert_tx(5, 'hr', 'Narudžba nije na raspolaganju');
execute upsert_tx(6, 'hr', 'Kupac nije prisutan');
execute upsert_tx(7, 'hr', 'Nema dovoljno mjesta u silosu');
execute upsert_tx(8, 'hr', 'Istovar silosa u kvaru');
execute upsert_tx(9, 'hr', 'Problem s vozilom');
execute upsert_tx(10, 'hr', 'Prekid (tipka nije pritisnuta)');
execute upsert_tx(11, 'hr', 'Drugi razlog');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - SR
execute upsert_tx(1, 'sr', 'Prerani dolazak');
execute upsert_tx(2, 'sr', 'Dolazak van radnog vremena');
execute upsert_tx(3, 'sr', 'Ostala vozila na utovaru / istovaru');
execute upsert_tx(4, 'sr', 'Utovarna tačka van funkcije');
execute upsert_tx(5, 'sr', 'Narudžbina nije na raspolaganju');
execute upsert_tx(6, 'sr', 'Kupac nije prisutan');
execute upsert_tx(7, 'sr', 'Nema dovoljno mesta u silosu');
execute upsert_tx(8, 'sr', 'Istovar silosa u kvaru');
execute upsert_tx(9, 'sr', 'Problem s vozilom');
execute upsert_tx(10, 'sr', 'Prekid (dugme nije pritisnuto)');
execute upsert_tx(11, 'sr', 'Drugi razlog');

prompt INSERTING into TLGNR_WAITINGTIME_REASON_TX - TR
execute upsert_tx(1, 'tr', 'Arrival too early');
execute upsert_tx(2, 'tr', 'Arrival out of opening hours');
execute upsert_tx(3, 'tr', 'Other vehicle at loading/unloading Point');
execute upsert_tx(4, 'tr', 'Loading point out of order');
execute upsert_tx(5, 'tr', 'No order available');
execute upsert_tx(6, 'tr', 'Customer is not present');
execute upsert_tx(7, 'tr', 'Not enough space left in silo');
execute upsert_tx(8, 'tr', 'Unloading silo defect');
execute upsert_tx(9, 'tr', 'Issue with vehicle');
execute upsert_tx(10, 'tr', 'Break (button not pressed)');
execute upsert_tx(11, 'tr', 'Other reason');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
