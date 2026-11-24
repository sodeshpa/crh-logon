<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderSpecialEmptyMaterialOptionInc.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp"%>
<%-- Struts needs this fake form tag, fond no better way to do it. --%> 
<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${order_auftrag_OrderSpecialForm}"/>
<%-- For JS syntax highlighting in Eclipse --%>


			document.getElementById("emptyMaterialText").innerHTML = "<c:out value="${order_auftrag_OrderSpecialForm.emptyWagonMatDesc}" />";
			var defaultData = {
				    sapMaterialId: '<c:out value="${order_auftrag_OrderSpecialForm.emptyWagonMaterialId}" />',
				    txt: '<c:out value="${order_auftrag_OrderSpecialForm.emptyWagonMatDesc}" />'
			};			
			var r = new materialCombo.store.recordType(defaultData, '<c:out value="${order_auftrag_OrderSpecialForm.emptyWagonMaterialId}" />');
			materialCombo.store.insert(0, r);
			materialCombo.preselectValue('<c:out value="${order_auftrag_OrderSpecialForm.emptyWagonMaterialId}" />', false);

			var defaultData = {
				    plantCd: '<c:out value="${order_auftrag_OrderSpecialForm.defaultPlant.plantCd}" />',
				    plantName: '<c:out value="${order_auftrag_OrderSpecialForm.defaultPlant.plantName}" />'
			};			
			var r = new prodPlantCombo.store.recordType(defaultData, '<c:out value="${order_auftrag_OrderSpecialForm.emptyWagonMaterialId}" />');
			prodPlantCombo.store.insert(0, r);
			prodPlantCombo.preselectValue('<c:out value="${order_auftrag_OrderSpecialForm.defaultPlant.plantCd}" />', false);
									
			document.getElementById('quantity').value = '';
			document.getElementById('quantityUom').value = '';
			
			materialCombo.setDisabled(true);
			prodPlantCombo.setDisabled(true);
			document.getElementById('quantity').disabled = true;
			document.getElementById('quantityUom').disabled = true;

<%-- END $RCSfile: OrderSpecialEmptyMaterialOptionInc.jsp,v $ --%>
