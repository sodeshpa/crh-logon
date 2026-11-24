-- The standard text views used for ref data (not needed in the context of sap staging area, but added for reasons of consistency.

CREATE OR REPLACE VIEW VLGNR_SAPIMPSTATUS AS
SELECT
    bs.SAPIMPSTATUS_CD || '_' || tx.LANG_CD  ID
    , bs.SAPIMPSTATUS_CD
    , tx.LANG_CD
    , tx.TXT
FROM
    TLGNR_SAPIMPSTATUS_BS bs,
    TLGNR_SAPIMPSTATUS_TX tx
WHERE bs.SAPIMPSTATUS_CD = tx.SAPIMPSTATUS_CD;
