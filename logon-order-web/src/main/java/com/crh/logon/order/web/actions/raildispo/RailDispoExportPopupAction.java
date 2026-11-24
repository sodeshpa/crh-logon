package com.crh.logon.order.web.actions.raildispo;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.crh.logon.order.web.forms.raildispo.DispoForm;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.business.services.beans.RailDispoExportParameters;
import com.crh.logon.business.services.interfaces.RailDispoExportService;
import com.crh.logon.business.services.interfaces.RefDataGenericsService;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.LoadingServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.MonitoringTablesServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ShippingPointServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UomConverterServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.business.services.utils.ServiceLocator;
import com.crh.logon.business.services.utils.TimeZone;
import com.crh.logon.constants.Constant;
import com.crh.logon.order.web.utils.railexport.RailDataProvider;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;
import com.crh.logon.web.actions.ByteArrayDownloadAction;
import com.crh.logon.web.utils.Authorization;
import com.crh.logon.web.utils.MessageUtils;
import com.crh.logon.web.utils.ServiceLocatorProvider;

/**
* @author      AdNovum Informatik AG
*
* @struts.action name="order_raildispo_DispoForm"
*  path="/raildispo/RailDispoExportPopup" scope="session"
*  validate="true" input="RailDispoTile.def"
*  
* @struts.action-forward name="failed" path="RailDispoTile.def"
*/
public class RailDispoExportPopupAction extends ByteArrayDownloadAction {
	
	private UserServiceLocalInterface userService;
	private DispoServiceLocalInterface dispoService;
	private UomConverterServiceLocalInterface unitOfMeasurementConverterService;
	private LoadingServiceLocalInterface loadingService;
	private ShippingPointServiceLocalInterface shippingPointService;
	private MonitoringTablesServiceLocalInterface monitoringService;
	
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(RailDispoExportPopupAction.class);

	private static final DateFormat FILE_NAME_DATE_FORMAT = new SimpleDateFormat("yyyyMMdd");

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
			if (request.getParameter("export") != null) {
				try {
					Authorization.authorize(Authorization.RAIL_DISPATCHING_R);
					return result = super.execute(mapping, form, request, response);
					//returns null if export was successful => no action needed
				}
				catch (LogonException logonException) {
					LOG.error("rail dispo export was not successful", logonException);
					handleLogonException(logonException, request);
					return result = mapping.findForward("failed");
				}
				catch (Exception e) {
					LOG.error("rail dispo export was not successful", e);
					if (e.getCause() instanceof LogonException) {
						LogonException logonException = (LogonException)e.getCause();
						handleLogonException(logonException, request);
						return result = mapping.findForward("failed");
					}
					else {
						MessageUtils.addActionError(request, e);
						return result = mapping.findForward("failed");
					}
				}
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
	
	private void handleLogonException(LogonException logonException, HttpServletRequest request) {
		Reason reason = logonException.getReason();
		String reasonText = reason.getReason();
		MessageUtils.addActionError(request, reasonText);
	}

	@Override
	protected DownloadData getDownloadData(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {

		ServiceLocator serviceLocator = ServiceLocatorProvider.getServiceLocator();

		HttpSession session = request.getSession();

		RailDispoExportService railDispoExportService = serviceLocator.getRailDispoExportService();
		RefDataGenericsService refDataGenericsService = serviceLocator.getRefDataGenericsService();

		DispoForm dispoForm = (DispoForm) session.getAttribute(DispoForm.FORM_NAME);

		Locale locale = request.getLocale();
		TimeZone userTimezone = getUserTimezone();

		userService = serviceLocator.getLocalUserService();
		dispoService = serviceLocator.getLocalDispoService();
		unitOfMeasurementConverterService = serviceLocator.getLocalUomConverterService();
		loadingService = serviceLocator.getLocalLoadingService();
		shippingPointService = serviceLocator.getLocalShippingPointService();
		monitoringService = serviceLocator.getLocalMonitoringTablesService();

		RailDataProvider dataProvider = new RailDataProvider(userService, dispoService, unitOfMeasurementConverterService, 
															 loadingService, shippingPointService, monitoringService, 
															 userTimezone, dispoForm, locale);
		RailDispoExportParameters exportParameters = dataProvider.getExportParameters();

		byte[] xlsBytes = railDispoExportService.export(exportParameters);

		MimeTypeBsEntity mimeBs = refDataGenericsService.findBsEntity(MimeTypeBsEntity.class, Constant.MIME_TYPE_XLS);
		String fileExtension = mimeBs.getFileExtension();
		String mimeType = mimeBs.getMimeType();

		String exportFileName = getDownloadFileName(fileExtension);

		DownloadData data = new DownloadData(exportFileName, mimeType, xlsBytes);

		return data;

	}

	protected String getDownloadFileName(String fileExtension){

		Date current = new Date();
		String currentDateFormat = FILE_NAME_DATE_FORMAT.format(current);	
		String exportFileName = "rail_export_" + currentDateFormat + "." + fileExtension;

		return exportFileName;

	}

}