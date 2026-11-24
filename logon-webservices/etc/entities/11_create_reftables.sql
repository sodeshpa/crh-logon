-- Additional (Ref-)tables which are not created out of the XSD interface definition

CREATE TABLE TLGNR_SAPIMPSTATUS_BS (
    SAPIMPSTATUS_CD                  DECIMAL(2)           NOT NULL,
    CTL_ACT_CD                       NUMBER(1)            NOT NULL
);
CREATE TABLE TLGNR_SAPIMPSTATUS_TX (
    SAPIMPSTATUS_CD                  DECIMAL(2)           NOT NULL,
    LANG_CD                          VARCHAR2(2)          NOT NULL,
    TXT                              VARCHAR2(40)         NOT NULL
);
