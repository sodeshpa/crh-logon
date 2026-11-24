/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

/* 
 * It's checking the existence of the given index. If exists, drops it.
 */
CREATE OR REPLACE PROCEDURE dropIndexIfExists(i_index_name IN VARCHAR2)
IS
	l_count NUMBER;
BEGIN
	SELECT COUNT(*)
	INTO l_count
	FROM user_indexes
	WHERE index_name = UPPER(i_index_name);
	IF l_count  > 0 THEN
		EXECUTE immediate 'DROP index ' || i_index_name;
		DBMS_OUTPUT.PUT_LINE('Index existed and dropped: "' || i_index_name || '"');
	END IF;
END;
/


PROMPT I_TLGNC_RFIDOBJECT_TYPE_LABELF

execute dropIndexIfExists('I_TLGNC_RFIDOBJECT_TYPE_LABELF');
CREATE INDEX "ULGN01"."I_TLGNC_RFIDOBJECT_TYPE_LABELF" 
	ON "ULGN01"."TLGNC_RFIDOBJECT" (upper(OBJECT_LABEL), "RFIDOBJECT_TYPE_CD") 
	TABLESPACE "INDEX_LGN_A";

REM END ULGN01 I_TLGNC_RFIDOBJECT_TYPE_LABELF
-------------------------------------------------------------------------------
-- filtering of order numbers --
PROMPT I_TLGNC_ORDER_ORDER_STRING

execute dropIndexIfExists('I_TLGNC_ORDER_NUMBER_STRING');
CREATE INDEX I_TLGNC_ORDER_NUMBER_STRING 
	ON TLGNC_ORDER (upper(to_char(ORDER_NUMBER))); 
	
REM END I_TLGNC_ORDER_NUMBER_STRING
-------------------------------------------------------------------------------
-- filtering of sap material names --
PROMPT I_TLGNS_SAP_MAT_DESC_UPPER

execute dropIndexIfExists('I_TLGNS_SAP_MAT_DESC_UPPER');
CREATE INDEX I_TLGNS_SAP_MAT_DESC_UPPER 
	ON TLGNS_SAP_MATERIAL_DESCRIPTION (upper(TXT)); 
	
REM END I_TLGNS_SAP_MAT_DESC_UPPER
-------------------------------------------------------------------------------
-- filtering of wagon numbers --
PROMPT I_TLGNS_SAP_MAT_NUMBER

execute dropIndexIfExists('I_TLGNS_SAP_MAT_NUMBER');
CREATE INDEX I_TLGNS_SAP_MAT_NUMBER 
	ON TLGNS_SAP_MATERIAL (upper(SAP_MATERIAL_NO)); 
	
REM END I_TLGNS_SAP_MAT_NUMBER
-------------------------------------------------------------------------------

DROP PROCEDURE dropIndexIfExists;
