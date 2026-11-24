/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.constants;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

/**
 * This class implements the business logic which material package types can be
 * delivered with which truck and trailer transport categories.
 * @author AdNovum Informatik AG
 */
public class PackageTypeTrspCatMapping 
{
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(PackageTypeTrspCatMapping.class);
	
	private static final Map<Long,Set<Long>> suitableTruckTrspCatsForPackageTypes = new HashMap<Long,Set<Long>>();
	private static final Map<Long,Set<Long>> suitableTrailerTrspCatsForPackageTypes = new HashMap<Long,Set<Long>>();
	
	// Cannot construct; static only.
	private PackageTypeTrspCatMapping() {};
	
	static 
	{
		/*
		 * Implementation of Table 5 in the LRQ 185.0/185.1 SDS (see LOGON-5043)
		 */
		final Set<Long> truckAllowedForBulkPowder = Collections.unmodifiableSet(new HashSet<Long>() {
			private static final long serialVersionUID = 1L;
			{
				add(Constant.TRUCK_TRSP_CAT_BULK_POWDER);
				add(Constant.TRUCK_TRSP_CAT_CONTAINER);
				add(Constant.TRUCK_TRSP_CAT_TRAILER);
				add(Constant.TRUCK_TRSP_CAT_ALL_MAT_PACKAGE_TYPES);
			}
		});
		
		final Set<Long> truckAllowedForBag = Collections.unmodifiableSet(new HashSet<Long>() {
			private static final long serialVersionUID = 1L;
			{
				add(Constant.TRUCK_TRSP_CAT_TRAILER);
				add(Constant.TRUCK_TRSP_CAT_GENERAL_FREIGHT);
				add(Constant.TRUCK_TRSP_CAT_BULK_DUMPED);
				add(Constant.TRUCK_TRSP_CAT_ALL_MAT_PACKAGE_TYPES);
			}
			
		});
		
		// Service orders: are never scheduled on trucks or trailers.
		final Set<Long> truckAllowedForService = Collections.unmodifiableSet(new HashSet<Long>());

		final Set<Long> truckAllowedForBulkDumped = Collections.unmodifiableSet(new HashSet<Long>() {
			private static final long serialVersionUID = 1L;
			{
				add(Constant.TRUCK_TRSP_CAT_TRAILER);
				add(Constant.TRUCK_TRSP_CAT_BULK_DUMPED);
				add(Constant.TRUCK_TRSP_CAT_CONCRETE);
				add(Constant.TRUCK_TRSP_CAT_ALL_MAT_PACKAGE_TYPES);
			}
		});
		
		
		/*
		 * Implementation of Table 6 in the LRQ 185.0/185.1 SDS (see LOGON-5043)
		 */
		final Set<Long> trailerAllowedForBulkPowder = Collections.unmodifiableSet(new HashSet<Long>() {
			private static final long serialVersionUID = 1L;
			{
				add(Constant.TRAILER_TKATEGORIE_BULK_POWDER);
				add(Constant.TRAILER_TKATEGORIE_CONTAINER);
				add(Constant.TRAILER_TKATEGORIE_ALL_MAT_PACKAGE_TYPES);
			}
		});
		
		final Set<Long> trailerAllowedForBag = Collections.unmodifiableSet(new HashSet<Long>() {
			private static final long serialVersionUID = 1L;
			{
				add(Constant.TRAILER_TKATEGORIE_BAG);
				add(Constant.TRAILER_TKATEGORIE_BULK_DUMPED);
				add(Constant.TRAILER_TKATEGORIE_ALL_MAT_PACKAGE_TYPES);
			}
		});
		
		// Service orders are not scheduled on trucks or trailers.
		final Set<Long> trailerAllowedForService = Collections.unmodifiableSet(new HashSet<Long>());
			
		final Set<Long> trailerAllowedForBulkDumped = Collections.unmodifiableSet(new HashSet<Long>() {
			private static final long serialVersionUID = 1L;
			{
				add(Constant.TRAILER_TKATEGORIE_BULK_DUMPED);
				add(Constant.TRAILER_TKATEGORIE_CONCRETE);
				add(Constant.TRAILER_TKATEGORIE_ALL_MAT_PACKAGE_TYPES);
			}
		});
		
		suitableTruckTrspCatsForPackageTypes.put(Constant.PACKAGE_TYPE_BULK_POWDER, truckAllowedForBulkPowder);
		suitableTruckTrspCatsForPackageTypes.put(Constant.PACKAGE_TYPE_BBAG, truckAllowedForBag);
		suitableTruckTrspCatsForPackageTypes.put(Constant.PACKAGE_TYPE_SERVICE, truckAllowedForService);
		suitableTruckTrspCatsForPackageTypes.put(Constant.PACKAGE_TYPE_BULK_DUMPED, truckAllowedForBulkDumped);
		
		suitableTrailerTrspCatsForPackageTypes.put(Constant.PACKAGE_TYPE_BULK_POWDER, trailerAllowedForBulkPowder);
		suitableTrailerTrspCatsForPackageTypes.put(Constant.PACKAGE_TYPE_BBAG, trailerAllowedForBag);
		suitableTrailerTrspCatsForPackageTypes.put(Constant.PACKAGE_TYPE_SERVICE, trailerAllowedForService);
		suitableTrailerTrspCatsForPackageTypes.put(Constant.PACKAGE_TYPE_BULK_DUMPED, trailerAllowedForBulkDumped);
	}
	
