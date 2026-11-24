<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: UserAdminAuthPopupClose.jsp,v $ --%>


<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	useradmin.submitWithActionToDiv('addRestriction', 'admin_UserAdminFormUserAdmin', {'restrictionType' : <c:out value="${admin_UserAdminAuthPopupForm.restrictionType}"/>})
	hidePopup();
</script>


