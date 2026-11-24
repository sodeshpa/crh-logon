/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.utils;

import java.util.Comparator;

import com.crh.logon.business.services.utils.BizeventAuditConfiguration;
import com.crh.logon.business.services.utils.BizeventAuditConfiguration.AUDIT_GROUP;
import com.crh.logon.business.services.utils.BizeventAuditUtil;
import com.crh.logon.business.services.utils.BizeventAuditWrapper;

/**
 * Order wrappers according to their place defined in {@link BizeventAuditConfiguration.AUDIT_GROUP}.
 * 
 * UI needs to make sure that for example the two wrappers saved for group{@link BizeventAuditConfiguration.AUDIT_GROUP#ORDERED}
 * appear next to each other, but in the sequence 1 tons not tons 1.
 * 
 * @author AdNovum Informatik AG
 */
public class BizeventAuditWrapperComparator implements Comparator<BizeventAuditWrapper> {


	@Override

	public int compare(BizeventAuditWrapper o1, BizeventAuditWrapper o2) {

		final int firstLevel = o1.getEntity().getBizeventId().compareTo(o2.getEntity().getBizeventId());
		/**
		 * If the bizevent id differs, leave ordering untouched.
		 */
		if ( firstLevel != 0 ) {
			return 0;
		}
		Long cd1 = ((BizeventAuditWrapper)o1).getEntity().getAuditBizeventConfigCd();
		Long cd2 = ((BizeventAuditWrapper)o2).getEntity().getAuditBizeventConfigCd();

		AUDIT_GROUP group1 = BizeventAuditUtil.getGroupOfAuditConfigCd(cd1);
		AUDIT_GROUP group2 = BizeventAuditUtil.getGroupOfAuditConfigCd(cd2);

		/**
		 * If either of the wrappers is not a grouped wrapper, leave ordering untouched.
		 */
		if ( group1 == null && group2 != null ) {
			return 0;
		}
		else if (group1 != null && group2 == null ) {
			return 0;
		}
		else if (group1 == null && group2 == null ) {
			return 0;
		}
		else if (group1 != null && group2 != null && (group1.getNr() != group2.getNr() )) {
			return 0;
		}

		/**
		 * Order according to the specified ordering withing the group.
		 */

		return Integer.valueOf(group1.getAuditCodes().indexOf(cd1)).compareTo(group2.getAuditCodes().indexOf(cd2));
	}
}
