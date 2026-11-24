<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ContainersEdit.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	function initContainerDropdown(fieldname){
	
		var containerRecordType = Ext.data.Record.create([
		    {name: 'id', mapping: 'containerNo'},    
		    {name: 'containerName', mapping: 'containerName'},    
		    {name: 'containerTypeName', mapping: 'containerTypeName'},
		    {name: 'loadId', mapping: 'loadId'}    
		]);
		
		var containerListStore = new Ext.data.Store({
	    	proxy: new Ext.data.DWRProxyForPager(RailDispoAjaxService.fetchContainerList, true),
	    	reader: new Ext.data.ListRangeReader({id:'id', totalProperty:'totalSize'}, containerRecordType),
		    remoteSort: true
		});
		
	
	    // Custom rendering Template
	    var resultTpl = new Ext.XTemplate(
	    	'<table class="classic"><thead><tr>',
	    	'<th width="150"><nobr><fmt:message key="raildispo.texte.ContainerNr"/></nobr></th>',
	    	'<th width="150"><nobr><fmt:message key="raildispo.texte.ContainerName"/></nobr></th>',
	    	'<th width="100"><nobr><fmt:message key="raildispo.texte.ContainerType"/></nobr></th>',
	    	'</tr></thead><tbody>',
	        '<tpl for="."><tr class="listitem zebra{[xindex % 2]} ',
	        	'<tpl if="loadId != null">',
					'x-item-disabled',
				'</tpl>',
	        '">',
	        '<td><nobr>{id}</nobr></td>',
	        '<td><nobr>{containerName}</nobr></td>',
	        '<td><nobr>{containerTypeName}</nobr></td>',
	        '</tr></tpl>',
	        '</tbody></table>'
	    );

	    
	    var search = new Ext.form.ComboBox({
	        store: containerListStore,
	        displayField:'id',
	        typeAhead: false,
	        loadingText: '',
	        maxHeight:800,
	        minChars:0,
	        pageSize:10,
	        forceSelection: true,
	        selectOnFocus : true,
	        triggerAction: 'all',
	        hideTrigger:false,
	        tpl: resultTpl,
	        applyTo: fieldname,
	        itemSelector: 'tr.listitem',
	        listWidth: 400
	    });

	    search.on('beforeselect', function(combo, record, index) {
		    if (record.data['loadId'] != null) {

			    return false;
		    }

		    return true;
	    });
	};

</script>

<html:form action="raildispo/ContainersEdit.do" onsubmit="return false;" styleId="ContainersEditForm">

<table class="classicblue" style="border:none; width:auto"  class="big_form2">
<tr>
	<th colspan="2">
		<fmt:message key="raildispo.texte.editcontainers"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%" class="big_form2" >
		<tr>
			<td colspan='3' class='border'>
				<table width='100%'>
					<tr><td colspan='3'  class='label'><fmt:message key="raildispo.texte.RailcarNr"/>: <c:out value="${order_raildispo_ContainersEditForm.wagon.wagonNo}"/></td></tr>
					<c:forEach begin='0' end='${order_raildispo_ContainersEditForm.wagon.wagonType.nrContainers - 1}' var='i'>
						<c:set var="container" value="${order_raildispo_ContainersEditForm.containers[i]}" />
						<c:set var="load" value="${null}" />
						<c:set var="item" value="${null}" />
						<c:if test="${container != null}">
							<c:set var="load" value="${order_raildispo_ContainersEditForm.loads[container.containerId]}" />
							<c:if test="${load != null}">
								<c:set var="item" value="${order_raildispo_ContainersEditForm.orderItems[load.loadId]}" />
							</c:if>
						</c:if>
						<tr>
						<td  class='label right'><fmt:message key="raildispo.texte.Container"/><c:out value='${i + 1}'/></td>
						<td>
							<c:if test="${item != null}"><c:out value="${container.containerNo}"/></c:if>
							<c:if test="${item == null}">
								<input type="text" name="newContainer[<c:out value='${i}'/>]" id="newContainer<c:out value='${i}'/>" value="<c:out value='${container.containerNo}'/>">
						<script type="text/javascript">
							initContainerDropdown("newContainer<c:out value='${i}'/>");
						</script>					
							</c:if>
						</td>
						<td><c:if test="${item != null}"><fmt:message key="raildispo.texte.OrderAssigned"/></c:if> </td>
						</tr>
					</c:forEach>					
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="4" class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="saveContainers()"/>   				
			</td>
		</tr>
	</table>
	</td>
</table>

</html:form>



<script type="text/javascript">



	function saveContainers(){	
		a = {save: ''};
		addFormElements(a, 'ContainersEditForm');

		showPopup('ContainersEdit.do', null, a);
	};
	
	onPopupReady(function() {
		
		var el = Ext.getDom('ContainersEditForm');
		if (el) {
			el.focus();
		}
	});
</script>


