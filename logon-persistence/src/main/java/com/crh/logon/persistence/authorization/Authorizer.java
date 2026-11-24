/*
 * Author            : AdNovum Informatik AG
 *
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.persistence.authorization;

import java.util.HashMap;
import java.util.Map;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.Query;

import ch.nevis.gdi.jpa.ThreadLocalUserInfo;
import ch.nevis.gdi.jpa.filter.Expression;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.persistence.entities.admin.UserCompanycodeAccEntity;
import com.crh.logon.persistence.entities.admin.UserPlantAccEntity;
import com.crh.logon.persistence.entities.admin.UserPoolAccEntity;
import com.crh.logon.persistence.entities.admin.UserRegionAccEntity;
import com.crh.logon.persistence.entities.admin.UserSapVendorAccEntity;
import com.crh.logon.persistence.entities.admin.UserSareaAccEntity;
import com.crh.logon.persistence.entities.admin.UserSgroupAccEntity;
import com.crh.logon.persistence.entities.admin.UserShippingpointAccEntity;
import com.crh.logon.persistence.entities.admin.UserShiptoAccEntity;
import com.crh.logon.persistence.entities.admin.UserSoldtoBpAccEntity;
import com.crh.logon.persistence.entities.common.DemandPlanEntity;
import com.crh.logon.persistence.entities.common.PoolTxEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShiptoEntity;
import com.crh.logon.persistence.entities.refdata.SorgBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapBpPfShiptoEntity;
import com.crh.logon.persistence.entities.sap.SapBpSareaEntity;
import com.crh.logon.persistence.entities.views.CommExecCarrPfActiveEntity;
import com.crh.logon.persistence.entities.views.DemandPlanDiffEntity;
import com.crh.logon.persistence.entities.views.DemandPlanMatEntity;
import com.crh.logon.persistence.entities.views.DemandPlanPlanEntity;
import com.crh.logon.persistence.entities.views.DemandPlanShipSoldEntity;
import com.crh.logon.persistence.entities.views.DispoAssignPartListEntity;
import com.crh.logon.persistence.entities.views.DispoAssignmentListEntity;
import com.crh.logon.persistence.entities.views.DispoExtOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderItemListEntity;
import com.crh.logon.persistence.entities.views.DispoOrderListEntity;
import com.crh.logon.persistence.entities.views.DispoTruckListV2Entity;
import com.crh.logon.persistence.entities.views.OrderSearchEntity;
import com.crh.logon.persistence.entities.views.OrderSearchExportEntity;
import com.crh.logon.persistence.entities.views.OrderSearchStatEntity;
import com.crh.logon.persistence.entities.views.OutputdocAccessEntity;
import com.crh.logon.persistence.entities.views.PlanningTruckEntity;
import com.crh.logon.persistence.entities.views.SapSoldPaySareaSrchEntity;
import com.crh.logon.persistence.entities.views.SoldshipBestFlexSrchEntity;
import com.crh.logon.persistence.entities.views.SoldshipFlexSrchEntity;
import com.crh.logon.persistence.entities.views.SoldshipSareaFlexSrchEntity;
import com.crh.logon.persistence.entities.views.TruckRouteSearchEntity;
import com.crh.logon.persistence.entities.views.WeborderShipToSoldToEntity;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.Reason;

/**
 * Handles data access restrictions.
 * @author AdNovum Informatik AG
 */
@SuppressWarnings("unchecked")
@AdnTrace
public class Authorizer implements ch.nevis.gdi.jpa.Authorizer {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(Authorizer.class);

	public static final String UID_PARAM_NAME = "authorizer_uid";

	private static final String IN_ShiptoOfSoldtoRestr =
		" IN (select "  + "sa_shipbp.sapBp.sapBpId" +
		" from " + UserSoldtoBpAccEntity.class.getName() + " sba, "
			+ SapBpSareaEntity.class.getName() + " sa_soldbp, "
			+ SapBpPfShiptoEntity.class.getName() + " bps, " + SapBpSareaEntity.class.getName()
			+ " sa_shipbp "
		+ " where   sba.soldtoSapBp.sapBpId = sa_soldbp.sapBp.sapBpId "
			+ " and sa_soldbp.sapBpSareaId = bps.sapBpPfShiptoPK.sapBpSareaId "
			+ " and bps.sapBpPfShiptoPK.shiptoSapBpSareaId = sa_shipbp.sapBpSareaId "
			+ " and sba.user.userId = :" + UID_PARAM_NAME + ") ";

	private static final String IN_SapBpOfSareaRestr =
		" IN (select "  + "sap_bp.sapBp.sapBpId" +
		" from " + UserSareaAccEntity.class.getName() + " saa, "
			+ SapBpSareaEntity.class.getName() + " sap_bp "
		+ " where saa.sarea.sareaId = sap_bp.sarea.sareaId " + " and saa.user.userId = :"
			+ UID_PARAM_NAME + ") ";

	private static final String IN_ShiptoOfSgroupRestr =
		" IN (select "  + "sa_shipbp.sapBp.sapBpId" +
		" from " + UserSgroupAccEntity.class.getName() + " sga, "
			+ SapBpSareaEntity.class.getName() + " sa_shipbp "
		+ " where   sga.sgroup.sgroupCd = sa_shipbp.sgroup.sgroupCd "
			+ " and sga.user.userId = :" + UID_PARAM_NAME + ") ";

