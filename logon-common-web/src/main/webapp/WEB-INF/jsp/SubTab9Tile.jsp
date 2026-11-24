				<%@ include file="includeTLDs.jsp" %>
				<%@page import="com.crh.logon.web.utils.Authorization"%>
				<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px;">
                  <tr>
                    <td style="border:0px; padding:8px 0px 0px 0px; margin:0px;width:100%;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation">
	                          <crh-common:authorize function="<%= Authorization.MASTER_DATA_SHIPTO_CARD_R %>">
	                          <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
	                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'dispokarte'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.1"/></span></span></a></li>
	                          </crh-common:authorize>
                              
	                          <crh-common:authorize function="<%= Authorization.MASTER_DATA_DISPOCARD_UPDATE_VIEW %>">
	                          <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
	                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'dpview'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.8"/></span></span></a></li>
	                          </crh-common:authorize>
                              
	                          <crh-common:authorize function="<%= Authorization.MASTER_DATA_CARRIER_VIEW %>">
	                          <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
	                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'commcarrier'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.9"/></span></span></a></li>
	                          </crh-common:authorize>
	                          
                              <crh-common:authorize function="<%= Authorization.MASTER_DATA_CARRIER_ASSIGNMT_SHIPTO_PROD %>">
	                          <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
							 <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'carrier'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.2"/></span></span></a></li>
							 </crh-common:authorize>
                              <crh-common:authorize function="<%= Authorization.MASTER_DATA_RFID_MAINTENANCE %>">
	                              <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
	                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'pda'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.3"/></span></span></a></li>
                              </crh-common:authorize>
                              <crh-common:authorize
                              function="<%= Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT %>"
                              function2="<%= Authorization.MASTER_DATA_GLOBAL_ZONEMAP_BAG %>"
                              function3="<%= Authorization.MASTER_DATA_GLOBAL_PRODUCT_CHANGE_MATRIX %>">
	                              <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
	                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'global'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.4"/></span></span></a></li>
                              </crh-common:authorize>
                              <crh-common:authorize
                              function="<%= Authorization.MASTER_DATA_ROAD_TRUCK %>"
                              function2="<%= Authorization.MASTER_DATA_ROAD_POOL %>"
                              function3="<%= Authorization.MASTER_DATA_ROAD_TRAILER %>"
                              function4="<%= Authorization.MASTER_DATA_ROAD_CONTAINER %>"
                              function5="<%= Authorization.MASTER_DATA_ROAD_CONTAINER %>"
                              function6="<%= Authorization.MASTER_DATA_ROAD_TRUCK_ROUTE_R %>"
                              function7="<%= Authorization.MASTER_DATA_ROAD_SILO %>"
                              >
                              <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'road'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.5"/></span></span></a></li>
                              </crh-common:authorize>

								<%--  switched 4 & 1 so the timetable is accessable  --%>
                              <crh-common:authorize
                              function="<%=Authorization.MASTER_DATA_RAIL_CARS %>"
                              function2="<%=Authorization.MASTER_DATA_RAIL_TIMETABLE%>"
                              function3="<%=Authorization.MASTER_DATA_RAIL_TIMETABLE_DISTANCE%>"
                              >
							  	<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              	<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'rail'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.6"/></span></span></a></li>
                              </crh-common:authorize>
                              <%--TODO-Reactivate as impl ready
                              <li><a href="" <c:if test="${ApplicationSubTabs1Bean == 'defaults'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.7"/></span></span></a></li>
                              --%>
                          </ul>
                      </div> 
                    </td>
                  </tr>
      		</table>