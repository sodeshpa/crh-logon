package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.dispatchers.ShiptoServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.ShiptoServiceLocalInterface;
import com.crh.logon.business.services.utils.DbUtils;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class ShiptoServiceBean extends ShiptoServiceDispatcher
	implements ShiptoServiceLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ShiptoServiceBean.class);

	/**
	 * @return a list of SHIPTO.SAP+BP_ID -> TIMEZONE for the given list of SA_BP_IDs.
	 */
	public Map<Long, com.crh.logon.business.services.utils.TimeZone> fetchShiptoTimeZonesForSapBpIds(Set<Long> sapBpIds, Locale locale) {
		// [[
		Map<Long, com.crh.logon.business.services.utils.TimeZone> result = null;
		Object[][] params = {{"sapBpIds", sapBpIds},
							 {"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchShiptoTimeZonesForSapBpIds", params);
		}
		// ]]
		try {
			final ReadOnlyDao<ShiptoEntity> dao = getDaoProvider().getShiptoEntityDao();

			final Filter<ShiptoEntity> f = dao.createFilter();
			f.addExpression(DbUtils.createSplittedInRestrictionOfIds(
					ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, new ArrayList<Long>(sapBpIds)));

			List<ShiptoEntity> shiptos = dao.find(f);
			result = new HashMap<Long, com.crh.logon.business.services.utils.TimeZone>();

			// --- Create TimeZone instances for each ShipTo ---
			com.crh.logon.business.services.utils.TimeZone defaultTZ = new com.crh.logon.business.services.utils.TimeZone(getConfigBean().getAppDefaultTimezone(), locale);
			for (ShiptoEntity shipto : shiptos) {
				SapBpEntity sapBp = shipto.getShiptoSapBp();
				if (sapBp.getTimezone() == null) {
					result.put(sapBp.getSapBpId(), defaultTZ);
				}
				else {
					result.put(sapBp.getSapBpId(),
							   new com.crh.logon.business.services.utils.TimeZone(sapBp.getTimezone().getTimezoneCd(),
											locale));
				}
			}

			return result;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}
}
