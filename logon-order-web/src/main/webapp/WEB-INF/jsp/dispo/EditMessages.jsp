<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: EditMessages.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<html:form action="dispo/MessagesEditEntry.do" onsubmit="return false;" styleId="MessagesEditForm">

<table class="classicblue" style="border:none; width:auto"  class="big_form2">
<tr>
	<th colspan="2">
		<fmt:message key="dispo.texte.editmessages"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%" class="big_form2" >
		<tr>
			<td>
				<table>
					<tr >
						<table class="border">
							<tr>				
								<td class='label' style='width:1px'>
									<nobr><fmt:message key="dispo.texte.Driver"/> :</nobr>
								</td>
								<td style='width:100%'>
									<c:out value="${order_dispo_MessagesEditForm.driver.user.person.firstname}"/>
									<c:out value="${order_dispo_MessagesEditForm.driver.user.person.name}"/>									
								</td>
							</tr>
						</table>
					</tr>
					<tr>
						<td id="cellMessageList">
						</td>
					</tr>
					<tr>
						<td>
							<table class="border" style="width:600px;table-layout: fixed;">
								<tr>
									<td class="label" style="width:170;px">
										<fmt:message key="dispo.texte.message"/>:
									</td>
									<td style="text-align: right;width:400px" id="MessageSize"></td>
								</tr>
								<tr>
									<td style="height:160px" colspan="2">
										<textarea id="TextareaMessage" style="width:100%;height:150px;display:none"></textarea>
										<div id="DivMessage" class="messagebox" style="width:570;height:150px;padding:3px;margin-right:5px;display: block">&nbsp;
										</div>
									</td>
								</tr>
								<tr>
									<td class="buttons right" colspan="2">
										<c:if test="${order_dispo_MessagesEditForm.canSend}">
											<input id="ButtonNew" type="button" class="SmallButtonStyle" value="<fmt:message key="dispo.texte.newmessage"/>..." onclick="showSend()"/>   				
											<input id="ButtonCancel" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hideSend()" style="display:none;"/>   				
											<input id="ButtonSend" type="button" class="SmallButtonStyle" value="<fmt:message key="dispo.texte.sendmessage"/>" onclick="sendMessage()"  style="display:none;"/>
										</c:if>
										<c:if test="${!order_dispo_MessagesEditForm.canSend}">
											<input id="ButtonNew" type="button" class="SmallButtonStyleDisabled" disabled="true" value="<fmt:message key="dispo.texte.newmessage"/>..."/>   				
										</c:if>
									</td>
								</tr>
							</table>								
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<tr>
			<td class="buttons right">
				<input id="ButtonClose" type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.close"/>" onclick="hidePopup(); refreshAssignments();"/>   				
			</td>
		</tr>
	</table>
</tr>
</table>
</table>

</html:form>

