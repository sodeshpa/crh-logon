<!-- Naming of tabs based on https://aww.adnovum.ch/ticketcenter/showattachment.cgi?attach_id=21045 -->
<%@ include file="includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page import="com.crh.logon.utils.LogonProperties"%>
				<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px;">
                  <tr>
                    <td style="border:0px; padding:8px 0px 0px 0px; margin:0px;width:100%;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation">
                          	  <c:set var="url">/../admin/useradmin/SelfAdminEntry.do</c:set>
	                            <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'selfadmin'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.10.1.4"/></span></span></a></li>
	                           <crh-common:authorize
	                           function="<%= Authorization.USER_ADMIN_LOGON %>"
	                           function2="<%= Authorization.USER_ADMIN_LOGON_EXTERN %>"
	                           function3="<%= Authorization.USER_ADMIN_PDA %>"
	                           >
								<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'useradmin'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.10.1"/></span></span></a></li>
								</crh-common:authorize>
								
	                          <c:set var="url">/../admin/useractivity/UserActivityEntry.do</c:set>
	                          <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'useractivity'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.10.5"/></span></span></a></li>
                              
<c:set var="zone"><%= ConfigBeanProvider.getConfigBean().getLogonProperties().getZone() %></c:set>
<c:set var="zone_int"><%= LogonProperties.ZONE_INT %></c:set>
<c:if test="${zone == zone_int}">
                              <crh-common:authorize function="<%= Authorization.TABLE_BROWSER %>">
	                              <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
	                              <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'tablebrowser'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.10.2"/></span></span></a></li>
                              </crh-common:authorize>
</c:if>
                              <%--TODO-Reactivate as impl ready
                              <crh-common:authorize function="<%= Authorization.USER_ADMIN_LOGON %>">
                              	<c:set var="url">/../admin</c:set>
                              	<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'pda'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.10.3"/></span></span></a></li>
                              </crh-common:authorize>
                              %-->
                              <%--TODO-Reactivate as impl ready
                              <crh-common:authorize function="<%= Authorization.USER_ADMIN_LOGON %>">
                              	<c:set var="url">/../admin</c:set>
                              	<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'systempartner'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.10.4"/></span></span></a></li>
                              </crh-common:authorize>
                              --%>
                          </ul>
                      </div> 
                    </td>
                  </tr>
      		</table>