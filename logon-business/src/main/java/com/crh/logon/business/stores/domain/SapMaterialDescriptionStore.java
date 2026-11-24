/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.util.Locale;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

/**
 * @author AdNovum Informatik AG
 */

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class SapMaterialDescriptionStore extends GenericReadOnlyProvider<SapMaterialDescEntity>
	implements SapMaterialDescriptionInterface {

	@Override
	public String getMaterialDescription(Long sapMaterialId, Locale locale) {	
		ReadOnlyDao<SapMaterialDescEntity> descDao = getDaoProvider().getSapMaterialDescEntityDao();
		Filter<SapMaterialDescEntity> filter = new FilterImpl<SapMaterialDescEntity>();
		
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID, sapMaterialId));
		filter.addExpression(Restrictions.eq(SapMaterialDescEntity.ATTR_LANG_CD, locale.getLanguage()));
		
		SapMaterialDescEntity descEntity = descDao.findSingle(filter);
		String result = "";
		
		if (descEntity != null) {
			result = descEntity.getTxt();
		}
		return result;
	}

}
