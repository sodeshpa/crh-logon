/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.List;
import java.util.Locale;

import javax.ejb.Stateless;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.dispatchers.ProductChangeMatrixViewServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.ProductChangeMatrixViewServiceLocalInterface;
import com.crh.logon.persistence.entities.views.MaterialCompatEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class ProductChangeMatrixViewServiceBean extends ProductChangeMatrixViewServiceDispatcher
	implements ProductChangeMatrixViewServiceLocalInterface {
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ProductChangeMatrixViewServiceBean.class);

	/**
	 * Load compatibilities.
	 * @adnlog.exclude return
	 */
	public List loadMaterialCompats(Locale locale) {
		// [[
		Object[][] params = {{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadMaterialCompats", params);
		}
		// ]]
		try {
			
			ReadOnlyDao<MaterialCompatEntity> dao = getDaoProvider().getMaterialCompatEntityDao();
			
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(MaterialCompatEntity.ATTR_LANG_CD, locale.getLanguage()));
			filter.addExpression(Order.asc(MaterialCompatEntity.ATTR_FROM_SAP_MATERIAL_NO));
			
			return dao.find(filter);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
		
	}
}
