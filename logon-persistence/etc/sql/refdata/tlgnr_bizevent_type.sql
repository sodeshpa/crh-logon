-- $Revision$
-- $Date$
--
-- List comes from several sources:
-- - All Order status are a potential source for Business events (triggered by the application)
-- - All additional MOP Input Events, such as geo

--LOGON-4819: Added Spanish language support (2012.05.16)

prompt '---------- refdata/tlgnr_bizevent_type.sql ----------'

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
-- tlgnr_bizevent_type_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_bizevent_type_cd   in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_bizevent_type_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_bizevent_type_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.bizevent_type_cd)
    into v_exists_flag
    from tlgnr_bizevent_type_bs bs
    where bs.bizevent_type_cd = p_bizevent_type_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_bizevent_type_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_bizevent_type_bs set
/* bizevent_type_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          bizevent_type_cd = p_bizevent_type_cd and
          ctl_act_cd != p_ctl_act_cd  /* not nullable */
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_bizevent_type_cd || ').');
      end if;
    else
      insert into tlgnr_bizevent_type_bs (
        bizevent_type_cd,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_bizevent_type_cd,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_bizevent_type_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_bizevent_type_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_bizevent_type_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_bizevent_type_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_bizevent_type_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.bizevent_type_cd) into v_exists_bs_flag
    from tlgnr_bizevent_type_bs bs
    where bs.bizevent_type_cd = p_bizevent_type_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.bizevent_type_cd) into v_exists_flag
      from tlgnr_bizevent_type_tx tx
      where tx.bizevent_type_cd = p_bizevent_type_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_bizevent_type_tx set
