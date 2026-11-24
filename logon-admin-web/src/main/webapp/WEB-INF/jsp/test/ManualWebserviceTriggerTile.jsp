<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page import="com.crh.logon.admin.web.forms.test.ManualWebserviceTriggerForm"%>

<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/TestAjaxService.js'/>"></script>
<c:set var="wsform" value="${sessionScope.admin_ManualWebserviceTriggerForm}"/>

<script type="text/javascript">
	function setOptions() {
		var diva = Ext.get('optionsDiv');
		form = Ext.getDom('formId');
		p = {'getOptions' : ''};
		addFormElements(p, form);
		diva.load( {
			url :'ManualWebserviceTrigger.do',
			params : p,
			scripts :true
		});
	}
	
	function loadFile() {
		var diva = Ext.get('optionsDiv');
		form = Ext.getDom('formId');
		p = {'loadFile' : ''};
		addFormElements(p, form);
		diva.load( {
			url :'ManualWebserviceTrigger.do',
			params : p,
			scripts :true
		});
	}
	
	function enableFormElements() {
		
		var msgType = Ext.get('messageType').getValue();
		Ext.getDom('orderNo').disabled = true;
		Ext.getDom('orderItemNo').disabled = true;
		Ext.getDom('sendCreditNote').disabled = true;
		Ext.getDom('shippingpointId').disabled = true;
		switch (msgType) {
			case '<%= ManualWebserviceTriggerForm.WS_SAP_SALESDOC %>':
			case '<%= ManualWebserviceTriggerForm.WS_SAP_TRANSFERDOC_INBOUND %>':
			case '<%= ManualWebserviceTriggerForm.WS_SAP_TRANSFERDOC_OUTBOUND %>':
				Ext.getDom('sendCreditNote').disabled = false;
				Ext.getDom('orderItemNo').disabled = !Ext.getDom('sendCreditNote').checked;	
				Ext.getDom('orderNo').disabled = false;
			break;
			case '<%=ManualWebserviceTriggerForm.WS_SAP_GOODSMOVEMENT%>':
				Ext.getDom('sendCreditNote').disabled = true;
				Ext.getDom('orderItemNo').disabled = false;	
				Ext.getDom('orderNo').disabled = false;
			break;
			case '<%= ManualWebserviceTriggerForm.WS_SAP_SHIPMENTDOC %>':
				Ext.getDom('sendCreditNote').disabled = true;
				Ext.getDom('orderItemNo').disabled = true;
				Ext.getDom('orderNo').disabled = false;
			break;
			case '<%= ManualWebserviceTriggerForm.WS_SAP_PRICING_CALL %>':
			case '<%= ManualWebserviceTriggerForm.WS_SAP_LOGISTIC_REPORT %>':
				Ext.getDom('orderNo').disabled = false;
			break;
			case '<%= ManualWebserviceTriggerForm.WS_LDS_PDA_LOGIN %>':
			case '<%= ManualWebserviceTriggerForm.WS_LDS_LOAD_ORDER_NOTIFICATION %>':
			case '<%= ManualWebserviceTriggerForm.WS_LDS_DELETE_LOAD_ORDER %>':
				Ext.getDom('orderNo').disabled = false;
				Ext.getDom('shippingpointId').disabled = false;
			break;
		}
		// enable order no if order item no is enabled.
		if (!Ext.getDom('orderItemNo').disabled) {
			Ext.getDom('orderNo').disabled = false;
		}
	}
	
	Ext.onReady(function() {
		enableFormElements();
		
		var fileNameRec = Ext.data.Record.create([ { name: 'name' }, { name: 'isDirectory' } ]);
		var fileNameStore = new Ext.data.Store({
	        proxy: new Ext.data.DWRProxy(function(query, callback) { TestAjaxService.getFileNameList(path, callback) }, true),
			reader: new Ext.data.ObjectReader({id:'name'}, fileNameRec), 
			remoteSort: true
	    });
		
		var path = [ <c:forEach var="pathElement" varStatus="status" items="${wsform.webServiceOptions.pathElements}">'<c:out value="${pathElement}"/>'<c:if test="${not status.last}">,</c:if></c:forEach> ];
		Ext.getDom('filePath').innerHTML = path.join('/');
		Ext.getDom('fileName').value = '';
				
		var fileNameCombo = new Ext.ux.TwinCombo({
			id: 'fileNameCombo',
		    applyTo:'fileNameCombo',
			selectOnFocus: true,
			editable: false,
		    typeAhead: false,
		    triggerAction: 'all',
		    store: fileNameStore,
		    width: 180,
		    listWidth: 250,
		    displayField: 'name',
	        valueField: 'name',
		    onTrigger2Click: function() {
		    	var fileName = Ext.getDom('fileName').value;
		    	var pathName = Ext.getDom('filePath').innerHTML;
		    	if ( (fileName) && (fileName.length > 0) ) {
		    		Ext.getDom('fileName').value = '';	
		    	} 
		    	else if (path.length > 0) {
			    	path.pop();
			    	Ext.getDom('filePath').innerHTML = path.join('/');
		    	}
			    fileNameCombo.clearValue();
			    fileNameStore.reload();
			    fileNameCombo.expand();
		    }
		});
		fileNameCombo.on('select', function() {
			var rec = fileNameStore.getById(fileNameCombo.getValue());
			if (rec.data.isDirectory) {
				path.push(fileNameCombo.getValue());
				Ext.getDom('filePath').innerHTML = path.join('/');
				fileNameStore.reload();
			}
			else {
				Ext.getDom('fileName').value = fileNameCombo.getValue();
			}
		});
	});

