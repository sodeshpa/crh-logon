-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_pda_error_message.sql ----------'

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
-- tlgnr_pda_error_message_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_pda_error_message_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_pda_error_message_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_pda_error_message_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.pda_error_message_cd)
    into v_exists_flag
    from tlgnr_pda_error_message_bs bs
    where bs.pda_error_message_cd = p_pda_error_message_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_pda_error_message_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_pda_error_message_bs set
/* pda_error_message_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          pda_error_message_cd = p_pda_error_message_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_pda_error_message_cd || ').');
      end if;
    else
      insert into tlgnr_pda_error_message_bs (
        pda_error_message_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_pda_error_message_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_pda_error_message_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_pda_error_message_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_pda_error_message_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_pda_error_message_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_pda_error_message_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.pda_error_message_cd) into v_exists_bs_flag
    from tlgnr_pda_error_message_bs bs
    where bs.pda_error_message_cd = p_pda_error_message_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.pda_error_message_cd) into v_exists_flag
      from tlgnr_pda_error_message_tx tx
      where tx.pda_error_message_cd = p_pda_error_message_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_pda_error_message_tx set
/* p_pda_error_message_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          pda_error_message_cd = p_pda_error_message_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_pda_error_message_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_pda_error_message_tx (
          pda_error_message_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_pda_error_message_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_pda_error_message_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_pda_error_message_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);
execute upsert_bs(5, 1);
execute upsert_bs(6, 1);
execute upsert_bs(7, 1);
execute upsert_bs(8, 1);

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - EN
execute upsert_tx(1, 'en', 'Customer unloading point in bad condition');
execute upsert_tx(2, 'en', 'Container defect');
execute upsert_tx(3, 'en', 'Wagon defect');
execute upsert_tx(4, 'en', 'Wagon hatch open');
execute upsert_tx(5, 'en', 'RFID at loading point is missing/defect');
execute upsert_tx(6, 'en', 'RFID at customer silo is missing/defect');
execute upsert_tx(7, 'en', 'RFID at container is missing/defect');
execute upsert_tx(8, 'en', 'RFID on railway wagon is missing/defect');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - DE
execute upsert_tx(1, 'de', 'Kundenentladestelle in schlechtem Zustand');
execute upsert_tx(2, 'de', 'Container defekt');
execute upsert_tx(3, 'de', 'Bahnwagen defekt');
execute upsert_tx(4, 'de', 'Bahnwagen Domdeckel offen');
execute upsert_tx(5, 'de', 'RFID-Chip an Ladestelle fehlt/defekt');
execute upsert_tx(6, 'de', 'RFID-Chip an Kundensilo fehlt/defekt');
execute upsert_tx(7, 'de', 'RFID-Chip an Container fehlt/defekt');
execute upsert_tx(8, 'de', 'RFID-Chip an Bahnwagen fehlt/defekt');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - FR
execute upsert_tx(1, 'fr', 'Lieu de livraison du client en de mauvais conditions');
execute upsert_tx(2, 'fr', 'Défaut de container');
execute upsert_tx(3, 'fr', 'Défaut de wagon');
execute upsert_tx(4, 'fr', 'Trappe de wagon ouverte');
execute upsert_tx(5, 'fr', 'RFID au lieu de déchargement manquant/défectueux');
execute upsert_tx(6, 'fr', 'RFID au silo du client manquant/défectueux');
execute upsert_tx(7, 'fr', 'RFID au container manquant/défectueux');
execute upsert_tx(8, 'fr', 'RFID au wagon manquant/défectueux');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - IT
execute upsert_tx(1, 'it', 'Punto di scarico del cliente in pessime condizioni');
execute upsert_tx(2, 'it', 'Container difettoso');
execute upsert_tx(3, 'it', 'Vagone difettoso');
execute upsert_tx(4, 'it', 'Portello del vagone difettoso');
execute upsert_tx(5, 'it', 'RFID al punto di carico difettoso');
execute upsert_tx(6, 'it', 'RFID al silo del cliente difettoso');
execute upsert_tx(7, 'it', 'RFID al container difettoso');
execute upsert_tx(8, 'it', 'RFID al vagone ferroviario difettoso');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - ES
execute upsert_tx(1, 'es', 'Punto de descarga del cliente en malas condiciones');
execute upsert_tx(2, 'es', 'Contenedor defectuoso');
execute upsert_tx(3, 'es', 'Vagón defectuoso');
execute upsert_tx(4, 'es', 'Escotilla del vagón abierta');
execute upsert_tx(5, 'es', 'RFID en Punto de carga falta/defectuoso');
execute upsert_tx(6, 'es', 'RFID en Silo de cliente falta/defectuoso');
execute upsert_tx(7, 'es', 'RFID en Contenedor falta/defectuoso');
execute upsert_tx(8, 'es', 'RFID en Vagón falta/defectuoso');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - CS
execute upsert_tx(1, 'cs', 'Místo vykládky ve špatném stavu');
execute upsert_tx(2, 'cs', 'Poškozený kontejner');
execute upsert_tx(3, 'cs', 'Poškozený vagón');
execute upsert_tx(4, 'cs', 'Otevřený poklop vagónu');
execute upsert_tx(5, 'cs', 'RFID chybí/je nefunkční na místě nakládky');
execute upsert_tx(6, 'cs', 'RFID chybí/je nefunkční na sile zákazníka');
execute upsert_tx(7, 'cs', 'RFID chybí/je nefunkční na kontejneru');
execute upsert_tx(8, 'cs', 'RFID chybí/je nefunkční na vagónu');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - SK
execute upsert_tx(1, 'sk', 'Zákazníkove miesto vykládky v zlom stave');
execute upsert_tx(2, 'sk', 'Kontajner pokazený');
execute upsert_tx(3, 'sk', 'Vagón pokazený');
execute upsert_tx(4, 'sk', 'Klapka na vagóne otvorená');
execute upsert_tx(5, 'sk', 'RFID na nakládke chýba/chybná');
execute upsert_tx(6, 'sk', 'RFID na zákazníkovom sile chýba/chybná');
execute upsert_tx(7, 'sk', 'RFID na kontajnery chýba/chybná');
execute upsert_tx(8, 'sk', 'RFID na vozni chýba/chybná');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - HU
execute upsert_tx(1, 'hu', 'A vevő lerakó helyének nem megfelelő kondíciója');
execute upsert_tx(2, 'hu', 'Konténer hiba');
execute upsert_tx(3, 'hu', 'Vagon hiba');
execute upsert_tx(4, 'hu', 'Vagon ajtaja nyitva');
execute upsert_tx(5, 'hu', 'RFID lerakó hely hiányzik/hibás');
execute upsert_tx(6, 'hu', 'RFID a vevő silója hiányzik/hibás');
execute upsert_tx(7, 'hu', 'RFID konténer hiányzik/hibás');
execute upsert_tx(8, 'hu', 'RFID vasúti vagon hiányzik/hibás');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - RO
execute upsert_tx(1, 'ro', 'Punct descarcare client in conditii precare');
execute upsert_tx(2, 'ro', 'Container defect');
execute upsert_tx(3, 'ro', 'Vagon defect');
execute upsert_tx(4, 'ro', 'Trapa vagon deschisa');
execute upsert_tx(5, 'ro', 'RFID la punctul de incarcare lipseste/este defect');
execute upsert_tx(6, 'ro', 'RFID la siloz client lipseste/este defect');
execute upsert_tx(7, 'ro', 'RFID la container lipseste/este defect');
execute upsert_tx(8, 'ro', 'RFID la vagon lipseste/este defect');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - BG
execute upsert_tx(1, 'bg', 'Мястото на разтоварване на клиента е в лошо състояние');
execute upsert_tx(2, 'bg', 'Дефектен контейнер');
execute upsert_tx(3, 'bg', 'Дефектен вагон');
execute upsert_tx(4, 'bg', 'Отворен люк на вагона');
execute upsert_tx(5, 'bg', 'Липсва/ или е дефектен RFID на мястото на товарене');
execute upsert_tx(6, 'bg', 'Липсва/или е дефектен RFID на клиентски силоз');
execute upsert_tx(7, 'bg', 'Липсва/или е дефектен RFID на контейнер');
execute upsert_tx(8, 'bg', 'Липсва/или е дефектен RFID на жп вагон');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'Плохое состояние пункта выгрузки у клиента');
execute upsert_tx(2, 'ru', 'неисправный контейнер');
execute upsert_tx(3, 'ru', 'неисправный вагон');
execute upsert_tx(4, 'ru', 'люк вагона открыт');
execute upsert_tx(5, 'ru', 'карта на пункте погрузке отсутствует\неисправна');
execute upsert_tx(6, 'ru', 'карта на силосе клиента отсутствует\неисправна');
execute upsert_tx(7, 'ru', 'карта на контейнере погрузке отсутствует\неисправна');
execute upsert_tx(8, 'ru', 'карта на погрузке вагона отсутствует\неисправна');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - PL
execute upsert_tx(1, 'pl', 'Rozładunek u klienta w złych warunkach');
execute upsert_tx(2, 'pl', 'Defekt pojemnika');
execute upsert_tx(3, 'pl', 'Defekt wagonu');
execute upsert_tx(4, 'pl', 'Właz wagonu otwarty');
execute upsert_tx(5, 'pl', 'Brak/defekt RFID w punkcie ładowania');
execute upsert_tx(6, 'pl', 'Brak /defekt RFID w silosie klienta');
execute upsert_tx(7, 'pl', 'Brak /defekt RFID w pojemniku');
execute upsert_tx(8, 'pl', 'Brak/defekt RFID w wagonie kolejowym');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'Customer unloading point in bad condition');
execute upsert_tx(2, 'az', 'Container defect');
execute upsert_tx(3, 'az', 'Wagon defect');
execute upsert_tx(4, 'az', 'Wagon hatch open');
execute upsert_tx(5, 'az', 'RFID at loading point is missing/defect');
execute upsert_tx(6, 'az', 'RFID at customer silo is missing/defect');
execute upsert_tx(7, 'az', 'RFID at container is missing/defect');
execute upsert_tx(8, 'az', 'RFID on railway wagon is missing/defect');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - HR
execute upsert_tx(1, 'hr', 'Istovarno mjesto kupca u lošem stanju ');
execute upsert_tx(2, 'hr', 'Kontejner u kvaru');
execute upsert_tx(3, 'hr', 'Vagon u kvaru');
execute upsert_tx(4, 'hr', 'Vrata vagona otvorena');
execute upsert_tx(5, 'hr', 'RFID na utovaru nedostaje / kvar');
execute upsert_tx(6, 'hr', 'RFID na silosu kupca nedostaje / kvar');
execute upsert_tx(7, 'hr', 'RFID na kontejneru nedostaje / kvar');
execute upsert_tx(8, 'hr', 'RFID na željezničkom vagonu nedostaje / kvar');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - SR (ENGLISH COPY)
execute upsert_tx(1, 'sr', 'Mesto istovara u lošem stanju');
execute upsert_tx(2, 'sr', 'Container defect');
execute upsert_tx(3, 'sr', 'Wagon defect');
execute upsert_tx(4, 'sr', 'Wagon hatch open');
execute upsert_tx(5, 'sr', 'RFID at loading point is missing/defect');
execute upsert_tx(6, 'sr', 'RFID at customer silo is missing/defect');
execute upsert_tx(7, 'sr', 'RFID at container is missing/defect');
execute upsert_tx(8, 'sr', 'RFID on railway wagon is missing/defect');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - TR
execute upsert_tx(1, 'tr', 'Customer unloading point in bad condition');
execute upsert_tx(2, 'tr', 'Container defect');
execute upsert_tx(3, 'tr', 'Wagon defect');
execute upsert_tx(4, 'tr', 'Wagon hatch open');
execute upsert_tx(5, 'tr', 'RFID at loading point is missing/defect');
execute upsert_tx(6, 'tr', 'RFID at customer silo is missing/defect');
execute upsert_tx(7, 'tr', 'RFID at container is missing/defect');
execute upsert_tx(8, 'tr', 'RFID on railway wagon is missing/defect');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

