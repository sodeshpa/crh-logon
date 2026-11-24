-- $Revision$
-- $Date$
--
-- 2009 - 2011: List based on clarification in https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/ShiptoOrderSAPCleanup#Clarification_of_Pickup_Transpor
-- 2012 - 2012: LOGON-5377: Distinction of Shipping type + Shipping Condition
--
-- E1, S1, S3, S4: - LOGON 1.0 - LOGON 1.6 (2009 - 2012)
--   Are the real Shippingcond, with some logic connected to it in SAP.
--
-- SA, SB, ... SH: - LOGON 1.2 - LOGON 1.6 (2009 - 2012)
--   Were introduced only because there are too many shippingpoints
--   to 1 plant. SAP can handle only 12(?) SP per plant.
--   The actual key for a SP-to-Plant however contains the SAP Shipping Cond,
--   therefore they kind of extend the possible number of SP per plant this way...
--
-- 01, 02, 03, Z4: - LOGON EBM/2.0 Shipping Conditions (2011 - ...)
--   The real shipping conditions, for truck/rail etc
--
-- 04, 05, 06: - LOGON EBM/2.1 (2012 - ...)
--   Same trick as for the original SA, SB, ..SH, see LOGON-5377
--   workaround for the max 12 shippingpoints per plant & shipping condition.
--   In contrast to the original LOGON 1.x solution, we now do distinguish
--   shipping CONDITION and shipping TYPE, i.e., there is now a mapping
--   for the 04/05/06 -> Z4 (bimodal)
--   The CHE LOGON 1.x types were removed (kept as comments, for future reference/migration)
--
-- Z5, Z6, Z7, Z8, Z9: update 18.10.2012, with new initialdata: replace the 04, 05, 06, and map to 01 (and not Z4)

prompt '---------- refdata/tlgnr_sap_shipping_cond.sql ----------'

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
-- tlgnr_sap_shipping_cond_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_sap_shipping_cond_cd      in varchar2,
  p_send_to_sap_shipping_type in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_sap_shipping_cond_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_sap_shipping_cond_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.sap_shipping_cond_cd)
    into v_exists_flag
    from tlgnr_sap_shipping_cond_bs bs
    where bs.sap_shipping_cond_cd = p_sap_shipping_cond_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_sap_shipping_cond_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_sap_shipping_cond_bs set
