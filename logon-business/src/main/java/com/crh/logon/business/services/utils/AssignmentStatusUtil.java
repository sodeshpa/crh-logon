/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.utils;

import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.refdata.AssignPartStatusBsEntity;
import com.crh.logon.persistence.entities.refdata.AssignStatusBsEntity;

/**
 * @author AdNovum Informatik AG
 */
public class AssignmentStatusUtil {

	public static final AssignStatusBsEntity ASSIGNMENT_STATUS_NEW = new AssignStatusBsEntity();
	public static final AssignStatusBsEntity ASSIGNMENT_STATUS_PRE_LOAD = new AssignStatusBsEntity();
	public static final AssignStatusBsEntity ASSIGNMENT_STATUS_LOADING = new AssignStatusBsEntity();
	public static final AssignStatusBsEntity ASSIGNMENT_STATUS_SHIPPING = new AssignStatusBsEntity();
	public static final AssignStatusBsEntity ASSIGNMENT_STATUS_UNLOADING = new AssignStatusBsEntity();
	public static final AssignStatusBsEntity ASSIGNMENT_STATUS_RETURNING = new AssignStatusBsEntity();
	public static final AssignStatusBsEntity ASSIGNMENT_STATUS_FINISHED = new AssignStatusBsEntity();
	public static final AssignStatusBsEntity ASSIGNMENT_STATUS_CANCELLED = new AssignStatusBsEntity();
	public static final AssignStatusBsEntity ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER = new AssignStatusBsEntity();	
	public static final AssignPartStatusBsEntity ASSIGNMENT_PART_STATUS_NEW = new AssignPartStatusBsEntity();
	public static final AssignPartStatusBsEntity ASSIGNMENT_PART_STATUS_TRANSMITTED_ACK = new AssignPartStatusBsEntity();	
	
	static{
		ASSIGNMENT_STATUS_NEW.setAssignStatusCd(Constant.ASSIGNMENT_STATUS_NEW);
		ASSIGNMENT_STATUS_PRE_LOAD.setAssignStatusCd(Constant.ASSIGNMENT_STATUS_PRE_LOAD);
		ASSIGNMENT_STATUS_LOADING.setAssignStatusCd(Constant.ASSIGNMENT_STATUS_LOADING);
		ASSIGNMENT_STATUS_SHIPPING.setAssignStatusCd(Constant.ASSIGNMENT_STATUS_SHIPPING);
		ASSIGNMENT_STATUS_UNLOADING.setAssignStatusCd(Constant.ASSIGNMENT_STATUS_UNLOADING);
		ASSIGNMENT_STATUS_RETURNING.setAssignStatusCd(Constant.ASSIGNMENT_STATUS_RETURNING);
		ASSIGNMENT_STATUS_FINISHED.setAssignStatusCd(Constant.ASSIGNMENT_STATUS_FINISHED);
		ASSIGNMENT_STATUS_CANCELLED.setAssignStatusCd(Constant.ASSIGNMENT_STATUS_CANCELLED);
		ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER.setAssignStatusCd(Constant.ASSIGNMENT_STATUS_RELEASED_BY_PRESCHEDULER);
		ASSIGNMENT_PART_STATUS_NEW.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_NEW);
		ASSIGNMENT_PART_STATUS_TRANSMITTED_ACK.setAssignPartStatusCd(Constant.ASSIGN_PART_STATUS_TRANSMITTED_ACK);
	}

}
