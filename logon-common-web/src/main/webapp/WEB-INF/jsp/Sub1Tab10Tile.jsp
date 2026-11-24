<!-- Naming of tabs based on https://aww.adnovum.ch/ticketcenter/showattachment.cgi?attach_id=21045 -->
<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
       		<%@ include file="includeTLDs.jsp" %>
       		<%@page import="com.crh.logon.web.utils.Authorization"%>
       		<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px; background-color:#FFFFFF;">
                  <tr>
                    <td style="border:0px; padding:0px; margin:0px; background-color:#FFFFFF;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation6">
			                 <crh-common:authorize function="<%= Authorization.USER_ADMIN_LOGON %>">
				  				<c:set var="url">/../admin/useradmin/UserAdminEntry.do</c:set>
                                <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'useradminlogon'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.10.1.1"/></span></span></a></li>
                             </crh-common:authorize>
                             <crh-common:authorize function="<%= Authorization.USER_ADMIN_LOGON_EXTERN %>">
								<c:set var="url">/../admin/useradmin/UserAdminExternalEntry.do</c:set>
				                <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'useradminexternal'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.10.1.2"/></span></span></a></li>
				             </crh-common:authorize>
				            <crh-common:authorize function="<%= Authorization.USER_ADMIN_PDA %>">
							<c:set var="url">/../admin/pdaadmin/PDAAdminEntry.do</c:set>
			                    <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'pdaadmin'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.10.1.3"/></span></span></a></li>
			                    </crh-common:authorize>
                          </ul> 
                    </div>                    </td>
                  </tr>
   			  </table>