<%@ include file="../includeTLDs.jsp" %>

<html:form action="/carrierassignment/CarrierAssignment.do" styleId="CarrierAssignmentForm">
<html:hidden property="formAction" value=""/>
		<table width="800" class="classicblue" style="margin-top:4px">
			<tr>
				<th>
					<fmt:message key="carrierassignment.header.pool"/>
					<br>
					<br>
					<html:select property="selectedPoolFilter" style="width:150px;" onchange="modifyFilter({filtersChanged:''})">
						<html:option value="-1">-</html:option>
						<c:forEach var="poolHashMapEntity" varStatus="st" items="${admin_CarrierAssignmentForm.poolMap}">
							<html:option value="${poolHashMapEntity.key}"><c:out value="${poolHashMapEntity.value}"/></html:option>
						</c:forEach>
					</html:select>
				</th>
				<th>
					<fmt:message key="carrierassignment.header.kunde"/>
					<br>
					<br>
					<html:select property="selectedSoldtoFilter" style="width:150px;" onchange="modifyFilter({filtersChanged:''})">
						<html:option value="-1">-</html:option>
						<c:forEach var="soldtoHashMapEntity" varStatus="st" items="${admin_CarrierAssignmentForm.soldtoMap}">
							<html:option value="${soldtoHashMapEntity.key}"><c:out value="${soldtoHashMapEntity.value}"/></html:option>
						</c:forEach>
					</html:select>
				</th>
				<th>
					<fmt:message key="carrierassignment.header.shipto"/>
					<br>
					<br>
					<html:select property="selectedShiptoFilter" style="width:200px;" onchange="modifyFilter({filtersChanged:''})">
						<html:option value="-1">-</html:option>
						<c:forEach var="shiptoHashMapEntity" varStatus="st" items="${admin_CarrierAssignmentForm.shiptoMap}">
							<html:option value="${shiptoHashMapEntity.key}"><c:out value="${shiptoHashMapEntity.value}"/></html:option>
						</c:forEach>
					</html:select>
				</th>
				<th>
					<fmt:message key="carrierassignment.header.produkt"/>
					<br>
					<fmt:message key="carrierassignment.header.baseprice"/>
					<br>
					<html:select property="selectedProductFilter" style="width:200px;" onchange="modifyFilter({filtersChanged:''})">
						<html:option value="-1">-</html:option>
						<c:forEach var="productHashMapEntity" varStatus="st" items="${admin_CarrierAssignmentForm.productMap}">
							<html:option value="${productHashMapEntity.key}"><c:out value="${productHashMapEntity.value}"/></html:option>
						</c:forEach>
					</html:select>
				</th>
				<th>
					<fmt:message key="carrierassignment.header.carrier"/>
				</th>
			</tr>
			<c:set var="zebracount" value="0"/>
			<c:forEach var="entry" varStatus="status" items="${admin_CarrierAssignmentForm.poolShiptoMaterialList}">
				<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
				<tr class="zebra<c:out value="${zebracount}" />">
					<td>
						<c:out value="${entry.poolName}"/>
					</td>
					<td>
						<c:out value="${entry.soldtoName}"/>
					</td>
					<td>
						<c:out value="${entry.shiptoName}"/>
					</td>
					<td>
						<c:out value="${entry.materialDescription}"/> (<c:out value="${entry.materialNr}"/>)
						<br>
						<c:out value="${entry.plantName}"/>
					</td>
					<td>
						<table>
							<tr>
								<td title='<fmt:message key="carrierassignment.autoForward"/>'>
									<html:checkbox value="true" property="poolShiptoMaterialList[${status.count-1}].autoCarrierAssignmentFlag" styleId="autoFlag${status.count-1}"/>
								</td>
								<td>
									<html:select property="poolShiptoMaterialList[${status.count-1}].commCarrierId" style="width:180px;" styleId="carrierDropdown${status.count-1}" onchange="setCheckbox('autoFlag${status.count-1}', 'carrierDropdown${status.count-1}')">
									<html:option value="-1">-</html:option>
									<c:forEach var="carrier" varStatus="st" items="${admin_CarrierAssignmentForm.poolCarrierMap[entry.poolId]}">
										<html:option value="${carrier.commCarrierId}"><c:out value="${carrier.commCarrierName}"/></html:option>
									</c:forEach>
									</html:select>
								</td>
							</tr>
						</table>
					</td>
				</tr>
			</c:forEach>
		</table>
		<table class="big_form2" style="margin-top: 8px;">
			<tr>
				<td class="buttons right">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.reset"/>" onClick="submitWithAction('reset')">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onClick="submitWithAction('save')">
				</td>
			</tr>
		</table>
</html:form>

<script type="text/javascript">
	for (var i=0; true; i++) {
		var dropdown = document.getElementById('carrierDropdown' + i);
		if ( dropdown == null ) {
			break;
		}
   		setCheckbox( 'autoFlag' + i, 'carrierDropdown' + i );
   	}
</script>		