<script type="text/javascript">
	var messgesSearchRec = Ext.data.Record.create([
		{name: "pdaMessageId"},
	    {name: "creTs"},
	    {name: "modTs"},
	    {name: "dispatcherUserId"},
	    {name: "pdaMessageTypeCd"},
	    {name: "message"},
	    {name: "receiverAckFlag"},
	    {name: "dispatcherFirstname"},
	    {name: "dispatcherName"}	    
	]);
	
    var messagesSearchStore = new Ext.data.Store({
        proxy: new Ext.data.DWRProxyForPager(function(skip, count, query, callback) { 
        	DispoAjaxService.searchMessages(skip, count, callback) 
        }, true),
		reader: new Ext.data.ListRangeReader({id:'pdaMessageId', totalProperty:'totalSize'}, messgesSearchRec), 
		remoteSort: true
    });
    
    var pagingBar = new Ext.PagingToolbar({
        pageSize: 10,
        store: messagesSearchStore,
        displayInfo: true,
        displayMsg: '<fmt:message key="dispo.texte.messages"/>  {0} - {1} <fmt:message key="dispo.texte.von"/> {2}',
        emptyMsg: "<fmt:message key="dispo.texte.nomessages"/>..."
    }); 
    
    var grid = new Ext.grid.GridPanel({
    	id: 'searchGrid',
        renderTo:'cellMessageList',
        width: 600,
        height:335,
		viewConfig: {autoFill: true, forceFit: true},        
		stripeRows: true,
        store: messagesSearchStore,
        pageSize: 10,
        loadMask: true,
		selModel: new Ext.grid.RowSelectionModel({singleSelect:true}),
		enableColumnHide:false,
  		enableColumnMove:false,
  		enableColumnResize: false,
  		enableHdMenu: false,		
        // grid columns
        columns:[
        {
            id: 'type', 
            header: "", 
            dataIndex: 'pdaMessageTypeCd',
            width: 30,
            sortable: false,
            renderer: function(val, meta, rec) {
            	var img = ""
            	switch (val){
            		case 1: img = "arrow_right_blue";
            				break;
            		case 2: img = "arrow_left_blue";
            				break;
            		case 3: img = "arrow_left_red";
            				break;
            	}	
            	return  '<img src="' + crhCommonUrl("../images/" +img+ ".gif") + '">';
            }
        },{
            id: 'ts', 
            header: "<fmt:message key="dispo.texte.time"/>", 
            dataIndex: 'creTs',
            align: 'left',
            width: 60,
            sortable: false,
            renderer: function(val, meta, rec) {
            	return  val.format('<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>');
            }
        },{
            id: 'dispatcher', 
            header: "<fmt:message key="dispo.texte.dispatcherUser"/>", 
            dataIndex: 'dispatcherUsestrId',
            width: 200,
            sortable: false,
            renderer: function(val, meta, rec) {
            	return '<nobr>' + (rec.data.dispatcherFirstname?rec.data.dispatcherFirstname:'') + ' ' + (rec.data.dispatcherName?rec.data.dispatcherName:'') + '</nobr>';
            }
        },{
            id: 'ack', 
            header: "<fmt:message key="dispo.texte.msgack"/>",
            dataIndex: 'receiverAckFlag',
            width: 50,
            align: 'center',
            sortable: false,
            renderer: function(val, meta, rec) {
            	return '<input type="checkbox" ' + ((val || rec.data.pdaMessageTypeCd == 1)?'disabled="true" ':'') + (val?'checked':'') + ' onclick="setMessageRead(' + rec.data.pdaMessageId +')">';
            }
            
        },{
            id: 'message', 
            header: "<fmt:message key="dispo.texte.message"/>", 
            dataIndex: 'message',
            width: 300,
            sortable: false
        }
        ],

        // paging bar on the bottom
        bbar: pagingBar
        
    });
    
    pagingBar.changePage(1);
    
    grid.selModel.on('selectionchange', function(event){
    	if (event.getSelected()){
    		Ext.getDom("DivMessage").innerHTML = "<pre>" + event.getSelected().data.message + "</pre>";
    	}
    	else{
    		Ext.getDom("DivMessage").innerHTML = "";
    	}
    });
    
    function showSend(){
    	Ext.get("ButtonNew").setDisplayed("none");
    	Ext.get("ButtonCancel").setDisplayed(true);
    	Ext.get("ButtonSend").setDisplayed(true);
    	Ext.get("TextareaMessage").setDisplayed(true);
    	Ext.get("DivMessage").setDisplayed("none");
    	showMsgSize();
    }
    
    function hideSend(){
    	Ext.get("ButtonNew").setDisplayed(true);
    	Ext.get("ButtonCancel").setDisplayed("none");
    	Ext.get("ButtonSend").setDisplayed("none");
    	Ext.get("TextareaMessage").setDisplayed("none");
    	Ext.get("TextareaMessage").dom.value="";
    	Ext.get("DivMessage").setDisplayed(true);
    	Ext.getDom("MessageSize").innerHTML = "";
    }
    
    function sendMessage(){
    	DispoAjaxService.sendMessage(Ext.getDom("TextareaMessage").value,{ 
    		callback: function(){
    			hideSend();
    			pagingBar.changePage(1);
    		},
    		errorHandler: function(err){
    			Logon.message.error('<fmt:message key="dispo.errors.cantSendMessage"/>');
    		}
    	});
    }

    function setMessageRead(msgId){
    	DispoAjaxService.setMessageRead(msgId, 
    		function(){
    			pagingBar.changePage(pagingBar.field.getValue())
    	});
    }    
    
    var ta = new Ext.form.TextArea({
	    maxLength: 250,
	    applyTo : 'TextareaMessage'
    	});
	ta.on('invalid', function(ta){
		ta.setValue(ta.getValue().substring(0,250));
		showMsgSize();
    }
	);
	ta.on('valid', showMsgSize);

	function showMsgSize(){
		Ext.getDom("MessageSize").innerHTML = 
			Ext.getDom("TextareaMessage").value.length + "/250";
	}
	
    onPopupReady(function() {
        var el = document.getElementById('ButtonClose');
        if (el) {
            el.focus();
        }
    });

</script>


