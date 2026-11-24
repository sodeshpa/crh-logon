/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.entities.common.MopeventDataOutEntity;
import com.crh.logon.persistence.entities.common.MopeventHeaderOutEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class MopeventDataOutStore extends GenericReadOnlyProvider<MopeventDataOutEntity> implements MopeventDataOutStoreInterface {

	public Long getMopDataOutFieldLong(MopeventHeaderOutEntity header, String fieldName) {
		String fieldString = getMopDataOutFieldValue(header, fieldName);
		Long result = null;
		
		if (fieldString != null) {
			result = Long.valueOf(fieldString);
		}
		
		return result;
	}

	public String getMopDataOutFieldValue(MopeventHeaderOutEntity header, String fieldName) {
		if (fieldName != null) {
			fieldName.toLowerCase();
		}
		
		ReadOnlyDao<MopeventDataOutEntity> dao = getDaoProvider().getMopeventDataOutEntityDao();
		Filter<MopeventDataOutEntity> filter = dao.createFilter();
		
		filter.addExpression(Restrictions.eq(MopeventDataOutEntity.ATTR_MOPEVENT_HEADER_OUT, header));
		filter.addExpression(Restrictions.eq(MopeventDataOutEntity.ATTR_NAME, fieldName));
		
		MopeventDataOutEntity data = dao.findSingle(filter);
		
		if (data == null) {
			return null;
		}
		
		return data.getValue();
	}
}
