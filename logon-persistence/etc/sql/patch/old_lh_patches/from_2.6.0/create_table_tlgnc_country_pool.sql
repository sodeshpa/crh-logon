
  CREATE TABLE "TLGNC_COUNTRY_POOL" 
   (	"COUNTRY_POOL_ID" NUMBER(12,0) NOT NULL ENABLE, 
	"POOL_ID" NUMBER(12,0) NOT NULL ENABLE, 
	"COUNTRY_CD" VARCHAR2(3 CHAR) NOT NULL ENABLE, 
	"CTL_CRE_UID" NUMBER(12,0) DEFAULT 1 NOT NULL ENABLE, 
	"CTL_CRE_TS" TIMESTAMP (6) DEFAULT systimestamp NOT NULL ENABLE, 
	"CTL_MOD_UID" NUMBER(12,0) DEFAULT 1 NOT NULL ENABLE, 
	"CTL_MOD_TS" TIMESTAMP (6) DEFAULT systimestamp NOT NULL ENABLE, 
	"CTL_TCN" NUMBER(12,0) DEFAULT 0 NOT NULL ENABLE, 
	 PRIMARY KEY ("COUNTRY_POOL_ID"), 
	 CONSTRAINT "UQ_TLGNC_COUNTRY_POOL" UNIQUE ("POOL_ID", "COUNTRY_CD"), 
	 CONSTRAINT "FK_TLGNC_CP_TLGNR_COUNTRY_BS" FOREIGN KEY ("COUNTRY_CD")
	  REFERENCES "TLGNR_COUNTRY_BS" ("COUNTRY_CD") ENABLE, 
	 CONSTRAINT "FK_TLGNC_CP_TLGNC_POOL" FOREIGN KEY ("POOL_ID")
	  REFERENCES "TLGNC_POOL" ("POOL_ID") ENABLE);

   COMMENT ON COLUMN "TLGNC_COUNTRY_POOL"."COUNTRY_POOL_ID" IS 'PK';
   COMMENT ON COLUMN "TLGNC_COUNTRY_POOL"."POOL_ID" IS 'FK tlgnc_pool. The Pool to which a Carrier is assigned.';
   COMMENT ON COLUMN "TLGNC_COUNTRY_POOL"."COUNTRY_CD" IS 'FK tlgnr_country_bs';
   COMMENT ON COLUMN "TLGNC_COUNTRY_POOL"."CTL_CRE_UID" IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who created the record.';
   COMMENT ON COLUMN "TLGNC_COUNTRY_POOL"."CTL_CRE_TS" IS 'Technical field. The time when the record got created.';
   COMMENT ON COLUMN "TLGNC_COUNTRY_POOL"."CTL_MOD_UID" IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who last modified the record.';
   COMMENT ON COLUMN "TLGNC_COUNTRY_POOL"."CTL_MOD_TS" IS 'Technical field. The time when the record was last modified.';
   COMMENT ON COLUMN "TLGNC_COUNTRY_POOL"."CTL_TCN" IS 'Technical field. For supporting optimistic locking.';
   COMMENT ON TABLE "TLGNC_COUNTRY_POOL"  IS 'The Pool assigned to ceratian Country. Pool:Country is M:N.';
