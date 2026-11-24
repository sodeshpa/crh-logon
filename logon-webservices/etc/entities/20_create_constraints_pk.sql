-- Primary key constraints for tables which are not created out of the XSD interface definition

ALTER TABLE TLGNR_SAPIMPSTATUS_BS                    ADD PRIMARY KEY (SAPIMPSTATUS_CD);
ALTER TABLE TLGNR_SAPIMPSTATUS_TX                    ADD PRIMARY KEY (SAPIMPSTATUS_CD, LANG_CD);
