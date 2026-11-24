package com.crh.logon.ejbtest;

import org.junit.Test;

import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

@AdnTrace
public class CreateTestShiptosTestCase extends JunitSupport {

	private DispokarteService dispoCarteServiceBean;
	public CreateTestShiptosTestCase(String testCaseName) {
		super(testCaseName);
		dispoCarteServiceBean = serviceLocator.getDispokarteService();
	}
	
	/** 
	 * We run the updateShiptoSalesAreas even if the shipto was already create to make sure
	 * the sales area tabs are created; not the best in terms of speed but even with hu<>ch it finishes
	 * in a minute or so
	 *  
	 * @param shiptoSapId
	 * @param soldtoSapId
	 * 
	 */
	private ShiptoEntity createOrFetchTestShipto(String shiptoSapId, String soldtoSapId) {
		ShiptoEntity shipToEntity = dispoCarteServiceBean.fetchShiptoBySapId(shiptoSapId);
		if (shipToEntity == null) {
			shipToEntity = dispoCarteServiceBean.createShipto(shiptoSapId, soldtoSapId);
		}

		SapBpEntity soldToEntity = dispoCarteServiceBean.fetchSoldtoSapBpBySapId(soldtoSapId);
		dispoCarteServiceBean.updateShiptoSalesAreas(shipToEntity, soldToEntity, true);

		return shipToEntity;
	}

	@Test public void testCreateTestShiptos() 	{
		assertNotNull(createOrFetchTestShipto("ADN2209206", "ADN900119"));
		assertNotNull(createOrFetchTestShipto("ADN1208869", "ADN73031"));
		assertNotNull(createOrFetchTestShipto("ADN800119", "ADN800119"));
		assertNotNull(createOrFetchTestShipto("ADN33", "ADN26"));
		assertNotNull(createOrFetchTestShipto("ADN20", "ADN21"));
		assertNotNull(createOrFetchTestShipto("ADN73031", "ADN73031"));
		assertNotNull(createOrFetchTestShipto("ADN800147", "ADN800147"));
		assertNotNull(createOrFetchTestShipto("ADN1863947", "ADN800147"));
		assertNotNull(createOrFetchTestShipto("ADNHIERARC", "ADN73161"));
		assertNotNull(createOrFetchTestShipto("ADN73161", "ADN73161"));
		assertNotNull(createOrFetchTestShipto("ADN35", "ADN28"));
		assertNotNull(createOrFetchTestShipto("ADN1644822", "ADN800147"));
		assertNotNull(createOrFetchTestShipto("ADN1209037", "ADN73161"));
		assertNotNull(createOrFetchTestShipto("ADN21", "ADN21"));
		assertNotNull(createOrFetchTestShipto("ADN36", "ADN29"));
		assertNotNull(createOrFetchTestShipto("ADN106570", "ADN206570"));
		assertNotNull(createOrFetchTestShipto("ADN1209206", "ADN800119"));
		assertNotNull(createOrFetchTestShipto("ADN30", "ADN22"));
		assertNotNull(createOrFetchTestShipto("ADN23", "ADN73161"));
		assertNotNull(createOrFetchTestShipto("ADN37", "ADN22"));
		assertNotNull(createOrFetchTestShipto("ADN32", "ADN25"));
		assertNotNull(createOrFetchTestShipto("ADN1470127", "ADN800147"));
		assertNotNull(createOrFetchTestShipto("ADN106570", "ADN106570"));
		assertNotNull(createOrFetchTestShipto("ADNAGG", "ADN73161"));
		assertNotNull(createOrFetchTestShipto("ADN1866134", "ADN800147"));
		assertNotNull(createOrFetchTestShipto("ADN31", "ADN24"));
		assertNotNull(createOrFetchTestShipto("ADN34", "ADN27"));
		assertNotNull(createOrFetchTestShipto("ADN41", "ADN40"));
	}
}
