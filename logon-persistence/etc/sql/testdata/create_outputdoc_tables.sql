/**
 * Productive database does not contain the export of the outputdoc tables, as a consequence
 * must be created at AdNovum.
 */
-- alter table tlgnc_custorder_outputdoc add (
create table tlgnc_custorder_outputdoc (
    custorder_outputdoc_id           number(12)           not null,
    custorder_id                     number(12)           null,
    pdf_outputdoc_id                 number(12)           null,
    ps_outputdoc_id                  number(12)           null,
    generated_in_custord_stat_cd     number(2)            not null,
    ctl_cre_uid                      number(12)            default 1 not null,
    ctl_cre_ts                       timestamp             default systimestamp not null,
    ctl_mod_uid                      number(12)            default 1 not null,
    ctl_mod_ts                       timestamp             default systimestamp not null,
    ctl_tcn                          number(12)            default 0 not null
);
-- alter table tlgnc_order_outputdoc add (
create table tlgnc_order_outputdoc (
    order_outputdoc_id               number(12)           not null,
    order_id                         number(12)           null,
    pdf_outputdoc_id                 number(12)           null,
    ps_outputdoc_id                  number(12)           null,
    generated_in_order_status_cd     number(2)            not null,
    ctl_cre_uid                      number(12)            default 1 not null,
    ctl_cre_ts                       timestamp             default systimestamp not null,
    ctl_mod_uid                      number(12)            default 1 not null,
    ctl_mod_ts                       timestamp             default systimestamp not null,
    ctl_tcn                          number(12)            default 0 not null
);
-- alter table tlgnc_outputdoc add (
create table tlgnc_outputdoc (
    outputdoc_id                     number(12)           not null,
    outputdoc_name                   varchar2(100)        not null,
    lang_cd                          varchar2(2)          not null,
    data                             blob                 null,
    data_mime_type_cd                number(12)           not null,
    outputdoc_type_cd                number(12)            default -1 not null,
    ctl_cre_uid                      number(12)            default 1 not null,
    ctl_cre_ts                       timestamp             default systimestamp not null,
    ctl_mod_uid                      number(12)            default 1 not null,
    ctl_mod_ts                       timestamp             default systimestamp not null,
    ctl_tcn                          number(12)            default 0 not null
);
ALTER TABLE TLGNC_CUSTORDER_OUTPUTDOC                ADD PRIMARY KEY (CUSTORDER_OUTPUTDOC_ID                            ) USING INDEX TABLESPACE INDEX_LGN_A;
ALTER TABLE TLGNC_ORDER_OUTPUTDOC                    ADD PRIMARY KEY (ORDER_OUTPUTDOC_ID                                ) USING INDEX TABLESPACE INDEX_LGN_A;
ALTER TABLE TLGNC_OUTPUTDOC                          ADD PRIMARY KEY (OUTPUTDOC_ID                                      ) USING INDEX TABLESPACE INDEX_LGN_A;

ALTER TABLE         TLGNC_CUSTORDER_OUTPUTDOC
    ADD CONSTRAINT  FK_TLGNC_CUSTORDER_ODOC_ORD
    FOREIGN KEY     (CUSTORDER_ID)
    REFERENCES      TLGNC_CUSTORDER (CUSTORDER_ID);


ALTER TABLE         TLGNC_CUSTORDER_OUTPUTDOC
    ADD CONSTRAINT  FK_TLGNC_CUSTORDER_ODOC_PDF
    FOREIGN KEY     (PDF_OUTPUTDOC_ID)
    REFERENCES      TLGNC_OUTPUTDOC (OUTPUTDOC_ID);

ALTER TABLE         TLGNC_CUSTORDER_OUTPUTDOC
    ADD CONSTRAINT  FK_TLGNC_CUSTORDER_ODOC_PS
    FOREIGN KEY     (PS_OUTPUTDOC_ID)
    REFERENCES      TLGNC_OUTPUTDOC (OUTPUTDOC_ID);
    
    
ALTER TABLE         TLGNC_CUSTORDER_OUTPUTDOC
    ADD CONSTRAINT  FK_TLGNC_CUSTORDER_STAT
    FOREIGN KEY     (GENERATED_IN_CUSTORD_STAT_CD)
    REFERENCES      TLGNR_CUSTORDER_STATUS_BS (CUSTORDER_STATUS_CD);
    
    
ALTER TABLE         TLGNC_ORDER_OUTPUTDOC
    ADD CONSTRAINT  FK_TLGNC_ORDER_OUTPUTDOC_ORD
    FOREIGN KEY     (ORDER_ID)
    REFERENCES      TLGNC_ORDER (ORDER_ID);

ALTER TABLE         TLGNC_ORDER_OUTPUTDOC
    ADD CONSTRAINT  FK_TLGNC_ORDER_OUTPUTDOC_PDF
    FOREIGN KEY     (PDF_OUTPUTDOC_ID)
    REFERENCES      TLGNC_OUTPUTDOC (OUTPUTDOC_ID);

ALTER TABLE         TLGNC_ORDER_OUTPUTDOC
    ADD CONSTRAINT  FK_TLGNC_ORDER_OUTPUTDOC_PS
    FOREIGN KEY     (PS_OUTPUTDOC_ID)
    REFERENCES      TLGNC_OUTPUTDOC (OUTPUTDOC_ID);

ALTER TABLE         TLGNC_ORDER_OUTPUTDOC
    ADD CONSTRAINT  FK_TLGNC_ORDER_OUTPUTDOC_STAT
    FOREIGN KEY     (GENERATED_IN_ORDER_STATUS_CD)
    REFERENCES      TLGNR_ORDER_STATUS_BS (ORDER_STATUS_CD);
create sequence slgnc_custorder_outputdoc start with 1000 maxvalue 88888888888;
create sequence slgnc_order_outputdoc start with 1000 maxvalue 88888888888;
create sequence slgnc_outputdoc start with 1000 maxvalue 88888888888;
    grant select, insert, update, delete on TLGNC_CUSTORDER_OUTPUTDOC to rl_lgn_user;
grant select on slgnc_custorder_outputdoc to rl_lgn_user;

grant select, insert, update, delete on TLGNC_ORDER_OUTPUTDOC to rl_lgn_user;
grant select on slgnc_order_outputdoc to rl_lgn_user;
grant select, insert, update, delete on TLGNC_OUTPUTDOC to rl_lgn_user;
grant select on slgnc_outputdoc to rl_lgn_user;

