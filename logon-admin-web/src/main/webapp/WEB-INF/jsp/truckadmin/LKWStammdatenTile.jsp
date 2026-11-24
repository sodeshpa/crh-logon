<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: LKWStammdatenTile.jsp,v $ --%>
<%-- Does not use, but is involved in functionality:
	LKWStammdatenCarrierTile - this is what one sees for the first time (single search field with carrrier data) from here one reaches LKWStammDatenTile
	
	Does use these tiles:
	LKWStammdatenData1Tile - div called masterdata1
	LKWStammdatenData2Tile - div called masterdata2, contains the selection for pools
	LKWStammdatenData3Tile - div called masterdata3, contains the map
	LKWStammdatenTruckList - div called trucklist contains the truck list
	RFIDPopupTile - div called RFIFPopupForm.DIV_NAME
	LKWStammdatenTile includes:	
	_________________________________________________________________________________________________________________________
	|       						   	|					   	|					   			|						    |
	|   LKWStammdatenTruckList     		|LKWStammdatemData1Tile |  LKWStammdatenData2Tile 		|    LKWStammdatenData3Tile | 
	|          							|					   	|	  RFIDPopup                 |							|
	_________________________________________________________________________________________________________________________
	
	Uses besides: LKWStammdatenChangeFields.jsp to render it to changefields div. This div is going to enable/disable the fields according to the rules.
--%>
<%@page import="com.crh.logon.admin.web.actions.truckadmin.TruckAdminAction"%>
<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.persistence.entities.common.TruckEntity"%>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/AdminRFIDPopupAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript">
var forwards = {
		ENTRY: '<%=TruckAdminAction.FORWARD_ENTRY %>',
		ADJUST_FIELDS: '<%=TruckAdminAction.FORWARD_ADJUST_FIELDS %>',
		SELECT_CARRIER: '<%=TruckAdminAction.FORWARD_SELECT_CARRIER %>',
		SAVE_NEW: '<%=TruckAdminAction.FORWARD_SAVE_NEW %>',
		SAVE: '<%=TruckAdminAction.FORWARD_SAVE %>',
		RESET: '<%=TruckAdminAction.FORWARD_RESET %>',
		CREATE_TRUCK: '<%=TruckAdminAction.FORWARD_CREATE_TRUCK %>',
		SHOW_ONLY_ACTIVE_TRUCKS: '<%=TruckAdminAction.FORWARD_SHOW_ONLY_ACTIVE_TRUCKS %>'
};

var lengths = {		
		commentLength: '<%=TruckEntity.DBSIZE_TRUCK_COMMENT + ""%>'
};

var texts = {
		buttonsSave: '<fmt:message key="buttons.save"/>',
		exjsloading: '<fmt:message key="shippingpoint.text.exjsloading"/>',
		errorsCommentLong: '<fmt:message key="roadmasterdata.errors.commentlong"/>',
		mouseoverPooldetach: '<fmt:message key="truckadmin.mouseover.pooldetach"/>',
		commonConfirm: '<fmt:message key="confirm.trim.textarealength"/>',
		commentField: '<fmt:message key="roadmasterdata.fields.comment"/>'
};			
</script>
<script type="text/javascript" src="<crh-common:url value='/js/extjshelper/executeUpdate.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/truckadmin/LKWStammdatenTile.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/logon-gmap-common.js'/>"></script>
<script type="text/javascript"> 
	executeUpdate.init('<fmt:message key="executeupdate.updatedText"/>', '<fmt:message key="executeupdate.exceptionTitle"/>', '<fmt:message key="executeupdate.updateButtonText"/>');
	LKWStammdatenTile.init('<fmt:message key="executeupdate.question.optimisticlock"/>');
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
		<jsp:include page="LKWStammdatenDataTile.jsp"/>
	</div>

            <!-- END Submenu 2-2-1 -->
            </td>
      
      
      </tr>
</table>

</div>