/* p_bizevent_type_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          bizevent_type_cd = p_bizevent_type_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_bizevent_type_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_bizevent_type_tx (
          bizevent_type_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_bizevent_type_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_bizevent_type_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_bizevent_type_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_BIZEVENT_TYPE_BS

-- Events based on Order status.
-- Having them in different ranges does NOT mean, that it's ok to assume that bizevent-100 = order_status !!!
-- Any mapping must be done explicitely in code or mapping tables.
-- This is only to allow adding additionally needed event types into their category.
execute upsert_bs(101, 1);
execute upsert_bs(102, 1);
execute upsert_bs(103, 1);
execute upsert_bs(104, 1);
execute upsert_bs(105, 1);
execute upsert_bs(106, 1);
execute upsert_bs(107, 1);
execute upsert_bs(108, 1);
execute upsert_bs(109, 1);
execute upsert_bs(110, 1);
execute upsert_bs(111, 1);
execute upsert_bs(112, 1);
execute upsert_bs(113, 1);
execute upsert_bs(114, 1);
execute upsert_bs(115, 1);
execute upsert_bs(116, 1);
execute upsert_bs(117, 1);
execute upsert_bs(118, 1);
execute upsert_bs(119, 1);
execute upsert_bs(120, 1);
execute upsert_bs(121, 1);
execute upsert_bs(122, 1);
execute upsert_bs(123, 1);
execute upsert_bs(124, 1);
execute upsert_bs(125, 1);
execute upsert_bs(126, 1);
execute upsert_bs(127, 1);
execute upsert_bs(130, 1);

-- Events for assignment handling
execute upsert_bs(150, 1);
execute upsert_bs(151, 1);
execute upsert_bs(152, 1);
execute upsert_bs(153, 1);
execute upsert_bs(154, 1);
execute upsert_bs(155, 1);
execute upsert_bs(160, 1);
execute upsert_bs(161, 1);
execute upsert_bs(162, 1);
execute upsert_bs(163, 1);
execute upsert_bs(165, 1);
execute upsert_bs(170, 1);
execute upsert_bs(171, 1);
execute upsert_bs(172, 1);
execute upsert_bs(173, 1);
execute upsert_bs(174, 1);
execute upsert_bs(175, 1);

-- Additional Events from MOP
-- Those correspond directly to Order-Status business events
-- - orderDataAck           : transmitted to truck (8)
-- - enterShippingPoint     : arrived at loadingpoint (9)
-- - startLoad/startLoadMop : loading (10)
-- - ...
-- switchTruck, changeTruckComposition, useTruckComposition
execute upsert_bs(201, 1);
execute upsert_bs(202, 1);
execute upsert_bs(203, 1);
-- trafficJamStart, trafficJamEnd
execute upsert_bs(210, 1);
execute upsert_bs(211, 1);

--edit field bizevent
execute upsert_bs(220, 1);
--reallocation bizevent
execute upsert_bs(166, 1);

-- Send to SAP events (success)
execute upsert_bs(300, 1);
execute upsert_bs(301, 1);
execute upsert_bs(302, 1);
execute upsert_bs(303, 1);
execute upsert_bs(304, 1);
-- Send to SAP events (failure)
execute upsert_bs(310, 1);
execute upsert_bs(311, 1);
execute upsert_bs(312, 1);
execute upsert_bs(313, 1);
execute upsert_bs(314, 1);
execute upsert_bs(315, 1);

-- Forced sending to SAP
execute upsert_bs(221, 1);

--Regularization
execute upsert_bs(316, 1);

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - EN
execute upsert_tx(101, 'en', 'New');
execute upsert_tx(102, 'en', 'Transmitted to pool');
execute upsert_tx(103, 'en', 'Confirmed by pool');
execute upsert_tx(104, 'en', 'Transmitted to comm. carrier');
execute upsert_tx(105, 'en', 'Confirmed by comm. carrier');
execute upsert_tx(106, 'en', 'Scheduled');
execute upsert_tx(107, 'en', 'Released for execution');
execute upsert_tx(108, 'en', 'Confirmed by driver');
execute upsert_tx(109, 'en', 'Arrived at loadingpoint');
execute upsert_tx(110, 'en', 'Loading');
execute upsert_tx(111, 'en', 'Loaded');
execute upsert_tx(112, 'en', 'Shipping');
execute upsert_tx(113, 'en', 'Arrived at delivery point');
execute upsert_tx(114, 'en', 'Unloading');
execute upsert_tx(115, 'en', 'Delivered');
execute upsert_tx(116, 'en', 'Finished');
execute upsert_tx(117, 'en', 'Deleted');
execute upsert_tx(119, 'en', 'Transmitted to rail company');
execute upsert_tx(118, 'en', 'Position');
execute upsert_tx(120, 'en', 'Comm. carrier set');
execute upsert_tx(121, 'en', 'Set pool');
execute upsert_tx(122, 'en', 'Pool ack');
execute upsert_tx(123, 'en', 'Carrier ack');
execute upsert_tx(124, 'en', 'Cancel on weighing system');
execute upsert_tx(125, 'en', 'Exec. carrier set');
execute upsert_tx(126, 'en', 'Exec carrier rejects');
execute upsert_tx(127, 'en', 'Comm. carrier rejects');
execute upsert_tx(130, 'en', 'Loading aborted');
execute upsert_tx(150, 'en', 'Pre-loading ass. tr. to truck');
execute upsert_tx(151, 'en', 'Pre-loading ass. tr. to truck*');
execute upsert_tx(152, 'en', 'Return ass. tr. to truck');
execute upsert_tx(153, 'en', 'Return ass. tr. to truck*');
execute upsert_tx(154, 'en', 'Pre-loading ass. cancelled');
execute upsert_tx(155, 'en', 'Return ass. cancelled');
execute upsert_tx(160, 'en', 'Assignment cancelled');
execute upsert_tx(161, 'en', 'Ext. assignment created');
execute upsert_tx(162, 'en', 'Assignment edited');
execute upsert_tx(163, 'en', 'Ext. assignment cancelled');
execute upsert_tx(165, 'en', 'Truck returned');
execute upsert_tx(166, 'en', 'Order reallocated');
execute upsert_tx(170, 'en', 'Delivery note');
execute upsert_tx(171, 'en', 'Set silo');
execute upsert_tx(172, 'en', 'Left delivery point');
execute upsert_tx(173, 'en', 'Scan wagon');
execute upsert_tx(174, 'en', 'Scan container');
execute upsert_tx(175, 'en', 'Modified finished order');
execute upsert_tx(201, 'en', 'Truck/Driver changed');
execute upsert_tx(202, 'en', 'Truck composition changed');
execute upsert_tx(203, 'en', 'Truck composition confirmed');
execute upsert_tx(210, 'en', 'Trafficjam start');
execute upsert_tx(211, 'en', 'Trafficjam end');
execute upsert_tx(220, 'en', 'Edited');
execute upsert_tx(221, 'en', 'Forced sending to SAP');
execute upsert_tx(300, 'en', 'Delivery doc sent');
execute upsert_tx(301, 'en', 'Shipment doc sent');
execute upsert_tx(302, 'en', 'Transfer doc sent');
execute upsert_tx(303, 'en', 'CN delivery doc sent');
execute upsert_tx(304, 'en', 'CN shipment doc sent');
execute upsert_tx(310, 'en', 'Delivery doc send failed');
execute upsert_tx(311, 'en', 'Shipment doc send failed');
execute upsert_tx(312, 'en', 'Transfer doc send failed');
execute upsert_tx(313, 'en', 'CN delivery doc send failed');
execute upsert_tx(314, 'en', 'CN shipment doc send failed');
execute upsert_tx(315, 'en', 'Updated Scheduling');
execute upsert_tx(316, 'en', 'Regularization');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - DE
execute upsert_tx(101, 'de', 'Neu');
execute upsert_tx(102, 'de', 'An Pool übermittelt');
execute upsert_tx(103, 'de', 'Von Pool bestätigt');
execute upsert_tx(104, 'de', 'An Komm. Transporteur übermittelt');
execute upsert_tx(105, 'de', 'Von Komm. Transporteur bestätigt');
execute upsert_tx(106, 'de', 'Disponiert');
execute upsert_tx(107, 'de', 'Freigegeben zur Ausführung');
execute upsert_tx(108, 'de', 'Von Fahrer bestätigt');
execute upsert_tx(109, 'de', 'Ankunft Beladestelle');
execute upsert_tx(110, 'de', 'Am Beladen');
execute upsert_tx(111, 'de', 'Beladen');
execute upsert_tx(112, 'de', 'Unterwegs');
execute upsert_tx(113, 'de', 'Ankunft Entladestelle');
execute upsert_tx(114, 'de', 'Am Entladen');
execute upsert_tx(115, 'de', 'Geliefert');
execute upsert_tx(116, 'de', 'Abgeschlossen');
execute upsert_tx(117, 'de', 'Gelöscht');
execute upsert_tx(118, 'de', 'Position');
execute upsert_tx(119, 'de', 'An Bahnunternehmen übermittelt');
execute upsert_tx(120, 'de', 'Kommerzieller Spediteur gesetzt');
execute upsert_tx(121, 'de', 'Pool gesetzt');
execute upsert_tx(122, 'de', 'Pool bestätigt');
execute upsert_tx(123, 'de', 'Carrier bestätigt');
execute upsert_tx(124, 'de', 'Abbrechen auf Wägesystem');
execute upsert_tx(125, 'de', 'Ausführung Spediteur setzen');
execute upsert_tx(126, 'de', 'Ausführung Spediteur lehnt');
execute upsert_tx(127, 'de', 'Kommerzieller Spediteur abgelehnt');
execute upsert_tx(130, 'de', 'Laden abgebrochen');
execute upsert_tx(150, 'de', 'Vorfahra. an LKW übermittelt');
execute upsert_tx(151, 'de', 'Vorfahra. an LKW übermittelt*');
execute upsert_tx(152, 'de', 'Rückfahra. an LKW übermittelt');
execute upsert_tx(153, 'de', 'Rückfahra. an LKW übermittelt*');
execute upsert_tx(154, 'de', 'Vorfahra. storniert');
execute upsert_tx(155, 'de', 'Rückfahra. storniert');
execute upsert_tx(160, 'de', 'Auftrag storniert');
execute upsert_tx(161, 'de', 'Ext. Auftrag erstellt');
execute upsert_tx(162, 'de', 'Auftrag bearbeitet');
execute upsert_tx(163, 'de', 'Ext. Auftrag storniert');
execute upsert_tx(165, 'de', 'LKW zurückgekehrt');
execute upsert_tx(166, 'de', 'Auftrag umdisponiert');
execute upsert_tx(170, 'de', 'Lieferschein');
execute upsert_tx(171, 'de', 'Silo gesetzt');
execute upsert_tx(172, 'de', 'Entladestelle verlassen');
execute upsert_tx(173, 'de', 'Scannen Bahnwagen');
execute upsert_tx(174, 'de', 'Scannen Container');
execute upsert_tx(175, 'de', 'Abgeschl. Auftrag modifiziert');
execute upsert_tx(201, 'de', 'LKW/Fahrerwechsel');
execute upsert_tx(202, 'de', 'LKW Kompositionsänderung');
execute upsert_tx(203, 'de', 'LKW Kompositionsbestätigung');
execute upsert_tx(210, 'de', 'Beginn Stau');
execute upsert_tx(211, 'de', 'Stau beendet');
execute upsert_tx(220, 'de', 'Bearbeitet:');
execute upsert_tx(221, 'de', 'Forced sending to SAP');
execute upsert_tx(300, 'de', 'Delivery doc senden');
execute upsert_tx(301, 'de', 'Shipment doc senden');
execute upsert_tx(302, 'de', 'Transfer doc senden');
execute upsert_tx(303, 'de', 'CN delivery doc senden');
execute upsert_tx(304, 'de', 'CN shipment doc senden');
execute upsert_tx(310, 'de', 'Delivery doc senden fehlgeschlagen');
execute upsert_tx(311, 'de', 'Shipment doc senden fehlgeschlagen');
execute upsert_tx(312, 'de', 'Transfer doc senden fehlgeschlagen');
execute upsert_tx(313, 'de', 'CN delivery doc senden fehlgeschlagen');
execute upsert_tx(314, 'de', 'CN shipment doc senden fehlgeschlagen');
execute upsert_tx(315, 'de', 'Aktualisierte Disposition');
execute upsert_tx(316, 'de', 'Regularisierung');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - FR
execute upsert_tx(101, 'fr', 'Nouveau');
execute upsert_tx(102, 'fr', 'Transmis au pool');
execute upsert_tx(103, 'fr', 'Confirmé par pool');
execute upsert_tx(104, 'fr', 'Transmis au comm. transporteur');
execute upsert_tx(105, 'fr', 'Confirmé par comm. transporteur');
execute upsert_tx(106, 'fr', 'Planifié');
execute upsert_tx(107, 'fr', 'Prêt pour l''exécution');
execute upsert_tx(108, 'fr', 'Confirmé par le chauffeaur');
execute upsert_tx(109, 'fr', 'Arrivé au lieu de chargement');
execute upsert_tx(110, 'fr', 'En chargement');
execute upsert_tx(111, 'fr', 'Chargé');
execute upsert_tx(112, 'fr', 'En route');
execute upsert_tx(113, 'fr', 'Arrivé au lieu de déchargement');
execute upsert_tx(114, 'fr', 'En déchargement');
execute upsert_tx(115, 'fr', 'Livré');
execute upsert_tx(116, 'fr', 'Terminé');
execute upsert_tx(117, 'fr', 'Annulé');
execute upsert_tx(118, 'fr', 'Position');
execute upsert_tx(119, 'fr', 'Transmis à la compagnie de chemins de fer');
execute upsert_tx(120, 'fr', 'Transporteur commercial fixé');
execute upsert_tx(121, 'fr', 'Définir pool');
execute upsert_tx(122, 'fr', 'Pool ack');
execute upsert_tx(123, 'fr', 'Transporteur ack');
execute upsert_tx(124, 'fr', 'Cancel on weighing system');
execute upsert_tx(125, 'fr', 'Définir exec. transporteur');
execute upsert_tx(126, 'fr', 'Exec. transporteur rejette');
execute upsert_tx(127, 'fr', 'Le transporteur commercial refuse');
execute upsert_tx(130, 'fr', 'Loading aborted');
execute upsert_tx(150, 'fr', 'Pre-loading ass. tr. to truck');
execute upsert_tx(151, 'fr', 'Pre-loading ass. tr. to truck*');
execute upsert_tx(152, 'fr', 'Return ass. tr. to truck');
execute upsert_tx(153, 'fr', 'Return ass. tr. to truck*');
execute upsert_tx(154, 'fr', 'Pre-chargement ass. annulée');
execute upsert_tx(155, 'fr', 'Retour ass. annulée');
execute upsert_tx(160, 'fr', 'Assignation annulée');
execute upsert_tx(161, 'fr', 'Assignation ext. créée');
execute upsert_tx(162, 'fr', 'Assignation changée');
execute upsert_tx(163, 'fr', 'Assignation ext. annulée');
execute upsert_tx(165, 'fr', 'Camion retourné');
execute upsert_tx(166, 'fr', 'Commande réaffectée');
execute upsert_tx(170, 'fr', 'Bulletin de livraison');
execute upsert_tx(171, 'fr', 'Définir silo');
execute upsert_tx(172, 'fr', 'Lieu de livraison parti');
execute upsert_tx(173, 'fr', 'Scanner wagon');
execute upsert_tx(174, 'fr', 'Scanner container');
execute upsert_tx(175, 'fr', 'Commande modifiée finie');
execute upsert_tx(201, 'fr', 'Camion/Chauffeur changé');
execute upsert_tx(202, 'fr', 'Composition camion changée');
execute upsert_tx(203, 'fr', 'Composition camion confirmée');
execute upsert_tx(210, 'fr', 'Début embouteillage');
execute upsert_tx(211, 'fr', 'Fin embouteillage');
execute upsert_tx(220, 'fr', 'Changer');
execute upsert_tx(221, 'fr', 'Forced sending to SAP');
execute upsert_tx(300, 'fr', 'Delivery doc envoyé');
execute upsert_tx(301, 'fr', 'Shipment doc envoyé');
execute upsert_tx(302, 'fr', 'Transfer doc envoyé');
execute upsert_tx(303, 'fr', 'CN delivery doc envoyé');
execute upsert_tx(304, 'fr', 'CN shipment doc envoyé');
execute upsert_tx(310, 'fr', 'Envoi Delivery doc manqué');
execute upsert_tx(311, 'fr', 'Envoi Shipment doc manqué');
execute upsert_tx(312, 'fr', 'Envoi Transfer doc manqué');
execute upsert_tx(313, 'fr', 'Envoi CN delivery doc manqué');
execute upsert_tx(314, 'fr', 'Envoi CN shipment doc manqué');
execute upsert_tx(315, 'fr', 'Planification mise � jour');
execute upsert_tx(316, 'fr', 'Régularisation');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - IT
execute upsert_tx(101, 'it', 'Nuovo');
execute upsert_tx(102, 'it', 'Trasmesso al pool');
execute upsert_tx(103, 'it', 'Confermato da pool');
execute upsert_tx(104, 'it', 'Trasmesso al vettore');
execute upsert_tx(105, 'it', 'Confermato da vettore');
execute upsert_tx(106, 'it', 'Schedulato');
execute upsert_tx(107, 'it', 'Rilasciato per l''esecuzione');
execute upsert_tx(108, 'it', 'Confermato dall''autista');
execute upsert_tx(109, 'it', 'Arrivato al luogo di carico');
execute upsert_tx(110, 'it', 'Carico in corso');
execute upsert_tx(111, 'it', 'Carico terminato');
execute upsert_tx(112, 'it', 'Strada facendo');
execute upsert_tx(113, 'it', 'Arrivato al luogo di scarico');
execute upsert_tx(114, 'it', 'Scarico in corso');
execute upsert_tx(115, 'it', 'Consegnato');
execute upsert_tx(116, 'it', 'Terminato');
execute upsert_tx(117, 'it', 'Cancellato');
execute upsert_tx(118, 'it', 'Posizione');
execute upsert_tx(119, 'it', 'Trasmesso alla società ferroviarie');
execute upsert_tx(120, 'it', 'Vettore fissato');
execute upsert_tx(121, 'it', 'Definire pool');
execute upsert_tx(122, 'it', 'Pool ack');
execute upsert_tx(123, 'it', '(Sub-)vettore ack');
execute upsert_tx(124, 'it', 'Cancel on weighing system');
execute upsert_tx(125, 'it', 'Definire sub-vettore');
execute upsert_tx(126, 'it', 'Sub-vettore respinge');
execute upsert_tx(127, 'it', 'Vettore rigetta');
execute upsert_tx(130, 'it', 'Carico annullato');
execute upsert_tx(150, 'it', 'Assegnazione del pre-carico trasmesso al camion');
execute upsert_tx(151, 'it', 'Assegnazione del pre-carico trasmesso al camion*');
execute upsert_tx(152, 'it', 'Assegnazione del reso trasmesso al camion');
execute upsert_tx(153, 'it', 'Assegnazione del reso trasmesso al camion*');
execute upsert_tx(154, 'it', 'Ass. pre-carico annullata');
execute upsert_tx(155, 'it', 'Ass. ritorno annullata');
execute upsert_tx(160, 'it', 'Assegnazione annullata');
execute upsert_tx(161, 'it', 'Assegnazione est. creata');
execute upsert_tx(162, 'it', 'Assegnazione modificata');
execute upsert_tx(163, 'it', 'Assegnazione cancellata');
execute upsert_tx(165, 'it', 'Camion tornato');
execute upsert_tx(166, 'it', 'Ordine riallocato');
execute upsert_tx(170, 'it', 'Bollettino di consegna');
execute upsert_tx(171, 'it', 'Definire silo');
execute upsert_tx(172, 'it', 'Punto di consegna lasciato');
execute upsert_tx(173, 'it', 'Scan vagone');
execute upsert_tx(174, 'it', 'Scan container');
execute upsert_tx(175, 'it', 'Ordine modificato finito');
execute upsert_tx(201, 'it', 'Camion/Autista cambiato');
execute upsert_tx(202, 'it', 'Composizione camion cambiata');
execute upsert_tx(203, 'it', 'Composizione camion confermata');
execute upsert_tx(210, 'it', 'Inizio ingorgo');
execute upsert_tx(211, 'it', 'Fine ingorgo');
execute upsert_tx(220, 'it', 'Modifica');
execute upsert_tx(221, 'it', 'Forced sending to SAP');
execute upsert_tx(300, 'it', 'Delivery doc inviato');
execute upsert_tx(301, 'it', 'Shipment doc inviato');
execute upsert_tx(302, 'it', 'Transfer doc inviato');
execute upsert_tx(303, 'it', 'CN delivery doc inviato');
execute upsert_tx(304, 'it', 'CN shipment doc inviato');
execute upsert_tx(310, 'it', 'Invio Delivery doc mancato');
execute upsert_tx(311, 'it', 'Invio Shipment doc mancato');
execute upsert_tx(312, 'it', 'Invio Transfer doc mancato');
execute upsert_tx(313, 'it', 'Invio CN delivery doc mancato');
execute upsert_tx(314, 'it', 'Invio CN shipment doc mancato');
execute upsert_tx(315, 'it', 'Schedulazione aggiornata');
execute upsert_tx(316, 'it', 'Regolarizzazione');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - ES
execute upsert_tx(101, 'es', 'Nuevo');
execute upsert_tx(102, 'es', 'Transmitido a región');
execute upsert_tx(103, 'es', 'Confirmado por región');
execute upsert_tx(104, 'es', 'Transmitido a operador logístico');
execute upsert_tx(105, 'es', 'Confirmado por operador logístico');
execute upsert_tx(106, 'es', 'Expedido');
execute upsert_tx(107, 'es', 'Listo para ejecutarse');
execute upsert_tx(108, 'es', 'Confirmado por conductor');
execute upsert_tx(109, 'es', 'Llegada a punto de carga');
execute upsert_tx(110, 'es', 'Cargando');
execute upsert_tx(111, 'es', 'Cargado');
execute upsert_tx(112, 'es', 'Entregando');
execute upsert_tx(113, 'es', 'Llegada a punto de entrega');
execute upsert_tx(114, 'es', 'Descargando');
execute upsert_tx(115, 'es', 'Entregado');
execute upsert_tx(116, 'es', 'Finalizado');
execute upsert_tx(117, 'es', 'Borrado');
execute upsert_tx(118, 'es', 'Posición');
execute upsert_tx(119, 'es', 'Transmitido a compañía ferroviaria');
execute upsert_tx(120, 'es', 'Operador logístico asignado');
execute upsert_tx(121, 'es', 'Asignar región');
execute upsert_tx(122, 'es', 'Región reconocida');
execute upsert_tx(123, 'es', 'Transportista reconocido');
execute upsert_tx(124, 'es', 'Cancelado en el sistema de pesaje');
execute upsert_tx(125, 'es', 'Asignar transportirta');
execute upsert_tx(126, 'es', 'Transportista rechaza');
execute upsert_tx(127, 'es', 'Operador logístico rechaza');
execute upsert_tx(130, 'es', 'Carga cancelada');
execute upsert_tx(150, 'es', 'Asignación de precarga transmitida al camión');
execute upsert_tx(151, 'es', 'Asignación de precarga transmitida al camión*');
execute upsert_tx(152, 'es', 'Asignación de devolución transmitida al camión');
execute upsert_tx(153, 'es', 'Asignación de devolución transmitida al camión*');
execute upsert_tx(154, 'es', 'Asign. pre-carga cancelada');
execute upsert_tx(155, 'es', 'Asign. devolución cancelada');
execute upsert_tx(160, 'es', 'Asignación cancelada');
execute upsert_tx(161, 'es', 'Asignación ext. creada');
execute upsert_tx(162, 'es', 'Asignación modificada');
execute upsert_tx(163, 'es', 'Asignación ext. cancelada');
execute upsert_tx(165, 'es', 'Camión regresa');
execute upsert_tx(166, 'es', 'Pedido reasignado');
execute upsert_tx(170, 'es', 'Albarán');
execute upsert_tx(171, 'es', 'Asignar silo');
execute upsert_tx(172, 'es', 'Salió del punto de entrega');
execute upsert_tx(173, 'es', 'Escanear vagón');
execute upsert_tx(174, 'es', 'Escanear contenedor');
execute upsert_tx(175, 'es', 'Orden finalizada modificada');
execute upsert_tx(201, 'es', 'Camión/Conductor cambiado');
execute upsert_tx(202, 'es', 'Composición camión cambiada');
execute upsert_tx(203, 'es', 'Composición camión confirmada');
execute upsert_tx(210, 'es', 'Inicio atasco');
execute upsert_tx(211, 'es', 'Fin atasco');
execute upsert_tx(220, 'es', 'Modifica');
execute upsert_tx(221, 'es', 'Forced sending to SAP');
execute upsert_tx(300, 'es', 'Doc. Delivery enviado');
execute upsert_tx(301, 'es', 'Doc. Shipment enviado');
execute upsert_tx(302, 'es', 'Doc. Transfer enviado');
execute upsert_tx(303, 'es', 'Doc. CN delivery enviado');
execute upsert_tx(304, 'es', 'Doc CN shipment enviado');
execute upsert_tx(310, 'es', 'Envío doc. Delivery fallido');
execute upsert_tx(311, 'es', 'Envío doc. Shipment fallido');
execute upsert_tx(312, 'es', 'Envío doc. Transfer fallido');
execute upsert_tx(313, 'es', 'Envío doc. CN delivery fallido');
execute upsert_tx(314, 'es', 'Envío doc. CN shipment fallido');
execute upsert_tx(315, 'es', 'Planificación Actualizada');
execute upsert_tx(316, 'es', 'Regularización');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - CS
execute upsert_tx(101, 'cs', 'Nový');
execute upsert_tx(102, 'cs', 'Odesláno do skupiny');
execute upsert_tx(103, 'cs', 'Potvrzeno ve skupině');
execute upsert_tx(104, 'cs', 'Odelsláno dopravci');
execute upsert_tx(105, 'cs', 'Potvrzeno dopravcem');
execute upsert_tx(106, 'cs', 'Naplánováno');
execute upsert_tx(107, 'cs', 'Uvolněno k provedení');
execute upsert_tx(108, 'cs', 'Potvrzeno řidičem');
execute upsert_tx(109, 'cs', 'Příjezd na místo nakládky');
execute upsert_tx(110, 'cs', 'Nakládka');
execute upsert_tx(111, 'cs', 'Naloženo');
execute upsert_tx(112, 'cs', 'Přeprava');
execute upsert_tx(113, 'cs', 'Příjezd na místo vykládky');
execute upsert_tx(114, 'cs', 'Vykládka');
execute upsert_tx(115, 'cs', 'Dodáno');
execute upsert_tx(116, 'cs', 'Dokončeno');
execute upsert_tx(117, 'cs', 'Smazáno');
execute upsert_tx(119, 'cs', 'Odesláno železniční společnosti');
execute upsert_tx(118, 'cs', 'Pozice');
execute upsert_tx(120, 'cs', 'Nastavení kom. dopravce');
execute upsert_tx(121, 'cs', 'Nastavení skupiny');
execute upsert_tx(122, 'cs', 'Potvrzení skupiny');
execute upsert_tx(123, 'cs', 'Potvrzení dopravce');
execute upsert_tx(124, 'cs', 'Přerušení ve vážním systému');
execute upsert_tx(125, 'cs', 'Nastavení prov. dopravce');
execute upsert_tx(126, 'cs', 'Prováděcí dopravce - zamítnutí');
execute upsert_tx(127, 'cs', 'Komerční dopravce - zamítnutí');
execute upsert_tx(130, 'cs', 'Nakládka přerušena');
execute upsert_tx(150, 'cs', 'Přiřazení na přednakládku odesláno dod vozidla');
execute upsert_tx(151, 'cs', 'Přiřazení na přednakládku odesláno dod vozidla*');
execute upsert_tx(152, 'cs', 'Vrácení odesláno do vozidla');
execute upsert_tx(153, 'cs', 'Vrácení odesláno do vozidla*');
execute upsert_tx(154, 'cs', 'Přiřazení přednakládky zrušeno');
execute upsert_tx(155, 'cs', 'Přiřazení Vrácení zrušeno');
execute upsert_tx(160, 'cs', 'Přiřazení zrušeno');
execute upsert_tx(161, 'cs', 'Externí přiřazení vytvořeno');
execute upsert_tx(162, 'cs', 'Přiřazení upraveno');
execute upsert_tx(163, 'cs', 'Externí přiřazení zrušeno');
execute upsert_tx(165, 'cs', 'Vozidlo vráceno');
execute upsert_tx(166, 'cs', 'Přesměrování objednávky');
execute upsert_tx(170, 'cs', 'Dodací list');
execute upsert_tx(171, 'cs', 'Nastav silo');
execute upsert_tx(172, 'cs', 'Opustit místo dodávky');
execute upsert_tx(173, 'cs', 'Oskenuj vagón');
execute upsert_tx(174, 'cs', 'Oskenuj kontejner');
execute upsert_tx(175, 'cs', 'Upravená hotová objednávka');
execute upsert_tx(201, 'cs', 'Vozidlo/Řidič bylo změněno');
execute upsert_tx(202, 'cs', 'Spřažení vozidel změněno');
execute upsert_tx(203, 'cs', 'Spřažení vozidel potvrzeno');
execute upsert_tx(210, 'cs', 'Začátek dopravní zácpy');
execute upsert_tx(211, 'cs', 'Konec dopravní zácpy');
execute upsert_tx(220, 'cs', 'Editováno');
execute upsert_tx(221, 'cs', 'Forced sending to SAP');
execute upsert_tx(300, 'cs', 'Dok. dodávky odeslány');
execute upsert_tx(301, 'cs', 'Dokumenty dodávky odeslány');
execute upsert_tx(302, 'cs', 'Dokumenty odeslány');
execute upsert_tx(303, 'cs', 'Dobropis (dodávka) odeslán');
execute upsert_tx(304, 'cs', 'Dobropis (přeprava) odeslán');
execute upsert_tx(310, 'cs', 'Odeslání dokumentů dodávky selhalo');
execute upsert_tx(311, 'cs', 'Odeslání dokumentů dodávky selhalo');
execute upsert_tx(312, 'cs', 'Odeslání dokumentů selhalo');
execute upsert_tx(313, 'cs', 'Odeslání dobropisu (dodávka) selhalo');
execute upsert_tx(314, 'cs', 'Odeslání dobropisu (přeprava) selhalo');
execute upsert_tx(315, 'cs', 'Plánování aktualizováno');
execute upsert_tx(316, 'cs', 'Regulace');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - SK
execute upsert_tx(101, 'sk', 'Nový');
execute upsert_tx(102, 'sk', 'Doručená do regiónu');
execute upsert_tx(103, 'sk', 'Potvrdená regiónom');
execute upsert_tx(104, 'sk', 'Doručená komerčnému dopravcovi');
execute upsert_tx(105, 'sk', 'Potvrdené komerčným dopravcom');
execute upsert_tx(106, 'sk', 'Plánované');
execute upsert_tx(107, 'sk', 'Uvoľnené na realizáciu');
execute upsert_tx(108, 'sk', 'Potvrdené vodičom');
execute upsert_tx(109, 'sk', 'Príchod na miesto nakládky');
execute upsert_tx(110, 'sk', 'Nakládka');
execute upsert_tx(111, 'sk', 'Naložené');
execute upsert_tx(112, 'sk', 'Preprava');
execute upsert_tx(113, 'sk', 'Príchod na miesto vykládky');
execute upsert_tx(114, 'sk', 'Vykládka');
execute upsert_tx(115, 'sk', 'Dodané');
execute upsert_tx(116, 'sk', 'Ukončenie');
execute upsert_tx(117, 'sk', 'Zmazané');
execute upsert_tx(119, 'sk', 'Doručené železničnej spoločnosti');
execute upsert_tx(118, 'sk', 'Poloha');
execute upsert_tx(120, 'sk', 'Komerčný dopravca určený');
execute upsert_tx(121, 'sk', 'Určiť región');
execute upsert_tx(122, 'sk', 'Región potvrdený');
execute upsert_tx(123, 'sk', 'Dopravca potvrdený');
execute upsert_tx(124, 'sk', 'Stornované systémom váženia');
execute upsert_tx(125, 'sk', 'Realizujúci dopravca určený');
execute upsert_tx(126, 'sk', 'Realizujúci dopravca odmietol');
execute upsert_tx(127, 'sk', 'Komerčný dopravca odmietol');
execute upsert_tx(130, 'sk', 'Nakládka prerušená');
execute upsert_tx(150, 'sk', 'Prednakládka poslaná do vozidla');
execute upsert_tx(151, 'sk', 'Prednakládka poslaná do vozidla*');
execute upsert_tx(152, 'sk', 'Spätná úloha zaslaná do vozidla');
execute upsert_tx(153, 'sk', 'Spätná úloha zaslaná do vozidla*');
execute upsert_tx(154, 'sk', 'Prednakládka zrušená');
execute upsert_tx(155, 'sk', 'Spätná úloha zrušená');
execute upsert_tx(160, 'sk', 'Úloha zrušená');
execute upsert_tx(161, 'sk', 'Externá úloha vytvorená');
execute upsert_tx(162, 'sk', 'Úloha upravená');
execute upsert_tx(163, 'sk', 'Externá úloha zrušená');
execute upsert_tx(165, 'sk', 'Ťahač sa vrátil');
execute upsert_tx(166, 'sk', 'Objednávka novo pridelená');
execute upsert_tx(170, 'sk', 'Dodací list');
execute upsert_tx(171, 'sk', 'Silo určené');
execute upsert_tx(172, 'sk', 'Zvyšné miesto dodania');
execute upsert_tx(173, 'sk', 'Skenovanie vagóna');
execute upsert_tx(174, 'sk', 'Skenovanie kontajnera');
execute upsert_tx(175, 'sk', 'Upravená ukončená objednávka');
execute upsert_tx(201, 'sk', 'Ťahač/Vodič zmenený');
execute upsert_tx(202, 'sk', 'Zloženie ťahača zmenené');
execute upsert_tx(203, 'sk', 'Zloženie ťahača potvrdené');
execute upsert_tx(210, 'sk', 'Dopravná zápcha štart');
execute upsert_tx(211, 'sk', 'Dopravná zápcha koniec');
execute upsert_tx(220, 'sk', 'Upravené');
execute upsert_tx(221, 'sk', 'Forced sending to SAP');
execute upsert_tx(300, 'sk', 'Dodací list poslaný');
execute upsert_tx(301, 'sk', 'Nákladný list poslaný');
execute upsert_tx(302, 'sk', 'Prepravný list poslaný');
execute upsert_tx(303, 'sk', 'Dobropis dodacieho listu poslaný');
execute upsert_tx(304, 'sk', 'Dobropis nakladacieho listu poslaný');
execute upsert_tx(310, 'sk', 'Poslanie dodacie listu neúspešné');
execute upsert_tx(311, 'sk', 'Poslanie nákladného listu neúspešné');
execute upsert_tx(312, 'sk', 'Poslanie prepravného listu neúspešné');
execute upsert_tx(313, 'sk', 'Poslanie dobropis dodacieho listu neúspešné');
execute upsert_tx(314, 'sk', 'Poslanie dobropis nakladacieho listu neúspešné');
execute upsert_tx(315, 'sk', 'Aktualizované plánovanie');
execute upsert_tx(316, 'sk', 'Regularizácie');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - HU
execute upsert_tx(101, 'hu', 'Új');
execute upsert_tx(102, 'hu', 'Készletre vétel');
execute upsert_tx(103, 'hu', 'Készlet megerősítése');
execute upsert_tx(104, 'hu', 'Kereskedelmi fuvarozói bevételezés');
execute upsert_tx(105, 'hu', 'Kereskedelmi fuvarozó általi megerősítés');
execute upsert_tx(106, 'hu', 'Ütemezett');
execute upsert_tx(107, 'hu', 'Végrehajtás megjelenítése');
execute upsert_tx(108, 'hu', 'Vezető általi megerősítés');
execute upsert_tx(109, 'hu', 'Felrakó helyre való érkezés');
execute upsert_tx(110, 'hu', 'Felrakás');
execute upsert_tx(111, 'hu', 'Megrakott');
execute upsert_tx(112, 'hu', 'Szállítás');
execute upsert_tx(113, 'hu', 'Szállítási címre való megérkezés');
execute upsert_tx(114, 'hu', 'Leadás');
execute upsert_tx(115, 'hu', 'Lerakott');
execute upsert_tx(116, 'hu', 'Befejezett');
execute upsert_tx(117, 'hu', 'Törölt');
execute upsert_tx(119, 'hu', 'Vasúti társaság által továbbított');
execute upsert_tx(118, 'hu', 'Pozíció');
execute upsert_tx(120, 'hu', 'Kereskedelmi fuvarozó beállítása');
execute upsert_tx(121, 'hu', 'Készlet beállítás');
execute upsert_tx(122, 'hu', 'Készlet visszaigazolás');
execute upsert_tx(123, 'hu', 'Fuvarozó visszaigazolás');
execute upsert_tx(124, 'hu', 'Mérlegelési rendszer törlése');
execute upsert_tx(125, 'hu', 'Fuvarozói végrehajtás beállítása');
execute upsert_tx(126, 'hu', 'Fuvarozói végrehajtás elutasítása');
execute upsert_tx(127, 'hu', 'Kereskedelmi fuvarozó elutasítása');
execute upsert_tx(130, 'hu', 'Töltés félbeszakítása');
execute upsert_tx(150, 'hu', 'Előtöltési megbízás továbbitása a kamionra');
execute upsert_tx(151, 'hu', 'Előtöltési megbízás továbbitása a kamionra*');
execute upsert_tx(152, 'hu', 'Visszatérés továbbítása a kamionra');
execute upsert_tx(153, 'hu', 'Visszatérés továbbítása a kamionra*');
execute upsert_tx(154, 'hu', 'Törölt előtöltési megbízás');
execute upsert_tx(155, 'hu', 'Törölt visszatérési megbízás');
execute upsert_tx(160, 'hu', 'Tölölt megbízás');
execute upsert_tx(161, 'hu', 'Végrehajtási megbízás létrehozása');
execute upsert_tx(162, 'hu', 'Megbízás szerkeztése');
execute upsert_tx(163, 'hu', 'Végrehajtási megbízás törlése');
execute upsert_tx(165, 'hu', 'Visszahívott kamion');
execute upsert_tx(166, 'hu', 'Átcsoportosított megrendelés');
execute upsert_tx(170, 'hu', 'Szállítási jegyzet');
execute upsert_tx(171, 'hu', 'Siló beállítása');
execute upsert_tx(172, 'hu', 'Bal szállítási pont');
execute upsert_tx(173, 'hu', 'Vagon beolvasása');
execute upsert_tx(174, 'hu', 'Konténer beolvasása');
execute upsert_tx(175, 'hu', 'Módosítás utáni kész megrendelés');
execute upsert_tx(201, 'hu', 'Cserélt kamion/sofőr');
execute upsert_tx(202, 'hu', 'Megváltozott kamion összetétel');
execute upsert_tx(203, 'hu', 'Megerősített kamion összetétel');
execute upsert_tx(210, 'hu', 'Forgalmi dugó kezdete');
execute upsert_tx(211, 'hu', 'Forgalmi dugó vége');
execute upsert_tx(220, 'hu', 'Szerkeztett');
execute upsert_tx(221, 'hu', 'Forced sending to SAP');
execute upsert_tx(300, 'hu', 'Küldött kiszállítási dokumentum');
execute upsert_tx(301, 'hu', 'Küldött szállítási dokumentum');
execute upsert_tx(302, 'hu', 'Küldött átadási dokumentum');
execute upsert_tx(303, 'hu', 'Küldött jóváírási kiszállítási dokumentum');
execute upsert_tx(304, 'hu', 'Küldött jóváírási szállítási dokumentum');
execute upsert_tx(310, 'hu', 'Kiszállítási dokumentum küldési hiba');
execute upsert_tx(311, 'hu', 'Szállítási dokumentum küldési hiba');
execute upsert_tx(312, 'hu', 'Átadási dokumentum küldési hiba');
execute upsert_tx(313, 'hu', 'Jóváírási kiszállítási értesítés küldési hiba');
execute upsert_tx(314, 'hu', 'Jóváírási szállítási értesítés küldési hiba');
execute upsert_tx(315, 'hu', 'Frissített üzemezés');
execute upsert_tx(316, 'hu', 'Regularizáció');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - RO
execute upsert_tx(101, 'ro', 'Nou');
execute upsert_tx(102, 'ro', 'Transmis catre regiunea');
execute upsert_tx(103, 'ro', 'Confirmat de catre regiunea');
execute upsert_tx(104, 'ro', 'Transmis catre Operator logistic');
execute upsert_tx(105, 'ro', 'Confirmat de catre Operator logistic');
execute upsert_tx(106, 'ro', 'Planificat');
execute upsert_tx(107, 'ro', 'Transmis pentru executare');
execute upsert_tx(108, 'ro', 'Confirmat de catre sofer');
execute upsert_tx(109, 'ro', 'Sosit la punct de incarcare');
execute upsert_tx(110, 'ro', 'Incarcare');
execute upsert_tx(111, 'ro', 'Incarcat');
execute upsert_tx(112, 'ro', 'Expeditie');
execute upsert_tx(113, 'ro', 'Sosit la punct de livrare');
execute upsert_tx(114, 'ro', 'Descarcare');
execute upsert_tx(115, 'ro', 'Livrat');
execute upsert_tx(116, 'ro', 'Terminat');
execute upsert_tx(117, 'ro', 'Sters');
execute upsert_tx(119, 'ro', 'Transmis catre compania feroviara');
execute upsert_tx(118, 'ro', 'Pozitie');
execute upsert_tx(120, 'ro', 'Setare Operator logistic');
execute upsert_tx(121, 'ro', 'Seteaza regiune');
execute upsert_tx(122, 'ro', 'Confirmare regiune');
execute upsert_tx(123, 'ro', 'Confirmare transportator');
execute upsert_tx(124, 'ro', 'Anuleaza sistem cantarire');
execute upsert_tx(125, 'ro', 'Executare setare transportator');
execute upsert_tx(126, 'ro', 'Executare respingeri transportator');
execute upsert_tx(127, 'ro', 'Respingeri operator logistic');
execute upsert_tx(130, 'ro', 'Incarcare oprita');
execute upsert_tx(150, 'ro', 'Atribuire pre-incarcare transmisa catre camion');
execute upsert_tx(151, 'ro', 'Atribuire pre-incarcare transmisa catre camion*');
execute upsert_tx(152, 'ro', 'Atribuire retur transmisa catre camion');
execute upsert_tx(153, 'ro', 'Atribuireretur transmisa catre camion*');
execute upsert_tx(154, 'ro', 'Atribuire pre-incarcare anulata');
execute upsert_tx(155, 'ro', 'Atribuire retur anulata');
execute upsert_tx(160, 'ro', 'Atribuire anulata');
execute upsert_tx(161, 'ro', 'Atribuire externa creata');
execute upsert_tx(162, 'ro', 'Atribuire editata');
execute upsert_tx(163, 'ro', 'Atribuire externa anulata');
execute upsert_tx(165, 'ro', 'Camion revenit');
execute upsert_tx(166, 'ro', 'Comanda realocata');
execute upsert_tx(170, 'ro', 'Nota de livrare');
execute upsert_tx(171, 'ro', 'Seteaza siloz');
execute upsert_tx(172, 'ro', 'Punct de livrare ramas');
execute upsert_tx(173, 'ro', 'Scaneaza vagon');
execute upsert_tx(174, 'ro', 'Scaneaza container');
execute upsert_tx(175, 'ro', 'Comanda incheiata modificata');
execute upsert_tx(201, 'ro', 'Camion/Sofer modificat');
execute upsert_tx(202, 'ro', 'Componenta camion modificata');
execute upsert_tx(203, 'ro', 'Componenta camion confirmata');
execute upsert_tx(210, 'ro', 'Blocaj rutier incepe');
execute upsert_tx(211, 'ro', 'Blocaj rutier se termina');
execute upsert_tx(220, 'ro', 'Editat');
execute upsert_tx(221, 'ro', 'Forced sending to SAP');
execute upsert_tx(300, 'ro', 'Document livrare trimis');
execute upsert_tx(301, 'ro', 'Document expeditie trimis');
execute upsert_tx(302, 'ro', 'Document transfer trimis');
execute upsert_tx(303, 'ro', 'Document livrare cu nota credit trimis');
execute upsert_tx(304, 'ro', 'Document expeditie cu nota credit trimis');
execute upsert_tx(310, 'ro', 'Trimitere document livrare esuata');
execute upsert_tx(311, 'ro', 'Trimitere document expeditie esuata');
execute upsert_tx(312, 'ro', 'Trimitere document transfer esuata');
execute upsert_tx(313, 'ro', 'Trimitere document livrare cu nota credit esuata');
execute upsert_tx(314, 'ro', 'Trimitere document expeditie cu nota credit esuata');
execute upsert_tx(315, 'ro', 'Planificare actualizata');
execute upsert_tx(316, 'ro', 'Regularizare');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - BG
execute upsert_tx(101, 'bg', 'Нов');
execute upsert_tx(102, 'bg', 'Прехвърлен към Pool');
execute upsert_tx(103, 'bg', 'Потвърден от Pool');
execute upsert_tx(104, 'bg', 'Прехвърлен към търговски превозвач');
execute upsert_tx(105, 'bg', 'Потвърден от търговски превозвач');
execute upsert_tx(106, 'bg', 'Планиран');
execute upsert_tx(107, 'bg', 'Освободен за изпълнение');
execute upsert_tx(108, 'bg', 'Потвърден от шофьор');
execute upsert_tx(109, 'bg', 'Пристигнал на мястото на товарене');
execute upsert_tx(110, 'bg', 'Товарене');
execute upsert_tx(111, 'bg', 'Натоварено количество');
execute upsert_tx(112, 'bg', 'Доставяне');
execute upsert_tx(113, 'bg', 'Пристигнал на мястото на доставяне');
execute upsert_tx(114, 'bg', 'Разтоварване');
execute upsert_tx(115, 'bg', 'Доставен');
execute upsert_tx(116, 'bg', 'Приключен');
execute upsert_tx(117, 'bg', 'Изтрит');
execute upsert_tx(119, 'bg', 'Прехвърлен към железопътна компания');
execute upsert_tx(118, 'bg', 'Позиция');
execute upsert_tx(120, 'bg', 'Зададен е търговски превозвач');
execute upsert_tx(121, 'bg', 'Зададен Pool');
execute upsert_tx(122, 'bg', 'Потвърждаване на Pool');
execute upsert_tx(123, 'bg', 'Потвърждаване на превозвач');
execute upsert_tx(124, 'bg', 'Отказване от системата за претегляне');
execute upsert_tx(125, 'bg', 'Зададен е изпълняващ превозвач');
execute upsert_tx(126, 'bg', 'Отхвърлен е изпълняващ превозвач');
execute upsert_tx(127, 'bg', 'Отхвърлен е търговски превозвач');
execute upsert_tx(130, 'bg', 'Товаренето е неуспешно');
execute upsert_tx(150, 'bg', 'Назначението за презареждане е предадено на камион');
execute upsert_tx(151, 'bg', ' Pre-loading ass. tr. to truck* ');
execute upsert_tx(152, 'bg', 'Върнатото назначение е предадено на камион');
execute upsert_tx(153, 'bg', 'Върнатото назначение е предадено на камион*');
execute upsert_tx(154, 'bg', 'Назначението за презареждане е отменено');
execute upsert_tx(155, 'bg', 'Върнатото назначение е отменено');
execute upsert_tx(160, 'bg', 'Назначението е отменено');
execute upsert_tx(161, 'bg', 'Създадено е външно назначение');
execute upsert_tx(162, 'bg', 'Назначението е променено');
execute upsert_tx(163, 'bg', 'Външното назначение е отменено');
execute upsert_tx(165, 'bg', 'Камионът се връща');
execute upsert_tx(166, 'bg', 'Поръчката е преразпределена');
execute upsert_tx(170, 'bg', 'Експедиционна бележка');
execute upsert_tx(171, 'bg', 'Определен е силоз');
execute upsert_tx(172, 'bg', 'Напускане на мястото за доставяне');
execute upsert_tx(173, 'bg', 'Преглеждане на вагон');
execute upsert_tx(174, 'bg', 'Преглеждане на контейнер');
execute upsert_tx(175, 'bg', 'Промяна на крайна поръчка');
execute upsert_tx(201, 'bg', 'Променен камион/шофьор');
execute upsert_tx(202, 'bg', 'Променена влакова композиция');
execute upsert_tx(203, 'bg', 'Потвърдена влакова композиция');
execute upsert_tx(210, 'bg', 'Начало на задръстване');
execute upsert_tx(211, 'bg', 'Край на задръстване');
execute upsert_tx(220, 'bg', 'Променен');
execute upsert_tx(221, 'bg', 'Forced sending to SAP');
execute upsert_tx(300, 'bg', 'Доставящият документ е изпратен');
execute upsert_tx(301, 'bg', 'Превозният документ е изпратен');
execute upsert_tx(302, 'bg', 'Трансферният документ е изпратен');
execute upsert_tx(303, 'bg', 'CN за доставящият документ е изпратено');
execute upsert_tx(304, 'bg', 'CN за превозният документ е изпратено');
execute upsert_tx(310, 'bg', 'Изпращането на доставящият документ е провалено');
execute upsert_tx(311, 'bg', 'Изпращането на превозния документ е провалено');
execute upsert_tx(312, 'bg', 'Изпращането на трансферния документ е провалено');
execute upsert_tx(313, 'bg', 'Изпращането CN на доставящия документ е провалено');
execute upsert_tx(314, 'bg', 'Изпращането CN на превозния документ е провалено');
execute upsert_tx(315, 'bg', 'Актуализиран график');
execute upsert_tx(316, 'bg', 'узаконяване');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - RU (ENGLISH COPY)
execute upsert_tx(101, 'ru', 'новый');
execute upsert_tx(102, 'ru', 'Передано в Список заказов');
execute upsert_tx(103, 'ru', 'Подтверждено в Списке заказов');
execute upsert_tx(104, 'ru', 'передано перевозчику');
execute upsert_tx(105, 'ru', 'подтверждена перевозчиком');
execute upsert_tx(106, 'ru', 'запланировано');
execute upsert_tx(107, 'ru', 'переход к исполнению');
execute upsert_tx(108, 'ru', 'Подтверждено водителем');
execute upsert_tx(109, 'ru', 'Прибыл в пункт загрузки');
execute upsert_tx(110, 'ru', 'загрузка');
execute upsert_tx(111, 'ru', 'загружено');
execute upsert_tx(112, 'ru', 'Доставка');
execute upsert_tx(113, 'ru', 'Прибыл в пункт разгрузки');
execute upsert_tx(114, 'ru', 'Выгрузка');
execute upsert_tx(115, 'ru', 'Доставленный');
execute upsert_tx(116, 'ru', 'Завершен');
execute upsert_tx(117, 'ru', 'Удален');
execute upsert_tx(119, 'ru', 'передано в жд компанию');
execute upsert_tx(118, 'ru', 'положение');
execute upsert_tx(120, 'ru', 'установить коммерческого перевозчика');
execute upsert_tx(121, 'ru', 'установить список заказов');
execute upsert_tx(122, 'ru', 'Pool ack');
execute upsert_tx(123, 'ru', 'перевозчик подтвежден');
execute upsert_tx(124, 'ru', 'отмена в весовой системе');
execute upsert_tx(125, 'ru', 'установить исполняющего перевозчика');
execute upsert_tx(126, 'ru', 'исполняющий перевозчик отказался');
execute upsert_tx(127, 'ru', 'Коммерческий Перевозчик отказался');
execute upsert_tx(130, 'ru', 'Загрузка прервана');
execute upsert_tx(150, 'ru', 'Назначенная предварит. загрузка передана ТС');
execute upsert_tx(151, 'ru', 'Назначенная предварит. загрузка передана ТС*');
execute upsert_tx(152, 'ru', 'возвращение задания передано в автомобиль');
execute upsert_tx(153, 'ru', 'возвращение задания передано в автомобиль*');
execute upsert_tx(154, 'ru', 'задание на погрузку в счет завтра отменено');
execute upsert_tx(155, 'ru', 'возвращение задания отменено');
execute upsert_tx(160, 'ru', 'назначение транспортного средства отменено');
execute upsert_tx(161, 'ru', 'внешнее задание создано');
execute upsert_tx(162, 'ru', 'назначение транспортного средства изменено');
execute upsert_tx(163, 'ru', 'внешнее задание отменено');
execute upsert_tx(165, 'ru', 'Автомобиль вернулся');
execute upsert_tx(166, 'ru', 'Заказ перераспределен');
execute upsert_tx(170, 'ru', 'накладная');
execute upsert_tx(171, 'ru', 'выбор силоса');
execute upsert_tx(172, 'ru', 'оставить точку доставки');
execute upsert_tx(173, 'ru', 'Сканирование вагона');
execute upsert_tx(174, 'ru', 'Сканирование контейнера');
execute upsert_tx(175, 'ru', 'изменение завешенного заказа');
execute upsert_tx(201, 'ru', 'Машина\водитель изменен');
execute upsert_tx(202, 'ru', 'Состав машины изменен');
execute upsert_tx(203, 'ru', 'Состав машины подтвержден');
execute upsert_tx(210, 'ru', 'Начало "пробки"');
execute upsert_tx(211, 'ru', 'Окончание "пробки"');
execute upsert_tx(220, 'ru', 'Отредактированный');
execute upsert_tx(221, 'ru', 'Forced sending to SAP');
execute upsert_tx(300, 'ru', 'документ поставки отправлен');
execute upsert_tx(301, 'ru', 'Shipment doc sent');
execute upsert_tx(302, 'ru', 'документ передачи отправлен');
execute upsert_tx(303, 'ru', 'финансовый документ поставки отправлен');
execute upsert_tx(304, 'ru', 'финансовый документ отгрузки отпрален');
execute upsert_tx(310, 'ru', 'документ поставки отправлен неуспешно');
execute upsert_tx(311, 'ru', 'документ отгрузки отпрален неуспешно');
execute upsert_tx(312, 'ru', 'документ передачи отправлен неуспешно');
execute upsert_tx(313, 'ru', 'финансовый документ поставки отправлен неуспешно');
execute upsert_tx(314, 'ru', 'финансовый документ отгрузки отпрален неуспешно');
execute upsert_tx(315, 'ru', 'Планирование обновлено');
execute upsert_tx(316, 'ru', 'регуляризация');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - PL (ENGLISH COPY)
execute upsert_tx(101, 'pl', 'Nowy');
execute upsert_tx(102, 'pl', 'Nadano do regionu');
execute upsert_tx(103, 'pl', 'Potwierdzono');
execute upsert_tx(104, 'pl', 'Nadano do przewoźnika');
execute upsert_tx(105, 'pl', 'Potwierdzono przez przewoźnika');
execute upsert_tx(106, 'pl', 'Zaplanowano');
execute upsert_tx(107, 'pl', 'Zwolnione do wykonania');
execute upsert_tx(108, 'pl', 'Potwierdzono przez kierowcę');
execute upsert_tx(109, 'pl', 'Przyjazd do punktu załadunkowego');
execute upsert_tx(110, 'pl', 'Załadunek');
execute upsert_tx(111, 'pl', 'Załadowano');
execute upsert_tx(112, 'pl', 'Spedycja');
execute upsert_tx(113, 'pl', 'Przyjazd do punktu dostawy');
execute upsert_tx(114, 'pl', 'Rozładunek');
execute upsert_tx(115, 'pl', 'Dostarczono');
execute upsert_tx(116, 'pl', 'Zakończony');
execute upsert_tx(117, 'pl', 'Usunięto');
execute upsert_tx(119, 'pl', 'Przekazano spółce kolejowej');
execute upsert_tx(118, 'pl', 'Stanowisko');
execute upsert_tx(120, 'pl', 'Ustalenie przewoźnika');
execute upsert_tx(121, 'pl', 'Określenie regionu');
execute upsert_tx(122, 'pl', 'Uznanie regionu');
execute upsert_tx(123, 'pl', 'Uznanie przewoźnika');
execute upsert_tx(124, 'pl', 'Anulowano w systemie wagi');
execute upsert_tx(125, 'pl', 'Wykonanie ustaleń przewoźnika');
execute upsert_tx(126, 'pl', 'Realizacja zadań odrzuconych przez przewoźnika');
execute upsert_tx(127, 'pl', 'Odmowy przewoźnika');
execute upsert_tx(130, 'pl', 'Ładowanie przerwane');
execute upsert_tx(150, 'pl', 'Wstępnego ładowania przek. pojazdu ciężarowego');
execute upsert_tx(151, 'pl', 'Wstępnego ładowania przek. pojazdu ciężarowego*');
execute upsert_tx(152, 'pl', 'Zwrot ustalenia przekazano pojazdu ciężarowego');
execute upsert_tx(153, 'pl', 'Zwrot ustalenia przekazano pojazdu ciężarowego*');
execute upsert_tx(154, 'pl', 'Ustalenie wstępnego ładowania anulowane');
execute upsert_tx(155, 'pl', 'Zwrot ustalenia anulowany');
execute upsert_tx(160, 'pl', 'Ustalenie anulowane');
execute upsert_tx(161, 'pl', 'Utworzono ustalenia zewnętrzne');
execute upsert_tx(162, 'pl', 'Ustalenia edytowane');
execute upsert_tx(163, 'pl', 'Zewnętrzne ustalenia anulowane');
execute upsert_tx(165, 'pl', 'Pojazd ciężarowy zawrócił');
execute upsert_tx(166, 'pl', 'Zamówienie przesunięte');
execute upsert_tx(170, 'pl', 'List przewozowy');
execute upsert_tx(171, 'pl', 'Ustal silos');
execute upsert_tx(172, 'pl', 'Opuszczony punkt dostawy');
execute upsert_tx(173, 'pl', 'Skanowanie wagonu');
execute upsert_tx(174, 'pl', 'Skanowanie pojemnika');
execute upsert_tx(175, 'pl', 'Zmodyfikowane zamówienie zakończono');
execute upsert_tx(201, 'pl', 'Zmiana Pojazdu Ciężarowego / Kierowcy');
execute upsert_tx(202, 'pl', 'Zmieniono skład pojazdu ciężarowego');
execute upsert_tx(203, 'pl', 'Potwierdzono skład pojazdu ciężarowego');
execute upsert_tx(210, 'pl', 'Korek uliczny start');
execute upsert_tx(211, 'pl', 'Korek uliczny koniec');
execute upsert_tx(220, 'pl', 'Edytowano');
execute upsert_tx(221, 'pl', 'Forced sending to SAP');
execute upsert_tx(300, 'pl', 'Wysłano dokument dostawy');
execute upsert_tx(301, 'pl', 'Wysłano dokument przesyłki');
execute upsert_tx(302, 'pl', 'Wysłano transfer dokumentu');
execute upsert_tx(303, 'pl', 'Wysłano notę kredytową dostawy');
execute upsert_tx(304, 'pl', 'Wysłano notę kredytową przesyłki');
execute upsert_tx(310, 'pl', 'Nie wysłano dokumentu dostawy');
execute upsert_tx(311, 'pl', 'Nie wysłano dokumentu przesyłki');
execute upsert_tx(312, 'pl', 'Nie wysłano transferu dokumentu');
execute upsert_tx(313, 'pl', 'Nie wysłano noty kredytowej przesyłki');
execute upsert_tx(314, 'pl', 'Nie wysłano noty kredytowej dostawy');
execute upsert_tx(315, 'pl', 'Zaktualizowany harmonogram');
execute upsert_tx(316, 'pl', 'Regularyzacja');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - AZ (ENGLISH COPY)
execute upsert_tx(101, 'az', 'New');
execute upsert_tx(102, 'az', 'Transmitted to pool');
execute upsert_tx(103, 'az', 'Confirmed by pool');
execute upsert_tx(104, 'az', 'Transmitted to comm. carrier');
execute upsert_tx(105, 'az', 'Confirmed by comm. carrier');
execute upsert_tx(106, 'az', 'Scheduled');
execute upsert_tx(107, 'az', 'Released for execution');
execute upsert_tx(108, 'az', 'Confirmed by driver');
execute upsert_tx(109, 'az', 'Arrived at loadingpoint');
execute upsert_tx(110, 'az', 'Loading');
execute upsert_tx(111, 'az', 'Loaded');
execute upsert_tx(112, 'az', 'Shipping');
execute upsert_tx(113, 'az', 'Arrived at delivery point');
execute upsert_tx(114, 'az', 'Unloading');
execute upsert_tx(115, 'az', 'Delivered');
execute upsert_tx(116, 'az', 'Finished');
execute upsert_tx(117, 'az', 'Deleted');
execute upsert_tx(119, 'az', 'Transmitted to rail company');
execute upsert_tx(118, 'az', 'Position');
execute upsert_tx(120, 'az', 'Comm. carrier set');
execute upsert_tx(121, 'az', 'Set pool');
execute upsert_tx(122, 'az', 'Pool ack');
execute upsert_tx(123, 'az', 'Carrier ack');
execute upsert_tx(124, 'az', 'Cancel on weighing system');
execute upsert_tx(125, 'az', 'Exec. carrier set');
execute upsert_tx(126, 'az', 'Exec carrier rejects');
execute upsert_tx(127, 'az', 'Comm. carrier rejects');
execute upsert_tx(130, 'az', 'Loading aborted');
execute upsert_tx(150, 'az', 'Pre-loading ass. tr. to truck');
execute upsert_tx(151, 'az', 'Pre-loading ass. tr. to truck*');
execute upsert_tx(152, 'az', 'Return ass. tr. to truck');
execute upsert_tx(153, 'az', 'Return ass. tr. to truck*');
execute upsert_tx(154, 'az', 'Pre-loading ass. cancelled');
execute upsert_tx(155, 'az', 'Return ass. cancelled');
execute upsert_tx(160, 'az', 'Assignment cancelled');
execute upsert_tx(161, 'az', 'Ext. assignment created');
execute upsert_tx(162, 'az', 'Assignment edited');
execute upsert_tx(163, 'az', 'Ext. assignment cancelled');
execute upsert_tx(165, 'az', 'Truck returned');
execute upsert_tx(166, 'az', 'Order reallocated');
execute upsert_tx(170, 'az', 'Delivery note');
execute upsert_tx(171, 'az', 'Set silo');
execute upsert_tx(172, 'az', 'Left delivery point');
execute upsert_tx(173, 'az', 'Scan wagon');
execute upsert_tx(174, 'az', 'Scan container');
execute upsert_tx(175, 'az', 'Modified finished order');
execute upsert_tx(201, 'az', 'Truck/Driver changed');
execute upsert_tx(202, 'az', 'Truck composition changed');
execute upsert_tx(203, 'az', 'Truck composition confirmed');
execute upsert_tx(210, 'az', 'Trafficjam start');
execute upsert_tx(211, 'az', 'Trafficjam end');
execute upsert_tx(220, 'az', 'Edited');
execute upsert_tx(221, 'az', 'Forced sending to SAP');
execute upsert_tx(300, 'az', 'Delivery doc sent');
execute upsert_tx(301, 'az', 'Shipment doc sent');
execute upsert_tx(302, 'az', 'Transfer doc sent');
execute upsert_tx(303, 'az', 'CN delivery doc sent');
execute upsert_tx(304, 'az', 'CN shipment doc sent');
execute upsert_tx(310, 'az', 'Delivery doc send failed');
execute upsert_tx(311, 'az', 'Shipment doc send failed');
execute upsert_tx(312, 'az', 'Transfer doc send failed');
execute upsert_tx(313, 'az', 'CN delivery doc send failed');
execute upsert_tx(314, 'az', 'CN shipment doc send failed');
execute upsert_tx(315, 'az', 'Updated Scheduling');
execute upsert_tx(316, 'az', 'Düzenlemeler');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - HR (ENGLISH COPY)
execute upsert_tx(101, 'hr', 'Novi');
execute upsert_tx(102, 'hr', 'Preneseno na segment');
execute upsert_tx(103, 'hr', 'Potvrđeno od strane segmenta');
execute upsert_tx(104, 'hr', 'Preneseno na prijevoznika');
execute upsert_tx(105, 'hr', 'Potvrđeno od strane prijevoznika');
execute upsert_tx(106, 'hr', 'Planiran');
execute upsert_tx(107, 'hr', 'Objavljen za izvršenje');
execute upsert_tx(108, 'hr', 'Potvrđeno od strane vozača');
execute upsert_tx(109, 'hr', 'Stigao na utovar');
execute upsert_tx(110, 'hr', 'Utovar');
execute upsert_tx(111, 'hr', 'Utovaren');
execute upsert_tx(112, 'hr', 'Isporuka');
execute upsert_tx(113, 'hr', 'Stigao na mjesto isporuke');
execute upsert_tx(114, 'hr', 'Istovar');
execute upsert_tx(115, 'hr', 'Isporučeno');
execute upsert_tx(116, 'hr', 'Gotov');
execute upsert_tx(117, 'hr', 'Izbrisan');
execute upsert_tx(119, 'hr', 'Preneseno na željezničko poduzeću');
execute upsert_tx(118, 'hr', 'Položaj');
execute upsert_tx(120, 'hr', 'Postavljen organizator prijevoza');
execute upsert_tx(121, 'hr', 'Postavite segment');
execute upsert_tx(122, 'hr', 'Segment potvrđen');
execute upsert_tx(123, 'hr', 'Prijevoznik potvrđen');
execute upsert_tx(124, 'hr', 'Poništiti na sustavu vaganja');
execute upsert_tx(125, 'hr', 'Postavi izvršnog  prijevoznika');
execute upsert_tx(126, 'hr', 'Izvršnog  prijevoznik odbija');
execute upsert_tx(127, 'hr', 'Organizator  prijevozna odbijen');
execute upsert_tx(130, 'hr', 'Utovar prekinut ');
execute upsert_tx(150, 'hr', 'Prije-utovarni raspored prenesen na kamion');
execute upsert_tx(151, 'hr', 'Prije-utovarni raspored prenesen na kamion*');
execute upsert_tx(152, 'hr', 'Povratni raspored prenosen na kamionu');
execute upsert_tx(153, 'hr', 'Povratni raspored prenosen na kamionu*');
execute upsert_tx(154, 'hr', 'Prije-utovarni raspored otkazan');
execute upsert_tx(155, 'hr', 'Povratni raspored otkazan');
execute upsert_tx(160, 'hr', 'Raspored otkazan');
execute upsert_tx(161, 'hr', 'Vanjski raspored kreiran');
execute upsert_tx(162, 'hr', 'Raspored uređen');
execute upsert_tx(163, 'hr', 'Vanjski raspored otkazan');
execute upsert_tx(165, 'hr', 'Kamion se vratio');
execute upsert_tx(166, 'hr', 'Narudžba preusmjerena');
execute upsert_tx(170, 'hr', 'Otpremnica');
execute upsert_tx(171, 'hr', 'Postavite silos');
execute upsert_tx(172, 'hr', 'Lijeva');
execute upsert_tx(173, 'hr', 'Skeniranje vagona');
execute upsert_tx(174, 'hr', 'Skeniranje kontejnera');
execute upsert_tx(175, 'hr', 'Izmjena završenog naloga');
execute upsert_tx(201, 'hr', 'Kamion/Vozač promijenjen');
execute upsert_tx(202, 'hr', 'Kompozicija kamiona  promijenjena');
execute upsert_tx(203, 'hr', 'Kompozicija kamiona  potvrđena');
execute upsert_tx(210, 'hr', 'Prometna gužva početak');
execute upsert_tx(211, 'hr', 'Prometna gužva na kraju');
execute upsert_tx(220, 'hr', 'Urediti');
execute upsert_tx(221, 'hr', 'Forced sending to SAP');
execute upsert_tx(300, 'hr', 'Dokument dostave poslan');
execute upsert_tx(301, 'hr', 'Dokument isporuke poslan');
execute upsert_tx(302, 'hr', 'Dokument o prijenosu poslan');
execute upsert_tx(303, 'hr', 'Kreditna bilješka dokument dostave');
execute upsert_tx(304, 'hr', 'Kreditna bilješka dokument isporuke');
execute upsert_tx(310, 'hr', 'Dokument dostave nije poslan');
execute upsert_tx(311, 'hr', 'Dokument isporuke nije poslan');
execute upsert_tx(312, 'hr', 'Dokument o prijenosu nije poslan');
execute upsert_tx(313, 'hr', 'Kreditna bilješka dokument dostave nije poslan');
execute upsert_tx(314, 'hr', 'Kreditna bilješka dokument isporuke  nije poslan');
execute upsert_tx(315, 'hr', 'Promjena rasporeda');
execute upsert_tx(316, 'hr', 'Reguliranje');

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - SR (ENGLISH COPY)
execute upsert_tx(101, 'sr', 'Novi');
execute upsert_tx(102, 'sr', 'Upisan segment');
execute upsert_tx(103, 'sr', 'Potvrđeno-segment');
execute upsert_tx(104, 'sr', 'Dodeljen prevoznik');
execute upsert_tx(105, 'sr', 'Potvrđeno-prevoznik');
execute upsert_tx(106, 'sr', 'Ubačeno u raspored');
execute upsert_tx(107, 'sr', 'Spreman za obradu');
execute upsert_tx(108, 'sr', 'Potvrđeno-vozač ');
execute upsert_tx(109, 'sr', 'Na utovaru');
execute upsert_tx(110, 'sr', 'Utovar');
execute upsert_tx(111, 'sr', 'Istovareno');
execute upsert_tx(112, 'sr', 'Isporuka');
execute upsert_tx(113, 'sr', 'Na mestu isporuke');
execute upsert_tx(114, 'sr', 'Istovar');
execute upsert_tx(115, 'sr', 'Isporučeno');
execute upsert_tx(116, 'sr', 'Realizovano');
execute upsert_tx(117, 'sr', 'Izbrisano');
execute upsert_tx(119, 'sr', 'Poslato železnici');
execute upsert_tx(118, 'sr', 'Pozicija');
execute upsert_tx(120, 'sr', 'Izabrati prevoznika');
execute upsert_tx(121, 'sr', 'Izabrati segment');
execute upsert_tx(122, 'sr', 'Segment potvrđen');
execute upsert_tx(123, 'sr', 'Prevoznik potvrđen');
execute upsert_tx(124, 'sr', 'Otkazati sistem za merenje');
execute upsert_tx(125, 'sr', 'Izabrati prevoznika');
execute upsert_tx(126, 'sr', 'Otkazati prevoznika');
execute upsert_tx(127, 'sr', 'Otkazati prevoznika');
execute upsert_tx(130, 'sr', 'Utovar prekinut');
execute upsert_tx(150, 'sr', 'Pre-utovarni raspored prenesen na kamion');
execute upsert_tx(151, 'sr', 'Pre-utovarni raspored prenesen na kamion*');
execute upsert_tx(152, 'sr', 'Povratni raspored upisan vozilu');
execute upsert_tx(153, 'sr', 'Povratni raspored upisan vozilu*');
execute upsert_tx(154, 'sr', 'Pre-utovarni raspored otkazan');
execute upsert_tx(155, 'sr', 'Otkazan raspored povra''aja');
execute upsert_tx(160, 'sr', 'Raspored otkazan');
execute upsert_tx(161, 'sr', 'Kreiran eksterni raspored');
execute upsert_tx(162, 'sr', 'Raspored uređen');
execute upsert_tx(163, 'sr', 'Otkazan eksterni raspored');
execute upsert_tx(165, 'sr', 'Vraćen kamion');
execute upsert_tx(166, 'sr', 'Nalog ponovo dodeljen kamionu');
execute upsert_tx(170, 'sr', 'Otpremnica');
execute upsert_tx(171, 'sr', 'Izabrati silos');
execute upsert_tx(172, 'sr', 'Napustio mesto isporuke');
execute upsert_tx(173, 'sr', 'Skenirati vagon');
execute upsert_tx(174, 'sr', 'Skenirati kontejner');
execute upsert_tx(175, 'sr', 'Izmenjen realizovan nalog');
execute upsert_tx(201, 'sr', 'Promena vozila/vozača');
execute upsert_tx(202, 'sr', 'Promenjena kompozicija vozila');
execute upsert_tx(203, 'sr', 'Potvrđena kompozicija vozila');
execute upsert_tx(210, 'sr', 'Početak zastoja u saobraćaju');
execute upsert_tx(211, 'sr', 'Kraj zastoja u saobraćaju');
execute upsert_tx(220, 'sr', 'Uređen');
execute upsert_tx(221, 'sr', 'Forced sending to SAP');
execute upsert_tx(300, 'sr', 'Poslata dokumenta o isporuci');
execute upsert_tx(301, 'sr', 'Poslata dokumenta o transportu');
execute upsert_tx(302, 'sr', 'Poslata dokumenta o transferu');
execute upsert_tx(303, 'sr', 'Poslat Credit Note za isporuku ');
execute upsert_tx(304, 'sr', 'Poslat Credit Note za transport');
execute upsert_tx(310, 'sr', 'Greška u slanju dokumenta o isporuci');
execute upsert_tx(311, 'sr', 'Greška u slanju dokumenta o prevozu');
execute upsert_tx(312, 'sr', 'Greška u slanju dokumenta o transferu');
execute upsert_tx(313, 'sr', 'Greška u slanju Credit Note dokum. za isporuku');
execute upsert_tx(314, 'sr', 'Greška u slanju Credit Note dokum. za transport');
execute upsert_tx(315, 'sr', 'Raspored dopunjen');
execute upsert_tx(316, 'sr', 'Regularizacija');

-- Add all keys for missing languages: done in generated file @refdata/insert_refdata_in_all_languages.sql

prompt INSERTING into TLGNR_BIZEVENT_TYPE_TX - TR
execute upsert_tx(101, 'tr', 'New');
execute upsert_tx(102, 'tr', 'Transmitted to pool');
execute upsert_tx(103, 'tr', 'Confirmed by pool');
execute upsert_tx(104, 'tr', 'Transmitted to comm. carrier');
execute upsert_tx(105, 'tr', 'Confirmed by comm. carrier');
execute upsert_tx(106, 'tr', 'Scheduled');
execute upsert_tx(107, 'tr', 'Released for execution');
execute upsert_tx(108, 'tr', 'Confirmed by driver');
execute upsert_tx(109, 'tr', 'Arrived at loadingpoint');
execute upsert_tx(110, 'tr', 'Loading');
execute upsert_tx(111, 'tr', 'Loaded');
execute upsert_tx(112, 'tr', 'Shipping');
execute upsert_tx(113, 'tr', 'Arrived at delivery point');
execute upsert_tx(114, 'tr', 'Unloading');
execute upsert_tx(115, 'tr', 'Delivered');
execute upsert_tx(116, 'tr', 'Finished');
execute upsert_tx(117, 'tr', 'Deleted');
execute upsert_tx(119, 'tr', 'Transmitted to rail company');
execute upsert_tx(118, 'tr', 'Position');
execute upsert_tx(120, 'tr', 'Comm. carrier set');
execute upsert_tx(121, 'tr', 'Set pool');
execute upsert_tx(122, 'tr', 'Pool ack');
execute upsert_tx(123, 'tr', 'Carrier ack');
execute upsert_tx(124, 'tr', 'Cancel on weighing system');
execute upsert_tx(125, 'tr', 'Exec. carrier set');
execute upsert_tx(126, 'tr', 'Exec carrier rejects');
execute upsert_tx(127, 'tr', 'Comm. carrier rejects');
execute upsert_tx(130, 'tr', 'Loading aborted');
execute upsert_tx(150, 'tr', 'Pre-loading ass. tr. to truck');
execute upsert_tx(151, 'tr', 'Pre-loading ass. tr. to truck*');
execute upsert_tx(152, 'tr', 'Return ass. tr. to truck');
execute upsert_tx(153, 'tr', 'Return ass. tr. to truck*');
execute upsert_tx(154, 'tr', 'Pre-loading ass. cancelled');
execute upsert_tx(155, 'tr', 'Return ass. cancelled');
execute upsert_tx(160, 'tr', 'Assignment cancelled');
execute upsert_tx(161, 'tr', 'Ext. assignment created');
execute upsert_tx(162, 'tr', 'Assignment edited');
execute upsert_tx(163, 'tr', 'Ext. assignment cancelled');
execute upsert_tx(165, 'tr', 'Truck returned');
execute upsert_tx(166, 'tr', 'Order reallocated');
execute upsert_tx(170, 'tr', 'Delivery note');
execute upsert_tx(171, 'tr', 'Set silo');
execute upsert_tx(172, 'tr', 'Left delivery point');
execute upsert_tx(173, 'tr', 'Scan wagon');
execute upsert_tx(174, 'tr', 'Scan container');
execute upsert_tx(175, 'tr', 'Modified finished order');
execute upsert_tx(201, 'tr', 'Truck/Driver changed');
execute upsert_tx(202, 'tr', 'Truck composition changed');
execute upsert_tx(203, 'tr', 'Truck composition confirmed');
execute upsert_tx(210, 'tr', 'Trafficjam start');
execute upsert_tx(211, 'tr', 'Trafficjam end');
execute upsert_tx(220, 'tr', 'Edited');
execute upsert_tx(221, 'tr', 'Forced sending to SAP');
execute upsert_tx(300, 'tr', 'Delivery doc sent');
execute upsert_tx(301, 'tr', 'Shipment doc sent');
execute upsert_tx(302, 'tr', 'Transfer doc sent');
execute upsert_tx(303, 'tr', 'CN delivery doc sent');
execute upsert_tx(304, 'tr', 'CN shipment doc sent');
execute upsert_tx(310, 'tr', 'Delivery doc send failed');
execute upsert_tx(311, 'tr', 'Shipment doc send failed');
execute upsert_tx(312, 'tr', 'Transfer doc send failed');
execute upsert_tx(313, 'tr', 'CN delivery doc send failed');
execute upsert_tx(314, 'tr', 'CN shipment doc send failed');
execute upsert_tx(315, 'tr', 'Updated Scheduling');
execute upsert_tx(316, 'tr', 'Regularization');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;

