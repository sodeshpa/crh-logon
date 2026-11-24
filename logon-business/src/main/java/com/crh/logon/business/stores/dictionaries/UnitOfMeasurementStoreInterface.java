/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.dictionaries;

import java.util.List;
import java.util.Locale;
import java.util.Set;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericDictionaryInterface;
import com.crh.logon.persistence.entities.refdata.UomBsEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;

@Local
public interface UnitOfMeasurementStoreInterface extends GenericDictionaryInterface<UomBsEntity, UomTxEntity, String> {
	
	/**
	 * It returns with the given unitOfMeasurements in the declared locale
	 * 
	 * @param unitOfMeasurementCodes
	 * @param locale
	 * @return List<UomTxEntity>
	 */
	public List <UomTxEntity> findTextsByCodes(Set<String> unitOfMeasurementCodes, Locale locale);
}
