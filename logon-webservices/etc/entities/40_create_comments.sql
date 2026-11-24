
COMMENT ON TABLE     SDN_SALESDOCUMENTNOTIFICATION                 is 'Main table of SAP Contract import (staging area). Added on behalf of LRQ 040 Logon EBM.';
COMMENT ON COLUMN    SDN_SALESDOCUMENTNOTIFICATION.SAPIMPSTATUSCD  is 'FK tlgnr_sapimpstatus_bs (0=imported from sap; 1=imported to logon; 2=skip import (newer version exists); 3=imported to logon partially; 4=imported but set to ignored/deleted; 8=imported partially - rest abandoned; 9=failed abandoned';

COMMENT ON TABLE     VEHN_VEHICLENOTIFICATION                 is 'Main table of SAP vehicle import (staging area). Added on behalf of LRQ 046 Logon EBM.';
COMMENT ON COLUMN    VEHN_VEHICLENOTIFICATION.SAPIMPSTATUSCD  is 'FK tlgnr_sapimpstatus_bs (0=imported from sap; 1=imported to logon; 2=skip import (newer version exists); 3=imported to logon partially; 4=imported but set to ignored/deleted; 8=imported partially - rest abandoned; 9=failed abandoned';

COMMENT ON TABLE     VN_VENDORNOTIFICATION                 is 'Main table of SAP vendor import (staging area). Added on behalf of LRQ 083 Logon EBM.';
COMMENT ON COLUMN    VN_VENDORNOTIFICATION.SAPIMPSTATUSCD  is 'FK tlgnr_sapimpstatus_bs (0=imported from sap; 1=imported to logon; 2=skip import (newer version exists); 3=imported to logon partially; 4=imported but set to ignored/deleted; 8=imported partially - rest abandoned; 9=failed abandoned';

COMMENT ON TABLE     BPN_BUSINESSPARTNERNOTIFICAT_0                 is 'Main table of SAP business partner import (staging area). Added on behalf of LRQ 042 Logon EBM.';
COMMENT ON COLUMN    BPN_BUSINESSPARTNERNOTIFICAT_0.SAPIMPSTATUSCD  is 'FK tlgnr_sapimpstatus_bs (0=imported from sap; 1=imported to logon; 2=skip import (newer version exists); 3=imported to logon partially; 4=imported but set to ignored/deleted; 8=imported partially - rest abandoned; 9=failed abandoned';



