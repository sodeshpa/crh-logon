package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;
import javax.inject.Named;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.TimeZoneServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.TimeZoneServiceLocalInterface;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.TimezoneBsEntity;
import com.crh.logon.persistence.entities.views.TimezoneEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@Interceptors(UserInfoInterceptor.class)
@Named
public class TimeZoneServiceBean extends TimeZoneServiceDispatcher implements TimeZoneServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(TimeZoneServiceBean.class);

	public List<TimeZone> loadTimeZonesForLocale(Locale locale) {
		// [[
		List<TimeZone> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTimeZonesForLocale");
		}
		// ]]
		try {
			List<TimezoneEntity> entities = loadTimezoneEntitiesForLocale(locale);

			result = new ArrayList<TimeZone>(entities.size());
			for (TimezoneEntity entity : entities) {
				TimeZone tz = new TimeZone(entity.getTimezoneCd(), locale)
						.withCountry(entity.getCountry())
						.withRegion(entity.getRegion());
				result.add(tz);
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

	public List<TimezoneEntity> loadTimezoneEntitiesForLocale(Locale locale) {
		// [[
		List<TimezoneEntity> result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadTimezoneEntitiesForLocale");
		}
		// ]]
		try {
			ReadOnlyDao<TimezoneEntity> dao = getDaoProvider().getTimezoneEntityDao();

			Filter<TimezoneEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.eq(TimezoneEntity.ATTR_LANG_CD, locale.getLanguage()));

			return result = dao.find(filter);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public TimeZone loadForUser(UserEntity user) {
		// [[
		TimeZone result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadForUser");
		}
		// ]]
		try {
			return result = loadForBaseEntity(user != null ? user.getTimezone() : null,
											  new Locale(user.getApplicationLang().getLangCd()));
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public TimeZone loadForShipTo(ShiptoEntity shipto, Locale locale) {
		// [[
		TimeZone result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadForShipTo");
		}
		// ]]
		try {
			return result = loadForBaseEntity(
					shipto != null ? shipto.getShiptoSapBp().getTimezone() : null,
					locale);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public TimeZone loadForShippingPoint(ShippingpointEntity shippingPoint, Locale locale) {
		// [[
		TimeZone result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadForShippingPoint");
		}
		// ]]
		try {
			return result = loadForBaseEntity(
					shippingPoint != null ? shippingPoint.getTimezone() : null,
					locale);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public TimeZone createDefault(Locale locale) {
		// [[
		TimeZone result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createDefault");
		}
		// ]]
		try {
			return result = new TimeZone(getConfigBean().getAppDefaultTimezone(), locale);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private TimeZone loadForBaseEntity(TimezoneBsEntity baseEntity, Locale locale) {
		if (baseEntity == null) {
			return createDefault(locale);
		}
		else {
			return new TimeZone(baseEntity.getTimezoneCd(), locale);
		}
	}
}
