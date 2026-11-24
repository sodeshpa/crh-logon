<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ include file="../includeTLDs.jsp"%>

<c:set var="preLoadWagonUrl">
	/../order/raildispo/PreLoadWagon.do
</c:set>

<script type="text/javascript">

	var preLoadPopupTexts = {
		loading : '<fmt:message key="extjs.loading"/>',
		removeProductConfirm : '<fmt:message key="raildispo.preloadpopup.message.removeproduct.confirm"/>'
	};
	
	var addOtherProductLinkDisabled = true;

	function selectMaterial() {
		var paramObject = {'selectMaterial' : ''};
		
		reloadThisPopup(paramObject);
	}
	
	function addOtherProduct() {
		if (!addOtherProductLinkDisabled) {
			var paramObject = {'addOtherProduct' : ''};
		
			reloadThisPopup(paramObject);
		}
	}
	
	function removeProduct(index) {
		var confirmResult = confirm(preLoadPopupTexts.removeProductConfirm);
		
		if (confirmResult) {
			var paramObject = {
					'removeProduct' : '',
					'index': index
			};
			
			reloadThisPopup(paramObject);
		}
	}

	function preLoad(param) {
		var paramObject = {'saveClose' : ''};
	
		reloadThisPopup(paramObject);
	}
	
	function reloadThisPopup(paramObject) {
		addFormElements(paramObject, "PreLoadWagonForm");
		showPopup('<c:url value="${preLoadWagonUrl}"/>', preLoadPopupTexts.loading, paramObject);
	}

	function cancel() {
		<c:if test="${callReloadWagonsJs}">
		if (logonPopupHideCallback) {
			logonPopupHideCallback();
		}
		</c:if>
		hidePopup();
	}

	function disableAddOtherProductLink() {
		Ext.getDom('imgAddOrderItem').src = '<crh-common:url value="/images/silk/basket_add_disabled.png"/>';
    	Ext.get('linkAddOrderItem').setStyle({'color': '#BBBBBB'});
    	addOtherProductLinkDisabled = true;
	}
	
	function enableAddOtherProductLink() {
		Ext.getDom('imgAddOrderItem').src = '<crh-common:url value="/images/silk/basket_add.png"/>';
    	Ext.get('linkAddOrderItem').setStyle({'color': '#000'});
    	addOtherProductLinkDisabled = false;
	}
	
	Ext.onReady(function() {
		
		<c:set var="preLoadWagonForm" value="${order_raildispo_PreLoadWagonForm}"/>
		
		<c:forEach var="preLoad" varStatus="status" items="${preLoadWagonForm.preLoads}">
		
			<c:set var="index" value="${status.index}"/>			
			<c:set var="shouldDisabled" value="${not status.last}"/>

			<%-- Disable material selects --%>
			<c:if test="${shouldDisabled}">
				var sapMatSelect<c:out value="${index}"/> = Ext.get('sapMaterial-<c:out value="${index}"/>');
				if (sapMatSelect<c:out value="${index}"/>) {
					sapMatSelect<c:out value="${index}"/>.set({
					    disabled: 'disabled'
					});
				}
			</c:if>
			
			<%-- Make tare weight read only --%>
			<c:if test="${preLoadWagonForm.isBag}">
				var tareWeight<c:out value="${index}"/> = Ext.get('tareWeight-<c:out value="${index}"/>');
				if (tareWeight<c:out value="${index}"/>) {
					tareWeight<c:out value="${index}"/>.set({
					    readonly: 'readonly'
					});
				}
			</c:if>
		</c:forEach>
		
		<%-- Disable / enable "Add other product link" --%>
		<c:choose>
			<c:when test="${preLoadWagonForm.isBulk}">
				disableAddOtherProductLink();
			</c:when>
			<c:when test="${preLoadWagonForm.isBag}">
				enableAddOtherProductLink();
			</c:when>
			<c:otherwise>
				disableAddOtherProductLink();
			</c:otherwise>
		</c:choose>
	});
</script>

