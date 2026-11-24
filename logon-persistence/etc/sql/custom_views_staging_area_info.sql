-- Master SQL script to create views to determine the last unprocessed request for each sap import
-- Views are used by the sap import cron job to determine if import need to be run or not

prompt Starting sapimport_new_data_available_views.sql
prompt Purpose to create views to retrieve last unprocessed request for each sap import

prompt VLGNS_STAGINGAREA_BPN_NEW
CREATE OR REPLACE FORCE VIEW VLGNS_STAGINGAREA_BPN_NEW
AS
  SELECT hjid,
    sapimpimporteddatelement AS new_data_available_ts
  FROM
    (SELECT req.hjid,
      req.sapimpimporteddatelement
    FROM ULGNSAP.BPN_BUSINESSPARTNERNOTIFICAT_0 req
    WHERE req.sapimpstatuscd IN (0,10)
    ORDER BY req.sapimpimporteddatelement DESC
    )
  WHERE rownum < 2;

COMMENT ON TABLE VLGNS_STAGINGAREA_BPN_NEW is 'View containing the last new data availability info for sapimport_bpn.';

REM END VLGNS_STAGINGAREA_BPN_NEW

-------------------------------------------------------------------------------

prompt VLGNS_STAGINGAREA_SDN_NEW
CREATE OR REPLACE FORCE VIEW VLGNS_STAGINGAREA_SDN_NEW
AS
  SELECT hjid,
    sapimpimporteddatelement AS new_data_available_ts
  FROM
    (SELECT req.hjid,
      req.sapimpimporteddatelement
    FROM ULGNSAP.sdn_salesdocumentnotification req
    WHERE req.sapimpstatuscd IN (0)
    ORDER BY req.sapimpimporteddatelement DESC
    )
  WHERE rownum < 2;

COMMENT ON TABLE VLGNS_STAGINGAREA_SDN_NEW is 'View containing the last new data availability info for sapimport_sdn.';

REM END VLGNS_STAGINGAREA_SDN_NEW

-------------------------------------------------------------------------------

prompt VLGNS_STAGINGAREA_MNN_NEW 
CREATE OR REPLACE FORCE VIEW VLGNS_STAGINGAREA_MNN_NEW
AS
  SELECT hjid,
    sapimpimporteddatelement AS new_data_available_ts
  FROM
    (SELECT req.hjid,
      req.sapimpimporteddatelement
    FROM ULGNSAP.mnn_materialnormnotification req
    WHERE req.sapimpstatuscd IN (0)
    ORDER BY req.sapimpimporteddatelement DESC
    )
  WHERE rownum < 2;
  
COMMENT ON TABLE VLGNS_STAGINGAREA_MNN_NEW is 'View containing the last new data availability info for sapimport_mnn.';

REM END VLGNS_STAGINGAREA_MNN_NEW  
-------------------------------------------------------------------------------
prompt VLGNS_STAGINGAREA_VEHN_NEW    
CREATE OR REPLACE FORCE VIEW VLGNS_STAGINGAREA_VEHN_NEW
AS
  SELECT hjid,
    sapimpimporteddatelement AS new_data_available_ts
  FROM
    (SELECT req.hjid,
      req.sapimpimporteddatelement
    FROM ULGNSAP.vehn_vehiclenotification req
    WHERE req.sapimpstatuscd IN (0)
    ORDER BY req.sapimpimporteddatelement DESC
    )
  WHERE rownum < 2;

COMMENT ON TABLE VLGNS_STAGINGAREA_VEHN_NEW is 'View containing the last new data availability info for sapimport_vehn.';

REM END VLGNS_STAGINGAREA_VEHN_NEW  
-------------------------------------------------------------------------------

prompt VLGNS_STAGINGAREA_VN_NEW   

CREATE OR REPLACE FORCE VIEW VLGNS_STAGINGAREA_VN_NEW
AS
  SELECT hjid,
    sapimpimporteddatelement AS new_data_available_ts
  FROM
    (SELECT req.hjid,
      req.sapimpimporteddatelement
    FROM ULGNSAP.vn_vendornotification req
    WHERE req.sapimpstatuscd IN (0,10)
    ORDER BY req.sapimpimporteddatelement DESC
    )
  WHERE rownum < 2;

COMMENT ON TABLE VLGNS_STAGINGAREA_VN_NEW is 'View containing the last new data availability info for sapimport_vn.';

REM END VLGNS_STAGINGAREA_VN_NEW    
-------------------------------------------------------------------------------
  
prompt VLGNS_STAGINGAREA_MN_NEW

CREATE OR REPLACE FORCE VIEW VLGNS_STAGINGAREA_MN_NEW
AS
  SELECT hjid,
    sapimpimporteddatelement AS new_data_available_ts
  FROM
    (SELECT req.hjid,
      req.sapimpimporteddatelement
    FROM ULGNSAP.mn_materialnotification req
    WHERE req.sapimpstatuscd IN (0)
    ORDER BY req.sapimpimporteddatelement DESC
    )
  WHERE rownum < 2;

COMMENT ON TABLE VLGNS_STAGINGAREA_MN_NEW is 'View containing the last new data availability info for sapimport_mn.';

REM END VLGNS_STAGINGAREA_MN_NEW
-------------------------------------------------------------------------------
prompt sapimport_new_data_available_views.sql ended