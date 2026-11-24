--
--This is template file to adding new users to the databses.
--

spool /temp/logon_update_adnovum_user___FROM_VERSION___to___TO_VERSION__.log

set define off
set serveroutput on

prompt Time STARTED:
select systimestamp from dual;

create or replace procedure upsert_person_user_driver (
    /*--- Person ---*/
    /* v_person_id - only internal */
    v_person_name                  in varchar2,
    v_person_firstname             in varchar2,
    v_person_phone_nr              in varchar2,
    v_person_mobile_nr             in varchar2,
    v_person_email                 in varchar2,
    v_person_active_flag           in number,
    /*--- User ---*/
    /* v_user_id                         - ignored: only internal */
    /* v_user_person_id                  - ignored: only internal */
    v_user_user_type_cd            in number,
    v_user_sap_bp_id               in varchar2,
    v_user_sap_vendor_id           in varchar2,
    v_user_ldap_holspace           in varchar2,
    p_user_ext_login_active_flag   in number,
    v_user_ext_login               in varchar2,
    v_user_ext_login_pwd           in varchar2,
    /* v_user_ext_login_pwd_valid_to_dat - ignored: Always set to sysdate, i.e. immediately expired! User must reset via 'forgot password' functionality. Never update */
    v_user_pda_user_no             in number,
    /* v_user_pda_user_rfidobject_id     - ignored: always null, never update */
    v_user_pda_pin                 in varchar2,
    /* v_user_pda_pin_valid_to_dat       - ignored: Always set to sysdata + 90 in insert, never update */
    v_user_pickup_barcode_passcode in varchar2,
    /* v_user_photo_image_id             - ignored: always null, never update */
    /* v_user_signature_id               - ignored: Always null in insert, never update */
    /* v_user_signature_valid_flag       - ignored: Always 0 currently, never update */
    v_user_application_lang_cd     in varchar2,
    /*--- Driver ---*/
    /* v_driver_id                       - ignored: only internal */
    /* v_driver_user_id                  - ignored: only internal */
    /* v_driver_parallel_badge_id        - ignored: Alwyas null, never update. Will be dropped anyway */
    v_driver_current_truck_plateno in varchar2,
    p_driver_certified_from_dat    in varchar2,
    p_driver_certified_to_dat      in varchar2,
    p_driver_active_flag           in number,
    v_driver_driver_comment        in varchar2,
    /*--- Roles ---*/
    v_userrole_rolelist            in varchar2
) is
    /*-- For insert/update person/user/driver--*/
    v_exists_person                number; -- number of existing records of that key (0 or 1)
    v_existing_person_id           number := NULL ; -- ID of existing record of that key, if exist
    v_exists_user_by_ldap_holspace number; -- number of existing records for that ldap_holspace (0 or 1)
    v_exists_manually_created_user number; -- number of existing manually created users with that ext_login
    v_existing_user_id             number := NULL ; -- ID of existing record of that key, if exist
    v_exists_driver                number; -- number of existing records of that key (0 or 1)
    v_existing_driver_id           number := NULL ; -- ID of existing record of that key, if exist
    v_exists_user_role             number; -- number of existing records of that key (0 or 1)
    /*-- For lookups */
    v_exists_vendor                number; -- number of existing records of tlgns_sap_vendor referred by this user  (0 or 1)
    v_existing_vendor_id           number := NULL ; -- ID of existing record of that key, if exist (for lookup of vendor)
    v_exists_bp                    number; -- number of existing records of tlgns_sap_bp referred by this user (0 or 1)
    v_existing_bp_id               number := NULL ; -- ID of existing record of that key, if exist (for lookup of BP)
    v_exists_user_sap_vendor_acc   number; -- number of existing records of TLGNU_USER_SAP_VENDOR_ACC
	v_exists_user_soldto_bp_acc    number; -- number of existing records of TLGNU_USER_SOLDTO_BP_ACC
    v_exists_truck                 number; -- number of existing records of tlgnc_truck with the number plate referred by this driver (0 or 1)
    v_existing_truck_id            number := NULL ; -- ID of existing record of that key, if exist (for lookup of Truck Number Plate)
    /*-- Parameters/Variables --*/
    /*   May need to be changed if some conditions are not ok, such as missing BP/Vendor. Initial variable value := passed param value */
    v_user_ext_login_active_flag   number := p_user_ext_login_active_flag;
    v_driver_active_flag           number := p_driver_active_flag; /* must not do nvl(p_driver_active_flag, 0), because that's used to check if a user is a driver or not*/
    v_driver_certified_from_dat    date := NULL;
    v_driver_certified_to_dat      date := NULL;
    /*-- varia --*/
    sql_statement   varchar2(500);
    v_existing_rfidobject_id       number;
