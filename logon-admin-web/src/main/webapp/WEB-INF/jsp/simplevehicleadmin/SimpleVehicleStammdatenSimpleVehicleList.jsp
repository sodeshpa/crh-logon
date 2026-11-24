<%@ include file="../includeTLDs.jsp" %>
<%-- See details in main tile: SimpleVehicleStammdatenTile --%>
                          <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="width:100%;">
                            <colgroup>
                            <col width="99%">
                            <col width="1%">
                            </colgroup>
                            <tbody>
	<c:set var="zebracount" value="1"/>
	<c:forEach var="entry" items="${admin_SimpleVehicleAdminForm.simpleVehicleList}">
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
                              <tr  <c:if test="${entry.simpleVehicleId eq admin_SimpleVehicleAdminForm.simpleVehicleId }">class="selected"</c:if><c:if test="${entry.simpleVehicleId ne admin_SimpleVehicleAdminForm.simpleVehicleId }"> class="zebra<c:out value="${zebracount}" />" </c:if> >
                                <td><a href="#" onclick='resetSimpleVehicle(<c:out value="${entry.simpleVehicleId}"/>)'><c:out value="${entry.vehicleName}"/></a></td>
                              </tr>
	</c:forEach>                   
                              
                            </tbody>
                          </table>