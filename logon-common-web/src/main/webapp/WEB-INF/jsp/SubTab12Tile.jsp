<%@ include file="includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.Authorization" %>
<table cellpadding="0" cellspacing="0" class="compact" style="border:0px; width:100%; margin:0px; padding:0px;">
	<tr>
		<td style="border:0px; padding:8px 0px 0px 0px; margin:0px;width:100%;">
			<div class="subnaviborder">
				<ul id="subnavigation">

					<crh-common:authorize function="<%= Authorization.DEMAND_PLANNING %>">
						<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
						<li>
							<a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'manageplanning'}">class="highlight"</c:if>>
								<span class="outer"><span class="inner"><fmt:message key="tabsubitem.12.1"/></span></span>
							</a>
						</li>
					</crh-common:authorize>

					<crh-common:authorize function="<%= Authorization.DEMAND_PLANNING_REPORT_EXPECTED_DEMAND %>">
						<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
						<li>
							<a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'reportexpected'}">class="highlight"</c:if>>
								<span class="outer"><span class="inner"><fmt:message key="tabsubitem.12.2"/></span></span>
							</a>
						</li>
					</crh-common:authorize>

					<crh-common:authorize function="<%= Authorization.DEMAND_PLANNING_REPORT_PLAN_VS_REAL %>">
						<c:set var="url">/../<crh-common:authorizedUrl/></c:set>
						<li>
							<a href="<c:url value="${url }"/>" <c:if test="${ApplicationSubTabs1Bean == 'reportexpectedandrealized'}">class="highlight"</c:if>>
								<span class="outer"><span class="inner"><fmt:message key="tabsubitem.12.3"/></span></span>
							</a>
						</li>
					</crh-common:authorize>

				</ul>
			</div>
		</td>
	</tr>
</table>
