/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.common.MopeventDataOutEntity;
import com.crh.logon.persistence.entities.common.MopeventHeaderOutEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface MopeventDataOutStoreInterface extends GenericReadOnlyInterface<MopeventDataOutEntity> {
	
	public Long getMopDataOutFieldLong(MopeventHeaderOutEntity header, String fieldName);
	
	public String getMopDataOutFieldValue(MopeventHeaderOutEntity header, String fieldName);
}
