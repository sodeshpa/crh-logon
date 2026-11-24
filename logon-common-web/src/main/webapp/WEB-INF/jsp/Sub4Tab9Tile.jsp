<%@ taglib uri="http://com.crh.logon/tags/common-taglib.tld" prefix="crh-common" %>
       		<%@ include file="includeTLDs.jsp" %>
       		<%@page import="com.crh.logon.web.utils.Authorization"%>
       		<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px; background-color:#FFFFFF;">
                  <tr>
                    <td style="border:0px; padding:0px; margin:0px; background-color:#FFFFFF;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation6">
							 	<crh-common:authorize function="<%= Authorization.MASTER_DATA_GLOBAL_ZONEMAP_BAG %>">
								  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
								  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'zonemap'}">class="highlight"</c:if>>
								  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.4.2"/></span></span></a></li>
								</crh-common:authorize>
								<crh-common:authorize function="<%= Authorization.MASTER_DATA_GLOBAL_SHIPPINGPOINT %>">
								  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
								  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'shippingpoint'}">class="highlight"</c:if>>
								  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.4.4"/></span></span></a></li>
								</crh-common:authorize>
							 	<crh-common:authorize function="<%= Authorization.MASTER_DATA_GLOBAL_MATERIAL %>">
								  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
								  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'material'}">class="highlight"</c:if>>
								  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.4.5"/></span></span></a></li>
								</crh-common:authorize>
								<crh-common:authorize function="<%= Authorization.MASTER_DATA_GLOBAL_PRODUCT_CHANGE_MATRIX %>">
								  <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
								  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'productchangematrixview'}">class="highlight"</c:if>>
								  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.9.4.7"/></span></span></a></li>
								</crh-common:authorize>
								<crh-common:authorize function="<%= Authorization.MASTER_DATA_GLOBAL_SALES_AREA %>">
								   <c:set var="url">/../<crh-common:authorizedUrl/></c:set>
								  <li><a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs2Bean == 'salesarea'}">class="highlight"</c:if>>								  
								  	<span class="outer"><span class="inner"><fmt:message key="tabsubitem.sales_area"/></span></span></a></li>
								</crh-common:authorize>
                          </ul> 
                    </div>                    </td>
                  </tr>
   			  </table>