<html:form action="raildispo/PreLoadWagonEntry.do" styleId='PreLoadWagonForm'>	

	<table class="classicblue" style="border: 1px solid lightgrey;">
		<tr>
			<th><fmt:message key="raildispo.preloadpopup.popuptitle"/> <c:out value="${preLoadWagonForm.wagon.wagonNo}"/></th>
		</tr>
		<tr>
			<td>
				<div>
					<table style="padding: 5px 5px 0; width: 100%;">
						<tr>
							<td>
								<span class="label"><fmt:message key="raildispo.preloadpopup.label.plant" />: </span>
								<c:out value="${preLoadWagonForm.plant.plantName}" />
							</td>
							<td>
								<span class="label"><fmt:message key="raildispo.preloadpopup.label.shippingpoint" />: </span>
								<c:out value="${preLoadWagonForm.loadingpoint.shippingpoint.shippingpointName}" />
							</td>
						</tr>
						<tr>
							<td>
								<span class="label"><fmt:message key="raildispo.preloadpopup.label.loadingpoint" />: </span>
								<c:out value="${preLoadWagonForm.loadingpoint.loadingpointName}"/>
							</td>
						</tr>
					</table>					
				
					<table style="padding:5px;">
						<thead>
							<tr>
								<th><fmt:message key="raildispo.preloadpopup.label.material"/></th>
								<th>
									<fmt:message key="raildispo.preloadpopup.label.tareweight"/>
									
									<c:if test="${preLoadWagonForm.isBulk}">
										<c:out value=" (${preLoadWagonForm.wagon.tareWeightT} t)"/>
									</c:if>
								</th>
								<th>
									<c:choose>
										<c:when test="${preLoadWagonForm.isBulk}">
											<fmt:message key="raildispo.preloadpopup.label.grossweight"/>
										</c:when>
										<c:when test="${preLoadWagonForm.isBag}">
											<fmt:message key="raildispo.preloadpopup.label.netqty"/>
										</c:when>
										<c:otherwise> - </c:otherwise>
									</c:choose>
								</th>
								<th><fmt:message key="raildispo.preloadpopup.label.uom"/></th>
								<th><fmt:message key="raildispo.preloadpopup.label.prodplant"/></th>
								<th></th>
							</tr>
						</thead>
						<tbody>
							<c:forEach var="preLoad" varStatus="status" items="${preLoadWagonForm.preLoads}">
								
								<c:set var="index" value="${status.index}"/>
								
								<tr>
									<td>
										<%-- Material select --%>
										<html:select property="sapMaterialIdIndexed[${index}]" styleId="sapMaterial-${index}"
														styleClass="preLoadInput" onchange="selectMaterial();">
											<html:option value=""><fmt:message key="raildispo.preloadpopup.label.selectmaterial"/></html:option>
											<c:forEach var="material" items="${preLoadWagonForm.materials}">
												<html:option value="${material.sapMaterialId}">
													<c:out value="${material.txt}"/>
												</html:option>
											</c:forEach>
										</html:select>
									</td>
									<td>
										<%-- Tare weight textbox --%>
										<c:choose>
											<c:when test="${preLoadWagonForm.isBulk or preLoadWagonForm.isBag}">
												<html:text property="tareWeightTIndexed[${index}]" 
													styleClass="preLoadInput" styleId="tareWeight-${index}" 
													title="(${preLoadWagonForm.wagon.tareWeightT} t)"/>
											</c:when>
											<c:otherwise> - </c:otherwise>
										</c:choose>
										
									</td>
									<td>
										<%-- Gross weight OR Net qty textbox --%>
										
										<c:choose>
											<c:when test="${preLoadWagonForm.isBulk}">
												<html:text property="grossWeightTIndexed[${index}]" 
													styleClass="preLoadInput" styleId="tareWeight-${index}"/>
											</c:when>
											<c:when test="${preLoadWagonForm.isBag}">
												<html:text property="netQuantityIndexed[${index}]" 
													styleClass="preLoadInput" styleId="netQuantity-${index}"/>
											</c:when>
											<c:otherwise> - </c:otherwise>
										</c:choose>
										
									</td>
									<td>
										<%-- Net qty UOM select --%>
										 
										<c:choose>
											<c:when test="${preLoadWagonForm.isBulk}">
												<c:out value="${preLoadWagonForm.tonUomTx.txt}"/>
											</c:when>
											<c:when test="${preLoadWagonForm.isBag}">
												<html:select property="netQuantityUomCdIndexed[${index}]"
													styleClass="preLoadInput" styleId="netQuantityUom-${index}">
													<html:option value="">
														<fmt:message key="option.select" />
													</html:option>
													
													<c:forEach var="altUom" items="${preLoad.sapMaterialAltUoms}">
														<html:option value="${altUom.uom.uomCd}">
															<c:out value="${altUom.uom.uomCd}"/>
														</html:option>
													</c:forEach>
												</html:select>
											</c:when>
											<c:otherwise> - </c:otherwise>
										</c:choose>
										
									</td>
									<td>
										<%-- Prod plant select --%>
										
										<c:choose>
											<c:when test="${preLoadWagonForm.isBulk or preLoadWagonForm.isBag}">
										
												<html:select property="prodPlantCdIndexed[${index}]"
																styleClass="preLoadInput prodPlantSelect" styleId="prodPlant-${index}">
													<html:option value=""><fmt:message key="option.select"/></html:option>
		
													<c:forEach var="plant" items="${preLoad.prodPlants}">
														<html:option value="${plant.plantCd}">
															<c:out value="${plant.plantName}"/>
														</html:option>
													</c:forEach>
												</html:select>
											</c:when>
											<c:otherwise> - </c:otherwise>
										</c:choose>
										
									</td>
									<td>
										<%-- Remove product link --%>
										<c:if test="${index ne 0}"> 
											<a id="linkRemoveOrderItem" href="#" onclick="removeProduct(<c:out value="${index}"/>);" 
													title="<fmt:message key="raildispo.preloadpopup.label.removeproduct"/>"> 
												<img id="imgremoveOrderItem" src="<crh-common:url value='/images/silk/basket_delete.png'/>"/>
											</a>
										</c:if>
									</td>
								</tr>
							</c:forEach>
							<tr>
								<td>
									<%-- Add other product link --%>
									<a id="linkAddOrderItem" href="#" onclick="addOtherProduct();"> 
										<img id="imgAddOrderItem" src="<crh-common:url value='/images/silk/basket_add.png'/>"/>
										<b><fmt:message key="raildispo.preloadpopup.label.addotherproduct"/></b>
									</a>
								</td>
							</tr>
						</tbody>
					</table>
				</div>
			</td>
		</tr>
		<%-- buttons --%>
		<tr>
			<td>
				<div align="right">
					<input onclick="preLoad();" type="button" id="preLoadBtn"
						class="ViewButtonStyle"
						value=" <fmt:message key="raildispo.preloadpopup.button.preload"/> ">
					<input onclick="cancel();" type="button"
						class="ViewButtonStyle"
						value=" <fmt:message key="raildispo.preloadpopup.button.cancel"/> ">
				</div>
			</td>
		</tr>
	</table>
</html:form>