begin
  dbms_output.enable(100000);

  if (p_driver_certified_from_dat is not null and length(p_driver_certified_from_dat)>8) then
    v_driver_certified_from_dat := to_date(substr(p_driver_certified_from_dat, 1, 8),'YY-MM-DD');
  end if;
  if (p_driver_certified_to_dat is not null and length(p_driver_certified_from_dat)>8) then
    v_driver_certified_to_dat := to_date(substr(p_driver_certified_to_dat, 1, 8),'YY-MM-DD');
  end if;

  /**
   * if we create the user manually (probably from the UI),
   * we might have setup a different 'ldap_holspace' compared to the CSV
   *
   * in this case we cannot update the existing user found by 'ldap_holspace',
   * because of unique constraint violation
   */
  select count(user_id) into v_exists_manually_created_user
    from tlgnu_user u
    inner join tlgnc_person p on (u.person_id = p.person_id)
  where
    u.ext_login = v_user_ext_login and
    nvl(u.ldap_holspace,'count if ldap_holspace != v_user_ldap_holspace or exactly one of them is null')
    	!=
    nvl(v_user_ldap_holspace,'count if ldap_holspace != v_user_ldap_holspace or exactly one of them is null');

  if (v_exists_manually_created_user > 0) then
    dbms_output.put_line('  DEBUG: user "' || v_user_ext_login || '" was probably created from the UI manually.');
    return;
  end if;

  /* --- Identify record(s) to update ---*/

  -- A person is identified through its user. Use this hierarchy/heuristic to find the person/user to update again.
  -- 1 if holspace is set                    -> use v_user_ldap_holspace
  -- 2 LATER: if not, but v_user_ext_login is set   -> use v_user_ext_login
  -- 3 LATER: if not, but v_user_pda_user_no is set -> user v_user_pda_user_no
  -- 4 if all fails:
  --   - v_user_sap_vendor_id + v_person_name + v_person_firstname (For the current list, this is the only relevant, besides Crh)
  -- 5 as v_user_sap_vendor_id is empty in the current DB (created earlier, originally without vendor lookup)
  --   - Use just v_person_name+v_person_firstname, but only if unique! -> otherwise, ERROR because ambigeous.
  select count(user_id) into v_exists_user_by_ldap_holspace
    from tlgnu_user u
    inner join tlgnc_person p on (u.person_id = p.person_id)
  where
    u.ldap_holspace = v_user_ldap_holspace;

  if (v_exists_user_by_ldap_holspace > 0) then
    -- Select that Person, User, and maybe Driver ID
    select u.user_id, u.person_id, d.driver_id  into v_existing_user_id, v_existing_person_id, v_existing_driver_id
      from tlgnu_user u
      inner join tlgnc_person p on (u.person_id = p.person_id)
      left outer join tlgnc_driver d on (u.user_id = d.user_id)
    where
      u.ldap_holspace = v_user_ldap_holspace;

  else
    -- Search by Vendor + Name + firstname:
    select count(user_id) into v_exists_user_by_ldap_holspace
      from tlgnu_user u
      inner join tlgnc_person p on (u.person_id = p.person_id)
      inner join tlgns_sap_vendor v on (u.sap_vendor_id = v.sap_vendor_id)
    where
      p.name = v_person_name
      and p.firstname = v_person_firstname
      and v.sap_id = v_user_sap_vendor_id;

    if (v_exists_user_by_ldap_holspace = 1) then
      -- Select that Person, User, and maybe Driver ID
      select u.user_id, u.person_id, d.driver_id into v_existing_user_id, v_existing_person_id, v_existing_driver_id
        from tlgnu_user u
        inner join tlgnc_person p on (u.person_id = p.person_id)
        inner join tlgns_sap_vendor v on (u.sap_vendor_id = v.sap_vendor_id)
        left outer join tlgnc_driver d on (u.user_id = d.user_id)
      where
        p.name = v_person_name
        and p.firstname = v_person_firstname
        and v.sap_id = v_user_sap_vendor_id;
      dbms_output.put_line('  DEBUG: found unique by sap_vendor + name + firstname');

    elsif (v_exists_user_by_ldap_holspace > 1) then
        dbms_output.put_line('ERROR: no unique record found by sap_vendor + name + firstname, manually update user data.');
        return;

    else
      dbms_output.put_line('  DEBUG: not found by vendor + name + firstname either, trying just Name + Firstname');

      select count(user_id) into v_exists_user_by_ldap_holspace
        from tlgnu_user u
        inner join tlgnc_person p on (u.person_id = p.person_id)
      where
        p.name = v_person_name
        and p.firstname = v_person_firstname
        and u.ldap_holspace is null;

      if (v_exists_user_by_ldap_holspace = 1) then
      -- Select that Person, User, and maybe Driver ID
        select u.user_id, u.person_id, d.driver_id into v_existing_user_id, v_existing_person_id, v_existing_driver_id
          from tlgnu_user u
          inner join tlgnc_person p on (u.person_id = p.person_id)
          left outer join tlgnc_driver d on (u.user_id = d.user_id)
        where
          p.name = v_person_name
          and p.firstname = v_person_firstname
          and u.ldap_holspace is null;
          dbms_output.put_line('  DEBUG: found unique by Name + firstname');

      elsif (v_exists_user_by_ldap_holspace > 1) then
        dbms_output.put_line('ERROR: no unique record found, manually resolve the SAP vendor id or update other user data.');
        return;
      end if;
    end if;
  end if;

  -- here, three cases:
  -- a) already aborted (because duplicate risk)
  -- b) or we have at least person_id + user_id => update, maybe also update driver
  -- c) no id => insert

  dbms_output.put_line('  INFO: IDENTIFIED to update: person_id=' || v_existing_person_id || ', user_id=' || v_existing_user_id || ', driver_id=' || v_existing_driver_id || '.');

  /* --- TLGNC_PERSON --- */

  -- Update Person
  if (v_existing_person_id is not null) then
    update tlgnc_person set
      /* do not touch person_id, but DO touch name/first name (because they can be updated in case holspace is used as identifier) */
      name        = v_person_name,
      firstname   = v_person_firstname,
      phone_nr    = v_person_phone_nr,
      mobile_nr   = v_person_mobile_nr,
      email       = v_person_email,
      active_flag = v_person_active_flag,
      ctl_mod_uid = 1,
      ctl_mod_ts  = systimestamp,
      ctl_tcn     = ctl_tcn+1
    where
      person_id = v_existing_person_id and ( 1 != 1
        /* to identify if something has changed at all, null must be handled specially for nullable fields, because "null != 'something'" would be false! */
        or nvl(name,        '(NULL)') != nvl(v_person_name,        '(NULL)')
        or nvl(firstname,   '(NULL)') != nvl(v_person_firstname,   '(NULL)')
        or nvl(phone_nr,    '(NULL)') != nvl(v_person_phone_nr,    '(NULL)')
        or nvl(mobile_nr,   '(NULL)') != nvl(v_person_mobile_nr,   '(NULL)')
        or nvl(email,       '(NULL)') != nvl(v_person_email,       '(NULL)')
        or     active_flag            !=     v_person_active_flag
      );
    dbms_output.put_line ('  INFO: tlgnc_person id=' || v_existing_person_id ||': UPDATED if needed: ' || SQL%ROWCOUNT ||'.');

  else
  -- Insert Person
    insert into tlgnc_person (
      person_id,
      name,
      firstname,
      phone_nr,
      mobile_nr,
      email,
      active_flag,
      ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
    ) values (
      slgnc_person.nextval,
      v_person_name,
      v_person_firstname,
      v_person_phone_nr,
      v_person_mobile_nr,
      v_person_email,
      v_person_active_flag,
      1, systimestamp, 1, systimestamp, 0
    );
    dbms_output.put_line ('  INFO: tlgnc_person: INSERTED ' || SQL%ROWCOUNT ||' with new person_id=' || slgnc_person.currval ||'.');
  end if;

  /* --- TLGNU_USER ---*/

  -- Preparation: lookup by sap_vendor_id if passed
  if (v_user_sap_vendor_id is not null) then
    select count(sap_vendor_id) into v_exists_vendor from tlgns_sap_vendor v where v.sap_id = v_user_sap_vendor_id;
    if (v_exists_vendor > 0) then
      select v.sap_vendor_id into v_existing_vendor_id
      from tlgns_sap_vendor v where v.sap_id = v_user_sap_vendor_id;
    else
      dbms_output.put_line ('  ERROR: tlgnu_user: SAP Vendor ''' || v_user_sap_vendor_id ||''' not found! Use null but set user.ext_login_active_flag=0. User will not be able to log in before this is corrected.');
      v_user_ext_login_active_flag := 0;
    end if;
  end if;

  -- Preparation: lookup by sap_bp_id if passed
  if (v_user_sap_bp_id is not null) then
    select count(sap_bp_id) into v_exists_bp from tlgns_sap_bp b where b.sap_id = v_user_sap_bp_id;
    if (v_exists_bp > 0) then
      select b.sap_bp_id into v_existing_bp_id
      from tlgns_sap_bp b where b.sap_id = v_user_sap_bp_id;
    else
      dbms_output.put_line ('  ERROR: tlgnu_user: SAP BP ''' || v_user_sap_bp_id ||''' not found! Use null but set user.ext_login_active_flag=0. User will not be able to log in before this is corrected.');
      v_user_ext_login_active_flag := 0;
    end if;
  end if;

  -- Update User if exists
  if (v_existing_user_id is not null) then
    /* Update base fields */
    update tlgnu_user set
      /* do not touch user_id, person_id, but DO touch holspace etc
         They are either identical or are subject to be updated in case another field is used as an identifier in some cases.
         Also, "valid until" fields are not updated (passwords/pins/accounts must be renewed independent of data loads).*/
      user_type_cd          = v_user_user_type_cd,
      sap_bp_id             = v_existing_bp_id,
      sap_vendor_id         = v_existing_vendor_id,
      ldap_holspace         = v_user_ldap_holspace,
      ext_login_active_flag = v_user_ext_login_active_flag,
      ext_login             = v_user_ext_login,
      pda_user_no           = v_user_pda_user_no,
      application_lang_cd   = v_user_application_lang_cd,
      ctl_mod_uid           = 1,
      ctl_mod_ts            = systimestamp,
      ctl_tcn               = ctl_tcn+1
    where
      user_id = v_existing_user_id and ( (1 != 1)
        /* to identify if something has changed at all, null must be handled specially, because "null != 'something'" would be false! */
        or user_type_cd                            != v_user_user_type_cd
        or nvl(sap_bp_id,                      -1) != nvl(v_existing_bp_id,             -1)
        or nvl(sap_vendor_id,                  -1) != nvl(v_existing_vendor_id,         -1)
        or nvl(ldap_holspace,            '(NULL)') != nvl(v_user_ldap_holspace,         '(NULL)')
        or nvl(ext_login_active_flag,          -1) != nvl(v_user_ext_login_active_flag, -1)
        or nvl(ext_login,                '(NULL)') != nvl(v_user_ext_login,             '(NULL)')
        or nvl(pda_user_no,                    -1) != nvl(v_user_pda_user_no,           -1)
        or     application_lang_cd                 !=     v_user_application_lang_cd
      );
    dbms_output.put_line ('  INFO: tlgnu_user   id=' || v_existing_user_id || ': UPDATED if needed: ' || SQL%ROWCOUNT ||'.');

    /* Update passwords only from "not set" to "set" */
    update tlgnu_user set
      ext_login_pwd         = v_user_ext_login_pwd,
      ctl_mod_uid           = 1,
      ctl_mod_ts            = systimestamp,
      ctl_tcn               = ctl_tcn+1
    where
      user_id = v_existing_user_id
      and ext_login_pwd is null
      and v_user_ext_login_pwd is not null;
    dbms_output.put_line ('  INFO: tlgnu_user.ext_logon_pwd for user_id=' || v_existing_user_id || ': UPDATED if needed: ' || SQL%ROWCOUNT ||'.');

    /* Update PIN only from "not set" to "set", no update if one exists already (set by user?) */
    update tlgnu_user set
      pda_pin               = v_user_pda_pin,
      ctl_mod_uid           = 1,
      ctl_mod_ts            = systimestamp,
      ctl_tcn               = ctl_tcn+1
    where
      user_id = v_existing_user_id
      and pda_pin is null
      and v_user_pda_pin is not null;
    dbms_output.put_line ('  INFO: tlgnu_user.pda_pin       for user_id=' || v_existing_user_id || ': UPDATED if needed: ' || SQL%ROWCOUNT ||'.');

    /* Update passcode only from "not set" to "set", no update if one exists already (set by user?) */
    update tlgnu_user set
      pickup_barcode_passcode = v_user_pickup_barcode_passcode,
      ctl_mod_uid             = 1,
      ctl_mod_ts              = systimestamp,
      ctl_tcn                 = ctl_tcn+1
    where
      user_id = v_existing_user_id
      and pickup_barcode_passcode is null
      and v_user_pickup_barcode_passcode is not null;
    dbms_output.put_line ('  INFO: tlgnu_user.pickup_barcode_passcode       for user_id=' || v_existing_user_id || ': UPDATED if needed: ' || SQL%ROWCOUNT ||'.');

  else
  -- Insert User

    insert into tlgnu_user (
      user_id,
      person_id,
      user_type_cd,
      sap_bp_id,
      sap_vendor_id,
      ldap_holspace,
      ext_login_active_flag,
      ext_login,
      ext_login_pwd,
      ext_login_pwd_valid_to_dat,
      pda_user_no,
      pda_user_rfidobject_id,
      pda_pin,
      pda_pin_valid_to_dat,
      pickup_barcode_passcode,
      photo_image_id,
      signature_id,
      signature_valid_flag,
      application_lang_cd,
      send_daily_reports_flag,
      ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
    ) values (
      slgnu_user.nextval,
      slgnc_person.currval,
      v_user_user_type_cd,
      v_existing_bp_id,
      v_existing_vendor_id /* Determined by lookup */,
      v_user_ldap_holspace,
      v_user_ext_login_active_flag,
      v_user_ext_login,
      v_user_ext_login_pwd,
      sysdate              /* Since LOGON 2.1: No longer adding 90 days, but set to 'expired' immediately. Use 'forgot password'. v_user_ext_login_pwd_valid_to_dat ignored anyway */,
      v_user_pda_user_no,
      null                 /* v_pda_user_rfidobject_id ignored */,
      v_user_pda_pin       /* TODO: what initial pin? */,
      null                 /* v_user_pda_pin_valid_to_dat ignored, always null, decision by Beat during real-cutover in Madrid */,
      v_user_pickup_barcode_passcode,
      null                 /* v_user_photo_image_id ignored*/,
      null                 /* v_user_signature_id ignored */,
      0                    /* v_user_signature_valid_flag ignored*/,
      v_user_application_lang_cd,
      0					   /* default 0 from 1.5 patch */,
      1, systimestamp, 1, systimestamp, 0
    );
    dbms_output.put_line ('  INFO: tlgnc_user:   INSERTED ' || SQL%ROWCOUNT ||' with new user_id=' || slgnu_user.currval ||', referring to person_id=' || slgnc_person.currval || '.');

    /* user id is required in insert driver! */
    select slgnu_user.currval into v_existing_user_id from dual;
  end if;

  /* -- If vendor is not null: make sure that a TLGNU_USER_SAP_VENDOR_ACC record exists -- */
  if (v_existing_vendor_id is not null) then
    select count(user_sap_vendor_acc_id) into v_exists_user_sap_vendor_acc
      from tlgnu_user_sap_vendor_acc acc
    where
      acc.user_id = v_existing_user_id;
    if (v_exists_user_sap_vendor_acc = 0) then
      insert into tlgnu_user_sap_vendor_acc (
        user_sap_vendor_acc_id,
        user_id,
        sap_vendor_id,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
      ) values (
        slgnu_user_sap_vendor_acc.nextval,
        v_existing_user_id,
        v_existing_vendor_id,
        1, systimestamp, 1, systimestamp, 0
      );
      dbms_output.put_line ('  INFO: tlgnu_user_sap_vendor_acc: INSERTED ' || SQL%ROWCOUNT ||' with id=' || slgnu_user_sap_vendor_acc.currval ||', referring to user_id=' || v_existing_user_id || '.');
    end if;
  end if;

    /* -- If vendor is not null: make sure that a TLGNU_USER_SOLDTO_BP_ACC record exists -- */
  if (v_existing_bp_id is not null) then
    select count(user_soldto_bp_acc_id) into v_exists_user_soldto_bp_acc
      from tlgnu_user_soldto_bp_acc acc
    where
      acc.user_id = v_existing_user_id;
    if (v_exists_user_soldto_bp_acc = 0) then
      insert into tlgnu_user_soldto_bp_acc (
        user_soldto_bp_acc_id,
        user_id,
        soldto_sap_bp_id,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
      ) values (
        slgnu_user_soldto_bp_acc.nextval,
        v_existing_user_id,
        v_existing_bp_id,
        1, systimestamp, 1, systimestamp, 0
      );
      dbms_output.put_line ('  INFO: tlgnu_user_soldto_bp_acc: INSERTED ' || SQL%ROWCOUNT ||' with id=' || slgnu_user_soldto_bp_acc.currval ||', referring to user_id=' || v_existing_user_id || '.');
    end if;
  end if;

  -- Preparation: lookup TRUCK_ID by truck_plateno if passed
  if (v_driver_current_truck_plateno is not null) then
    select count(truck_id) into v_exists_truck from tlgnc_truck t where t.truck_plate_no = v_driver_current_truck_plateno;
    if (v_exists_truck > 0) then
      select t.truck_id into v_existing_truck_id
      from tlgnc_truck t where t.truck_plate_no = v_driver_current_truck_plateno;
    else
      dbms_output.put_line ('  ERROR: tlgnu_user: TRUCK ''' || v_driver_current_truck_plateno ||''' not found! Will use null instead = no current truck.');
    end if;
  end if;

  -- Update Driver
  if (v_existing_driver_id is not null) then
    v_driver_active_flag := nvl(v_driver_active_flag, 0); -- active_flag on DB is not-nullable
    update tlgnc_driver set
      /* do not touch driver_id, user_id*/
      current_truck_id   = v_existing_truck_id,
      certified_from_dat = v_driver_certified_from_dat,
      certified_to_dat   = v_driver_certified_to_dat,
      active_flag        = v_driver_active_flag,
      driver_comment     = v_driver_driver_comment,
      ctl_mod_uid        = 1,
      ctl_mod_ts         = systimestamp,
      ctl_tcn            = ctl_tcn+1
    where
      driver_id = v_existing_driver_id and ( 1 != 1
        /* to identify if something has changed at all, null must be handled specially, because "null != 'something'" would be false! */
        or current_truck_id   != v_existing_truck_id
          or (current_truck_id   is null     and v_existing_truck_id         is not null)
          or (current_truck_id   is not null and v_existing_truck_id         is null)
        or certified_from_dat != v_driver_certified_from_dat
          or (certified_from_dat is null     and v_driver_certified_from_dat is not null)
          or (certified_from_dat is not null and v_driver_certified_from_dat is null)
        or certified_to_dat   != v_driver_certified_to_dat
          or (certified_to_dat   is null     and v_driver_certified_to_dat   is not null)
          or (certified_to_dat   is not null and v_driver_certified_to_dat   is null)
        or     active_flag                   !=     v_driver_active_flag
        or nvl(driver_comment,     '(NULL)') != nvl(v_driver_driver_comment,     '(NULL)')
      );
    dbms_output.put_line ('  INFO: tlgnc_driver UPDATED if needed: ' || SQL%ROWCOUNT ||'. Existing tlgnc_driver with id=' || v_existing_driver_id ||'.');

  elsif (v_driver_active_flag is not null) then
  -- Insert Driver
    insert into tlgnc_driver (
      driver_id,
      user_id,
      current_truck_id,
      certified_from_dat,
      certified_to_dat,
      active_flag,
      driver_comment,
      ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
    ) values (
      slgnc_driver.nextval,
      v_existing_user_id,
      v_existing_truck_id,
      v_driver_certified_from_dat,
      v_driver_certified_to_dat,
      v_driver_active_flag,
      v_driver_driver_comment,
      1, systimestamp, 1, systimestamp, 0
    );
    dbms_output.put_line ('  INFO: tlgnc_driver: INSERTED ' || SQL%ROWCOUNT ||' with new driver_id=' || slgnc_driver.currval ||', referring to user_id=' || v_existing_user_id || '.');

    if(v_user_pda_user_no is not null) then
      -- Insert Rfidbject
      insert into tlgnc_rfidobject (
        rfidobject_id,
        rfidobject_type_cd,
        object_label,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
      ) values (
        slgnc_rfidobject.nextval,
        1,
        v_user_pda_user_no,
        1, systimestamp, 1, systimestamp, 0
      );
      dbms_output.put_line ('  INFO: tlgnc_rfidobject: INSERTED ' || SQL%ROWCOUNT ||' with new rfidobject_id=' || slgnc_rfidobject.currval ||', referring to object_lable=' || v_user_pda_user_no || '.');

      select slgnc_rfidobject.currval into v_existing_rfidobject_id from dual;

      update tlgnu_user set pda_user_rfidobject_id = v_existing_rfidobject_id where user_id = v_existing_user_id;

      dbms_output.put_line ('  INFO: tlgnc_user: UPDATED ' || v_existing_user_id || ' with new pda_user_rfidobject_id=' || v_existing_rfidobject_id || '.');
    end if;
  end if;

  -- Insert initial roles (no updates) if needed
  if v_userrole_rolelist != '''' then

    select count(user_role_id) into v_exists_user_role
    from tlgnu_user_role ur
    where ur.user_id = v_existing_user_id;

    if (v_exists_user_role = 0) then
      /* hack with execute immediate, because we want user_roles to be something like 1,2,3 to get 'where role_id in (1,2,3)' */
      sql_statement := '
      insert into tlgnu_user_role (
        user_role_id,
        user_id,
        role_id,
        ctl_cre_uid, ctl_cre_ts, ctl_mod_uid, ctl_mod_ts, ctl_tcn
      ) (select
          slgnu_user_role.nextval,
          slgnu_user.currval,
          role_id,
          1, systimestamp, 1, systimestamp, 0
        from tlgnu_role
        where role_id in ('|| v_userrole_rolelist ||') )';
      /*dbms_output.put_line ('  DEBUG sql_statement for user_roles: ' || sql_statement);*/
      execute immediate sql_statement;
      dbms_output.put_line ('  INFO: tlgnu_user_role: INSERTED ' || SQL%ROWCOUNT ||', with last id=' || slgnu_user_role.currval || ' for roles=' || v_userrole_rolelist || '.');

    else
      dbms_output.put_line ('  INFO: User Roles already exist, no updating. Do manually if needed in the UI');
    end if;

  end if;

end upsert_person_user_driver;
/
show errors;
--
-- New user upserts should be inserted below. ˇˇˇ
--
--
--Example for a user:
--
--prompt Inserting or Updating Person/User/Driver/Roles for 'Ginter', 'Gabor', LDAP/Ext/PDA='gabor@EA.CRH.NET', 'gabor',  'null', BP/Vendor=''/'', Driver: null
--execute upsert_person_user_driver(/*person: */ 'Ginter', 'Gabor', '', '', 'gabor@adnovum.hu', 1, /*user: */ 0, '', '', 'gabor@EA.CRH.NET', 1, 'gabor', 'mi6?E87H', null, '', '', 'en', /*driver: */ '', '', '', null, '', '1,2,3,4,5,6,7,8,9,10,12,13,16,18,20,21,22,23,24,25,26');

--
-- New user upserts should be inserted above. ^^^
--
commit;

-- in dev/test, when manually adding additional records, simply recreate the procedure again
drop procedure upsert_person_user_driver;

prompt Time FINISHED:
select systimestamp from dual;

spool off
