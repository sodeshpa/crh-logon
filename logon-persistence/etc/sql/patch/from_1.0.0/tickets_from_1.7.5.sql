prompt ************************************************************************
prompt *** BEGIN CRHLOGON-1322 Add cust reference to email confirmation
prompt ************************************************************************

prompt *** Change ONLY relevant tlgnr_text_bs and tlgnr_text_tx entries for CRHLOGON-1322

-------BELOW COPIED OVER tlgnr_text.sql relevant rows. Since the tlgnr_text_tx and tlgnr_text_bs entries are not anymore the same as the ones in the  tlgnr_text.sql change only the relevant rows. See also CRHLOGON-1331
----------------------------------

-- $Revision$
-- $Date$
--
-- This is a half internal table:
-- - Extending the possible values requires code changes
-- - Changing the localized values possible without code changes, as long as number of placeholders stays the same.

--LOGON-4819: Added Spanish language support (2012.05.16)
--LOGON-5893: Replaced the text for pdf.deliverynote.deliverytype to one space in order to not show the text anymore (2012.11.22)

prompt '---------- refdata/tlgnr_text.sql ----------'

set define off;

-------------------------------------------------------------------------------
-- is_different_varchar2
-------------------------------------------------------------------------------
-- compares two varchars.
-- They are identical either if a) both are null, b) they are equal by "="
-- They are different if a) either is null and the other not, b) different by "="
create or replace function is_different_varchar2 (
  i_a in varchar2,   i_b in varchar2)
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
  i_a in number,   i_b in number)
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
-- tlgnr_text_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_text_cd                   in varchar2,   p_placeholder_count         in number,   p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_text_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_text_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.text_cd)
    into v_exists_flag
    from tlgnr_text_bs bs
    where bs.text_cd = p_text_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_text_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_text_bs set
