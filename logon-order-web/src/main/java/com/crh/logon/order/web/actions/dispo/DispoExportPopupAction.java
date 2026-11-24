package com.crh.logon.order.web.actions.dispo;

import java.text.ParseException;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.crh.logon.order.web.actions.reporting.AbstractTruckRevenuePlanningDownloadAction;
import com.crh.logon.order.web.forms.dispo.DispoExportPopupForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.SelectableGeneralDisplayNameDto;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;
import com.crh.logon.web.utils.Authorization;

/**
 * @author      AdNovum Informatik AG
 * @version     $Revision$ $Date$
 *
 * @struts.action name="order_dispo_DispoExportPopupForm"
 *  path="/dispo/DispoExportPopup" scope="session"
 *  validate="true" 
 *
 * @struts.action-forward name="success" path="DispoExportPopupTile.def"
 * @struts.action-forward name="failed" path="DispoTile2.def"
 */
public class DispoExportPopupAction extends AbstractTruckRevenuePlanningDownloadAction {

	private static final JcanLogger LOG =
		JcanLoggerFactory.getLogger(DispoExportPopupAction.class);

	@Override
	public ActionForward execute(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
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
			Authorization.authorize(Authorization.DISPATCHING_R);
			setSelectedTab("dispo", request);
			return result = super.execute(mapping, form, request, response);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	@Override
	protected DownloadData getDownloadData(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) {
		try {

			DispoExportPopupForm helpForm = (DispoExportPopupForm) form;
			String exportType = helpForm.getExportType();

			List<Long> selectedCarrierIDList = helpForm.getSelectedCarrierIDList();
			List<Long> selectedPoolIDList = helpForm.getSelectedPoolIDList();
			
			if (exportType.equals(DispoExportPopupForm.GENERATION_TYPE_PDF)){
				MimeTypeBsEntity mimeBs = getRefDataGenericsService().findBsEntity(MimeTypeBsEntity.class, Constant.MIME_TYPE_PDF);
				String stringFromDate = helpForm.getStringFromDate();
				String stringToDate = helpForm.getStringToDate();
				Date dateFrom = getUserTimezone().parseDayMonthYear(stringFromDate);
				Date dateTo = getUserTimezone().parseDayMonthYear(stringToDate);
				byte [] pdfByte = getServiceLocator().getTruckRevenuePlanningService().generatePdf(
						selectedCarrierIDList, selectedPoolIDList, dateFrom, dateTo, request.getLocale(), false,
						getUserTimezone());

				String fileName = "";
				if ( selectedCarrierIDList.size() == 1 ) {					
					Long carrierId = (Long)selectedCarrierIDList.get(0);
					String carrierName = getCarrierNameForId(helpForm.getCarriers(), carrierId);
					fileName = carrierName;
				}
				boolean showRange = !stringFromDate.equals(stringToDate);
				DownloadData data = new DownloadData(fileName + (!StringUtils.empty(fileName) ? "_" : "")
						+ getDateFileInFileNameFormat(dateFrom)
						+ (showRange ? (" - " + getDateFileInFileNameFormat(dateTo)) : "") + "."
						+ mimeBs.getFileExtension(), mimeBs.getMimeType(), pdfByte);
				return data;
			}
			else if (exportType.equals(DispoExportPopupForm.GENERATION_TYPE_EXCEL)){
				
				MimeTypeBsEntity mimeBs = getRefDataGenericsService().findBsEntity(MimeTypeBsEntity.class, Constant.MIME_TYPE_XLS);
				
				String stringFromDate = helpForm.getStringFromDate();
				Date dateFrom = getUserTimezone().parseDayMonthYear(stringFromDate);
				
				if ( selectedCarrierIDList.size() == 1 ) {					
					Long carrierId = (Long)selectedCarrierIDList.get(0);
					
					String carrierName = getCarrierNameForId(helpForm.getCarriers(), carrierId);
					
					byte[] pdfByte = getServiceLocator().getTruckRevenuePlanningService().generateXls(
							carrierId, selectedPoolIDList, dateFrom, request.getLocale(), getUserTimezone());
					DownloadData data = new DownloadData(carrierName
							+ (!StringUtils.empty(carrierName) ? "_" : "")
							+ getDateFileInFileNameFormat(dateFrom) + "." + mimeBs.getFileExtension(),
							mimeBs.getMimeType(), pdfByte);
					return data;
				}
				else {
					throw new LogonException("Single carrier selection is expected for excel generation",
							Reason.RES_ERROR_IMPL);
				}
			}
			else {
				throw new LogonException("Invalid document type", Reason.RES_ERROR_IMPL);
			}
		}
		catch (ParseException e) {
			throw new LogonException("Parse exception when generating pdf/excel", Reason.RES_ERROR_IMPL);
		}
	}

	/**
	 * Returns the name of the carrier belonging the given carrierId by iterating over the 
	 * available caerriers of the form.
	 */
	private String getCarrierNameForId(List<SelectableGeneralDisplayNameDto> carriers, Long carrierId) {
		String carrierlName = "";
		for (SelectableGeneralDisplayNameDto carrier : carriers) {
			if (carrierId.equals(carrier.getId())){
				carrierlName = carrier.getName() ;
				break;
			}
		}
		return carrierlName;
	}

}
