CREATE OR REPLACE PACKAGE DELETE_INVALID_RESTRICTIONS AS

PROCEDURE main(i_min_loglevel IN VARCHAR2, expiration_day IN NUMBER);
PROCEDURE delete_ssr(shipto_sarea_restr_id IN NUMBER);

END DELETE_INVALID_RESTRICTIONS;
/

SHOW ERRORS;

CREATE OR REPLACE PACKAGE BODY DELETE_INVALID_RESTRICTIONS AS
  ----------------------------------------------------------------
  ----------------------------------------------------------------
  --------------------DELETE_INVALID_RESTRICTIONS---------
  ----------------------------------------------------------------
  ----------------------------------------------------------------
  

  PROCEDURE main(i_min_loglevel IN VARCHAR2, expiration_day IN NUMBER) AS
    
    -- Contracts that has been expired for more than expiration_day value days.
	-- zero expiration day means yesterday so we have to add one plus here.
    CURSOR CUR_EXPIRED_CONTRACTS_RESTR IS
	  SELECT ctr.sap_contract_id, rs.shipto_sarea_restr_id, ctr.valid_until_dat
	  FROM tlgnc_shipto_sarea_restr rs
      JOIN tlgns_sap_contract_item it ON rs.sap_contract_item_id=it.sap_contract_item_id
      JOIN tlgns_sap_contract ctr ON ctr.sap_contract_id=it.sap_contract_id
	  WHERE (TRUNC(SYSDATE) - TRUNC(ctr.VALID_UNTIL_DAT)) > (expiration_day + 1);
    
    BEGIN
	  
	  -- set logging parameters
      LOGON_LOG.g_min_loglevel := i_min_loglevel;
    
      LOGON_LOG.INFO('DIR: main','Started of deleting expired dispocards that has been expired for longer than: ' || expiration_day || 'days');
      	  
	  FOR expired_contract_restriction IN CUR_EXPIRED_CONTRACTS_RESTR
      LOOP
      
      	LOGON_LOG.INFO('	DIR: main','Contract with SAP_CONTRACT_ID: ' || expired_contract_restriction.sap_contract_id || ' has expired on: ' ||  expired_contract_restriction.valid_until_dat);
      
      	delete_ssr(expired_contract_restriction.shipto_sarea_restr_id);
	  
	  END LOOP;
      
	  commit;
	  
      LOGON_LOG.INFO('DIR: main','End of deleting expired dispocards');
    
    END main;
    
  ---------------------------------------------
  ---------------------------------------------
  -----delete all shipto sarea restrictions ------------
  ---------------------------------------------
  ---------------------------------------------
  PROCEDURE delete_ssr(shipto_sarea_restr_id IN NUMBER) AS
	
  BEGIN

    LOGON_LOG.INFO('	DIR: delete_ssrs', ' Deletion shipto sarea restrictions by restriction id ' || shipto_sarea_restr_id);
	         
	delete from TLGNC_SHSAREA_RESTR_TRUCK_TYPE
	where SHIPTO_SAREA_RESTR_ID = shipto_sarea_restr_id;
		  
	delete from TLGNC_SHIPTO_SAREA_RESTR_BTRUL
	where SHIPTO_SAREA_RESTR_ID = shipto_sarea_restr_id;
	    
	delete from TLGNC_SHIPTO_SAREA_RESTR
	where SHIPTO_SAREA_RESTR_ID = shipto_sarea_restr_id;
	
    LOGON_LOG.INFO('	DIR: delete_ssrs', '  Deleted shipto sarea restrictions ' || SHIPTO_SAREA_RESTR_ID);
	
  END delete_ssr;
    

END DELETE_INVALID_RESTRICTIONS;
/

SHOW ERRORS;