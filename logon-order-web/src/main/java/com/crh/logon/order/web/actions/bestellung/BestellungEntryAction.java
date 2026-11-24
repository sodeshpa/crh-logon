/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.web.actions.bestellung;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.OrderActionSupport;
import com.crh.logon.order.web.forms.bestellung.BestellungForm;
import com.crh.logon.order.web.forms.bestellung.DummyFields;
import com.crh.logon.order.web.forms.truck.ChartForm;
import com.crh.logon.order.web.utils.OrderTakingUtils;
import org.apache.commons.lang.BooleanUtils;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.utils.OrderTakingEditorDto;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.DailyOrderStatusBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.time.DateTimeParser;
import com.crh.logon.utils.time.DateTimeUtils;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.BestellungChartBean;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_bestellung_BestellungForm"
 *  path="/bestellung/BestellungEntry" scope="session"
 *  validate="false"
 *
 * @struts.action-forward name="success" path="BestellungTile.def"
 * @struts.action-forward name="successAjax" path="BestellungOrderProcessingResultTile.def"
 * @struts.action-forward name="saveCustomerOrderSuccess" path="BestellungOrderProcessingResultTile.def"
 * @struts.action-forward name="initfailed" path="/bestellung/BestellungShipToEntry.do"
 */
public class BestellungEntryAction extends OrderActionSupport {

    private static final JcanLogger LOG =
        JcanLoggerFactory.getLogger(BestellungEntryAction.class);

