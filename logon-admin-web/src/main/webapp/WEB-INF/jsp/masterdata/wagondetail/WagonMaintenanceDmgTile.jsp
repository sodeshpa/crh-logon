<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.admin.web.actions.masterdata.WagonDetailAction"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page" value="${admin_WagonDetailForm}" />
<c:set var="wagonDetailMaintenance" scope="page" value="${admin_WagonDetailForm.wagonDetailMaintenance}" />
<%@page import="com.crh.logon.persistence.entities.common.WagonEntity"%>

<script type="text/javascript">
function removeWagonMaintAttachment(id){
		//save with a form
	    form = Ext.getDom('WagonDetailFormId');
		p = {'formAction' :'<%=WagonDetailAction.FORM_ACTION_REMOVE_WAGON_REP_ATT_FILE%>',
			 'selectedTab' :'<%=WagonDetailAction.FORWARD_MAINTENANCE_TAB%>',
			 'wagonDetailMaintenance.uploadBean.wagonRepAttId': id
			};
		addFormElements(p, form);
			
		var diva = Ext.get('emailAttachments');
		diva.load( {
			url :'WagonDetail.do',
			params : p,
					text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
					scripts :true
				});
	}

function upload() {
	var form = Ext.getDom('WagonDetailFormId');
	fileField = document.getElementById('replaceThisUploadField');
	fileField.name='wagonDetailMaintenance.uploadBean.file';
	fileField.id='wagonDetailMaintenance.uploadBean.file';
	var type = 'emailAttachments';
	Ext.Ajax.request({
		url :'WagonDetail.do',
		form: 'WagonDetailFormId',
		method: 'POST',
		isUpload: true,
		params:{type : type, 'wagonDetailMaintenance.uploadBean.type' : type, 'formAction' :'<%=WagonDetailAction.FORM_ACTION_UPLOAD_WAGON_REP_ATT_FILE%>'},
        success: function(result, request) {
		    clearFileUpload('wagonDetailMaintenance.uploadBean.file');
			p = {'formAction' :'<%=WagonDetailAction.FORM_ACTION_MAINT_TAB_RELOAD_ATTACHMENT_DIV%>', type: type};
			reloadAttachmentDiv(p);
		}
	});

}

function reloadAttachmentDiv(p){
	var diva = Ext.get('emailAttachments');
	diva.load( {
		url :'WagonDetail.do',
		params : p,
				text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
				scripts :true
			});
	return true;
}

function clearFileUpload(name){
	<%--- Why this is so implemented: setFile of the bean throws exception for empty file input--%>
	fileField = document.getElementsByName(name)[0];
	parentNod = fileField.parentNode;
	tmpForm = document.createElement("form");
	tmpForm.id="replaceThisUploadField";
	tmpForm.name="replaceThisUploadField";
	parentNod.replaceChild(tmpForm,fileField);
	tmpForm.appendChild(fileField);
	tmpForm.reset();
	parentNod.replaceChild(fileField,tmpForm);
	fileField.id="replaceThisUploadField";
	fileField.name="replaceThisUploadField";
}

Ext.onReady(function(){
	// --- Common properties for all filter-comboboxes ---
	Ext.form.ComboBox.prototype.shadow = false;
	Ext.form.ComboBox.prototype.emptyText = "Select...";
	Ext.form.ComboBox.prototype.minChars = 1;
	Ext.form.ComboBox.prototype.allowBlank = true;
	Ext.form.ComboBox.prototype.forceSelection = true;
	Ext.menu.Menu.prototype.shadow = false;

	// --- Data record and DataStore definitions ---
	var damageCodeRec = Ext.data.Record.create([
	    {name: "damagecodeCd", mapping: "damagecodeCd"},
	    {name: "txt", mapping: "txt"},
	    {name: "damagecodeTxt", mapping: "damagecodeTxt"}
	]);

	var template = new Ext.XTemplate(
			'<tpl for="."><div class="x-combo-list-item">{damagecodeTxt} <span class="dynlabel">({txt})</span></div></tpl>',
	{
	    compiled: true,
	    disableFormats: true
	}
	);
	
    var damageCodeTagStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, sort, query, callback) { WagonAdminAjaxService.fetchDamageCodeList(skip, count, sort, query, callback) }, true),
		reader: new Ext.data.ListRangeReader({id:'damagecodeCd', totalProperty:'totalSize'}, damageCodeRec)
    });

	// --- damageCode Tag Comboboxes
	damageCodeTagCombo = new Ext.ux.TwinCombo({
    	id: 'wagonDetailMaintenance.damageCode',
        applyTo: 'damageCodeDummy',
        store:  damageCodeTagStore,
        displayField: 'txt',
        valueField: 'damagecodeCd',
        pageSize: 20,
        width: 220,
        listWidth: 330,
        typeAhead: false,
        triggerAction: 'all' ,
        selectOnFocus: true,
        tpl:template
	});

	damageCodeTagCombo.on('select', function(combo, record) {
		var buttSave = document.getElementById("addDamageCdButton");
    	buttSave.disabled = false;
	});
});

