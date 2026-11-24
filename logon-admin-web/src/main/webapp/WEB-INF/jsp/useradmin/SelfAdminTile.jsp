<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SelfAdminTile.jsp,v $ --%>

<%@ include file="/WEB-INF/jsp/includeTLDs.jsp" %>
  
<script type="text/javascript" src="<crh-common:url value='/js/useradmin/DDView.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/UserAdminAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/extjshelper/executeUpdate.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/useradmin/useradmin-util.js'/>"></script>
<script type="text/javascript">
	executeUpdate.init('<fmt:message key="executeupdate.updatedText"/>', '<fmt:message key="executeupdate.exceptionTitle"/>', '<fmt:message key="executeupdate.updateButtonText"/>');
	useradmin.init('<fmt:message key="shippingpoint.text.exjsloading"/>', '<fmt:message key="executeupdate.question.optimisticlock"/>');
</script>
<div class="customer_max">
    <input type="hidden" name="inputAction" id="inputAction" value=""/>
    <!-- beginn Border-Table   -->
    <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
    <tr>
<jsp:include page="../SubTab10Tile.jsp"/>   

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.admin"/> / <fmt:message key="tabsubitem.10.1"/></th>
      </tr>
      <tr>
      <td>
		<div id="user-details-div" style="width:100%">
				<jsp:include page="SelfAdminDetail.jsp"></jsp:include>
		</div>
            </td>
      </tr>
</table>
<br>
  </td>
  <td width="10">&nbsp;</td>
  </tr>
  </table>
</div>

<tiles:insert page="PageSetupTile.jsp">
	<tiles:put name="focus" value="userDetailsPanel_phoneNr"/>
</tiles:insert>