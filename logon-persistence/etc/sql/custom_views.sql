-- - Called during schema creation for a new OR patched DB, as OWNER (ulgn01)
-- - Can be re-run as many times as desired


/**
 * START: Backward compatibility views
 */

PROMPT  VLGNC_WEBBEST_SOLDSHIP_SAREA

CREATE OR REPLACE FORCE VIEW VLGNC_WEBBEST_SOLDSHIP_SAREA AS
SELECT
  shipto.SAP_ID || '_' || pf_soldto_shipto.SAREA_ID || '_' || shipto.SAP_BP_ID || '_' || soldto.SAP_BP_ID AS COMPOSITE_ID,
  shipto.NAME1 AS SHIPTO_NAME,
  shipto.SAP_ID AS SHIPTO_SAP_ID,
  shipto.SAP_BP_ID AS SHIPTO_SAP_BP_ID,
  soldto.SAP_ID AS SOLDTO_SAP_ID,
  soldto.SAP_BP_ID AS SOLDTO_SAP_BP_ID, -- included for authorization
  pf_soldto_shipto.SAREA_ID,
  sarea.SAREA_DISPLAY_LONGNAME,
  shipto_sarea.SAREA_ID SHIPTO_SAREA_ID,
  shipto_sarea.SGROUP_CD SHIPTO_SGROUP_CD, -- included for authorization
  sorg.companycode_cd -- included for authorization
FROM
  TLGNS_SAP_BP shipto,
  TLGNS_SAP_BP soldto,
  TLGNS_SAP_BP_SAREA shipto_sarea,
  TLGNS_SAP_BP_SAREA soldto_sarea,
  TLGNS_SAP_BP_PF_SHIPTO pf_soldto_shipto,
  TLGNC_SAREA sarea join TLGNR_SORG_BS sorg on (sorg.sorg_cd = sarea.sorg_cd)
WHERE
  shipto.bp_is_shipto = 1 /* should not be required, see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
  AND soldto.bp_is_soldto = 1 /* needed! see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
  AND shipto_sarea.SAP_BP_ID = shipto.SAP_BP_ID
  AND soldto_sarea.SAP_BP_ID = soldto.SAP_BP_ID
  AND shipto_sarea.MARKED_FOR_DELETION = '0'
  AND pf_soldto_shipto.SAP_BP_SAREA_ID = soldto_sarea.SAP_BP_SAREA_ID
  AND pf_soldto_shipto.SHIPTO_SAP_BP_SAREA_ID = shipto_sarea.SAP_BP_SAREA_ID
  AND pf_soldto_shipto.SAREA_ID = sarea.SAREA_ID;

COMMENT ON TABLE VLGNC_WEBBEST_SOLDSHIP_SAREA is 'Used by webbestellung. Deprecated, not used view. Had been replaced by VLGNC_WEBORDER_SHIP_TO_SOLD_TO. Kept for backward compatibility.';

REM END VLGNC_WEBBEST_SOLDSHIP_SAREA

-------------------------------------------------------------------------------

/**
 *   END: Backward compatibility views
 */

-------------------------------------------------------------------------------

PROMPT VLGNC_CARRRIER_VENDOR

CREATE OR REPLACE FORCE VIEW VLGNC_CARRIER_VENDOR as
SELECT
  c.display_name,
  c.carrier_id,
  v.SAP_VENDOR_ID,
  v.SAP_ID,
  v.MARKED_FOR_DELETION,
  v.NAME1,
  v.NAME2,
  v.LANG_CD
FROM tlgnc_carrier c
inner join  tlgns_sap_vendor v on v.sap_vendor_id=c.sap_vendor_id WHERE c.active_flag=1;

COMMENT ON TABLE VLGNC_CARRIER_VENDOR is 'Carrier and the corresponding vendor view';

REM END VLGNC_CARRIER_VENDOR

PROMPT VLGNC_RFIDOBJECT_SILO_ATTACH

CREATE OR REPLACE FORCE VIEW VLGNC_RFIDOBJECT_SILO_ATTACH as
SELECT
  rf.rfidobject_id,
  rf.rfidobject_type_cd,
  rf.object_label,
  s.shipto_silo_id,
  s.shipto_id,
  s.active_flag,
  s.portable_silo_flag
FROM TLGNC_RFIDOBJECT rf
LEFT OUTER JOIN TLGNC_SHIPTO_SILO s ON s.RFIDOBJECT_ID = rf.RFIDOBJECT_ID
WHERE rf.RFIDOBJECT_TYPE_CD = 8;

COMMENT ON TABLE VLGNC_RFIDOBJECT_SILO_ATTACH is 'RFID objects attached to silos.';

REM END VLGNC_RFIDOBJECT_SILO_ATTACH

PROMPT VLGNC_SHIPTO_SAP_BP

CREATE OR REPLACE FORCE VIEW VLGNC_SHIPTO_SAP_BP as
SELECT
   shipto.SHIPTO_ID,
   shipto.shipto_sap_bp_id,
   bp.SAP_ID,
   bp.NAME1,
   bp.POSTAL_CODE,
   bp.CITY,
   bp.REGION,
   bp.COUNTRY_CD,
   bp.STREET,
   bp.HOUSE_NUMBER
from TLGNC_SHIPTO shipto
inner join TLGNS_SAP_BP bp on (shipto.SHIPTO_SAP_BP_ID = bp.SAP_BP_ID and shipto.ACTIVE_FLAG = 1);

COMMENT ON TABLE VLGNC_SHIPTO_SAP_BP is 'Shipto and sap_bp joined. Used for querying shiptos.';

REM END VLGNC_SHIPTO_SAP_BP

PROMPT VGDIA_TEST_TYPES

CREATE OR REPLACE FORCE VIEW VGDIA_TEST_TYPES
as
select
  TEST_TYPES_ID,
  NUMBER_12_0_LONG_NOT_NULL,
  NUMBER_12_0_LONG_NULLABLE,
  NUMBER_8_0_INT_NOT_NULL,
  NUMBER_8_0_INT_NULLABLE,
  NUMBER_4_0_INTEGER_NOT_NULL,
  NUMBER_4_0_INTEGER_NULLABLE,
  VARCHAR2_35_STRING_NOT_NULL,
  VARCHAR2_35_STRING_NULLABLE,
  DATE_JAVA_UTIL_DATE_NOT_NULL,
  DATE_JAVA_UTIL_DATE_NULLABLE,
  TSTMP_JAVA_SQL_TSTAMP_NOT_NULL,
  TSTMP_JAVA_SQL_TSTAMP_NULLABLE,
  NUMBER_8_3_BIGDECIMAL_NOT_NULL,
  NUMBER_8_3_BIGDECIMAL_NULLABLE,
  NUMBER_6_2_CURRENCY_NOT_NULL,
  NUMBER_6_2_CURRENCY_NULLABLE,
  NUMBER_DOUBLE_NOT_NULL,
  NUMBER_DOUBLE_NULLABLE,
  CTL_CRE_UID,
  CTL_CRE_TS,
  CTL_MOD_UID,
  CTL_MOD_TS,
  CTL_TCN
from
  TGDIA_TEST_TYPES
;

  COMMENT ON TABLE VGDIA_TEST_TYPES is 'One-to-one view of TGDIA_TEST_TYPES, used for testing view mapping decoding from the database.';

REM END VGDIA_TEST_TYPES

-------------------------------------------------------------------------------

PROMPT  VLGNU_PLANT_SHIPPINGPOINT

CREATE OR REPLACE FORCE VIEW VLGNU_PLANT_SHIPPINGPOINT AS
  SELECT
  p.plant_CD || '_' || sp.shippingpoint_ID as COMPOSITE_ID,
  p.PLANT_CD as PLANT_CD,
  p.PLANT_NAME as PLANT_NAME,
  sp.SHIPPINGPOINT_ID SHIPPINGPOINT_ID,
  sp.SHIPPINGPOINT_NAME as SHIPPINGPOINT_NAME,
  sp.SAP_SHIPPINGPOINT_NO as SAP_SHIPPINGPOINT_NO
FROM
  TLGNC_SHIPPINGPOINT sp
  inner join TLGNR_PLANT_BS p on(sp.plant_CD = p.plant_CD);

COMMENT ON TABLE VLGNU_PLANT_SHIPPINGPOINT is 'View for Assigned Shipping Point list in Admin / User Admin / User Admin (internal/external) and Admin / User profile.';

REM END VLGNU_PLANT_SHIPPINGPOINT

-------------------------------------------------------------------------------

PROMPT  VLGNU_USER_ADMIN

CREATE OR REPLACE FORCE VIEW VLGNU_USER_ADMIN AS
  SELECT
    usertab.user_id as USER_ID,
    usertab.sap_bp_id as SAP_BP_ID,
    sap_bp.name1 as CUSTOMER,
    usertab.sap_vendor_id as SAP_VENDOR_ID,
    carrier.display_name as CARRIER,
    persontab.person_id as PERSON_ID,
    persontab.firstname as FIRSTNAME,
    persontab.name as NAME,
    persontab.name || persontab.firstname || persontab.name as COMPOSITE_NAME,
    usertab.ldap_holspace as LDAP_HOLSPACE,
    usertab.ext_login as EXT_LOGIN,
    usertab.pda_user_no as PDA_USER_NO,
    persontab.active_Flag as ACTIVE_FLAG,
    getRolesString(usertab.user_id) as ROLE_NAMES,
    drivertab.certified_from_dat as CERTIFIED_FROM_DAT,
    drivertab.certified_to_dat as CERTIFIED_TO_DAT
  FROM
    TLGNU_USER usertab
    JOIN TLGNC_PERSON persontab ON (usertab.PERSON_ID = persontab.PERSON_ID)
    LEFT OUTER JOIN TLGNC_DRIVER drivertab ON (usertab.USER_ID = drivertab.USER_ID)
    LEFT OUTER JOIN TLGNS_SAP_BP sap_bp ON (usertab.SAP_BP_ID = sap_bp.SAP_BP_ID)
    LEFT OUTER JOIN TLGNC_CARRIER carrier ON (usertab.SAP_VENDOR_ID = carrier.SAP_VENDOR_ID);

COMMENT ON TABLE VLGNU_USER_ADMIN is 'View for user search in Admin / User Admin / User Admin (internal/external).';

REM END VLGNU_USER_ADMIN

-------------------------------------------------------------------------------

PROMPT  VLGNU_USER_ROLE_SELECTION

  CREATE OR REPLACE FORCE VIEW VLGNU_USER_ROLE_SELECTION AS
  select
    c.user_id || '_' || c.role_id as COMPOSITE_ID,
    c.user_id as USER_ID,
    c.role_id as ROLE_ID,
    c.role_name as ROLE_NAME,
  c.role_descr as ROLE_DESCR,
    ur.user_role_id as USER_ROLE_ID
    from
      (select u.user_id, r.role_id, r.role_name, r.role_descr from tlgnu_user u, tlgnu_role r) c
      left outer join tlgnu_user_role ur on (ur.role_id = c.role_id and ur.user_id = c.user_id)
    order by
  c.user_id;

COMMENT ON TABLE VLGNU_USER_ROLE_SELECTION is 'TODO author of this view: describe purpose and usage of view';

REM END VLGNU_USER_ROLE_SELECTION

-------------------------------------------------------------------------------

prompt vlgnc_common_sarea_map

create or replace force view vlgnc_common_sarea_map as
select
  ref_sarea.sarea_id || '_' || sarea.sarea_id as composite_id,
  ref_sarea.sarea_id ref_sarea_id,
  sarea.sarea_id sarea_id
from tlgnc_sarea ref_sarea
inner join tlgnr_common_sarea_map_bs map on (
  ref_sarea.sorg_cd = map.ref_sorg_cd
  and ref_sarea.division_cd = map.ref_division_cd
  and ref_sarea.distribution_channel = map.ref_distribution_channel
)
inner join tlgnc_sarea sarea on (
  sarea.sorg_cd = map.sorg_cd
  and sarea.division_cd = map.division_cd
  and sarea.distribution_channel = map.distribution_channel
  and map.ctl_act_cd = 1
);

comment on table vlgnc_common_sarea_map is 'Auxiliary view which provides better access to tlgnr_sarea_dependency (without defaults)';

-------------------------------------------------------------------------------

prompt vlgns_soldship_flex_srch

-- About the BP flags bp_is_shipto/bp_is_soldto:
--
-- - bp_is_shipto = 1 may not be needed (as the BP_PF_SHIPTO is included in the join),
--   but as long as it is not really bad for performance: keep it, helps readability.
--
-- - bp_is_soldto =1 IS however needed!! Reason: We do not explicitely store soldto-PF,
--   The assumption, that sap_bp_pf_shipto always goes from soldto -> shipto is wrong:
--   Reason: The "self" entries for shipto (A-shipto->A) are also stored there!
--   To only get the real soldo-shipto relations (and not all shipto-shipto as well),
--   the check for this flag bp_is_soldto is important!
CREATE OR REPLACE FORCE VIEW VLGNS_SOLDSHIP_FLEX_SRCH AS
  SELECT DISTINCT
     /* technical IDs */
     soldto.SAP_BP_ID || '_' || shipto.SAP_BP_ID  AS COMPOSITE_ID,
     soldto.SAP_BP_ID AS SOLDTO_SAP_BP_ID,
     shipto.SAP_BP_ID AS SHIPTO_SAP_BP_ID,
     /* fields as contained in searchable string as well */
     soldto.SAP_ID    AS SOLDTO_NR,
     soldto.NAME1     AS SOLDTO_NAME,
     shipto.SAP_ID    AS SHIPTO_NR,
     shipto.NAME1     AS SHIPTO_NAME,
     shipto.POSTAL_CODE,
     shipto.CITY,
     shipto.STREET || ' ' || shipto.HOUSE_NUMBER as shipto_address,
     /* additional fields for UI */
   shipto.MARKED_FOR_DELETION AS SHIPTO_MARKED_FOR_DELETION,
   soldto.MARKED_FOR_DELETION AS SOLDTO_MARKED_FOR_DELETION,
     /* searchable string - order matters!*/
     lower(soldto.SAP_ID || soldto.name1 || shipto.sap_id || shipto.name1 || shipto.POSTAL_CODE || shipto.CITY  || shipto.STREET || shipto.HOUSE_NUMBER) AS COMPOSITE_SEARCH_STRING,
	 shipto.CEM_VALID_UNTIL_DAT,
	 shipto.AGG_VALID_UNTIL_DAT
  FROM         TLGNS_SAP_BP           soldto
    inner join TLGNS_SAP_BP_SAREA     soldto_sarea     on (soldto.sap_bp_id = soldto_sarea.sap_bp_id)
    inner join TLGNS_SAP_BP_PF_SHIPTO pf_soldto_shipto on (soldto_sarea.sap_bp_sarea_id = pf_soldto_shipto.sap_bp_sarea_id)
    inner join TLGNS_SAP_BP_SAREA     shipto_sarea     on (shipto_sarea.sap_bp_sarea_id = pf_soldto_shipto.shipto_sap_bp_sarea_id)
    inner join TLGNS_SAP_BP           shipto           on (shipto_sarea.sap_bp_id = shipto.sap_bp_id)
  where /* without those, we have 10394, with those 9713. Reason? See comment at beginning of view */
    shipto.bp_is_shipto = 1       /* is redundant to bp_pf_shipto, but should not hurt. bp_is_soldto=1 however is needed, see above! */
    AND soldto.bp_is_soldto = 1;

-- Example queries:
-- select * from VLGNS_SOLDSHIP_FLEX_SRCH where SHIPTO_NAME like '%stadion%';
-- select * from VLGNS_SOLDSHIP_FLEX_SRCH where COMPOSITE_SEARCH_STRING like '%stadion%8050%';
-- select * from VLGNS_SOLDSHIP_FLEX_SRCH where COMPOSITE_SEARCH_STRING like lower('%m%er%8003%');

COMMENT ON TABLE VLGNS_SOLDSHIP_FLEX_SRCH is 'View to search for Soldto/Shipto in Dispokarte in a flexible way: "stadion 8050" will search both name AND postal_code at the same time, etc.';

-------------------------------------------------------------------------------

PROMPT  VLGNS_SOLDSHIP_SAREA_FLEX_SRCH

CREATE OR REPLACE FORCE VIEW VLGNS_SOLDSHIP_SAREA_FLEX_SRCH AS
  SELECT DISTINCT
         shipto.SAP_ID || '_' || pf_soldto_shipto.SAREA_ID || '_' || shipto.SAP_BP_ID || '_' || soldto.SAP_BP_ID || '_' || dailystatus.LANG_CD AS COMPOSITE_ID,
         shipto.NAME1 AS SHIPTO_NAME,
         shipto.SAP_ID AS SHIPTO_NR,
         shipto.SAP_BP_ID AS SHIPTO_SAP_BP_ID,
    shipto.RAILWAY_STATION_ID AS SHIPTO_RAILWAY_STATION_ID,
         shipto.POSTAL_CODE,
         shipto.CITY,
      shipto.MARKED_FOR_DELETION AS SHIPTO_MARKED_FOR_DELETION,
         logonshipto.DAILY_ORDER_CONTACT_FLAG,
         logonshipto.DAILY_ORDER_LAST_CONTACT_DAT,
         decode(logonshipto.DAILY_ORDER_STATUS_CD, NULL, 1, decode(SIGN(trunc(SYSDATE) - trunc(logonshipto.DAILY_ORDER_LAST_CONTACT_DAT)), 1, 1, logonshipto.DAILY_ORDER_STATUS_CD)) as DAILY_ORDER_STATUS_CD,
         dailystatus.LANG_CD as DAILY_ORDER_STATUS_LANG_CD,
         dailystatus.TXT as DAILY_ORDER_STATUS_TXT,
         soldto.NAME1 AS SOLDTO_NAME,
         soldto.SAP_ID AS SOLDTO_NR,
         soldto.SAP_BP_ID AS SOLDTO_SAP_BP_ID,
     soldto.MARKED_FOR_DELETION AS SOLDTO_MARKED_FOR_DELETION,
         pf_soldto_shipto.SAREA_ID,
         sarea.SORG_CD,
     sarea.SAREA_DISPLAY_LONGNAME,
     sarea.RAIL_RETURN_SAREA_FLAG,
     logonshipto.SHIPTO_TYPE_CD,
     shipto_sarea.SAREA_ID SHIPTO_SAREA_ID,
     shipto_sarea.SGROUP_CD SHIPTO_SGROUP_CD,
     shipto.STREET || ' ' || shipto.HOUSE_NUMBER as shipto_address,
     /* searchable string - order matters!*/
    lower(soldto.SAP_ID || soldto.name1 || shipto.sap_id || shipto.name1 || shipto.POSTAL_CODE || shipto.CITY || shipto.STREET || shipto.HOUSE_NUMBER) AS COMPOSITE_SEARCH_STRING
  FROM
    TLGNS_SAP_BP shipto
      left outer join TLGNC_SHIPTO logonshipto on (logonshipto.shipto_sap_bp_id = shipto.sap_bp_id)
      left outer join TLGNR_DAILY_ORDER_STATUS_TX dailystatus on (dailystatus.DAILY_ORDER_STATUS_CD = decode(logonshipto.DAILY_ORDER_STATUS_CD, NULL, 1, decode(SIGN(trunc(SYSDATE) - trunc(logonshipto.DAILY_ORDER_LAST_CONTACT_DAT)), 1, 1, logonshipto.DAILY_ORDER_STATUS_CD))),
    TLGNS_SAP_BP soldto,
    TLGNS_SAP_BP_SAREA shipto_sarea,
    TLGNS_SAP_BP_SAREA soldto_sarea,
    TLGNS_SAP_BP_PF_SHIPTO pf_soldto_shipto,
    TLGNC_SAREA sarea
  WHERE
    shipto.bp_is_shipto = 1 /* should not be required, see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
    AND soldto.bp_is_soldto = 1 /* needed! see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
    AND shipto_sarea.SAP_BP_ID = shipto.SAP_BP_ID
    AND soldto_sarea.SAP_BP_ID = soldto.SAP_BP_ID
    AND shipto_sarea.MARKED_FOR_DELETION = '0'
    AND soldto_sarea.MARKED_FOR_DELETION = '0' /* LOGON-6477 (Avoid loading inactive/invalid sales areas) */
    AND sarea.CTL_ACT_CD = 1
    AND pf_soldto_shipto.SAP_BP_SAREA_ID = soldto_sarea.SAP_BP_SAREA_ID
    AND pf_soldto_shipto.SHIPTO_SAP_BP_SAREA_ID = shipto_sarea.SAP_BP_SAREA_ID
    AND pf_soldto_shipto.SAREA_ID = sarea.SAREA_ID;

COMMENT ON TABLE VLGNS_SOLDSHIP_SAREA_FLEX_SRCH is 'Used by the Ship-to search screen of Order Taking';

REM END VLGNS_SOLDSHIP_SAREA_FLEX_SRCH

-------------------------------------------------------------------------------

PROMPT  VLGNS_SOLDSHIP_BEST_FLEX_SRCH

CREATE OR REPLACE FORCE VIEW VLGNS_SOLDSHIP_BEST_FLEX_SRCH AS
  SELECT DISTINCT
         shipto.SAP_ID || '_' || shipto.SAP_BP_ID || '_' || soldto.SAP_BP_ID || '_' || dailystatus.LANG_CD AS COMPOSITE_ID,
         shipto.NAME1 AS SHIPTO_NAME,
         shipto.SAP_ID AS SHIPTO_SAP_ID,
         shipto.SAP_BP_ID AS SHIPTO_SAP_BP_ID, -- for authorization
         shipto.POSTAL_CODE,
         shipto.CITY,
         shipto.STREET || ' ' || shipto.HOUSE_NUMBER as address,
      shipto.MARKED_FOR_DELETION AS SHIPTO_MARKED_FOR_DELETION,
         logonshipto.DAILY_ORDER_CONTACT_FLAG,
         decode(logonshipto.DAILY_ORDER_STATUS_CD, NULL, 1, decode(SIGN(trunc(SYSDATE) - trunc(logonshipto.DAILY_ORDER_LAST_CONTACT_DAT)), 1, 1, logonshipto.DAILY_ORDER_STATUS_CD)) as DAILY_ORDER_STATUS_CD,
         dailystatus.LANG_CD as LANG_CD,
         dailystatus.TXT as DAILY_ORDER_STATUS_TXT,
         soldto.NAME1 AS SOLDTO_NAME,
         soldto.SAP_ID AS SOLDTO_SAP_ID,
         soldto.SAP_BP_ID AS SOLDTO_SAP_BP_ID, -- for authorization
     soldto.MARKED_FOR_DELETION AS SOLDTO_MARKED_FOR_DELETION,
     /* searchable string - order matters!*/
    lower(soldto.SAP_ID || soldto.name1 || shipto.sap_id || shipto.name1 || shipto.POSTAL_CODE || shipto.CITY || shipto.STREET || shipto.HOUSE_NUMBER) AS COMPOSITE_SEARCH_STRING,
		 shipto.CEM_VALID_UNTIL_DAT,
		 shipto.AGG_VALID_UNTIL_DAT
  FROM
    TLGNS_SAP_BP shipto
      left outer join TLGNC_SHIPTO logonshipto on (logonshipto.shipto_sap_bp_id = shipto.sap_bp_id)
      left outer join tlgnr_daily_order_status_tx dailystatus on (dailystatus.daily_order_status_cd = decode(logonshipto.daily_order_status_cd, null, 1, decode(sign(trunc(sysdate) - trunc(logonshipto.daily_order_last_contact_dat)), 1, 1, logonshipto.daily_order_status_cd)))
      join tlgns_sap_bp_sarea shipto_sarea on ( shipto_sarea.sap_bp_id = shipto.sap_bp_id)
      join tlgns_sap_bp_pf_shipto pf_soldto_shipto on (pf_soldto_shipto.shipto_sap_bp_sarea_id = shipto_sarea.sap_bp_sarea_id)
      join TLGNS_SAP_BP_SAREA soldto_sarea on (pf_soldto_shipto.SAP_BP_SAREA_ID = soldto_sarea.SAP_BP_SAREA_ID)
      join TLGNS_SAP_BP soldto on (soldto_sarea.SAP_BP_ID = soldto.SAP_BP_ID)
      left outer join tlgnr_sap_account_group_bs sapaccountgroup on (soldto.account_group_cd = sapaccountgroup.sap_account_group_cd)
  WHERE
    shipto.bp_is_shipto = 1 /* should not be required, see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
    AND soldto.bp_is_soldto = 1 /* needed! see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
    AND shipto_sarea.MARKED_FOR_DELETION = '0'
    AND soldto_sarea.MARKED_FOR_DELETION = '0' /* LOGON-6477 (Avoid loading inactive/invalid sales areas) */
    and (shipto_sarea.sap_bp_id != soldto_sarea.sap_bp_id
         OR sapaccountgroup.invent_soldto_for_shipto = 1);

COMMENT ON TABLE VLGNS_SOLDSHIP_BEST_FLEX_SRCH is 'Used by the Ship-to search screen of Order Taking';

REM END VLGNS_SOLDSHIP_BEST_FLEX_SRCH

-------------------------------------------------------------------------------

PROMPT  VLGNC_WEBORDER_SHIP_TO_SOLD_TO

CREATE OR REPLACE FORCE VIEW VLGNC_WEBORDER_SHIP_TO_SOLD_TO AS
SELECT distinct
  shipto.SAP_ID || '_' || shipto.SAP_BP_ID || '_' || soldto.SAP_BP_ID AS COMPOSITE_ID,
  shipto.NAME1 AS SHIPTO_NAME,
  shipto.SAP_ID AS SHIPTO_SAP_ID,
  shipto.SAP_BP_ID AS SHIPTO_SAP_BP_ID,
  shipto.STREET,
  shipto.HOUSE_NUMBER,
  shipto.CITY,
  shipto.POSTAL_CODE,
  shipto.COUNTRY_CD,
  soldto.NAME1 AS SOLDTO_NAME,
  soldto.SAP_ID AS SOLDTO_SAP_ID,
  soldto.SAP_BP_ID AS SOLDTO_SAP_BP_ID -- included for authorization
FROM
  TLGNS_SAP_BP shipto,
  TLGNS_SAP_BP soldto,
  TLGNS_SAP_BP_SAREA shipto_sarea,
  TLGNS_SAP_BP_SAREA soldto_sarea,
  TLGNS_SAP_BP_PF_SHIPTO pf_soldto_shipto,
  TLGNC_SAREA sarea
WHERE
  shipto.bp_is_shipto = 1 /* should not be required, see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
  AND soldto.bp_is_soldto = 1 /* needed! see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
  AND shipto_sarea.SAP_BP_ID = shipto.SAP_BP_ID
  AND soldto_sarea.SAP_BP_ID = soldto.SAP_BP_ID
  AND shipto_sarea.MARKED_FOR_DELETION = '0'
  AND soldto_sarea.MARKED_FOR_DELETION = '0'
  AND soldto.MARKED_FOR_DELETION = '0'
  AND shipto.MARKED_FOR_DELETION = '0'
  AND pf_soldto_shipto.SAP_BP_SAREA_ID = soldto_sarea.SAP_BP_SAREA_ID
  AND pf_soldto_shipto.SHIPTO_SAP_BP_SAREA_ID = shipto_sarea.SAP_BP_SAREA_ID
  AND pf_soldto_shipto.SAREA_ID = sarea.SAREA_ID
order by shipto.NAME1, soldto.NAME1;

COMMENT ON TABLE VLGNC_WEBORDER_SHIP_TO_SOLD_TO is 'Used by web order taking';

REM END VLGNC_WEBORDER_SHIP_TO_SOLD_TO

-------------------------------------------------------------------------------

PROMPT  VLGNC_EDITBP_SOLDSHIP_SAREA

CREATE OR REPLACE FORCE VIEW VLGNC_EDITBP_SOLDSHIP_SAREA AS
SELECT
  shipto.SAP_ID || '_' || pf_soldto_shipto.SAREA_ID || '_' || shipto.SAP_BP_ID || '_' || soldto.SAP_BP_ID AS COMPOSITE_ID,
  shipto.NAME1 AS SHIPTO_NAME,
  shipto.SAP_ID AS SHIPTO_SAP_ID,
  shipto.MARKED_FOR_DELETION AS SHIPTO_MARKED_FOR_DELETION,
  shipto.POSTAL_CODE AS SHIPTO_POSTAL_CODE,
  shipto.CITY AS SHIPTO_CITY,
  soldto.SAP_ID AS SOLDTO_SAP_ID,
  soldto.NAME1 AS SOLDTO_NAME,
  soldto.MARKED_FOR_DELETION AS SOLDTO_MARKED_FOR_DELETION,
  pf_soldto_shipto.SAREA_ID AS SAREA_ID,
  sarea.SAREA_DISPLAY_LONGNAME as SAREA_DISPLAY_LONGNAME,
  dailystatus.LANG_CD as DAILY_ORDER_STATUS_LANG_CD,
  lower(soldto.SAP_ID || soldto.name1 || shipto.sap_id || shipto.name1 || shipto.POSTAL_CODE || shipto.CITY || shipto.STREET || shipto.HOUSE_NUMBER) AS COMPOSITE_SEARCH_STRING
FROM
  TLGNS_SAP_BP shipto
    left outer join TLGNC_SHIPTO logonshipto on (logonshipto.shipto_sap_bp_id = shipto.sap_bp_id)
    left outer join TLGNR_DAILY_ORDER_STATUS_TX dailystatus on (dailystatus.DAILY_ORDER_STATUS_CD = decode(logonshipto.DAILY_ORDER_STATUS_CD, NULL, 1, decode(SIGN(trunc(SYSDATE) - trunc(logonshipto.DAILY_ORDER_LAST_CONTACT_DAT)), 1, 1, logonshipto.DAILY_ORDER_STATUS_CD))),
  TLGNS_SAP_BP soldto,
  TLGNS_SAP_BP_SAREA shipto_sarea,
  TLGNS_SAP_BP_SAREA soldto_sarea,
  TLGNS_SAP_BP_PF_SHIPTO pf_soldto_shipto,
  TLGNC_SAREA sarea
WHERE
  shipto.bp_is_shipto = 1 /* should not be required, see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
  AND soldto.bp_is_soldto = 1 /* needed! see comments in VLGNS_SOLDSHIP_FLEX_SRCH */
  AND shipto_sarea.SAP_BP_ID = shipto.SAP_BP_ID
  AND soldto_sarea.SAP_BP_ID = soldto.SAP_BP_ID
  AND pf_soldto_shipto.SAP_BP_SAREA_ID = soldto_sarea.SAP_BP_SAREA_ID
  AND pf_soldto_shipto.SHIPTO_SAP_BP_SAREA_ID = shipto_sarea.SAP_BP_SAREA_ID
  AND pf_soldto_shipto.SAREA_ID = sarea.SAREA_ID;

COMMENT ON TABLE VLGNC_EDITBP_SOLDSHIP_SAREA is 'Used at BP modification after delivered status.';

REM END VLGNC_EDITBP_SOLDSHIP_SAREA

-------------------------------------------------------------------------------
PROMPT  VLGNC_SHIPPINGPOINT_MATERIAL

CREATE OR REPLACE FORCE VIEW VLGNC_SHIPPINGPOINT_MATERIAL AS
  SELECT distinct
  (descr.sap_material_id || '_' || descr.lang_cd || '_' || sp.shippingpoint_id)id,
  descr.SAP_MATERIAL_ID,
  descr.TXT,
  descr.LANG_CD,
  lp.shippingpoint_id,
  sp.shippingpoint_name,
  sp.coord_lat,
  sp.coord_lon,
    sp.plant_cd,
  lp.package_type_cd
  FROM
  TLGNC_SHIPPINGPOINT sp
  inner join TLGNC_LOADINGPOINT lp on (lp.shippingpoint_id = sp.shippingpoint_id)
  inner join TLGNC_LDPOINT_MAT_RESTR matrestr on (matrestr.loadingpoint_id=lp.loadingpoint_id)
        inner join TLGNS_SAP_MATERIAL mat on (mat.SAP_MATERIAL_ID=matrestr.SAP_MATERIAL_ID)
        inner join TLGNS_SAP_MATERIAL_DESCRIPTION descr on (descr.SAP_MATERIAL_ID=mat.SAP_MATERIAL_ID);

COMMENT ON TABLE VLGNC_SHIPPINGPOINT_MATERIAL is 'TODO author of this view: describe purpose and usage of view. Suspicious that its not even used anywhere';

REM END VLGNC_SHIPPINGPOINT_MATERIAL

-------------------------------------------------------------------------------

PROMPT  VLGNC_POOL_CARRIER_CARRIER
CREATE OR REPLACE FORCE VIEW VLGNC_POOL_CARRIER_CARRIER AS
  select
    pc.pool_id || '_' || c.carrier_id as COMPOSITE_ID,
    pc.pool_id as POOL_ID,
    pooltx.name as POOL_NAME,
    pooltx.lang_cd as POOL_NAME_LANG_CD,
    pc.carrier_id as CARRIER_ID,
    c.active_flag as CARRIER_ACTIVE_FLAG,
    NVL(c.display_name, sapvendor.name1) as CARRIER_NAME,
    sapvendor.sap_vendor_id,
    sapvendor.VN_IS_COMM_CARRIER,
    sapvendor.VN_IS_EXEC_CARRIER,
    sapvendor.VN_IS_INTERNAL
    from
      tlgnc_pool_carrier pc
      inner join tlgnc_carrier c on (c.carrier_id = pc.carrier_id)
      inner join tlgns_sap_vendor sapvendor on (sapvendor.sap_vendor_id = c.sap_vendor_id)
      inner join tlgnc_pool_tx poolTx on pooltx.pool_id = pc.pool_id
    order by pc.pool_id, pc.carrier_id;

COMMENT ON TABLE VLGNC_POOL_CARRIER_CARRIER is 'selects carriers for pool with display name';

REM END VLGNC_POOL_CARRIER_CARRIER

-------------------------------------------------------------------------------
PROMPT  VLGNC_POOL_CARRIER_CARRIER

CREATE OR REPLACE FORCE VIEW VLGNC_POOL_ALL_CARRIER AS
  select
    c.carrier_id as COMPOSITE_ID,
    c.carrier_id as CARRIER_ID,
    c.active_flag as CARRIER_ACTIVE_FLAG,
    NVL(c.display_name, sapvendor.name1) as CARRIER_NAME,
    sapvendor.sap_vendor_id,
    sapvendor.VN_IS_COMM_CARRIER,
    sapvendor.VN_IS_EXEC_CARRIER,
    sapvendor.VN_IS_INTERNAL
    from tlgnc_carrier c
      inner join tlgns_sap_vendor sapvendor on (sapvendor.sap_vendor_id = c.sap_vendor_id)
    order by c.carrier_id;

COMMENT ON TABLE VLGNC_POOL_CARRIER_CARRIER is 'selects all carriers with display name';

REM END VLGNC_POOL_ALL_CARRIER


-------------------------------------------------------------------------------

PROMPT  VLGNC_POOL_CARRIER_CARRIER_CNT
CREATE OR REPLACE FORCE VIEW VLGNC_POOL_CARRIER_CARRIER_CNT AS
  select
    pc.pool_id as POOL_ID,
  cast(count(1) as number(12)) as NUM_CARRIERS
    from
      tlgnc_pool_carrier pc
      inner join tlgnc_carrier c on (c.carrier_id = pc.carrier_id)
      inner join tlgns_sap_vendor sapvendor on (sapvendor.sap_vendor_id = c.sap_vendor_id)
    where c.active_flag = 1
    group by pc.pool_id;

COMMENT ON TABLE VLGNC_POOL_CARRIER_CARRIER_CNT is 'get count of carriers for a pool';

REM END VLGNC_POOL_CARRIER_CARRIER_CNT


-------------------------------------------------------------------------------

PROMPT  VLGNC_POOL_CARRIER_CARRIER_RES
CREATE OR REPLACE FORCE VIEW VLGNC_POOL_CARRIER_CARRIER_RES AS
  select
    pc.pool_id as POOL_ID,
    sapvendor.sap_vendor_id as SAP_VENDOR_ID
    from
      tlgnc_pool_carrier pc
      inner join tlgnc_carrier c on (c.carrier_id = pc.carrier_id)
      inner join tlgns_sap_vendor sapvendor on (
          sapvendor.sap_vendor_id = c.sap_vendor_id
          and sapvendor.marked_for_deletion='0')
    where c.active_flag = 1;

COMMENT ON TABLE VLGNC_POOL_CARRIER_CARRIER_RES is 'get restricted pools';

REM END VLGNC_POOL_CARRIER_CARRIER_RES


-------------------------------------------------------------------------------

PROMPT  VLGNC_SHIPTO_SAREA_DETAIL

CREATE OR REPLACE FORCE VIEW VLGNC_SHIPTO_SAREA_DETAIL AS
  SELECT
rbk.SHIPTO_SAREA_RESTR_ID || '_' || materialdesc.lang_cd as COMPOSITE_ID,
rbk.SHIPTO_SAREA_RESTR_ID,
rbk.SHIPTO_SAREA_ID,
rbk.SAP_MATERIAL_ID,
rbk.ENABLED_FLAG,
rbk.SHIPTO_SILO_ID,
rbk.QUANTITY,
rbk.DELIVERY_TYPE_CD,
rbk.POOL_ID,
rbk.AUTO_POOL_ASSIGNMENT_FLAG,
rbk.COMM_CARRIER_ID,
rbk.AUTO_CARRIER_ASSIGNMENT_FLAG,
rbk.BILLTO_SAP_BP_ID,
rbk.PAYER_SAP_BP_ID,
rbk.BILLTO_RULETYPE_CD,
rbk.FREE_DELIVERY_FLAG,
rbk.RELEASE_SAP_FLAG,
rbk.NOTE_ORDER_MGMT,
rbk.NOTE_DELIVERY,
rbk.NOTE_DISPATCHING,
rbk.NOTE_TRUCK_DRIVER,
rbk.NOTE_RAILWAY_DISP,
rbk.SHIPPINGPOINT_ID,
rbk.LOADINGPOINT_ID,
rbk.PRODUCTION_PLANT_CD,
rbk.UOM_CD,
rbk.AVAILABLE_IN_CUST_WEB_FLAG,
rbk.AVAILABLE_IN_PICKUP_APP_FLAG,
material.sap_material_no,
material.material_package_cd,
materialdesc.txt as txt,
materialdesc.lang_cd,
packtx.txt as material_package_txt,
pkg.logon_package_type_cd
from
TLGNC_SHIPTO_SAREA_RESTR rbk
left outer join TLGNS_SAP_MATERIAL material on (rbk.SAP_MATERIAL_ID=material.SAP_MATERIAL_ID)
left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc on (rbk.SAP_MATERIAL_ID=materialdesc.SAP_MATERIAL_ID)
left outer join TLGNR_MATERIAL_PACKAGE_TX packtx on (material.MATERIAL_PACKAGE_CD=packtx.MATERIAL_PACKAGE_CD and packtx.lang_cd = materialdesc.lang_cd)
left outer join tlgnr_material_package_bs pkg ON(material.material_package_cd = pkg.material_package_cd);

COMMENT ON TABLE VLGNC_SHIPTO_SAREA_DETAIL is 'Basically the same data as tlgnc_shipto_sarea_restr (restrictions of a shipto-salesarea), but including the material names in all languages.';

REM END VLGNC_SHIPTO_SAREA_DETAIL

-------------------------------------------------------------------------------
PROMPT  vlgnc_dispo_order_list

create or replace force view vlgnc_dispo_order_list as
select
ord.order_id  || '_' || nvl(to_char(tt.rail_timetable_id), '_') as composite_id,
ord.order_id,
ord.custorder_id,
ord.order_number,
to_char(ord.order_number) as order_number_in_str,
ord.delivery_from_dat,
ord.delivery_to_dat,
--carrier block start
ord.exec_carrier_id,
ord.comm_carrier_id,
ccarrier.display_name as comm_carrier_name,
ecarrier.display_name as exec_carrier_name,
--Custom authorization for SAP vendor
ccarrier.sap_vendor_id as comm_sap_vendor_id,
ecarrier.sap_vendor_id as exec_sap_vendor_id,
--carrier block end
ccarrier.carrier_requires_planning_flag,
ord.pool_id,
ord.order_status_cd,
ord.transport_duration_min,
ord.shippingpoint_id,
ord.note_dispatching,
ord.note_truck_driver,
ord.carrier_truck_driver_note,
ord.note_delivery,
ord.note_railway_disp,
ord.carrier_decline_note,
ord.cust_weborder_note,
ord.carrier_delivery_note,
ord.transport_distance_m,
ord.delivery_type_cd,
ord.shipto_sap_bp_id,
ord.on_hold_order_and_locked_flag,
ord.lnf_coord_lat,
ord.lnf_coord_lon,
ord.lnf_site_name,
ord.lnf_site_street_house_no,
ord.lnf_site_country,
ord.lnf_site_postal_code,
ord.lnf_site_city,
ord.planned_unloading_time_min,
shippt.shippingpoint_name,
shippt.plant_cd,
sbp.sap_id as shipto_sap_id,
sbp.name1 as shipto_name,
sbp.name2 as shipto_name2,
sbp.city as shipto_city,
cbp.sap_id as soldto_sap_id,
cbp.name1 as soldto_name,
cbp.name2 as soldto_name2,
st.coord_lat as shipto_coordlat,
st.coord_lon as shipto_coordlon,
sattustext.lang_cd,
sattustext.txt as status_txt,
pooltext.name as pool_name,
tt.arr_railway_station_id,
tt.dep_railway_station_id,
tt.arr_switching_time_min,
tt.dep_switching_time_min,
tt.arr_offset_days,
rw_arr.railway_station_no AS arr_railway_station_no,
rw_arr.railway_station_name AS arr_railway_station_name,
ord.delivery_from_dat - nvl(tt.arr_offset_days, 0) as sort_from_dat,
ord.delivery_to_dat - nvl(tt.arr_offset_days, 0) as sort_to_dat,
bagstock.coord_lon as bag_stock_coord_lon,
bagstock.coord_lat as bag_stock_coord_lat
from tlgnc_order ord
join tlgnc_shippingpoint shippt on (ord.shippingpoint_id = shippt.shippingpoint_id)
join tlgnc_shipto st on (ord.shipto_sap_bp_id = st.shipto_sap_bp_id and st.active_flag = 1)
join tlgns_sap_bp sbp on (st.shipto_sap_bp_id = sbp.sap_bp_id)
join tlgns_sap_bp cbp on (ord.soldto_sap_bp_id = cbp.sap_bp_id)
left join tlgnc_carrier ccarrier on (ord.comm_carrier_id = ccarrier.carrier_id)
left join tlgnc_carrier ecarrier on (ord.exec_carrier_id = ecarrier.carrier_id)
left join tlgnc_shippingpoint bagstock on (ccarrier.bag_stock_shippingpoint_id = bagstock.shippingpoint_id)
left join tlgnr_order_status_tx sattustext on (sattustext.order_status_cd=ord.order_status_cd)
left join tlgnc_pool_tx pooltext on (pooltext.pool_id=ord.pool_id and pooltext.lang_cd=sattustext.lang_cd)
left join tlgnc_rail_timetable tt on (tt.arr_railway_station_id = sbp.railway_station_id and tt.dep_railway_station_id = shippt.railway_station_id)
left join tlgnc_railway_station rw_arr on (rw_arr.railway_station_id = sbp.railway_station_id)
join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1);

comment on table vlgnc_dispo_order_list is 'orders for dispo screen';

rem end vlgnc_dispo_order_list

-------------------------------------------------------------------------------
PROMPT  vlgnc_dispo_ext_order_list

create or replace force view vlgnc_dispo_ext_order_list as
select
'EXT_' || ord.ext_order_id as composite_id,
NULL as order_id,
NULL as custorder_id,
ord.ext_order_id as ext_order_id,
ord.ext_order_number as order_number,
ord.delivery_from_dat,
ord.delivery_to_dat,
--Double the carrier references, as it must be the same as dispo_order_list
--carrier block start
ord.carrier_id as comm_carrier_id,
ord.carrier_id as exec_carrier_id,
carrier.display_name as comm_carrier_name,
carrier.display_name as exec_carrier_name,
carrier.sap_vendor_id as comm_sap_vendor_id,
carrier.sap_vendor_id as exec_sap_vendor_id,
--carrier block end
carrier.sap_vendor_id,
ord.pool_id,
ord.order_status_cd,
ord.transport_duration_min,
NULL as shippingpoint_id,
cast('' as varchar2(256)) as note_dispatching,
ord.note_truck_driver,
st.carrier_truck_driver_note,
cast('' as varchar2(256)) as note_delivery,
cast('' as varchar2(256)) as note_railway_disp,
cast('' as varchar2(256)) as cust_weborder_note,
cast('' as varchar2(256)) as carrier_delivery_note,
ord.transport_distance_m,
cast(1 as number(2)) as delivery_type_cd,
NULL as shipto_sap_bp_id,
cast(0 as number(1)) as on_hold_order_and_locked_flag,
NULL as lnf_site_name,
NULL as lnf_site_street_house_no,
NULL as lnf_site_country,
NULL as lnf_site_postal_code,
NULL as lnf_site_city,
shippt.site_name as shippingpoint_name,
NULL as plant_cd,
shippt.load_unload_time_min as loading_time,
st.ext_site_id as shipto_id,
st.site_name as shipto_name,
st.site_city as shipto_city,
st.site_name as soldto_name,
st.load_unload_time_min as unloading_time,
st.load_unload_time_min as planned_unloading_time_min,
sattustext.lang_cd,
sattustext.txt as status_txt,
pooltext.name as pool_name,
NULL as arr_railway_station_id,
NULL as dep_railway_station_id,
cast(0 as number(2)) as arr_switching_time_min,
cast(0 as number(2)) as dep_switching_time_min,
cast(0 as number(2)) as arr_offset_days,
NULL as arr_railway_station_no,
NULL as arr_railway_station_name,
ord.delivery_from_dat as sort_from_dat,
ord.delivery_to_dat as sort_to_dat,
ord.ordered_quantity,
ord.loaded_quantity,
mat.material_name,
uomtx.txt as uom_name,
uomtx.uom_cd as uom_cd
from tlgnc_ext_order ord
join tlgnc_ext_site shippt on (ord.loading_ext_site_id = shippt.ext_site_id)
join tlgnc_ext_site st on (ord.unloading_ext_site_id = st.ext_site_id)
join tlgnc_ext_material mat on (ord.ext_material_id = mat.ext_material_id)
left join tlgnc_carrier carrier on (ord.carrier_id = carrier.carrier_id)
left join tlgnr_order_status_tx sattustext on (sattustext.order_status_cd=ord.order_status_cd)
left join tlgnr_uom_tx uomtx on (ord.ordered_uom_cd = uomtx.uom_cd and uomtx.lang_cd=sattustext.lang_cd)
left join tlgnc_pool_tx pooltext on (pooltext.pool_id=ord.pool_id and pooltext.lang_cd=sattustext.lang_cd);

comment on table vlgnc_dispo_ext_order_list is 'external orders for dispo screen';

rem end vlgnc_dispo_ext_order_list

-------------------------------------------------------------------------------
prompt  vlgnc_dispo_order_item_list

create or replace force view vlgnc_dispo_order_item_list as
select
item.order_item_id,
item.order_id,
item.order_item_no,
ord.delivery_type_cd,
item.ordered_quantity,
item.loaded_uom_cd,
item.loaded_quantity,
item.sap_material_id,
item.production_plant_cd,
item.planned_loading_time_min,
item.load_id,
ldpt.loading_time,
ldpt.loadingpoint_name,
silo.unloading_time,
mat.material_is_lf islandfill,
mat.material_package_cd,
mat.sap_material_no as material_number,
matdesc.lang_cd as material_lang_cd,
matdesc.txt as material_name,
replace(matdesc.txt,' ','') as purged_material_name,
uom.uom_cd,
uom.fuzzy_quantity_flag,
uomtx.txt as uom_name,
load.load_order_id,
load.load_order_item_id,
brord.order_status_cd as br_order_status_cd,
brord.order_id as br_order_id,
brwagon.wagon_id as br_wagon_id,
brwagon.wagon_no as br_wagon_no,
pkg.logon_package_type_cd,
ord.etran_return,
ord.etran_outbound
from
tlgnc_order_item item
join tlgnc_order ord on (item.order_id = ord.order_id)
join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1)
left join tlgnc_loadingpoint ldpt on (item.loadingpoint_id = ldpt.loadingpoint_id)
left join tlgnc_shipto_silo silo on (item.shipto_silo_id = silo.shipto_silo_id)
join tlgns_sap_material mat on (item.sap_material_id = mat.sap_material_id)
JOIN tlgnr_material_package_bs pkg ON(mat.material_package_cd = pkg.material_package_cd)
join tlgns_sap_material_description matdesc on (item.sap_material_id = matdesc.sap_material_id)
join tlgnr_uom_bs uom on (item.ordered_uom_cd = uom.uom_cd)
join tlgnr_uom_tx uomtx on (item.ordered_uom_cd = uomtx.uom_cd and matdesc.lang_cd =  uomtx.lang_cd)
left join tlgnc_load load on (load.load_id = item.load_id)
left join (
    tlgnc_order_item britem
    join tlgnc_order brord on (britem.order_id = brord.order_id)
    left join (
              tlgnc_load brload
              join tlgnc_wagon brwagon on (brload.wagon_id = brwagon.wagon_id)
    ) on (britem.load_id = brload.load_id)
) on (britem.order_item_id = item.bimodal_rail_order_item_id)
order by item.order_id, item.order_item_no;

comment on table vlgnc_dispo_order_item_list is 'items of orders on dispo screen';

rem end vlgnc_dispo_order_item_list

-------------------------------------------------------------------------------

prompt vlgnc_dispo_home_plant

create or replace force view vlgnc_dispo_home_plant as
select
  shippt.shippingpoint_id,
  shippt.shippingpoint_name,
  plant.plant_cd,
  plant.plant_name,
  plant.coord_lat,
  plant.coord_lon,
  greatest(nvl(shippt.ctl_mod_ts, DATE'1970-01-01'), nvl(plant.ctl_mod_ts, DATE'1970-01-01')) as ctl_mod_ts
from tlgnc_shippingpoint shippt
left join tlgnr_plant_bs plant on (shippt.plant_cd = plant.plant_cd);

COMMENT ON TABLE vlgnc_dispo_home_plant is 'List of possible Home Plants for trucks on dispo screen';

rem end vlgnc_dispo_home_plant

-------------------------------------------------------------------------------

prompt  vlgnc_dispo_wagon_type_list

create or replace force view vlgnc_dispo_wagon_type_list as
select
stwt.shipto_wagon_type_id,
st.shipto_sap_bp_id,
stwt.evaluation_priority,
wt.wagon_type_cd,
wt.wagon_type_name,
wtx.lang_cd,
wtx.txt as wagon_type_name_tx
from tlgnc_shipto st
join tlgnc_shipto_wagon_type stwt on (st.shipto_id = stwt.shipto_id)
join tlgnr_wagon_type_bs wt on (wt.wagon_type_cd = stwt.wagon_type_cd)
join tlgnr_wagon_type_tx wtx on (wtx.wagon_type_cd = stwt.wagon_type_cd);

comment on table vlgnc_dispo_wagon_type_list is 'wagon types for a shipto. used in rail dispo';

rem end vlgnc_dispo_wagon_type_list

-------------------------------------------------------------------------------

prompt  vlgnc_dispo_wagon_trsp_cat

create or replace force view vlgnc_dispo_wagon_trsp_cat as
select wagon_id,
  wt.wagon_type_cd,
  wt.wagon_trsp_cat_cd
from tlgnc_wagon wagon
join tlgnr_wagon_type_bs wt on (wagon.wagon_type_cd = wt.wagon_type_cd)
join tlgnr_wagon_trsp_cat_bs wtrsp on (wt.wagon_trsp_cat_cd = wtrsp.wagon_trsp_cat_cd);

comment on table vlgnc_dispo_wagon_trsp_cat is 'wagon transport categories used in rail dispo, preload popup.';

rem end vlgnc_dispo_wagon_trsp_cat

-------------------------------------------------------------------------------

prompt  vlgnc_dispo_truck_type_list

create or replace force view vlgnc_dispo_truck_type_list as
select ott.order_truck_type_id,
  ott.order_id,
  ott.evaluation_priority,
  ttx.truck_type_cd,
  ttx.lang_cd,
  ttx.txt as truck_type_name_tx
from tlgnr_truck_type_tx ttx
join tlgnc_order_truck_type ott  on (ttx.truck_type_cd = ott.truck_type_cd);

comment on table vlgnc_dispo_truck_type_list is 'truck types for orders. used in scheduling';

rem end vlgnc_dispo_truck_type_list

-------------------------------------------------------------------------------

prompt  vlgnc_dispo_truck_list
create or replace force view vlgnc_dispo_truck_list as
  select
  truck.truck_id || '_' || trucktype.lang_cd as composite_id,
  truck.truck_id,
  truck.carrier_id,
  truck.truck_name,
  truck.truck_type_cd,
  trucktype.txt as truck_type_txt,
  trucktype.lang_cd,
  truck.pda_available_flag,
  truck.priority,
  trailer.trailer_id,
  poolt.pool_id,
  driver.driver_id,
  pers.name as driver_name,
  pers.firstname as driver_firstname,
  pda_session_id,
  pda_session_status_cd,
  container.container_trsp_cat_cd,
  trailer.trailer_trsp_cat_cd,
  truck.truck_transport_cat_cd,
  cast(coalesce(trailer.nr_chambers, truck.nr_chambers, 1) as number(12)) as nr_chambers,
  truck.dispatching_carrier_id,
  truck.sap_vehicle_char_cd,
  carrier.sap_vendor_id,
  carrier.display_name as carrier_display_name,
  shippt.shippingpoint_id,
  shippt.sap_shippingpoint_no,
  shippt.shippingpoint_name,
  plant.plant_cd,
  plant.plant_name,
  plant.coord_lat,
  plant.coord_lon,
  greatest( nvl(truck.ctl_mod_ts, DATE'1970-01-01'), nvl(poolt.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(carrier.ctl_mod_ts, DATE'1970-01-01'), nvl(driver.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(usr.ctl_mod_ts, DATE'1970-01-01'), nvl(pers.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(trailer.ctl_mod_ts, DATE'1970-01-01'), nvl(container.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(trucktype.ctl_mod_ts, DATE'1970-01-01'), nvl(shippt.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(plant.ctl_mod_ts, DATE'1970-01-01')) as ctl_mod_ts
from tlgnc_truck truck
join  tlgnc_pool_truck poolt on (truck.truck_id = poolt.truck_id)
join tlgnc_carrier carrier on (carrier.carrier_id = truck.carrier_id)
left join (
  tlgnc_driver driver
  join tlgnu_user usr on (driver.user_id = usr.user_id)
  join tlgnc_person pers on (usr.person_id = pers.person_id)) on (driver.current_truck_id = truck.truck_id)
  left join (select /*+ FIRST_ROWS(1) */ pdasess.pda_session_id as pda_session_id, pdasess.pda_session_status_cd as pda_session_status_cd,  pdasess.user_id as pdauser_id
              from tlgnc_pda_session pdasess where pda_session_id in (select max(pdas.pda_session_id) from tlgnc_pda_session pdas where pdas.pda_session_status_cd in (2,4) and pdas.user_id = pdasess.user_id)
            ) on pdauser_id = driver.user_id
left join tlgnc_trailer trailer  on (truck.truck_id = trailer.current_truck_id)
left join tlgnc_container container on
    ((truck.truck_id = container.current_truck_id) or (trailer.trailer_id = container.current_trailer_id))
left join tlgnr_truck_type_tx trucktype on truck.truck_type_cd = trucktype.truck_type_cd
left join tlgnc_shippingpoint shippt  on (truck.return_shippingpoint_id = shippt.shippingpoint_id)
left join tlgnr_plant_bs plant on (shippt.plant_cd = plant.plant_cd)
    where truck.active_flag = 1;

COMMENT ON TABLE vlgnc_dispo_truck_list is 'List of trucks on dispo screen';

REM END vlgnc_dispo_truck_list

-------------------------------------------------------------------------------

prompt  vlgnc_dispo_truck_list_v2
create or replace force view vlgnc_dispo_truck_list_v2 as
  select
  truck.truck_id || '_' || trucktype.lang_cd as composite_id,
  truck.truck_id,
  truck.carrier_id,
  truck.truck_name || ' (' || truck.truck_plate_no || ')' as truck_name,
  truck.truck_type_cd,
  trucktypebs.truck_type_short_name as truck_type_txt,
  trucktype.lang_cd,
  truck.pda_available_flag,
  truck.priority,
  trailer.trailer_id,
  poolt.pool_id,
  driver.driver_id,
  pers.name as driver_name,
  pers.firstname as driver_firstname,
  pda_session_id,
  pda_session_status_cd,
  container.container_trsp_cat_cd,
  trailer.trailer_trsp_cat_cd,
  truck.truck_transport_cat_cd,
  cast(coalesce(trailer.nr_chambers, truck.nr_chambers, 1) as number(12)) as nr_chambers,
  truck.dispatching_carrier_id,
  truck.sap_vehicle_char_cd,
  carrier.sap_vendor_id,
  carrier.display_name as carrier_display_name,
  shippt.shippingpoint_id,
  shippt.sap_shippingpoint_no,
  shippt.shippingpoint_name,
  plant.plant_cd,
  plant.plant_name,
  plant.coord_lat,
  plant.coord_lon,
  nvl(ast.txt, '-') as assign_status,
  greatest( nvl(truck.ctl_mod_ts, DATE'1970-01-01'), nvl(poolt.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(carrier.ctl_mod_ts, DATE'1970-01-01'), nvl(driver.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(usr.ctl_mod_ts, DATE'1970-01-01'), nvl(pers.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(trailer.ctl_mod_ts, DATE'1970-01-01'), nvl(container.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(trucktype.ctl_mod_ts, DATE'1970-01-01'), nvl(shippt.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(plant.ctl_mod_ts, DATE'1970-01-01'), nvl(ast.ctl_mod_ts, DATE'1970-01-01'),
  			nvl(loadstat.ctl_mod_ts, DATE'1970-01-01')) as ctl_mod_ts
from tlgnc_truck truck
join  tlgnc_pool_truck poolt on (truck.truck_id = poolt.truck_id)
join tlgnc_carrier carrier on (carrier.carrier_id = truck.carrier_id)
left join (
  tlgnc_driver driver
  join tlgnu_user usr on (driver.user_id = usr.user_id)
  join tlgnc_person pers on (usr.person_id = pers.person_id)) on (driver.current_truck_id = truck.truck_id)
  left join (select /*+ FIRST_ROWS(1) */ pdasess.pda_session_id as pda_session_id, pdasess.pda_session_status_cd as pda_session_status_cd,  pdasess.user_id as pdauser_id
              from tlgnc_pda_session pdasess where pda_session_id in (select max(pdas.pda_session_id) from tlgnc_pda_session pdas where pdas.pda_session_status_cd in (2,4) and pdas.user_id = pdasess.user_id)
            ) on pdauser_id = driver.user_id
left join tlgnc_trailer trailer  on (truck.truck_id = trailer.current_truck_id)
left join tlgnc_container container on
    ((truck.truck_id = container.current_truck_id) or (trailer.trailer_id = container.current_trailer_id))
left join tlgnr_truck_type_tx trucktype on truck.truck_type_cd = trucktype.truck_type_cd
left join tlgnr_truck_type_bs trucktypeBs on (trucktype.truck_type_cd = trucktypeBs.truck_type_cd)
left join tlgnc_shippingpoint shippt  on (truck.return_shippingpoint_id = shippt.shippingpoint_id)
left join tlgnr_plant_bs plant on (shippt.plant_cd = plant.plant_cd)
left join (select load.truck_id, max(assignment.assign_status_cd) as assign_status_cd, max(load.ctl_mod_ts) as ctl_mod_ts
            from tlgnc_load load
            left join tlgnc_assignment_part ap on (ap.load_id = load.load_id)
            left join tlgnc_assignment assignment on (assignment.assignment_id = ap.assignment_id)
            where assignment.assign_status_cd > 0 and load.loading_ts is not null and load.unloading_ts is null
            group by load.truck_id) loadstat on loadstat.truck_id = truck.truck_id
left join tlgnr_assign_status_tx ast on (loadstat.assign_status_cd = ast.assign_status_cd and trucktype.lang_cd = ast.lang_cd)
    where truck.active_flag = 1;

COMMENT ON TABLE vlgnc_dispo_truck_list_v2 is 'List of trucks on dispo screen v2';

REM END vlgnc_dispo_truck_list_v2

-------------------------------------------------------------------------------

prompt  vlgnc_dispo_truck_change_list

create or replace force view vlgnc_dispo_truck_change_list as
  select distinct
  truck.truck_id || '_' || unav.ctl_mod_ts as composite_id,
  truck.truck_id,
  unav.start_dat as unav_start_dat,
  unav.end_dat as unav_end_dat,
  traffjam.start_dat as traffjam_start_dat,
  drvbreakmsg.drvbreak_start_dat,
  drvbreakmsg.msg_cre_ts,
  greatest( nvl(truck.ctl_mod_ts, DATE'1970-01-01'),
        nvl(unav.ctl_mod_ts,DATE'1970-01-01'),
        nvl(traffjam.ctl_mod_ts,DATE'1970-01-01'),
        nvl(drvbreakmsg.driver_mod_ts,DATE'1970-01-01'),
        nvl(drvbreakmsg.drvbreak_mod_ts,DATE'1970-01-01'),
        nvl(drvbreakmsg.msg_mod_ts,DATE'1970-01-01')) as ctl_mod_ts
from tlgnc_truck truck
left join TLGNC_TRUCK_UNAVAILABILITY unav on truck.truck_id = unav.truck_id
left join TLGNC_TRAFFIC_JAM traffjam on truck.truck_id = traffjam.truck_id
left join (select   truck.truck_id,
                    drvbreak.start_dat as drvbreak_start_dat,
                    msg.CTL_CRE_TS as msg_cre_ts,
                    driver.ctl_mod_ts as driver_mod_ts,
                    drvbreak.ctl_mod_ts as drvbreak_mod_ts,
                    msg.ctl_mod_ts as msg_mod_ts
          from tlgnc_truck truck
          join tlgnc_driver driver on driver.current_truck_id = truck.truck_id
          left join tlgnc_driver_break drvbreak on driver.driver_id = drvbreak.driver_id
          left join tlgnc_pda_message msg on driver.user_id  = msg.pda_user_id) drvbreakmsg on truck.truck_id = drvbreakmsg.truck_id
where truck.active_flag = 1;


COMMENT ON TABLE vlgnc_dispo_truck_change_list is 'List of truck changes on dispo screen';

REM END vlgnc_dispo_truck_change_list

-------------------------------------------------------------------------------

PROMPT  vlgnc_dispo_truck_load_list
--
-- February 2011:
-- As an outcome of optimization described in ticket Bugzilla 66211 / LOGON-3660 there was a nested loop hint like this:
--   USE_NL(load, materiald,ap,ast,uom,uom_tc,assignment)
-- The optimization requires an index tlgnc_load(truck_id,unloading_ts) as well, see I_LOAD_TRUCK_UNLOADING_TS
--
-- In August 2011:
-- The execution plan in CHE prod suddenly fell over, see LOGON-4052 / Bugzilla 74018.
-- As part of that, the hint was rewritten to
--   USE_NL(load, materiald, uom_tx, uom, ap, assignment, ast) INDEX (ap I_ASSIGNMENT_PART_7)
-- I_ASSIGNMENT_PART_7 is the index on TLGNC_ASSIGNMENT_PART (LOAD_ID), in LOGON 1.6 + still in LOGON 2.1
-- PLUS, the groupings with () in the last left joins changed:
--   A left join (B inner join C inner join D) -> A left join B left join C left join D
--
-- October 2012: Added this change finally to LOGON 2.1 as well (until now only in CHE Prod, see ticket LOGON4052)
--
CREATE OR REPLACE FORCE VIEW vlgnc_dispo_truck_load_list as
select /*+ USE_NL(load, materiald, uom_tx, uom, ap, assignment, ast) INDEX (ap I_ASSIGNMENT_PART_7) */
    load.load_id,
  load.truck_id,
  materiald.lang_cd,
    load.tare_weight_t as tare_quantity,
    load.net_quantity,
    load.net_quantity_uom_cd,
    load.sap_material_id,
    load.production_plant_cd,
    load.loading_ts,
    materiald.txt as material_description,
    ap.planned_start as load_time,
    ap.assignment_id,
    uom_tx.txt as uom_txt,
    uom.uom_dimension_cd,
    uom.numerator_for_conversion,
    uom.denominator_for_conversion,
    ast.assign_status_cd,
    ast.txt as assign_status,
    greatest(nvl(load.ctl_mod_ts,DATE'1970-01-01'),   nvl(materiald.ctl_mod_ts,DATE'1970-01-01'),   nvl(uom_tx.ctl_mod_ts,DATE'1970-01-01'),   nvl(ap.ctl_mod_ts,DATE'1970-01-01')) as ctl_mod_ts
from
  tlgnc_load load
    join tlgns_sap_material_description materiald
          on (materiald.sap_material_id = load.sap_material_id)
    join tlgnr_uom_tx uom_tx
          on (load.net_quantity_uom_cd = uom_tx.uom_cd and materiald.lang_cd = uom_tx.lang_cd)
    join tlgnr_uom_bs uom
         on (load.net_quantity_uom_cd = uom.uom_cd)
    left join tlgnc_assignment_part ap
        on (ap.load_id = load.load_id)
    left join tlgnc_assignment assignment
        on (assignment.assignment_id = ap.assignment_id)
    left join tlgnr_assign_status_tx ast
        on (assignment.assign_status_cd = ast.assign_status_cd and materiald.lang_cd = ast.lang_cd)
where load.loading_ts is not null and load.unloading_ts is null;

COMMENT ON TABLE vlgnc_dispo_truck_load_list is 'List of loads of trucks on dispo screen';

REM END vlgnc_dispo_truck_load_list

-------------------------------------------------------------------------------

PROMPT  vlgnc_dispo_truck_load_list_v2

CREATE OR REPLACE FORCE VIEW vlgnc_dispo_truck_load_list_v2 as
select /*+ USE_NL(load, materiald, uom_tx, uom, ap, assignment, ast) INDEX (ap I_ASSIGNMENT_PART_7) */
    load.load_id,
  load.truck_id,
  materiald.lang_cd,
    load.tare_weight_t as tare_quantity,
    load.net_quantity,
    load.net_quantity_uom_cd,
    load.sap_material_id,
    load.production_plant_cd,
    load.loading_ts,
    materiald.txt as material_description,
    ap.planned_start as load_time,
    ap.assignment_id,
    uom_tx.txt as uom_txt,
    uom.uom_dimension_cd,
    uom.numerator_for_conversion,
    uom.denominator_for_conversion,
    greatest(nvl(load.ctl_mod_ts,DATE'1970-01-01'),   nvl(materiald.ctl_mod_ts,DATE'1970-01-01'),   nvl(uom_tx.ctl_mod_ts,DATE'1970-01-01'),   nvl(ap.ctl_mod_ts,DATE'1970-01-01')) as ctl_mod_ts
from
  tlgnc_load load
    join tlgns_sap_material_description materiald
          on (materiald.sap_material_id = load.sap_material_id)
    join tlgnr_uom_tx uom_tx
          on (load.net_quantity_uom_cd = uom_tx.uom_cd and materiald.lang_cd = uom_tx.lang_cd)
    join tlgnr_uom_bs uom
         on (load.net_quantity_uom_cd = uom.uom_cd)
    left join tlgnc_assignment_part ap
        on (ap.load_id = load.load_id)
where load.loading_ts is not null and load.unloading_ts is null;

COMMENT ON TABLE vlgnc_dispo_truck_load_list_v2 is 'List of loads of trucks on dispo screen v2';

REM END vlgnc_dispo_truck_load_list_v2

-------------------------------------------------------------------------------

PROMPT  vlgnc_dispo_driver_break_list
CREATE OR REPLACE FORCE VIEW vlgnc_dispo_driver_break_list as
select
    driver_break_id,
    driver_id,
    start_dat,
    end_dat,
    description,
    is_lunch_break,
    is_mop_break,
    ctl_mod_ts
from
  tlgnc_driver_break
where active_flag = 1;

COMMENT ON TABLE vlgnc_dispo_driver_break_list is 'Simplified driver break list for dispo';

REM END vlgnc_dispo_driver_break_list

-------------------------------------------------------------------------------

PROMPT  vlgnc_dispo_wagon_load_list
CREATE OR REPLACE FORCE VIEW vlgnc_dispo_wagon_load_list as
select
    load.load_id,
  load.wagon_id,
  load.container_id,
  materiald.lang_cd,
    load.tare_weight_t as tare_quantity,
    load.net_quantity,
    load.net_quantity_uom_cd,
    load.sap_material_id,
    load.production_plant_cd,
    load.loading_ts,
    materiald.txt as material_description,
    ap.planned_start as load_time,
    ap.assignment_id,
    uom_tx.txt as uom_txt,
    uom.uom_dimension_cd,
    uom.numerator_for_conversion,
    uom.denominator_for_conversion,
    ast.assign_status_cd,
    ast.txt as assign_status
from
  tlgnc_load load
    join tlgns_sap_material_description materiald
          on (materiald.sap_material_id = load.sap_material_id)
    join tlgnr_uom_tx uom_tx
          on (load.net_quantity_uom_cd = uom_tx.uom_cd and materiald.lang_cd = uom_tx.lang_cd)
    join tlgnr_uom_bs uom
         on (load.net_quantity_uom_cd = uom.uom_cd)
    left join (
        tlgnc_assignment_part ap
        join tlgnc_assignment assignment on (assignment.assignment_id = ap.assignment_id)
        join tlgnr_assign_status_tx ast on (assignment.assign_status_cd = ast.assign_status_cd)
    ) on (ap.load_id = load.load_id and materiald.lang_cd = ast.lang_cd)
where load.unloading_ts is null;

COMMENT ON TABLE vlgnc_dispo_wagon_load_list is 'List of loads of wagons on dispo screen';

REM END vlgnc_dispo_wagon_load_list

--------------------------------------------------------------------------------------------------------------------------------------------------------------

PROMPT  vlgnc_driver_without_session

CREATE OR REPLACE FORCE VIEW vlgnc_driver_without_session as
  SELECT
    driver.driver_id,
    driver.current_truck_id,
    pers.name
      || ', '
      || pers.firstname AS name,
    usr.sap_vendor_id,
    c.carrier_id
  FROM
    (
      SELECT *
      FROM tlgnc_driver d
      WHERE NOT EXISTS (
        SELECT *
        FROM tlgnc_pda_session ps
        WHERE ps.pda_session_status_cd IN(1,2)
        AND ps.user_id = d.user_id
      )
    ) driver
    INNER JOIN tlgnu_user usr ON (driver.user_id = usr.user_id)
    INNER JOIN tlgns_sap_vendor v ON (usr.sap_vendor_id = v.sap_vendor_id)
    INNER JOIN tlgnc_carrier c ON (c.sap_vendor_id = v.sap_vendor_id)
    INNER JOIN tlgnc_person pers ON (usr.person_id = pers.person_id)
  WHERE
    driver.active_flag = 1
    AND c.active_flag = 1;

COMMENT ON TABLE vlgnc_driver_without_session is 'List of drivers who does not have an active session';

REM END vlgnc_driver_without_session
--------------------------------------------------------------------------------------------------------------------------------------------------------------

PROMPT  vlgnc_dispo_message_list
CREATE OR REPLACE FORCE VIEW vlgnc_dispo_message_list as
select
    msg.PDA_MESSAGE_ID,
  drv.driver_id,
    msg.PDA_USER_ID,
    msg.DISPATCHER_USER_ID,
    msg.MESSAGE,
    msg.PDA_MESSAGE_TYPE_CD,
    msg.RECEIVER_ACK_FLAG,
    msg.CTL_CRE_TS as cre_ts,
    msg.CTL_MOD_TS as mod_ts,
    greatest (nvl(msg.CTL_MOD_TS,DATE'1970-01-01'), nvl(prs.CTL_MOD_TS,DATE'1970-01-01')) as ctl_mod_ts,
    prs.name as dispatcher_name,
    prs.firstname as dispatcher_firstname
from tlgnc_pda_message msg
join tlgnc_driver drv on (msg.pda_user_id = drv.user_id)
left join (tlgnu_user usr
  join  tlgnc_person prs on (prs.person_id = usr.person_id)
)on  (msg.DISPATCHER_USER_ID = usr.user_id);

COMMENT ON TABLE vlgnc_dispo_message_list is 'list of pda messages on dispo screen';

REM END vlgnc_dispo_message_list

-------------------------------------------------------------------------------

prompt vlgnc_dispo_assign_truck
create or replace force view vlgnc_dispo_assign_truck as
select
  ass.assignment_id,
  ass.truck_id,
  ass.assignment_dat,
  ass.min_start,
  ass.max_end
from tlgnc_assignment ass
where ass.active_flag = 1;

comment on table vlgnc_dispo_assign_truck is 'This view is used to query assignment ids based on truck ids. These are the so-called authorized assignments (DispoServiceBean.getAssignmentList).';

rem end vlgnc_dispo_assign_truck

-------------------------------------------------------------------------------

prompt vlgnc_dispo_assign_pool_carr
create or replace force view vlgnc_dispo_assign_pool_carr as
select
  assignment.assignment_id,
  ord.pool_id,
  ord.comm_carrier_id,
  ord.exec_carrier_id,
  ord.order_status_cd,
  assignment.truck_id,
  assignment.planning_truck_id,
  assignment.assignment_dat,
  assignment.min_start,
  assignment.max_end
from tlgnc_assignment_part assignment_part
  inner join tlgnc_assignment assignment on (assignment_part.assignment_id = assignment.assignment_id)
  inner join tlgnc_order ord on (assignment_part.order_id = ord.order_id)
where assignment.active_flag = 1;

comment on table vlgnc_dispo_assign_pool_carr is 'This view is used to query assignment ids based on pool and commercial or executing carrier. These are the so-called unauthorized assignments (DispoServiceBean.getAssignmentList).';

rem end vlgnc_dispo_assign_pool_carr

-------------------------------------------------------------------------------

prompt  vlgnc_dispo_assignment_list
create or replace force view vlgnc_dispo_assignment_list as
select distinct
   ass.assignment_id,
   ass.truck_id,
   ass.material_name,
   ass.material_uom_cd as material_uom,
   ass.material_quantity,
   ass.assignment_dat,
   ass.assign_status_cd,
   ass.assign_tr_status_cd,
   ass.assignment_type_cd,
   ass.load_order_id,
   ass.carrier_truck_driver_note,
   ass.carrier_dispatching_note,
   auomt.lang_cd as lang_cd,
   carrier.bag_stock_shippingpoint_id,
   truck.sap_vehicle_char_cd,
   non_archived_order.order_number as order_number,
   cast(coalesce(trailer.nr_chambers, truck.nr_chambers, 1) as number(12)) as nr_chambers
from
   tlgnc_assignment ass
   inner join tlgnc_assignment_part unload_assignment_part on ass.assignment_id = unload_assignment_part.assignment_id and
   													  unload_assignment_part.assignment_part_type_cd = 3
   left join tlgnc_order non_archived_order on unload_assignment_part.order_id = non_archived_order.order_id
   left join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (non_archived_order.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD)
   left join tlgnr_uom_tx auomt on (ass.material_uom_cd = auomt.uom_cd)
   join  tlgnc_truck truck on (ass.truck_id = truck.truck_id)
   left join tlgnc_trailer trailer on (truck.truck_id = trailer.current_truck_id)
   left join tlgnc_carrier carrier on carrier.carrier_id=truck.carrier_id
   where ass.active_flag = 1 and ass.assign_status_cd <> 8 and (archivingstatus.DISPLAY_FLAG is null or archivingstatus.DISPLAY_FLAG = 1);

comment on table vlgnc_dispo_assignment_list is 'assignment list for dispo screen - The dispo_assignment_list was delivering the pool info too, but in the code the filtering for the pool has been removed. Since the truck_pool_id was not used at all in this view, it has been removed. The assingment list is delivered records unique by truck_id.';

rem end vlgnc_dispo_assignment_list

-------------------------------------------------------------------------------

PROMPT  VLGNC_TRUCK_LAST_KNOWN_COORD

CREATE OR REPLACE FORCE VIEW VLGNC_TRUCK_LAST_KNOWN_COORD AS
SELECT DISTINCT BIZ.TRUCK_ID,
  BIZ.COORD_LAT AS LAST_COORD_LAT,
  BIZ.COORD_LON AS LAST_COORD_LON
FROM TLGNC_BIZEVENT BIZ
JOIN
  (
  SELECT TRUCK_ID, MAX(CTL_CRE_TS) AS CTL_CRE_TS
  FROM TLGNC_BIZEVENT INNERB
  WHERE INNERB.COORD_LAT IS NOT NULL AND INNERB.COORD_LON IS NOT NULL
  GROUP BY TRUCK_ID
  ) LASTRECORD ON LASTRECORD.TRUCK_ID = BIZ.TRUCK_ID AND LASTRECORD.CTL_CRE_TS = BIZ.CTL_CRE_TS;

COMMENT ON TABLE VLGNC_TRUCK_LAST_KNOWN_COORD IS 'The last known coordinate of trucks queried from TLGNC_BIZEVENT';

REM END VLGNC_TRUCK_LAST_KNOWN_COORD

-------------------------------------------------------------------------------

prompt  vlgnc_dispo_rail_ass_list
create or replace force view vlgnc_dispo_rail_ass_list as
select distinct
  ass.assignment_id,
  ass.assignment_dat,
  ass.assign_status_cd,
  ass.assign_tr_status_cd,
  ass.assignment_type_cd,
  load.container_id,
  ass.carrier_dispatching_note,
  ass.wagon_id,
  ass_part_drive_to.shippingpoint_id,
  sp.railway_station_id
from
  tlgnc_assignment ass
  join tlgnc_assignment_part ass_part_drive_to on (ass_part_drive_to.assignment_id = ass.assignment_id and ass_part_drive_to.assignment_part_type_cd = 2)
  left join (tlgnc_assignment_part ass_part
    join tlgnc_load load on (ass_part.load_id = load.load_id)
  )on (ass_part.assignment_id = ass.assignment_id and ass_part.assignment_part_type_cd = 1)
  join tlgnc_shippingpoint sp on (ass_part_drive_to.shippingpoint_id = sp.shippingpoint_id)
  where
    ass.active_flag = 1 and
    ass.assign_status_cd < 6;

comment on table vlgnc_dispo_rail_ass_list is 'assignment list for rail dispo screen';

rem end vlgnc_dispo_rail_ass_list
-------------------------------------------------------------------------------
prompt  vlgnc_dispo_assign_part_list

rem 2013-Jan-21/awe: note on left joins on sttx, osbp, ocbp, ccar:
rem logically these are normal joins with ord, but since ord is joined with a left join
rem these joins also have to be left joins
rem also note that ccar should never be null for assigned orders

create or replace force view vlgnc_dispo_assign_part_list as
select /*+ ordered use_nl(part sp st sbp ord osbp sttx ocbp)*/
  part.assignment_part_id,
  part.assignment_id,
  part.order_id,
  part.ext_order_id,
  custorder.custorder_number,
  part.assignment_part_seq_no,
  part.assignment_part_type_cd,
  part.lnf_shipto_flag,
  ord.pool_id,
  ord.comm_carrier_id,
  ord.exec_carrier_id,
  ord.order_number,
  to_char(ord.order_number) as order_number_in_str,
  ord.order_status_cd,
  ord.on_hold_order_and_locked_flag,
  sttx.txt as order_status_txt,
  ord.delivery_from_dat,
  ord.delivery_to_dat,
  ord.note_dispatching,
  ord.note_truck_driver,
  ord.note_delivery,
  ord.note_railway_disp,
  ord.cust_weborder_note,
  ord.distribution_dest_cd,
  ord.etran_outbound,
  ord.etran_return,
  ord.carrier_delivery_note,
  part.transport_distance_m,
  ord.delivery_type_cd,
  osbp.name1 as order_shipto_name,
  ocbp.sap_id as order_soldto_number,
  ocbp.name1 as order_soldto_name,
  ocbp.name2 as order_soldto_name2,
  part.planned_start,
  part.real_start,
  part.assign_part_status_cd,
  part.shipto_id,
  sbp.sap_id as shipto_sap_id,
  sbp.sap_bp_id as shipto_sap_bp_id,
  sbp.name1 as shipto_name,
  sbp.name2 as shipto_name2,
  sbp.city as shipto_city,
  sp.shippingpoint_name,
  sp.plant_cd,
  part.shippingpoint_id,
  sp.coord_lat as sp_coord_lat,
  sp.coord_lon as sp_coord_lon,
  sp.rail_arrival_warning_min,
  st.coord_lat as st_coord_lat,
  st.coord_lon as st_coord_lon,
  st.shipto_sap_bp_id as st_sap_bp_id,
  part.coord_lat,
  part.coord_lon,
  part.site_name,
  part.site_city,
  sttx.lang_cd,
  ccar.carrier_requires_planning_flag,
  ccar.display_name as comm_carrier_name
from
  tlgnc_assignment_part part
  left join tlgnc_order ord on (part.order_id = ord.order_id)
  left join tlgnr_order_status_tx sttx on (sttx.order_status_cd = ord.order_status_cd)
  left join tlgns_sap_bp osbp on (ord.shipto_sap_bp_id = osbp.sap_bp_id)
  left join tlgns_sap_bp ocbp on (ord.soldto_sap_bp_id = ocbp.sap_bp_id)
  left join tlgnc_carrier ccar on ( ccar.carrier_id = ord.comm_carrier_id)
  left join tlgnc_carrier ecar on ( ecar.carrier_id = ord.exec_carrier_id)
   left join tlgnc_shippingpoint sp on
    (part.shippingpoint_id = sp.shippingpoint_id)
  left join tlgnc_shipto st on
    (part.shipto_id = st.shipto_id)
  left join tlgns_sap_bp sbp on
    (st.shipto_sap_bp_id = sbp.sap_bp_id)
  left join tlgnc_custorder custorder on custorder.custorder_id = ord.custorder_id
ORDER BY part.assignment_id, part.assignment_part_seq_no;

comment on table vlgnc_dispo_assign_part_list is 'assignment item list for dispo screen';

rem end vlgnc_dispo_assign_part_list

-------------------------------------------------------------------------------
PROMPT  VLGNC_DISPO_WAGON_APART_LIST

CREATE OR REPLACE FORCE VIEW "ULGN01"."VLGNC_DISPO_WAGON_APART_LIST"  as
  select /*+ ordered use_nl(part sp st sbp ord osbp sttx ocbp)*/
  part.assignment_part_id,
  part.assignment_id,
  part.order_id,
  part.ext_order_id,
  custorder.custorder_number,
  part.assignment_part_seq_no,
  part.assignment_part_type_cd,
  part.lnf_shipto_flag,
  ord.pool_id,
  ord.comm_carrier_id,
  ord.exec_carrier_id,
  ord.order_number,
  to_char(ord.order_number) as order_number_s,
  ord.order_status_cd,
  ord.on_hold_order_and_locked_flag,
  sttx.txt as order_status_txt,
  ord.delivery_from_dat,
  ord.delivery_to_dat,
  ord.note_dispatching,
  ord.note_truck_driver,
  ord.note_delivery,
  ord.note_railway_disp,
  ord.cust_weborder_note,
  ord.carrier_delivery_note,
  part.transport_distance_m,
  ord.delivery_type_cd,
  osbp.name1 as order_shipto_name,
  ocbp.name1 as order_soldto_name,
  part.planned_start,
  part.real_start,
  part.assign_part_status_cd,
  part.shipto_id,
  sbp.name1 as shipto_name,
  sbp.name2 as shipto_name2,
  sbp.city as shipto_city,
  sp.shippingpoint_name,
  sp.plant_cd,
  part.shippingpoint_id,
  sp.coord_lat as sp_coord_lat,
  sp.coord_lon as sp_coord_lon,
  sp.rail_arrival_warning_min,
  st.coord_lat as st_coord_lat,
  st.coord_lon as st_coord_lon,
  st.shipto_sap_bp_id as st_sap_bp_id,
  part.coord_lat,
  part.coord_lon,
  part.site_name,
  part.site_city,
  sttx.lang_cd,
  ccar.carrier_requires_planning_flag,
  ccar.display_name as comm_carrier_name,
  rwstation.railway_station_no as  ShipToStationNo,
  rwstation.railway_station_name as ShipToStationName,
  rwstation.uic_country_cd as ShipToCountryCd,
  rwstationsp.railway_station_no as ShippingPtStationNo,
  rwstationsp.railway_station_name as ShippingPtStationName,
  rwstationsp.uic_country_cd as ShippingPtCountryCd
from
  tlgnc_assignment_part part
  left join tlgnc_order ord on (part.order_id = ord.order_id)
  left join tlgnr_order_status_tx sttx on (sttx.order_status_cd = ord.order_status_cd)
  left join tlgns_sap_bp osbp on (ord.shipto_sap_bp_id = osbp.sap_bp_id)
  left join tlgns_sap_bp ocbp on (ord.soldto_sap_bp_id = ocbp.sap_bp_id)
  left join tlgnc_carrier ccar on ( ccar.carrier_id = ord.comm_carrier_id)
  left join tlgnc_carrier ecar on ( ecar.carrier_id = ord.exec_carrier_id)
   left join tlgnc_shippingpoint sp on
    (part.shippingpoint_id = sp.shippingpoint_id)
    left join tlgnc_railway_station rwstationsp on sp.railway_station_id = rwstationsp.railway_station_id
  left join tlgnc_shipto st on
    (part.shipto_id = st.shipto_id)
  left join tlgns_sap_bp sbp on
    (st.shipto_sap_bp_id = sbp.sap_bp_id)
  left join tlgnc_custorder custorder on custorder.custorder_id = ord.custorder_id
  left join tlgnc_railway_station rwstation on sbp.railway_station_id = rwstation.railway_station_id
ORDER BY part.assignment_id, part.assignment_part_seq_no;

comment on table vlgnc_dispo_wagon_apart_list is 'assignment item list for wagon dispo screen';

rem end vlgnc_dispo_wagon_apart_list
-------------------------------------------------------------------------------
PROMPT  VLGNC_ORDER_MAT_POINTS

CREATE OR REPLACE FORCE VIEW VLGNC_ORDER_MAT_POINTS AS
SELECT
distinct
(material_restriction.SAP_MATERIAL_ID || '_' || shippingpoint.shippingpoint_id || '_' || loadingpoint.loadingpoint_id) as COMPOSITE_ID,
 material_restriction.SAP_MATERIAL_ID,
 plant_bs.plant_name,
 shippingpoint.plant_cd,
 shippingpoint.shippingpoint_id,
 shippingpoint.shippingpoint_name,
 shippingpoint.shipppoint_type_cd,
 shippingpoint.railway_station_id,
 shippingpoint.coord_lat,
 shippingpoint.coord_lon,
 loadingpoint.loadingpoint_id,
 loadingpoint.loadingpoint_name,
 package_type_cd
FROM
  TLGNC_SHIPPINGPOINT shippingpoint
  inner join tlgnr_plant_bs plant_bs on (plant_bs.plant_cd=shippingpoint.plant_cd and plant_bs.ctl_act_cd=1)
  left join TLGNC_LOADINGPOINT loadingpoint on (loadingpoint.shippingpoint_id = shippingpoint.shippingpoint_id and loadingpoint.ACTIVE_FLAG = 1)
  left join TLGNC_LDPOINT_MAT_RESTR material_restriction on (material_restriction.loadingpoint_id=loadingpoint.loadingpoint_id);

COMMENT ON TABLE VLGNC_ORDER_MAT_POINTS is 'On order taking the view is used to filter loadingpoints by material restrictions applied on master data shippingpoint/loadingpoint.';

REM END VLGNC_ORDER_MAT_POINTS

-------------------------------------------------------------------------------
PROMPT VLGNS_SAP_BP_STAGING_AREA

create or replace view VLGNS_SAP_BP_STAGING_AREA as
select req.hjid,
  req.sapimpimporteddatelement,
  req.sapimpstatuscd,
  status.txt sapimpstatus,
  req.sapimperrmsg,
  gen.msgfn,
  gen.kunnr,
  gen.name1,
  gen.ort01,
  (select count(*)
  from ulgnsap.bpn_businesspartnernotificat_0 req1
  inner join ulgnsap.BPN_MASTERCUSTOMERBASICDATA gen1 ON(req1.MASTERCUSTOMERBASICDATA_BPN__0  = gen1.hjid)
  inner join ulgnsap.BPN_MASTERCUSTOMERBASICDATA__1 rel1 ON(rel1.PARENT_BPN_MASTERCUSTOMERBAS_0 = gen1.hjid)
  where req1.hjid = req.hjid) as SAREA,
  (select count(*)
  from ulgnsap.bpn_businesspartnernotificat_0 notif
  inner join ulgnsap.bpn_mastercustomerbasicdata gen2 ON (notif.mastercustomerbasicdata_bpn__0 = gen2.hjid)
  inner join ulgnsap.bpn_mastercustomerbasicdata__3 master_compcd ON (gen2.hjid = master_compcd.parent_bpn_mastercustomerbas_0)
  where notif.hjid = req.hjid) as CC
from ulgnsap.bpn_businesspartnernotificat_0 req
inner join ulgnsap.TLGNR_SAPIMPSTATUS_TX status ON (req.sapimpstatuscd = status.sapimpstatus_cd AND status.lang_cd = 'en')
inner join ulgnsap.BPN_MASTERCUSTOMERBASICDATA gen ON (req.MASTERCUSTOMERBASICDATA_BPN__0  = gen.hjid)
where req.sapimpstatuscd IN (2, 3, 8, 9, 12, 19);

COMMENT ON TABLE VLGNS_SAP_BP_STAGING_AREA is 'Utility view: BP staging area info made available in table browser';

REM END VLGNS_SAP_BP_STAGING_AREA;

-------------------------------------------------------------------------------
PROMPT VLGNS_SAP_VENDOR_STAGING_AREA

create or replace view VLGNS_SAP_VENDOR_STAGING_AREA as
select req.hjid,
  req.sapimpimporteddatelement,
  req.sapimpstatuscd,
  status.txt sapimpstatus,
  req.sapimperrmsg,
  gen.msgfn,
  gen.lifnr,
  gen.name1,
  gen.ort01,
  (select count(*)
  from ulgnsap.VN_VENDORNOTIFICATION req1
  inner join ulgnsap.VN_VENDORGENERALDATA gen1 ON(req1.VENDORGENERALDATA_VN_VENDORG_0 = gen1.hjid)
  inner join ulgnsap.VN_VENDORGENERALDATA_VENDORP_0 rel1 ON(rel1.PARENT_VN_VENDORGENERALDATA__0 = gen1.hjid)
  where req1.hjid = req.hjid) as PORG
from ulgnsap.VN_VENDORNOTIFICATION req
inner join ulgnsap.TLGNR_SAPIMPSTATUS_TX status ON (req.sapimpstatuscd = status.sapimpstatus_cd AND status.lang_cd = 'en')
inner join ulgnsap.VN_VENDORGENERALDATA gen ON (req.VENDORGENERALDATA_VN_VENDORG_0 = gen.hjid)
where req.sapimpstatuscd IN (2, 3, 8, 9, 12, 19);

COMMENT ON TABLE VLGNS_SAP_VENDOR_STAGING_AREA is 'Utility view: Vendor staging area info made available in table browser';

REM END VLGNS_SAP_VENDOR_STAGING_AREA;

-------------------------------------------------------------------------------
PROMPT VLGNS_SAP_MAT_STAGING_AREA

create or replace view VLGNS_SAP_MAT_STAGING_AREA as
select req.hjid,
  req.sapimpimporteddatelement,
  req.sapimpstatuscd,
  status.txt sapimpstatus,
  req.sapimperrmsg,
  gen.msgfn,
  gen.matnr,
  gen.normt,
  (select count(*)
  from ulgnsap.MN_MATERIALNOTIFICATION req1
  inner join ulgnsap.MN_MATERIALNOTIFICATION_MATE_0 rel1 ON (req1.hjid  = rel1.parent_mn_materialnotificati_0)
  inner join ulgnsap.MN_MATERIALGENERALDATA_MATER_2 rel2 ON (rel1.child_mn_materialgeneraldata_0 = rel2.parent_mn_materialgeneraldat_0)
  where req1.hjid = req.hjid) as PLANT,
  (select count(*)
  from ulgnsap.MN_MATERIALNOTIFICATION req1
  inner join ulgnsap.MN_MATERIALNOTIFICATION_MATE_0 rel1 ON (req1.hjid  = rel1.parent_mn_materialnotificati_0)
  inner join ulgnsap.MN_MATERIALGENERALDATA_MATER_6 rel2 ON (rel1.child_mn_materialgeneraldata_0 = rel2.parent_mn_materialgeneraldat_0)
  where req1.hjid = req.hjid) as SAREA,
  (select count(*)
  from ulgnsap.MN_MATERIALNOTIFICATION req1
  inner join ulgnsap.MN_MATERIALNOTIFICATION_MATE_0 rel1 ON (req1.hjid  = rel1.parent_mn_materialnotificati_0)
  inner join ulgnsap.MN_MATERIALGENERALDATA_MATER_3 rel2 ON (rel1.child_mn_materialgeneraldata_0 = rel2.parent_mn_materialgeneraldat_0)
  where req1.hjid = req.hjid) as ALT,
  (select count(*)
  from ulgnsap.MN_MATERIALNOTIFICATION req1
  inner join ulgnsap.MN_MATERIALNOTIFICATION_MATE_0 rel1 ON (req1.hjid  = rel1.parent_mn_materialnotificati_0)
  inner join ulgnsap.MN_MATERIALGENERALDATA_MATER_1 rel2 ON (rel1.child_mn_materialgeneraldata_0 = rel2.parent_mn_materialgeneraldat_0)
  where req1.hjid = req.hjid) as DSC
from ulgnsap.MN_MATERIALNOTIFICATION req
inner join ulgnsap.TLGNR_SAPIMPSTATUS_TX status ON (req.sapimpstatuscd = status.sapimpstatus_cd AND status.lang_cd = 'en')
inner join ulgnsap.MN_MATERIALNOTIFICATION_MATE_0 rel ON (req.hjid = rel.parent_mn_materialnotificati_0)
inner join ulgnsap.MN_MATERIALGENERALDATA gen ON (rel.child_mn_materialgeneraldata_0 = gen.hjid)
where req.sapimpstatuscd IN (2, 3, 8, 9);

COMMENT ON TABLE VLGNS_SAP_MAT_STAGING_AREA is 'Utility view: Material staging area info made available in table browser';

REM END VLGNS_SAP_MAT_STAGING_AREA;

-------------------------------------------------------------------------------
PROMPT  VLGNS_SAP_MATERIAL_PLANT_DESC

CREATE OR REPLACE FORCE VIEW VLGNS_SAP_MATERIAL_PLANT_DESC AS
SELECT pla.sap_material_plant_id || '_' || des.lang_cd id,
  pla.sap_material_id,
  pla.plant_cd,
  plantbs.plant_name,
  des.lang_cd,
  des.txt,
  des.txt
  || ' ('
  || mat.sap_material_no
  || ')' display,
  mat.material_package_cd,
  mat.base_uom_cd,
  pkg.logon_package_type_cd
FROM tlgns_sap_material_plant pla
INNER JOIN tlgns_sap_material_description des ON(pla.sap_material_id = des.sap_material_id)
INNER JOIN tlgnr_plant_bs plantbs ON (pla.plant_cd = plantbs.plant_cd)
INNER JOIN tlgns_sap_material mat ON(pla.sap_material_id = mat.sap_material_id)
INNER JOIN tlgnr_material_package_bs pkg ON(mat.material_package_cd = pkg.material_package_cd);

COMMENT ON TABLE VLGNS_SAP_MATERIAL_PLANT_DESC is 'TODO author of this view: describe purpose and usage of view';

REM END VLGNS_SAP_MATERIAL_PLANT_DESC

-------------------------------------------------------------------------------
PROMPT  VLGNS_SAP_MATERIAL_PLANT_SAREA

CREATE OR REPLACE VIEW VLGNS_SAP_MATERIAL_PLANT_SAREA as
  SELECT pla.sap_material_plant_id || '_' || plantsarea.plant_sarea_id AS composite_id,
    /** START: Backward compatibility columns */
    mat.material_package_cd,
    mat.base_uom_cd,
    /** START: Backward compatibility columns */
    pla.sap_material_id,
    pla.plant_cd,
    plantbs.plant_name,
    plantbs.production_plant,
    plantsarea.sarea_id
  FROM tlgns_sap_material_plant pla
  INNER JOIN tlgnr_plant_bs plantbs ON (pla.plant_cd = plantbs.plant_cd)
  INNER JOIN tlgnc_plant_sarea plantsarea ON (plantsarea.plant_cd = plantbs.plant_cd)
  INNER JOIN tlgns_sap_material mat ON(pla.sap_material_id   = mat.sap_material_id)
  WHERE plantbs.ctl_act_cd = 1 AND pla.marked_for_deletion <> 1;

COMMENT ON TABLE VLGNS_SAP_MATERIAL_PLANT_SAREA is 'View containing all sap material plant entries basically with the connected sales area table in order to be able to filter for the sales area id and therefore only consider those, which are valid in the sales area.Used by supply and production plant dropdowns on order taking sources, and validation.';

REM END VLGNS_SAP_MATERIAL_PLANT_SAREA

-------------------------------------------------------------------------------
PROMPT  VLGNS_UTIL_SAPMATDESC_MISSING

CREATE OR REPLACE FORCE VIEW VLGNS_UTIL_SAPMATDESC_MISSING as
select
  incomplete.sap_material_id,
  mat.sap_material_no,
  cast(incomplete.num_lang_in_sap_material_desc as number(10,0)) as num_lang_in_sap_material_desc,
  mat.material_group_desc,
  mat.product_hierarchy_desc /* Field no longer used by application, but OK to display it in a utility view*/,
  mat.industry_standard_desc,
  mat.material_package_cd
from (
  select
    sap_material_id as sap_material_id ,
    count(*)        as num_lang_in_sap_material_desc
  from tlgns_sap_material_description
  group by sap_material_id) incomplete
  inner join tlgns_sap_material mat on (mat.sap_material_id = incomplete.sap_material_id)
where incomplete.num_lang_in_sap_material_desc < (select count(*) from tlgnr_lang_bs);

COMMENT ON TABLE VLGNS_UTIL_SAPMATDESC_MISSING is 'Utility/Support view to show SAP materials with incomplete (not in all languages) descriptions. View is not used by the application itself.';
REM END VLGNS_UTIL_SAPMATDESC_MISSING

-------------------------------------------------------------------------------
PROMPT  VLGNC_MAT_PLANT_MISSING
CREATE OR REPLACE FORCE VIEW VLGNC_MAT_PLANT_MISSING as
  SELECT
    smp.sap_material_id,
    smp.plant_cd,
    des.lang_cd,
    des.txt,
    des.txt || ' (' || mat.sap_material_no || ')' AS DISPLAY
  FROM tlgns_sap_material_plant smp
  LEFT OUTER JOIN tlgnc_mat_plant mp ON( smp.sap_material_id = mp.sap_material_id AND smp.plant_cd = mp.plant_cd)
  INNER JOIN tlgns_sap_material mat ON(mat.sap_material_id = smp.sap_material_id)
  INNER JOIN tlgns_sap_material_description des ON(des.sap_material_id = mat.sap_material_id)
  WHERE mp.sap_material_id is NULL AND mp.plant_cd is NULL;

COMMENT ON TABLE VLGNC_MAT_PLANT_MISSING is 'View to show not yet assigned TLGNS_SAP_MATERIAL entries which are contined in TLGNS_SAP_MATERIAL_PLANT but not in TLGNC_MAT_PLANT.';
REM END VLGNC_MAT_PLANT_MISSING

-------------------------------------------------------------------------------
PROMPT  VLGNS_SAP_MAT_PLANT_NORM_DESC

CREATE OR REPLACE FORCE VIEW VLGNS_SAP_MAT_PLANT_NORM_DESC as
SELECT
  smn.sap_mat_plant_norm_id,
  smn.plant_cd,
  des.txt,
  des.lang_cd,
  mat.sap_material_no,
  mat.sap_material_id,
  smn.sap_material_norm_cd,
  smn.sap_language_cd,
  smn.sorg_cd,
  smn.show_logo_flag,
  smn.zcode1,
  smn.zcode2,
  smn.text1,
  smn.text2,
  smn.text3,
  smn.marked_for_deletion,
  smn.dest_country,
  smn.dn_logo_image_name,
  smn.dn_logo_archivdocid
FROM tlgns_sap_mat_plant_norm smn
INNER JOIN tlgns_sap_material mat on (mat.sap_material_id = smn.sap_material_id)
INNER JOIN tlgns_sap_material_description des on (des.sap_material_id = mat.sap_material_id);

COMMENT ON TABLE VLGNS_SAP_MAT_PLANT_NORM_DESC is 'View to show the SAP material plant norms on material enchancement tab.';
REM END VLGNS_SAP_MAT_PLANT_NORM_DESC
-------------------------------------------------------------------------------
PROMPT  VLGNS_UTIL_MATPLANT_MISSING

CREATE OR REPLACE FORCE VIEW VLGNS_UTIL_MATPLANT_MISSING as
select
  smp.sap_material_id,
  smp.plant_cd,
  smp.nhm_code,
  sm.product_hierarchy_desc,
  sm.material_group_desc,
  sm.ctl_cre_ts as sap_material_cre_ts
from
  tlgns_sap_material_plant smp
  inner join tlgns_sap_material sm on (smp.sap_material_id = sm.sap_material_id)
  inner join tlgnr_plant_bs p on (smp.plant_cd = p.plant_cd)
where
  p.production_plant = 1 /* mat_plant only needs to contain prod plants. Supply plants not needed there. */
and not exists (
  select lmp.mat_plant_id
  from tlgnc_mat_plant lmp
  where lmp.plant_cd = smp.plant_cd and lmp.sap_material_id = smp.sap_material_id
);

COMMENT ON TABLE VLGNS_UTIL_MATPLANT_MISSING is 'Utilitly/Support view to show SAP material-plant entries for production plants without an entry in tlgnc_mat_plant. Since LOGON 1.4.0 an entry in tlgnc_mat_plant is mandatory for all material-prod-plant (because of NORM_DESC, BRAND_SALES_NAME). If missing: material cannot be used by LOGON. View is not used by the application itself.';
REM END VLGNS_UTIL_MATPLANT_MISSING

-------------------------------------------------------------------------------
PROMPT  VLGNU_COLUMN_INFO

-- cast on d.char_length: is needed because Oracle would have "number" (no precision!) as
-- datatype otherwise, which would be generated into Double(!) with adnant oraviews.
CREATE OR REPLACE FORCE VIEW VLGNU_COLUMN_INFO AS
SELECT
  d.table_name || '_' || d.column_name  ID,
  d.table_name as TABLE_NAME,
  d.column_name as COLUMN_NAME,
  initcap(d.data_type) ||
      decode(d.data_type,
      'CHAR',      '('|| d.char_length ||')',
      'VARCHAR',   '('|| d.char_length ||')',
      'VARCHAR2',  '('|| d.char_length ||')',
      'NCHAR',     '('|| d.char_length ||')',
      'NVARCHAR',  '('|| d.char_length ||')',
      'NVARCHAR2', '('|| d.char_length ||')',
      'NUMBER',    '('||
          nvl(d.data_precision,d.data_length)||
          decode(d.data_scale,null,null,
              ','||d.data_scale)||')',
          null) as DATA_TYPE,
  d.nullable as NULLABLE,
  cast( d.char_length as number(10) ) as CHAR_LENGTH,
  c.comments as COMMENTS
FROM sys.all_tab_columns d,
  sys.all_col_comments c,
  sys.all_tab_comments t
WHERE UPPER(d.owner) = 'ULGN01'
  and d.owner = t.owner
  and d.owner = c.owner
  and d.table_name = t.table_name
  and d.table_name = c.table_name
  and d.column_name = c.column_name
  and d.table_name like 'TLGN%'
  and substr(d.table_name,1,4) != 'BIN$'
  and substr(d.table_name,1,3) != 'DR$'
order by d.owner, d.table_name, d.column_id;

COMMENT ON TABLE VLGNU_COLUMN_INFO is 'Column metadata (type, size, nullable) and column comments, used by Table Browser to display column info and check size constraints for text fields.';

REM END VLGNU_COLUMN_INFO

-------------------------------------------------------------------------------
PROMPT  VLGNU_TABLE_INFO

CREATE OR REPLACE FORCE VIEW VLGNU_TABLE_INFO AS
  select
    table_name as TABLE_NAME,
    table_type as TABLE_TYPE,
    comments as COMMENTS
  from sys.all_tab_comments
  where UPPER(owner) = 'ULGN01'
    and substr(table_name,1,4) != 'BIN$'
    and substr(table_name,1,3) != 'DR$'
  order by owner, table_name;

COMMENT ON TABLE VLGNU_TABLE_INFO is 'Table/View metadata (type) and comments, used by Table Browser to display table/view comments.';

REM END VLGNU_TABLE_INFO

-------------------------------------------------------------------------------
PROMPT  VLGNC_DISPO_SHIPTO_SOLDTO

CREATE OR REPLACE FORCE VIEW VLGNC_DISPO_SHIPTO_SOLDTO AS
  SELECT DISTINCT
         shipto.NAME1 AS SHIPTO_NAME,
         shipto.SAP_ID AS SHIPTO_NR,
         shipto.POSTAL_CODE,
         shipto.CITY,
         soldto.NAME1 AS SOLDTO_NAME,
         soldto.SAP_ID AS SOLDTO_NR,
         shipto_c.COORD_LAT,
         shipto_c.COORD_LON,
         shipto_c.GEOFENCE_RADIUS
    FROM
    TLGNS_SAP_BP shipto,
    TLGNS_SAP_BP soldto,
    TLGNS_SAP_BP_SAREA shipto_sarea,
    TLGNS_SAP_BP_SAREA soldto_sarea,
    TLGNS_SAP_BP_PF_SHIPTO pf_soldto_shipto,
    TLGNC_SHIPTO shipto_c
  WHERE
    shipto.BP_IS_SHIPTO=1
    AND soldto.BP_IS_SOLDTO=1
    AND shipto_sarea.SAP_BP_ID = shipto.SAP_BP_ID
    AND soldto_sarea.SAP_BP_ID = soldto.SAP_BP_ID
    AND pf_soldto_shipto.SAP_BP_SAREA_ID = soldto_sarea.SAP_BP_SAREA_ID
    AND pf_soldto_shipto.SHIPTO_SAP_BP_SAREA_ID = shipto_sarea.SAP_BP_SAREA_ID
    AND shipto_c.shipto_sap_bp_id = shipto.sap_bp_id;

COMMENT ON TABLE VLGNC_DISPO_SHIPTO_SOLDTO is 'TODO author of this view: describe purpose and usage of view';

REM END VLGNC_DISPO_SHIPTO_SOLDTO

-------------------------------------------------------------------------------
PROMPT VLGNS_SAP_MATERIAL_DESC

CREATE OR REPLACE FORCE VIEW VLGNS_SAP_MATERIAL_DESC AS
  SELECT mat.sap_material_id || '_' || des.lang_cd as ID,
  mat.sap_material_id,
  mat.sap_material_no,
  mat.base_uom_cd,
  mat.material_package_cd,
  mat.marked_for_deletion,
  des.lang_cd,
  des.txt,
  des.txt || ' (' || mat.sap_material_no || ')' as DISPLAY
FROM tlgns_sap_material mat
INNER JOIN tlgns_sap_material_description des ON(des.sap_material_id = mat.sap_material_id);

COMMENT ON TABLE VLGNS_SAP_MATERIAL_DESC is 'TODO author of this view: describe purpose and usage of view';

REM END VLGNS_SAP_MATERIAL_DESC

-------------------------------------------------------------------------------
-- View to display all Soldto's in the user admin.
PROMPT VLGNU_SAP_SOLDTO_SRCH

CREATE OR REPLACE FORCE VIEW VLGNU_SAP_SOLDTO_SRCH AS
  SELECT DISTINCT
    soldto.SAP_BP_ID AS SOLDTO_SAP_BP_ID,
    soldto.SAP_ID AS SOLDTO_SAP_ID,
    soldto.NAME1 AS SOLDTO_NAME
FROM
    TLGNS_SAP_BP soldto
where soldto.bp_is_soldto=1;

COMMENT ON TABLE VLGNU_SAP_SOLDTO_SRCH is 'All SAP Nr. and Names of the business partners (BP) which are a soldto, used in User Administration.';

REM END VLGNU_SAP_SOLDTO_SRCH

-------------------------------------------------------------------------------

PROMPT VLGNC_ORD_CHART_NOT_ASSIGNED

CREATE OR REPLACE FORCE VIEW VLGNC_ORD_CHART_NOT_ASSIGNED AS
SELECT
  ord.ORDER_ID || '_' || item.SHIPTO_SILO_ID as COMPOSITE_ID,
  ord.ORDER_ID,
  ord.SHIPTO_SAP_BP_ID,
  ord.TRANSPORT_DURATION_MIN,
  ord.DELIVERY_FROM_DAT,
  ord.DELIVERY_TO_DAT,
  ord.POOL_ID,
  ord.COMM_CARRIER_ID,
  ord.EXEC_CARRIER_ID,
  ord.PLANNED_UNLOADING_TIME_MIN,
  ord.SHIPPINGPOINT_ID,
  item.PLANNED_LOADING_TIME_MIN,
  trucktype.TRUCK_TYPE_CD,
  shiptosilo.UNLOADING_TIME as SHIPTO_SILO_UNLOADING_TIME,
  lp.LOADING_TIME as LOADING_TIME,
    shipto.OPEN1_FROM,
    shipto.OPEN1_TO,
    shipto.OPEN2_FROM,
    shipto.OPEN2_TO,
    shippingpoint.PLANT_CD,
    cast(first_value(sm.MATERIAL_PACKAGE_CD ignore nulls) over (partition by ord.order_id) as varchar2(4)) as MATERIAL_PACKAGE_CD,
    cast(first_value(sm.MATERIAL_IS_AGG ignore nulls) over (partition by ord.order_id) as number(1)) as MATERIAL_IS_AGG,
    cast(first_value(sm.MATERIAL_IS_CEM ignore nulls) over (partition by ord.order_id) as number(1)) as MATERIAL_IS_CEM,
    cast(first_value(sm.MATERIAL_IS_LF ignore nulls) over (partition by ord.order_id) as number(1)) as MATERIAL_IS_LF
from TLGNC_SHIPTO shipto
join TLGNC_ORDER ord on (shipto.SHIPTO_SAP_BP_ID = ord.SHIPTO_SAP_BP_ID and shipto.ACTIVE_FLAG = 1  and ord.transport_duration_min > 0 and cast(ord.delivery_type_cd as number(12,0)) in (1, 4) and cast(ord.order_status_cd as number(12,0)) <> 17 and cast(ord.order_status_cd as number(12,0)) <> 20)
join TLGNC_ORDER_ITEM item on (ord.order_id=item.order_id)
join tlgns_sap_material sm on (item.sap_material_id = sm.sap_material_id)
left outer join tlgnc_order_truck_type trucktype on (ord.order_id = trucktype.order_id)
left outer join TLGNC_SHIPTO_SILO shiptosilo on (shiptosilo.SHIPTO_SILO_ID = item.SHIPTO_SILO_ID)
left outer join TLGNC_SHIPPINGPOINT shippingpoint on (shippingpoint.SHIPPINGPOINT_ID = ord.SHIPPINGPOINT_ID)
left outer join TLGNC_LOADINGPOINT lp on (lp.LOADINGPOINT_ID = item.LOADINGPOINT_ID)
where
 not exists (select 1 from TLGNC_ASSIGNMENT_PART assp where assp.ORDER_ID = ord.ORDER_ID )
 group by ord.ORDER_ID, item.SHIPTO_SILO_ID, ord.SHIPTO_SAP_BP_ID, ord.TRANSPORT_DURATION_MIN, ord.DELIVERY_FROM_DAT, ord.DELIVERY_TO_DAT, ord.POOL_ID, ord.COMM_CARRIER_ID, ord.EXEC_CARRIER_ID, ord.PLANNED_UNLOADING_TIME_MIN, ord.SHIPPINGPOINT_ID, item.PLANNED_LOADING_TIME_MIN, trucktype.TRUCK_TYPE_CD, shiptosilo.UNLOADING_TIME, lp.LOADING_TIME, shipto.OPEN1_FROM, shipto.OPEN1_TO, shipto.OPEN2_FROM, shipto.OPEN2_TO, shippingpoint.PLANT_CD, sm.MATERIAL_PACKAGE_CD, sm.MATERIAL_IS_AGG, sm.MATERIAL_IS_CEM, sm.MATERIAL_IS_LF;

COMMENT ON TABLE VLGNC_ORD_CHART_NOT_ASSIGNED is 'View for non-assigned orders, used by the Truck utilization chart';

REM END VLGNC_ORD_CHART_NOT_ASSIGNED

-------------------------------------------------------------------------------

PROMPT VLGNC_CHART_LOAD_UNSCHEDULED

CREATE OR REPLACE FORCE VIEW VLGNC_CHART_LOAD_UNSCHEDULED AS
SELECT distinct
  ord.ORDER_ID || '_' || item.SHIPTO_SILO_ID as COMPOSITE_ID,
  ord.ORDER_ID,
  ord.SHIPTO_SAP_BP_ID,
  ord.TRANSPORT_DURATION_MIN,
  ord.DELIVERY_FROM_DAT,
  ord.DELIVERY_TO_DAT,
  ord.POOL_ID,
  ord.EXEC_CARRIER_ID,
  ord.PLANNED_UNLOADING_TIME_MIN,
  ord.SHIPPINGPOINT_ID,
    ord.delivery_type_cd,
  item.PLANNED_LOADING_TIME_MIN,
  trucktype.truck_type_cd,
  shiptosilo.UNLOADING_TIME as SHIPTO_SILO_UNLOADING_TIME,
  lp.LOADING_TIME as LOADING_TIME,
    shipto.OPEN1_FROM,
    shipto.OPEN1_TO,
    shipto.OPEN2_FROM,
    shipto.OPEN2_TO,
    shippingpoint.PLANT_CD,
    cast(first_value(sm.MATERIAL_PACKAGE_CD ignore nulls) over (partition by ord.order_id) as varchar2(4)) as MATERIAL_PACKAGE_CD,
    cast(first_value(sm.MATERIAL_IS_AGG ignore nulls) over (partition by ord.order_id) as number(1)) as MATERIAL_IS_AGG,
    cast(first_value(sm.MATERIAL_IS_CEM ignore nulls) over (partition by ord.order_id) as number(1)) as MATERIAL_IS_CEM,
    cast(first_value(sm.MATERIAL_IS_LF ignore nulls) over (partition by ord.order_id) as number(1)) as MATERIAL_IS_LF
from TLGNC_ORDER ord
inner join TLGNC_ORDER_ITEM item on (ord.order_id=item.order_id)
left outer join tlgnc_order_truck_type trucktype on (ord.order_id = trucktype.order_id)
left outer join tlgns_sap_material sm on (item.sap_material_id = sm.sap_material_id)
left outer join TLGNC_SHIPTO_SILO shiptosilo on (shiptosilo.SHIPTO_SILO_ID = item.SHIPTO_SILO_ID)
left outer join TLGNC_SHIPTO shipto on (shipto.SHIPTO_SAP_BP_ID = ord.SHIPTO_SAP_BP_ID and shipto.ACTIVE_FLAG = 1)
left outer join TLGNC_SHIPPINGPOINT shippingpoint on (shippingpoint.SHIPPINGPOINT_ID = ord.SHIPPINGPOINT_ID)
left outer join TLGNC_LOADINGPOINT lp on (lp.LOADINGPOINT_ID = item.LOADINGPOINT_ID)
left outer join TLGNC_ASSIGNMENT_PART assp on (assp.ORDER_ID = ord.ORDER_ID and assp.assign_part_status_cd <> 3)
left outer join TLGNC_ASSIGNMENT ass on (ass.assignment_id = assp.assignment_id and ass.active_flag = 1)
where
  ass.active_flag IS NULL and
  ord.transport_duration_min is not null and
  ord.transport_duration_min > 0 and
  ord.order_status_cd <> 17 and
  ord.order_status_cd <> 20
group by ord.ORDER_ID, item.SHIPTO_SILO_ID, ord.SHIPTO_SAP_BP_ID, ord.TRANSPORT_DURATION_MIN,
  ord.DELIVERY_FROM_DAT, ord.DELIVERY_TO_DAT, ord.POOL_ID, ord.EXEC_CARRIER_ID,
  ord.PLANNED_UNLOADING_TIME_MIN, ord.SHIPPINGPOINT_ID, ord.delivery_type_cd, item.PLANNED_LOADING_TIME_MIN, trucktype.truck_type_cd,
  shiptosilo.UNLOADING_TIME, /* no longer exists shipto.UNLOADING_TIME, */ lp.LOADING_TIME, shipto.OPEN1_FROM, shipto.OPEN1_TO,
  shipto.OPEN2_FROM, shipto.OPEN2_TO, shippingpoint.PLANT_CD, sm.MATERIAL_PACKAGE_CD, sm.MATERIAL_IS_AGG, sm.MATERIAL_IS_CEM, sm.MATERIAL_IS_LF;

COMMENT ON TABLE VLGNC_CHART_LOAD_UNSCHEDULED is 'View for unscheduled orders, used by the Loadings utilization chart';

REM END VLGNC_CHART_LOAD_UNSCHEDULED

-------------------------------------------------------------------------------

PROMPT VLGNC_CHART_LOAD_SCHEDULED

CREATE OR REPLACE FORCE VIEW VLGNC_CHART_LOAD_SCHEDULED AS
select distinct
  a.ASSIGNMENT_ID,
  a.truck_id,
  a.wagon_id,
  tr.carrier_id,
  tr.truck_type_cd,
  wg.wagon_type_cd,
  cast(first_value (item.sap_material_id ignore nulls) over (partition by ap.assignment_id) as number(12)) as sap_material_id,
  cast(first_value (ord.pool_id ignore nulls) over (partition by ap.assignment_id) as number(12)) as pool_id,
  cast(first_value (ord.delivery_type_cd ignore nulls) over (partition by ap.assignment_id) as number(2)) as delivery_type_cd,
  cast(first_value (spoint.plant_cd ignore nulls) over (partition by ap.assignment_id) as varchar2(4)) as plant_cd,
  cast(first_value (spoint.shippingpoint_id ignore nulls) over (partition by ap.assignment_id) as number(12)) as shippingpoint_id,
  cast(first_value(sm.MATERIAL_PACKAGE_CD ignore nulls) over (partition by ap.assignment_id) as varchar2(4)) as MATERIAL_PACKAGE_CD,
  cast(first_value(sm.MATERIAL_IS_AGG ignore nulls) over (partition by ap.assignment_id) as number(1)) as MATERIAL_IS_AGG,
  cast(first_value(sm.MATERIAL_IS_CEM ignore nulls) over (partition by ap.assignment_id) as number(1)) as MATERIAL_IS_CEM,
  cast(first_value(sm.MATERIAL_IS_LF ignore nulls) over (partition by ap.assignment_id) as number(1)) as MATERIAL_IS_LF,
  -- get the first 'load' planned_start date time
  cast ((select ap2.planned_start FROM TLGNC_ASSIGNMENT_PART ap2 where ap2.ASSIGNMENT_ID = a.ASSIGNMENT_ID and ap2.assignment_part_type_cd = 1 and ap2.assignment_part_id = (select min(assignment_part_id) from TLGNC_ASSIGNMENT_PART  where ASSIGNMENT_ID = a.ASSIGNMENT_ID and assignment_part_type_cd = 1)) AS DATE) first_load_dat,
  -- get the first 'to' planned_start date time
  cast ((select ap2.planned_start FROM TLGNC_ASSIGNMENT_PART ap2 where ap2.ASSIGNMENT_ID = a.ASSIGNMENT_ID and ap2.assignment_part_type_cd = 2 and ap2.assignment_part_id = (select min(assignment_part_id) from TLGNC_ASSIGNMENT_PART  where ASSIGNMENT_ID = a.ASSIGNMENT_ID and assignment_part_type_cd = 2)) AS DATE) first_to_dat
  from tlgnc_assignment a
  inner join TLGNC_ASSIGNMENT_PART ap on (ap.ASSIGNMENT_ID = a.ASSIGNMENT_ID and ap.assignment_part_type_cd in (1,3))
  left outer join tlgnc_truck tr on (a.truck_id = tr.truck_id)
  left outer join tlgnc_wagon wg on (a.wagon_id = wg.wagon_id)
  left outer join (tlgnc_order ord
    join tlgnc_order_item item on (ord.order_id = item.order_id)
    join tlgnc_shippingpoint spoint on (ord.shippingpoint_id = spoint.shippingpoint_id)
    join tlgns_sap_material sm on (item.sap_material_id = sm.sap_material_id)
  ) on (ap.order_id = ord.order_id);

COMMENT ON TABLE VLGNC_CHART_LOAD_UNSCHEDULED is 'View for scheduled orders, used by the Loadings utilization chart';

REM END VLGNC_CHART_LOAD_SCHEDULED

-------------------------------------------------------------------------------

PROMPT VLGNC_TRUCK_CHART_ASSIGNED

CREATE OR REPLACE FORCE VIEW VLGNC_TRUCK_CHART_ASSIGNED AS
select distinct
  a.assignment_id,
  t.truck_type_cd,
  pc.pool_id,
  pc.carrier_id,
  a.assignment_dat,
  shippingpoint.plant_cd,
  shippingpoint.shippingpoint_id,
  cast(first_value(ap.order_id ignore nulls) over (partition by a.assignment_id ) as number(12)) as order_id,
  min(ap.planned_start) over (partition by a.assignment_id ) as from_dat,
  max(ap.planned_start) over (partition by a.assignment_id ) as to_dat,
  cast(first_value(sm.material_package_cd ignore nulls) over (partition by a.assignment_id) as varchar2(4)) as material_package_cd,
  cast(first_value(sm.material_is_lf ignore nulls) over (partition by a.assignment_id) as number(1)) as material_is_lf,
  cast(first_value(sm.material_is_cem ignore nulls) over (partition by a.assignment_id)  as number(1)) as material_is_cem,
  cast(first_value(sm.material_is_agg ignore nulls) over (partition by a.assignment_id) as number(1))  as material_is_agg
from tlgnc_truck t
  inner join tlgnc_pool_carrier pc on (t.carrier_id = pc.carrier_id)
  inner join tlgnc_assignment a on (a.truck_id = t.truck_id and a.active_flag = 1 and a.assign_status_cd not in (6,7) and a.assignment_type_cd=0)
  inner join tlgnc_assignment_part ap on (a.assignment_id = ap.assignment_id)
  inner join tlgnc_order o on (o.order_id = ap.order_id)
  inner join tlgnc_order_item oi on (o.order_id = oi.order_id)
  inner join tlgns_sap_material sm on (oi.sap_material_id = sm.sap_material_id)
  inner join tlgnc_shippingpoint shippingpoint on (o.shippingpoint_id = shippingpoint.shippingpoint_id);
COMMENT ON TABLE VLGNC_TRUCK_CHART_ASSIGNED is 'View for assigned orders, used by the Truck utilization chart';

REM END VLGNC_TRUCK_CHART_ASSIGNED

-------------------------------------------------------------------------------
PROMPT VLGNS_SAP_SOLD_BILL_SAREA_SRCH

CREATE OR REPLACE FORCE VIEW VLGNS_SAP_SOLD_BILL_SAREA_SRCH AS
  SELECT DISTINCT
         billto.SAP_ID || '_' || pf_billto_soldto.SAREA_ID AS COMPOSITE_ID,
         billto.NAME1 AS BILLTO_NAME,
         billto.NAME2 AS BILLTO_NAME2,
         billto.SAP_ID AS BILLTO_NR,
         billto.SAP_BP_ID AS BILLTO_SAP_BP_ID,
         billto.POSTAL_CODE AS BILLTO_POSTAL_CODE,
         billto.CITY AS BILLTO_CITY,
         billto.MARKED_FOR_DELETION as BILLTO_MARKED_FOR_DELETION,
         soldto.MARKED_FOR_DELETION as SOLDTO_MARKED_FOR_DELETION,
         soldto.NAME1 AS SOLDTO_NAME,
         soldto.SAP_ID AS SOLDTO_NR,
         soldto.SAP_BP_ID AS SOLDTO_SAP_BP_ID,
         pf_billto_soldto.SAREA_ID AS SAREA_ID,
         sarea.SORG_CD AS SORG_CD
  FROM
    TLGNS_SAP_BP billto,
    TLGNS_SAP_BP soldto,
    TLGNS_SAP_BP_SAREA billto_sarea,
    TLGNS_SAP_BP_SAREA soldto_sarea,
    TLGNS_SAP_BP_PF_BILLTO pf_billto_soldto,
    TLGNC_SAREA sarea
  WHERE
    billto.bp_is_billto = 1
    AND soldto.bp_is_soldto = 1
    AND billto_sarea.SAP_BP_ID = billto.SAP_BP_ID
    AND soldto_sarea.SAP_BP_ID = soldto.SAP_BP_ID
    AND pf_billto_soldto.SAP_BP_SAREA_ID = soldto_sarea.SAP_BP_SAREA_ID
    AND pf_billto_soldto.BILLTO_SAP_BP_SAREA_ID = billto_sarea.SAP_BP_SAREA_ID
    AND pf_billto_soldto.SAREA_ID = sarea.SAREA_ID;


COMMENT ON TABLE VLGNS_SAP_SOLD_BILL_SAREA_SRCH is 'This view contains the data for displaying billtos. Mainly used from Dispokarte salesarea tabs.';

REM END VLGNS_SAP_SOLD_BILL_SAREA_SRCH

-------------------------------------------------------------------------------
prompt VLGNS_SAP_SOLD_PAY_SAREA_SRCH

create or replace force view VLGNS_SAP_SOLD_PAY_SAREA_SRCH as
  select distinct
    payer.SAP_ID || '_' || pf_payer_soldto.SAREA_ID as COMPOSITE_ID,
    payer.NAME1 as PAYER_NAME,
    payer.SAP_ID as PAYER_NR,
    payer.SAP_BP_ID as PAYER_SAP_BP_ID,
    payer.POSTAL_CODE as PAYER_POSTAL_CODE,
    payer.CITY as PAYER_CITY,
    payer.MARKED_FOR_DELETION as PAYER_MARKED_FOR_DELETION,
    soldto.MARKED_FOR_DELETION as SOLDTO_MARKED_FOR_DELETION,
    soldto.NAME1 as SOLDTO_NAME,
    soldto.SAP_ID as SOLDTO_NR,
    soldto.SAP_BP_ID as SOLDTO_SAP_BP_ID,
    pf_payer_soldto.SAREA_ID as SAREA_ID,
    sarea.SORG_CD as SORG_CD
  from
    TLGNS_SAP_BP payer,
    TLGNS_SAP_BP soldto,
    TLGNS_SAP_BP_SAREA payer_sarea,
    TLGNS_SAP_BP_SAREA soldto_sarea,
    TLGNS_SAP_BP_PF_PAYER pf_payer_soldto,
    TLGNC_SAREA sarea
  where
    payer.BP_IS_PAYER = 1
    and soldto.BP_IS_SOLDTO = 1
    and payer_sarea.SAP_BP_ID = payer.SAP_BP_ID
    and soldto_sarea.SAP_BP_ID = soldto.SAP_BP_ID
    and pf_payer_soldto.SAP_BP_SAREA_ID = soldto_sarea.SAP_BP_SAREA_ID
    and pf_payer_soldto.PAYER_SAP_BP_SAREA_ID = payer_sarea.SAP_BP_SAREA_ID
    and pf_payer_soldto.SAREA_ID = sarea.SAREA_ID;

comment on table VLGNS_SAP_SOLD_PAY_SAREA_SRCH is 'This view contains the data for displaying payers. Mainly used from Dispokarte salesarea tabs.';

rem end VLGNS_SAP_SOLD_PAY_SAREA_SRCH

-------------------------------------------------------------------------------

PROMPT VLGNC_DRIVER_PERSON

  CREATE OR REPLACE FORCE VIEW VLGNC_DRIVER_PERSON AS
  SELECT prs.person_id, prs.firstname, prs.name, dri.current_truck_id as TRUCK_ID
FROM tlgnc_driver dri
INNER JOIN tlgnu_user usr ON(dri.user_id = usr.user_id)
INNER JOIN tlgnc_person prs ON(prs.person_id = usr.person_id);

COMMENT ON TABLE VLGNC_DRIVER_PERSON is 'This view contains the data for displaying driver''s firstname and name. Mainly used from Live Karte screen.';

REM END VLGNC_DRIVER_PERSON

-------------------------------------------------------------------------------

PROMPT VLGNC_TRUCK_ASSIGNMENT

CREATE OR REPLACE FORCE VIEW VLGNC_TRUCK_ASSIGNMENT AS
SELECT ass.assignment_id || '_' || assp.assignment_part_id assignment_truck_id, ass.assignment_id, assp.assignment_part_id, assp.order_id, ass.truck_id, tru.truck_name
FROM tlgnc_assignment ass
INNER JOIN tlgnc_assignment_part assp ON(assp.assignment_id = ass.assignment_id)
INNER JOIN tlgnc_truck tru ON(ass.truck_id = tru.truck_id)
WHERE ass.active_flag = 1;

COMMENT ON TABLE VLGNC_TRUCK_ASSIGNMENT is 'This view contains the data for displaying assigned trucks. Mainly used from Live Karte screen.';

REM END VLGNC_TRUCK_ASSIGNMENT

-------------------------------------------------------------------------------

PROMPT VLGNC_ORDER_CREATE

CREATE OR REPLACE FORCE VIEW VLGNC_ORDER_CREATE AS
SELECT
    ord.ORDER_ID  || '_' || item.ORDER_ITEM_ID as COMPOSITE_ID,
    ord.ORDER_ID,
    ord.SHIPTO_SAP_BP_ID,
    ord.SAREA_ID,
    ord.ORDER_NUMBER,
    ord.ORDER_STATUS_CD,
    ord.CUSTORDER_ID,
    ord.SAP_CONTRACT_ID,
    ord.DELIVNOTE_SINGLE_PAPER_FLAG,
    ord.DELIVNOTE_EMAIL_FLAG,
    ord.DELIVNOTE_FAX_FLAG,
    ord.ORDER_CONF_EMAIL_FLAG,
    ord.ORDER_CONF_EMAIL_ADRESSES,
    ord.ORDER_CONF_FAX_FLAG,
    ord.ORDER_CONF_FAX_NUMBER,
    ord.ORDER_CONF_SMS_FLAG,
    ord.ORDER_CONF_SMS_NUMBER,
    ord.DELIVERY_FROM_DAT,
    ord.DELIVERY_TO_DAT,
    ord.DELIVERY_TYPE_CD,
    ord.POOL_ID,
    ord.COMM_CARRIER_ID,
    ord.EXEC_CARRIER_ID,
    ord.BILLTO_SAP_BP_ID,
    ord.PAYER_SAP_BP_ID,
    ord.NOTE_ORDER_MGMT,
    ord.SHIPPINGPOINT_ID,
    ord.NOTE_DELIVERY,
    ord.CUST_WEBORDER_NOTE,
    ord.NOTE_DISPATCHING,
    ord.NOTE_TRUCK_DRIVER,
    ord.NOTE_RAILWAY_DISP,
    ord.TRANSPORT_DURATION_MIN,
    ord.RELEASE_SAP_FLAG,
    ord.CONTACT_PERSON_TXT,
    ord.ORDER_CREATION_TYPE_CD,
    ord.ON_HOLD_ORDER_AND_LOCKED_FLAG,
    ord.DELIVERY_TRSP_ZONE_CD,
    item.ORDER_ITEM_ID,
    item.ORDER_ITEM_NO,
    item.SAP_CONTRACT_ITEM_ID,
    item.SAP_MATERIAL_ID,
    item.CURRENCY_CD,
    item.RATE,
    item.ORDERED_UOM_CD,
    item.ORDERED_QUANTITY,
    item.DELIVERED_UOM_CD,
    item.DELIVERED_QUANTITY,
    item.SHIPTO_SILO_ID,
    item.LOADINGPOINT_ID,
    item.BIMODAL_RAIL_ORDER_ITEM_ID,
    item.PRODUCTION_PLANT_CD,
    status.LANG_CD,
    pooltext.NAME as POOL_NAME,
    ccarrier.DISPLAY_NAME as COMM_CARRIER_NAME,
    ecarrier.DISPLAY_NAME as EXEC_CARRIER_NAME,
    shipp.SHIPPINGPOINT_NAME,
    -- TODO (romek 29 Jun, 2011): Consider renaming BP_NAME to BILLTO_BP_NAME
    (select billto_sbp.NAME1
     FROM TLGNS_SAP_BP billto_sbp
     WHERE ord.BILLTO_SAP_BP_ID = billto_sbp.SAP_BP_ID) as BP_NAME,
    (select soldto_sbp.NAME1
     FROM TLGNS_SAP_BP soldto_sbp
     WHERE ord.SOLDTO_SAP_BP_ID = soldto_sbp.SAP_BP_ID) as SOLDTO_BP_NAME,
    (select shipto_sbp.NAME1
     FROM TLGNS_SAP_BP shipto_sbp
     WHERE ord.SHIPTO_SAP_BP_ID = shipto_sbp.SAP_BP_ID) as SHIPTO_BP_NAME,
    (select payer_sbp.NAME1
     FROM TLGNS_SAP_BP payer_sbp
     WHERE ord.PAYER_SAP_BP_ID = payer_sbp.SAP_BP_ID) as PAYER_BP_NAME,
    unittxt.TXT as UNIT_TXT,
    material.SAP_MATERIAL_NO as MATERIAL_NO,
    materialdesc.txt as MATERIAL_TXT,
    status.TXT as STATUS_TXT,
    lp.LOADINGPOINT_NAME,
    shiptosilo.SILO_NAME,
    delivtype.SAP_TRSP_MODE_CD,
    delivtypetx.TXT as DELIVERY_TYPE_TXT,
    plant.PLANT_NAME as PRODUCTION_PLANT_NAME,
    supplyplant.PLANT_NAME as SUPPLY_PLANT_NAME,
    supplyplant.PLANT_CD as SUPPLY_PLANT_CD,
    ass_part_unload.ASSIGN_PART_STATUS_CD AS UNLOAD_STATUS_CD,
    ass_part_unload.PLANNED_START as UNLOAD_PLANNED_START,
    ass_part_unload.REAL_START as UNLOAD_REAL_START,
    custorder.CUSTORDER_CUSTREF_NO as CUSTORDER_CUSTREF_NO,
    ord.ITRACK_AVAILABLE_FLAG
  FROM TLGNR_ORDER_STATUS_TX status
    join TLGNC_ORDER ord on (status.ORDER_STATUS_CD = ord.ORDER_STATUS_CD and status.order_status_cd not in (16,17,19,20))
    join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1)
    join TLGNC_ORDER_ITEM item on (ord.order_id = item.order_id )
      left join TLGNC_CUSTORDER custorder on (ord.custorder_id = custorder.custorder_id)
    left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc
      on (materialdesc.SAP_MATERIAL_ID = item.SAP_MATERIAL_ID
        and status.LANG_CD = materialdesc.LANG_CD)
    left outer join TLGNC_POOL pool on (pool.POOL_ID = ord.POOL_ID)
    left outer join TLGNC_SHIPPINGPOINT shipp on (shipp.SHIPPINGPOINT_ID = ord.SHIPPINGPOINT_ID)
    left outer join TLGNC_LOADINGPOINT lp on (lp.LOADINGPOINT_ID = item.LOADINGPOINT_ID)
    left outer join TLGNC_CARRIER ccarrier on (ccarrier.CARRIER_ID = ord.COMM_CARRIER_ID)
    left outer join TLGNC_CARRIER ecarrier on (ecarrier.CARRIER_ID = ord.EXEC_CARRIER_ID)
    left outer join TLGNR_LANG_BS lang on (status.LANG_CD = lang.LANG_CD)
    left outer join TLGNC_POOL_TX pooltext on (pooltext.POOL_ID = ord.POOL_ID
      and pooltext.LANG_CD = lang.lang_cd)
    left outer join TLGNC_ASSIGNMENT_PART ass_part_unload
      on (ass_part_unload.ORDER_ID = ord.ORDER_ID)
    left outer join TLGNR_UOM_BS unit on (item.ORDERED_UOM_CD = unit.UOM_CD)
    left outer join TLGNR_UOM_TX unittxt on (unittxt.LANG_CD = status.LANG_CD
      and unittxt.UOM_CD = item.ORDERED_UOM_CD)
    left outer join TLGNR_DELIVERY_TYPE_BS delivtype
      on (delivtype.DELIVERY_TYPE_CD = ord.DELIVERY_TYPE_CD)
    left outer join TLGNR_DELIVERY_TYPE_TX delivtypetx
      on (delivtypetx.LANG_CD = status.LANG_CD
        and delivtypetx.DELIVERY_TYPE_CD = ord.DELIVERY_TYPE_CD)
    left outer join TLGNC_SHIPTO_SILO shiptosilo on (shiptosilo.SHIPTO_SILO_ID = item.SHIPTO_SILO_ID)
    left outer join TLGNS_SAP_MATERIAL material on (material.SAP_MATERIAL_ID = item.SAP_MATERIAL_ID)
    left outer join TLGNR_PLANT_BS plant on (item.PRODUCTION_PLANT_CD = plant.PLANT_CD)
    left outer join TLGNR_PLANT_BS supplyplant on (shipp.PLANT_CD = supplyplant.PLANT_CD);

COMMENT ON TABLE VLGNC_ORDER_CREATE is 'Used by the Ongoing/Completed order list of UC 4012: Order Create.';

REM END VLGNC_ORDER_CREATE

-------------------------------------------------------------------------------

PROMPT VLGNC_ORDER_FINISHED

CREATE OR REPLACE FORCE VIEW VLGNC_ORDER_FINISHED AS
  SELECT
    ord.ORDER_ID  || '_' || item.ORDER_ITEM_ID as COMPOSITE_ID,
    ord.ORDER_ID,
    ord.SHIPTO_SAP_BP_ID,
    ord.SAREA_ID,
    ord.ORDER_NUMBER,
    ord.ORDER_STATUS_CD,
    ord.CUSTORDER_ID,
    ord.SAP_CONTRACT_ID,
    ord.DELIVNOTE_SINGLE_PAPER_FLAG,
    ord.DELIVNOTE_EMAIL_FLAG,
    ord.DELIVNOTE_FAX_FLAG,
    ord.ORDER_CONF_EMAIL_FLAG,
    ord.ORDER_CONF_EMAIL_ADRESSES,
    ord.ORDER_CONF_FAX_FLAG,
    ord.ORDER_CONF_FAX_NUMBER,
    ord.ORDER_CONF_SMS_FLAG,
    ord.ORDER_CONF_SMS_NUMBER,
    ord.DELIVERY_FROM_DAT,
    ord.DELIVERY_TO_DAT,
    ord.DELIVERY_TYPE_CD,
    ord.POOL_ID,
    ord.COMM_CARRIER_ID,
    ord.EXEC_CARRIER_ID,
    ord.BILLTO_SAP_BP_ID,
    ord.PAYER_SAP_BP_ID,
    ord.NOTE_ORDER_MGMT,
    ord.SHIPPINGPOINT_ID,
    ord.NOTE_DELIVERY,
    ord.CUST_WEBORDER_NOTE,
    ord.NOTE_DISPATCHING,
    ord.NOTE_TRUCK_DRIVER,
    ord.NOTE_RAILWAY_DISP,
    ord.TRANSPORT_DURATION_MIN,
    ord.RELEASE_SAP_FLAG,
    ord.CONTACT_PERSON_TXT,
    ord.ORDER_CREATION_TYPE_CD,
    ord.ON_HOLD_ORDER_AND_LOCKED_FLAG,
    ord.DELIVERY_TRSP_ZONE_CD,
    item.ORDER_ITEM_ID,
    item.ORDER_ITEM_NO,
    item.SAP_CONTRACT_ITEM_ID,
    item.SAP_MATERIAL_ID,
    item.CURRENCY_CD,
    item.RATE,
    item.ORDERED_UOM_CD,
    item.ORDERED_QUANTITY,
    item.DELIVERED_UOM_CD,
    item.DELIVERED_QUANTITY,
    item.SHIPTO_SILO_ID,
    item.LOADINGPOINT_ID,
    item.BIMODAL_RAIL_ORDER_ITEM_ID,
    item.PRODUCTION_PLANT_CD,
    status.LANG_CD,
    pooltext.NAME as POOL_NAME,
    ccarrier.DISPLAY_NAME as COMM_CARRIER_NAME,
    ecarrier.DISPLAY_NAME as EXEC_CARRIER_NAME,
    shipp.SHIPPINGPOINT_NAME,
    -- TODO (romek 29 Jun, 2011): Consider renaming BP_NAME to BILLTO_BP_NAME
    (select billto_sbp.NAME1
     FROM TLGNS_SAP_BP billto_sbp
     WHERE ord.BILLTO_SAP_BP_ID = billto_sbp.SAP_BP_ID) as BP_NAME,
    (select soldto_sbp.NAME1
     FROM TLGNS_SAP_BP soldto_sbp
     WHERE ord.SOLDTO_SAP_BP_ID = soldto_sbp.SAP_BP_ID) as SOLDTO_BP_NAME,
    (select shipto_sbp.NAME1
     FROM TLGNS_SAP_BP shipto_sbp
     WHERE ord.SHIPTO_SAP_BP_ID = shipto_sbp.SAP_BP_ID) as SHIPTO_BP_NAME,
    (select payer_sbp.NAME1
     FROM TLGNS_SAP_BP payer_sbp
     WHERE ord.PAYER_SAP_BP_ID = payer_sbp.SAP_BP_ID) as PAYER_BP_NAME,
    unittxt.TXT as UNIT_TXT,
    material.SAP_MATERIAL_NO as MATERIAL_NO,
    materialdesc.txt as MATERIAL_TXT,
    status.TXT as STATUS_TXT,
    lp.LOADINGPOINT_NAME,
    shiptosilo.SILO_NAME,
    delivtype.SAP_TRSP_MODE_CD,
    delivtypetx.TXT as DELIVERY_TYPE_TXT,
    plant.PLANT_NAME as PRODUCTION_PLANT_NAME,
    supplyplant.PLANT_NAME as SUPPLY_PLANT_NAME,
    supplyplant.PLANT_CD as SUPPLY_PLANT_CD,
    ass_part_unload.ASSIGN_PART_STATUS_CD AS UNLOAD_STATUS_CD,
    ass_part_unload.PLANNED_START as UNLOAD_PLANNED_START,
    ass_part_unload.REAL_START as UNLOAD_REAL_START,
    custorder.CUSTORDER_CUSTREF_NO as CUSTORDER_CUSTREF_NO
  FROM TLGNR_ORDER_STATUS_TX status
    join TLGNC_ORDER ord on (status.ORDER_STATUS_CD = ord.ORDER_STATUS_CD and status.order_status_cd in (16, 17))
    join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1)
    join TLGNC_ORDER_ITEM item on (ord.order_id = item.order_id )
      left join TLGNC_CUSTORDER custorder on (ord.custorder_id = custorder.custorder_id)
    left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc
      on (materialdesc.SAP_MATERIAL_ID = item.SAP_MATERIAL_ID
        and status.LANG_CD = materialdesc.LANG_CD)
    left outer join TLGNC_POOL pool on (pool.POOL_ID = ord.POOL_ID)
    left outer join TLGNC_SHIPPINGPOINT shipp on (shipp.SHIPPINGPOINT_ID = ord.SHIPPINGPOINT_ID)
    left outer join TLGNC_LOADINGPOINT lp on (lp.LOADINGPOINT_ID = item.LOADINGPOINT_ID)
    left outer join TLGNC_CARRIER ccarrier on (ccarrier.CARRIER_ID = ord.COMM_CARRIER_ID)
    left outer join TLGNC_CARRIER ecarrier on (ecarrier.CARRIER_ID = ord.EXEC_CARRIER_ID)
    left outer join TLGNR_LANG_BS lang on (status.LANG_CD = lang.LANG_CD)
    left outer join TLGNC_POOL_TX pooltext on (pooltext.POOL_ID = ord.POOL_ID
      and pooltext.LANG_CD = lang.lang_cd)
    left outer join TLGNC_ASSIGNMENT_PART ass_part_unload
      on (ass_part_unload.ORDER_ID = ord.ORDER_ID)
    left outer join TLGNR_UOM_BS unit on (item.ORDERED_UOM_CD = unit.UOM_CD)
    left outer join TLGNR_UOM_TX unittxt on (unittxt.LANG_CD = status.LANG_CD
      and unittxt.UOM_CD = item.ORDERED_UOM_CD)
    left outer join TLGNR_DELIVERY_TYPE_BS delivtype
      on (delivtype.DELIVERY_TYPE_CD = ord.DELIVERY_TYPE_CD)
    left outer join TLGNR_DELIVERY_TYPE_TX delivtypetx
      on (delivtypetx.LANG_CD = status.LANG_CD
        and delivtypetx.DELIVERY_TYPE_CD = ord.DELIVERY_TYPE_CD)
    left outer join TLGNC_SHIPTO_SILO shiptosilo on (shiptosilo.SHIPTO_SILO_ID = item.SHIPTO_SILO_ID)
    left outer join TLGNS_SAP_MATERIAL material on (material.SAP_MATERIAL_ID = item.SAP_MATERIAL_ID)
    left outer join TLGNR_PLANT_BS plant on (item.PRODUCTION_PLANT_CD = plant.PLANT_CD)
    left outer join TLGNR_PLANT_BS supplyplant on (shipp.PLANT_CD = supplyplant.PLANT_CD);


COMMENT ON TABLE VLGNC_ORDER_FINISHED is 'Used by the Completed order list of UC 4012: Order Create.';

REM END VLGNC_ORDER_FINISHED

-------------------------------------------------------------------------------
prompt vlgnc_custorder_create

create or replace force view vlgnc_custorder_create as
select /*+ ORDERED ALL_ROWS */
    custord.CUSTORDER_ID || '_' || item.CUSTORDER_ITEM_ID as COMPOSITE_ID,
    custord.CUSTORDER_ID,
    custord.SAP_CONTRACT_ID,
    custord.SHIPTO_SAP_BP_ID,
    custord.SAREA_ID,
    custord.CUSTORDER_NUMBER,
    custord.CUSTORDER_CUSTREF_NO,
    custord.CUSTORDER_STATUS_CD,
    custord.DELIVNOTE_SINGLE_PAPER_FLAG,
    custord.DELIVNOTE_COLL_PAPER_FLAG,
    custord.DELIVNOTE_EMAIL_FLAG,
    custord.DELIVNOTE_FAX_FLAG,
    custord.ORDER_CONF_EMAIL_FLAG,
    custord.ORDER_CONF_EMAIL_ADRESSES,
    custord.ORDER_CONF_FAX_FLAG,
    custord.ORDER_CONF_FAX_NUMBER,
    custord.ORDER_CONF_SMS_FLAG,
    custord.ORDER_CONF_SMS_NUMBER,
    custord.DELIVERY_FROM_DAT,
    custord.DELIVERY_TO_DAT,
    custord.DELIVERY_TYPE_CD,
    custord.POOL_ID,
    custord.COMM_CARRIER_ID,
    custord.BILLTO_SAP_BP_ID,
    custord.PAYER_SAP_BP_ID,
    custord.NOTE_ORDER_MGMT,
    custord.SHIPPINGPOINT_ID,
    custord.NOTE_DELIVERY,
    custord.CUST_WEBORDER_NOTE,
    custord.NOTE_DISPATCHING,
    custord.NOTE_TRUCK_DRIVER,
    custord.RELEASE_SAP_FLAG,
    custord.CONTACT_PERSON_TXT,
    custord.ORDER_CREATION_TYPE_CD,
    custord.ON_HOLD_ORDER_AND_LOCKED_FLAG,
    item.CUSTORDER_ITEM_ID,
    item.CUSTORDER_ITEM_NO,
    item.SAP_CONTRACT_ITEM_ID,
    item.SAP_MATERIAL_ID,
    item.CURRENCY_CD,
    item.RATE,
    item.ORDERED_UOM_CD,
    item.ORDERED_QUANTITY,
    item.SHIPTO_SILO_ID,
    item.LOADINGPOINT_ID,
    item.PRODUCTION_PLANT_CD,
    status.LANG_CD,
    pooltext.NAME as POOL_NAME,
    ccarrier.DISPLAY_NAME as COMM_CARRIER_NAME,
    shipp.SHIPPINGPOINT_NAME,
    (select billto_sbp.NAME1
     FROM TLGNS_SAP_BP billto_sbp
     WHERE custord.BILLTO_SAP_BP_ID = billto_sbp.SAP_BP_ID) as BILLTO_BP_NAME,
    (select soldto_sbp.NAME1
     FROM TLGNS_SAP_BP soldto_sbp
     WHERE custord.SOLDTO_SAP_BP_ID = soldto_sbp.SAP_BP_ID) as SOLDTO_BP_NAME,
    (select shipto_sbp.NAME1
     FROM TLGNS_SAP_BP shipto_sbp
     WHERE custord.SHIPTO_SAP_BP_ID = shipto_sbp.SAP_BP_ID) as SHIPTO_BP_NAME,
    (select payer_sbp.NAME1
     FROM TLGNS_SAP_BP payer_sbp
     WHERE custord.PAYER_SAP_BP_ID = payer_sbp.SAP_BP_ID) as PAYER_BP_NAME,
    unittxt.TXT as UNIT_TXT,
    material.SAP_MATERIAL_NO as MATERIAL_NO,
    materialdesc.txt as MATERIAL_TXT,
    status.TXT as STATUS_TXT,
    lp.LOADINGPOINT_NAME,
    shiptosilo.SILO_NAME,
    delivtype.SAP_TRSP_MODE_CD,
    delivtypetx.TXT as DELIVERY_TYPE_TXT,
     (select plant.PLANT_NAME from TLGNR_PLANT_BS plant where item.PRODUCTION_PLANT_CD = plant.PLANT_CD) as PRODUCTION_PLANT_NAME,
    (select supplyplant.PLANT_NAME from TLGNR_PLANT_BS supplyplant where shipp.PLANT_CD = supplyplant.PLANT_CD) as SUPPLY_PLANT_NAME,
    shipp.PLANT_CD as SUPPLY_PLANT_CD
  from TLGNR_LANG_BS lang
    join TLGNR_CUSTORDER_STATUS_TX status on (status.LANG_CD = lang.LANG_CD and status.custorder_status_cd in (1,2))
    join TLGNC_CUSTORDER custord   on (status.CUSTORDER_STATUS_CD = custord.CUSTORDER_STATUS_CD and custord.custorder_status_cd in (1,2))
    join TLGNR_DELIVERY_TYPE_BS delivtype
      on (delivtype.DELIVERY_TYPE_CD = custord.DELIVERY_TYPE_CD)
    join TLGNR_DELIVERY_TYPE_TX delivtypetx on (delivtypetx.LANG_CD = status.LANG_CD
      and delivtypetx.DELIVERY_TYPE_CD = delivtype.DELIVERY_TYPE_CD)
    join TLGNC_CUSTORDER_ITEM item on (custord.custorder_id = item.custorder_id)
    join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc
      on (materialdesc.SAP_MATERIAL_ID = item.SAP_MATERIAL_ID and  materialdesc.LANG_CD = status.LANG_CD)
    left outer join TLGNC_POOL pool on (pool.POOL_ID = custord.POOL_ID)
    left outer join TLGNC_SHIPPINGPOINT shipp on (shipp.SHIPPINGPOINT_ID = custord.SHIPPINGPOINT_ID)
    left outer join TLGNC_LOADINGPOINT lp on (lp.LOADINGPOINT_ID = item.LOADINGPOINT_ID)
    left outer join TLGNC_CARRIER ccarrier on (ccarrier.CARRIER_ID = custord.COMM_CARRIER_ID)
    left outer join TLGNC_POOL_TX pooltext
      on (pooltext.POOL_ID = custord.POOL_ID and pooltext.LANG_CD = lang.lang_cd)
    left outer join TLGNR_UOM_BS unit on (item.ORDERED_UOM_CD = unit.UOM_CD)
    left outer join TLGNR_UOM_TX unittxt on (unittxt.LANG_CD = status.LANG_CD
      and unittxt.UOM_CD = item.ORDERED_UOM_CD)
    left outer join TLGNC_SHIPTO_SILO shiptosilo on (shiptosilo.SHIPTO_SILO_ID = item.SHIPTO_SILO_ID)
    left outer join TLGNS_SAP_MATERIAL material on (material.SAP_MATERIAL_ID = item.SAP_MATERIAL_ID);

comment on table vlgnc_custorder_create is 'Used for displaying the Open customer order list on the Order Taking screen. Analogous to VLGNC_ORDER_CREATE.';

rem end vlgnc_custorder_create

-------------------------------------------------------------------------------

prompt vlgnc_custorder_finished

create or replace force view vlgnc_custorder_finished as
  select /*+ ORDERED ALL_ROWS */
    custord.CUSTORDER_ID || '_' || item.CUSTORDER_ITEM_ID as COMPOSITE_ID,
    custord.CUSTORDER_ID,
    custord.SAP_CONTRACT_ID,
    custord.SHIPTO_SAP_BP_ID,
    custord.SAREA_ID,
    custord.CUSTORDER_NUMBER,
    custord.CUSTORDER_CUSTREF_NO,
    custord.CUSTORDER_STATUS_CD,
    custord.DELIVNOTE_SINGLE_PAPER_FLAG,
    custord.DELIVNOTE_COLL_PAPER_FLAG,
    custord.DELIVNOTE_EMAIL_FLAG,
    custord.DELIVNOTE_FAX_FLAG,
    custord.ORDER_CONF_EMAIL_FLAG,
    custord.ORDER_CONF_EMAIL_ADRESSES,
    custord.ORDER_CONF_FAX_FLAG,
    custord.ORDER_CONF_FAX_NUMBER,
    custord.ORDER_CONF_SMS_FLAG,
    custord.ORDER_CONF_SMS_NUMBER,
    custord.DELIVERY_FROM_DAT,
    custord.DELIVERY_TO_DAT,
    custord.DELIVERY_TYPE_CD,
    custord.POOL_ID,
    custord.COMM_CARRIER_ID,
    custord.BILLTO_SAP_BP_ID,
    custord.PAYER_SAP_BP_ID,
    custord.NOTE_ORDER_MGMT,
    custord.SHIPPINGPOINT_ID,
    custord.NOTE_DELIVERY,
    custord.CUST_WEBORDER_NOTE,
    custord.NOTE_DISPATCHING,
    custord.NOTE_TRUCK_DRIVER,
    custord.RELEASE_SAP_FLAG,
    custord.CONTACT_PERSON_TXT,
    custord.ORDER_CREATION_TYPE_CD,
    custord.ON_HOLD_ORDER_AND_LOCKED_FLAG,
    item.CUSTORDER_ITEM_ID,
    item.CUSTORDER_ITEM_NO,
    item.SAP_CONTRACT_ITEM_ID,
    item.SAP_MATERIAL_ID,
    item.CURRENCY_CD,
    item.RATE,
    item.ORDERED_UOM_CD,
    item.ORDERED_QUANTITY,
    item.SHIPTO_SILO_ID,
    item.LOADINGPOINT_ID,
    item.PRODUCTION_PLANT_CD,
    status.LANG_CD,
    pooltext.NAME as POOL_NAME,
    ccarrier.DISPLAY_NAME as COMM_CARRIER_NAME,
    shipp.SHIPPINGPOINT_NAME,
    (select billto_sbp.NAME1
     FROM TLGNS_SAP_BP billto_sbp
     WHERE custord.BILLTO_SAP_BP_ID = billto_sbp.SAP_BP_ID) as BILLTO_BP_NAME,
    (select soldto_sbp.NAME1
     FROM TLGNS_SAP_BP soldto_sbp
     WHERE custord.SOLDTO_SAP_BP_ID = soldto_sbp.SAP_BP_ID) as SOLDTO_BP_NAME,
    (select shipto_sbp.NAME1
     FROM TLGNS_SAP_BP shipto_sbp
     WHERE custord.SHIPTO_SAP_BP_ID = shipto_sbp.SAP_BP_ID) as SHIPTO_BP_NAME,
    (select payer_sbp.NAME1
     FROM TLGNS_SAP_BP payer_sbp
     WHERE custord.PAYER_SAP_BP_ID = payer_sbp.SAP_BP_ID) as PAYER_BP_NAME,
    unittxt.TXT as UNIT_TXT,
    material.SAP_MATERIAL_NO as MATERIAL_NO,
    materialdesc.txt as MATERIAL_TXT,
    status.TXT as STATUS_TXT,
    lp.LOADINGPOINT_NAME,
    shiptosilo.SILO_NAME,
    delivtype.SAP_TRSP_MODE_CD,
    delivtypetx.TXT as DELIVERY_TYPE_TXT,
     (select plant.PLANT_NAME from TLGNR_PLANT_BS plant where item.PRODUCTION_PLANT_CD = plant.PLANT_CD) as PRODUCTION_PLANT_NAME,
    (select supplyplant.PLANT_NAME from TLGNR_PLANT_BS supplyplant where shipp.PLANT_CD = supplyplant.PLANT_CD) as SUPPLY_PLANT_NAME,
    shipp.PLANT_CD as SUPPLY_PLANT_CD
  from TLGNR_LANG_BS lang
    join TLGNR_CUSTORDER_STATUS_TX status on (status.LANG_CD = lang.LANG_CD and status.custorder_status_cd = 3)
    join TLGNC_CUSTORDER custord   on (status.CUSTORDER_STATUS_CD = custord.CUSTORDER_STATUS_CD and custord.custorder_status_cd = 3)
    join TLGNR_DELIVERY_TYPE_BS delivtype
      on (delivtype.DELIVERY_TYPE_CD = custord.DELIVERY_TYPE_CD)
    join TLGNR_DELIVERY_TYPE_TX delivtypetx on (delivtypetx.LANG_CD = status.LANG_CD
      and delivtypetx.DELIVERY_TYPE_CD = delivtype.DELIVERY_TYPE_CD)
    join TLGNC_CUSTORDER_ITEM item on (custord.custorder_id = item.custorder_id)
    join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc
      on (materialdesc.SAP_MATERIAL_ID = item.SAP_MATERIAL_ID and  materialdesc.LANG_CD = status.LANG_CD)
    left outer join TLGNC_POOL pool on (pool.POOL_ID = custord.POOL_ID)
    left outer join TLGNC_SHIPPINGPOINT shipp on (shipp.SHIPPINGPOINT_ID = custord.SHIPPINGPOINT_ID)
    left outer join TLGNC_LOADINGPOINT lp on (lp.LOADINGPOINT_ID = item.LOADINGPOINT_ID)
    left outer join TLGNC_CARRIER ccarrier on (ccarrier.CARRIER_ID = custord.COMM_CARRIER_ID)
    left outer join TLGNC_POOL_TX pooltext
      on (pooltext.POOL_ID = custord.POOL_ID and pooltext.LANG_CD = lang.lang_cd)
    left outer join TLGNR_UOM_BS unit on (item.ORDERED_UOM_CD = unit.UOM_CD)
    left outer join TLGNR_UOM_TX unittxt on (unittxt.LANG_CD = status.LANG_CD
      and unittxt.UOM_CD = item.ORDERED_UOM_CD)
    left outer join TLGNC_SHIPTO_SILO shiptosilo on (shiptosilo.SHIPTO_SILO_ID = item.SHIPTO_SILO_ID)
    left outer join TLGNS_SAP_MATERIAL material on (material.SAP_MATERIAL_ID = item.SAP_MATERIAL_ID);

comment on table vlgnc_custorder_finished is 'Used for displaying the Closed customer order list on the Order Taking screen. Analogous to VLGNC_ORDER_CREATE.';

rem end vlgnc_custorder_finished

-------------------------------------------------------------------------------

PROMPT VLGNC_ORDER_MONITORING
create or replace force view vlgnc_order_monitoring as
select /*+ first_rows(25) domain_index_no_sort */
  /** START: Backward compatibility columns */
  cast('TO'           as varchar2(3))  as ORDERED_UOM_CD,
  cast('TO'           as varchar2(3))  as LOADED_UOM_CD,
  cast('TO'           as varchar2(3))  as DELIVERED_UOM_CD,
  cast('TO'           as varchar2(3))  as RETURNED_UOM_CD,
  cast(1              as decimal(5))   as TRANSPORT_DURATION_MIN,
  cast(1              as decimal(10))  as PLANNED_LOADING_TIME_MIN,
  cast(1              as decimal(12))  as LOADINGPOINT_ID,
  cast(1              as decimal(12))  as SHIPTO_SAREA_ID,
  cast('A0'           as varchar2(3))  as SGROUP_CD,
  cast(0              as number(1,0))  as ON_HOLD_ORDER_AND_LOCKED_FLAG,
  cast('Loadingpoint' as varchar2(50)) as LOADINGPOINT_NAME,
  cast(1              as decimal(10))  as LOADINGPOINT_LOADING_TIME,
  cast(11             as varchar2(18)) as MATERIAL_NO ,
  cast('Plant'        as varchar2(80)) as PLANT_NAME,
  cast('S1'           as varchar2(3))  as SORG_CD,
  cast(1              as decimal(12))  as TRUCK_ID,
  cast(1              as decimal(12))  as WAGON_ID,
  cast(1              as number)       as TRUCK_MAX_NET_WEIGHT_T,
  cast(1              as number)       as WAGON_MAX_NET_WEIGHT_T,
  cast(1              as number)       as STATS_QUANTITY,
  cast('TO'           as varchar2(3))  as STATS_UOM_CD,
  /** END: Backward compatibility columns */
  order_item.order_item_id || '_' || shipto_sales_area.sgroup_cd as composite_id,
  /**
   * Displayed columns
   */
  cast(
    get_warning_status(
      logon_order.transport_duration_min,
      order_item.planned_loading_time_min,
      logon_order.delivery_to_dat,
      logon_order.order_status_cd,
      logon_order.on_hold_order_and_locked_flag
    ) as number(2,0)
  ) as warn_status_code,
  cast(
    get_minutes_to_timeline(
      logon_order.transport_duration_min,
      order_item.planned_loading_time_min,
      logon_order.delivery_to_dat,
      logon_order.order_status_cd,
      'red'
    ) as decimal(12)
  ) as time_to_red_line,
  order_item.order_item_no,
  logon_order.order_number,
  logon_order.LDS_DELIVERY_NOTE_NO,
  logon_order.NOTE_ORDER_MGMT,
  logon_order.NOTE_DELIVERY,
  logon_order.NOTE_DISPATCHING,
  logon_order.NOTE_TRUCK_DRIVER,
  logon_order.NOTE_RAILWAY_DISP,
  logon_order.ORDER_NUMBER_FROM_SEQ_USAGE,
  load.LOADING_TS,
  bill_to.name1 as billto_name1,
  ship_to.city as shipto_city,
  order_status.txt as status_txt,
  material_description.txt as material_txt,
  order_item.ordered_quantity,
  ordered_unit_of_measurement.txt as ordered_uom_txt,
  order_item.delivered_quantity,
  delivered_unit_of_measurement.txt as delivered_uom_txt,
  order_item.loaded_quantity,
  loaded_unit_of_measurement.txt as loaded_uom_txt,
  order_item.returned_quantity,
  returned_unit_of_measurement.txt as returned_uom_txt,
  shipping_point.shippingpoint_name,
  logon_order.delivery_from_dat,
  logon_order.delivery_to_dat,
  logon_order.distribution_dest_cd,
  logon_order.actual_delivery_dat as planned_delivery_dat,
  delivery_type.txt as delivery_type_txt,
  commercial_carrier.display_name as comm_carrier_name,
  executive_carrier.display_name as exec_carrier_name,
  pool.name as pool_name,
  driver_person.firstname || ' ' || driver_person.name as driver_full_name,
  coalesce(
	nvl2(
		nvl(delivered_truck.truck_name, assigned_truck.truck_name),
		nvl(delivered_truck.truck_name, assigned_truck.truck_name) || ' (' || nvl(delivered_truck.truck_plate_no, assigned_truck.truck_plate_no) ||')',
		null
	),
	nvl(delivered_wagon.wagon_no, assigned_wagon.wagon_no),
    nvl(delivered_simple_vehicle.vehicle_name, assigned_simple_vehicle.vehicle_name)
  ) as vehicle_name,
  logon_order.order_validation_code,
  coalesce(logon_order.ONE_TIME_SOLDTO_NAME, sold_to.NAME1) AS SOLDTO_NAME1,
  coalesce(logon_order.LNF_SITE_NAME, ship_to.NAME1) AS SHIPTO_NAME1,
  case when logon_order.LNF_SITE_POSTAL_CODE is not null and logon_order.LNF_SITE_CITY is not null and logon_order.LNF_SITE_STREET_HOUSE_NO is not null then
    	(logon_order.LNF_SITE_POSTAL_CODE || ' ' || logon_order.LNF_SITE_CITY || ' ' || logon_order.LNF_SITE_STREET_HOUSE_NO)
  else
  	(ship_to.POSTAL_CODE || ' ' || ship_to.CITY || ' ' || ship_to.STREET || ' ' || ship_to.HOUSE_NUMBER)
  end AS SHIPTO_ADDRESS,
  /**
   * Filtered columns
   */
  order_status.lang_cd,
  --
  logon_order.order_creation_type_cd,
  logon_order.order_status_cd,
  order_item.sap_material_id,
  material.material_package_cd,
  --
  logon_order.sarea_id,
  shipto_sales_area.soffice_cd shipto_soffice_cd,
  shipto_sales_area.sgroup_cd shipto_sgroup_cd,
  --
  logon_order.soldto_sap_bp_id,
  logon_order.shipto_sap_bp_id,
  logon_order.billto_sap_bp_id,
  --
  logon_order.pool_id,
  logon_order.comm_carrier_id,
  logon_order.exec_carrier_id,
  cast (
    nvl(delivered_simple_vehicle.simple_vehicle_id, assigned_simple_vehicle.simple_vehicle_id)
    as number(12,0)
  ) as simple_vehicle_id,
  --
  shipping_point.plant_cd,
  logon_order.shippingpoint_id,
  logon_order.delivery_type_cd,
  /**
   * Authorized columns
   */
  commercial_carrier.sap_vendor_id as sap_vendor_id,
  sales_organisation.companycode_cd,
  /**
   * Helper columns for display
   */
  logon_order.order_id,
  ship_to.timezone_cd as shipto_timezone_cd
from tlgnc_order logon_order
join tlgnc_order_item order_item
  on (order_item.order_id = logon_order.order_id
        and
      (logon_order.order_status_cd < 14 or logon_order.order_status_cd = 18))
join tlgns_sap_material material
  on (material.sap_material_id = order_item.sap_material_id)
/**
 * Sold to, ship to, bill to, shipping point
 */
join tlgns_sap_bp sold_to
  on (logon_order.soldto_sap_bp_id = sold_to.sap_bp_id)
join tlgns_sap_bp ship_to
  on (logon_order.shipto_sap_bp_id = ship_to.sap_bp_id)
left join tlgns_sap_bp bill_to
  on (logon_order.billto_sap_bp_id = bill_to.sap_bp_id)
left join tlgnc_shippingpoint shipping_point
  on (shipping_point.shippingpoint_id = logon_order.shippingpoint_id)
/**
 * Sales area, sales organisation
 */
join tlgnc_sarea sales_area
  on (logon_order.sarea_id = sales_area.sarea_id)
join tlgnr_sorg_bs sales_organisation
  on (sales_area.sorg_cd = sales_organisation.sorg_cd)
join tlgns_sap_bp_sarea shipto_sales_area
  on (shipto_sales_area.sarea_id = logon_order.sarea_id
        and
      shipto_sales_area.sap_bp_id = ship_to.sap_bp_id)
/**
 * Localized texts
 */
join tlgnr_order_status_tx order_status
  on (order_status.order_status_cd = logon_order.order_status_cd)
join tlgns_sap_material_description material_description
  on (material_description.sap_material_id = order_item.sap_material_id
        and
      order_status.lang_cd = material_description.lang_cd)
left join tlgnr_uom_tx ordered_unit_of_measurement
  on (ordered_unit_of_measurement.lang_cd = order_status.lang_cd
        and
      ordered_unit_of_measurement.uom_cd = order_item.ordered_uom_cd)
left join tlgnr_uom_tx delivered_unit_of_measurement
  on (delivered_unit_of_measurement.lang_cd = order_status.lang_cd
        and
      delivered_unit_of_measurement.uom_cd = order_item.delivered_uom_cd)
left join tlgnr_uom_tx loaded_unit_of_measurement
  on (loaded_unit_of_measurement.lang_cd = order_status.lang_cd
        and
      loaded_unit_of_measurement.uom_cd = order_item.loaded_uom_cd)
left join tlgnr_uom_tx returned_unit_of_measurement
  on (returned_unit_of_measurement.lang_cd = order_status.lang_cd
        and
      returned_unit_of_measurement.uom_cd = order_item.returned_uom_cd)
left join tlgnc_pool_tx pool
  on (pool.pool_id = logon_order.pool_id
        and
      pool.lang_cd=order_status.lang_cd)
left join tlgnr_delivery_type_tx delivery_type
  on (delivery_type.delivery_type_cd = logon_order.delivery_type_cd
        and
      delivery_type.lang_cd = order_status.lang_cd)
/**
 * Carriers
 */
left join tlgnc_carrier commercial_carrier
  on (commercial_carrier.carrier_id = logon_order.comm_carrier_id)
left join tlgnc_carrier executive_carrier
  on (executive_carrier.carrier_id = logon_order.exec_carrier_id)
/**
 * Delivered truck, wagon, simple_vehicle
 */
left join tlgnc_truck delivered_truck
  on (logon_order.delivered_truck_id = delivered_truck.truck_id)
left join tlgnc_wagon delivered_wagon
  on (logon_order.delivered_wagon_id = delivered_wagon.wagon_id)
left join tlgnc_simple_vehicle delivered_simple_vehicle
  on (logon_order.delivered_simple_vehicle_id = delivered_simple_vehicle.simple_vehicle_id)
/**
 * Assigned truck, wagon, simple_vehicle
 */
left join tlgnc_assignment_part assignment_part
  on (logon_order.order_id = assignment_part.order_id)
left join tlgnc_assignment assignment
  on (assignment_part.assignment_id = assignment.assignment_id
        and
      assignment.active_flag = 1
        and
      assignment.assign_status_cd < 6)
left join tlgnc_truck assigned_truck
  on (assignment.truck_id = assigned_truck.truck_id)
left join tlgnc_wagon assigned_wagon
  on (assignment.wagon_id = assigned_wagon.wagon_id)
left join tlgnc_simple_vehicle assigned_simple_vehicle
  on (assignment.simple_vehicle_id = assigned_simple_vehicle.simple_vehicle_id)
/**
 * Driver full name
 */
left join tlgnc_driver driver
  on (logon_order.delivered_driver_id = driver.driver_id)
left join tlgnu_user driver_user
  on (driver.user_id = driver_user.user_id)
left join tlgnc_person driver_person
  on (driver_user.person_id = driver_person.person_id)
left join TLGNC_LOAD load
  on (load.load_id=order_item.load_id);

COMMENT ON TABLE VLGNC_ORDER_MONITORING is 'Used to collect the resultlist for Order Monitoring';

REM END VLGNC_ORDER_MONITORING

-------------------------------------------------------------------------------

PROMPT VLGNC_ORDER_SEARCH

-- WARNING: The disaster-recovery job uses this view, when
-- adding/removing/renaming columns, make sure to contact dbeng (Reto)!
CREATE OR REPLACE FORCE VIEW VLGNC_ORDER_SEARCH AS
   SELECT /*+ FIRST_ROWS(25)*/
  item.ORDER_ITEM_ID || '_' || shipto_sarea.SGROUP_CD AS COMPOSITE_ID,
    item.ordered_uom_numerator,
    item.ordered_uom_denominator,
    item.lr_uom_numerator,
    item.lr_uom_denominator,
    ord.ORDER_ID,
    ord.ORDER_CREATION_TYPE_CD,
    ord.SHIPTO_SAP_BP_ID,
    ord.SOLDTO_SAP_BP_ID,
    ord.BILLTO_SAP_BP_ID,
    ord.SAREA_ID,
    item.SAP_MATERIAL_ID,
    ord.ORDER_NUMBER,
    ord.LDS_DELIVERY_NOTE_NO,
    ord.NOTE_ORDER_MGMT,
    ord.NOTE_DELIVERY,
    ord.NOTE_DISPATCHING,
    ord.NOTE_TRUCK_DRIVER,
    ord.NOTE_RAILWAY_DISP,
    ord.ORDER_NUMBER_FROM_SEQ_USAGE,
    load.LOADING_TS,
    item.ORDER_ITEM_NO,
    item.ORDERED_UOM_CD,
    (select txt from TLGNR_UOM_TX where UOM_CD =item.ORDERED_UOM_CD AND LANG_CD=lang.LANG_CD) AS ORDERED_UOM_TXT,
    (select txt from TLGNR_UOM_TX where UOM_CD =item.LOADED_UOM_CD AND LANG_CD=lang.LANG_CD) AS LOADED_UOM_TXT,
    (select txt from TLGNR_UOM_TX where UOM_CD =item.DELIVERED_UOM_CD AND LANG_CD=lang.LANG_CD) AS DELIVERED_UOM_TXT,
    (select txt from TLGNR_UOM_TX where UOM_CD =item.RETURNED_UOM_CD AND LANG_CD=lang.LANG_CD) AS RETURNED_UOM_TXT,
    item.ORDERED_QUANTITY,
    item.LOADED_UOM_CD,
    item.LOADED_QUANTITY,
    item.DELIVERED_UOM_CD,
    item.DELIVERED_QUANTITY,
    item.RETURNED_UOM_CD,
    item.RETURNED_QUANTITY,
    ord.DELIVERY_FROM_DAT,
    ord.DELIVERY_TO_DAT,
    ord.DISTRIBUTION_DEST_CD,
    ord.ORDER_STATUS_CD,
    ord.DELIVERY_TYPE_CD,
    ord.SHIPPINGPOINT_ID,
    ord.TRANSPORT_DURATION_MIN,
    ord.POOL_ID,
    ord.COMM_CARRIER_ID,
    ord.EXEC_CARRIER_ID,
    ord.ACTUAL_DELIVERY_DAT AS PLANNED_DELIVERY_DAT,
    ord.ORDER_VALIDATION_CODE,
  	ord.ITRACK_STATUS_CD,
    coalesce(ord.ONE_TIME_SOLDTO_NAME, soldtoBp.NAME1) AS SOLDTO_NAME1,
    coalesce(ord.LNF_SITE_NAME, shiptoBp.NAME1) AS SHIPTO_NAME1,
    case when ord.LNF_SITE_POSTAL_CODE is not null and ord.LNF_SITE_CITY is not null and ord.LNF_SITE_STREET_HOUSE_NO is not null then
    	(ord.LNF_SITE_POSTAL_CODE || ' ' || ord.LNF_SITE_CITY || ' ' || ord.LNF_SITE_STREET_HOUSE_NO)
    else
    	(shiptoBp.POSTAL_CODE || ' ' || shiptoBp.CITY || ' ' || shiptoBp.STREET || ' ' || shiptoBp.HOUSE_NUMBER)
   	end AS SHIPTO_ADDRESS,
    shiptoBp.CITY AS SHIPTO_CITY,
    billtoBp.NAME1 AS BILLTO_NAME1,
    shipto_sarea.SAREA_ID SHIPTO_SAREA_ID,
    shipto_sarea.SGROUP_CD SHIPTO_SGROUP_CD,
    shipto_sarea.SGROUP_CD,
    shipto_sarea.SOFFICE_CD SHIPTO_SOFFICE_CD,
    lang.LANG_CD,
    status.TXT AS STATUS_TXT,
    CAST (0 AS NUMBER(2,0)) AS WARN_STATUS_CODE,
    pooltext.NAME AS POOL_NAME,
    --Custom authorization for SAP vendor
    ccarrier.DISPLAY_NAME AS COMM_CARRIER_NAME,
    ccarrier.SAP_VENDOR_ID AS COMM_SAP_VENDOR_ID,
    ecarrier.DISPLAY_NAME AS EXEC_CARRIER_NAME,
    ecarrier.SAP_VENDOR_ID AS EXEC_SAP_VENDOR_ID,
    shipp.PLANT_CD,
    shipp.SHIPPINGPOINT_NAME,
    material.SAP_MATERIAL_NO AS MATERIAL_NO,
    (select txt from
    TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc where materialdesc.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID AND lang.LANG_CD=materialdesc.LANG_CD) AS MATERIAL_TXT,
    delivtypetx.TXT AS DELIVERY_TYPE_TXT,
    plant.PLANT_NAME,
    sareabs.SORG_CD,
    sorgbs.COMPANYCODE_CD, -- added for authorization
    driverPerson.FIRSTNAME || ' ' || driverPerson.NAME AS DRIVER_FULL_NAME,
    CAST (NVL(deliveredTruck.TRUCK_ID, assignedTruck.TRUCK_ID) AS NUMBER(12,0)) AS TRUCK_ID,
    CAST (NVL(deliveredWagon.WAGON_ID, assignedWagon.WAGON_ID) AS NUMBER(12,0)) AS WAGON_ID,
    CAST (NVL(deliveredSimpleVehicle.SIMPLE_VEHICLE_ID, assignedSimpleVehicle.SIMPLE_VEHICLE_ID) AS NUMBER(12,0)) AS SIMPLE_VEHICLE_ID,
    coalesce(
		nvl2(
			NVL(deliveredTruck.TRUCK_NAME, assignedTruck.TRUCK_NAME),
			NVL(deliveredTruck.TRUCK_NAME, assignedTruck.TRUCK_NAME) || ' (' || NVL(deliveredTruck.TRUCK_PLATE_NO, assignedTruck.TRUCK_PLATE_NO) ||')',
			null
		),
		NVL(deliveredWagon.WAGON_NO, assignedWagon.WAGON_NO),
		NVL(deliveredSimpleVehicle.VEHICLE_NAME, assignedSimpleVehicle.VEHICLE_NAME)
    ) AS VEHICLE_NAME,
    --- used for statistics
    nvl2(deliveredTruck.TRUCK_ID, deliveredTruck.MAX_GROSS_WEIGHT_T - deliveredTruck.TARE_WEIGHT_T, assignedTruck.MAX_GROSS_WEIGHT_T - assignedTruck.TARE_WEIGHT_T) AS TRUCK_MAX_NET_WEIGHT_T,
    nvl2(deliveredWagon.WAGON_ID, deliveredWagon.MAX_GROSS_WEIGHT_T - deliveredWagon.TARE_WEIGHT_T, assignedWagon.MAX_GROSS_WEIGHT_T - assignedWagon.TARE_WEIGHT_T) AS WAGON_MAX_NET_WEIGHT_T,
    nvl2(deliveredSimpleVehicle.SIMPLE_VEHICLE_ID, deliveredSimpleVehicle.MAX_GROSS_WEIGHT_T - deliveredSimpleVehicle.TARE_WEIGHT_T, assignedSimpleVehicle.MAX_GROSS_WEIGHT_T - assignedSimpleVehicle.TARE_WEIGHT_T) AS SIM_VEH_MAX_NET_WEIGHT_T,
    cast(
      NVL(item.DELIVERED_QUANTITY, item.ORDERED_QUANTITY)
      as number
    ) AS STATS_QUANTITY,
    nvl2(item.DELIVERED_QUANTITY, item.DELIVERED_UOM_CD, item.ORDERED_UOM_CD) AS STATS_UOM_CD,
    shiptoBp.TIMEZONE_CD as SHIPTO_TIMEZONE_CD,
    decode(stats_uom.fuzzy_quantity_flag, 0, NVL(item.DELIVERED_QUANTITY, item.ORDERED_QUANTITY) * (
        -- We are only doing a best-effort conversion, ie. no alt UoM lookups.
        case nvl2(item.DELIVERED_QUANTITY, item.DELIVERED_UOM_CD, item.ORDERED_UOM_CD)
          when 'TO' then
            1
          when item.ordered_uom_cd then
            decode(base_uom_dim.uom_dimension_cd, 'TO',
                ((item.ordered_uom_numerator / item.ordered_uom_denominator) *
                    (base_uom.numerator_for_conversion / base_uom.denominator_for_conversion)))
          when item.loaded_uom_cd then
            decode(base_uom_dim.uom_dimension_cd, 'TO',
                ((item.lr_uom_numerator / item.lr_uom_denominator) *
                    (base_uom.numerator_for_conversion / base_uom.denominator_for_conversion)))
          else
            decode(stats_uom_dim.uom_dimension_cd, 'TO',
                (stats_uom.numerator_for_conversion / stats_uom.denominator_for_conversion))
        end
    )) tons,
    material.material_package_cd
FROM TLGNC_ORDER_ITEM item
JOIN TLGNR_LANG_BS lang ON 1=1
JOIN TLGNS_SAP_MATERIAL material ON (material.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID)
JOIN (TLGNC_ORDER ord
  JOIN TLGNC_SAREA sareabs ON (ord.SAREA_ID=sareabs.sarea_id)
  JOIN TLGNR_SORG_BS sorgbs ON (sareabs.sorg_cd = sorgbs.sorg_cd)
  LEFT JOIN TLGNC_CARRIER ccarrier ON (ccarrier.CARRIER_ID=ord.COMM_CARRIER_ID)
  LEFT JOIN TLGNC_CARRIER ecarrier ON (ecarrier.CARRIER_ID=ord.EXEC_CARRIER_ID)
  LEFT JOIN TLGNC_TRUCK deliveredTruck ON (deliveredTruck.TRUCK_ID=ord.DELIVERED_TRUCK_ID)
  LEFT JOIN TLGNC_WAGON deliveredWagon ON (deliveredWagon.WAGON_ID=ord.DELIVERED_WAGON_ID)
  LEFT JOIN TLGNC_SIMPLE_VEHICLE deliveredSimpleVehicle ON (deliveredSimpleVehicle.SIMPLE_VEHICLE_ID=ord.DELIVERED_SIMPLE_VEHICLE_ID)
  LEFT JOIN (TLGNC_SHIPPINGPOINT shipp
    JOIN TLGNR_PLANT_BS plant ON (plant.PLANT_CD=shipp.PLANT_CD)
  ) ON (shipp.SHIPPINGPOINT_ID=ord.SHIPPINGPOINT_ID)
  LEFT JOIN (TLGNC_DRIVER driver
    JOIN TLGNU_USER driverUser ON (driver.USER_ID=driverUser.USER_ID)
    JOIN TLGNC_PERSON driverPerson ON (driverUser.PERSON_ID=driverPerson.PERSON_ID)
  ) ON (ord.DELIVERED_DRIVER_ID=driver.DRIVER_ID)
  JOIN TLGNS_SAP_BP shiptoBp ON (shiptoBp.SAP_BP_ID=ord.SHIPTO_SAP_BP_ID)
  JOIN TLGNS_SAP_BP_SAREA shipto_sarea ON (shipto_sarea.SAREA_ID=ord.SAREA_ID AND shipto_sarea.sap_bp_id=shiptoBp.sap_bp_id)
  JOIN TLGNS_SAP_BP soldtoBp ON (soldtoBp.SAP_BP_ID=ord.SOLDTO_SAP_BP_ID)
  LEFT JOIN TLGNS_SAP_BP billtoBp ON (billtoBp.SAP_BP_ID=ord.BILLTO_SAP_BP_ID)
) ON (item.order_id=ord.order_id)
LEFT JOIN TLGNC_POOL_TX pooltext ON (pooltext.POOL_ID=ord.POOL_ID AND pooltext.LANG_CD=lang.lang_cd)
JOIN TLGNR_ORDER_STATUS_TX status ON (status.LANG_CD=lang.LANG_CD and status.ORDER_STATUS_CD=ord.ORDER_STATUS_CD)
join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1)
JOIN TLGNR_DELIVERY_TYPE_TX delivtypetx ON (delivtypetx.LANG_CD=lang.LANG_CD AND delivtypetx.DELIVERY_TYPE_CD=ord.DELIVERY_TYPE_CD)
LEFT JOIN TLGNC_ASSIGNMENT_PART ap ON (ord.DELIVERED_TRUCK_ID IS NULL AND ord.DELIVERED_WAGON_ID IS NULL) AND (ap.ORDER_ID=ord.ORDER_ID) AND (ap.ASSIGNMENT_PART_TYPE_CD='3')
LEFT JOIN TLGNC_ASSIGNMENT ass ON ass.ASSIGNMENT_ID=ap.ASSIGNMENT_ID AND ass.ACTIVE_FLAG=1 AND ass.ASSIGN_STATUS_CD IN (0, 1, 2, 3, 4)
LEFT JOIN TLGNC_TRUCK assignedTruck ON (assignedTruck.TRUCK_ID=ass.TRUCK_ID)
LEFT JOIN TLGNC_WAGON assignedWagon ON (assignedWagon.WAGON_ID=ass.WAGON_ID)
LEFT JOIN TLGNC_SIMPLE_VEHICLE assignedSimpleVehicle ON (assignedSimpleVehicle.SIMPLE_VEHICLE_ID=ass.SIMPLE_VEHICLE_ID)
LEFT JOIN tlgnr_uom_bs stats_uom on stats_uom.uom_cd = nvl2(item.DELIVERED_QUANTITY, item.DELIVERED_UOM_CD, item.ORDERED_UOM_CD)
LEFT JOIN tlgnr_uom_bs base_uom on material.base_uom_cd = base_uom.uom_cd
LEFT JOIN tlgnr_uom_dimension_bs stats_uom_dim on stats_uom_dim.uom_dimension_cd = stats_uom.uom_dimension_cd and stats_uom.fuzzy_quantity_flag = 0
LEFT JOIN tlgnr_uom_dimension_bs base_uom_dim on base_uom_dim.uom_dimension_cd = base_uom.uom_dimension_cd and base_uom.fuzzy_quantity_flag = 0
LEFT JOIN TLGNC_LOAD load on (load.load_id=item.load_id);

COMMENT ON TABLE VLGNC_ORDER_SEARCH is 'Used to collect the resultlist for UC 4015: Order Search';

REM END VLGNC_ORDER_SEARCH

-------------------------------------------------------------------------------

PROMPT VLGNC_ORDER_SEARCH_EXPORT

-- WARNING: The disaster-recovery job uses this view, when
-- adding/removing/renaming columns, make sure to contact dbeng (Reto)!
CREATE OR REPLACE FORCE VIEW VLGNC_ORDER_SEARCH_EXPORT AS
   SELECT /*+ FIRST_ROWS(25)*/
  item.ORDER_ITEM_ID || '_' || shipto_sarea.SGROUP_CD AS COMPOSITE_ID,
    item.ordered_uom_numerator,
    item.ordered_uom_denominator,
    item.lr_uom_numerator,
    item.lr_uom_denominator,
    ord.ORDER_ID,
    ord.ORDER_CREATION_TYPE_CD,
    ord.SHIPTO_SAP_BP_ID,
    ord.SOLDTO_SAP_BP_ID,
    ord.BILLTO_SAP_BP_ID,
    ord.SAREA_ID,
    item.SAP_MATERIAL_ID,
    ord.ORDER_NUMBER,
    ord.LDS_DELIVERY_NOTE_NO,
    ord.NOTE_ORDER_MGMT,
    ord.NOTE_DELIVERY,
    ord.NOTE_DISPATCHING,
    ord.NOTE_TRUCK_DRIVER,
    ord.NOTE_RAILWAY_DISP,
    ord.ORDER_NUMBER_FROM_SEQ_USAGE,
    load.LOADING_TS,
    item.ORDER_ITEM_NO,
    item.ORDERED_UOM_CD,
    (select txt from TLGNR_UOM_TX where UOM_CD =item.ORDERED_UOM_CD AND LANG_CD=lang.LANG_CD) AS ORDERED_UOM_TXT,
    (select txt from TLGNR_UOM_TX where UOM_CD =item.LOADED_UOM_CD AND LANG_CD=lang.LANG_CD) AS LOADED_UOM_TXT,
    (select txt from TLGNR_UOM_TX where UOM_CD =item.DELIVERED_UOM_CD AND LANG_CD=lang.LANG_CD) AS DELIVERED_UOM_TXT,
    (select txt from TLGNR_UOM_TX where UOM_CD =item.RETURNED_UOM_CD AND LANG_CD=lang.LANG_CD) AS RETURNED_UOM_TXT,
    item.ORDERED_QUANTITY,
    item.LOADED_UOM_CD,
    item.LOADED_QUANTITY,
    item.DELIVERED_UOM_CD,
    item.DELIVERED_QUANTITY,
    item.RETURNED_UOM_CD,
    item.RETURNED_QUANTITY,
    ord.DELIVERY_FROM_DAT,
    ord.DELIVERY_TO_DAT,
    ord.DISTRIBUTION_DEST_CD,
    ord.ORDER_STATUS_CD,
    ord.DELIVERY_TYPE_CD,
    ord.SHIPPINGPOINT_ID,
    ord.TRANSPORT_DURATION_MIN,
    ord.POOL_ID,
    ord.COMM_CARRIER_ID,
    ord.EXEC_CARRIER_ID,
    ord.ACTUAL_DELIVERY_DAT AS PLANNED_DELIVERY_DAT,
    ord.ORDER_VALIDATION_CODE,
	CASE WHEN ord.ITRACK_STATUS_CD = 2 THEN 1 ELSE 0 END AS CHECK_UNLOAD_LOCATION,
    coalesce(ord.ONE_TIME_SOLDTO_NAME, soldtoBp.NAME1) AS SOLDTO_NAME1,
    coalesce(ord.LNF_SITE_NAME, shiptoBp.NAME1) AS SHIPTO_NAME1,
    case when ord.LNF_SITE_POSTAL_CODE is not null and ord.LNF_SITE_CITY is not null and ord.LNF_SITE_STREET_HOUSE_NO is not null then
    	(ord.LNF_SITE_POSTAL_CODE || ' ' || ord.LNF_SITE_CITY || ' ' || ord.LNF_SITE_STREET_HOUSE_NO)
    else
    	(shiptoBp.POSTAL_CODE || ' ' || shiptoBp.CITY || ' ' || shiptoBp.STREET || ' ' || shiptoBp.HOUSE_NUMBER)
   	end AS SHIPTO_ADDRESS,
    shiptoBp.CITY AS SHIPTO_CITY,
    billtoBp.NAME1 AS BILLTO_NAME1,
    shipto_sarea.SAREA_ID SHIPTO_SAREA_ID,
    shipto_sarea.SGROUP_CD SHIPTO_SGROUP_CD,
    shipto_sarea.SGROUP_CD,
    shipto_sarea.SOFFICE_CD SHIPTO_SOFFICE_CD,
    lang.LANG_CD,
    status.TXT AS STATUS_TXT,
    CAST (0 AS NUMBER(2,0)) AS WARN_STATUS_CODE,
    pooltext.NAME AS POOL_NAME,
    --Custom authorization for SAP vendor
    ccarrier.DISPLAY_NAME AS COMM_CARRIER_NAME,
    ccarrier.SAP_VENDOR_ID AS COMM_SAP_VENDOR_ID,
    ecarrier.DISPLAY_NAME AS EXEC_CARRIER_NAME,
    ecarrier.SAP_VENDOR_ID AS EXEC_SAP_VENDOR_ID,
    shipp.PLANT_CD,
    shipp.SHIPPINGPOINT_NAME,
    material.SAP_MATERIAL_NO AS MATERIAL_NO,
    (select txt from
    TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc where materialdesc.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID AND lang.LANG_CD=materialdesc.LANG_CD) AS MATERIAL_TXT,
    delivtypetx.TXT AS DELIVERY_TYPE_TXT,
    plant.PLANT_NAME,
    sareabs.SORG_CD,
    sorgbs.COMPANYCODE_CD, -- added for authorization
    driverPerson.FIRSTNAME || ' ' || driverPerson.NAME AS DRIVER_FULL_NAME,
    CAST (NVL(deliveredTruck.TRUCK_ID, assignedTruck.TRUCK_ID) AS NUMBER(12,0)) AS TRUCK_ID,
    CAST (NVL(deliveredWagon.WAGON_ID, assignedWagon.WAGON_ID) AS NUMBER(12,0)) AS WAGON_ID,
    CAST (NVL(deliveredSimpleVehicle.SIMPLE_VEHICLE_ID, assignedSimpleVehicle.SIMPLE_VEHICLE_ID) AS NUMBER(12,0)) AS SIMPLE_VEHICLE_ID,
    coalesce(
		nvl2(
			NVL(deliveredTruck.TRUCK_NAME, assignedTruck.TRUCK_NAME),
			NVL(deliveredTruck.TRUCK_NAME, assignedTruck.TRUCK_NAME) || ' (' || NVL(deliveredTruck.TRUCK_PLATE_NO, assignedTruck.TRUCK_PLATE_NO) ||')',
			null
		),
		NVL(deliveredWagon.WAGON_NO, assignedWagon.WAGON_NO),
		NVL(deliveredSimpleVehicle.VEHICLE_NAME, assignedSimpleVehicle.VEHICLE_NAME)
    ) AS VEHICLE_NAME,
    --- used for statistics
    nvl2(deliveredTruck.TRUCK_ID, deliveredTruck.MAX_GROSS_WEIGHT_T - deliveredTruck.TARE_WEIGHT_T, assignedTruck.MAX_GROSS_WEIGHT_T - assignedTruck.TARE_WEIGHT_T) AS TRUCK_MAX_NET_WEIGHT_T,
    nvl2(deliveredWagon.WAGON_ID, deliveredWagon.MAX_GROSS_WEIGHT_T - deliveredWagon.TARE_WEIGHT_T, assignedWagon.MAX_GROSS_WEIGHT_T - assignedWagon.TARE_WEIGHT_T) AS WAGON_MAX_NET_WEIGHT_T,
    nvl2(deliveredSimpleVehicle.SIMPLE_VEHICLE_ID, deliveredSimpleVehicle.MAX_GROSS_WEIGHT_T - deliveredSimpleVehicle.TARE_WEIGHT_T, assignedSimpleVehicle.MAX_GROSS_WEIGHT_T - assignedSimpleVehicle.TARE_WEIGHT_T) AS SIM_VEH_MAX_NET_WEIGHT_T,
    cast(
      NVL(item.DELIVERED_QUANTITY, item.ORDERED_QUANTITY)
      as number
    ) AS STATS_QUANTITY,
    nvl2(item.DELIVERED_QUANTITY, item.DELIVERED_UOM_CD, item.ORDERED_UOM_CD) AS STATS_UOM_CD,
    shiptoBp.TIMEZONE_CD as SHIPTO_TIMEZONE_CD,
    decode(stats_uom.fuzzy_quantity_flag, 0, NVL(item.DELIVERED_QUANTITY, item.ORDERED_QUANTITY) * (
        -- We are only doing a best-effort conversion, ie. no alt UoM lookups.
        case nvl2(item.DELIVERED_QUANTITY, item.DELIVERED_UOM_CD, item.ORDERED_UOM_CD)
          when 'TO' then
            1
          when item.ordered_uom_cd then
            decode(base_uom_dim.uom_dimension_cd, 'TO',
                ((item.ordered_uom_numerator / item.ordered_uom_denominator) *
                    (base_uom.numerator_for_conversion / base_uom.denominator_for_conversion)))
          when item.loaded_uom_cd then
            decode(base_uom_dim.uom_dimension_cd, 'TO',
                ((item.lr_uom_numerator / item.lr_uom_denominator) *
                    (base_uom.numerator_for_conversion / base_uom.denominator_for_conversion)))
          else
            decode(stats_uom_dim.uom_dimension_cd, 'TO',
                (stats_uom.numerator_for_conversion / stats_uom.denominator_for_conversion))
        end
    )) tons,
    material.material_package_cd,
    custorder.CUSTORDER_CUSTREF_NO
FROM TLGNC_ORDER_ITEM item
JOIN TLGNR_LANG_BS lang ON 1=1
JOIN TLGNS_SAP_MATERIAL material ON (material.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID)
JOIN (TLGNC_ORDER ord
  JOIN TLGNC_SAREA sareabs ON (ord.SAREA_ID=sareabs.sarea_id)
  JOIN TLGNR_SORG_BS sorgbs ON (sareabs.sorg_cd = sorgbs.sorg_cd)
  LEFT JOIN TLGNC_CARRIER ccarrier ON (ccarrier.CARRIER_ID=ord.COMM_CARRIER_ID)
  LEFT JOIN TLGNC_CARRIER ecarrier ON (ecarrier.CARRIER_ID=ord.EXEC_CARRIER_ID)
  LEFT JOIN TLGNC_TRUCK deliveredTruck ON (deliveredTruck.TRUCK_ID=ord.DELIVERED_TRUCK_ID)
  LEFT JOIN TLGNC_WAGON deliveredWagon ON (deliveredWagon.WAGON_ID=ord.DELIVERED_WAGON_ID)
  LEFT JOIN TLGNC_SIMPLE_VEHICLE deliveredSimpleVehicle ON (deliveredSimpleVehicle.SIMPLE_VEHICLE_ID=ord.DELIVERED_SIMPLE_VEHICLE_ID)
  LEFT JOIN (TLGNC_SHIPPINGPOINT shipp
    JOIN TLGNR_PLANT_BS plant ON (plant.PLANT_CD=shipp.PLANT_CD)
  ) ON (shipp.SHIPPINGPOINT_ID=ord.SHIPPINGPOINT_ID)
  LEFT JOIN (TLGNC_DRIVER driver
    JOIN TLGNU_USER driverUser ON (driver.USER_ID=driverUser.USER_ID)
    JOIN TLGNC_PERSON driverPerson ON (driverUser.PERSON_ID=driverPerson.PERSON_ID)
  ) ON (ord.DELIVERED_DRIVER_ID=driver.DRIVER_ID)
  JOIN TLGNS_SAP_BP shiptoBp ON (shiptoBp.SAP_BP_ID=ord.SHIPTO_SAP_BP_ID)
  JOIN TLGNS_SAP_BP_SAREA shipto_sarea ON (shipto_sarea.SAREA_ID=ord.SAREA_ID AND shipto_sarea.sap_bp_id=shiptoBp.sap_bp_id)
  JOIN TLGNS_SAP_BP soldtoBp ON (soldtoBp.SAP_BP_ID=ord.SOLDTO_SAP_BP_ID)
  LEFT JOIN TLGNS_SAP_BP billtoBp ON (billtoBp.SAP_BP_ID=ord.BILLTO_SAP_BP_ID)
) ON (item.order_id=ord.order_id)
LEFT JOIN TLGNC_POOL_TX pooltext ON (pooltext.POOL_ID=ord.POOL_ID AND pooltext.LANG_CD=lang.lang_cd)
JOIN TLGNR_ORDER_STATUS_TX status ON (status.LANG_CD=lang.LANG_CD and status.ORDER_STATUS_CD=ord.ORDER_STATUS_CD)
join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1)
JOIN TLGNR_DELIVERY_TYPE_TX delivtypetx ON (delivtypetx.LANG_CD=lang.LANG_CD AND delivtypetx.DELIVERY_TYPE_CD=ord.DELIVERY_TYPE_CD)
LEFT JOIN TLGNC_ASSIGNMENT_PART ap ON (ord.DELIVERED_TRUCK_ID IS NULL AND ord.DELIVERED_WAGON_ID IS NULL) AND (ap.ORDER_ID=ord.ORDER_ID) AND (ap.ASSIGNMENT_PART_TYPE_CD='3')
LEFT JOIN TLGNC_ASSIGNMENT ass ON ass.ASSIGNMENT_ID=ap.ASSIGNMENT_ID AND ass.ACTIVE_FLAG=1 AND ass.ASSIGN_STATUS_CD IN (0, 1, 2, 3, 4)
LEFT JOIN TLGNC_TRUCK assignedTruck ON (assignedTruck.TRUCK_ID=ass.TRUCK_ID)
LEFT JOIN TLGNC_WAGON assignedWagon ON (assignedWagon.WAGON_ID=ass.WAGON_ID)
LEFT JOIN TLGNC_SIMPLE_VEHICLE assignedSimpleVehicle ON (assignedSimpleVehicle.SIMPLE_VEHICLE_ID=ass.SIMPLE_VEHICLE_ID)
LEFT JOIN tlgnr_uom_bs stats_uom on stats_uom.uom_cd = nvl2(item.DELIVERED_QUANTITY, item.DELIVERED_UOM_CD, item.ORDERED_UOM_CD)
LEFT JOIN tlgnr_uom_bs base_uom on material.base_uom_cd = base_uom.uom_cd
LEFT JOIN tlgnr_uom_dimension_bs stats_uom_dim on stats_uom_dim.uom_dimension_cd = stats_uom.uom_dimension_cd and stats_uom.fuzzy_quantity_flag = 0
LEFT JOIN tlgnr_uom_dimension_bs base_uom_dim on base_uom_dim.uom_dimension_cd = base_uom.uom_dimension_cd and base_uom.fuzzy_quantity_flag = 0
LEFT JOIN TLGNC_LOAD load on (load.load_id=item.load_id)
LEFT JOIN TLGNC_CUSTORDER custorder on (ord.custorder_id = custorder.custorder_id);

COMMENT ON TABLE VLGNC_ORDER_SEARCH_EXPORT is 'Used to collect the resultlist for UC 4015: Order Search';

REM END VLGNC_ORDER_SEARCH_EXPORT

-------------------------------------------------------------------------------

prompt vlgnc_order_search_stat

create or replace force view vlgnc_order_search_stat as
  select /*+ NO_MERGE(vlgnc_order_search) */
    ords.composite_id,
    ords.lang_cd,
    ords.order_number,
    ords.order_creation_type_cd,
    ords.soldto_sap_bp_id,
    ords.shipto_sap_bp_id,
    ords.billto_sap_bp_id,
    ords.sap_material_id,
    ords.delivery_from_dat,
    ords.delivery_to_dat,
    ords.delivery_type_cd,
    ords.shippingpoint_id,
    ords.pool_id,
    ords.comm_carrier_id,
    ords.exec_carrier_id,
    ords.sarea_id,
    ords.shipto_soffice_cd,
    ords.shipto_sgroup_cd,
    ords.sgroup_cd,
    ords.companycode_cd, -- added for authorization
    --custom authorization for sap vendor
    ords.comm_sap_vendor_id,
    ords.exec_sap_vendor_id,
    ords.plant_cd,
    cast (ords.truck_id as number(12,0)) as truck_id,
    cast (ords.wagon_id as number(12,0)) as wagon_id,
    cast (ords.simple_vehicle_id as number(12,0)) as simple_vehicle_id,
    ords.distribution_dest_cd,
    ords.order_status_cd,
    ords.stats_quantity,
    ords.stats_uom_cd,
    decode(stats_uom.fuzzy_quantity_flag, 0, ords.stats_quantity * (
        -- We are only doing a best-effort conversion, ie. no alt UoM lookups.
        case ords.stats_uom_cd
          when 'TO' then
            1
          when ords.ordered_uom_cd then
            decode(base_uom_dim.uom_dimension_cd, 'TO',
                ((ords.ordered_uom_numerator / ords.ordered_uom_denominator) *
                    (base_uom.numerator_for_conversion / base_uom.denominator_for_conversion)))
          when ords.loaded_uom_cd then
            decode(base_uom_dim.uom_dimension_cd, 'TO',
                ((ords.lr_uom_numerator / ords.lr_uom_denominator) *
                    (base_uom.numerator_for_conversion / base_uom.denominator_for_conversion)))
          else
            decode(stats_uom_dim.uom_dimension_cd, 'TO',
                (stats_uom.numerator_for_conversion / stats_uom.denominator_for_conversion))
        end
    )) tons
  from
    vlgnc_order_search ords
    left join tlgnr_uom_bs stats_uom on stats_uom.uom_cd = ords.stats_uom_cd
    left join tlgnr_uom_dimension_bs stats_uom_dim on
        stats_uom_dim.uom_dimension_cd = stats_uom.uom_dimension_cd
        and stats_uom.fuzzy_quantity_flag = 0
    left join tlgnc_wagon wagon on wagon.wagon_id = ords.wagon_id
    left join tlgnr_wagon_type_bs wagon_type on wagon_type.wagon_type_cd = wagon.wagon_type_cd
    left join tlgns_sap_material material on ords.sap_material_id = material.sap_material_id
    left join tlgnr_uom_bs base_uom on material.base_uom_cd = base_uom.uom_cd
    left join tlgnr_uom_dimension_bs base_uom_dim on
        base_uom_dim.uom_dimension_cd = base_uom.uom_dimension_cd
        and base_uom.fuzzy_quantity_flag = 0;

comment on table vlgnc_order_search_stat is 'Used to collect the statistics on order search screen';

rem end vlgnc_order_search_stat

-------------------------------------------------------------------------------

PROMPT VLGNC_EXT_ORDER_SEARCH

CREATE OR REPLACE FORCE VIEW VLGNC_EXT_ORDER_SEARCH AS
SELECT distinct
ord.ext_order_id || '_' || statustext.lang_cd as composite_id,
ord.ext_order_id,
ord.ext_order_number,
ord.delivery_from_dat,
ord.delivery_to_dat,
ord.carrier_id,
carrier.display_name as carrier_name,
carrier.sap_vendor_id,
ord.pool_id,
ord.order_status_cd,
sp.site_name as shippingpoint_name,
sp.site_load_unload as sp_load_unload,
sp.site_contact_person_txt as sp_contact_person_txt,
sp.site_street_house_no as sp_street_house_no,
sp.site_country as sp_country,
sp.site_postal_code as sp_postal_code,
sp.site_city as sp_city,
shipto.site_name as shipto_name,
shipto.site_load_unload as shipto_load_unload,
shipto.site_contact_person_txt as shipto_contact_person_txt,
shipto.site_street_house_no as shipto_street_house_no,
shipto.site_country as shipto_country,
shipto.site_postal_code as shipto_postal_code,
shipto.site_city as shipto_city,
statustext.lang_cd,
statustext.txt as status_txt,
pooltext.name as pool_name,
ord.ordered_quantity,
ord.loaded_quantity,
ord.delivered_quantity,
ord.cust_delivery_note_comment as ext_delivery_note,
mat.material_name,
orduomtx.txt as ordered_uom_name,
orduomtx.uom_cd as ordered_uom_cd,
deluomtx.txt as delivered_uom_name,
deluomtx.uom_cd as delivered_uom_cd,
CAST (NVL(deliveredTruck.truck_id, assignedTruck.truck_id) as NUMBER(12,0)) as truck_id,
nvl2(
	nvl(deliveredTruck.truck_name, assignedTruck.truck_name),
	nvl(deliveredTruck.truck_name, assignedTruck.truck_name) || ' (' || nvl(deliveredTruck.truck_plate_no, assignedTruck.truck_plate_no) ||')',
	null
) as truck_name,
cast(NVL(deliveredTruck.sap_vehicle_is_internal_flag,
    assignedTruck.sap_vehicle_is_internal_flag) as number(1,0)) as truck_sap_internal_flag
from tlgnc_ext_order ord
join tlgnc_ext_site sp on (ord.loading_ext_site_id = sp.ext_site_id)
join tlgnc_ext_site shipto on (ord.unloading_ext_site_id = shipto.ext_site_id)
join tlgnc_ext_material mat on (ord.ext_material_id = mat.ext_material_id)
left join tlgnr_order_status_tx statustext on (statustext.order_status_cd = ord.order_status_cd)
left join tlgnc_pool_tx pooltext on (pooltext.pool_id = ord.pool_id and pooltext.lang_cd = statustext.lang_cd)
left join tlgnc_carrier carrier on (ord.carrier_id = carrier.carrier_id)
left join tlgnr_uom_tx orduomtx on (ord.ordered_uom_cd = orduomtx.uom_cd and orduomtx.lang_cd = statustext.lang_cd)
left join tlgnr_uom_tx deluomtx on (ord.loaded_and_delivered_uom_cd = deluomtx.uom_cd and deluomtx.lang_cd = statustext.lang_cd)
left join tlgnc_truck deliveredTruck on (deliveredTruck.truck_id = ord.delivered_truck_id)
left outer join (
  TLGNC_ASSIGNMENT_PART ap
   left outer join TLGNC_ASSIGNMENT ass on (ap.ASSIGNMENT_ID = ass.ASSIGNMENT_ID and ass.active_flag = 1 and ass.assign_status_cd < 6)
   left outer join TLGNC_TRUCK assignedtruck on (ass.TRUCK_ID = assignedtruck.TRUCK_ID)
  ) on (ord.EXT_ORDER_ID = ap.EXT_ORDER_ID);


COMMENT ON TABLE VLGNC_EXT_ORDER_SEARCH is 'Used to collect the resultlist for NGE External Order Search';

REM END VLGNC_EXT_ORDER_SEARCH

-------------------------------------------------------------------------------
PROMPT VLGNC_VEHICLE

CREATE OR REPLACE FORCE VIEW VLGNC_VEHICLE AS
  SELECT
  'truck_' || truck.truck_id AS composite_id,
  truck.truck_id AS vehicle_id,
  truck.truck_name || ' (' ||  truck.truck_plate_no || ')' AS vehicle_name,
  cast('truck' AS varchar2(5)) AS vehicle_type
FROM tlgnc_truck truck
UNION
SELECT
  'wagon_' || w.wagon_id AS composite_id,
  w.wagon_id AS vehicle_id,
  w.wagon_no AS vehicle_name,
  cast('wagon' AS varchar2(5)) AS vehicle_type
FROM tlgnc_wagon w
UNION
SELECT
  'simve_' || sv.simple_vehicle_id AS composite_id,
  sv.simple_vehicle_id AS vehicle_id,
  sv.vehicle_name AS vehicle_name,
  cast('simve' AS varchar2(5)) AS vehicle_type
FROM tlgnc_simple_vehicle sv;

COMMENT ON TABLE VLGNC_VEHICLE is 'Used for the vehicle filter in Order Search and Order Monitoring';

REM END VLGNC_VEHICLE

-------------------------------------------------------------------------------
PROMPT VLGNC_POOL_SHIPTO_MAT_SOLDTO

CREATE OR REPLACE FORCE VIEW VLGNC_POOL_SHIPTO_MAT_SOLDTO AS
  SELECT DISTINCT
         restr.shipto_sarea_restr_id,
         shipto_bp.NAME1 AS SHIPTO_NAME,
         shipto_bp.SAP_ID AS SHIPTO_NR,
         restr.POOL_ID,
         pooltx.NAME AS POOL_NAME,
         sap_mat.sap_material_no as MATERIAL_NR,
         sap_mat.BASE_UOM_CD,
         mat_descript.TXT as MATERIAL_DESCRIPTION,
         mat_descript.LANG_CD as MATERIAL_DESC_LANG_CD,
         soldto.NAME1 AS SOLDTO_NAME,
         soldto.SAP_BP_ID AS SOLDTO_ID,
         plant.plant_name,
         restr.AUTO_CARRIER_ASSIGNMENT_FLAG,
         restr.COMM_CARRIER_ID,
         ccarrier.DISPLAY_NAME as COMM_CARRIER_NAME
  FROM
    TLGNC_SHIPTO_SAREA_RESTR restr
  LEFT OUTER JOIN TLGNC_POOL pool on (pool.POOL_ID = restr.POOL_ID)
  LEFT OUTER JOIN TLGNC_SHIPTO_SAREA tlgnc_shipto_sarea on (restr.SHIPTO_SAREA_ID = tlgnc_shipto_sarea.SHIPTO_SAREA_ID)
  LEFT OUTER JOIN TLGNC_SHIPTO tlgnc_shipto on (tlgnc_shipto_sarea.SHIPTO_ID = tlgnc_shipto.SHIPTO_ID)
  LEFT OUTER JOIN TLGNS_SAP_MATERIAL sap_mat on (restr.SAP_MATERIAL_ID = sap_mat.SAP_MATERIAL_ID)
  LEFT OUTER JOIN TLGNS_SAP_MATERIAL_DESCRIPTION mat_descript on (sap_mat.SAP_MATERIAL_ID = mat_descript.SAP_MATERIAL_ID)
  LEFT OUTER JOIN TLGNS_SAP_BP shipto_bp on ( shipto_bp.BP_IS_SHIPTO = 1 and shipto_bp.SAP_BP_ID = tlgnc_shipto.SHIPTO_SAP_BP_ID)
  LEFT OUTER JOIN TLGNS_SAP_BP_SAREA shipto_sarea on ( shipto_sarea.SAP_BP_ID = shipto_bp.SAP_BP_ID )
  LEFT OUTER JOIN TLGNC_SAREA sarea on (sarea.sarea_id = tlgnc_shipto_sarea.SAREA_ID)
  LEFT OUTER JOIN TLGNS_SAP_BP_PF_SHIPTO pf_soldto_shipto on (pf_soldto_shipto.SHIPTO_SAP_BP_SAREA_ID = shipto_sarea.SAP_BP_SAREA_ID
    and pf_soldto_shipto.SAREA_ID = sarea.sarea_id)
  LEFT OUTER JOIN TLGNS_SAP_BP_SAREA soldto_sarea on (pf_soldto_shipto.SAP_BP_SAREA_ID = soldto_sarea.SAP_BP_SAREA_ID)
  LEFT OUTER JOIN TLGNS_SAP_BP soldto on (soldto.BP_IS_SOLDTO = 1
    and soldto_sarea.SAP_BP_ID = soldto.SAP_BP_ID)
  LEFT OUTER JOIN TLGNC_POOL_tx pooltx on (pooltx.POOL_ID = restr.POOL_ID and pooltx.LANG_CD=mat_descript.LANG_CD)
  LEFT OUTER JOIN TLGNR_PLANT_BS plant on (restr.PRODUCTION_PLANT_CD = plant.PLANT_CD)
  LEFT OUTER JOIN TLGNC_CARRIER ccarrier on (restr.COMM_CARRIER_ID = ccarrier.CARRIER_ID);

COMMENT ON TABLE VLGNC_POOL_SHIPTO_MAT_SOLDTO is 'used for the Transporteurzuteilung page';

REM END ULGN01 VLGNC_POOL_SHIPTO_MAT_SOLDTO
-------------------------------------------------------------------------------
PROMPT VLGNS_BP_SAREA_SOFFICE_SGROUP

create or replace force view VLGNS_BP_SAREA_SOFFICE_SGROUP as
select distinct
  bs.sap_bp_sarea_id,
  bs.sap_bp_id,
  bs.SAREA_ID,
  office.soffice_cd,
  office.soffice_name,
  sgroup.sgroup_cd,
  sgroup.sgroup_name
from
  TLGNS_SAP_BP_SAREA bs
left outer join TLGNR_SOFFICE_BS office on ( bs.soffice_cd = office.soffice_cd )
left outer join TLGNR_SGROUP_BS sgroup on ( bs.sgroup_cd = sgroup.sgroup_cd );

comment on table VLGNS_BP_SAREA_SOFFICE_SGROUP is 'used at the Transporteurzuteilung page';

REM END ULGN01 VLGNS_BP_SAREA_SOFFICE_SGROUP
-------------------------------------------------------------------------------
PROMPT VLGNC_ORDER_FILTERS


CREATE OR REPLACE FORCE VIEW VLGNC_ORDER_FILTERS AS
  SELECT distinct
    ord.ORDER_ID|| '_' || status.LANG_CD as COMPOSITE_ID,
    ord.ORDER_ID,
    ord.ORDER_NUMBER,
    item.PRODUCTION_PLANT_CD,
    plant.PLANT_NAME,
    ord.SHIPPINGPOINT_ID,
    shipp.SHIPPINGPOINT_NAME,
    shipp.PLANT_CD,
    load.LOADINGPOINT_ID,
    lp.LOADINGPOINT_NAME,
    ord.POOL_ID,
    pooltext.NAME as POOL_NAME,
    ord.COMM_CARRIER_ID,
    ccarrier.DISPLAY_NAME as COMM_CARRIER_NAME,
    --Authorizer will add authorization part according to commercial carrier
    ccarrier.SAP_VENDOR_ID AS SAP_VENDOR_ID,
    ass.ACTIVE_FLAG ASSIGNMENT_ACTIVE_FLAG,
    ass.truck_id,
    ass.wagon_id,
    trailer.trailer_id,
    cont.container_id,
    truck.truck_name,
    driver.person_id DRIVER_ID,
    driver.firstname || '-' || driver.name DRIVER_NAME,
    bp_sarea.SOFFICE_CD,
    soffice.SOFFICE_NAME,
    bp_sarea.SGROUP_CD,
    sgroup.SGROUP_NAME,
    ord.SAREA_ID,
    sarea.SORG_CD || '-' || sarea.DISTRIBUTION_CHANNEL || '-' || sarea.division_cd as SAREA_NAME,
    ord.SHIPTO_SAP_BP_ID,
    shiptoBp.NAME1 as SHIPTO_NAME1,
    ord.SOLDTO_SAP_BP_ID,
    soldtoBp.NAME1 as SOLDTO_NAME1,
    item.SAP_MATERIAL_ID,
    material.SAP_MATERIAL_NO as MATERIAL_NO,
    materialdesc.txt as MATERIAL_TXT,
    status.TXT as STATUS_TXT,
    status.LANG_CD,
    ord.DELIVERY_FROM_DAT,
    ord.DELIVERY_TO_DAT,
    ord.ACTUAL_DELIVERY_DAT,
    ord.ORDER_STATUS_CD,
    item.order_item_no,
    plant.COORD_LAT PLANT_COORD_LAT,
    plant.COORD_LON PLANT_COORD_LON,
    DECODE(shipto.shipto_type_cd,2,ord.lnf_coord_lat, shipto.coord_lat) shipto_coord_lat,
    DECODE(shipto.shipto_type_cd,2,ord.lnf_coord_lon, shipto.coord_lon) shipto_coord_lon,
    shipto.GEOFENCE_RADIUS SHIPTO_GEOFENCE_RADIUS,
    shipp.COORD_LAT SHIPP_COORD_LAT,
    shipp.COORD_LON SHIPP_COORD_LON,
    shipp.GEOFENCE_RADIUS SHIPP_GEOFENCE_RADIUS,
    load.LOADING_TS,
    load.UNLOADING_TS,
    shiptoBp.TIMEZONE_CD as SHIPTO_TIMEZONE_CD
FROM
    TLGNC_ORDER ord
left outer join TLGNC_ORDER_ITEM item on (ord.order_id=item.order_id)
left outer join TLGNR_ORDER_STATUS_TX status on (status.ORDER_STATUS_CD=ord.ORDER_STATUS_CD)
left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc on (materialdesc.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID and status.LANG_CD=materialdesc.LANG_CD )
left outer join TLGNC_POOL pool on (pool.POOL_ID=ord.POOL_ID)
left outer join TLGNC_SHIPPINGPOINT shipp on (shipp.SHIPPINGPOINT_ID=ord.SHIPPINGPOINT_ID)
left outer join TLGNS_SAP_BP soldtoBp on (ord.SOLDTO_SAP_BP_ID=soldtoBp.SAP_BP_ID)
left outer join TLGNS_SAP_BP shiptoBp on (ord.SHIPTO_SAP_BP_ID=shiptoBp.SAP_BP_ID)
left outer join TLGNC_CARRIER ccarrier on (ccarrier.CARRIER_ID=ord.COMM_CARRIER_ID)
left outer join TLGNR_LANG_BS lang on (status.LANG_CD=lang.LANG_CD)
left outer join TLGNC_POOL_TX pooltext on (pooltext.POOL_ID=ord.POOL_ID and pooltext.LANG_CD=lang.lang_cd)
left outer join TLGNS_SAP_MATERIAL material on (material.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID)
left outer join TLGNR_PLANT_BS plant on (plant.PLANT_CD=item.PRODUCTION_PLANT_CD)
left outer join TLGNC_SAREA sarea on (sarea.sarea_id=ord.SAREA_ID)
left outer join TLGNS_SAP_BP_SAREA bp_sarea on (bp_sarea.SAREA_ID=ord.SAREA_ID and bp_sarea.SAP_BP_ID=shiptoBp.SAP_BP_ID)
left outer join TLGNR_SOFFICE_BS soffice on (soffice.SOFFICE_CD=bp_sarea.SOFFICE_CD)
left outer join TLGNR_SGROUP_BS sgroup on (sgroup.SGROUP_CD=bp_sarea.SGROUP_CD)
left outer join TLGNC_ASSIGNMENT_PART assp on (ord.order_id=assp.order_id)
left outer join TLGNC_ASSIGNMENT ass on (ass.assignment_id=assp.assignment_id and ass.ACTIVE_FLAG = 1)
left outer join TLGNC_TRUCK truck on (truck.truck_id=ass.truck_id)
left outer join VLGNC_DRIVER_PERSON driver on (driver.truck_id=truck.truck_id)
left outer join TLGNC_LOAD load on (load.load_id=item.load_id)
left outer join TLGNC_LOADINGPOINT lp on (lp.LOADINGPOINT_ID=load.LOADINGPOINT_ID)
left outer join TLGNC_TRAILER trailer on (ass.truck_id = trailer.current_truck_id)
left outer join TLGNC_CONTAINER cont on (ass.truck_id = cont.current_truck_id or ass.wagon_id = cont.current_wagon_id or trailer.trailer_id = cont.current_trailer_id)
INNER JOIN TLGNC_SHIPTO shipto ON (ord.SHIPTO_SAP_BP_ID = shipto.shipto_sap_bp_id and shipto.active_flag = 1);

comment on table VLGNC_ORDER_FILTERS is 'Used at the Live Karte page to show filters';

REM END ULGN01 VLGNC_ORDER_FILTERS
-------------------------------------------------------------------------------
PROMPT VLGNC_BIZEVENT_MAP

CREATE OR REPLACE FORCE VIEW VLGNC_BIZEVENT_MAP AS
SELECT biz.bizevent_id,
      biz.bizevent_type_cd,
      biz_txt.lang_cd,
      biz_txt.txt bizevent_type_txt,
      biz.description,
      biz.truck_id,
      truck.truck_name,
      truck.truck_plate_no,
      truck_type.txt truck_type_txt,
      driver.firstname || ' ' || driver.name driver_name,
      biz.trailer_id,
      trailer.trailer_name,
      trailer.trailer_plate_no,
      biz.container_id,
      cont.container_no,
      cont.container_name,
      biz.wagon_id,
      wagon.wagon_no,
      biz.order_id,
      biz.assignment_id,
      biz.coord_lat,
      biz.coord_lon,
      DECODE(shipto.shipto_type_cd,2,ord.lnf_coord_lat, shipto.coord_lat) shipto_coord_lat,
      DECODE(shipto.shipto_type_cd,2,ord.lnf_coord_lon, shipto.coord_lon) shipto_coord_lon,
      ord.delivery_to_dat,
      biz.event_ts,
      biz.ctl_cre_ts bizevent_cre_ts
   FROM tlgnc_bizevent biz
   INNER JOIN tlgnr_bizevent_type_tx biz_txt ON (biz_txt.bizevent_type_cd  = biz.bizevent_type_cd )
   INNER JOIN tlgnc_order ord ON (biz.order_id = ord.order_id)
   LEFT OUTER JOIN tlgnc_shipto shipto ON (ord.shipto_sap_bp_id = shipto.shipto_sap_bp_id and shipto.active_flag = 1)
   LEFT OUTER JOIN tlgnc_truck truck ON (truck.truck_id = biz.truck_id)
   LEFT OUTER JOIN vlgnc_driver_person driver on (driver.truck_id=truck.truck_id)
   LEFT OUTER JOIN tlgnr_truck_type_tx truck_type ON (truck.truck_type_cd = truck_type.truck_type_cd and biz_txt.lang_cd = truck_type.lang_cd)
   LEFT OUTER JOIN tlgnc_trailer trailer ON (trailer.trailer_id = biz.trailer_id)
   LEFT OUTER JOIN tlgnc_container cont ON (cont.container_id = biz.container_id)
   LEFT OUTER JOIN tlgnc_wagon wagon ON (wagon.wagon_id = biz.wagon_id)
   WHERE  biz.coord_lat IS NOT NULL
      AND biz.coord_lon IS NOT NULL
      AND biz.order_id IS NOT NULL
UNION ALL
  SELECT biz.bizevent_id,
      biz.bizevent_type_cd,
      biz_txt.lang_cd,
      biz_txt.txt bizevent_type_txt,
      biz.description,
      biz.truck_id,
      truck.truck_name,
      truck.truck_plate_no,
      truck_type.txt truck_type_txt,
      driver.firstname || ' ' || driver.name driver_name,
      biz.trailer_id,
      trailer.trailer_name,
      trailer.trailer_plate_no,
      biz.container_id,
      cont.container_no,
      cont.container_name,
      biz.wagon_id,
      wagon.wagon_no,
      assp.order_id,
      biz.assignment_id,
      biz.coord_lat,
      biz.coord_lon,
      DECODE(shipto.shipto_type_cd,2,ord.lnf_coord_lat, shipto.coord_lat) shipto_coord_lat,
      DECODE(shipto.shipto_type_cd,2,ord.lnf_coord_lon, shipto.coord_lon) shipto_coord_lon,
      ord.delivery_to_dat,
      biz.event_ts,
      biz.ctl_cre_ts bizevent_cre_ts
   FROM tlgnc_assignment_part assp
   INNER JOIN tlgnc_bizevent biz ON (assp.assignment_id = biz.assignment_id )
   INNER JOIN tlgnr_bizevent_type_tx biz_txt ON (biz_txt.bizevent_type_cd  = biz.bizevent_type_cd )
   INNER JOIN tlgnc_order ord ON (assp.order_id = ord.order_id)
   LEFT OUTER JOIN tlgnc_shipto shipto ON (ord.shipto_sap_bp_id = shipto.shipto_sap_bp_id and shipto.active_flag = 1)
   LEFT OUTER JOIN tlgnc_truck truck ON (truck.truck_id = biz.truck_id)
   LEFT OUTER JOIN vlgnc_driver_person driver on (driver.truck_id=truck.truck_id)
   LEFT OUTER JOIN tlgnr_truck_type_tx truck_type ON (truck.truck_type_cd = truck_type.truck_type_cd and biz_txt.lang_cd = truck_type.lang_cd)
   LEFT OUTER JOIN tlgnc_trailer trailer ON (trailer.trailer_id = biz.trailer_id)
   LEFT OUTER JOIN tlgnc_container cont ON (cont.container_id = biz.container_id)
   LEFT OUTER JOIN tlgnc_wagon wagon ON (wagon.wagon_id = biz.wagon_id)
   WHERE  biz.coord_lat IS NOT NULL
      AND biz.coord_lon IS NOT NULL
      AND biz.order_id IS NULL;

comment on table VLGNC_BIZEVENT_MAP is 'used at the Live Karte page to show dynamic objects(trucks, trailers, etc.)';

REM END ULGN01 VLGNC_BIZEVENT_MAP
-------------------------------------------------------------------------------
PROMPT VLGNC_CREDIT_NOTE_PROPOSALS

CREATE OR REPLACE FORCE VIEW VLGNC_CREDIT_NOTE_PROPOSALS AS
  SELECT
    item.ORDER_ITEM_ID,
    item.ORDER_ITEM_NO,
    item.ORDER_ID,
    ord.ORDER_NUMBER,
    plant.PLANT_NAME,
    soldtoBp.NAME1 as SOLDTO_NAME,
    shiptoBp.NAME1 as SHIPTO_NAME,
    billtoBp.NAME1 as BILLTO_NAME,
    material.SAP_MATERIAL_NO,
    materialdesc.txt as MATERIAL_TXT,
    materialdesc.LANG_CD,
    item.DELIVERED_UOM_CD,
    deliveredunit.TXT DELIVERED_UOM_TXT,
    item.DELIVERED_QUANTITY,
    item.RETURNED_UOM_CD,
    returnedunit.TXT RETURNED_UOM_TXT,
    item.RETURNED_QUANTITY,
    item.RETURN_QUANT_HANDLING_CD,
    compcode.COMPANYCODE_CD
FROM
                TLGNC_ORDER ord
left outer join TLGNC_ORDER_ITEM item on (ord.order_id=item.order_id)
left outer join TLGNC_SHIPPINGPOINT shipp on (shipp.SHIPPINGPOINT_ID=ord.SHIPPINGPOINT_ID)
left outer join TLGNR_PLANT_BS plant on (shipp.PLANT_CD = plant.PLANT_CD)
left outer join TLGNC_PLANT_COMPCODE compcode on (plant.PLANT_CD = compcode.PLANT_CD)
left outer join TLGNS_SAP_MATERIAL material on (material.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID)
left outer join TLGNS_SAP_BP soldtoBp on (ord.SOLDTO_SAP_BP_ID=soldtoBp.SAP_BP_ID)
left outer join TLGNS_SAP_BP shiptoBp on (ord.SHIPTO_SAP_BP_ID=shiptoBp.SAP_BP_ID)
left outer join TLGNS_SAP_BP billtoBp on (ord.BILLTO_SAP_BP_ID=billtoBp.SAP_BP_ID)
left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc on (materialdesc.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID)
left outer join TLGNR_UOM_TX deliveredunit on (item.DELIVERED_UOM_CD=deliveredunit.UOM_CD and deliveredunit.LANG_CD=materialdesc.LANG_CD)
left outer join TLGNR_UOM_TX returnedunit on (item.RETURNED_UOM_CD=returnedunit.UOM_CD and returnedunit.LANG_CD=materialdesc.LANG_CD)
left outer join TLGNR_RETURN_QUANT_HANDLING_BS rqhandling on (item.RETURN_QUANT_HANDLING_CD=rqhandling.RETURN_QUANT_HANDLING_CD)
where
    item.RETURN_QUANT_HANDLING_CD = 3 /* credit note needed */ and ord.archiving_status_cd = 1;

COMMENT ON TABLE VLGNC_CREDIT_NOTE_PROPOSALS is 'Used to collect the resultlist for UC 6013: Credit Notes.';

REM END VLGNC_CREDIT_NOTE_PROPOSALS
-------------------------------------------------------------------------------
PROMPT VLGNC_TRUCK_ROUTE_SEARCH

CREATE OR REPLACE FORCE VIEW VLGNC_TRUCK_ROUTE_SEARCH AS
SELECT distinct
  truckRoute.TRUCK_ROUTE_ID,
  startShippingPoint.SHIPPINGPOINT_ID as START_SHIPPINGPOINT_ID,
  startShippingPoint.SHIPPINGPOINT_NAME as START_SHIPPINGPOINT_NAME,
  endShipToBp.SAP_BP_ID as END_SHIPTO_BP_ID,
  endShipToBp.SAP_ID AS END_SHIPTO_BP_SAP_ID,
  endShipToBp.NAME1 as END_SHIPTO_BP_NAME1,
  endShipToBp.POSTAL_CODE as END_SHIPTO_BP_POSTAL_CODE,
  endShipToBp.CITY as END_SHIPTO_BP_CITY,
  truckRoute.TIME_CALCULATED_MIN,
  truckRoute.TIME_CORRECTION_FACTOR,
  truckRoute.TIME_CALCULATED_MIN * NVL(truckRoute.TIME_CORRECTION_FACTOR,1) as TIME_PLANNED,
  truckRoute.DISTANCE_CALCULATED_M,
  truckRoute.DISTANCE_CORRECTION_FACTOR,
  truckRoute.DISTANCE_CALCULATED_M * NVL(truckRoute.DISTANCE_CORRECTION_FACTOR,1) as CORRECTED_DISTANCE,
  truckRoute.ROUTE_COMMENT,
  truckRoute.SAP_TRSP_MODE_CD,
  trspModeTx.LANG_CD,
  trspModeTx.TXT SAP_TRSP_MODE_TXT
FROM
  TLGNC_TRUCK_ROUTE truckRoute
  left outer join TLGNC_SHIPPINGPOINT startShippingPoint on (truckRoute.START_SHIPPINGPOINT_ID = startShippingPoint.SHIPPINGPOINT_ID)
  left outer join TLGNC_SHIPTO endShipTo on (truckRoute.END_SHIPTO_ID = endShipTo.SHIPTO_ID)
  left outer join TLGNS_SAP_BP endShipToBp on (endShipTo.SHIPTO_SAP_BP_ID = endShipToBp.SAP_BP_ID)
  left outer join TLGNR_SAP_TRSP_MODE_TX trspModeTx on (trspModeTx.SAP_TRSP_MODE_CD = truckRoute.SAP_TRSP_MODE_CD)
GROUP BY
  truckRoute.TRUCK_ROUTE_ID,
  startShippingPoint.SHIPPINGPOINT_ID,
  startShippingPoint.SHIPPINGPOINT_NAME,
  endShipToBp.SAP_BP_ID,
  endShipToBp.SAP_ID,
  endShipToBp.NAME1,
  endShipToBp.POSTAL_CODE,
  endShipToBp.CITY,
  truckRoute.TIME_CALCULATED_MIN,
  truckRoute.TIME_CORRECTION_FACTOR,
  truckRoute.DISTANCE_CALCULATED_M,
  truckRoute.DISTANCE_CORRECTION_FACTOR,
  truckRoute.ROUTE_COMMENT,
  truckRoute.SAP_TRSP_MODE_CD,
  trspModeTx.LANG_CD,
  trspModeTx.TXT;

comment on table VLGNC_TRUCK_ROUTE_SEARCH is 'Used to collect the resultlist for truck routes screen';

REM END ULGN01 VLGNC_TRUCK_ROUTE_SEARCH

-------------------------------------------------------------------------------

PROMPT VLGNC_RFIDTAG_DETAIL

create or replace force view vlgnc_rfidtag_detail as
  select
  rfidtag.rfidtag_id            rfidtag_id,
  rfidtag.rfid_identity         rfid_identity,
  rfidtag.name                  name,
  rfidobject.object_label       object_label,
  rfidobject.rfidobject_type_cd rfidobject_type_cd,
  rfidtype.lang_cd              rfid_object_type_lang_cd,
  rfidtype.txt                  rfid_object_type,
  rfidtag.active_flag           active_flag
from  tlgnc_rfidtag rfidtag
left outer join tlgnc_rfidobject rfidobject on(rfidtag.rfidobject_id = rfidobject.rfidobject_id)
left outer join tlgnr_rfidobject_type_tx rfidtype on (rfidobject.rfidobject_type_cd = rfidtype.rfidobject_type_cd);

comment on table vlgnc_rfidtag_detail is 'Used to display rfid tags together with the objects for which the tag is assigned on the rfid admin screen';

REM END ULGN01 VLGNC_RFIDTAG_DETAIL

-------------------------------------------------------------------------------
PROMPT VLGNC_LDS_SERVER_DROPDOWN

CREATE OR REPLACE FORCE VIEW VLGNC_LDS_SERVER_DROPDOWN AS
SELECT config_cd,
  key,
  SUBSTR(key, INSTR(key, '.', 1, 1) + 1, INSTR(key, '.', 1, 2) - INSTR(key, '.', 1, 1) - 1) AS type,
  SUBSTR(key, INSTR(key, '.', 1, 4) + 1) AS name
FROM tlgnr_config
WHERE key LIKE 'wsclient.%.host.name.%'
AND instance IS NULL
ORDER BY key;

comment on table VLGNC_LDS_SERVER_DROPDOWN is 'Used to display LDS server names from tlgnr_config table on the shipping point screen. We are only interested in keys, not values, therefore hard-coding instance to null is ok.';

REM END ULGN01 VLGNC_LDS_SERVER_DROPDOWN

-------------------------------------------------------------------------------
PROMPT VLGNC_PDA_DEVICE_CARRIER

create or replace force view vlgnc_pda_device_carrier as
  select
  pda.pda_device_id pda_device_id,
  pda.pda_device_no pda_device_no,
  pda.last_user_id last_user_id,
  pda.active_flag pda_device_active_flag,
  pda.type_description type_description,
  pda.inventory_number inventory_number,
  sim.phone_number sim_phone_number,
  pda.last_registration_date last_registration_date,
  pda.software_version software_version,
  carr.carrier_id carrier_id,
  carr.display_name carrier_display_name,
  carr.active_flag carrier_active_flag
from tlgnc_pda_device pda
left outer join tlgnc_carrier carr on (pda.carrier_id = carr.carrier_id)
left outer join tlgnr_simcard_bs sim on (pda.simcard_cd = sim.simcard_cd);

comment on table VLGNC_PDA_DEVICE_CARRIER is 'Used to display the list of pda devices, together with the carriers to wich the pda device is assigned.';
REM END ULGN01 VLGNC_PDA_DEVICE_CARRIER
-------------------------------------------------------------------------------
PROMPT VLGNC_MAN_POOL_ASSIGNMENT_LIST
 --WARNING: Used with beanutils.copy, if you rename a field, it will not break the code, it will just not work!
    CREATE OR REPLACE VIEW VLGNC_MAN_POOL_ASSIGNMENT_LIST AS
  SELECT distinct
    ord.ORDER_ID || '_' || item.SAP_MATERIAL_ID || '_' || materialdesc.LANG_CD AS MAN_POOL_ASSIGNMENT_LIST_ID,
    ord.ORDER_ID,
    materialdesc.LANG_CD,
    soldtoBp.NAME1 as SOLDTO_NAME1,
    shiptoBp.NAME1 as SHIPTO_NAME1,
    shiptoBp.SAP_BP_ID AS SAP_BP_ID,
    materialdesc.txt as MATERIAL_TXT,
    deliveryTypeTx.txt as DELIVERY_TYPE_TXT,
    ord.ORDER_NUMBER,
    shipp.SHIPPINGPOINT_NAME,
    ord.DELIVERY_FROM_DAT,
    ord.DELIVERY_TO_DAT,
    ord.ORDER_STATUS_CD,
    ord.POOL_ID,
    pooltext.NAME as POOL_NAME,
    ord.COMM_CARRIER_ID,
    ccarrier.DISPLAY_NAME as COMM_CARRIER_NAME,
    ccarrier.sap_vendor_id,
    ord.SHIPTO_SAP_BP_ID,
    ord.SOLDTO_SAP_BP_ID,
    ord.SAREA_ID,
    ord.SHIPPINGPOINT_ID,
    shipp.PLANT_CD,
    sorgbs.COMPANYCODE_CD
FROM
    TLGNC_ORDER ord
left outer join TLGNC_ORDER_ITEM item on (ord.order_id=item.order_id)
left outer join TLGNC_POOL pool on (pool.POOL_ID=ord.POOL_ID)
left outer join TLGNC_SHIPPINGPOINT shipp on (shipp.SHIPPINGPOINT_ID=ord.SHIPPINGPOINT_ID)
left outer join TLGNS_SAP_BP soldtoBp on (ord.SOLDTO_SAP_BP_ID=soldtoBp.SAP_BP_ID)
left outer join TLGNS_SAP_BP shiptoBp on (ord.SHIPTO_SAP_BP_ID=shiptoBp.SAP_BP_ID)
left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc on (materialdesc.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID)
left outer join TLGNR_DELIVERY_TYPE_TX deliveryTypeTx on (deliveryTypeTx.DELIVERY_TYPE_CD=ord.DELIVERY_TYPE_CD AND deliveryTypeTx.LANG_CD=materialdesc.LANG_CD)
left outer join TLGNC_CARRIER ccarrier on (ccarrier.CARRIER_ID=ord.COMM_CARRIER_ID)
left outer join TLGNC_POOL_TX pooltext on (pooltext.POOL_ID=ord.POOL_ID and pooltext.LANG_CD=materialdesc.LANG_CD)
left outer join TLGNC_SAREA sareabs ON (ord.SAREA_ID=sareabs.sarea_id)
left outer join TLGNR_SORG_BS sorgbs ON (sareabs.sorg_cd = sorgbs.sorg_cd)
join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1)
where ord.ORDER_STATUS_CD = '1'
group by ord.ORDER_ID, item.SAP_MATERIAL_ID, materialdesc.LANG_CD, soldtoBp.NAME1, shiptoBp.NAME1, shiptoBp.SAP_BP_ID, materialdesc.txt, deliveryTypeTx.txt, ord.ORDER_NUMBER, shipp.SHIPPINGPOINT_NAME, ord.DELIVERY_FROM_DAT, ord.DELIVERY_TO_DAT, ord.ORDER_STATUS_CD, ord.POOL_ID, pooltext.NAME, ord.COMM_CARRIER_ID, ccarrier.DISPLAY_NAME,ccarrier.sap_vendor_id,ord.SHIPTO_SAP_BP_ID,ord.SOLDTO_SAP_BP_ID,ord.SAREA_ID,ord.SHIPPINGPOINT_ID,shipp.PLANT_CD,sorgbs.COMPANYCODE_CD;

comment on table VLGNC_MAN_POOL_ASSIGNMENT_LIST is 'Used to display the list of orders in the pool assignment screen.';
REM END ULGN01 VLGNC_MAN_POOL_ASSIGNMENT_LIST

-------------------------------------------------------------------------------

prompt vlgnc_bizevent_order_log

create or replace force view vlgnc_bizevent_order_log as
select biz.bizevent_id || '_' || btx.lang_cd as COMPOSITE_ID,
  base.order_id base_order_id,
  base.assignment_id base_assignment_id,
  biz.order_id,
  biz.bizevent_id,
  biz.assignment_id,
  biz.event_ts,
  biz.description,
  biz.ctl_mod_ts mod_ts,
  biz.ctl_cre_ts cre_ts,
  btx.lang_cd,
  btx.txt bizevent_type_txt,
  biz.bizevent_type_cd,
  biz.original_mopevent_id,
  (
    CASE WHEN hin.pda_no IS NOT NULL THEN
      CASE WHEN substr(hin.pda_no,0,4) = 'http' THEN
        'Parallel'
      ELSE
        'PDA'
      END
    ELSE
      prs.firstname || ' ' || prs.name
    END
  ) initiator,
  hin.speed,
  hin.course,
  hin.event_name,
  hin.truck,
  hin.geo_x,
  hin.geo_y,
  hin.geo_time,
  w.wagon_no,
  t.truck_plate_no,
  c.container_no
from TLGNC_BIZEVENT base
inner join TLGNC_BIZEVENT biz on (
    (base.bizevent_id = biz.bizevent_id)
    or
    (base.bizevent_type_cd in (160, 166) and base.assignment_id = biz.assignment_id and biz.order_id is null and base.event_ts > biz.event_ts)
  )
left outer join TLGNC_MOPEVENT_HEADER_IN hin on (hin.mopevent_header_in_id = biz.original_mopevent_id)
inner join TLGNR_BIZEVENT_TYPE_TX btx on (btx.bizevent_type_cd = biz.bizevent_type_cd)
inner join TLGNU_USER usr on (usr.user_id = biz.ctl_mod_uid)
inner join TLGNC_PERSON prs on (prs.person_id = usr.person_id)
left join TLGNC_TRUCK t on (t.TRUCK_ID = biz.TRUCK_ID)
left join TLGNC_WAGON w on (w.WAGON_ID=biz.WAGON_ID)
left join TLGNC_CONTAINER c on (c.container_id = biz.container_id);

comment on table vlgnc_bizevent_order_log is 'Used to display the list of biz-events in the order log of order details.';
rem end vlgnc_bizevent_order_log

-------------------------------------------------------------------------------

prompt vlgnc_bizevent_truck_log

create or replace force view vlgnc_bizevent_truck_log as
select biz.bizevent_id || '_' || btx.lang_cd as COMPOSITE_ID,
  biz.order_id,
  biz.assignment_id,
  biz.ctl_mod_ts mod_ts,
  biz.ctl_cre_ts cre_ts,
  btx.lang_cd,
  btx.txt bizevent_type_txt,
  hin.speed
from TLGNC_BIZEVENT biz
left outer join TLGNC_MOPEVENT_HEADER_IN hin on (hin.mopevent_header_in_id = biz.original_mopevent_id)
inner join TLGNR_BIZEVENT_TYPE_TX btx on (btx.bizevent_type_cd = biz.bizevent_type_cd);

comment on table vlgnc_bizevent_truck_log is 'Used to display the list of biz-events in the truck log of order details.';
rem end vlgnc_bizevent_truck_log

-------------------------------------------------------------------------------
--There is an Oracle 11.2 bug for which workaround is needed to cast truck_id. Please see ticket 75943.
--Another option would be:
--alter session set OPTIMIZER_FEATURES_ENABLE='11.2.0.2.1'; -- query fails
--alter session set OPTIMIZER_FEATURES_ENABLE='10.2.0.4';  -- query works
--For further details please see mails:
--      mailing list:dbeng@adnovum.ch (or pegasus-dev@adnovum.ch)
--      subject: Oracle (11.2) bug with in clause and aggregate function min, or wrong usage?
--      date: 11/10/11 14:53
--Logon will NOT be run in compatibility mode though, so this was not really an option.
PROMPT  VLGNC_ORD_ITEM_ASSIGNMT_LOAD
CREATE OR REPLACE FORCE VIEW VLGNC_ORD_ITEM_ASSIGNMT_LOAD AS
SELECT
ord.order_id ||'_' || order_item_id as composite_id,
ord.ORDER_ID,
ord.SHIPTO_SAP_BP_ID,
ord.SAREA_ID,
ord.ORDER_NUMBER,
ord.ORDER_STATUS_CD,
ord.DELIVERY_FROM_DAT,
ord.DELIVERY_TO_DAT,
ord.DELIVERY_TYPE_CD,
ord.SHIPPINGPOINT_ID,
ord.POOL_ID,
ord.COMM_CARRIER_ID,
ord.EXEC_CARRIER_ID,
ord.BILLTO_SAP_BP_ID,
ord.NOTE_ORDER_MGMT,
ord.delivered_driver_id,
ord.NOTE_DELIVERY,
ord.CUST_WEBORDER_NOTE,
ord.NOTE_DISPATCHING,
ord.NOTE_TRUCK_DRIVER,
ord.NOTE_RAILWAY_DISP,
ord.TRANSPORT_DURATION_MIN,
soldto.NAME1 as SOLDTO_BP_NAME,
shipto.NAME1 as SHIPTO_BP_NAME,
item.ORDER_ITEM_ID,
item.ORDER_ITEM_NO,
item.SAP_MATERIAL_ID,
item.CURRENCY_CD,
item.RATE,
item.ORDERED_QUANTITY,
item.ORDERED_UOM_CD,
item.DELIVERED_UOM_CD,
item.DELIVERED_QUANTITY,
item.SHIPTO_SILO_ID,
item.LOADED_QUANTITY,
item.LOADED_UOM_CD,
item.PRODUCTION_PLANT_CD,
deliverybs.SAP_TRSP_MODE_CD,
plant.PLANT_NAME as PRODUCTION_PLANT_NAME,
material.SAP_MATERIAL_NO as MATERIAL_NO,
material.MATERIAL_PACKAGE_CD,
material.MATERIAL_IS_LF as IS_LANDFILL,
baseuom.UOM_CD as BASE_UOM_CD,
baseuom.UOM_DIMENSION_CD as ORDER_UOM_DIMENSION_CD,
pkg.LOGON_PACKAGE_TYPE_CD,
status.TXT as STATUS_TXT,
status.LANG_CD,
billto.NAME1 as BP_NAME,
ccarrier.DISPLAY_NAME as COMM_CARRIER_NAME,
ecarrier.DISPLAY_NAME as EXEC_CARRIER_NAME,
shipp.SHIPPINGPOINT_NAME,
shipp.SHIPPPOINT_TYPE_CD,
shipp.PLANT_CD,
shipp.TIMEZONE_CD,
load.LOADINGPOINT_ID,
load.LOAD_ID,
load.PRODUCTION_PLANT_CD as LOAD_PRODUCTION_PLANT_CD,
load.TRUCK_ID as LOAD_TRUCK_ID,
load.LOADING_TS as LOADING_TS,
load.UNLOADING_TS as UNLOADING_TS,
load.TARE_WEIGHT_T as TARE_QUANTITY,
load.NET_QUANTITY,
cast (load.TARE_WEIGHT_T as NUMBER(12,4)) LOAD_TARE_MEASURE,
cast (load.CHAMBER_NUMBER as NUMBER(2,0)) CHAMBER_NUMBER,
loadpart.PLANNED_START as LOADING_PLANNED_START,
loadpart.REAL_START as LOADING_REAL_START,
ass.assignment_id,
ass.assign_status_cd,
ass.load_order_id,
ass.lds_driver_name,
ass.lds_driver_card_no,
ass.lds_driver_id,
ass.lds_attorney_name,
ass.lds_attorney_start,
ass.lds_dispatcher_name,
ass.lds_dispatcher_tel,
ass.lds_dispatcher_id,
ass.lds_carrier_crm,
ass.lds_seal_numbers,
itemunit.TXT  as ORDER_UNIT_TXT,
baseunit.TXT as ORDER_UOM_DIMENSION_TXT,
itemuom.FUZZY_QUANTITY_FLAG as ORDERED_UOM_FUZZY,
cast ((select pdf_outputdoc_id FROM TLGNC_ORDER_OUTPUTDOC doc where doc.order_id = ord.order_id and CTL_CRE_TS = (select max(CTL_CRE_TS) from tlgnc_order_outputdoc where order_id=ord.order_id)) AS NUMBER(12,0)) DELIVERYNOTE_OUTPUTDOC_ID,
pooltext.NAME as POOL_NAME,
wssp.PUSH_MODE_ON,
loadunit.TXT as LOAD_UNIT_TXT,
baseunit.TXT as LOAD_UOM_DIMENSION_TXT,
(load.NET_QUANTITY * item.LR_UOM_NUMERATOR / item.LR_UOM_DENOMINATOR * baseuom.NUMERATOR_FOR_CONVERSION / baseuom.DENOMINATOR_FOR_CONVERSION) as LOAD_NET_MEASURE,
cast (item.LOADED_QUANTITY * item.LR_UOM_NUMERATOR / item.LR_UOM_DENOMINATOR as NUMBER(12,4)) LOADED_NET_MEASURE,
materialdesc.txt as MATERIAL_TXT,
baseunit.TXT as BASE_UNIT_TXT,
cast((truck.TRUCK_ID) as NUMBER(12,0)) as truck_id, --workaround of Oracle (11.2) bug see ticket 75943 and text before view definition
truck.TRUCK_PLATE_NO,
truck.TRUCK_NAME,
truck.TARE_WEIGHT_T,
truck.SAP_VEHICLE_CHAR_CD,
cast (nvl(trailer.TARE_WEIGHT_T,currtrailer.TARE_WEIGHT_T) as NUMBER(12,4))  TRAILER_TARE_WEIGHT_T,
nvl(container.CONTAINER_NAME, nvl(currtruckcontainer.CONTAINER_NAME, currtrailercontainer.CONTAINER_NAME)) as CONTAINER_NAME,
cast(nvl(container.TARE_WEIGHT_T, nvl(currtruckcontainer.TARE_WEIGHT_T, currtrailercontainer.TARE_WEIGHT_T)) as NUMBER(12,4))  CONTAINER_TARE_WEIGHT_T,
lp.LOADINGPOINT_NAME,
lp.WEIGH_SYSTEM_LP_CD,
lp.PACKAGE_TYPE_CD,
(select MIN(planned_start) from TLGNC_ASSIGNMENT_PART where ASSIGNMENT_ID=ass.ASSIGNMENT_ID and ASSIGNMENT_PART_TYPE_CD=1 ) as LOADING_MIN_PLANNED_START,
(truck.MAX_GROSS_WEIGHT_T-truck.TARE_WEIGHT_T) as TRUCK_MAX_NET_WEIGHT_T,
cast ((container.MAX_NET_WEIGHT_T) as NUMBER) CONTAINER_MAX_NET_WEIGHT_T,
nvl(
(select (pers.FIRSTNAME || ' ' || pers.NAME) as DRIVER_NAME from TLGNC_DRIVER driver inner join TLGNU_USER us on (us.USER_ID = driver.USER_ID) inner join TLGNC_PERSON pers on (us.PERSON_ID = pers.PERSON_ID) where driver.DRIVER_ID=ord.delivered_driver_id  and rownum <= 1),
(select (pers.FIRSTNAME || ' ' || pers.NAME) as DRIVER_NAME from TLGNC_DRIVER driver inner join TLGNU_USER us on (us.USER_ID = driver.USER_ID) inner join TLGNC_PERSON pers on (us.PERSON_ID = pers.PERSON_ID) where driver.CURRENT_TRUCK_ID=ass.TRUCK_ID  and rownum <= 1)) as DRIVER_NAME,
(item.ORDERED_QUANTITY * item.ORDERED_UOM_NUMERATOR / item.ORDERED_UOM_DENOMINATOR * baseuom.NUMERATOR_FOR_CONVERSION / baseuom.DENOMINATOR_FOR_CONVERSION) as ORDERED_MEASURE,
simplevehicle.VEHICLE_NAME SIMPLE_VEHICLE_NAME,
simplevehicle.VEHICLE_REG_NO SIMPLE_VEHICLE_REG_NO,
simplevehicle.TARE_WEIGHT_T SIMPLE_VEHICLE_TARE_WEIGHT_T
from
TLGNC_ORDER ord
inner join TLGNS_SAP_BP shipto on (ord.SHIPTO_SAP_BP_ID = shipto.SAP_BP_ID)
inner join TLGNS_SAP_BP soldto on (ord.SOLDTO_SAP_BP_ID = soldto.SAP_BP_ID)
inner join TLGNC_ORDER_ITEM item on (ord.order_id = item.order_id)
inner join TLGNR_DELIVERY_TYPE_BS deliverybs on (deliverybs.delivery_type_cd = ord.delivery_type_cd)
inner join TLGNR_PLANT_BS plant on (item.PRODUCTION_PLANT_CD = plant.PLANT_CD)
inner join TLGNS_SAP_MATERIAL material on (item.SAP_MATERIAL_ID = material.SAP_MATERIAL_ID)
inner join TLGNR_UOM_BS baseuom on (material.BASE_UOM_CD = baseuom.UOM_CD)
inner join TLGNR_MATERIAL_PACKAGE_BS pkg on (material.MATERIAL_PACKAGE_CD = pkg.MATERIAL_PACKAGE_CD)
left outer join TLGNR_ORDER_STATUS_TX status on (ord.ORDER_STATUS_CD = status.ORDER_STATUS_CD)
left outer join TLGNS_SAP_BP billto on (ord.BILLTO_SAP_BP_ID=billto.SAP_BP_ID)
left outer join TLGNC_CARRIER ccarrier on (ord.COMM_CARRIER_ID = ccarrier.CARRIER_ID)
left outer join TLGNC_CARRIER ecarrier on (ord.EXEC_CARRIER_ID = ecarrier.CARRIER_ID)
left outer join TLGNC_SHIPPINGPOINT shipp on (shipp.SHIPPINGPOINT_ID=ord.SHIPPINGPOINT_ID)
left outer join TLGNC_LOAD load on (item.LOAD_ID = load.LOAD_ID)
left outer join TLGNC_ASSIGNMENT_PART loadpart on (load.load_id = loadpart.load_id)
left outer join TLGNC_ASSIGNMENT_PART unloadpart on (ord.order_id = unloadpart.order_id)
left outer join TLGNC_ASSIGNMENT ass on (ass.ASSIGNMENT_ID=unloadpart.ASSIGNMENT_ID and ass.ACTIVE_FLAG = 1)
left outer join TLGNC_TRUCK truck on (truck.TRUCK_ID=ass.TRUCK_ID)
left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc on (materialdesc.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID and status.LANG_CD=materialdesc.LANG_CD )
left outer join TLGNR_WEIGH_SYSTEM_SP_BS wssp on (shipp.WEIGH_SYSTEM_SP_CD = wssp.WEIGH_SYSTEM_SP_CD)
left outer join TLGNC_LOADINGPOINT lp on (lp.LOADINGPOINT_ID=load.LOADINGPOINT_ID)
left outer join TLGNC_POOL_TX pooltext on (pooltext.POOL_ID=ord.POOL_ID and pooltext.LANG_CD=status.lang_cd)
left outer join TLGNC_TRAILER trailer on (trailer.TRAILER_ID = load.TRAILER_ID)
left outer join TLGNC_TRAILER currtrailer on (currtrailer.CURRENT_TRUCK_ID = truck.TRUCK_ID)
left outer join TLGNC_CONTAINER container on (container.CONTAINER_ID = load.CONTAINER_ID)
left outer join TLGNC_CONTAINER currtruckcontainer on (currtruckcontainer.CURRENT_TRUCK_ID = truck.TRUCK_ID)
left outer join TLGNC_CONTAINER currtrailercontainer on (currtrailercontainer.CURRENT_TRAILER_ID = currtrailer.TRAILER_ID)
left outer join TLGNR_UOM_TX itemunit on (itemunit.UOM_CD=item.ORDERED_UOM_CD and itemunit.LANG_CD=status.LANG_CD)
left outer join TLGNR_UOM_TX loadunit on (loadunit.UOM_CD=load.net_quantity_uom_cd and loadunit.LANG_CD=status.LANG_CD)
left outer join TLGNR_UOM_BS itemuom on (itemuom.uom_cd = item.ORDERED_UOM_CD)
left outer join TLGNR_UOM_TX baseunit on (baseunit.UOM_CD = baseuom.UOM_CD and baseunit.LANG_CD = status.LANG_CD)
left outer join TLGNC_SIMPLE_VEHICLE simplevehicle on (load.simple_vehicle_id = simplevehicle.simple_vehicle_id)
where ord.DELIVERY_TYPE_CD in (1, 4, 5, 10, 11, 12) and ord.order_status_cd <> 16 and ord.order_status_cd <> 19;


COMMENT ON TABLE VLGNC_ORD_ITEM_ASSIGNMT_LOAD is 'Contains all the text information about an order including the active assignment, and the load entry for the order.';

REM END VLGNC_ORD_ITEM_ASSIGNMT_LOAD

-------------------------------------------------------------------------------

PROMPT  VLGNC_SHIPPING_STAT
CREATE OR REPLACE FORCE VIEW VLGNC_SHIPPING_STAT AS
SELECT
distinct
ord.order_id ||'_' || order_item_id as composite_id,
ord.ORDER_ID,
ord.Order_status_cd,
ord.shippingpoint_id,
item.ORDER_ITEM_NO,
item.SAP_MATERIAL_ID,
item.ORDERED_UOM_CD,
item.ORDERED_QUANTITY,
item.DELIVERED_UOM_CD,
item.DELIVERED_QUANTITY,
ord.DELIVERY_FROM_DAT,
ord.DELIVERY_TO_DAT,
ord.DELIVERY_TYPE_CD,
(item.ORDERED_QUANTITY * item.ORDERED_UOM_NUMERATOR / item.ORDERED_UOM_DENOMINATOR * baseuom.NUMERATOR_FOR_CONVERSION / baseuom.DENOMINATOR_FOR_CONVERSION) as ORDERED_MEASURE,
baseuom.UOM_DIMENSION_CD as ORDER_UOM_DIMENSION_CD,
(truck.MAX_GROSS_WEIGHT_T-truck.TARE_WEIGHT_T) as TRUCK_MAX_NET_WEIGHT_T,
cast ((container.MAX_NET_WEIGHT_T) as NUMBER) CONTAINER_MAX_NET_WEIGHT_T,
item.PRODUCTION_PLANT_CD,
materialdesc.txt as MATERIAL_TXT,
(load.NET_QUANTITY * item.LR_UOM_NUMERATOR / item.LR_UOM_DENOMINATOR * baseuom.NUMERATOR_FOR_CONVERSION / baseuom.DENOMINATOR_FOR_CONVERSION) as LOAD_NET_MEASURE,
plant.PLANT_NAME as PRODUCTION_PLANT_NAME,
itemunit.lang_cd,
deliverybs.SAP_TRSP_MODE_CD,
shipp.PLANT_CD,
part.PLANNED_START as LOADING_PLANNED_START
from
TLGNC_ORDER ord
inner join TLGNC_ORDER_ITEM item on (ord.order_id=item.order_id)
left outer join TLGNC_SHIPPINGPOINT shipp on (shipp.SHIPPINGPOINT_ID=ord.SHIPPINGPOINT_ID)
left outer join TLGNC_LOAD load on (load.LOAD_ID = item.LOAD_ID)
left outer join TLGNC_ASSIGNMENT_PART part on (part.load_id = item.load_id)
left outer join TLGNC_ASSIGNMENT ass on (ass.ASSIGNMENT_ID=part.ASSIGNMENT_ID and ass.ACTIVE_FLAG = 1)
left outer join TLGNC_TRUCK truck on (truck.TRUCK_ID=ass.TRUCK_ID)
left outer join TLGNR_UOM_TX itemunit on (itemunit.UOM_CD=item.ORDERED_UOM_CD)
left outer join TLGNR_UOM_TX loadunit on (loadunit.UOM_CD=load.net_quantity_uom_cd and loadunit.lang_cd=itemunit.lang_cd)
left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc on (materialdesc.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID and materialdesc.lang_cd = itemunit.lang_cd)
left outer join TLGNC_TRAILER trailer on (trailer.TRAILER_ID = load.TRAILER_ID)
left outer join TLGNC_TRAILER currtrailer on (currtrailer.CURRENT_TRUCK_ID = truck.TRUCK_ID)
left outer join TLGNC_CONTAINER container on (container.CONTAINER_ID = load.CONTAINER_ID)
left outer join TLGNC_CONTAINER currtruckcontainer on (currtruckcontainer.CURRENT_TRUCK_ID = truck.TRUCK_ID)
left outer join TLGNC_CONTAINER currtrailercontainer on (currtrailercontainer.CURRENT_TRAILER_ID = currtrailer.TRAILER_ID)
left outer join TLGNS_SAP_MATERIAL material on (material.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID)
left outer join TLGNR_PLANT_BS plant on (item.PRODUCTION_PLANT_CD = plant.PLANT_CD)
left outer join TLGNR_UOM_BS loaduom on (loaduom.uom_cd = load.net_quantity_uom_cd)
left outer join TLGNR_UOM_BS itemuom on (itemuom.uom_cd = item.ORDERED_UOM_CD)
left outer join TLGNR_UOM_BS baseuom on (baseuom.UOM_CD = material.BASE_UOM_CD)
left outer join TLGNR_DELIVERY_TYPE_BS deliverybs on (deliverybs.delivery_type_cd = ord.delivery_type_cd)
where ord.DELIVERY_TYPE_CD in (1,4,5) and ord.order_status_cd <> 16 and ord.order_status_cd <> 19;


COMMENT ON TABLE VLGNC_SHIPPING_STAT is 'Contains necessary information for fetching statistics on terminal screen.';

REM END VLGNC_SHIPPING_STAT

-------------------------------------------------------------------------------

PROMPT  VLGNC_BIMODAL_RAIL_ORDER
CREATE OR REPLACE FORCE VIEW VLGNC_BIMODAL_RAIL_ORDER AS
SELECT
  ord.order_id ||'_' || order_item_id || '_' || matdesc.lang_cd as composite_id,
  ord.ORDER_ID,
  ord.SHIPTO_SAP_BP_ID,
  ord.ORDER_NUMBER,
  ord.ORDER_STATUS_CD,
  ord.DELIVERY_TYPE_CD,
  ord.SHIPPINGPOINT_ID,
  ord.actual_delivery_dat,
  item.ORDER_ITEM_NO,
  item.ORDER_ITEM_ID,
  item.SAP_MATERIAL_ID,
  status.txt AS ORDER_STATUS_TXT,
  item.BIMODAL_RAIL_ORDER_ITEM_ID,
  item.PRODUCTION_PLANT_CD,
  ass.assignment_id,
  shiptobp.name1,
  station.RAILWAY_STATION_ID,
  station.RAILWAY_STATION_NAME,
  plant.plant_name as production_plant_name,
  sorg.companycode_cd,
  ass.assign_status_cd,
  truck.TRUCK_ID,
  matdesc.txt as sap_material_desc,
  wagon.WAGON_ID,
  cast(round(to_number(SYSDATE-ord.actual_delivery_dat)*1440) AS number(12)) WAGON_IDLE_TIME_MIN,
  substr(wagon.WAGON_NO, 1, 4) || ' ' || substr(wagon.WAGON_NO, 5, 4) || ' '
    || substr(wagon.WAGON_NO, 9, 3) || '-' || substr(wagon.WAGON_NO, 12, 1) as WAGON_NO,
  wagontype.WAGON_TYPE_NAME as WAGON_TYPE,
  container.CONTAINER_NAME,
  container.TARE_WEIGHT_T as CONTAINER_TARE_WEIGHT_T,
  (item.ORDERED_QUANTITY * item.ordered_uom_numerator / item.ordered_uom_denominator * baseuom.NUMERATOR_FOR_CONVERSION / baseuom.DENOMINATOR_FOR_CONVERSION)
    as ORDERED_MEASURE,
  load.LOAD_ID,
  load.PRODUCTION_PLANT_CD as LOAD_PRODUCTION_PLANT_CD,
  (load.NET_QUANTITY * item.lr_uom_numerator / item.lr_uom_denominator * baseuom.NUMERATOR_FOR_CONVERSION / baseuom.DENOMINATOR_FOR_CONVERSION)
    as LOAD_NET_MEASURE,
  load.TARE_WEIGHT_T as LOAD_TARE_MEASURE,
  (truck.MAX_GROSS_WEIGHT_T - truck.TARE_WEIGHT_T
    - nvl(nvl(trailer.TARE_WEIGHT_T, currtrailer.TARE_WEIGHT_T), 0)
    - nvl(nvl(container.TARE_WEIGHT_T, nvl(currtruckcontainer.TARE_WEIGHT_T,
      currtrailercontainer.TARE_WEIGHT_T)), 0)) as TRUCK_MAX_NET_WEIGHT_T,
  (wagon.MAX_GROSS_WEIGHT_T-wagon.TARE_WEIGHT_T) as WAGON_MAX_NET_WEIGHT_T,
  sp.SHIPPINGPOINT_NAME,
  sp.FVL_MIX_SHIPTO_PER_WAGON_FLAG,
  matdesc.lang_cd as lang_cd
from
  TLGNC_ORDER ord
  inner join tlgnc_sarea sarea on sarea.sarea_id=ord.sarea_id
  inner join TLGNR_SORG_BS sorg on sorg.sorg_cd=sarea.sorg_cd
  inner join TLGNC_ORDER_ITEM item on (ord.order_id=item.order_id)
  inner join TLGNS_SAP_BP shiptobp on shiptobp.sap_bp_id=ord.shipto_sap_bp_id
  LEFT JOIN TLGNC_RAILWAY_STATION station on station.RAILWAY_STATION_ID=shiptobp.railway_station_id
  left outer join TLGNS_SAP_MATERIAL_DESCRIPTION matdesc on matdesc.sap_material_id=item.SAP_MATERIAL_ID
  LEFT JOIN tlgnr_order_status_tx status ON status.order_status_cd = ord.order_status_cd AND status.lang_cd = matdesc.lang_cd
  left outer join TLGNC_LOAD load on (load.LOAD_ID = item.LOAD_ID)
  left outer join TLGNC_SHIPPINGPOINT sp on (ord.SHIPPINGPOINT_ID = sp.SHIPPINGPOINT_ID)
  left outer join TLGNR_PLANT_BS plant on plant.PLANT_CD=item.PRODUCTION_PLANT_CD
  left outer join TLGNC_ASSIGNMENT_PART part on (part.load_id = item.load_id)
  left outer join TLGNC_ASSIGNMENT ass on (ass.ASSIGNMENT_ID=part.ASSIGNMENT_ID
    and ass.ACTIVE_FLAG = 1)
  left outer join TLGNC_TRUCK truck on (truck.TRUCK_ID=ass.TRUCK_ID)
  left outer join TLGNC_WAGON wagon on (wagon.WAGON_ID=ass.WAGON_ID)
  left outer join TLGNC_TRAILER trailer on (trailer.TRAILER_ID = load.TRAILER_ID)
  left outer join TLGNC_TRAILER currtrailer on (currtrailer.CURRENT_TRUCK_ID = truck.TRUCK_ID)
  left outer join TLGNC_CONTAINER container on (container.CONTAINER_ID = load.CONTAINER_ID)
  left outer join TLGNC_CONTAINER currtruckcontainer
    on (currtruckcontainer.CURRENT_TRUCK_ID = truck.TRUCK_ID)
  left outer join TLGNC_CONTAINER currtrailercontainer
    on (currtrailercontainer.CURRENT_TRAILER_ID = currtrailer.TRAILER_ID)
  left outer join TLGNR_WAGON_TYPE_BS wagontype on (wagon.WAGON_TYPE_CD=wagontype.WAGON_TYPE_CD)
  left outer join TLGNR_UOM_BS loaduom on (loaduom.uom_cd = load.net_quantity_uom_cd)
  left outer join TLGNR_UOM_BS itemuom on (itemuom.uom_cd = item.ORDERED_UOM_CD)
  left join tlgns_sap_material material on (item.sap_material_id = material.sap_material_id)
  left join tlgnr_uom_bs baseuom on material.base_uom_cd = baseuom.uom_cd
  join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1);

COMMENT ON TABLE VLGNC_BIMODAL_RAIL_ORDER is 'Naming is misleading! It also contains bimodal road orders! Used to display the combined Order Item and ship-to combobox for bimodal truck-part orders, and on Order monitoring/Monitoring/Bimodal railcar monitoring';

REM END VLGNC_BIMODAL_RAIL_ORDER

-------------------------------------------------------------------------------
PROMPT VLGNS_SAP_NETP_ORDER_ITEM_SUM

CREATE OR REPLACE FORCE VIEW VLGNS_SAP_NETP_ORDER_ITEM_SUM AS
SELECT np.order_item_id,
  CAST((SUM(np.netprice_quantity)) AS NUMBER(18,3)) netprice_quantity,
  CAST((SUM(np.netprice_net_value)) AS NUMBER(17,2)) netprice_net_value,
  CAST((SUM(np.netprice_rebate_total)) AS NUMBER(17,2)) netprice_rebate_total,
  CAST((SUM(np.netprice_tax)) AS NUMBER(17,2)) netprice_tax,
  CAST((SUM(np.netprice_cash_discount)) AS NUMBER(17,2)) netprice_cash_discount
FROM tlgns_sap_netprice_order_item np
GROUP by np.order_item_id;

comment on table VLGNS_SAP_NETP_ORDER_ITEM_SUM is 'Sum up of tlgns_sap_netprice_order_item over order_item_id. Used in vlgnc_order_item_margin.';
REM END ULGN01 VLGNS_SAP_NETP_ORDER_ITEM_SUM
-------------------------------------------------------------------------------
PROMPT VLGNC_ORDER_ITEM_MARGIN

-- See: https://aww.adnovum.ch/twiki/bin/view/CrhPegasus/ContributionMarginCalculation

CREATE OR REPLACE FORCE VIEW VLGNC_ORDER_ITEM_MARGIN AS
  SELECT
  oi.order_item_id,
  oi.order_item_no,
  oi.order_id,
  ord.order_number,
  oi.sap_material_id,
  mat.sap_material_no,
  oi.production_plant_cd,
  oi.delivered_quantity quantity,
  oi.currency_cd,
  /********INCOME********/
  oi.delivered_uom_cd, /*TODO - Limitation: assumes all UOM are TO (Nov 2009: This is the case, all SAP Material only have base_uom_cd = TO)*/
  CAST((np.netprice_quantity) AS NUMBER(18,3)) netprice_quantity,
  --oi.gross_price_mat gross_price, /* = rate * delivered_quantity as well */
  cast(NULL as decimal(10,3)) AS gross_price,
  -- cast((oi.rate) as NUMBER) gross_price_pt, /* The rate is the only price which is also directly stored as "per UOM" on the order item itself. For consistency however cast to NUMBER (i.e. double) in Java as well) */
  cast(NULL as number) AS gross_price_pt,
  cast((np.netprice_net_value - oi.gross_price_mat) AS NUMBER(17,2)) discounts,
  (np.netprice_net_value - oi.gross_price_mat) / NULLIF(oi.delivered_quantity,   0) discounts_pt,
  cast(np.netprice_net_value as NUMBER(17,2)) net_price,
  np.netprice_net_value / NULLIF(oi.delivered_quantity,   0) net_price_pt,
  cast(np.netprice_rebate_total as NUMBER(17,2)) rebates,
  np.netprice_rebate_total / NULLIF(oi.delivered_quantity,   0) rebates_pt,
  cast(np.netprice_cash_discount as NUMBER(17,2)) cash_discount,
  np.netprice_cash_discount / NULLIF(oi.delivered_quantity,   0)  cash_discount_pt,
  cast((np.netprice_net_value + np.netprice_rebate_total + np.netprice_cash_discount) AS NUMBER(17,2))  net_sales_earning,
  (np.netprice_net_value + np.netprice_rebate_total + np.netprice_cash_discount) / NULLIF(oi.delivered_quantity,   0)net_sales_earning_pt,
  /********COSTS********/
  oi.prod_cost_mat prod_cost,
  oi.prod_cost_mat / NULLIF(oi.delivered_quantity,   0) prod_cost_pt,
  oi.iud_cost iud_cost,
  oi.iud_cost / NULLIF(oi.delivered_quantity,   0) iud_cost_pt,
  oi.handling_cost_mat handling_cost,
  oi.handling_cost_mat / NULLIF(oi.delivered_quantity,   0) handling_cost_pt,
  oi.out_cost out_cost,
  oi.out_cost / NULLIF(oi.delivered_quantity,   0) out_cost_pt,
  cast((oi.iud_cost + oi.handling_cost_mat + oi.out_cost) AS NUMBER(11,2)) total_dist_cost,
  (oi.iud_cost + oi.handling_cost_mat + oi.out_cost) / NULLIF(oi.delivered_quantity,   0) total_dist_cost_pt,
   cast((oi.prod_cost_mat + oi.iud_cost + oi.handling_cost_mat + oi.out_cost) AS NUMBER(11,2)) total_cost,
  (oi.prod_cost_mat + oi.iud_cost + oi.handling_cost_mat + oi.out_cost) / NULLIF(oi.delivered_quantity,   0) total_cost_pt,
  cast(((np.netprice_net_value + np.netprice_rebate_total + np.netprice_cash_discount) - (oi.prod_cost_mat + oi.iud_cost + oi.handling_cost_mat + oi.out_cost)) AS NUMBER(17,2)) contrib_margin,
  ((np.netprice_net_value + np.netprice_rebate_total + np.netprice_cash_discount) - (oi.prod_cost_mat + oi.iud_cost + oi.handling_cost_mat + oi.out_cost)) / NULLIF(oi.delivered_quantity,   0) contrib_margin_pt
FROM tlgnc_order_item oi
INNER JOIN tlgns_sap_material mat ON (oi.sap_material_id = mat.sap_material_id)
INNER JOIN tlgnc_order ord ON (oi.order_id = ord.order_id)
LEFT OUTER JOIN vlgns_sap_netp_order_item_sum np ON(np.order_item_id = oi.order_item_id);

comment on table VLGNC_ORDER_ITEM_MARGIN is 'Used to display income and costs in the commercial tab in the order detail screen.';
rem end ulgn01 vlgnc_order_item_margin
-------------------------------------------------------------------------------
PROMPT VLGNC_RETURN_STATIONS_IN_USE

CREATE OR REPLACE FORCE VIEW VLGNC_RETURN_STATIONS_IN_USE AS
SELECT DISTINCT
  rw.railway_station_id,
  rw.railway_station_name,
  rw.uic_country_cd,
  rw.railway_station_no
FROM tlgnc_wagon wagon
INNER JOIN tlgnc_shipto sh ON (sh.shipto_sap_bp_id = wagon.return_shipto_sap_bp_id and sh.shipto_type_cd = 6)
INNER JOIN tlgns_sap_bp sapbp ON(sapbp.sap_bp_id = sh.shipto_sap_bp_id)
INNER JOIN tlgnc_railway_station rw ON (rw.railway_station_id = sapbp.railway_station_id);

comment on table VLGNC_RETURN_STATIONS_IN_USE is 'Railway stations signed as return railway stations for wagons.';
REM END ULGN01 VLGNC_RETURN_STATIONS_IN_USE

-------------------------------------------------------------------------------
PROMPT VLGNC_LOCATION_STATIONS_IN_USE

CREATE OR REPLACE FORCE VIEW VLGNC_LOCATION_STATIONS_IN_USE AS
SELECT DISTINCT
  rw.railway_station_id,
  rw.railway_station_name,
  rw.uic_country_cd,
  rw.railway_station_no
FROM tlgnc_wagon wagon
INNER JOIN tlgnc_railway_station rw ON (rw.railway_station_id = wagon.last_known_railway_station_id);

comment on table VLGNC_LOCATION_STATIONS_IN_USE is 'Railway stations where any wagons were last seen';
REM END ULGN01 VLGNC_LOCATION_STATIONS_IN_USE

-------------------------------------------------------------------------------
PROMPT VLGNC_WAGON_SEARCH

CREATE OR REPLACE FORCE VIEW VLGNC_WAGON_SEARCH AS
SELECT
  -- WAGON
  nvl2(ord.order_number, wagon.wagon_id || '_' || ord.order_number || '_' || item.order_item_no || '_' || wagon_type_tx.lang_cd, wagon.wagon_id || '_' || wagon_type_tx.lang_cd) AS COMPOSITE_ID,
  wagon_type_tx.lang_cd,
  wagon.wagon_id,
  wagon.wagon_no,
  wagon_type_bs.wagon_type_cd,
  wagon_type_bs.wagon_type_name,
  wagon.active_flag,
  wagon.wagon_inactive_reason_cd,
  wagonInactiveReasonTx.TXT AS WAGON_INACTIVE_REASON_TXT,
  wagon_keeper.wagon_keeper_id,
  wagon_keeper.wagon_keeper_name,
  wagon.wagon_note_disposition,
  wagon.wagon_note_railway_spedition,
  return_shipto.name1 AS RETURN_SHIPTO_NAME1,
  return_railway_station.railway_station_id AS RETURN_STATION_ID,
  return_railway_station.railway_station_no AS RETURN_STATION_NO,
  return_railway_station.railway_station_name AS RETURN_STATION_NAME,
  railway_station.railway_station_id AS LAST_KNOWN_STATION_ID,
  railway_station.railway_station_no AS LAST_KNOWN_STATION_NO,
  railway_station.railway_station_name AS LAST_KNOWN_STATION_NAME,
  wagon.arrival_last_known_station_dat,
  wagon.arrival_last_known_station_dat AS ARRIVAL_LAST_KNOWN_STATION_TIM,
  round(to_number(SYSDATE-wagon.arrival_last_known_station_dat)*1440) AS WAGON_IDLE_TIME_MIN,
  plant_track_tx.plant_track_cd,
  plant_track_tx.txt AS PLANT_TRACK_TXT,
  wagon.current_track_position_no,
  wagon.max_gross_weight_t,
  wagon.tare_weight_t,
  wagon.volume_m3,
  wagon.limit_credit_note_t,
  -- ORDER
  ord.order_id,
  ord.order_number,
  item.order_item_no,
  ord.order_status_cd,
  status.txt AS ORDER_STATUS_TXT,
  ord.distribution_dest_cd,
  distributionDestTx.TXT AS DISTRIBUTION_DEST_TXT,
  soldto.sap_id AS SOLDTO_SAP_ID,
  soldto.name1 AS SOLDTO_NAME1,
  shipto.sap_id AS SHIPTO_SAP_ID,
  shipto.name1 AS SHIPTO_NAME1,
  shipto_rws.railway_station_id AS DEST_RAILWAY_STATION_ID,
  shipto_rws.railway_station_no AS DEST_RAILWAY_STATION_NO,
  shipto_rws.railway_station_name AS DEST_RAILWAY_STATION_NAME,
  material.sap_material_no,
  materialdesc.txt AS SAP_MATERIAL_TXT,
  item.loaded_quantity,
  loadeduom.txt AS LOADED_UOM,
  wagon.loading_limit_a_t,
  wagon.loading_limit_b_t,
  wagon.loading_limit_c_t,
  wagon.loading_limit_d_t,
  wagon.wheelbase_mm,
  wagon.wheelbase_bpp_mm,
  wagon.buffer_length_mm,
  wagon.undercarriage_manufacturer,
  wagon.undercarriage_const_year,
  wagon.superstructure_manufacturer,
  wagon.superstructure_const_year,
  wagon.unloading_system,
  wagon.drawing_type,
  wagon.brake_type,
  wagon.brake_blocks_type,
  wagon.brake_blocks_count,
  wagon.brake_cylinder_no,
  wagon.brake_rigging_no,
  wagon.brake_hosepipe_no,
  wagon.brake_hosepipe_nbs,
  wagon.brake_hosepipe_bs,
  wagon.mechanical_load_change,
  wagon.air_shut_off_cock_type,
  wagon.triple_valve_no,
  wagon.triple_valve_type,
  wagon.weighing_valve_no,
  wagon.weighing_valve_type,
  wagon.pressure_exchanger_type,
  wagon.pressure_exchanger_no,
  wagon.buffer_type,
  wagon.buffer_const_year,
  wagon.bogie_type,
  wagon.bogie_no,
  wagon.spring_type,
  wagon.wheelset_1_nbs_no,
  wagon.wheelset_1_nbs_build,
  wagon.wheelset_1_nbs_diameter_mm,
  wagon.wheelset_1_nbs_year,
  wagon.wheelset_2_nbs_no,
  wagon.wheelset_2_nbs_build,
  wagon.wheelset_2_nbs_diameter_mm,
  wagon.wheelset_2_nbs_year,
  wagon.wheelset_3_bs_no,
  wagon.wheelset_3_bs_build,
  wagon.wheelset_3_bs_diameter_mm,
  wagon.wheelset_3_bs_year,
  wagon.wheelset_4_bs_no,
  wagon.wheelset_4_bs_build,
  wagon.wheelset_4_bs_diameter_mm,
  wagon.wheelset_4_bs_year,
  wagon.maint_rhythm_type_cd,
  nvl2(maint_g10.maint_type_cd, maint_g10.maint_type_cd || ' ' || maint_g10_maint_type_tx.txt, null) AS MAINT_G10_MAIN_TYPE_TXT,
  maint_g10.last_dat AS MAINT_G10_LAST_DAT,
  maint_g10.next_dat AS MAINT_G10_NEXT_DAT,
  nvl2(maint_g10_shipto.name1, maint_g10_shipto.name1 || ' (' || maint_g10_shipto.sap_id || ')', null) AS MAINT_G10_SHIPTO,
  nvl2(maint_g20.maint_type_cd, maint_g20.maint_type_cd || ' ' || maint_g20_maint_type_tx.txt, null) AS MAINT_G20_MAIN_TYPE_TXT,
  maint_g20.last_dat AS MAINT_G20_LAST_DAT,
  maint_g20.next_dat AS MAINT_G20_NEXT_DAT,
  nvl2(maint_g20_shipto.name1, maint_g20_shipto.name1 || ' (' || maint_g20_shipto.sap_id || ')', null) AS MAINT_G20_SHIPTO,
  nvl2(maint_g30.maint_type_cd, maint_g30.maint_type_cd || ' ' || maint_g30_maint_type_tx.txt, null) AS MAINT_G30_MAIN_TYPE_TXT,
  maint_g30.last_dat AS MAINT_G30_LAST_DAT,
  maint_g30.next_dat AS MAINT_G30_NEXT_DAT,
  nvl2(maint_g30_shipto.name1, maint_g30_shipto.name1 || ' (' || maint_g30_shipto.sap_id || ')', null) AS MAINT_G30_SHIPTO,
  nvl2(maint_g40.maint_type_cd, maint_g40.maint_type_cd || ' ' || maint_g40_maint_type_tx.txt, null) AS MAINT_G40_MAIN_TYPE_TXT,
  maint_g40.last_dat AS MAINT_G40_LAST_DAT,
  maint_g40.next_dat AS MAINT_G40_NEXT_DAT,
  nvl2(maint_g40_shipto.name1, maint_g40_shipto.name1 || ' (' || maint_g40_shipto.sap_id || ')', null) AS MAINT_G40_SHIPTO,
  nvl2(maint_g42.maint_type_cd, maint_g42.maint_type_cd || ' ' || maint_g42_maint_type_tx.txt, null) AS MAINT_G42_MAIN_TYPE_TXT,
  maint_g42.last_dat AS MAINT_G42_LAST_DAT,
  maint_g42.next_dat AS MAINT_G42_NEXT_DAT,
  nvl2(maint_g42_shipto.name1, maint_g42_shipto.name1 || ' (' || maint_g42_shipto.sap_id || ')', null) AS MAINT_G42_SHIPTO,
  nvl2(maint_g43.maint_type_cd, maint_g43.maint_type_cd || ' ' || maint_g43_maint_type_tx.txt, null) AS MAINT_G43_MAIN_TYPE_TXT,
  maint_g43.last_dat AS MAINT_G43_LAST_DAT,
  maint_g43.next_dat AS MAINT_G43_NEXT_DAT,
  nvl2(maint_g43_shipto.name1, maint_g43_shipto.name1 || ' (' || maint_g43_shipto.sap_id || ')', null) AS MAINT_G43_SHIPTO,
  nvl2(maint_g48.maint_type_cd, maint_g48.maint_type_cd || ' ' || maint_g48_maint_type_tx.txt, null) AS MAINT_G48_MAIN_TYPE_TXT,
  maint_g48.last_dat AS MAINT_G48_LAST_DAT,
  maint_g48.next_dat AS MAINT_G48_NEXT_DAT,
  nvl2(maint_g48_shipto.name1, maint_g48_shipto.name1 || ' (' || maint_g48_shipto.sap_id || ')', null) AS MAINT_G48_SHIPTO,
  nvl2(maint_p3m.maint_type_cd, maint_p3m.maint_type_cd || ' ' || maint_p3m_maint_type_tx.txt, null) AS MAINT_P3M_MAIN_TYPE_TXT,
  maint_p3m.last_dat AS MAINT_P3M_LAST_DAT,
  maint_p3m.next_dat AS MAINT_P3M_NEXT_DAT,
  nvl2(maint_p3m_shipto.name1, maint_p3m_shipto.name1 || ' (' || maint_p3m_shipto.sap_id || ')', null) AS MAINT_P3M_SHIPTO,
  nvl2(maint_p5.maint_type_cd, maint_p5.maint_type_cd || ' ' || maint_p5_maint_type_tx.txt, null) AS MAINT_P5_MAIN_TYPE_TXT,
  maint_p5.last_dat AS MAINT_P5_LAST_DAT,
  maint_p5.next_dat AS MAINT_P5_NEXT_DAT,
  nvl2(maint_p5_shipto.name1, maint_p5_shipto.name1 || ' (' || maint_p5_shipto.sap_id || ')', null) AS MAINT_P5_SHIPTO,
  nvl2(maint_c.maint_type_cd, maint_c.maint_type_cd || ' ' || maint_c_maint_type_tx.txt, null) AS MAINT_C_MAIN_TYPE_TXT,
  maint_c.last_dat AS MAINT_C_LAST_DAT,
  maint_c.next_dat AS MAINT_C_NEXT_DAT,
  nvl2(maint_c_shipto.name1, maint_c_shipto.name1 || ' (' || maint_c_shipto.sap_id || ')', null) AS MAINT_C_SHIPTO
FROM
  tlgnc_wagon wagon
  JOIN tlgnr_wagon_type_bs wagon_type_bs ON wagon_type_bs.wagon_type_cd = wagon.wagon_type_cd
  JOIN tlgnr_wagon_type_tx wagon_type_tx ON wagon_type_tx.wagon_type_cd = wagon_type_bs.wagon_type_cd
  JOIN tlgnc_wagon_keeper wagon_keeper ON wagon_keeper.wagon_keeper_id = wagon.wagon_keeper_id
  LEFT JOIN tlgnr_wagon_inactive_reason_tx wagonInactiveReasonTx ON wagon.wagon_inactive_reason_cd = wagonInactiveReasonTx.wagon_inactive_reason_cd AND wagonInactiveReasonTx.lang_cd = wagon_type_tx.lang_cd
  LEFT JOIN tlgns_sap_bp return_shipto ON return_shipto.sap_bp_id = wagon.return_shipto_sap_bp_id AND return_shipto.bp_is_shipto = 1
  LEFT JOIN tlgnc_railway_station return_railway_station ON (return_railway_station.railway_station_id = return_shipto.railway_station_id)
  LEFT JOIN tlgnc_railway_station railway_station ON railway_station.railway_station_id = wagon.last_known_railway_station_id
  LEFT JOIN tlgnr_plant_track_tx plant_track_tx ON plant_track_tx.plant_track_cd = wagon.current_plant_track_cd AND plant_track_tx.lang_cd = wagon_type_tx.lang_cd
  LEFT JOIN (
  (select distinct wagon_id as wagon_id, first_value(a1.assignment_id)
  over (partition by wagon_id order by planned_start ) as assignment_id  from tlgnc_assignment a1 join tlgnc_assignment_part p1 on p1.assignment_id=a1.assignment_id
  where  wagon_id is not null and active_flag = 1 AND assign_status_cd NOT IN (6, 7)) assi)   on (assi.wagon_id = wagon.wagon_id)
  LEFT  JOIN (tlgnc_assignment_part unloadpart
    JOIN tlgnc_order ord ON ord.order_id = unloadpart.order_id
    JOIN tlgnc_order_item item ON ord.order_id = item.order_id
    JOIN tlgns_sap_material material ON material.sap_material_id = item.sap_material_id
    JOIN tlgns_sap_bp shipto ON ord.shipto_sap_bp_id = shipto.sap_bp_id AND shipto.bp_is_shipto = 1
    JOIN tlgns_sap_bp soldto ON ord.soldto_sap_bp_id = soldto.sap_bp_id AND soldto.bp_is_soldto = 1
    LEFT JOIN tlgnc_railway_station shipto_rws ON shipto.railway_station_id = shipto_rws.railway_station_id
    )  ON unloadpart.assignment_id = assi.assignment_id AND unloadpart.assignment_part_type_cd = 3
  LEFT JOIN tlgnr_order_status_tx status ON status.order_status_cd = ord.order_status_cd AND status.lang_cd = wagon_type_tx.lang_cd
  LEFT JOIN tlgnr_distribution_dest_tx distributionDestTx on distributionDestTx.distribution_dest_cd = ord.distribution_dest_cd and distributionDestTx.lang_cd = wagon_type_tx.lang_cd
  LEFT JOIN tlgns_sap_material_description materialdesc ON materialdesc.sap_material_id = item.sap_material_id AND materialdesc.lang_cd = wagon_type_tx.lang_cd
  LEFT JOIN tlgnr_uom_tx loadeduom ON loadeduom.uom_cd = item.loaded_uom_cd AND loadeduom.lang_cd = wagon_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_g10
    LEFT JOIN tlgns_sap_bp maint_g10_shipto ON maint_g10.shipto_sap_bp_id = maint_g10_shipto.sap_bp_id AND maint_g10_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_g10_maint_type_tx ON maint_g10.maint_type_cd = maint_g10_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_g10.wagon_id AND maint_g10.maint_type_cd = 'G1.0' AND wagon_type_tx.lang_cd = maint_g10_maint_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_g20
    LEFT JOIN tlgns_sap_bp maint_g20_shipto ON maint_g20.shipto_sap_bp_id = maint_g20_shipto.sap_bp_id AND maint_g20_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_g20_maint_type_tx ON maint_g20.maint_type_cd = maint_g20_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_g20.wagon_id AND maint_g20.maint_type_cd = 'G2.0' AND wagon_type_tx.lang_cd = maint_g20_maint_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_g30
    LEFT JOIN tlgns_sap_bp maint_g30_shipto ON maint_g30.shipto_sap_bp_id = maint_g30_shipto.sap_bp_id AND maint_g30_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_g30_maint_type_tx ON maint_g30.maint_type_cd = maint_g30_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_g30.wagon_id AND maint_g30.maint_type_cd = 'G3.0' AND wagon_type_tx.lang_cd = maint_g30_maint_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_g40
    LEFT JOIN tlgns_sap_bp maint_g40_shipto ON maint_g40.shipto_sap_bp_id = maint_g40_shipto.sap_bp_id AND maint_g40_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_g40_maint_type_tx ON maint_g40.maint_type_cd = maint_g40_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_g40.wagon_id AND maint_g40.maint_type_cd = 'G4.0' AND wagon_type_tx.lang_cd = maint_g40_maint_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_g42
    LEFT JOIN tlgns_sap_bp maint_g42_shipto ON maint_g42.shipto_sap_bp_id = maint_g42_shipto.sap_bp_id AND maint_g42_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_g42_maint_type_tx ON maint_g42.maint_type_cd = maint_g42_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_g42.wagon_id AND maint_g42.maint_type_cd = 'G4.2' AND wagon_type_tx.lang_cd = maint_g42_maint_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_g43
    LEFT JOIN tlgns_sap_bp maint_g43_shipto ON maint_g43.shipto_sap_bp_id = maint_g43_shipto.sap_bp_id AND maint_g43_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_g43_maint_type_tx ON maint_g43.maint_type_cd = maint_g43_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_g43.wagon_id AND maint_g43.maint_type_cd = 'G4.3' AND wagon_type_tx.lang_cd = maint_g43_maint_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_g48
    LEFT JOIN tlgns_sap_bp maint_g48_shipto ON maint_g48.shipto_sap_bp_id = maint_g48_shipto.sap_bp_id AND maint_g48_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_g48_maint_type_tx ON maint_g48.maint_type_cd = maint_g48_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_g48.wagon_id AND maint_g48.maint_type_cd = 'G4.8' AND wagon_type_tx.lang_cd = maint_g48_maint_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_p3m
    LEFT JOIN tlgns_sap_bp maint_p3m_shipto ON maint_p3m.shipto_sap_bp_id = maint_p3m_shipto.sap_bp_id AND maint_p3m_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_p3m_maint_type_tx ON maint_p3m.maint_type_cd = maint_p3m_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_p3m.wagon_id AND maint_p3m.maint_type_cd = '+3M' AND wagon_type_tx.lang_cd = maint_p3m_maint_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_p5
    LEFT JOIN tlgns_sap_bp maint_p5_shipto ON maint_p5.shipto_sap_bp_id = maint_p5_shipto.sap_bp_id AND maint_p5_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_p5_maint_type_tx ON maint_p5.maint_type_cd = maint_p5_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_p5.wagon_id AND maint_p5.maint_type_cd = 'P5' AND wagon_type_tx.lang_cd = maint_p5_maint_type_tx.lang_cd
  LEFT JOIN (tlgnc_wagon_maint maint_c
    LEFT JOIN tlgns_sap_bp maint_c_shipto ON maint_c.shipto_sap_bp_id = maint_c_shipto.sap_bp_id AND maint_c_shipto.bp_is_shipto = 1
    JOIN tlgnr_maint_type_tx maint_c_maint_type_tx ON maint_c.maint_type_cd = maint_c_maint_type_tx.maint_type_cd
  ) on wagon.wagon_id = maint_c.wagon_id AND maint_c.maint_type_cd = 'C' AND wagon_type_tx.lang_cd = maint_c_maint_type_tx.lang_cd
  left join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD)
  where (archivingstatus.DISPLAY_FLAG is null or archivingstatus.DISPLAY_FLAG = 1);

comment on table VLGNC_WAGON_SEARCH is 'Wagon search view with data about wagons. Needed by wagon search screen.';
REM END ULGN01 VLGNC_WAGON_SEARCH


-------------------------------------------------------------------------------
PROMPT vlgnc_plant_railway_station

 create or replace force view vlgnc_plant_railway_station as
  select distinct
    sp.plant_cd || '_' || tr.plant_track_cd as composite_id,
    sp.plant_cd,
    rw.railway_station_id,
    rw.railway_station_no,
    tr.track_name,
    tr.plant_track_cd,
    trtx.txt,
    trtx.lang_cd,
    cast((select max (current_track_position_no) from tlgnc_wagon where current_plant_track_cd = tr.plant_track_cd) as number(12))as max_track_position_no,
    tr.track_name || ' - ' || trtx.txt as display_track_name
from tlgnc_shippingpoint sp
         join tlgnc_railway_station rw on (sp.railway_station_id = rw.railway_station_id and sp.shipppoint_type_cd = 1)
         join tlgnr_plant_track_bs tr on (tr.plant_cd = sp.plant_cd)
         join tlgnr_plant_track_tx trtx on (trtx.plant_track_cd = tr.plant_track_cd);

comment on table vlgnc_plant_railway_station is 'railway stations which are related to plants';
rem end ulgn01 vlgnc_plant_railway_station

-------------------------------------------------------------------------------
PROMPT vlgnc_rail_distance

 create or replace force view vlgnc_rail_distance as
  select
  	row_number() over(order by dep_station_name) as row_number,
  	dist.rail_distance_id,
    stations.dep_station_name,
    stations.dep_station_id,
    stations.arr_station_name,
    stations.arr_station_id,
    dist.tariff_distance_m
 from tlgnc_rail_distance dist
  right join
 (
  select
	tr_dep.railway_station_name as DEP_STATION_NAME,
	tr_dep.railway_station_id as DEP_STATION_ID,
	tr_arr.railway_station_name as ARR_STATION_NAME,
	tr_arr.railway_station_id as ARR_STATION_ID
 from tlgnc_rail_distance trd
  join tlgnc_railway_station tr_dep on trd.dep_railway_station_id = tr_dep.railway_station_id
  join tlgnc_railway_station tr_arr on trd.arr_railway_station_id = tr_arr.railway_station_id
  union
  select
	tr_dep.railway_station_name as DEP_STATION_NAME,
	tr_dep.railway_station_id as DEP_STATION_ID,
	tr_arr.railway_station_name as ARR_STATION_NAME,
	tr_arr.railway_station_id as ARR_STATION_ID
 from tlgnc_rail_timetable trt
  join tlgnc_railway_station tr_dep on trt.dep_railway_station_id = tr_dep.railway_station_id
  join tlgnc_railway_station tr_arr on trt.arr_railway_station_id = tr_arr.railway_station_id
 ) stations
 on (dist.dep_railway_station_id = stations.dep_station_id and dist.arr_railway_station_id = stations.arr_station_id)
 where not exists
 (select 1
   from tlgnc_rail_distance helperDist
   where helperDist.arr_railway_station_id = stations.dep_station_id
     and helperDist.dep_railway_station_id = stations.arr_station_id);

comment on table vlgnc_rail_distance is 'distances with corresponding timetables';
rem end ulgn01 vlgnc_rail_distance

-------------------------------------------------------------------------------
PROMPT vlgnc_rail_plant

 create or replace force view vlgnc_rail_plant as
  select distinct
    plant.plant_cd,
    plant.plant_name,
    plant.etran_flag
from tlgnr_plant_bs plant
         join tlgnc_shippingpoint sp on (sp.plant_cd = plant.plant_cd and sp.shipppoint_type_cd in (1,4,5,6,7));

comment on table vlgnc_rail_plant is 'plants with rail shippingpoints for raildispo';
rem end ulgn01 vlgnc_rail_plant

-------------------------------------------------------------------------------
PROMPT vlgnc_rail_plant_sp

 create or replace force view vlgnc_rail_plant_sp as
  select
    sp.shippingpoint_id,
    sp.shippingpoint_name,
    sp.railway_station_id,
    plant.plant_cd
from tlgnr_plant_bs plant
         join tlgnc_shippingpoint sp on (sp.plant_cd = plant.plant_cd and sp.shipppoint_type_cd in (1,4,5,6,7))
where sp.railway_station_id is not null;

comment on table vlgnc_rail_plant_sp is 'rail shippingpoints by plant for raildispo';
rem end ulgn01 vlgnc_rail_plant_sp

-------------------------------------------------------------------------------
PROMPT vlgnc_rail_repair

  create or replace force view vlgnc_rail_repair as
  select
    rr.*,
    cast(
      case when
      rr.order_status_cd < 16 then
        0 else
        case when
        ((rr.offer_desired_flag = 1 AND (rr.has_offer_attachment_flag = 0 OR rr.offer_accept_dat IS NULL))
        OR (rr.has_invoice_attachment_flag = 1 AND rr.invoice_paid_flag = 0)) then
        1 else
        2
      end
    end
  as number(2)) as combined_order_status_flag
    from (select
    ord_rep.order_rep_id || nvl2(damagecode.damagecode_cd, '.' || damagecode.damagecode_cd, '') as rail_rep_composite_id,
    ord_rep.order_rep_id,
    ord.order_id,
    ord.order_status_cd,
    ordtx.txt as order_status_txt,
    ordtx.lang_cd,
    wagon.wagon_id,
    wagon.wagon_no,
    wagon_keeper.wagon_keeper_id,
    wagon_keeper.wagon_keeper_name,
    shipto.sap_id as shipto_sap_id,
    shipto.sap_bp_id as shipto_sap_bp_id,
    shipto.name1 as shipto_name,
    ord.order_number as order_no,
    ord.ctl_cre_ts as created,
    unloadpart.real_start as arrived,
    ord_rep.enter_plant_dat as plant_enter,
    ord_rep.leave_plant_dat as plant_leave,
    round(to_number((nvl(ord_rep.leave_plant_dat, SYSDATE) - unloadpart.real_start))*1440) as wagon_idle_time_min,
    damagecode.damagecode_cd as damagecode,
    damagecode.damagecode_txt as damagecode_txt,
    ord_rep_mt_g10.maint_type_cd as maint_g10_main_type_txt,
    ord_rep_mt_g20.maint_type_cd as maint_g20_main_type_txt,
    ord_rep_mt_g30.maint_type_cd as maint_g30_main_type_txt,
    ord_rep_mt_g40.maint_type_cd as maint_g40_main_type_txt,
    ord_rep_mt_g42.maint_type_cd as maint_g42_main_type_txt,
    ord_rep_mt_g43.maint_type_cd as maint_g43_main_type_txt,
    ord_rep_mt_g48.maint_type_cd as maint_g48_main_type_txt,
    ord_rep_mt_p3m.maint_type_cd as maint_p3m_main_type_txt,
    ord_rep_mt_p5.maint_type_cd as maint_p5_main_type_txt,
    ord_rep_mt_c.maint_type_cd as maint_c_main_type_txt,
    ord_rep.email_additional_remarks as remarks,
    ord_rep.offer_desired_flag,
    ord_rep.offer_accept_dat,
    ord_rep.invoice_paid_flag,
    ord_rep.documentation_accept_flag,
    case when exists (select 1 from TLGNC_ORDER_REP_ATT ord_rep_att where ord_rep_att.order_rep_id = ord_rep.order_rep_id and attachment_type_cd = 1) then 1 else 0 end as has_offer_attachment_flag,
    case when exists (select 1 from TLGNC_ORDER_REP_ATT ord_rep_att where ord_rep_att.order_rep_id = ord_rep.order_rep_id and attachment_type_cd = 2) then 1 else 0 end as has_docu_attachment_flag,
    case when exists (select 1 from TLGNC_ORDER_REP_ATT ord_rep_att where ord_rep_att.order_rep_id = ord_rep.order_rep_id and attachment_type_cd = 3) then 1 else 0 end as has_invoice_attachment_flag
  FROM tlgnc_order_rep ord_rep
    LEFT JOIN (select wagon_id AS wagon_id,
                 order_id AS order_id,
                 a1.assignment_id AS assignment_id
            FROM tlgnc_assignment a1
            JOIN tlgnc_assignment_part p1 ON p1.assignment_id = a1.assignment_id
            WHERE wagon_id IS NOT NULL
              AND active_flag = 1
              AND assign_status_cd <> 7) assi on (assi.order_id = ord_rep.order_id)
    join tlgnc_wagon wagon on (wagon.wagon_id = assi.wagon_id)
    join tlgnc_wagon_keeper wagon_keeper ON (wagon_keeper.wagon_keeper_id = wagon.wagon_keeper_id)
    JOIN tlgnc_assignment_part unloadpart ON (unloadpart.assignment_id = assi.assignment_id AND unloadpart.assignment_part_type_cd = 3)
      JOIN tlgnc_order ord ON (ord.order_id = ord_rep.order_id AND ord.order_status_cd <> 17 AND ord.order_status_cd <> 20 AND ord.distribution_dest_cd = 'REP')
      JOIN tlgnr_order_status_tx ordtx ON (ord.order_status_cd = ordtx.order_status_cd)
      INNER JOIN TLGNC_ORDER_ITEM item ON (ord.order_id = item.order_id)
      LEFT OUTER JOIN TLGNC_LOAD load ON (load.LOAD_ID = item.LOAD_ID)
      LEFT OUTER JOIN TLGNC_ASSIGNMENT_PART part ON (part.load_id = item.load_id)
      join tlgns_sap_bp shipto ON (ord.shipto_sap_bp_id = shipto.sap_bp_id AND shipto.bp_is_shipto = 1)
      left join tlgnc_order_rep_dmgcode_type on ord_rep.ORDER_REP_ID = tlgnc_order_rep_dmgcode_type.ORDER_REP_ID
        left join tlgnr_damagecode_tx damagecode on (damagecode.damagecode_cd = tlgnc_order_rep_dmgcode_type.dmgcode_type_id AND damagecode.lang_cd = ordtx.lang_cd)
      left join tlgnc_order_rep_maint_type ord_rep_mt_g10 ON ord_rep.order_rep_id = ord_rep_mt_g10.order_rep_id and ord_rep_mt_g10.maint_type_cd = 'G1.0'
      left join tlgnc_order_rep_maint_type ord_rep_mt_g20 ON ord_rep.order_rep_id = ord_rep_mt_g20.order_rep_id and ord_rep_mt_g20.maint_type_cd = 'G2.0'
      left join tlgnc_order_rep_maint_type ord_rep_mt_g30 ON ord_rep.order_rep_id = ord_rep_mt_g30.order_rep_id and ord_rep_mt_g30.maint_type_cd = 'G3.0'
      left join tlgnc_order_rep_maint_type ord_rep_mt_g40 ON ord_rep.order_rep_id = ord_rep_mt_g40.order_rep_id and ord_rep_mt_g40.maint_type_cd = 'G4.0'
      left join tlgnc_order_rep_maint_type ord_rep_mt_g42 ON ord_rep.order_rep_id = ord_rep_mt_g42.order_rep_id and ord_rep_mt_g42.maint_type_cd = 'G4.2'
      left join tlgnc_order_rep_maint_type ord_rep_mt_g43 ON ord_rep.order_rep_id = ord_rep_mt_g43.order_rep_id and ord_rep_mt_g43.maint_type_cd = 'G4.3'
      left join tlgnc_order_rep_maint_type ord_rep_mt_g48 ON ord_rep.order_rep_id = ord_rep_mt_g48.order_rep_id and ord_rep_mt_g48.maint_type_cd = 'G4.8'
      left join tlgnc_order_rep_maint_type ord_rep_mt_p3m ON ord_rep.order_rep_id = ord_rep_mt_p3m.order_rep_id and ord_rep_mt_p3m.maint_type_cd = '+3M'
      left join tlgnc_order_rep_maint_type ord_rep_mt_p5 ON ord_rep.order_rep_id = ord_rep_mt_p5.order_rep_id and ord_rep_mt_p5.maint_type_cd = 'P5'
      left join tlgnc_order_rep_maint_type ord_rep_mt_c ON ord_rep.order_rep_id = ord_rep_mt_c.order_rep_id and ord_rep_mt_c.maint_type_cd = 'C') rr;

comment on table vlgnc_rail_repair is 'rail repair order listing for BW-Instandhaltung';
rem end ulgn01 vlgnc_rail_repair

-------------------------------------------------------------------------------
prompt vlgnc_dispo_wagon_list

create or replace force view vlgnc_dispo_wagon_list as
  select
    wagon.wagon_id,
    wagon.wagon_no,
    wagon.wagon_type_cd,
    wagon.written_tare_weight_t,
    wagon.active_flag,
    wagon.wagon_note_disposition,
    wagon.wagon_note_railway_spedition,
    wagon.last_known_railway_station_id,
    wagon.arrival_last_known_station_dat,
    wagon.current_plant_track_cd,
    wagon.current_track_position_no,
    wagon.tare_weight_t,
    wagon.etran_outbound,
    wagon.etran_return,
    rw.railway_station_no,
    rw.railway_station_name,
    nvl(wagon.max_gross_weight_t, wagontype.max_gross_weight_t) as max_gross_weight_t,
    track.plant_cd as track_plant_cd,
    track.track_name,
    track_lp.loadingpoint_id,
    wagontype.wagon_trsp_cat_cd,
    cast(wagontype.nr_containers as number(12)) as nr_containers,
    wagontype.wagon_type_name,
    wagontypetx.lang_cd,
    wagontypetx.txt as wagon_type_name_tx,
    cast(load.load_id as number(12)) as load_id,
    cast(load.sap_material_id as number(12)) as sap_material_id,
    load.tare_weight_t as tare_quantity,
    load.net_quantity,
    load.net_quantity_uom_cd,
    cast(load.load_order_id as number(12)) as load_order_id,
    load.production_plant_cd,
    load.loading_ts,
    matdesc.txt as material_name,
    mat_pack.logon_package_type_cd,
    incoming.planned_start,
    incoming.railway_station_id return_railway_station_id,
    incoming.railway_station_no as arr_railway_station_no,
    incoming.railway_station_name as arr_railway_station_name,
    incoming.assignment_id as incoming_assignment_id,
    incoming.assign_status_cd as incoming_assign_status_cd,
    incoming.assign_tr_status_cd as incoming_assign_tr_status_cd,
    cast(last_load.load_id as number(12)) as last_load_id,
    cast(last_load.sap_material_id as number(12)) as last_sap_material_id,
    last_load.production_plant_cd as last_production_plant_cd,
    last_load.unloading_ts as last_unloading_ts,
    last_matdesc.txt as last_material_name,
    compat.mat_compat_id,
    compat.insignificant_threshold_perc,
    compat.force_identical_threshold_perc,
    wagon_keeper.wagon_keeper_id,
    wagon_keeper.wagon_keeper_name
  from
    tlgnc_wagon wagon
    join tlgnr_wagon_type_bs wagontype on (wagon.wagon_type_cd = wagontype.wagon_type_cd)
    join tlgnr_wagon_type_tx wagontypetx on (wagon.wagon_type_cd = wagontypetx.wagon_type_cd)
    join tlgnc_wagon_keeper wagon_keeper ON wagon_keeper.wagon_keeper_id = wagon.wagon_keeper_id
    left join (
      select distinct
        wagon_id,
        first_value(load_id) over (partition by wagon_id order by load_id) as load_id,
        first_value(sap_material_id) over (partition by wagon_id order by load_id)
            as sap_material_id,
        first_value(tare_weight_t) over (partition by wagon_id order by load_id) as tare_weight_t,
        first_value(net_quantity) over (partition by wagon_id order by load_id) as net_quantity,
        first_value(net_quantity_uom_cd) over (partition by wagon_id order by load_id)
            as net_quantity_uom_cd,
        first_value(load_order_id) over (partition by wagon_id order by load_id) as load_order_id,
        first_value(production_plant_cd) over (partition by wagon_id order by load_id)
           as production_plant_cd,
        first_value(loading_ts) over (partition by wagon_id order by load_id) as loading_ts
      from
        tlgnc_load
      where
        container_id is null
        and wagon_id is not null
        and unloading_ts is null
    ) load on (load.wagon_id = wagon.wagon_id)
    left join tlgns_sap_material mat on (load.sap_material_id = mat.sap_material_id)
    left join tlgnr_material_package_bs mat_pack
        on (mat.material_package_cd = mat_pack.material_package_cd)
    left join tlgns_sap_material_description matdesc
        on (load.sap_material_id = matdesc.sap_material_id
            and wagontypetx.lang_cd = matdesc.lang_cd)
    left join (
      tlgnc_assignment a1
      join tlgnc_assignment_part p1 on (p1.assignment_id = a1.assignment_id
          and p1.assignment_part_type_cd = 3 )
    ) on (a1.wagon_id = wagon.wagon_id and p1.real_start > wagon.arrival_last_known_station_dat
        and a1.assign_status_cd not in (6,7))
    left join (
      select distinct
        a2.wagon_id,
        a2.assignment_id,
        a2.assign_status_cd,
        a2.assign_tr_status_cd,
        first_value(p2.planned_start) over (partition by a2.wagon_id order by p2.planned_start)
            as planned_start,
        bp2.railway_station_id,
        rw2.railway_station_no,
        rw2.railway_station_name
      from
        tlgnc_assignment a2
        join tlgnc_assignment_part p2 on (p2.assignment_id = a2.assignment_id
            and p2.assignment_part_type_cd = 5)
        join tlgnc_shipto st2 on (p2.shipto_id = st2.shipto_id)
        join tlgns_sap_bp bp2 on (st2.shipto_sap_bp_id = bp2.sap_bp_id)
        join tlgnc_railway_station rw2 on (rw2.railway_station_id = bp2.railway_station_id)
      where
        a2.assign_status_cd not in (6,7)
    ) incoming on (incoming.wagon_id = wagon.wagon_id
        and incoming.planned_start > wagon.arrival_last_known_station_dat
        and incoming.railway_station_id != wagon.last_known_railway_station_id)
    left join (
      select distinct
        l.wagon_id,
        first_value(l.load_id) over (partition by l.wagon_id order by l.unloading_ts desc)
            as load_id,
        first_value(l.sap_material_id) over (partition by l.wagon_id order by l.unloading_ts desc)
            as sap_material_id,
        first_value(l.production_plant_cd) over (partition by l.wagon_id order by l.unloading_ts
            desc) as production_plant_cd,
        first_value(l.unloading_ts) over (partition by l.wagon_id order by l.unloading_ts desc)
            as unloading_ts
      from
        tlgnc_load l
        join tlgns_sap_material m on (m.sap_material_id = l.sap_material_id)
      where
        l.wagon_id is not null
        and l.container_id is null
        and l.unloading_ts is not null
        and m.base_uom_cd = 'TO'
    ) last_load on (last_load.wagon_id = wagon.wagon_id)
    left join tlgnc_mat_compat compat on (compat.from_sap_material_id = last_load.sap_material_id)
    left join tlgns_sap_material_description last_matdesc
        on (last_load.sap_material_id = last_matdesc.sap_material_id
            and wagontypetx.lang_cd = last_matdesc.lang_cd)
    left join tlgnr_plant_track_bs track
        on (wagon.current_plant_track_cd = track.plant_track_cd)
    left join tlgnc_track_loadingpoint track_lp
        on (wagon.current_plant_track_cd = track_lp.plant_track_cd)
    left join tlgnc_railway_station rw
        on (rw.railway_station_id = wagon.last_known_railway_station_id)
  where
    a1.assignment_id is null;

comment on table vlgnc_dispo_wagon_list is 'wagon list for raildispo';
rem end ulgn01 vlgnc_dispo_wagon_list;
-------------------------------------------------------------------------------
prompt vlgnc_dispo_w_container_list

create or replace force view vlgnc_dispo_w_container_list as
  select
    container.container_id,
    container.container_no,
    container.container_type_cd,
    container.container_name,
    container.container_comment,
    container.tare_weight_t,
    container.current_wagon_id,
    container.max_net_weight_t,
    containertype.lang_cd,
    containertype.txt as container_type_name,
    cast(load.load_id as number(12)) as load_id,
    cast(load.sap_material_id as number(12)) as sap_material_id,
    load.tare_weight_t as tare_quantity,
    load.net_quantity,
    load.net_quantity_uom_cd,
    cast(load.load_order_id as number(12)) as load_order_id,
    load.production_plant_cd,
    load.loading_ts,
    load.load_cnt,
    matdesc.txt as material_name,
    mat_pack.logon_package_type_cd,
    cast(last_load.load_id as number(12)) as last_load_id,
    cast(last_load.sap_material_id as number(12)) as last_sap_material_id,
    last_load.production_plant_cd as last_production_plant_cd,
    last_load.unloading_ts as last_unloading_ts,
    last_matdesc.txt as last_material_name,
    compat.mat_compat_id,
    compat.insignificant_threshold_perc,
    compat.force_identical_threshold_perc
    from
      tlgnc_container container
      join tlgnr_container_type_tx containertype
          on (container.container_type_cd = containertype.container_type_cd)
      left join (
        select distinct
          container_id,
          count(load_id) over (partition by container_id) as load_cnt,
          first_value(load_id) over (partition by container_id order by load_id ) as load_id,
          first_value(sap_material_id) over (partition by container_id order by load_id)
              as sap_material_id,
          first_value(tare_weight_t) over (partition by container_id order by load_id )
              as tare_weight_t,
          first_value(net_quantity) over (partition by container_id order by load_id)
              as net_quantity,
          first_value(net_quantity_uom_cd) over (partition by container_id order by load_id)
              as net_quantity_uom_cd,
          first_value(load_order_id) over (partition by container_id order by load_id )
              as load_order_id,
          first_value(production_plant_cd) over (partition by container_id order by load_id)
              as production_plant_cd,
          first_value(loading_ts) over (partition by container_id order by load_id) as loading_ts
        from
          tlgnc_load
        where
          container_id is not null
          and unloading_ts is null
      ) load on (load.container_id = container.container_id)
      left join tlgns_sap_material_description matdesc
          on (load.sap_material_id = matdesc.sap_material_id
              and containertype.lang_cd = matdesc.lang_cd)
      left join tlgns_sap_material mat on (load.sap_material_id = mat.sap_material_id)
      left join tlgnr_material_package_bs mat_pack
          on (mat.material_package_cd = mat_pack.material_package_cd)
      left join (
        select distinct
          l.container_id,
          first_value(l.load_id) over (partition by l.container_id order by l.unloading_ts desc)
              as load_id,
          first_value(l.sap_material_id) over (partition by l.container_id order by l.unloading_ts
              desc) as sap_material_id,
          first_value(l.production_plant_cd) over (partition by l.container_id
              order by l.unloading_ts desc) as production_plant_cd,
          first_value(l.unloading_ts) over (partition by l.container_id order by l.unloading_ts
              desc) as unloading_ts
        from
          tlgnc_load l
          join tlgns_sap_material m on (m.sap_material_id = l.sap_material_id)
        where
          l.container_id is not null
          and l.unloading_ts is not null
          and m.base_uom_cd = 'TO'
      ) last_load on (last_load.container_id = container.container_id)
      left join tlgnc_mat_compat compat on (compat.from_sap_material_id = last_load.sap_material_id)
      left join tlgns_sap_material_description last_matdesc
          on (last_load.sap_material_id = last_matdesc.sap_material_id
              and containertype.lang_cd = last_matdesc.lang_cd);

comment on table vlgnc_dispo_w_container_list is 'container on wagon list for raildispo';
rem end ulgn01 vlgnc_dispo_w_container_list;

---------------------------------------------------------------------------------
prompt vlgnc_dispo_plant_track_list

create or replace force view vlgnc_dispo_plant_track_list as
select
  track.plant_track_cd,
  track.plant_cd,
  track.track_name,
  track_tx.lang_cd,
  track_tx.txt as track_name_tx
  from tlgnr_plant_track_bs track
  join tlgnr_plant_track_tx track_tx on (track.plant_track_cd = track_tx.plant_track_cd);

comment on table vlgnc_dispo_plant_track_list is 'track list for raildispo';
rem end ulgn01 vlgnc_dispo_plant_track_list;

---------------------------------------------------------------------------------
prompt vlgnc_dispo_max_wagon_pos

create or replace force view vlgnc_dispo_max_wagon_pos as
select
  wagon_no,
  current_plant_track_cd,
  current_track_position_no,
   cast(max(current_track_position_no) over (partition by current_plant_track_cd) as number(12))as max_track_position_no
from tlgnc_wagon;

comment on table vlgnc_dispo_max_wagon_pos is 'max existing track position for the track containing the wagon. used for position dropdown';
rem end ulgn01 vlgnc_dispo_max_wagon_pos;

---------------------------------------------------------------------------------
prompt vlgnc_dispo_day_loads

create or replace force view vlgnc_dispo_day_loads as
select distinct
load.load_id,
spa.plant_cd as assignment_plant_cd,
sp.plant_cd as wagon_plant_cd,
load.loading_ts,
load.net_quantity,
load.net_quantity_uom_cd,
load.container_id,
load.sap_material_id,
load.production_plant_cd
from
  tlgnc_load load
  left join (
          tlgnc_assignment_part ap
          join tlgnc_shippingpoint spa on (ap.shippingpoint_id = spa.shippingpoint_id))
      on (ap.load_id = load.load_id)
  left join (
          tlgnc_wagon wagon
          join tlgnc_shippingpoint sp on (wagon.last_known_railway_station_id = sp.railway_station_id))
      on (wagon.wagon_id = load.wagon_id);

comment on table vlgnc_dispo_day_loads is 'lists all loads joined with day and shippingpoint, either by assignment or by wagon. used for preloading';
rem end ulgn01 vlgnc_dispo_day_loads;

---------------------------------------------------------------------------------
prompt vlgnc_dispo_loadtarget

create or replace force view vlgnc_dispo_loadtarget as
select
target.shippingpoint_loadtarget_id,
target.shippingpoint_id,
target.sap_material_id,
target.production_plant_cd,
target.target_load_count,
target.load_uom_cd,
target.load_quantity,
target.preload_comment,
sp.plant_cd,
mat.txt as material_name,
mat.lang_cd,
uom.fuzzy_quantity_flag,
uomtx.txt as uom_name
from tlgnc_shippingpoint_loadtarget target
join tlgnc_shippingpoint sp on (target.shippingpoint_id = sp.shippingpoint_id)
join tlgns_sap_material_description mat on (target.sap_material_id = mat.sap_material_id)
join tlgnr_uom_bs uom on (uom.uom_cd = target.load_uom_cd)
join tlgnr_uom_tx uomtx on (uomtx.uom_cd = target.load_uom_cd and uomtx.lang_cd = mat.lang_cd);

comment on table vlgnc_dispo_loadtarget is 'loadtarget search view for dispo, including material name and plant_cd';
rem end ulgn01 vlgnc_dispo_loadtarget;

---------------------------------------------------------------------------------
prompt vlgnc_material_compat

create or replace force view vlgnc_material_compat as
select
  mcm.mat_compat_mat_id || '_' || frommatdesc.lang_cd as COMPOSITE_ID,
  mcm.mat_compat_mat_id, /* can be used as the ID, together with the lang_cd */
  frommatdesc.lang_cd,   /* is identical to tomatdesc.lang_cd, only needed once to filter in UI. */
  frommat.sap_material_no as from_sap_material_no,
  frommatdesc.txt         as from_sap_material_txt,
  tomat.sap_material_no   as to_sap_material_no,
  tomatdesc.txt           as to_sap_material_name,
  mc.insignificant_threshold_perc,
  mc.force_identical_threshold_perc,
  mc.compat_comment,
  mc.incompat_comment,
  mcm.mat_compat_mat_comment,
  /* mcm.from_mat_compat_id - same as mc.mat_compat_id, not interesting to have in a view */
  mc.mat_compat_id         /* just useful for debugging, dont show in UI. */,
  mc.from_sap_material_id  /* just useful for debugging, dont show in UI. */,
  mcm.to_sap_material_id   /* just useful for debugging, dont show in UI. */
from
  tlgnc_mat_compat mc
  inner join tlgns_sap_material frommat on (mc.from_sap_material_id = frommat.sap_material_id)
  left outer join tlgns_sap_material_description frommatdesc on (frommat.sap_material_id = frommatdesc.sap_material_id)
  inner join tlgnc_mat_compat_mat mcm on (mc.mat_compat_id = mcm.from_mat_compat_id)
  inner join tlgns_sap_material tomat on (mcm.to_sap_material_id = tomat.sap_material_id)
  left outer join tlgns_sap_material_description tomatdesc on (tomat.sap_material_id = tomatdesc.sap_material_id)
where frommatdesc.lang_cd = tomatdesc.lang_cd;

comment on table vlgnc_material_compat is 'Material compatibilitity table, also known as Product Change Matrix, for read-only display in UI (user verification).';
rem end ulgn01 vlgnc_material_compat;

---------------------------------------------------------------------------------
prompt vlgnc_wagon_detail_assignment

create or replace force view vlgnc_wagon_detail_assignment as
SELECT
  wagon.wagon_id || '_'  || ass.assignment_id || '_' ||  status.lang_cd as COMPOSITE_ID,
  load.load_id,
  wagon.wagon_id,
  ass.assignment_id,
  loadpart.assignment_part_id as load_assignment_part_id,
  drivetopart.shippingpoint_id as shippingpoint_id,
  drivetopart.assignment_part_id as driveto_assignment_part_id,
  drivetopart.planned_start as driveto_planned_start,
  drivetopart.real_start as driveto_real_start,
  unloadpart.assignment_part_id as unload_assignment_part_id,
  unloadpart.planned_start as unload_planned_start,
  unloadpart.real_start as unload_real_start,
  unloadpart.order_id,
  unloadpart.shipto_id,
  ord.ctl_mod_ts,
  ord.order_number,
  ord.order_status_cd,
  ord.distribution_dest_cd,
  ord.rail_hold_back_re_order_flag,
  ord.delivery_type_cd,
  status.txt as status_txt,
  status.lang_cd
FROM tlgnc_wagon wagon LEFT JOIN  tlgnc_load load ON(load.wagon_id = wagon.wagon_id
 AND load.unloading_ts IS NULL
 AND load.container_id IS NULL) LEFT JOIN tlgnc_assignment ass ON(ass.wagon_id = wagon.wagon_id
 AND ass.assign_status_cd not in (6,7))
  LEFT JOIN tlgnc_assignment_part loadpart ON(loadpart.assignment_id = ass.assignment_id and loadpart.assignment_part_type_cd = 1)
  LEFT JOIN tlgnc_assignment_part drivetopart ON(drivetopart.assignment_id = ass.assignment_id and drivetopart.assignment_part_type_cd = 2)
  LEFT JOIN tlgnc_assignment_part unloadpart ON(unloadpart.assignment_id = ass.assignment_id and unloadpart.assignment_part_type_cd = 3)
  LEFT JOIN tlgnc_order ord ON(ord.order_id = unloadpart.order_id)
  left outer join TLGNR_ORDER_STATUS_TX status on (status.ORDER_STATUS_CD=ord.ORDER_STATUS_CD)
  left join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD)
WHERE ass.active_flag = 1 and (archivingstatus.DISPLAY_FLAG is null or archivingstatus.DISPLAY_FLAG = 1);
comment on table vlgnc_wagon_detail_assignment is 'Used by wagon detail screen for retrieving the current assignment, assignment part load, and load of wagons.';

rem end ulgn01 vlgnc_wagon_detail_assignment;

-------------------------------------------------------------------------------
PROMPT  VLGNC_WAGON_DETAIL_LOAD_LIST
CREATE OR REPLACE FORCE VIEW VLGNC_WAGON_DETAIL_LOAD_LIST AS
SELECT
load.load_id || '_' || materialdesc.LANG_CD as COMPOSITE_ID,
load.LOAD_ID,
materialdesc.LANG_CD,
loadunit.TXT as LOAD_UNIT_TXT,
itemunit.TXT as ORDERED_UNIT_TXT,
material1.SAP_MATERIAL_NO as MATERIAL_NO,
material1.MATERIAL_PACKAGE_CD,
plantbs.PLANT_NAME,
materialdesc.txt as MATERIAL_TXT,
wagon.WAGON_ID,
container.CONTAINER_NAME,
container.TARE_WEIGHT_T as CONTAINER_TARE_WEIGHT_T,
container.MAX_NET_WEIGHT_T as CONTAINER_MAX_NET_WEIGHT_T,
load.PRODUCTION_PLANT_CD as LOAD_PRODUCTION_PLANT_CD,
load.TRUCK_ID as LOAD_TRUCK_ID,
load.LOADING_TS as LOADING_TS,
load.UNLOADING_TS as UNLOADING_TS,
load.NET_QUANTITY,
(load.NET_QUANTITY * oi.lr_uom_numerator / oi.lr_uom_denominator * baseuom2.NUMERATOR_FOR_CONVERSION / baseuom2.DENOMINATOR_FOR_CONVERSION) as LOAD_NET_MEASURE,
load.WS_ORIGINAL_TARE_MEASURE_T,
load.TARE_WEIGHT_T as TARE_QUANTITY,
((load.NET_QUANTITY * oi.lr_uom_numerator / oi.lr_uom_denominator * baseuom2.NUMERATOR_FOR_CONVERSION / baseuom2.DENOMINATOR_FOR_CONVERSION) + load.TARE_WEIGHT_T) as GROSS_WEIGHT_T, /* this new column is needed because of the ticket ADN58166 */
(load.WS_ORIGINAL_TARE_MEASURE_T - wagon.TARE_WEIGHT_T) as DELTA,
summeditem.ordered_measure,
CAST((decode(
  (SELECT MAX(order_item_no)
   FROM tlgnc_order_item selectitem
   WHERE selectitem.order_id = o.order_id),    10,    CAST((o.order_number) AS
   VARCHAR2(15 CHAR)),    CAST((o.order_number || ' (' || oi.order_item_no || ')') AS
   VARCHAR2(15 CHAR)))) AS
  VARCHAR2(15 CHAR)) ORDER_NUMBER_WITH_ITEM_NO,
oi.ORDERED_QUANTITY,
oi.order_id
from
TLGNC_LOAD load
inner join TLGNC_WAGON wagon on (wagon.WAGON_ID=load.WAGON_ID)
left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc on (materialdesc.SAP_MATERIAL_ID=load.SAP_MATERIAL_ID )
left outer join TLGNC_CONTAINER container on (container.CONTAINER_ID = load.CONTAINER_ID)
left outer join TLGNS_SAP_MATERIAL material1 on (material1.SAP_MATERIAL_ID=load.SAP_MATERIAL_ID)
left outer join TLGNR_UOM_TX loadunit on (loadunit.UOM_CD=load.net_quantity_uom_cd and loadunit.LANG_CD=materialdesc.LANG_CD)
left outer join TLGNR_UOM_BS loaduom on (loaduom.uom_cd = load.net_quantity_uom_cd)
left outer join (
  select
    item.load_id,
    ordered_uom_cd,
    sum(item.ORDERED_QUANTITY * item.ordered_uom_numerator / item.ordered_uom_denominator * baseuom1.NUMERATOR_FOR_CONVERSION / baseuom1.DENOMINATOR_FOR_CONVERSION) as ordered_measure
  from
    tlgnc_order_item item
    left outer join tlgns_sap_material material2 on (item.sap_material_id = material2.sap_material_id)
    left outer join tlgnr_uom_bs baseuom1 on material2.base_uom_cd = baseuom1.uom_cd
    group by load_id, ordered_uom_cd) summeditem on (summeditem.load_id = load.load_id)
left outer join tlgnc_order_item oi on oi.load_id = load.load_id
left outer join TLGNR_PLANT_BS plantbs on (plantbs.PLANT_CD = load.PRODUCTION_PLANT_CD) /* dont join via oi, but load, because there may be no order_item - ADN58166 */
left outer join tlgnc_order o on o.order_id = oi.order_id
left outer join TLGNR_UOM_TX itemunit on (itemunit.UOM_CD=summeditem.ORDERED_UOM_CD and itemunit.LANG_CD=materialdesc.LANG_CD)
left outer join tlgnr_uom_bs baseuom2 on material1.base_uom_cd = baseuom2.uom_cd
left outer join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (o.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD)
where load.loading_ts is not null and (archivingstatus.DISPLAY_FLAG is null or archivingstatus.DISPLAY_FLAG = 1);

COMMENT ON TABLE VLGNC_WAGON_DETAIL_LOAD_LIST is 'Mainly load information of wagons with language dependent desciptions';

REM END VLGNC_WAGON_DETAIL_LOAD_LIST

---------------------------------------------------------------------------------
PROMPT  VLGNC_RAIL_SPECIAL_ASSIGNMENT

create or replace force view vlgnc_rail_special_assignment as
select
ass.assignment_id,
ass.assignment_dat,
ass.wagon_id,
ord.order_id,
ord.shippingpoint_id,
ord.delivery_type_cd,
ord.distribution_dest_cd,
ord.delivery_from_dat,
ord.delivery_to_dat,
ord.order_status_cd
from
tlgnc_assignment ass
join tlgnc_assignment_part ap on (ap.assignment_id = ass.assignment_id)
join tlgnc_order ord on (ord.order_id = ap.order_id )
where
    ass.active_flag = 1 and
    ass.assign_status_cd not in (6, 7);

COMMENT ON TABLE VLGNC_RAIL_SPECIAL_ASSIGNMENT is 'Helps finding special (return, maintenance etc) rail assignments for a given wagon, at a given shippingpoint, on a given date ';

REM END VLGNC_RAIL_SPECIAL_ASSIGNMENT
---------------------------------------------------------------------------------

PROMPT  VLGNC_ORD_UNLOAD_SCHED_INFO

CREATE OR REPLACE FORCE VIEW VLGNC_ORD_UNLOAD_SCHED_INFO AS
      SELECT ord.order_id,
       ord.delivery_from_dat,
       ord.delivery_to_dat,
       ord.order_number,
       sel.assignment_id,
       sel.assign_status_cd,
       sel.assign_tr_status_cd,
       sel.truck_id,
       sel.last_known_real_start,
       sel.last_known_planned_start,
       sel.planned_start_load,
       sel.to_assign_part_status_cd,
       asspart.planned_start as planned_start_unload,
       dr.driver_id
       FROM
      tlgnc_order ord
      INNER JOIN tlgnc_assignment_part asspart on (asspart.order_id = ord.order_id and asspart.real_start is null) --unload part
      INNER JOIN
           (SELECT
         ass.assignment_id,
         ass.truck_id,
         ass.assign_status_cd,
         ass.assign_tr_status_cd,
          max(realstart_notnull.planned_start) AS
      last_known_planned_start,
          PART.planned_start as
      planned_start_load,
          max(realstart_notnull.real_start) AS
      last_known_real_start,
          topart.assign_part_status_cd
      AS
      to_assign_part_status_cd
       FROM tlgnc_assignment ass
       LEFT OUTER JOIN tlgnc_assignment_part PART ON(PART.assignment_id = ass.assignment_id and assignment_part_type_cd=1) -- load part
       LEFT OUTER JOIN tlgnc_assignment_part realstart_notnull on (realstart_notnull.assignment_id = ass.assignment_id and realstart_notnull.real_start IS NOT NULL) --first part the real start of which is known
       LEFT OUTER JOIN tlgnc_assignment_part topart on (topart.assignment_id = ass.assignment_id and topart.assignment_part_type_cd = 2) --to part
       WHERE ass.active_flag = 1 and ass.assign_status_cd <= 4 group by
         ass.assignment_id, ass.assign_status_cd, ass.assign_tr_status_cd,
         ass.truck_id, topart.assign_part_status_cd, PART.planned_start
       ) sel on (sel.assignment_id = asspart.assignment_id)
      LEFT OUTER JOIN TLGNC_DRIVER dr on dr.current_truck_id=sel.truck_id
       where (ord.delivery_type_cd=1 or ord.delivery_type_cd = 4)  order by order_id;
comment on table VLGNC_ORD_UNLOAD_SCHED_INFO is 'Special view, which shows some overall time data related to assignments and orders, but only for active, not having known unloading time info.';
REM END ULGN01 VLGNC_ORD_UNLOAD_SCHED_INFO

-------------------------------------------------------------------------------

PROMPT  VLGNC_ORD_LOAD_SCHED_INFO

CREATE OR REPLACE FORCE VIEW VLGNC_ORD_LOAD_SCHED_INFO AS
       SELECT ord.order_id,
       ass.assignment_id,
       ass.assign_status_cd,
       ass.assign_tr_status_cd,
       ass.truck_id,
       max(part.planned_start) as planned_start_load,
       dr.driver_id
       FROM
      tlgnc_order ord
      INNER JOIN tlgnc_order_item it on it.order_id=ord.order_id
      INNER JOIN tlgnc_assignment_part PART ON(part.load_id=it.load_id) -- load part
      INNER JOIN tlgnc_assignment ass on ass.assignment_id=part.assignment_id
      LEFT OUTER JOIN TLGNC_DRIVER dr on dr.current_truck_id=ass.truck_id
             WHERE ass.active_flag = 1 and ass.assign_status_cd <= 4
             and ord.delivery_type_cd=1 or ord.delivery_type_cd = 4
             group by ord.order_id,
         ass.assignment_id, ass.assign_status_cd, ass.assign_tr_status_cd,
         ass.truck_id, PART.planned_start, dr.driver_id;

comment on table  VLGNC_ORD_LOAD_SCHED_INFO  is 'Special view, which shows some overall time data related to assignments and orders, but only for active, not having known loading time info.';
REM END ULGN01  VLGNC_ORD_LOAD_SCHED_INFO

---------------------------------------------------------------------------------

PROMPT VLGNC_PICKUP_TERMINAL

CREATE OR REPLACE FORCE VIEW VLGNC_PICKUP_TERMINAL AS
SELECT
  ord.order_id ||'_' || order_item_id as composite_id,
  ord.ORDER_ID,
  ord.CUSTORDER_ID,
  item.ORDER_ITEM_NO,
  item.ORDER_ITEM_ID,
  ord.SHIPTO_SAP_BP_ID,
  ord.SOLDTO_SAP_BP_ID,
  ord.SAREA_ID,
  item.SAP_MATERIAL_ID,
  item.CURRENCY_CD,
  item.RATE,
  ord.ORDER_NUMBER,
  ord.ORDER_STATUS_CD,
  item.ORDERED_UOM_CD,
  item.ORDERED_QUANTITY,
  ord.DELIVERY_FROM_DAT,
  ord.DELIVERY_TO_DAT,
  ord.DELIVERY_TYPE_CD,
  ord.SHIPPINGPOINT_ID,
  item.LOADINGPOINT_ID,
  item.PRODUCTION_PLANT_CD,
  ord.ACTUAL_DELIVERY_DAT,
  ord.BILLTO_SAP_BP_ID,
  status.LANG_CD,
  sbp.NAME1 as BP_NAME,
  sbp1.NAME1 as SOLDTO_BP_NAME,
  sbp2.NAME1 as SHIPTO_BP_NAME,
  itemunit.TXT  as ORDER_UNIT_TXT,
  baseuom.UOM_DIMENSION_CD as ORDER_UOM_DIMENSION_CD,
  baseunit.TXT as ORDER_UOM_DIMENSION_TXT,
  material.SAP_MATERIAL_NO as MATERIAL_NO,
  material.MATERIAL_PACKAGE_CD,
  materialdesc.txt as MATERIAL_TXT,
  status.TXT as STATUS_TXT,
  ord.ORDER_VALIDATION_CODE,
  (item.ORDERED_QUANTITY * item.ORDERED_UOM_NUMERATOR / item.ORDERED_UOM_DENOMINATOR * baseuom.NUMERATOR_FOR_CONVERSION / baseuom.DENOMINATOR_FOR_CONVERSION) as ORDERED_MEASURE
from
  TLGNC_ORDER ord
  inner join TLGNC_ORDER_ITEM item
    on (ord.order_id=item.order_id)
  left outer join TLGNR_ORDER_STATUS_TX status
    on (status.ORDER_STATUS_CD=ord.ORDER_STATUS_CD)
  left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc
    on (materialdesc.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID and status.LANG_CD=materialdesc.LANG_CD )
  left outer join TLGNS_SAP_BP sbp
    on (ord.BILLTO_SAP_BP_ID=sbp.SAP_BP_ID)
  left outer join TLGNS_SAP_BP sbp1
    on (ord.SOLDTO_SAP_BP_ID=sbp1.SAP_BP_ID)
  left outer join TLGNS_SAP_BP sbp2
    on (ord.SHIPTO_SAP_BP_ID=sbp2.SAP_BP_ID)
  left outer join TLGNR_LANG_BS lang
    on (status.LANG_CD=lang.LANG_CD)
  left outer join TLGNS_SAP_MATERIAL material
    on (material.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID)
  left outer join TLGNR_UOM_BS baseuom
    on material.base_uom_cd = baseuom.uom_cd
  left outer join TLGNR_UOM_TX baseunit
    on (baseunit.UOM_CD = baseuom.UOM_CD and baseunit.LANG_CD = lang.LANG_CD)
  left outer join TLGNR_UOM_TX itemunit
    on (itemunit.UOM_CD=item.ORDERED_UOM_CD and itemunit.LANG_CD=lang.LANG_CD)
  left outer join TLGNR_MATERIAL_PACKAGE_BS pkg
    on (material.MATERIAL_PACKAGE_CD = pkg.MATERIAL_PACKAGE_CD)
where delivery_type_CD in (5,9) and
  (
    (delivery_from_dat between -- delivery from within current day
      to_date(to_char(sysdate,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
        and
      to_date(to_char(sysdate+1,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
    )
    or
    (delivery_to_dat between -- delivery to within current day
      to_date(to_char(sysdate,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
        and
      to_date(to_char(sysdate+1,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
    )
    or (delivery_from_dat < -- delivery interval contains current day
      to_date(to_char(sysdate-1,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
        and
      delivery_to_dat >
      to_date(to_char(sysdate,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
    )
    or (ord.actual_delivery_dat is null and ord.ORDER_STATUS_CD <> 17 and ord.ORDER_STATUS_CD <> 20)
  );

COMMENT ON TABLE VLGNC_PICKUP_TERMINAL is 'View of pickup terminal used by customers going for pickup orders.';
REM END VLGNC_PICKUP_TERMINAL

--------------------------------------------------------------------------------------------------------------------------------------------------------------

PROMPT VLGNC_FREE_TRUCKS

CREATE OR REPLACE FORCE VIEW VLGNC_FREE_TRUCKS as
SELECT
  TRUCK_ID,
  CARRIER_ID,
  TRUCK_NAME,
  TRUCK_PLATE_NO,
  t.ACTIVE_FLAG
from TLGNC_TRUCK t
left outer join TLGNC_DRIVER d on d.CURRENT_TRUCK_ID=t.TRUCK_ID
where d.CURRENT_TRUCK_ID is null or CURRENT_TRUCK_ID not in
(
SELECT driver.current_truck_id
FROM
  (SELECT *
  FROM tlgnc_driver d
  WHERE EXISTS
    (SELECT *
    FROM tlgnc_pda_session ps
    WHERE ps.pda_session_status_cd IN(1,2)
    AND ps.user_id                  = d.user_id
    )
  ) driver
INNER JOIN tlgnu_user usr ON (driver.user_id = usr.user_id)
INNER JOIN tlgns_sap_vendor v ON (usr.sap_vendor_id = v.sap_vendor_id)
INNER JOIN tlgnc_carrier c ON (c.sap_vendor_id = v.sap_vendor_id)
AND driver.active_flag=1);

COMMENT ON TABLE VLGNC_FREE_TRUCKS is 'List of trucks which do not have a driver, and have a driver, but he does not have active pda session.';

REM END VLGNC_FREE_TRUCKS

-------------------------------------------------------------------------------
PROMPT VLGNC_BAG_TERMINAL

CREATE OR REPLACE FORCE VIEW VLGNC_BAG_TERMINAL AS
SELECT
  ord.order_id ||'_' || order_item_id as composite_id,
  ord.ORDER_ID,
  ord.CUSTORDER_ID,
  item.ORDER_ITEM_NO,
  ass.assignment_id,
  ass.assign_status_cd,
  item.ORDER_ITEM_ID,
  ord.SHIPTO_SAP_BP_ID,
  ord.SOLDTO_SAP_BP_ID,
  ord.SAREA_ID,
  item.SAP_MATERIAL_ID,
  item.CURRENCY_CD,
  item.RATE,
  ord.ORDER_NUMBER,
  ord.ORDER_STATUS_CD,
  item.ORDERED_UOM_CD,
  item.ORDERED_QUANTITY,
  ord.DELIVERY_FROM_DAT,
  ord.DELIVERY_TO_DAT,
  ord.DELIVERY_TYPE_CD,
  ord.SHIPPINGPOINT_ID,
  item.LOADINGPOINT_ID,
  item.PRODUCTION_PLANT_CD,
  ord.POOL_ID,
  ord.COMM_CARRIER_ID,
  ord.ACTUAL_DELIVERY_DAT,
  ord.BILLTO_SAP_BP_ID,
  ord.delivered_driver_id,
  status.LANG_CD,
  sbp.NAME1 as BP_NAME,
  sbp1.NAME1 as SOLDTO_BP_NAME,
  sbp2.NAME1 as SHIPTO_BP_NAME,
  itemunit.TXT  as ORDER_UNIT_TXT,
  baseuom.UOM_DIMENSION_CD as ORDER_UOM_DIMENSION_CD,
  baseunit.TXT as ORDER_UOM_DIMENSION_TXT,
  material.SAP_MATERIAL_NO as MATERIAL_NO,
  material.MATERIAL_PACKAGE_CD,
  materialdesc.txt as MATERIAL_TXT,
  status.TXT as STATUS_TXT,
  truck.TRUCK_ID,
  truck.TRUCK_PLATE_NO,
  truck.TRUCK_NAME,
  part.PLANNED_START as LOADING_PLANNED_START,
  (select MIN(planned_start) from TLGNC_ASSIGNMENT_PART where ASSIGNMENT_ID=ass.ASSIGNMENT_ID and ASSIGNMENT_PART_TYPE_CD=1 ) as LOADING_MIN_PLANNED_START,
  part.REAL_START as LOADING_REAL_START,
  (item.ORDERED_QUANTITY * item.ORDERED_UOM_NUMERATOR / item.ORDERED_UOM_DENOMINATOR * baseuom.NUMERATOR_FOR_CONVERSION / baseuom.DENOMINATOR_FOR_CONVERSION) as ORDERED_MEASURE,
  nvl(
  (select (pers.FIRSTNAME || ' ' || pers.NAME) as DRIVER_NAME from TLGNC_DRIVER driver inner join TLGNU_USER us on (us.USER_ID = driver.USER_ID) inner join TLGNC_PERSON pers on (us.PERSON_ID = pers.PERSON_ID) where driver.DRIVER_ID=ord.delivered_driver_id  and rownum <= 1),
  (select (pers.FIRSTNAME || ' ' || pers.NAME) as DRIVER_NAME from TLGNC_DRIVER driver inner join TLGNU_USER us on (us.USER_ID = driver.USER_ID) inner join TLGNC_PERSON pers on (us.PERSON_ID = pers.PERSON_ID) where driver.CURRENT_TRUCK_ID=ass.TRUCK_ID  and rownum <= 1)) as DRIVER_NAME,
  ord.ORDER_VALIDATION_CODE
from
  TLGNC_ORDER ord
  inner join TLGNC_ORDER_ITEM item
    on (ord.order_id=item.order_id)
  left outer join TLGNC_ASSIGNMENT_PART part
    on (part.load_id = item.load_id)
  left outer join TLGNC_ASSIGNMENT ass
    on (ass.ASSIGNMENT_ID=part.ASSIGNMENT_ID and ass.ACTIVE_FLAG = 1)
  left outer join TLGNC_TRUCK truck
    on (truck.TRUCK_ID=ass.TRUCK_ID)
  left outer join TLGNR_ORDER_STATUS_TX status
    on (status.ORDER_STATUS_CD=ord.ORDER_STATUS_CD)
  left outer join TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc
    on (materialdesc.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID and status.LANG_CD=materialdesc.LANG_CD )
  left outer join TLGNS_SAP_BP sbp
    on (ord.BILLTO_SAP_BP_ID=sbp.SAP_BP_ID)
  left outer join TLGNS_SAP_BP sbp1
    on (ord.SOLDTO_SAP_BP_ID=sbp1.SAP_BP_ID)
  left outer join TLGNS_SAP_BP sbp2
    on (ord.SHIPTO_SAP_BP_ID=sbp2.SAP_BP_ID)
  left outer join TLGNR_LANG_BS lang
    on (status.LANG_CD=lang.LANG_CD)
  left outer join TLGNS_SAP_MATERIAL material
    on (material.SAP_MATERIAL_ID=item.SAP_MATERIAL_ID)
  left outer join TLGNR_UOM_BS baseuom
    on material.base_uom_cd = baseuom.uom_cd
  left outer join TLGNR_UOM_TX baseunit
    on (baseunit.UOM_CD = baseuom.UOM_CD and baseunit.LANG_CD = lang.LANG_CD)
  left outer join TLGNR_UOM_TX itemunit
    on (itemunit.UOM_CD=item.ORDERED_UOM_CD and itemunit.LANG_CD=lang.LANG_CD)
  left outer join TLGNR_MATERIAL_PACKAGE_BS pkg
    on (material.MATERIAL_PACKAGE_CD = pkg.MATERIAL_PACKAGE_CD)
where
  (
    (delivery_from_dat between -- delivery from within current day
      to_date(to_char(sysdate,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
        and
      to_date(to_char(sysdate+1,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
    )
    or
    (delivery_to_dat between -- delivery to within current day
      to_date(to_char(sysdate,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
        and
      to_date(to_char(sysdate+1,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
    )
    or -- LOADING_MIN_PLANNED_START within current day
    ((select MIN(planned_start) from TLGNC_ASSIGNMENT_PART where ASSIGNMENT_ID=ass.ASSIGNMENT_ID and ASSIGNMENT_PART_TYPE_CD=1 ) between
      to_date(to_char(sysdate,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
        and
      to_date(to_char(sysdate+1,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
    )
    or (delivery_from_dat < -- delivery interval contains current day
      to_date(to_char(sysdate-1,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
        and
      delivery_to_dat >
      to_date(to_char(sysdate,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
    )
    or (part.PLANNED_START is not null and -- LOADING_PLANNED_START exists within current day
      part.PLANNED_START between
      to_date(to_char(sysdate,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
        and
      to_date(to_char(sysdate+1,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss')
    )
    or ord.actual_delivery_dat is null
  )
  and delivery_type_CD in (1,9) and
    (ord.ORDER_STATUS_CD between 7 and 16
    or ord.ORDER_STATUS_CD = 19);

COMMENT ON TABLE VLGNC_BAG_TERMINAL is 'View of bag terminal: for bag road direct orders';

REM END VLGNC_BAG_TERMINAL

-------------------------------------------------------------------------------
PROMPT VLGNC_SHIPP_BIMODAL_ROAD_RAIL

CREATE OR REPLACE FORCE VIEW VLGNC_SHIPP_BIMODAL_ROAD_RAIL AS
SELECT
item.order_item_id,
bimodalitem.order_item_id as bimodal_rail_item_id,
item.order_id,
ord.order_id as bimodal_rail_order_id,
ord.order_status_cd as bimodal_rail_order_status_cd,
assi.wagon_id,
ord.actual_delivery_dat,
w.wagon_no,
lo.net_quantity as wagon_load_net_quantity,
lo.net_quantity_uom_cd as wagon_load_uom_cd,
sumloads.already_loaded_quantity
from
tlgnc_order_item item
inner join tlgnc_order_item bimodalitem on bimodalitem.order_item_id=item.bimodal_rail_order_item_id
inner join tlgnc_order ord on ord.order_id=bimodalitem.order_id
inner join tlgnc_assignment_part part on part.order_id= bimodalitem.order_id
inner join tlgnc_assignment assi on assi.assignment_id=part.assignment_id
inner join tlgnc_wagon w on w.wagon_id=assi.wagon_id
left outer join tlgnc_load lo on lo.load_id = bimodalitem.load_id
left outer join
(select l.loaded_from_load_id, sum(net_quantity) as already_loaded_quantity from tlgnc_load l group by l.loaded_from_load_id ) sumloads on sumloads.loaded_from_load_id=lo.load_id
where item.bimodal_rail_order_item_id is not null;

COMMENT ON TABLE VLGNC_SHIPP_BIMODAL_ROAD_RAIL is 'View of order items and related bimodal rail orders.';

REM END VLGNC_SHIPP_BIMODAL_ROAD_RAIL
-------------------------------------------------------------------------------

prompt vlgnc_rail_chart

create or replace force view vlgnc_rail_chart as
  select distinct
  wagon.wagon_id,
  wagon.wagon_no,
  wagon.active_flag,
  wagon.wagon_keeper_id,
  wagon.wagon_type_cd,
  wagontrsp.WAGON_TRSP_CAT_CD,
  wagon.last_known_railway_station_id,
  wagon.arrival_last_known_station_dat,
  rw.railway_station_no,
  rw.railway_station_name,
  cast(load.load_id as number(12)) as load_id,
  load.production_plant_cd,
  load.loading_ts,
  nvl(incoming.real_start, incoming.planned_start) as planned_start,
  incoming.railway_station_id return_railway_station_id,
  incoming.railway_station_no as arr_railway_station_no,
  incoming.railway_station_name as arr_railway_station_name,
  incoming.assignment_id as incoming_assignment_id,
  incoming.assign_status_cd as incoming_assign_status_cd,
  incoming.assign_tr_status_cd as incoming_assign_tr_status_cd,
  cast(last_load.load_id as number(12)) as last_load_id,
  last_load.production_plant_cd as last_production_plant_cd,
  nvl(p2.real_start, p2.planned_start)  as outgoing_planned_start,
  a2.assign_status_cd as outgoing_assign_status_cd
  from tlgnc_wagon wagon
left join tlgnr_wagon_type_bs wagontype on (wagontype.wagon_type_cd = wagon.wagon_type_cd)
left join TLGNR_WAGON_TRSP_CAT_BS wagontrsp on (wagontrsp.WAGON_TRSP_CAT_CD = wagontype.WAGON_TRSP_CAT_CD)
left join (select distinct wagon_id,
        first_value(load_id) over (partition by wagon_id order by load_id ) as load_id,
        first_value(net_quantity_uom_cd) over (partition by wagon_id order by load_id) as net_quantity_uom_cd,
        first_value(load_order_id) over (partition by wagon_id order by load_id ) as load_order_id,
        first_value(production_plant_cd) over (partition by wagon_id order by load_id) as production_plant_cd,
        first_value(loading_ts) over (partition by wagon_id order by load_id) as loading_ts
    from tlgnc_load
    where container_id IS NULL
    and wagon_id is not null
    and unloading_ts IS NULL) load on (load.wagon_id = wagon.wagon_id)
left join (
  tlgnc_assignment a1
  join tlgnc_assignment_part p1 on (p1.assignment_id = a1.assignment_id and p1.assignment_part_type_cd = 3 )
  ) on (a1.wagon_id = wagon.wagon_id and  p1.real_start >  wagon.arrival_last_known_station_dat and a1.assign_status_cd not in (6,7))
left join (
  select distinct
        a2.wagon_id,
        a2.assignment_id,
        a2.assign_status_cd,
        a2.assign_tr_status_cd,
        first_value(p2.planned_start) over (partition by a2.wagon_id order by p2.planned_start) as planned_start,
        first_value(p2.real_start) over (partition by a2.wagon_id order by p2.real_start) as real_start,
        bp2.railway_station_id,
        rw2.railway_station_no,
        rw2.railway_station_name
  from  tlgnc_assignment a2
  join tlgnc_assignment_part p2 on (p2.assignment_id = a2.assignment_id and p2.assignment_part_type_cd = 5)
  join tlgnc_shipto st2 on (p2.shipto_id = st2.shipto_id)
  join tlgns_sap_bp bp2 on (st2.shipto_sap_bp_id = bp2.sap_bp_id)
  join tlgnc_railway_station rw2 on (rw2.railway_station_id = bp2.railway_station_id)
  where a2.assign_status_cd not in (6,7)
  ) incoming on (incoming.wagon_id = wagon.wagon_id and incoming.planned_start >  wagon.arrival_last_known_station_dat and incoming.railway_station_id != wagon.last_known_railway_station_id)
left join (select distinct l.wagon_id,
        first_value(l.load_id) over (partition by l.wagon_id order by l.unloading_ts desc) as load_id,
        first_value(l.production_plant_cd) over (partition by l.wagon_id order by l.unloading_ts desc) as production_plant_cd,
        first_value(l.unloading_ts) over (partition by l.wagon_id order by l.unloading_ts desc) as unloading_ts
    from tlgnc_load l join tlgns_sap_material m on (m.sap_material_id = l.sap_material_id)
    where l.wagon_id is not null
    and l.container_id IS NULL
    and l.unloading_ts is not null) last_load on (last_load.wagon_id = wagon.wagon_id)
left join tlgnc_railway_station rw on (rw.railway_station_id = wagon.last_known_railway_station_id)
left join (
  tlgnc_assignment a2
  join tlgnc_assignment_part p2 on (p2.assignment_id = a2.assignment_id
and p2.assignment_part_type_cd = 2 )
join tlgnc_shippingpoint sp on (sp.shippingpoint_id=p2.shippingpoint_id)
  ) on (a2.wagon_id = wagon.wagon_id  and a2.assign_status_cd not in (6,7) and sp.railway_station_id=wagon.last_known_railway_station_id and p2.planned_start >  wagon.arrival_last_known_station_dat)
where a1.assignment_id IS NULL;

comment on table vlgnc_rail_chart is 'View for rail chart on call center (bestellung). Currently used only in native query.';
rem end ulgn01 vlgnc_rail_chart;

-------------------------------------------------------------------------------
PROMPT  VLGNC_REQUIRE_FREEZING_LIST

create or replace force view VLGNC_REQUIRE_FREEZING_LIST as
select distinct
  oif.oi_order_id as order_id
from tlgns_sap_netprice_order_item snoi
join tlgnf_order_item_final oif on
  oif.oi_order_item_id = snoi.order_item_id and
  oif.ctl_mod_ts < snoi.ctl_mod_ts;

COMMENT ON TABLE VLGNC_REQUIRE_FREEZING_LIST is 'Orders that require to be re-frozen after being updated by SAP netprice import (a first-time freeze is not triggered this way)';

REM END VLGNC_REQUIRE_FREEZING_LIST

-------------------------------------------------------------------------------

PROMPT  VLGNC_RAILCAR_OPT_LOADED_CARS

create or replace force view VLGNC_RAILCAR_OPT_LOADED_CARS as
  select
  wagon.wagon_id,
  wagon.wagon_no,
  wagon.wagon_type_cd,
  wagon.active_flag,
  wagon.last_known_railway_station_id,
  wagon.arrival_last_known_station_dat,
  wagontype.wagon_trsp_cat_cd,
  wagontype.wagon_type_name,
  wagontypetx.lang_cd,
  wagontypetx.txt as wagon_type_name_tx,
  cast(load.load_id as number(12)) as load_id,
  cast(load.sap_material_id as number(12)) as sap_material_id,
  load.net_quantity,
  load.net_quantity_uom_cd,
  cast(load.load_order_id as number(12)) as load_order_id,
  load.loading_ts,
  matdesc.txt as material_name,
  mat_pack.logon_package_type_cd,
  ord.order_number,
  ord.order_status_cd,
  sbp1.NAME1 as SOLDTO_BP_NAME,
  sbp2.NAME1 as SHIPTO_BP_NAME,
  sattustext.TXT as order_status_txt
  from tlgnc_wagon wagon
join tlgnr_wagon_type_bs wagontype on (wagon.wagon_type_cd = wagontype.wagon_type_cd)
join tlgnr_wagon_type_tx wagontypetx on (wagon.wagon_type_cd = wagontypetx.wagon_type_cd)
inner join (select distinct wagon_id,
        first_value(load_id) over (partition by wagon_id order by load_id ) as load_id,
        first_value(sap_material_id) over (partition by wagon_id order by load_id) as sap_material_id,
        first_value(net_quantity) over (partition by wagon_id order by load_id) as net_quantity,
        first_value(net_quantity_uom_cd) over (partition by wagon_id order by load_id) as net_quantity_uom_cd,
        first_value(load_order_id) over (partition by wagon_id order by load_id ) as load_order_id,
        first_value(loading_ts) over (partition by wagon_id order by load_id) as loading_ts
    from tlgnc_load
    where wagon_id is not null
    and unloading_ts IS NULL) load on (load.wagon_id = wagon.wagon_id)
left join tlgnc_order_item item on (item.load_id= load.load_id)
left join tlgnc_order ord on (ord.order_id=item.order_id)
left outer join TLGNS_SAP_BP sbp1 on (ord.SOLDTO_SAP_BP_ID=sbp1.SAP_BP_ID)
left outer join TLGNS_SAP_BP sbp2 on (ord.SHIPTO_SAP_BP_ID=sbp2.SAP_BP_ID)
left join tlgns_sap_material mat ON (load.sap_material_id = mat.sap_material_id)
left join tlgnr_material_package_bs mat_pack ON (mat.material_package_cd = mat_pack.material_package_cd)
left join tlgns_sap_material_description matdesc on (load.sap_material_id = matdesc.sap_material_id and wagontypetx.lang_cd = matdesc.lang_cd)
left join tlgnr_order_status_tx sattustext on (sattustext.order_status_cd=ord.order_status_cd and sattustext.lang_cd=matdesc.lang_cd)
left join (
  tlgnc_assignment a1
  join tlgnc_assignment_part p1 on (p1.assignment_id = a1.assignment_id and p1.assignment_part_type_cd = 3 )
  ) on (a1.wagon_id = wagon.wagon_id and  p1.real_start >  wagon.arrival_last_known_station_dat and a1.assign_status_cd not in (6,7))
where a1.assignment_id IS NULL and (ord.order_status_cd in (6,10,11,18));

COMMENT ON TABLE VLGNC_RAILCAR_OPT_LOADED_CARS is 'View which contains wagons and information about the order assigned to them. Only wagons which are above and including dispatched but below and including transmitted to the rw company.';

REM END VLGNC_RAILCAR_OPT_LOADED_CARS
-------------------------------------------------------------------------------
PROMPT  VLGNC_RAILCAR_OPT_OPEN_ORDERS

create or replace force view VLGNC_RAILCAR_OPT_OPEN_ORDERS as
select
ord.order_id  || '_' || nvl(to_char(tt.rail_timetable_id), '_') || '_' || sattustext.lang_cd as composite_id,
ord.order_id,
ord.order_number,
ord.delivery_from_dat,
ord.delivery_to_dat,
ord.order_status_cd,
ord.shippingpoint_id,
ord.delivery_type_cd,
ord.shipto_sap_bp_id,
shippt.shippingpoint_name,
sbp.name1 as shipto_name,
sattustext.lang_cd,
sattustext.txt as status_txt,
st.shipto_id,
tt.rail_timetable_id,
tt.arr_railway_station_id,
tt.dep_railway_station_id,
tt.arr_switching_time_min,
tt.dep_switching_time_min,
tt.arr_offset_days,
rw_arr.railway_station_no AS arr_railway_station_no,
rw_arr.railway_station_name AS arr_railway_station_name,
ord.delivery_from_dat - nvl(tt.arr_offset_days, 0) as sort_from_dat,
ord.delivery_to_dat - nvl(tt.arr_offset_days, 0) as sort_to_dat,
it.ordered_quantity,
it.ordered_uom_cd,
descr.txt as material_description,
--in case delivery to is on a weekend it must be considered on friday
decode(decode (to_char(delivery_to_dat - nvl(tt.arr_offset_days, 0),'D'),7,'saturday',1,'sunday','normal'),'saturday', ord.delivery_to_dat - nvl(tt.arr_offset_days, 0)-1, 'sunday', ord.delivery_to_dat - nvl(tt.arr_offset_days, 0)-2,ord.delivery_to_dat - nvl(tt.arr_offset_days, 0)) as last_departure,
shiptoprio.min_evaluation_priority,
shiptoprio.count_wagon_types
from tlgnc_order ord
join tlgnc_order_item it on it.order_id=ord.order_id
join tlgnc_shippingpoint shippt on (ord.shippingpoint_id = shippt.shippingpoint_id)
join tlgnc_shipto st on (ord.shipto_sap_bp_id = st.shipto_sap_bp_id and st.active_flag = 1)
join tlgns_sap_bp sbp on (st.shipto_sap_bp_id = sbp.sap_bp_id)
join tlgns_sap_bp cbp on (ord.soldto_sap_bp_id = cbp.sap_bp_id)
left join tlgnr_order_status_tx sattustext on (sattustext.order_status_cd=ord.order_status_cd)
join tlgns_sap_material_description descr on (descr.sap_material_id=it.sap_material_id and descr.lang_cd=sattustext.lang_cd)
left join (tlgnc_rail_timetable tt inner join
(select distinct first_value(rail_timetable_id) over (partition by dep_railway_station_id, arr_railway_station_id  order by (60*arr_offset_days+(60*24-dep_switching_time_min)) ) as rail_timetable_id  from tlgnc_rail_timetable ) latest_dep_tt  on (tt.rail_timetable_id=latest_dep_tt.rail_timetable_id)
)on (tt.arr_railway_station_id = sbp.railway_station_id and tt.dep_railway_station_id = shippt.railway_station_id)
left join tlgnc_railway_station rw_arr on (rw_arr.railway_station_id = sbp.railway_station_id)
left join (select distinct  shipto_id, min(evaluation_priority) over (partition by shipto_id) as min_evaluation_priority,count(wagon_type_cd) over (partition by shipto_id) as count_wagon_types from tlgnc_shipto_wagon_type) shiptoprio  on shiptoprio.shipto_id=st.shipto_id;

comment on table VLGNC_RAILCAR_OPT_OPEN_ORDERS is 'Contains open orders for rail car optimization screen.';

rem end VLGNC_RAILCAR_OPT_OPEN_ORDERS
-------------------------------------------------------------------------------
PROMPT  VLGNC_SHIPTO_WAGON_PRIO_FACTOR

create or replace force view VLGNC_SHIPTO_WAGON_PRIO_FACTOR as
select
wtype.shipto_id  || '_' || wagon_type_cd as composite_id,
wtype.shipto_id,
wagon_type_cd,
cast (1/count_allowed_wagon_types  as NUMBER(12,4)) as factor
from tlgnc_shipto_wagon_type wtype
left join (
  select distinct shipto_id, count(wagon_type_cd) over (partition by shipto_id) as count_allowed_wagon_types from tlgnc_shipto_wagon_type
  ) total
on total.shipto_id = wtype.shipto_id;

comment on table VLGNC_SHIPTO_WAGON_PRIO_FACTOR is 'Used by railcar optimization screen upper table. Contains a number for each wagon type and shipto which depends on the number of usable wagon types for the shipto. ';

rem end VLGNC_SHIPTO_WAGON_PRIO_FACTOR

-------------------------------------------------------------------------------
PROMPT  VLGNC_DISPATCHING_CARRIER

create or replace force view VLGNC_DISPATCHING_CARRIER as
  SELECT DISTINCT
  tr.carrier_id,
    tr.dispatching_carrier_id,
    c.sap_vendor_id AS sap_vendor_id
FROM tlgnc_truck tr
INNER JOIN tlgnc_pool_carrier pc ON pc.carrier_id = tr.carrier_id
INNER JOIN tlgnc_carrier c ON c.carrier_id = tr.dispatching_carrier_id
INNER JOIN tlgnc_pool_carrier pc2 ON pc2.carrier_id = c.carrier_id
 AND pc2.pool_id = pc.pool_id
WHERE tr.dispatching_carrier_id IS NOT NULL
 AND tr.active_flag = 1;

comment on table VLGNC_DISPATCHING_CARRIER is 'Contains all carriers for which there is a truck which is dispatched by another carrier, and for which there is a common pool at least.';

rem end VLGNC_DISPATCHING_CARRIER

-------------------------------------------------------------------------------
PROMPT  VLGNC_DAMAGECODE_SEARCH

create or replace force view VLGNC_DAMAGECODE_SEARCH as
select
  tx.DAMAGECODE_TXT,
  tx.TXT,
  bs.DAMAGECODE_CD,
  tx.LANG_CD
FROM TLGNR_DAMAGECODE_BS bs
INNER JOIN TLGNR_DAMAGECODE_TX tx on tx.DAMAGECODE_CD=bs.DAMAGECODE_CD where bs.ctl_act_cd=1;

comment on table VLGNC_DAMAGECODE_SEARCH is 'Used on order detail repair tab to search for damage codes.';

rem end VLGNC_DAMAGECODE_SEARCH

-------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW VLGNC_MATERIAL_ENHANCEMENTS AS
SELECT
    matPlant.MAT_PLANT_ID,
    plant.PLANT_NAME AS PLANT_NAME,
    plant.PLANT_CD AS PLANT_CD,
    sapMaterialDescription.TXT AS MATERIAL_DESC,
    sapMaterialDescription.LANG_CD,
    sapMaterial.SAP_MATERIAL_ID,
    sapMaterial.SAP_MATERIAL_NO AS SAP_MATERIAL_NO,
    sapMaterial.UPDATE_BLOCKED_BY_HJID,
    matPlant.BRAND_SALES_NAME AS BRAND_SALES_NAME,
    matPlant.NORM_DESC AS NORM_DESC,
    matPlant.CPD_NUMBER AS CPD_NUMBER,
    matPlant.LOGO_1_Z1 AS LOGO_1_Z1,
    matPlant.LOGO_1_Z2 AS LOGO_1_Z2,
    matPlant.NATIONAL_NORM AS LOGO_1_Z3,
    matPlant.AUTOADD_1_PER_N_OF_UOM,
    matPlant.AUTOADD_N_UOM_CD,
    autoaddMaterialDescription.TXT AS AUTOADD_MATERIAL_DESC
  FROM
    TLGNR_PLANT_BS plant
    JOIN TLGNC_MAT_PLANT matPlant ON (plant.PLANT_CD = matPlant.PLANT_CD)
    JOIN TLGNS_SAP_MATERIAL sapMaterial ON (matPlant.SAP_MATERIAL_ID = sapMaterial.SAP_MATERIAL_ID)
    LEFT OUTER JOIN TLGNS_SAP_MATERIAL_DESCRIPTION sapMaterialDescription ON (sapMaterial.SAP_MATERIAL_ID = sapMaterialDescription.SAP_MATERIAL_ID)
    LEFT OUTER JOIN TLGNS_SAP_MATERIAL autoaddMaterial ON (matPlant.AUTOADD_SAP_MATERIAL_ID = autoaddMaterial.SAP_MATERIAL_ID)
    LEFT OUTER JOIN TLGNS_SAP_MATERIAL_DESCRIPTION autoaddMaterialDescription ON (autoaddMaterial.SAP_MATERIAL_ID = autoaddMaterialDescription.SAP_MATERIAL_ID and sapMaterialDescription.LANG_CD = autoaddMaterialDescription.LANG_CD);

COMMENT ON TABLE VLGNC_MATERIAL_ENHANCEMENTS IS 'Used to display material enhancements search results on the admin screen';

REM END VLGNC_MATERIAL_ENHANCEMENTS

-------------------------------------------------------------------------------

PROMPT VLGNC_SHIPTO_SOLDTO_LNF_DATA

CREATE OR REPLACE FORCE VIEW VLGNC_SHIPTO_SOLDTO_LNF_DATA AS
SELECT
  ORDER_ID,
  ACTUAL_DELIVERY_DAT,
  SHIPTO_SAP_BP_ID,
  SOLDTO_SAP_BP_ID,
  LNF_COORD_LAT,
  LNF_COORD_LON,
  LNF_SITE_NAME,
  LNF_SITE_STREET_HOUSE_NO,
  LNF_SITE_COUNTRY,
  LNF_SITE_POSTAL_CODE,
  LNF_SITE_CITY
from
  (
  select distinct
    first_value(o.ORDER_ID) over (partition by o.SHIPTO_SAP_BP_ID, o.SOLDTO_SAP_BP_ID, LNF_COORD_LAT, LNF_COORD_LON order by ACTUAL_DELIVERY_DAT desc) as ORDER_ID
  from
    tlgnc_shipto s
    inner join tlgnc_order o on s.shipto_sap_bp_id=o.shipto_sap_bp_id
  where
    s.shipto_type_cd=2
    and o.order_status_cd not in (17, 20)
  ) inner join tlgnc_order using(order_id)
  order by ACTUAL_DELIVERY_DAT desc;

COMMENT ON TABLE VLGNC_SHIPTO_SOLDTO_LNF_DATA IS 'Latest LNF addresses for LNF orders (shipto + soldto combination). Warning: Manually patched view in production!';

REM END VLGNC_SHIPTO_SOLDTO_LNF_DATA


-------------------------------------------------------------------------------

PROMPT VLGNC_SAP_MAT_DESC_PACK

CREATE OR REPLACE FORCE VIEW VLGNC_SAP_MAT_DESC_PACK AS
SELECT des.sap_material_id || '_' || des.lang_cd as composite_id,
  des.sap_material_id,
  mat.sap_material_no,
  des.lang_cd,
  des.txt,
  mat.material_package_cd,
  mat.base_uom_cd,
  pkg.logon_package_type_cd
FROM tlgns_sap_material mat
INNER JOIN tlgns_sap_material_description des ON(mat.sap_material_id = des.sap_material_id)
INNER JOIN tlgnr_material_package_bs pkg ON(mat.material_package_cd = pkg.material_package_cd);
COMMENT ON TABLE VLGNC_SAP_MAT_DESC_PACK IS 'Sap material description including packaging information.';

REM END VLGNC_SAP_MAT_DESC_PACK

-------------------------------------------------------------------------------

PROMPT VLGNC_BIZEVENT_AUDIT_DISPLAY

CREATE OR REPLACE FORCE VIEW VLGNC_BIZEVENT_AUDIT_DISPLAY AS
  SELECT
  au.BIZEVENT_AUDIT_ID || '_' || tx.lang_cd as composite_id,
  au.BIZEVENT_AUDIT_ID,
  au.OLD_VALUE_STR,
  au.NEW_VALUE_STR,
  au.OLD_VALUE_ID,
  au.NEW_VALUE_ID,
  au.OLD_VALUE_DAT,
  au.NEW_VALUE_DAT,
  au.AUDIT_BIZEVENT_CONFIG_CD,
  au.BIZEVENT_ID,
  tx.LANG_CD,
  (prs.firstname || ' ' || prs.name) initiator,
  tx.TXT,
  au.CTL_CRE_TS as event_time
FROM TLGNC_BIZEVENT_AUDIT au join
  TLGNR_AUDIT_BIZEVENT_CONFIG_BS bs on bs.AUDIT_BIZEVENT_CONFIG_CD = au.AUDIT_BIZEVENT_CONFIG_CD join
  TLGNR_AUDIT_BIZEVENT_CONFIG_TX tx on tx.AUDIT_BIZEVENT_CONFIG_CD = bs.AUDIT_BIZEVENT_CONFIG_CD join
  TLGNU_USER usr ON (usr.user_id = au.ctl_cre_uid) join
  TLGNC_PERSON prs ON (prs.person_id = usr.person_id);

COMMENT ON TABLE VLGNC_BIZEVENT_AUDIT_DISPLAY IS 'View containint bizevent audit entries. The valuas are modified by the application in order to show the correct resolved field content.';

REM END VLGNC_BIZEVENT_AUDIT_DISPLAY

-------------------------------------------------------------------------------

PROMPT VLGNC_SAREA_DISPLAY

CREATE OR REPLACE FORCE VIEW VLGNC_SAREA_DISPLAY AS
  SELECT DISTINCT s.sarea_id
    || '_'
    || sorgTx.lang_cd AS composite_id,
    s.sarea_id,
    s.sorg_cd,
    s.distribution_channel,
    s.division_cd,
    s.sarea_display_longname,
    sorgtx.address_name AS SORG_ADDRESS_NAME,
    sorgtx.lang_cd,
    s.delnote_uses_ws_number_flag,
    s.document_del_time_window_flag,
    s.document_distance_flag,
    s.document_comm_carrier_flag,
    s.document_exec_carrier_flag,
    s.cust_comm_flag,
    s.allow_sd_web_order_until_mi,
    s.allow_nd_web_order_until_mi,
    s.apply_sd_leadtime_chk_from_mi,
    s.apply_nd_leadtime_chk_until_mi,
    s.short_term_order_threshold_mi,
    s.allow_bimodal_creation_till_mi,
    s.shipto_open_1_from,
    s.shipto_open_1_to,
    s.shipto_open_2_from,
    s.shipto_open_2_to,
    s.mail_from,
    s.mail_test_to,
    s.mail_bcc,
    s.mail_railadmin,
    s.mail_encoding,
    s.delay_send_to_sap_mi,
    s.exact_quantity_order_tolerance,
    s.allow_planning_material_flag,
    s.cs_enable_one_time_cust_flag,
    s.cs_show_payment_mth_flag,
    s.cs_price_type,
    s.cs_show_totals_text_flag,
    s.cs_show_cust_tax_code_flag,
    s.cs_show_contract_currency_flag,
    s.dn_show_trailer_flag,
    s.dn_show_seals_info_flag,
    s.dn_show_silo_no_flag,
    s.dn_show_plant_cycle_flag,
    s.dn_show_fix_text,
    s.dn_show_dispatcher_driver_flag,
    s.dn_show_carrier_crm_flag,
    s.dn_show_shipto_contact_flag,
    exp_show_total_txt_flag,
    exp_show_legal_text_1_flag,
    exp_show_legal_text_2_flag,
    exp_show_issue_dat_loc_flag,
    exp_show_ad_invoice_no_flag,
    exp_show_ctrl_no_flag,
    exp_mat_price_type,
    exp_show_zcode1_2_flag,
    s.enable_pricing_call_flag,
    s.saturday_working_day_flag,
    s.allow_web_order_on_weekend,
    s.force_to_sap_flag,
    s.rail_return_sarea_flag,
    s.dblt_distance_l1,
    s.dblt_distance_l2,
    s.h1_early_order_limit,
    s.h2_earliest_delivery_from,
    s.dblt_offset_d1,
    s.dblt_offset_d2,
    s.dblt_offset_d3,
    s.dblt_offset_d4,
    s.dblt_offset_d5,
    s.dblt_offset_d6,
    s.enable_demand_planning_flag,
    s.demand_plan_latest_until_h
  FROM TLGNC_SAREA s
  JOIN tlgnr_sorg_tx sorgTx ON sorgTx.sorg_cd = s.sorg_cd;
  COMMENT ON TABLE VLGNC_SAREA_DISPLAY
    IS 'The VLGNC_SAREA_DISPLAY combines the text elements for the connected tables to sarea';
REM END VLGNC_SAREA_DISPLAY

-------------------------------------------------------------------------------

prompt  vlgnc_truck_revenue_planning

create or replace force view vlgnc_truck_revenue_planning as
  select
    to_char(trunc(order_schedule.planned_start), 'YYYY/MM/DD') || '|' || carrier.carrier_id
      || '|' || order_schedule.truck_id || '|' || ord.order_id as composite_id,
    trunc(order_schedule.planned_start) as planned_start_day,
    carrier.carrier_id,
    NVL(carrier.display_name, carrier_sapvendor.name1) as carrier_name,
    ord.pool_id,
    ord.order_id,
    ord.order_number,
    ord.freight_cost,
    ord.freight_cost_currency_cd,
    ord.order_validation_code,
    shippt.shippingpoint_name || ' (' || shippt.plant_cd  || ')' as shippt_plant,
    order_schedule.truck_id,
    order_schedule.truck_name,
    order_schedule.planned_start,
    order_schedule.load_duration,
    order_schedule.transport_duration,
    null as unload_duration,
    order_schedule.is_planning,
    ord.delivery_from_dat,
    ord.delivery_to_dat,
    shipto_sap_bp.name1 as shipto_name1,
    shipto_sap_bp.street as shipto_street,
    shipto_sap_bp.house_number as shipto_house_number,
    shipto_sap_bp.postal_code as shipto_postal_code,
    shipto_sap_bp.city as shipto_city,
    shipto_sap_bp.timezone_cd as shipto_timezone_cd,
    ord.note_dispatching as scheduling_comment,
    order_schedule.planned_start+(order_schedule.load_duration/(24*60))+(order_schedule.transport_duration/(24*60)) as planned_start_of_unloading
  from
    (
      select assp3.order_id order_id, decode(truck.sap_vehicle_char_cd, 2, 1, 0) is_planning, assp1.planned_start,
      truck.truck_id, truck.carrier_id, truck.truck_name,
      floor((assp2.planned_start - assp1.planned_start) * 1440) load_duration,
      floor((assp3.planned_start - assp2.planned_start) * 1440) transport_duration
      from tlgnc_assignment ass
      inner join tlgnc_truck truck on ass.truck_id = truck.truck_id
      left join
      (
        select assignment_id, min(planned_start) planned_start from tlgnc_assignment_part where assignment_part_type_cd = 1 group by assignment_id
      ) assp1 on assp1.assignment_id = ass.assignment_id
      left join
      (
        select assignment_id, min(planned_start) planned_start from tlgnc_assignment_part where assignment_part_type_cd = 2 group by assignment_id
      ) assp2 on assp2.assignment_id = ass.assignment_id
      inner join
      (
        select assignment_id, order_id, min(planned_start) planned_start from tlgnc_assignment_part where assignment_part_type_cd = 3 group by assignment_id, order_id
      ) assp3 on assp3.assignment_id = ass.assignment_id
      where ass.assign_tr_status_cd = 1
    ) order_schedule
    inner join tlgnc_carrier carrier on (order_schedule.carrier_id = carrier.carrier_id)
    inner join tlgns_sap_vendor carrier_sapvendor
      on (carrier_sapvendor.sap_vendor_id = carrier.sap_vendor_id)
    inner join tlgnc_order ord on (order_schedule.order_id = ord.order_id)
    inner join tlgnc_shippingpoint shippt on (ord.shippingpoint_id = shippt.shippingpoint_id)
    inner join tlgns_sap_bp shipto_sap_bp on (ord.shipto_sap_bp_id = shipto_sap_bp.sap_bp_id);

comment on table vlgnc_truck_revenue_planning is 'Truck revenue planning data for reporting/export without the materials. Because one order can have multiple materials, the material data is in a separate view (vlgnc_truck_revenue_plan_mat)';

rem end vlgnc_truck_revenue_planning

-------------------------------------------------------------------------------

prompt  vlgnc_truck_revenue_plan_mat

create or replace force view vlgnc_truck_revenue_plan_mat as
  select
    ord.order_id || '_' || order_item.order_item_id || '_' || sap_material_description.lang_cd as composite_id,
    ord.order_id,
    order_item.order_item_id,
    nvl(mat_plant.brand_sales_name, sap_material_description.txt) as order_item_name,
    sap_material_description.lang_cd,
    order_item.ordered_quantity,
    order_item.ordered_uom_cd,
    uom_tx.txt as ordered_uom_txt
  from
    tlgnc_order ord
    inner join tlgnc_order_item order_item on (ord.order_id = order_item.order_id)
    left join tlgnc_mat_plant mat_plant on (order_item.production_plant_cd = mat_plant.plant_cd
      and order_item.sap_material_id = mat_plant.sap_material_id)
    inner join tlgns_sap_material_description sap_material_description
      on (order_item.sap_material_id = sap_material_description.sap_material_id)
    inner join tlgnr_uom_tx uom_tx on (uom_tx.uom_cd = order_item.ordered_uom_cd
      and uom_tx.lang_cd = sap_material_description.lang_cd);

comment on table vlgnc_truck_revenue_plan_mat is 'Material data for the truck revenue planning reporting/export (vlgnc_truck_revenue_planning).';

rem end vlgnc_truck_revenue_plan_mat

-------------------------------------------------------------------------------

prompt  vlgnc_dispo_planning

create or replace force view vlgnc_dispo_planning as
SELECT ord.order_id,
    ord.order_number,
    ord.freight_cost,
    ord.freight_cost_currency_cd,
    ccarrier.carrier_id,
    truck_id,
    order_preschedule.planned_start,
    cast( (order_preschedule.LOAD_DURATION + order_preschedule.TRANSPORT_DURATION + order_preschedule.UNLOAD_DURATION ) as number (12,0)) order_delivery_duration
  FROM tlgnc_carrier ccarrier INNER JOIN tlgnc_order ord
  ON (ord.comm_carrier_id=ccarrier.carrier_id) LEFT OUTER JOIN tlgnc_order_preschedule order_preschedule
  ON (order_preschedule.order_id=ord.order_id);

comment on table vlgnc_dispo_planning is 'The view is supposed to return the planning data for any order where a prescheduling had happened.  Used on disposition screen.';

rem end vlgnc_dispo_planning

-------------------------------------------------------------------------------

prompt  vlgns_sap_contract_disp

CREATE OR replace force VIEW vlgns_sap_contract_disp AS
SELECT c.SAP_CONTRACT_ID,
    /** START: Backward compatibility columns */
    cast ((case when c.ITEM_GROUPING_TYPE = 1
    then 0
      else 1
    end) as NUMBER(1,0)) as QUANTITY_CONTROL_FLAG,
    /** END: Backward compatibility columns */
    c.SAP_CONTRACT_NO,
    c.SAP_CONTRACT_TYPE_CD,
    c.SAREA_ID,
    c.SOLDTO_SAP_BP_ID,
    c.SHIPTO_SAP_BP_ID,
    c.BILLTO_SAP_BP_ID,
     c.PAYER_SAP_BP_ID,
    c.VALID_FROM_DAT,
    c.VALID_UNTIL_DAT,
    c.VALID_UNTIL_IGNORE_FLAG,
    c.SAP_SHIPPING_COND_CD,
    c.CUST_PURCHASE_ORDER_NO,
    c.ITEM_GROUPING_TYPE,
    c.CURRENCY_CD,
    c.CREDIT_CONTROL_AREA_CD,
    c.CREDIT_LIMIT_ACCOUNT_NO,
    c.CUSTOMER_CREDIT_GROUP,
    c.CREDIT_MANAGEMENT_GROUP,
    c.CREDIT_RISK_CATEGORY,
    c.CREDIT_CURRENCY_CD,
    c.SALES_DISTRICT_CD,
    c.INCOTERMS_CD,
      c.CTL_CRE_UID,
      c.CTL_CRE_TS,
      c.CTL_MOD_UID,
      c.CTL_MOD_TS,
      c.CTL_TCN,
      c.VALIDATION_CODE,
    soldto.name1 AS soldto_name,
    soldto.sap_id AS soldto_sap_id,
    shipto.name1 AS shipto_name,
    shipto.sap_id AS shipto_sap_id,
    billto.name1 AS billto_name,
    billto.sap_id AS billto_sap_id,
    payer.name1 AS payer_name,
    payer.sap_id AS payer_sap_id,
    sarea.sarea_display_longname
FROM
tlgns_sap_contract c
LEFT JOIN TLGNS_SAP_BP soldto ON c.soldto_sap_bp_id = soldto.sap_bp_id
LEFT JOIN TLGNS_SAP_BP shipto ON c.shipto_sap_bp_id = shipto.sap_bp_id
LEFT JOIN TLGNS_SAP_BP billto ON c.billto_sap_bp_id = billto.sap_bp_id
LEFT JOIN TLGNS_SAP_BP payer ON c.payer_sap_bp_id = payer.sap_bp_id
LEFT JOIN TLGNC_SAREA sarea ON c.sarea_id = sarea.sarea_id;

comment ON TABLE vlgns_sap_contract_disp IS 'The view is supposed to return the data for the sap_contract as it is.';

rem END vlgns_sap_contract_disp

-------------------------------------------------------------------------------

prompt vlgns_sap_contract_item_disp

create or replace force view vlgns_sap_contract_item_disp as
select ci.sap_contract_item_id || '_' || md.lang_cd as composite_id,
       ci.SAP_CONTRACT_ITEM_ID,
	   ci.SAP_CONTRACT_ID,
	   ci.CONTRACT_ITEM_NO,
	   ci.SAP_MATERIAL_ID,
	   mat.PROD_PLANT_CD as PRODUCTION_PLANT_CD,
	   ci.SUPPLY_PLANT_CD,
	   ci.MATERIAL_GROUP_1,
	   ci.MATERIAL_GROUP_2,
	   ci.MATERIAL_GROUP_3,
	   ci.MATERIAL_GROUP_4,
	   ci.MATERIAL_GROUP_5,
	   ci.ITEM_CATEGORY,
	   ci.TARGET_QUANTITY_UOM_CD,
	   ci.NET_VALUE,
	   ci.MARKED_FOR_DELETION,
       ci.CTL_CRE_UID,
       ci.CTL_CRE_TS,
       ci.CTL_MOD_UID,
       ci.CTL_MOD_TS,
       ci.CTL_TCN,
       ci.TARGET_QUANTITY,
       md.txt as material_txt,
       md.lang_cd,
       prod_plant.plant_name as prod_plant_name,
       supply_plant.plant_name as supply_plant_name,
       ci.IS_AP_GROUP_LEADER,
       ci.AP_GROUP_LEADER_ID
       from tlgns_sap_contract_item ci
left join TLGNS_SAP_MATERIAL_DESCRIPTION md on ci.sap_material_id = md.sap_material_id
left join tlgns_sap_material mat on mat.sap_material_id =  ci.SAP_MATERIAL_ID
left join TLGNR_PLANT_BS prod_plant on mat.PROD_PLANT_cD = prod_plant.plant_cd
left join TLGNR_PLANT_BS supply_plant on ci.supply_plant_cd = supply_plant.plant_cd;

comment on table vlgns_sap_contract_item_disp is 'The view is supposed to return the data for the sap_contract_item as it is.';

rem end vlgns_sap_contract_item_disp

-------------------------------------------------------------------------------

PROMPT VLGNU_COMP_CODE_SAREA

CREATE OR REPLACE FORCE VIEW VLGNU_COMP_CODE_SAREA AS
  SELECT compcode.COMPANYCODE_CD || '_' || sarea.SAREA_ID as COMPOSITE_ID,
  compcode.COMPANYCODE_CD as COMPANYCODE_CD,
  compcode.COMPANYCODE_NAME as COMPANYCODE_NAME,
  sarea.SAREA_ID as SAREA_ID,
  sarea.SAREA_DISPLAY_LONGNAME as SAREA_DISPLAY_LONGNAME
  FROM
  TLGNC_SAREA sarea
  INNER JOIN TLGNR_SORG_BS sorg ON sarea.SORG_CD = sorg.SORG_CD
  INNER JOIN TLGNR_COMPANYCODE_BS compcode ON sorg.COMPANYCODE_CD = compcode.COMPANYCODE_CD;

COMMENT ON TABLE VLGNU_COMP_CODE_SAREA IS 'View for Assigned Sales Areas list in Admin / User Admin / User Admin (internal/external) and Admin / User profile.';

REM END VLGNU_COMP_CODE_SAREA

-------------------------------------------------------------------------------

prompt vlgnc_order_item_quantity

-- TODO 26-Oct-2012/romek: This view has a too generic name, and that causes confusion.

create or replace force view vlgnc_order_item_quantity as
  select
    item.order_item_id,
    ord.order_number,
    item.order_item_no,
    item.sap_contract_item_id,
    item.ordered_quantity,
    item.ordered_uom_cd,
    ordered_uom.fuzzy_quantity_flag as o_fuzzy_quantity_flag,
    ordered_uom.uom_dimension_cd as o_uom_dimension_cd,
    ordered_uom.numerator_for_conversion as o_numerator_for_conversion,
    ordered_uom.denominator_for_conversion as o_denominator_for_conversion,
    item.delivered_quantity,
    item.delivered_uom_cd,
    delivered_uom.fuzzy_quantity_flag as d_fuzzy_quantity_flag,
    delivered_uom.uom_dimension_cd as d_uom_dimension_cd,
    delivered_uom.numerator_for_conversion as d_numerator_for_conversion,
    delivered_uom.denominator_for_conversion as d_denominator_for_conversion,
    item.ordered_uom_numerator as o2b_numerator_for_conversion,
    item.ordered_uom_denominator as o2b_denominator_for_conversion,
    item.loaded_quantity,
    item.loaded_uom_cd
  from
    tlgnc_order_item item
    inner join tlgnc_order ord on (item.order_id = ord.order_id)
    left join tlgnr_uom_bs ordered_uom on (item.ordered_uom_cd = ordered_uom.uom_cd)
    left join tlgnr_uom_bs delivered_uom on (item.delivered_uom_cd = delivered_uom.uom_cd)
  where
    ord.order_status_cd <> 17 and ord.order_status_cd <> 20
    and item.sap_contract_item_id is not null;

comment on table vlgnc_order_item_quantity is 'View on order items created for calculation of the sum quantity of all delivered and open orders (to be checked against the contract target quantity), used by LRQ 002.1 - Quantity Restricted Contract';

rem end vlgnc_order_item_quantity

-------------------------------------------------------------------------------

prompt vlgnc_sdo_onetime_data

create or replace force view vlgnc_sdo_onetime_data as
  select
    ord.ORDER_ID,
    ord.LNF_COORD_LAT,
    ord.LNF_COORD_LON,
    ord.LNF_SITE_NAME,
    ord.LNF_SITE_STREET_HOUSE_NO,
    ord.LNF_SITE_COUNTRY,
    ord.LNF_SITE_POSTAL_CODE,
    ord.LNF_SITE_CITY,
    ord.ONE_TIME_SOLDTO_NAME,
    ord.ONE_TIME_SOLDTO_STREET_HOUSENO,
    ord.ONE_TIME_SOLDTO_CITY,
    ord.ONE_TIME_SOLDTO_POSTAL_CODE,
    ord.ONE_TIME_SOLDTO_COUNTRY
  from
    TLGNC_ORDER ord;

comment on table vlgnc_sdo_onetime_data is 'View on one-time shipto/soldto addresses stored in single-delivery orders, to be used when cloning an order in Call Center. (Extending VLGNC_ORDER_CREATE for this is not a good idea because we do not need this information when listing orders, but only when cloning.)';

rem end vlgnc_sdo_onetime_data

-------------------------------------------------------------------------------

prompt vlgnc_custord_onetime_data

create or replace force view vlgnc_custord_onetime_data as
  select
    custord_info.CUSTORDER_ID,
    ord.LNF_COORD_LAT,
    ord.LNF_COORD_LON,
    ord.LNF_SITE_NAME,
    ord.LNF_SITE_STREET_HOUSE_NO,
    ord.LNF_SITE_COUNTRY,
    ord.LNF_SITE_POSTAL_CODE,
    ord.LNF_SITE_CITY,
    ord.ONE_TIME_SOLDTO_NAME,
    ord.ONE_TIME_SOLDTO_STREET_HOUSENO,
    ord.ONE_TIME_SOLDTO_CITY,
    ord.ONE_TIME_SOLDTO_POSTAL_CODE,
    ord.ONE_TIME_SOLDTO_COUNTRY
  from
    (
      select
        custord.CUSTORDER_ID,
        MAX(ord.ORDER_NUMBER) as ORDER_NUMBER
      from
        TLGNC_CUSTORDER custord
        inner join TLGNC_ORDER ord on (custord.CUSTORDER_ID = ord.CUSTORDER_ID)
      group by
        custord.CUSTORDER_ID
    ) custord_info
    inner join TLGNC_ORDER ord on (ord.ORDER_NUMBER = custord_info.ORDER_NUMBER);

comment on table vlgnc_custord_onetime_data is 'View on one-time shipto/soldto addresses stored in the last single-delivery order per customer order, to be used when cloning an order in Call Center.';

rem end vlgnc_custord_onetime_data

-------------------------------------------------------------------------------

prompt VLGNC_PLANNING_TRUCK

create or replace force view VLGNC_PLANNING_TRUCK as
  select
    (truck.truck_id || '_' || pool_truck.pool_id) as composite_id,
    truck.truck_id as truck_id,
    truck.truck_type_cd as truck_type_cd,
    truck.carrier_id as carrier_id,
    pool_truck.pool_id as pool_id
  from tlgnc_truck truck
    left join tlgnc_pool_truck pool_truck
      on (truck.truck_id = pool_truck.truck_id)
  where
    truck.sap_vehicle_char_cd = 2 and
    truck.active_flag = 1
  order by truck.priority asc;
comment on table VLGNC_PLANNING_TRUCK is 'View for searching planning trucks IDs based on carrier, pool and truck type code.';

rem end VLGNC_PLANNING_TRUCK

-------------------------------------------------------------------------------

prompt VLGNC_COMM_EXEC_CARRIER_PF

create or replace force view VLGNC_COMM_EXEC_CARRIER_PF as
  select
  distinct
    --purchasing organization should not be taken into consideration at the moment, if there is a connection between them in any purchorg here it will appear and only once
    (comm_carr.carrier_id || '_' || exec_carr.carrier_id ) as composite_id,
    comm_carr.carrier_id as comm_carrier_id,
    comm_carr.display_name as comm_carrier_display_name,
    exec_carr.carrier_id as exec_carrier_id,
    exec_carr.display_name AS exec_carrier_display_name
  from (
    select sap_vendor_porg_id, exec_sap_vendor_porg_id, purchasingorg_cd
    from TLGNS_SAP_VN_PF_EXEC_CARRIER
    union
    select comm_sap_vendor_porg_id, sap_vendor_porg_id, purchasingorg_cd
    from TLGNS_SAP_VN_PF_COMM_CARRIER
    where sap_vendor_porg_id <> comm_sap_vendor_porg_id
  ) pf
  join TLGNR_PURCHASINGORG_BS porg_bs on ( porg_bs.purchasingorg_cd = pf.purchasingorg_cd and porg_bs.ctl_act_cd = 1 )
  join TLGNS_SAP_VENDOR_PORG comm_vendor_porg on ( comm_vendor_porg.sap_vendor_porg_id = pf.sap_vendor_porg_id and comm_vendor_porg.purchasingorg_cd=pf.purchasingorg_cd and comm_vendor_porg.marked_for_deletion  = '0' )
  join TLGNS_SAP_VENDOR_PORG exec_vendor_porg on ( exec_vendor_porg.sap_vendor_porg_id = pf.exec_sap_vendor_porg_id and exec_vendor_porg.purchasingorg_cd=pf.purchasingorg_cd and exec_vendor_porg.marked_for_deletion  = '0')
  join TLGNS_SAP_VENDOR comm_vendor on (comm_vendor.sap_vendor_id = comm_vendor_porg.sap_vendor_id and comm_vendor.vn_is_comm_carrier = 1)
  join TLGNS_SAP_VENDOR exec_vendor on (exec_vendor.sap_vendor_id = exec_vendor_porg.sap_vendor_id and exec_vendor.vn_is_exec_carrier = 1)
  join TLGNC_CARRIER exec_carr on (exec_carr.sap_vendor_id=exec_vendor_porg.sap_vendor_id)
  join TLGNC_CARRIER comm_carr on (comm_carr.sap_vendor_id=comm_vendor_porg.sap_vendor_id)
  order by comm_carrier_id;

comment on table VLGNC_COMM_EXEC_CARRIER_PF is 'View containing pairs of commercial and executing carriers which are partner in at least one purchasing organization.';

rem end VLGNC_COMM_EXEC_CARRIER_PF

-------------------------------------------------------------------------------

prompt VLGNC_CURRENT_EXCHANGE_RATE

create or replace force view VLGNC_CURRENT_EXCHANGE_RATE as
  select distinct er.currency_cd,
  cast( (select distinct first_value(er0.exchange_rate) over (order by er0.valid_from_dat desc)
       from tlgnc_currency_exch_rate er0
       where er0.currency_cd=er.currency_cd
       and current_date > er0.valid_from_dat) as number(12,4) ) exchange_rate
  from tlgnc_currency_exch_rate er;

comment on table VLGNC_CURRENT_EXCHANGE_RATE is 'View which returns the current exchange rate for all currencies present in tlgnc_currency_exch_rate.';

rem end VLGNC_CURRENT_EXCHANGE_RATE;

-------------------------------------------------------------------------------

prompt VLGNC_FVL_SAP_BP

CREATE OR REPLACE FORCE VIEW VLGNC_FVL_SAP_BP as
SELECT
   shipto.SHIPTO_ID || '_' || ccode.companycode_cd as composite_id,
   shipto.shipto_sap_bp_id,
   bp.SAP_ID,
   bp.NAME1,
   ccode.companycode_cd
from TLGNC_SHIPTO shipto
inner join TLGNS_SAP_BP bp on (shipto.SHIPTO_SAP_BP_ID = bp.SAP_BP_ID and shipto.ACTIVE_FLAG = 1 and shipto.shipto_type_cd=3)
left join TLGNS_SAP_BP_COMPANYCODE ccode on (ccode.sap_bp_id=bp.sap_bp_id)
inner join TLGNC_SHIPPINGPOINT sp on sp.SHIPPINGPOINT_ID=shipto.MAT_CHAIN_SHIPPINGPOINT_ID and sp.SHIPPPOINT_TYPE_CD=3;

comment on table VLGNC_FVL_SAP_BP is 'View containing FVL shiptos including companycode associations.';

rem end VLGNC_FVL_SAP_BP;

-------------------------------------------------------------------------------

PROMPT  VLGNC_SHIPTO_SAREA_RESTR_SEAR

CREATE OR REPLACE FORCE VIEW VLGNC_SHIPTO_SAREA_RESTR_SEAR AS
    SELECT
    shiptosr.SHIPTO_SAREA_RESTR_ID || '_' || lang.LANG_CD as COMPOSITE_ID,
    shiptosr.SAP_MATERIAL_ID,
    shiptosr.POOL_ID,
    shiptosr.COMM_CARRIER_ID,
    shiptosr.DELIVERY_TYPE_CD,
    materialdesc.TXT as MATERIAL_TXT,
    pooltext.NAME as POOL_NAME,
    carrier.DISPLAY_NAME as CARRIER_NAME,
    shipp.PLANT_CD,
    plant.PLANT_NAME,
    delivtypetx.TXT AS DELIVERY_TYPE_TXT,
    shiptobp.SAP_ID AS SHIPTO_NR,
    shiptobp.NAME1  AS SHIPTO_NAME,
    sc.SAP_CONTRACT_NO,
    sarea.SAREA_DISPLAY_LONGNAME,
    shiptosr.ENABLED_FLAG,
    material.SAP_MATERIAL_NO,
    lang.LANG_CD,
    sarea.SAREA_ID,
    shipto.SHIPTO_ID,
    shipto.SHIPTO_SAP_BP_ID,
    shipp.SHIPPINGPOINT_ID
  from
    TLGNC_SHIPTO_SAREA_RESTR shiptosr
    CROSS JOIN TLGNR_LANG_BS lang
    INNER JOIN TLGNC_SHIPTO_SAREA shiptosarea ON (shiptosr.SHIPTO_SAREA_ID = shiptosarea.SHIPTO_SAREA_ID)
    INNER JOIN TLGNC_SHIPTO shipto ON (shiptosarea.SHIPTO_ID = shipto.SHIPTO_ID)
    INNER JOIN TLGNS_SAP_BP shiptobp ON (shipto.SHIPTO_SAP_BP_ID = shiptobp.SAP_BP_ID)
    INNER JOIN TLGNS_SAP_CONTRACT_ITEM scitem ON (shiptosr.SAP_CONTRACT_ITEM_ID = scitem.SAP_CONTRACT_ITEM_ID)
    INNER JOIN TLGNS_SAP_CONTRACT sc ON (scitem.SAP_CONTRACT_ID = sc.SAP_CONTRACT_ID)
    INNER JOIN TLGNC_SAREA sarea ON (shiptosarea.SAREA_ID = sarea.SAREA_ID)
    INNER JOIN TLGNS_SAP_MATERIAL material on (shiptosr.SAP_MATERIAL_ID = material.SAP_MATERIAL_ID)
    INNER JOIN TLGNS_SAP_MATERIAL_DESCRIPTION materialdesc on (shiptosr.SAP_MATERIAL_ID = materialdesc.SAP_MATERIAL_ID and lang.LANG_CD = materialdesc.LANG_CD)
    LEFT OUTER JOIN TLGNC_CARRIER carrier on (shiptosr.COMM_CARRIER_ID = carrier.CARRIER_ID)
    LEFT OUTER JOIN TLGNC_POOL_TX pooltext ON (shiptosr.POOL_ID = pooltext.POOL_ID AND pooltext.LANG_CD = lang.LANG_CD)
    LEFT OUTER JOIN (TLGNC_SHIPPINGPOINT shipp
      JOIN TLGNR_PLANT_BS plant ON (plant.PLANT_CD = shipp.PLANT_CD)
    ) ON (shiptosr.SHIPPINGPOINT_ID = shipp.SHIPPINGPOINT_ID)
    LEFT OUTER JOIN TLGNR_DELIVERY_TYPE_TX delivtypetx ON (delivtypetx.DELIVERY_TYPE_CD = shiptosr.DELIVERY_TYPE_CD AND delivtypetx.LANG_CD = lang.LANG_CD)
;
COMMENT ON TABLE VLGNC_SHIPTO_SAREA_RESTR_SEAR is 'Collect all information needed in Dispocard view Search screen';

REM END VLGNC_SHIPTO_SAREA_RESTR_SEAR

-------------------------------------------------------------------------------

prompt VLGNU_USER_ACTIVITY_LOG

create or replace force view VLGNU_USER_ACTIVITY_LOG as
  select
    ua.user_activity_id,
    ua.ctl_cre_ts as timestamp,
    exec.user_id as executor_id,
      exec.user_type_cd as executor_type_cd,
      exec.ldap_holspace as executor_holspace,
      exec.ext_login as executor_extlogin,
      exec.pda_user_no as executor_pdauser,
      execpers.name as executor_name,
      execpers.firstname as executor_firstname,
    usr.user_id as user_id,
      usr.user_type_cd as user_type_cd,
      usr.ldap_holspace as user_holspace,
      usr.ext_login as user_extlogin,
      usr.pda_user_no as user_pdauser,
      usrpers.name as user_name,
      usrpers.firstname as user_firstname,
    ua.user_activity_action_cd as action_cd,
    ua.ip_address,
    ua.description
  from tlgnu_user_activity ua
    join tlgnu_user usr on (usr.user_id = ua.user_id)
      join tlgnc_person usrpers on (usrpers.person_id = usr.person_id)
      join tlgnu_user exec on (exec.user_id = ua.ctl_cre_uid)
      join tlgnc_person execpers on (execpers.person_id = exec.person_id)
  order by timestamp desc;

comment on table VLGNU_USER_ACTIVITY_LOG is 'View containing the user activites';

rem end VLGNU_USER_ACTIVITY_LOG

-------------------------------------------------------------------------------

prompt VLGNU_UTIL_LAST_LOGIN

create or replace force view VLGNU_UTIL_LAST_LOGIN as
  select
    usr.user_id,
    al.timestamp as last_login,
    usr.ldap_holspace,
    usr.ext_login,
    usr.pda_user_no
  from tlgnu_user usr
  left join (
      select distinct
        vual.user_id,
        max(vual.timestamp) as timestamp
      from vlgnu_user_activity_log vual
      where
        vual.action_cd = 3 or vual.action_cd = 22 or vual.action_cd = 42 or vual.action_cd = 62
      group by vual.user_id
  ) al on al.user_id = usr.user_id
  order by usr.user_id;

comment on table VLGNU_UTIL_LAST_LOGIN is 'Utility view containing the last successful login of the users. Could be used to report/detect dormant users';

rem end VLGNU_UTIL_LAST_LOGIN

-------------------------------------------------------------------------------

PROMPT VLGNS_RAIL_REP_SOLDTO_SHIPTO

CREATE OR REPLACE FORCE VIEW VLGNS_RAIL_REP_SOLDTO_SHIPTO AS
  SELECT soldto_sarea.sap_bp_id AS sap_bp_id, soldto_sarea.sarea_id, shipto_sarea.sap_bp_id AS shipto_sap_bp_id, sarea.rail_return_sarea_flag
  FROM TLGNS_SAP_BP_PF_SHIPTO shipto
  INNER JOIN TLGNS_SAP_BP_SAREA soldto_sarea ON (soldto_sarea.sap_bp_sarea_id = shipto.sap_bp_sarea_id)
  INNER JOIN TLGNS_SAP_BP soldto_bp ON (soldto_bp.sap_bp_id = soldto_sarea.sap_bp_id)
  INNER JOIN TLGNS_SAP_BP_SAREA shipto_sarea ON (shipto_sarea.sap_bp_sarea_id = shipto.shipto_sap_bp_sarea_id)
  INNER JOIN TLGNC_SAREA sarea ON (shipto_sarea.sarea_id = sarea.sarea_id)
  WHERE soldto_bp.marked_for_deletion <> 1
    AND soldto_sarea.marked_for_deletion <> 1
    AND shipto_sarea.marked_for_deletion <> 1
    AND soldto_bp.bp_is_soldto = 1;

COMMENT ON TABLE VLGNS_RAIL_REP_SOLDTO_SHIPTO IS 'View to query sold-tos for ship-tos (used for automatic creation of rail repair orders)';

REM END VLGNS_RAIL_REP_SOLDTO_SHIPTO

-------------------------------------------------------------------------------

PROMPT VLGNC_CARRIER_SVENDOR

CREATE OR REPLACE FORCE VIEW VLGNC_CARRIER_SVENDOR AS
  SELECT
    carrier.CARRIER_ID,
    svendor.UPDATE_BLOCKED_BY_HJID,
    svendor.NAME1,
    svendor.NAME2,
    svendor.STREET,
    svendor.CITY,
    svendor.COUNTRY_CD,
    svendor.SAP_ID,
    svendor.SAP_VENDOR_ID,
    svendor.ACCOUNT_GROUP,
    svendor.VN_IS_COMM_CARRIER,
    svendor.VN_IS_EXEC_CARRIER,
    svendor.VN_IS_INTERNAL,
    carrier.DISPLAY_NAME,
    carrier.TRANSMIT_TO_CARRIER_FLAG,
    carrier.FREIGHT_REVENUE_CURRENCY_CD,
    carrier.CARRIER_REQUIRES_PLANNING_FLAG,
    shippingpoint.SAP_SHIPPINGPOINT_NO || '-' || shippingpoint.SHIPPINGPOINT_NAME as BAG_STOCK_SP
  FROM TLGNC_CARRIER carrier
  INNER JOIN TLGNS_SAP_VENDOR svendor ON (svendor.SAP_VENDOR_ID = carrier.SAP_VENDOR_ID)
  LEFT JOIN TLGNC_SHIPPINGPOINT shippingpoint ON (shippingpoint.SHIPPINGPOINT_ID = carrier.BAG_STOCK_SHIPPINGPOINT_ID);

COMMENT ON TABLE VLGNC_CARRIER_SVENDOR IS 'Contain all carriers with the sap vendor/shippingpoint details.';

REM END VLGNC_CARRIER_SVENDOR

-------------------------------------------------------------------------------

PROMPT VLGNC_ORDER_READY_TO_FINISH

CREATE OR REPLACE FORCE VIEW VLGNC_ORDER_READY_TO_FINISH AS
SELECT distinct
    o.order_id,
    o.actual_delivery_dat,
    CASE WHEN o.force_to_sap_flag + sa.force_to_sap_flag > 0 THEN 1 ELSE 0 END AS force_to_sap_flag
  FROM tlgnc_order o
  INNER JOIN tlgnc_order_item oi ON oi.order_id = o.order_id
  INNER JOIN tlgnc_sarea sa ON sa.sarea_id = o.sarea_id
  LEFT JOIN (SELECT DISTINCT load_id FROM tlgnc_goods_movement gm
        WHERE gm.sent_goodsmove_to_sap_ts IS NULL) unsent_goods_movements on unsent_goods_movements.load_id=oi.load_id
    WHERE o.order_status_cd = 15
    AND
    (
      (
        (o.sent_salesdoc_to_sap_ts is not null)
        AND
        (
          (o.delivery_type_cd = 1 AND o.sap_contract_id in (
                    select sap_contract_id
                    from tlgns_sap_contract
                    where incoterms_cd IN ('EXW', 'FOB'))
          ) -- road direct orders with EXW and FOB incoterms don't have a shipment doc, see LOGON-5437
          OR
          (o.delivery_type_cd = 2 AND o.sap_contract_id in (
                    select sap_contract_id
                    from tlgns_sap_contract
                    where incoterms_cd IN ('EXW'))
          ) -- Rail EXW set to Finished see LOGON-8615
          OR
          (o.delivery_type_cd = 5) -- pickup orders don't have a shipment doc, see LOGON-5437
          OR
          (o.delivery_type_cd = 8) -- service only orders don't have a shipment doc, see LOGON-4743
          OR
          (o.delivery_type_cd = 9) -- return pickup orders don't have a shipment doc, see LOGON-6761, LOGON-6532 (LRQ 018.2)
          OR
          (o.delivery_type_cd = 11) -- return ship pickup orders don't have a shipment doc, see LOGON-6761, LOGON-6621 (LRQ 108.x)
          OR
          (o.delivery_type_cd = 12) -- return blocktrain pickup orders don't have a shipment doc, see LOGON-6761, LOGON-6621 (LRQ 108.x)
          OR
          (o.sent_shipmentdoc_to_sap_ts is not null)
        )
      )
      OR -- Rail return or rep orders, see LOGON-6445
      (
        (o.delivery_type_cd in (2, 3))
        AND
        (o.distribution_dest_cd in ('REP', 'OUTR', 'IUDR',  'IUPR', 'INBR'))
      )
    )
    AND unsent_goods_movements.LOAD_ID is null;

COMMENT ON TABLE VLGNC_ORDER_READY_TO_FINISH IS 'Contains all Orders, which fulfill all the requirements to be finished and frozen.';

REM END VLGNC_ORDER_READY_TO_FINISH

-------------------------------------------------------------------------------

PROMPT VLGNC_GOODS_MOVEMENT_HELPER

CREATE OR REPLACE FORCE VIEW VLGNC_GOODS_MOVEMENT_HELPER AS
  SELECT DISTINCT l.load_id || '_' || o.order_id as composite_id,
    l.load_id,
    o.order_id,
    o.delivery_type_cd,
    NVL(o.order_number_from_seq_usage,
      DECODE(NVL(o.lds_delivery_note_no, l.ws_delivery_note_no), null, TO_CHAR(o.order_number),
        DECODE(sarea.delnote_uses_ws_number_flag, 0, TO_CHAR(o.order_number),
          NVL(o.lds_delivery_note_no, l.ws_delivery_note_no)))) as ref_doc_nr_sap,
    ap.planned_start,
    sarea.sarea_id
  FROM tlgnc_load l
  JOIN tlgnc_order_item oi ON oi.load_id = l.load_id
  JOIN tlgnc_order o ON o.order_id = oi.order_id
  JOIN tlgnc_sarea sarea ON sarea.sarea_id = o.sarea_id
  JOIN tlgnc_assignment_part ap ON ap.order_id = oi.order_id;

COMMENT ON TABLE VLGNC_GOODS_MOVEMENT_HELPER IS 'helps creating the goodsMovemntEntity by providing data from order for load_id';

REM END VLGNC_GOODS_MOVEMENT_HELPER

-------------------------------------------------------------------------------

PROMPT VLGNC_CUSTORDER_VIEW

CREATE OR REPLACE FORCE VIEW VLGNC_CUSTORDER_VIEW AS
  select distinct
    custord.custorder_id || '_' || matdesc.lang_cd as custorderview_id,
    custord.custorder_id,
    custord.custorder_number,
    custord.custorder_status_cd,
    custord.pool_id,
      custord.delivery_from_dat,
      custord.delivery_to_dat,
      custord.comm_carrier_id,
      shippt.shippingpoint_name,
      shipto.name1 as shipto_name,
      shipto.city as shipto_city,
      matdesc.txt as material_name,
        matdesc.lang_cd,
      soldto.name1 as soldto_name,
      shipto.name1 || ', ' || shipto.city || ', ' || shipto.street as shipto_city_address,
      sarea.exact_quantity_order_tolerance,
      coi.exact_custorder_qty_flag
  from tlgnc_custorder custord
  join tlgnc_shippingpoint shippt on (custord.shippingpoint_id = shippt.shippingpoint_id)
  join tlgns_sap_bp shipto on (custord.shipto_sap_bp_id = shipto.sap_bp_id)
  join tlgns_sap_bp soldto on (custord.soldto_sap_bp_id = soldto.sap_bp_id)
  join tlgnc_custorder_item coi on custord.custorder_id = coi.custorder_id
  join tlgns_sap_material_description matdesc on (coi.sap_material_id = matdesc.sap_material_id)
  join tlgnc_sarea sarea on (custord.sarea_id = sarea.sarea_id);

COMMENT ON TABLE VLGNC_CUSTORDER_VIEW IS 'gathering all the info for the dispo screen for custorders';

REM END VLGNC_CUSTORDER_VIEW

-------------------------------------------------------------------------------

prompt vlgnc_custorder_item_sum_qty

-- See LOGON-5372 for requirements.
-- when changing please consider changing vlgnc_custord_item_sum_qty_ext which is 100% identical to this view just containing material info

create or replace force view vlgnc_custorder_item_sum_qty as
  select
    custord.custorder_id || '_' || material.sap_material_id as composite_id,
    custord.custorder_id,
    custord.custorder_number,
    material.base_uom_cd,
    base_uom_tx.lang_cd,
    base_uom_tx.txt as base_uom_txt,
    material.sap_material_id,
    count(*) as nr_of_orders,
    count(case
      when (o.order_status_cd in (6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20)) then
        1
      else
        null
    end) as nr_of_scheduled_orders,
    coitem.ordered_quantity,
    coitem.ordered_uom_cd,
    ordered_uom_tx.txt as ordered_uom_txt,
    sum(case
      when (o.order_status_cd in (6, 7, 8, 9, 10)) then
        oitem.ordered_quantity * (oitem.ordered_uom_numerator / oitem.ordered_uom_denominator)
      when (o.order_status_cd in (11, 12, 13, 14, 18)) then
        oitem.loaded_quantity * (oitem.lr_uom_numerator / oitem.lr_uom_denominator)
      when (o.order_status_cd in (15, 16, 19)) then
        oitem.delivered_quantity
      else
        0
    end) as scheduled_quantity_in_base_uom,
    sum(case
      when (o.order_status_cd in (1, 2, 3, 4, 5, 6, 7, 18, 8, 9, 10, 11)) then
        oitem.ordered_quantity * (oitem.ordered_uom_numerator / oitem.ordered_uom_denominator)
      when (o.order_status_cd in (12, 13, 14)) then
        oitem.loaded_quantity * (oitem.lr_uom_numerator / oitem.lr_uom_denominator)
      else
        0
    end) as open_quantity_in_base_uom,
    sum(case
      when (o.order_status_cd in (11, 12, 13, 14)) then
        oitem.loaded_quantity * (oitem.lr_uom_numerator / oitem.lr_uom_denominator)
      when (o.order_status_cd in (15, 16, 19)) then
        oitem.delivered_quantity
      else
        0
    end) as loaded_quantity_in_base_uom,
    sum(case
      when (o.order_status_cd in (15, 16, 19)) then
        oitem.delivered_quantity
      else
        0
    end) as delivered_quantity_in_base_uom
  from
    tlgnc_custorder custord
    join tlgnc_order o on custord.custorder_id = o.custorder_id
    join tlgnc_order_item oitem on o.order_id = oitem.order_id
    join tlgns_sap_material material on oitem.sap_material_id = material.sap_material_id
    join tlgnc_custorder_item coitem on custord.custorder_id = coitem.custorder_id
    and (coitem.sap_material_id = material.sap_material_id or coitem.sap_material_id = 1 or coitem.sap_material_id = 2)
    join tlgnr_uom_bs base_uom on material.base_uom_cd = base_uom.uom_cd
    left join tlgnr_uom_tx base_uom_tx on (base_uom.uom_cd = base_uom_tx.uom_cd)
    left join tlgnr_uom_tx ordered_uom_tx on (ordered_uom_tx.uom_cd = coitem.ordered_uom_cd
        and ordered_uom_tx.lang_cd = base_uom_tx.lang_cd)
  group by
    custord.custorder_id, custord.custorder_number, material.base_uom_cd, base_uom_tx.lang_cd,
    base_uom_tx.txt, material.sap_material_id, coitem.ordered_quantity, coitem.ordered_uom_cd,
    ordered_uom_tx.txt;

comment on table vlgnc_custorder_item_sum_qty is 'used for in on scheduling screen. calculating ordered, scheduled, loaded, delivered quantities per customer order item. Contains no detailed material information (like description).';

rem end vlgnc_custorder_item_sum_qty

-------------------------------------------------------------------------------

prompt vlgnc_custord_item_sum_qty_ext

-- See LOGON-5372 for requirements.
-- view 100% identical to vlgnc_custorder_item_sum_qty but contains a join to the slow tlgns_sap_material_description table

create or replace force view vlgnc_custord_item_sum_qty_ext as
  select
    custord.custorder_id || '_' || material.sap_material_id as composite_id,
    custord.custorder_id,
    custord.custorder_number,
    material.base_uom_cd,
    base_uom_tx.lang_cd,
    base_uom_tx.txt as base_uom_txt,
    material.sap_material_id,
    coitem.custorder_item_no,
    descr.txt as material_txt,
    count(*) as nr_of_orders,
    count(case
      when (o.order_status_cd in (6, 7, 8, 9, 10, 11, 12, 13, 14, 15, 16, 17, 19, 20)) then
        1
      else
        null
    end) as nr_of_scheduled_orders,
    coitem.ordered_quantity,
    coitem.ordered_uom_cd,
    ordered_uom_tx.txt as ordered_uom_txt,
    sum(case
      when (o.order_status_cd in (6, 7, 8, 9, 10)) then
        oitem.ordered_quantity * (oitem.ordered_uom_numerator / oitem.ordered_uom_denominator)
      when (o.order_status_cd in (11, 12, 13, 14, 18)) then
        oitem.loaded_quantity * (oitem.lr_uom_numerator / oitem.lr_uom_denominator)
      when (o.order_status_cd in (15, 16, 19)) then
        oitem.delivered_quantity
      else
        0
    end) as scheduled_quantity_in_base_uom,
    sum(case
      when (o.order_status_cd in (1, 2, 3, 4, 5, 6, 7, 18, 8, 9, 10, 11)) then
        oitem.ordered_quantity * (oitem.ordered_uom_numerator / oitem.ordered_uom_denominator)
      when (o.order_status_cd in (12, 13, 14)) then
        oitem.loaded_quantity * (oitem.lr_uom_numerator / oitem.lr_uom_denominator)
      else
        0
    end) as open_quantity_in_base_uom,
    sum(case
      when (o.order_status_cd in (11, 12, 13, 14)) then
        oitem.loaded_quantity * (oitem.lr_uom_numerator / oitem.lr_uom_denominator)
      when (o.order_status_cd in (15, 16, 19)) then
        oitem.delivered_quantity
      else
        0
    end) as loaded_quantity_in_base_uom,
    sum(case
      when (o.order_status_cd in (15, 16, 19)) then
        oitem.delivered_quantity
      else
        0
    end) as delivered_quantity_in_base_uom
  from
    tlgnc_custorder custord
    join tlgnc_order o on custord.custorder_id = o.custorder_id
    join tlgnc_order_item oitem on o.order_id = oitem.order_id
    join tlgns_sap_material material on oitem.sap_material_id = material.sap_material_id
    join tlgnc_custorder_item coitem on custord.custorder_id = coitem.custorder_id
    and (coitem.sap_material_id = material.sap_material_id or coitem.sap_material_id = 1 or coitem.sap_material_id = 2)
    join tlgnr_uom_bs base_uom on material.base_uom_cd = base_uom.uom_cd
    join tlgns_sap_material_description descr on (descr.sap_material_id=oitem.sap_material_id)
    left join tlgnr_uom_tx base_uom_tx on (base_uom.uom_cd = base_uom_tx.uom_cd and base_uom_tx.lang_cd=descr.lang_cd)
    left join tlgnr_uom_tx ordered_uom_tx on (ordered_uom_tx.uom_cd = coitem.ordered_uom_cd
        and ordered_uom_tx.lang_cd = descr.lang_cd)
  group by
    custord.custorder_id, custord.custorder_number, material.base_uom_cd, base_uom_tx.lang_cd,
    base_uom_tx.txt, material.sap_material_id, coitem.custorder_item_no, descr.txt, coitem.ordered_quantity, coitem.ordered_uom_cd,
    ordered_uom_tx.txt;

comment on table vlgnc_custord_item_sum_qty_ext is 'used for in order details and on scheduling screen. calculating ordered, scheduled, loaded, delivered quantities per customer order item. Contains detailed material information (like description).';

rem end vlgnc_custord_item_sum_qty_ext

-------------------------------------------------------------------------------

prompt vlgnc_order_item_ordered_qty

create or replace force view vlgnc_order_item_ordered_qty as
  select
    o.order_id || '_' || material.sap_material_id as composite_id,
    o.order_id,
    o.order_number,
    oitem.order_item_no,
    oitem.ordered_quantity,
    oitem.ordered_uom_cd,
    material.sap_material_no,
    uomtx.lang_cd,
    uomtx.txt as ordered_uom_txt,
    material.sap_material_id
  from
    tlgnc_order o
    join tlgnc_order_item oitem on o.order_id = oitem.order_id
    join tlgns_sap_material material on oitem.sap_material_id = material.sap_material_id
    left join tlgnr_uom_tx uomtx on (oitem.ordered_uom_cd = uomtx.uom_cd)
  order by oitem.order_item_no;

comment on table vlgnc_order_item_ordered_qty is 'used in simple vehicle scheduling screen. Fetches the ordered quantities per orderitem.';

rem end vlgnc_order_item_ordered_qty

-------------------------------------------------------------------------------

PROMPT VLGNC_TEST_SP_DROPDOWN

CREATE OR REPLACE FORCE VIEW VLGNC_TEST_SP_DROPDOWN AS
  SELECT
    sp.shippingpoint_id,
    sp.shippingpoint_name,
    NVL2(sp.lds_server, ' - ' || sp.lds_server, '') lds_server,
    wstx.lang_cd,
    wstx.txt
  FROM tlgnc_shippingpoint sp
  INNER JOIN tlgnr_weigh_system_sp_bs wsbs ON (sp.weigh_system_sp_cd = wsbs.weigh_system_sp_cd)
  INNER JOIN tlgnr_weigh_system_sp_tx wstx ON (wsbs.weigh_system_sp_cd = wstx.weigh_system_sp_cd)
  WHERE wsbs.push_mode_on = 1
    OR sp.weigh_system_sp_cd = 1;

COMMENT ON TABLE VLGNC_TEST_SP_DROPDOWN IS 'Used in the test tab to fill the shippingpoint dropdown.';

REM END VLGNC_TEST_SP_DROPDOWN

-------------------------------------------------------------------------------

PROMPT VLGNC_SAP_MAT_MIXDESIGN

CREATE OR REPLACE FORCE VIEW VLGNC_SAP_MAT_MIXDESIGN AS
  SELECT
    mat.sap_material_id,
    fireq.mix_design_cd,
    fireq.agg_rmx_code
  FROM tlgns_sap_material mat
  INNER JOIN tlgnc_fireq_material_mixdesign fireq ON (mat.sap_material_id = fireq.sap_material_id)
  WHERE mat.prod_plant_cd IS NOT NULL
  ORDER BY fireq.agg_rmx_code;

COMMENT ON TABLE VLGNC_SAP_MAT_MIXDESIGN IS 'SAP material connected with 1:1 fireq mixdesign and agg/rmx code.';

REM END VLGNC_SAP_MAT_MIXDESIGN

-------------------------------------------------------------------------------

PROMPT VLGNC_PLANT_SAREA_COMPCODE

CREATE OR REPLACE FORCE VIEW VLGNC_PLANT_SAREA_COMPCODE AS
  SELECT
    psa.PLANT_SAREA_ID,
    sa.SAREA_ID,
    plantbs.PLANT_CD,
    plantbs.PLANT_NAME,
    plantbs.PRODUCTION_PLANT,
    plantbs.CTL_ACT_CD,
    cpbs.COMPANYCODE_CD,
    cpbs.COMPANYCODE_NAME
FROM TLGNC_PLANT_SAREA psa
  INNER JOIN TLGNR_PLANT_BS plantbs ON (psa.PLANT_CD = plantbs.PLANT_CD)
  INNER JOIN TLGNC_SAREA sa ON (psa.SAREA_ID = sa.SAREA_ID)
  INNER JOIN TLGNR_SORG_BS sorgbs ON (sa.SORG_CD = sorgbs.SORG_CD)
  INNER JOIN TLGNR_COMPANYCODE_BS cpbs ON (sorgbs.COMPANYCODE_CD = cpbs.COMPANYCODE_CD);

COMMENT ON TABLE VLGNC_PLANT_SAREA_COMPCODE IS 'Plant and company code information, This view is used to add user authorization restriction for TLGNU_USER_COMPANYCODE_ACC / TLGNU_USER_SAREA_ACC / TLGNU_USER_PLANT_ACC.';

REM END VLGNC_PLANT_SAREA_COMPCODE

-------------------------------------------------------------------------------

PROMPT VLGNC_SIMPLE_VEHICLE_ASSG

CREATE OR REPLACE FORCE VIEW VLGNC_SIMPLE_VEHICLE_ASSG AS
SELECT
  vehicle.simple_vehicle_id || '_' || ass.assignment_id || '_' ||assp.assignment_part_id as composite_id,
  vehicle.simple_vehicle_id,
  vehicle.vehicle_name,
  vehicle.vehicle_reg_no,
  vehicle.simple_vehicle_type_cd,
  veh_type_tx.txt vehicle_type_tx,
  vehicle.priority,
  vehicle.max_gross_weight_t,
  vehicle.tare_weight_t,
  vehicle.carrier_id,
  carrier.display_name carrier_name,
  ass.assignment_id,
  ass.assignment_dat,
  ass_st_tx.txt ass_st_txt,
  ord.order_id,
  ord.order_number,
  ord.order_status_cd,
  sp.weigh_system_sp_cd,
  status_tx.txt order_status_txt,
  matdesc.txt material_txt,
  bp_shipto.name1 shipto,
  veh_type_tx.lang_cd
FROM tlgnc_simple_vehicle vehicle
inner join tlgnc_carrier carrier on vehicle.carrier_id = carrier.carrier_id
inner join tlgnr_simple_vehicle_type_tx veh_type_tx on  veh_type_tx.simple_vehicle_type_cd = vehicle.simple_vehicle_type_cd
-- Join with vehicle's active assignments in state "new" (0) or "shipping" (3)
LEFT JOIN tlgnc_assignment ass
ON (vehicle.simple_vehicle_id = ass.simple_vehicle_id and ass.active_flag = 1 and ass.assign_status_cd in (0, 3))
left join tlgnr_assign_status_tx ass_st_tx on (ass_st_tx.assign_status_cd = ass.assign_status_cd and ass_st_tx.lang_cd = veh_type_tx.lang_cd)
LEFT JOIN tlgnc_assignment_part assp
ON assp.assignment_id = ass.assignment_id
LEFT JOIN tlgnc_order ord
ON assp.order_id = ord.order_id
LEFT JOIN tlgnc_shippingpoint sp
ON ord.shippingpoint_id = sp.shippingpoint_id
LEFT JOIN tlgns_sap_bp bp_shipto
ON ord.shipto_sap_bp_id = bp_shipto.sap_bp_id
-- get language-dependent order status
LEFT JOIN tlgnr_order_status_tx status_tx
ON (ord.order_status_cd = status_tx.order_status_cd AND status_tx.lang_cd = veh_type_tx.lang_cd)
LEFT JOIN tlgnc_order_item item ON item.order_id = ord.order_id
left join tlgns_sap_material_description matdesc on
(matdesc.sap_material_id = item.sap_material_id AND
matdesc.lang_cd = veh_type_tx.lang_cd)
WHERE
-- Only the "unloading" assigment parts are needed
(assp.assignment_part_type_cd is null OR assp.assignment_part_type_cd = 3) -- Unload
AND (item.order_item_no IS NULL -- in case there is no order
OR item.order_item_no    =
  -- First order item is the one with the smallest order_item_no
  (
  SELECT MIN(order_item_no)
  FROM tlgnc_order_item
  WHERE order_id = ord.order_id
  ))
ORDER BY carrier.display_name ASC,
  vehicle.priority ASC,
  vehicle.vehicle_name;


COMMENT ON TABLE VLGNC_SIMPLE_VEHICLE_ASSG IS 'This view returns all simple vehicles with their active assignments (if any). It is used for the top half of the simple scheduling screen.';

REM END VLGNC_SIMPLE_VEHICLE_ASSG

-------------------------------------------------------------------------------

PROMPT VLGNC_SIMPVEH_UNALC_ORD

CREATE OR REPLACE FORCE VIEW VLGNC_SIMPVEH_UNALC_ORD
AS
  SELECT ord.order_id,
    ord.order_number,
    ord.pool_id,
    custord.custorder_number,
    custord.note_dispatching,
    pkg_type_tx.txt package_type,
    matdesc.txt mat_desc,
    item.ordered_quantity,
    ord_uom_tx.txt ordered_uom_txt,
    ord.delivery_from_dat,
    ord.delivery_to_dat,
    soldto_bp.name1 soldto_name,
    shipto_bp.name1 shipto_name,
    carrier.display_name carrier_name,
    matdesc.lang_cd
  FROM tlgnc_order ord
  join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1)
  INNER JOIN tlgnc_custorder custord
  ON ord.custorder_id = custord.custorder_id
  INNER JOIN tlgns_sap_bp soldto_bp
  ON soldto_bp.sap_bp_id = ord.soldto_sap_bp_id
  INNER JOIN tlgns_sap_bp shipto_bp
  ON shipto_bp.sap_bp_id = ord.shipto_sap_bp_id
  INNER JOIN tlgnc_carrier carrier
  ON custord.comm_carrier_id = carrier.carrier_id
  INNER JOIN tlgnc_order_item item
  ON item.order_id = ord.order_id
  INNER JOIN tlgns_sap_material_description matdesc
  ON item.sap_material_id = matdesc.sap_material_id
  inner join tlgns_sap_material material on material.sap_material_id = item.sap_material_id
  inner join tlgnr_material_package_bs mat_pkg on mat_pkg.material_package_cd = material.material_package_cd
  -- TLGNR_MATERIAL_PACKAGE_BS.LOGON_PACKAGE_TYPE is nullable so we need a left join
  left join tlgnr_package_type_tx pkg_type_tx on
    (pkg_type_tx.package_type_cd = mat_pkg.logon_package_type_cd and pkg_type_tx.lang_cd = matdesc.lang_cd)
  INNER JOIN tlgnr_uom_tx ord_uom_tx
  ON (ord_uom_tx.uom_cd     = item.ordered_uom_cd
  AND ord_uom_tx.lang_cd    = matdesc.lang_cd)
  WHERE ord.order_status_cd = 5 -- "Confirmed by comm. Carrier"
  AND ord.delivery_type_cd IN (6, 7) -- type "Ship" or "Block Train"
  AND item.order_item_no    =
    (SELECT MIN(order_item_no)
    FROM tlgnc_order_item
    WHERE order_id = ord.order_id
    )
  ORDER BY custord.delivery_from_dat ASC;
  COMMENT ON TABLE VLGNC_SIMPVEH_UNALC_ORD
IS
  'This view returns all unallocated simple vehicle orders along with the information needed for their display on the simple vehicle scheduling screen.';

REM END VLGNC_SIMPVEH_UNALC_ORD

-------------------------------------------------------------------------------

PROMPT VLGNC_SIMPVEH_ORD_VHC

CREATE OR REPLACE FORCE VIEW VLGNC_SIMPVEH_ORD_VHC
  AS
SELECT ord.order_id
  || '_'
  || simpveh.simple_vehicle_id AS composite_id,
  ord.order_id,
  simpveh.simple_vehicle_id,
  simpveh.vehicle_name,
  simpveh.carrier_id
FROM tlgnc_order ord
join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (ord.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD and archivingstatus.DISPLAY_FLAG = 1)
-- Vehicle's carrier must be executing carrier of the order's comm. carrier
INNER JOIN vlgnc_comm_exec_carrier_pf comm_exec_carrier
ON comm_exec_carrier.comm_carrier_id = ord.comm_carrier_id
INNER JOIN tlgnc_simple_vehicle simpveh
ON simpveh.carrier_id              = comm_exec_carrier.exec_carrier_id
-- Status 5 ("trx to comm carrier") means unallocated, see LOGON-5901
WHERE ord.order_status_cd         = 5
-- Delivery type must be "ship" or "block train"
AND ord.delivery_type_cd          IN (6, 7)
-- Exclude vehicles with active assignments that have status "shipping"
AND simpveh.simple_vehicle_id NOT IN
  (
  -- Must not have active assignment that ...
  SELECT tlgnc_simple_vehicle.simple_vehicle_id
  FROM tlgnc_simple_vehicle
  INNER JOIN tlgnc_assignment
  ON tlgnc_assignment.simple_vehicle_id = tlgnc_simple_vehicle.simple_vehicle_id
  LEFT JOIN tlgnc_assignment_part
  ON tlgnc_assignment.assignment_id = tlgnc_assignment_part.assignment_id
  LEFT JOIN tlgnc_order
  ON tlgnc_assignment_part.order_id = tlgnc_order.order_id
  WHERE tlgnc_assignment.active_flag    = 1
  AND
    (
    tlgnc_assignment.assign_status_cd = 3 -- ... are in status shipping
    OR tlgnc_order.order_status_cd in (7, 8, 9, 10) -- ... contain a released order
    )
  )
-- Exclude vehicles with active assignments with order status released
ORDER BY order_id ASC,
  vehicle_name ASC;

  COMMENT ON TABLE VLGNC_SIMPVEH_ORD_VHC
IS 'Lists for each order ID the simple vehicles to which the order can be allocated and their carrier.';

REM END VLGNC_SIMPVEH_ORD_VHC

-------------------------------------------------------------------------------

PROMPT VLGNC_SIMPVEH_ORD_ITEM_MAT

CREATE OR REPLACE FORCE VIEW VLGNC_SIMPVEH_ORD_ITEM_MAT AS
SELECT
  ord.order_id || '_' || item.order_item_id AS composite_id,
  ord.order_id,
  ord.order_number,
  item.order_item_id,
  item.order_item_no,
  item.loaded_quantity,
  item.loaded_uom_cd,
  des.txt,
  des.lang_cd
FROM tlgnc_order ord
INNER JOIN tlgnc_order_item item on (ord.order_id = item.order_id)
INNER JOIN tlgns_sap_material_description des on (item.sap_material_id = des.sap_material_id)
inner join TLGNS_SAP_MATERIAL mat on (mat.SAP_MATERIAL_ID = item.SAP_MATERIAL_ID)
inner join tlgnr_material_package_bs mat_pkg on mat_pkg.material_package_cd = mat.material_package_cd
where mat_pkg.logon_package_type_cd != 3
ORDER BY item.order_item_no ASC;

  COMMENT ON TABLE VLGNC_SIMPVEH_ORD_ITEM_MAT
IS 'Lists for each order the order items and the related material info.';

REM END VLGNC_SIMPVEH_ORD_ITEM_MAT
-------------------------------------------------------------------------------

PROMPT VLGNC_SIMPVEH_LOADINGPOINT

CREATE OR REPLACE FORCE VIEW VLGNC_SIMPVEH_LOADINGPOINT as
select
item.order_item_id || '_' || lp.loadingpoint_id as simpveh_loadingpoint_id,
ord.order_id,
item.order_item_id simpveh_load_order_item_id,
lp.loadingpoint_id,
lp.loadingpoint_name
from tlgnc_order ord
inner join tlgnc_order_item item on ord.order_id = item.order_id
inner join tlgns_sap_material mat on item.sap_material_id = mat.sap_material_id
inner join tlgnr_material_package_bs matpkg on mat.material_package_cd = matpkg.material_package_cd
inner join tlgnc_shippingpoint sp on sp.shippingpoint_id = ord.shippingpoint_id
inner join tlgnc_loadingpoint lp on (lp.shippingpoint_id = sp.shippingpoint_id and lp.package_type_cd = matpkg.logon_package_type_cd)
order by ord.order_id asc, order_item_no asc, lp.loadingpoint_id asc;

COMMENT ON TABLE VLGNC_SIMPVEH_LOADINGPOINT
IS 'Lists the possible loading points for each order item';

REM END VLGNC_SIMPVEH_LOADINGPOINT

-------------------------------------------------------------------------------

PROMPT VLGNC_SIMPVEH_LOAD_ORDER_ITEM

CREATE OR REPLACE FORCE VIEW VLGNC_SIMPVEH_LOAD_ORDER_ITEM as
select
-- Have this again as a key with its own name, needed
-- not to confuse hibernate
order_item_id simpveh_load_order_item_id,
vehicle.simple_vehicle_id,
ord.order_id,
ord.order_status_cd,
order_number,
order_item_id,
order_item_no,
matdesc.txt material_description,
item.ordered_quantity,
item.ordered_uom_cd,
item.loaded_quantity,
mat.base_uom_cd,
-- If the item hasn't been loaded yet, display the UOM that will be selected
-- as the loaded UOM (for display purposes)
-- else display the actually loaded UOM.
-- See SDS LRQ 108.0, Section 6.4.4.2 "Popup load"
case
  when item.loaded_uom_cd is null then
  case
    -- package type 2 is bag
    when mat_pkg.logon_package_type_cd = 2 then item.ordered_uom_cd -- ordered uom for bag/pallet like materials
    else mat.base_uom_cd -- base uom for bulk materials
  end
  else item.loaded_uom_cd
end as loaded_uom_cd,
lp.loadingpoint_id,
matdesc.lang_cd
from tlgnc_order_item item
inner join tlgnc_order ord on item.order_id = ord.order_id
inner join tlgns_sap_material mat on mat.sap_material_id = item.sap_material_id
inner join tlgnr_material_package_bs mat_pkg on mat_pkg.material_package_cd = mat.material_package_cd
inner join tlgns_sap_material_description matdesc on matdesc.sap_material_id = item.sap_material_id
inner join tlgnc_assignment_part assp on assp.order_id = ord.order_id
inner join tlgnc_assignment ass on ass.assignment_id = assp.assignment_id
inner join tlgnc_simple_vehicle vehicle on vehicle.simple_vehicle_id = ass.simple_vehicle_id
inner join tlgnc_load load on load.load_id = item.load_id
left join tlgnc_loadingpoint lp on lp.loadingpoint_id = load.loadingpoint_id
-- Exclude service materials
where mat_pkg.logon_package_type_cd != 3
order by vehicle.simple_vehicle_id asc, ord.order_number asc, item.order_item_no asc;


COMMENT ON TABLE VLGNC_SIMPVEH_LOAD_ORDER_ITEM
IS 'Lists all loadable order items for orders assigned to simple vehicles';

REM END VLGNC_SIMPVEH_LOAD_ORDER_ITEM

-------------------------------------------------------------------------------

PROMPT VLGNC_SHIPMENTDOC_ASSIGNMENT

  CREATE OR REPLACE FORCE VIEW VLGNC_SHIPMENTDOC_ASSIGNMENT AS
  SELECT
  DISTINCT
    assignment.assignment_id
  FROM
    tlgnc_assignment_part part
  INNER JOIN
    tlgnc_assignment assignment ON assignment.assignment_id = part.assignment_id
  INNER JOIN
    tlgnc_order ordrec ON ordrec.order_id = part.order_id
  INNER JOIN
    tlgnc_order_item item ON ordrec.order_id = item.order_id
  INNER JOIN
    tlgnc_sarea sarea ON ordrec.sarea_id = sarea.sarea_id
  WHERE
    ordrec.release_sap_flag = 1
  AND
    ( ordrec.ITRACK_STATUS_CD IS NULL
  OR
	  ordrec.ITRACK_STATUS_CD = 1
	)
  AND
    ordrec.order_status_cd = 15 -- Delivered
  AND
    ( ordrec.actual_delivery_dat < SYSDATE
  OR
      ordrec.force_to_sap_flag = 1
  OR
      sarea.force_to_sap_flag = 1
    )
  AND
    ordrec.sent_shipmentdoc_to_sap_ts is null -- Not yet sent
  AND
    ordrec.delivery_type_cd NOT IN (5, 11, 12)
  AND
    ( NOT (ordrec.delivery_type_cd = 1
    AND ordrec.sap_contract_id in (
                SELECT sap_contract_id
                FROM tlgns_sap_contract
                WHERE incoterms_cd IN ('EXW', 'FOB'))
    )
   )
    -- Do not send Rail EXW Shipment iDocs to SAP
    AND
       ( NOT (
        -- Rail Direct (single wagon)
        ordrec.delivery_type_cd = 2
        AND
        ordrec.sap_contract_id in (
                  SELECT sap_contract_id
                  FROM tlgns_sap_contract
                  WHERE incoterms_cd = 'EXW')
      )
     )
  AND NOT EXISTS (
      SELECT *
      FROM tlgnc_order
      INNER JOIN tlgnc_assignment_part on tlgnc_order.order_id = tlgnc_assignment_part.order_id
      INNER JOIN tlgnc_order_item ON tlgnc_order.order_id = tlgnc_order_item.order_id
      INNER JOIN tlgnc_sarea ON tlgnc_order.sarea_id = tlgnc_sarea.sarea_id
      WHERE
          tlgnc_assignment_part.assignment_id = part.assignment_id
      AND (
              tlgnc_order.release_sap_flag <> 1
			OR
			  ( tlgnc_order.ITRACK_STATUS_CD IS NOT NULL
			AND
				tlgnc_order.ITRACK_STATUS_CD <> 1
			  )
  		    OR
              tlgnc_order.order_status_cd <> 15 -- Delivered
            OR
              tlgnc_order.actual_delivery_dat >= SYSDATE
            OR
              tlgnc_order.sent_shipmentdoc_to_sap_ts is not null -- Not yet sent
            OR
              tlgnc_order.delivery_type_cd IN (5, 11, 12)
            OR
              (ordrec.delivery_type_cd = 1
               AND ordrec.sap_contract_id in (
                          SELECT sap_contract_id
                          FROM tlgns_sap_contract
                          WHERE incoterms_cd IN ('EXW', 'FOB'))
              )
             -- Do not send Rail EXW Shipment iDocs to SAP
             OR
             -- Rail Direct (single wagon)
              (ordrec.delivery_type_cd = 2
               AND ordrec.sap_contract_id in (
                  SELECT sap_contract_id
                  FROM tlgns_sap_contract
                  WHERE incoterms_cd = 'EXW'))
          )
      AND
          tlgnc_order.order_id <> ordrec.order_id
  );

COMMENT ON TABLE VLGNC_SHIPMENTDOC_ASSIGNMENT
IS 'Lists assignments where which are eligible for shipment processing';

REM END VLGNC_SHIPMENTDOC_ASSIGNMENT
-------------------------------------------------------------------------------

PROMPT VLGNS_SAP_FINANCIAL_DATA

CREATE OR REPLACE FORCE VIEW VLGNS_SAP_FINANCIAL_DATA as
select
soldto.sap_bp_id || '_' || shipto_sap_bp_id  || '_' ||  sarea.sarea_id  || '_' ||
billto.sap_bp_id  || '_' || payer.sap_bp_id
as composite_key,
soldto.sap_id as soldto_id,
shipto.sap_id as shipto_id,
soldto.sap_bp_id as soldto_sap_bp_id,
shipto.sap_bp_id as shipto_sap_bp_id,
sarea.sarea_id,
soldto_sarea.customer_order_block soldto_cust_order_block,
soldto_sarea.customer_delivery_block soldto_cust_delivery_block,
soldto_sarea.customer_posting_block soldto_cust_posting_block,
shipto_sarea.marked_for_deletion as shipto_sarea_marked_for_del,
shipto_sgroup.ctl_act_cd as shipto_sgroup_act_cd,
shipto_soffice.ctl_act_cd as shipto_soffice_act_cd,
billto.sap_bp_id as billto_sap_bp_id,
soldto_cc.posting_block as soldto_cc_posting_block,
payer.sap_bp_id as payer_sap_bp_id
from tlgnc_shipto lshipto
inner join tlgns_sap_bp shipto on (lshipto.shipto_sap_bp_id = shipto.sap_bp_id)
inner join tlgns_sap_bp_sarea shipto_sarea on (shipto.sap_bp_id = shipto_sarea.sap_bp_id)
inner join tlgns_sap_bp_pf_shipto pf_shipto on (shipto_sarea.sap_bp_sarea_id = pf_shipto.shipto_sap_bp_sarea_id)
inner join tlgnc_sarea sarea on (sarea.sarea_id = pf_shipto.sarea_id)
inner join tlgnc_shipto_sarea lshipto_sarea on (lshipto_sarea.shipto_id = lshipto.shipto_id and lshipto_sarea.sarea_id = sarea.sarea_id)
inner join tlgns_sap_bp_sarea soldto_sarea on (pf_shipto.sap_bp_sarea_id = soldto_sarea.sap_bp_sarea_id)
inner join tlgns_sap_bp soldto on (soldto_sarea.sap_bp_id = soldto.sap_bp_id)
-- join the soldto's billto partner BPs
left outer join tlgns_sap_bp_pf_billto pf_billto on (pf_billto.sap_bp_sarea_id = soldto_sarea.sap_bp_sarea_id)
left outer join tlgns_sap_bp_sarea billto_sarea on (billto_sarea.sap_bp_sarea_id = pf_billto.billto_sap_bp_sarea_id)
left outer join tlgns_sap_bp billto on (billto.sap_bp_id = billto_sarea.sap_bp_id)
-- join the soldto's company codes (if any). only join the one given by the sales area
left outer join tlgnr_sorg_bs sorg on (sarea.sorg_cd = sorg.sorg_cd)
left outer join tlgns_sap_bp_companycode soldto_cc on (soldto_cc.sap_bp_id = soldto.sap_bp_id and soldto_cc.companycode_cd = sorg.companycode_cd and soldto_cc.marked_for_deletion = '0')
-- join the soldto's payer partner BPs
left outer join tlgns_sap_bp_pf_payer pf_payer on (pf_payer.sap_bp_sarea_id = soldto_sarea.sap_bp_sarea_id)
left outer join tlgns_sap_bp_sarea payer_sarea on (payer_sarea.sap_bp_sarea_id = pf_payer.payer_sap_bp_sarea_id)
left outer join tlgns_sap_bp payer on (payer.sap_bp_id = payer_sarea.sap_bp_id and payer.bp_is_payer = '1' and payer.marked_for_deletion = '0')
-- join the shipto sarea's sgroup ref table
left outer join tlgnr_sgroup_bs shipto_sgroup on (shipto_sgroup.sgroup_cd = shipto_sarea.sgroup_cd)
-- join the shipto sarea's soffice ref table
left outer join tlgnr_soffice_bs shipto_soffice on (shipto_soffice.soffice_cd = shipto_sarea.soffice_cd);

COMMENT ON TABLE VLGNS_SAP_FINANCIAL_DATA
IS 'Provides payer and billto data for a (soldto, shipto) pair used in the order taking and dispokarte screen';

REM END VLGNS_SAP_FINANCIAL_DATA

-------------------------------------------------------------------------------

prompt vlgnc_order_item_quantity


----------------------------------------------------------------------------------------------------------------|
-- LOGON-6624                                                                                                   |
-- LRQ 303.0 Advance Payment Solution                                                                           |
----------------------------------------------------------------------------------------------------------------|
PROMPT start LOGON-6624 VLGNC_CONTRACT_ITEM_QUANTITY

-- this view excludes DELETED orders (order_status_cd <> 17) and orders which have been transmitted
-- to SAP (sent_salesdoc_to_sap_ts is null)
-- for transmitted orders, the AP GROUP_LEADER's target quantity is adjusted at the time the order is
-- set to finished.
-- for returned orders, the AP GROUP LEADERS's target quantity is adjusted at the time the credit note
-- is approved.
CREATE OR REPLACE FORCE VIEW VLGNC_CONTRACT_ITEM_QUANTITY AS
  SELECT
    ci.sap_contract_item_id || '_' || oi.order_item_id || '_'
      ||  oi.order_item_no  || '_' || ord.order_number as composite_id,
    ci.sap_contract_item_id,
    ord.order_number,
    ord.order_status_cd,
    ord.delivery_type_cd,
    pkg_bs.package_type_cd,
    oi.order_item_id,
    oi.order_item_no,
    oi.delivered_quantity,
    oi.delivered_uom_cd,
    oi.loaded_quantity,
    oi.loaded_uom_cd,
    oi.ordered_quantity,
    oi.ordered_uom_cd,
    oi.ordered_uom_numerator AS o2b_numerator_for_conversion,
    oi.ordered_uom_denominator AS o2b_denominator_for_conversion,
    delivered_uom.fuzzy_quantity_flag AS d_fuzzy_quantity_flag,
    delivered_uom.uom_dimension_cd AS d_uom_dimension_cd,
    delivered_uom.numerator_for_conversion AS d_numerator_for_conversion,
    delivered_uom.denominator_for_conversion AS d_denominator_for_conversion,
    loaded_uom.fuzzy_quantity_flag AS l_fuzzy_quantity_flag,
    loaded_uom.uom_dimension_cd AS l_uom_dimension_cd,
    loaded_uom.numerator_for_conversion AS l_numerator_for_conversion,
    loaded_uom.denominator_for_conversion AS l_denominator_for_conversion,
    ordered_uom.fuzzy_quantity_flag AS o_fuzzy_quantity_flag,
    ordered_uom.uom_dimension_cd AS o_uom_dimension_cd,
    ordered_uom.numerator_for_conversion AS o_numerator_for_conversion,
    ordered_uom.denominator_for_conversion AS o_denominator_for_conversion
  FROM
    tlgns_sap_contract_item ci
  LEFT JOIN tlgnc_order_item oi ON (oi.sap_contract_item_id = ci.sap_contract_item_id)
  LEFT JOIN tlgnc_order ord ON (oi.order_id = ord.order_id)
  LEFT JOIN tlgnr_uom_bs base_uom ON (ci.target_quantity_uom_cd = base_uom.uom_cd)
  LEFT JOIN tlgnr_uom_bs delivered_uom ON (oi.delivered_uom_cd = delivered_uom.uom_cd)
  LEFT JOIN tlgnr_uom_bs loaded_uom ON (oi.loaded_uom_cd = loaded_uom.uom_cd)
  LEFT JOIN tlgnr_uom_bs ordered_uom ON (oi.ordered_uom_cd = ordered_uom.uom_cd)
  LEFT JOIN tlgns_sap_material sap_mat on (oi.sap_material_id = sap_mat.sap_material_id)
  LEFT JOIN tlgnr_material_package_bs pkg on (sap_mat.material_package_cd = pkg.material_package_cd)
  LEFT JOIN tlgnr_package_type_bs pkg_bs on (pkg.logon_package_type_cd = pkg_bs.package_type_cd)
  WHERE
    COALESCE(ord.order_status_cd, 0) <> 17 and COALESCE(ord.order_status_cd, 0) <> 20
    and ord.sent_salesdoc_to_sap_ts is null;

COMMENT ON TABLE VLGNC_CONTRACT_ITEM_QUANTITY IS 'View on contract items created for calculation of the sum quantity of all order items (delivered, loaded, ordered) used by LRQ 303.0 - Advance Payment Solution';

rem end VLGNC_CONTRACT_ITEM_QUANTITY

PROMPT end LOGON-6624 VLGNC_CONTRACT_ITEM_QUANTITY

PROMPT start LOGON-6624 VLGNC_SENT_CONTRACT_ITEM_QTY

-- this view returns the quantities for all valid SAP contract items which are in a quantity restricted group
CREATE OR REPLACE VIEW VLGNC_SENT_CONTRACT_ITEM_QTY AS
	SELECT
		ci2.sap_contract_item_id,
		ci2.contract_item_no,
		ci2.ap_group_leader_id,
		cast(COALESCE(ci2.target_quantity, 0) as decimal(17,3)) AS target_quantity,
		cast(COALESCE(ci2.target_quantity, 0) - total_delivered_quantity as decimal(17,4)) AS remaining_quantity,
		cast(ci2.ctl_cre_ts as date) as contract_item_cre_ts
	FROM (SELECT
					ci.sap_contract_item_id,
					SUM(COALESCE(oici.delivered_quantity, 0)) as total_delivered_quantity
				FROM
					tlgns_sap_contract_item ci
				LEFT JOIN tlgnc_order_item_contract_item oici
              ON (oici.sap_contract_item_id = ci.sap_contract_item_id AND COALESCE(oici.deleted_flag, 0)=0)
        WHERE
          ci.ap_group_leader_id is not null
          and ci.marked_for_deletion != 1
        GROUP BY ci.sap_contract_item_id
        ) totals
  JOIN tlgns_sap_contract_item ci2 ON (ci2.sap_contract_item_id = totals.sap_contract_item_id)
  WHERE
    (COALESCE(ci2.target_quantity, 0) - total_delivered_quantity) > 0;

COMMENT ON TABLE VLGNC_SENT_CONTRACT_ITEM_QTY IS 'View on contract items sent to SAP created for calculation of the sum quantity of all order items (delivered, loaded, ordered) used by LRQ 303.0 - Advance Payment Solution';

rem end VLGNC_SENT_CONTRACT_ITEM_QTY

PROMPT end LOGON-6624 VLGNC_SENT_CONTRACT_ITEM_QTY

PROMPT start LOGON-7170 VLGNC_CONTRACT_ITEM_QTY

CREATE OR REPLACE VIEW VLGNC_CONTRACT_ITEM_QTY AS
	SELECT
		ci2.sap_contract_item_id,
		ci2.contract_item_no,
		ci2.ap_group_leader_id,
		cast(COALESCE(ci2.target_quantity, 0) as decimal(17,3)) AS target_quantity,
		cast(COALESCE(ci2.target_quantity, 0) - total_delivered_quantity as decimal(17,4)) AS remaining_quantity,
		cast(ci2.ctl_cre_ts as date) as contract_item_cre_ts
	FROM (SELECT
					ci.sap_contract_item_id,
					SUM(COALESCE(oici.delivered_quantity, 0)) as total_delivered_quantity
				FROM
					tlgns_sap_contract_item ci
				LEFT JOIN tlgnc_order_item_contract_item oici
              ON (oici.sap_contract_item_id = ci.sap_contract_item_id AND COALESCE(oici.deleted_flag, 0)=0)
        WHERE
          ci.ap_group_leader_id is not null
          and ci.marked_for_deletion != 1
        GROUP BY ci.sap_contract_item_id
        ) totals
  JOIN tlgns_sap_contract_item ci2 ON (ci2.sap_contract_item_id = totals.sap_contract_item_id);

COMMENT ON TABLE VLGNC_CONTRACT_ITEM_QTY IS 'View on contract items created for calculation of the sum quantity of all order items (delivered, loaded, ordered) used by LRQ 303.0 - Advance Payment Solution';

rem end VLGNC_CONTRACT_ITEM_QTY

PROMPT end LOGON-7170 VLGNC_CONTRACT_ITEM_QTY

PROMPT start VLGNC_CONTRACTITEM_OI_LST

CREATE OR REPLACE VIEW VLGNC_CONTRACTITEM_OI_LST AS
select
  oici.order_item_contract_item_id as composite_id,
  sci.sap_contract_item_id,
  oici.delivered_quantity,
  oici.sent_order_item_no,
  oici.deleted_flag,
  oi.order_item_no,
  o.order_number
from TLGNC_ORDER_ITEM_CONTRACT_ITEM oici
inner join tlgns_sap_contract_item sci on oici.sap_contract_item_id = sci.sap_contract_item_id
inner join tlgnc_order_item oi on oici.order_item_id = oi.order_item_id
inner join tlgnc_order o on oi.order_id = o.order_id
order by o.order_number, oi.order_item_no;
COMMENT ON TABLE VLGNC_CONTRACTITEM_OI_LST IS 'View on contract items created for all order items - Advance Payment Solution';

rem end VLGNC_CONTRACTITEM_OI_LST

PROMPT end VLGNC_CONTRACTITEM_OI_LST

----------------------------------------------------------------------------------------------------------------|
-- LOGON-6681                                                                                                   |
-- TimeZone Handling                                                                                            |
----------------------------------------------------------------------------------------------------------------|

PROMPT start LOGON-6681 VLGNC_TIMEZONE

CREATE OR REPLACE VIEW VLGNC_TIMEZONE as
SELECT  tlgnr_timezone_tx.lang_cd || '_' || tlgnr_timezone_bs.sap_country_cd || '_' || tlgnr_timezone_bs.timezone_cd AS COMPOSITE_CD,
        tlgnr_timezone_bs.timezone_cd AS TIMEZONE_CD,
        tlgnr_sap_country_tx.txt AS COUNTRY,
        tlgnr_timezone_tx.txt AS REGION,
        tlgnr_timezone_tx.lang_cd AS LANG_CD
FROM tlgnr_timezone_bs
INNER JOIN tlgnr_timezone_tx ON (tlgnr_timezone_bs.timezone_cd = tlgnr_timezone_tx.timezone_cd)
LEFT JOIN tlgnr_sap_country_tx ON (tlgnr_timezone_bs.sap_country_cd = tlgnr_sap_country_tx.sap_country_cd)
WHERE tlgnr_timezone_tx.lang_cd = tlgnr_sap_country_tx.lang_cd;

rem end VLGNC_TIMEZONE

PROMPT end LOGON-6681 VLGNC_TIMEZONE


----------------------------------------------------------------------------------------------------------------|
-- LOGON-6657                                                                                                   |
-- LRQ 176.0 Updating Container Monitoring View                                                                 |
-- Creating new view VLGNC_CONTAINER_MONITORING                                                                 |
-- Creating new view VLGNC_PROD_PLANT_NAME                                                                      |
----------------------------------------------------------------------------------------------------------------|
prompt *** START LOGON-6657

prompt creating view: VLGNC_CONTAINER_MONITORING

CREATE OR REPLACE VIEW VLGNC_CONTAINER_MONITORING AS
SELECT
    load.load_id ||'_' || uom.lang_cd  as composite_id,
    sp.shippingpoint_name container_terminal,
    sp.shippingpoint_id,
    sp.timezone_cd as shippingpoint_timezone_cd,
    cont.container_name,
    plant.plant_name,
    load.production_plant_cd,
    mat.txt material_name,
    load.sap_material_id,
    load.net_quantity material_quantity,
    uom.txt uom,
    uom.lang_cd,
    load.loading_ts loading_time,
    cont.arrival_last_known_sp_dat arrival_time,
    cast(round(to_number(SYSDATE-cont.arrival_last_known_sp_dat)*1440) AS number(12)) standing_time
FROM tlgnc_load load
LEFT JOIN tlgnc_container cont ON (cont.container_id = load.container_id)
LEFT JOIN tlgnc_shippingpoint sp ON (cont.last_known_shippingpoint_id = sp.shippingpoint_id)
LEFT JOIN tlgnr_plant_bs plant ON (load.production_plant_cd = plant.plant_cd)
LEFT JOIN tlgns_sap_material_description mat ON (mat.sap_material_id = load.sap_material_id)
LEFT JOIN tlgnr_uom_tx uom ON (load.net_quantity_uom_cd = uom.uom_cd AND uom.lang_cd = mat.lang_cd)
WHERE cont.last_known_shippingpoint_id IS NOT NULL AND
      load.loading_ts IS NOT NULL AND
      load.unloading_ts IS NULL AND
      cont.current_truck_id IS NULL AND
      cont.current_trailer_id IS NULL AND
      cont.current_wagon_id IS NULL AND
      sp.shipppoint_type_cd = 2;

prompt end creating view

prompt creating view for getting the Name and CD of Production plants

CREATE OR REPLACE FORCE VIEW VLGNC_PROD_PLANT_NAME AS
SELECT
  plant_cd,
  plant_name
FROM tlgnr_plant_bs
WHERE ctl_act_cd = 1 AND
      production_plant = 1;

COMMENT ON TABLE VLGNC_PROD_PLANT_NAME is 'View for getting the name and CD of active Production Plants - using in Bimodal Railcar optimization and Container Monitoring';

prompt end creating view

prompt *** STOP LOGON-6657

PROMPT VLGNC_RAILWAY_STATIONS

----------------------------------------------------------------------------------------------------------------|
-- LOGON-6557                                                                                                   |
-- LRQ 090.1 Archiving of Deliveries in LOGON                                                                   |
----------------------------------------------------------------------------------------------------------------|
PROMPT start LOGON-6557 VLGNC_ORDER_ARCHIVING

prompt creating view: VLGNC_ARCHIVING_METADATA

CREATE OR REPLACE FORCE VIEW VLGNC_ARCHIVING_METADATA AS
with
first_loading_biz_ts as (
	select oi.order_id, max(load.LOADING_TS) as loading_ts
	from TLGNC_ORDER_ITEM oi
	inner join TLGNC_LOAD load on oi.LOAD_ID = load.LOAD_ID
	group by order_id
),
last_bizevent as (
  select order_id, max(last_bizevent_ts) as last_bizevent_ts
  from  ( select order_id, max(event_ts) as last_bizevent_ts
           from tlgnc_bizevent
           where (order_id is not null)
           group by order_id
          union
          select unload_assignment_part.order_id as order_id, max(bizevent.event_ts) as last_bizevent_ts
           from tlgnc_bizevent bizevent
           inner join tlgnc_assignment_part unload_assignment_part
             on bizevent.assignment_id = unload_assignment_part.assignment_id and
                unload_assignment_part.assignment_part_type_cd = 3
           where (unload_assignment_part.order_id is not null)
           group by unload_assignment_part.order_id
        )
  group by order_id
),
first_doc_cre_ts as (
  select order_id, min(doc.ctl_cre_ts) as doc_ts
  from tlgnc_order_outputdoc ood
  inner join tlgnc_outputdoc doc on (doc.outputdoc_id = ood.pdf_outputdoc_id)
  where order_id is not null
  group by order_id
)
select
  o.order_id,
  doc.doc_ts,
  o.order_status_cd, -- document validity
  sorg.companycode_cd, -- company code
  sarea.sorg_cd, -- sales organization
  sarea.division_cd, -- division
  sarea.distribution_channel, -- distribution channel
  o.order_number, -- logon order number
  o.lds_delivery_note_no, -- delivery note number
  o.order_number_from_seq_usage, -- sequential order number
  co.custorder_number, -- customer order number
  o.shipto.sap_id as shipto_sap_id,
  shipto.name1 as shipto_sap_name,
  o.soldto.sap_id as soldto_sap_id,
  soldto.name1 as soldto_sap_name,
  o.billto.sap_id as billto_sap_id,
  billto.name1 as billto_sap_name,
  o.payer.sap_id as payer_sap_id,
  payer.name1 as payer_sap_name,
  commsap.sap_id as comm_sap_id,
  comm.display_name as comm_display_name,
  execsap.sap_id as exec_sap_id,
  exec.display_name as exec_display_name,
  sap.sap_contract_no,
  sap.sap_contract_type_cd,
  sap.incoterms_cd,
  o.delivery_type_cd,
  o.pool_id,
  pool.name as pool_name,
  o.delivery_from_dat,
  o.delivery_to_dat,
  o.actual_delivery_dat,
  last_bizevent.last_bizevent_ts,
  COALESCE(ob.loading_ts, o.actual_delivery_dat, SYSDATE) as loading_ts,
  sp.sap_shippingpoint_no,
  sp.plant_cd,
  sp.sap_shipping_cond_cd
from tlgnc_order o
inner join last_bizevent on (o.order_id = last_bizevent.order_id)
left outer join first_doc_cre_ts doc on (doc.order_id = o.order_id)
left outer join tlgnc_custorder co on (co.custorder_id = o.custorder_id)
left outer join first_loading_biz_ts ob on (ob.order_id = o.order_id)
left outer join tlgnc_sarea sarea on (sarea.sarea_id = o.sarea_id)
left outer join tlgns_sap_contract sap on (sap.sap_contract_id = o.sap_contract_id)
left outer join tlgnc_shippingpoint sp on (sp.shippingpoint_id = o.shippingpoint_id)
left outer join tlgns_sap_bp billto on (billto.sap_bp_id = o.billto_sap_bp_id)
left outer join tlgns_sap_bp shipto on (shipto.sap_bp_id = o.shipto_sap_bp_id)
left outer join tlgns_sap_bp soldto on (soldto.sap_bp_id = o.soldto_sap_bp_id)
left outer join tlgns_sap_bp payer on (payer.sap_bp_id = o.payer_sap_bp_id)
left outer join tlgnc_carrier comm on (comm.carrier_id = o.comm_carrier_id)
left outer join tlgnc_carrier exec on (exec.carrier_id = o.exec_carrier_id)
left outer join tlgnc_pool_tx pool on (pool.pool_id = o.pool_id)
left outer join tlgns_sap_vendor commsap on (commsap.sap_vendor_id = comm.sap_vendor_id)
left outer join tlgns_sap_vendor execsap on (execsap.sap_vendor_id = exec.sap_vendor_id)
left outer join tlgnr_sorg_bs sorg on (sorg.sorg_cd = sarea.sorg_cd)
join TLGNR_ARCHIVING_STATUS_BS archivingstatus on (o.ARCHIVING_STATUS_CD = archivingstatus.ARCHIVING_STATUS_CD)
where (o.order_status_cd = 16 or o.order_status_cd = 17) and archivingstatus.ARCHIVING_STATUS_CD = 1
and pool.lang_cd = 'en'
order by last_bizevent.last_bizevent_ts;


COMMENT ON TABLE VLGNC_ARCHIVING_METADATA IS 'View on document metadata of archivable orders for use in archiving service';

prompt end creating view

prompt creating view: VLGNC_ARCHIVING_PDF

CREATE OR REPLACE FORCE VIEW VLGNC_ARCHIVING_PDF AS
select
  ord.order_id ||'_' || outdoc.outputdoc_id as composite_id,
  ord.order_id,
  ord.order_number,
  ordout.ctl_cre_ts as doc_ts,
  outdoc.outputdoc_id,
  replace(outdoc.outputdoc_name,' ') as outputdoc_name,
  outdoc.data,
  mime.mime_type
from tlgnc_order ord
inner join tlgnc_order_outputdoc ordout on (ordout.order_id = ord.order_id)
inner join tlgnc_outputdoc outdoc on (outdoc.outputdoc_id = ordout.pdf_outputdoc_id)
inner join tlgnr_mime_type_bs mime on (mime.mime_type_cd = outdoc.data_mime_type_cd)
where
(outdoc.outputdoc_id in (
  select  max(outputdoc.outputdoc_id)
  from tlgnc_outputdoc outputdoc
  inner join tlgnc_order_outputdoc order_outputdoc on outputdoc.outputdoc_id = order_outputdoc.pdf_outputdoc_id
  where order_outputdoc.order_id = ord.order_id
  group by outputdoc.outputdoc_type_cd
) and ord.deliverynote_signature_id is null)
or
( ord.deliverynote_signature_id is not null
and outdoc.outputdoc_id in (
  select  max(outputdoc.outputdoc_id)
  from tlgnc_outputdoc outputdoc
  inner join tlgnc_order_outputdoc order_outputdoc on outputdoc.outputdoc_id = order_outputdoc.pdf_outputdoc_id
  where order_outputdoc.order_id = ord.order_id
  group by outputdoc.outputdoc_type_cd
  union
  select  min(outputdoc.outputdoc_id)
  from tlgnc_outputdoc outputdoc
  inner join tlgnc_order_outputdoc order_outputdoc on outputdoc.outputdoc_id = order_outputdoc.pdf_outputdoc_id
  where order_outputdoc.order_id = ord.order_id
  group by outputdoc.outputdoc_type_cd
));

COMMENT ON TABLE VLGNC_ARCHIVING_PDF IS 'View on PDF files associated with archivable orders for use in archiving service';

prompt end creating view

prompt creating view: VLGNC_ARCHIVING_ORDER_LOG

CREATE OR REPLACE FORCE VIEW VLGNC_ARCHIVING_ORDER_LOG AS
select
  b.bizevent_id,
  b.order_id,
  b.ctl_cre_ts as order_log_ts,
  bt.txt
from tlgnc_bizevent b
inner join tlgnr_bizevent_type_tx bt on (bt.bizevent_type_cd = b.bizevent_type_cd)
where b.order_id is not null
and bt.lang_cd = 'en';

COMMENT ON TABLE VLGNC_ARCHIVING_ORDER_LOG IS 'View on Order Logs of archivable orders for use in archiving service';

prompt end creating view

prompt creating view: VLGNC_DELETABLE_ORDERS

CREATE OR REPLACE FORCE VIEW VLGNC_DELETABLE_ORDERS AS
with
valid_ord as (
  select ord.order_id, ord.custorder_id
   from tlgnc_order ord
   where (ord.order_status_cd = 16 or ord.order_status_cd = 17) and ord.archiving_status_cd = 3
   and ord.order_id not in (
    select o.order_id
    from tlgnc_order o
    inner join tlgns_sap_contract sc on (
      sc.sap_contract_id = o.sap_contract_id)
    where sc.valid_until_dat >= trunc(sysdate)
    and (sc.item_grouping_type = 0 -- Constant.SAP_CONTRACT_ITEM_GROUPING_TYPE_NONE
      or sc.item_grouping_type = 2 -- Constant.SAP_CONTRACT_ITEM_GROUPING_TYPE_QUANTITY_RESTRICTED
      or sc.item_grouping_type = 3 -- Constant.SAP_CONTRACT_ITEM_GROUPING_TYPE_ADVANCE_PAYMENT)
    )
  )
   and ord.order_id not in (
    select o.order_id
     from tlgnc_order o
     inner join tlgnc_order_item oi on (
        oi.order_id = o.order_id)
     inner join tlgnc_credit_note cn on (
        cn.order_item_id = oi.order_item_id)
     where cn.sent_salesdoc_to_sap_ts > trunc(sysdate-365)
  )
),
bimodal (load_id, loaded_from_load_id, order_item_id, order_id, order_status_cd, custorder_id, signature_id, lvl) as (
   (select
       l.load_id,
       l.loaded_from_load_id,
       oi.order_item_id,
       o.order_id,
       o.order_status_cd,
       o.custorder_id,
       o.deliverynote_signature_id as signature_id,
       0 as lvl
    from tlgnc_load l
    inner join tlgnc_order_item oi on (oi.load_id = l.load_id)
    inner join tlgnc_order o on (o.order_id = oi.order_id))
   union all
   (select ln.load_id,
       bn_1.loaded_from_load_id,
       bn_1.order_item_id,
       bn_1.order_id,
       bn_1.order_status_cd,
       bn_1.custorder_id,
       bn_1.signature_id,
       bn_1.lvl
    from tlgnc_load ln
    inner join bimodal bn_1 on (
       ln.loaded_from_load_id = bn_1.load_id))
),
assignment_order as (
  select
    b.order_id,
    ap.assignment_id
  from bimodal b
  left outer join tlgnc_assignment_part ap on (b.load_id = ap.load_id)
  where b.order_id is not null
)
select distinct order_id, custorder_id
   from valid_ord
   where order_id not in (select b1.order_id
              from bimodal b1
              inner join bimodal b2 on (b1.load_id = b2.load_id)
              where b1.order_id in (select order_id from valid_ord)
                and b2.order_id not in (select order_id from valid_ord)
                 )
     and order_id not in (select distinct a1.order_id
                 from assignment_order a1
                inner join assignment_order a2 on (a1.assignment_id = a2.assignment_id)
                where a1.order_id in (select order_id from valid_ord)
                  and a2.order_id not in (select order_id from valid_ord)
               )
     and order_id not in (select ord1.order_id
                 from tlgnc_order ord1
                   inner join tlgnc_order ord2 on (ord1.custorder_id = ord2.custorder_id)
                 where ord1.order_id in ( select order_id from valid_ord)
                   and ord2.order_id not in (select order_id from valid_ord)
                  );

COMMENT ON TABLE VLGNC_DELETABLE_ORDERS IS 'View on document metadata of archivable orders for use in archiving service';

prompt end creating view


PROMPT end LOGON-6557 VLGNC_ORDER_ARCHIVING
-------------------------------------------------------------------------------

CREATE OR REPLACE FORCE VIEW VLGNC_SAPBP_RAILWAY_STATION AS
SELECT SAPBP.SAP_BP_ID AS SAP_BP_ID,
       RAILWAYSTATION.RAILWAY_STATION_NAME AS RAILWAY_STATION_NAME,
       RAILWAYSTATION.RAILWAY_STATION_NO AS RAILWAY_STATION_NO,
       RAILWAYSTATION.UIC_COUNTRY_CD AS UIC_COUNTRY_CD
FROM TLGNS_SAP_BP SAPBP
INNER JOIN TLGNC_RAILWAY_STATION RAILWAYSTATION ON SAPBP.RAILWAY_STATION_ID = RAILWAYSTATION.RAILWAY_STATION_ID;

COMMENT on table VLGNC_SAPBP_RAILWAY_STATION is 'Railway stations connected to sapBp.';

PROMPT END VLGNC_SAPBP_RAILWAY_STATION

-------------------------------------------------------------------------------

PROMPT VLGNR_INCOTERM_DELIV_TYPE

create or replace force view vlgnr_incoterm_deliv_type as
  select
    incoterm_deliv_type_cd,
    sarea_id,
    sap_incoterm_cd,
    delivery_type_cd
  from
    tlgnr_incoterm_deliv_type_bs
  where
    ctl_act_cd = 1;

comment on table vlgnr_incoterm_deliv_type is 'View to search active delivery types per incoterm by sales area';

REM END VLGNR_INCOTERM_DELIV_TYPE

-------------------------------------------------------------------------------
CREATE OR REPLACE FORCE VIEW VLGNC_POOL_COMM_CARRIER AS
SELECT
  PC.POOL_CARRIER_ID,
  C.CARRIER_ID,
  P.POOL_ID,
  P.SAP_TRSP_MODE_CD,
  C.DISPLAY_NAME,
  C.CARRIER_REQUIRES_PLANNING_FLAG
FROM TLGNC_CARRIER C
INNER JOIN TLGNC_POOL_CARRIER PC ON PC.CARRIER_ID=C.CARRIER_ID
INNER JOIN TLGNC_POOL P ON P.POOL_ID = PC.POOL_ID
INNER JOIN TLGNS_SAP_VENDOR VN ON (VN.SAP_VENDOR_ID=C.SAP_VENDOR_ID AND VN.MARKED_FOR_DELETION='0' AND VN.VN_IS_COMM_CARRIER=1)
AND P.CTL_ACT_CD=1;

COMMENT on table VLGNC_POOL_COMM_CARRIER is 'Commercial carriers belonging to active pools.';

PROMPT END VLGNC_POOL_COMM_CARRIER
-------------------------------------------------------------------------------
CREATE OR REPLACE FORCE VIEW VLGNC_COMM_EXEC_CARR_PF_ACTIVE AS
  SELECT
  DISTINCT
    --PURCHASING ORGANIZATION SHOULD NOT BE TAKEN INTO CONSIDERATION AT THE MOMENT, IF THERE IS A CONNECTION BETWEEN THEM IN ANY PURCHORG HERE IT WILL APPEAR AND ONLY ONCE
    (COMM_CARR.CARRIER_ID || '_' || EXEC_CARR.CARRIER_ID ) AS COMPOSITE_ID,
    COMM_CARR.CARRIER_ID AS COMM_CARRIER_ID,
    EXEC_CARR.CARRIER_ID AS EXEC_CARRIER_ID,
    EXEC_CARR.DISPLAY_NAME AS EXEC_CARRIER_DISPLAY_NAME,
    EXEC_CARR.SAP_VENDOR_ID AS EXEC_SAP_VENDOR_ID
  FROM (
    SELECT SAP_VENDOR_PORG_ID, EXEC_SAP_VENDOR_PORG_ID, PURCHASINGORG_CD
    FROM TLGNS_SAP_VN_PF_EXEC_CARRIER
    UNION
    SELECT COMM_SAP_VENDOR_PORG_ID, SAP_VENDOR_PORG_ID, PURCHASINGORG_CD
    FROM TLGNS_SAP_VN_PF_COMM_CARRIER
    WHERE SAP_VENDOR_PORG_ID <> COMM_SAP_VENDOR_PORG_ID
  ) PF
  JOIN TLGNR_PURCHASINGORG_BS PORG_BS ON ( PORG_BS.PURCHASINGORG_CD = PF.PURCHASINGORG_CD AND PORG_BS.CTL_ACT_CD = 1 )
  JOIN TLGNS_SAP_VENDOR_PORG COMM_VENDOR_PORG ON ( COMM_VENDOR_PORG.SAP_VENDOR_PORG_ID = PF.SAP_VENDOR_PORG_ID AND COMM_VENDOR_PORG.PURCHASINGORG_CD=PF.PURCHASINGORG_CD AND COMM_VENDOR_PORG.MARKED_FOR_DELETION  = '0' )
  JOIN TLGNS_SAP_VENDOR_PORG EXEC_VENDOR_PORG ON ( EXEC_VENDOR_PORG.SAP_VENDOR_PORG_ID = PF.EXEC_SAP_VENDOR_PORG_ID AND EXEC_VENDOR_PORG.PURCHASINGORG_CD=PF.PURCHASINGORG_CD AND EXEC_VENDOR_PORG.MARKED_FOR_DELETION  = '0')
  JOIN TLGNS_SAP_VENDOR COMM_VENDOR ON (COMM_VENDOR.SAP_VENDOR_ID = COMM_VENDOR_PORG.SAP_VENDOR_ID AND COMM_VENDOR.VN_IS_COMM_CARRIER = 1 AND COMM_VENDOR.MARKED_FOR_DELETION='0')
  JOIN TLGNS_SAP_VENDOR EXEC_VENDOR ON (EXEC_VENDOR.SAP_VENDOR_ID = EXEC_VENDOR_PORG.SAP_VENDOR_ID AND EXEC_VENDOR.VN_IS_EXEC_CARRIER = 1)
  JOIN TLGNC_CARRIER EXEC_CARR ON (EXEC_CARR.SAP_VENDOR_ID=EXEC_VENDOR_PORG.SAP_VENDOR_ID AND EXEC_CARR.ACTIVE_FLAG=1)
  JOIN TLGNC_CARRIER COMM_CARR ON (COMM_CARR.SAP_VENDOR_ID=COMM_VENDOR_PORG.SAP_VENDOR_ID)
  INNER JOIN (SELECT DISTINCT CARRIER_ID FROM TLGNC_POOL_CARRIER JOIN TLGNC_POOL P ON P.CTL_ACT_CD=1) PC ON PC.CARRIER_ID=COMM_CARR.CARRIER_ID;

COMMENT ON TABLE VLGNC_COMM_EXEC_CARR_PF_ACTIVE IS 'VIEW CONTAINING PAIRS OF COMMERCIAL AND EXECUTING CARRIERS WHICH ARE PARTNER IN AT LEAST ONE PURCHASING ORGANIZATION. ACTIVE, BELONGING TO ACTIVE POOL.';

PROMPT END VLGNC_COMM_EXEC_CARR_PF_ACTIVE

-------------------------------------------------------------------------------
CREATE OR REPLACE FORCE VIEW VLGNS_SAP_MATERIAL_SAREA_INFO
AS
  SELECT
  	material_sales_area.SAP_MATERIAL_SAREA_ID,
    material_sales_area.SAP_MATERIAL_ID,
    material_sales_area.DISTRIBUTION_CHANNEL,
    material_sales_area.MARKED_FOR_DELETION,
    material_sales_area.SORG_CD,
    material.SAP_MATERIAL_NO,
    material_package.LOGON_PACKAGE_TYPE_CD
  FROM tlgns_sap_material_sarea material_sales_area
  INNER JOIN tlgns_sap_material material ON( material.sap_material_id = material_sales_area.sap_material_id)
  INNER JOIN TLGNR_MATERIAL_PACKAGE_BS material_package ON material_package.MATERIAL_PACKAGE_CD=material.MATERIAL_PACKAGE_CD;

COMMENT on table VLGNS_SAP_MATERIAL_SAREA_INFO is 'View containing tlgns_sap_material_sarea data and related sap material data. Used by order creation sources.';

PROMPT END VLGNS_SAP_MATERIAL_SAREA_INFO

-------------------------------------------------------------------------------
CREATE OR REPLACE FORCE VIEW VLGNS_SAP_MAT_SAREA_CONTRACT
AS
select
    (CASE WHEN materialsarea.material_group4=2 THEN pggenericmatsarea.sap_material_id
    WHEN  materialsarea.material_group4=1 THEN divgenericmatsarea.sap_material_id
    ELSE materialsarea.sap_material_id
    END) || '_'||
    item.sap_contract_item_id AS composite_id,
    shiptosarea.sap_bp_id AS shipto_sap_bp_id,
    soldtosarea.sap_bp_id AS soldto_sap_bp_id,
    contract.sap_contract_no,
    contract.sap_contract_id,
    contractbs.contract_hierarchy_level,
    item.is_ap_group_leader,
    item.ap_group_leader_id,
    contract.item_grouping_type,
    item.sap_contract_item_id,
    contract.valid_from_dat,
    materialsarea.sap_material_id AS contract_sap_material_id,
    materialsarea.is_generic_flag,
    materialsarea.material_group4 AS material_group_4,
    material.sap_material_no,
  (CASE WHEN materialsarea.material_group4=2 THEN pggenericmatsarea.sap_material_id
    WHEN  materialsarea.material_group4=1 THEN divgenericmatsarea.sap_material_id
    ELSE materialsarea.sap_material_id
  END) as sap_material_id,
  (CASE WHEN materialsarea.material_group4=2 THEN pgpackbs.logon_package_type_cd
    WHEN  materialsarea.material_group4=1 THEN divpackbs.logon_package_type_cd
    ELSE matpackbs.logon_package_type_cd
  END) as logon_package_type_cd
  from tlgns_sap_contract contract
  join tlgnr_sap_contract_type_bs contractbs on contractbs.sap_contract_type_cd=contract.sap_contract_type_cd
  join tlgns_sap_bp_sarea shiptosarea on shiptosarea.sarea_id=contract.sarea_id and shiptosarea.sap_bp_id=contract.shipto_sap_bp_id
  join tlgns_sap_bp_sarea soldtosarea on soldtosarea.sarea_id=contract.sarea_id and soldtosarea.sap_bp_id=contract.soldto_sap_bp_id
  join tlgnc_sarea sarea on sarea.sarea_id=contract.sarea_id
  join tlgns_sap_contract_item item on item.sap_contract_id=contract.sap_contract_id and (item.marked_for_deletion = '0' or item.marked_for_deletion is null) --currently there are marked_for_deletion null entries, yet unclear why exactly tracked in LOGON-8588
  join tlgns_sap_material material on material.sap_material_id=item.sap_material_id and material.marked_for_deletion = '0'
  join tlgnr_material_package_bs matpackbs on matpackbs.material_package_cd=material.material_package_cd
  join tlgns_sap_material_sarea materialsarea on (materialsarea.sap_material_id=item.sap_material_id and materialsarea.distribution_channel=sarea.distribution_channel and materialsarea.sorg_cd=sarea.sorg_cd and materialsarea.marked_for_deletion = '0') --all materials within the contract and sales area main attributes
  left outer join (
       tlgns_sap_material_sarea pggenericmatsarea
       join tlgns_sap_material genericmaterialbypg on genericmaterialbypg.sap_material_id=pggenericmatsarea.sap_material_id and genericmaterialbypg.marked_for_deletion = '0'
       join tlgnr_material_package_bs pgpackbs on pgpackbs.material_package_cd=genericmaterialbypg.material_package_cd)
    on materialsarea.is_generic_flag=1 and materialsarea.material_group4=2 and pggenericmatsarea.sorg_cd=materialsarea.sorg_cd and pggenericmatsarea.distribution_channel=materialsarea.distribution_channel and pggenericmatsarea.is_generic_flag=0 and pggenericmatsarea.pricing_group=materialsarea.pricing_group
  left outer join (
       tlgns_sap_material_sarea divgenericmatsarea
       join tlgns_sap_material genericmaterialbydiv on genericmaterialbydiv.sap_material_id=divgenericmatsarea.sap_material_id and genericmaterialbydiv.marked_for_deletion = '0'
       join tlgnr_material_package_bs divpackbs on divpackbs.material_package_cd=genericmaterialbydiv.material_package_cd)
    on materialsarea.is_generic_flag=1 and materialsarea.material_group4=1 and divgenericmatsarea.sorg_cd=materialsarea.sorg_cd and divgenericmatsarea.distribution_channel=materialsarea.distribution_channel  and divgenericmatsarea.is_generic_flag=0 and genericmaterialbydiv.division_cd=material.division_cd;

COMMENT on table VLGNS_SAP_MAT_SAREA_CONTRACT is 'View containing materials for shipto+soldto+sarea parameter including generic material resolution.';

PROMPT END VLGNS_SAP_MAT_SAREA_CONTRACT
-------------------------------------------------------------------------------

PROMPT VLGNC_TP_SAP_VENDOR_SEARCH

CREATE OR REPLACE FORCE VIEW VLGNC_TP_SAP_VENDOR_SEARCH AS
  SELECT
	T.TP_SAP_VENDOR_ID,
	T.SAP_VENDOR_ID,
	T.SAP_TP_SAP_VENDOR_PF_CD,
	V.SAP_IFC_ID,
	V.NAME1
FROM
	TLGNC_TP_SAP_VENDOR T
JOIN TLGNS_SAP_VENDOR V on T.SAP_VENDOR_ID = V.SAP_VENDOR_ID
JOIN tlgnr_sap_tp_sap_vendor_pf_bs R on t.sap_tp_sap_vendor_pf_cd = r.sap_tp_sap_vendor_pf_cd
WHERE
	T.ACTIVE_FLAG = 1
AND
	V.MARKED_FOR_DELETION = '0'
AND
  	R.CTL_ACT_CD = 1;

COMMENT ON TABLE VLGNC_TP_SAP_VENDOR_SEARCH IS 'View for searching among TLGNC_TP_SAP_VENDOR entries by SAP Vendor Number and Vendor Partner Function codes';

PROMPT END VLGNC_TP_SAP_VENDOR_SEARCH

-------------------------------------------------------------------------------

PROMPT VLGNC_TP_SAP_VENDOR_SEARCH

CREATE OR REPLACE FORCE VIEW VLGNC_TP_SAP_VENDOR_SEARCH AS
SELECT
	T.TP_SAP_VENDOR_ID,
	T.SAP_VENDOR_ID,
	T.SAP_TP_SAP_VENDOR_PF_CD,
	V.SAP_IFC_ID,
	V.NAME1
FROM
	TLGNC_TP_SAP_VENDOR T
JOIN TLGNS_SAP_VENDOR V on T.SAP_VENDOR_ID = V.SAP_VENDOR_ID
WHERE
	T.ACTIVE_FLAG = 1
AND
	V.MARKED_FOR_DELETION = 0;

COMMENT ON TABLE VLGNC_TP_SAP_VENDOR_SEARCH IS 'View for searching among TLGNC_TP_SAP_VENDOR entries by SAP Vendor Number and Vendor Partner Function codes';

PROMPT END VLGNC_TP_SAP_VENDOR_SEARCH

------------------------------------------------------------------------------

PROMPT VLGNC_ORDER_SHIPP_NOTIFICATION

create or replace view vlgnc_order_shipp_notification as
select o.ORDER_ID,
       o.DELIVERY_TYPE_CD,
       o.DISTRIBUTION_DEST_CD,
       o.ORDER_STATUS_CD,
       to_date(to_char(ap.PLANNED_START,'dd-mon-yyyy') || '00:00:00', 'dd-mon-yyyyhh24:mi:ss') PLANNED_START,
       (st.OPEN2_TO- NVL(o.TRANSPORT_DURATION_MIN,0) + 60) as PREV_LATE_DELIVERY,
       carrier.TRANSMIT_TO_CARRIER_FLAG
FROM TLGNC_ORDER o
inner join TLGNC_SHIPTO st on o.SHIPTO_SAP_BP_ID = st.SHIPTO_SAP_BP_ID
inner join TLGNC_CARRIER carrier on o.COMM_CARRIER_ID = carrier.CARRIER_ID
inner join tlgnc_assignment_part ap
on o.order_id= ap.ORDER_ID
and o.DELIVERY_FROM_DAT - nvl(o.TRANSPORT_DURATION_MIN/1440, 0) < CURRENT_TIMESTAMP
and o.DELIVERY_TO_DAT > CURRENT_TIMESTAMP
and (o.LOAD_CONF_EMAIL_FLAG = 1 or o.LOAD_CONF_FAX_FLAG = 1 or o.LOAD_CONF_SMS_FLAG = 1)
and o.LOAD_NOTIFICATION_SENT_TS is null
and ap.ASSIGNMENT_PART_TYPE_CD = 3
and o.DISTRIBUTION_DEST_CD in ('OUT','IUP','IUD','INB')
and o.ORDER_STATUS_CD in (12,13,14,15,16,18)
and o.DELIVERY_TYPE_CD in (1,2,4,5);

COMMENT ON TABLE VLGNC_ORDER_SHIPP_NOTIFICATION IS 'View for Cronjob to send out order shipping notification';

PROMPT END VLGNC_ORDER_SHIPP_NOTIFICATION

------------------------------------------------------------------------------

PROMPT VLGNR_USER_TYPE

CREATE OR replace VIEW VLGNR_USER_TYPE AS
SELECT
	bs.user_type_cd,
	bs.function_id,
	tx.lang_cd,
	tx.txt
FROM TLGNR_USER_TYPE_BS bs
INNER join TLGNR_USER_TYPE_TX tx on bs.user_type_cd = tx.user_type_cd;

COMMENT ON TABLE VLGNR_USER_TYPE IS 'View for dropdown in admin tab to create new user';

PROMPT END VLGNR_USER_TYPE

------------------------------------------------------------------------------

PROMPT VLGNC_OUTPUTDOC_ACCESS

CREATE OR replace VIEW VLGNC_OUTPUTDOC_ACCESS AS
SELECT
    o.OUTPUTDOC_ID,
    ord.order_id,
    NULL AS custorder_id,
    o.OUTPUTDOC_NAME,
    o.OUTPUTDOC_TYPE_CD,
    o.DATA_MIME_TYPE_CD,
    ord.SHIPTO_SAP_BP_ID,
    ord.SOLDTO_SAP_BP_ID,
    ord.SAREA_ID,
    s.shippingpoint_id,
	ord.pool_id,
    shipto_sarea.SGROUP_CD,
    s.plant_cd,
    sorgbs.COMPANYCODE_CD,
    ccarrier.SAP_VENDOR_ID as comm_sap_vendor_id,
    ecarrier.SAP_VENDOR_ID as exec_sap_vendor_id
FROM ulgn01.tlgnc_outputdoc o
JOIN ulgn01.tlgnc_order_outputdoc od
ON  od.PDF_OUTPUTDOC_ID=o.OUTPUTDOC_ID
 OR od.PS_OUTPUTDOC_ID =o.OUTPUTDOC_ID
JOIN ulgn01.TLGNC_ORDER ord
ON  ord.order_id=od.order_id
LEFT OUTER JOIN (ulgn01.TLGNC_SHIPPINGPOINT s
JOIN ulgn01.TLGNR_PLANT_BS p
ON (p.PLANT_CD        =s.PLANT_CD) )
ON (s.SHIPPINGPOINT_ID=ord.SHIPPINGPOINT_ID)
LEFT OUTER JOIN TLGNC_SAREA sa
ON (ord.SAREA_ID=sa.sarea_id)
LEFT OUTER JOIN TLGNR_SORG_BS sorgbs
ON (sa.sorg_cd = sorgbs.sorg_cd)
LEFT JOIN TLGNC_CARRIER ccarrier
ON (ccarrier.CARRIER_ID=ord.COMM_CARRIER_ID)
LEFT JOIN TLGNC_CARRIER ecarrier
ON (ecarrier.CARRIER_ID=ord.EXEC_CARRIER_ID)
LEFT OUTER JOIN TLGNS_SAP_BP_SAREA shipto_sarea
ON (shipto_sarea.SAREA_ID =ord.SAREA_ID
AND shipto_sarea.sap_bp_id=ord.shipto_sap_bp_id)
UNION
SELECT
    o.OUTPUTDOC_ID,
    NULL AS order_id,
    c.custorder_id,
    o.OUTPUTDOC_NAME,
    o.OUTPUTDOC_TYPE_CD,
    o.DATA_MIME_TYPE_CD,
    c.SHIPTO_SAP_BP_ID,
    c.SOLDTO_SAP_BP_ID,
    c.SAREA_ID,
    s.shippingpoint_id,
	c.pool_id,
    shipto_sarea.SGROUP_CD,
    s.plant_cd,
    sorgbs.COMPANYCODE_CD,
    ccarrier.SAP_VENDOR_ID as comm_sap_vendor_id,
    ecarrier.SAP_VENDOR_ID as exec_sap_vendor_id
FROM ulgn01.tlgnc_outputdoc o
JOIN ulgn01.TLGNC_CUSTORDER_OUTPUTDOC cd
ON  cd.PDF_OUTPUTDOC_ID =o.OUTPUTDOC_ID
 OR cd.PS_OUTPUTDOC_ID  =o.OUTPUTDOC_ID
JOIN ulgn01.TLGNC_CUSTORDER c
ON  c.custorder_id=cd.custorder_id
LEFT OUTER JOIN (ulgn01.TLGNC_SHIPPINGPOINT s
JOIN ulgn01.TLGNR_PLANT_BS p
ON (p.PLANT_CD        =s.PLANT_CD) )
ON (s.SHIPPINGPOINT_ID=c.SHIPPINGPOINT_ID)
LEFT OUTER JOIN TLGNC_SAREA sa
ON (c.SAREA_ID=sa.sarea_id)
LEFT OUTER JOIN TLGNR_SORG_BS sorgbs
ON (sa.sorg_cd = sorgbs.sorg_cd)
LEFT JOIN TLGNC_CARRIER ccarrier
ON (ccarrier.CARRIER_ID=c.COMM_CARRIER_ID)
LEFT JOIN TLGNC_CARRIER ecarrier
ON (ecarrier.CARRIER_ID=c.EXEC_CARRIER_ID)
LEFT OUTER JOIN TLGNS_SAP_BP_SAREA shipto_sarea
ON (shipto_sarea.SAREA_ID =c.SAREA_ID
AND shipto_sarea.sap_bp_id=c.shipto_sap_bp_id);

COMMENT ON TABLE VLGNC_OUTPUTDOC_ACCESS is 'View containing authorization fields for accessing outputdoc rows. The view gets the authorization values from the order or the customer order belonging to the outputdoc.';

PROMPT END VLGNC_OUTPUTDOC_ACCESS

------------------------------------------------------------------------------

PROMPT VLGNC_DEMAND_PLAN_PLAN

CREATE OR REPLACE VIEW VLGNC_DEMAND_PLAN_PLAN AS
SELECT
	demplan.DEMAND_PLAN_ID,
	shipto.SHIPTO_SAP_BP_ID,
	demplan.SAP_MATERIAL_ID,
	demplan.PLANNED_WEIGHT_T,
	demplan.START_DATE,
	demplan.END_DATE
FROM ulgn01.TLGNC_DEMAND_PLAN demplan
JOIN ulgn01.TLGNC_SHIPTO shipto ON demplan.SHIPTO_ID = shipto.SHIPTO_ID
WHERE
	-- A shipto can be associated to multiple SAreas and if at least one of them has the flag enabled, we show that shipto
	demplan.SHIPTO_ID IN (
		SELECT shiptoSArea.SHIPTO_ID
		FROM ulgn01.TLGNC_SAREA sArea
		JOIN ulgn01.TLGNC_SHIPTO_SAREA shiptoSArea ON sArea.SAREA_ID = shiptoSArea.SAREA_ID
		WHERE sArea.ENABLE_DEMAND_PLANNING_FLAG = 1
	) AND
	-- A material can be associated to multiple SAreas and if at least one of them has the flag enabled, we show that material
	demplan.SAP_MATERIAL_ID IN (
		SELECT sapMatSArea.SAP_MATERIAL_ID
		FROM ulgn01.TLGNC_SAREA sArea
		JOIN ulgn01.TLGNS_SAP_MATERIAL_SAREA sapMatSArea ON
			sArea.SORG_CD = sapMatSArea.SORG_CD AND
			sArea.DISTRIBUTION_CHANNEL = sapMatSArea.DISTRIBUTION_CHANNEL
		WHERE sArea.ENABLE_DEMAND_PLANNING_FLAG = 1
	)
ORDER BY demplan.CTL_CRE_TS;

PROMPT END VLGNC_DEMAND_PLAN_PLAN

------------------------------------------------------------------------------

PROMPT VLGNC_DEMAND_PLAN_MAT

CREATE OR REPLACE VIEW VLGNC_DEMAND_PLAN_MAT AS
SELECT
	conitem.SAP_CONTRACT_ITEM_ID SAP_CONTRACT_ITEM_ID,
	con.SHIPTO_SAP_BP_ID SHIPTO_SAP_BP_ID,
	conitem.SAP_MATERIAL_ID,
	sarea.DEMAND_PLAN_LATEST_UNTIL_H LATEST_UNTIL_H,
	con.VALID_FROM_DAT,
	con.VALID_UNTIL_DAT,
	NVL(rest.available, 0) AVAILABLE,
	CASE
		WHEN matplant.PLANT_CD = 'K101' THEN 'Rohoznik'
		WHEN matplant.PLANT_CD = 'K102' THEN 'Turna'
		WHEN matplant.PLANT_CD = 'H133' THEN 'Királyegyháza'
		ELSE matplant.PLANT_CD
	END PLANT_NAME
FROM TLGNS_SAP_CONTRACT con
JOIN TLGNS_SAP_CONTRACT_ITEM conitem ON con.SAP_CONTRACT_ID = conitem.SAP_CONTRACT_ID
JOIN TLGNS_SAP_MATERIAL mat ON conitem.SAP_MATERIAL_ID = mat.SAP_MATERIAL_ID
JOIN TLGNC_SAREA sarea ON con.SAREA_ID = sarea.SAREA_ID
JOIN TLGNS_SAP_MATERIAL_SAREA matsarea ON (
	conitem.SAP_MATERIAL_ID = matsarea.SAP_MATERIAL_ID AND
	sarea.DISTRIBUTION_CHANNEL = matsarea.DISTRIBUTION_CHANNEL AND
	sarea.SORG_CD = matsarea.SORG_CD AND
	matsarea.MARKED_FOR_DELETION = '0')
JOIN (
	SELECT DISTINCT
		sapmatplant.sap_material_id,
		MIN(sapmatplant.plant_cd) KEEP (DENSE_RANK FIRST ORDER BY matplant.default_prod_plant_flag NULLS LAST, sapmatplant.plant_cd) OVER (PARTITION BY sapmatplant.sap_material_id) plant_cd
	FROM TLGNS_SAP_MATERIAL_PLANT sapmatplant
	LEFT JOIN TLGNC_MAT_PLANT matplant on
		sapmatplant.PLANT_CD = matplant.plant_cd AND
		sapmatplant.SAP_MATERIAL_ID = matplant.SAP_MATERIAL_ID AND
		matplant.default_prod_plant_flag = 1
	WHERE
		sapmatplant.PLANT_CD IN ('K101', 'K102', 'H133') AND
		sapmatplant.MARKED_FOR_DELETION = 0
	) matplant ON conitem.SAP_MATERIAL_ID = matplant.SAP_MATERIAL_ID
LEFT JOIN (
	SELECT
		SAP_CONTRACT_ITEM_ID,
		1 available
	FROM TLGNC_SHIPTO_SAREA_RESTR
	WHERE
		ENABLED_FLAG = 1 AND
		AVAILABLE_IN_CUST_WEB_FLAG = 1
	GROUP BY SAP_CONTRACT_ITEM_ID
	) rest ON conitem.SAP_CONTRACT_ITEM_ID = rest.SAP_CONTRACT_ITEM_ID
WHERE
	sarea.ENABLE_DEMAND_PLANNING_FLAG = 1 AND
	matsarea.IS_GENERIC_FLAG = 0 AND
	mat.material_package_cd = 'S001';

PROMPT END VLGNC_DEMAND_PLAN_MAT

------------------------------------------------------------------------------

PROMPT VLGNC_DEMAND_PLAN_SHIP_SOLD

CREATE OR REPLACE VIEW VLGNC_DEMAND_PLAN_SHIP_SOLD AS
SELECT
	shiptobp.SAP_BP_ID || '_' || soldtobp.SAP_BP_ID COMPOUND_ID,
	shiptobp.SAP_BP_ID SHIPTO_SAP_BP_ID,
	shiptobp.SAP_ID SHIPTO_SAP_ID,
	shiptobp.NAME1 SHIPTO_NAME1,
	shiptobp.NAME2 SHIPTO_NAME2,
	shipto.SHIPTO_ID SHIPTO_ID,
	soldtobp.SAP_BP_ID SOLDTO_SAP_BP_ID,
	soldtobp.SAP_ID SOLDTO_SAP_ID,
	soldtobp.NAME1 SOLDTO_NAME1,
	soldtobp.NAME2 SOLDTO_NAME2
FROM TLGNS_SAP_BP shiptobp
JOIN TLGNC_SHIPTO shipto ON shiptobp.SAP_BP_ID = shipto.SHIPTO_SAP_BP_ID
JOIN TLGNS_SAP_BP_SAREA shiptobp_sarea ON shiptobp_sarea.SAP_BP_ID = shiptobp.SAP_BP_ID
JOIN TLGNS_SAP_BP_PF_SHIPTO pf_soldtobp_shiptobp ON pf_soldtobp_shiptobp.SHIPTO_SAP_BP_SAREA_ID = shiptobp_sarea.SAP_BP_SAREA_ID
JOIN TLGNS_SAP_BP_SAREA soldtobp_sarea on pf_soldtobp_shiptobp.SAP_BP_SAREA_ID = soldtobp_sarea.SAP_BP_SAREA_ID
JOIN TLGNS_SAP_BP soldtobp ON soldtobp_sarea.SAP_BP_ID = soldtobp.SAP_BP_ID
WHERE
	shiptobp.BP_IS_SHIPTO = 1 AND
	soldtobp.BP_IS_SOLDTO = 1 AND
	shiptobp.MARKED_FOR_DELETION = 0 AND
	soldtobp.MARKED_FOR_DELETION = 0 AND
	shiptobp_sarea.MARKED_FOR_DELETION = 0 AND
	soldtobp_sarea.MARKED_FOR_DELETION = 0 AND
	-- A shipto can be associated to multiple SAreas and if at least one of them has the flag enabled, we show that shipto
	shipto.SHIPTO_ID IN (
		SELECT shiptoSArea.SHIPTO_ID
		FROM ulgn01.TLGNC_SAREA sArea
		JOIN ulgn01.TLGNC_SHIPTO_SAREA shiptoSArea ON sArea.SAREA_ID = shiptoSArea.SAREA_ID
		WHERE sArea.ENABLE_DEMAND_PLANNING_FLAG = 1
	) AND
	-- Only display shiptos that have material
	shiptobp.SAP_BP_ID IN (SELECT SHIPTO_SAP_BP_ID FROM VLGNC_DEMAND_PLAN_MAT);

PROMPT END VLGNC_DEMAND_PLAN_SHIP_SOLD

------------------------------------------------------------------------------

PROMPT VLGNC_DEMAND_PLAN_DIFF

CREATE OR REPLACE VIEW VLGNC_DEMAND_PLAN_DIFF AS
SELECT
	dp.DEMAND_PLAN_ID || '_' || oi.ORDER_ITEM_ID COMP_ID,
	NVL(dp.START_DATE, oi.START_DATE) START_DATE,
	NVL(dp.END_DATE, oi.START_DATE) END_DATE,
	NVL(shipto.SHIPTO_SAP_BP_ID, oi.SHIPTO_SAP_BP_ID) SHIPTO_SAP_BP_ID,
	NVL(dp.SAP_MATERIAL_ID, oi.SAP_MATERIAL_ID) SAP_MATERIAL_ID,
	dp.PLANNED_WEIGHT_T PLANNED_QTY,
	oi.quantity DELIVERY_QTY
FROM TLGNC_DEMAND_PLAN dp
JOIN TLGNC_SHIPTO shipto ON dp.SHIPTO_ID = shipto.SHIPTO_ID
FULL OUTER JOIN (
	SELECT
		MIN(ORDER_ITEM_ID) ORDER_ITEM_ID,
		SHIPTO_SAP_BP_ID,
		SAP_MATERIAL_ID,
		START_DATE,
		SUM(QUANTITY) QUANTITY
	FROM (
			SELECT
				oi.ORDER_ITEM_ID,
				o.SHIPTO_SAP_BP_ID,
				oi.SAP_MATERIAL_ID,
				TRUNC(o.ACTUAL_DELIVERY_DAT, 'IW') START_DATE,
				oi.DELIVERED_QUANTITY QUANTITY
			FROM TLGNC_ORDER_ITEM oi
			JOIN TLGNC_ORDER o ON oi.ORDER_ID = o.ORDER_ID
			WHERE
				oi.DELIVERED_UOM_CD = 'TO' AND
				o.ORDER_STATUS_CD IN (15, 16, 21)
	)
	GROUP BY SHIPTO_SAP_BP_ID, SAP_MATERIAL_ID, START_DATE
) oi ON
	shipto.SHIPTO_SAP_BP_ID = oi.SHIPTO_SAP_BP_ID AND
	dp.SAP_MATERIAL_ID = oi.SAP_MATERIAL_ID AND
	dp.START_DATE <= oi.START_DATE AND
	dp.END_DATE >= oi.START_DATE;

PROMPT END VLGNC_DEMAND_PLAN_DIFF

------------------------------------------------------------------------------

PROMPT VLGNC_UNLOADS_FOR_TARGET
CREATE OR REPLACE FORCE VIEW VLGNC_UNLOADS_FOR_TARGET AS
SELECT
	ord.ORDER_ID,
	truck.TRUCK_PLATE_NO,
	load.LOADING_TS,
	ord.ITRACK_STATUS_CD
FROM TLGNC_ORDER ord
		 JOIN TLGNC_ASSIGNMENT_PART apart on apart.ORDER_ID = ord.ORDER_ID
		 JOIN TLGNC_ASSIGNMENT ass on ass.ASSIGNMENT_ID = apart.ASSIGNMENT_ID
		 JOIN TLGNC_TRUCK truck on truck.TRUCK_ID = ass.TRUCK_ID
		 JOIN TLGNC_ORDER_ITEM orditem on orditem.ORDER_ID = ord.ORDER_ID
		 JOIN TLGNC_LOAD load on load.LOAD_ID = orditem.LOAD_ID
WHERE ord.ITRACK_AVAILABLE_FLAG = 1
  AND ((ord.ORDER_STATUS_CD = 12 AND (ord.ITRACK_STATUS_CD IS NULL OR ord.ITRACK_STATUS_CD = 4))
  OR (ord.ORDER_STATUS_CD = 15 AND ord.ITRACK_STATUS_CD IS NOT NULL AND ord.ITRACK_STATUS_CD NOT IN (1,2)));

comment on table VLGNC_UNLOADS_FOR_TARGET is 'View for gathering iTrack available orders ';

PROMPT END VLGNC_UNLOADS_FOR_TARGET
------------------------------------------------------------------------------

PROMPT VLGNC_ITRACK_SHIPTO_SAP_BP

CREATE OR REPLACE VIEW VLGNC_ITRACK_SHIPTO_SAP_BP AS
SELECT
		shiptobp.SAP_BP_ID SHIPTO_SAP_BP_ID,
		shiptobp.SAP_ID SHIPTO_SAP_ID,
		shiptobp.NAME1 SHIPTO_NAME1,
		shiptobp.NAME2 SHIPTO_NAME2,
		shipto.SHIPTO_ID SHIPTO_ID
FROM TLGNS_SAP_BP shiptobp
		 JOIN TLGNC_SHIPTO shipto ON shiptobp.SAP_BP_ID = shipto.SHIPTO_SAP_BP_ID
		 JOIN TLGNS_SAP_BP_SAREA shiptobp_sarea ON shiptobp_sarea.SAP_BP_ID = shiptobp.SAP_BP_ID
WHERE
		shiptobp.BP_IS_SHIPTO = 1 AND
		shiptobp.MARKED_FOR_DELETION = 0 AND
		shiptobp_sarea.MARKED_FOR_DELETION = 0 AND
  -- A shipto can be associated to multiple SAreas and if at least one of them has the flag enabled, we show that shipto
		shipto.SHIPTO_ID IN (
		SELECT shiptoSArea.SHIPTO_ID
		FROM ulgn01.TLGNC_SAREA sArea
				 JOIN ulgn01.TLGNC_SHIPTO_SAREA shiptoSArea ON sArea.SAREA_ID = shiptoSArea.SAREA_ID
		WHERE sArea.ITRACK_available_FLAG = 1
	);


COMMENT ON TABLE VLGNC_ITRACK_SHIPTO_SAP_BP is 'Contains shipto partners, used on itrack Reporting screen shipto dropdown.';

PROMPT END VLGNC_ITRACK_SHIPTO_SAP_BP


------------------------------------------------------------------------------

PROMPT VLGNC_ITRACK_SOLDTO_SAP_BP

CREATE OR REPLACE VIEW VLGNC_ITRACK_SOLDTO_SAP_BP AS
SELECT
		soldtobp.SAP_BP_ID SOLDTO_SAP_BP_ID,
		soldtobp.SAP_ID SOLDTO_SAP_ID,
		soldtobp.NAME1 SOLDTO_NAME1,
		soldtobp.NAME2 SOLDTO_NAME2
FROM TLGNS_SAP_BP soldtobp
		 JOIN TLGNS_SAP_BP_SAREA soldtobp_sarea on soldtobp_sarea.sap_bp_id = soldtobp.SAP_BP_ID
WHERE
		soldtobp.BP_IS_SOLDTO = 1 AND
		soldtobp.MARKED_FOR_DELETION = 0 AND
		soldtobp_sarea.MARKED_FOR_DELETION = 0 AND
  -- A shipto can be associated to multiple SAreas and if at least one of them has the flag enabled, we show that shipto
		soldtobp_sarea.sarea_id IN (
		SELECT sArea.sarea_id
		FROM ulgn01.TLGNC_SAREA sArea
		WHERE sArea.ITRACK_available_FLAG = 1
	);

COMMENT ON TABLE VLGNC_ITRACK_SOLDTO_SAP_BP is 'Contains soldto partners, used on itrack Reporting screen soldto dropdown.';

PROMPT END VLGNC_ITRACK_SOLDTO_SAP_BP

------------------------------------------------------------------------------

PROMPT VLGNC_ITRACK_REPORT

CREATE OR REPLACE VIEW VLGNC_ITRACK_REPORT AS
SELECT
	o.order_number                                AS order_number,
	sarea.sarea_id								  AS sarea_id,
	sarea.SAREA_DISPLAY_LONGNAME				  AS sarea_SAREA_DISPLAY_LONGNAME,
	o.soldto_sap_bp_id							  AS soldto_sap_bp_id,
	ag.sap_id                                     AS soldto_sap_id,
	ag.name1                                      AS soldto_name1,
	o.shipto_sap_bp_id							  AS shipto_sap_bp_id,
	we.sap_id                                     AS shipto_sap_id,
	we.name1                                      AS shipto_name1,
	plant.plant_cd								  AS sp_plant_cd,
	plant.plant_name                              AS sp_plant_name,
	sp.shippingpoint_id                           AS sp_shippingpoint_id,
	sp.sap_shippingpoint_no                       AS sp_sap_shippingpoint_no,
	sp.shippingpoint_name                         AS sp_shippingpoint_name,
	o.comm_carrier_id							  AS comm_carrier_id,
	commvend.sap_id	                           	  AS comm_carrier_v_sap_id,
	comm.display_name                             AS comm_carrier_display_name,
	o.exec_carrier_id							  AS exec_carrier_id,
	execvend.sap_id		                          AS exec_carrier_v_sap_id,
	exec.display_name                             AS exec_carrier_display_name,
	truck.truck_plate_no                          AS truck_plate_no,
	o.delivery_from_dat							  AS delivery_from_dat,
	o.delivery_to_dat							  AS delivery_to_dat,
	ld.unloading_ts                      	  	  AS unloading_time,
	cast(round((ld.unloading_ts-delivery_from_dat)*24*60,0) as number(12)) as diff_vs_del_from,
	cast(round((ld.unloading_ts-delivery_to_dat)*24*60,0) as number(12)) as diff_vs_del_to,
	o.toll_fee									  AS toll_fee,
	o.itrack_distance_km,
	sp.sap_shipping_cond_cd						  AS flow_type,
    sarea.sorg_cd								  AS sarea_sorg_cd,
    ap.real_start								  AS load_start_date,
	ld.loading_ts							  	  AS load_end_date
FROM
		tlgnc_sarea                     	 sarea
		JOIN tlgnc_order                     o         ON (o.sarea_id             	= sarea.sarea_id)
		JOIN tlgnc_shipto                    shipto    ON (o.shipto_sap_bp_id     	= shipto.shipto_sap_bp_id)
		JOIN tlgns_sap_bp                    we        ON (shipto.shipto_sap_bp_id 	= we.sap_bp_id)
		JOIN tlgns_sap_bp                    ag        ON (o.soldto_sap_bp_id     	= ag.sap_bp_id)
		LEFT JOIN tlgnc_shippingpoint        sp        ON (o.shippingpoint_id     	= sp.shippingpoint_id)
		LEFT JOIN tlgnr_plant_bs             plant     ON (sp.plant_cd            	= plant.plant_cd)
		LEFT JOIN tlgnc_carrier              comm      ON (o.comm_carrier_id      	= comm.carrier_id)
		LEFT JOIN tlgns_sap_vendor           commvend  ON (comm.sap_vendor_id     	= commvend.sap_vendor_id)
		LEFT JOIN tlgnc_carrier              exec      ON (o.exec_carrier_id      	= exec.carrier_id)
		LEFT JOIN tlgns_sap_vendor           execvend  ON (exec.sap_vendor_id     	= execvend.sap_vendor_id)
		LEFT JOIN tlgnc_truck                truck     ON (o.delivered_truck_id   	= truck.truck_id)
		LEFT JOIN tlgnc_order_item			 oitem	   ON (o.order_id			  	= oitem.order_id and oitem.order_item_no=10)
		LEFT JOIN tlgnc_load				 ld		   ON (oitem.load_id		  	= ld.load_id)
		LEFT JOIN tlgnc_assignment_part 	 ap		   ON (ld.load_id 				= ap.load_id and ap.assignment_part_type_cd=1)
		WHERE o.itrack_available_flag=1 and o.order_status_cd=16;

COMMENT ON TABLE VLGNC_ITRACK_REPORT is 'View on which the itrack Reporting excel export is based.';

PROMPT END VLGNC_ITRACK_REPORT

------------------------------------------------------------------------------

PROMPT VLGNC_OC_GET_ORDERS_VIEW

CREATE OR REPLACE VIEW VLGNC_OC_GET_ORDERS_VIEW AS
SELECT
	ord.ORDER_ID							AS order_id,
	ord.ORDER_NUMBER						AS order_number,
	soldtosap.SAP_IFC_ID 					AS soldto_sap_id,
	shiptosap.SAP_IFC_ID					AS shipto_sap_id,
	ord.DELIVERY_FROM_DAT					AS delivery_from_dat,
	ord.DELIVERY_TO_DAT						AS delivery_to_dat,
	ord.DELIVERY_TYPE_CD					AS delivery_type_cd,
	ord.ORDER_STATUS_CD						AS order_status_cd,
	shippingpoint.SAP_SHIPPINGPOINT_NO		AS sap_shippingpoint_no,
    shippingpoint.PLANT_CD					AS supply_plant_no,
	sarea.SORG_CD							AS sarea_sorg_cd,
    ord.ORDER_CREATION_TYPE_CD				AS order_creation_type_cd
FROM
	TLGNC_ORDER 						ord
	LEFT JOIN 	TLGNC_SHIPPINGPOINT 	shippingpoint 	ON (ord.SHIPPINGPOINT_ID 	= shippingpoint.SHIPPINGPOINT_ID)
	LEFT JOIN 	TLGNC_CUSTORDER 		custorder 		ON (ord.CUSTORDER_ID 		= custorder.CUSTORDER_ID)
	LEFT JOIN 	TLGNC_SAREA 			sarea 			ON (custorder.SAREA_ID 		= sarea.SAREA_ID)
	LEFT JOIN 	TLGNS_SAP_BP 			soldtosap 		ON (ord.SOLDTO_SAP_BP_ID 	= soldtosap.SAP_BP_ID)
	LEFT JOIN 	TLGNS_SAP_BP 			shiptosap 		ON (ord.SHIPTO_SAP_BP_ID 	= shiptosap.SAP_BP_ID)
WHERE ord.DELIVERY_TYPE_CD IN (1, 5);
COMMENT ON TABLE VLGNC_OC_GET_ORDERS_VIEW IS 'View for orders sent in response for OC getOrders method.';

PROMPT END VLGNC_OC_GET_ORDERS_VIEW

------------------------------------------------------------------------------

--- ^^^ Add new views ABOVE ^^^ or in the middle of the file if they logically belong to another view.
-- Do NOT add a REM START... line, but use "PROMPT", because this makes it easier to read the log when setting up a new DB
--
-- MANDATORY: Include a DB COMMENT for each new view! Purpose + (Main) Usage must be described.
--   comment on table vlgn.... is '...';
