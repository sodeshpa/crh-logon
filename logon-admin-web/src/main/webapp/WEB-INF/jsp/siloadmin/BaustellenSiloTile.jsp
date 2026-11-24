<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: BaustellenSiloTile.jsp,v $ --%>
<%--	
	Does use these tiles:
	SiloData1Tile - div called masterdata
	SiloData3Tile - div called masterdata3, contains the map
	RFIDPopupTile - div called RFIFPopupForm.DIV_NAME
	
	
	BaustellenSiloTile includes:	
	____________________________________________________________________
	|          |					 |									|
	|          |  	SiloData1Tile 	 |  		SiloData3Tile			|  
	|          |	RFIDPopupTile	 |									|
	____________________________________________________________________
--%>
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.admin.web.actions.siloadmin.SiloAdminAction"%>
<%@page import="com.crh.logon.admin.web.forms.siloadmin.SiloAdminForm"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ page session="true" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/AdminRFIDPopupAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/SiloAdminAjaxService.js'/>"></script>
<script type="text/javascript">
var forwards = {
		LOAD_SILO: '<%=SiloAdminAction.FORWARD_LOAD_SILO %>',
		REMOVE_SHIPTO: '<%=SiloAdminAction.FORWARD_REMOVE_SHIPTO %>',
		SAVE_NEW: '<%=SiloAdminAction.FORWARD_SAVE_NEW %>',
		SAVE: '<%=SiloAdminAction.FORWARD_SAVE %>',
		RESET: '<%=SiloAdminAction.FORWARD_RESET %>',
		CREATE_SILO: '<%=SiloAdminAction.FORWARD_CREATE_SILO %>',
		CHANGE_SHIPTO: '<%=SiloAdminAction.FORWARD_CHANGE_SHIPTO %>'
};

var texts = {
		buttonsSave: '<fmt:message key="buttons.save"/>',
		errorsCommentLong: '<fmt:message key="roadmasterdata.errors.commentlong"/>',
		shiptoId: '<fmt:message key="fields.shipto.id"/>',
		shiptoName: '<fmt:message key="fields.shipto.name"/>',
		shiptoOrt: '<fmt:message key="fields.shipto.ort"/>',
		shiptoPlz: '<fmt:message key="fields.shipto.plz"/>',
		exjsloading: '<fmt:message key="shippingpoint.text.exjsloading"/>'
};
var defaultMapCenter = 
{  
	lat : <%=Constant.MAP_CENTER_LAT%>,
	lng : <%=Constant.MAP_CENTER_LNG%>,
	zoomLevel : <%=Constant.MAP_CENTER_ZOOM_LEVEL%>
};
</script>
<script type="text/javascript" src="<crh-common:url value='/js/siloadmin/BaustellenSiloTile.js'/>"></script>

<div class="customer_max">
    <!-- beginn Border-Table   -->   
<jsp:include page="../SubTab9Tile.jsp"/>   

<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th height="21" colspan="4" class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/></th>
      </tr>
      <tr>
    
      <td>
            <!-- Beginn Submenu 2-2-1-1 -->
