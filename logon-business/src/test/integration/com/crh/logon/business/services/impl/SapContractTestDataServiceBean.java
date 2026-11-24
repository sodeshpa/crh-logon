/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.Restrictions;

import com.crh.logon.business.services.interfaces.SapContractTestDataService;
import com.crh.logon.business.stores.sap.ContractStoreInterface;
import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.persistence.entities.sap.SapContractItemEntity;
import com.crh.logon.persistence.utils.DaoProvider;

/**
 * @author AdNovum Informatik AG
 */
@Stateless
public class SapContractTestDataServiceBean implements SapContractTestDataService {

	private static final SapContractEntity DELETE_CONTRACT_REFERENCE = null;
	private static final SareaEntity DELETE_SAREA_REFERENCE = null;

	@PersistenceContext(unitName = "logon")
	EntityManager em;

	@EJB
	DispokarteService dispoKarteService;
	
	@EJB
	private ContractStoreInterface contractStore;

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public List<SapContractEntity> getTestContractsBySalesArea(
			String salesAreaLongDisplayName) {
		DaoProvider daoProvider = new DaoProvider(em,
				PlanningContractItemIntegrationTest.DO_NOT_AUTHORIZE);
		ReadOnlyDao<SareaEntity> salesAreaDao = daoProvider.getSareaEntityDao();
		Filter<SareaEntity> filter = new FilterImpl<SareaEntity>();
		filter.addExpression(Restrictions.eq(SareaEntity.ATTR_SAREA_DISPLAY_LONGNAME,
				salesAreaLongDisplayName));
		SareaEntity salesArea = salesAreaDao.findSingle(filter);
		Long salesAreaId = salesArea.getSareaId();
		List<SapContractEntity> initializedContracts = new ArrayList<SapContractEntity>();
		List<SapContractEntity> contracts = contractStore.findBySalesAreaIdentifier(salesAreaId);
		for (SapContractEntity contract : contracts) {
			Long sapContractId = contract.getSapContractId();
			contract = em.find(SapContractEntity.class, sapContractId);
			contract.initialize();
			initializedContracts.add(contract);
		}
		return initializedContracts;
	}
	
	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public SareaEntity setAllowPlanningMaterialFlag(SareaEntity salesArea, boolean value) {
		Long salesAreaId = salesArea.getSareaId();
		salesArea = em.find(SareaEntity.class, salesAreaId);
		salesArea.setAllowPlanningMaterialFlag(value);
		return salesArea;
	}

	@TransactionAttribute(TransactionAttributeType.REQUIRES_NEW)
	public void deleteContractsFromSalesArea(String salesAreaLongDisplayName) {
		List<SapContractEntity> contracts = getTestContractsBySalesArea(salesAreaLongDisplayName);
		for (SapContractEntity contract : contracts) {
			List<SapContractItemEntity> sapContractItems = contract.getSapContractItems();
			for (SapContractItemEntity contractItem : sapContractItems) {
				contractItem.setSapContract(DELETE_CONTRACT_REFERENCE);
				em.remove(contractItem);
			}
			contract.setSarea(DELETE_SAREA_REFERENCE);
			em.remove(contract);
		}
	}

}
