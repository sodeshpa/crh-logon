/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.stores.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.inject.Named;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.business.services.pdftools.store.MaterialPlantNormComparator;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.MatPlantEntity;
import com.crh.logon.persistence.entities.common.OrderEntity;
import com.crh.logon.persistence.entities.common.OrderItemEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.refdata.MaterialPackageBsEntity;
import com.crh.logon.persistence.entities.refdata.PackageTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.SapCountryBsEntity;
import com.crh.logon.persistence.entities.refdata.SapLanguageBsEntity;
import com.crh.logon.persistence.entities.refdata.SapMaterialNormBsEntity;
import com.crh.logon.persistence.entities.refdata.SorgBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapMatPlantNormEntity;
import com.crh.logon.persistence.entities.sap.SapMaterialEntity;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
@Named
public class SapMaterialPlantNormStore extends GenericReadOnlyProvider<SapMatPlantNormEntity>
	implements SapMaterialPlantNormStoreInterface{
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(SapMaterialPlantNormStore.class);
	private static final String NO_COUNTRY = null;
	
	@Override
	public List<SapMatPlantNormEntity> getSapMatPlantNormList(MatPlantEntity materialPlant,
			OrderEntity order) {
		List <SapMatPlantNormEntity> sapMaterialPlantNorms = null;
		final ReadOnlyDao<SapMatPlantNormEntity> dao = getDaoProvider().getSapMatPlantNormEntityDao();
		
		final List<Long> sapMaterialIds = new ArrayList<Long>();
		List<OrderItemEntity> orderItems = order.getOrderItems();
		for (OrderItemEntity orderItem : orderItems) {
			SapMaterialEntity sapMaterial = orderItem.getSapMaterial();
			MaterialPackageBsEntity materialPackage = sapMaterial.getMaterialPackage();
			PackageTypeBsEntity logonPackageType = materialPackage.getLogonPackageType();
			if (logonPackageType.getPackageTypeCd() != Constant.PACKAGE_TYPE_SERVICE) {
				Long sapMaterialId = sapMaterial.getSapMaterialId();
				sapMaterialIds.add(sapMaterialId);
			}
		}
		
		if (sapMaterialIds.isEmpty()) { // Orders with service only materials doesn't have any norm info
			LOG.info("Only service item in the order. No MatPlanNorm info available.");
			return new ArrayList<SapMatPlantNormEntity>();
		}
		
		SapBpEntity shiptoSapBp = order.getShiptoSapBp();
		SapCountryBsEntity country = shiptoSapBp.getCountry();
		String shipToCountry = country.getSapCountryCd();
		
		final Filter<SapMatPlantNormEntity> filter = dao.createFilter();
		PlantBsEntity plant = materialPlant.getPlant();
		String plantCode = plant.getPlantCd();
		filter.addExpression(Restrictions.eq(SapMatPlantNormEntity.ATTR_PLANT_CD, plantCode));
		filter.addExpression(Restrictions.in(SapMatPlantNormEntity.ATTR_SAP_MATERIAL_ID, sapMaterialIds));
		SareaEntity salesArea = order.getSarea();
		SorgBsEntity salesOrganisation = salesArea.getSorg();
		String salesOrganisationCode = salesOrganisation.getSorgCd();
		filter.addExpression(Restrictions.or(
				Restrictions.eq(SapMatPlantNormEntity.ATTR_SORG_CD, salesOrganisationCode),
				Restrictions.isNull(SapMatPlantNormEntity.ATTR_SORG_CD)));
		filter.addExpression(Restrictions.eq(SapMatPlantNormEntity.ATTR_SHOW_LOGO_FLAG, true));
		filter.addExpression(Restrictions.eq(SapMatPlantNormEntity.ATTR_MARKED_FOR_DELETION, false));
		SapLanguageBsEntity sapLanguage = shiptoSapBp.getSapLanguage();
		filter.addExpression(Restrictions.eq(SapMatPlantNormEntity.ATTR_SAP_LANGUAGE, sapLanguage));
		filter.addExpression(Restrictions.or(
				Restrictions.isNotNull(SapMatPlantNormEntity.ATTR_SAP_MATERIAL_NORM + "." + SapMaterialNormBsEntity.ATTR_DN_LOGO_IMAGE_NAME),
				Restrictions.isNotNull(SapMatPlantNormEntity.ATTR_DN_LOGO_IMAGE_NAME)));

		List<SapMatPlantNormEntity> foundNorms = dao.find(filter);

		try {
			// LOGON-6634: first we prefer entries where the country matches; if we find none we fallback to entries where destCountry == null
			Map<String, List<SapMatPlantNormEntity>> foundNormsByNormCode = new HashMap<String, List<SapMatPlantNormEntity>>();

			for (SapMatPlantNormEntity currentNorm : foundNorms) {
				SapMaterialNormBsEntity sapMaterialNorm = currentNorm.getSapMaterialNorm();
				String currentNormCode = sapMaterialNorm.getSapMaterialNormCd();
				List<SapMatPlantNormEntity> normsForCurrentNormCode = foundNormsByNormCode.get(currentNormCode);
				if (normsForCurrentNormCode == null) {
					normsForCurrentNormCode = new ArrayList<SapMatPlantNormEntity>();
					foundNormsByNormCode.put(currentNormCode, normsForCurrentNormCode);
				}
				normsForCurrentNormCode.add(currentNorm);
			}

			sapMaterialPlantNorms = new ArrayList<SapMatPlantNormEntity>();
			Set<String> normCodes = foundNormsByNormCode.keySet();
			for (String normCode : normCodes) {
				List<SapMatPlantNormEntity> normsForCurrentNormCode = foundNormsByNormCode.get(normCode);
				List<SapMatPlantNormEntity> materialPlantNormsOfCountry = getMaterialPlantNormsOfCountry(
						shipToCountry, normsForCurrentNormCode);
				if (materialPlantNormsOfCountry.isEmpty()) {
					List<SapMatPlantNormEntity> normsWithoutCountry = getMaterialPlantNormsOfCountry(
							NO_COUNTRY, normsForCurrentNormCode);
					sapMaterialPlantNorms.addAll(normsWithoutCountry);
				}
				else {
					sapMaterialPlantNorms.addAll(materialPlantNormsOfCountry);
				}
			}
		}
		catch (Exception exception) {
			LOG.error("Failed to filter SapMatPlantNormEntity list by destCountry!", exception);
			return null;
		}

		Collections.sort(sapMaterialPlantNorms, new MaterialPlantNormComparator());

		return sapMaterialPlantNorms;
	}
	
	/**
	 * Collect material plant norms belonging to a destination country
	 * @param country
	 * @param materialPlantNorms
	 * @return
	 */
	private List<SapMatPlantNormEntity> getMaterialPlantNormsOfCountry(String country,
			List<SapMatPlantNormEntity> materialPlantNorms) {
		List<SapMatPlantNormEntity> materialPlantNormsOfCountry = new ArrayList<SapMatPlantNormEntity>();
		for (SapMatPlantNormEntity materialPlantNorm : materialPlantNorms) {
			String destinationCountry = materialPlantNorm.getDestCountry();
			if (destinationCountry == null && country == null) {
				materialPlantNormsOfCountry.add(materialPlantNorm);
			}
			else if (destinationCountry != null
					&& destinationCountry.equals(country)) {
				materialPlantNormsOfCountry.add(materialPlantNorm);
			}
		}
		return materialPlantNormsOfCountry;
	}
}
