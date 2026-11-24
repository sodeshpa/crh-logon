/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */
package com.crh.logon.business.stores.dictionaries;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericDictionaryInterface;
import com.crh.logon.persistence.entities.refdata.MopStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.MopStatusTxEntity;

@Local
public interface MopStatusStoreInterface extends GenericDictionaryInterface<MopStatusBsEntity, MopStatusTxEntity, String> {
	
	/**
	 * Returns the {@link MopStatusTxEntity} for the given language code and status code.
	 * Currently all incoming interfaces use in certain amount this lookup table.
	 * 
	 * @param mopStatusCode
	 * @param locale
	 * @return
	 */
	public MopStatusTxEntity findText(String mopStatusCode, String languageCode);
}