function addDamageCd() {
		var damageCd = damageCodeTagCombo.getValue();
		p = {'formAction' :'<%=WagonDetailAction.FORM_ACTION_ADD_DAMAGECODE%>',
				 'selectedTab' :'<%=WagonDetailAction.FORWARD_MAINTENANCE_TAB%>',
				 'wagonDetailMaintenance.damageCode': damageCd
		};
		form = Ext.getDom('WagonDetailFormId');
		addFormElements(p, form);	
		var divp = Ext.get('damageCodeDiv');
		divp.load({
			url :'WagonDetail.do',
			text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
			scripts: true,
			params: p,
			callback:function(){
				damageCodeTagCombo.clearValue();			
				var buttSave = document.getElementById("addDamageCdButton");
		    	buttSave.disabled = true;
				}
		});
}

function removeDamageCd(damageCd) {
	p = {'formAction' :'<%=WagonDetailAction.FORM_ACTION_REMOVE_DAMAGECODE%>',
			 'selectedTab' :'<%=WagonDetailAction.FORWARD_MAINTENANCE_TAB%>',
			 'wagonDetailMaintenance.damageCode': damageCd
	};
	form = Ext.getDom('WagonDetailFormId');	
	var divp = Ext.get('damageCodeDiv');
	divp.load({
		url :'WagonDetail.do',
		text: '<fmt:message key="shippingpoint.text.exjsloading"/>',
		scripts: true,
		params: p
	});
}

</script>

<%----------------------------------------------------------------------------------------------------------%>
<%----------------------------------------EMAIL DIV----------------------------------------------------------------%>
<%-------------------------------------------------------------------------------------------------------------%>	

<table border="0" cellspacing="0" cellpadding="0" width="100%">

<tr>
<td>


<table border="0" cellspacing="0" cellpadding="0" width="100%">
	<colgroup>
		<col width="50%">
		<col width="50%">
	</colgroup>
	<tbody>
	<tr>
		<td rowspan="3">
<span class="labelblue"><fmt:message
				key="wagondetail.maint.label.email.title" /> </span>
		<table class="classic" cellpadding="0" cellspacing="0">
			<colgroup>
				<col width="30%">
				<col width="70%">
			</colgroup>
			<tbody>
				<tr>
					<td colspan="2"><span class="label"><nobr><fmt:message
						key="wagondetail.maint.label.email.text" /></nobr></span></td>
				</tr>
				<tr>
					<td colspan="2">
					<c:set var="textarealength"><%=WagonEntity.DBSIZE_WAGON_NOTE_MAINTENANCE + ""%></c:set>
					<html:textarea styleClass="textfield"
						styleId="wagonDetailMaintenance.wagonNoteMaintenance" rows="11" style="width:100%;"
						property="wagonDetailMaintenance.wagonNoteMaintenance"
						onkeypress="return checkMaxLength(event, this, ${textarealength})"/>
					</td>
				</tr>
			</tbody>
		</table>
		</td>
	</tr>
	<tr>
	<td>

<%----------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- DAMAGECODE-----------------------------------------------------%>
<%-------------------------------------------------------------------------------------------------------------%>
<span class="labelblue"><fmt:message
				key="wagondetail.maint.label.damagecode.title" /></span>
                          <div class="subformlist" id="damageCodeDiv" style="height:40px;clear:both;font-size:12px;">
		<jsp:include page="WagonMaintenanceDmgCodeTile.jsp"></jsp:include>
		</div>
		<div style="margin-top:4px;width:98%">
		<table class="noborder" style="vertical-align: top">
		<tr>
		<td>
					<input type="text" id="damageCodeDummy"  value="" />
				</td>
				<td>
					<input id="addDamageCdButton" disabled type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="addDamageCd()"/>
				</td>
				</tr>
			</table>
		</div>

	</td>
	</tr>
	<tr>
	<td>


<%----------------------------------------------------------------------------------------------------------%>
<%---------------------------------------- ATTACHMENT E-MAIL-------------------------------------------------%>
<%----------------------------------------------------------------------------------------------------------%>

		<span class="labelblue"><fmt:message
				key="wagondetail.maint.label.email.upload.title" /> </span> <br /><nobr>
									<input type="file" id="replaceThisUploadField"/>
									<input type="button" value='<fmt:message key="buttons.upload"/>' class="ViewButtonStyle"  onclick="upload();" /></nobr>
									
		<div id="emailAttachments" class="scrollist" style="margin-top:4px;height: 40px;">
					<c:set var="attachmentTypeDiv" scope="request"><%=Constant.WAGON_MAINT_ATTACHMENT_TYPE_EMAIL %></c:set>
					<jsp:include page="WagonMaintenanceDmgAttachmentTile.jsp"></jsp:include>
                        </div>                          
           	
	</td>
	</tr>	
	</tbody>
	</table>
	
	</td>
	</tr>
	</table>
