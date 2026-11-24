<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SimpleVehicleStammdatenTile.jsp,v $ --%>
<%-- Does not use, but is involved in functionality:
	SimpleVehicleStammdatenCarrierTile - this is what one sees for the first time (single search field with carrrier data) from here one reaches SimpleVehicleStammdatenTile
	
	Does use these tiles:
	SimpleVehicleStammdatenData1Tile - div called masterdata1
	SimpleVehicleStammdatenData2Tile - div called masterdata2, contains the map
	SimpleVehicleStammdatenSimpleVehicleList - div called simpleVehiclelist contains the simpleVehicle list
	SimpleVehicleStammdatenTile includes:	
	_________________________________________________________________________________________________________________________
	|       						   	|					   	|					   			|						    |
	|   SimpleVehicleStammdatenSimpleVehicleList     		|SimpleVehicleStammdatemData1Tile |  SimpleVehicleStammdatenData2Tile  | 
	|          							|					   	|	                   |							|
	_________________________________________________________________________________________________________________________
	
	Uses besides: SimpleVehicleStammdatenChangeFields.jsp to render it to changefields div. This div is going to enable/disable the fields according to the rules.
--%>
<%@page import="com.crh.logon.admin.web.actions.simplevehicleadmin.SimpleVehicleAdminAction"%>
<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.admin.web.forms.simplevehicleadmin.SimpleVehicleAdminForm"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.SimpleVehicleEntity"%>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript">
var forwards = {
		ENTRY: '<%=SimpleVehicleAdminAction.FORWARD_ENTRY %>',
		ADJUST_FIELDS: '<%=SimpleVehicleAdminAction.FORWARD_ADJUST_FIELDS %>',
		SELECT_CARRIER: '<%=SimpleVehicleAdminAction.FORWARD_SELECT_CARRIER %>',
		SAVE_NEW: '<%=SimpleVehicleAdminAction.FORWARD_SAVE_NEW %>',
		SAVE: '<%=SimpleVehicleAdminAction.FORWARD_SAVE %>',
		RESET: '<%=SimpleVehicleAdminAction.FORWARD_RESET %>',
		CREATE_SIMPLE_VEHICLE: '<%=SimpleVehicleAdminAction.FORWARD_CREATE_SIMPLE_VEHICLE %>'
};

var lengths = {		
		commentLength: '<%=SimpleVehicleEntity.DBSIZE_VEHICLE_COMMENT + ""%>'
};

var texts = {
		buttonsSave: '<fmt:message key="buttons.save"/>',
		exjsloading: '<fmt:message key="shippingpoint.text.exjsloading"/>',
		errorsCommentLong: '<fmt:message key="roadmasterdata.errors.commentlong"/>',
		commonConfirm: '<fmt:message key="confirm.trim.textarealength"/>',
		commentField: '<fmt:message key="roadmasterdata.fields.comment"/>'
};			
</script>
<script type="text/javascript" src="<crh-common:url value='/js/extjshelper/executeUpdate.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/simplevehicleadmin/SimpleVehicleStammdatenTile.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/logon-gmap-common.js'/>"></script>
<script type="text/javascript"> 
	executeUpdate.init('<fmt:message key="executeupdate.updatedText"/>', '<fmt:message key="executeupdate.exceptionTitle"/>', '<fmt:message key="executeupdate.updateButtonText"/>');
	SimpleVehicleStammdatenTile.init('<fmt:message key="executeupdate.question.optimisticlock"/>');
</script>
                       
<div class="customer_max">

<jsp:include page="../SubTab9Tile.jsp"/>   

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/></th>
      </tr>
      <tr>
    
      <td>
            <!-- Beginn Submenu 2-2-1-1 -->
<jsp:include page="../Sub5Tab9Tile.jsp"/>   
   			  
       		  
       		  <div class="subform" style="border-top:0px;">
       		    
                
	<div id = "masterdata">
		<jsp:include page="SimpleVehicleStammdatenDataTile.jsp"/>
	</div>

            <!-- END Submenu 2-2-1 -->
            </td>
      
      
      </tr>
</table>

</div>
