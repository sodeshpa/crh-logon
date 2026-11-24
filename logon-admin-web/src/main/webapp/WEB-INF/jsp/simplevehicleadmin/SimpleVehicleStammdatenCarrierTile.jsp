<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: SimpleVehicleStammdatenCarrierTile.jsp,v $ --%>
<%-- See details in main tile: SimpleVehicleStammdatenTile --%>

<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.admin.web.actions.simplevehicleadmin.SimpleVehicleAdminAction;"%>
                
                        
                   <table cellpadding="0" cellspacing="0" border="0" class="big_form2" style="padding:0px;">
                  	<colgroup>
                    <col width="20%">
                    <col width="80%">
                    </colgroup>
                          
                            <tr>
                              <td valign="top" style="border:0px;"><span class="labelblue"><fmt:message key="roadmasterdata.fields.carrierchoose"/></span><br>
                              
                                  <table class="noborder" cellpadding="0" cellspacing="0" border="0">
                                      <tr>
                                          <td valign="bottom" style="white-space:nowrap;border:0px; padding-right: 4px;"><p class="formtitle"><fmt:message key="roadmasterdata.fields.choose"/></p></td>
                                          <td style="white-space:nowrap;border:0px;"><span class="content" style="border:0px;">
					<html:select onchange="selectCarrier()" styleId="carrierId" property="carrierId" style="margin:0px; padding:0px; width:auto;">
					<c:if test="${admin_SimpleVehicleAdminForm.carrierId eq null}">
						<html:option value="">Select carrier...</html:option>	
					</c:if>										
						<c:forEach var="entry" items="${admin_SimpleVehicleAdminForm.carrierList}">
						<html:option value="${entry.carrierId}"><c:out value="${entry.displayName}"/></html:option>						
						</c:forEach>
					</html:select>
                                          </span>
                                          </td>
                                      </tr>
                                  </table>  
                                                            
                               </td>
                              <td style="border:0px;"><span class="labelblue"><fmt:message key="roadmasterdata.fields.address"/></span><br>
                              <c:out value="${admin_SimpleVehicleAdminForm.carrier.sapVendor.name1}"/><br>
                              <c:out value="${admin_SimpleVehicleAdminForm.carrier.sapVendor.street}"/> <c:out value="${admin_SimpleVehicleAdminForm.carrier.sapVendor.houseNumber}"/><br>
                              <c:out value="${admin_SimpleVehicleAdminForm.carrier.sapVendor.city}"/>
                              </td>
                            </tr>
                  </table>
