-- $Revision$
-- $Date$
--
-- initial data from
-- file: https://aww.adnovum.ch/jira/secure/attachment/50674/SAP+Returns+Reason+Codes.jpg

prompt '---------- refdata/tlgnr_return_order_reason.sql ----------'
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
-- tlgnr_return_order_reason_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_return_order_reason_cd    in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_return_order_reason_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)
  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_return_order_reason_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');
    select count(bs.return_order_reason_cd)
    into v_exists_flag
    from tlgnr_return_order_reason_bs bs
    where bs.return_order_reason_cd = p_return_order_reason_cd;
    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_return_order_reason_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_return_order_reason_bs set
/* return_order_reason_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          return_order_reason_cd = p_return_order_reason_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_return_order_reason_cd || ').');
      end if;
    else
      insert into tlgnr_return_order_reason_bs (
        return_order_reason_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_return_order_reason_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_return_order_reason_cd || ').');
    end if;
  end upsert_bs;
/
show errors;
-------------------------------------------------------------------------------
-- tlgnr_return_order_reason_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_return_order_reason_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_return_order_reason_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records
  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_return_order_reason_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');
----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.return_order_reason_cd) into v_exists_bs_flag
    from tlgnr_return_order_reason_bs bs
    where bs.return_order_reason_cd = p_return_order_reason_cd;
    if (v_exists_bs_flag > 0) then
      select count(tx.return_order_reason_cd) into v_exists_flag
      from tlgnr_return_order_reason_tx tx
      where tx.return_order_reason_cd = p_return_order_reason_cd and tx.lang_cd = p_lang_cd;
      if (v_exists_flag > 0) then
        update tlgnr_return_order_reason_tx set
/* p_return_order_reason_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          return_order_reason_cd = p_return_order_reason_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_return_order_reason_cd || ', LANG=' || p_lang_cd || ').');
      else
        insert into tlgnr_return_order_reason_tx (
          return_order_reason_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_return_order_reason_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_return_order_reason_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_return_order_reason_cd || ').');
    end if;
  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_BS
execute upsert_bs('Z00', 1);
execute upsert_bs('Z01', 1);
execute upsert_bs('Z02', 1);
execute upsert_bs('Z03', 1);
execute upsert_bs('Z04', 1);
execute upsert_bs('Z05', 1);
execute upsert_bs('Z06', 1);
execute upsert_bs('Z07', 1);
execute upsert_bs('Z08', 1);
execute upsert_bs('Z09', 1);
execute upsert_bs('Z10', 1);
execute upsert_bs('Z11', 1);
execute upsert_bs('Z12', 1);
execute upsert_bs('Z13', 1);
execute upsert_bs('Z14', 1);
execute upsert_bs('Z15', 1);

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - EN
execute upsert_tx('Z00', 'en', 'Pricing Revision');
execute upsert_tx('Z01', 'en', 'Quantity restriction');
execute upsert_tx('Z02', 'en', 'No quantity restriction');
execute upsert_tx('Z03', 'en', 'Rerouted customer refusal');
execute upsert_tx('Z04', 'en', 'Rerouted cancelation');
execute upsert_tx('Z05', 'en', 'Voluntary rerouting');
execute upsert_tx('Z06', 'en', 'Counter error');
execute upsert_tx('Z07', 'en', 'Loading problem');
execute upsert_tx('Z08', 'en', 'Unloading problem');
execute upsert_tx('Z09', 'en', 'Return Orders not Conform');
execute upsert_tx('Z10', 'en', 'Return refusal customer');
execute upsert_tx('Z11', 'en', 'Voluntary return');
execute upsert_tx('Z12', 'en', 'Planning error');
execute upsert_tx('Z13', 'en', 'Price error');
execute upsert_tx('Z14', 'en', 'Commercial Settlement');
execute upsert_tx('Z15', 'en', 'Problem Product Quality');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - DE (ENGLISH COPY)
execute upsert_tx('Z00', 'de', 'Preisänderung');
execute upsert_tx('Z01', 'de', 'Mengenbeschränkung');
execute upsert_tx('Z02', 'de', 'Keine Mengenbeschränkung');
execute upsert_tx('Z03', 'de', 'Umgeleitete Kundenablehnung');
execute upsert_tx('Z04', 'de', 'Umgeleitete Abbestellung');
execute upsert_tx('Z05', 'de', 'Freiwillige Umleitung');
execute upsert_tx('Z06', 'de', 'Abrechnungsfehler');
execute upsert_tx('Z07', 'de', 'Beladeproblem');
execute upsert_tx('Z08', 'de', 'Entladeproblem');
execute upsert_tx('Z09', 'de', 'Rückmengen nicht korrekt');
execute upsert_tx('Z10', 'de', 'Rückmenge Kund. abgelehnt');
execute upsert_tx('Z11', 'de', 'Freiwillige Rückmenge');
execute upsert_tx('Z12', 'de', 'Planungsfehler');
execute upsert_tx('Z13', 'de', 'Preisfehler');
execute upsert_tx('Z14', 'de', 'Zahlungsbedingungen');
execute upsert_tx('Z15', 'de', 'Produktqual. mangelhaft');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - FR (ENGLISH COPY)
execute upsert_tx('Z00', 'fr', 'Révision des prix');
execute upsert_tx('Z01', 'fr', 'Restr. niveau quantités');
execute upsert_tx('Z02', 'fr', 'Pas de restrs. sur quant.');
execute upsert_tx('Z03', 'fr', 'Rerout. refusé par client');
execute upsert_tx('Z04', 'fr', 'Annulation du reroutage');
execute upsert_tx('Z05', 'fr', 'Reroutage volontaire');
execute upsert_tx('Z06', 'fr', 'Erreur guichet');
execute upsert_tx('Z07', 'fr', 'Problème de chargement');
execute upsert_tx('Z08', 'fr', 'Problème de déchargement');
execute upsert_tx('Z09', 'fr', 'Commandes ret. non conf.');
execute upsert_tx('Z10', 'fr', 'Retour refusé par client');
execute upsert_tx('Z11', 'fr', 'Retour volontaire');
execute upsert_tx('Z12', 'fr', 'Erreur de planification');
execute upsert_tx('Z13', 'fr', 'Erreur de prix');
execute upsert_tx('Z14', 'fr', 'Règlement commercial');
execute upsert_tx('Z15', 'fr', 'Problème qualité de prod.');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - IT (ENGLISH COPY)
execute upsert_tx('Z00', 'it', 'Revisione Prezzo');
execute upsert_tx('Z01', 'it', 'Limitazione Quantità');
execute upsert_tx('Z02', 'it', 'Nessuna limitazione qtà');
execute upsert_tx('Z03', 'it', 'Resi da clienti deviati');
execute upsert_tx('Z04', 'it', 'Deviazioni cancellate');
execute upsert_tx('Z05', 'it', 'Rerouting volontario');
execute upsert_tx('Z06', 'it', 'Contatore errori');
execute upsert_tx('Z07', 'it', 'Problema di carico');
execute upsert_tx('Z08', 'it', 'Problema di scarico');
execute upsert_tx('Z09', 'it', 'Ordini reso non conformi');
execute upsert_tx('Z10', 'it', 'Resi da rifiuto clienti');
execute upsert_tx('Z11', 'it', 'Reso volontario');
execute upsert_tx('Z12', 'it', 'Errore pianificazione');
execute upsert_tx('Z13', 'it', 'Errore prezzo');
execute upsert_tx('Z14', 'it', 'Transazione commerciale');
execute upsert_tx('Z15', 'it', 'Problema qualità prodotto');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - ES (ENGLISH COPY)
execute upsert_tx('Z00', 'es', 'Revisión precios');
execute upsert_tx('Z01', 'es', 'Restricción cantidad');
execute upsert_tx('Z02', 'es', 'Sin restricción cantidad');
execute upsert_tx('Z03', 'es', 'Cambio ruta rechazo clnte');
execute upsert_tx('Z04', 'es', 'Cambio ruta cancelada');
execute upsert_tx('Z05', 'es', 'Cambio ruta voluntario');
execute upsert_tx('Z06', 'es', 'Error de cuenta');
execute upsert_tx('Z07', 'es', 'Problem en carga');
execute upsert_tx('Z08', 'es', 'Problema en descarga');
execute upsert_tx('Z09', 'es', 'Pedidos dev. no conformes');
execute upsert_tx('Z10', 'es', 'Rechazo devolución clnte');
execute upsert_tx('Z11', 'es', 'Devolución voluntaria');
execute upsert_tx('Z12', 'es', 'Erro de planificación');
execute upsert_tx('Z13', 'es', 'Error de precio');
execute upsert_tx('Z14', 'es', 'Acuerdo comercial');
execute upsert_tx('Z15', 'es', 'Problema calidad producto');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - CS (ENGLISH COPY)
execute upsert_tx('Z00', 'cs', 'Pricing Revision');
execute upsert_tx('Z01', 'cs', 'Quantity restriction');
execute upsert_tx('Z02', 'cs', 'No quantity restriction');
execute upsert_tx('Z03', 'cs', 'Rerouted customer refusal');
execute upsert_tx('Z04', 'cs', 'Rerouted cancelation');
execute upsert_tx('Z05', 'cs', 'Volumentary rerouting');
execute upsert_tx('Z06', 'cs', 'Counter error');
execute upsert_tx('Z07', 'cs', 'Loading problem');
execute upsert_tx('Z08', 'cs', 'Unloading problem');
execute upsert_tx('Z09', 'cs', 'Return Orders not Conform');
execute upsert_tx('Z10', 'cs', 'Return refusal customer');
execute upsert_tx('Z11', 'cs', 'Voluntary return');
execute upsert_tx('Z12', 'cs', 'Planning error');
execute upsert_tx('Z13', 'cs', 'Price error');
execute upsert_tx('Z14', 'cs', 'Commercial Settlement');
execute upsert_tx('Z15', 'cs', 'Problem Product Quality');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - SK (ENGLISH COPY)
execute upsert_tx('Z00', 'sk', 'Revízia cenotvorby');
execute upsert_tx('Z01', 'sk', 'Množstevné obmedzenie');
execute upsert_tx('Z02', 'sk', 'žiadne množst. obmedz.');
execute upsert_tx('Z03', 'sk', 'Odmietnutie presm. zákaz.');
execute upsert_tx('Z04', 'sk', 'Presmerovanie zrušené');
execute upsert_tx('Z05', 'sk', 'Dobrovoľné presmerovanie');
execute upsert_tx('Z06', 'sk', 'Chybné počítadlo');
execute upsert_tx('Z07', 'sk', 'Problém na nakládke');
execute upsert_tx('Z08', 'sk', 'Problém na vykládke');
execute upsert_tx('Z09', 'sk', 'Vrátené obj. nie sú potv.');
execute upsert_tx('Z10', 'sk', 'Vrátenie odm. zákazníka');
execute upsert_tx('Z11', 'sk', 'Dobrovoľný návrat');
execute upsert_tx('Z12', 'sk', 'Chyba plánovania');
execute upsert_tx('Z13', 'sk', 'Chyba ceny');
execute upsert_tx('Z14', 'sk', 'Obchodné vysporiadanie');
execute upsert_tx('Z15', 'sk', 'Problém kvality výrobku');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - HU (ENGLISH COPY)
execute upsert_tx('Z00', 'hu', 'Ár módosítás');
execute upsert_tx('Z01', 'hu', 'Mennyiségi korlátozás');
execute upsert_tx('Z02', 'hu', 'Nincs mennyis. korlátozás');
execute upsert_tx('Z03', 'hu', 'Ügyfél miatt árirányítás');
execute upsert_tx('Z04', 'hu', 'Törlés miatt átirányítás');
execute upsert_tx('Z05', 'hu', 'Szándékos átirányítás');
execute upsert_tx('Z06', 'hu', 'Számláló hiba');
execute upsert_tx('Z07', 'hu', 'Töltési hiba');
execute upsert_tx('Z08', 'hu', 'Lerakodási hiba');
execute upsert_tx('Z09', 'hu', 'Return nem megfel. miatt');
execute upsert_tx('Z10', 'hu', 'Return ügyfél elut. miatt');
execute upsert_tx('Z11', 'hu', 'Szándékos return');
execute upsert_tx('Z12', 'hu', 'Tervezési hiba');
execute upsert_tx('Z13', 'hu', 'Ár hiba');
execute upsert_tx('Z14', 'hu', 'Kereskedelmi megállapodás');
execute upsert_tx('Z15', 'hu', 'Termék minőségi probléma');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - RO (ENGLISH COPY)
execute upsert_tx('Z00', 'ro', 'Revizuire preturi');
execute upsert_tx('Z01', 'ro', 'Restrictie de cantitate');
execute upsert_tx('Z02', 'ro', 'Nicio restricitie cant.');
execute upsert_tx('Z03', 'ro', 'Redirect. refuzata client');
execute upsert_tx('Z04', 'ro', 'Redirectionare anulata');
execute upsert_tx('Z05', 'ro', 'Redirectionare voluntara');
execute upsert_tx('Z06', 'ro', 'Eroare numaratoare');
execute upsert_tx('Z07', 'ro', 'Probleme la incarcare');
execute upsert_tx('Z08', 'ro', 'Probleme la descarcare');
execute upsert_tx('Z09', 'ro', 'Retur comanda neconforma');
execute upsert_tx('Z10', 'ro', 'Retur refuz client');
execute upsert_tx('Z11', 'ro', 'Retur voluntar');
execute upsert_tx('Z12', 'ro', 'Eroare planificare');
execute upsert_tx('Z13', 'ro', 'Eroare pret');
execute upsert_tx('Z14', 'ro', 'Solutionare comerciala');
execute upsert_tx('Z15', 'ro', 'Problema calitate produs');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - BG (ENGLISH COPY)
execute upsert_tx('Z00', 'bg', 'Редакция ценообразув.');
execute upsert_tx('Z01', 'bg', 'Ограничение количеството');
execute upsert_tx('Z02', 'bg', 'Без ограничение количес.');
execute upsert_tx('Z03', 'bg', 'Отказ пренасочен клиент');
execute upsert_tx('Z04', 'bg', 'Пренасочването анулирано');
execute upsert_tx('Z05', 'bg', 'Доброволно пренасочване');
execute upsert_tx('Z06', 'bg', 'Грешка в преброяване');
execute upsert_tx('Z07', 'bg', 'Проблем при товаренето');
execute upsert_tx('Z08', 'bg', 'Проблем при разтоварв.');
execute upsert_tx('Z09', 'bg', 'Несъ. на върнат. поръчка');
execute upsert_tx('Z10', 'bg', 'Върнат отказ на клиент');
execute upsert_tx('Z11', 'bg', 'Доброволно връщане');
execute upsert_tx('Z12', 'bg', 'Грешка при планирането');
execute upsert_tx('Z13', 'bg', 'Грешка ценообразуването');
execute upsert_tx('Z14', 'bg', 'Търговско споразумение');
execute upsert_tx('Z15', 'bg', 'Проблем качест. продукта');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - RU (ENGLISH COPY)
execute upsert_tx('Z00', 'ru', 'Пересмотр цены');
execute upsert_tx('Z01', 'ru', 'Ограничение количества');
execute upsert_tx('Z02', 'ru', 'Нет количест. ограничения');
execute upsert_tx('Z03', 'ru', 'Переадр., отказ клиента');
execute upsert_tx('Z04', 'ru', 'Переадресация, отказ');
execute upsert_tx('Z05', 'ru', 'Добров. изм. маршрута');
execute upsert_tx('Z06', 'ru', 'Счетчик ошибок');
execute upsert_tx('Z07', 'ru', 'Проблема загрузки');
execute upsert_tx('Z08', 'ru', 'Проблема выгрузки');
execute upsert_tx('Z09', 'ru', 'Возврат заказов не подт.');
execute upsert_tx('Z10', 'ru', 'возврат отказа клиента');
execute upsert_tx('Z11', 'ru', 'Добровольный возврат');
execute upsert_tx('Z12', 'ru', 'Ошибка планирования');
execute upsert_tx('Z13', 'ru', 'Ценовая ошибка');
execute upsert_tx('Z14', 'ru', 'Коммерческое соглашение');
execute upsert_tx('Z15', 'ru', 'Проблема качества прод.');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - PL (ENGLISH COPY)
execute upsert_tx('Z00', 'pl', 'Edycja cen');
execute upsert_tx('Z01', 'pl', 'Ograniczenie ilości');
execute upsert_tx('Z02', 'pl', 'Brak ograniczenia ilości');
execute upsert_tx('Z03', 'pl', 'Przekier. odmowa klienta');
execute upsert_tx('Z04', 'pl', 'Przekierowane anulowanie');
execute upsert_tx('Z05', 'pl', 'Dobrowolne przekierowanie');
execute upsert_tx('Z06', 'pl', 'Licznik błędów');
execute upsert_tx('Z07', 'pl', 'Problem z ładowaniem');
execute upsert_tx('Z08', 'pl', 'Problem z rozładunkiem');
execute upsert_tx('Z09', 'pl', 'Pow. Zamówienia niezgodny');
execute upsert_tx('Z10', 'pl', 'Powrót odmowy klienta');
execute upsert_tx('Z11', 'pl', 'Dobrowolny powrót');
execute upsert_tx('Z12', 'pl', 'Błąd planowania');
execute upsert_tx('Z13', 'pl', 'Błąd ceny');
execute upsert_tx('Z14', 'pl', 'Ugoda handlowa');
execute upsert_tx('Z15', 'pl', 'Probl. z jakością produk.');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - AZ (ENGLISH COPY)
execute upsert_tx('Z00', 'az', 'Qiymətə yenidən baxılması');
execute upsert_tx('Z01', 'az', 'Kəmiyyət məhdudiyyəti');
execute upsert_tx('Z02', 'az', 'Kəmiyyət məhdudiyyəti yoxdur');
execute upsert_tx('Z03', 'az', 'Yönəldilmiş müştəri imtinası');
execute upsert_tx('Z04', 'az', 'Yönəldilmiş ləğvetmə');
execute upsert_tx('Z05', 'az', 'Könüllü yönəltmə');
execute upsert_tx('Z06', 'az', 'Sayğac səhvi');
execute upsert_tx('Z07', 'az', 'Yükləmə problemi');
execute upsert_tx('Z08', 'az', 'Yükboşaltma problemi');
execute upsert_tx('Z09', 'az', 'Sifariş qayıdışı uyğun deyil');
execute upsert_tx('Z10', 'az', 'Müştərinin geriyə imtinası');
execute upsert_tx('Z11', 'az', 'Könüllü qaytarma');
execute upsert_tx('Z12', 'az', 'Planlaşdırma səhvi');
execute upsert_tx('Z13', 'az', 'Qiymət səhvi');
execute upsert_tx('Z14', 'az', 'Kommersiya hesablaşması');
execute upsert_tx('Z15', 'az', 'Problemli məhsul keyfiyyəti');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - HR (ENGLISH COPY)
execute upsert_tx('Z00', 'hr', 'Revizija cijene');
execute upsert_tx('Z01', 'hr', 'Količine ograničene');
execute upsert_tx('Z02', 'hr', 'Nema ogranič. količina');
execute upsert_tx('Z03', 'hr', 'Preusmjer. kupca odbij.');
execute upsert_tx('Z04', 'hr', 'Poništavanje preusmj.');
execute upsert_tx('Z05', 'hr', 'Dobrovoljno preusmj.');
execute upsert_tx('Z06', 'hr', 'Cjenovna pogreška');
execute upsert_tx('Z07', 'hr', 'Problem kod ukrcaja');
execute upsert_tx('Z08', 'hr', 'Problem kod iskrcaja');
execute upsert_tx('Z09', 'hr', 'Vraćeni nal. nisu potvr.');
execute upsert_tx('Z10', 'hr', 'Povratak odbijanja kupca');
execute upsert_tx('Z11', 'hr', 'Dobrovoljno vraćanje');
execute upsert_tx('Z12', 'hr', 'Pogreška u planiranju');
execute upsert_tx('Z13', 'hr', 'Cjenovna pogreška');
execute upsert_tx('Z14', 'hr', 'Trgovinsko rješenje');
execute upsert_tx('Z15', 'hr', 'Problem kvalitete proiz.');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - SR (ENGLISH COPY)
execute upsert_tx('Z00', 'sr', 'Problem kvalitete proiz.');
execute upsert_tx('Z01', 'sr', 'Ograničenja u količini');
execute upsert_tx('Z02', 'sr', 'Nema ograničenja količ. ');
execute upsert_tx('Z03', 'sr', 'Prekartir. odbio kupac');
execute upsert_tx('Z04', 'sr', 'Otkazano prekartiravanje');
execute upsert_tx('Z05', 'sr', 'Dobrovoljno prekartirano');
execute upsert_tx('Z06', 'sr', 'Greška');
execute upsert_tx('Z07', 'sr', 'Problem u utovaru ');
execute upsert_tx('Z08', 'sr', 'Problem u istovaru ');
execute upsert_tx('Z09', 'sr', 'Povraćaj nije potvrđen');
execute upsert_tx('Z10', 'sr', 'Povraćaj odbio kupac');
execute upsert_tx('Z11', 'sr', 'Dobrovoljni povraćaj ');
execute upsert_tx('Z12', 'sr', 'Planirana greška');
execute upsert_tx('Z13', 'sr', 'Greška');
execute upsert_tx('Z14', 'sr', 'Komercijalni ugovor');
execute upsert_tx('Z15', 'sr', 'Problem kvalitetom');

prompt INSERTING into TLGNR_RETURN_ORDER_REASON_TX - TR
execute upsert_tx('Z00', 'tr', 'Pricing Revision');
execute upsert_tx('Z01', 'tr', 'Quantity restriction');
execute upsert_tx('Z02', 'tr', 'No quantity restriction');
execute upsert_tx('Z03', 'tr', 'Rerouted customer refusal');
execute upsert_tx('Z04', 'tr', 'Rerouted cancelation');
execute upsert_tx('Z05', 'tr', 'Voluntary rerouting');
execute upsert_tx('Z06', 'tr', 'Counter error');
execute upsert_tx('Z07', 'tr', 'Loading problem');
execute upsert_tx('Z08', 'tr', 'Unloading problem');
execute upsert_tx('Z09', 'tr', 'Return Orders not Conform');
execute upsert_tx('Z10', 'tr', 'Return refusal customer');
execute upsert_tx('Z11', 'tr', 'Voluntary return');
execute upsert_tx('Z12', 'tr', 'Planning error');
execute upsert_tx('Z13', 'tr', 'Price error');
execute upsert_tx('Z14', 'tr', 'Commercial Settlement');
execute upsert_tx('Z15', 'tr', 'Problem Product Quality');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
