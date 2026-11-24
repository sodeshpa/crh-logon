package com.crh.logon.persistence.utils;

import javax.persistence.EntityManager;

import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;

import ch.nevis.gdi.jpa.Authorizer;

public class DaoProviderNew extends DaoProvider {

	private ReadOnlyDaoImplNew<SapMaterialPlantEntity> sapMaterialPlantEntityDaoNew;

	public DaoProviderNew(EntityManager em) {
		super(em);
	}

	public DaoProviderNew(EntityManager em, Authorizer authorizer) {
		super(em, authorizer);
	}

	public ReadOnlyDaoImplNew<SapMaterialPlantEntity> getSapMaterialPlantEntityDaoNew() {
		if (sapMaterialPlantEntityDaoNew == null) {
			sapMaterialPlantEntityDaoNew = new ReadOnlyDaoImplNew<SapMaterialPlantEntity>(em, authorizer, SapMaterialPlantEntity.class);
		}
		return sapMaterialPlantEntityDaoNew;
	}
	
}
