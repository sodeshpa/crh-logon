<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: UserAdminDetailTile.jsp,v $ --%>

<%@ include file="/WEB-INF/jsp/includeTLDs.jsp" %>
  
<script type="text/javascript" src="<crh-common:url value='/js/extjshelper/executeUpdate.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/useradmin/useradmin-util.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/useradmin/DDView.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/UserAdminAjaxService.js'/>"></script>
<script type="text/javascript">
	executeUpdate.init('<fmt:message key="executeupdate.updatedText"/>', '<fmt:message key="executeupdate.exceptionTitle"/>', '<fmt:message key="executeupdate.updateButtonText"/>');
	useradmin.init('<fmt:message key="shippingpoint.text.exjsloading"/>', '<fmt:message key="executeupdate.question.optimisticlock"/>');
</script>

<div class="customer_max" id="mainDiv">
    <table width="100%">
    <tr>
    <td>
		<div id="user-details-div" style="width:100%">
				<jsp:include page="UserAdminDetail.jsp"></jsp:include>
		</div>
</td>
</tr>
</table>
</div>
