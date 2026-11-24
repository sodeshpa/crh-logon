<%@ include file="../includeTLDs.jsp" %>


<%@page import="com.crh.logon.persistence.entities.common.PdaDeviceEntity"%>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>
<script type="text/javascript" src="<crh-common:url value='../js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/PDAAdminAjaxService.js'/>"></script>

<script type="text/javascript">

	var carrierJsonData = <c:out value="${admin_PDAAdminForm.json['carrierList']}" escapeXml="false"/>;

    Ext.onReady(function () {
        // --- Carrier ---
        var carrierCombo2 = new Ext.ux.TwinCombo({
            id: 'carrierCombo2',
            applyTo: 'carrierIdOfPDA',
            selectOnFocus: true,
            editable: true,
            typeAhead: true,
            forceSelection: true,
            triggerAction: 'all',
            store: new Ext.data.JsonStore({
                fields: ['carrierId', 'displayName'],
                data : carrierJsonData
            }),
            mode: 'local',
            width: 180,
            listWidth: 250,
            valueField: 'carrierId',
            displayField: 'displayName',
            hiddenName: 'carrierId'
         });


        var lastRegistrationDate = new Ext.form.DateField({
            id: 'lastRegistrationDate',
            format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
            altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
            applyTo: 'lastRegistrationDateStr',
            allowBlank: true
        });

        var lastRegistrationTime = new Ext.form.TimeField({
            id: 'lastRegistrationTime',
            increment: 15,
            format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>',
            applyTo: 'lastRegistrationTimeStr',
            allowBlank: true
        });

		   //sim card - phone number search boxes

	   		var simCardAndPhoneNrRecordType = Ext.data.Record.create([
           	    {name: 'simcardNumber', mapping: 'simcardNumber'},
           	    {name: 'phoneNumber', mapping: 'phoneNumber'}, 
           	    {name: 'simcardCd', mapping: 'simcardCd'}    
           	]);

	   		var simCardAndPhoneNrListBySimStore = new Ext.data.Store({
	   	    	proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { PDAAdminAjaxService.fetchSimCardList(skip, count, sort, query, true, callback) }, true),
	   	    	reader: new Ext.data.ListRangeReader({id:'simcardNumber', totalProperty:'totalSize'}, simCardAndPhoneNrRecordType)
	   		});
	   		
	   		var simCardAndPhoneNrListByPhoneNrStore = new Ext.data.Store({
	   			proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { PDAAdminAjaxService.fetchSimCardList(skip, count, sort, query, false, callback) }, true),
	   	    	reader: new Ext.data.ListRangeReader({id:'phoneNumber', totalProperty:'totalSize'}, simCardAndPhoneNrRecordType)
	   		});


	   	    // Custom rendering Template
	   	    var resultTpl = new Ext.XTemplate(
	   	    	'<table class="classic"><thead><tr>',
	   	    	'<th width="100"><fmt:message key="pdaadmin.field.simcardNumber"/></th>',
	   	    	'<th width="100"><fmt:message key="pdaadmin.field.phoneNumber"/></th>',
	   	    	'</tr></thead><tbody>',
	   	        '<tpl for="."><tr  class="listitem zebra{[xindex % 2]}">',
	   	        '<td>{simcardNumber}</td>',
	   	        '<td>{phoneNumber}</td>',
	   	        '</tr></tpl>',
	   	        '</tbody></table>'
	   	    );

	   	    
	   	    var searchSimCardAndPhoneNrBySimNr = new Ext.form.ComboBox({
	   	        store: simCardAndPhoneNrListBySimStore,
	   	        displayField:'simcardNumber',
	   	        valueField:'',
	   	        typeAhead: false,
	   	        loadingText: '<fmt:message key="dispokarte.texte.shiptosearching"/>',
	   	        maxHeight:800,
	   	        minChars:1,
	   	        pageSize:20,
	   	        hideTrigger:true,
	   	        tpl: resultTpl,
	   	        applyTo: 'searchSimCardAndPhoneNrBySimNr',
	   	        itemSelector: 'tr.listitem',
	   	        onSelect: function(record){
	   	        	Ext.get('searchSimCardAndPhoneNrBySimNr').dom.value = record.data.simcardNumber;
	   	        	Ext.get('searchSimCardAndPhoneNrByPhoneNr').dom.value = record.data.phoneNumber;
	   	        	Ext.get('simcardCd').dom.value = record.data.simcardCd;
	   	        	searchSimCardAndPhoneNrBySimNr.collapse();
	   	      		//submitWithActionToDiv('selectSoldTo', 'admin_UserAdminFormUserAdmin', 'user-details-div');
	   	        }
	   	    });
	   	    
	   	    var searchSimCardAndPhoneNrByPhoneNr = new Ext.form.ComboBox({
	   	        store: simCardAndPhoneNrListByPhoneNrStore,
	   	        displayField:'phoneNumber',
	   	        typeAhead: false,
	   	        loadingText: '<fmt:message key="dispokarte.texte.shiptosearching"/>',
	   	        maxHeight:800,
	   	        minChars:1,
	   	        pageSize:20,
	   	        hideTrigger:true,
	   	        tpl: resultTpl,
	   	        applyTo: 'searchSimCardAndPhoneNrByPhoneNr',
	   	        itemSelector: 'tr.listitem',
	   	        onSelect: function(record){
	   	        	Ext.get('searchSimCardAndPhoneNrBySimNr').dom.value = record.data.simcardNumber;
	   	        	Ext.get('searchSimCardAndPhoneNrByPhoneNr').dom.value = record.data.phoneNumber;
	   	        	Ext.get('simcardCd').dom.value = record.data.simcardCd;
		   	        	searchSimCardAndPhoneNrByPhoneNr.collapse();
	   	      		//submitWithActionToDiv('selectSoldTo', 'admin_UserAdminFormUserAdmin', 'user-details-div');
	   	        }
	   	    });

		// Input Buttons
		var submitWithAction = function(action) {
			var form = Ext.getDom('admin_PDAAdminForm');
			Ext.query("input[name=inputAction]", form)[0].value = action;
			form.submit();
		};

		Ext.get('btn_cancel').on('click', function() { submitWithAction('cancel') });
		Ext.get('btn_reset').on('click', function() { submitWithAction('reset') });
		Ext.get('btn_save').on('click', function() { submitWithAction('save') });
		Ext.get('btn_ok').on('click', function() { submitWithAction('ok') });

		var btn_verify = Ext.get('btn_verify');
		if (btn_verify != null) {
			btn_verify.on('click', function() { submitWithAction('verify') });
		}
	});
