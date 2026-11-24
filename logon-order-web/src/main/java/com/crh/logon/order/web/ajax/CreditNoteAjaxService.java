/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.ajax;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.stream.Collectors;
import javax.servlet.http.HttpServletRequest;

import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.interfaces.CreditNoteService;
import com.crh.logon.business.services.utils.Message;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.SortOrder;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.utils.CreditNoteBean;
import com.crh.logon.persistence.entities.views.CreditNoteProposalsEntity;
import com.crh.logon.utils.ExceptionUtil;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.ajax.DwrServiceSupport;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.DwrMessageBean;

/**
 * @author AdNovum Informatik AG
 * 
 * The purpose of this class is to make business functions available to 
 * Ajax components. 
 * 
 * Please note that this service is otherwise a totally normal spring
 * service. All functions within this service must be lightweight.
 * 
 * @adnbus.service name="CreditNoteAjaxService" dwr="true"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.order.web.utils.*"
 * @adnbus.dwr.convert converter="hibernate3" match="com.crh.logon.persistence.entities.*"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRange"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.utils.ListRangeWithMessages"
 * @adnbus.dwr.convert converter="bean" match="com.crh.logon.web.utils.DwrMessageBean" 
 * @adnbus.dwr.signature method="saveCreditNotes" parameters="List<CreditNoteBean> notes, HttpServletRequest request" imports="java.util.List, javax.servlet.http.HttpServletRequest, com.crh.logon.order.web.utils.CreditNoteBean"
 *
 */
public class CreditNoteAjaxService extends DwrServiceSupport {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(CreditNoteAjaxService.class);

	private static final String NO_ERROR = "noError";

	private static final String VALIDATION_ERROR = "validationError";

	private static final String TECHNICAL_ERROR = "technicalError";
	
	private static final String TRUE = "true";

