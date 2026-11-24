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

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.OrdItemAssignmtLoadEntity;
import com.crh.logon.utils.StringUtils;

/**
 * Used to determine whether functionality is available for a given order.
 * @author AdNovum Informatik AG
 */
public class OrderSimpleChangeOrderedMeasureTag extends TagSupport { 
	
	private static final String C_NAME = 
		OrderSimpleChangeFunctionDisabledTag.class.getName();

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(C_NAME);

	/**
	 * The context key for getting the value for {@link #lpObject}
	 */
	private String orderKey = null;
	
	private OrdItemAssignmtLoadEntity order = null;

	
	public int doStartTag() throws JspException {
	
		int result = 0;

			if (StringUtils.notEmpty(orderKey)){				
				order = (OrdItemAssignmtLoadEntity) pageContext.findAttribute(orderKey);
				if ( order == null ) {
					return result = SKIP_BODY;
				}
				if (order.getOrderedUomCd().equals(Constant.UOM_LKW)){
					Double weight = order.getTruckMaxNetWeightT() != null ?  order.getTruckMaxNetWeightT() : order.getOrderedMeasure();
					try {
						pageContext.getOut().write( weight != null ? (weight.toString() + " (" + weight.toString() + " t)") : "" );
					}
					catch (IOException e) {
						LOG.error("Exception in tag OrderedMeasureTag");
					}
				}
				else if (order.getOrderedUomCd().equals(Constant.UOM_CONTAINER)){
					Double weight = order.getContainerMaxNetWeightT() != null ?  order.getContainerMaxNetWeightT() : order.getOrderedMeasure();
					try {
						pageContext.getOut().write( weight != null ? (weight.toString() + " (" + weight.toString() + " t)" ) : "");
					}
					catch (IOException e) {
						LOG.error("Exception in tag OrderedMeasureTag");
					}
				}
				else{
					//no special case
					Double weight = order.getOrderedMeasure();
					try {
						pageContext.getOut().write( weight != null ? ( "(" + weight.toString() + " " + order.getOrderUomDimensionTxt() + ")" ) : "");
					}
					catch (IOException e) {
						LOG.error("Exception in tag OrderedMeasureTag");
					}
				}
			}
			return result = SKIP_BODY;

	}


	public String getOrderKey() {
		return orderKey;
	}


	public void setOrderKey(String orderKey) {
		this.orderKey = orderKey;
	}
}
