-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_shippingpoint_type.sql ----------'

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
-- tlgnr_shipppoint_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_shipppoint_type_cd   in varchar2,
  p_is_rail_flag         in number,
  p_ctl_act_cd           in number)
is
  v_table_name    varchar2(48) := 'tlgnr_shipppoint_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_shipppoint_type_cd || ', is_rail_flag='  || p_is_rail_flag || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.shipppoint_type_cd)
    into v_exists_flag
    from tlgnr_shipppoint_type_bs bs
    where bs.shipppoint_type_cd = p_shipppoint_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_shipppoint_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_shipppoint_type_bs set
          /* shipppoint_type_cd: cannot update because this is the key */
          is_rail_flag                   = p_is_rail_flag,
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          shipppoint_type_cd = p_shipppoint_type_cd and
          (is_rail_flag != p_is_rail_flag or
           ctl_act_cd != p_ctl_act_cd)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_shipppoint_type_cd || ').');
      end if;
    else
      insert into tlgnr_shipppoint_type_bs (
        shipppoint_type_cd,
        is_rail_flag,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_shipppoint_type_cd,
        p_is_rail_flag,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_shipppoint_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_shipppoint_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_shipppoint_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_shipppoint_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_shipppoint_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.shipppoint_type_cd) into v_exists_bs_flag
    from tlgnr_shipppoint_type_bs bs
    where bs.shipppoint_type_cd = p_shipppoint_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.shipppoint_type_cd) into v_exists_flag
      from tlgnr_shipppoint_type_tx tx
      where tx.shipppoint_type_cd = p_shipppoint_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_shipppoint_type_tx set
          /* p_shipppoint_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          shipppoint_type_cd = p_shipppoint_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_shipppoint_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_shipppoint_type_tx (
          shipppoint_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_shipppoint_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_shipppoint_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_shipppoint_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_BS
execute upsert_bs(0, 0, 1);
execute upsert_bs(1, 1, 1);
execute upsert_bs(2, 0, 1);
execute upsert_bs(3, 0, 1);
execute upsert_bs(4, 1, 1);
execute upsert_bs(5, 1, 1);
execute upsert_bs(6, 1, 1);
execute upsert_bs(7, 1, 1);
execute upsert_bs(8, 0, 1);

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - EN
execute upsert_tx(0, 'en', 'Road'                          );
execute upsert_tx(1, 'en', 'Rail'                          );
execute upsert_tx(2, 'en', 'Container Terminal (Truck)'    );
execute upsert_tx(3, 'en', 'Bimodal Truck (FVL)'           );
execute upsert_tx(4, 'en', 'Rail Supplier'                 );
execute upsert_tx(5, 'en', 'Rail Repair'                   );
execute upsert_tx(6, 'en', 'Rail External/Return'          );
execute upsert_tx(7, 'en', 'Container Terminal (Rail)'     );
execute upsert_tx(8, 'en', 'Simple vehicle'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - DE
execute upsert_tx(0, 'de', 'Strasse'                       );
execute upsert_tx(1, 'de', 'Eisenbahn'                     );
execute upsert_tx(2, 'de', 'Container Terminal (Strasse)'  );
execute upsert_tx(3, 'de', 'Freiverlad/Bimodal Strasse'    );
execute upsert_tx(4, 'de', 'Eisenbahn Lieferant'           );
execute upsert_tx(5, 'de', 'Eisenbahn Werkstatt/Reparatur' );
execute upsert_tx(6, 'de', 'Eisenbahn Extern/Retouren'     );
execute upsert_tx(7, 'de', 'Container Terminal (Eisenbahn)');
execute upsert_tx(8, 'de', 'Einfaches Fahrzeug'            );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - FR
execute upsert_tx(0, 'fr', 'Route'                         );
execute upsert_tx(1, 'fr', 'Rail'                          );
execute upsert_tx(2, 'fr', 'Terminaux Container (Camion)'  );
execute upsert_tx(3, 'fr', 'Bimodal Route (FVL)'           );
execute upsert_tx(4, 'fr', 'Fournisseur de Rail'           );
execute upsert_tx(5, 'fr', 'Réparation de Rail'            );
execute upsert_tx(6, 'fr', 'Rail Externe/juste Retour'     );
execute upsert_tx(7, 'fr', 'Terminaux Container (Rail)'    );
execute upsert_tx(8, 'fr', 'Véhicule simple'               );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - IT
execute upsert_tx(0, 'it', 'Strada'                        );
execute upsert_tx(1, 'it', 'Ferrovia'                      );
execute upsert_tx(2, 'it', 'Terminal Container (Camion)'   );
execute upsert_tx(3, 'it', 'Bimodale Strada (FVL)'         );
execute upsert_tx(4, 'it', 'Fornitore Ferroviario'         );
execute upsert_tx(5, 'it', 'Riparazione Ferroviario'       );
execute upsert_tx(6, 'it', 'Ferrovia Esterna/solo Ritorno' );
execute upsert_tx(7, 'it', 'Terminal Container (Ferrovia)' );
execute upsert_tx(8, 'it', 'Veicolo semplice'              );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - ES
execute upsert_tx(0, 'es', 'Carretera'                     );
execute upsert_tx(1, 'es', 'Tren'                          );
execute upsert_tx(2, 'es', 'Terminal Contenedor (Camión)'  );
execute upsert_tx(3, 'es', 'Bimodal Carretera (FVL)'       );
execute upsert_tx(4, 'es', 'Compañía de tren'              );
execute upsert_tx(5, 'es', 'Reparación tren'               );
execute upsert_tx(6, 'es', 'Tren Externo/Devolución'       );
execute upsert_tx(7, 'es', 'Terminal Contenedor (Tren)'    );
execute upsert_tx(8, 'es', 'Vehículo simple'               );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - CS
execute upsert_tx(0, 'cs', 'Silnice'                          );
execute upsert_tx(1, 'cs', 'Železnice'                        );
execute upsert_tx(2, 'cs', 'Kontejnerový terminál (tahač)'    );
execute upsert_tx(3, 'cs', 'Bimodální vozidlo (FVL)'          );
execute upsert_tx(4, 'cs', 'Železniční dodavatel'             );
execute upsert_tx(5, 'cs', 'Železnice - oprava'               );
execute upsert_tx(6, 'cs', 'Železnice Externí/Návrat'         );
execute upsert_tx(7, 'cs', 'Kontejnerový terminál (železnice)');
execute upsert_tx(8, 'cs', 'Jednotlivé vozidlo'               );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - SK
execute upsert_tx(0, 'sk', 'Cesta'                          );
execute upsert_tx(1, 'sk', 'Železnica'                      );
execute upsert_tx(2, 'sk', 'Kontajnerový terminál (Ťahač)'  );
execute upsert_tx(3, 'sk', 'Bimodálny ťahač (FVL)'          );
execute upsert_tx(4, 'sk', 'Železničný dodávateľ'           );
execute upsert_tx(5, 'sk', 'Železničná oprava'              );
execute upsert_tx(6, 'sk', 'Železnica externá/Návrat'       );
execute upsert_tx(7, 'sk', 'Kontajnerový terminál (Železnica)');
execute upsert_tx(8, 'sk', 'Vozidlo'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - HU
execute upsert_tx(0, 'hu', 'Út'                          );
execute upsert_tx(1, 'hu', 'Vasút'                          );
execute upsert_tx(2, 'hu', 'Konténer terminál (kamion)'    );
execute upsert_tx(3, 'hu', 'Bimodális kamion'           );
execute upsert_tx(4, 'hu', 'Vasúti szállítás'                 );
execute upsert_tx(5, 'hu', 'Vasúti javítás'                   );
execute upsert_tx(6, 'hu', 'Külső vasút/Vissza'          );
execute upsert_tx(7, 'hu', 'Konténer terminál (vasúti)'     );
execute upsert_tx(8, 'hu', 'Játmű'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - RO
execute upsert_tx(0, 'ro', 'Rutier'                          );
execute upsert_tx(1, 'ro', 'Feroviar'                          );
execute upsert_tx(2, 'ro', 'Terminal container (Camion)'    );
execute upsert_tx(3, 'ro', 'Bimodal Camion (FVL)'           );
execute upsert_tx(4, 'ro', 'Furnizor feroviar'                 );
execute upsert_tx(5, 'ro', 'Reparatie feroviara'                   );
execute upsert_tx(6, 'ro', 'Cale feroviara externa/retur'          );
execute upsert_tx(7, 'ro', 'Terminal container (feroviar)'     );
execute upsert_tx(8, 'ro', 'Vehicul simplu'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - BG
execute upsert_tx(0, 'bg', 'Автомобилен'                          );
execute upsert_tx(1, 'bg', 'ЖП'                          );
execute upsert_tx(2, 'bg', 'Терминал на контейнер (Камион)'    );
execute upsert_tx(3, 'bg', 'Бимодален камион (FVL)'           );
execute upsert_tx(4, 'bg', 'ЖП доставчик'                 );
execute upsert_tx(5, 'bg', 'ЖП ремонт'                   );
execute upsert_tx(6, 'bg', 'ЖП външен/връщане'          );
execute upsert_tx(7, 'bg', 'Терминал на контейнер (ЖП)'     );
execute upsert_tx(8, 'bg', 'Просто превозно средство'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(0, 'ru', 'автомобильная '                          );
execute upsert_tx(1, 'ru', 'железнодорожная '                          );
execute upsert_tx(2, 'ru', 'Контейнерный терминал (автомобильный)'    );
execute upsert_tx(3, 'ru', 'Бимодальный автомобиль (FVL)'           );
execute upsert_tx(4, 'ru', 'Железнодорожный поставщик'                 );
execute upsert_tx(5, 'ru', 'Железнодорожный ремонт'                   );
execute upsert_tx(6, 'ru', 'Rail External/Return'          );
execute upsert_tx(7, 'ru', 'Контейнерный терминал (железнодорожный)'     );
execute upsert_tx(8, 'ru', 'простой автомобиль'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - PL
execute upsert_tx(0, 'pl', 'Droga'                          );
execute upsert_tx(1, 'pl', 'Kolej'                          );
execute upsert_tx(2, 'pl', 'Terminal Kontererowy (ciężarówka)'    );
execute upsert_tx(3, 'pl', 'Bimodalny pojazd cięzarowy (FVL)'           );
execute upsert_tx(4, 'pl', 'Dostawca kolejowy'                 );
execute upsert_tx(5, 'pl', 'Serwis kolejowy'                   );
execute upsert_tx(6, 'pl', 'Kolejowy zewnętrzny/zwrotny'          );
execute upsert_tx(7, 'pl', 'Terminal kontenerowy (kolej)'     );
execute upsert_tx(8, 'pl', 'Prosty pojazd'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(0, 'az', 'Road'                          );
execute upsert_tx(1, 'az', 'Rail'                          );
execute upsert_tx(2, 'az', 'Container Terminal (Truck)'    );
execute upsert_tx(3, 'az', 'Bimodal Truck (FVL)'           );
execute upsert_tx(4, 'az', 'Rail Supplier'                 );
execute upsert_tx(5, 'az', 'Rail Repair'                   );
execute upsert_tx(6, 'az', 'Rail External/Return'          );
execute upsert_tx(7, 'az', 'Container Terminal (Rail)'     );
execute upsert_tx(8, 'az', 'Simple vehicle'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - HR
execute upsert_tx(0, 'hr', 'Cesta'                          );
execute upsert_tx(1, 'hr', 'Željeznica'                          );
execute upsert_tx(2, 'hr', 'Kontejnerski terminal (kamion)'    );
execute upsert_tx(3, 'hr', 'Bimodalni kamion (FVL)'           );
execute upsert_tx(4, 'hr', 'Željeznički Dobavljač'                 );
execute upsert_tx(5, 'hr', 'Željeznički popravak'                   );
execute upsert_tx(6, 'hr', 'Željeznički Vanjski / Povratak'          );
execute upsert_tx(7, 'hr', 'Kontejn. terminal (željeznica)'     );
execute upsert_tx(8, 'hr', 'Jednostavno vozilo'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - SR
execute upsert_tx(0, 'sr', 'Drumski prevoz'                          );
execute upsert_tx(1, 'sr', 'Prevoz železnicom'                          );
execute upsert_tx(2, 'sr', 'Kontejnerski terminal (drum)'    );
execute upsert_tx(3, 'sr', 'Bimodalni kamion (FVL)'           );
execute upsert_tx(4, 'sr', 'Dobavljač (železnica)'                 );
execute upsert_tx(5, 'sr', 'Popravke (železnica)'                   );
execute upsert_tx(6, 'sr', 'Železnica eksterna/povraćaj'          );
execute upsert_tx(7, 'sr', 'Kontejnerski terminal (železnica)'     );
execute upsert_tx(8, 'sr', 'Vozilo'                );

prompt INSERTING into TLGNR_SHIPPPOINT_TYPE_TX - TR
execute upsert_tx(0, 'tr', 'Road'                          );
execute upsert_tx(1, 'tr', 'Rail'                          );
execute upsert_tx(2, 'tr', 'Container Terminal (Truck)'    );
execute upsert_tx(3, 'tr', 'Bimodal Truck (FVL)'           );
execute upsert_tx(4, 'tr', 'Rail Supplier'                 );
execute upsert_tx(5, 'tr', 'Rail Repair'                   );
execute upsert_tx(6, 'tr', 'Rail External/Return'          );
execute upsert_tx(7, 'tr', 'Container Terminal (Rail)'     );
execute upsert_tx(8, 'tr', 'Simple vehicle'                );

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

