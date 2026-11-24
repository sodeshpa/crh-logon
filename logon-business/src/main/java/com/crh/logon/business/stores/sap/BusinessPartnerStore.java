package com.crh.logon.business.stores.sap;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import com.crh.logon.business.common.base.GenericReadOnlyProvider;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShiptoBilltoEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.common.ShiptoSareaEntity;
import com.crh.logon.persistence.entities.refdata.UomTxEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.views.SapSoldPaySareaSrchEntity;

@Stateless
@TransactionAttribute(TransactionAttributeType.MANDATORY)
@AdnTrace
public class BusinessPartnerStore extends GenericReadOnlyProvider<UomTxEntity> implements BusinessPartnerInterface{
	
	public List<SapBpEntity> fetchPayers(Set<Long> businessPartnerIds) {
			final ReadOnlyDao<SapBpEntity> dao = getDaoProvider().getSapBpEntityDao();
			final Filter<SapBpEntity> filter = dao.createFilter();
			filter.addExpression(Restrictions.in(SapBpEntity.ATTR_SAP_BP_ID, businessPartnerIds));
			filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_PAYER, Boolean.TRUE));
			filter.addExpression(Restrictions.neq(SapBpEntity.ATTR_MARKED_FOR_DELETION, Constant.MARKED_FOR_DELETION));
			filter.addExpression(Order.asc(SapBpEntity.ATTR_NAME1));
			filter.addExpression(Order.asc(SapBpEntity.ATTR_NAME2));
			
			return dao.find(filter);
	}

	public SapBpEntity getSoldToSapBpEntityBySapIfcId(String sapIfcId) {
		final ReadOnlyDao<SapBpEntity> dao = getDaoProvider().getSapBpEntityDao();
		final Filter<SapBpEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SOLDTO, Boolean.TRUE));
		filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_IFC_ID, sapIfcId));
		return dao.findSingle(filter);
	}

	public ShiptoEntity getShiptoEntityBySapIfcId(String sapIfcId) {
		final ReadOnlyDao<ShiptoEntity> dao = getDaoProvider().getShiptoEntityDao();
		final Filter<ShiptoEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP + "." + SapBpEntity.ATTR_BP_IS_SHIPTO, Boolean.TRUE));
		filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_SHIPTO_SAP_BP + "." + SapBpEntity.ATTR_SAP_IFC_ID, sapIfcId));
		filter.addExpression(Restrictions.eq(ShiptoEntity.ATTR_ACTIVE_FLAG, Boolean.TRUE));
		return dao.findSingle(filter);
	}

	public SapBpEntity getBilltoSapBpEntityBySapIfcId(String sapIfcId) {
		SapBpEntity billtoSapBp;
		final ReadOnlyDao<SapBpEntity> dao = getDaoProvider().getSapBpEntityDao();
		final Filter<SapBpEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_BILLTO, Boolean.TRUE));
		filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_IFC_ID, sapIfcId));
		billtoSapBp = dao.findSingle(filter);
		return billtoSapBp;
	}

	public SapBpEntity getPayerSapBpEntityBySapId(String sapIfcId) {
		SapBpEntity payerSapBp;
		final ReadOnlyDao<SapBpEntity> dao = getDaoProvider().getSapBpEntityDao();
		final Filter<SapBpEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_PAYER, Boolean.TRUE));
		filter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_IFC_ID, sapIfcId));
		payerSapBp = dao.findSingle(filter);
		return payerSapBp;
	}

	/**
	 * Loads a list of billtos.
	 * @adnlog.exclude return
	 */
	public List<ShiptoBilltoEntity> loadBilltos(Long shiptoId, Long sareaId) {
		final ReadOnlyDao<ShiptoBilltoEntity> dao = getDaoProvider().getShiptoBilltoEntityDao();
		final Filter<ShiptoBilltoEntity> filter =  dao.createFilter();

		filter.addExpression(Restrictions.eq(ShiptoBilltoEntity.ATTR_SHIPTO_SAREA + "." + ShiptoSareaEntity.ATTR_SHIPTO_ID, shiptoId));
		filter.addExpression(Restrictions.eq(ShiptoBilltoEntity.ATTR_SHIPTO_SAREA + "." + ShiptoSareaEntity.ATTR_SAREA_ID, sareaId));
		filter.addExpression(Restrictions.eq(ShiptoBilltoEntity.ATTR_BILLTO_SAP_BP + "." + SapBpEntity.ATTR_MARKED_FOR_DELETION, "0"));
		return dao.find(filter);
	}

	/**
	 * Loads a list of Payer SapBpEntities belonging to the specified Sold To and Sales Area.
	 *
	 * @param sareaId
	 * @param soldtoSapBpId
	 * @adnlog.exclude return
	 */
	@AdnTrace
	public List<SapBpEntity> loadPayers(Long sareaId, Long soldtoSapBpId) {
		// 1) Fetch SOLDTO_SAP_BP_IDs from view
		ReadOnlyDao<SapSoldPaySareaSrchEntity> sareaDao =
				getDaoProvider().getSapSoldPaySareaSrchEntityDao();

		Filter<SapSoldPaySareaSrchEntity> sareaFilter = new FilterImpl<SapSoldPaySareaSrchEntity>();
		sareaFilter.addExpression(Restrictions.eq(SapSoldPaySareaSrchEntity.ATTR_SAREA_ID, sareaId));
		sareaFilter.addExpression(Restrictions.eq(SapSoldPaySareaSrchEntity.ATTR_SOLDTO_SAP_BP_ID,
				soldtoSapBpId));

		JPAQuery jpaQuery = sareaDao.createJPAQuery();
		jpaQuery.setFilter(sareaFilter);
		jpaQuery.addSelect(SelectExpression.simpleSelect(
				SapSoldPaySareaSrchEntity.ATTR_PAYER_SAP_BP_ID));
		jpaQuery.setDistinct(true);
		List<Long> payerSapBpIdList = sareaDao.find(jpaQuery);

		// 2) Fetch Payer SapBpEntities
		List<SapBpEntity> payerSapBpList;

		if (payerSapBpIdList.isEmpty()) {
			payerSapBpList = new ArrayList();
		}
		else {
			ReadWriteDao<SapBpEntity> sapBpDao = getDaoProvider().getSapBpEntityDao();
			Filter<SapBpEntity> sapBpFilter = new FilterImpl<SapBpEntity>();
			sapBpFilter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_PAYER, Boolean.TRUE));
			sapBpFilter.addExpression(Restrictions.in(SapBpEntity.ATTR_SAP_BP_ID, payerSapBpIdList));
			sapBpFilter.addExpression(Order.asc(SapBpEntity.ATTR_NAME1));
			sapBpFilter.addExpression(Order.asc(SapBpEntity.ATTR_NAME2));
			payerSapBpList = sapBpDao.find(sapBpFilter);
		}

		return payerSapBpList;
	}
}