	public ActionForward execute(
		ActionMapping mapping, ActionForm form,
		HttpServletRequest request,	HttpServletResponse response)
		throws Exception
	{
		
		// [[
		ActionForward result = null;
		Object[][] params = {{"mapping", mapping},
							{"form", form},
							{"request", request},
							{"response", response}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "execute", params);
		}
		// ]]
		try {
			Authorization.authorize(Authorization.ORDER);
			
			String forwardName = null;
			
			// Get the struts form.
			BestellungForm helpForm = (BestellungForm) form;
			Locale locale = helpForm.getLocale();
			
			// --- TODO extract into separate action possibly ---
			String action = request.getParameter("action");
			if ("saveAjax".equals(action) || "saveCustomerOrder".equals(action)) {
				
				forwardName = "successAjax";

				initializeFormAndChart(helpForm, request, locale, true, false);
		    	
        		request.setAttribute("isEditorDisabled", true); // FIXME cleanup the disabled flag 
				
			}
			else if ("resetOrdersOnSelectedLnfTimeZone".equals(action)) {
				forwardName = "success";
				initializeFormAndChart(helpForm, request, locale, false, false);
			}
			// --- Initial page load ---
			else {
				forwardName = "success";

				setSelectedTab("ccbestellung", request);

				if (helpForm.shipToInitialized()) {
					if (helpForm.getShipTo().getContactPerson() != null) {
						helpForm.setSelectedContactPersonId(helpForm.getShipTo().getContactPerson().getPersonId());
					}

					initializeFormAndChart(helpForm, request, locale, true, true);
				}
				else {
					forwardName = "initfailed";
				}

			}
			return result = mapping.findForward(forwardName);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	private void initializeFormAndChart(BestellungForm helpForm,
										HttpServletRequest request,
										Locale locale,
										boolean reinitEditorListAndOrder,
										boolean initPage) throws Exception {

		if (reinitEditorListAndOrder) {
			List<OrderTakingEditorDto> editorList = new ArrayList();

			editorList.add(OrderTakingUtils.getOrderEditorInstance(getServiceLocator(),
																   helpForm.getShiptoMap(), locale));

			helpForm.setEditorList(editorList);

			helpForm.setOrderCommon(editorList.get(0).getOrder());
			//helpForm.setOrderItemCommon((OrderItemEntity)editorList.get(0).getOrder().getOrderItems().get(0)); // TODO cleanup

			// LOGON-5630: DeliveryNoteCollFlag is no longer saved on indiviadual Order but on CustOrder
			helpForm.setDelivnoteCollPaperFlag(BooleanUtils.toBoolean(editorList.get(0).getPrintCollDeliveryNoteFlag()));
		}

		helpForm.initShipto();
		initEditorFields(helpForm, initPage);

		// TODO move this all into populateForm()?
		ChartForm chartForm = (ChartForm)request.getSession().getAttribute(ChartForm.FORM_NAME);
		BestellungChartBean chartBean = chartForm.getChartBean();

		TimeZone shipToTz = helpForm.getShipToTimeZone();

		OrderTakingEditorDto selectedEditor = helpForm.getSelectedEditor();

		Calendar deliveryFrom = OrderTakingUtils.getEditorDeliveryFrom(selectedEditor, helpForm.getShipTo(), shipToTz);
		Calendar deliveryTo = OrderTakingUtils.getEditorDeliveryTo(selectedEditor, helpForm.getShipTo(), shipToTz);

		try {
			chartBean.setOpen1From(DateTimeParser.parseHourMinute(selectedEditor.getDeliveryFromTime()));
			chartBean.setOpen2To(DateTimeParser.parseHourMinute(selectedEditor.getDeliveryToTime()));
			chartBean.setDeliveryFrom(deliveryFrom.getTime());
			chartBean.setDeliveryTo(deliveryTo.getTime());
			chartBean.setSelectedDayStr(getUserTimezone().fmtAsDayMonthYear(chartBean.getDeliveryFrom())); // Default day is the first day
			chartBean.setDayList(new ArrayList());
			chartBean.getDayList().add(chartBean.getSelectedDayStr());
		}
		catch (ParseException e) {
			throw new LogonException("Unable to setup truck utilization chart", Reason.RES_ERROR_INIT);
		}
		// Used by the Chart servlet (it cannot access the BestellungForm directly)
		request.getSession(true).setAttribute(BestellungChartBean.SESSION_KEY, chartForm.getChartBean());
	}

	private void initEditorFields(BestellungForm form, Boolean initPage) {
		
		ShiptoEntity shipto = form.getShipTo();
		SapBpEntity shiptoSapBp = shipto.getShiptoSapBp();
		Long sapBpId = shiptoSapBp.getSapBpId();
		RefDataGenericsService refData = getRefDataGenericsService();
		
		if (initPage) {
			form.setOpenCustorderList(fetchOpenCustorders(shipto, form.getLocale()));
			form.setClosedCustorderList(fetchClosedCustorders(shipto, form.getLocale()));
		}
	    // --- Setup of Delivery To / From time fields ---
		Long open1FromNotNull = shipto.getOpen1From() != null ? shipto.getOpen1From() : 300;
		Long open2ToNotNull = shipto.getOpen2To() != null ? shipto.getOpen2To() : 1380;
		
		OrderTakingEditorDto selectedEditor = form.getSelectedEditor();
		
		selectedEditor.setDeliveryFromTime(DateTimeUtils.formatHoursMinutes( 
				DateTimeUtils.getHoursMinutes(open1FromNotNull)));
		selectedEditor.setDeliveryToTime(DateTimeUtils.formatHoursMinutes( 
				DateTimeUtils.getHoursMinutes(open2ToNotNull)));
		
	    // --- Daily order status ---
	    if (shipto.getDailyOrderContactFlag() != null && shipto.getDailyOrderContactFlag()) {
			Calendar now = form.getShipToTimeZone().asCalendar();
			Calendar todayMorning = DateTimeUtils.getDatePart(now);
	    	if (shipto.getDailyOrderStatus() == null || shipto.getDailyOrderLastContactDat() == null || shipto.getDailyOrderLastContactDat().before(todayMorning.getTime())) {
	    		shipto.setDailyOrderStatus(refData.findBsEntity(DailyOrderStatusBsEntity.class, Constant.DAILY_ORDER_STATUS_NOT_CONTACTED_YET));
	    	}
	    	form.setDailyOrderStatusCd(shipto.getDailyOrderStatus().getDailyOrderStatusCd());
	    }
		
		// --------------------
		// Object to store dummy values. This will be gradually eliminated as the DB model transforms to finalized
		// --------------------
	    DummyFields dummyFields = new DummyFields();
		dummyFields.setDelTimeMin(selectedEditor.getDeliveryFromTime());
		dummyFields.setDelTimeMax(selectedEditor.getDeliveryToTime());
	    form.setDummyFields(dummyFields);
	    
    	// --- Duration correction factor ---
	    selectedEditor.setDurationCorrFactor(1d);
	}
}
