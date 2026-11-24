<%@ include file="../includeTLDs.jsp" %>

<html:form action="/rfidadmin/RFIDAdmin.do" styleId="admin_RFIDAdminForm">


<table width="800" class="classicblue" style="margin-top:4px">
			<tr>
				<th>
					<fmt:message key="rfidadmin.header.rfidIdentity"/>
					<br>
				</th>
				<th>
					<fmt:message key="rfidadmin.header.name"/>
					<br>
				</th>
				<th>
					<fmt:message key="rfidadmin.header.objectLabel"/>
					<br>
				</th>
				<th>
					
				</th>
				<th>
					<fmt:message key="rfidadmin.header.activeFlag"/>
				</th>
			</tr>
			<c:set var="zebracount" value="0"/>
			<c:forEach var="rfid" varStatus="status" items="${admin_RFIDAdminForm.rfidList}">
				<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
				<tr class="zebra<c:out value="${zebracount}" />">
					<td>
						<c:out value="${rfid.rfidIdentity}"/>
					</td>
					<td>
						<c:out value="${rfid.name}"/>
					</td>
					<td>
						<c:out value="${rfid.objectLabel}"/>
					</td>
					<td>
						<c:out value="${rfid.rfidObjectType}"/>
					</td>
					<td>
						<c:choose>
	      	    		<c:when test="${rfid.activeFlag == true}">
	      	    			<img width="16" height="16" alt="activated" src="<crh-common:url value='/images/active.png'/>"/>
	      	    		</c:when>
	      	    		<c:otherwise>
	      	    			<img width="16" height="16" alt="activated" src="<crh-common:url value='/images/deactivated.png'/>"/>
	      	    		</c:otherwise>
	      	    		</c:choose>
					</td>
				</tr>
			</c:forEach>
		</table>
</html:form>