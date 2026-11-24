-- $Revision$
-- $Date$
--
--LOGON-4819: Added Spanish language support (2012.05.16)
--LOGON-4679: Added ORDER_SPI_CD Z009-Z014 (2012.11.21)
--LOGON-5962: Added ORDER_SPI_CD Z015 (2012.11.21)

prompt '---------- refdata/tlgnr_order_spi.sql ----------'

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
-- tlgnr_order_spi_bs
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_order_spi_cd              in varchar2,
  p_order_spi_txt             in varchar2,
  p_ctl_act_cd                in number)
is
  v_table_name    varchar2(48) := 'tlgnr_order_spi_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_order_spi_cd || ', ctl_act_cd=' || p_ctl_act_cd || ')');

    select count(bs.order_spi_cd)
    into v_exists_flag
    from tlgnr_order_spi_bs bs
    where bs.order_spi_cd = p_order_spi_cd;

    if (v_exists_flag > 0) then
      if (p_ctl_act_cd != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_order_spi_cd || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgnr_order_spi_bs set
/* order_spi_cd: cannot update because this is the key */
          ctl_act_cd                     = p_ctl_act_cd,
          order_spi_txt                = p_order_spi_txt,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          order_spi_cd = p_order_spi_cd
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_order_spi_cd || ').');
      end if;
    else
      insert into tlgnr_order_spi_bs (
        order_spi_cd,
        order_spi_txt,
        ctl_act_cd,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_order_spi_cd,
        p_order_spi_txt,
        p_ctl_act_cd,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_order_spi_cd || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgnr_order_spi_tx
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_order_spi_cd in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgnr_order_spi_tx';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_order_spi_cd || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.order_spi_cd) into v_exists_bs_flag
    from tlgnr_order_spi_bs bs
    where bs.order_spi_cd = p_order_spi_cd;

    if (v_exists_bs_flag > 0) then
      select count(tx.order_spi_cd) into v_exists_flag
      from tlgnr_order_spi_tx tx
      where tx.order_spi_cd = p_order_spi_cd and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgnr_order_spi_tx set
/* p_order_spi_cd and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          order_spi_cd = p_order_spi_cd and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_order_spi_cd || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgnr_order_spi_tx (
          order_spi_cd,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_order_spi_cd,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_order_spi_cd || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_order_spi_cd || ').');
    end if;

  end upsert_tx;
/
show errors;

prompt INSERTING into TLGNR_ORDER_SPI_BS

execute upsert_bs('Z001', 'Multi-Drop Shipment', 1);
execute upsert_bs('Z002', 'Shipment with Pallets return', 1);
execute upsert_bs('Z003', 'Shipment with AFR inbound', 1);
execute upsert_bs('Z004', 'Shipment with raw materials inbound', 1);
execute upsert_bs('Z007', 'Shipment with express return Empty', 1);

--LOGON-4679: Added ORDER_SPI_CD Z009-Z014 (2012.11.21)
execute upsert_bs('Z009', 'Triangular flow - Pays supplying company', 1);
execute upsert_bs('Z010', 'Triangular flow - Pays selling company', 1);
execute upsert_bs('Z011', 'Triangular- Pays selling- Return Pallets', 1);
execute upsert_bs('Z012', 'Triangular- Pays selling- Return Raw Mat', 1);
execute upsert_bs('Z013', 'Selling Company- Cycling trip', 1);
execute upsert_bs('Z014', 'Selling plant - Last trip', 1);

--LOGON-5962: Added ORDER_SPI_CD Z015 (2012.11.21)
execute upsert_bs('Z015', 'Supplying Plant -Last trip', 1);

--Added by Enrique Velasco while enhancing Initial Data for the CITIES machine: 4 new SPIs  (2015.05.05)
execute upsert_bs('Z016', 'Train with 1 wagon', 1);
execute upsert_bs('Z017', 'Train with 2 wagons', 1);
execute upsert_bs('Z018', 'Train with 3-5 wagons', 1);
execute upsert_bs('Z019', 'Train with 6-20 wagons', 1);

prompt INSERTING into TLGNR_ORDER_SPI_TX - EN
execute upsert_tx('Z001', 'en', 'Multi-Drop Shipment');
execute upsert_tx('Z002', 'en', 'Shipment with Pallets return');
execute upsert_tx('Z003', 'en', 'Shipment with AFR inbound');
execute upsert_tx('Z004', 'en', 'Shipment with raw materials inbound');
execute upsert_tx('Z007', 'en', 'Shipment with express return Empty');
execute upsert_tx('Z009', 'en', 'Triangular flow - Pays supplying company');
execute upsert_tx('Z010', 'en', 'Triangular flow - Pays selling company');
execute upsert_tx('Z011', 'en', 'Triangular- Pays selling- Return Pallets');
execute upsert_tx('Z012', 'en', 'Triangular- Pays selling- Return Raw Mat');
execute upsert_tx('Z013', 'en', 'Selling Company- Cycling trip');
execute upsert_tx('Z014', 'en', 'Selling plant - Last trip');
execute upsert_tx('Z015', 'en', 'Supplying Plant -Last trip');
execute upsert_tx('Z016', 'en', 'Train with 1 wagon');
execute upsert_tx('Z017', 'en', 'Train with 2 wagons');
execute upsert_tx('Z018', 'en', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'en', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - DE
execute upsert_tx('Z001', 'de', 'Multidrop Belieferung');
execute upsert_tx('Z002', 'de', 'Lieferung mit Rück-Paletten ');
execute upsert_tx('Z003', 'de', 'Lieferung mit AFR Anlieferung');
execute upsert_tx('Z004', 'de', 'Lieferung mit Rohmaterial Anlieferung');
execute upsert_tx('Z007', 'de', 'Lieferung mit leerer Express-Rückkehr');
execute upsert_tx('Z009', 'de', 'Triangular - Lieferant zahlt');
execute upsert_tx('Z010', 'de', 'Triangular - Verkäufer zahlt');
execute upsert_tx('Z011', 'de', 'Triangular - Verkäufer zahlt - Retour Paletten');
execute upsert_tx('Z012', 'de', 'Triangular - Verkäufer zahlt - Retour Rohmaterial');
execute upsert_tx('Z013', 'de', 'Umfuhr');
execute upsert_tx('Z014', 'de', 'Letzte Fuhre (Verkaufswerk)');
execute upsert_tx('Z015', 'de', 'Letzte Fuhre (Lieferwerk)');
execute upsert_tx('Z016', 'de', 'Train with 1 wagon');
execute upsert_tx('Z017', 'de', 'Train with 2 wagons');
execute upsert_tx('Z018', 'de', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'de', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - FR
execute upsert_tx('Z001', 'fr', 'Expédition multi-drop');
execute upsert_tx('Z002', 'fr', 'Expédition avec des palettes de retour');
execute upsert_tx('Z003', 'fr', 'Expédition avec AFR inbound');
execute upsert_tx('Z004', 'fr', 'Expédition avec AFR inbound');
execute upsert_tx('Z007', 'fr', 'Expédition avec retour express vide');
execute upsert_tx('Z009', 'fr', 'Triangular flow - Pays supplying company');
execute upsert_tx('Z010', 'fr', 'Triangular flow - Pays selling company');
execute upsert_tx('Z011', 'fr', 'Triangular- Pays selling- Return Pallets');
execute upsert_tx('Z012', 'fr', 'Triangular- Pays selling- Return Raw Mat');
execute upsert_tx('Z013', 'fr', 'Selling Company- Cycling trip');
execute upsert_tx('Z014', 'fr', 'Selling plant - Last trip');
execute upsert_tx('Z015', 'fr', 'Supplying Plant -Last trip');
execute upsert_tx('Z016', 'fr', 'Train with 1 wagon');
execute upsert_tx('Z017', 'fr', 'Train with 2 wagons');
execute upsert_tx('Z018', 'fr', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'fr', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - IT
execute upsert_tx('Z001', 'it', 'Spedizione multipla');
execute upsert_tx('Z002', 'it', 'Spedizione con ritorno di Bancali');
execute upsert_tx('Z003', 'it', 'Spedizione con AFR in entrata');
execute upsert_tx('Z004', 'it', 'Spedizione con materie prime in entrata');
execute upsert_tx('Z007', 'it', 'Spedizione express con ritorno vuoto');
execute upsert_tx('Z009', 'it', 'Triangolazione - Paga società fornitrice');
execute upsert_tx('Z010', 'it', 'Triangolazione - Paga società venditrice');
execute upsert_tx('Z011', 'it', 'Triangolazione- Paga chi vende- Pallets resi');
execute upsert_tx('Z012', 'it', 'Triangolar-Paga chi vende-Reso di materia prima');
execute upsert_tx('Z013', 'it', 'Società venditrice- Viaggio cicliclo');
execute upsert_tx('Z014', 'it', 'Impianto venditore - Ultimo viaggio');
execute upsert_tx('Z015', 'it', 'Impianto fornitore -Ultimo viaggio');
execute upsert_tx('Z016', 'it', 'Train with 1 wagon');
execute upsert_tx('Z017', 'it', 'Train with 2 wagons');
execute upsert_tx('Z018', 'it', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'it', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - ES
execute upsert_tx('Z001', 'es', 'Transporte Multi-Vuelco');
execute upsert_tx('Z002', 'es', 'Transporte con devolución de palés');
execute upsert_tx('Z003', 'es', 'Transporte con entrada de AFR');
execute upsert_tx('Z004', 'es', 'Transporte con entrada de materias primas');
execute upsert_tx('Z007', 'es', 'Transporte con retorno vació exprés');
execute upsert_tx('Z009', 'es', 'Flujo triangular - Compañía suministradora paga');
execute upsert_tx('Z010', 'es', 'Flujo triangular - Compañía vendedora paga');
execute upsert_tx('Z011', 'es', 'Triangular- Paga vendedor- Devolución Pallets');
execute upsert_tx('Z012', 'es', 'Triangular-Paga vendedor-Devolución Materia Prima');
execute upsert_tx('Z013', 'es', 'Compañía vendedora- Tour cerrado');
execute upsert_tx('Z014', 'es', 'Planta vendedora - Último viaje');
execute upsert_tx('Z015', 'es', 'Planta suministradora -Último viaje');
execute upsert_tx('Z016', 'es', 'Train with 1 wagon');
execute upsert_tx('Z017', 'es', 'Train with 2 wagons');
execute upsert_tx('Z018', 'es', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'es', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - CS
execute upsert_tx('Z001', 'cs', 'Více materiálová objednávka');
execute upsert_tx('Z002', 'cs', 'Dodávka s návratem palet');
execute upsert_tx('Z003', 'cs', 'Dodávka s AFR inbound zpět');
execute upsert_tx('Z004', 'cs', 'Dodávka s přepravou materiálů do výroby zpět');
execute upsert_tx('Z007', 'cs', 'Dodávka s rychlým návratem - prázdný');
execute upsert_tx('Z009', 'cs', 'Triangular proces-Placeno odesílající společností');
execute upsert_tx('Z010', 'cs', 'Triangular proces-Placeno prodávající společností');
execute upsert_tx('Z011', 'cs', 'Triangular- Placeno prodávajícím- Návrat palet');
execute upsert_tx('Z012', 'cs', 'Triang-Placeno prodávajícím-Návrat materiály');
execute upsert_tx('Z013', 'cs', 'Prodávající společnost- přeprava v kole');
execute upsert_tx('Z014', 'cs', 'Prodávající závod - jednosměrně');
execute upsert_tx('Z015', 'cs', 'Dodávající závod -jednosměrně');
execute upsert_tx('Z016', 'cs', 'Train with 1 wagon');
execute upsert_tx('Z017', 'cs', 'Train with 2 wagons');
execute upsert_tx('Z018', 'cs', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'cs', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - SK
execute upsert_tx('Z001', 'sk', 'Viac-vykládková preprava');
execute upsert_tx('Z002', 'sk', 'Preprava s vrátenými paletami');
execute upsert_tx('Z003', 'sk', 'Preprava s prichádzajúcim AFR');
execute upsert_tx('Z004', 'sk', 'Preprava s prichádzajúcimi surovinami');
execute upsert_tx('Z007', 'sk', 'Preprava s rýchlym návratom prázdny');
execute upsert_tx('Z009', 'sk', 'Triangulárný tok-Platba dodávateľskej spoločnosti');
execute upsert_tx('Z010', 'sk', 'Triangulárný tok - Platba predávajúcej spoločnosti');
execute upsert_tx('Z011', 'sk', 'Triangulárne- Platí predávajúci- Návrat palety');
execute upsert_tx('Z012', 'sk', 'Triangulárne- Platí predávajúci- Návrat suroviny');
execute upsert_tx('Z013', 'sk', 'Predávajúca spoločnosť- cyklická preprava');
execute upsert_tx('Z014', 'sk', 'Predávajúci závod - posledná preprava');
execute upsert_tx('Z015', 'sk', 'Dodávajúci závod -posledná preprava');
execute upsert_tx('Z016', 'sk', 'Train with 1 wagon');
execute upsert_tx('Z017', 'sk', 'Train with 2 wagons');
execute upsert_tx('Z018', 'sk', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'sk', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - HU
execute upsert_tx('Z001', 'hu', 'Többlépcsős szállítás');
execute upsert_tx('Z002', 'hu', 'Szállítás raklap visszaszállítással');
execute upsert_tx('Z003', 'hu', 'Szállítás beérkező AFR-rel');
execute upsert_tx('Z004', 'hu', 'Szállítás beérkező alapanyaggal');
execute upsert_tx('Z007', 'hu', 'Gyors visszaszállítás');
execute upsert_tx('Z009', 'hu', 'Háromszög áramlás - Szállító cég fizet');
execute upsert_tx('Z010', 'hu', 'Háromszög áramlás - Értékesítő cég fizet');
execute upsert_tx('Z011', 'hu', 'Triangular-Értékesítő fizet-Raklap visszaszállítás');
execute upsert_tx('Z012', 'hu', 'Triangular-Vevő fizet - alapanyag return');
execute upsert_tx('Z013', 'hu', 'Értékesítő cég- Cycling trip');
execute upsert_tx('Z014', 'hu', 'Értékesítő gyár - Végső út');
execute upsert_tx('Z015', 'hu', 'Ellátó gyár - Utolsó út');
execute upsert_tx('Z016', 'hu', 'Train with 1 wagon');
execute upsert_tx('Z017', 'hu', 'Train with 2 wagons');
execute upsert_tx('Z018', 'hu', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'hu', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - RO
execute upsert_tx('Z001', 'ro', 'Expeditie Destinatii multiple');
execute upsert_tx('Z002', 'ro', 'Expeditie cu retur paleti');
execute upsert_tx('Z003', 'ro', 'Expeditie cu inbound AFR');
execute upsert_tx('Z004', 'ro', 'Expeditie cu inbound materii prime');
execute upsert_tx('Z007', 'ro', 'Expeditie cu retur expres gol');
execute upsert_tx('Z009', 'ro', 'Curs triunghiular - Plateste compania furnizoare');
execute upsert_tx('Z010', 'ro', 'Curs triunghiular - Plateste compania vanzatoare');
execute upsert_tx('Z011', 'ro', 'Triunghiular- Plateste vanzator- Retur paleti');
execute upsert_tx('Z012', 'ro', 'Triunghiular-Plateste vanzator-Retur materii prime');
execute upsert_tx('Z013', 'ro', 'Companie vanzatoare- Tur ciclu');
execute upsert_tx('Z014', 'ro', 'Companie vanzatoare - Ultima oprire');
execute upsert_tx('Z015', 'ro', 'Fabrica Furnizor -Ultima oprire');
execute upsert_tx('Z016', 'ro', 'Train with 1 wagon');
execute upsert_tx('Z017', 'ro', 'Train with 2 wagons');
execute upsert_tx('Z018', 'ro', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'ro', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - BG
execute upsert_tx('Z001', 'bg', 'Multi-drop изпращане');
execute upsert_tx('Z002', 'bg', 'Пратка с върнати палети');
execute upsert_tx('Z003', 'bg', 'Пратка с AFR входящ');
execute upsert_tx('Z004', 'bg', 'Пратка с входящи суровини');
execute upsert_tx('Z007', 'bg', 'Пратка с експресно връщане празно');
execute upsert_tx('Z009', 'bg', 'Триъгълен поток - заплаща фирмата доставчик');
execute upsert_tx('Z010', 'bg', 'Триъгълен поток - заплаща продаващата фирма');
execute upsert_tx('Z011', 'bg', 'Триъгълен- Плащане- Върнати палети');
execute upsert_tx('Z012', 'bg', 'Триъгълен- Плащане- Върнати суровини');
execute upsert_tx('Z013', 'bg', 'Фирма продавач- Пътуване');
execute upsert_tx('Z014', 'bg', 'Завод продавач - Последно пътуване');
execute upsert_tx('Z015', 'bg', 'Доставящ завод -Последно пътуване');
execute upsert_tx('Z016', 'bg', 'Train with 1 wagon');
execute upsert_tx('Z017', 'bg', 'Train with 2 wagons');
execute upsert_tx('Z018', 'bg', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'bg', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - RU
execute upsert_tx('Z001', 'ru', 'доставка с мульти-выгрузкой');
execute upsert_tx('Z002', 'ru', 'доставка с возвратом паллет');
execute upsert_tx('Z003', 'ru', 'доставка с входящим AFR ');
execute upsert_tx('Z004', 'ru', 'доставка с входящим сырьем');
execute upsert_tx('Z007', 'ru', 'Отгрузка с ускоренным возвратом порожним');
execute upsert_tx('Z009', 'ru', 'Трехст.-платежи поставляющей компании');
execute upsert_tx('Z010', 'ru', 'Трехст.-платежи продающей компании');
execute upsert_tx('Z011', 'ru', 'Трехст.-платежи продающей компании-возврат паллет');
execute upsert_tx('Z012', 'ru', 'Трехст.-платежи продающей компании-возврат сырья');
execute upsert_tx('Z013', 'ru', 'Selling Company-Cycling trip');
execute upsert_tx('Z014', 'ru', 'Продающий завод-последняя поездка');
execute upsert_tx('Z015', 'ru', 'Поставляющий завод-последняя поездка');
execute upsert_tx('Z016', 'ru', 'Train with 1 wagon');
execute upsert_tx('Z017', 'ru', 'Train with 2 wagons');
execute upsert_tx('Z018', 'ru', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'ru', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - PL
execute upsert_tx('Z001', 'pl', 'Przesyłka Multi-Drop');
execute upsert_tx('Z002', 'pl', 'Przesyłka ze zwrotem palet');
execute upsert_tx('Z003', 'pl', 'Przesyłka ze zwrotnym AFR');
execute upsert_tx('Z004', 'pl', 'Przesyłka przychodząca z surowcami');
execute upsert_tx('Z007', 'pl', 'Przesyłka z szybkim zwrotem pusta');
execute upsert_tx('Z009', 'pl', 'Trójstronny przepływ - płaci zaopatrująca firma');
execute upsert_tx('Z010', 'pl', 'Trójstronny przepływ - płaci sprzedająca firma');
execute upsert_tx('Z011', 'pl', 'Trójstronny - płaci sprzedający - zwrot palet');
execute upsert_tx('Z012', 'pl', 'Trójstronny - płaci sprzedający - zwrot surowców');
execute upsert_tx('Z013', 'pl', 'Sprzedająca firma - Cycling trip');
execute upsert_tx('Z014', 'pl', 'Zakład sprzedający - ostatnia podróż');
execute upsert_tx('Z015', 'pl', 'Zakład zaopatrujący - ostatnia podróż');
execute upsert_tx('Z016', 'pl', 'Train with 1 wagon');
execute upsert_tx('Z017', 'pl', 'Train with 2 wagons');
execute upsert_tx('Z018', 'pl', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'pl', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - AZ (ENGLISH COPY)
execute upsert_tx('Z001', 'az', 'Çoxnöqtəli nəqletmə');
execute upsert_tx('Z002', 'az', 'Altlıqlarla nəqletmə qaytarması');
execute upsert_tx('Z003', 'az', 'Daxilolan AFR ilə nəqletmə');
execute upsert_tx('Z004', 'az', 'Daxilolan xammalla nəqletmə');
execute upsert_tx('Z007', 'az', 'Boş qayıtma ilə nəqletmə');
execute upsert_tx('Z009', 'az', 'Üçbucaqlı axın – təchizatçı şirkət ödəyir');
execute upsert_tx('Z010', 'az', 'Üçbucaqlı axın – satıcı firma ödəyir');
execute upsert_tx('Z011', 'az', 'Üçbucaq – satan ödəyir – qayıdış altlıqları');
execute upsert_tx('Z012', 'az', 'Üçbucaq – satan ödəyir – qayıdış xammal');
execute upsert_tx('Z013', 'az', 'Satan şirkət – Velosiped yürüşü');
execute upsert_tx('Z014', 'az', 'Satan zavod – Son yürüş');
execute upsert_tx('Z015', 'az', 'Təchiz edən zavod – Son yürüş');
execute upsert_tx('Z016', 'az', 'Train with 1 wagon');
execute upsert_tx('Z017', 'az', 'Train with 2 wagons');
execute upsert_tx('Z018', 'az', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'az', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - HR
execute upsert_tx('Z001', 'hr', 'Multi Drop pošiljka');
execute upsert_tx('Z002', 'hr', 'Pošiljka s  povratom paleta');
execute upsert_tx('Z003', 'hr', 'Pošiljka s ulazom AFR-a');
execute upsert_tx('Z004', 'hr', 'Pošiljka sa ulazom sirovina');
execute upsert_tx('Z007', 'hr', 'Express Pošiljka bez povrata');
execute upsert_tx('Z009', 'hr', 'Trokutasti tok - plaća dobavljač');
execute upsert_tx('Z010', 'hr', 'Trokutasti tok - plaća prodavač');
execute upsert_tx('Z011', 'hr', 'Trokutasti-isplatiti - Povratak paleta');
execute upsert_tx('Z012', 'hr', 'Trokutasti -isplatiti -  Povratak sirovine');
execute upsert_tx('Z013', 'hr', 'Prodajna tvrtka - Cycling trip');
execute upsert_tx('Z014', 'hr', 'Tvornica prodavača - Posljednje putovanje');
execute upsert_tx('Z015', 'hr', 'Tvornica dobavljača - Posljednje putovanje');
execute upsert_tx('Z016', 'hr', 'Train with 1 wagon');
execute upsert_tx('Z017', 'hr', 'Train with 2 wagons');
execute upsert_tx('Z018', 'hr', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'hr', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - SR
execute upsert_tx('Z001', 'sr', 'Multi-Drop prevoz');
execute upsert_tx('Z002', 'sr', 'Transport sa povraćajem paleta');
execute upsert_tx('Z003', 'sr', 'Transport sa dopremom AFR materijala');
execute upsert_tx('Z004', 'sr', 'Transport sa dopremom repromaterijala');
execute upsert_tx('Z007', 'sr', 'Isporuka sa hitnim povraćajem bez tereta');
execute upsert_tx('Z009', 'sr', 'Triangular-Plaća pošiljalac');
execute upsert_tx('Z010', 'sr', 'Triangular-Plaća prodavac');
execute upsert_tx('Z011', 'sr', 'Triangular-Plaća prodavac-povraćaj paleta');
execute upsert_tx('Z012', 'sr', 'Triangular-Plaća prodavac-doprema repromaterijala');
execute upsert_tx('Z013', 'sr', 'Prodavac - kružna ruta');
execute upsert_tx('Z014', 'sr', 'Fabrika koja prodaje - poslednji prevoz');
execute upsert_tx('Z015', 'sr', 'Fabrika koja snabdeva - poslednji prevoz');
execute upsert_tx('Z016', 'sr', 'Train with 1 wagon');
execute upsert_tx('Z017', 'sr', 'Train with 2 wagons');
execute upsert_tx('Z018', 'sr', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'sr', 'Train with 6-20 wagons');

prompt INSERTING into TLGNR_ORDER_SPI_TX - TR
execute upsert_tx('Z001', 'tr', 'Multi-Drop Shipment');
execute upsert_tx('Z002', 'tr', 'Shipment with Pallets return');
execute upsert_tx('Z003', 'tr', 'Shipment with AFR inbound');
execute upsert_tx('Z004', 'tr', 'Shipment with raw materials inbound');
execute upsert_tx('Z007', 'tr', 'Shipment with express return Empty');
execute upsert_tx('Z009', 'tr', 'Triangular flow - Pays supplying company');
execute upsert_tx('Z010', 'tr', 'Triangular flow - Pays selling company');
execute upsert_tx('Z011', 'tr', 'Triangular- Pays selling- Return Pallets');
execute upsert_tx('Z012', 'tr', 'Triangular- Pays selling- Return Raw Mat');
execute upsert_tx('Z013', 'tr', 'Selling Company- Cycling trip');
execute upsert_tx('Z014', 'tr', 'Selling plant - Last trip');
execute upsert_tx('Z015', 'tr', 'Supplying Plant -Last trip');
execute upsert_tx('Z016', 'tr', 'Train with 1 wagon');
execute upsert_tx('Z017', 'tr', 'Train with 2 wagons');
execute upsert_tx('Z018', 'tr', 'Train with 3-5 wagons');
execute upsert_tx('Z019', 'tr', 'Train with 6-20 wagons');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
