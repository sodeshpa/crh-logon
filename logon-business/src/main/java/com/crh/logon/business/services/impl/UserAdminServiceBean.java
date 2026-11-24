/*
 * Author            : AdNovum Informatik AG
 * Version Number    : $Revision$
 * Date of last edit : $Date$
 */

package com.crh.logon.business.services.impl;

import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import com.crh.logon.business.services.beans.CompanycodeSareaBean;
import com.crh.logon.business.services.beans.PlantShippingpointBean;
import com.crh.logon.business.services.beans.SoldshipFlexSrchBean;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.utils.LogonUtil;
import com.crh.logon.business.services.utils.PwGenerator;
import com.crh.logon.business.services.utils.RFIDBean;

import ch.nevis.gdi.jpa.DirectJpaDao;
import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.ReadWriteDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.FilterImpl;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Order;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;

import com.crh.logon.beans.UnlockExtLoginBean;
import com.crh.logon.business.services.beans.UserAdminSrchBean;
import com.crh.logon.business.services.directjpa.CarrierJpaQueryData;
import com.crh.logon.business.services.directjpa.CompCodeSareaJpaQueryData;
import com.crh.logon.business.services.directjpa.CompanycodeJpaQueryData;
import com.crh.logon.business.services.directjpa.JpaQueryData;
import com.crh.logon.business.services.directjpa.PlantJpaQueryData;
import com.crh.logon.business.services.directjpa.PlantShippingpointJpaQueryData;
import com.crh.logon.business.services.directjpa.PoolTxJpaQueryData;
import com.crh.logon.business.services.directjpa.PurchasingorgBsJpaQueryData;
import com.crh.logon.business.services.directjpa.SapSoldtoSrchJpaQueryData;
import com.crh.logon.business.services.directjpa.SgroupBsJpaQueryData;
import com.crh.logon.business.services.directjpa.SoldshipFlexSrchJpaQueryData;
import com.crh.logon.business.services.directjpa.UserAdminJpaQueryData;
import com.crh.logon.business.services.dispatchers.UserAdminServiceDispatcher;
import com.crh.logon.business.services.localinterfaces.ConfigServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.EmailServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.ExtLoginPwdHistoryServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.PDFGeneratorServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RFIDServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.RefDataGenericsServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserActivityServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserAdminServiceLocalInterface;
import com.crh.logon.business.services.localinterfaces.UserServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.authorization.Authorizer;
import com.crh.logon.persistence.entities.admin.RoleEntity;
import com.crh.logon.persistence.entities.admin.RoleFuncEntity;
import com.crh.logon.persistence.entities.admin.UserCompanycodeAccEntity;
import com.crh.logon.persistence.entities.admin.UserEntity;
import com.crh.logon.persistence.entities.admin.UserPlantAccEntity;
import com.crh.logon.persistence.entities.admin.UserPoolAccEntity;
import com.crh.logon.persistence.entities.admin.UserPurchorgAccEntity;
import com.crh.logon.persistence.entities.admin.UserRoleEntity;
import com.crh.logon.persistence.entities.admin.UserSapVendorAccEntity;
import com.crh.logon.persistence.entities.admin.UserSareaAccEntity;
import com.crh.logon.persistence.entities.admin.UserSgroupAccEntity;
import com.crh.logon.persistence.entities.admin.UserShippingpointAccEntity;
import com.crh.logon.persistence.entities.admin.UserShiptoAccEntity;
import com.crh.logon.persistence.entities.admin.UserSoldtoBpAccEntity;
import com.crh.logon.persistence.entities.common.AuditEntity;
import com.crh.logon.persistence.entities.common.DriverEntity;
import com.crh.logon.persistence.entities.common.ImageEntity;
import com.crh.logon.persistence.entities.common.PoolEntity;
import com.crh.logon.persistence.entities.common.RfidobjectEntity;
import com.crh.logon.persistence.entities.common.SareaEntity;
import com.crh.logon.persistence.entities.common.ShippingpointEntity;
import com.crh.logon.persistence.entities.common.SignatureEntity;
import com.crh.logon.persistence.entities.refdata.AuditActionBsEntity;
import com.crh.logon.persistence.entities.refdata.CompanycodeBsEntity;
import com.crh.logon.persistence.entities.refdata.LangTxEntity;
import com.crh.logon.persistence.entities.refdata.MimeTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.PlantBsEntity;
import com.crh.logon.persistence.entities.refdata.PurchasingorgBsEntity;
import com.crh.logon.persistence.entities.refdata.RfidobjectTypeBsEntity;
import com.crh.logon.persistence.entities.refdata.SgroupBsEntity;
import com.crh.logon.persistence.entities.sap.SapBpEntity;
import com.crh.logon.persistence.entities.sap.SapVendorEntity;
import com.crh.logon.persistence.entities.views.CarrierVendorEntity;
import com.crh.logon.persistence.entities.views.SapSoldtoSrchEntity;
import com.crh.logon.persistence.entities.views.UserActivityLogEntity;
import com.crh.logon.persistence.entities.views.UserAdminEntity;
import com.crh.logon.persistence.entities.views.UserTypeViewEntity;
import com.crh.logon.persistence.utils.DaoProvider;
import com.crh.logon.utils.ConfigBean;
import com.crh.logon.utils.ListRange;
import com.crh.logon.utils.LogonException;
import com.crh.logon.utils.PasswordUtil;
import com.crh.logon.utils.Reason;
import com.crh.logon.utils.StringUtils;

/**
 * This is the service bean, which is designed to take care about all the calls coming from the ajax service
 * or from the UserAdminAction, UserAdminEntryAction actions. 
 * Mainly has query, and update functions.
 * 
 * @author AdNovum Informatik AG
 */
@AdnTrace
@Alternative
@Stateless
@Interceptors(UserInfoInterceptor.class)
public class UserAdminServiceBean extends UserAdminServiceDispatcher implements UserAdminServiceLocalInterface {
	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(UserAdminServiceBean.class);

	public static final String SEARCH_FIELD_FIRST_NAME_OR_NAME = "firstNameOrName";

	public static final String SEARCH_FIELD_HOLSPACE_OR_EXT_LOGIN = "holspaceOrExtLogin";

	public static final String SEARCH_FIELD_CUSTOMER_OR_CARRIER = "customerOrCarrier";

	private static final long MILLISECONDS_PER_SECOND = 1000;
	
	private static final long CUSTOMER_USER_TYPE_CD = 1;
	
	private static final long DRIVER_USER_TYPE_CD = 3;
	
	private static List<Long> defaultDriverRoleIdList = null;
	private static List<Long> defaultCustomerRoleIdList = null;
	
	static {
		defaultDriverRoleIdList = new ArrayList<Long>();
		//driver role from tlgnu_role
		defaultDriverRoleIdList.add(13L);
		
		defaultCustomerRoleIdList = new ArrayList<Long>();
		//customer (weborder) role from tlgnu_role
		defaultCustomerRoleIdList.add(12L);
	}

	@EJB private ExtLoginPwdHistoryServiceLocalInterface externalLoginPasswordHistoryService;
    @EJB private UserActivityServiceLocalInterface userActivityService;
	@EJB private RefDataGenericsServiceLocalInterface referentialDataService;
	@EJB private UserServiceLocalInterface userService;
	@EJB private PDFGeneratorServiceLocalInterface pdfGeneratorService;
	@EJB private RFIDServiceLocalInterface rfIdService;
	@EJB private ConfigServiceLocalInterface configService;
	@EJB private EmailServiceLocalInterface emailService;

	/**
	 * Gets the users according to the parameters given from the UserAdminView. 
	 */
	@AdnTrace
	public ListRange<UserAdminSrchBean> fetchUserList(int skip, int num, String sort, boolean asc,
			HashMap<String, String> searchCrit,
			UserEntity userLoggedIn) {
		ReadOnlyDao<UserAdminEntity> userViewDao = getDaoProvider().getUserAdminEntityDao();

		boolean doFilter = false;
		if (!StringUtils.empty(searchCrit.get("doFilter"))) {
			doFilter = Boolean.valueOf(searchCrit.get("doFilter"));
		}

		//--------------------------------------------------------------------------------------
		// Load all users (if there is some filtering than use) NOTE: There is no authorization check
		//--------------------------------------------------------------------------------------
		DaoProvider daoProvider = getDaoProvider();
		Long userId = null; // <NULL> to avoid restriction check at users load 
		UserAdminJpaQueryData<UserAdminJpaQueryData.ColumnNames> queryData = new UserAdminJpaQueryData<UserAdminJpaQueryData.ColumnNames>(
				daoProvider, userId);
		queryData.addSelectColumnNames(UserAdminJpaQueryData.COLUMNS_SET_1);
		queryData.setUserId(userLoggedIn.getUserId());

		if (doFilter) {
			String firstNameOrName = searchCrit.get(SEARCH_FIELD_FIRST_NAME_OR_NAME);
			firstNameOrName = firstNameOrName.replace(' ', '%');
			queryData.setCompositeName(firstNameOrName);

			String holspaceOrExtLogin = searchCrit.get(SEARCH_FIELD_HOLSPACE_OR_EXT_LOGIN);
			queryData.setHolspaceOrExtLoginOrPdaUserNo(holspaceOrExtLogin);

			String customerOrCarrier = searchCrit.get(SEARCH_FIELD_CUSTOMER_OR_CARRIER);
			queryData.setCustomerOrCarrier(customerOrCarrier);

			Long roleId = null;
			if (!StringUtils.empty(searchCrit.get("roleId"))) {
				roleId = Long.valueOf(searchCrit.get("roleId"));
				queryData.setRoleId(roleId);
			}
		}
		
		if (!StringUtils.empty(searchCrit.get(UserAdminEntity.ATTR_ACTIVE_FLAG))) {
			boolean active = Boolean.valueOf(searchCrit.get(UserAdminEntity.ATTR_ACTIVE_FLAG));
			if (active) {
				queryData.setActiveFlag(active);
			}
		}
		if(StringUtils.notEmpty(sort)) {
			queryData.setOrderBy(sort);
			queryData.setAsc(asc);
		}

		String sqlQuery = queryData.getJpaQuery();
		Object[] paramArray = queryData.getQueryParams();
		List<Object[]> list = daoProvider.getDirectJpaDao().find(sqlQuery, paramArray);

		if (list == null || skip >= list.size()) {
			ListRange<UserAdminSrchBean> emptyList = new ListRange<UserAdminSrchBean>();
			emptyList.setData(new UserAdminSrchBean[0]);
			emptyList.setTotalSize(0);
			return emptyList;
		}

		int maxIndex = (list.size() < (skip + num)) ? list.size() : (skip + num);
		Map<Long, UserAdminSrchBean> userAdminSrcMap = new LinkedHashMap<Long, UserAdminSrchBean>();
		for (int i = skip; i < maxIndex; ++i) {
			UserAdminSrchBean bean = createUserAdminSearchBean((Object[]) list.get(i), queryData);
			userAdminSrcMap.put(bean.getUserId(), bean);
		}

		//--------------------------------------------------------------------------------------
		// Load the users with restriction
		//--------------------------------------------------------------------------------------
		Long sapBpId = userLoggedIn.getSapBp() == null ? null
				: userLoggedIn.getSapBp().getSapBpId();
		Long sapVedorId = userLoggedIn.getSapVendor() == null ? null
				: userLoggedIn.getSapVendor().getSapVendorId();

		Filter<UserAdminEntity> userAdminViewFilter = new FilterImpl<UserAdminEntity>();
		if (sapBpId != null) {
			userAdminViewFilter.addExpression(Restrictions.eq(UserAdminEntity.ATTR_SAP_BP_ID,
					sapBpId));
		}
		if (sapVedorId != null) {
			userAdminViewFilter.addExpression(Restrictions.eq(UserAdminEntity.ATTR_SAP_VENDOR_ID,
					sapVedorId));
		}
		userAdminViewFilter.addExpression(Restrictions.neq(UserAdminEntity.ATTR_USER_ID,
				userLoggedIn.getUserId()));
		userAdminViewFilter.addExpression(Restrictions.in(UserAdminEntity.ATTR_USER_ID,
				userAdminSrcMap.keySet()));

		List<UserAdminEntity> userList = userViewDao.findNRecs(0, 0, userAdminViewFilter);

		refreshUserAdminSrcMap(userAdminSrcMap, userList);

		ListRange<UserAdminSrchBean> users = new ListRange<UserAdminSrchBean>();
		users.setTotalSize(list.size());
		Collection<UserAdminSrchBean> userAdminSearchBeans = userAdminSrcMap.values();
		UserAdminSrchBean[] userArray = new UserAdminSrchBean[userAdminSearchBeans.size()];
		userAdminSearchBeans.toArray(userArray);
		users.setData(userArray);
		return users;
	}
	
