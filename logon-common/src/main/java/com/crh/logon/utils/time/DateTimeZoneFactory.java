/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.utils.time;

import java.io.IOException;
import java.time.ZoneId;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.utils.StringUtils;
import org.joda.time.DateTimeZone;

/**
 * We wrap the retrieval of DateTimeZone instances through this factory to ensure that our custom provider is initialized and
 * set.
 * <p/>
 * We need this internal provider because Joda Time does not support all timezone ids required in Logon. In particular, the
 * Europe/Pristina timezone id does not exist for the main reason that Kosovo has not been voted an independent country by the UN
 * Security council which means that there is no ISO country code for it. For the time being, it has been given the country code
 * 'XK' which is a temporary solution until a decision is final. However, since is it not fully accepted, no Olsen timezone id has
 * been allocated for Kosovo and as such, it still uses the timezone in Serbia.
 * <p/>
 * In order to have some flexibility on our side for current and any future customer requests, we create our custom time zone
 * provider which merely maps Logon supported TimeZone ids to internal ones known to Joda Time. For this reason, it is not wise to
 * rely on internal naming of returned TimeZone objects for display purposes.
 *
 * @uathor AdNovum Informatik AG
 */
public final class DateTimeZoneFactory {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(DateTimeZoneFactory.class);

	private static final Map<String, String> MAPPINGS;

	static {
		Map<String, String> map = new HashMap<>();
		map.put("Europe/Pristina", "Europe/Belgrade");
		map.put("Europe/Bern", "Europe/Zurich");
		map.put("Asia/Jerusalen", "Asia/Jerusalem");
		map.put("Africa/Bloemfontein", "Africa/Johannesburg");
		map.put("Africa/Nouakchott", "Africa/Accra");
		map.put("America/Willemstad", "America/Havana");
		map.put("Asia/Abu Dhabi", "Asia/Dubai");
		map.put("Asia/Dili2", "Asia/Dili");
		map.put("Asia/Kuala Lumpur", "Asia/Hong_Kong");
		map.put("Australia/Heard/McDon.Isl", "Etc/GMT-5");
		map.put("Europe/Bouvet", "Etc/GMT");
		MAPPINGS = Collections.unmodifiableMap(map);
	}

	private DateTimeZoneFactory(){
	}

	/*
	 * Internal class for providing support for mapping timezone ids. For now, we use hard-coded
	 * values, but we could think about maintaining (yet) another mapping table which is read in on
	 * application startup and used to fill the mappings table. This would simply require the code in
	 * the ConfigBeanFilter (or some other appropriate startup filter) and an extra method on
	 * the DateTimeZoneFactory to accept the mapping and configure it on the instance of this class.
	 *
	 * For now, we keep it simple until we figure out whether there is an actual need for this. So
	 * far, we have run into the problem with Europe/Pristina (LOGON-7193), but this may be a one of
	 * case.
	 */
	static class LogonZoneInfoProvider implements org.joda.time.tz.Provider {

		private final org.joda.time.tz.Provider delegate;

		public LogonZoneInfoProvider() {
			try {
				delegate = new org.joda.time.tz.ZoneInfoProvider("org/joda/time/tz/data");
			}
			catch (IOException exc) {
				throw new IllegalArgumentException(exc);
			}
		}

		public DateTimeZone getZone(String id) {
			String mappedId = MAPPINGS.get(id);
			mappedId = (mappedId == null) ? id : mappedId;

			return delegate.getZone(mappedId);
		}

		public Set<String> getAvailableIDs() {
			return delegate.getAvailableIDs();
		}
	}

	private static transient String defaultTimeZoneId = "Europe/Paris";

	private static final LogonZoneInfoProvider TZ_PROVIDER = new LogonZoneInfoProvider();

	static {
		DateTimeZone.setProvider(TZ_PROVIDER);
	}

	public static String getDefaultTimeZoneId() {
		return defaultTimeZoneId;
	}

	/**
	 * Sets the global default timezone id for the application. This "backdoor" provides a way for the web initialization code to
	 * read the DB and configure our global factory since we do not have any references to the ConfigBean from this project
	 * <p>
	 * Because this method provides a way to bootstrap the initialization of the global default timezone id, we allow exceptions
	 * to propagate back to the caller. This ensures that if something is configured incorrectly, then we fail fast.
	 *
	 * @throws IllegalArgumentException if the given id is unknown
	 */
	public static void setDefaultTimeZoneId(String id) {
		// ensure that it is actually valid - calling DateTimeZone.forID(null) will actually return UTC
		// if the caller really wants "UTC", then they should call this method with "UTC"
		if (StringUtils.empty(id)) {
			throw new IllegalArgumentException("Given timezone id is empty/null ['" + id + "']");
		}

		// ensure that we currently support this default id
		DateTimeZone.forID(id);

		defaultTimeZoneId = id;
	}

	/**
	 * Returns a JODA Time DateTimeZone for the given id. If the id is unknown/unsupported, then we return the default timezone
	 */
	public static DateTimeZone forID(String id) {
		try {
			return DateTimeZone.forID(id);
		}
		catch (IllegalArgumentException exc) {
			LOG.error(String.format("Timezone '%s' is currently unsupported, please check LogonZoneRulesProvider map in class "
					+ "DateTimeZoneFactory. Defaulting to '%s'. Cause: %s", id, defaultTimeZoneId, exc.getMessage()));
			return DateTimeZone.forID(defaultTimeZoneId);
		}
		catch (Exception exc) {
			LOG.error(String.format("Exception when using timezone '%s'. Defaulting to timezone '%s'.",
					id, defaultTimeZoneId), exc);
			return DateTimeZone.forID(defaultTimeZoneId);
		}
	}

	public static ZoneId getZoneId(String id) {
		String mapped = MAPPINGS.get(id);
		return ZoneId.of(mapped != null ? mapped : id);
	}
}
