/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.util.Locale;

import javax.ejb.Local;

import com.crh.logon.business.common.base.GenericReadOnlyInterface;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;

/**
 * @author AdNovum Informatik AG
 */
@Local
public interface SapMaterialDescriptionInterface extends GenericReadOnlyInterface<SapMaterialDescEntity> {

	public String getMaterialDescription(Long sapMaterialId, Locale locale);
}
