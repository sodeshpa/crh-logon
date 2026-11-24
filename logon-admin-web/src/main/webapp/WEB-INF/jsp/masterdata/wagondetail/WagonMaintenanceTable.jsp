<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<%@ include file="../../includeTLDs.jsp"%>
<script type="text/javascript">

function modifyDate(field){
	if (document.getElementById('wagonDetailMaintenance.selectedMaintRhythmTypeCd').value != '0') {
			var params = ({lastDateChanged:field} );
			addFormElements(params, "WagonDetailForm");
			var diva = Ext.get('tableDiv');
			diva.load({
				url: 'WagonDetail.do', 
				params: params,
				text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
				scripts: true
			});
	}
}

Ext.onReady(function() {

			transformSelectToCombobox({ transform: 'maintenanceBeanG10.selectedShipto', width: 200, listWidth: 200 } );	
			transformSelectToCombobox({ transform: 'maintenanceBeanG20.selectedShipto', width: 200, listWidth: 200 } );	
			transformSelectToCombobox({ transform: 'maintenanceBeanG30.selectedShipto', width: 200, listWidth: 200 } );	
			transformSelectToCombobox({ transform: 'maintenanceBeanG40.selectedShipto', width: 200, listWidth: 200 } );	
			transformSelectToCombobox({ transform: 'maintenanceBeanG42.selectedShipto', width: 200, listWidth: 200 } );	
			transformSelectToCombobox({ transform: 'maintenanceBeanG43.selectedShipto', width: 200, listWidth: 200 } );	
			transformSelectToCombobox({ transform: 'maintenanceBeanG48.selectedShipto', width: 200, listWidth: 200 } );	
			transformSelectToCombobox({ transform: 'maintenanceBean3M.selectedShipto', width: 200, listWidth: 200 } );	
			transformSelectToCombobox({ transform: 'maintenanceBeanP5.selectedShipto', width: 200, listWidth: 200 } );	
			transformSelectToCombobox({ transform: 'maintenanceBeanC.selectedShipto', width: 200, listWidth: 200 } );	
			
			var lastDateG10 = new Ext.form.DateField({
				id: 'maintenanceBeanG10.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG10.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG10.lastDate}"/>'
			});
			
			lastDateG10.on('change', function(field, newValue, oldValue){
				modifyDate('G1.0');
			});
				
			new Ext.form.DateField({
				id: 'maintenanceBeanG10.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG10.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG10.nextDate}"/>'
			});
			
			var lastDateG20 = new Ext.form.DateField({
				id: 'maintenanceBeanG20.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG20.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG20.lastDate}"/>'
			});
				
			lastDateG20.on('change', function(field, newValue, oldValue){
				modifyDate('G2.0');
			});	
				
			new Ext.form.DateField({
				id: 'maintenanceBeanG20.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG20.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG20.nextDate}"/>'
			});
			
			var lastDateG30 = new Ext.form.DateField({
				id: 'maintenanceBeanG30.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG30.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG30.lastDate}"/>'
			});
				
			lastDateG30.on('change', function(field, newValue, oldValue){
				modifyDate('G3.0');
			});	
				
			new Ext.form.DateField({
				id: 'maintenanceBeanG30.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG30.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG30.nextDate}"/>'
			});
			
			var lastDateG40 = new Ext.form.DateField({
				id: 'maintenanceBeanG40.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG40.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG40.lastDate}"/>'
			});
			
			lastDateG40.on('change', function(field, newValue, oldValue){
				modifyDate('G4.0');
			});
				
			new Ext.form.DateField({
				id: 'maintenanceBeanG40.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG40.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG40.nextDate}"/>'
			});
			
			var lastDateG42 = new Ext.form.DateField({
				id: 'maintenanceBeanG42.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG42.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG42.lastDate}"/>'
			});
			
			lastDateG42.on('change', function(field, newValue, oldValue){
				modifyDate('G4.2');
			});
				
			new Ext.form.DateField({
				id: 'maintenanceBeanpG42.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG42.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG42.nextDate}"/>'
			});
			
			var lastDateG43 = new Ext.form.DateField({
				id: 'maintenanceBeanG43.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG43.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG43.lastDate}"/>'
			});
			
			lastDateG43.on('change', function(field, newValue, oldValue){
				modifyDate('G4.3');
			});
				
			new Ext.form.DateField({
				id: 'maintenanceBeanG43.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG43.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG43.nextDate}"/>'
			});
			
			var lastDateG48 = new Ext.form.DateField({
				id: 'maintenanceBeanG48.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG48.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG48.lastDate}"/>'
			});
			
			lastDateG48.on('change', function(field, newValue, oldValue){
				modifyDate('G4.8');
			});
				
			new Ext.form.DateField({
				id: 'maintenanceBeanG48.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanG48.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanG48.nextDate}"/>'
			});
			
			var lastDate3M = new Ext.form.DateField({
				id: 'maintenanceBean3M.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBean3M.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBean3M.lastDate}"/>'
			});
			
			lastDate3M.on('change', function(field, newValue, oldValue){
				modifyDate('3M');
			});
				
			new Ext.form.DateField({
				id: 'maintenanceBean3M.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBean3M.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBean3M.nextDate}"/>'
			});
									
			var lastDateP5 = new Ext.form.DateField({
				id: 'maintenanceBeanP5.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanP5.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanP5.lastDate}"/>'
			});
			
			lastDateP5.on('change', function(field, newValue, oldValue){
				modifyDate('P5');
			});
				
			new Ext.form.DateField({
				id: 'maintenanceBeanP5.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanP5.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanP5.nextDate}"/>'
			});
			
			var lastDateC = new Ext.form.DateField({
				id: 'maintenanceBeanC.lastDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanC.lastDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanC.lastDate}"/>'
			});
			
			lastDateC.on('change', function(field, newValue, oldValue){
				modifyDate('C');
			});
				
			new Ext.form.DateField({
				id: 'maintenanceBeanC.nextDate',
				format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
				altFormats: 'd.m.Y',
				applyTo: 'maintenanceBeanC.nextDate',
				allowBlank: true,
				style: 'width:140px;',
				width:'140px',
				//this is somewhat hacked, but as far the format itself must be d.m.y, but the format is in fact different, needed
				markInvalid:function(msg){
					msg = msg.replace(this.format, "dd.mm.yy or dd.mm.yyyy");
					Ext.form.DateField.superclass.markInvalid.call(this,msg);
				},
				value:'<c:out value="${admin_WagonDetailForm.wagonDetailMaintenance.maintenanceBeanC.nextDate}"/>'
			});
});
</script>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${admin_WagonDetailForm}" />
<c:set var="wagonDetailMaintenance" scope="page" value="${admin_WagonDetailForm.wagonDetailMaintenance}" />
		<table class="classicblue" style="border: 1px; padding: 0px; width: 99%;">
			<colgroup>
				<col width="16%">
				<col width="31%">
				<col width="16%">
				<col width="16%">
				<col width="21%">
			</colgroup>
			<thead>
	        <tr>
	            <th><fmt:message key="wagon.maintType"/></th> 
	            <th><fmt:message key="wagon.maintTypeDescription"/></th> 
	            <th><fmt:message key="wagon.lastDate"/></th>
	            <th><fmt:message key="wagon.nextDate"/></th> 
	            <th><fmt:message key="wagon.place"/></th>
			</tr>
	        </thead>
			<tbody>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG10.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG10.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG10.lastDate" name="wagonDetailMaintenance.maintenanceBeanG10.lastDate" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG10.nextDate" name="wagonDetailMaintenance.maintenanceBeanG10.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBeanG10.selectedShipto" property="wagonDetailMaintenance.maintenanceBeanG10.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG20.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG20.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG20.lastDate" name="wagonDetailMaintenance.maintenanceBeanG20.lastDate" value=""/>
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG20.nextDate" name="wagonDetailMaintenance.maintenanceBeanG20.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBeanG20.selectedShipto" property="wagonDetailMaintenance.maintenanceBeanG20.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG30.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG30.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG30.lastDate" name="wagonDetailMaintenance.maintenanceBeanG30.lastDate" value=""/>
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG30.nextDate" name="wagonDetailMaintenance.maintenanceBeanG30.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBeanG30.selectedShipto" property="wagonDetailMaintenance.maintenanceBeanG30.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG40.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG40.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG40.lastDate" name="wagonDetailMaintenance.maintenanceBeanG40.lastDate" value=""/>
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG40.nextDate" name="wagonDetailMaintenance.maintenanceBeanG40.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBeanG40.selectedShipto" property="wagonDetailMaintenance.maintenanceBeanG40.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG42.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG42.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG42.lastDate" name="wagonDetailMaintenance.maintenanceBeanG42.lastDate" value=""/>
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG42.nextDate" name="wagonDetailMaintenance.maintenanceBeanG42.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBeanG42.selectedShipto" property="wagonDetailMaintenance.maintenanceBeanG42.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG43.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG43.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG43.lastDate" name="wagonDetailMaintenance.maintenanceBeanG43.lastDate" value=""/>
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG43.nextDate" name="wagonDetailMaintenance.maintenanceBeanG43.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBeanG43.selectedShipto" property="wagonDetailMaintenance.maintenanceBeanG43.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG48.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanG48.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG48.lastDate" name="wagonDetailMaintenance.maintenanceBeanG48.lastDate" value=""/>
					</td>
					<td>
						<input type = "text" id="maintenanceBeanG48.nextDate" name="wagonDetailMaintenance.maintenanceBeanG48.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBeanG48.selectedShipto" property="wagonDetailMaintenance.maintenanceBeanG48.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBean3M.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBean3M.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBean3M.lastDate" name="wagonDetailMaintenance.maintenanceBean3M.lastDate" value=""/>
					</td>
					<td>
						<input type = "text" id="maintenanceBean3M.nextDate" name="wagonDetailMaintenance.maintenanceBean3M.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBean3M.selectedShipto" property="wagonDetailMaintenance.maintenanceBean3M.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanP5.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanP5.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanP5.lastDate" name="wagonDetailMaintenance.maintenanceBeanP5.lastDate" value=""/>
					</td>
					<td>
						<input type = "text" id="maintenanceBeanP5.nextDate" name="wagonDetailMaintenance.maintenanceBeanP5.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBeanP5.selectedShipto" property="wagonDetailMaintenance.maintenanceBeanP5.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				<tr>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanC.maintTypeCd}" />
					</td>
					<td>
						<c:out value="${wagonDetailMaintenance.maintenanceBeanC.maintTypeTx.txt}" />
					</td>
					<td>
						<input type = "text" id="maintenanceBeanC.lastDate" name="wagonDetailMaintenance.maintenanceBeanC.lastDate" value=""/>
					</td>
					<td>
						<input type = "text" id="maintenanceBeanC.nextDate" name="wagonDetailMaintenance.maintenanceBeanC.nextDate" value=""/>
					</td>
					<td>
						<html:select size="1" styleId="maintenanceBeanC.selectedShipto" property="wagonDetailMaintenance.maintenanceBeanC.sapBpId" style="width: 200;" styleClass="${defaultTextfieldStyleClass}">
						<html:option value="-1">-</html:option>
						<c:forEach var="row" items="${wagonDetailMaintenance.repairShiptoList}">
							<html:option value="${row.sapBpId}"><c:out value="${row.name1}"/></html:option>
						</c:forEach>
						</html:select>
					</td>
				</tr>
				
			</tbody>
		</table>