				<%@ include file="includeTLDs.jsp" %>
				<%@page import="com.crh.logon.web.utils.Authorization"%>
				<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px;">
                  <tr>
                    <td style="border:0px; padding:8px 0px 0px 0px; margin:0px;width:100%;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation">
							<crh-common:authorize
							function="<%= Authorization.ORDER_VIEW_3_MONITORING %>"
							function2="<%= Authorization.RAIL_CAR_OPTIMIZATION %>"
							function3="<%= Authorization.LIVE_MAP %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
							  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'monitoring'}">class="highlight"</c:if>>
							  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.1"/></span></span></a></li>
							</crh-common:authorize>
						  		
							<%-- FIXME: Do not switch the order, until VIEW_1 s functionality does not have a page at all--%>
							<crh-common:authorize
							function="<%= Authorization.POOL_ASSIGNMENT %>"
							function2="<%= Authorization.ORDER_VIEW_2 %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
  								<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'orderviews'}">class="highlight"</c:if>>
  								<span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.2"/></span></span></a></li>
  							</crh-common:authorize>
						  		
							<%-- FIXME: Do not switch the order, until VIEW_1 s functionality does not have a page at all--%>
							<crh-common:authorize
							function="<%= Authorization.ORDER_VIEW_4 %>">
							  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
  								<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'truckplanning'}">class="highlight"</c:if>>
  								<span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.3"/></span></span></a></li>
  							</crh-common:authorize>
                          </ul>
                      </div> 
                    </td>
                  </tr>
      		</table>