/* sap_shipping_cond_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          send_to_sap_shipping_type      = p_send_to_sap_shipping_type,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_shipping_cond_cd = p_sap_shipping_cond_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_shipping_cond_cd || ').');
      end if;
    else
      insert into tlgnr_sap_shipping_cond_bs (
        sap_shipping_cond_cd,
        send_to_sap_shipping_type,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_sap_shipping_cond_cd,
        p_send_to_sap_shipping_type,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_shipping_cond_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_sap_shipping_cond_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_sap_shipping_cond_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_sap_shipping_cond_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_sap_shipping_cond_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.sap_shipping_cond_cd) into v_exists_bs_flag
    from tlgnr_sap_shipping_cond_bs bs
    where bs.sap_shipping_cond_cd = p_sap_shipping_cond_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.sap_shipping_cond_cd) into v_exists_flag
      from tlgnr_sap_shipping_cond_tx tx
      where tx.sap_shipping_cond_cd = p_sap_shipping_cond_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_sap_shipping_cond_tx set
/* p_sap_shipping_cond_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_shipping_cond_cd = p_sap_shipping_cond_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_shipping_cond_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_sap_shipping_cond_tx (
          sap_shipping_cond_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_sap_shipping_cond_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_shipping_cond_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_sap_shipping_cond_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_BS
execute upsert_bs('01', '01', 1);
execute upsert_bs('02', '02', 1);
execute upsert_bs('03', '03', 1);
execute upsert_bs('Z4', 'Z4', 1);
execute upsert_bs('Z5', '01', 1);
execute upsert_bs('Z6', '01', 1);
execute upsert_bs('Z7', '01', 1);
execute upsert_bs('Z8', '01', 1);
execute upsert_bs('Z9', '01', 1);
execute upsert_bs('ZA', 'ZA', 1);

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - EN
execute upsert_tx('01', 'en', 'Truck'               );
execute upsert_tx('02', 'en', 'Rail (direct)'       );
execute upsert_tx('03', 'en', 'Ship'                );
execute upsert_tx('Z4', 'en', 'Multimodal Transport');
execute upsert_tx('Z5', 'en', 'Train Station 01'    );
execute upsert_tx('Z6', 'en', 'Train Station 02'    );
execute upsert_tx('Z7', 'en', 'Train Station 03'    );
execute upsert_tx('Z8', 'en', 'Train Station 04'    );
execute upsert_tx('Z9', 'en', 'Train Station 05'    );
execute upsert_tx('ZA', 'en', 'Belt Conveyor'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - DE

execute upsert_tx('01', 'de', 'Fahrzeug'               );
execute upsert_tx('02', 'de', 'Bahn (direkt)'       );
execute upsert_tx('03', 'de', 'Shiff'                );
execute upsert_tx('Z4', 'de', 'Multimodal Transport');
execute upsert_tx('Z5', 'de', 'Bahnstation 01'    );
execute upsert_tx('Z6', 'de', 'Bahnstation 02'    );
execute upsert_tx('Z7', 'de', 'Bahnstation 03'    );
execute upsert_tx('Z8', 'de', 'Bahnstation 04'    );
execute upsert_tx('Z9', 'de', 'Bahnstation 05'    );
execute upsert_tx('ZA', 'de', 'Förderband'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - FR
execute upsert_tx('01', 'fr', 'Camion'              );
execute upsert_tx('02', 'fr', 'Train'               );
execute upsert_tx('03', 'fr', 'Expédition'          );
execute upsert_tx('Z4', 'fr', 'Multi-expédition'    );
execute upsert_tx('Z5', 'fr', 'Train Station 01'    );
execute upsert_tx('Z6', 'fr', 'Train Station 02'    );
execute upsert_tx('Z7', 'fr', 'Train Station 03'    );
execute upsert_tx('Z8', 'fr', 'Train Station 04'    );
execute upsert_tx('Z9', 'fr', 'Train Station 05'    );
execute upsert_tx('ZA', 'fr', 'Convoyeur à bande'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - IT
execute upsert_tx('01', 'it', 'Camion'              );
execute upsert_tx('02', 'it', 'Ferrovia'            );
execute upsert_tx('03', 'it', 'Spedizione'          );
execute upsert_tx('Z4', 'it', 'Trasporto multimodale');
execute upsert_tx('Z5', 'it', 'Stazione ferroviaria 01'    );
execute upsert_tx('Z6', 'it', 'Stazione ferroviaria 02'    );
execute upsert_tx('Z7', 'it', 'Stazione ferroviaria 03'    );
execute upsert_tx('Z8', 'it', 'Stazione ferroviaria 04'    );
execute upsert_tx('Z9', 'it', 'Stazione ferroviaria 05'    );
execute upsert_tx('ZA', 'it', 'Nastro trasportatore'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - ES
execute upsert_tx('01', 'es', 'Camión'              );
execute upsert_tx('02', 'es', 'Tren'                );
execute upsert_tx('03', 'es', 'Envío'               );
execute upsert_tx('Z4', 'es', 'Transporte Multimodal');
execute upsert_tx('Z5', 'es', 'Estación de Tren 01'    );
execute upsert_tx('Z6', 'es', 'Estación de Tren 02'    );
execute upsert_tx('Z7', 'es', 'Estación de Tren 03'    );
execute upsert_tx('Z8', 'es', 'Estación de Tren 04'    );
execute upsert_tx('Z9', 'es', 'Estación de Tren 05'    );
execute upsert_tx('ZA', 'es', 'Cinta Transportadora'   );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - CS
execute upsert_tx('01', 'cs', 'Vozidlo'               );
execute upsert_tx('02', 'cs', 'Železnice (přímo)'       );
execute upsert_tx('03', 'cs', 'Loď'                );
execute upsert_tx('Z4', 'cs', 'Multimodální přeprava');
execute upsert_tx('Z5', 'cs', 'Železniční stanice 01'    );
execute upsert_tx('Z6', 'cs', 'Železniční stanice 02'    );
execute upsert_tx('Z7', 'cs', 'Železniční stanice 03'    );
execute upsert_tx('Z8', 'cs', 'Železniční stanice 04'    );
execute upsert_tx('Z9', 'cs', 'Železniční stanice 05'    );
execute upsert_tx('ZA', 'cs', 'Belt Conveyor'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - SK
execute upsert_tx('01', 'sk', 'Ťahač'               );
execute upsert_tx('02', 'sk', 'Železnica (priamo)'       );
execute upsert_tx('03', 'sk', 'Loď'                );
execute upsert_tx('Z4', 'sk', 'Multimodálna preprava');
execute upsert_tx('Z5', 'sk', 'Vlaková stanica 01'    );
execute upsert_tx('Z6', 'sk', 'Vlaková stanica 02'    );
execute upsert_tx('Z7', 'sk', 'Vlaková stanica 03'    );
execute upsert_tx('Z8', 'sk', 'Vlaková stanica 04'    );
execute upsert_tx('Z9', 'sk', 'Vlaková stanica 05'    );
execute upsert_tx('ZA', 'sk', 'Pásový dopravník'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - HU
execute upsert_tx('01', 'hu', 'Kamion'               );
execute upsert_tx('02', 'hu', 'Vasút'       );
execute upsert_tx('03', 'hu', 'Szállítás'                );
execute upsert_tx('Z4', 'hu', 'Multimodális közlekedés');
execute upsert_tx('Z5', 'hu', 'Vasútállomás 01'    );
execute upsert_tx('Z6', 'hu', 'Vasútállomás 02'    );
execute upsert_tx('Z7', 'hu', 'Vasútállomás 03'    );
execute upsert_tx('Z8', 'hu', 'Vasútállomás 04'    );
execute upsert_tx('Z9', 'hu', 'Vasútállomás 05'    );
execute upsert_tx('ZA', 'hu', 'Szállítószalag'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - RO
execute upsert_tx('01', 'ro', 'Camion'               );
execute upsert_tx('02', 'ro', 'Cale feroviara (direct)'       );
execute upsert_tx('03', 'ro', 'Vapor'                );
execute upsert_tx('Z4', 'ro', 'Multimodal Transport');
execute upsert_tx('Z5', 'ro', 'Statie tren 01'    );
execute upsert_tx('Z6', 'ro', 'Statie tren 02'    );
execute upsert_tx('Z7', 'ro', 'Statie tren 03'    );
execute upsert_tx('Z8', 'ro', 'Statie tren 04'    );
execute upsert_tx('Z9', 'ro', 'Statie tren 05'    );
execute upsert_tx('ZA', 'ro', 'Banda transportoare'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - BG
execute upsert_tx('01', 'bg', 'Камон'               );
execute upsert_tx('02', 'bg', 'ЖП (директен)'       );
execute upsert_tx('03', 'bg', 'Кораб'                );
execute upsert_tx('Z4', 'bg', 'Мултимодален транспорт');
execute upsert_tx('Z5', 'bg', 'Гара 01'    );
execute upsert_tx('Z6', 'bg', 'Гара 02'    );
execute upsert_tx('Z7', 'bg', 'Гара 03'    );
execute upsert_tx('Z8', 'bg', 'Гара 04'    );
execute upsert_tx('Z9', 'bg', 'Гара 05'    );
execute upsert_tx('ZA', 'bg', 'Колан конвейер'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - RU (ENGLISH COPY)
execute upsert_tx('01', 'ru', 'автомобильная доставка'               );
execute upsert_tx('02', 'ru', 'железнодорожная доставка'       );
execute upsert_tx('03', 'ru', 'водная доставка'                );
execute upsert_tx('Z4', 'ru', 'мультимодальная доставка');
execute upsert_tx('Z5', 'ru', 'ЖД станция 01'    );
execute upsert_tx('Z6', 'ru', 'ЖД станция 02'    );
execute upsert_tx('Z7', 'ru', 'ЖД станция 03'    );
execute upsert_tx('Z8', 'ru', 'ЖД станция 04'    );
execute upsert_tx('Z9', 'ru', 'ЖД станция 05'    );
execute upsert_tx('ZA', 'ru', 'Конвейер'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - PL
execute upsert_tx('01', 'pl', 'Pojazd ciężarowy'               );
execute upsert_tx('02', 'pl', 'Kolej (bezpośredni)'       );
execute upsert_tx('03', 'pl', 'Statek'                );
execute upsert_tx('Z4', 'pl', 'Transp. multimodalnego');
execute upsert_tx('Z5', 'pl', 'Dworzec kolejowy 01'    );
execute upsert_tx('Z6', 'pl', 'Dworzec kolejowy 02'    );
execute upsert_tx('Z7', 'pl', 'Dworzec kolejowy 03'    );
execute upsert_tx('Z8', 'pl', 'Dworzec kolejowy 04'    );
execute upsert_tx('Z9', 'pl', 'Dworzec kolejowy 05'    );
execute upsert_tx('ZA', 'pl', 'Przenośnik taśmowy'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - AZ (ENGLISH COPY)
execute upsert_tx('01', 'az', 'Yük maşını'               );
execute upsert_tx('02', 'az', 'Dəmiryol (birbaşa)'       );
execute upsert_tx('03', 'az', 'Gəmi'                );
execute upsert_tx('Z4', 'az', 'Multimodal nəqletmə');
execute upsert_tx('Z5', 'az', 'Dəmiryol vağzalı 01'    );
execute upsert_tx('Z6', 'az', 'Dəmiryol vağzalı 02'    );
execute upsert_tx('Z7', 'az', 'Dəmiryol vağzalı 03'    );
execute upsert_tx('Z8', 'az', 'Dəmiryol vağzalı 04'    );
execute upsert_tx('Z9', 'az', 'Dəmiryol vağzalı 05'    );
execute upsert_tx('ZA', 'az', 'Lentşəkilli konveyer'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - HR
execute upsert_tx('01', 'hr', 'Tegljač'               );
execute upsert_tx('02', 'hr', 'Željeznica (izravno)'       );
execute upsert_tx('03', 'hr', 'Brod'                );
execute upsert_tx('Z4', 'hr', 'Multimodalni prijevoz');
execute upsert_tx('Z5', 'hr', 'Željeznička stanica 01'    );
execute upsert_tx('Z6', 'hr', 'Željeznička stanica 02'    );
execute upsert_tx('Z7', 'hr', 'Željeznička stanica 03'    );
execute upsert_tx('Z8', 'hr', 'Željeznička stanica 04'    );
execute upsert_tx('Z9', 'hr', 'Željeznička stanica 05'    );
execute upsert_tx('ZA', 'hr', 'Konvejner remena'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - SR
execute upsert_tx('01', 'sr', 'Kamion'               );
execute upsert_tx('02', 'sr', 'Železnica'       );
execute upsert_tx('03', 'sr', 'Brod'                );
execute upsert_tx('Z4', 'sr', 'Multimodalni transport');
execute upsert_tx('Z5', 'sr', 'Železnička stanica 01'    );
execute upsert_tx('Z6', 'sr', 'Železnička stanica 02'    );
execute upsert_tx('Z7', 'sr', 'Železnička stanica 03'    );
execute upsert_tx('Z8', 'sr', 'Železnička stanica 04'    );
execute upsert_tx('Z9', 'sr', 'Železnička stanica 05'    );
execute upsert_tx('ZA', 'sr', 'Trakasti transporter'       );

prompt INSERTING into TLGNR_SAP_SHIPPING_COND_TX - TR
execute upsert_tx('01', 'tr', 'Truck'               );
execute upsert_tx('02', 'tr', 'Rail (direct)'       );
execute upsert_tx('03', 'tr', 'Ship'                );
execute upsert_tx('Z4', 'tr', 'Multimodal Transport');
execute upsert_tx('Z5', 'tr', 'Train Station 01'    );
execute upsert_tx('Z6', 'tr', 'Train Station 02'    );
execute upsert_tx('Z7', 'tr', 'Train Station 03'    );
execute upsert_tx('Z8', 'tr', 'Train Station 04'    );
execute upsert_tx('Z9', 'tr', 'Train Station 05'    );
execute upsert_tx('ZA', 'tr', 'Belt Conveyor'       );

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
