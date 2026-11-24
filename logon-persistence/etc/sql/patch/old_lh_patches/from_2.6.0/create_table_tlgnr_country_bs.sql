 CREATE TABLE "TLGNR_COUNTRY_BS" 
   (	"COUNTRY_CD" VARCHAR2(3 CHAR) NOT NULL ENABLE, 
	"COUNTRY_NAME" VARCHAR2(30 CHAR), 
	"CTL_ACT_CD" NUMBER(1,0) NOT NULL ENABLE, 
	"CTL_CRE_UID" NUMBER(12,0) DEFAULT 1 NOT NULL ENABLE, 
	"CTL_CRE_TS" TIMESTAMP (6) DEFAULT systimestamp NOT NULL ENABLE, 
	"CTL_MOD_UID" NUMBER(12,0) DEFAULT 1 NOT NULL ENABLE, 
	"CTL_MOD_TS" TIMESTAMP (6) DEFAULT systimestamp NOT NULL ENABLE, 
	 PRIMARY KEY ("COUNTRY_CD"));

   COMMENT ON COLUMN "TLGNR_COUNTRY_BS"."COUNTRY_CD" IS 'Country code of the comapde code of the orders'' sales area. This field is only used ';
   COMMENT ON COLUMN "TLGNR_COUNTRY_BS"."COUNTRY_NAME" IS 'Full name of the Country';
   COMMENT ON COLUMN "TLGNR_COUNTRY_BS"."CTL_ACT_CD" IS 'Active Flag. When new data is entered, only reference data with active flag are available for selection normally.';
   COMMENT ON COLUMN "TLGNR_COUNTRY_BS"."CTL_CRE_UID" IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who created the record.';
   COMMENT ON COLUMN "TLGNR_COUNTRY_BS"."CTL_CRE_TS" IS 'Technical field. The time when the record got created.';
   COMMENT ON COLUMN "TLGNR_COUNTRY_BS"."CTL_MOD_UID" IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who last modified the record.';
   COMMENT ON COLUMN "TLGNR_COUNTRY_BS"."CTL_MOD_TS" IS 'Technical field. The time when the record was last modified.';