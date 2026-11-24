<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
       		<%@ include file="includeTLDs.jsp" %>
       		<%@page import="com.crh.logon.web.utils.Authorization"%>
       		<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px; background-color:#FFFFFF;">
                  <tr>
                    <td style="border:0px; padding:0px; margin:0px; background-color:#FFFFFF;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation6">
								<crh-common:authorize function="<%= Authorization.POOL_ASSIGNMENT %>">
								  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
								  <li><a href="<c:url value="${url}" />" <c:if test="${ApplicationSubTabs2Bean == 'pool_carrier'}">class="highlight"</c:if>>
								    <span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.2.2"/></span></span></a></li>
								</crh-common:authorize>
								<crh-common:authorize function="<%= Authorization.ORDER_VIEW_2%>">
									<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
								  <li><a href="<c:url value="${url}" />" <c:if test="${ApplicationSubTabs2Bean == 'creditnote'}">class="highlight"</c:if>>
								    <span class="outer"><span class="inner"><fmt:message key="tabsubitem.7.2.5"/></span></span></a></li>
								</crh-common:authorize>
                          </ul> 
                    </div>                    
                    </td>
                  </tr>
   			  </table>