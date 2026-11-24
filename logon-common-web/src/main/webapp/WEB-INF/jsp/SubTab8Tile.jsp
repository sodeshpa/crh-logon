				<%@ include file="includeTLDs.jsp" %>
				<%@page import="com.crh.logon.web.utils.Authorization"%>
				<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px;">
                  <tr>
                    <td style="border:0px; padding:8px 0px 0px 0px; margin:0px;width:100%;"> 
                      <div class="subnaviborder">
                          <ul id="subnavigation">

						  <li><a href="#" <c:if test="${ApplicationSubTabs1Bean == '2'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.8.1"/></span></span></a></li>
						  <li><a href="#" <c:if test="${ApplicationSubTabs1Bean == '3'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.8.2"/></span></span></a></li>
						  <li><a href="#" <c:if test="${ApplicationSubTabs1Bean == '4'}">class="highlight"</c:if>><span class="outer"><span class="inner"><fmt:message key="tabsubitem.8.3"/></span></span></a></li>
                          </ul>
                      </div> 
                    </td>
                  </tr>
      		</table>