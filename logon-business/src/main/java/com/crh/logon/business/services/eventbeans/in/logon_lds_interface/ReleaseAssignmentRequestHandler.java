
package com.crh.logon.business.services.eventbeans.in.logon_lds_interface;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.enterprise.inject.Alternative;
import javax.interceptor.Interceptors;

import ch.nevis.gdi.jpa.ReadOnlyDao;
import ch.nevis.gdi.jpa.filter.Filter;
import ch.nevis.gdi.jpa.filter.JPAQuery;
import ch.nevis.gdi.jpa.filter.Restrictions;
import ch.nevis.gdi.jpa.filter.SelectExpression;
import ch.nevis.jcan.log.AdnTrace;
import ch.nevis.jcan.log.JcanLogger;
import ch.nevis.jcan.log.JcanLoggerFactory;
import com.crh.logon.business.services.eventbeans.in.RequestHandlerLocalInterface;
import com.crh.logon.business.services.interceptor.UserInfoInterceptor;
import com.crh.logon.business.services.localinterfaces.DispoServiceLocalInterface;
import com.crh.logon.constants.Constant;
import com.crh.logon.persistence.entities.views.DispoAssignPartListEntity;
import com.holcim.logon.eventbeans.in.LogonEventRequest;
import com.holcim.logon.eventbeans.in.LogonEventResponse;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReleaseAssignmentRequest;
import com.holcim.logon.eventbeans.in.logon_lds_interface.ReleaseAssignmentResponse;

@Alternative
@Interceptors(UserInfoInterceptor.class)
@Stateless
public class ReleaseAssignmentRequestHandler extends LogonEventRequestHandler implements RequestHandlerLocalInterface {

	private static final JcanLogger LOG = JcanLoggerFactory.getLogger(ReleaseAssignmentRequestHandler.class);

	private static final String FIELD_LOGON_ORDER_NO = "logonOrderNo";

	@EJB
	private DispoServiceLocalInterface dispoService;

	@Override
	protected void validate(LogonEventRequest eventBean) {
		final ReleaseAssignmentRequest request = (ReleaseAssignmentRequest) eventBean;

		validateMandatory(FIELD_CLIENT_TYPE, request.getClientType());
		validateMandatory(FIELD_LOGON_ORDER_NO, request.getLogonOrderNo());
	}

	@Override
	@AdnTrace
	protected LogonEventResponse perform(LogonEventRequest eventBean) {

		final ReleaseAssignmentRequest request = (ReleaseAssignmentRequest) eventBean;
		final ReleaseAssignmentResponse response = createLogonOKEventResponse(ReleaseAssignmentResponse.class);

		LOG.debug("Releasing order to driver {}", request.getLogonOrderNo());
		final Long logonOrderNo = parse(request.getLogonOrderNo(), FIELD_LOGON_ORDER_NO, true);

		ReadOnlyDao<DispoAssignPartListEntity> dao = getDaoProvider().getDispoAssignPartListEntityDao();
		Filter<DispoAssignPartListEntity> filter = dao.createFilter();
		filter.addExpression(Restrictions.eq(DispoAssignPartListEntity.ATTR_ORDER_NUMBER, logonOrderNo));

		JPAQuery<DispoAssignPartListEntity> query = dao.createJPAQuery();
		query.setFilter(filter);
		query.setDistinct(true);
		query.addSelect(SelectExpression.simpleSelect(DispoAssignPartListEntity.ATTR_ASSIGNMENT_ID));
		Long assignmentId = (Long) dao.findSingle(query);

		if (assignmentId == null) {
			LOG.error("No assignment found with order number {}", logonOrderNo);
			throw newApplicationError("No assignment found with this order number");
		}

		dispoService.transmitToDriver(assignmentId, Constant.WEIGH_SYSTEM_SP_NONE,
				Constant.WEIGH_SYSTEM_LP_LOGON);

		return response;
	}

}
