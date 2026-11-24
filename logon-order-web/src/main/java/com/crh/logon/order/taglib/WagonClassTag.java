/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.order.taglib;

import java.io.IOException;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.TagSupport;

import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.order.web.forms.railcaroptimization.RailCarOptimizationForm;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.RailcarOptLoadedCarsEntity;

/**
 * Prints out a class name depending on the wagon's certain attribute.
 * @author AdNovum Informatik AG
 */
public class WagonClassTag extends TagSupport { 
	
	private static final String C_NAME = 
		WagonClassTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);

	private RailcarOptLoadedCarsEntity wagon;

	public int doStartTag() throws JspException {
			int result = EVAL_BODY_INCLUDE;
			RailcarOptLoadedCarsEntity wagon = getWagon();
			String className = "arriving";
			RailCarOptimizationForm form = (RailCarOptimizationForm)pageContext.getSession().getAttribute("order_RailCarOptimizationForm");
			boolean isReturning = wagon.getLastKnownRailwayStationId() != null && !form.getSelectedShippinpointsStationIds().contains(wagon.getLastKnownRailwayStationId());
			//in fact on the screen no arrived, and arriving wagons should appear
			if ( !isReturning ) {
				className = "arrived";
				if (wagon.getLoadId() != null && (wagon.getLoadingTs() != null || wagon.getLogonPackageTypeCd() == null )) {
					className = "loaded";
				}
				if (wagon.getOrderStatusCd() != null  && !( wagon.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_LOADED || 
						wagon.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_LOADING ||
						wagon.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_ARRIVED_AT_LOADINGPOINT ||
						wagon.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_TRANSMITTED_TO_DRIVER_ACK ||
						wagon.getOrderStatusCd().longValue() == Constant.ORDER_STATUS_DISPATCHED
				)) {
					className = "transmitted";
				}
			}
			try {
				pageContext.getOut().write(className);
			}
			catch (IOException e) {
				LOG.error("Error rendering class name for wagon");
			}
			return result;

	}
	
	public int doEndTag(){
		int i = EVAL_PAGE;
		return i;
	}

	/**
	 * @return the wagon
	 */
	public RailcarOptLoadedCarsEntity getWagon() {
		return wagon;
	}

	/**
	 * @param wagon the wagon to set
	 */
	public void setWagon(RailcarOptLoadedCarsEntity wagon) {
		this.wagon = wagon;
	}

}
