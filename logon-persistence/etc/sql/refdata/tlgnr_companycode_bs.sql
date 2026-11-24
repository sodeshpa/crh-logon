-- ************ Start LOGON-7867: COUNTRY_CD changes  ***********
-- Added region_cd field to tlgnr_companycode_bs table and have to fill the missing values

prompt '---------- refdata/tlgnr_companycode_bs.sql ----------'

-- so we can escape the ampersand char in company names
SET ESCAPE ON 
set serveroutput on

-------------------------------------------------------------------------------
-- tlgnr_companycode_bs
-------------------------------------------------------------------------------
create or replace procedure upsert_bs (
  p_companycode_cd   in varchar2,
  p_companycode_name in varchar2,
  p_region_cd 		 in varchar2)
is
  v_table_name    varchar2(21) := 'tlgnr_companycode_bs';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_companycode_cd || ', region_cd=' || p_region_cd || ')');
    
    select count(bs.companycode_cd)
    into v_exists_flag
    from tlgnr_companycode_bs bs
    where bs.companycode_cd = p_companycode_cd;

    if (v_exists_flag > 0) then
		update tlgnr_companycode_bs set
			region_cd	= p_region_cd,
			ctl_mod_uid   = 1,
			ctl_mod_ts    = systimestamp
		where
			companycode_cd = p_companycode_cd
		;
		dbms_output.put_line ('UPDATED: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_companycode_cd || ').');
    else
		insert into tlgnr_companycode_bs (
			companycode_cd,
        	companycode_name,
        	region_cd,
        	ctl_act_cd, ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      	) values (
        	p_companycode_cd,
        	p_companycode_name,
        	p_region_cd,
        	1, 1, systimestamp, 1, systimestamp
      	);
		dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_companycode_cd || ').');
    end if;
  end upsert_bs;
/
show errors;

