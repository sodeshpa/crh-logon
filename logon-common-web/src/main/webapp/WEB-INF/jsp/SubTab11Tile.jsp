				<!-- Naming of tabs based on https://aww.adnovum.ch/ticketcenter/showattachment.cgi?attach_id=21045 -->
				<%@ include file="includeTLDs.jsp" %>
				<%@page import="com.crh.logon.web.utils.Authorization"%>
				<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px;">
                  <tr>
                    <td style="border:0px; padding:8px 0px 0px 0px; margin:0px;width:100%;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation">
                          
                          <crh-common:authorize function="<%= Authorization.TEST_WEBSERVICE %>">
							<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
  							<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'testwebservice'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.11.1"/></span></span></a></li>
						  </crh-common:authorize>
                          <crh-common:authorize function="<%= Authorization.TEST_PDA %>">
							<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
  							<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'testpda'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.11.2"/></span></span></a></li>
						  </crh-common:authorize>
						  
						  <%--crh-common:authorize function="<%= Authorization.TEST_VERSIONCHECK %>">
							<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
  							<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'tablebrowser'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.11.3"/></span></span></a></li>
						  </crh-common:authorize--%>
						  
					<%--	  <li><a href="http://localhost:8080/logon/admin/test/CronJobsEntry.do" <c:if test="${ApplicationSubTabs1Bean == 'cronjobs'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.11.4"/></span></span></a></li> --%>
						  
						  <crh-common:authorize function="<%= Authorization.TEST_CRONJOBS %>">
							<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
  							<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'cronjobs'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.11.4"/></span></span></a></li>
						  </crh-common:authorize>
						                            
						  <crh-common:authorize function="<%= Authorization.TEST_FAX_SMS %>">
							<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
  							<li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'testfaxsms'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.11.5"/></span></span></a></li>
						  </crh-common:authorize>
						  
                          </ul>
                      </div> 
                    </td>
                  </tr>
      		</table>