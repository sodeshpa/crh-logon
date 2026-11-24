package com.crh.logon.business.stores.sap;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.sap.SapContractEntity;
import com.crh.logon.utils.time.DateTimeUtils;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class ContractStore extends GenericReadOnlyProvider<SapContractEntity> implements
		ContractStoreInterface {

	@EJB
	private ShiptoSareaStoreInterface shiptoSareaStore;

	public List<SapContractEntity> findBySalesAreaIdentifier(Long salesAreaIdentifier) {
		ReadOnlyDao<SapContractEntity> sapContractDao = getDaoProvider().getSapContractEntityDao();
		final Filter<SapContractEntity> sapContractFilter = sapContractDao.createFilter();
		sapContractFilter.addExpression(Restrictions.eq(SapContractEntity.ATTR_SAREA_ID,
				salesAreaIdentifier));
		return sapContractDao.find(sapContractFilter);
	}

	public Set<Long> fetchContractsForShiptoSareaSoldto(
			Long salesAreaId,
			Long shiptoSapBpId,
			Long soldtoSapBpId,
			boolean filterOutFutureContracts) {

		Filter<SapContractEntity> filt = new FilterImpl<>();
		filt.addExpression(Restrictions.eq(SapContractEntity.ATTR_SAREA_ID, salesAreaId));

		return fetchContractsForShiptoSoldto(shiptoSapBpId, soldtoSapBpId, filterOutFutureContracts, filt)
				.stream()
				.map(SapContractEntity::getSapContractId)
				.collect(Collectors.toSet());
	}

	public Set<SapContractEntity> fetchContractEntitiesForShiptoSoldtoContractNo(
			Long shiptoSapBpId,
			Long soldtoSapBpId,
			String contractNumber,
			boolean filterOutFutureContracts) {

		Filter<SapContractEntity> filt = new FilterImpl<>();
		if (contractNumber != null) {
			filt.addExpression(Restrictions.eq(SapContractEntity.ATTR_SAP_CONTRACT_NO, contractNumber));
		}

		return fetchContractsForShiptoSoldto(shiptoSapBpId, soldtoSapBpId, filterOutFutureContracts, filt);
	}

	public Set<SapContractEntity> fetchContractEntitiesForShiptoSoldtoContractNoSorgCd(
			Long shiptoSapBpId,
			Long soldtoSapBpId,
			String contractNumber,
			String sorgCd,
			boolean filterOutFutureContracts) {

		Filter<SapContractEntity> filt = new FilterImpl<>();
		if (contractNumber != null) {
			filt.addExpression(Restrictions.eq(SapContractEntity.ATTR_SAP_CONTRACT_NO, contractNumber));
		}
		if (sorgCd != null) {
			filt.addExpression(Restrictions.eq(SapContractEntity.ATTR_SAREA + "." + SareaEntity.ATTR_SORG_CD, sorgCd));
		}

		return fetchContractsForShiptoSoldto(shiptoSapBpId, soldtoSapBpId, filterOutFutureContracts, filt);
	}

	@AdnTrace
	private Set<SapContractEntity> fetchContractsForShiptoSoldto(
			Long shiptoSapBpId,
			Long soldtoSapBpId,
			boolean filterOutFutureContracts,
			Filter<SapContractEntity> filt) {

		Authorizer.disableNextAccessRestriction();
		Date today = new Date();
		today = DateTimeUtils.getMorningOfDay(today);
		ReadOnlyDao<SapContractEntity> contractDao = getDaoProvider().getSapContractEntityDao();
		JPAQuery<SapContractEntity> query = contractDao.createJPAQuery();
		query.setFilter(filt);
		filt.addExpression(
				Restrictions.and(
						Restrictions.or(
								Restrictions.ge(SapContractEntity.ATTR_VALID_UNTIL_DAT, today),
								Restrictions.eq(SapContractEntity.ATTR_VALID_UNTIL_IGNORE_FLAG, true)
						),
						Restrictions.or(
								Restrictions.and(
										Restrictions.eq(SapContractEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId),
										Restrictions.eq(SapContractEntity.ATTR_SOLDTO_SAP_BP_ID, soldtoSapBpId)
								),
								Restrictions.and(
										Restrictions.eq(SapContractEntity.ATTR_SHIPTO_SAP_BP_ID, soldtoSapBpId),
										Restrictions.eq(SapContractEntity.ATTR_SOLDTO_SAP_BP_ID, soldtoSapBpId),
										Restrictions.neq(SapContractEntity.ATTR_SHIPTO_SAP_BP_ID, shiptoSapBpId)
								)
						)
				)
		);

		if(filterOutFutureContracts){
			filt.addExpression(Restrictions.le(SapContractEntity.ATTR_VALID_FROM_DAT, today));
		}

		final List<SapContractEntity> contracts = contractDao.find(query);

		final Set<SapContractEntity> contractSet;

		if (contracts != null && !contracts.isEmpty()) {
			contractSet = new HashSet<>(contracts);
		}
		else {
			contractSet = Collections.emptySet();
		}
		return contractSet;
	}

	public SapContractEntity getSapContractByContractNumber(String contractNumber) {
		final ReadOnlyDao<SapContractEntity> dao = getDaoProvider().getSapContractEntityDao();
		final Filter<SapContractEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SapContractEntity.ATTR_SAP_CONTRACT_NO, contractNumber));
		return dao.findSingle(filter);
	}
}
