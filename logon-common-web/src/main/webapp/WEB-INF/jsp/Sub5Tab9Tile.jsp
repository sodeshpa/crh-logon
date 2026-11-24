<!-- Naming of tabs based on https://aww.adnovum.ch/ticketcenter/showattachment.cgi?attach_id=21045 -->
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
       		<%@ include file="includeTLDs.jsp" %>
       		
<%@page import="com.crh.logon.web.utils.Authorization"%><table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px; background-color:#FFFFFF;">
                  <tr>
                    <td style="border:0px; padding:0px; margin:0px; background-color:#FFFFFF;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation6">                          	
							<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRUCK %>">
  							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'truck'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5.3"/></span></span></a></li>
                            </crh-common:authorize>
                            <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_POOL %>">
                          	<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
							  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'pool'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5.1"/></span></span></a></li>
							</crh-common:authorize>
                            <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRAILER %>">
			      			  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'trailer'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5.4"/></span></span></a></li>
                            </crh-common:authorize>
                            <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_CONTAINER %>">
			      			  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'container'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5.5"/></span></span></a></li>
                            </crh-common:authorize>
                            <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SIMPLE_VEHICLE %>">
			      			  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'simplevehicle'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5.10"/></span></span></a></li>
                            </crh-common:authorize>
                            <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_R %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'truckroute'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5.6"/></span></span></a></li>
                            </crh-common:authorize>
                            <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_SILO %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'silo'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5.7"/></span></span></a></li>
                            </crh-common:authorize>
                           	<crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_EXT_MAT_R %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'externalmaterial'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5.8"/></span></span></a></li>
                            </crh-common:authorize>
                            <crh-common:authorize function="<%= Authorization.MASTER_DATA_ROAD_EXT_SITE_R %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'externalsite'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5.9"/></span></span></a></li>
                            </crh-common:authorize>
                                                     
                          </ul> 
                    </div>                    </td>
                  </tr>
   			  </table>