	public boolean canModifyUser(Long userModifyId) {
		// [[
		boolean result = false;
		Object[][] params = {{"userModifyId", userModifyId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "canModifyUser", params);
		}
		// ]]
		try {
			Long userLoggedInId = getUserId();
			
			// check company code
			Map userLoggedInRestrMap = fetchAssignedCompCodeList(userLoggedInId, false);
			if (userLoggedInRestrMap != null && !userLoggedInRestrMap.isEmpty()) {
				Map userModifyRestrMap = fetchAssignedCompCodeList(userModifyId, false);
				if ((userModifyRestrMap == null || userModifyRestrMap.isEmpty())
						|| (Collections.disjoint(userLoggedInRestrMap.keySet(),
								userModifyRestrMap.keySet()))) { 
					LOG.debug("The logged in user with id: '" + userLoggedInId
							+ "' has less authorization than the modify user with id: '" + userModifyId
							+ "'");
					return result = false;
				}
			}
			
			// check sarea
			userLoggedInRestrMap = fetchAssignedSareaList(userLoggedInId, null, false);
			if (userLoggedInRestrMap != null && !userLoggedInRestrMap.isEmpty()) {
				Map userModifyRestrMap = fetchAssignedSareaList(userModifyId, null, false);
				if ((userModifyRestrMap == null || userModifyRestrMap.isEmpty())
						|| (Collections.disjoint(userLoggedInRestrMap.keySet(),
								userModifyRestrMap.keySet()))) {
					LOG.debug("The logged in user with id: '" + userLoggedInId
							+ "' has less authorization than the modify user with id: '"
							+ userModifyId + "'");
					return result = false;
				}
			}
			return result = true;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	private void refreshUserAdminSrcMap(Map<Long, UserAdminSrchBean> userAdminSrcMap, List<UserAdminEntity> userList) {
		
		Set<Long> authorizedUserIdSet = new HashSet<Long>();
		for (Iterator<UserAdminEntity> iter = userList.iterator(); iter.hasNext();) {
			UserAdminEntity entity = iter.next();
			
			authorizedUserIdSet.add(entity.getUserId());
		}
		
		for (Iterator<Long> iter = userAdminSrcMap.keySet().iterator(); iter.hasNext();) {
			Long userId = iter.next();
			if (!authorizedUserIdSet.contains(userId) || !canModifyUser(userId)) {
				UserAdminSrchBean bean = userAdminSrcMap.get(userId);
				bean.setReadOnly(true);
				bean.setUserId(null);
			}
		}
	}
	
	private UserAdminSrchBean createUserAdminSearchBean(Object[] objArray,
			UserAdminJpaQueryData<UserAdminJpaQueryData.ColumnNames> queryData) {
		// [[
		UserAdminSrchBean result = null;
		Object[][] params = {{"objArray", objArray},
							{"queryData", queryData}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createUserAdminSearchBean", params);
		}
		// ]]
		try {
			result = new UserAdminSrchBean();
			result.setActiveFlag((Boolean)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.ACTIVE_FLAG.toString())]);
			result.setCarrier((String)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.CARRIER.toString())]);
			result.setCompositeName((String)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.COMPOSITE_NAME.toString())]);
			result.setCustomer((String)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.CUSTOMER.toString())]);
			result.setExtLogin((String)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.EXT_LOGIN.toString())]);
			result.setFirstname((String)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.FIRSTNAME.toString())]);
			result.setLdapHolspace((String)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.LDAP_HOLSPACE.toString())]);
			result.setName((String)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.NAME.toString())]);
			result.setPdaUserNo((Long)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.PDA_USER_NO.toString())]);
			result.setPersonId((Long)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.PERSON_ID.toString())]);
			result.setRoleNames((String)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.ROLE_NAMES.toString())]);
			result.setSapBpId((Long)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.SAP_BP_ID.toString())]);
			result.setSapVendorId((Long)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.SAP_VENDOR_ID.toString())]);
			result.setUserId((Long)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.USER_ID.toString())]);
			result.setCertifiedFromDat((Date)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.CERTIFIED_FROM_DAT.toString())]);
			result.setCertifiedToDat((Date)objArray[queryData.getIndexOf(UserAdminJpaQueryData.ColumnNames.CERTIFIED_TO_DAT.toString())]);
			result.setReadOnly(false); // set to false as default
			
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

	public List fetchSoldToList(int skip, int num, String query, boolean byId) {
		// [[
		List result = null;
		Object[][] params = { { "skip", skip + "" }, { "num", num + "" }, { "query", query },
				{ "byId", byId + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSoldToList", params);
		}
		// ]]
		try {
			return result = fetchSoldToListImpl(skip, num, query, byId, false);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public long countSoldToList(String query, boolean byId) {
		// [[
		long result = 0L;
		Object[][] params = { { "query", query }, { "byId", byId + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countSoldToList", params);
		}
		// ]]
		try {
			List list = fetchSoldToListImpl(0, 2, query, byId, true);
			result = (Long) list.get(0);
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

	private List fetchSoldToListImpl(int skip, int num, String query, boolean byId, boolean count) {
		// [[
		List result = null;
		Object[][] params = { { "skip", skip + "" }, { "num", num + "" }, { "query", query },
				{ "byId", byId + "" }, { "count", count + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSoldToListImpl", params);
		}
		// ]]
		try {
			List customerList = new LinkedList();

			ReadOnlyDao<SapSoldtoSrchEntity> sapSoldtoSrchEntityDao = getDaoProvider().getSapSoldtoSrchEntityDao();
			Filter filter = new FilterImpl();

			/* Obviously not a number, now do wildcard replacements*/
			query = query.replace("|", "||");
			query = query.replace("%", "|%");
			query = query.replace("_", "|_");
			/* now replace the query wildcard characlter '*'
			 * by the JPAQL escape character*/
			query = query.replace('*', '%');
			/* finally add a wildcard character at the end of the String*/
			query = query += '%';
			/* now convert to uppercase */
			query = query.toUpperCase();

			if (byId) {
				filter.addExpression(Restrictions.like(SapSoldtoSrchEntity.ATTR_SOLDTO_SAP_ID,
						query));
			}
			else {
				filter.addExpression(Restrictions.ilike(SapSoldtoSrchEntity.ATTR_SOLDTO_NAME, query));
			}

			filter.addExpression(Order.asc(SapSoldtoSrchEntity.ATTR_SOLDTO_NAME));

			if (count) {
				long cnt = sapSoldtoSrchEntityDao.count(filter);
				customerList.add(Long.valueOf(cnt));
			}
			else {
				customerList = sapSoldtoSrchEntityDao.findNRecs(skip, num, filter);
			}

			return result = customerList;
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
	 * Returns the carrier for the vendor.
	 */
	public CarrierVendorEntity fetchCarrierVendor(long sapVendorId) {
		// [[
		CarrierVendorEntity result1 = null;
		Object[][] params = { { "sapVendorId", sapVendorId + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarrierVendor", params);
		}
		// ]]
		try {
			ReadOnlyDao<CarrierVendorEntity> dao = getDaoProvider().getCarrierVendorEntityDao();
			Filter filter = new FilterImpl();
			filter.addExpression(Restrictions.eq(CarrierVendorEntity.ATTR_SAP_VENDOR_ID,
					sapVendorId));
			CarrierVendorEntity carrier = dao.findSingle(filter);
			return result1 = carrier;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result1);
			}
			// ]]
		}
	}

	/**
	 * Returns carrierEntities.
	 */
	public List fetchCarrierVendorList(int skip, int num, String query, boolean byId) {
		// [[
		List result = null;
		Object[][] params = { { "skip", skip + "" }, { "num", num + "" }, { "query", query },
				{ "byId", byId + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarrierVendorList", params);
		}
		// ]]
		try {
			return result = fetchCarrierVendorListImpl(skip, num, query, byId, false);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public long countCarrierVendorList(String query, boolean byId) {
		// [[
		long result = 0L;
		Object[][] params = { { "query", query }, { "byId", byId + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "countCarrierVendorList", params);
		}
		// ]]
		try {
			List list = fetchCarrierVendorListImpl(0, 2, query, byId, true);

			result = (Long) list.get(0);
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
	 * Returns carriersEntities.
	 * @param skip
	 * @param num
	 * @param query
	 * @param byId
	 * @param count
	 * @return
	 */
	private List fetchCarrierVendorListImpl(int skip, int num, String query, boolean byId,
			boolean count) {
		// [[
		List result = null;
		Object[][] params = { { "skip", skip + "" }, { "num", num + "" }, { "query", query },
				{ "byId", byId + "" }, { "count", count + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchCarrierVendorListImpl", params);
		}
		// ]]
		try {
			List vendorList = new LinkedList();
			ReadOnlyDao<CarrierVendorEntity> carrierEntityDao = getDaoProvider().getCarrierVendorEntityDao();
			Filter filter = new FilterImpl();

			/* First replace special characters with escape sequences 
			 * We use | as the escape character */
			query = query.replace("|", "||");
			query = query.replace("%", "|%");
			query = query.replace("_", "|_");
			/* now replace the query wildcard characlter '*'
			 * by the JPAQL escape character*/
			query = query.replace('*', '%');
			/* finally add a wildcard character at the end of the String*/
			query = query += '%';
			/* now convert to uppercase */
			query = query.toUpperCase();

			if (byId) {
				filter.addExpression(Restrictions.like(CarrierVendorEntity.ATTR_SAP_ID,
						'%' + query + '%'));
			}
			else {
				filter.addExpression(Restrictions.or(
						Restrictions.ilike(CarrierVendorEntity.ATTR_NAME1, '%' + query + '%'),
						Restrictions.ilike(CarrierVendorEntity.ATTR_DISPLAY_NAME, '%' + query + '%')));
			}
			filter.addExpression(Order.ascCaseInsensitive(CarrierVendorEntity.ATTR_DISPLAY_NAME));

			if (count) {
				long cnt = carrierEntityDao.count(filter);
				vendorList.add(cnt);
			}
			else {
				vendorList = carrierEntityDao.findNRecs(skip, num, filter);
			}
			return result = vendorList;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public SapVendorEntity fetchSapVendorBySapId(String sapId) {
		// [[
		SapVendorEntity result = null;
		Object[][] params = { { "sapId", sapId } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchSapVendorBySapId", params);
		}
		// ]]
		try {
			ReadWriteDao<SapVendorEntity> dao = getDaoProvider().getSapVendorEntityDao();
			Filter filt = new FilterImpl();
			filt.addExpression(Restrictions.eq(SapVendorEntity.ATTR_SAP_ID, sapId));
			return result = dao.findSingle(filt);
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
	 * Loads all existing roles from TLGNU_ROLE. These are shown in role dropdown on the UI.
	 * @param for different user-types role-list sould be limited for certain values. For example 
	 * if user admin is adding a customer user type, for that type roles customer or 
	 * customer super user can be added
	 * @param orderby String
	 * @param asc boolean
	 * @adnlog.exclude return
	 */
	@AdnTrace
	public List<RoleEntity> fetchRoleList(String orderby, boolean asc) {
		ReadWriteDao<RoleEntity> roleDao = getDaoProvider().getRoleEntityDao();
		if (orderby == null) {
			return roleDao.find((Filter<RoleEntity>) null);
		}

		Filter<RoleEntity> filter = new FilterImpl<RoleEntity>();
		if (asc) {
			filter.addExpression(Order.asc(orderby));
		}
		else {
			filter.addExpression(Order.desc(orderby));
		}

		return roleDao.find(filter);
	}

	@AdnTrace
	public List<UserRoleEntity> fetchAssignedRoleList(long userId) {
		ReadWriteDao<UserRoleEntity> dao = getDaoProvider().getUserRoleEntityDao();
		Filter<UserRoleEntity> filter = new FilterImpl<UserRoleEntity>();
		filter.addExpression(Restrictions.eq(UserRoleEntity.ATTR_USER_ID, userId));
		filter.addExpression(Order.asc("role.roleName"));

		List<UserRoleEntity> selectedRoles = dao.find(filter);

		return selectedRoles;
	}

	@AdnTrace
	public List<RoleEntity> fetchGrantableRoleList(long loginUserId) {
		List<UserRoleEntity> assignedRoles = fetchAssignedRoleList(loginUserId);
		List<Long> assignedRoleIds = new ArrayList<Long>();

		for (UserRoleEntity userRole : assignedRoles) {
			RoleEntity role = userRole.getRole();
			Long roleId = role.getRoleId();
			assignedRoleIds.add(roleId);
		}

		if (!assignedRoleIds.isEmpty()) {
			// assigned functions
			ReadWriteDao<RoleFuncEntity> roleFunctionDao = getDaoProvider().getRoleFuncEntityDao();
			Filter<RoleFuncEntity> roleFunctionFilter = new FilterImpl<RoleFuncEntity>();
			roleFunctionFilter.addExpression(Restrictions.in(RoleFuncEntity.ATTR_ROLE_ID, assignedRoleIds));
			JPAQuery<RoleFuncEntity> query = roleFunctionDao.createJPAQuery();
			query.addSelect(SelectExpression.simpleSelect(RoleFuncEntity.ATTR_FUNC_ID));
			query.setFilter(roleFunctionFilter);
			@SuppressWarnings("unchecked")
			List<Long> assignedFunctionIds = roleFunctionDao.find(query);
			
			// grantable functions from ROLE by GRANTABLE_BY_FUNC_ID
			ReadWriteDao<RoleEntity> dao = getDaoProvider().getRoleEntityDao();
			Filter<RoleEntity> roleFilter = new FilterImpl<RoleEntity>();
			if (!assignedFunctionIds.isEmpty()) {
				roleFunctionFilter.addExpression(Restrictions.in(RoleEntity.ATTR_GRANTABLE_BY_FUNC_ID,
						assignedFunctionIds));
				roleFunctionFilter.addExpression(Order.asc(RoleEntity.ATTR_ROLE_NAME));
				return dao.find(roleFilter);
			}
		}
		return Collections.<RoleEntity> emptyList();
	}
	
	/**
	 * 
	 * @param userId
	 * @param loadWhenNoRestr
	 * @return
	 */
	public Map fetchAssignedSoldtoList(Long userId, boolean loadWhenNoRestr) {
		// [[
		Map result = null;
		Object[][] params = {{"userId", userId},
							{"loadWhenNoRestr", loadWhenNoRestr + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedSoldtoList", params);
		}
		// ]]
		try {		
			if (userId == null) {
				String errorMsg = "The 'userId' cannot be NULL!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			DaoProvider daoProvider = getDaoProvider();
			SapSoldtoSrchJpaQueryData<SapSoldtoSrchJpaQueryData.ColumnNames> queryData = new SapSoldtoSrchJpaQueryData<SapSoldtoSrchJpaQueryData.ColumnNames>(daoProvider, userId);
			queryData.addSelectColumnNames(SapSoldtoSrchJpaQueryData.COLUMNS_SET_1);
			String sqlQuery =  queryData.getJpaQuery();
			Object[] paramArray = queryData.getQueryParams();
			
			if (!loadWhenNoRestr && !queryData.hasUserRestriction(UserSoldtoBpAccEntity.class)) {
				LOG.info("The user: '" + userId + "' has no restriction in:'" + UserSoldtoBpAccEntity.class.getName() + "'");
				return result = null; // the user has no restriction --> return null;
			}
			
			List<Object[]> list = daoProvider.getDirectJpaDao().find(sqlQuery, paramArray);
			
			// create response
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(SapSoldtoSrchJpaQueryData.ColumnNames.SOLDTO_NAME.toString());
			result = LogonUtil.createMapFromList(list, queryData,
					SapSoldtoSrchJpaQueryData.ColumnNames.SOLDTO_SAP_ID.toString(), textColumns, "%s");

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

	public Map fetchAssignedShiptoList(Long userId, List soldToList, boolean loadWhenNoRestr, String format) {
		// [[
		Map result = null;
		Object[][] params = {{"userId", userId},
							{"loadWhenNoRestr", loadWhenNoRestr + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedShiptoList", params);
		}
		// ]]
		try {
			if (userId == null) {
				String errorMsg = "The 'userId' cannot be NULL!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			DaoProvider daoProvider = getDaoProvider();
			SoldshipFlexSrchJpaQueryData<SoldshipFlexSrchJpaQueryData.ColumnNames> queryData = new SoldshipFlexSrchJpaQueryData<SoldshipFlexSrchJpaQueryData.ColumnNames>(
					daoProvider, userId, soldToList);
			queryData.addSelectColumnNames(SoldshipFlexSrchJpaQueryData.COLUMNS_SET_1);

			String sqlQuery =  queryData.getJpaQuery();
			Object[] paramArray = queryData.getQueryParams();
			
			if (!loadWhenNoRestr && !queryData.hasUserRestriction(UserShiptoAccEntity.class)) {
				LOG.info("The user: '" + userId + "' has no restriction in:'" + UserShiptoAccEntity.class.getName() + "'");
				return result = null; // the user has no restriction --> return null;
			}
			
			List<Object[]> list = daoProvider.getDirectJpaDao().find(sqlQuery, paramArray);
			
			// create response
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(SoldshipFlexSrchJpaQueryData.ColumnNames.SOLDTO_NAME.toString());
			textColumns.add(SoldshipFlexSrchJpaQueryData.ColumnNames.SHIPTO_NAME.toString());
			textColumns.add(SoldshipFlexSrchJpaQueryData.ColumnNames.SHIPTO_NR.toString());
			result = LogonUtil.createMapFromList(list, queryData,
					SoldshipFlexSrchJpaQueryData.ColumnNames.COMPOSITE_ID.toString(), textColumns, format);
			
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
	 * Load TLGNC_POOL_TX information for the given user.  
	 * 
	 * The user pool restriction is stored in TLGNU_USER_POOL_ACC.
	 * 
	 * @param userId
	 * 			The user to use for load his pool information. NOT Nullable.
	 * @param loadWhenNoRestr
	 * 			when <code>true</code>, will load TLGNC_POOL_TX no matter if the user has no pool restriction,
	 * 			otherwise will load only if the user has pool restriction.
	 * @param locale
	 * 			The locale language to use at lookup. NOT Nullable.
	 * @return
	 * 			Return a Map where key = TLGNC_POOL_TX -> POOL_ID, value = LGNC_POOL_TX -> NAME			 
	 */
	public Map fetchPoolsAssignedToUser(Long userId, boolean loadWhenNoRestr, Locale locale) {
		// [[
		Map result = null;
		Object[][] params = {{"userId", userId}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchPoolsAssignedToUser", params);
		}
		// ]]
		try {
			if (userId == null || locale == null) {
				throw new IllegalArgumentException("The userId:'" + userId + "' and/or locale:'" + locale + "' is NULL!");
			}
			
			DaoProvider daoProvider = getDaoProvider();
			JpaQueryData<PoolTxJpaQueryData.ColumnNames> queryData = new PoolTxJpaQueryData<PoolTxJpaQueryData.ColumnNames>(daoProvider, userId, locale.getLanguage());
			queryData.addSelectColumnNames(PoolTxJpaQueryData.COLUMNS_SET_1);
			String sqlQuery =  queryData.getJpaQuery();
			Object[] paramArray = queryData.getQueryParams();
			
			if (!loadWhenNoRestr && !queryData.hasUserRestriction(UserPoolAccEntity.class)) {
				LOG.info("The user: '" + userId + "' has no restriction in:'" + UserPoolAccEntity.class.getName() + "'");
				return result = null; // the user has no restriction --> return null;
			}
			
			List list = daoProvider.getDirectJpaDao().find(sqlQuery, paramArray);
			
			// create response
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(PoolTxJpaQueryData.ColumnNames.NAME.toString());
			result = LogonUtil.createMapFromList(list, queryData,
					PoolTxJpaQueryData.ColumnNames.POOL_ID_FIELD_NAME.toString(), textColumns, "%s");

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
	 * Load carrier information for the given user.  
	 * 
	 * The user carrier restriction is stored in TLGNU_USER_SAP_VENDOR_ACC.
	 * 
	 * @param userId
	 * 			The user to use for load his carrier information. NOT Nullable.
	 * @param loadWhenNoRestr
	 * 			when <code>true</code>, will load TLGNC_POOL_TX no matter if the user has no pool restriction,
	 * @param format
	 * 			 A <a href="../util/Formatter.html#syntax">format string</a>, used to format
	 * 			the text for the result Map value
	 * @return
	 * 			Return a Map with carrier information
	 */
	public Map fetchAssignedCarrierList(Long userId, boolean loadWhenNoRestr, String format) {
		// [[
		Map result = null;
		Object[][] params = { { "userId", userId } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedCarrierList", params);
		}
		// ]]
		try {
			if (userId == null) {
				String errorMsg = "The 'userId' cannot be NULL!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			DaoProvider daoProvider = getDaoProvider();
			JpaQueryData<CarrierJpaQueryData.ColumnNames> jpaQueryData = new CarrierJpaQueryData<CarrierJpaQueryData.ColumnNames>(userId, daoProvider);
			jpaQueryData.addSelectColumnNames(CarrierJpaQueryData.COLUMNS_SET_1);
			String sqlQuery =  jpaQueryData.getJpaQuery();
			Object[] paramArray = jpaQueryData.getQueryParams();
					
			if (!loadWhenNoRestr && !jpaQueryData.hasUserRestriction(UserSapVendorAccEntity.class)) {
				LOG.info("The user: '" + userId + "' has no restriction in:'" + UserSapVendorAccEntity.class.getName() + "'");
				return result = null; // the user has no restriction --> return null;
			}
			
			List list = daoProvider.getDirectJpaDao().find(sqlQuery, paramArray);

			// create response
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(CarrierJpaQueryData.ColumnNames.DISPLAY_NAME.toString());
			textColumns.add(CarrierJpaQueryData.ColumnNames.NAME1.toString());
			textColumns.add(CarrierJpaQueryData.ColumnNames.SAP_ID.toString());
			result = LogonUtil.createMapFromList(list, jpaQueryData,
					CarrierJpaQueryData.ColumnNames.SAP_VENDOR_ID.toString(), textColumns, format);

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
	 * Load shippingpoint information for the given user.  
	 * 
	 * The user shippingpoint restriction is stored in TLGNU_USER_SHIPPINGPOINT_ACC.
	 * 
	 * @param userId
	 * 			The user to use for load his shippinpoint information. NOT Nullable.
	 * @param plantCdList
	 * 			Filter also for plants. Nullable.
	 * @param loadWhenNoRestr
	 * 			when <code>true</code>, will load TLGNC_POOL_TX no matter if the user has no pool restriction,
	 * @param format
	 * 			 A <a href="../util/Formatter.html#syntax">format string</a>, used to format
	 * 			the text for the return Map value
	 * @return
	 * 			Return a Map with shipping information.
	 */
	public Map fetchAssignedShippingPointList(Long userId, List plantCdList, boolean loadWhenNoRestr, String format) {
		// [[
		Map result = null;
		Object[][] params = {{"userId", userId},
							{"plantCdList", plantCdList},
							{"loadWhenNoRestr", loadWhenNoRestr + ""},
							{"format", format}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedShippingPointList", params);
		}
		// ]]
		try {
			if (userId == null) {
				String errorMsg = "The 'userId' cannot be NULL!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			DaoProvider daoProvider = getDaoProvider();
			PlantShippingpointJpaQueryData<PlantShippingpointJpaQueryData.ColumnNames> jpaQueryData = new PlantShippingpointJpaQueryData<PlantShippingpointJpaQueryData.ColumnNames>(
					daoProvider, userId, plantCdList);
			jpaQueryData.addSelectColumnNames(PlantShippingpointJpaQueryData.COLUMNS_SET_1);
			
			String queryString = jpaQueryData.getJpaQuery();
			Object[] values = jpaQueryData.getQueryParams();
			
			if (!loadWhenNoRestr && !jpaQueryData.hasUserRestriction(UserShippingpointAccEntity.class)) {
				LOG.info("The user: '" + userId + "' has no restriction in:'" + UserShippingpointAccEntity.class.getName() + "'");
				return result = null; // the user has no restriction --> return null;
			}
			
			List list = daoProvider.getDirectJpaDao().find(queryString, values);

			// create response
			List<String> textColumns = new ArrayList<String>();
			// The order is IMPORTANT! Will be used to retrieve the data from the object array ...
			textColumns.add(PlantShippingpointJpaQueryData.ColumnNames.PLANT_NAME.toString());
			textColumns.add(PlantShippingpointJpaQueryData.ColumnNames.SHIPPINGPOINT_NAME.toString());
			textColumns.add(PlantShippingpointJpaQueryData.ColumnNames.SAP_SHIPPINGPOINT_NO.toString());
			result = LogonUtil.createMapFromList(list, jpaQueryData,
					PlantShippingpointJpaQueryData.ColumnNames.COMPOSITE_ID.toString(), textColumns, format);

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
	 * 
	 * @param userId
	 * @param loadWhenNoRestr
	 * @return
	 */
	public Map fetchAssignedCompCodeList(Long userId, boolean loadWhenNoRestr) {
		// [[
		Map result = null;
		Object[][] params = {{"userId", userId},
							{"loadWhenNoRestr", loadWhenNoRestr + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedCompCodeList", params);
		}
		// ]]
		try {
			if (userId == null) {
				String errorMsg = "The 'userId' cannot be NULL!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			DaoProvider daoProvider = getDaoProvider();
			CompanycodeJpaQueryData<CompanycodeJpaQueryData.ColumnNames> jpaQueryData = new CompanycodeJpaQueryData<CompanycodeJpaQueryData.ColumnNames>(
					getDaoProvider(), userId);
			jpaQueryData.addSelectColumnNames(CompanycodeJpaQueryData.COLUMNS_SET_1);
			
			String queryString = jpaQueryData.getJpaQuery();
			Object[] values = jpaQueryData.getQueryParams();
			
			if (!loadWhenNoRestr && !jpaQueryData.hasUserRestriction(UserCompanycodeAccEntity.class)) {
				LOG.info("The user: '" + userId + "' has no restriction in:'" + UserCompanycodeAccEntity.class.getName() + "'");
				return result = null; // the user has no restriction --> return null;
			}
			
			List list = daoProvider.getDirectJpaDao().find(queryString, values);
			
			// create response
			/*
			 * The order of the text columns is important, it is used to generate the text value
			 * for the UI with the "format"
			 */
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(CompanycodeJpaQueryData.ColumnNames.COMPANYCODE_NAME.toString());
			result = LogonUtil.createMapFromList(list, jpaQueryData,
					CompanycodeJpaQueryData.ColumnNames.COMPANYCODE_CD.toString(), textColumns, "%s");
			
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

	public Map fetchAssignedSareaList(Long userId, List companycodeList, boolean loadWhenNoRestr) {
		// [[
		Map result = null;
		Object[][] params = {{"userId", userId},
							{"companycodeList", companycodeList},
							{"loadWhenNoRestr", loadWhenNoRestr + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedSareaList", params);
		}
		// ]]
		try {	
			if (userId == null) {
				String errorMsg = "The 'userId' cannot be NULL!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			DaoProvider daoProvider = getDaoProvider();

			CompCodeSareaJpaQueryData<CompCodeSareaJpaQueryData.ColumnNames> jpaQueryData = new CompCodeSareaJpaQueryData<CompCodeSareaJpaQueryData.ColumnNames>(
					getDaoProvider(), userId, companycodeList);
			jpaQueryData.addSelectColumnNames(CompCodeSareaJpaQueryData.COLUMNS_SET_1);
			
			String queryString = jpaQueryData.getJpaQuery();
			Object[] values = jpaQueryData.getQueryParams();
						
			if (!loadWhenNoRestr && !jpaQueryData.hasUserRestriction(UserSareaAccEntity.class)) {
				return result = null; // the user has no restriction --> return null;
			}
			
			
			List list = daoProvider.getDirectJpaDao().find(queryString, values);
			
			// create response
			/*
			 * The order of the text columns is important, it is used to generate the text value
			 * for the UI with the "format"
			 */
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(CompCodeSareaJpaQueryData.ColumnNames.SAREA_DISPLAY_LONGNAME.toString());
			result = LogonUtil.createMapFromList(list, jpaQueryData,
					CompCodeSareaJpaQueryData.ColumnNames.COMPOSITE_ID.toString(), textColumns, "%s");
			
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

	public Map fetchAssignedSgroupList(Long userId, boolean loadWhenNoRestr, String format) {
		// [[
		Map result = null;
		Object[][] params = {{"userId", userId},
							{"loadWhenNoRestr", loadWhenNoRestr + ""},
							{"format", format}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedSgroupList", params);
		}
		// ]]
		try {
			if (userId == null) {
				String errorMsg = "The 'userId' cannot be NULL!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			DaoProvider daoProvider = getDaoProvider();
			SgroupBsJpaQueryData<SgroupBsJpaQueryData.ColumnNames> jpaQueryData = new SgroupBsJpaQueryData<SgroupBsJpaQueryData.ColumnNames>(
					getDaoProvider(), userId);
			jpaQueryData.addSelectColumnNames(SgroupBsJpaQueryData.COLUMNS_SET_1);
			
			String queryString = jpaQueryData.getJpaQuery();
			Object[] values = jpaQueryData.getQueryParams();
			
			if (!loadWhenNoRestr && !jpaQueryData.hasUserRestriction(UserSgroupAccEntity.class)) {
				return result = null; // the user has no restriction --> return null;
			}
			
			List list = daoProvider.getDirectJpaDao().find(queryString, values);
			
			// create response
			/*
			 * The order of the text columns is important, it is used to generate the text value
			 * for the UI with the "format"
			 */
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(SgroupBsJpaQueryData.ColumnNames.SGROUP_NAME.toString());
			textColumns.add(SgroupBsJpaQueryData.ColumnNames.SGROUP_CD.toString());
			result = LogonUtil.createMapFromList(list, jpaQueryData,
					SgroupBsJpaQueryData.ColumnNames.SGROUP_CD.toString(), textColumns, format);
			
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

	public Map fetchAssignedPurchOrgList(Long userId, boolean loadWhenNoRestr) {
		// [[
		Map result = null;
		Object[][] params = {{"userId", userId},
							{"loadWhenNoRestr", loadWhenNoRestr + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedPurchOrgList", params);
		}
		// ]]
		try {
			if (userId == null) {
				String errorMsg = "The 'userId' cannot be NULL!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			DaoProvider daoProvider = getDaoProvider();
			PurchasingorgBsJpaQueryData<PurchasingorgBsJpaQueryData.ColumnNames> jpaQueryData = new PurchasingorgBsJpaQueryData<PurchasingorgBsJpaQueryData.ColumnNames>(
					getDaoProvider(), userId);
			jpaQueryData.addSelectColumnNames(PurchasingorgBsJpaQueryData.COLUMNS_SET_1);
			
			String queryString = jpaQueryData.getJpaQuery();
			Object[] values = jpaQueryData.getQueryParams();
			
			if (!loadWhenNoRestr && !jpaQueryData.hasUserRestriction(UserPurchorgAccEntity.class)) {
				return result = null; // the user has no restriction --> return null;
			}
			
			List list = daoProvider.getDirectJpaDao().find(queryString, values);
			
			// create response
			/*
			 * The order of the text columns is important, it is used to generate the text value
			 * for the UI with the "format"
			 */
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(PurchasingorgBsJpaQueryData.ColumnNames.PURCHASINGORG_NAME.toString());
			result = LogonUtil.createMapFromList(list, jpaQueryData,
					PurchasingorgBsJpaQueryData.ColumnNames.PURCHASINGORG_CD.toString(), textColumns, "%s");
			
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
	 * 
	 */
	public Map fetchAssignedPlantList(Long userId, List companyCodeList, List sareaList, boolean loadWhenNoRestr) {
		// [[
		Map result = null;
		Object[][] params = {{"userId", userId},
							{"companyCodeList", companyCodeList},
							{"sareaList", sareaList},
							{"loadWhenNoRestr", loadWhenNoRestr + ""}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchAssignedPlantList", params);
		}
		// ]]
		try {
			if (userId == null) {
				String errorMsg = "The 'userId' cannot be NULL!";
				LOG.error(errorMsg);
				throw new IllegalArgumentException(errorMsg);
			}
			
			// make sure we have a sareaList of Long objects
			Set<Long> sareaSet = getSareaIdsByCompositeIdList(sareaList);
			sareaList = (sareaSet == null || sareaSet.isEmpty()) ? null : new ArrayList<Long>(sareaSet);
			DaoProvider daoProvider = getDaoProvider();
			PlantJpaQueryData<PlantJpaQueryData.ColumnNames> jpaQueryData = new PlantJpaQueryData<PlantJpaQueryData.ColumnNames>(
					getDaoProvider(), userId);
			jpaQueryData.addSelectColumnNames(PlantJpaQueryData.COLUMNS_SET_1);
			jpaQueryData.setCompanycodeCdList(companyCodeList);
			jpaQueryData.setSareaList(sareaList);
			jpaQueryData.setRestrToCompcode(true);
			jpaQueryData.setRestrToSarea(true);
			
			String queryString = jpaQueryData.getJpaQuery();
			Object[] values = jpaQueryData.getQueryParams();
			
			if (!loadWhenNoRestr && !jpaQueryData.hasUserRestriction(UserPlantAccEntity.class)) {
				return result = null; // the user has no restriction --> return null;
			}
			
			List list = daoProvider.getDirectJpaDao().find(queryString, values);
			
			// create response
			/*
			 * The order of the text columns is important, it is used to generate the text value
			 * for the UI with the "format"
			 */
			List<String> textColumns = new ArrayList<String>();
			textColumns.add(PlantJpaQueryData.ColumnNames.PLANT_NAME.toString());
			result = LogonUtil.createMapFromList(list, jpaQueryData,
					PlantJpaQueryData.ColumnNames.PLANT_CD.toString(), textColumns, "%s");
			
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
	 * Load Sarea ids based by the given list of VLGNU_COMP_CODE_SAREA.COMPOSITE_ID records
	 * @param sareaCompositeIdList
	 * @return
	 */
	private Set<Long> getSareaIdsByCompositeIdList(List sareaCompositeIdList) {
		// [[
		Set<Long> result = null;
		Object[][] params = {{"sareaCompositeIdList", sareaCompositeIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSareaIdsByCompositeIdList", params);
		}
		// ]]
		try {
			List<CompanycodeSareaBean> list = getCompanycodeSareaByCompositeIdList(sareaCompositeIdList);
			if (list == null || list.isEmpty()) {
				return result = null;
			}
			
			result = new HashSet<Long>();
			for (Iterator<CompanycodeSareaBean> iter = list.iterator(); iter.hasNext();) { 
				CompanycodeSareaBean bean = iter.next();
				
				result.add(bean.getSareaId());
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
	 * Load a list of {@link CompanycodeSareaBean} based by the given list of VLGNU_COMP_CODE_SAREA.COMPOSITE_ID records
	 * 
	 * @param sareaCompositeIdList
	 * @return
	 */
	private List<CompanycodeSareaBean> getCompanycodeSareaByCompositeIdList(List sareaCompositeIdList) {
		// [[
		List<CompanycodeSareaBean> result = null;
		Object[][] params = {{"sareaCompositeIdList", sareaCompositeIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSareaIdsByCompositeIdList", params);
		}
		// ]]
		try {
			
			if (sareaCompositeIdList == null || sareaCompositeIdList.isEmpty()) {
				return result = null;
			}
			DaoProvider daoProvider = getDaoProvider();
			
			CompCodeSareaJpaQueryData<CompCodeSareaJpaQueryData.ColumnNames> jpaQueryData = new CompCodeSareaJpaQueryData<CompCodeSareaJpaQueryData.ColumnNames>(
					getDaoProvider(), null);
			jpaQueryData.addSelectColumnNames(CompCodeSareaJpaQueryData.COLUMNS_SET_1);
			jpaQueryData.setCompositeIdList(sareaCompositeIdList);
			
			String queryString = jpaQueryData.getJpaQuery();
			Object[] values = jpaQueryData.getQueryParams();
			
			List list = daoProvider.getDirectJpaDao().find(queryString, values);
			if (list == null || list.isEmpty()) {
				return result = null;
			}
			
			int keyColIdx = jpaQueryData.getIndexOf(CompCodeSareaJpaQueryData.ColumnNames.SAREA_ID.toString());
			int key2ColIdx = jpaQueryData.getIndexOf(CompCodeSareaJpaQueryData.ColumnNames.COMPANYCODE_CD.toString());
			result = new ArrayList<CompanycodeSareaBean>();
			CompanycodeSareaBean bean = null;
			for (Iterator<Object[]> iter = list.iterator(); iter.hasNext();) { 
				Object[] objArray = iter.next();
				bean = new CompanycodeSareaBean(StringUtils.nvl((String)objArray[key2ColIdx]), (Long)objArray[keyColIdx]);
				result.add(bean);
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
	 * Load a list of {@link PlantShippingpointBean} based by the given list of VLGNU_PLANT_SHIPPINGPOINT.COMPOSITE_ID records
	 * 
	 * @param sareaCompositeIdList
	 * @return
	 */
	private List<PlantShippingpointBean> getPlantShippingpointByCompositeIdList(List plantShpCompositeIdList) {
		// [[
		List<PlantShippingpointBean> result = null;
		Object[][] params = {{"plantShpCompositeIdList", plantShpCompositeIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getPlantShippingpointByCompositeIdList", params);
		}
		// ]]
		try {
			
			if (plantShpCompositeIdList == null || plantShpCompositeIdList.isEmpty()) {
				return result = null;
			}
			DaoProvider daoProvider = getDaoProvider();
			
			PlantShippingpointJpaQueryData<PlantShippingpointJpaQueryData.ColumnNames> jpaQueryData = new PlantShippingpointJpaQueryData<PlantShippingpointJpaQueryData.ColumnNames>(
					getDaoProvider(), null);
			jpaQueryData.addSelectColumnNames(PlantShippingpointJpaQueryData.COLUMNS_SET_1);
			jpaQueryData.setCompositeIdList(plantShpCompositeIdList);
			
			String queryString = jpaQueryData.getJpaQuery();
			Object[] values = jpaQueryData.getQueryParams();
			
			List list = daoProvider.getDirectJpaDao().find(queryString, values);
			if (list == null || list.isEmpty()) {
				return result = null;
			}
			
			int keyColIdx = jpaQueryData.getIndexOf(PlantShippingpointJpaQueryData.ColumnNames.PLANT_CD.toString());
			int key2ColIdx = jpaQueryData.getIndexOf(PlantShippingpointJpaQueryData.ColumnNames.SHIPPINGPOINT_ID.toString());
			result = new ArrayList<PlantShippingpointBean>();
			PlantShippingpointBean bean = null;
			for (Iterator<Object[]> iter = list.iterator(); iter.hasNext();) { 
				Object[] objArray = iter.next();
				bean = new PlantShippingpointBean(StringUtils.nvl((String)objArray[keyColIdx]), (Long)objArray[key2ColIdx]);
				result.add(bean);
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
	 * Load a list of {@link SoldshipFlexSrchBean} based by the given list of VLGNS_SOLDSHIP_FLEX_SRCH.COMPOSITE_ID records
	 * 
	 * @param sareaCompositeIdList
	 * @return
	 */
	private List<SoldshipFlexSrchBean> getSoldtoshipFlexSrcByCompositeIdList(List soldtoShiptoCompositeIdList) {
		// [[
		List<SoldshipFlexSrchBean> result = null;
		Object[][] params = {{"soldtoShiptoCompositeIdList", soldtoShiptoCompositeIdList}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getSoldtoshipFlexSrcByCompositeIdList", params);
		}
		// ]]
		try {
			
			if (soldtoShiptoCompositeIdList == null || soldtoShiptoCompositeIdList.isEmpty()) {
				return result = null;
			}
			DaoProvider daoProvider = getDaoProvider();
			
			SoldshipFlexSrchJpaQueryData<SoldshipFlexSrchJpaQueryData.ColumnNames> jpaQueryData = new SoldshipFlexSrchJpaQueryData<SoldshipFlexSrchJpaQueryData.ColumnNames>(
					getDaoProvider(), null);
			jpaQueryData.addSelectColumnNames(SoldshipFlexSrchJpaQueryData.COLUMNS_SET_1);
			jpaQueryData.setCompositeIdList(soldtoShiptoCompositeIdList);
			
			String queryString = jpaQueryData.getJpaQuery();
			Object[] values = jpaQueryData.getQueryParams();
			
			List list = daoProvider.getDirectJpaDao().find(queryString, values);
			if (list == null || list.isEmpty()) {
				return result = null;
			}
			
			int keyColIdx = jpaQueryData.getIndexOf(SoldshipFlexSrchJpaQueryData.ColumnNames.SOLDTO_SAP_BP_ID.toString());
			int key2ColIdx = jpaQueryData.getIndexOf(SoldshipFlexSrchJpaQueryData.ColumnNames.SHIPTO_SAP_BP_ID.toString());
			result = new ArrayList<SoldshipFlexSrchBean>();
			SoldshipFlexSrchBean bean = null;
			for (Iterator<Object[]> iter = list.iterator(); iter.hasNext();) { 
				Object[] objArray = iter.next();
				bean = new SoldshipFlexSrchBean((Long)objArray[key2ColIdx], (Long)objArray[keyColIdx]);
				result.add(bean);
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
	 *Fetches the details of a user identified by it's id 
	 */
	public UserEntity loadUserDetail(Long id) {
		// [[
		UserEntity result = null;
		Object[][] params = { { "id", id } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadUserDetail", params);
		}
		// ]]
		try {
			ReadWriteDao<UserEntity> userDao = getDaoProvider().getUserEntityDao();
			UserEntity userEntity = userDao.findById(id);

			if (userEntity.getPdaUserRfidobject() != null) {
				userEntity.getPdaUserRfidobject().initialize();
			}
			return result = userEntity;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public List loadDriverDetail(Long userId) {
		// [[
		List result = null;
		Object[][] params = { { "userId", userId } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "loadDriverDetail", params);
		}
		// ]]
		try {
			ReadWriteDao<DriverEntity> driverEntityDao = getDaoProvider().getDriverEntityDao();
			Filter driverFilter = new FilterImpl();
			driverFilter.addExpression(Restrictions.eq(DriverEntity.ATTR_USER_ID, userId));
			List driverList = driverEntityDao.find(driverFilter);

			return result = driverList;
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
	 * Adds a new user in the db. 
	 * @param userToAdd - user entity containing inputs from front-end
	 * @param roleIds
	 * @param customerIds
	 * @param shipToIds
	 * @param carrierIds
	 * @param poolIds
	 */
	public UserEntity addUser(Long userIdPerformingOperation, UserEntity userToAdd,
			DriverEntity driverToAdd, Map userSelections, boolean driverFlag) {
		// [[
		UserEntity result = null;
		Object[][] params = { { "userIdPerformingOperation", userIdPerformingOperation },
				{ "userToAdd", userToAdd }, { "driverToAdd", driverToAdd },
				{ "userSelections", userSelections }, { "driverFlag", driverFlag + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addUser", params);
		}
		// ]]
		try {
			DaoProvider daoProvider = getDaoProvider();
			//setting entitys to null that we don't want to save
			if (userToAdd.getSapBp() != null && userToAdd.getSapBp().getSapBpId() == null) {
				userToAdd.setSapBp(null);
			}
			if (userToAdd.getSapVendor() != null
					&& userToAdd.getSapVendor().getSapVendorId() == null) {
				userToAdd.setSapVendor(null);
			}
			if (userToAdd.getSignature() != null) {

				SignatureEntity signature = userToAdd.getSignature();

				daoProvider.getSignatureEntityDao().persist(signature);

				userToAdd.setSignature(signature);

			}
			if (userToAdd.getPhotoImage() != null) {
				ImageEntity photoImage = userToAdd.getPhotoImage();

				Filter imageNameFilter = new FilterImpl();
				imageNameFilter.addExpression(Restrictions.eq(ImageEntity.ATTR_IMAGE_NAME,
						photoImage.getImageName()));

				List sameNamedImages = daoProvider.getImageEntityDao().find(imageNameFilter);

				if (sameNamedImages != null && !sameNamedImages.isEmpty()) {
					throw new LogonException("Given Image Name is already used",
							Reason.RES_IMAGE_NAME_NOT_UNIQUE);
				}

				daoProvider.getImageEntityDao().persist(photoImage);

				userToAdd.setPhotoImage(photoImage);

			}
			if (!StringUtils.empty(userToAdd.getLdapHolspace())) {
				//check holspace uniqueness
				Filter hspFilter = new FilterImpl();
				hspFilter.addExpression(Restrictions.eq(UserEntity.ATTR_LDAP_HOLSPACE,
						userToAdd.getLdapHolspace()));

				List usersWithSameHspList = daoProvider.getUserEntityDao().find(hspFilter);

				if (usersWithSameHspList != null && !usersWithSameHspList.isEmpty()) {
					throw new LogonException("Given Ldap Holspace is already used",
							Reason.RES_USER_LDAP_HOLSPACE_NOT_UNIQUE);
				}

			}
			if (userToAdd.getPdaUserNo() != null) {
				//check pda user uniqueness
				Filter pdaUserNoFilter = new FilterImpl();
				pdaUserNoFilter.addExpression(Restrictions.eq(UserEntity.ATTR_PDA_USER_NO,
						userToAdd.getPdaUserNo()));

				List usersWithSamePDAUserNo = daoProvider.getUserEntityDao().find(pdaUserNoFilter);

				if (usersWithSamePDAUserNo != null && !usersWithSamePDAUserNo.isEmpty()) {
					throw new LogonException("Given PDA PIN is already used",
							Reason.RES_USER_PDA_USER_NUMBER_NOT_UNIQUE);
				}
				RfidobjectEntity rfidobject = userToAdd.getPdaUserRfidobject();

				//it can be not null, if the rfid popup was called once, create one though if it does not exist
				// TODO (emoke Dec 2, 2009):This is impossible since init initUser of abstractaction it is created anyway remove this part, but not today
				if (rfidobject == null) {
					rfidobject = new RfidobjectEntity();
					RfidobjectTypeBsEntity rfidType = referentialDataService.findBsEntity(RfidobjectTypeBsEntity.class, 
							Constant.RFIDOBJECT_TYPE_PDA_USER);
					rfidobject.setRfidobjectType(rfidType);
					userToAdd.setPdaUserRfidobject(rfidobject);
				}

				RFIDBean.AbstractRFIDBean bean = RFIDBean.UserRFIDBean(userToAdd);
				rfidobject = rfIdService.validateAndPersistRFIDObject(Constant.RFIDOBJECT_TYPE_PDA_USER,
						bean);

				userToAdd.setPdaUserRfidobject(rfidobject);
			}
			else {
				userToAdd.setPdaUserRfidobject(null);
			}

			daoProvider.getPersonEntityDao().persist(userToAdd.getPerson());
			userToAdd = daoProvider.getUserEntityDao().persist(userToAdd);
			if (driverToAdd != null && driverFlag) {
				driverToAdd.setActiveFlag(Boolean.TRUE);
				driverToAdd.setUser(userToAdd);
				daoProvider.getDriverEntityDao().persist(driverToAdd);
			}
			//adding the selections the user made (storing them to access-tables)

			List roleIdList = null;
			List soldToIdList = null;
			List shipToList = null;
			List poolIdList = null;
			List carrierIdList = null;
			List plantIdList = null;
			List shippingpointList = null;
			List compCodeIdList = null;
			List sareaIdList = null;
			List sgroupIdList = null;
			List purchOrgIdList = null;

			roleIdList = (List) userSelections.get("roleIds");
			addSelectedRoles(roleIdList, userToAdd);

			shipToList = (List) userSelections.get("shipToIds");
			soldToIdList = (List) userSelections.get("soldToIds");
			handleSoldToShipToRestricitons(soldToIdList, shipToList, userToAdd);

			poolIdList = (List) userSelections.get("poolIds");
			addSelectedPools(poolIdList, userToAdd);
			carrierIdList = (List) userSelections.get("carrierIds");
			addSelectedSapVendors(carrierIdList, userToAdd);

			plantIdList = (List) userSelections.get("plantIds");
			shippingpointList = (List) userSelections.get("shippingpointIds");
			handlePlantShippingpointRestrictions(plantIdList, shippingpointList, userToAdd);

			compCodeIdList = (List) userSelections.get("compCodeIds");
			sareaIdList = (List) userSelections.get("sareaIds");
			handleCompCodeSareaRestrictions(compCodeIdList, sareaIdList, userToAdd);

			sgroupIdList = (List) userSelections.get("sgroupIds");
			addSelectedSgroups(sgroupIdList, userToAdd);

			purchOrgIdList = (List) userSelections.get("purchOrgIds");
			addSelectedPurchOrgIds(purchOrgIdList, userToAdd);

			if (StringUtils.notEmpty(userToAdd.getExtLogin())
					&& StringUtils.notEmpty(userToAdd.getExtLoginPwd())) {
				userToAdd = externalPasswordChange(userToAdd.getExtLogin(),
						userToAdd.getExtLoginPwd(), true);
			}
			audit(userIdPerformingOperation, userToAdd, Constant.AUDIT_ACTION_USERADMIN_NEW_USER);

			return result = userToAdd;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public UserEntity modifyUser(Long userIdPerformingOperation, UserEntity userToModify,
			DriverEntity driverToModify, Map userSelections, boolean self) {
		// [[
		UserEntity result = null;
		Object[][] params = { { "userIdPerformingOperation", userIdPerformingOperation },
				{ "userToModify", userToModify }, { "driverToModify", driverToModify },
				{ "userSelections", userSelections }, { "self", self + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "modifyUser", params);
		}
		// ]]
		try {
			DaoProvider daoProvider = getDaoProvider();

			if (!StringUtils.empty(userToModify.getLdapHolspace())) {
				//check holspace uniqueness
				Filter hspFilter = new FilterImpl();
				hspFilter.addExpression(Restrictions.eq(UserEntity.ATTR_LDAP_HOLSPACE,
						userToModify.getLdapHolspace()));
				hspFilter.addExpression(Restrictions.neq(UserEntity.ATTR_USER_ID,
						userToModify.getUserId()));

				List usersWithSameHspList = daoProvider.getUserEntityDao().find(hspFilter);

				if (usersWithSameHspList != null && !usersWithSameHspList.isEmpty()) {
					throw new LogonException("Given Ldap Holspace is already used",
							Reason.RES_USER_LDAP_HOLSPACE_NOT_UNIQUE);
				}

			}

			boolean isPwdChanging = false;
			boolean isReactivatingUser = false;
			boolean isInactivatingUser = false;

			UserEntity oldUser = daoProvider.getUserEntityDao().findById(userToModify.getUserId());

			Boolean userWasActive = oldUser.getPerson().getActiveFlag();
			Boolean userIsActive = userToModify.getPerson().getActiveFlag();

			if (!userWasActive && userIsActive) {
				isReactivatingUser = true;
			}
			if (userWasActive && !userIsActive) {
				isInactivatingUser = true;
			}

			String oldExtLoginPwd = StringUtils.nvl(oldUser.getExtLoginPwd());
			String newExtLoginPwd = StringUtils.nvl(userToModify.getExtLoginPwd());

			if (!oldExtLoginPwd.equals(newExtLoginPwd)) {
				isPwdChanging = true;
			}
			//setting entitys to null that we don't want to save
			if (userToModify.getSapBp() != null && userToModify.getSapBp().getSapBpId() == null) {
				userToModify.setSapBp(null);
			}
			if (userToModify.getSapVendor() != null
					&& userToModify.getSapVendor().getSapVendorId() == null) {
				userToModify.setSapVendor(null);
			}
			if (!self) {
				if (userToModify.getSignature() != null) {
					SignatureEntity signature = userToModify.getSignature();
					ReadWriteDao<SignatureEntity> signatureEntityDao = daoProvider.getSignatureEntityDao();

					if (signature.getSignatureId() == null) {
						signatureEntityDao.persist(signature);
					}
					else {
						signatureEntityDao.merge(signature);
					}

					userToModify.setSignature(signature);

				}
				if (userToModify.getPhotoImage() != null) {
					ImageEntity photoImage = userToModify.getPhotoImage();
					ReadWriteDao<ImageEntity> imageEntityDao = daoProvider.getImageEntityDao();

					Filter imageNameFilter = new FilterImpl();
					imageNameFilter.addExpression(Restrictions.eq(ImageEntity.ATTR_IMAGE_NAME,
							photoImage.getImageName()));

					List sameNamedImages = daoProvider.getImageEntityDao().find(imageNameFilter);

					if (photoImage.getImageId() == null) {
						if (sameNamedImages != null && !sameNamedImages.isEmpty()) {
							throw new LogonException("Given Image Name is already used",
									Reason.RES_IMAGE_NAME_NOT_UNIQUE);
						}
						imageEntityDao.persist(photoImage);
					}
					else {
						if (sameNamedImages != null && sameNamedImages.size() == 1) {
							ImageEntity imageWithSameName = (ImageEntity) sameNamedImages.get(0);
							if (!imageWithSameName.getImageId().equals(photoImage.getImageId())) {
								throw new LogonException("Given Image Name is already used",
										Reason.RES_IMAGE_NAME_NOT_UNIQUE);
							}
						}
						imageEntityDao.merge(photoImage);
					}

					userToModify.setPhotoImage(photoImage);

				}

				// PDA

				updatePdaAndRelatedData(userToModify, daoProvider, oldUser);

				// Driver

				ReadWriteDao<DriverEntity> driverEntityDao = daoProvider.getDriverEntityDao();
				if (driverToModify != null) {
					if (driverToModify.getDriverId() != null) {
						//update existing
						driverEntityDao.merge(driverToModify);
					}
					else {
						//persist new
						driverToModify.setActiveFlag(Boolean.TRUE);
						driverToModify.setUser(userToModify);
						daoProvider.getDriverEntityDao().persist(driverToModify);
					}
				}
				if (userSelections != null) {
					List roleIdList = null;
					List soldToIdList = null;
					List shipToList = null;
					List poolIdList = null;
					List carrierIdList = null;
					List plantIdList = null;
					List shippingpointList = null;
					List compCodeIdList = null;
					List sareaIdList = null;
					List sgroupIdList = null;
					List purchOrgIdList = null;

					ReadWriteDao<UserRoleEntity> userRoleEntityDao = daoProvider.getUserRoleEntityDao();
					Filter filt = new FilterImpl();
					filt.addExpression(Restrictions.eq(UserRoleEntity.ATTR_USER_ID,
							userToModify.getUserId()));
					List toRemove = userRoleEntityDao.find(filt);

					List toAdd = new ArrayList();

					roleIdList = (List) userSelections.get("roleIds");
					for (Iterator iterator = roleIdList.iterator(); iterator.hasNext();) {
						Long t = (Long) iterator.next();

						Filter filter = new FilterImpl();
						filter.addExpression(Restrictions.eq(UserRoleEntity.ATTR_USER_ID,
								userToModify.getUserId()));
						filter.addExpression(Restrictions.eq(UserRoleEntity.ATTR_ROLE_ID, t));
						UserRoleEntity entity = userRoleEntityDao.findSingle(filter);
						if (entity == null) { // selected, but not in db - needs to be added
							toAdd.add(t);
						}
						else { // selected, and exists in db - do not change
							toRemove.remove(entity);
						}
					}

					for (Iterator iterator = toRemove.iterator(); iterator.hasNext();) {
						UserRoleEntity userRoleEntityToRemove = (UserRoleEntity) iterator.next();
						userRoleEntityDao.remove(userRoleEntityToRemove);
					}

					for (Iterator iterator = toAdd.iterator(); iterator.hasNext();) {
						Long roleId = (Long) iterator.next();
						UserRoleEntity userRoleEntityToAdd = new UserRoleEntity();
						Filter filter = new FilterImpl();
						filter.addExpression(Restrictions.eq(RoleEntity.ATTR_ROLE_ID, roleId));
						RoleEntity roleEntity = daoProvider.getRoleEntityDao().findSingle(filter);
						userRoleEntityToAdd.setUser(userToModify);
						userRoleEntityToAdd.setRole(roleEntity);
						userRoleEntityDao.persist(userRoleEntityToAdd);
					}

					soldToIdList = (List) userSelections.get("soldToIds");
					shipToList = (List) userSelections.get("shipToIds");

					handleSoldToShipToRestricitons(soldToIdList, shipToList, userToModify);

					poolIdList = (List) userSelections.get("poolIds");
					removeAssignedPools(userToModify);
					addSelectedPools(poolIdList, userToModify);

					carrierIdList = (List) userSelections.get("carrierIds");
					removeAssignedSapVendors(userToModify);
					addSelectedSapVendors(carrierIdList, userToModify);

					plantIdList = (List) userSelections.get("plantIds");
					shippingpointList = (List) userSelections.get("shippingpointIds");
					handlePlantShippingpointRestrictions(plantIdList, shippingpointList,
							userToModify);

					compCodeIdList = (List) userSelections.get("compCodeIds");
					sareaIdList = (List) userSelections.get("sareaIds");
					handleCompCodeSareaRestrictions(compCodeIdList, sareaIdList, userToModify);

					sgroupIdList = (List) userSelections.get("sgroupIds");
					removeAssignedSgroups(userToModify);
					addSelectedSgroups(sgroupIdList, userToModify);

					purchOrgIdList = (List) userSelections.get("purchOrgIds");
					removeAssignedPurchOrgs(userToModify);
					addSelectedPurchOrgIds(purchOrgIdList, userToModify);

				}
			}//end of not self block
			else {
				//in case a user does not have an rfid and rfid sync did not run yet, rfidobject must be created in certain cases
				updatePdaAndRelatedData(userToModify, daoProvider, oldUser);
			}
			daoProvider.getPersonEntityDao().merge(userToModify.getPerson());
			userToModify = daoProvider.getUserEntityDao().merge(userToModify);

			if (userToModify.getPdaUserNo() == null) {
				userToModify = rfIdService.removeRfidObjectOfUser(userToModify);
			}

			if (isPwdChanging) {
				// change external login password
				if (StringUtils.notEmpty(userToModify.getExtLogin())
						&& StringUtils.notEmpty(userToModify.getExtLoginPwd())) {
					if (self) {
						userToModify = externalPasswordChange(userToModify.getExtLogin(),
								userToModify.getExtLoginPwd(), false);
					}
					else {
						userToModify = externalPasswordChange(userToModify.getExtLogin(),
								userToModify.getExtLoginPwd(), true);

						// unlock external password if neccessary
						if (userToModify.getExtLoginPwdLockedTs() != null) {
							LOG.info("unlock external login password, extLogin: "
									+ userToModify.getExtLogin());
							userToModify = unlockExtLogin(userToModify);
							sendExternalLoginPasswordUnlockedMail(userToModify);
						}
					}
				}
				audit(userIdPerformingOperation, userToModify,
						Constant.AUDIT_ACTION_USERADMIN_PASSWORD_CHANGE);
			}
			if (isReactivatingUser) {
				audit(userIdPerformingOperation, userToModify,
						Constant.AUDIT_ACTION_USERADMIN_ACTIVATE_USER);
			}
			if (isInactivatingUser) {
				audit(userIdPerformingOperation, userToModify,
						Constant.AUDIT_ACTION_USERADMIN_INACTIVATE_USER);
			}
			return result = userToModify;
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
	 * @param userToModify
	 * @param daoProvider
	 * @param oldUser
	 */
	private void updatePdaAndRelatedData(UserEntity userToModify, DaoProvider daoProvider,
			UserEntity oldUser) {
		Long pdaUserNo = userToModify.getPdaUserNo();
		Long oldPdaUserNo = oldUser.getPdaUserNo();
		RfidobjectEntity oldUserRfid = oldUser.getPdaUserRfidobject();
		if (oldPdaUserNo == null && pdaUserNo == null && oldUserRfid == null) {
			//in case there was no rfid, and it did not get a new pda user no
			userToModify.setPdaUserRfidobject(null);
		}
		if (pdaUserNo != null) {
			//check pda user uniqueness
			Filter pdaUserNoFilter = new FilterImpl();
			pdaUserNoFilter.addExpression(Restrictions.eq(UserEntity.ATTR_PDA_USER_NO, pdaUserNo));
			pdaUserNoFilter.addExpression(Restrictions.neq(UserEntity.ATTR_USER_ID,
					userToModify.getUserId()));

			List usersWithSamePDAUserNo = daoProvider.getUserEntityDao().find(pdaUserNoFilter);

			if (usersWithSamePDAUserNo != null && !usersWithSamePDAUserNo.isEmpty()) {
				throw new LogonException("Given PDA PIN is already used",
						Reason.RES_USER_PDA_USER_NUMBER_NOT_UNIQUE);
			}

			// TODO (istvan Jun 24, 2009): add rfid object modification code later if needed (check with matyas)

			if (oldPdaUserNo == null) {
				RfidobjectEntity rfidobject = userToModify.getPdaUserRfidobject();

				// TODO (emoke Dec 2, 2009):This is impossible since in initUser of abstractaction it is created anyway remove this part, but not today
				if (rfidobject == null) {
					rfidobject = new RfidobjectEntity();
					RfidobjectTypeBsEntity rfidType = referentialDataService.findBsEntity(RfidobjectTypeBsEntity.class, 
							Constant.RFIDOBJECT_TYPE_PDA_USER);
					rfidobject.setRfidobjectType(rfidType);
					userToModify.setPdaUserRfidobject(rfidobject);
				}
				if (rfidobject.getRfidobjectId() == null) {
					RFIDBean.AbstractRFIDBean bean = RFIDBean.UserRFIDBean(userToModify);
					rfidobject = rfIdService.validateAndPersistRFIDObject(
							Constant.RFIDOBJECT_TYPE_PDA_USER, bean);
				}
				else {
					//it was inactivated last time
					RFIDBean.AbstractRFIDBean bean = RFIDBean.UserRFIDBean(userToModify);
					rfidobject = rfIdService.mergeRfIdObject(bean,
							Constant.RFIDOBJECT_TYPE_PDA_USER);
				}
				userToModify.setPdaUserRfidobject(rfidobject);
			}
			else {
				RFIDBean.AbstractRFIDBean bean = RFIDBean.UserRFIDBean(userToModify);
				RfidobjectEntity rfidobject = rfIdService.mergeRfIdObject(bean,
						Constant.RFIDOBJECT_TYPE_PDA_USER);
				userToModify.setPdaUserRfidobject(rfidobject);
			}
		}
	}

	public List fetchUserEntityListForExtLogin(String extLogin) {
		// [[
		List result = null;
		Object[][] params = { { "extLogin", extLogin } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchUserEntityListForExtLogin", params);
		}
		// ]]
		try {
			Filter userPwdFilter = new FilterImpl();
			userPwdFilter.addExpression(Restrictions.eq(UserEntity.ATTR_EXT_LOGIN, extLogin));
			List userList = getDaoProvider().getUserEntityDao().find(userPwdFilter);
			return result = userList;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private void removeAssignedPools(UserEntity user) {
		// [[
		Object[][] params = { { "user", user } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeAssignedPools", params);
		}
		// ]]
		try {
			ReadWriteDao<UserPoolAccEntity> userPoolAccEntityDao = getDaoProvider().getUserPoolAccEntityDao();

			Filter userPoolAccFilter = new FilterImpl();
			userPoolAccFilter.addExpression(Restrictions.eq(UserPoolAccEntity.ATTR_USER_ID,
					user.getUserId()));
			List toRemovePoolList = userPoolAccEntityDao.find(userPoolAccFilter);

			for (Iterator iterator = toRemovePoolList.iterator(); iterator.hasNext();) {
				UserPoolAccEntity userPoolAccEntityToRemove = (UserPoolAccEntity) iterator.next();
				userPoolAccEntityDao.remove(userPoolAccEntityToRemove);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * @param poolIds
	 * @param userEntity
	 */
	private void addSelectedPools(List poolIds, UserEntity userEntity) {
		// [[
		Object[][] params = { { "poolIds", poolIds }, { "userEntity", userEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addSelectedPools", params);
		}
		// ]]
		try {
			//saving selected pools
			if (poolIds == null || poolIds.isEmpty()) {
				return;
			}
			ReadWriteDao<UserPoolAccEntity> userPoolAccEntityDao = getDaoProvider().getUserPoolAccEntityDao();
			for (Iterator iterator = poolIds.iterator(); iterator.hasNext();) {
				Long poolId = (Long) iterator.next();

				PoolEntity poolEntity = getDaoProvider().getPoolEntityDao().findById(poolId);

				UserPoolAccEntity userPoolAccEntity = new UserPoolAccEntity();
				userPoolAccEntity.setPool(poolEntity);
				userPoolAccEntity.setUser(userEntity);

				userPoolAccEntityDao.persist(userPoolAccEntity);

				LOG.debug("SAVING DONE, NEW ID IN  UserPoolAcc TABLE : "
						+ userPoolAccEntity.getUserPoolAccId());
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void removeAssignedSapVendors(UserEntity user) {
		// [[
		Object[][] params = { { "user", user } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeAssignedSapVendors", params);
		}
		// ]]
		try {
			ReadWriteDao<UserSapVendorAccEntity> userSapVendorAccEntityDao = getDaoProvider().getUserSapVendorAccEntityDao();

			Filter userSapVendorAccFilter = new FilterImpl();
			userSapVendorAccFilter.addExpression(Restrictions.eq(
					UserSapVendorAccEntity.ATTR_USER_ID, user.getUserId()));
			//for vendor users do not delete the restriction to itself
			if (user.getSapVendor() != null && user.getSapVendor().getSapVendorId() != null) {
				userSapVendorAccFilter.addExpression(Restrictions.neq(
						UserSapVendorAccEntity.ATTR_SAP_VENDOR_ID,
						user.getSapVendor().getSapVendorId()));
			}
			List toRemoveSapVendorList = userSapVendorAccEntityDao.find(userSapVendorAccFilter);

			for (Iterator iterator = toRemoveSapVendorList.iterator(); iterator.hasNext();) {
				UserSapVendorAccEntity userSapVendorAccEntityToRemove = (UserSapVendorAccEntity) iterator.next();
				userSapVendorAccEntityDao.remove(userSapVendorAccEntityToRemove);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * @param carrierIds
	 * @param userEntity
	 */
	private void addSelectedSapVendors(List sapVendorIds, UserEntity userEntity) {
		// [[
		Object[][] params = { { "sapVendorIds", sapVendorIds }, { "userEntity", userEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addSelectedSapVendors", params);
		}
		// ]]
		try {
			ReadWriteDao<UserSapVendorAccEntity> userSapVendorAccEntityDao = getDaoProvider().getUserSapVendorAccEntityDao();
			ReadWriteDao<SapVendorEntity> sapVendorEntityDao = getDaoProvider().getSapVendorEntityDao();

			Long sapVendorItselfSapVendorId = null;
			if (userEntity.getSapVendor() != null) {
				sapVendorItselfSapVendorId = userEntity.getSapVendor().getSapVendorId();
				if (sapVendorItselfSapVendorId != null) {
					//checking that restrition on itself is aleady stored
					Filter userSapVendorToAccFilter = new FilterImpl();
					userSapVendorToAccFilter.addExpression(Restrictions.eq(
							UserSapVendorAccEntity.ATTR_USER, userEntity));
					userSapVendorToAccFilter.addExpression(Restrictions.eq(
							UserSapVendorAccEntity.ATTR_SAP_VENDOR_ID, sapVendorItselfSapVendorId));

					UserSapVendorAccEntity vendorRestToItself = userSapVendorAccEntityDao.findSingle(userSapVendorToAccFilter);
					if (vendorRestToItself == null) {
						//storing rest. to itself
						SapVendorEntity vendorItself = sapVendorEntityDao.findById(sapVendorItselfSapVendorId);

						UserSapVendorAccEntity userSapVendorAccEntity = new UserSapVendorAccEntity();
						userSapVendorAccEntity.setSapVendor(vendorItself);
						userSapVendorAccEntity.setUser(userEntity);

						userSapVendorAccEntityDao.persist(userSapVendorAccEntity);
					}
				}
			}

			if (sapVendorIds == null || sapVendorIds.isEmpty()) {
				return;
			}
			for (Iterator iterator = sapVendorIds.iterator(); iterator.hasNext();) {
				Long sapVendorRestrictionSapId = (Long) iterator.next();

				if (!sapVendorRestrictionSapId.equals(sapVendorItselfSapVendorId)) {

					SapVendorEntity sapVendorEntity = getDaoProvider().getSapVendorEntityDao().findById(
							sapVendorRestrictionSapId);

					UserSapVendorAccEntity userSapVendorAccEntity = new UserSapVendorAccEntity();
					userSapVendorAccEntity.setSapVendor(sapVendorEntity);
					userSapVendorAccEntity.setUser(userEntity);

					userSapVendorAccEntityDao.persist(userSapVendorAccEntity);

					LOG.debug("SAVING DONE, NEW ID IN  UserSapVendorBpAcc TABLE : "
							+ userSapVendorAccEntity.getUserSapVendorAccId());
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void handleSoldToShipToRestricitons(List soldtoSapIds, List sapSoldShipSrchIdList,
			UserEntity userEntity) {
		// [[
		Object[][] params = { { "soldtoSapIds", soldtoSapIds },
				{ "sapSoldShipSrchIdList", sapSoldShipSrchIdList },
				{ "userEntity", userEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleSoldToShipToRestricitons", params);
		}
		// ]]
		try {

			//perform soldto restrictions to add / remove
			ReadWriteDao<UserSoldtoBpAccEntity> userSoldtoBpAccEntityDao = getDaoProvider().getUserSoldtoBpAccEntityDao();
			ReadWriteDao<UserShiptoAccEntity> userShiptoAccEntityDao = getDaoProvider().getUserShiptoAccEntityDao();
			ReadWriteDao<SapBpEntity> sapBpEntityDao = getDaoProvider().getSapBpEntityDao();
			Set storedUserRestrictionIds = new HashSet();
			Filter storedRestrictionsFilter = new FilterImpl();

			storedRestrictionsFilter.addExpression(Restrictions.eq(UserSoldtoBpAccEntity.ATTR_USER,
					userEntity));
			List storedUserSoldToBpAccList = userSoldtoBpAccEntityDao.find(storedRestrictionsFilter);

			for (Iterator iterator = storedUserSoldToBpAccList.iterator(); iterator.hasNext();) {
				UserSoldtoBpAccEntity storedRestriction = (UserSoldtoBpAccEntity) iterator.next();
				String restId = storedRestriction.getSoldtoSapBp().getSapId();
				storedUserRestrictionIds.add(restId);
			}

			Set selectedRestrictionIds = new HashSet();
			selectedRestrictionIds.addAll(soldtoSapIds);

			//to add = selected - stored
			Set restrictionIdsToAdd = new HashSet(selectedRestrictionIds);
			restrictionIdsToAdd.removeAll(storedUserRestrictionIds);

			//to delete = stored - selected
			Set soldtoRestrictionIdsToDelete = new HashSet(storedUserRestrictionIds);
			soldtoRestrictionIdsToDelete.removeAll(selectedRestrictionIds);

			//remove shipto restrictions

			Filter removeAccFilter = new FilterImpl();
			removeAccFilter.addExpression(Restrictions.eq(UserShiptoAccEntity.ATTR_USER_ID,
					userEntity.getUserId()));

			List shiptoRestrictionsToRemove = userShiptoAccEntityDao.find(removeAccFilter);

			if (shiptoRestrictionsToRemove != null) {
				for (Iterator iterator = shiptoRestrictionsToRemove.iterator(); iterator.hasNext();) {
					UserShiptoAccEntity restrictionToRemove = (UserShiptoAccEntity) iterator.next();
					userShiptoAccEntityDao.remove(restrictionToRemove);
				}
			}
			//remove soldto restrictions
			List soldtoRestrictionsToDelete = new ArrayList();

			if (!soldtoRestrictionIdsToDelete.isEmpty()) {

				Filter soldTosToDeleteInRestirctionFilter = new FilterImpl();
				soldTosToDeleteInRestirctionFilter.addExpression(Restrictions.in(
						SapBpEntity.ATTR_SAP_ID, new ArrayList(soldtoRestrictionIdsToDelete)));

				// Removing soldto restrictions should always be possible, regardless of the user's restrictions who performs it.
				Authorizer.disableNextAccessRestriction();
				List soldtosToDeleteInrestrictions = sapBpEntityDao.find(soldTosToDeleteInRestirctionFilter);

				if (soldtosToDeleteInrestrictions != null
						&& !soldtosToDeleteInrestrictions.isEmpty()) {
					Filter soldToRestrictionsToDeleteFilter = new FilterImpl();
					soldToRestrictionsToDeleteFilter.addExpression(Restrictions.eq(
							UserSoldtoBpAccEntity.ATTR_USER, userEntity));
					soldToRestrictionsToDeleteFilter.addExpression(Restrictions.in(
							UserSoldtoBpAccEntity.ATTR_SOLDTO_SAP_BP, soldtosToDeleteInrestrictions));
					// Removing soldto restrictions should always be possible, regardless of the user's restrictions who performs it.
					Authorizer.disableNextAccessRestriction();
					soldtoRestrictionsToDelete = userSoldtoBpAccEntityDao.find(soldToRestrictionsToDeleteFilter);
				}

			}

			for (Iterator iterator2 = soldtoRestrictionsToDelete.iterator(); iterator2.hasNext();) {
				UserSoldtoBpAccEntity soldtoRestrictionToRemove = (UserSoldtoBpAccEntity) iterator2.next();
				userSoldtoBpAccEntityDao.remove(soldtoRestrictionToRemove);
			}

			//add soldto restrictions
			List soldtoRestrictionsToAdd = new ArrayList();

			if (!restrictionIdsToAdd.isEmpty()) {
				Filter soldtosToAddfilter = new FilterImpl();
				soldtosToAddfilter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SOLDTO,
						Boolean.TRUE));
				soldtosToAddfilter.addExpression(Restrictions.in(SapBpEntity.ATTR_SAP_ID,
						new ArrayList(restrictionIdsToAdd)));

				soldtoRestrictionsToAdd = sapBpEntityDao.find(soldtosToAddfilter);
			}

			for (Iterator iterator = soldtoRestrictionsToAdd.iterator(); iterator.hasNext();) {
				SapBpEntity soldtoRestriction = (SapBpEntity) iterator.next();

				UserSoldtoBpAccEntity userSoldtoBpAccEntity = new UserSoldtoBpAccEntity();
				userSoldtoBpAccEntity.setSoldtoSapBp(soldtoRestriction);
				userSoldtoBpAccEntity.setUser(userEntity);

				userSoldtoBpAccEntityDao.persist(userSoldtoBpAccEntity);
			}

			// perform shipto restrictions to add			
			List<SoldshipFlexSrchBean> sapSoldShipSrchEntityList = null;
			if (sapSoldShipSrchIdList != null && !sapSoldShipSrchIdList.isEmpty()) {
				sapSoldShipSrchEntityList = getSoldtoshipFlexSrcByCompositeIdList(sapSoldShipSrchIdList);
			}
			
			List shiptoRestrictionsToAdd = new ArrayList();
			if (sapSoldShipSrchEntityList != null) {

				for (Iterator<SoldshipFlexSrchBean> iterator = sapSoldShipSrchEntityList.iterator(); iterator.hasNext();) {

					SoldshipFlexSrchBean sapSoldShipSrchEntity = iterator.next();

					Filter soldtoAccFilter = new FilterImpl();

					soldtoAccFilter.addExpression(Restrictions.eq(
							UserSoldtoBpAccEntity.ATTR_USER_ID, userEntity.getUserId()));
					soldtoAccFilter.addExpression(Restrictions.eq(
							UserSoldtoBpAccEntity.ATTR_SOLDTO_SAP_BP_ID,
							sapSoldShipSrchEntity.getSoldtoId()));
					UserSoldtoBpAccEntity userSoldtoBpAccEntity = userSoldtoBpAccEntityDao.findSingle(soldtoAccFilter);

					Filter accFilter = new FilterImpl();
					accFilter.addExpression(Restrictions.eq(UserShiptoAccEntity.ATTR_USER_ID,
							userEntity.getUserId()));
					accFilter.addExpression(Restrictions.eq(
							UserShiptoAccEntity.ATTR_SHIPTO_SAP_BP_ID,
							sapSoldShipSrchEntity.getShiptoId()));
					accFilter.addExpression(Restrictions.eq(
							UserShiptoAccEntity.ATTR_USER_SOLDTO_BP_ACC, userSoldtoBpAccEntity));

					UserShiptoAccEntity shipToRestriction = userShiptoAccEntityDao.findSingle(accFilter);

					if (shipToRestriction == null) {
						//add restriction
						shipToRestriction = new UserShiptoAccEntity();
						Filter shiptoFilter = new FilterImpl();
						shiptoFilter.addExpression(Restrictions.eq(SapBpEntity.ATTR_SAP_BP_ID,
								sapSoldShipSrchEntity.getShiptoId()));
						shiptoFilter.addExpression(Restrictions.eq(SapBpEntity.ATTR_BP_IS_SHIPTO,
								Boolean.TRUE));
						SapBpEntity shipto = sapBpEntityDao.findSingle(shiptoFilter);
						shipToRestriction.setShiptoSapBp(shipto);
						shipToRestriction.setUserSoldtoBpAcc(userSoldtoBpAccEntity);
						shipToRestriction.setUser(userEntity);

						shiptoRestrictionsToAdd.add(shipToRestriction);
					}
				}
			}

			//add shipto restrictions
			if (shiptoRestrictionsToAdd != null) {
				for (Iterator iterator = shiptoRestrictionsToAdd.iterator(); iterator.hasNext();) {
					UserShiptoAccEntity restrictionToAdd = (UserShiptoAccEntity) iterator.next();
					userShiptoAccEntityDao.persist(restrictionToAdd);
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void handlePlantShippingpointRestrictions(List plantIds,
			List plantShippingpointList, UserEntity userEntity) {
		// [[
		Object[][] params = { { "plantIds", plantIds },
				{ "plantShippingpointList", plantShippingpointList },
				{ "userEntity", userEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handlePlantShippingpointRestrictions", params);
		}
		// ]]
		try {

			ReadWriteDao<ShippingpointEntity> shippingpointEntityDao = getDaoProvider().getShippingpointEntityDao();
			ReadWriteDao<UserPlantAccEntity> userPlantAccEntityDao = getDaoProvider().getUserPlantAccEntityDao();
			ReadWriteDao<UserShippingpointAccEntity> userShippingpointAccEntityDao = getDaoProvider().getUserShippingpointAccEntityDao();

			Set storedUserRestrictionIds = new HashSet();
			Filter storedRestrictionsFilter = new FilterImpl();
			storedRestrictionsFilter.addExpression(Restrictions.eq(UserPlantAccEntity.ATTR_USER,
					userEntity));

			//performing plant restrictions to add / delete
			List storedUserPlantAccList = userPlantAccEntityDao.find(storedRestrictionsFilter);

			for (Iterator iterator = storedUserPlantAccList.iterator(); iterator.hasNext();) {
				UserPlantAccEntity storedRestriction = (UserPlantAccEntity) iterator.next();
				String restId = storedRestriction.getPlant().getPlantCd();
				storedUserRestrictionIds.add(restId);
			}

			Set selectedRestrictionIds = new HashSet();
			selectedRestrictionIds.addAll(plantIds);

			//to add = selected - stored
			Set plantRestrictionIdsToAdd = new HashSet(selectedRestrictionIds);
			plantRestrictionIdsToAdd.removeAll(storedUserRestrictionIds);

			//to delete = stored - selected
			Set restrictionIdsToDelete = new HashSet(storedUserRestrictionIds);
			restrictionIdsToDelete.removeAll(selectedRestrictionIds);

			//remove shippingpoint restrictions
			Filter removeShippingpointAccFilter = new FilterImpl();
			removeShippingpointAccFilter.addExpression(Restrictions.eq(
					UserShippingpointAccEntity.ATTR_USER_ID, userEntity.getUserId()));

			List shippingpointRestrictionsToRemove = userShippingpointAccEntityDao.find(removeShippingpointAccFilter);

			if (shippingpointRestrictionsToRemove != null) {
				for (Iterator iterator = shippingpointRestrictionsToRemove.iterator(); iterator.hasNext();) {
					UserShippingpointAccEntity restrictionToRemove = (UserShippingpointAccEntity) iterator.next();
					userShippingpointAccEntityDao.remove(restrictionToRemove);
				}
			}

			//remove plant restrictions
			if (!restrictionIdsToDelete.isEmpty()) {
				for (Iterator iterator = restrictionIdsToDelete.iterator(); iterator.hasNext();) {
					String plantCdToRemove = (String) iterator.next();

					PlantBsEntity plantBsEntity = referentialDataService.findBsEntity(PlantBsEntity.class, 
							plantCdToRemove);

					Filter plantAccToRemoveFilter = new FilterImpl();
					plantAccToRemoveFilter.addExpression(Restrictions.eq(
							UserPlantAccEntity.ATTR_USER, userEntity));
					plantAccToRemoveFilter.addExpression(Restrictions.eq(
							UserPlantAccEntity.ATTR_PLANT, plantBsEntity));
					UserPlantAccEntity plantRestrictionToRemove = userPlantAccEntityDao.findSingle(plantAccToRemoveFilter);

					//removing related shippingpoint restrictions

					Filter shippingpointAccToRemoveFilter = new FilterImpl();
					shippingpointAccToRemoveFilter.addExpression(Restrictions.eq(
							UserShippingpointAccEntity.ATTR_USER, userEntity));
					shippingpointAccToRemoveFilter.addExpression(Restrictions.eq(
							UserShippingpointAccEntity.ATTR_USER_PLANT_ACC,
							plantRestrictionToRemove));

					List shippingpointAccListToRemove = userShippingpointAccEntityDao.find(shippingpointAccToRemoveFilter);

					if (shippingpointAccListToRemove != null
							&& !shippingpointAccListToRemove.isEmpty()) {
						for (Iterator iterator2 = shippingpointAccListToRemove.iterator(); iterator2.hasNext();) {
							UserShippingpointAccEntity shippingpointRestrictionToRemove = (UserShippingpointAccEntity) iterator2.next();
							userShippingpointAccEntityDao.remove(shippingpointRestrictionToRemove);
						}
					}

					userPlantAccEntityDao.remove(plantRestrictionToRemove);
				}
			}

			//adding plant restrictions
			if (!plantRestrictionIdsToAdd.isEmpty()) {
				for (Iterator iterator = plantRestrictionIdsToAdd.iterator(); iterator.hasNext();) {

					String plantCdToAdd = (String) iterator.next();
					PlantBsEntity plantBsEntity = referentialDataService.findBsEntity(PlantBsEntity.class, 
							plantCdToAdd);

					UserPlantAccEntity userpPlantAccEntityToAdd = new UserPlantAccEntity();
					userpPlantAccEntityToAdd.setPlant(plantBsEntity);
					userpPlantAccEntityToAdd.setUser(userEntity);

					userPlantAccEntityDao.persist(userpPlantAccEntityToAdd);
				}
			}

			//performing shippingpoint restrictions to add 
			List shippingpointRestrictionsToAdd = new ArrayList();
			
			List<PlantShippingpointBean> plantShippingpointEntityList = null;
			if (plantShippingpointList != null && !plantShippingpointList.isEmpty()) {
				plantShippingpointEntityList = getPlantShippingpointByCompositeIdList(plantShippingpointList);
			}

			if (plantShippingpointEntityList != null) {

				for (Iterator<PlantShippingpointBean> iterator = plantShippingpointEntityList.iterator(); iterator.hasNext();) {

					PlantShippingpointBean plantShippingPointEntity = iterator.next();

					Filter plantAccFilter = new FilterImpl();

					plantAccFilter.addExpression(Restrictions.eq(UserPlantAccEntity.ATTR_USER_ID,
							userEntity.getUserId()));
					plantAccFilter.addExpression(Restrictions.eq(UserPlantAccEntity.ATTR_PLANT_CD,
							plantShippingPointEntity.getPlantCd()));
					UserPlantAccEntity userPlantAccEntity = userPlantAccEntityDao.findSingle(plantAccFilter);

					Filter accFilter = new FilterImpl();
					accFilter.addExpression(Restrictions.eq(
							UserShippingpointAccEntity.ATTR_USER_ID, userEntity.getUserId()));
					accFilter.addExpression(Restrictions.eq(
							UserShippingpointAccEntity.ATTR_SHIPPINGPOINT_ID,
							plantShippingPointEntity.getShippingpointId()));
					accFilter.addExpression(Restrictions.eq(
							UserShippingpointAccEntity.ATTR_USER_PLANT_ACC, userPlantAccEntity));

					UserShippingpointAccEntity shippingpointRestriction = userShippingpointAccEntityDao.findSingle(accFilter);

					if (shippingpointRestriction == null) {
						//add restriction
						shippingpointRestriction = new UserShippingpointAccEntity();
						ShippingpointEntity shippingpointEntity = shippingpointEntityDao.findById(plantShippingPointEntity.getShippingpointId());
						shippingpointRestriction.setShippingpoint(shippingpointEntity);
						shippingpointRestriction.setUserPlantAcc(userPlantAccEntity);
						shippingpointRestriction.setUser(userEntity);
						shippingpointRestrictionsToAdd.add(shippingpointRestriction);
					}
				}
			}

			//add shippingpoint restrictions			
			if (shippingpointRestrictionsToAdd != null) {
				for (Iterator iterator = shippingpointRestrictionsToAdd.iterator(); iterator.hasNext();) {
					UserShippingpointAccEntity shippingpointRestrictionToAdd = (UserShippingpointAccEntity) iterator.next();
					userShippingpointAccEntityDao.persist(shippingpointRestrictionToAdd);
				}
			}

		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void handleCompCodeSareaRestrictions(List companycodeIds, List compCodeSareaIdList,
			UserEntity userEntity) {
		// [[
		Object[][] params = { { "companycodeIds", companycodeIds },
				{ "compCodeSareaIdList", compCodeSareaIdList },
				{ "userEntity", userEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "handleCompCodeSareaRestrictions", params);
		}
		// ]]
		try {
			ReadWriteDao<SareaEntity> sareaDao = getDaoProvider().getSareaEntityDao();
			ReadWriteDao<UserCompanycodeAccEntity> userCompanycodeAccDao = getDaoProvider().getUserCompanycodeAccEntityDao();
			ReadWriteDao<UserSareaAccEntity> userSareaAccDao = getDaoProvider().getUserSareaAccEntityDao();

			Set storedUserRestrictionIds = new HashSet();
			Filter storedRestrictionsFilter = new FilterImpl();
			storedRestrictionsFilter.addExpression(Restrictions.eq(
					UserCompanycodeAccEntity.ATTR_USER, userEntity));

			//performing company code restrictions to add / delete
			List storedUserCompanycodeAccList = userCompanycodeAccDao.find(storedRestrictionsFilter);

			for (Iterator iterator = storedUserCompanycodeAccList.iterator(); iterator.hasNext();) {
				UserCompanycodeAccEntity storedRestriction = (UserCompanycodeAccEntity) iterator.next();
				String restId = storedRestriction.getCompanycode().getCompanycodeCd();
				storedUserRestrictionIds.add(restId);
			}

			Set selectedRestrictionIds = new HashSet();
			selectedRestrictionIds.addAll(companycodeIds);

			//to add = selected - stored
			Set companycodeRestrictionIdsToAdd = new HashSet(selectedRestrictionIds);
			companycodeRestrictionIdsToAdd.removeAll(storedUserRestrictionIds);

			//to delete = stored - selected
			Set restrictionIdsToDelete = new HashSet(storedUserRestrictionIds);
			restrictionIdsToDelete.removeAll(selectedRestrictionIds);

			//remove sales area restrictions
			Filter removeSareaAccFilter = new FilterImpl();
			removeSareaAccFilter.addExpression(Restrictions.eq(UserSareaAccEntity.ATTR_USER_ID,
					userEntity.getUserId()));

			List sareaRestrictionsToRemove = userSareaAccDao.find(removeSareaAccFilter);

			if (sareaRestrictionsToRemove != null) {
				for (Iterator iterator = sareaRestrictionsToRemove.iterator(); iterator.hasNext();) {
					UserSareaAccEntity restrictionToRemove = (UserSareaAccEntity) iterator.next();
					userSareaAccDao.remove(restrictionToRemove);
				}
			}

			//remove company code restrictions
			if (!restrictionIdsToDelete.isEmpty()) {
				for (Iterator iterator = restrictionIdsToDelete.iterator(); iterator.hasNext();) {
					String companycodeCdToRemove = (String) iterator.next();

					CompanycodeBsEntity companycodeBs = referentialDataService.findBsEntity(CompanycodeBsEntity.class, 
							companycodeCdToRemove);

					Filter companycodeAccToRemoveFilter = new FilterImpl();
					companycodeAccToRemoveFilter.addExpression(Restrictions.eq(
							UserCompanycodeAccEntity.ATTR_USER, userEntity));
					companycodeAccToRemoveFilter.addExpression(Restrictions.eq(
							UserCompanycodeAccEntity.ATTR_COMPANYCODE, companycodeBs));
					UserCompanycodeAccEntity companycodeRestrictionToRemove = userCompanycodeAccDao.findSingle(companycodeAccToRemoveFilter);

					//removing related sales area restrictions

					Filter sareaAccToRemoveFilter = new FilterImpl();
					sareaAccToRemoveFilter.addExpression(Restrictions.eq(
							UserSareaAccEntity.ATTR_USER, userEntity));
					sareaAccToRemoveFilter.addExpression(Restrictions.eq(
							UserSareaAccEntity.ATTR_USER_COMPANYCODE_ACC,
							companycodeRestrictionToRemove));

					List sareaAccListToRemove = userSareaAccDao.find(sareaAccToRemoveFilter);

					if (sareaAccListToRemove != null && !sareaAccListToRemove.isEmpty()) {
						for (Iterator iterator2 = sareaAccListToRemove.iterator(); iterator2.hasNext();) {
							UserSareaAccEntity sareaRestrictionToRemove = (UserSareaAccEntity) iterator2.next();
							userSareaAccDao.remove(sareaRestrictionToRemove);
						}
					}

					userCompanycodeAccDao.remove(companycodeRestrictionToRemove);
				}
			}

			//adding company code restrictions
			if (!companycodeRestrictionIdsToAdd.isEmpty()) {
				for (Iterator iterator = companycodeRestrictionIdsToAdd.iterator(); iterator.hasNext();) {
					String companycodeCdToAdd = (String) iterator.next();
					CompanycodeBsEntity companycodeBs = referentialDataService.findBsEntity(CompanycodeBsEntity.class, 
							companycodeCdToAdd);

					UserCompanycodeAccEntity userCompanycodeAccEntityToAdd = new UserCompanycodeAccEntity();
					userCompanycodeAccEntityToAdd.setCompanycode(companycodeBs);
					userCompanycodeAccEntityToAdd.setUser(userEntity);

					userCompanycodeAccDao.persist(userCompanycodeAccEntityToAdd);
				}
			}

			//performing sales area restrictions to add 		
			List<CompanycodeSareaBean> compCodeSareaEntityList = null;
			if (compCodeSareaIdList != null && !compCodeSareaIdList.isEmpty()) {
				compCodeSareaEntityList = getCompanycodeSareaByCompositeIdList(compCodeSareaIdList);
			}
			
			List sareaRestrictionsToAdd = new ArrayList();

			if (compCodeSareaEntityList != null) {
				for (Iterator<CompanycodeSareaBean> iterator = compCodeSareaEntityList.iterator(); iterator.hasNext();) {
					CompanycodeSareaBean compCodeSarea = iterator.next();

					Filter companycodeAccFilter = new FilterImpl();

					companycodeAccFilter.addExpression(Restrictions.eq(
							UserCompanycodeAccEntity.ATTR_USER_ID, userEntity.getUserId()));
					companycodeAccFilter.addExpression(Restrictions.eq(
							UserCompanycodeAccEntity.ATTR_COMPANYCODE_CD,
							compCodeSarea.getCompanycodeCd()));
					UserCompanycodeAccEntity userCompanycodeAcc = userCompanycodeAccDao.findSingle(companycodeAccFilter);

					Filter accFilter = new FilterImpl();
					accFilter.addExpression(Restrictions.eq(UserSareaAccEntity.ATTR_USER_ID,
							userEntity.getUserId()));
					accFilter.addExpression(Restrictions.eq(UserSareaAccEntity.ATTR_SAREA_ID,
							compCodeSarea.getSareaId()));
					accFilter.addExpression(Restrictions.eq(
							UserSareaAccEntity.ATTR_USER_COMPANYCODE_ACC, userCompanycodeAcc));

					UserSareaAccEntity sareaRestriction = userSareaAccDao.findSingle(accFilter);

					if (sareaRestriction == null) {
						//add restriction
						sareaRestriction = new UserSareaAccEntity();
						SareaEntity sarea = sareaDao.findById(compCodeSarea.getSareaId());
						sareaRestriction.setSarea(sarea);
						sareaRestriction.setUserCompanycodeAcc(userCompanycodeAcc);
						sareaRestriction.setUser(userEntity);
						sareaRestrictionsToAdd.add(sareaRestriction);
					}
				}
			}

			//add sales area restrictions			
			if (sareaRestrictionsToAdd != null) {
				for (Iterator iterator = sareaRestrictionsToAdd.iterator(); iterator.hasNext();) {
					UserSareaAccEntity sareaRestrictionToAdd = (UserSareaAccEntity) iterator.next();
					userSareaAccDao.persist(sareaRestrictionToAdd);
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void addSelectedSgroups(List SgroupIds, UserEntity userEntity) {
		// [[
		Object[][] params = { { "SgroupIds", SgroupIds }, { "userEntity", userEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addSelectedSgroups", params);
		}
		// ]]
		try {
			//saving selected Sgroups
			if (SgroupIds == null || SgroupIds.isEmpty()) {
				return;
			}
			ReadWriteDao<UserSgroupAccEntity> userSgroupAccEntityDao = getDaoProvider().getUserSgroupAccEntityDao();
			for (Iterator iterator = SgroupIds.iterator(); iterator.hasNext();) {
				String sgroupCd = (String) iterator.next();

				SgroupBsEntity sgroupBsEntity = referentialDataService.findBsEntity(SgroupBsEntity.class, sgroupCd);
				UserSgroupAccEntity userSgroupAccEntity = new UserSgroupAccEntity();
				userSgroupAccEntity.setSgroup(sgroupBsEntity);
				userSgroupAccEntity.setUser(userEntity);

				userSgroupAccEntityDao.persist(userSgroupAccEntity);

				LOG.debug("SAVING DONE, NEW ID IN  UserSgroupAcc TABLE : "
						+ userSgroupAccEntity.getUserSgroupAccId());
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void removeAssignedSgroups(UserEntity user) {
		// [[
		Object[][] params = { { "user", user } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeAssignedSgroups", params);
		}
		// ]]
		try {
			ReadWriteDao<UserSgroupAccEntity> userSgroupAccEntityDao = getDaoProvider().getUserSgroupAccEntityDao();

			Filter userSgroupAccFilter = new FilterImpl();
			userSgroupAccFilter.addExpression(Restrictions.eq(UserSgroupAccEntity.ATTR_USER_ID,
					user.getUserId()));
			List toRemoveSgroupList = userSgroupAccEntityDao.find(userSgroupAccFilter);

			for (Iterator iterator = toRemoveSgroupList.iterator(); iterator.hasNext();) {
				UserSgroupAccEntity userSgroupAccEntityToRemove = (UserSgroupAccEntity) iterator.next();
				userSgroupAccEntityDao.remove(userSgroupAccEntityToRemove);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void addSelectedPurchOrgIds(List purchOrgIds, UserEntity userEntity) {
		// [[
		Object[][] params = { { "compCodeIds", purchOrgIds }, { "userEntity", userEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addSelectedPurchOrgIds", params);
		}
		// ]]
		try {
			//saving selected Company codes
			if (purchOrgIds == null || purchOrgIds.isEmpty()) {
				return;
			}
			ReadWriteDao<UserPurchorgAccEntity> userPurchorgAccEntityDao = getDaoProvider().getUserPurchorgAccEntityDao();

			for (Iterator iterator = purchOrgIds.iterator(); iterator.hasNext();) {
				String purchOrgCd = (String) iterator.next();

				PurchasingorgBsEntity purchOrg = referentialDataService.findBsEntity(PurchasingorgBsEntity.class, 
						purchOrgCd);
				UserPurchorgAccEntity userPurchorgAccEntity = new UserPurchorgAccEntity();
				userPurchorgAccEntity.setPurchasingorg(purchOrg);
				userPurchorgAccEntity.setUser(userEntity);

				userPurchorgAccEntityDao.persist(userPurchorgAccEntity);

				LOG.debug("SAVING DONE, NEW ID IN  UserPurchorgAcc TABLE : "
						+ userPurchorgAccEntity.getUserPurchorgAccId());
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	private void removeAssignedPurchOrgs(UserEntity user) {
		// [[
		Object[][] params = { { "user", user } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "removeAssignedPurchOrgs", params);
		}
		// ]]
		try {
			ReadWriteDao<UserPurchorgAccEntity> userPurchorgAccEntityDao = getDaoProvider().getUserPurchorgAccEntityDao();

			Filter userPurchorgAccFilter = new FilterImpl();
			userPurchorgAccFilter.addExpression(Restrictions.eq(UserPurchorgAccEntity.ATTR_USER_ID,
					user.getUserId()));
			List toRemovePurchorgList = userPurchorgAccEntityDao.find(userPurchorgAccFilter);

			for (Iterator iterator = toRemovePurchorgList.iterator(); iterator.hasNext();) {
				UserPurchorgAccEntity userPurchOrgAccEntityToRemove = (UserPurchorgAccEntity) iterator.next();
				userPurchorgAccEntityDao.remove(userPurchOrgAccEntityToRemove);
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	/**
	 * @param roleIds
	 * @param userEntity
	 */
	private void addSelectedRoles(List roleIds, UserEntity userEntity) {
		// [[
		Object[][] params = { { "roleIds", roleIds }, { "userEntity", userEntity } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "addSelectedRoles", params);
		}
		// ]]
		try {
			//saving selected roles
			if (roleIds == null || roleIds.isEmpty()) {
				return;
			}
			ReadWriteDao<UserRoleEntity> dao = getDaoProvider().getUserRoleEntityDao();
			for (Iterator iterator = roleIds.iterator(); iterator.hasNext();) {
				Long roleId = (Long) iterator.next();
				RoleEntity role = getDaoProvider().getRoleEntityDao().findById(roleId);
				// TODO (istvan Dec 15, 2008): add exception handling here if role is null
				UserRoleEntity entity = new UserRoleEntity();
				entity.setUser(userEntity);
				entity.setRole(role);

				dao.persist(entity);
				if (LOG.isDebugEnabled()) {
					LOG.debug("SAVING DONE, NEW ID IN  Userrole TABLE : " + entity.getUserRoleId());
				}
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public UserEntity externalPasswordChange(String extLogin, String newExtLoginPwd,
			boolean isOneTimePwd) {
		// ### !!! DO NOT LOG PASSWORD !!! ###
		// [[
		UserEntity result = null;
		Object[][] params = { { "extLogin", extLogin }, { "newExtLoginPwd", "********" },
				{ "isOneTimePwd", isOneTimePwd + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "externalPasswordChange", params);
		}
		// ]]
		// ### !!! DO NOT LOG PASSWORD !!! ###
		try {
			if (StringUtils.empty(extLogin) || StringUtils.empty(newExtLoginPwd)) {
				String msg = "extLogin and/or newExtLoginPwd is null or empty";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}

			// fetch user
			UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			if (user == null) {
				String msg = "user not found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_NOT_FOUND);
			}

			// hash password if not hashed
			newExtLoginPwd = PasswordUtil.hashValueIfNotHashed(newExtLoginPwd);

			// set the new password
			user.setExtLoginPwd(newExtLoginPwd);

			//setting new expiration date
			if (isOneTimePwd) {
				user.setExtLoginPwdValidToDat(new Date());
			}
			else {
				ConfigBean cfg = configService.getConfigBean();
				int passwordLifeTime = cfg.getAppPolicyPasswordLifeTimeD();

				if (passwordLifeTime > 0) {
					Calendar cal = Calendar.getInstance();
					cal.add(Calendar.DATE, passwordLifeTime);
					user.setExtLoginPwdValidToDat(cal.getTime());
					// if the user is a customer/vendor user then the password never expires
					user = LogonUtil.setExtLoginPxdValidForCustomerVendor(user);
				}
				else {
					// valid forever
					user.setExtLoginPwdValidToDat(null);
				}
			}

			// update user
			user = getDaoProvider().getUserEntityDao().merge(user);

			// log external password change
			userActivityService.recordExLoginPwdChanged(user.getExtLogin());

			// external password history
			externalLoginPasswordHistoryService.createExtLoginPwdHistory(
					user.getExtLogin(), newExtLoginPwd);

			return result = user;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public Long getFreePdaUserNo(int start, int end) {
		// [[
		Long result = null;
		Object[][] params = { { "start", start + "" }, { "end", end + "" } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getFreePdaUserNo", params);
		}
		// ]]
		try {

			DirectJpaDao directJpaDao = getDaoProvider().getDirectJpaDao();
			//		List res = directJpaDao.execNative("select findFirstFreePDAUserNo(?1, ?2) from dual", new Object[] {start, end});
			List res = directJpaDao.findNative("select " + Constant.SCHEMA_USER_PREPEND + "findFirstFreePDAUserNo(?1, ?2) from dual",
					new Object[] { start, end });
			BigDecimal userNo = null;
			if (res != null) {
				userNo = (BigDecimal) res.get(0);
			}
			if (userNo != null) {
				result = userNo.longValue();
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

	public MimeTypeBsEntity fetchMimeTypeByMimeTypeName(String mimeTypeName) {
		// [[
		MimeTypeBsEntity result = null;
		Object[][] params = { { "mimeTypeName", mimeTypeName } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "fetchMimeTypeByMimeTypeName", params);
		}
		// ]]
		try {
			Filter mimeFilter = new FilterImpl();
			mimeFilter.addExpression(Restrictions.eq(MimeTypeBsEntity.ATTR_MIME_TYPE, mimeTypeName));
			mimeFilter.addExpression(Restrictions.eq(MimeTypeBsEntity.ATTR_CTL_ACT_CD, true));
			List mimeList = referentialDataService.findBsEntityFiltered(MimeTypeBsEntity.class, mimeFilter);
			if (mimeList == null || mimeList.isEmpty()) {
				throw new LogonException("Mime type not supported", Reason.RES_MIME_UNSUPPORTED);
			}
			return result = (MimeTypeBsEntity) mimeList.get(0);
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
	 * @param tableName
	 * @param user
	 */
	private void audit(Long userId, UserEntity user, Long actionKey) {
		// [[
		Object[][] params = { { "userId", userId }, { "user", user }, { "actionKey", actionKey } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "audit", params);
		}
		// ]]
		try {
			if (actionKey == null) {
				return;
			}
			AuditEntity auditEntity = new AuditEntity();
			AuditActionBsEntity auditAction = referentialDataService.findBsEntity(AuditActionBsEntity.class, actionKey);
			auditEntity.setAuditAction(auditAction);
			UserEntity userEntity = getDaoProvider().getUserEntityDao().findById(userId);
			auditEntity.setUser(userEntity);
			auditEntity.setChangeTs(new Date(System.currentTimeMillis()));
			auditEntity.setTargetEntryId(user.getUserId().toString());

			auditEntity.setTargetTableName("TLGNU_USER");
			getDaoProvider().getAuditEntityDao().persist(auditEntity);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public List<LangTxEntity> findLangTxEntityListDependingOnAdminSource(String source,
			Locale locale) {
		List languageTxList = new ArrayList();
		if (source.equals("self")) {
			List userLanguageList = referentialDataService.findLangTxEntityList();
			Iterator it = userLanguageList.iterator();
			while (it.hasNext()) {
				LangTxEntity type = (LangTxEntity) it.next();
				if (!type.getLangTxPK().getLang1Cd().equals(
						type.getLangTxPK().getLangBs().getLangCd())) {
					it.remove();
				}
			}
			languageTxList = userLanguageList;
		}
		else {
			languageTxList = referentialDataService.findLangTxEntityList(locale);
		}
		return languageTxList;
	}

	public String getEmailAddress(String extLogin) {

		Filter filter = new FilterImpl();
		filter.addExpression(Restrictions.eq(UserEntity.ATTR_EXT_LOGIN, extLogin));
		filter.addExpression(Restrictions.eq(UserEntity.ATTR_EXT_LOGIN_ACTIVE_FLAG, Boolean.TRUE));
		UserEntity user = getDaoProvider().getUserEntityDao().findSingle(filter);

		if (user != null) {

			return user.getPerson().getEmail();
		}

		return null;
	}

	/**
	 * Sends a new password to the external user.
	 * This function is called when the user clicks on the "Forgot password" link in the login screen.
	 * This function could not be used (intentionally!) to unlock a locked external login password!
	 * @param extLogin the external login name
	 */
	public void sendNewPassword(String extLogin) {

		UserEntity user = userService.fetchUserByExtLogin(extLogin, null);

		if ((user == null) || (user.getPerson().getEmail() == null)) {
			throw new LogonException("No user found", Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND);
		}

		if (user.getExtLoginPwdLockedTs() != null) {
			LOG.error("could not reset external password, it is locked! extLogin: "
					+ user.getExtLogin());
			throw new LogonException("No user found", Reason.RES_ERROR_USER_EXT_PWD_LOCKED);
		}

		String newPassword = PwGenerator.generateNewExternalPassword(getConfigBean());

		user = externalPasswordChange(user.getExtLogin(), PasswordUtil.createHash(newPassword),
				true);

		Locale locale = new Locale(user.getApplicationLang().getLangCd());

		String announcementSubject = pdfGeneratorService.formatText(
				"new_password1.email.subject", locale,
				new Object[] { getConfigBean().getAppSystemName() });
		String announcementText = pdfGeneratorService.formatText("new_password1.email.text",
				locale, new Object[] { user.getExtLogin(), getConfigBean().getAppSystemName() });
		String passwordSubject = pdfGeneratorService.formatText("new_password2.email.subject",
				locale, null);
		String passwordText = pdfGeneratorService.formatText("new_password2.email.text",
				locale, new Object[] { newPassword });
		
		final String[] to = new String[]{user.getPerson().getEmail()};
		
		emailService.send(
				to, null, null,
				announcementSubject, announcementText,
				null);
		
		emailService.send(
				to, null, null,
				passwordSubject, passwordText,
				null);
	}

    /**
     * If the username is valid and the last password reset request wasn't too recent,
     * this method causes a password reset token to be generated and e-mailed to the 
     * user's stored e-mail address.
     * @param extLogin A user's external login name
     */
	// Corresponding ticket is LOGON-5396
    public void requestPasswordResetToken(String extLogin)
    {
		// [[
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "requestPasswordResetToken", params);
		}
		// ]]
		try {
			LOG.info("Received password reset request for user {}", new Object[] {extLogin});
			/*
			 * Upon requesting a password reset, the user doesn't get any feedback if his
			 * request was successful, since that would allow an attacker to enumerate valid users
			 * (see LOGON-5396 and LOGON-4738).
			 * Therefore it is not necessary to return any error status or throw an exception in case of
			 * errors, we just log it. 
			 */
			if (StringUtils.empty(extLogin)) {
				LOG.info("Empty username received for reset password request, aborting.");
				return;
			}
			if (!isExistingUser(extLogin)) {
				LOG.info("The user '{}' does not exist, aborting password reset request.", new Object[] {extLogin});
				return;
			}
			if (lastPasswordResetRequestTooRecent(extLogin)) {
				LOG.info("The last password reset attempt for user '{}' was too recent; aborting.", new Object[] {extLogin});
				return;
			}
			if (usersExtPwdIsLocked(extLogin)) {
				LOG.error("could not reset external password for user '{}', it is locked. Aborting.", new Object[] {extLogin});
				return;
			}
			
			// At this point, all the preconditions for creating the reset token are satisfied.
			createAndEMailPasswordResetToken(extLogin);
			
			return;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
    }
 
	
	/**
	 * Checks if a user's external password is locked.
	 * @param extLogin the external login corresponding to a valid existing user
	 * @return true if the "external password locked" flag is set for the user, false otherwise.
	 */
	private boolean usersExtPwdIsLocked(String extLogin) 
	{
		// [[
		boolean result = false;
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "usersExtPwdIsLocked", params);
		}
		// ]]
		try {
			UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			// User must exist per contract of this method.
			if (user == null) {
				final String msg = "Called with invalid user - violating contract (extLogin '" + extLogin + "')";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			
			// Locked means that the locked timestamp is set to a nonnull value. 
			return result = (user.getExtLoginPwdLockedTs() != null);
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
	 * Creates a new password reset token in the database for the specified user and sends it to
	 * the e-mail address stored for the user. 
	 * @param extLogin the external login of an existing user
	 * @throws IllegalArgumentException if the login does not belong to an existing user, or if the user doesn't have an e-mail address set
	 */
	private void createAndEMailPasswordResetToken(String extLogin) {
		// [[
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "createAndEMailPasswordResetToken", params);
		}
		// ]]
		try {
			LOG.info("Creating token for user with external login '" + extLogin + "'.");
			// This token is secret; it should never be logged anywhere.
			final String token = PwGenerator.generatePasswordResetToken();
			
			// Check preconditions
			final UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			if (user == null) {
				final String msg = "The external login '" + extLogin + "' does not belong to a a valid user.";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			else if (user.getPerson() == null) {
				final String msg = "user.getPerson() is null for ext login '" + extLogin + "', aborting";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			else if (StringUtils.empty(user.getPerson().getEmail())) {
				final String msg = "User '" + extLogin + "' has null or empty e-mail address, aborting.";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}
			else if (user.getApplicationLang() == null || user.getApplicationLang().getLangCd() == null) {
				final String msg = "Cannot determine language code for user with ext login '" + extLogin + "'.";
				LOG.error(msg);
				throw new RuntimeException(msg);
			}
			
			// We know now: We have a valid user and a well defined e-mail address and language code.
			final String userEmail = user.getPerson().getEmail();
			
			LOG.info("Storing password reset token for user with external login '" + extLogin + "'.");
			String tokenHash = storePasswordResetToken(extLogin, token);
			LOG.info("Emailing password reset token for user with external login '" + extLogin + "'.");
			String encodedTokenData = PasswordUtil.createEncodedTokenData(tokenHash, token);
			emailPasswordResetToken(extLogin, userEmail, URLEncoder.encode(encodedTokenData, Constant.DEFAULT_ENCODING), user.getApplicationLang().getLangCd());
			
			return;
		} catch (UnsupportedEncodingException e) {
			LOG.error("could not encode URL", e);
			throw new LogonException("could not encode URL", e);
		} finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Sends an e-mail with the password reset instruction to the specified e-mail address.
	 * The external login is contained in the e-mail as well, since multiple users can share the
	 * same e-mail address.
	 * @param extLogin the external login name that will be contained in the e-mail 
	 * @param email the e-mail address to which the token is sent 
	 * @param token the password reset token (secret!)
	 * @param langCd the user's language code, used to determine the e-mail's language
	 */
	@AdnTrace(excludeArgument={"token"})
	private void emailPasswordResetToken(String extLogin, String email, String token, String langCd)
	{
		boolean logDebugEnabled = LOG.isDebugEnabled();
		final Locale locale = new Locale(langCd);
		final String tokenURL = resetURLFromToken(token);
		final String subject = pdfGeneratorService.formatText("password_reset_token.email.subject",
				locale, new Object[] { extLogin });
		final String body = pdfGeneratorService.formatText("password_reset_token.email.text",
				locale, new Object[] { extLogin, tokenURL });
		if (logDebugEnabled) {
			LOG.debug("Calling e-mail service; subject is '{}' and body is not logged because it contains the secret token.", new Object[] {subject});
		}

		final String[] to = new String[]{email};

		emailService.send(
				to, null, null,
				subject, body,
				null);

		return;
	}

	/**
	 * Returns the URL at which a token can be used to reset a user's password.
	 * The URL is taken from the config values.
	 * @param token a previously generated password reset token
	 * @return a URL of the form <code>http://...?token=xxxxxxx</code>
	 */
	private String resetURLFromToken(String token) {
		// ### !!! DO NOT LOG TOKEN !!! ###
		// [[
		Object[][] params = {{"token", "********"}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "resetURLFromToken", params);
		}
		// ]]
		// ### !!! DO NOT LOG TOKEN !!! ###
		try {
			final String resetURLBase = getConfigBean().getAppPolicyPasswordResetTokenUrl();
			if (logDebugEnabled) {
				LOG.debug("Retrieved reset token url '{}'", new Object[] {resetURLBase});
			}
			return resetURLBase + "?token=" + token;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting("--not logging result because it contains secret token--");
			}
		// ]]
		}
	}

	/**
	 * Persists the given password reset token for the given user.
	 * @param extLogin the external login of an existing user
	 * @param token the random token used to reset the password
	 */
	private String storePasswordResetToken(String extLogin, String token) {
		// ### !!! DO NOT LOG TOKEN !!! ###
		// [[
		Object[][] params = {{"extLogin", extLogin},
							{"token", "********"}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "storePasswordResetToken", params);
		}
		// ]]
		// ### !!! DO NOT LOG TOKEN !!! ###
		try {
			final UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			final String hashedToken = PasswordUtil.createHash(token);
			final Date now = new Date();
			final Date tokenExpiryTime = tokenExpiryTime(now);
			
			user.setExtPwdResettokenHash(hashedToken);
			user.setExtPwdLastResetAttemptTs(now);
			user.setExtPwdResettokenValidToTs(tokenExpiryTime);
			
			getDaoProvider().getUserEntityDao().merge(user);
			
			return hashedToken;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * @param tokenCreationTime the time at which a password reset token was created 
	 * @return the time at which the token expires
	 */
	private Date tokenExpiryTime(Date tokenCreationTime) 
	{
		// [[
		Date result = null;
		Object[][] params = {{"tokenCreationTime", tokenCreationTime}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "tokenExpiryTime", params);
		}
		// ]]
		try {
			final int tokenLifetimeS = getConfigBean().getAppPolicyPasswordTokenLifetimeS();
			final Calendar cal = Calendar.getInstance();
			cal.setTime(tokenCreationTime);
			cal.add(Calendar.SECOND, tokenLifetimeS);
			return result = cal.getTime();
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
	 * @param extLogin External login name of a user
	 * @return true if the last password reset request for the specified user was
	 * more recent than the number of seconds ago specified in app.policy.password.minimal_reset_interval_s
	 * seconds, false otherwise.
	 */
	private boolean lastPasswordResetRequestTooRecent(String extLogin) {
		// [[
		boolean result = false;
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "lastPasswordResetRequestTooRecent", params);
		}
		// ]]
		try {
			final int MIN_RESET_REQUEST_INTERVAL = getConfigBean().getAppPolicyPasswordMinResetIntervalS();
			final UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			if (user == null) {
				throw new IllegalArgumentException("No user with external login '" + extLogin + "' could be found.");
			}
			
			final Date lastResetTimestamp = user.getExtPwdLastResetAttemptTs();
			final Date now = new Date();
			
			// Special case: there has not yet been any reset request for this user, then
			// it's always possible to reset.
			if (lastResetTimestamp == null) {
				LOG.info("This is the first ever password reset request for user '{}'.", new Object[] {extLogin});
				return result = false;
			}
			
			long secondsSinceLastReset = (now.getTime() - lastResetTimestamp.getTime()) / MILLISECONDS_PER_SECOND;
			
			return result = secondsSinceLastReset < MIN_RESET_REQUEST_INTERVAL;
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
	 * Returns true if and only if the provided string is the external login of an existing user.
	 */
	private boolean isExistingUser(String extLogin) {
		// [[
		boolean result = false;
		Object[][] params = {{"extLogin", extLogin}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isExistingUser", params);
		}
		// ]]
		try {
			UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			return result = user != null;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}

	public void generateNewPasswordAndSendUnlockPasswordMail(String extLogin) {
		// [[
		Object[][] params = { { "extLogin", extLogin } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendUnlockPassword", params);
		}
		// ]]
		try {
			if (extLogin == null) {
				String msg = "extLogin is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}

			UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			if (user == null) {
				String msg = "external user not found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND);
			}

			String email = user.getPerson().getEmail();
			if (email == null) {
				String msg = "no email address found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_NO_EMAIL_FOUND);
			}

			Date now = new Date();
			String newPassword = PwGenerator.generateNewExternalPassword(getConfigBean());
			String resetToken = PwGenerator.generatePasswordResetToken();
			user.setExtLoginPwd(PasswordUtil.createHash(newPassword));
			user.setExtLoginPwdValidToDat(now);
			
			String tokenHash = PasswordUtil.createHash(resetToken);
			user.setExtPwdResettokenHash(tokenHash);
			user = getDaoProvider().getUserEntityDao().merge(user);

			Locale locale = new Locale(user.getApplicationLang().getLangCd());

			SimpleDateFormat sdfDate = new SimpleDateFormat("d MMM yyyy", locale);
			SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aaa", locale);

			StringBuilder link = new StringBuilder();
			link.append(getConfigBean().getAppPolicyPasswordRecoveryUrl());
			link.append("?extlogin=").append(URLEncoder.encode(extLogin, Constant.DEFAULT_ENCODING));
			
			String lock = PasswordUtil.createEncodedTokenData(tokenHash, resetToken);
			link.append("&lock=").append(URLEncoder.encode(lock, Constant.DEFAULT_ENCODING));
						
			String announcementSubject = pdfGeneratorService.formatText(
					"unlock_password.email.subject", locale,
					new Object[] { getConfigBean().getAppSystemName() });
			String announcementText = pdfGeneratorService.formatText(
					"unlock_password.email.text",
					locale,
					new Object[] { user.getExtLogin(), getConfigBean().getAppSystemName(),
							sdfDate.format(now), sdfTime.format(now), link.toString() });

			String passwordSubject = pdfGeneratorService.formatText(
					"new_password2.email.subject", locale, null);
			String passwordText = pdfGeneratorService.formatText("new_password2.email.text",
					locale, new Object[] { newPassword });


			final String[] to = new String[]{email};

			emailService.send(
					to, null, null,
					announcementSubject, announcementText,
					null);

			emailService.send(
					to, null, null,
					passwordSubject, passwordText,
					null);
		}
		catch (UnsupportedEncodingException e) {
			LOG.error("could not encode URL", e);
			throw new LogonException("could not encode URL", e);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public String unlockExtLogin(UnlockExtLoginBean bean) {
		// [[
		String result = null;
		Object[][] params = { { "bean", bean } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "unlockExtLogin", params);
		}
		// ]]
		try {
			// check input bean
			if (bean == null) {
				LOG.warn("Unlock external user password attempt failed due to invalid input parameters");
				return result = UnlockExtLoginBean.UNLOCK_FAILED;
			}
			else if (bean.getExtLogin() == null || bean.getLock() == null
					|| bean.getPassword() == null) {
				LOG.warn("Unlock external user password attempt failed due to invalid input parameters");
				return result = UnlockExtLoginBean.UNLOCK_FAILED;
			}

			// check external user exists and active
			UserEntity user = userService.fetchUserByExtLogin(bean.getExtLogin(), null);
			if (user == null) {
				LOG.warn("Unlock external user password attempt failed due to invalid input parameters");
				return result = UnlockExtLoginBean.UNLOCK_FAILED;
			}

			// check external login password is locked
			if (user.getExtLoginPwdLockedTs() == null) {
				if (user.getExtLogin() != null) {
					LOG.error("Unlock external user password attempt failed, extLogin is not locked: "
							+ user.getExtLogin());
					userActivityService.recordExtLoginPwdUnlockAttempt(
							user.getExtLogin(), false);
				}
				return result = UnlockExtLoginBean.UNLOCK_FAILED;
			}

			// generate hash for lock
			String lockHash = PasswordUtil.createHash(bean.getLock());

			// check input lock hash
			if (!lockHash.equals(user.getExtPwdResettokenHash())) {
				if (user.getExtLogin() != null) {
					LOG.error("Unlock external user password attempt failed due to incorrect lock id. extLogin: "
							+ user.getExtLogin());
					userActivityService.recordExtLoginPwdUnlockAttempt(
							user.getExtLogin(), false);
				}
				return result = UnlockExtLoginBean.UNLOCK_FAILED;
			}

			// get actual external login password of the user
			String pwd = user.getExtLoginPwd();
			if (pwd == null) {
				if (user.getExtLogin() != null) {
					LOG.error("Unlock external user password attempt failed due to incorrect password (null). extLogin: "
							+ user.getExtLogin());
					userActivityService.recordExtLoginPwdUnlockAttempt(
							user.getExtLogin(), false);
				}
				return result = UnlockExtLoginBean.UNLOCK_FAILED;
			}

			// check input password
			String inPwd = bean.getPassword();
			if (!PasswordUtil.validatePassword(inPwd, pwd)) {
				if (user.getExtLogin() != null) {
					LOG.error("Unlock external user password attempt failed due to incorrect password. extLogin: "
							+ user.getExtLogin());
					userActivityService.recordExtLoginPwdUnlockAttempt(
							user.getExtLogin(), false);
				}
				return result = UnlockExtLoginBean.UNLOCK_FAILED;
			}

			// unlock user
			user = unlockExtLogin(user);

			// send notification mail
			sendExternalLoginPasswordUnlockedMail(user);

			return result = UnlockExtLoginBean.UNLOCK_SUCCESSFUL;
		}
		catch (Exception e) {
			LOG.error("unlock failed due to an exception", e);
			return result = UnlockExtLoginBean.UNLOCK_FAILED;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private UserEntity unlockExtLogin(UserEntity user) {
		// [[
		UserEntity result = null;
		Object[][] params = { { "user", user } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "unlockExtLogin", params);
		}
		// ]]
		try {
			user.setExtLoginPwdLockedTs(null);
			user = getDaoProvider().getUserEntityDao().persist(user);
			LOG.info("unlock external password was successful. extLogin: " + user.getExtLogin());

			// create user activity log
			userActivityService.recordExtLoginPwdUnlockAttempt(
					user.getExtLogin(), true);

			return result = user;
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	private void sendExternalLoginPasswordUnlockedMail(final UserEntity user) {
		// [[
		Object[][] params = { { "user", user } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "sendExternalLoginPasswordUnlockedMail", params);
		}
		// ]]
		try {
			// send notification mail
			String email = user.getPerson().getEmail();
			if (email == null) {
				String msg = "no email address found, extLogin: " + user.getExtLogin();
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_NO_EMAIL_FOUND);
			}

			Locale locale = new Locale(user.getApplicationLang().getLangCd());

			Date now = new Date();
			SimpleDateFormat sdfDate = new SimpleDateFormat("d MMM yyyy", locale);
			SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm aaa", locale);

			String announcementSubject = pdfGeneratorService.formatText(
					"unlock_pwdsucc.email.subject", locale,
					new Object[] { getConfigBean().getAppSystemName() });
			String announcementText = pdfGeneratorService.formatText(
					"unlock_pwdsucc.email.text",
					locale,
					new Object[] { user.getExtLogin(), getConfigBean().getAppSystemName(),
							sdfDate.format(now), sdfTime.format(now) });


			final String[] to = new String[]{email};
			
			emailService.send(
					to, null, null,
					announcementSubject, announcementText,
					null);
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
			// ]]
		}
	}

	public Date getAllowedExtPwdChangeDate(final String extLogin) {
		// [[
		Date result = null;
		Object[][] params = { { "extLogin", extLogin } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "getAllowedExtPwdChangeDate", params);
		}
		// ]]
		try {
			if (extLogin == null) {
				String msg = "extLogin is null";
				LOG.error(msg);
				throw new IllegalArgumentException(msg);
			}

			UserEntity user = userService.fetchUserByExtLogin(extLogin, true);
			if (user == null) {
				String msg = "external user not found, extLogin: " + extLogin;
				LOG.error(msg);
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_USER_NOT_FOUND);
			}

			int minExtPwdLifetimeInMinutes = getConfigBean().getAppPolicyPasswordMinPasswordLifetimeMinutes();
			UserActivityLogEntity uale = userActivityService.fetchLastUserActivityLog(
					user.getUserId(), user.getUserId(), UserActivityServiceBean.EXT_PWD_CHANGED);

			if (uale == null) {
				return result = new Date(0);
			}
			else {
				Date lastChanged = uale.getTimestamp();
				return result = new Date(lastChanged.getTime()
						+ (minExtPwdLifetimeInMinutes * 60 * 1000));
			}
		}
		finally {
			// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
			// ]]
		}
	}

	public boolean isExtLoginPwdLocked(final String extLogin) {
		// [[
		boolean result = false;
		Object[][] params = { { "extLogin", extLogin } };
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "isExtLoginPwdLocked", params);
		}
		// ]]
		try {
			result = true;
			UserEntity user = userService.fetchUserByExtLogin(extLogin, null);
			if (user != null) {
				if (user.getExtLoginPwdLockedTs() == null) {
					result = false;
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
	
	/**
	 * Reset a user's password if the token matches the stored one.
	 * This service uses the token value to determine the user to which it belongs (it is unique) and
	 * then sets the user's external password to the provided one. The uniqueness of the token (or rather,
	 * its hash) is ensured by the DB constraint.  
	 * @param token the password reset token that the user received by e-mail
	 * @param newPassword the new external password to set for the user
	 */
	public void resetExtPasswordWithToken(String token, String newPassword)
	{
		// !!! ### DO NOT LOG TOKEN OR PASSWORD ### !!!
		// [[
		Object[][] params = {{"token", "********"},
							{"newPassword", "********"}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "resetExtPasswordWithToken", params);
		}
		// ]]
		// !!! ### DO NOT LOG TOKEN OR PASSWORD ### !!!
		try {
			LOG.info("Resetting external password given a token");
			if (token == null) {
				throw new IllegalArgumentException("Token must not be null");
			}
			if (newPassword == null) {
				throw new IllegalArgumentException("newPassword must not be null");
			}
			
			final UserEntity user = userService.fetchUserByExtPwdResetToken(token);
			if (user == null) {
				// No user found for token
				final String msg = "no user found with the specified token";
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_PWD_TOKEN_INVALID);
			}
			LOG.info("Determined that the token is for the user with ext login '{}'", new Object[] {user.getExtLogin()});
			
			if (tokenHasExpired(user)) 
			{
				final String msg = "the reset token for user '" + user.getExtLogin() + "' has expired";
				throw new LogonException(msg, Reason.RES_ERROR_USER_EXT_PWD_TOKEN_EXPIRED);
			}
			LOG.info("Token is valid until {}", new Object[] {user.getExtPwdResettokenValidToTs()});
			
			// All good :-)
			LOG.info("Resetting password to new value");
			final UserEntity updatedUser = externalPasswordChange(user.getExtLogin(), PasswordUtil.createHash(newPassword), false);
			final UserEntity resettedUser = clearExtPasswordResetToken(updatedUser);
			getDaoProvider().getUserEntityDao().merge(resettedUser);
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting();
			}
		// ]]
		}
	}

	/**
	 * Checks if the external password reset token for the user has expired
	 * @param user
	 * @return true if the current time is after the "valid to" time stored in the user,
	 * false otherwise
	 */
	private boolean tokenHasExpired(UserEntity user) 
	{
		// [[
		boolean result = false;
		Object[][] params = {{"user", user}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "tokenHasExpired", params);
		}
		// ]]
		try {
			final Date now = new Date();
			final Date expirationTs = user.getExtPwdResettokenValidToTs();
			// Safety first: if there is no date, we consider the token as invalid.
			// The date must be explicitly set and it must be in the future for the token to be valid.
			if (expirationTs == null || now.after(expirationTs)) 
			{
				// We warn because this should not happen. If a token is set, then a validity date must also be set.
				if (expirationTs == null) {
					LOG.warn("Token validity check for ext login '{}' required, but no validity date set", 
							new Object[] {user.getExtLogin()});
				}
				
				// Expired
				return result = true;
			}
			else {
				// There is an expiration time and it is after (or on) now.
				return result = false;
			}
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
	 * Returns an user entity object with the password reset token and related fields cleared.
	 * @param user the user object to change
	 * @return a new user entity object with the reset token fields
	 * (token hash, token validity, last reset request) cleared
	 */
	private UserEntity clearExtPasswordResetToken(UserEntity user) 
	{
		// !!! DO NOT LOG PASSWORD !!!
		// [[
		UserEntity result = null;
		Object[][] params = {{"user", user},
							{"newPassword", "********"}};
		boolean logDebugEnabled = LOG.isDebugEnabled();
		if (logDebugEnabled) {
			LOG.entering(this, "resetExtUserPassword", params);
		}
		// ]]
		// !!! DO NOT LOG PASSWORD !!!
		try {
			if (user == null) {
				throw new IllegalArgumentException("must not be null");
			}
			user.setExtPwdLastResetAttemptTs(null);
			user.setExtPwdResettokenHash(null);
			user.setExtPwdResettokenValidToTs(null);
			
			return result = user;
		}
		finally {
		// [[
			if (logDebugEnabled) {
				LOG.exiting(result);
			}
		// ]]
		}
	}
	
	public List<UserTypeViewEntity> getUserTypesToCreate(String langCd) {
		
		List<UserTypeViewEntity> userTypes = null;
		
		ReadOnlyDao<UserTypeViewEntity> dao = getDaoProvider().getUserTypeViewEntityDao();
		
		Filter<UserTypeViewEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(UserTypeViewEntity.ATTR_LANG_CD, langCd));
		
		userTypes = dao.find(filter);
		
		return userTypes;
	}
	
	public Map<Long, RoleEntity> getDefaultRoleForUserType(Long userTypeCd) {
		Map<Long, RoleEntity> defaultRoleMap = new LinkedHashMap<Long, RoleEntity>();
		ReadWriteDao<RoleEntity> dao = getDaoProvider().getRoleEntityDao();
		if (CUSTOMER_USER_TYPE_CD == userTypeCd) {
			for (Long roleId : defaultCustomerRoleIdList) {
				RoleEntity entity = dao.findById(roleId);
				defaultRoleMap.put(roleId, entity);
			}
		}
		else if (DRIVER_USER_TYPE_CD == userTypeCd) {
			for (Long roleId : defaultDriverRoleIdList) {
				RoleEntity entity = dao.findById(roleId);
				defaultRoleMap.put(roleId, entity);
			}
		}
		return defaultRoleMap;
	}
}