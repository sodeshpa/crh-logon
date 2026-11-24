package com.crh.logon.persistence.utils;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import ch.nevis.gdi.jpa.Authorizer;
import ch.nevis.gdi.jpa.ReadOnlyDaoImpl;

import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialPlantEntity;

public class ReadOnlyDaoImplNew<T> extends
		ReadOnlyDaoImpl<SapMaterialPlantEntity> {

	public ReadOnlyDaoImplNew(EntityManager em, Authorizer authorizer,
			Class<SapMaterialPlantEntity> clazz) {
		super(em, authorizer, clazz);
	}

	public List<PlantBsEntity> findNew(Long materialId, Boolean filterOnProductionPlant) {
		StringBuilder qString = new StringBuilder();
		qString.append("select distinct p from SapMaterialPlantEntity as x inner join x.plant p where ");
		qString.append("x.sapMaterial.sapMaterialId = :materialId and ");
		if(filterOnProductionPlant) {
			qString.append("p.productionPlant = :plant_productionPlant and ");
		}
		qString.append("p.ctlActCd = :plant_ctlActCd order by p.plantName asc");
		Query query = this.em.createQuery(qString.toString());
		query.setParameter("materialId", materialId);
		if (filterOnProductionPlant) {
			query.setParameter("plant_productionPlant", filterOnProductionPlant);
		}
		query.setParameter("plant_ctlActCd", true);
		return (List<PlantBsEntity>) query.getResultList();
	}
	
}