/* text_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,           placeholder_count              = p_placeholder_count,           ctl_mod_uid                    = 1,           ctl_mod_ts                     = systimestamp
        where
          text_cd = p_text_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_text_cd || ').');
      end if;
    else
      insert into tlgnr_text_bs (
        text_cd,         placeholder_count,         ctl_act_cd,         ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_text_cd,         p_placeholder_count,         p_ctl_act_cd,         1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_text_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_text_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_text_cd in varchar2,   p_lang_cd                 in varchar2,   p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_text_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_text_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.text_cd) into v_exists_bs_flag
    from tlgnr_text_bs bs
    where bs.text_cd = p_text_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.text_cd) into v_exists_flag
      from tlgnr_text_tx tx
      where tx.text_cd = p_text_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_text_tx set
/* p_text_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,           ctl_mod_uid                    = 1,           ctl_mod_ts                     = systimestamp
        where
          text_cd = p_text_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_text_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_text_tx (
          text_cd,           lang_cd,           txt,           ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_text_cd,           p_lang_cd,           p_text,           1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_text_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_text_cd || ').');
    end if;

  end upsert_tx;
/
show errors;


prompt INSERTING into TLGNR_TEXT_BS
execute upsert_bs('order.confirm.email.message', 16, 1);

prompt INSERTING into TLGNR_TEXT_TX - EN
execute upsert_tx('order.confirm.email.message', 'en', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Many thanks for your order at CRH.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Order confirmation {0}:'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Your reference {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Delivery Time' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Delivery Location' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular CRH contact or sales representative.');

prompt INSERTING into TLGNR_TEXT_TX - DE
execute upsert_tx('order.confirm.email.message', 'de', 'Sehr geehrter Empfänger,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Vielen Dank für Ihre Bestellung bei CRH.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Auftragsbestätigung {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ihre Referenz {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Lieferzeit' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Lieferort' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Besteller:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Weitere Details zu Ihrem Auftrag sowie den aktuellen Auftragsstatus finden Sie unter https://lpdv-logon.crh.com' || chr(13) || chr(10) || chr(13) || chr(10) || 'mit freundlichen Grüssen,' || chr(13) || chr(10) || 'Ihre CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Diese Email wurde automatisch generiert. Eine direkte Antwort auf diese Email-Adresse ist nicht möglich. Bei Fragen wenden Sie sich bitte an Ihre Hotline oder den zuständigen Verkäufer.');

prompt INSERTING into TLGNR_TEXT_TX - FR
execute upsert_tx('order.confirm.email.message', 'fr', 'Cher destinataire,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Merci pour votre commande chez EQIOM.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Confirmation de commande {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Votre référence {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Délai de livraison' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Adresse Client' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Acheteur:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Des informations complémentaires ainsi que le statut actuel de votre commande sont disponibles sur https://lpdv-logon.crh.com' || chr(13) || chr(10) || chr(13) || chr(10) || 'Meilleures salutations,' || chr(13) || chr(10) || 'EQIOM' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Ce Mail a été généré automatiquement. Une réponse directe sur cette adresse E-mail n''''est pas possible. Pour toutes informations complémentaires, veuillez contacter votre représentant des ventes.');

prompt INSERTING into TLGNR_TEXT_TX - IT
execute upsert_tx('order.confirm.email.message', 'it', 'Gentile Destinatario,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Grazie mille per l''''ordine presso CRH.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Conferma d''''ordine {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Il vostro riferimento {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Tempo di consegna' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Destinazione Cliente' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordinato da:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ulteriori dettagli sul vostro ordine così come lo stato dello stesso può trovarli su https://lpdv-logon.crh.com' || chr(13) || chr(10) || chr(13) || chr(10) || 'Coi migliori saluti,' || chr(13) || chr(10) || 'La vostra CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Questo E-mail é stato generato automaticamente. Una riposta a questo indirizzo E-mail é impossibile. Per ulteriori informazioni prendere contatto direttamente con l''''Hotline o con il servizio di vendita.');

prompt INSERTING into TLGNR_TEXT_TX - ES
execute upsert_tx('order.confirm.email.message', 'es', 'Querido Cliente,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Muchas gracias por el pedido realizado a CRH.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Confirmación de pedido {0}:'|| chr(13) || chr(10) || chr(13) || chr(10) || 'Tu referencia {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Hora de Entrega' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.aaaa})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.aaaa})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Lugar de Entrega' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Pedido por:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Más detalles y el estado actual del pedido pueden encontrarse en la página web de LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Saludos cordiales,' || chr(13) || chr(10) || 'CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Este email ha sido creado automáticamente. No responder directamente a esta dirección de email. Para cualquier pregunta, por favor, contacte con su línea de Atención al Cliente CRH o con su Representante de Ventas.');

prompt INSERTING into TLGNR_TEXT_TX - CS
execute upsert_tx('order.confirm.email.message', 'cs', 'Vážený zákazníku,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Děkujeme za Vaši objednávku u CRH (Česko) a.s.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Potvrzení objednávky {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Vaše reference {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Čas dodání' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Místo dodání' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Objednáno:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Více detailů a status objednávky je možné najít na webové stránce LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S pozdravem,' || chr(13) || chr(10) || 'CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Tento e-mail byl vytvořen automaticky. Odpověď na tento e-mail není možná. V případě dotazů se prosím obraťte na svého obchodního zástupce nebo jiný CRH kontakt.');

prompt INSERTING into TLGNR_TEXT_TX - SK
execute upsert_tx('order.confirm.email.message', 'sk', 'Vážený zákazník,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ďakujeme za Vašu objednávku v CRHe.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Potvrdenie objednávky {0}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Tvoja referencia {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Dodacia lehota' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Miesto dodania' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Objednané:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Viac detailov a aktuálny status objednávky je na LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'S pozdravom,' || chr(13) || chr(10) || 'CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Tento email bol vytvorený automaticky. Priama odpoveď na tento email nie je možná. V prípade otázok volajte Vašu kontaktnú osobu v CRHe alebo obchodného zástupcu.');

prompt INSERTING into TLGNR_TEXT_TX - HU
execute upsert_tx('order.confirm.email.message', 'hu', 'Tisztelt Ügyfelünk,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Köszönjük szépen CRH-s megrendelését.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Megrendelés visszaigazolása {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Az Ön referenciája {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Szállítási idő' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kézbesítés /szállítási cím' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Megrendelte:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'További részletek és aktuális megrendelése állapotáról tájékozódni tud LOGON weboldalán.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Üdvözlettel,' || chr(13) || chr(10) || 'CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Ez az e-mailt automatikusan generálódott. Közvetlen üzenet erre az e-mail címre nem lehetséges. Kérdése esetén lépjen kapcsolatba az Ön CRH-s kapcsolattartójával vagy értéseksítő kollegáinkkal.');

prompt INSERTING into TLGNR_TEXT_TX - RO
execute upsert_tx('order.confirm.email.message', 'ro', 'Draga Client,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Va multumim pentru comanda dumneavoastra la CRH.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Confirmare comanda {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Referința ta {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Timp de livrare' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Locatie Livrare' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Comandat de catre:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Mai multe detalii si status-ul actual al comenzii pot fi gasite pe website-ul LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Cu respect,' || chr(13) || chr(10) || 'CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'Acest email a fost creat automat. Nu este posibil un raspuns direct la aceasta adresa de email. Daca aveti intrebari, va rugam sa sunati  persoana dumneavostra de contact de la CRH sau reprezentantul vanzari.');

prompt INSERTING into TLGNR_TEXT_TX - BG
execute upsert_tx('order.confirm.email.message', 'bg', 'Уважаеми клиент,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Много благодаря за вашата поръчка на Холсим.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Потвърждение на поръчка {0}:'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Вашата препоръка {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Време на доставяне' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Място на доставяне' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Поръчва се от:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Повече детайли и текущ статус на поръчка може да бъде открит на ЛОГОН сайта.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Поздрави,' || chr(13) || chr(10) || 'CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular CRH contact or sales representative.');

prompt INSERTING into TLGNR_TEXT_TX - RU
execute upsert_tx('order.confirm.email.message', 'ru', 'Уважаемый партнер,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Благодарим за размещенный заказ в CRH.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Заказ подтвержден {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ваша ссылка {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Время доставки' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Место доставки' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Заказ сделан (кем):' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Дополнительная информация и текущий статус заказа доступка на сайте LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'с уважением,' || chr(13) || chr(10) ||            'CRH' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Это письмо было создано автоматически. Прямой ответ на это сообщение невозможно. В случае возникновения вопросов, пожалуйста, свяжитесь по телефону с Вашим постоянным контактным лицом CRH или торговым представителем.');

prompt INSERTING into TLGNR_TEXT_TX - PL
execute upsert_tx('order.confirm.email.message', 'pl', 'Szanowni Państwo,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Dziękujemy za zamówienie w CRH.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Potwierdzenie zamówienia {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Twoje referencje {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Czas dostawy' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Miejsce dostawy' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Zamówione przez:' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Więcej szczegółów i aktualny status zamówienia można znaleźć na stronie internetowej LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Z poważaniem,' || chr(13) || chr(10) ||            'CRH' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Ten e-mail został utworzony automatycznie. Bezpośrednia odpowiedź na ten adres nie jest możliwa. W przypadku pytań prosimy o standardowy kontakt telefoniczny z CRH lub Państwa opiekunem handlowym.');

prompt INSERTING into TLGNR_TEXT_TX - AZ
execute upsert_tx('order.confirm.email.message', 'az', 'Hörmətli müştəri,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'CRH şirkətinə sifariş verdiyiniz üçün sizə təşəkkür edirik.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Sifarişin təsdiqi {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Sizin rəyiniz {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Çatdırılma vaxtı' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Çatdırılma yeri' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Sifarişi verən:' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Sifarişin təfərrüatları və cari statusu barədə LOGON veb səhifəsindən öyrənə bilərsiniz.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Hörmətlə,' || chr(13) || chr(10) ||            'CRH' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Bu e-mail avtomatik yaradılıb. Bu e-mail ünvanına birbaşa cavab mümkün deyil. Suallarınız yarandıqda, CRH daimi əlaqə nömrəsinə zəng edə və ya satış təmsilçisi ilə əlaqə saxlaya bilərsiniz.');

prompt INSERTING into TLGNR_TEXT_TX - HR
execute upsert_tx('order.confirm.email.message', 'hr', 'Poštovani kupci,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Puno hvala na vašoj narudžbi u CRHu.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Potvrda narudžbe {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Vaša referenca {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Vrijeme isporuke' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Mjesto isporuke' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Naručitelj:' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Više informacija i trenutni status narudžbe može se naći na web stranici LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Lijep pozdrav,' || chr(13) || chr(10) ||            'CRH' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Ovaj e-mail je stvoren automatski.Izravan odgovor na ovu adresu e-pošte nije moguć. U slučaju pitanja molimo nazvati svoj redoviti CRH kontakt ili prodajnog predstavnika.');

prompt INSERTING into TLGNR_TEXT_TX - SR
execute upsert_tx('order.confirm.email.message', 'sr', 'Poštovani,' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Zahvaljujemo se na Vašoj porudžbini.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Potvrda narudžbe {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ваш референтни {15}' || chr(13) || chr(10) || chr(13) || chr(10) ||            '{1}' || chr(13) || chr(10) ||            'Vreme isporuke' || chr(13) || chr(10) ||            '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) ||            '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Lokacija isporuke' || chr(13) || chr(10) ||            '  {4} {5}' || chr(13) || chr(10) ||            '  {6} {7}' || chr(13) || chr(10) ||            '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) ||            '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Poručio:' || chr(13) || chr(10) ||            '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Više informacija i trenutni status narudžbe može se naći na web stranici LOGON.' || chr(13) || chr(10) || chr(13) || chr(10) ||            'Srdačan pozdrav,' || chr(13) || chr(10) ||            'CRH' || chr(13) || chr(10) || chr(13) || chr(10) ||            '-------------------' || chr(13) || chr(10) ||            'Ovaj e-mail je kreiran automatski. Direktan odgovor na ovu adresu e-pošte nije moguć. U slučaju pitanja molimo pozvati CRH info telefon ili prodajnog predstavnika.');

prompt INSERTING into TLGNR_TEXT_TX - TR
execute upsert_tx('order.confirm.email.message', 'tr', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Many thanks for your order at CRH.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Order confirmation {0}:' || chr(13) || chr(10) || chr(13) || chr(10) || 'Your reference {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Delivery Time' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Delivery Location' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular CRH contact or sales representative.');

prompt INSERTING into TLGNR_TEXT_TX - NL
execute upsert_tx('order.confirm.email.message', 'nl', 'Dear Customer,' || chr(13) || chr(10) || chr(13) || chr(10) || 'Many thanks for your order at CRH.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Order confirmation {0}:'  || chr(13) || chr(10) || chr(13) || chr(10) || 'Your reference {15}' || chr(13) || chr(10) || chr(13) || chr(10) || '{1}' || chr(13) || chr(10) || 'Delivery Time' || chr(13) || chr(10) || '  {2,time,HH:mm}    ({2,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || '  {3,time,HH:mm}    ({3,date,EEEE dd.MM.yyyy})' || chr(13) || chr(10) || chr(13) || chr(10) || 'Delivery Location' || chr(13) || chr(10) || '  {4} {5}' || chr(13) || chr(10) || '  {6} {7}' || chr(13) || chr(10) || '  {8} {9}, {10}-{11} {12}' || chr(13) || chr(10) || '  {13}' || chr(13) || chr(10) || chr(13) || chr(10) || 'Ordered by:' || chr(13) || chr(10) || '  {14}' || chr(13) || chr(10) || chr(13) || chr(10) || 'More details and the current status of the order can be found on the LOGON website.' || chr(13) || chr(10) || chr(13) || chr(10) || 'Kind regards,' || chr(13) || chr(10) || 'CRH' || chr(13) || chr(10) || chr(13) || chr(10) || '-------------------' || chr(13) || chr(10) || 'This email was created automatically. A direct response to this email address is not possible. In case of questions please phone your regular CRH contact or sales representative.');
commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;


-------ABOVE COPIED OVER tlgnr_text.sql relevant rows. Since the tlgnr_text_tx and tlgnr_text_bs entries are not anymore the same as the ones in the  tlgnr_text.sql change only the relevant rows. See also CRHLOGON-1331
----------------------------------

prompt ************************************************************************
prompt *** END CRHLOGON-1322 Add cust reference to email confirmation
prompt ************************************************************************