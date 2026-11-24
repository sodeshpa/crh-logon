/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.admin.web.ajax;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.crh.logon.admin.web.forms.traileradmin.TrailerAdminForm;
import com.crh.logon.admin.web.forms.containeradmin.ContainerAdminForm;
import com.crh.logon.admin.web.forms.masterdata.LoadingPointBean;
import com.crh.logon.admin.web.forms.masterdata.ShippingPointForm;
import com.crh.logon.admin.web.forms.siloadmin.SiloAdminForm;
import com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm;
import com.crh.logon.admin.web.forms.useradmin.UserAdminForm;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.common.ContainerEntity;
import com.crh.logon.persistence.entities.common.LoadingpointEntity;
import com.crh.logon.persistence.entities.common.ShiptoSiloEntity;
import com.crh.logon.persistence.entities.common.TrailerEntity;
import com.crh.logon.persistence.entities.common.TruckEntity;
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
 * @adnbus.service name="AdminRFIDPopupAjaxService" dwr="true"
 */
public class AdminRFIDPopupAjaxService extends DwrServiceSupport {

	private static final Long NOT_A_PDA_USER = null;

	/**
	 * Called when rfid icon is pressed on the truckadminform, needs to set the plateno, because it
	 * is needed to generate the rfidobject's label, which must be unique.
	 * @param request
	 */
	public void selectTruckByPlateNumber(String plateNumber, HttpServletRequest request) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRUCK);
		HttpSession session = request.getSession();
		TruckAdminForm form = (TruckAdminForm) session.getAttribute(TruckAdminForm.formName);
		TruckEntity truck = form.getTruck();
		truck.setTruckPlateNo(plateNumber);
	}

	/**
	 * Called when rfid icon is pressed on the container adminform, needs to set the plateno, because it
	 * is needed to generate the rfidobject's label, which must be unique.
	 * @param request
	 */
	public void selectTrailerByPlateNumber(String plateNumber, HttpServletRequest request) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_TRAILER);
		HttpSession session = request.getSession();
		TrailerAdminForm form = (TrailerAdminForm) session.getAttribute(TrailerAdminForm.formName);
		TrailerEntity trailer = form.getTrailer();
		trailer.setTrailerPlateNo(plateNumber);
	}

	/**
	 * Called when rfid icon is pressed on the traileradminform, needs to set the containerNo, because it
	 * is needed to generate the rfidobject's label, which must be unique.
	 * @param request
	 */
	public void selectContainerByNumber(String containerNumber, HttpServletRequest request) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_CONTAINER);
		HttpSession session = request.getSession();
		ContainerAdminForm form = (ContainerAdminForm) session.getAttribute(ContainerAdminForm.formName);
		ContainerEntity container = form.getContainer();
		container.setContainerNo(containerNumber);
	}

	/**
	 * Called when rfid icon is pressed on the siloadminform, needs to set the siloname, because it
	 * is needed to generate the rfidobject's label, which must be unique.
	 * @param request
	 */
	public void selectSiloByName(String name, HttpServletRequest request) {
		Authorization.authorize(Authorization.MASTER_DATA_ROAD_SILO);
		HttpSession session = request.getSession();
		SiloAdminForm form = (SiloAdminForm) session.getAttribute(SiloAdminForm.formName);
		ShiptoSiloEntity silo = form.getSilo();
		silo.setSiloName(name);
	}

	/**
	 * Called when rfid icon is pressed on the useradminform, needs to set the pdausername, because it
	 * is needed to generate the rfidobject's label, which must be unique.
	 * @param request
	 */
	public void selectUserByPdaUserNumber(String pdaUserNumberText, HttpServletRequest request) {
		Authorization.authorize(Authorization.USER_ADMIN_LOGON);
		HttpSession session = request.getSession();
		UserAdminForm form = (UserAdminForm) session.getAttribute(UserAdminForm.formName);
		UserEntity user = form.getUserEntity();
		if (StringUtils.notEmpty(pdaUserNumberText)) {
			Long pdaUserNumber;
			try {
				pdaUserNumber = new Long(pdaUserNumberText);
			}
			catch (NumberFormatException numberFormatException) {
				pdaUserNumber = null;
			}
			user.setPdaUserNo(pdaUserNumber);
		}
		else {
			user.setPdaUserNo(NOT_A_PDA_USER);
		}
	}

	/**
	 * Called when rfid icon is pressed on the loadingpoint, needs to set the object label, because it
	 * is needed to generate the rfidobject's label, which must be unique.
	 * @param request
	 */
	public void selectLoadingPointByLabel(String loadingPointLabel, HttpServletRequest request) {
		Authorization.authorize(Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT);
		HttpSession session = request.getSession();
		ShippingPointForm form = (ShippingPointForm) session.getAttribute("admin_ShippingPointForm");
		List<LoadingPointBean> loadingPoints = form.getLoadingPointWrapperList();
		int currentLoadingPointIndex = form.getCurrLoadingPointIndex();
		LoadingPointBean loadingPointSettings = loadingPoints.get(currentLoadingPointIndex);
		LoadingpointEntity loadingPoint = loadingPointSettings.getLpEntity();
		loadingPoint.setLoadingpointObjectLabel(loadingPointLabel);
	}

}
