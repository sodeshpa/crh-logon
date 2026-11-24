
  CREATE TABLE "TLGNU_USER_COUNTRY_ACC" 
   (	"USER_COUNTRY_ACC_ID" NUMBER(12,0) NOT NULL ENABLE, 
	"USER_ID" NUMBER(12,0) NOT NULL ENABLE, 
	"COUNTRY_CD" VARCHAR2(3 CHAR) NOT NULL ENABLE, 
	"CTL_CRE_UID" NUMBER(12,0) DEFAULT 1 NOT NULL ENABLE, 
	"CTL_CRE_TS" TIMESTAMP (6) DEFAULT systimestamp NOT NULL ENABLE, 
	"CTL_MOD_UID" NUMBER(12,0) DEFAULT 1 NOT NULL ENABLE, 
	"CTL_MOD_TS" TIMESTAMP (6) DEFAULT systimestamp NOT NULL ENABLE, 
	"CTL_TCN" NUMBER(12,0) DEFAULT 0 NOT NULL ENABLE, 
	 PRIMARY KEY ("USER_COUNTRY_ACC_ID"), 
	 CONSTRAINT "UQ_TLGNU_USER_COUNTRYCD_ACC" UNIQUE ("USER_ID", "COUNTRY_CD"),
	 CONSTRAINT "FK_TLGNR_COUNTRY_BS" FOREIGN KEY ("COUNTRY_CD")
	  REFERENCES "TLGNR_COUNTRY_BS" ("COUNTRY_CD") ENABLE, 
	 CONSTRAINT "FK_COUNTRY_ACC_USER" FOREIGN KEY ("USER_ID")
	  REFERENCES "TLGNU_USER" ("USER_ID") ENABLE
   );

   COMMENT ON COLUMN "TLGNU_USER_COUNTRY_ACC"."USER_COUNTRY_ACC_ID" IS 'PK';
   COMMENT ON COLUMN "TLGNU_USER_COUNTRY_ACC"."USER_ID" IS 'FK tlgnu_user. The Logon user.';
   COMMENT ON COLUMN "TLGNU_USER_COUNTRY_ACC"."COUNTRY_CD" IS 'FK tlgnr_country_bs The country code for which access is granted.';
   COMMENT ON COLUMN "TLGNU_USER_COUNTRY_ACC"."CTL_CRE_UID" IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who created the record.';
   COMMENT ON COLUMN "TLGNU_USER_COUNTRY_ACC"."CTL_CRE_TS" IS 'Technical field. The time when the record got created.';
   COMMENT ON COLUMN "TLGNU_USER_COUNTRY_ACC"."CTL_MOD_UID" IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who last modified the record.';
   COMMENT ON COLUMN "TLGNU_USER_COUNTRY_ACC"."CTL_MOD_TS" IS 'Technical field. The time when the record was last modified.';
   COMMENT ON COLUMN "TLGNU_USER_COUNTRY_ACC"."CTL_TCN" IS 'Technical field. For supporting optimistic locking.';
   COMMENT ON TABLE "TLGNU_USER_COUNTRY_ACC"  IS 'Access Control/Data restriction table. Defines the country codes (tlgnr_country_bs) a user has access to.';