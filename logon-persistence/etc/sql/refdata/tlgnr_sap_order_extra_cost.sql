-- $Revision$
-- $Date$
--
-- refdata from
-- file:///share/doc/crh/logon_2_1/spec/biz/block3/SDS_Logon_block3_LRQ_017.1_Services_Extracost_20120418.pdf

prompt '---------- refdata/tlgnr_sap_extra_cost.sql ----------'

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
-- tlgnr_sap_order_extra_cost_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_sap_order_extra_cost_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_sap_order_extra_cost_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_sap_order_extra_cost_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.sap_order_extra_cost_cd)
    into v_exists_flag
    from tlgnr_sap_order_extra_cost_bs bs
    where bs.sap_order_extra_cost_cd = p_sap_order_extra_cost_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_sap_order_extra_cost_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_sap_order_extra_cost_bs set
/* sap_order_extra_cost_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_order_extra_cost_cd = p_sap_order_extra_cost_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_order_extra_cost_cd || ').');
      end if;
    else
      insert into tlgnr_sap_order_extra_cost_bs (
        sap_order_extra_cost_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_sap_order_extra_cost_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_order_extra_cost_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_sap_order_extra_cost_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_sap_order_extra_cost_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_sap_order_extra_cost_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_sap_order_extra_cost_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.sap_order_extra_cost_cd) into v_exists_bs_flag
    from tlgnr_sap_order_extra_cost_bs bs
    where bs.sap_order_extra_cost_cd = p_sap_order_extra_cost_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.sap_order_extra_cost_cd) into v_exists_flag
      from tlgnr_sap_order_extra_cost_tx tx
      where tx.sap_order_extra_cost_cd = p_sap_order_extra_cost_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_sap_order_extra_cost_tx set
/* p_sap_order_extra_cost_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_order_extra_cost_cd = p_sap_order_extra_cost_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_order_extra_cost_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_sap_order_extra_cost_tx (
          sap_order_extra_cost_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_sap_order_extra_cost_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_order_extra_cost_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_sap_order_extra_cost_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_BS

execute upsert_bs('Z000000001', 1);

execute upsert_bs('Z000000002', 1);

execute upsert_bs('Z000000003', 1);

execute upsert_bs('Z000000004', 1);

execute upsert_bs('Z000000005', 1);

execute upsert_bs('Z000000006', 1);

execute upsert_bs('Z000000007', 1);

execute upsert_bs('Z000000008', 1);

execute upsert_bs('Z000000009', 1);

execute upsert_bs('Z000000010', 1);

execute upsert_bs('Z000000011', 1);

execute upsert_bs('Z000000012', 1);

execute upsert_bs('Z000000013', 1);

execute upsert_bs('Z000000014', 1);
execute upsert_bs('Z000000015', 1);
execute upsert_bs('Z000000016', 1);
execute upsert_bs('Z000000017', 1);
execute upsert_bs('Z000000018', 1);
execute upsert_bs('Z000000019', 1);
execute upsert_bs('Z000000020', 1);
execute upsert_bs('Z000000021', 1);
execute upsert_bs('Z000000022', 1);

execute upsert_bs('Z000000023', 1);
execute upsert_bs('Z000000024', 1);
execute upsert_bs('Z000000025', 1);

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - EN

execute upsert_tx('Z000000001', 'en', 'Rerouting due to Logistics Reasons');

execute upsert_tx('Z000000002', 'en', 'Rerouting due to Customer Reasons');

execute upsert_tx('Z000000003', 'en', 'Saturday');

execute upsert_tx('Z000000004', 'en', 'Sunday');

execute upsert_tx('Z000000005', 'en', 'Bank Holiday');

execute upsert_tx('Z000000006', 'en', 'Night deliveries');

execute upsert_tx('Z000000007', 'en', 'Weekend');

execute upsert_tx('Z000000008', 'en', 'Specific delivery time');

execute upsert_tx('Z000000009', 'en', 'Delivery cancellation');

execute upsert_tx('Z000000010', 'en', 'Logistic costs');

execute upsert_tx('Z000000011', 'en', 'Incomplete Truck');

execute upsert_tx('Z000000012', 'en', 'Waiting time Fix surcharge');

execute upsert_tx('Z000000013', 'en', 'Multi-Drop (Milkman tour)');

execute upsert_tx('Z000000014', 'en', 'Partial Return');

execute upsert_tx('Z000000015', 'en', 'Pallet unloading');

execute upsert_tx('Z000000016', 'en', 'Empty trip');

execute upsert_tx('Z000000017', 'en', 'Temperature below 0 degrees');

execute upsert_tx('Z000000018', 'en', 'Weight check charge');

execute upsert_tx('Z000000019', 'en', 'Off-Duty deliveries');

execute upsert_tx('Z000000020', 'en', 'Winter surcharge');

execute upsert_tx('Z000000021', 'en', 'Fuel surcharge');

execute upsert_tx('Z000000022', 'en', 'Short-term performance rump up');

execute upsert_tx('Z000000023', 'en', 'Additional shunting');

execute upsert_tx('Z000000024', 'en', 'Return');

execute upsert_tx('Z000000025', 'en', 'Small Truck');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - DE (ENGLISH COPY)
execute upsert_tx('Z000000001', 'de', 'Umleitung logist. Anforderungen');

execute upsert_tx('Z000000002', 'de', 'Umleitung Kundenanforderungen');

execute upsert_tx('Z000000003', 'de', 'Samstag');

execute upsert_tx('Z000000004', 'de', 'Sonntag');

execute upsert_tx('Z000000005', 'de', 'Feiertag');

execute upsert_tx('Z000000006', 'de', 'Nachtbelieferung');

execute upsert_tx('Z000000007', 'de', 'Wochenende');

execute upsert_tx('Z000000008', 'de', 'Spezifische Lieferzeit');

execute upsert_tx('Z000000009', 'de', 'Lierfung abgesagt');

execute upsert_tx('Z000000010', 'de', 'Logistikkosten');

execute upsert_tx('Z000000011', 'de', 'Unvollständige Fahrzeugkompos.');

execute upsert_tx('Z000000012', 'de', 'Wartezeiten-Aufpreis');

execute upsert_tx('Z000000013', 'de', 'Multi-Drop');

execute upsert_tx('Z000000014', 'de', 'Teilrücklieferung');

execute upsert_tx('Z000000015', 'de', 'Palettenentladung');

execute upsert_tx('Z000000016', 'de', 'Leerfahrt');

execute upsert_tx('Z000000017', 'de', 'Temperatur unter 0°C');

execute upsert_tx('Z000000018', 'de', 'Gefälligkeitsverwiegungskosten');

execute upsert_tx('Z000000019', 'de', 'Lieferung außerhalb Öffnungsz.');

execute upsert_tx('Z000000020', 'de', 'Winteraufschlag');

execute upsert_tx('Z000000021', 'de', 'Treibstoffzuschlag');

execute upsert_tx('Z000000022', 'de', 'Kurzfristige Anfahrtszeit');

execute upsert_tx('Z000000023', 'de', 'Zusätzlicher Umschlag');

execute upsert_tx('Z000000024', 'de', 'Rücklieferung');

execute upsert_tx('Z000000025', 'de', 'Kleines Fahrzeug');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - FR (ENGLISH COPY)
execute upsert_tx('Z000000001', 'fr', 'Rerout. pour raisons logistiques');

execute upsert_tx('Z000000002', 'fr', 'Rerout. pour raisons clients');

execute upsert_tx('Z000000003', 'fr', 'Samedi');

execute upsert_tx('Z000000004', 'fr', 'Dimanche');

execute upsert_tx('Z000000005', 'fr', 'Jour férié');

execute upsert_tx('Z000000006', 'fr', 'Livraisons de nuit');

execute upsert_tx('Z000000007', 'fr', 'Weekend');

execute upsert_tx('Z000000008', 'fr', 'Heures de livraison spécifiques');

execute upsert_tx('Z000000009', 'fr', 'Livraison annulée');

execute upsert_tx('Z000000010', 'fr', 'Coûts logistiques');

execute upsert_tx('Z000000011', 'fr', 'Camion incomplet');

execute upsert_tx('Z000000012', 'fr', 'Supplément heures d''attente');

execute upsert_tx('Z000000013', 'fr', 'Livraison multiple');

execute upsert_tx('Z000000014', 'fr', 'Retour partiel');

execute upsert_tx('Z000000015', 'fr', 'Déchargement de palette');

execute upsert_tx('Z000000016', 'fr', 'Voyage à vide');

execute upsert_tx('Z000000017', 'fr', 'Température sous 0 degrés');

execute upsert_tx('Z000000018', 'fr', 'Vérification du poids chargé');

execute upsert_tx('Z000000019', 'fr', 'Livrs. hors heures de travail');

execute upsert_tx('Z000000020', 'fr', 'Supplément hivernale');

execute upsert_tx('Z000000021', 'fr', 'Supplément gasoil');

execute upsert_tx('Z000000022', 'fr', 'Short-term performance rump up');

execute upsert_tx('Z000000023', 'fr', 'Prestation supplémentaire');

execute upsert_tx('Z000000024', 'fr', 'Retour');

execute upsert_tx('Z000000025', 'fr', 'Petit camion');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - IT (ENGLISH COPY)
execute upsert_tx('Z000000001', 'it', 'Rerouting per motivi logistici');

execute upsert_tx('Z000000002', 'it', 'Rerouting per motivi del cliente');

execute upsert_tx('Z000000003', 'it', 'Sabato');

execute upsert_tx('Z000000004', 'it', 'Domenica');

execute upsert_tx('Z000000005', 'it', 'Bank Holiday');

execute upsert_tx('Z000000006', 'it', 'Consegne notturne');

execute upsert_tx('Z000000007', 'it', 'Weekend');

execute upsert_tx('Z000000008', 'it', 'Specifico tempo di consegna');

execute upsert_tx('Z000000009', 'it', 'Cancellazione consegna');

execute upsert_tx('Z000000010', 'it', 'Costi logistici');

execute upsert_tx('Z000000011', 'it', 'Camion incompleto');

execute upsert_tx('Z000000012', 'it', 'Supplemento fisso sosta');

execute upsert_tx('Z000000013', 'it', 'Multi-Drop (Milkman tour)');

execute upsert_tx('Z000000014', 'it', 'Reso parziale');

execute upsert_tx('Z000000015', 'it', 'Scarico pallet');

execute upsert_tx('Z000000016', 'it', 'Viaggio vuoto');

execute upsert_tx('Z000000017', 'it', 'Temperatura sotto zero');

execute upsert_tx('Z000000018', 'it', 'Costo controllo peso');

execute upsert_tx('Z000000019', 'it', 'Consegne fuori servizio');

execute upsert_tx('Z000000020', 'it', 'Supplemento invernale');

execute upsert_tx('Z000000021', 'it', 'Supplememento carburante');

execute upsert_tx('Z000000022', 'it', 'Prestazione nel breve in salita');

execute upsert_tx('Z000000023', 'it', 'Smistamento addizionale');

execute upsert_tx('Z000000024', 'it', 'Reso');

execute upsert_tx('Z000000025', 'it', 'Piccolo camion');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - ES (ENGLISH COPY)
execute upsert_tx('Z000000001', 'es', 'Cambio ruta por razone logísticas');

execute upsert_tx('Z000000002', 'es', 'Cambio ruta requerimiento clnte');

execute upsert_tx('Z000000003', 'es', 'Sábado');

execute upsert_tx('Z000000004', 'es', 'Domingo');

execute upsert_tx('Z000000005', 'es', 'Festivo');

execute upsert_tx('Z000000006', 'es', 'Entrega nocturna');

execute upsert_tx('Z000000007', 'es', 'Fin de semana');

execute upsert_tx('Z000000008', 'es', 'Tiempo de entrega específico');

execute upsert_tx('Z000000009', 'es', 'Entrega cancelada');

execute upsert_tx('Z000000010', 'es', 'Costes logísticos');

execute upsert_tx('Z000000011', 'es', 'Camión incompleto');

execute upsert_tx('Z000000012', 'es', 'Recargo fijo por tiempo de espera');

execute upsert_tx('Z000000013', 'es', 'Multi-entrega');

execute upsert_tx('Z000000014', 'es', 'Devolución parcial');

execute upsert_tx('Z000000015', 'es', 'No carga de palets');

execute upsert_tx('Z000000016', 'es', 'Viaje vacío');

execute upsert_tx('Z000000017', 'es', 'Temperatura bajo cero');

execute upsert_tx('Z000000018', 'es', 'Cargo por comprobación de peso');

execute upsert_tx('Z000000019', 'es', 'Entregas fuera de servicio');

execute upsert_tx('Z000000020', 'es', 'Recargo invernal');

execute upsert_tx('Z000000021', 'es', 'Recargo combustible');

execute upsert_tx('Z000000022', 'es', 'Cumplim. corto plazo desarrollo');

execute upsert_tx('Z000000023', 'es', 'Cambio de vía adicional');

execute upsert_tx('Z000000024', 'es', 'Devolución');

execute upsert_tx('Z000000025', 'es', 'Camión pequeño');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - CS (ENGLISH COPY)
execute upsert_tx('Z000000001', 'cs', 'Rerouting due to Logistics Reasons');

execute upsert_tx('Z000000002', 'cs', 'Rerouting due to Customer Reasons');

execute upsert_tx('Z000000003', 'cs', 'Saturday');

execute upsert_tx('Z000000004', 'cs', 'Sunday');

execute upsert_tx('Z000000005', 'cs', 'Bank Holiday');

execute upsert_tx('Z000000006', 'cs', 'Night deliveries');

execute upsert_tx('Z000000007', 'cs', 'Weekend');

execute upsert_tx('Z000000008', 'cs', 'Specific delivery time');

execute upsert_tx('Z000000009', 'cs', 'Delivery cancellation');

execute upsert_tx('Z000000010', 'cs', 'Logistic costs');

execute upsert_tx('Z000000011', 'cs', 'Incomplete Truck');

execute upsert_tx('Z000000012', 'cs', 'Waiting time Fix surcharge');

execute upsert_tx('Z000000013', 'cs', 'Multi-Drop (Milkman tour)');

execute upsert_tx('Z000000014', 'cs', 'Partial Return');

execute upsert_tx('Z000000015', 'cs', 'Pallet unloading');

execute upsert_tx('Z000000016', 'cs', 'Empty trip');

execute upsert_tx('Z000000017', 'cs', 'Temperature below 0 degrees');

execute upsert_tx('Z000000018', 'cs', 'Weight check charge');

execute upsert_tx('Z000000019', 'cs', 'Off-Duty deliveries');

execute upsert_tx('Z000000020', 'cs', 'Winter surcharge');

execute upsert_tx('Z000000021', 'cs', 'Fuel surcharge');

execute upsert_tx('Z000000022', 'cs', 'Short-term performance rump up');

execute upsert_tx('Z000000023', 'cs', 'Additional shunting');

execute upsert_tx('Z000000024', 'cs', 'Return');

execute upsert_tx('Z000000025', 'cs', 'Small Truck');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - SK (ENGLISH COPY)
execute upsert_tx('Z000000001', 'sk', 'Presmerovanie logistic. dôvodov');

execute upsert_tx('Z000000002', 'sk', 'Presmerovanie dôvodov od zákazn.');

execute upsert_tx('Z000000003', 'sk', 'Sobota');

execute upsert_tx('Z000000004', 'sk', 'Nedeľa');

execute upsert_tx('Z000000005', 'sk', 'štátny sviatok');

execute upsert_tx('Z000000006', 'sk', 'Nočná dodávka');

execute upsert_tx('Z000000007', 'sk', 'Víkend');

execute upsert_tx('Z000000008', 'sk', 'špecifický dodací čas');

execute upsert_tx('Z000000009', 'sk', 'Zrušenie dodávky');

execute upsert_tx('Z000000010', 'sk', 'Logistické náklady');

execute upsert_tx('Z000000011', 'sk', 'Neúplný ťahač');

execute upsert_tx('Z000000012', 'sk', 'Fixný príplatok za čakaciu dobu');

execute upsert_tx('Z000000013', 'sk', 'Multi-Drop (Milkman tour)');

execute upsert_tx('Z000000014', 'sk', 'čiastočný návrat');

execute upsert_tx('Z000000015', 'sk', 'Vykládka paliet');

execute upsert_tx('Z000000016', 'sk', 'Prázdna jazda');

execute upsert_tx('Z000000017', 'sk', 'Teplota pod 0 stupňov');

execute upsert_tx('Z000000018', 'sk', 'Preverenie váženie');

execute upsert_tx('Z000000019', 'sk', 'Bez colné dodávky');

execute upsert_tx('Z000000020', 'sk', 'Zimný príplatok');

execute upsert_tx('Z000000021', 'sk', 'Palivový príplatok');

execute upsert_tx('Z000000022', 'sk', 'Krátkodobý výkon zadok hore');

execute upsert_tx('Z000000023', 'sk', 'ďalšie posunovanie');

execute upsert_tx('Z000000024', 'sk', 'Návrat');

execute upsert_tx('Z000000025', 'sk', 'Malý ťahač');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - HU (ENGLISH COPY)
execute upsert_tx('Z000000001', 'hu', 'Logisztikai okok átirányítás');

execute upsert_tx('Z000000002', 'hu', 'Ügyfél okok átirányítás');

execute upsert_tx('Z000000003', 'hu', 'Szombat');

execute upsert_tx('Z000000004', 'hu', 'Vasárnap');

execute upsert_tx('Z000000005', 'hu', 'Banki szünnap');

execute upsert_tx('Z000000006', 'hu', 'Éjszakai kiszállítás');

execute upsert_tx('Z000000007', 'hu', 'Hétvége');

execute upsert_tx('Z000000008', 'hu', 'Speciális kiszállítási idő');

execute upsert_tx('Z000000009', 'hu', 'Szállítás törlése');

execute upsert_tx('Z000000010', 'hu', 'Logisztikai költségek');

execute upsert_tx('Z000000011', 'hu', 'Hiányos megtöltött kamion');

execute upsert_tx('Z000000012', 'hu', 'Várakozási idő fix felárral');

execute upsert_tx('Z000000013', 'hu', 'Multi -drop (Milkman)');

execute upsert_tx('Z000000014', 'hu', 'Részleges return');

execute upsert_tx('Z000000015', 'hu', 'Raklap leadás');

execute upsert_tx('Z000000016', 'hu', 'Üres járat');

execute upsert_tx('Z000000017', 'hu', '0 fok alatti hőmérséklet');

execute upsert_tx('Z000000018', 'hu', 'Mérlegelési díj');

execute upsert_tx('Z000000019', 'hu', 'Határon kívüli szállítás');

execute upsert_tx('Z000000020', 'hu', 'Téli felár');

execute upsert_tx('Z000000021', 'hu', 'Üzemanyag felár');

execute upsert_tx('Z000000022', 'hu', 'Rövid teljesítmény vége');

execute upsert_tx('Z000000023', 'hu', 'Kiegészítő vasúti tolatás');

execute upsert_tx('Z000000024', 'hu', 'Visszaszállítás (return)');

execute upsert_tx('Z000000025', 'hu', 'Kis kamion');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - RO (ENGLISH COPY)
execute upsert_tx('Z000000001', 'ro', 'Redirectionare din motive Logist.');

execute upsert_tx('Z000000002', 'ro', 'Redirectionare din motive client');

execute upsert_tx('Z000000003', 'ro', 'Sambata');

execute upsert_tx('Z000000004', 'ro', 'Duminica');

execute upsert_tx('Z000000005', 'ro', 'Sarbatoare legala');

execute upsert_tx('Z000000006', 'ro', 'Livrari noaptea');

execute upsert_tx('Z000000007', 'ro', 'Weekend');

execute upsert_tx('Z000000008', 'ro', 'Interval livrare spacific');

execute upsert_tx('Z000000009', 'ro', 'Anulare livrare');

execute upsert_tx('Z000000010', 'ro', 'Costuri logistice');

execute upsert_tx('Z000000011', 'ro', 'Camion incomplet');

execute upsert_tx('Z000000012', 'ro', 'Cost fix timp de asteptare');

execute upsert_tx('Z000000013', 'ro', 'Multi-drop');

execute upsert_tx('Z000000014', 'ro', 'Retur partial');

execute upsert_tx('Z000000015', 'ro', 'Palet nedescarcat');

execute upsert_tx('Z000000016', 'ro', 'Cursa goala');

execute upsert_tx('Z000000017', 'ro', 'Temperaturi sub zero grade');

execute upsert_tx('Z000000018', 'ro', 'Verificare greutate de incarcat');

execute upsert_tx('Z000000019', 'ro', 'Livrari off-duty');

execute upsert_tx('Z000000020', 'ro', 'Taxa de iarna');

execute upsert_tx('Z000000021', 'ro', 'Taxa de combustibil');

execute upsert_tx('Z000000022', 'ro', 'Performanta pe termen scurt');

execute upsert_tx('Z000000023', 'ro', 'Manevra suplimentara');

execute upsert_tx('Z000000024', 'ro', 'Retur');

execute upsert_tx('Z000000025', 'ro', 'Camion mic');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - BG (ENGLISH COPY)
execute upsert_tx('Z000000001', 'bg', 'Маршрут променя поради логистика');

execute upsert_tx('Z000000002', 'bg', 'Маршрутът променя заради клиента');

execute upsert_tx('Z000000003', 'bg', 'Събота');

execute upsert_tx('Z000000004', 'bg', 'Неделя');

execute upsert_tx('Z000000005', 'bg', 'Празник');

execute upsert_tx('Z000000006', 'bg', 'Нощни доставки');

execute upsert_tx('Z000000007', 'bg', 'Почивни дни');

execute upsert_tx('Z000000008', 'bg', 'Специфично време за доставка');

execute upsert_tx('Z000000009', 'bg', 'Анулиране на доставка');

execute upsert_tx('Z000000010', 'bg', 'Логистични разходи');

execute upsert_tx('Z000000011', 'bg', 'Непълен камион');

execute upsert_tx('Z000000012', 'bg', 'Доплащане за изчакване');

execute upsert_tx('Z000000013', 'bg', 'Мулти-дроп');

execute upsert_tx('Z000000014', 'bg', 'Частично връщане');

execute upsert_tx('Z000000015', 'bg', 'Разтоварване на палети');

execute upsert_tx('Z000000016', 'bg', 'Празно пътуване');

execute upsert_tx('Z000000017', 'bg', 'Температура под 0 градуса');

execute upsert_tx('Z000000018', 'bg', 'Разходи за проверка на теглото');

execute upsert_tx('Z000000019', 'bg', 'Доставки без мито');

execute upsert_tx('Z000000020', 'bg', 'Допълнителна такса зима');

execute upsert_tx('Z000000021', 'bg', 'Такса гориво');

execute upsert_tx('Z000000022', 'bg', 'Краткосрочно изпълнение');

execute upsert_tx('Z000000023', 'bg', 'Допълнителна маневра');

execute upsert_tx('Z000000024', 'bg', 'Връщане');

execute upsert_tx('Z000000025', 'bg', 'Малък камион');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - RU (ENGLISH COPY)
execute upsert_tx('Z000000001', 'ru', 'Переадр. причинами логистики');

execute upsert_tx('Z000000002', 'ru', 'Переадр. причинами клиента');

execute upsert_tx('Z000000003', 'ru', 'суббота');

execute upsert_tx('Z000000004', 'ru', 'воскресенье');

execute upsert_tx('Z000000005', 'ru', 'праздничный день');

execute upsert_tx('Z000000006', 'ru', 'ночные поставки');

execute upsert_tx('Z000000007', 'ru', 'выходные');

execute upsert_tx('Z000000008', 'ru', 'Специальное время поставки');

execute upsert_tx('Z000000009', 'ru', 'Отмена поставки');

execute upsert_tx('Z000000010', 'ru', 'Логистические затраты');

execute upsert_tx('Z000000011', 'ru', 'Неполный цементовоз');

execute upsert_tx('Z000000012', 'ru', 'Вемя ожидания дополнит. плату');

execute upsert_tx('Z000000013', 'ru', 'Мульти выгрузка (Milkman tour)');

execute upsert_tx('Z000000014', 'ru', 'Частичный возврат');

execute upsert_tx('Z000000015', 'ru', 'Выгрузка паллет');

execute upsert_tx('Z000000016', 'ru', 'Порожний рейс');

execute upsert_tx('Z000000017', 'ru', 'Температура ниже "0" градусов');

execute upsert_tx('Z000000018', 'ru', 'штраф за превышение веса');

execute upsert_tx('Z000000019', 'ru', 'Беспошлинные поставки');

execute upsert_tx('Z000000020', 'ru', 'Зимняя надбавка');

execute upsert_tx('Z000000021', 'ru', 'Топливная надбавка');

execute upsert_tx('Z000000022', 'ru', 'Short-term performance rump up');

execute upsert_tx('Z000000023', 'ru', 'Дополнительная маневровка');

execute upsert_tx('Z000000024', 'ru', 'возврат');

execute upsert_tx('Z000000025', 'ru', 'маленькая машина');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - PL (ENGLISH COPY)
execute upsert_tx('Z000000001', 'pl', 'Przekierowywanie względów logist.');

execute upsert_tx('Z000000002', 'pl', 'Przekierowywanie zależnych klien.');

execute upsert_tx('Z000000003', 'pl', 'Sobota');

execute upsert_tx('Z000000004', 'pl', 'Niedziela');

execute upsert_tx('Z000000005', 'pl', 'Bank Święto');

execute upsert_tx('Z000000006', 'pl', 'Nocne dostawy');

execute upsert_tx('Z000000007', 'pl', 'Koniec tygodnia');

execute upsert_tx('Z000000008', 'pl', 'Określony czas dostawy');

execute upsert_tx('Z000000009', 'pl', 'Anulowanie dostawy');

execute upsert_tx('Z000000010', 'pl', 'Koszty logistyczne');

execute upsert_tx('Z000000011', 'pl', 'Niekompletny samochód ciężarowy');

execute upsert_tx('Z000000012', 'pl', 'Czas oczekiwania naprawę płatny');

execute upsert_tx('Z000000013', 'pl', 'Multi-Drop');

execute upsert_tx('Z000000014', 'pl', 'Częściowy zwrot');

execute upsert_tx('Z000000015', 'pl', 'Rozładunek palet');

execute upsert_tx('Z000000016', 'pl', 'Pusty przelot');

execute upsert_tx('Z000000017', 'pl', 'Temperatura poniżej 0 °');

execute upsert_tx('Z000000018', 'pl', 'Kontrola wagi opłata');

execute upsert_tx('Z000000019', 'pl', 'Dostawy po służbie');

execute upsert_tx('Z000000020', 'pl', 'Zimowe opłaty');

execute upsert_tx('Z000000021', 'pl', 'Opłata paliwowa');

execute upsert_tx('Z000000022', 'pl', 'Wyniki krótkoterminowe');

execute upsert_tx('Z000000023', 'pl', 'Dodatkowe manewry');

execute upsert_tx('Z000000024', 'pl', 'Powrót');

execute upsert_tx('Z000000025', 'pl', 'Mały samochód ciężarowy');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - AZ (ENGLISH COPY)

execute upsert_tx('Z000000001', 'az', 'Logistika səbəbi görə istiqam dəyişdi');
execute upsert_tx('Z000000002', 'az', 'Müştəri səbəbi görə istiqam dəyişdi');

execute upsert_tx('Z000000003', 'az', 'Şənbə');

execute upsert_tx('Z000000004', 'az', 'Bazar');

execute upsert_tx('Z000000005', 'az', 'Bank tətili');

execute upsert_tx('Z000000006', 'az', 'Gecə çatdırmaları');

execute upsert_tx('Z000000007', 'az', 'Həftə sonu');

execute upsert_tx('Z000000008', 'az', 'Xüsusi çatdırılma vaxtı');

execute upsert_tx('Z000000009', 'az', 'Çatdırılmanın ləğvi');

execute upsert_tx('Z000000010', 'az', 'Maddi-texniki təchizat xərcləri');

execute upsert_tx('Z000000011', 'az', 'Natamam Yük maşını');

execute upsert_tx('Z000000012', 'az', 'Gözləməyə görə əlavə haqq');

execute upsert_tx('Z000000013', 'az', 'Çoxkanallı rabitə xətti (Südçü turu)');

execute upsert_tx('Z000000014', 'az', 'Qismən qaytarma');

execute upsert_tx('Z000000015', 'az', 'Altlıq yükboşaltma');

execute upsert_tx('Z000000016', 'az', 'Boş səfər');

execute upsert_tx('Z000000017', 'az', '0 dərəcədən aşağı temperatur');

execute upsert_tx('Z000000018', 'az', 'Tərəzi yoxlamasının haqqı');

execute upsert_tx('Z000000019', 'az', 'Qeyri-iş saatlarında çatdırılmalar');

execute upsert_tx('Z000000020', 'az', 'Qış üstəliyi');

execute upsert_tx('Z000000021', 'az', 'Yanacaq üstəliyi');

execute upsert_tx('Z000000022', 'az', 'Qısamüddətli məhsuldarlıq artımı');

execute upsert_tx('Z000000023', 'az', 'Əlavə şuntlama');

execute upsert_tx('Z000000024', 'az', 'Qaytarma');

execute upsert_tx('Z000000025', 'az', 'Kiçik yük maşını');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - HR (ENGLISH COPY)
execute upsert_tx('Z000000001', 'hr', 'Preusmeravanje logist. razloga');

execute upsert_tx('Z000000002', 'hr', 'Preusmjeravanje kupčevih razloga');

execute upsert_tx('Z000000003', 'hr', 'Subota');

execute upsert_tx('Z000000004', 'hr', 'Nedjelja');

execute upsert_tx('Z000000005', 'hr', 'Praznik');

execute upsert_tx('Z000000006', 'hr', 'Noćne isporuke');

execute upsert_tx('Z000000007', 'hr', 'Vikend');

execute upsert_tx('Z000000008', 'hr', 'Specifično vrijeme isporuke');

execute upsert_tx('Z000000009', 'hr', 'Poništavanje isporuke');

execute upsert_tx('Z000000010', 'hr', 'Troškovi logistike');

execute upsert_tx('Z000000011', 'hr', 'Nepotpuni kamion');

execute upsert_tx('Z000000012', 'hr', 'Čeka se dopuna');

execute upsert_tx('Z000000013', 'hr', 'Multi-drop');

execute upsert_tx('Z000000014', 'hr', 'Djelomično vraćanje');

execute upsert_tx('Z000000015', 'hr', 'Istovar paleta');

execute upsert_tx('Z000000016', 'hr', 'Prazno putovanje');

execute upsert_tx('Z000000017', 'hr', 'Temperatura ispod 0 stupnjeva C');

execute upsert_tx('Z000000018', 'hr', 'Provjera punjive težine');

execute upsert_tx('Z000000019', 'hr', 'Isporuka izvan dužnosti');

execute upsert_tx('Z000000020', 'hr', 'Zimska naplata');

execute upsert_tx('Z000000021', 'hr', 'Naplata goriva');

execute upsert_tx('Z000000022', 'hr', 'Kratkoročna povećana realizacija');

execute upsert_tx('Z000000023', 'hr', 'Dodatno manevriranje');

execute upsert_tx('Z000000024', 'hr', 'Povrat');

execute upsert_tx('Z000000025', 'hr', 'Mali kamion');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - SR (ENGLISH COPY)
execute upsert_tx('Z000000001', 'sr', 'Prekartiravanje - logist. razlozi');

execute upsert_tx('Z000000002', 'sr', 'Prekartiravanje - kupčevi razlozi');

execute upsert_tx('Z000000003', 'sr', 'Subota');

execute upsert_tx('Z000000004', 'sr', 'Nedelja');

execute upsert_tx('Z000000005', 'sr', 'Praznik');

execute upsert_tx('Z000000006', 'sr', 'Noćne isporuke');

execute upsert_tx('Z000000007', 'sr', 'Vikend');

execute upsert_tx('Z000000008', 'sr', 'Specifično vreme isporuke');

execute upsert_tx('Z000000009', 'sr', 'Otkazivanje isporuke');

execute upsert_tx('Z000000010', 'sr', 'Troškovi logistike');

execute upsert_tx('Z000000011', 'sr', 'Nepotpun tovar');

execute upsert_tx('Z000000012', 'sr', 'Vreme čekanja sa fiksnom tarifom');

execute upsert_tx('Z000000013', 'sr', 'Multi-Drop');

execute upsert_tx('Z000000014', 'sr', 'Delimičan povraćaj');

execute upsert_tx('Z000000015', 'sr', 'Istovar paleta');

execute upsert_tx('Z000000016', 'sr', 'Prazna tura');

execute upsert_tx('Z000000017', 'sr', 'Temperatura ispod 0 stepeni');

execute upsert_tx('Z000000018', 'sr', 'Tarifa za proveru težine');

execute upsert_tx('Z000000019', 'sr', 'Vanredna isporuka');

execute upsert_tx('Z000000020', 'sr', 'Zimska tarifa');

execute upsert_tx('Z000000021', 'sr', 'Tarifa za gorivo');

execute upsert_tx('Z000000022', 'sr', 'Kratkoročne performanse');

execute upsert_tx('Z000000023', 'sr', 'Dodatni manevar');

execute upsert_tx('Z000000024', 'sr', 'Povraćaj');

execute upsert_tx('Z000000025', 'sr', 'Mali kamion');

prompt INSERTING into TLGNR_SAP_ORDER_EXTRA_COST_TX - TR

execute upsert_tx('Z000000001', 'tr', 'Rerouting due to Logistics Reasons');

execute upsert_tx('Z000000002', 'tr', 'Rerouting due to Customer Reasons');

execute upsert_tx('Z000000003', 'tr', 'Saturday');

execute upsert_tx('Z000000004', 'tr', 'Sunday');

execute upsert_tx('Z000000005', 'tr', 'Bank Holiday');

execute upsert_tx('Z000000006', 'tr', 'Night deliveries');

execute upsert_tx('Z000000007', 'tr', 'Weekend');

execute upsert_tx('Z000000008', 'tr', 'Specific delivery time');

execute upsert_tx('Z000000009', 'tr', 'Delivery cancellation');

execute upsert_tx('Z000000010', 'tr', 'Logistic costs');

execute upsert_tx('Z000000011', 'tr', 'Incomplete Truck');

execute upsert_tx('Z000000012', 'tr', 'Waiting time Fix surcharge');

execute upsert_tx('Z000000013', 'tr', 'Multi-Drop (Milkman tour)');

execute upsert_tx('Z000000014', 'tr', 'Partial Return');

execute upsert_tx('Z000000015', 'tr', 'Pallet unloading');

execute upsert_tx('Z000000016', 'tr', 'Empty trip');

execute upsert_tx('Z000000017', 'tr', 'Temperature below 0 degrees');

execute upsert_tx('Z000000018', 'tr', 'Weight check charge');

execute upsert_tx('Z000000019', 'tr', 'Off-Duty deliveries');

execute upsert_tx('Z000000020', 'tr', 'Winter surcharge');

execute upsert_tx('Z000000021', 'tr', 'Fuel surcharge');

execute upsert_tx('Z000000022', 'tr', 'Short-term performance rump up');

execute upsert_tx('Z000000023', 'tr', 'Additional shunting');

execute upsert_tx('Z000000024', 'tr', 'Return');

execute upsert_tx('Z000000025', 'tr', 'Small Truck');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
