/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.localinterfaces.BestellungServiceLocalInterface;
import com.crh.logon.business.services.dispatchers.BimodalRailcarMonitoringServiceDispatcher;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.BimodalRailcarMonitoringServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.persistence.entities.refdata.CompanycodeBsEntity;
import com.crh.logon.persistence.entities.views.BimodalRailOrderEntity;
import com.crh.logon.persistence.entities.views.FvlSapBpEntity;
import com.crh.logon.persistence.entities.views.ProdPlantNameEntity;
import com.crh.logon.persistence.entities.views.SapMaterialDescEntity;
import com.crh.logon.utils.ListRange;

/**
 * @author AdNovum Informatik AG
 * 
 * @adnbus.service name="BimodalRailcarMonitoringAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 */
@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class BimodalRailcarMonitoringServiceBean extends BimodalRailcarMonitoringServiceDispatcher implements BimodalRailcarMonitoringServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(BimodalRailcarMonitoringServiceBean.class);

    @EJB private BestellungServiceLocalInterface bestellungService;
    @EJB private RefDataGenericsServiceLocalInterface referentialDataService;

	
	/**
	 * Returns those plants for which there is a bimodal rail order with an active assignment, or no assignment at all.
	 */
	public ListRange fetchPlantList(int skip, int num, List shiptoSapBps, String companyCode, String productionPlantTxt) {
		// [[
		ListRange result = null;
		Object[][] params = {{"skip", skip + ""},
							{"num", num + ""},
							{"shiptoSapBps", shiptoSapBps},
							{"companyCode", companyCode},
							{"productionPlantTxt", productionPlantTxt}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPlantList", params);
		}
		// ]]
		try {
			result =  new ListRange();
			ReadOnlyDao<BimodalRailOrderEntity> bimodalDao = getDaoProvider().getBimodalRailOrderEntityDao();
			
			JPAQuery<BimodalRailOrderEntity> query = bimodalDao.createJPAQuery();
			query.setDistinct(true);
			query.addSelect(SelectExpression.simpleSelect(BimodalRailOrderEntity.ATTR_PRODUCTION_PLANT_CD));
			
			final JPAQuery<BimodalRailOrderEntity> countquery = bimodalDao.createJPAQuery();
			countquery.addSelect(SelectExpression.countDistinct(BimodalRailOrderEntity.ATTR_PRODUCTION_PLANT_CD));
			
			final List resultList = bimodalDao.find(query);
			final Object count = bimodalDao.findSingle(countquery);
			
			if ( resultList.isEmpty() ) {
				result.setData(new Object[] {});
				result.setTotalSize(0);
				return result;
			}
			
			Filter <ProdPlantNameEntity> plantFilter = new FilterImpl<ProdPlantNameEntity>();
			plantFilter.addExpression(Restrictions.in(ProdPlantNameEntity.ATTR_PLANT_CD, resultList.toArray()));
			plantFilter.addExpression(Order.asc(ProdPlantNameEntity.ATTR_PLANT_NAME));

			ReadOnlyDao<ProdPlantNameEntity> prodPlantDao = getDaoProvider().getProdPlantNameEntityDao();
			List<ProdPlantNameEntity> find =  prodPlantDao.findNRecs(skip, num, plantFilter);
			
			result.setData(find.toArray());
			result.setTotalSize((Long)count);
			
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns those materials for which there is a bimodal rail order with an active assignment, or no assignment at all.
	 * 
	 * @param plantCd
	 * @param sapMaterialDescTxt
	 * @param locale
	 * @return
	 */
	public ListRange fetchSapMaterialList(int skip, int num, List shiptoSapBps, String companyCode, String plantCd, String sapMaterialDescTxt, Locale locale) {
		// [[
		ListRange result = null;
		Object[][] params = {{"plantCd", plantCd},
							{"sapMaterialDescTxt", sapMaterialDescTxt},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapMaterialList", params);
		}
		// ]]
		try {
			result =  new ListRange();
			
			ReadOnlyDao<BimodalRailOrderEntity> bimodalDao = getDaoProvider().getBimodalRailOrderEntityDao();
			
			final JPAQuery<BimodalRailOrderEntity> query = bimodalDao.createJPAQuery();
			query.setDistinct(true);
			query.addSelect(SelectExpression.simpleSelect(BimodalRailOrderEntity.ATTR_SAP_MATERIAL_ID));
			
			final JPAQuery<BimodalRailOrderEntity> countquery = bimodalDao.createJPAQuery();
			countquery.addSelect(SelectExpression.countDistinct(BimodalRailOrderEntity.ATTR_SAP_MATERIAL_ID));
			
			Filter<BimodalRailOrderEntity> bimodalRailFilter = bestellungService.getFilterForBimodalRailSearch(null, plantCd, shiptoSapBps, null, companyCode, Locale.ENGLISH.getLanguage());
			
			if ( sapMaterialDescTxt != null ) {				
				bimodalRailFilter.addExpression(Restrictions.ilike(BimodalRailOrderEntity.ATTR_SAP_MATERIAL_DESC, "%" + sapMaterialDescTxt + "%"));
			}
			
			query.setFilter(bimodalRailFilter);
			countquery.setFilter(bimodalRailFilter);
			
			final List resultList = bimodalDao.findNRecs(skip, num, query);
			final Object count = bimodalDao.findSingle(countquery);
			
			if ( resultList.isEmpty() ) {
				result.setData(new Object[] {});
				result.setTotalSize(0);
				return result;
			}
			
			final ReadOnlyDao<SapMaterialDescEntity> sapMaterialDescEntityDao = getDaoProvider().getSapMaterialDescEntityDao();
			Filter<SapMaterialDescEntity> matFilt =  new FilterImpl<SapMaterialDescEntity>();
			matFilt.addExpression(Restrictions.in(SapMaterialDescEntity.ATTR_SAP_MATERIAL_ID, resultList.toArray() ));
			matFilt.addExpression(Order.asc(SapMaterialDescEntity.ATTR_TXT));
			final List<SapMaterialDescEntity> find = sapMaterialDescEntityDao.findNRecs(skip, num, matFilt);
			
			result.setData(find.toArray());
			result.setTotalSize((Long)count);
			
			return result;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns the fvl shiptos for the specified company code.
	 */
	public List <FvlSapBpEntity> fetchFvlShiptos(String companyCode) {
		// [[
		List result = null;
		Object[][] params = {{"companyCode", companyCode}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchFvlShiptos", params);
		}
		// ]]
		try {
			final ReadOnlyDao<FvlSapBpEntity> shiptoEntityDao = getDaoProvider().getFvlSapBpEntityDao();
			
			Filter<FvlSapBpEntity> filter = new FilterImpl<FvlSapBpEntity>();
			if ( companyCode != null ) {				
				filter.addExpression(Restrictions.eq(FvlSapBpEntity.ATTR_COMPANYCODE_CD, companyCode));
			}
			
			return result = shiptoEntityDao.find(filter);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	/**
	 * Returns the company codes which the user is authorized for. 
	 */
	public List <CompanycodeBsEntity> fetchCompanyCodes() {
		// [[
		List result = null;
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCompanyCodes");
		}
		// ]]
		try {
			final ReadOnlyDao<FvlSapBpEntity> bimodalDao = getDaoProvider().getFvlSapBpEntityDao();
			final JPAQuery<FvlSapBpEntity> query = bimodalDao.createJPAQuery();
			query.setDistinct(true);
			query.addSelect(SelectExpression.simpleSelect(FvlSapBpEntity.ATTR_COMPANYCODE_CD));
			
			final List<String> find = bimodalDao.find(query);
			
			if ( !find.isEmpty() ) {
				
				Filter filter =  new FilterImpl();
				filter.addExpression(Restrictions.in(CompanycodeBsEntity.ATTR_COMPANYCODE_CD, find));
				return result = referentialDataService.findBsEntityFiltered(CompanycodeBsEntity.class, filter);
			}
			else {
				return new ArrayList<CompanycodeBsEntity>();
			}
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	/**
	 * Returns a list of bimodal rail orders for the specified filter parameters. Ordering is important as it is shown on the UI expecting this
	 * order, and adding rowspan accordingly.
	 * @param companyCode
	 * @param shiptoSapBpIds
	 * @param productionPlantCd
	 * @param sapMaterialId
	 * @return
	 */
	public List <BimodalRailOrderEntity> fetchBimodalRailOrders(String companyCode, List shiptoSapBpIds, String productionPlantCd, Long sapMaterialId, String langCd, String orderByColumn, boolean asc ) {
		// [[
		List result = null;
		Object[][] params = {{"companyCode", companyCode},
							{"shiptoSapBpIds", shiptoSapBpIds},
							{"productionPlantCd", productionPlantCd},
							{"sapMaterialId", sapMaterialId},
							{"langCd", langCd},
							{"orderByColumn", orderByColumn},
							{"asc", asc + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchBimodalRailOrders", params);
		}
		// ]]
		try {
			
			final ReadOnlyDao<BimodalRailOrderEntity> dao = getDaoProvider().getBimodalRailOrderEntityDao();
			
			Filter <BimodalRailOrderEntity> filter = bestellungService.getFilterForBimodalRailSearch(sapMaterialId, productionPlantCd,
					shiptoSapBpIds, null, companyCode,langCd);
			
			//important to keep this sort as rowspan is according to this attribute
			filter.addExpression(Order.asc(BimodalRailOrderEntity.ATTR_RAILWAY_STATION_NAME));
			if ( orderByColumn != null && asc ) {
				filter.addExpression(Order.asc(orderByColumn));	
			}
			else if ( orderByColumn != null && !asc ) {
				filter.addExpression(Order.desc(orderByColumn));
			}
			
			return result = dao.findNRecs(0, getConfigBean().getAppDefaultMonitoringBimodalmonitoringNumOfDisplayedOrders(), filter);
			
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}




}