	private static final String IN_SapBpOfCompanycodeRestr =
		" IN (select " + "sap_bp.sapBp.sapBpId" +
		" from "
			+ UserCompanycodeAccEntity.class.getName()+ " ucc_acc, "
			+ SorgBsEntity.class.getName() + " sorg, "
			+ SareaEntity.class.getName() + " sarea, "
			+ SapBpSareaEntity.class.getName() + " sap_bp "
		+ " where ucc_acc."
			+ UserCompanycodeAccEntity.ATTR_COMPANYCODE_CD + " = sorg." + SorgBsEntity.ATTR_COMPANYCODE_CD + " and "
			+ "sorg." + SorgBsEntity.ATTR_SORG_CD + " = sarea." + SareaEntity.ATTR_SORG_CD + " and "
			+ "sarea." + SareaEntity.ATTR_SAREA_ID + " = sap_bp." + SapBpSareaEntity.ATTR_SAREA_ID + " and "
			+ "ucc_acc." + UserCompanycodeAccEntity.ATTR_USER_ID + " = :" + Authorizer.UID_PARAM_NAME + ") ";

	/**
	 * disable access restrictions for next dao-call.
	 * note that after the next dao-call, we will again do access restrictions
	 * if they are enabled.
	 */
	public static void disableNextAccessRestriction() {
		ThreadLocalUserInfo.setDisableNextAccessRestrictions(true);
	}

