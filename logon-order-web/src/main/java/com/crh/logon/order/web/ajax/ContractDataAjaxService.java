/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.DispokarteService;
import com.crh.logon.business.services.utils.SortOrder;
import com.crh.logon.persistence.entities.views.SapContractDispEntity;
import com.crh.logon.persistence.entities.views.SapContractItemDispEntity;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to
 * AJAX components.
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="ContractDataAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.views.SapContractItemDispEntity"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.business.sap.dto.SapContractDispDto"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 */
public class ContractDataAjaxService extends DwrServiceSupport {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ContractDataAjaxService.class);

	/**
	 * Returns the list of contract data
	 * @param skip number of elements to skip from the beginning
	 * @param count number of elements to return
	 * @param sort "ASC" or "DESC", SAP order if null or empty
	 * @param query {@link Map} of query parameters
	 * @param request the {@link HttpServletRequest}
	 * @param session the {@link HttpSession}
	 * @param showOnlyActiveContracts if true, only contracts/contract items with ACTIVE_FLAG == true should be returned
	 * @return {@link ListRange} of contract data
	 */
	@AdnTrace
	public ListRange searchContractData(
			int skip,
			int count,
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session,
			boolean showOnlyActiveContracts) {
		Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);

		SortOrder sortOrder = null;
		if (StringUtils.notEmpty(sort)) {
			sortOrder = SortOrder.parse(sort);
		}
		else {
			sortOrder = SortOrder.asc(SapContractDispEntity.ATTR_SAP_CONTRACT_NO);
		}

		DispokarteService service = getServiceLocator().getDispokarteService();
		Long selectedShipTo = Long.valueOf(query.get("selectedShipTo"));
		Long selectedSoldTo = Long.valueOf(query.get("selectedSoldTo"));
		Locale locale = request.getLocale();
		ListRange result = service.fetchSapContractDispDtoList(selectedShipTo, selectedSoldTo, skip,
				count, locale, sortOrder.getPropertyName(), sortOrder.isAscending(),
				showOnlyActiveContracts);
		return result;
	}
	
	/**
	 * Returns list of SAP Contracts Items
	 * @param skip number of elements to skip from the beginning
	 * @param count number of elements to return
	 * @param sort "ASC" or "DESC", SAP order if null or empty
	 * @param query {@link Map} of query parameters
	 * @param request the {@link HttpServletRequest}
	 * @param session the {@link HttpSession}
	 * @return {@link ListRange} of contract data
	 */
	@AdnTrace
	public ListRange searchContractItems(
			int skip,
			int count,
			String sort,
			Map<String, String> query,
			HttpServletRequest request,
			HttpSession session) {
			Authorization.authorize(Authorization.MASTER_DATA_SHIPTO_CARD_R);
			
			SortOrder sortOrder = null;
			if (StringUtils.notEmpty(sort)) {
				sortOrder = SortOrder.parse(sort);
			} else {
				sortOrder = SortOrder.asc(SapContractItemDispEntity.ATTR_CONTRACT_ITEM_NO);
			}
			
			DispokarteService service = getServiceLocator().getDispokarteService();
			Locale locale = request.getLocale();
			Long sapContractId = Long.valueOf(query.get("sapContractId"));
			ListRange result;
			return result = service.fetchSapContractItemDispEntityList(sapContractId, locale, skip, count, sortOrder.getPropertyName(), sortOrder.isAscending());			
	}
}
