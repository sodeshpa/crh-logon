<%@ include file="../includeTLDs.jsp" %>
<%-- See details in main tile: LKWStammdatenTile --%>
                          <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="width:100%;">
                            <colgroup>
                            <col width="99%">
                            <col width="1%">
                            </colgroup>
                            <tbody>
	<c:set var="zebracount" value="1"/>
	<c:forEach var="entry" items="${admin_TruckAdminForm.truckList}">
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
                              <tr  <c:if test="${entry.truckId eq admin_TruckAdminForm.truckId }">class="selected"</c:if><c:if test="${entry.truckId ne admin_TruckAdminForm.truckId }"> class="zebra<c:out value="${zebracount}" />" </c:if> >
                                <td><a href="#" onclick='resetTruck(<c:out value="${entry.truckId}"/>)'><c:out value="${entry.truckName}"/></a></td>
                              </tr>
	</c:forEach>                   
                              
                            </tbody>
                          </table>