</script>

<jsp:include page="../SubTab11Tile.jsp" />

<html:form action="test/ManualWebserviceTrigger.do" styleId="formId">

<!-- Begin table 3 rows -->
<table class="big_form2">
	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.test" />
		/ <fmt:message key="tabsubitem.11.1" /></th>
	</tr>
	<tr>
		<td><!-- beginn Border-Table   -->
			<table class="classicblue" style="width:100%">
				<tr>
					<td style="border: 1px solid #888888; padding: 10px;">
						<h2>Create new request</h2>
						<span style="padding-right: 20px;">
							<html:select size="1" property="webServiceOptions.selectedMessageType" style="width: 250px;" styleId="messageType" onchange="enableFormElements();">
								<c:forEach var="messageType" items="${wsform.webServiceOptions.messageTypes}">
									<html:option value="${messageType}"><c:out value="${messageType}"/></html:option>
								</c:forEach>
							</html:select>
						</span>
						<span style="padding-right: 20px;">
							order number: <html:text property="webServiceOptions.orderNo" styleClass="textfield" styleId="orderNo" style="width: 100px;"/>
						</span>
						<span style="padding-right: 20px;">
							order item number: <html:text property="webServiceOptions.orderItemNo" styleClass="textfield" styleId="orderItemNo" style="width: 100px;"/>
							<html:checkbox property="webServiceOptions.sendCreditNote" styleId="sendCreditNote" onchange="enableFormElements();"/><label for="sendCreditNote">credit note</label>
						</span>
						<span style="padding-right: 20px;">
							shipping point: <html:select size="1" property="webServiceOptions.shippingpointId" style="width: 300px;" styleId="shippingpointId">
								<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">-</html:option>
								<c:forEach var="sp" items="${wsform.webServiceOptions.shippingpointList}">
									<html:option value="${sp.shippingpointId}">
										<c:out value="${sp.shippingpointName}"/> - <c:out value="${sp.txt}"/><c:out value="${sp.ldsServer}"/>
									</html:option>
								</c:forEach>
							</html:select>
						</span>
						<input type="button" onclick="setOptions()" class="SmallButtonStyle" value="create"/>
					</td>
				</tr>
				<tr>
					<td style="border: 1px solid #888888; padding: 10px;">
						<h2>Load previously sent request</h2>
						<span>&nbsp;</span><span id="filePath"></span>
						<html:hidden property="webServiceOptions.fileName" styleId="fileName"/>
						<div style="width: 250px">
							<div style="float: left;">
								<input type="text" id="fileNameCombo"/>
							</div>
							<div style="float: right; padding-top: 2px;"><input type="button" onclick="loadFile()" class="SmallButtonStyle" value="load"/></div>
						</div>
					</td>
				</tr>
				<tr>
					<td style="vertical-align: top;">
						<div id="optionsDiv">
							<jsp:include page="Options.jsp"/>
						</div>
					</td>
				</tr>
				<tr>
					<td>
						<html:submit value="submit" property="callWebService" styleClass="ViewButtonStyle" ></html:submit>
					</td>
				</tr>
			</table>
		</td>
	</tr>
	
	<tr>
		<td>
					<table class="classicblue" style="width:100%">
						<colgroup>
							<col width="30%">
							<col width="70%">
						</colgroup>
						<tbody>
							<tr>
								<td class="labelblue" colspan="2">
									callEmailInfo
									<% if (!ConfigBeanProvider.getConfigBean().getMailTestEnabled()) { %>
										<div style="font-style: oblique; color: red;">Email test mode is disabled!</div>
									<% } else { %>
										<div>
											Email test mode is enabled to <%= ConfigBeanProvider.getConfigBean().getMailTestTo() %><br />
											(unless a specific recipient is configured for the sales area the order belongs to)
										</div>
									<% } %>									
								</td>
							</tr>
							<tr>
								<td class="label left">
									recipient:
								</td>
								<td>
									<nobr><html:text property="email.to" styleClass="textfield"/>*</nobr>
								</td>
							</tr>
							<tr>
								<td class="label left">
									message:
								</td>
								<td>
									<nobr><html:text property="email.text" styleClass="textfield"/>*</nobr>
								</td>
							</tr>
							<tr>
								<td class="label left">
									documentId:
								</td>
								<td>
									<html:text property="email.documentId" styleClass="textfield"/>
								</td>
							</tr>
							<tr>
								<td class="label left">
									orderNo:
								</td>
								<td>
									<html:text property="email.orderNo" styleClass="textfield"/>
								</td>
							</tr>
							<tr>
								<td colspan="2">
									*This parameter is ignored when an order confirmation is being send.
								</td>
							</tr>
							<tr>
								<td class="buttons" colspan="2">
									<html:submit value="submit" property="callEmailInfo" styleClass="ViewButtonStyle" ></html:submit>
								</td>
							</tr>
						</tbody>
					</table>
	</tr>
	<tr>
		<td>
					<table class="classicblue" style="width:100%">
						<colgroup>
							<col width="30%">
							<col width="70%">
						</colgroup>
						<tbody>
							<tr>
								<td class="labelblue" colspan="2">
									Printer
								</td>
							</tr>
							<tr>
								<td class="label left">
									printer URI:
								</td>
								<td>
									<html:text property="printer.printerUri" styleClass="textfield"/>
								</td>
							</tr>
							<tr>
								<td class="label left">
									printer name:
								</td>
								<td>
									<html:text property="printer.printerName" styleClass="textfield"/>
								</td>
							</tr>
							<tr>
								<td class="label left">
									orderNo:
								</td>
								<td>
									<html:text property="printer.orderNo" styleClass="textfield"/>
								</td>
							</tr>
							<tr>
								<td class="label left">
									documentId:
								</td>
								<td>
									<html:text property="printer.documentId" styleClass="textfield"/>
								</td>
							</tr>
							<tr>
								<td class="buttons" colspane="2">
									<html:submit value="submit" property="sendToPrinter" styleClass="ViewButtonStyle" ></html:submit>
								</td>
							</tr>
						</tbody>
					</table>
				</td>
			</tr>
		</table>
		</html:form>