<jsp:include page="../Sub5Tab9Tile.jsp"/>   

              
              
              <html:form action="siloadmin/SiloAdmin.do" styleId="admin_SiloAdminForm">
    <input type="hidden" name="inputAction" id="inputAction" value=""/>
	<input type="hidden" name="siloId" id="siloId" value='<c:out value="${admin_SiloAdminForm.siloId}"/>'/>
	<input type="hidden" name="inputShiptoId" id="inputShiptoId" value=""/>
       		  
       		  <div class="subform" style="border-top:0px;">
                <c:if test = "${!empty admin_SiloAdminForm.silo}">
       		      <table class="classicblue" style="width:100%;">
              <colgroup>
              <col width="20%">
              <col width="60%">
              <col width="20%">
              </colgroup>
              <tr>
				<th height="21" colspan="4" class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/> / <fmt:message key="tabsubitem.9.5.7"/></th></th>              
              </tr>
              <tr>
                <td><p class="subtitle1"><fmt:message key="silomasterdata.labels.silo"/></p>
                    <!-- beginn Fahrzeug liste -->
                    <div class="subformlist" style="font-size:12px; margin-top:4px; height:400px;">
                      <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="width:100%;">
                        <colgroup>
                        <col width="99%">
                        <col width="1%">
                        </colgroup>
                        <tbody>
                        	<c:set var="zebracount" value="1"/>
	<c:forEach var="entry" items="${admin_SiloAdminForm.siloList}">
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
                              <tr  <c:if test="${entry.shiptoSiloId eq admin_SiloAdminForm.siloId }">class="selected"</c:if><c:if test="${entry.shiptoSiloId ne admin_SiloAdminForm.siloId }"> class="zebra<c:out value="${zebracount}" />" </c:if> >
                                <td><a href="#" onclick='selectSilo(<c:out value="${entry.shiptoSiloId}"/>)'><c:out value="${entry.siloName}"/></a></td>
                              </tr>
	</c:forEach>  
                        </tbody>
                      </table>
                    </div>
                    <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SILO_CREATE %>">
                  <div align="right">
                  <input onclick="javascript:addNewSilo();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> " alt="Silo">
                    </div>
                    </crh-common:authorize>
                  </td>
                <td>
                    <div>
                    <jsp:include page="SiloData1Tile.jsp"/>
                  </div>
                    </td>
                    
                    
                <td style="height:auto">                
                <p class="subtitle1"><fmt:message key="roadmasterdata.fields.lastknown"/></p>
                        <div id="masterdata3" name="masterdata3" class="subform">
                        <jsp:include page="SiloData3Tile.jsp"/>
                        </div>
                 </td>
              </tr>
              <tr>
                <td colspan="3"></td>
              </tr>
              <tr>
                <td colspan="3"><div align="right">
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SILO %>">
<%if (((SiloAdminForm)session.getAttribute("admin_SiloAdminForm")).getSilo() != null) {%>
       <input onclick="javascript:resetSilo()" type="button" class="ViewButtonStyle" style="margin-bottom:4px;" value="<fmt:message key="buttons.reset"/>">
                      &nbsp;&nbsp;
		<input id="saveButton" name="saveButton" onclick="javascript:saveSilo(false)" type="button" class="ViewButtonStyle"  style="margin-bottom:4px;" 
			value="<fmt:message key="buttons.save"/>">
<% }
else{%>
<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SILO_CREATE %>">
		<input onclick="javascript:resetSilo()" type="button" class="ViewButtonStyle" style="margin-bottom:4px;" value="<fmt:message key="buttons.reset"/>">
                      &nbsp;&nbsp;
		<input id="saveButton" name="saveButton" onclick="javascript:saveSilo(true)" type="button" class="ViewButtonStyle"  style="margin-bottom:4px;" 
			value="<fmt:message key="siloadmin.buttons.savenew"/>">
</crh-common:authorize>
<% }%>
</crh-common:authorize>
                  </div>
                    <br>                </td>
              </tr>
            </table>
				</c:if>
				
                <%----------------------------------------------------------------------------%>
                <%------------------------------------NO SILO IS PRESENT -------------------%>
                <%----------------------------------------------------------------------------%>
                				
                <c:if test = "${empty admin_SiloAdminForm.silo}">
                <table class="classicblue" style="width:100%;"><tr><td>
	                <fmt:message key="silomasterdata.info.nosilo"/>
	                <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SILO_CREATE %>">
                      <div align="right">
                        <input onclick="javascript:addNewSilo();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> ">
                        </div>
					</crh-common:authorize>
					</td>
					</tr>
					</table>    
                </c:if>

                        </div>
            </html:form>
            

            </td>
      
      
      </tr>
</table>
  <!-- End Border Table -->

