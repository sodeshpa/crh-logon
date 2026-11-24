-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_shipto_type.sql ----------'

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
-- tlgnr_shipto_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_shipto_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_shipto_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_shipto_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.shipto_type_cd)
    into v_exists_flag
    from tlgnr_shipto_type_bs bs
    where bs.shipto_type_cd = p_shipto_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_shipto_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_shipto_type_bs set
/* shipto_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          shipto_type_cd = p_shipto_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_shipto_type_cd || ').');
      end if;
    else
      insert into tlgnr_shipto_type_bs (
        shipto_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_shipto_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_shipto_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_shipto_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_shipto_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_shipto_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_shipto_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.shipto_type_cd) into v_exists_bs_flag
    from tlgnr_shipto_type_bs bs
    where bs.shipto_type_cd = p_shipto_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.shipto_type_cd) into v_exists_flag
      from tlgnr_shipto_type_tx tx
      where tx.shipto_type_cd = p_shipto_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_shipto_type_tx set
/* p_shipto_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          shipto_type_cd = p_shipto_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_shipto_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_shipto_type_tx (
          shipto_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_shipto_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_shipto_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_shipto_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SHIPTO_TYPE_BS
execute upsert_bs(0, 1);
execute upsert_bs(1, 1);
execute upsert_bs(2, 1);
execute upsert_bs(3, 1);
execute upsert_bs(4, 1);
execute upsert_bs(5, 1);
execute upsert_bs(6, 1);
execute upsert_bs(7, 1);

-- Location not fixed: Introduced because of Ton-Kilometers, see ADN40552 / ADN40396
-- Freiverlad/FVL: Introduced to distinguish

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - EN
execute upsert_tx(0, 'en', 'Standard'                       );
execute upsert_tx(1, 'en', 'Carrier Stock'              );
execute upsert_tx(2, 'en', 'Location not fixed'             );
execute upsert_tx(3, 'en', 'Bimodal (FVL)'                  );
execute upsert_tx(4, 'en', 'Container Terminal'             );
execute upsert_tx(5, 'en', 'Maintenance/Repair'             );
execute upsert_tx(6, 'en', 'Plant (Prod./Supply/Terminal)'  );
execute upsert_tx(7, 'en', 'Supplier'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - DE
execute upsert_tx(0, 'de', 'Standard'                       );
execute upsert_tx(1, 'de', 'Transporteurlager'              );
execute upsert_tx(2, 'de', 'Location not fixed'             );
execute upsert_tx(3, 'de', 'Freiverlad (Bimodal, FVL)'      );
execute upsert_tx(4, 'de', 'Container Terminal'             );
execute upsert_tx(5, 'de', 'Werkstatt/Reparatur'            );
execute upsert_tx(6, 'de', 'Werk (Prod./Lieferw./Terminal)' );
execute upsert_tx(7, 'de', 'Lieferant'                      );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - FR
execute upsert_tx(0, 'fr', 'Standard'                           );
execute upsert_tx(1, 'fr', 'Stock de transporteur'              );
execute upsert_tx(2, 'fr', 'Lieu pas fixé'                      );
execute upsert_tx(3, 'fr', 'Bimodal (FVL)'                      );
execute upsert_tx(4, 'fr', 'Terminaux Container'                );
execute upsert_tx(5, 'fr', 'Entretien/Réparation'               );
execute upsert_tx(6, 'fr', 'Usine (Prod./Approvis./Terminaux)'  );
execute upsert_tx(7, 'fr', 'Fournisseur'                        );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - IT
execute upsert_tx(0, 'it', 'Standard'                             );
execute upsert_tx(1, 'it', 'Magazzino del trasportatore'          );
execute upsert_tx(2, 'it', 'Luogo non definito'                   );
execute upsert_tx(3, 'it', 'Bimodale (FVL)'                       );
execute upsert_tx(4, 'it', 'Terminal Container'                   );
execute upsert_tx(5, 'it', 'Manutenzione/Riparazione'             );
execute upsert_tx(6, 'it', 'Impianto (Prod./Fornitura/Terminal)'  );
execute upsert_tx(7, 'it', 'Fornitore'                            );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - ES
execute upsert_tx(0, 'es', 'Estándar'                          );
execute upsert_tx(1, 'es', 'Stock transportista'               );
execute upsert_tx(2, 'es', 'Lugar no fijo'                     );
execute upsert_tx(3, 'es', 'Bimodal (FVL)'                     );
execute upsert_tx(4, 'es', 'Terminal Contenedor'               );
execute upsert_tx(5, 'es', 'Mantenimiento/Reparación'          );
execute upsert_tx(6, 'es', 'Planta (Produc./Sumin./Terminal)'  );
execute upsert_tx(7, 'es', 'Proveedor'                         );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - CS
execute upsert_tx(0, 'cs', 'Standard'                       );
execute upsert_tx(1, 'cs', 'Sklad dopravce'              );
execute upsert_tx(2, 'cs', 'Pozice nenalezena'             );
execute upsert_tx(3, 'cs', 'Bimodální (FVL)'                  );
execute upsert_tx(4, 'cs', 'Kontejnerový terminál'             );
execute upsert_tx(5, 'cs', 'Údržba/Oprava'             );
execute upsert_tx(6, 'cs', 'Závod (Výroba, Zásoba, Terminál) ');
execute upsert_tx(7, 'cs', 'Dodavatel'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - SK
execute upsert_tx(0, 'sk', 'Štandard'                       );
execute upsert_tx(1, 'sk', 'Miesto dopravcu'              );
execute upsert_tx(2, 'sk', 'Miesto neopravené'             );
execute upsert_tx(3, 'sk', 'Bimodálne (FVL)'                  );
execute upsert_tx(4, 'sk', 'Kontajnerový terminál'             );
execute upsert_tx(5, 'sk', 'Údržba/oprava'             );
execute upsert_tx(6, 'sk', 'Závod (Výr./Zásobovanie/Terminál)'  );
execute upsert_tx(7, 'sk', 'Dodávateľ'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - HU
execute upsert_tx(0, 'hu', 'Szabvány'                       );
execute upsert_tx(1, 'hu', 'Fuvarozó'              );
execute upsert_tx(2, 'hu', 'Nem rögzített hely'             );
execute upsert_tx(3, 'hu', 'Bimodális'                  );
execute upsert_tx(4, 'hu', 'Konténer terminál'             );
execute upsert_tx(5, 'hu', 'Karbantartás/Javítás'             );
execute upsert_tx(6, 'hu', 'Gyár (Termelés/Ellátás/terminál)'  );
execute upsert_tx(7, 'hu', 'Beszállító'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - RO
execute upsert_tx(0, 'ro', 'Standard'                       );
execute upsert_tx(1, 'ro', 'Lagar transportor'              );
execute upsert_tx(2, 'ro', 'Locatia nu este fixata'             );
execute upsert_tx(3, 'ro', 'Bimodal (FVL)'                  );
execute upsert_tx(4, 'ro', 'Terminal container'             );
execute upsert_tx(5, 'ro', 'Mentenanta/Reparatii'             );
execute upsert_tx(6, 'ro', 'Fabrica (Productie/Furnizare/Terminal)'  );
execute upsert_tx(7, 'ro', 'Furnizor'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - BG
execute upsert_tx(0, 'bg', 'Стандартен'                       );
execute upsert_tx(1, 'bg', 'Наличен превозвач'              );
execute upsert_tx(2, 'bg', 'Местоположението не е фиксирано'             );
execute upsert_tx(3, 'bg', 'Бимодален (FVL)'                  );
execute upsert_tx(4, 'bg', 'Терминал на контейнер'             );
execute upsert_tx(5, 'bg', 'Поддръжка/ремонт'             );
execute upsert_tx(6, 'bg', 'Завод (Prod./Снабдябане/Терминал)'  );
execute upsert_tx(7, 'bg', 'Доставчик'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'стандартный'                       );
execute upsert_tx(1, 'ru', 'склад перевозчиков'              );
execute upsert_tx(2, 'ru', 'Местоположение не зафиксировано'             );
execute upsert_tx(3, 'ru', 'Бимодальный  (FVL)'                  );
execute upsert_tx(4, 'ru', 'Контейнерный терминал'             );
execute upsert_tx(5, 'ru', 'Техническое обслуживание\ремонт'             );
execute upsert_tx(6, 'ru', 'Завод (производство\поставка\терминал)'  );
execute upsert_tx(7, 'ru', 'Поставщик'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - PL
execute upsert_tx(0, 'pl', 'Standard'                       );
execute upsert_tx(1, 'pl', 'Transporteurlager'              );
execute upsert_tx(2, 'pl', 'Lokalizacja nie zmieniona'             );
execute upsert_tx(3, 'pl', 'Bimodalny (FVL)'                  );
execute upsert_tx(4, 'pl', 'Terminal Kontenerowy'             );
execute upsert_tx(5, 'pl', 'Serwis/Naprawa'             );
execute upsert_tx(6, 'pl', 'Zakład(Prod./Zaopatrzenie/Terminal)'  );
execute upsert_tx(7, 'pl', 'Dostawca'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'Standard'                       );
execute upsert_tx(1, 'az', 'Transporteurlager'              );
execute upsert_tx(2, 'az', 'Location not fixed'             );
execute upsert_tx(3, 'az', 'Bimodal (FVL)'                  );
execute upsert_tx(4, 'az', 'Container Terminal'             );
execute upsert_tx(5, 'az', 'Maintenance/Repair'             );
execute upsert_tx(6, 'az', 'Plant (Prod./Supply/Terminal)'  );
execute upsert_tx(7, 'az', 'Supplier'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - HR
execute upsert_tx(0, 'hr', 'Standardni'                       );
execute upsert_tx(1, 'hr', 'Prijevoz eurlagera'              );
execute upsert_tx(2, 'hr', 'Lokacija nije fiksna'             );
execute upsert_tx(3, 'hr', 'Bimodalni (FVL)'                  );
execute upsert_tx(4, 'hr', 'Kontejnerski terminal'             );
execute upsert_tx(5, 'hr', 'Održavanje / popravak'             );
execute upsert_tx(6, 'hr', 'Postrojenja(Proiz./Opskrbiti/Terminal)'  );
execute upsert_tx(7, 'hr', 'Dobavljač'                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - SR
execute upsert_tx(0, 'sr', 'Standard'                       );
execute upsert_tx(1, 'sr', 'Transporteurlager'              );
execute upsert_tx(2, 'sr', 'Lokacija nije fiksirana'             );
execute upsert_tx(3, 'sr', 'Bimodalni (FVL)'                  );
execute upsert_tx(4, 'sr', 'Kontejnerski terminal'             );
execute upsert_tx(5, 'sr', 'Održavanje/popravka'             );
execute upsert_tx(6, 'sr', 'Fabrika'  );
execute upsert_tx(7, 'sr', 'Dobavljač '                       );

prompt INSERTING into TLGNR_SHIPTO_TYPE_TX - TR
execute upsert_tx(0, 'tr', 'Standard'                       );
execute upsert_tx(1, 'tr', 'Carrier Stock'              );
execute upsert_tx(2, 'tr', 'Location not fixed'             );
execute upsert_tx(3, 'tr', 'Bimodal (FVL)'                  );
execute upsert_tx(4, 'tr', 'Container Terminal'             );
execute upsert_tx(5, 'tr', 'Maintenance/Repair'             );
execute upsert_tx(6, 'tr', 'Plant (Prod./Supply/Terminal)'  );
execute upsert_tx(7, 'tr', 'Supplier'                       );

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

