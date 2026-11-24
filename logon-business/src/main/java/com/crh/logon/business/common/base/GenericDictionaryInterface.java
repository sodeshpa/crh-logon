/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.common.base;

import ch.nevis.gdi.jpa.BaseEntity;

public interface GenericDictionaryInterface<BaseEntityClass extends BaseEntity, TextEntityClass extends BaseEntity, PrimaryKeyType> extends GenericReadOnlyInterface<BaseEntityClass> {
	/**
	 * It returns with the TxEntity related to the default Bs entity.  
	 * 
	 * @param id
	 * @param languageCode
	 * @return textEntity
	 */
	public abstract TextEntityClass findText(PrimaryKeyType id, String languageCode);
}
