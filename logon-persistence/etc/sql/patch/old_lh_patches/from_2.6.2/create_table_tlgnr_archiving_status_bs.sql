create table tlgnr_archiving_status_bs (
    archiving_status_cd              number(2)            not null,
    description                      varchar2(50)         not null,
    display_flag                     number(1)            not null,
    ctl_cre_uid                      number(12)            default 1 not null,
    ctl_cre_ts                       timestamp             default systimestamp not null,
    ctl_mod_uid                      number(12)            default 1 not null,
    ctl_mod_ts                       timestamp             default systimestamp not null
);

ALTER TABLE TLGNR_ARCHIVING_STATUS_BS                ADD PRIMARY KEY (ARCHIVING_STATUS_CD                               ) USING INDEX TABLESPACE INDEX_LGN_A;

COMMENT ON TABLE     TLGNR_ARCHIVING_STATUS_BS        IS 'Reference table for the different types of order archiving status.';
COMMENT ON COLUMN    TLGNR_ARCHIVING_STATUS_BS.ARCHIVING_STATUS_CD IS 'PK. Status of the order archiving. 1=new, 2=archiving failed, 3=deletable, etc.';
COMMENT ON COLUMN    TLGNR_ARCHIVING_STATUS_BS.DESCRIPTION IS 'Description.';
COMMENT ON COLUMN    TLGNR_ARCHIVING_STATUS_BS.DISPLAY_FLAG IS 'When this is false then the order is not visible. Please do NOT use for business decisions, this is only for the decision about displaying or hiding the order.';
COMMENT ON COLUMN    TLGNR_ARCHIVING_STATUS_BS.CTL_CRE_UID IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who created the record.';
COMMENT ON COLUMN    TLGNR_ARCHIVING_STATUS_BS.CTL_CRE_TS IS 'Technical field. The time when the record got created.';
COMMENT ON COLUMN    TLGNR_ARCHIVING_STATUS_BS.CTL_MOD_UID IS 'Technical field. The principal name (corresponding field of javax.security.Principal) of the user who last modified the record.';
COMMENT ON COLUMN    TLGNR_ARCHIVING_STATUS_BS.CTL_MOD_TS IS 'Technical field. The time when the record was last modified.';


CREATE SEQUENCE  slgnr_archiving_status_bs  MINVALUE 1 MAXVALUE 88888888888 INCREMENT BY 1 START WITH 1 CACHE 20 NOORDER  NOCYCLE;