	/**
	 * @param start
	 * @param count
	 * @param orderBy
	 * @param query
	 * @param request
	 * @return
	 */
	public ListRange fetchCreditNotes(int start, int count, String orderBy,
			String query, HttpServletRequest request) {
		// [[
		ListRange result = null;
		Object[][] params = {{"start", start + ""},
							{"count", count + ""},
							{"orderBy", orderBy},
							{"query", query},
							{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCreditNotes", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER_VIEW_2);

			ListRange listRange = new ListRange();
			String langCd = getLanguageCode(request);

			listRange.setData(
					getCreditNotes(start, count, orderBy, query, langCd));
			listRange.setTotalSize(
					getNumberOfCreditNotes(query, langCd));

			return result = listRange;
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
	 * @param notes
	 * @param request
	 * @return
	 */
	@AdnTrace
	public DwrMessageBean[] saveCreditNotes(List<CreditNoteBean> notes, HttpServletRequest request) {
		Authorization.authorize(Authorization.ORDER_VIEW_2);
		
		if (notes == null) {
			throw new LogonException("CreditNotes: no items to process.",
					Reason.RES_ERROR_IMPL);
		}
		
		Locale locale = request.getLocale();
		ServiceLocator srvLocator = getServiceLocator();
		CreditNoteService service = srvLocator.getCreditNoteService();
		final List<DwrMessageBean> responseBeans = new ArrayList<DwrMessageBean>();

		HashMap<Long, List<Long>> orderItemsByOrderIdMap = getBeansToSend(notes);
		List<Long> leftOutOrderIds = checkForLeftOutItems(notes, locale);
		
		if(leftOutOrderIds.isEmpty()) {
			service.processReturnQuantity(orderItemsByOrderIdMap, Boolean.TRUE);
			for (CreditNoteBean note : notes) {
				try {
					if (note == null) {
						continue;
					}
			
					boolean createNote = Constant.TRUE.equalsIgnoreCase(note.getYesFlag());
					boolean dontCreateNote = Constant.TRUE.equalsIgnoreCase(note.getNoFlag());
			
					if ((createNote && dontCreateNote) || 
							(!createNote && !dontCreateNote)) {
						responseBeans.add(
								createResponseForNote(
										note,
										VALIDATION_ERROR,
										"creditnote.list.valid.error.yesno",
										"", locale));
			
						continue;
					}
			
					if(dontCreateNote) {
						service._processReturnQuantity(note.getOrderItemId(), createNote);
					}
					// with the previous condition we checked, if only one of 
					// the 'Yes'/'No' flags are set at the same time.
					String additionalMsgKey = (createNote ? "creditnote.save.response.toSAPsubmitted" 
						: "creditnote.save.response.notSAPsubmitted");
					String additionalMsg = Message.lookup(locale, additionalMsgKey);

					responseBeans.add(createResponseForNote(note, NO_ERROR,
							"creditnote.save.response.noError", additionalMsg, locale));
				} 
				catch (LogonException lException) {
					Object[][] eParams = { { "note", note } };
					ExceptionUtil.errException(LOG, lException, eParams);
			
					String errorType = TECHNICAL_ERROR; 
					String messageKey = "creditnote.save.response.technicalError";
					responseBeans.add(
							createResponseForNote(
									note, 
									errorType, 
									messageKey, 
									lException.getId(), 
									locale));
				}
				catch (Exception exception) {
					Object[][] eParams = { { "note", note } };
					LogonException lException = null;
					String errorType = TECHNICAL_ERROR; 
					String messageKey = "creditnote.save.response.technicalError";
					if(exception.getCause() != null && exception.getCause() instanceof LogonException) {
						lException = (LogonException)exception.getCause();
						errorType = VALIDATION_ERROR;
						messageKey = lException.getMessage();
					} else {
						lException = new LogonException(exception);
					}
					ExceptionUtil.errException(LOG, lException, eParams);
			
					responseBeans.add(
							createResponseForNote(
									note, 
									errorType, 
									messageKey, 
									lException.getId(), 
									locale));
				}
			}
		} else {
			for (Long orderId : leftOutOrderIds) {
				String orderNumber = "";
				for (CreditNoteBean note : notes) {
					if (note.getOrderId().equals(orderId)) {
						orderNumber = note.getOrderNumber().toString();
					}
					break;
				}
				DwrMessageBean errorMessageBean = new DwrMessageBean();
				
				String dwrMessage = Message.lookup(locale, "creditnote.save.response.leftoutitem", new Object[] {orderNumber});

				errorMessageBean.setType(DwrMessageBean.MsgTypes.ERROR);
				errorMessageBean.setRecordId(orderNumber);
				errorMessageBean.setMessage(dwrMessage);
				
				responseBeans.add(errorMessageBean);
			}
		}
		
		return responseBeans.toArray(new DwrMessageBean[responseBeans.size()]);
	}

	/**
	 * @param notes - credit notes to be sent to SAP
	 * @return true if there were no left out items
	 */
	private List<Long> checkForLeftOutItems(List<CreditNoteBean> notes, Locale locale) {

		CreditNoteService service = getServiceLocator().getCreditNoteService();
		List<CreditNoteProposalsEntity> neededNodes = service.fetchCreditNotesListByOrderIds(
				notes.stream()
						.map(CreditNoteBean::getOrderId)
						.collect(Collectors.toList()),
				locale.getLanguage());

		List<CreditNoteBean> creditNoteBeans = mapCreditNoteProposalsToBeans(neededNodes);

		// Long == orderId, Integer == how many credit notes should be sent for the order number
		HashMap<Long, Integer> notesToSendMap = leftOutHelper(notes, false);
		HashMap<Long, Integer> neededNodesMap = leftOutHelper(creditNoteBeans, true);
		List<Long> result = new ArrayList<Long>();

		for (Map.Entry<Long, Integer> entry : notesToSendMap.entrySet()) {
			Long key = entry.getKey();
			if(!neededNodesMap.get(key).equals(entry.getValue())) {
				result.add(key);
			}
		}
		
		return result;
	}
	
	private HashMap<Long, Integer> leftOutHelper(List<CreditNoteBean> notes, boolean allNotes) {
		HashMap<Long, Integer> sendNotes = new HashMap<Long, Integer>();
		for (CreditNoteBean note : notes) {
			if (allNotes || Constant.TRUE.equals(note.getNoFlag()) || TRUE.equals(note.getYesFlag())) {
				Long orderId = note.getOrderId();
				Integer value = sendNotes.get(orderId);
				if (value != null) {
					value++;
				} else {
					value = 1;
				}
				sendNotes.put(note.getOrderId(), value);
			}
		}
		return sendNotes;
	}
	
	private HashMap<Long, List<Long>> getBeansToSend(List<CreditNoteBean> notes) {
		HashMap<Long, List<Long>> sendNotes = new HashMap<Long, List<Long>>();
		for (CreditNoteBean note : notes) {
			if (Constant.TRUE.equals(note.getNoFlag())) {
				continue;
			}
			Long orderId = note.getOrderId();
			List<Long> value = sendNotes.get(orderId);
			if (value != null) {
				value.add(note.getOrderItemId());
			} else {
				value = new ArrayList<Long>();
				value.add(note.getOrderItemId());
				sendNotes.put(note.getOrderId(), value);
			}
		}
		return sendNotes;
	}

	/**
	 * @param start
	 * @param count
	 * @param orderBy
	 * @param query
	 * @param langCd
	 * @return
	 */
	@AdnTrace
	private final CreditNoteBean[] getCreditNotes(int start, int count, String orderBy,
			String query, String langCd) {

		CreditNoteService service = getServiceLocator().getCreditNoteService();

		SortOrder sortOrder = null;
		if (org.apache.commons.lang.StringUtils.isNotBlank(orderBy)) {
			sortOrder = SortOrder.parse(orderBy);
		}

		final List<CreditNoteProposalsEntity> creditNotes = service.fetchCreditNotesList(start, count,
				sortOrder, query, langCd);
		final List<CreditNoteBean> creditNoteBeans = mapCreditNoteProposalsToBeans(creditNotes);

		return creditNoteBeans.toArray(new CreditNoteBean[0]);
	}

	private List<CreditNoteBean> mapCreditNoteProposalsToBeans(List<CreditNoteProposalsEntity> creditNoteProposalsEntities) {
		final List<CreditNoteBean> creditNoteBeans = new ArrayList<CreditNoteBean>();

		if ((creditNoteProposalsEntities != null) && (!creditNoteProposalsEntities.isEmpty())) {

			for (CreditNoteProposalsEntity creditNote : creditNoteProposalsEntities) {
				CreditNoteBean creditNoteBean = getCreditNoteBean(creditNote);

				if (creditNoteBean != null) {
					creditNoteBeans.add(creditNoteBean);
				}
			}
		}

		return creditNoteBeans;
	}

	/**
	 * @param query
	 * @param langCd
	 * @return
	 */
	private final long getNumberOfCreditNotes(String query, String langCd) {
		// [[
		long result = 0L;
		Object[][] params = {{"query", query},
							{"langCd", langCd}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getNumberOfCreditNotes", params);
		}
		// ]]
		try {
			CreditNoteService service = getServiceLocator().getCreditNoteService();
			
			return (result = service.countCreditNotesList(query, langCd));
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
	 * @param quantity
	 * @param uomTxt
	 * @return
	 */
	private final String getAmount(BigDecimal quantity, String uomTxt) {
		// [[
		String result = null;
		Object[][] params = {{"quantity", quantity},
							{"uomTxt", uomTxt}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAmount", params);
		}
		// ]]
		try {
			StringBuilder sBuilder = new StringBuilder();
			
			if (quantity != null) {
				sBuilder.append(quantity);
				sBuilder.append(" ");
			}
			
			if (StringUtils.notEmpty(uomTxt)) {
				sBuilder.append(uomTxt);
			}
			
			return (result = sBuilder.toString());
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
	 * @param cnpEntity
	 * @return
	 */
	private final CreditNoteBean getCreditNoteBean(CreditNoteProposalsEntity cnpEntity) {
		// [[
		CreditNoteBean result = null;
		Object[][] params = {{"cnpEntity", cnpEntity}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getCreditNoteBean", params);
		}
		// ]]
		try {
			if (cnpEntity != null) {
				String amount = null;
			
				result = new CreditNoteBean();
			
				result.setOrderNumber(cnpEntity.getOrderNumber());
				result.setOrderItemNo(cnpEntity.getOrderItemNo());
				result.setPlantName(cnpEntity.getPlantName());
				result.setSoldtoName(cnpEntity.getSoldtoName());
				result.setShiptoName(cnpEntity.getShiptoName());
				result.setBilltoName(cnpEntity.getBilltoName());
				result.setSapMaterialNo(cnpEntity.getSapMaterialNo());
				result.setMaterialTxt(cnpEntity.getMaterialTxt());
			
				// getting the amount of 'Liefermaenge'
				amount = getAmount(
						cnpEntity.getDeliveredQuantity(), 
						cnpEntity.getDeliveredUomTxt());
			
				result.setDeliveredQuantity(amount);
			
				// getting the amount of 'Rueckwaegemenge'
				amount = getAmount(
						cnpEntity.getReturnedQuantity(), 
						cnpEntity.getReturnedUomTxt());
			
				result.setReturnedQuantity(amount);
			
				result.setOrderId(cnpEntity.getOrderId());
				result.setOrderItemId(cnpEntity.getOrderItemId());
			}
			
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
	 * @param request
	 * @return
	 */
	private final String getLanguageCode(HttpServletRequest request) {
		// [[
		String result = null;
		Object[][] params = {{"request", request}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getLanguageCode", params);
		}
		// ]]
		try {
			if (request != null) {
				Locale locale = request.getLocale();
			
				if (locale != null) {
					result = locale.getLanguage();
				}
			
			}
			
			if (StringUtils.empty(result)) {
				result = Locale.ENGLISH.getLanguage();
			}
			
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
	 * @param note
	 * @param errorType
	 * @param messageKey
	 * @param additionalMessage
	 * @param locale
	 * @return
	 */
	private final DwrMessageBean createResponseForNote(CreditNoteBean note,
			String errorType, String messageKey, String additionalMessage,
			Locale locale) {
		// [[
		DwrMessageBean result = null;
		Object[][] params = {{"note", note},
							{"errorType", errorType},
							{"messageKey", messageKey},
							{"additionalMessage", additionalMessage},
							{"locale", locale}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createResponseForNote", params);
		}
		// ]]
		try {
			Long orderItemId = note.getOrderItemId();
			Long orderNo = note.getOrderNumber();
			Long orderItemNo = note.getOrderItemNo();
			
			String recordId = (orderItemId != null ? orderItemId.toString()
					: "");
			String orderNoStr = (orderNo != null ? orderNo.toString() : "");
			String orderItemNoStr = (orderItemNo != null ? 
					orderItemNo.toString() : "");
			
			String messageType = DwrMessageBean.MsgTypes.INFO;
			String dwrMessage = Message.lookup(locale, messageKey, new Object[] { orderNoStr, orderItemNoStr, additionalMessage });
			
			if (TECHNICAL_ERROR.equals(errorType) || 
					VALIDATION_ERROR.equals(errorType)) {
				messageType = DwrMessageBean.MsgTypes.ERROR;
			}
			else if (NO_ERROR.equals(errorType)){
				messageType = DwrMessageBean.MsgTypes.SUCCESS;
			}
			
			result = new DwrMessageBean();
			
			result.setType(messageType);
			result.setRecordId(recordId);
			result.setMessage(dwrMessage);
			
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

}