	public Expression getRestriction(EntityManager em, Filter filter, String clazzname, String identificationVariable) {
		if (ThreadLocalUserInfo.getAccessRestrictions() == false) {
			LOG.error("ATTENTION! ACCESS RESTRICTION DISABLED! (see 'logon.properties')");
			return null;
		}
		if (ThreadLocalUserInfo.getDisableNextAccessRestrictions() == true) {
			ThreadLocalUserInfo.setDisableNextAccessRestrictions(false);
			LOG.debug("access restrictios were disabled for this dao-call only.");
			return null;
		}
		Long uid = ThreadLocalUserInfo.getUserId();

		Expression expression = null;
		if (clazzname.equals(SapBpEntity.class.getSimpleName())) {
			// This one is complex. The query could be about
			// soldto's or shipto's - we don't know. See the
			// getSapBpRestr method about details
			expression = getSapBpRestr(em, filter, identificationVariable,
					SapBpEntity.ATTR_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(DemandPlanEntity.class.getSimpleName())) {
			expression = getDemandPlanningShiptoRestr(em, identificationVariable,
					DemandPlanEntity.ATTR_SHIPTO + "." + ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(DemandPlanShipSoldEntity.class.getSimpleName())) {
			expression = getDemandPlanningShiptoRestr(em, identificationVariable,
					DemandPlanShipSoldEntity.ATTR_SHIPTO_SAP_BP_ID, DemandPlanShipSoldEntity.ATTR_SOLDTO_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(DemandPlanPlanEntity.class.getSimpleName())) {
			expression = getDemandPlanningShiptoRestr(em, identificationVariable,
					DemandPlanPlanEntity.ATTR_SHIPTO_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(DemandPlanMatEntity.class.getSimpleName())) {
			expression = getDemandPlanningShiptoRestr(em, identificationVariable,
					DemandPlanMatEntity.ATTR_SHIPTO_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(DemandPlanDiffEntity.class.getSimpleName())) {
			expression = getDemandPlanningShiptoRestr(em, identificationVariable,
					DemandPlanDiffEntity.ATTR_SHIPTO_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(SoldshipFlexSrchEntity.class.getSimpleName())) {
			expression = getShiptoSoldtoRestr(em, identificationVariable,
					SoldshipFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID,
					SoldshipFlexSrchEntity.ATTR_SOLDTO_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(SoldshipSareaFlexSrchEntity.class.getSimpleName())) {
			expression = getSoldshipSareaFlexSrchRestr(em, identificationVariable,
					SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID,
					SoldshipSareaFlexSrchEntity.ATTR_SOLDTO_SAP_BP_ID,
					SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_SAREA_ID,
					SoldshipSareaFlexSrchEntity.ATTR_SHIPTO_SGROUP_CD, uid);
		}
		else if (clazzname.equals(SoldshipBestFlexSrchEntity.class.getSimpleName())) {
			expression = getSoldshipBestFlexSrchRestr(em, identificationVariable,
					SoldshipBestFlexSrchEntity.ATTR_SHIPTO_SAP_BP_ID,
					SoldshipBestFlexSrchEntity.ATTR_SOLDTO_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(WeborderShipToSoldToEntity.class.getSimpleName())) {
			expression = getWeborderShipToSoldToRestr(em, identificationVariable,
					WeborderShipToSoldToEntity.ATTR_SHIPTO_SAP_BP_ID,
					WeborderShipToSoldToEntity.ATTR_SOLDTO_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(OrderSearchEntity.class.getSimpleName())) {
			expression = getOrderSearchRestr(em, identificationVariable,
					OrderSearchEntity.ATTR_SHIPTO_SAP_BP_ID,
					OrderSearchEntity.ATTR_SOLDTO_SAP_BP_ID, OrderSearchEntity.ATTR_SAREA_ID,
					OrderSearchEntity.ATTR_COMPANYCODE_CD, OrderSearchEntity.ATTR_SHIPPINGPOINT_ID,
					OrderSearchEntity.ATTR_POOL_ID, OrderSearchEntity.ATTR_SGROUP_CD,
					OrderSearchEntity.ATTR_PLANT_CD, OrderSearchEntity.ATTR_COMM_SAP_VENDOR_ID,
					OrderSearchEntity.ATTR_EXEC_SAP_VENDOR_ID, uid);
		}
		else if (clazzname.equals(OrderSearchExportEntity.class.getSimpleName())) {
			//Use the same authorization as order search. 
			//But please note that the order search export uses custom authorization code ExportMetaData.getAccessRestriction
			//not this.
			expression = getOrderSearchRestr(em, identificationVariable,
					OrderSearchExportEntity.ATTR_SHIPTO_SAP_BP_ID,
					OrderSearchExportEntity.ATTR_SOLDTO_SAP_BP_ID, OrderSearchExportEntity.ATTR_SAREA_ID,
					OrderSearchExportEntity.ATTR_COMPANYCODE_CD, OrderSearchExportEntity.ATTR_SHIPPINGPOINT_ID,
					OrderSearchExportEntity.ATTR_POOL_ID, OrderSearchExportEntity.ATTR_SGROUP_CD,
					OrderSearchExportEntity.ATTR_PLANT_CD, OrderSearchExportEntity.ATTR_COMM_SAP_VENDOR_ID,
					OrderSearchExportEntity.ATTR_EXEC_SAP_VENDOR_ID, uid);
		}		
		else if (clazzname.equals(OutputdocAccessEntity.class.getSimpleName())) {
			expression = getOutputdocAccessRestr(em, identificationVariable,
					OutputdocAccessEntity.ATTR_SHIPTO_SAP_BP_ID,
					OutputdocAccessEntity.ATTR_SOLDTO_SAP_BP_ID, OutputdocAccessEntity.ATTR_SAREA_ID,
					OutputdocAccessEntity.ATTR_COMPANYCODE_CD, OutputdocAccessEntity.ATTR_SHIPPINGPOINT_ID,
					OutputdocAccessEntity.ATTR_POOL_ID, OutputdocAccessEntity.ATTR_SGROUP_CD,
					OutputdocAccessEntity.ATTR_PLANT_CD, OutputdocAccessEntity.ATTR_COMM_SAP_VENDOR_ID,
					OutputdocAccessEntity.ATTR_EXEC_SAP_VENDOR_ID, uid);
		}		
		else if (clazzname.equals(OrderSearchStatEntity.class.getSimpleName())) {
			expression = getOrderSearchRestr(em, identificationVariable,
					OrderSearchStatEntity.ATTR_SHIPTO_SAP_BP_ID,
					OrderSearchStatEntity.ATTR_SOLDTO_SAP_BP_ID,
					OrderSearchStatEntity.ATTR_SAREA_ID, OrderSearchStatEntity.ATTR_COMPANYCODE_CD,
					OrderSearchStatEntity.ATTR_SHIPPINGPOINT_ID,
					OrderSearchStatEntity.ATTR_POOL_ID, OrderSearchStatEntity.ATTR_SGROUP_CD,
					OrderSearchStatEntity.ATTR_PLANT_CD,
					OrderSearchStatEntity.ATTR_COMM_SAP_VENDOR_ID,
					OrderSearchStatEntity.ATTR_EXEC_SAP_VENDOR_ID, uid);
		}
		/**
		 * This is intended to be the same, as Scheduling screen uses a combination of this two lists for:
		 * DispoOrderListEntity and DispoExtOrderListEntity! It is necessary to keep it consistent.
		 * At the time of the change for external orders commercial carrier and executing carrier
		 * are not in the scope.
		 */
		else if (clazzname.equals(DispoOrderListEntity.class.getSimpleName())) {
			expression = getDispoOrderListRestr(em, identificationVariable,
					DispoOrderListEntity.ATTR_POOL_ID, DispoOrderListEntity.ATTR_SHIPPINGPOINT_ID,
					DispoOrderListEntity.ATTR_SHIPTO_SAP_BP_ID,
					DispoOrderListEntity.ATTR_COMM_SAP_VENDOR_ID,
					DispoOrderListEntity.ATTR_EXEC_SAP_VENDOR_ID, uid);
		}
		/**
		 * This is intended to be the same, as Scheduling screen uses a combination of this two lists for:
		 * DispoOrderListEntity and DispoExtOrderListEntity! It is necessary to keep it consistent.
		 * At the time of the change for external orders commercial carrier and executing carrier
		 * are not in the scope.
		 */
		else if (clazzname.equals(DispoExtOrderListEntity.class.getSimpleName())) {
			expression = getDispoOrderListRestr(em, identificationVariable,
					DispoExtOrderListEntity.ATTR_POOL_ID,
					DispoExtOrderListEntity.ATTR_SHIPPINGPOINT_ID,
					DispoExtOrderListEntity.ATTR_SHIPTO_SAP_BP_ID,
					DispoExtOrderListEntity.ATTR_COMM_SAP_VENDOR_ID,
					DispoExtOrderListEntity.ATTR_EXEC_SAP_VENDOR_ID, uid);
		}
		else if (clazzname.equals(DispoTruckListV2Entity.class.getSimpleName())) {
			expression = getDispoTruckListRestr(em, identificationVariable,
					DispoTruckListV2Entity.ATTR_POOL_ID, DispoTruckListV2Entity.ATTR_SAP_VENDOR_ID, uid);
		}
		// this is an entity with a PK class, special handling required
		else if (clazzname.equals(PoolTxEntity.class.getSimpleName())) {
			expression = getPoolTxRestr(em, identificationVariable, "poolTxPK.poolId", uid);
		}
		else if (clazzname.equals(ShiptoEntity.class.getSimpleName())) {
			expression = getShiptoRestr(em, identificationVariable,
					ShiptoEntity.ATTR_SHIPTO_SAP_BP_ID, uid);
		}
		else if (clazzname.equals(TruckRouteSearchEntity.class.getSimpleName())) {
			expression = getTruckRouteSearchEntity(em, identificationVariable, uid);
		}
		else if (clazzname.equals(SapSoldPaySareaSrchEntity.class.getSimpleName())) {
			expression = getSapSoldPaySareaSrchRestr();
		}
		else if (clazzname.equals(CommExecCarrPfActiveEntity.class.getSimpleName())) {
			expression = getCommExecCarrPfActiveRestr(em, identificationVariable,
					CommExecCarrPfActiveEntity.ATTR_EXEC_SAP_VENDOR_ID, uid);
		}
		else if (clazzname.equals(PlanningTruckEntity.class.getSimpleName())) {
			expression = getPlanningTruckRestr(em, identificationVariable,
					PlanningTruckEntity.ATTR_POOL_ID, uid);
		}
		/**
		 * This is intended to be the same, as Scheduling screen (and also Rail) uses a combination
		 * of this three lists for:
		 * DispoAssignmentListEntity, DispoAssignPartListEntity and DispoOrderItemListEntity!
		 * No restriction needed, as we need to show the assignments as placeholder.
		 */
		else if (clazzname.equals(DispoAssignPartListEntity.class.getSimpleName())) {
			LOG.debug("no restriction for access to table VLGNC_DISPO_ASSIGN_PART_LIST");
		}
		/**
		 * This is intended to be the same, as Scheduling screen (and also Rail) uses a combination
		 * of this three lists for:
		 * DispoAssignmentListEntity, DispoAssignPartListEntity and DispoOrderItemListEntity!
		 * No restriction needed, as we need to show the assignments as placeholder.
		 */
		else if (clazzname.equals(DispoAssignmentListEntity.class.getSimpleName())) {
			LOG.debug("no restriction for access to table VLGNC_DISPO_ASSIGNMENT_LIST");
		}
		/**
		 * This is intended to be the same, as Scheduling screen (and also Rail) uses a combination
		 * of this three lists for:
		 * DispoAssignmentListEntity, DispoAssignPartListEntity and DispoOrderItemListEntity!
		 * No restriction needed, as we need to show the assignments as placeholder.
		 */
		else if (clazzname.equals(DispoOrderItemListEntity.class.getSimpleName())) {
			LOG.debug("no restriction for access to table VLGNC_DISPO_ORDER_ITEM_LIST");
		}
		else {
			/*
			 * all other tables are generically handled.
			 */
			expression = GeneratedAuthorizer.getRestriction(em, clazzname, identificationVariable);
		}

		return expression;
	}

	/**
	 * Do NOT apply authorization for this view. The access is similar than the one for tlgnc_shipto_billto, but the payers
	 * are not maintained on the intermediate shipto sales area level. Their access is always through the partner function (this view).
	 *
	 * There was no special cause that they are not handled exactly as billtos (using a logon table), just that Crh did not require the possibility
	 * to enable/disable the payer per shipto sales area, as it was implemented in 1.6 Logon for the bill-tos, and this solution
	 * using the view seemed less complicated.
	 *
	 * Billtos are loaded without appying authorization, therefore load payers also without authorization. They can only appear
	 * after the user is authorized for the soldto business partner anyway.
	 *
	 * Company access restrictions do not apply for payer business partners either.
	 */
	private static Expression getSapSoldPaySareaSrchRestr() {
		Expression expression = Restrictions.trueExpression();
		/**
		 * The generated Authorizer would apply this filters.
		 */
		// expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "." + soldtoSapBpIdFld, UserSoldtoBpAccEntity.class.getName(), IN_UserSoldtoBpAcc, uid);
		// expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "." + sareaIdFld, UserSareaAccEntity.class.getName(), IN_UserSareaAcc, uid);
		// expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "." + sareaIdFld, UserCompanycodeAccEntity.class.getName(), IN_SareaOfCompanycodeRestr, uid);
		return expression;
	}

	private static boolean hasUserAccessToTheTable(
		EntityManager em,
		Long uid,
		String accTable)
	{
		boolean logDebugEnabled = LOG.isDebugEnabled();
		String jpaql = "select 1 from " + accTable + " x where user.userId = ?1";
		Query queryObject = em.createQuery(jpaql);
		queryObject.setParameter(1, uid);
		queryObject.setMaxResults(1);

		boolean isThereUserAccess = false;
		if (logDebugEnabled) {
			LOG.debug("JPA QL: " + jpaql + " uid = " + uid);
		}
		try {
			queryObject.getSingleResult();
			isThereUserAccess = true;
		}
		catch (NoResultException ex) {
			isThereUserAccess = false;
		}
		if (logDebugEnabled) {
			LOG.debug("isThereUserAccess in " + accTable + ": " + isThereUserAccess);
		}

		return isThereUserAccess;
	}

	// --------------------------------------------------------------------------------
	/**
	 * Add an expression tho an existing expression. This is just
	 * a utility function allowing one liners.
	 *
	 * @param expression the existing expression
	 * @param em Entity Manager (we do not have access to ServiceSupport from here)
	 * @param field JPA name for the field to be compared to the query
	 * @param accessClass JPA name for the access table
	 * @param query the query to be executed, if there are restriction in the acc table
	 * @param uid userid of the user
	 */
	public static Expression addRestrictionPart(
		Expression expression,
		EntityManager em,
		String field,
		String accessClass,
		String query,
		Long uid)
	{
		Expression restriction = getRestrictionInStatementNullAllowed(em, field, accessClass,
				query, uid);
		if (restriction != null) {
			expression = Restrictions.and(expression, restriction);
		}

		return expression;
	}

	/**
	 * Return an expression that restricts access according to an
	 * access table. This function checks, if there is any restriction
	 * at all in the table. If not, it returns null (meaning: no
	 * restriction = full access allowed.
	 *
	 * In case there is at least one restriction, the result will be filtered accordingly,
	 * but NULL will also be allowed for that field.
	 * In case you DO NOT want to allow null if there is a restriction for the user
	 * use {@link #getRestrictionInStatementNullNotAllowed(EntityManager, String, String, String, Long)}
	 *
	 * @param em Entity Manager (we do not have access to ServiceSupport from here)
	 * @param field JPA name for the field to be compared to the query
	 * @param accessClass JPA name for the access table
	 * @param query the query to be executed, if there are restriction in the acc table
	 * @param uid userid of the user
	 */
	private static Expression getRestrictionInStatementNullAllowed(
		EntityManager em,
		String field,
		String accessClass,
		String query,
		Long uid)
	{
		Expression expression = null;

		boolean isThereUserAccess = hasUserAccessToTheTable(em, uid, accessClass);

		if (isThereUserAccess) {
			// If there is at least one restriction, add the related subquery,
			// e.g. and (shiptoSgroupCd IN (			expression = addRestrictionPart(expression, em, shiptoFld, UserShiptoAccEntity.class.getName(),   null, uid);

			//			 select sgroup.sgroupCd
			//			 from com.crh.logon.persistence.entities.admin.UserSgroupAccEntity
			//			 where user.userId = ?8))
			Map queryParams = new HashMap();
			queryParams.put(UID_PARAM_NAME, uid);
			expression = Restrictions.jpaqlExpression(field + " is null or " + field + query,
					queryParams);
		}
		// else: No restriction record means: no restriction = user sees everything

		return expression;
	}

	/**
	 * Return an expression that restricts access according to an
	 * access table. This function checks, if there is any restriction
	 * at all in the table. If not, it returns null (meaning: no
	 * restriction = full access allowed.
	 *
	 * In case there is at least one restriction, the result will be filtered accordingly,
	 * and rows having NULL as values at the field will also be filtered out.
	 * In case you want to allow NULL in case there is an access restriction specified
	 * use {@link #getRestrictionInStatementNullAllowed(EntityManager, String, String, String, Long)}
	 *
	 * @param em Entity Manager (we do not have access to ServiceSupport from here)
	 * @param field JPA name for the field to be compared to the query
	 * @param accessClass JPA name for the access table
	 * @param query the query to be executed, if there are restriction in the acc table
	 * @param uid userid of the user
	 */
	private static Expression getRestrictionInStatementNullNotAllowed(
		EntityManager em,
		String field,
		String accessClass,
		String query,
		Long uid)
	{
		Expression expression = null;

		boolean isThereUserAccess = hasUserAccessToTheTable(em, uid, accessClass);

		if (isThereUserAccess) {
			Map queryParams = new HashMap();
			queryParams.put(UID_PARAM_NAME, uid);
			expression = Restrictions.jpaqlExpression(field + query, queryParams);
		}
		// else: No restriction record means: no restriction = user sees everything

		return expression;
	}

	/**
	 * Return an expression that restricts access according to the REQUESTED BP
	 * type.
	 * The filter MUST contain exactly ONE of BP_IS_BILLTO, BP_IS_PAYER,
	 * BP_IS_SHIPTO or BP_IS_SOLDTO.
	 * For shipto's: Shipto restriction + Soldto restriction for the the soldto
	 * PF.
	 * For soldto's: Soldto restriction.
	 * For all BP's (including the above): sales area, sgroup, company code.
	 *
	 * @param em Entity Manager (we do not have access to ServiceSupport from here)
	 * @param sapBpFld JPA name for the bp field to be compared to tlgnu_user_soldto_bp_acc.soldto_bp_id
	 * @param uid userid of the user
	 */
	private static Expression getSapBpRestr(
		EntityManager em,
		Filter filter,
		String identificationVariable,
		String sapBpFld,
		Long uid)
	{
		final int FILTER_TYPE_NONE = 0;
		final int FILTER_TYPE_BILLTO = 1;
		final int FILTER_TYPE_PAYER = 2;
		final int FILTER_TYPE_SHIPTO = 3;
		final int FILTER_TYPE_SOLDTO = 4;
		int filterType = FILTER_TYPE_NONE;
		Expression expression = Restrictions.trueExpression();

		// initialize the input parameters see ADN75314.
		filter.toJPAQL();
		if ((filter.getInputParameters().get(SapBpEntity.ATTR_BP_IS_BILLTO) != null)
				&& (filter.getInputParameters().get(SapBpEntity.ATTR_BP_IS_BILLTO).equals(Boolean.TRUE))) {
			if (filterType == FILTER_TYPE_NONE) {
				filterType = FILTER_TYPE_BILLTO;
			}
			else {

				throw new LogonException("Filter contains multiple BP types", Reason.RES_IMPL_BUG);
			}
		}
		if ((filter.getInputParameters().get(SapBpEntity.ATTR_BP_IS_PAYER) != null)
				&& (filter.getInputParameters().get(SapBpEntity.ATTR_BP_IS_PAYER).equals(Boolean.TRUE))) {
			if (filterType == FILTER_TYPE_NONE) {
				filterType = FILTER_TYPE_PAYER;
			}
			else {

				throw new LogonException("Filter contains multiple BP types", Reason.RES_IMPL_BUG);
			}
		}
		if ((filter.getInputParameters().get(SapBpEntity.ATTR_BP_IS_SHIPTO) != null)
				&& (filter.getInputParameters().get(SapBpEntity.ATTR_BP_IS_SHIPTO).equals(Boolean.TRUE))) {
			if (filterType == FILTER_TYPE_NONE) {
				filterType = FILTER_TYPE_SHIPTO;
			}
			else {

				throw new LogonException("Filter contains multiple BP types", Reason.RES_IMPL_BUG);
			}
		}
		if ((filter.getInputParameters().get(SapBpEntity.ATTR_BP_IS_SOLDTO) != null)
				&& (filter.getInputParameters().get(SapBpEntity.ATTR_BP_IS_SOLDTO).equals(Boolean.TRUE))) {
			if (filterType == FILTER_TYPE_NONE) {
				filterType = FILTER_TYPE_SOLDTO;
			}
			else {

				throw new LogonException("Filter contains multiple BP types", Reason.RES_IMPL_BUG);
			}
		}
		switch (filterType) {
		case FILTER_TYPE_BILLTO:
		case FILTER_TYPE_PAYER:
			break;
		case FILTER_TYPE_SHIPTO:
			expression = addRestrictionPart(expression, em,
					identificationVariable + "." + sapBpFld,
					UserShiptoAccEntity.class.getName(), GeneratedAuthorizer.IN_UserShiptoAcc, uid);
			expression = addRestrictionPart(expression, em,
					identificationVariable + "." + sapBpFld,
					UserSoldtoBpAccEntity.class.getName(), IN_ShiptoOfSoldtoRestr, uid);
			break;
		case FILTER_TYPE_SOLDTO:
			expression = addRestrictionPart(expression, em,
					identificationVariable + "." + sapBpFld,
					UserSoldtoBpAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSoldtoBpAcc,
					uid);
			break;
		default:

			throw new LogonException("Please include one of " + SapBpEntity.ATTR_BP_IS_BILLTO
					+ ", " + SapBpEntity.ATTR_BP_IS_PAYER + ", " + SapBpEntity.ATTR_BP_IS_SHIPTO
					+ ", " + SapBpEntity.ATTR_BP_IS_SOLDTO + " in your filter for SapBpEntity",
					Reason.RES_IMPL_BUG);
		}
		expression = addGenericSapBpRestrictionPart(expression, em, identificationVariable,
				sapBpFld, uid);

		return expression;
	}

	/**
	 * This logic applies the same authorization as what getSapBpRestr() would apply to a single shipto.
	 * We based the authorization login on Order Search's Shipto dropdown.
	 */
	private static Expression getDemandPlanningShiptoRestr(EntityManager em,
			String identificationVariable,
			String shiptoSapBpFld,
			Long uid) {
		Expression expression = Restrictions.trueExpression();
		expression = addRestrictionPart(expression, em,
				identificationVariable + "." + shiptoSapBpFld,
				UserShiptoAccEntity.class.getName(), GeneratedAuthorizer.IN_UserShiptoAcc, uid);
		expression = addRestrictionPart(expression, em,
				identificationVariable + "." + shiptoSapBpFld,
				UserSoldtoBpAccEntity.class.getName(), IN_ShiptoOfSoldtoRestr, uid);
		expression = addGenericSapBpRestrictionPart(expression, em, identificationVariable,
				shiptoSapBpFld, uid);

		return expression;
	}

	private static Expression getDemandPlanningShiptoRestr(EntityManager em,
			String identificationVariable,
			String shiptoSapBpFld,
			String soldtoSapBpFld,
			Long uid) {
		Expression expression = Restrictions.trueExpression();
		expression = addRestrictionPart(expression, em,
				identificationVariable + "." + shiptoSapBpFld,
				UserShiptoAccEntity.class.getName(), GeneratedAuthorizer.IN_UserShiptoAcc, uid);
		expression = addRestrictionPart(expression, em,
				identificationVariable + "." + soldtoSapBpFld,
				UserSoldtoBpAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSoldtoBpAcc, uid);
		expression = addGenericSapBpRestrictionPart(expression, em, identificationVariable,
				shiptoSapBpFld, uid);

		return expression;
	}

	/**
	 * Return an expression that restricts access to shiptos.
	 * This depends on:
	 *	- tlgnu_user_soldto_bp_acc
	 *	- tlgnu_user_shipto_acc
	 *	- tlgns_sap_bp_sarea
	 *	- tlgnu_user_sgroup_acc
	 * This function requires, that the soldto, sarea and sgroup information
	 * of the shipto is available in the view. If soldto/sarea/sgroup
	 * information is not available within the view, consider extending the
	 * the view or (usually not preferrable) use the getFullShiptoRestr function
	 * below instead.
	 *
	 * @param em Entity Manager (we do not have access to ServiceSupport from here)
	 * @param shiptoFld the field containing the shipto
	 * @param soldtoFld the field containing the soldto of the shipto
	 * @param  sareaFld the field containing the sarea  of the shipto
	 * @param sgroupFld the field containing the sgroup of the shipto
	 * @param uid userid of the user
	 */
	private static Expression getSoldshipSareaFlexSrchRestr(
		EntityManager em,
		String identificationVariable,
		String shiptoFld,
		String soldtoFld,
		String  sareaFld,
		String sgroupFld,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = addRestrictionPart(expression, em, identificationVariable + "." + shiptoFld,
				UserShiptoAccEntity.class.getName(), GeneratedAuthorizer.IN_UserShiptoAcc, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + soldtoFld,
				UserSoldtoBpAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSoldtoBpAcc, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + sareaFld,
				UserSareaAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSareaAcc, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + sgroupFld,
				UserSgroupAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSgroupAcc, uid);

		return expression;
	}

	/**
	 * Do NOT apply plant authorization for shipto.
	 */
	private static Expression getShiptoRestr(
		EntityManager em,
		String identificationVariable,
		String shiptoSapBpIdFld,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ shiptoSapBpIdFld, UserShiptoAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserShiptoAcc, uid);

		/* The GeneratedAuthorizer would add these filters as well:
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "." + plantCdFld, UserPlantAccEntity.class.getName(), IN_UserPlantAcc, uid);
		*/
		return expression;
	}

	private static Expression getWeborderShipToSoldToRestr(
		EntityManager em,
		String identificationVariable,
		String shiptoFld,
		String soldtoFld,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = addRestrictionPart(expression, em, identificationVariable + "." + shiptoFld,
				UserShiptoAccEntity.class.getName(), GeneratedAuthorizer.IN_UserShiptoAcc, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + soldtoFld,
				UserSoldtoBpAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSoldtoBpAcc, uid);
		return expression;
	}

	/**
	 * Adds the check for sgroup, sarea, company code for a given (any type of)
	 * sap bp.
	 * @param expression
	 * @param em
	 * @param identificationVariable
	 * @param sapBpFld
	 * @param uid
	 * @return
	 */
	private static Expression addGenericSapBpRestrictionPart(
		Expression expression,
		EntityManager em,
		String identificationVariable,
		String sapBpFld,
		Long uid)
	{
		expression = addRestrictionPart(expression, em, identificationVariable + "." + sapBpFld,
				UserSareaAccEntity.class.getName(), IN_SapBpOfSareaRestr, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + sapBpFld,
				UserSgroupAccEntity.class.getName(), IN_ShiptoOfSgroupRestr, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + sapBpFld,
				UserCompanycodeAccEntity.class.getName(), IN_SapBpOfCompanycodeRestr, uid);

		return expression;
	}

	/**
	 * Return an expression that restricts access to shipto/soldto bps.
	 * This depends on:
	 *	- tlgnu_user_soldto_bp_acc
	 *	- tlgnu_user_shipto_acc
	 * This function requires, that the shipto, soldto information
	 * are is available in the view.
	 *
	 * @param em Entity Manager (we do not have access to ServiceSupport from here)
	 * @param shiptoFld the field containing the shipto
	 * @param soldtoFld the field containing the soldto of the shipto
	 * @param uid userid of the user
	 */
	private static Expression getShiptoSoldtoRestr(
		EntityManager em,
		String identificationVariable,
		String shiptoFld,
		String soldtoFld,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = addRestrictionPart(expression, em, identificationVariable + "." + shiptoFld,
				UserShiptoAccEntity.class.getName(), GeneratedAuthorizer.IN_UserShiptoAcc, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + soldtoFld,
				UserSoldtoBpAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSoldtoBpAcc, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + shiptoFld,
				UserCompanycodeAccEntity.class.getName(), IN_SapBpOfCompanycodeRestr, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + shiptoFld,
				UserSareaAccEntity.class.getName(), IN_SapBpOfSareaRestr, uid);

		return expression;
	}

	private static Expression getSoldshipBestFlexSrchRestr(
		EntityManager em,
		String identificationVariable,
		String shiptoFld,
		String soldtoFld,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = addRestrictionPart(expression, em, identificationVariable + "." + shiptoFld,
				UserShiptoAccEntity.class.getName(), GeneratedAuthorizer.IN_UserShiptoAcc, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + soldtoFld,
				UserSoldtoBpAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSoldtoBpAcc, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + shiptoFld,
				UserCompanycodeAccEntity.class.getName(), IN_SapBpOfCompanycodeRestr, uid);
		expression = addRestrictionPart(expression, em, identificationVariable + "." + shiptoFld,
				UserSareaAccEntity.class.getName(), IN_SapBpOfSareaRestr, uid);

		return expression;
	}

	private static Expression getPoolTxRestr(
		EntityManager em,
		String identificationVariable,
		String poolIdFld,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ poolIdFld,
				UserPoolAccEntity.class.getName(), GeneratedAuthorizer.IN_UserPoolAcc, uid);
		return expression;
	}

	private static Expression getTruckRouteSearchEntity(
		EntityManager em,
		String identificationVariable,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ TruckRouteSearchEntity.ATTR_START_SHIPPINGPOINT_ID,
				UserShippingpointAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserShippingpointAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ TruckRouteSearchEntity.ATTR_END_SHIPTO_BP_ID,
				UserShippingpointAccEntity.class.getName(), GeneratedAuthorizer.IN_UserShiptoAcc,
				uid);
		return expression;
	}

	private static Expression getOrderSearchRestr(
		EntityManager em, String identificationVariable,
		String shiptoSapBpIdFld, String soldtoSapBpIdFld, String sareaIdFld,
		String companycodeCdFld, String shippingpointIdFld, String poolIdFld,
		String sgroupCdFld, String plantCdFld, String commSapVendorIdFld,
		String execSapVendorIdFld, Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ shiptoSapBpIdFld, UserShiptoAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserShiptoAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ soldtoSapBpIdFld, UserSoldtoBpAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserSoldtoBpAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ sareaIdFld, UserSareaAccEntity.class.getName(), 
				GeneratedAuthorizer.IN_UserSareaAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ sareaIdFld, UserRegionAccEntity.class.getName(),
				GeneratedAuthorizer.IN_CompanycodeOfRegionRestr, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ companycodeCdFld, UserCompanycodeAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserCompanycodeAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ shippingpointIdFld, UserShippingpointAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserShippingpointAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ sgroupCdFld,
				UserSgroupAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSgroupAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ plantCdFld,
				UserPlantAccEntity.class.getName(), GeneratedAuthorizer.IN_UserPlantAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ poolIdFld,
				UserPoolAccEntity.class.getName(), GeneratedAuthorizer.IN_UserPoolAcc, uid);

		expression = Authorizer.addSapVendorOrRestrictionPart(expression, em,
				identificationVariable,
				commSapVendorIdFld, execSapVendorIdFld, uid);

		return expression;
	}
	
	private static Expression getOutputdocAccessRestr(
			EntityManager em, String identificationVariable,
			String shiptoSapBpIdFld, String soldtoSapBpIdFld, String sareaIdFld,
			String companycodeCdFld, String shippingpointIdFld, String poolIdFld,
			String sgroupCdFld, String plantCdFld, String commSapVendorIdFld,
			String execSapVendorIdFld, Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ shiptoSapBpIdFld, UserShiptoAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserShiptoAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ soldtoSapBpIdFld, UserSoldtoBpAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserSoldtoBpAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ sareaIdFld, UserSareaAccEntity.class.getName(), 
				GeneratedAuthorizer.IN_UserSareaAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ sareaIdFld, UserRegionAccEntity.class.getName(),
				GeneratedAuthorizer.IN_CompanycodeOfRegionRestr, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ companycodeCdFld, UserCompanycodeAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserCompanycodeAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ shippingpointIdFld, UserShippingpointAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserShippingpointAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ sgroupCdFld,
				UserSgroupAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSgroupAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ plantCdFld,
				UserPlantAccEntity.class.getName(), GeneratedAuthorizer.IN_UserPlantAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ poolIdFld,
				UserPoolAccEntity.class.getName(), GeneratedAuthorizer.IN_UserPoolAcc, uid);

		expression = Authorizer.addSapVendorOrRestrictionPart(expression, em,
				identificationVariable,
				commSapVendorIdFld, execSapVendorIdFld, uid);

		return expression;
	}

	private static Expression getDispoOrderListRestr(
		EntityManager em,
		String identificationVariable,
		String poolIdFld,
		String shippingpointIdFld,
		String shiptoSapBpIdFld,
		String commSapVendorIdFld,
		String execSapVendorIdFld,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ poolIdFld,
				UserPoolAccEntity.class.getName(), GeneratedAuthorizer.IN_UserPoolAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ shippingpointIdFld, UserShippingpointAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserShippingpointAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ shiptoSapBpIdFld, UserShiptoAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserShiptoAcc, uid);

		expression = Authorizer.addSapVendorOrRestrictionPart(expression, em,
				identificationVariable,
				commSapVendorIdFld, execSapVendorIdFld, uid);

		return expression;
	}

	/**
	 * Do NOT apply plant and shippingpoint authorization for this view.
	 *
	 * On Scheduling screen, the DispoTruckListEntity-s needed to be loaded
	 * without plant and shippingpoint authorization.
	 */
	private static Expression getDispoTruckListRestr(
		EntityManager em,
		String identificationVariable,
		String poolIdFld,
		String sapVendorIdFld,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ poolIdFld,
				UserPoolAccEntity.class.getName(), GeneratedAuthorizer.IN_UserPoolAcc, uid);
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ sapVendorIdFld,
				UserSapVendorAccEntity.class.getName(), GeneratedAuthorizer.IN_UserSapVendorAcc,
				uid);
		/* The GeneratedAuthorizer would add these filters as well:
		 *
		 * expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "." + shippingpointIdFld, UserShippingpointAccEntity.class.getName(), GeneratedAuthorizer.IN_UserShippingpointAcc, uid);
		 * expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "." + plantCdFld, UserPlantAccEntity.class.getName(), GeneratedAuthorizer.IN_UserPlantAcc, uid);
		 */
		return expression;
	}

	private static Expression addSapVendorOrRestrictionPart(
		Expression expression,
		EntityManager em,
		String identificationVariable,
		String commSapVendorIdFld,
		String execSapVendorIdFld,
		Long uid)
	{
		/**
		* In case a view contains both the exec_sap_vendor_id and the comm_sap_vendor_id it is most likely
		* supposed to filter with an OR statement for these.
		*/
		Expression restriction = getRestrictionInStatementNullAllowed(em, identificationVariable
				+ "."
				+ commSapVendorIdFld, UserSapVendorAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserSapVendorAcc, uid);
		Expression restr2 = getRestrictionInStatementNullNotAllowed(em, identificationVariable
				+ "." + execSapVendorIdFld, UserSapVendorAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserSapVendorAcc, uid);

		if (restriction != null && restr2 != null) {
			expression = Restrictions.and(expression, Restrictions.or(restriction, restr2));
		}
		return expression;
	}

	private static Expression getCommExecCarrPfActiveRestr(
		EntityManager em,
		String identificationVariable,
		String sapVendorIdFld,
		Long uid)
	{
		Expression expression = Restrictions.trueExpression();
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ sapVendorIdFld,
				"UserSapVendorAccEntity", GeneratedAuthorizer.IN_UserSapVendorAcc, uid);
		return expression;
	}

	private static Expression getPlanningTruckRestr(EntityManager em,
			String identificationVariable, String attrPoolId, Long uid) {
		Expression expression = Restrictions.trueExpression();
		expression = Authorizer.addRestrictionPart(expression, em, identificationVariable + "."
				+ attrPoolId, UserPoolAccEntity.class.getName(),
				GeneratedAuthorizer.IN_UserPoolAcc, uid);
		return expression;
	}

}
