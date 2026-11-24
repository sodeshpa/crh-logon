-- $Revision$
-- $Date$
--
--LOGON-4744, LOGON-6560

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
-- tlgns_sap_material
-------------------------------------------------------------------------------
create or replace
procedure upsert_bs (
  p_sap_material_id       in number,
  p_sap_material_no       in varchar2,
  p_sap_material_ifc_no   in varchar2,
  p_base_uom_cd           in varchar2,
  p_material_package_cd   in varchar2,
  p_marked_for_deletion   in number,
  p_material_is_agg       in number,
  p_material_is_cem       in number,
  p_material_is_lf        in number
)
is
  v_table_name    varchar2(48) := 'tlgns_sap_material';
  v_exists_flag   number;  -- number of existing records of that key (0 or 1)

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' (' || p_sap_material_id || ', ctl_act_cd=' || 1 || ')');

    select count(bs.sap_material_id)
    into v_exists_flag
    from tlgns_sap_material bs
    where bs.sap_material_id = p_sap_material_id;

    if (v_exists_flag > 0) then
      if (1 != 1) then
        dbms_output.put_line ('NOT updating ' || v_table_name || ' with CD=' || p_sap_material_id || ', because ctl_act_cd is 0 in loader and data in DB may be better maintained.');
      else
        update tlgns_sap_material set
/* sap_material_id: cannot update because this is the key */
          sap_material_no                = p_sap_material_no,
          sap_material_ifc_no            = p_sap_material_ifc_no,
          base_uom_cd                    = p_base_uom_cd,
          material_package_cd            = p_material_package_cd,
          marked_for_deletion            = p_marked_for_deletion,
          material_is_agg                = p_material_is_agg,
          material_is_cem                = p_material_is_cem,
          material_is_lf                 = p_material_is_lf,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_material_id = p_sap_material_id
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_material_id || ').');
      end if;
    else
      insert into tlgns_sap_material (
        sap_material_id,
        sap_material_no,
        sap_material_ifc_no,
        base_uom_cd,
        material_package_cd,
        marked_for_deletion,
        material_is_agg,
        material_is_cem,
        material_is_lf,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
      ) values (
        p_sap_material_id,
        p_sap_material_no,
        p_sap_material_ifc_no,
        p_base_uom_cd,
        p_material_package_cd,
        p_marked_for_deletion,
        p_material_is_agg,
        p_material_is_cem,
        p_material_is_lf,
        1, systimestamp, 1, systimestamp
      );
      dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_material_id || ').');
    end if;

  end upsert_bs;
/
show errors;

-------------------------------------------------------------------------------
-- tlgns_sap_material_description
-------------------------------------------------------------------------------
create or replace
procedure upsert_tx (
  p_sap_material_id in varchar2,
  p_lang_cd                 in varchar2,
  p_text                    in varchar2)
