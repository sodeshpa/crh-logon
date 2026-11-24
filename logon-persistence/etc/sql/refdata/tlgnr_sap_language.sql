-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_sap_language.sql ----------'

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
-- tlgnr_sap_language_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_sap_lang_bs (
  p_sap_lang_cd           in varchar2,
  p_map_to_appl_lang_cd   in varchar2,
  p_ctl_act_cd            in number

) is
  v_exists_flag number;        -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);

    dbms_output.put_line('Insert or Update tlgnr_sap_language_bs (' || p_sap_lang_cd || ', ' || p_map_to_appl_lang_cd || ', ' || 'ctl_act_cd=' || p_ctl_act_cd || ')');

    ----------------------------------------------------------------------------
    -- Lookup tlgnr_sap_language_bs
    ----------------------------------------------------------------------------
    select count(bs.sap_language_cd) into v_exists_flag
    from tlgnr_sap_language_bs bs
    where bs.sap_language_cd = p_sap_lang_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating tlgnr_sap_language_bs with sap_lang_cd=' || p_sap_lang_cd || ' because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_sap_language_bs set
          /* sap_lang_cd: cannot update because this is the key */
          map_to_application_lang_cd = p_map_to_appl_lang_cd,
          ctl_act_cd = p_ctl_act_cd,
          ctl_mod_uid = 1,
          ctl_mod_ts = systimestamp
        where
          sap_language_cd = p_sap_lang_cd and
          (ctl_act_cd != p_ctl_act_cd  or
           1 = is_different_varchar2(map_to_application_lang_cd, p_map_to_appl_lang_cd))
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_sap_language_bs (CD=' || p_sap_lang_cd || ').');
      end if;
    else
      insert into tlgnr_sap_language_bs (
        sap_language_cd,
        map_to_application_lang_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_sap_lang_cd,
        p_map_to_appl_lang_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_sap_language_bs (CD=' || p_sap_lang_cd || ').');
    end if;

  end upsert_sap_lang_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_sap_language_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_sap_lang_tx (
  p_sap_lang_cd   in varchar2,
  p_lang_cd       in varchar2,
  p_text          in varchar2

) is
  v_exists_flag    number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);

    dbms_output.put_line('Insert or Update tlgnr_sap_language_tx with (' || p_sap_lang_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

    ----------------------------------------------------------------------------
    -- Lookup tlgnr_sap_language_bs entry first
    ----------------------------------------------------------------------------
    select count(bs.sap_language_cd) into v_exists_bs_flag
    from tlgnr_sap_language_bs bs
    where bs.sap_language_cd = p_sap_lang_cd;

    if (v_exists_bs_flag > 0) then
      ----------------------------------------------------------------------------
      -- Lookup tlgnr_sap_language_tx
      ----------------------------------------------------------------------------
      select count(tx.sap_language_cd) into v_exists_flag
      from tlgnr_sap_language_tx tx
      where tx.sap_language_cd = p_sap_lang_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_sap_language_tx set
          /* p_sap_lang_cd and p_lang_cd: cannot update because these are the keys */
          txt         = p_text,
          ctl_mod_uid = 1,
          ctl_mod_ts  = systimestamp
        where
          sap_language_cd = p_sap_lang_cd     and
          lang_cd = p_lang_cd                 and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnr_sap_language_tx (CD=' || p_sap_lang_cd || ', LANG=' || p_lang_cd || ', TEXT=' || p_text || ').');

      else
        insert into tlgnr_sap_language_tx (
          sap_language_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_sap_lang_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' tlgnr_sap_language_tx (CD=' || p_sap_lang_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in tlgnr_sap_language_bs (CD=' || p_sap_lang_cd || ').');
    end if;

  end upsert_sap_lang_tx;
/
show errors;

execute upsert_sap_lang_bs('AF', 'en', 1);
execute upsert_sap_lang_bs('AR', 'en', 1);
execute upsert_sap_lang_bs('AZ', 'az', 1);
execute upsert_sap_lang_bs('BG', 'bg', 1);
execute upsert_sap_lang_bs('CA', 'en', 1);
execute upsert_sap_lang_bs('CS', 'cs', 1);
execute upsert_sap_lang_bs('DA', 'en', 1);
execute upsert_sap_lang_bs('DE', 'de', 1);
execute upsert_sap_lang_bs('EL', 'en', 1);
execute upsert_sap_lang_bs('EN', 'en', 1);
execute upsert_sap_lang_bs('ES', 'es', 1);
execute upsert_sap_lang_bs('ET', 'en', 1);
execute upsert_sap_lang_bs('FI', 'en', 1);
execute upsert_sap_lang_bs('FR', 'fr', 1);
execute upsert_sap_lang_bs('HE', 'en', 1);
execute upsert_sap_lang_bs('HR', 'hr', 1);
execute upsert_sap_lang_bs('HU', 'hu', 1);
execute upsert_sap_lang_bs('ID', 'en', 1);
execute upsert_sap_lang_bs('IS', 'en', 1);
execute upsert_sap_lang_bs('IT', 'it', 1);
execute upsert_sap_lang_bs('JA', 'en', 1);
execute upsert_sap_lang_bs('KO', 'en', 1);
execute upsert_sap_lang_bs('LB', 'en', 1);
execute upsert_sap_lang_bs('LT', 'en', 1);
execute upsert_sap_lang_bs('LV', 'en', 1);
execute upsert_sap_lang_bs('MS', 'en', 1);
execute upsert_sap_lang_bs('NL', 'en', 1);
execute upsert_sap_lang_bs('NO', 'en', 1);
execute upsert_sap_lang_bs('PL', 'pl', 1);
execute upsert_sap_lang_bs('PT', 'en', 1);
execute upsert_sap_lang_bs('RO', 'ro', 1);
execute upsert_sap_lang_bs('RU', 'ru', 1);
execute upsert_sap_lang_bs('SH', 'en', 1);
execute upsert_sap_lang_bs('SK', 'sk', 1);
execute upsert_sap_lang_bs('SL', 'en', 1);
execute upsert_sap_lang_bs('SR', 'sr', 1);
execute upsert_sap_lang_bs('SV', 'en', 1);
execute upsert_sap_lang_bs('TH', 'en', 1);
execute upsert_sap_lang_bs('TR', 'en', 1);
execute upsert_sap_lang_bs('UK', 'en', 1);
execute upsert_sap_lang_bs('Z1', 'de', 1);
execute upsert_sap_lang_bs('Z2', 'en', 1);
execute upsert_sap_lang_bs('ZF', 'en', 1);
execute upsert_sap_lang_bs('ZH', 'en', 1);

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - EN
execute upsert_sap_lang_tx('AF', 'en', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'en', 'Arabic');
execute upsert_sap_lang_tx('BG', 'en', 'Bulgarian');
execute upsert_sap_lang_tx('CA', 'en', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'en', 'Chinese');
execute upsert_sap_lang_tx('HR', 'en', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'en', 'Customer reserve');
execute upsert_sap_lang_tx('CS', 'en', 'Czech');
execute upsert_sap_lang_tx('DA', 'en', 'Danish');
execute upsert_sap_lang_tx('NL', 'en', 'Dutch');
execute upsert_sap_lang_tx('EN', 'en', 'English');
execute upsert_sap_lang_tx('ET', 'en', 'Estonian');
execute upsert_sap_lang_tx('FI', 'en', 'Finnish');
execute upsert_sap_lang_tx('FR', 'en', 'French');
execute upsert_sap_lang_tx('DE', 'en', 'German');
execute upsert_sap_lang_tx('EL', 'en', 'Greek');
execute upsert_sap_lang_tx('HE', 'en', 'Hebrew');
execute upsert_sap_lang_tx('HU', 'en', 'Hungarian');
execute upsert_sap_lang_tx('IS', 'en', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'en', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'en', 'Italian');
execute upsert_sap_lang_tx('JA', 'en', 'Japanese');
execute upsert_sap_lang_tx('KO', 'en', 'Korean');
execute upsert_sap_lang_tx('LV', 'en', 'Latvian');
execute upsert_sap_lang_tx('LT', 'en', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'en', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'en', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'en', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'en', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'en', 'Polish');
execute upsert_sap_lang_tx('PT', 'en', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'en', 'Romanian');
execute upsert_sap_lang_tx('RU', 'en', 'Russian');
execute upsert_sap_lang_tx('SR', 'en', 'Serbian');
execute upsert_sap_lang_tx('SH', 'en', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'en', 'Slovakian');
execute upsert_sap_lang_tx('SL', 'en', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'en', 'Spanish');
execute upsert_sap_lang_tx('SV', 'en', 'Swedish');
execute upsert_sap_lang_tx('TH', 'en', 'Thai');
execute upsert_sap_lang_tx('TR', 'en', 'Turkish');
execute upsert_sap_lang_tx('UK', 'en', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - DE
execute upsert_sap_lang_tx('AF', 'de', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'de', 'Arabisch');
execute upsert_sap_lang_tx('BG', 'de', 'Bulgarisch');
execute upsert_sap_lang_tx('ZH', 'de', 'Chinesisch');
execute upsert_sap_lang_tx('DA', 'de', 'Dänisch');
execute upsert_sap_lang_tx('DE', 'de', 'Deutsch');
execute upsert_sap_lang_tx('EN', 'de', 'Englisch');
execute upsert_sap_lang_tx('ET', 'de', 'Estnisch');
execute upsert_sap_lang_tx('FI', 'de', 'Finnisch');
execute upsert_sap_lang_tx('FR', 'de', 'Französisch');
execute upsert_sap_lang_tx('EL', 'de', 'Griechisch');
execute upsert_sap_lang_tx('HE', 'de', 'Hebräisch');
execute upsert_sap_lang_tx('ID', 'de', 'Indonesisch');
execute upsert_sap_lang_tx('IS', 'de', 'Isländisch');
execute upsert_sap_lang_tx('IT', 'de', 'Italienisch');
execute upsert_sap_lang_tx('JA', 'de', 'Japanisch');
execute upsert_sap_lang_tx('CA', 'de', 'Katalanisch');
execute upsert_sap_lang_tx('KO', 'de', 'Koreanisch');
execute upsert_sap_lang_tx('HR', 'de', 'Kroatisch');
execute upsert_sap_lang_tx('Z1', 'de', 'Kundenreserve');
execute upsert_sap_lang_tx('LV', 'de', 'Lettisch');
execute upsert_sap_lang_tx('LB', 'de', 'Letzeburgerisch');
execute upsert_sap_lang_tx('LT', 'de', 'Litauisch');
execute upsert_sap_lang_tx('MS', 'de', 'Malaysisch');
execute upsert_sap_lang_tx('Z2', 'de', 'Naming Conventio');
execute upsert_sap_lang_tx('NL', 'de', 'Niederländisch');
execute upsert_sap_lang_tx('NO', 'de', 'Norwegisch');
execute upsert_sap_lang_tx('PL', 'de', 'Polnisch');
execute upsert_sap_lang_tx('PT', 'de', 'Portugiesisch');
execute upsert_sap_lang_tx('RO', 'de', 'Rumänisch');
execute upsert_sap_lang_tx('RU', 'de', 'Russisch');
execute upsert_sap_lang_tx('SV', 'de', 'Schwedisch');
execute upsert_sap_lang_tx('SR', 'de', 'Serbisch');
execute upsert_sap_lang_tx('SH', 'de', 'Serbisch (latein)');
execute upsert_sap_lang_tx('SK', 'de', 'Slowakisch');
execute upsert_sap_lang_tx('SL', 'de', 'Slowenisch');
execute upsert_sap_lang_tx('ES', 'de', 'Spanisch');
execute upsert_sap_lang_tx('TH', 'de', 'Thailändisch');
execute upsert_sap_lang_tx('CS', 'de', 'Tschechisch');
execute upsert_sap_lang_tx('TR', 'de', 'Türkisch');
execute upsert_sap_lang_tx('UK', 'de', 'Ukrainisch');
execute upsert_sap_lang_tx('HU', 'de', 'Ungarisch');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - FR
execute upsert_sap_lang_tx('AF', 'fr', 'Afrikaans');
execute upsert_sap_lang_tx('DE', 'fr', 'Allemand');
execute upsert_sap_lang_tx('EN', 'fr', 'Anglais');
execute upsert_sap_lang_tx('AR', 'fr', 'Arabe');
execute upsert_sap_lang_tx('BG', 'fr', 'Bulgare');
execute upsert_sap_lang_tx('CA', 'fr', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'fr', 'Chinois');
execute upsert_sap_lang_tx('KO', 'fr', 'Coréen');
execute upsert_sap_lang_tx('HR', 'fr', 'Croate');
execute upsert_sap_lang_tx('DA', 'fr', 'Danois');
execute upsert_sap_lang_tx('ES', 'fr', 'Espagnol');
execute upsert_sap_lang_tx('ET', 'fr', 'Estonien');
execute upsert_sap_lang_tx('FI', 'fr', 'Finnois');
execute upsert_sap_lang_tx('FR', 'fr', 'Français');
execute upsert_sap_lang_tx('EL', 'fr', 'Grec');
execute upsert_sap_lang_tx('HE', 'fr', 'Hébreu');
execute upsert_sap_lang_tx('HU', 'fr', 'Hongrois');
execute upsert_sap_lang_tx('ID', 'fr', 'Indonésien');
execute upsert_sap_lang_tx('IS', 'fr', 'Islandais');
execute upsert_sap_lang_tx('IT', 'fr', 'Italien');
execute upsert_sap_lang_tx('JA', 'fr', 'Japonais');
execute upsert_sap_lang_tx('LV', 'fr', 'Letton');
execute upsert_sap_lang_tx('LT', 'fr', 'Lituanien');
execute upsert_sap_lang_tx('LB', 'fr', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'fr', 'Malais');
execute upsert_sap_lang_tx('Z2', 'fr', 'Naming Conventio');
execute upsert_sap_lang_tx('NL', 'fr', 'Néerlandais');
execute upsert_sap_lang_tx('NO', 'fr', 'Norvégien');
execute upsert_sap_lang_tx('PL', 'fr', 'Polonais');
execute upsert_sap_lang_tx('PT', 'fr', 'Portugais');
execute upsert_sap_lang_tx('Z1', 'fr', 'Réserve client');
execute upsert_sap_lang_tx('RO', 'fr', 'Roumain');
execute upsert_sap_lang_tx('RU', 'fr', 'Russe');
execute upsert_sap_lang_tx('SR', 'fr', 'Serbe');
execute upsert_sap_lang_tx('SH', 'fr', 'Serbe (latin)');
execute upsert_sap_lang_tx('SK', 'fr', 'Slovaque');
execute upsert_sap_lang_tx('SL', 'fr', 'Slovène');
execute upsert_sap_lang_tx('SV', 'fr', 'Suédois');
execute upsert_sap_lang_tx('CS', 'fr', 'Tchèque');
execute upsert_sap_lang_tx('TH', 'fr', 'Thaï');
execute upsert_sap_lang_tx('TR', 'fr', 'Turc');
execute upsert_sap_lang_tx('UK', 'fr', 'Ukrainien');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - IT
execute upsert_sap_lang_tx('AF', 'it', 'Afrikaan');
execute upsert_sap_lang_tx('AR', 'it', 'Arabo');
execute upsert_sap_lang_tx('BG', 'it', 'Bulgaro');
execute upsert_sap_lang_tx('CA', 'it', 'Catalano');
execute upsert_sap_lang_tx('CS', 'it', 'Ceco');
execute upsert_sap_lang_tx('ZH', 'it', 'Cinese');
execute upsert_sap_lang_tx('KO', 'it', 'Coreano');
execute upsert_sap_lang_tx('HR', 'it', 'Croato');
execute upsert_sap_lang_tx('DA', 'it', 'Danese');
execute upsert_sap_lang_tx('HE', 'it', 'Ebraico');
execute upsert_sap_lang_tx('ET', 'it', 'Estone');
execute upsert_sap_lang_tx('FI', 'it', 'Finlandese');
execute upsert_sap_lang_tx('FR', 'it', 'Francese');
execute upsert_sap_lang_tx('JA', 'it', 'Giapponese');
execute upsert_sap_lang_tx('EL', 'it', 'Greco');
execute upsert_sap_lang_tx('ID', 'it', 'Indonesiano');
execute upsert_sap_lang_tx('EN', 'it', 'Inglese');
execute upsert_sap_lang_tx('IS', 'it', 'Islandese');
execute upsert_sap_lang_tx('IT', 'it', 'Italiano');
execute upsert_sap_lang_tx('LV', 'it', 'Lettone');
execute upsert_sap_lang_tx('LT', 'it', 'Lituano');
execute upsert_sap_lang_tx('LB', 'it', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'it', 'Malese');
execute upsert_sap_lang_tx('Z2', 'it', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'it', 'Norvegese');
execute upsert_sap_lang_tx('NL', 'it', 'Olandese');
execute upsert_sap_lang_tx('PL', 'it', 'Polacco');
execute upsert_sap_lang_tx('PT', 'it', 'Portoghese');
execute upsert_sap_lang_tx('Z1', 'it', 'Riserva cliente');
execute upsert_sap_lang_tx('RO', 'it', 'Rumeno');
execute upsert_sap_lang_tx('RU', 'it', 'Russo');
execute upsert_sap_lang_tx('SR', 'it', 'Serbo');
execute upsert_sap_lang_tx('SH', 'it', 'Serbo (Latino)');
execute upsert_sap_lang_tx('SK', 'it', 'Slovacco');
execute upsert_sap_lang_tx('SL', 'it', 'Sloveno');
execute upsert_sap_lang_tx('ES', 'it', 'Spagnolo');
execute upsert_sap_lang_tx('SV', 'it', 'Svedese');
execute upsert_sap_lang_tx('TH', 'it', 'Tailandese');
execute upsert_sap_lang_tx('DE', 'it', 'Tedesco');
execute upsert_sap_lang_tx('TR', 'it', 'Turco');
execute upsert_sap_lang_tx('UK', 'it', 'Ucraino');
execute upsert_sap_lang_tx('HU', 'it', 'Ungherese');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - ES
execute upsert_sap_lang_tx('AF', 'es', 'Afrikáans');
execute upsert_sap_lang_tx('DE', 'es', 'Alemán');
execute upsert_sap_lang_tx('AR', 'es', 'Árabe');
execute upsert_sap_lang_tx('BG', 'es', 'Búlgaro');
execute upsert_sap_lang_tx('CA', 'es', 'Catalán');
execute upsert_sap_lang_tx('CS', 'es', 'Checo');
execute upsert_sap_lang_tx('ZH', 'es', 'Chino');
execute upsert_sap_lang_tx('KO', 'es', 'Coreano');
execute upsert_sap_lang_tx('HR', 'es', 'Croata');
execute upsert_sap_lang_tx('DA', 'es', 'Danés');
execute upsert_sap_lang_tx('SK', 'es', 'Eslovaco');
execute upsert_sap_lang_tx('SL', 'es', 'Esloveno');
execute upsert_sap_lang_tx('ES', 'es', 'Español');
execute upsert_sap_lang_tx('ET', 'es', 'Estoniano');
execute upsert_sap_lang_tx('FI', 'es', 'Finlandés');
execute upsert_sap_lang_tx('FR', 'es', 'Francés');
execute upsert_sap_lang_tx('EL', 'es', 'Griego');
execute upsert_sap_lang_tx('HE', 'es', 'Hebreo');
execute upsert_sap_lang_tx('NL', 'es', 'Holandés');
execute upsert_sap_lang_tx('HU', 'es', 'Húngaro');
execute upsert_sap_lang_tx('ID', 'es', 'Indonesio');
execute upsert_sap_lang_tx('EN', 'es', 'Inglés');
execute upsert_sap_lang_tx('IS', 'es', 'Islandés');
execute upsert_sap_lang_tx('IT', 'es', 'Italiano');
execute upsert_sap_lang_tx('JA', 'es', 'Japonés');
execute upsert_sap_lang_tx('LV', 'es', 'Letoniano');
execute upsert_sap_lang_tx('LT', 'es', 'Lituaniano');
execute upsert_sap_lang_tx('LB', 'es', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'es', 'Malayo');
execute upsert_sap_lang_tx('Z2', 'es', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'es', 'Noruego');
execute upsert_sap_lang_tx('PL', 'es', 'Polaco');
execute upsert_sap_lang_tx('PT', 'es', 'Portugués');
execute upsert_sap_lang_tx('Z1', 'es', 'Reserva cliente');
execute upsert_sap_lang_tx('RO', 'es', 'Rumano');
execute upsert_sap_lang_tx('RU', 'es', 'Ruso');
execute upsert_sap_lang_tx('SR', 'es', 'Serbio');
execute upsert_sap_lang_tx('SH', 'es', 'Serbo (latino)');
execute upsert_sap_lang_tx('SV', 'es', 'Sueco');
execute upsert_sap_lang_tx('TH', 'es', 'Tailandés');
execute upsert_sap_lang_tx('TR', 'es', 'Turco');
execute upsert_sap_lang_tx('UK', 'es', 'Ucraniano');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - CS
execute upsert_sap_lang_tx('AF', 'cs', 'Afrikaans');
execute upsert_sap_lang_tx('EN', 'cs', 'Anglicky');
execute upsert_sap_lang_tx('AR', 'cs', 'Arabic');
execute upsert_sap_lang_tx('BG', 'cs', 'Bulharsky');
execute upsert_sap_lang_tx('CA', 'cs', 'Catalan');
execute upsert_sap_lang_tx('CS', 'cs', 'Česky');
execute upsert_sap_lang_tx('ZH', 'cs', 'Chinese');
execute upsert_sap_lang_tx('HR', 'cs', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'cs', 'Customer reserve');
execute upsert_sap_lang_tx('DA', 'cs', 'Danish');
execute upsert_sap_lang_tx('NL', 'cs', 'Dutch');
execute upsert_sap_lang_tx('ET', 'cs', 'Estonian');
execute upsert_sap_lang_tx('FI', 'cs', 'Finnish');
execute upsert_sap_lang_tx('FR', 'cs', 'Francouzsky');
execute upsert_sap_lang_tx('EL', 'cs', 'Greek');
execute upsert_sap_lang_tx('HE', 'cs', 'Hebrew');
execute upsert_sap_lang_tx('IS', 'cs', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'cs', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'cs', 'Italsky');
execute upsert_sap_lang_tx('JA', 'cs', 'Japanese');
execute upsert_sap_lang_tx('KO', 'cs', 'Korean');
execute upsert_sap_lang_tx('LV', 'cs', 'Latvian');
execute upsert_sap_lang_tx('LT', 'cs', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'cs', 'Luxembourgish');
execute upsert_sap_lang_tx('HU', 'cs', 'Maďarsky');
execute upsert_sap_lang_tx('MS', 'cs', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'cs', 'Naming Conventio');
execute upsert_sap_lang_tx('DE', 'cs', 'Německy');
execute upsert_sap_lang_tx('NO', 'cs', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'cs', 'Polish');
execute upsert_sap_lang_tx('PT', 'cs', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'cs', 'Rumunsky');
execute upsert_sap_lang_tx('RU', 'cs', 'Russian');
execute upsert_sap_lang_tx('SR', 'cs', 'Serbian');
execute upsert_sap_lang_tx('SH', 'cs', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SL', 'cs', 'Slovenian');
execute upsert_sap_lang_tx('SK', 'cs', 'Slovensky');
execute upsert_sap_lang_tx('ES', 'cs', 'Španělsky');
execute upsert_sap_lang_tx('SV', 'cs', 'Swedish');
execute upsert_sap_lang_tx('TH', 'cs', 'Thai');
execute upsert_sap_lang_tx('TR', 'cs', 'Turkish');
execute upsert_sap_lang_tx('UK', 'cs', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - SK
execute upsert_sap_lang_tx('AF', 'sk', 'Afrikaans');
execute upsert_sap_lang_tx('EN', 'sk', 'Angličtina');
execute upsert_sap_lang_tx('AR', 'sk', 'Arabic');
execute upsert_sap_lang_tx('BG', 'sk', 'Bulharčina');
execute upsert_sap_lang_tx('CA', 'sk', 'Catalan');
execute upsert_sap_lang_tx('CS', 'sk', 'Čeština');
execute upsert_sap_lang_tx('ZH', 'sk', 'Chinese');
execute upsert_sap_lang_tx('HR', 'sk', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'sk', 'Customer reserve');
execute upsert_sap_lang_tx('DA', 'sk', 'Danish');
execute upsert_sap_lang_tx('NL', 'sk', 'Dutch');
execute upsert_sap_lang_tx('ET', 'sk', 'Estonian');
execute upsert_sap_lang_tx('FI', 'sk', 'Finnish');
execute upsert_sap_lang_tx('FR', 'sk', 'Francúzština');
execute upsert_sap_lang_tx('EL', 'sk', 'Greek');
execute upsert_sap_lang_tx('HE', 'sk', 'Hebrew');
execute upsert_sap_lang_tx('IS', 'sk', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'sk', 'Indonesian');
execute upsert_sap_lang_tx('JA', 'sk', 'Japanese');
execute upsert_sap_lang_tx('KO', 'sk', 'Korean');
execute upsert_sap_lang_tx('LV', 'sk', 'Latvian');
execute upsert_sap_lang_tx('LT', 'sk', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'sk', 'Luxembourgish');
execute upsert_sap_lang_tx('HU', 'sk', 'Maďarčina');
execute upsert_sap_lang_tx('MS', 'sk', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'sk', 'Naming Conventio');
execute upsert_sap_lang_tx('DE', 'sk', 'Nemčina');
execute upsert_sap_lang_tx('NO', 'sk', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'sk', 'Polish');
execute upsert_sap_lang_tx('PT', 'sk', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'sk', 'Rumunčina');
execute upsert_sap_lang_tx('RU', 'sk', 'Russian');
execute upsert_sap_lang_tx('SR', 'sk', 'Serbian');
execute upsert_sap_lang_tx('SH', 'sk', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'sk', 'Slovenčina');
execute upsert_sap_lang_tx('SL', 'sk', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'sk', 'Španielčina');
execute upsert_sap_lang_tx('SV', 'sk', 'Swedish');
execute upsert_sap_lang_tx('IT', 'sk', 'Taliančina');
execute upsert_sap_lang_tx('TH', 'sk', 'Thai');
execute upsert_sap_lang_tx('TR', 'sk', 'Turkish');
execute upsert_sap_lang_tx('UK', 'sk', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - HU
execute upsert_sap_lang_tx('AF', 'hu', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'hu', 'Arabic');
execute upsert_sap_lang_tx('BG', 'hu', 'Bulgarian');
execute upsert_sap_lang_tx('CA', 'hu', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'hu', 'Chinese');
execute upsert_sap_lang_tx('HR', 'hu', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'hu', 'Customer reserve');
execute upsert_sap_lang_tx('CS', 'hu', 'Czech');
execute upsert_sap_lang_tx('DA', 'hu', 'Danish');
execute upsert_sap_lang_tx('NL', 'hu', 'Dutch');
execute upsert_sap_lang_tx('EN', 'hu', 'English');
execute upsert_sap_lang_tx('ET', 'hu', 'Estonian');
execute upsert_sap_lang_tx('FI', 'hu', 'Finnish');
execute upsert_sap_lang_tx('FR', 'hu', 'French');
execute upsert_sap_lang_tx('DE', 'hu', 'German');
execute upsert_sap_lang_tx('EL', 'hu', 'Greek');
execute upsert_sap_lang_tx('HE', 'hu', 'Hebrew');
execute upsert_sap_lang_tx('HU', 'hu', 'Hungarian');
execute upsert_sap_lang_tx('IS', 'hu', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'hu', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'hu', 'Italian');
execute upsert_sap_lang_tx('JA', 'hu', 'Japanese');
execute upsert_sap_lang_tx('KO', 'hu', 'Korean');
execute upsert_sap_lang_tx('LV', 'hu', 'Latvian');
execute upsert_sap_lang_tx('LT', 'hu', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'hu', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'hu', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'hu', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'hu', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'hu', 'Polish');
execute upsert_sap_lang_tx('PT', 'hu', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'hu', 'Romanian');
execute upsert_sap_lang_tx('RU', 'hu', 'Russian');
execute upsert_sap_lang_tx('SR', 'hu', 'Serbian');
execute upsert_sap_lang_tx('SH', 'hu', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'hu', 'Slovakian');
execute upsert_sap_lang_tx('SL', 'hu', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'hu', 'Spanish');
execute upsert_sap_lang_tx('SV', 'hu', 'Swedish');
execute upsert_sap_lang_tx('TH', 'hu', 'Thai');
execute upsert_sap_lang_tx('TR', 'hu', 'Turkish');
execute upsert_sap_lang_tx('UK', 'hu', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - RO
execute upsert_sap_lang_tx('AF', 'ro', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'ro', 'Arabic');
execute upsert_sap_lang_tx('BG', 'ro', 'Bulgara');
execute upsert_sap_lang_tx('CA', 'ro', 'Catalan');
execute upsert_sap_lang_tx('CS', 'ro', 'Ceha');
execute upsert_sap_lang_tx('ZH', 'ro', 'Chinese');
execute upsert_sap_lang_tx('HR', 'ro', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'ro', 'Customer reserve');
execute upsert_sap_lang_tx('DA', 'ro', 'Danish');
execute upsert_sap_lang_tx('NL', 'ro', 'Dutch');
execute upsert_sap_lang_tx('EN', 'ro', 'Engleza');
execute upsert_sap_lang_tx('ET', 'ro', 'Estonian');
execute upsert_sap_lang_tx('FI', 'ro', 'Finnish');
execute upsert_sap_lang_tx('FR', 'ro', 'Franceza');
execute upsert_sap_lang_tx('DE', 'ro', 'Germana');
execute upsert_sap_lang_tx('EL', 'ro', 'Greek');
execute upsert_sap_lang_tx('HE', 'ro', 'Hebrew');
execute upsert_sap_lang_tx('IS', 'ro', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'ro', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'ro', 'Italiana');
execute upsert_sap_lang_tx('JA', 'ro', 'Japanese');
execute upsert_sap_lang_tx('KO', 'ro', 'Korean');
execute upsert_sap_lang_tx('LV', 'ro', 'Latvian');
execute upsert_sap_lang_tx('LT', 'ro', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'ro', 'Luxembourgish');
execute upsert_sap_lang_tx('HU', 'ro', 'Maghiara');
execute upsert_sap_lang_tx('MS', 'ro', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'ro', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'ro', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'ro', 'Polish');
execute upsert_sap_lang_tx('PT', 'ro', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'ro', 'Romana');
execute upsert_sap_lang_tx('RU', 'ro', 'Russian');
execute upsert_sap_lang_tx('SR', 'ro', 'Serbian');
execute upsert_sap_lang_tx('SH', 'ro', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'ro', 'Slovaca');
execute upsert_sap_lang_tx('SL', 'ro', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'ro', 'Spaniola');
execute upsert_sap_lang_tx('SV', 'ro', 'Swedish');
execute upsert_sap_lang_tx('TH', 'ro', 'Thai');
execute upsert_sap_lang_tx('TR', 'ro', 'Turkish');
execute upsert_sap_lang_tx('UK', 'ro', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - BG
execute upsert_sap_lang_tx('AF', 'bg', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'bg', 'Arabic');
execute upsert_sap_lang_tx('CA', 'bg', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'bg', 'Chinese');
execute upsert_sap_lang_tx('HR', 'bg', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'bg', 'Customer reserve');
execute upsert_sap_lang_tx('DA', 'bg', 'Danish');
execute upsert_sap_lang_tx('NL', 'bg', 'Dutch');
execute upsert_sap_lang_tx('ET', 'bg', 'Estonian');
execute upsert_sap_lang_tx('FI', 'bg', 'Finnish');
execute upsert_sap_lang_tx('EL', 'bg', 'Greek');
execute upsert_sap_lang_tx('HE', 'bg', 'Hebrew');
execute upsert_sap_lang_tx('IS', 'bg', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'bg', 'Indonesian');
execute upsert_sap_lang_tx('JA', 'bg', 'Japanese');
execute upsert_sap_lang_tx('KO', 'bg', 'Korean');
execute upsert_sap_lang_tx('LV', 'bg', 'Latvian');
execute upsert_sap_lang_tx('LT', 'bg', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'bg', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'bg', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'bg', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'bg', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'bg', 'Polish');
execute upsert_sap_lang_tx('PT', 'bg', 'Portuguese');
execute upsert_sap_lang_tx('RU', 'bg', 'Russian');
execute upsert_sap_lang_tx('SR', 'bg', 'Serbian');
execute upsert_sap_lang_tx('SH', 'bg', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SL', 'bg', 'Slovenian');
execute upsert_sap_lang_tx('SV', 'bg', 'Swedish');
execute upsert_sap_lang_tx('TH', 'bg', 'Thai');
execute upsert_sap_lang_tx('TR', 'bg', 'Turkish');
execute upsert_sap_lang_tx('UK', 'bg', 'Ukrainian');
execute upsert_sap_lang_tx('EN', 'bg', 'Английски');
execute upsert_sap_lang_tx('BG', 'bg', 'Български');
execute upsert_sap_lang_tx('ES', 'bg', 'Испански');
execute upsert_sap_lang_tx('IT', 'bg', 'Италиански');
execute upsert_sap_lang_tx('DE', 'bg', 'Немски');
execute upsert_sap_lang_tx('RO', 'bg', 'Румънски');
execute upsert_sap_lang_tx('SK', 'bg', 'Словашки');
execute upsert_sap_lang_tx('HU', 'bg', 'Унгарски');
execute upsert_sap_lang_tx('FR', 'bg', 'Френски');
execute upsert_sap_lang_tx('CS', 'bg', 'Чешки');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - RU
execute upsert_sap_lang_tx('AF', 'ru', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'ru', 'Arabic');
execute upsert_sap_lang_tx('BG', 'ru', 'Bulgarian');
execute upsert_sap_lang_tx('CA', 'ru', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'ru', 'Chinese');
execute upsert_sap_lang_tx('HR', 'ru', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'ru', 'Customer reserve');
execute upsert_sap_lang_tx('CS', 'ru', 'Czech');
execute upsert_sap_lang_tx('DA', 'ru', 'Danish');
execute upsert_sap_lang_tx('NL', 'ru', 'Dutch');
execute upsert_sap_lang_tx('EN', 'ru', 'English');
execute upsert_sap_lang_tx('ET', 'ru', 'Estonian');
execute upsert_sap_lang_tx('FI', 'ru', 'Finnish');
execute upsert_sap_lang_tx('FR', 'ru', 'French');
execute upsert_sap_lang_tx('DE', 'ru', 'German');
execute upsert_sap_lang_tx('EL', 'ru', 'Greek');
execute upsert_sap_lang_tx('HE', 'ru', 'Hebrew');
execute upsert_sap_lang_tx('HU', 'ru', 'Hungarian');
execute upsert_sap_lang_tx('IS', 'ru', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'ru', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'ru', 'Italian');
execute upsert_sap_lang_tx('JA', 'ru', 'Japanese');
execute upsert_sap_lang_tx('KO', 'ru', 'Korean');
execute upsert_sap_lang_tx('LV', 'ru', 'Latvian');
execute upsert_sap_lang_tx('LT', 'ru', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'ru', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'ru', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'ru', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'ru', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'ru', 'Polish');
execute upsert_sap_lang_tx('PT', 'ru', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'ru', 'Romanian');
execute upsert_sap_lang_tx('RU', 'ru', 'Russian');
execute upsert_sap_lang_tx('SR', 'ru', 'Serbian');
execute upsert_sap_lang_tx('SH', 'ru', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'ru', 'Slovakian');
execute upsert_sap_lang_tx('SL', 'ru', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'ru', 'Spanish');
execute upsert_sap_lang_tx('SV', 'ru', 'Swedish');
execute upsert_sap_lang_tx('TH', 'ru', 'Thai');
execute upsert_sap_lang_tx('TR', 'ru', 'Turkish');
execute upsert_sap_lang_tx('UK', 'ru', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - PL
execute upsert_sap_lang_tx('AF', 'pl', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'pl', 'Arabic');
execute upsert_sap_lang_tx('BG', 'pl', 'Bulgarian');
execute upsert_sap_lang_tx('CA', 'pl', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'pl', 'Chinese');
execute upsert_sap_lang_tx('HR', 'pl', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'pl', 'Customer reserve');
execute upsert_sap_lang_tx('CS', 'pl', 'Czech');
execute upsert_sap_lang_tx('DA', 'pl', 'Danish');
execute upsert_sap_lang_tx('NL', 'pl', 'Dutch');
execute upsert_sap_lang_tx('EN', 'pl', 'English');
execute upsert_sap_lang_tx('ET', 'pl', 'Estonian');
execute upsert_sap_lang_tx('FI', 'pl', 'Finnish');
execute upsert_sap_lang_tx('FR', 'pl', 'French');
execute upsert_sap_lang_tx('DE', 'pl', 'German');
execute upsert_sap_lang_tx('EL', 'pl', 'Greek');
execute upsert_sap_lang_tx('HE', 'pl', 'Hebrew');
execute upsert_sap_lang_tx('HU', 'pl', 'Hungarian');
execute upsert_sap_lang_tx('IS', 'pl', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'pl', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'pl', 'Italian');
execute upsert_sap_lang_tx('JA', 'pl', 'Japanese');
execute upsert_sap_lang_tx('KO', 'pl', 'Korean');
execute upsert_sap_lang_tx('LV', 'pl', 'Latvian');
execute upsert_sap_lang_tx('LT', 'pl', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'pl', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'pl', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'pl', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'pl', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'pl', 'Polish');
execute upsert_sap_lang_tx('PT', 'pl', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'pl', 'Romanian');
execute upsert_sap_lang_tx('RU', 'pl', 'Russian');
execute upsert_sap_lang_tx('SR', 'pl', 'Serbian');
execute upsert_sap_lang_tx('SH', 'pl', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'pl', 'Slovakian');
execute upsert_sap_lang_tx('SL', 'pl', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'pl', 'Spanish');
execute upsert_sap_lang_tx('SV', 'pl', 'Swedish');
execute upsert_sap_lang_tx('TH', 'pl', 'Thai');
execute upsert_sap_lang_tx('TR', 'pl', 'Turkish');
execute upsert_sap_lang_tx('UK', 'pl', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - AZ
execute upsert_sap_lang_tx('AF', 'az', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'az', 'Arabic');
execute upsert_sap_lang_tx('BG', 'az', 'Bulgarian');
execute upsert_sap_lang_tx('CA', 'az', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'az', 'Chinese');
execute upsert_sap_lang_tx('HR', 'az', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'az', 'Customer reserve');
execute upsert_sap_lang_tx('CS', 'az', 'Czech');
execute upsert_sap_lang_tx('DA', 'az', 'Danish');
execute upsert_sap_lang_tx('NL', 'az', 'Dutch');
execute upsert_sap_lang_tx('EN', 'az', 'English');
execute upsert_sap_lang_tx('ET', 'az', 'Estonian');
execute upsert_sap_lang_tx('FI', 'az', 'Finnish');
execute upsert_sap_lang_tx('FR', 'az', 'French');
execute upsert_sap_lang_tx('DE', 'az', 'German');
execute upsert_sap_lang_tx('EL', 'az', 'Greek');
execute upsert_sap_lang_tx('HE', 'az', 'Hebrew');
execute upsert_sap_lang_tx('HU', 'az', 'Hungarian');
execute upsert_sap_lang_tx('IS', 'az', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'az', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'az', 'Italian');
execute upsert_sap_lang_tx('JA', 'az', 'Japanese');
execute upsert_sap_lang_tx('KO', 'az', 'Korean');
execute upsert_sap_lang_tx('LV', 'az', 'Latvian');
execute upsert_sap_lang_tx('LT', 'az', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'az', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'az', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'az', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'az', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'az', 'Polish');
execute upsert_sap_lang_tx('PT', 'az', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'az', 'Romanian');
execute upsert_sap_lang_tx('RU', 'az', 'Russian');
execute upsert_sap_lang_tx('SR', 'az', 'Serbian');
execute upsert_sap_lang_tx('SH', 'az', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'az', 'Slovakian');
execute upsert_sap_lang_tx('SL', 'az', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'az', 'Spanish');
execute upsert_sap_lang_tx('SV', 'az', 'Swedish');
execute upsert_sap_lang_tx('TH', 'az', 'Thai');
execute upsert_sap_lang_tx('TR', 'az', 'Turkish');
execute upsert_sap_lang_tx('UK', 'az', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - HR
execute upsert_sap_lang_tx('AF', 'hr', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'hr', 'Arabic');
execute upsert_sap_lang_tx('BG', 'hr', 'Bulgarian');
execute upsert_sap_lang_tx('CA', 'hr', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'hr', 'Chinese');
execute upsert_sap_lang_tx('HR', 'hr', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'hr', 'Customer reserve');
execute upsert_sap_lang_tx('CS', 'hr', 'Czech');
execute upsert_sap_lang_tx('DA', 'hr', 'Danish');
execute upsert_sap_lang_tx('NL', 'hr', 'Dutch');
execute upsert_sap_lang_tx('EN', 'hr', 'English');
execute upsert_sap_lang_tx('ET', 'hr', 'Estonian');
execute upsert_sap_lang_tx('FI', 'hr', 'Finnish');
execute upsert_sap_lang_tx('FR', 'hr', 'French');
execute upsert_sap_lang_tx('DE', 'hr', 'German');
execute upsert_sap_lang_tx('EL', 'hr', 'Greek');
execute upsert_sap_lang_tx('HE', 'hr', 'Hebrew');
execute upsert_sap_lang_tx('HU', 'hr', 'Hungarian');
execute upsert_sap_lang_tx('IS', 'hr', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'hr', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'hr', 'Italian');
execute upsert_sap_lang_tx('JA', 'hr', 'Japanese');
execute upsert_sap_lang_tx('KO', 'hr', 'Korean');
execute upsert_sap_lang_tx('LV', 'hr', 'Latvian');
execute upsert_sap_lang_tx('LT', 'hr', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'hr', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'hr', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'hr', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'hr', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'hr', 'Polish');
execute upsert_sap_lang_tx('PT', 'hr', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'hr', 'Romanian');
execute upsert_sap_lang_tx('RU', 'hr', 'Russian');
execute upsert_sap_lang_tx('SR', 'hr', 'Serbian');
execute upsert_sap_lang_tx('SH', 'hr', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'hr', 'Slovakian');
execute upsert_sap_lang_tx('SL', 'hr', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'hr', 'Spanish');
execute upsert_sap_lang_tx('SV', 'hr', 'Swedish');
execute upsert_sap_lang_tx('TH', 'hr', 'Thai');
execute upsert_sap_lang_tx('TR', 'hr', 'Turkish');
execute upsert_sap_lang_tx('UK', 'hr', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - SR
execute upsert_sap_lang_tx('AF', 'sr', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'sr', 'Arabic');
execute upsert_sap_lang_tx('BG', 'sr', 'Bulgarian');
execute upsert_sap_lang_tx('CA', 'sr', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'sr', 'Chinese');
execute upsert_sap_lang_tx('HR', 'sr', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'sr', 'Customer reserve');
execute upsert_sap_lang_tx('CS', 'sr', 'Czech');
execute upsert_sap_lang_tx('DA', 'sr', 'Danish');
execute upsert_sap_lang_tx('NL', 'sr', 'Dutch');
execute upsert_sap_lang_tx('EN', 'sr', 'English');
execute upsert_sap_lang_tx('ET', 'sr', 'Estonian');
execute upsert_sap_lang_tx('FI', 'sr', 'Finnish');
execute upsert_sap_lang_tx('FR', 'sr', 'French');
execute upsert_sap_lang_tx('DE', 'sr', 'German');
execute upsert_sap_lang_tx('EL', 'sr', 'Greek');
execute upsert_sap_lang_tx('HE', 'sr', 'Hebrew');
execute upsert_sap_lang_tx('HU', 'sr', 'Hungarian');
execute upsert_sap_lang_tx('IS', 'sr', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'sr', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'sr', 'Italian');
execute upsert_sap_lang_tx('JA', 'sr', 'Japanese');
execute upsert_sap_lang_tx('KO', 'sr', 'Korean');
execute upsert_sap_lang_tx('LV', 'sr', 'Latvian');
execute upsert_sap_lang_tx('LT', 'sr', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'sr', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'sr', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'sr', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'sr', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'sr', 'Polish');
execute upsert_sap_lang_tx('PT', 'sr', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'sr', 'Romanian');
execute upsert_sap_lang_tx('RU', 'sr', 'Russian');
execute upsert_sap_lang_tx('SR', 'sr', 'Serbian');
execute upsert_sap_lang_tx('SH', 'sr', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'sr', 'Slovakian');
execute upsert_sap_lang_tx('SL', 'sr', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'sr', 'Spanish');
execute upsert_sap_lang_tx('SV', 'sr', 'Swedish');
execute upsert_sap_lang_tx('TH', 'sr', 'Thai');
execute upsert_sap_lang_tx('TR', 'sr', 'Turkish');
execute upsert_sap_lang_tx('UK', 'sr', 'Ukrainian');

prompt INSERTING into TLGNR_SAP_LANGUAGE_TX - TR
execute upsert_sap_lang_tx('AF', 'tr', 'Afrikaans');
execute upsert_sap_lang_tx('AR', 'tr', 'Arabic');
execute upsert_sap_lang_tx('BG', 'tr', 'Bulgarian');
execute upsert_sap_lang_tx('CA', 'tr', 'Catalan');
execute upsert_sap_lang_tx('ZH', 'tr', 'Chinese');
execute upsert_sap_lang_tx('HR', 'tr', 'Croatian');
execute upsert_sap_lang_tx('Z1', 'tr', 'Customer reserve');
execute upsert_sap_lang_tx('CS', 'tr', 'Czech');
execute upsert_sap_lang_tx('DA', 'tr', 'Danish');
execute upsert_sap_lang_tx('NL', 'tr', 'Dutch');
execute upsert_sap_lang_tx('EN', 'tr', 'English');
execute upsert_sap_lang_tx('ET', 'tr', 'Estonian');
execute upsert_sap_lang_tx('FI', 'tr', 'Finnish');
execute upsert_sap_lang_tx('FR', 'tr', 'French');
execute upsert_sap_lang_tx('DE', 'tr', 'German');
execute upsert_sap_lang_tx('EL', 'tr', 'Greek');
execute upsert_sap_lang_tx('HE', 'tr', 'Hebrew');
execute upsert_sap_lang_tx('HU', 'tr', 'Hungarian');
execute upsert_sap_lang_tx('IS', 'tr', 'Icelandic');
execute upsert_sap_lang_tx('ID', 'tr', 'Indonesian');
execute upsert_sap_lang_tx('IT', 'tr', 'Italian');
execute upsert_sap_lang_tx('JA', 'tr', 'Japanese');
execute upsert_sap_lang_tx('KO', 'tr', 'Korean');
execute upsert_sap_lang_tx('LV', 'tr', 'Latvian');
execute upsert_sap_lang_tx('LT', 'tr', 'Lithuanian');
execute upsert_sap_lang_tx('LB', 'tr', 'Luxembourgish');
execute upsert_sap_lang_tx('MS', 'tr', 'Malaysian');
execute upsert_sap_lang_tx('Z2', 'tr', 'Naming Conventio');
execute upsert_sap_lang_tx('NO', 'tr', 'Norwegian');
execute upsert_sap_lang_tx('PL', 'tr', 'Polish');
execute upsert_sap_lang_tx('PT', 'tr', 'Portuguese');
execute upsert_sap_lang_tx('RO', 'tr', 'Romanian');
execute upsert_sap_lang_tx('RU', 'tr', 'Russian');
execute upsert_sap_lang_tx('SR', 'tr', 'Serbian');
execute upsert_sap_lang_tx('SH', 'tr', 'Serbian (Latin)');
execute upsert_sap_lang_tx('SK', 'tr', 'Slovakian');
execute upsert_sap_lang_tx('SL', 'tr', 'Slovenian');
execute upsert_sap_lang_tx('ES', 'tr', 'Spanish');
execute upsert_sap_lang_tx('SV', 'tr', 'Swedish');
execute upsert_sap_lang_tx('TH', 'tr', 'Thai');
execute upsert_sap_lang_tx('TR', 'tr', 'Turkish');
execute upsert_sap_lang_tx('UK', 'tr', 'Ukrainian');

commit;

drop procedure upsert_sap_lang_bs;
drop procedure upsert_sap_lang_tx;
drop function is_different_number;
drop function is_different_varchar2;

