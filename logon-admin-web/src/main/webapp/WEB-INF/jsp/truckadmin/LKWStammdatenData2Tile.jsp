<%@ include file="../includeTLDs.jsp" %>
<fmt:setBundle basename="LitDict"/>
<%@ taglib uri="http://com.crh.logon/tags/admin-taglib.tld" prefix="crh-admin" %>
<%@ page import="com.crh.logon.admin.web.utils.TruckAdminChangeLogic" %>
<%@ page import="com.crh.logon.admin.web.forms.truckadmin.TruckAdminForm"%>
<%@ page import="com.crh.logon.web.forms.RFIDPopupForm" %>
<%@page import="com.crh.logon.persistence.entities.common.TruckEntity"%>

<script>
							var trailerData = new Array();
							
							<c:forEach var="entry" items="${admin_TruckAdminForm.trailerList}"> 
							trailerData[<c:out value="${entry.trailerId}" />]=
								{
									maxGross: "<c:out value="${entry.maxGrossWeightT}" />",
									tara:"<c:out value="${entry.tareWeightT}" />",
									chamber1:"<c:out value="${entry.maxNetWeightChamber1T}" />",
									chamber2:"<c:out value="${entry.maxNetWeightChamber2T}" />",
									nrChambers: "<c:out value="${entry.nrChambers}" />"
								};

											
                             </c:forEach>
									                                


</script>
<%-- See details in main tile: LKWStammdatenTile --%>
<%pageContext.setAttribute("admin_TruckAdminForm", ((TruckAdminForm) session.getAttribute("admin_TruckAdminForm")));%>
                              <%TruckAdminForm form = (TruckAdminForm) session.getAttribute("admin_TruckAdminForm");
pageContext.setAttribute("trailerList", form.getTrailerList());
pageContext.setAttribute("containerList", form.getContainerList());
pageContext.setAttribute("driverList", form.getDriverList());
%>

                          <table cellpadding="0" cellspacing="0" class="noborder" style="width:100%;">
                          
                            <colgroup>
                            <col width="40%">
                            <col width="20%">
                            <col width="40%">
                            </colgroup>
<%--  Start of left pool panel --%>
                            <tr>
                              <td  class="label" style="white-space:nowrap;border:0px;"><p class="label"><fmt:message key="roadmasterdata.fields.pool"/></p>
                                  <select name="selectPool" multiple id="selectPool"  size="5">
							<c:forEach var="entry" items="${admin_TruckAdminForm.poolList}">                                 
                                    <option id='<c:out value="${entry.poolTxPK.poolId}" />' value = '<c:out value="${entry.name}" />'><c:out value="${entry.name}" /></option>
                             </c:forEach>
                                  </select>
                                  <div align="right" style="white-space:nowrap">
                                    <input onclick='javascript:deSelectAll("selectPool")' type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.keine"/>">
                                    &nbsp;&nbsp;
                                    <input onclick='javascript:selectAll("selectPool")' type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.all"/>">
                                </div>
                                </td>
                              <td valign="middle" style="vertical-align:middle; text-align:center;" >
                              <a onclick="javascript:moveSelectedRight()"><img src="<crh-common:url value='/images/arrow_right.gif'/>" width="16" height="16" border="0"  align="middle"><br><b><fmt:message key="buttons.add"/></b></a></td>
<%--  Start of right pool panel --%>                                    
                              <td class="label" style="white-space:nowrap;border:0px;"><fmt:message key="roadmasterdata.fields.assignedpools"/><br>
                                  <!-- beginn Fahrzeugzuordnung -->
                                  <div class="subformlist" style="font-size:12px; margin-top:4px;">
                                    <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="width:100%;">
                                      <colgroup>
                                      <col width="99%">
                                      <col width="1%">
                                      </colgroup>
                                      <tbody id="selectedPoolsTbody">
	<c:set var="zebracount" value="1"/>
	<c:forEach var="entry" items="${admin_TruckAdminForm.selectedPools}">
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
                              <tr class="zebra<c:out value="${zebracount}" />"><td><input type="hidden" id="<c:out value="${entry.poolTxPK.poolId}" />" name="<c:out value="${entry.name}" />"/><c:out value="${entry.name}" /></td>
                                          <td valign="middle"><a onclick="javascript:moveBackPool(this.parentNode.parentNode.rowIndex, '<c:out value="${entry.poolTxPK.poolId}" />','<c:out value="${entry.name}" />')"><img src="<crh-common:url value='/images/delete2.png'/>" alt="<fmt:message key="truckadmin.mouseover.pooldetach"/>" width="16" height="16" border="0"></a></td>
                              </tr>
		</c:forEach>                                     
                                      </tbody>
                                    </table>
                                  </div>
                                <div align="right">
                                    <input  onclick='javascript:resetPoolSelection()'  type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.all.delete"/>">
                                  </div>
                                <br>
                              </td>
                            </tr>
                          </table>
                          <div id="assignments">
                          <!--  <%-- Struts needs this, fond no better way to do it --%> <html:form action="truckadmin/TruckAdmin.do" styleId="admin_TruckAdminForm">-->
<table class="noborder" cellpadding="0" cellspacing="0" border="0" style="margin-top:4px; width:100%">
                            <colgroup>
                            <col width="1%">
                            <col width="99%">
                            </colgroup>
                            <tr>
                              <td valign="top" style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;"><span class="label"><fmt:message key="roadmasterdata.fields.auflieger"/></span></td>
                              <td>
					<html:select onchange="javascript:selectTrailer(this)" property="inputTrailerSelect"  styleId="inputTrailerSelect" style="width:100%;" >
						<option value=""><fmt:message key="option.select"/></option>
						<html:options collection="trailerList" property="trailerId" labelProperty="trailerName"/>
					</html:select>					
