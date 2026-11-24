-- $Revision$
-- $Date$
--
-- Add nes Status AT THE END, always
-- Reason:
-- - Do not break existing codes (constants)
-- - Do not break existing views (there, no constants are in use!!!)
--
-- State comparison with < and > in the workflow is forbidden anyway (see recently added state 18, only for rail!
-- In the UI, attribute sorting_no can be used to control the appearence in a drop down field

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_order_status.sql ----------'


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
-- tlgnr_order_status_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_order_status_cd             in number,
  p_sorting_no                  in number,
  p_possible_truck_status_flag  in number,
  p_possible_rail_status_flag   in number,
  p_ctl_act_cd                  in number)
is
  v_table_name    varchar2(48) := 'tlgnr_order_status_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_order_status_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.order_status_cd)
    into v_exists_flag
    from tlgnr_order_status_bs bs
    where bs.order_status_cd = p_order_status_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_order_status_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_order_status_bs set
/* order_status_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          sorting_no                     = p_sorting_no,
          possible_truck_status_flag     = p_possible_truck_status_flag,
          possible_rail_status_flag      = p_possible_rail_status_flag,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          order_status_cd = p_order_status_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_order_status_cd || ').');
      end if;
    else
      insert into tlgnr_order_status_bs (
        order_status_cd,
        sorting_no,
        possible_truck_status_flag,
        possible_rail_status_flag,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_order_status_cd,
        p_sorting_no,
        p_possible_truck_status_flag,
        p_possible_rail_status_flag,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
);
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_order_status_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_order_status_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_order_status_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2,
  p_warn_txt                in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_order_status_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_order_status_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.order_status_cd) into v_exists_bs_flag
    from tlgnr_order_status_bs bs
    where bs.order_status_cd = p_order_status_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.order_status_cd) into v_exists_flag
      from tlgnr_order_status_tx tx
      where tx.order_status_cd = p_order_status_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_order_status_tx set
/* p_order_status_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          warn_txt                       = p_warn_txt,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          order_status_cd = p_order_status_cd and
          lang_cd = p_lang_cd         and
          (1 = is_different_varchar2(txt, p_text) or 1 = is_different_varchar2(warn_txt, p_warn_txt))
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_order_status_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_order_status_tx (
          order_status_cd,
          lang_cd,
          txt,
          warn_txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_order_status_cd,
          p_lang_cd,
          p_text,
          p_warn_txt,
          1, systimestamp, 1, systimestamp
);
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_order_status_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_order_status_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_ORDER_STATUS_BS
execute upsert_bs( 1,  10, 1, 1, 1);
execute upsert_bs( 2,  20, 1, 0, 1);
execute upsert_bs( 3,  30, 1, 0, 1);
execute upsert_bs( 4,  40, 1, 0, 1);
execute upsert_bs( 5,  50, 1, 0, 1);
execute upsert_bs( 6,  60, 1, 1, 1);
execute upsert_bs( 7,  70, 1, 0, 1);
execute upsert_bs( 8,  80, 1, 0, 1);
execute upsert_bs( 9,  90, 1, 1, 1);
execute upsert_bs(10, 100, 1, 1, 1);
execute upsert_bs(11, 110, 1, 1, 1);
execute upsert_bs(12, 120, 1, 1, 1);
execute upsert_bs(13, 130, 1, 1, 1);
execute upsert_bs(14, 140, 1, 1, 1);
execute upsert_bs(15, 150, 1, 1, 1);
execute upsert_bs(16, 160, 1, 1, 1);
execute upsert_bs(17, 170, 1, 1, 1);
execute upsert_bs(18, 115, 0, 1, 1);
execute upsert_bs(19, 180, 1, 1, 1);
execute upsert_bs(20, 190, 1, 1, 1);
execute upsert_bs(21, 145, 1, 0, 1);

prompt INSERTING into TLGNR_ORDER_STATUS_TX - EN
execute upsert_tx( 1, 'en', 'New'                            , 'Pool assignment missing');
execute upsert_tx( 2, 'en', 'Transmitted to pool'            , 'Pool confirmation missing');
execute upsert_tx( 3, 'en', 'Confirmed by pool'              , 'Carrier assignment missing');
execute upsert_tx( 4, 'en', 'Transmitted to comm. Carrier'   , 'Carrier confirmation missing');
execute upsert_tx( 5, 'en', 'Confirmed by comm. Carrier'     , 'Order not yet scheduled');
execute upsert_tx( 6, 'en', 'Scheduled'                      , 'Order not yet submitted');
execute upsert_tx( 7, 'en', 'Released for execution'         , 'Driver confirmation missing');
execute upsert_tx( 8, 'en', 'Confirmed by driver'            , 'Not yet arrived at loadingpoint');
execute upsert_tx( 9, 'en', 'Arrived at loadingpoint'        , 'Start load missing');
execute upsert_tx(10, 'en', 'Loading'                        , 'On time departure at risk');
execute upsert_tx(11, 'en', 'Loaded'                         , 'On time delivery at risk');
execute upsert_tx(12, 'en', 'Shipping'                       , 'On time delivery at risk');
execute upsert_tx(13, 'en', 'Arrived at delivery point'      , 'Arrival too late');
execute upsert_tx(14, 'en', 'Unloading'                      , 'Arrival too late');
execute upsert_tx(15, 'en', 'Delivered'                      , 'Arrival too late');
execute upsert_tx(16, 'en', 'Finished'                       , 'Arrival too late');
execute upsert_tx(17, 'en', 'Deleted'                        , 'Arrival too late');
execute upsert_tx(18, 'en', 'Transmitted to rail company'    , 'Order not yet submitted');
execute upsert_tx(19, 'en', 'Archived'                       , 'Arrival too late');
execute upsert_tx(20, 'en', 'Archived'                       , 'Arrival too late');
execute upsert_tx(21, 'en', 'Delivered – Pending LDS Data'   , 'LDS loading was skipped');

prompt INSERTING into TLGNR_ORDER_STATUS_TX - DE
execute upsert_tx( 1, 'de', 'Neu'                                  , 'Zuteilung an Pool ausstehend');
execute upsert_tx( 2, 'de', 'An Pool übermittelt'                  , 'Poolbestätigung ausstehend');
execute upsert_tx( 3, 'de', 'Von Pool bestätigt'                   , 'Zuteilung an Transporteur ausstehend');
execute upsert_tx( 4, 'de', 'An komm. Transporteur übermittelt'    , 'Transporteurbestätigung ausstehend');
execute upsert_tx( 5, 'de', 'Von komm. Transporteur bestätigt'     , 'Auftrag noch nicht disponiert');
execute upsert_tx( 6, 'de', 'Disponiert'                           , 'Auftrag noch nicht übermittelt');
execute upsert_tx( 7, 'de', 'Freigegeben zur Ausführung'           , 'Fahrerbestätigung ausstehend');
execute upsert_tx( 8, 'de', 'Von Fahrer bestätigt'                 , 'Noch nicht an Beladestelle angekommen');
execute upsert_tx( 9, 'de', 'Ankunft Beladestelle'                 , 'Noch nicht am Beladen');
execute upsert_tx(10, 'de', 'Am Beladen'                           , 'Rechtzeitige Abfahrt unsicher');
execute upsert_tx(11, 'de', 'Beladen'                              , 'Rechtzeitige Ankunft unsicher');
execute upsert_tx(12, 'de', 'Unterwegs'                            , 'Rechtzeitige Ankunft unsicher');
execute upsert_tx(13, 'de', 'Ankunft Entladestelle'                , 'Verspätete Ankunft');
execute upsert_tx(14, 'de', 'Am Entladen'                          , 'Verspätete Ankunft');
execute upsert_tx(15, 'de', 'Geliefert'                            , 'Verspätete Ankunft');
execute upsert_tx(16, 'de', 'Abgeschlossen'                        , 'Verspätete Ankunft');
execute upsert_tx(17, 'de', 'Gelöscht'                             , 'Verspätete Ankunft');
execute upsert_tx(18, 'de', 'An Bahnunternehmen übermittelt'       , 'Auftrag noch nicht übermittelt');
execute upsert_tx(19, 'de', 'Archiviert'                             , 'Verspätete Ankunft');
execute upsert_tx(20, 'de', 'Archiviert'                             , 'Verspätete Ankunft');
execute upsert_tx(21, 'de', 'Delivered – Pending LDS Data (de)'  , 'LDS loading was skipped');

prompt INSERTING into TLGNR_ORDER_STATUS_TX - FR
execute upsert_tx( 1, 'fr', 'Nouveau'                                      , 'Pool assignment manque');
execute upsert_tx( 2, 'fr', 'Transmis au pool'                             , 'Pool confirmation manque');
execute upsert_tx( 3, 'fr', 'Confirmé par pool'                            , 'Carrier assignment manque');
execute upsert_tx( 4, 'fr', 'Transmis au comm. transporteur'               , 'Carrier confirmation manque');
execute upsert_tx( 5, 'fr', 'Confirmé par comm. transporteur'              , 'L''order n''est pas programmé');
execute upsert_tx( 6, 'fr', 'Planifié'                                     , 'L''order n''est pas transmis');
execute upsert_tx( 7, 'fr', 'Prêt pour l''exécution'                       , 'Chauffeur confirmation manque');
execute upsert_tx( 8, 'fr', 'Confirmé par le chauffeaur'                   , 'Pas encore arrivé au lieu de chargement');
execute upsert_tx( 9, 'fr', 'Arrivé au lieu de chargement'                 , 'Début de charge manque');
execute upsert_tx(10, 'fr', 'En chargement'                                , 'Départ à l''heure à risque');
execute upsert_tx(11, 'fr', 'Chargé'                                       , 'La livraison à l''heure à risque');
execute upsert_tx(12, 'fr', 'En route'                                     , 'La livraison à l''heure à risque');
execute upsert_tx(13, 'fr', 'Arrivé au lieu de déchargement'               , 'Arrivée trop tard');
execute upsert_tx(14, 'fr', 'En déchargement'                              , 'Arrivée trop tard');
execute upsert_tx(15, 'fr', 'Livré'                                        , 'Arrivée trop tard');
execute upsert_tx(16, 'fr', 'Terminé'                                      , 'Arrivée trop tard');
execute upsert_tx(17, 'fr', 'Annulé'                                       , 'Arrivée trop tard');
execute upsert_tx(18, 'fr', 'Transmis à la compagnie de chemins de fer'    , 'L''ordre n''est pas transmis');
execute upsert_tx(19, 'fr', 'Archivé'                                     , 'Arrivée trop tard');
execute upsert_tx(20, 'fr', 'Archivé'                                     , 'Arrivée trop tard');
execute upsert_tx(21, 'fr', 'Delivered – Pending LDS Data (fr)'   , 'LDS loading was skipped');

prompt INSERTING into TLGNR_ORDER_STATUS_TX - IT
execute upsert_tx( 1, 'it', 'Nuovo'                                      , null);
execute upsert_tx( 2, 'it', 'Trasmesso al pool'                          , null);
execute upsert_tx( 3, 'it', 'Confermato da pool'                         , null);
execute upsert_tx( 4, 'it', 'Trasmesso al vettore'                       , null);
execute upsert_tx( 5, 'it', 'Confermato da vettore'                      , null);
execute upsert_tx( 6, 'it', 'Schedulato'                                 , null);
execute upsert_tx( 7, 'it', 'Rilasciato per l''esecuzione'               , null);
execute upsert_tx( 8, 'it', 'Confermato dall''autista'                   , null);
execute upsert_tx( 9, 'it', 'Arrivato al luogo di carico'                , null);
execute upsert_tx(10, 'it', 'Carico in corso'                            , null);
execute upsert_tx(11, 'it', 'Carico terminato'                           , null);
execute upsert_tx(12, 'it', 'Strada facendo'                             , null);
execute upsert_tx(13, 'it', 'Arrivato al luogo di scarico'               , null);
execute upsert_tx(14, 'it', 'Scarico in corso'                           , null);
execute upsert_tx(15, 'it', 'Consegnato'                                 , null);
execute upsert_tx(16, 'it', 'Terminato'                                  , null);
execute upsert_tx(17, 'it', 'Cancellato'                                 , null);
execute upsert_tx(18, 'it', 'Trasmesso alla società ferroviarie'         , null);
execute upsert_tx(19, 'it', 'Archiviato'                                 , null);
execute upsert_tx(20, 'it', 'Archiviato'                                 , null);
execute upsert_tx(21, 'it', 'Delivered – Pending LDS Data (it)'   , 'LDS loading was skipped');

prompt INSERTING into TLGNR_ORDER_STATUS_TX - ES
execute upsert_tx( 1, 'es', 'Nuevo'                                , 'Falta signación a región');
execute upsert_tx( 2, 'es', 'Transmitido a región'                 , 'Falta confirmación de la región');
execute upsert_tx( 3, 'es', 'Confirmado por región'                , 'Falta asignación a transportista');
execute upsert_tx( 4, 'es', 'Transmitido a operador logístico'     , 'Falta confirmación transportista');
execute upsert_tx( 5, 'es', 'Confirmado por operador logístico'    , 'Pedido sin expedir aún');
execute upsert_tx( 6, 'es', 'Expedido'                             , 'Pedido sin enviar aún');
execute upsert_tx( 7, 'es', 'Listo para ejecutarse'                , 'Falta confirmación conductor');
execute upsert_tx( 8, 'es', 'Confirmado por conductor'             , 'Aún no ha llegado al punto de carga');
execute upsert_tx( 9, 'es', 'Llegada a punto de carga'             , 'Falta inicio carga');
execute upsert_tx(10, 'es', 'Cargando'                             , 'Salida a tiempo en riesgo');
execute upsert_tx(11, 'es', 'Cargado'                              , 'Entrega a tiempo en riesgo');
execute upsert_tx(12, 'es', 'Entregando'                           , 'Entrega a tiempo en riesgo');
execute upsert_tx(13, 'es', 'Llegada a punto de entrega'           , 'Llegada demasiado tarde');
execute upsert_tx(14, 'es', 'Descargando'                          , 'Llegada demasiado tarde');
execute upsert_tx(15, 'es', 'Entregado'                            , 'Llegada demasiado tarde');
execute upsert_tx(16, 'es', 'Finalizado'                           , 'Llegada demasiado tarde');
execute upsert_tx(17, 'es', 'Borrado'                              , 'Llegada demasiado tarde');
execute upsert_tx(18, 'es', 'Transmitido a compañía ferroviaria'   , 'Pedido sin enviar aún');
execute upsert_tx(19, 'es', 'Archivado'                              , 'Llegada demasiado tarde');
execute upsert_tx(20, 'es', 'Archivado'                              , 'Llegada demasiado tarde');
execute upsert_tx(21, 'es', 'Delivered – Pending LDS Data (es)'   , 'LDS loading was skipped');

prompt INSERTING into TLGNR_ORDER_STATUS_TX - CS (ENGLISH COPIED)
execute upsert_tx( 1, 'cs', 'Nový'                            , 'Přiřažení skupiny chybí');
execute upsert_tx( 2, 'cs', 'Odesláno do skupiny'            , 'Potvrzení skupiny chybí');
execute upsert_tx( 3, 'cs', 'Potvrzeno ve skupině'              , 'Přiřazení dopravce chybí');
execute upsert_tx( 4, 'cs', 'Odesláno komerčnímu dopravci'   , 'Potvrzení dopravce chybí');
execute upsert_tx( 5, 'cs', 'Potvrzeno komerčním dopravcem'     , 'Zakázka není ještě naplánována');
execute upsert_tx( 6, 'cs', 'Naplánováno'                      , 'Zakázka není ještě odeslána');
execute upsert_tx( 7, 'cs', 'Uvolněno k provedení'         , 'Potvrzení řidičem chybí');
execute upsert_tx( 8, 'cs', 'Potvrzeno řidičem'            , 'Ještě nepřijel na nakládku');
execute upsert_tx( 9, 'cs', 'Příjezd na nakládku'        , 'Začátek nakládky chybí');
execute upsert_tx(10, 'cs', 'Nakládka'                        , 'Dodávka na čas s rizikem');
execute upsert_tx(11, 'cs', 'Naloženo'                         , 'Dodávka na čas s rizikem');
execute upsert_tx(12, 'cs', 'Přeprava'                       , 'Dodávka na čas s rizikem');
execute upsert_tx(13, 'cs', 'Příjezd na vykládku'      , 'Příjezd příliš pozdě');
execute upsert_tx(14, 'cs', 'Vykládka'                      , 'Příjezd příliš pozdě');
execute upsert_tx(15, 'cs', 'Dodáno'                      , 'Příjezd příliš pozdě');
execute upsert_tx(16, 'cs', 'Ukončeno'                       , 'Příjezd příliš pozdě');
execute upsert_tx(17, 'cs', 'Vymazáno'                        , 'Příjezd příliš pozdě');
execute upsert_tx(18, 'cs', 'Odesláno železniční společnosti'    , 'Zakázka nebyla ještě odeslána');
execute upsert_tx(19, 'cs', 'Archived'                        , 'Příjezd příliš pozdě');
execute upsert_tx(20, 'cs', 'Archived'                        , 'Příjezd příliš pozdě');
execute upsert_tx(21, 'cs', 'Delivered – Pending LDS Data (cs)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - SK (ENGLISH COPIED)
execute upsert_tx( 1, 'sk', 'Nový'                            , 'Pridelenie pre región chýba');
execute upsert_tx( 2, 'sk', 'Poslané pre región'            , 'Potvrdenie pre región chýba');
execute upsert_tx( 3, 'sk', 'Potvrdené pre región'              , 'Pridelenie pre dopravcu chýba');
execute upsert_tx( 4, 'sk', 'Poslané pre komerčného dopravcu'   , 'Potvrdenie pre dopravcu chýba');
execute upsert_tx( 5, 'sk', 'Potvrdené pre región'     , 'Objednávka ešte nebola plánovaná');
execute upsert_tx( 6, 'sk', 'Plánované'                      , 'Objednávke ešte nepodaná');
execute upsert_tx( 7, 'sk', 'Uvoľnené k realizácii'         , 'Potvrdenie vodiča chýba');
execute upsert_tx( 8, 'sk', 'Potvrdené vodičom'            , 'Ešte sa nedostavil na miesto nakládky');
execute upsert_tx( 9, 'sk', 'Dostavil sa na miesto nakládky'        , 'Chýba začiatok nakládky');
execute upsert_tx(10, 'sk', 'Nakládka'                        , 'Príchod na čas v ohrození');
execute upsert_tx(11, 'sk', 'Naložené'                         , 'Včasná dodávka ohrozená');
execute upsert_tx(12, 'sk', 'Na ceste'                       , 'Včasná dodávka ohrozená');
execute upsert_tx(13, 'sk', 'Dorazil na miesto dodania'      , 'Neskorý príchod');
execute upsert_tx(14, 'sk', 'Vykládka'                      , 'Neskorý príchod');
execute upsert_tx(15, 'sk', 'Dodané'                      , 'Neskorý príchod');
execute upsert_tx(16, 'sk', 'Ukončené'                       , 'Neskorý príchod');
execute upsert_tx(17, 'sk', 'Vymazané'                        , 'Neskorý príchod');
execute upsert_tx(18, 'sk', 'Poslané železničnej spoločnosti'    , 'Objednávka nebola postúpená');
execute upsert_tx(19, 'sk', 'Archivované'                        , 'Neskorý príchod');
execute upsert_tx(20, 'sk', 'Archivované'                        , 'Neskorý príchod');
execute upsert_tx(21, 'sk', 'Delivered – Pending LDS Data (sk)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - HU (ENGLISH COPIED)
execute upsert_tx( 1, 'hu', 'Új'                            , 'Készletezési feladat hiányzik');
execute upsert_tx( 2, 'hu', 'Továbbított a készletre'            , 'Készlet visszaigazolási hiányzik');
execute upsert_tx( 3, 'hu', 'Készlet általi megerősítés'              , 'Fuvarozó hozzárendelése hiányzik');
execute upsert_tx( 4, 'hu', 'Kereskedelmi fuvarozói bevételezés'   , 'Fuvarozó visszaigazolás hiányzik');
execute upsert_tx( 5, 'hu', 'Kereskedelmi fuvarozó általi megerősítés'     , 'A rendelés még nem ütemezett');
execute upsert_tx( 6, 'hu', 'Ütemezett'                      , 'A rendelés még nem benyújtott');
execute upsert_tx( 7, 'hu', 'Végrehajtás kiadása'         , 'Vezetői megereősítésének hiányzik');
execute upsert_tx( 8, 'hu', 'Vezető általi megerősítés'            , 'Még nem érkezett meg a felrakó helyre');
execute upsert_tx( 9, 'hu', 'Megérkezett a felrakó helyre'        , 'Töltési kezdés hiánya');
execute upsert_tx(10, 'hu', 'Töltés'                        , 'Időben való indulás veszélye');
execute upsert_tx(11, 'hu', 'Megtöltött'                         , 'Időben történő szállítás rizikója');
execute upsert_tx(12, 'hu', 'Szállítás'                       , 'Időben történő szállítás rizikója');
execute upsert_tx(13, 'hu', 'Megérkezett a szállítási címhez'      , 'Késői érkezés');
execute upsert_tx(14, 'hu', 'Leadás'                      , 'Késői érkezés');
execute upsert_tx(15, 'hu', 'Kiszállítva'                      , 'Késői érkezés');
execute upsert_tx(16, 'hu', 'Vége'                       , 'Késői érkezés');
execute upsert_tx(17, 'hu', 'Törölt'                        , 'Késői érkezés');
execute upsert_tx(18, 'hu', 'Vasúti társaság által továbbított'    , 'A rendelés még nem benyújtott');
execute upsert_tx(19, 'hu', 'Archivált'                        , 'Késői érkezés');
execute upsert_tx(20, 'hu', 'Archivált'                        , 'Késői érkezés');
execute upsert_tx(21, 'hu', 'Delivered – Pending LDS Data (hu)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - RO (ENGLISH COPIED)
execute upsert_tx( 1, 'ro', 'Nou'                            , 'Lipseste atribuire regiune');
execute upsert_tx( 2, 'ro', 'Transmis regiunii'            , 'Lipsa confirmare regiune');
execute upsert_tx( 3, 'ro', 'Confirmat de regiune'              , 'Lipsa atribuire transportator');
execute upsert_tx( 4, 'ro', 'Transmis operatorului logistic'   , 'Lipsa confirmare transportator');
execute upsert_tx( 5, 'ro', 'Confirmat de catre operatorul logistic'     , 'Comanda nu este inca planificata');
execute upsert_tx( 6, 'ro', 'Planificat'                      , 'Comanda nu a fost inca transmisa');
execute upsert_tx( 7, 'ro', 'Trimis pentru executie'         , 'Lipsa confirmare sofer');
execute upsert_tx( 8, 'ro', 'Confirmat de catre sofer'            , 'Nu a sosit inca la Punctul de incarcare');
execute upsert_tx( 9, 'ro', 'Sosit la Punctul de incarcare'        , 'Lipsa incepere incarcare');
execute upsert_tx(10, 'ro', 'Incarcare'                        , 'Plecare la timp in pericol');
execute upsert_tx(11, 'ro', 'Incarcat'                         , 'Livrare la timp in pericol');
execute upsert_tx(12, 'ro', 'Expeditie'                       , 'Livrare la timp in pericol');
execute upsert_tx(13, 'ro', 'Sosit la Punctul de livrare'      , 'Sosire prea intarziata');
execute upsert_tx(14, 'ro', 'Descarcare'                      , 'Sosire prea intarziata');
execute upsert_tx(15, 'ro', 'Livrat'                      , 'Sosire prea intarziata');
execute upsert_tx(16, 'ro', 'Terminat'                       , 'Sosire prea intarziata');
execute upsert_tx(17, 'ro', 'Sters'                        , 'Sosire prea intarziata');
execute upsert_tx(18, 'ro', 'Transmis companiei feroviare'    , 'Comanda nu a fost inca transmisa');
execute upsert_tx(19, 'ro', 'Arhivat'                        , 'Sosire prea intarziata');
execute upsert_tx(20, 'ro', 'Arhivat'                        , 'Sosire prea intarziata');
execute upsert_tx(21, 'ro', 'Delivered – Pending LDS Data (ro)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - BG (ENGLISH COPIED)
execute upsert_tx( 1, 'bg', 'Нов'                            , 'Липсва назначен Pool');
execute upsert_tx( 2, 'bg', 'Предаден на Pool'            , 'Липсва потвърждение за Pool');
execute upsert_tx( 3, 'bg', 'Потвърден от Pool'              , 'Липсва назначен превозвач');
execute upsert_tx( 4, 'bg', 'Изпратен до търговски превозвач'   , 'Липсва потвърждение за превозвач');
execute upsert_tx( 5, 'bg', 'Потвърден от търговски превозвач'     , 'Поръчката още няма график');
execute upsert_tx( 6, 'bg', 'Планиран'                      , 'Поръчката още не е предоставена');
execute upsert_tx( 7, 'bg', 'Освободен за изпълнение'         , 'Липсва потвърждение от шофьра');
execute upsert_tx( 8, 'bg', 'Потвърден от шофьор'            , 'Все още не е пристигнал в товарене');
execute upsert_tx( 9, 'bg', 'Пристигане на мястото за товарене'        , 'Липсва започване на товарене');
execute upsert_tx(10, 'bg', 'Товарене'                        , 'Времето на изпращане е рисково');
execute upsert_tx(11, 'bg', 'Натоварен'                         , 'Времето на доставяне е рисково');
execute upsert_tx(12, 'bg', 'Експедиция'                       , 'Времето на доставяне е рисково');
execute upsert_tx(13, 'bg', 'Пристигане на мястото на доставяне'      , 'Твърде късно пристигане');
execute upsert_tx(14, 'bg', 'Разтоварване'                      , 'Твърде късно пристигане');
execute upsert_tx(15, 'bg', 'Доставен'                      , 'Твърде късно пристигане');
execute upsert_tx(16, 'bg', 'Приключен'                       , 'Твърде късно пристигане');
execute upsert_tx(17, 'bg', 'Изтрит'                        , 'Твърде късно пристигане');
execute upsert_tx(18, 'bg', 'Предаден на жп компания'    , 'Поръчката още не е предадена');
execute upsert_tx(19, 'bg', 'Архивиран'                        , 'Твърде късно пристигане');
execute upsert_tx(20, 'bg', 'Архивиран'                        , 'Твърде късно пристигане');
execute upsert_tx(21, 'bg', 'Delivered – Pending LDS Data (bg)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - RU (ENGLISH COPY)
execute upsert_tx( 1, 'ru', 'новый'                            , 'Не внесен в Список заказов');
execute upsert_tx( 2, 'ru', 'Передан в Список заказов'            , 'Передан в Список заказов');
execute upsert_tx( 3, 'ru', 'Подствержден в Списке заказов'              , 'Назначение перевозчика отсутствует');
execute upsert_tx( 4, 'ru', 'передано перевозчику'   , 'отсутствует подтверждение перевозчика');
execute upsert_tx( 5, 'ru', 'подтверждено перевозчиком'     , 'заказ еще не запланирован');
execute upsert_tx( 6, 'ru', 'запланирован'                      , 'Заказ еще не представлен');
execute upsert_tx( 7, 'ru', 'Разрешен к исполнению'         , 'отсутствует подтверждение водителя ');
execute upsert_tx( 8, 'ru', 'подтверждено водителем'            , 'еще не прибыл на пункт погрузки');
execute upsert_tx( 9, 'ru', 'прибыл на пункт погрузки'        , 'ожидание начала погрузки');
execute upsert_tx(10, 'ru', 'погрузка'                        , 'Риск опоздания выезда');
execute upsert_tx(11, 'ru', 'загружено'                         , 'Риск опоздания доставки');
execute upsert_tx(12, 'ru', 'доставка'                       , 'Риск опоздания доставки');
execute upsert_tx(13, 'ru', 'прибыл на пункт разгрузки'      , 'опоздал');
execute upsert_tx(14, 'ru', 'выгрузка'                      , 'опоздал');
execute upsert_tx(15, 'ru', 'доставлено'                      , 'опоздал');
execute upsert_tx(16, 'ru', 'окончание'                       , 'опоздал');
execute upsert_tx(17, 'ru', 'удалено'                        , 'опоздал');
execute upsert_tx(18, 'ru', 'передано в жд компанию'    , 'Заказ еще не представлен');
execute upsert_tx(19, 'ru', 'Архивировано'                        , 'опоздал');
execute upsert_tx(20, 'ru', 'Архивировано'                        , 'опоздал');
execute upsert_tx(21, 'ru', 'Delivered – Pending LDS Data (ru)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - PL
execute upsert_tx( 1, 'pl', 'Nowy'                            , 'Brak przypisanego regionu');
execute upsert_tx( 2, 'pl', 'Nadano do regionu'            , 'Brak potwierdzenia regionu');
execute upsert_tx( 3, 'pl', 'Potwierdzono przez region'              , 'Brak ustaleń z przewoźnikiem');
execute upsert_tx( 4, 'pl', 'Przekazano do przewoźnika'   , 'Brak potwierdzenia przewoźnika');
execute upsert_tx( 5, 'pl', 'Potwierdzone przez przewoźnika'     , 'Zamówienie jeszcze nie zaplanowane');
execute upsert_tx( 6, 'pl', 'Zaplanowano'                      , 'Zamówienie jeszcze nie potwierdzone');
execute upsert_tx( 7, 'pl', 'Dopuszczone do realizacji'         , 'Brak potwierdzenia od kierowcy');
execute upsert_tx( 8, 'pl', 'Potwierdzone przez kierowcę'            , 'Jeszcze nie dotarł na punkt załadunkowy');
execute upsert_tx( 9, 'pl', 'Przyjazd na punkt załadunkowy'        , 'Nie rozpoczęto załadunku');
execute upsert_tx(10, 'pl', 'Załadunek'                        , 'Zagrożony wyjazd o czasie');
execute upsert_tx(11, 'pl', 'Załadowany'                         , 'Zagrożona dostawa o czasie');
execute upsert_tx(12, 'pl', 'Spedycja'                       , 'Zagrożona dostawa o czasie');
execute upsert_tx(13, 'pl', 'Przyjazd na miejsce dostawy'      , 'Przyjazd spóźniony');
execute upsert_tx(14, 'pl', 'Rozładunek'                      , 'Przyjazd spóźniony');
execute upsert_tx(15, 'pl', 'Dostarczono'                      , 'Przyjazd spóźniony');
execute upsert_tx(16, 'pl', 'Zakończony'                       , 'Przyjazd spóźniony');
execute upsert_tx(17, 'pl', 'Usunięto'                        , 'Przyjazd spóźniony');
execute upsert_tx(18, 'pl', 'Przekazane firmie kolejowej'    , 'Zamówienie jeszcze nie potwierdzone');
execute upsert_tx(19, 'pl', 'Archived'                        , 'Przyjazd spóźniony');
execute upsert_tx(20, 'pl', 'Archived'                        , 'Przyjazd spóźniony');
execute upsert_tx(21, 'pl', 'Delivered – Pending LDS Data (pl)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - AZ (ENGLISH COPY)
execute upsert_tx( 1, 'az', 'Yeni'                            , 'Park tapşırığının buraxılması');
execute upsert_tx( 2, 'az', 'Poola köçürülüb'            , 'Park təsdiqinin buraxılması');
execute upsert_tx( 3, 'az', 'Pool tərəfindən təsdiq edilib'              , 'Daşıyıcı tapşırığının buraxılması');
execute upsert_tx( 4, 'az', 'Köçürülüb kommersiya daşıyıcısına'   , 'Daşıyıcı təsdiqinin buraxılması');
execute upsert_tx( 5, 'az', 'Kommersiya daşıyıcısı tərəfindən təsdiq edilib'     , 'Sifariş not yet qrafikə salınıb');
execute upsert_tx( 6, 'az', 'Qrafikə salınıb'                      , 'Sifariş hələ təqdim edilməyib');
execute upsert_tx( 7, 'az', 'İcraya buraxılıb'         , 'Sürücü təsdiqinin buraxılması');
execute upsert_tx( 8, 'az', 'Sürücü tərəfindən təsdiq edilib'            , 'Hələ yükləmə məntəqəsinə gəlməyib');
execute upsert_tx( 9, 'az', 'Yükləmə məntəqəsinə gəlib'        , 'Başlanğıc yükün buraxılması');
execute upsert_tx(10, 'az', 'Yükləmə'                        , 'Yola düşmə vaxtı risklidir');
execute upsert_tx(11, 'az', 'Yüklənib'                         , 'Çatdırılma vaxtı risklidir');
execute upsert_tx(12, 'az', 'Nəqletmə'                       , 'Çatdırılma vaxtı risklidir');
execute upsert_tx(13, 'az', 'Tədarük məntəqəsinə gəlib'      , 'Gəlmə həddindən artıq gecikir');
execute upsert_tx(14, 'az', 'Yükboşaltma'                      , 'Gəlmə həddindən artıq gecikir');
execute upsert_tx(15, 'az', 'Çatdırılıb'                      , 'Gəlmə həddindən artıq gecikir');
execute upsert_tx(16, 'az', 'Başa çatıb'                       , 'Gəlmə həddindən artıq gecikir');
execute upsert_tx(17, 'az', 'Silinib'                        , 'Gəlmə həddindən artıq gecikir');
execute upsert_tx(18, 'az', 'Dəmiryolu şirkətinə köçürülüb'    , 'Sifariş hələ təqdim edilməyib');
execute upsert_tx(19, 'az', 'Arxivə verilib'                        , 'Gəlmə həddindən artıq gecikir');
execute upsert_tx(20, 'az', 'Arxivə verilib'                        , 'Gəlmə həddindən artıq gecikir');
execute upsert_tx(21, 'az', 'Delivered – Pending LDS Data (az)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - HR
execute upsert_tx( 1, 'hr', 'Novi'                            , 'Nedostaje raspored segmenta');
execute upsert_tx( 2, 'hr', 'Preneseno na segment'            , 'Nedostaje potvrda segmenta');
execute upsert_tx( 3, 'hr', 'Potvrđeno od strane  segmenta'              , 'Nedostaje raspored prijevoznika');
execute upsert_tx( 4, 'hr', 'Prenoseno na  prijevoznika'   , 'Nedostaje potvrda prijevoznika');
execute upsert_tx( 5, 'hr', 'Potvrđeno od strane prijevoznika'     , 'Narudžba još nije raspoređena');
execute upsert_tx( 6, 'hr', 'Planiran'                      , 'Narudžba još nije podnesena');
execute upsert_tx( 7, 'hr', 'Objavljeno za izvršenje'         , 'Nedostaje potvrda vozača');
execute upsert_tx( 8, 'hr', 'Potvrđeno od strane vozača'            , 'Još nije stigao na utovar');
execute upsert_tx( 9, 'hr', 'Stigao na utovar '        , 'Nedostaje početak utovara');
execute upsert_tx(10, 'hr', 'Utovar'                        , 'Na vrijeme odlazak u opasnosti');
execute upsert_tx(11, 'hr', 'Utovaren'                         , 'Isporuka na vrijeme u opasnosti');
execute upsert_tx(12, 'hr', 'Isporuka'                       , 'Na vrijeme isporuka u opasnosti');
execute upsert_tx(13, 'hr', 'Dolazak na istovar'      , 'Dolazak prekasno');
execute upsert_tx(14, 'hr', 'Istovar'                      , 'Dolazak prekasno');
execute upsert_tx(15, 'hr', 'Isporučeno'                      , 'Dolazak prekasno');
execute upsert_tx(16, 'hr', 'Gotov'                       , 'Dolazak prekasno');
execute upsert_tx(17, 'hr', 'Izbrisano'                        , 'Dolazak prekasno');
execute upsert_tx(18, 'hr', 'Prenoseno na željezničko poduzeće'    , 'Narudžba još nije podnesena');
execute upsert_tx(19, 'hr', 'Arhivirano'                        , 'Dolazak prekasno');
execute upsert_tx(20, 'hr', 'Arhivirano'                        , 'Dolazak prekasno');
execute upsert_tx(21, 'hr', 'Delivered – Pending LDS Data (hr)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - SR
execute upsert_tx( 1, 'sr', 'Novi'                            , 'Nedostaje raspored segmenta');
execute upsert_tx( 2, 'sr', 'Upisan segment'            , 'Nedostaje potvrda segmenta');
execute upsert_tx( 3, 'sr', 'Potvrđeno - segment'              , 'Nedostaje raspored prevoznika');
execute upsert_tx( 4, 'sr', 'Dodeljen prevoznik'   , 'Nedostaje potvrda prevoznika');
execute upsert_tx( 5, 'sr', 'Potvrđeno - prevoznik'     , 'Nalog još nije raspoređen');
execute upsert_tx( 6, 'sr', 'Raspoređeno'                      , 'Nalog još nije potvrđen');
execute upsert_tx( 7, 'sr', 'Spreman za obradu'         , 'Nedostaje potvrda vozača');
execute upsert_tx( 8, 'sr', 'Potvrđeno - vozač '            , 'Još nije pristigao na utovarno mesto');
execute upsert_tx( 9, 'sr', 'Na utovarnom mestu'        , 'Nedostaje početak utovara');
execute upsert_tx(10, 'sr', 'Utovar'                        , 'Dolazak na vreme - rizik');
execute upsert_tx(11, 'sr', 'Istovareno'                         , 'Isporuka na vreme - rizik');
execute upsert_tx(12, 'sr', 'Isporuka'                       , 'Isporuka na vreme - rizik');
execute upsert_tx(13, 'sr', 'Na istovarnom mestu'      , 'Prekasan odlazak');
execute upsert_tx(14, 'sr', 'Istovar'                      , 'Prekasan odlazak');
execute upsert_tx(15, 'sr', 'Isporučeno'                      , 'Prekasan odlazak');
execute upsert_tx(16, 'sr', 'Realizovano'                       , 'Prekasan odlazak');
execute upsert_tx(17, 'sr', 'Izbrisano'                        , 'Prekasan odlazak');
execute upsert_tx(18, 'sr', 'Dodeljen železnici'    , 'Nalog još nije potvrđen');
execute upsert_tx(19, 'sr', 'Archived'                        , 'Prekasan odlazak');
execute upsert_tx(20, 'sr', 'Archived'                        , 'Prekasan odlazak');
execute upsert_tx(21, 'sr', 'Delivered – Pending LDS Data (sr)'   , 'LDS loading was skipped');


prompt INSERTING into TLGNR_ORDER_STATUS_TX - TR
execute upsert_tx( 1, 'tr', 'New'                            , 'Pool assignment missing');
execute upsert_tx( 2, 'tr', 'Transmitted to pool'            , 'Pool confirmation missing');
execute upsert_tx( 3, 'tr', 'Confirmed by pool'              , 'Carrier assignment missing');
execute upsert_tx( 4, 'tr', 'Transmitted to comm. Carrier'   , 'Carrier confirmation missing');
execute upsert_tx( 5, 'tr', 'Confirmed by comm. Carrier'     , 'Order not yet scheduled');
execute upsert_tx( 6, 'tr', 'Scheduled'                      , 'Order not yet submitted');
execute upsert_tx( 7, 'tr', 'Released for execution'         , 'Driver confirmation missing');
execute upsert_tx( 8, 'tr', 'Confirmed by driver'            , 'Not yet arrived at loadingpoint');
execute upsert_tx( 9, 'tr', 'Arrived at loadingpoint'        , 'Start load missing');
execute upsert_tx(10, 'tr', 'Loading'                        , 'On time departure at risk');
execute upsert_tx(11, 'tr', 'Loaded'                         , 'On time delivery at risk');
execute upsert_tx(12, 'tr', 'Shipping'                       , 'On time delivery at risk');
execute upsert_tx(13, 'tr', 'Arrived at delivery point'      , 'Arrival too late');
execute upsert_tx(14, 'tr', 'Unloading'                      , 'Arrival too late');
execute upsert_tx(15, 'tr', 'Delivered'                      , 'Arrival too late');
execute upsert_tx(16, 'tr', 'Finished'                       , 'Arrival too late');
execute upsert_tx(17, 'tr', 'Deleted'                        , 'Arrival too late');
execute upsert_tx(18, 'tr', 'Transmitted to rail company'    , 'Order not yet submitted');
execute upsert_tx(19, 'tr', 'Archived'                       , 'Arrival too late');
execute upsert_tx(20, 'tr', 'Archived'                       , 'Arrival too late');
execute upsert_tx(21, 'tr', 'Delivered – Pending LDS Data (tr)'   , 'LDS loading was skipped');


commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
