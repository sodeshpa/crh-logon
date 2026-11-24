-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_pda_marketinfo.sql ----------'

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
-- tlgnr_pda_marketinfo_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_pda_marketinfo_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_pda_marketinfo_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_pda_marketinfo_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.pda_marketinfo_cd)
    into v_exists_flag
    from tlgnr_pda_marketinfo_bs bs
    where bs.pda_marketinfo_cd = p_pda_marketinfo_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_pda_marketinfo_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_pda_marketinfo_bs set
/* pda_marketinfo_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          pda_marketinfo_cd = p_pda_marketinfo_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_pda_marketinfo_cd || ').');
      end if;
    else
      insert into tlgnr_pda_marketinfo_bs (
        pda_marketinfo_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_pda_marketinfo_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_pda_marketinfo_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_pda_marketinfo_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_pda_marketinfo_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_pda_marketinfo_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_pda_marketinfo_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.pda_marketinfo_cd) into v_exists_bs_flag
    from tlgnr_pda_marketinfo_bs bs
    where bs.pda_marketinfo_cd = p_pda_marketinfo_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.pda_marketinfo_cd) into v_exists_flag
      from tlgnr_pda_marketinfo_tx tx
      where tx.pda_marketinfo_cd = p_pda_marketinfo_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_pda_marketinfo_tx set
/* p_pda_marketinfo_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          pda_marketinfo_cd = p_pda_marketinfo_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_pda_marketinfo_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_pda_marketinfo_tx (
          pda_marketinfo_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_pda_marketinfo_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_pda_marketinfo_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_pda_marketinfo_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_PDA_MARKETINFO_BS
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);
execute upsert_bs(5, 1);
execute upsert_bs(6, 1);
execute upsert_bs(7, 1);

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - EN
execute upsert_tx(1, 'en', 'Customer wants sales contact');
execute upsert_tx(2, 'en', 'Got interesting information from customer');
execute upsert_tx(3, 'en', 'Customer complaint - Material/Application');
execute upsert_tx(4, 'en', 'Customer complaint - other');
execute upsert_tx(5, 'en', 'Competitor at customer site');
execute upsert_tx(6, 'en', 'Potential construction site sighted');
execute upsert_tx(7, 'en', 'Other (please contact the driver)');

prompt INSERTING into TLGNR_PDA_ERROR_MESSAGE_TX - DE
execute upsert_tx(1, 'de', 'Kunde wünscht Verkaufskontakt');
execute upsert_tx(2, 'de', 'Interessante Information von Kunde erhalten');
execute upsert_tx(3, 'de', 'Kundenreklamation - Produkt/Anwendung');
execute upsert_tx(4, 'de', 'Kundenreklamation - andere');
execute upsert_tx(5, 'de', 'Mitbewerber auf Platz');
execute upsert_tx(6, 'de', 'Potenzielle Baustelle gesichtet');
execute upsert_tx(7, 'de', 'Andere (bitte Kontakt mit Fahrer aufnehmen)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - FR
execute upsert_tx(1, 'fr', 'Le client souhaite rendez-vous avec contact de ventes');
execute upsert_tx(2, 'fr', 'Informations intéressants du client, obtenues');
execute upsert_tx(3, 'fr', 'Réclamation du client - Matériel/Application');
execute upsert_tx(4, 'fr', 'Réclamation du client - autre');
execute upsert_tx(5, 'fr', 'Concurrent sur le site du client');
execute upsert_tx(6, 'fr', 'Chantier de construction potentiel, aperçu');
execute upsert_tx(7, 'fr', 'Autre (veuillez contacter le conducteur)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - IT
execute upsert_tx(1, 'it', 'Il cliente vuole un contatto con le vendite');
execute upsert_tx(2, 'it', 'Interessante informazione dal cliente');
execute upsert_tx(3, 'it', 'Reclamo cliente - Materiale/Applicazione');
execute upsert_tx(4, 'it', 'Reclamo cliente - altro');
execute upsert_tx(5, 'it', 'Concorrente al cantiere del cliente');
execute upsert_tx(6, 'it', 'Potenziale cantiere in avviamento avvistato');
execute upsert_tx(7, 'it', 'Altro (prego contattare l''autista)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - ES
execute upsert_tx(1, 'es', 'El cliente desea contactos de ventas');
execute upsert_tx(2, 'es', 'Obtener información de interés del Cliente');
execute upsert_tx(3, 'es', 'Reclamaciones de clientes - Producto/Aplicación');
execute upsert_tx(4, 'es', 'Reclamaciones de clientes - Otras');
execute upsert_tx(5, 'es', 'Competencia donde se encuentra el cliente');
execute upsert_tx(6, 'es', 'Localización potencial de construcción avistada');
execute upsert_tx(7, 'es', 'Otro (póngase en contacto con el conductor)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - CS (ENGLISH COPY)
execute upsert_tx(1, 'cs', 'Customer wants sales contact');
execute upsert_tx(2, 'cs', 'Got interesting information from customer');
execute upsert_tx(3, 'cs', 'Customer complaint - Material/Application');
execute upsert_tx(4, 'cs', 'Customer complaint - other');
execute upsert_tx(5, 'cs', 'Competitor at customer site');
execute upsert_tx(6, 'cs', 'Potential construction site sighted');
execute upsert_tx(7, 'cs', 'Other (please contact the driver)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - SK
execute upsert_tx(1, 'sk', 'Zákazník chce predajný kontrakt');
execute upsert_tx(2, 'sk', 'Máme zaujímavé informácie o zákazníkovi');
execute upsert_tx(3, 'sk', 'Reklamácia zákazníka - materiál/použitie');
execute upsert_tx(4, 'sk', 'Reklamácia zákazníka - iné');
execute upsert_tx(5, 'sk', 'Konkurencia na strane zákazníka');
execute upsert_tx(6, 'sk', 'Predpokladané umiestnenie staveniska');
execute upsert_tx(7, 'sk', 'Iné (prosím kontaktujte vodiča)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - HU
execute upsert_tx(1, 'hu', 'Ügyfél Sales contract igénye');
execute upsert_tx(2, 'hu', 'Értékes információ az ügyféltől');
execute upsert_tx(3, 'hu', 'Ügyfél panasz - Anyag / Alkalmazás');
execute upsert_tx(4, 'hu', 'Ügyfél panasz - egyéb');
execute upsert_tx(5, 'hu', 'Versenytárs az ügyfél telephelyén');
execute upsert_tx(6, 'hu', 'Potenciális építési project ');
execute upsert_tx(7, 'hu', 'Egyéb ( kérjük forduljon a vezetőhöz)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - RO
execute upsert_tx(1, 'ro', 'Clientul vrea contract de vanzari');
execute upsert_tx(2, 'ro', 'Informatii interesante primite de la client');
execute upsert_tx(3, 'ro', 'Reclamatie client - produs/aplicatie');
execute upsert_tx(4, 'ro', 'Reclamatie client - altele');
execute upsert_tx(5, 'ro', 'Concurenta in locatia clientului');
execute upsert_tx(6, 'ro', 'Potential santier deficiente de reperare');
execute upsert_tx(7, 'ro', 'Altele (va rugam contactati soferul)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - BG
execute upsert_tx(1, 'bg', 'Клиентът иска договор за продажби');
execute upsert_tx(2, 'bg', 'Получаване на интересна информация от клиента');
execute upsert_tx(3, 'bg', 'Оплакване на клиента - Материал/Молба');
execute upsert_tx(4, 'bg', 'Оплакване на клиента-друго');
execute upsert_tx(5, 'bg', 'Конкурент на клиента');
execute upsert_tx(6, 'bg', 'Потенциална строителна площадка');
execute upsert_tx(7, 'bg', 'Друго (моля свържете се с шофьора)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - RU (ENGLISH COPY)
execute upsert_tx(1, 'ru', 'Клиент хочет заключить договор на поставку');
execute upsert_tx(2, 'ru', 'Есть интересная информация от клиентов');
execute upsert_tx(3, 'ru', 'Обращение клиента - Материал\Применение');
execute upsert_tx(4, 'ru', 'Обращение клиента - другое');
execute upsert_tx(5, 'ru', 'Конкурент на территории клиента');
execute upsert_tx(6, 'ru', 'Потенциальная территория конкурента обнаружена');
execute upsert_tx(7, 'ru', 'Другое (пожалуйста, свяжитесь с водителем)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - PL
execute upsert_tx(1, 'pl', 'Klient chce umowę sprzedaży');
execute upsert_tx(2, 'pl', 'Zdobyte ciekawe informacje od klienta');
execute upsert_tx(3, 'pl', 'Reklamacja klienta - Materiał / Aplikacja');
execute upsert_tx(4, 'pl', 'Reklamacja klienta - inne');
execute upsert_tx(5, 'pl', 'Konkurent na terenie klienta');
execute upsert_tx(6, 'pl', 'Potencjalna konstrukcja terenu ');
execute upsert_tx(7, 'pl', 'Inne (prosimy o kontakt z kierowcą)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - AZ (ENGLISH COPY)
execute upsert_tx(1, 'az', 'Customer wants sales contact');
execute upsert_tx(2, 'az', 'Got interesting information from customer');
execute upsert_tx(3, 'az', 'Customer complaint - Material/Application');
execute upsert_tx(4, 'az', 'Customer complaint - other');
execute upsert_tx(5, 'az', 'Competitor at customer site');
execute upsert_tx(6, 'az', 'Potential construction site sighted');
execute upsert_tx(7, 'az', 'Other (please contact the driver)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - HR
execute upsert_tx(1, 'hr', 'Kupac želi prodajni ugovor');
execute upsert_tx(2, 'hr', 'Dobio zanimljive informacije od kupca');
execute upsert_tx(3, 'hr', 'Prigovor kupca - materijal/primjena');
execute upsert_tx(4, 'hr', 'Prigovor kupca - ostalo');
execute upsert_tx(5, 'hr', 'Konkurent na strani kupca');
execute upsert_tx(6, 'hr', 'Procijenjeno potencijalno gradilište');
execute upsert_tx(7, 'hr', 'Ostalo (molim kontaktirajte vozača)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - SR
execute upsert_tx(1, 'sr', 'Kupac želi ugovor o prodaji');
execute upsert_tx(2, 'sr', 'Kupac prosledio informaciju');
execute upsert_tx(3, 'sr', 'Kupčeva žalba - Materijal/Aplikacija');
execute upsert_tx(4, 'sr', 'Kupčeva žalba - Ostalo');
execute upsert_tx(5, 'sr', 'Konkurencija kod kupca');
execute upsert_tx(6, 'sr', 'Potencijalno gradilište na tržištu');
execute upsert_tx(7, 'sr', 'Ostalo (molimo kontaktirajte vozača)');

prompt INSERTING into TLGNR_PDA_MARKETINFO_TX - TR
execute upsert_tx(1, 'tr', 'Customer wants sales contact');
execute upsert_tx(2, 'tr', 'Got interesting information from customer');
execute upsert_tx(3, 'tr', 'Customer complaint - Material/Application');
execute upsert_tx(4, 'tr', 'Customer complaint - other');
execute upsert_tx(5, 'tr', 'Competitor at customer site');
execute upsert_tx(6, 'tr', 'Potential construction site sighted');
execute upsert_tx(7, 'tr', 'Other (please contact the driver)');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

