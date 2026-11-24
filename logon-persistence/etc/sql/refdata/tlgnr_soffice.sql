-- $Revision$
-- $Date$
--
prompt '---------- refdata/tlgnr_soffice.sql ----------'

-- Special value to allow SAP import even when an invalid sales office is received.

prompt INSERTING into TLGNR_SOFFICE_BS
insert into TLGNR_SOFFICE_BS (SOFFICE_CD,SOFFICE_NAME,CTL_ACT_CD) values ('INV','Invalid/Missing in SAP',0);