<script type="text/javascript">
var trailer = document.getElementById("inputTrailerSelect");
trailer.disabled=false;
<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_TRAILER%>">trailer.disabled=true;</crh-admin:disableTruckChange>
</script>
                             </td>
                            </tr>
                            <tr>
                              <td valign="top" style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;"><span class="label"><fmt:message key="roadmasterdata.fields.container"/></span></td>
                              <td>

					<html:select property="inputContainerSelect"  styleId="inputContainerSelect" style="width:100%;" >
						<option value=""><fmt:message key="option.select"/></option>
						<html:options collection="containerList" property="containerId" labelProperty="containerName"/>
					</html:select>	
<script type="text/javascript">					
	var container = document.getElementById("inputContainerSelect");
	container.disabled=false;
	<crh-admin:disableTruckChange formKey="admin_TruckAdminForm" functionality="<%=TruckAdminChangeLogic.FUNCTIONALITY_CHANGE_CONTAINER%>">container.disabled=true;</crh-admin:disableTruckChange>
</script>                              
                		      </td>
                            </tr>
                            <tr>
                              <td valign="top" style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;"><span class="label"><fmt:message key="roadmasterdata.fields.driver"/></span></td>
                              <td>
					<html:select property="inputDriverSelect"  styleId="inputDriverSelect" style="width:100%;" >
						<option value="-1"><fmt:message key="option.select"/></option>
						<c:forEach var="driver" varStatus="driverListStatus" items="${admin_TruckAdminForm.driverList}">
							<html:option value="${driverListStatus.count - 1}"><c:out value="${driver.name}"/></html:option>
						</c:forEach>
					</html:select>
                             </td>
                            </tr>
                          </table> 
<!--</html:form>-->              
                          </div>
                          <div style="margin-top:5px; padding-left:4px;">
                                <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="margin-top:4px; width:100%">
                                      <colgroup>
                                            <col width="10%">
                                            <col width="90%">
                                      </colgroup>
                                        <tr>                                          
                                            <td colspan="2">
                                                <span class="label">
                                                    <fmt:message key="roadmasterdata.fields.curDriverMobileNo"/>: 
                                                    <span class="formtitle">
                                                        <c:out value="${admin_TruckAdminForm.selectedDriverMobileNr}"/> 
                                                    </span> 
                                                </span>                                 
                                            </td>
                                        </tr>                                                                                
                                        <tr>
                                            <td>
                                                <input class="checkbox" name="inputSmsFlag"  id="inputSmsFlag" type="checkbox" value="true" <c:if test="${admin_TruckAdminForm.inputSmsFlag}" >checked=true</c:if> style="padding:0px; margin:0px;">                              
                                            </td>
                                            <td>
                                                <span class="formtitle">
                                                    <fmt:message key="roadmasterdata.fields.smsFlag"/>:
                                                </span>                                 
                                            </td>
                                        </tr>                                                                                
                                </table>
                          </div>
                          <br>
                          <!-- beginn special title with button aligned to the right -->
                          <div style="margin-bottom:0px; padding-left:4px;">
                            <div style="float:left; width:auto; padding:0px; margin:0px; vertical-align:bottom;"><span class="label"><fmt:message key="roadmasterdata.fields.rfid"/></span> </div>
                            <div style="float:right; right:0px;text-align:right; width:auto; padding:0px;">
                              <table border="0" cellspacing="0" cellpadding="0" class="noborder">
                                <tr>
                                  <td  onclick="renderTypeSelect()" style="padding:0px; margin:0px;">
                                  <input class="checkbox" <c:if test="${admin_TruckAdminForm.inputPdaFlagDisabled}">disabled</c:if> name="inputPdaFlag"  id="inputPdaFlag" type="checkbox" value="true" <c:if test="${admin_TruckAdminForm.inputPdaFlag}" >checked=true</c:if> style="padding:0px; margin:0px;"></td>
                                  <td style="white-space:nowrap;" class="formtitle"><fmt:message key="roadmasterdata.fields.pdaflag"/></td>
                                </tr>
                              </table>
                            </div>
                          </div>
                          <!-- beginn RFID -->
                          <div class="subformlist" id="<%=RFIDPopupForm.NAME_OF_HTML_DIV_TO_RELOAD%>" style="clear:both;font-size:12px; margin:0px 4px 0px 4px;">
<jsp:include page="../AdminRfidsTile.jsp"/>    
                          </div>
                          <div align="right">
                            <input id="addRFID" onclick="javascript:updatePlateNoShowRFIDPopup();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> ">
                          </div>
                          <!-- End RFID -->
                          <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="margin-top:4px; width:100%"><tr><td>
                          <div style="width:100%;"> <span class="label"><fmt:message key="roadmasterdata.fields.comment"/></span><br>
                              <textarea id="inputComment" name="inputComment" rows="5" class="textfield" onkeypress="return checkMaxLength(event, this, <%=TruckEntity.DBSIZE_TRUCK_COMMENT + ""%>)" cols="1" style="width: 100%;"><c:out value="${admin_TruckAdminForm.inputComment}" /></textarea>
                          </div>
                              </td></tr></table>

                        