</script>
<div class="customer_max">
<html:form action="/pdaadmin/PDAAdmin.do" styleId="admin_PDAAdminForm">
<html:hidden property="inputAction" value=""/>
<html:hidden property="simcardCd" styleId="simcardCd" value="${admin_PDAAdminForm.simcardCd}"/>
 <!-- beginn Border-Table   -->
       <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
    	<tr>
    		<td>
    			<jsp:include page="../SubTab10Tile.jsp"/> 
				<table class="big_form2">
			    	<tr class="classicblue">
      					<th class="classicblue">
      						<fmt:message key="tabitem.admin"/> / <fmt:message key="tabsubitem.10.1"/> / <fmt:message key="tabsubitem.10.1.3"/>
      					</th>
      				</tr>
					<tr>
						<td>
						<jsp:include page="../Sub1Tab10Tile.jsp"/>
			<table width="100%" class="classicblue" style="margin-top:4px">
			<tr>
			<td>
			<table>
			<tbody>
			<tr>
				<td valign="bottom" colspan="2">
					<p class="subtitle1"><fmt:message key="pdaadmin.texte.pdainfo"/></p>
				</td>
			</tr>
			<tr>
					<td class="left"><nobr><fmt:message
						key="pdaadmin.field.activeFlag" /></nobr></td>
					<td>
						<nobr>
							<html:checkbox
								property="activeFlag" />
						</nobr>
					</td>
				</tr>
				<tr>
					<td class="left"><nobr><fmt:message
						key="pdaadmin.field.pdaDeviceNo" /></nobr></td>
					<td>
					<html:text style="width:130px;" styleId="pdaDeviceId" property="pdaDeviceNo"  maxlength="<%=PdaDeviceEntity.DBSIZE_PDA_DEVICE_NO + ""%>"/>
				</tr>
				<tr>
					<td class="left"><nobr><fmt:message
						key="pdaadmin.field.carrier" /></nobr></td>
					<td>
					<html:text style="width: 120px" property="carrierId" styleId="carrierIdOfPDA"/>
				</tr>						
				<tr>
					<td class="left"><nobr><fmt:message
						key="pdaadmin.field.typeDescription" /></nobr></td>
					<td>
					<html:text style="width: 120px" property="typeDescription" styleId="typeDescription"  maxlength="<%=PdaDeviceEntity.DBSIZE_TYPE_DESCRIPTION + ""%>"/>
				</tr>						
				<tr>
					<td class="left"><nobr><fmt:message
						key="pdaadmin.field.inventoryNumber" /></nobr></td>
					<td>
					<html:text style="width: 120px" property="inventoryNumber" styleId="inventoryNumber" maxlength="<%=PdaDeviceEntity.DBSIZE_INVENTORY_NUMBER + ""%>"/>
				</tr>						
				<tr>
					<td class="left"><nobr><fmt:message key="pdaadmin.field.simcardNumber" /></nobr></td>
					<td>
					<html:text style="width: 300px" property="simcardNumber" styleId="searchSimCardAndPhoneNrBySimNr"/>
				</tr>						
				<tr>
					<td class="left"><nobr><fmt:message key="pdaadmin.field.phoneNumber" /></nobr></td>
					<td>
					<html:text style="width: 300px" property="phoneNumber" styleId="searchSimCardAndPhoneNrByPhoneNr"/>
				</tr>						
				<tr>
					<td class="left"><nobr><fmt:message
						key="pdaadmin.field.lastRegistrationDate" /></nobr></td>
					<td>
					<html:text style="width: 120px" property="lastRegistrationDateStr" styleId="lastRegistrationDateStr"/>
					<html:text style="width: 120px" property="lastRegistrationTimeStr" styleId="lastRegistrationTimeStr"/>
				</tr>						
				<tr>
					<td class="left"><nobr><fmt:message
						key="pdaadmin.field.softwareVersion" /></nobr></td>
					<td>
					<html:text style="width: 120px" property="softwareVersion" styleId="softwareVersion" maxlength="<%=PdaDeviceEntity.DBSIZE_SOFTWARE_VERSION + ""%>"/>
				</tr>
				</tbody>
				</table>
				</td>
				</tr>						
		</table>
		<table class="big_form2" style="margin-top: 8px;">
			<tr>
				<td class="buttons right">
					<input type="button" id="btn_cancel" class="ViewButtonStyle" value='<fmt:message key="buttons.cancel"/>'>
					<input type="button" id="btn_reset" class="ViewButtonStyle" value='<fmt:message key="buttons.reset"/>'>
					<input type="button" id="btn_save" class="ViewButtonStyle" value='<fmt:message key="buttons.save"/>'>
					<c:if test="${admin_PDAAdminForm.deviceFunctionalityCheckState != 0}">
						<input type="button" id="btn_verify" class="ViewButtonStyle" value='<fmt:message key="pdaadmin.buttons.veriyPDADecviceFunc"/>'>
					</c:if>
					<input type="button" id="btn_ok" class="ViewButtonStyle" value='<fmt:message key="buttons.ok"/>'>
				</td>
			</tr>
		</table>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
	</html:form>
	</div>