	/**
	 * Returns the complete map from all known package types to trailer transport categories.
	 * @return
	 */
	public static Map<Long,Set<Long>> getTrailerTrspCatForAllPackageTypes()
	{
		return suitableTrailerTrspCatsForPackageTypes;
	}

	/**
	 * Returns the complete map from all known package types to truck transport categories.
	 * @return
	 */
	public static Map<Long,Set<Long>> getTruckTrspCatForAllPackageTypes()
	{
		return suitableTruckTrspCatsForPackageTypes;
	}

	/**
	 * Returns the set of truck transport categories for a given package type.
	 * @param packageType A material package type, e.g. <code>Constant.PACKAGE_TYPE_BULK</code>
	 * @return a set containing all allowed truck transport categories for this
	 * package type. If the package type is unknown, the empty set is returned (and
	 * a warning logged). 
	 */
	public static Set<Long> getTruckTrspCatForPackageType(long packageType) 
	{
		if (!suitableTruckTrspCatsForPackageTypes.containsKey(packageType)) {
			LOG.warn("No truck transport categories found for package type '{}'", new Object[] { packageType });
			return Collections.unmodifiableSet(new HashSet<Long>());
		}
		else
		{
			return  Collections.unmodifiableSet(suitableTruckTrspCatsForPackageTypes.get(packageType));
		}
	}

	/**
	 * Returns the set of trailer transport categories for a given package type.
	 * @param packageType A material package type, e.g. <code>Constant.PACKAGE_TYPE_BULK</code>
	 * @return a set containing all allowed trailer transport categories for this
	 * package type. If the package type is unknown, the empty set is returned (and
	 * a warning logged). 
	 */
	public static Set<Long> getTrailerTrspCatForPackageType(long packageType) 
	{
		if (!suitableTrailerTrspCatsForPackageTypes.containsKey(packageType)) {
			LOG.warn("No trailer transport categories found for package type '{}'", new Object[] { packageType });
			return Collections.unmodifiableSet(new HashSet<Long>());
		}
		else
		{
			return  Collections.unmodifiableSet(suitableTrailerTrspCatsForPackageTypes.get(packageType));
		}
	}

	public static boolean isTruckCatAllowedForPackageType(Long truckCatCd, Long packageTypeCd)
	{
		if (!suitableTruckTrspCatsForPackageTypes.containsKey(packageTypeCd)) 
		{
			LOG.warn("Unknown package type code: '{}'", new Object [] {packageTypeCd});
			return false;
		}
		else {
			return suitableTruckTrspCatsForPackageTypes.get(packageTypeCd).contains(truckCatCd);
		}
	}

	public static boolean isTrailerCatAllowedForPackageType(Long trailerCatCd, Long packageTypeCd)
	{
		if (!suitableTrailerTrspCatsForPackageTypes.containsKey(packageTypeCd)) 
		{
			LOG.warn("Unknown package type code: '{}'", new Object [] {packageTypeCd});
			return false;
		}
		else {
			return suitableTrailerTrspCatsForPackageTypes.get(packageTypeCd).contains(trailerCatCd);
		}
	}
}