execute upsert_bs('AT01', 'Crh (Wien) GmbH', 'AT');
execute upsert_bs('AT04', 'ecorec Österreich GmbH', 'AT');
execute upsert_bs('AT50', 'Crh (Vorarlberg) GmbH', 'AT');
execute upsert_bs('ATZZ', 'Legacy Austria - HR', 'AT');
execute upsert_bs('BE01', 'Crh (Belgique) SA', 'BE');
execute upsert_bs('BE02', 'Crh Bétons (Belgique)', 'BE');
execute upsert_bs('BE06', 'Geocycle SA', 'BE');
execute upsert_bs('BE08', 'Crh Environment Servic', 'BE');
execute upsert_bs('BE10', 'Carrières de Leffe SA', 'BE');
execute upsert_bs('BE11', 'Calcaires Rive Gauche I S', 'BE');
execute upsert_bs('BE12', 'Crh GeoMinerals SDC', 'BE');
execute upsert_bs('BEZZ', 'Legacy Belgium - HR', 'BE');
execute upsert_bs('BG01', 'Crh (Bulgaria) AD', 'BG');
execute upsert_bs('BG02', 'Crh Karierni Materiali', 'BG');
execute upsert_bs('BG03', 'HKM Plovdiv AD', 'BG');
execute upsert_bs('BG04', 'Vris EOOD', 'BG');
execute upsert_bs('BG05', 'ecorec Bulgaria EOOD', 'BG');
execute upsert_bs('BG06', 'Gumirec EAD', 'BG');
execute upsert_bs('BG07', 'HKM Rudinata AD', 'BG');
execute upsert_bs('BGZZ', 'Legacy Bulgaria - HR', 'BG');
execute upsert_bs('CH01', 'Crh (Schweiz) AG', 'CH');
execute upsert_bs('CH02', 'Crh Kies und Beton AG', 'CH');
execute upsert_bs('CH03', 'Crh (Cham) AG', 'CH');
execute upsert_bs('CH04', 'Crh Bétondrance SA', 'CH');
execute upsert_bs('CH05', 'Crh BF+P SA', 'CH');
execute upsert_bs('CH07', 'Basaltstein AG', 'CH');
execute upsert_bs('CH08', 'AG Hunziker \& Cie', 'CH');
execute upsert_bs('CH09', 'Kalt Kies- und Betonwerk', 'CH');
execute upsert_bs('CH10', 'Praz SA', 'CH');
execute upsert_bs('CH81', 'Crh Kies und Beton AG', 'CH');
execute upsert_bs('CH99', 'Crh Europe Services AG', 'CH');
execute upsert_bs('CHZZ', 'Legacy Switzerland - HR', 'CH');
execute upsert_bs('CZZZ', 'Legacy Czech Rep - HR', 'CZ');
execute upsert_bs('DE01', 'Crh (Deutschland) AG', 'DE');
execute upsert_bs('DE02', 'Arge Salzbeton Morsleben', 'DE');
execute upsert_bs('DE03', 'Crh Beton und Zuschlag', 'DE');
execute upsert_bs('DE04', 'Union-Transportbeton GmbH', 'DE');
execute upsert_bs('DE05', 'Union-Beton Beteil. GmbH', 'DE');
execute upsert_bs('DE06', 'Betontechnik Nord GmbH', 'DE');
execute upsert_bs('DE07', 'Kieswerk Borsberg GmbH', 'DE');
execute upsert_bs('DE09', 'Crh KW Zeithain KG', 'DE');
execute upsert_bs('DE10', 'VETRA BFTW GmbH, Oldenb.', 'DE');
execute upsert_bs('DE11', 'VETRA BFTW GmbH\&Co.KG, Nb', 'DE');
execute upsert_bs('DE12', 'Betonfertigteilwerke GmbH', 'DE');
execute upsert_bs('DE13', 'BLS Beton-Labor Soltau Gm', 'DE');
execute upsert_bs('DE14', 'Schulenburg GmbH', 'DE');
execute upsert_bs('DE17', 'Hemmoor Zement AG i.A.', 'DE');
execute upsert_bs('DE18', 'Arb-Wofa-Stiftung Hemmoor', 'DE');
execute upsert_bs('DE19', 'Ang-Wofa-Stiftung Hemmoor', 'DE');
execute upsert_bs('DE20', 'U-Fonds der APCH', 'DE');
execute upsert_bs('DE22', 'Verw.Ges. Kiesw. Borsberg', 'DE');
execute upsert_bs('DE24', 'HABEWO Vermögensverwalt.', 'DE');
execute upsert_bs('DE25', 'Versorgungskasse NC', 'DE');
execute upsert_bs('DE26', 'VG Crh KW Zeithain mbH', 'DE');
execute upsert_bs('DE33', 'Unterstützungskasse AB', 'DE');
execute upsert_bs('DE36', 'SBU Dresden GmbH \& Co.KG', 'DE');
execute upsert_bs('DE37', 'Stiftung Bau-Steine-Erden', 'DE');
execute upsert_bs('DE40', 'Happy Beton Gmb \& Co.KG', 'DE');
execute upsert_bs('DE41', 'Happy Kies S Rec.GmbH\&Co.', 'DE');
execute upsert_bs('DE42', 'Happy Translogistik GmbH', 'DE');
execute upsert_bs('DE50', 'Hüttensand Salzgitter Gmb', 'DE');
execute upsert_bs('DE51', 'Hüttensand Salzgitter Ver', 'DE');
execute upsert_bs('DE52', 'VETRA GmbH\&Co.KG', 'DE');
execute upsert_bs('DE53', 'VETRA Verwalt. GmbH', 'DE');
execute upsert_bs('DE55', 'Fertigbeton Saldern\&Co.KG', 'DE');
execute upsert_bs('DE57', 'Union Beton NSach \&CO. KG', 'DE');
execute upsert_bs('DE58', 'Union Beton NSach GmbH', 'DE');
execute upsert_bs('DE65', 'Heide Transportbeton GmbH', 'DE');
execute upsert_bs('DE66', 'Heide-Wind Verwalt. GmbH', 'DE');
execute upsert_bs('DE67', 'Kieswerk Leinetal  \&Co.KG', 'DE');
execute upsert_bs('DE68', 'Kieswerk Leinetal  GmbH', 'DE');
execute upsert_bs('DE70', 'Crh (Süddeutschland) G', 'DE');
execute upsert_bs('DE71', 'Rohstoffgewinnung Hohente', 'DE');
execute upsert_bs('DE72', 'Glaser Sand- und Kieswer', 'DE');
execute upsert_bs('DE73', 'Glaser Trockensand GmbH', 'DE');
execute upsert_bs('DE80', 'Crh Kies und Beton Gmb', 'DE');
execute upsert_bs('DE89', 'Osteuropäische Zementbete', 'DE');
execute upsert_bs('DE91', 'Crh Schiffarht Baustof', 'DE');
execute upsert_bs('DEK1', 'Adjustments on G_DEU_1', 'DE');
execute upsert_bs('DEZZ', 'Legacy Germany - HR', 'DE');
execute upsert_bs('ES28', 'CRH Services EMEA, S.L', 'ES');
execute upsert_bs('ESZZ', 'Legacy Spain - HR', 'ES');
execute upsert_bs('FR01', 'Crh (France) SAS', 'FR');
execute upsert_bs('FR02', 'Crh Bétons (France)SAS', 'FR');
execute upsert_bs('FR03', 'Crh Granulats (France)', 'FR');
execute upsert_bs('FR05', 'Crh Informatique SAS', 'FR');
execute upsert_bs('FR06', 'Dunkerque Ajouts SNC', 'FR');
execute upsert_bs('FR07', 'VDN S.A.S', 'FR');
execute upsert_bs('FR09', 'Béton Castel SAS', 'FR');
execute upsert_bs('FR10', 'Béton Rapid SA', 'FR');
execute upsert_bs('FR11', 'Carrière Bourgogne Sud SA', 'FR');
execute upsert_bs('FR12', 'Socalcor SA', 'FR');
execute upsert_bs('FR13', 'Granulats de Franche Comt', 'FR');
execute upsert_bs('FR14', 'SOCOVAL SARL', 'FR');
execute upsert_bs('FR17', 'Crh Granulats Distribu', 'FR');
execute upsert_bs('FR18', 'Crh Bétons (Ouest) SAS', 'FR');
execute upsert_bs('FR20', 'Dijon Béton', 'FR');
execute upsert_bs('FR21', 'Carrières de Ternuay', 'FR');
execute upsert_bs('FR23', 'EST Granulats', 'FR');
execute upsert_bs('FR25', 'Gravières de la Hardt SNC', 'FR');
execute upsert_bs('FR28', 'Atlantique Négoce SAS', 'FR');
execute upsert_bs('FR29', 'Euro Stockage SAS', 'FR');
execute upsert_bs('FR30', 'EDCM', 'FR');
execute upsert_bs('FR31', 'SAS MPA', 'FR');
execute upsert_bs('FRZZ', 'Legacy France - HR', 'FR');
execute upsert_bs('HBEL', 'Holcibel S.A.', 'BE');
execute upsert_bs('HFRA', 'Crh Investments (Franc', 'FR');
execute upsert_bs('HR01', 'Crh (Hrvatska) d.o.o.', 'HR');
execute upsert_bs('HR02', 'ecorec d.o.o.', 'HR');
execute upsert_bs('HR03', 'Crh mineralni agre.d.o', 'HR');
execute upsert_bs('HR04', 'Crh mineralni agre.d.o', 'HR');
execute upsert_bs('HR05', 'Transplus d.o.o.', 'HR');
execute upsert_bs('HR06', 'Resnik d.o.o.', 'HR');
execute upsert_bs('HRZZ', 'Legacy Croatia - HR', 'HR');
execute upsert_bs('HT01', 'Crh Trading, S.A.', 'ES');
execute upsert_bs('HT02', 'Iberoamericana de Cemento', 'ES');
execute upsert_bs('HT03', 'Crh Trading PTE LTD', 'SG');
execute upsert_bs('HT04', 'Crh Trading FZCO', 'AE');
execute upsert_bs('HT05', 'UMAR Ltd.', 'BM');
execute upsert_bs('HT06', 'Hansen Holding, S.A.', 'PA');
execute upsert_bs('HT07', 'Holsten International, LT', 'VG');
execute upsert_bs('HT08', 'Yemen Cement Holding LTD', 'VG');
execute upsert_bs('HT09', 'CAN-CIMED Trading LTD.', 'VG');
execute upsert_bs('HT10', 'Seamar Trading', 'PA');
execute upsert_bs('HT11', 'Seamar Shipping', 'PA');
execute upsert_bs('HT12', 'Seamar Enterprises', 'PA');
execute upsert_bs('HT13', 'Iberoamérica Cement Tradi', 'ES');
execute upsert_bs('HT14', 'Kimaxtra Produtos de Cons', 'PT');
execute upsert_bs('HT15', 'Ecotrade International', 'CH');
execute upsert_bs('HTZZ', 'Legacy Trading - HR', 'ES');
execute upsert_bs('HU11', 'ecorec Hungária Kft.', 'HU');
execute upsert_bs('HU12', 'AM  Vagyonkezelõ Kft', 'HU');
execute upsert_bs('HU16', 'TransPlus(Hungary)Kft.', 'HU');
execute upsert_bs('HU17', 'Crh Magyarország Kft.', 'HU');
execute upsert_bs('HU19', 'Nyerges Real  2012  Kft', 'HU');
execute upsert_bs('HU20', 'Osteuropäische Zementbete', 'HU');
execute upsert_bs('HUZZ', 'Legacy Hungary - HR', 'HU');
execute upsert_bs('IT01', 'Crh (Italia) S.p.A.', 'IT');
execute upsert_bs('IT03', 'Eurofuels S.p.A.', 'IT');
execute upsert_bs('IT05', 'Micron Mineral S.r.l.', 'IT');
execute upsert_bs('IT10', 'Rolcim S.p.A.', 'IT');
execute upsert_bs('IT30', 'Crh Aggregati Calcest', 'IT');
execute upsert_bs('IT39', 'F.lli Manara  \& C. Srl', 'IT');
execute upsert_bs('IT49', 'Crh Gruppo (Italia)SPA', 'IT');
execute upsert_bs('IT91', 'Fusine Energia S.r.l.', 'IT');
execute upsert_bs('IT92', 'Fonte Curella S.r.l.', 'IT');
execute upsert_bs('IT97', 'Shipping Adv \& Serv. S.R.', 'IT');
execute upsert_bs('ITZZ', 'Legacy Italy - HR', 'IT');
execute upsert_bs('LU01', 'Crh (Luxembourg) SA', 'LU');
execute upsert_bs('LUZZ', 'Legacy Luxembourg - HR', 'LU');
execute upsert_bs('NCLH', 'Crh (Nouv. Calédonie)', 'NC');
execute upsert_bs('NL01', 'Crh (Nederland) B.V.', 'NL');
execute upsert_bs('NL02', 'Crh Betonmortel B.V.', 'NL');
execute upsert_bs('NL08', 'Crh Coastal BV', 'NL');
execute upsert_bs('NL09', 'Crh Prefab Wanden BV', 'NL');
execute upsert_bs('NLZZ', 'Legacy Netherlands - HR', 'NL');
execute upsert_bs('PL01', 'Crh (Polska) Sp. Z o.o', 'PL');
execute upsert_bs('RO01', 'Crh (Romania) SA', 'RO');
execute upsert_bs('RO02', 'ecovalor SRL', 'RO');
execute upsert_bs('RO03', 'Estagre SRL', 'RO');
execute upsert_bs('RO04', 'Agrocomp International SR', 'RO');
execute upsert_bs('RO05', 'TransPlus Activ (Romania)', 'RO');
execute upsert_bs('ROZZ', 'Legacy Romania - HR', 'RO');
execute upsert_bs('RS01', 'Crh (Srbija) d.o.o.', 'RS');
execute upsert_bs('RS02', 'ecorec d.o.o.', 'RS');
execute upsert_bs('RS03', 'Crh Agregati a.d.', 'RS');
execute upsert_bs('RS04', 'AGG Proizvodnja d.o.o.', 'RS');
execute upsert_bs('RSZZ', 'Legacy Serbia - HR', 'RS');
execute upsert_bs('SK01', 'Crh (Slovensko) a. s.', 'SK');
execute upsert_bs('SK02', 'ecorec Slovensko s.r.o.', 'SK');
execute upsert_bs('SK04', 'TransPlus (Slovensko)', 'SK');
execute upsert_bs('SK11', 'Crh Emerging Eur. Serv', 'SK');
execute upsert_bs('SK12', 'Crh Business Services', 'SK');
execute upsert_bs('SKZZ', 'Legacy Slovakia - HR', 'SK');

commit;

drop procedure upsert_bs;

prompt ************ End LOGON-7867 ***********************