is
  v_table_name      varchar2(48) := 'tlgns_sap_material_description';
  v_exists_flag     number;        -- number of existing records of that key (0 or 1)
  v_exists_bs_flag  number;        -- number of existing BASE records

  begin
    dbms_output.enable(100000);
    dbms_output.put_line('Insert or Update ' || v_table_name || ' with (' || p_sap_material_id || ', ' || p_lang_cd || ', ''' || p_text || ''')');

----------------------------------------------------------------------------
-- Lookup BS entry first
----------------------------------------------------------------------------
    select count(bs.sap_material_id) into v_exists_bs_flag
    from tlgns_sap_material bs
    where bs.sap_material_id = p_sap_material_id;

    if (v_exists_bs_flag > 0) then
      select count(tx.sap_material_id) into v_exists_flag
      from tlgns_sap_material_description tx
      where tx.sap_material_id = p_sap_material_id and tx.lang_cd = p_lang_cd;

      if (v_exists_flag > 0) then
        update tlgns_sap_material_description set
/* p_sap_material_id and lang_cd: cannot update because these are the keys */
          txt                            = p_text,
          ctl_mod_uid                    = 1,
          ctl_mod_ts                     = systimestamp
        where
          sap_material_id = p_sap_material_id and
          lang_cd = p_lang_cd         and
          1 = is_different_varchar2(txt, p_text)
        ;
        dbms_output.put_line ('UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing ' || v_table_name || ' (CD=' || p_sap_material_id || ', LANG=' || p_lang_cd || ').');

      else
        insert into tlgns_sap_material_description (
          sap_material_id,
          lang_cd,
          txt,
          ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts
        ) values (
          p_sap_material_id,
          p_lang_cd,
          p_text,
          1, systimestamp, 1, systimestamp
        );
        dbms_output.put_line ('INSERTED ' || SQL%ROWCOUNT ||' ' || v_table_name || ' (CD=' || p_sap_material_id || ', LANG=' || p_lang_cd || ').');
      end if;
    else
      dbms_output.put_line ('ERROR: Missing entry in ' || v_table_name || ' (CD=' || p_sap_material_id || ').');
    end if;

  end upsert_tx;
/
show errors;

--adding landfill planning material (LOGON00001) to tlgns_sap_material table
prompt adding planning material (LOGON00001) to tlgns_sap_material table

execute upsert_bs(1,'LOGON00001','LOGON00001','TO','S006',0,0,0,1);

--adding aggregate planning material (LOGON00002) to tlgns_sap_material table
prompt adding planning material (LOGON00002) to tlgns_sap_material table
execute upsert_bs(2,'LOGON00002','LOGON00002','TO','S006',0,1,0,0);

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - EN
execute upsert_tx(1,'en','PLANNING MATERIAL LANDFILL');
execute upsert_tx(2,'en','PLANNING MATERIAL AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - DE
execute upsert_tx(1,'de','PLANMATERIAL LANDFILL');
execute upsert_tx(2,'de','PLANMATERIAL AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - FR
execute upsert_tx(1,'fr','PLANNING MATERIAL LANDFILL');
execute upsert_tx(2,'fr','PLANNING MATERIAL AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - IT
execute upsert_tx(1,'it','MATERIALE DI PIANIFICAZIONE LANDFILL');
execute upsert_tx(2,'it','MATERIALE DI PIANIFICAZIONE AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - ES
execute upsert_tx(1,'es','MATERIAL DE PLANIFICACIÓN LANDFILL');
execute upsert_tx(2,'es','MATERIAL DE PLANIFICACIÓN AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - CS
execute upsert_tx(1,'cs','PLÁNOVACÍ MATERIÁL LANDFILL');
execute upsert_tx(2,'cs','PLÁNOVACÍ MATERIÁL AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - SK
execute upsert_tx(1,'sk','PLÁNOVANÝ MATERIÁL LANDFILL');
execute upsert_tx(2,'sk','PLÁNOVANÝ MATERIÁL AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - HU
execute upsert_tx(1,'hu','TERVEZETT ANYAG LANDFILL');
execute upsert_tx(2,'hu','TERVEZETT ANYAG AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - RO
execute upsert_tx(1,'ro','PLANIFICARE MATERIAL LANDFILL');
execute upsert_tx(2,'ro','PLANIFICARE MATERIAL AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - BG
execute upsert_tx(1,'bg','ПЛАНИРАН МАТЕРИАЛ LANDFILL');
execute upsert_tx(2,'bg','ПЛАНИРАН МАТЕРИАЛ AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - RU
execute upsert_tx(1,'ru','Планируемый материал LANDFILL');
execute upsert_tx(2,'ru','Планируемый материал AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - PL
execute upsert_tx(1,'pl','PLANOWANIE MATERIAŁU LANDFILL');
execute upsert_tx(2,'pl','PLANOWANIE MATERIAŁU AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - AZ
execute upsert_tx(1,'az','MATERİALIN PLANLAMASI ZİBİLXANA');
execute upsert_tx(2,'az','MATERİALIN PLANLAMASI AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - HR
execute upsert_tx(1,'hr','PLANIRANI MATERIJAL LANDFILL');
execute upsert_tx(2,'hr','PLANIRANI MATERIJAL AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - SR
  execute upsert_tx(1,'sr','PLANIRANI MATERIJAL LANDFILL');
execute upsert_tx(2,'sr','PLANIRANI MATERIJAL AGG');

prompt INSERTING into TLGNS_SAP_MATERIAL_DESCRIPTION - TR
execute upsert_tx(1,'tr','PLANNING MATERIAL LANDFILL');
execute upsert_tx(2,'tr','PLANNING MATERIAL AGG');

commit;

drop procedure upsert_bs;
drop procedure upsert_tx;
drop function is_different_number;
drop function is_different_varchar2;
