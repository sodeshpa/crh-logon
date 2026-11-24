/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.common.base;

import ch.nevis.gdi.jpa.BaseEntity;

public abstract class GenericDictionaryProvider<BaseEntityClass extends BaseEntity, TextEntityClass extends BaseEntity, PrimaryKeyType extends Object> extends GenericReadOnlyProvider<BaseEntityClass> implements GenericDictionaryInterface<BaseEntityClass, TextEntityClass, PrimaryKeyType> {  
	
	public abstract TextEntityClass findText(PrimaryKeyType id, String languageCode);
} 