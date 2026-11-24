<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: AuftragBearbeitungContainerSelectorPopup.jsp,v $ --%>
<%---- Visible coming from shipping screen, only available in case the shippingpoint is type container terminal.-----%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>


<%@page import="com.crh.logon.order.web.forms.auftrag.AuftragBearbeitungForm"%>
<%@page import="com.crh.logon.order.web.actions.auftrag.AuftragBearbeitungAction"%>

<% if (request.getAttribute("close")!= null){%>
<script>
	hidePopup();
	loadOrders();
</script>
<%}

else{%>

<script type="text/javascript">

	function enableSave(value){
		if (value != ""){
			document.getElementById("save").disabled=false;
		}
		else{
			document.getElementById("save").disabled=true;
		}
	}
	
	function saveSelection() {
		var sel = document.getElementById("inputContainerIdSelect").value;
		a = {
			"formAction" :"<%=AuftragBearbeitungAction.ACTION_SAVE_CONTAINER_SELECTION%>",
			"<%=AuftragBearbeitungForm.REQUEST_ORDER_ITEM_ID%>": '<%=request.getAttribute(AuftragBearbeitungForm.REQUEST_ORDER_ITEM_ID) %>',
			"<%=AuftragBearbeitungForm.REQUEST_CONTAINER_ID%>": sel
		};	

		var divp = Ext.get(logonPopup);
		divp.load({
			url: 'AuftragBearbeitung.do', 
			text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts: true,
			params: a
		});
	}
</script>


<html:form styleId = "order_auftrag_AuftragBearbeitungForm" action="auftrag/AuftragBearbeitung.do">
<table class="classicblue" style="border:none; width:auto">
<tr>
	<th colspan="2">
		<fmt:message key="auftragbearbeitung.loadcontainerpopup.title"/>
	</th>
</tr>
<tr>

	<td class="popupcontent">
			<table width="100%">
				<tr>
					<td class="label right" >
					<%pageContext.setAttribute("list",request.getAttribute(AuftragBearbeitungForm.REQUEST_CONTAINERLIST)); %>
					<html:select onchange='javascript:enableSave(this.value)' property="inputContainerIdSelect"  styleId="inputContainerIdSelect" style="width:100%;" >
						<html:option value=""><fmt:message key="option.select"/></html:option>
						<html:options collection="list" property="containerId" labelProperty="containerName"/>
					</html:select>
					</td>
				</tr>
				<tr>
			<td colspan="2" class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<input id="save" disabled type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="saveSelection('<c:out value="${itemId }"/>')"/>
			</td>
		</tr>
	</table>
	</td>
</table>

</html:form>
<%} %>
