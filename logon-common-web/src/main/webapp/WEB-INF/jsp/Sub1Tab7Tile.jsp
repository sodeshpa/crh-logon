<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
       		<%@ include file="includeTLDs.jsp" %>
       		<%@page import="com.crh.logon.web.utils.Authorization"%>
       		<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px; background-color:#FFFFFF;">
                  <tr>
                    <td style="border:0px; padding:0px; margin:0px; background-color:#FFFFFF;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation6">
						  	<%-- FIXME which role is for Order Monitoring? --%>
							<crh-common:authorize function="<%= Authorization.ORDER_VIEW_3_MONITORING %>">
							  <c:set var="url">/../order/bestellung/BestellungMonitoringEntry.do</c:set>
  								<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'ordermonitoring'}">class="highlight"</c:if>>
  								<span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.1.1"/></span></span></a></li>
  							</crh-common:authorize>

							<crh-common:authorize function="<%= Authorization.RAIL_CAR_OPTIMIZATION %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
							  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'RailCarOptimization'}">class="highlight"</c:if>>
							  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.1.2"/></span></span></a></li>
						  	</crh-common:authorize>
						  							  		
							<crh-common:authorize function="<%= Authorization.LIVE_MAP %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
							  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'livekarte'}">class="highlight"</c:if>>
							  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.1.3"/></span></span></a></li>
						  	</crh-common:authorize>
						  	
							<crh-common:authorize function="<%= Authorization.BIMODAL_MONITORING %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
							  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'BimodalRailcarMonitoring'}">class="highlight"</c:if>>
							  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.1.4"/></span></span></a></li>
						  	</crh-common:authorize>
						  	
							<crh-common:authorize function="<%= Authorization.CONTAINER_MONITORING %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
							  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'ContainerMonitoring'}">class="highlight"</c:if>>
							  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.1.5"/></span></span></a></li>
						  	</crh-common:authorize>
                          </ul> 
                    </div>                    
                    </td>
                  </tr>
   			  </table>