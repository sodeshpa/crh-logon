<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: TableEditor.jsp,v $ --%>

<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/TableBrowserAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/tableview-util.js'/>"></script>

<c:set var="tableForm" value="${admin_TableViewForm}" scope="request"/>
<c:set var="action" value="/tablebrowser/TableView" scope="request"/>
<c:set var="tableViewUrl">
    /../admin/tablebrowser/TableView.do
</c:set>

<c:if test="${referenceSearch}">
	<c:set var="tableForm" value="${admin_ReferenceSearchForm}" scope="request"/>
	<c:set var="action" value="/tablebrowser/ReferenceSearch" scope="request"/>
</c:if>
<html:form action="${action}" enctype="multipart/form-data" method="post" styleId="tableForm">
<script type="text/javascript">
function setEmptyText() {
var table = '<c:out value="${tableForm.table.name}"/>';
if(table != '') {
	if(window.document.forms['admin_TableViewForm']){
		window.document.forms['admin_TableViewForm'].tableSelectionDropdown.value = table;
	}
}
return '<bean:message key="tablebrowser.selectTable"/>';
}

function doFilter() {
	form = Ext.getDom('tableForm');
	whereClause = form.whereClause.value;
	hidePopup();
	var params = { 'whereClause' : whereClause}; 
	showPopup('ReferenceSearch.do?','', params);
}

function doFilterOnEnter(e)
{
	var key;
	if(window.event)
		key = window.event.keyCode;     //IE
	else
		key = e.which;     //firefox
	if(key == 13)
	{
		doFilter();
		return false;
	}
	else
		return true;
}

function deleteRow(id, value)
{
	Ext.MessageBox.confirm('<fmt:message key="orderdetail.messagebox.confirm"/>',
			'<fmt:message key="tablebrowser.confirm.delete"/>' + value, function(btn) {
		if (btn == 'yes') {
			var diva = Ext.get('testDiv');
			diva.load( {
                url :'<c:url value="${tableViewUrl}"/>',
                params : {'rowNr' : id, 'deleteRow':''},
                text :'<fmt:message key="shippingpoint.text.exjsloading"/>',
                scripts :true
			});
		}
	});
}
</script>
<c:if test="${!referenceSearch}">
	<jsp:include page="../SubTab10Tile.jsp"/>
</c:if>   
<table width="100%">
		<tbody>
			<c:if test="${referenceSearch && tableForm.table != null}">
				<th>
					<div class="labelblue"> <c:out value="${tableForm.table.name}"/></div>
				</th>
			</c:if>
			<tr>
			  	<c:if test="${!referenceSearch}">
				<td width="300">
					<div>
    					<input type="text" id="tableSelectionDropdown" size="50" />
					</div>
					<html:hidden property="selectedTable" value=""/>
				</td>
  				</c:if>
				<td width="50"/>
				<c:if test="${tableForm.table != null}">
				<c:if test="${referenceSearch}">
					<nobr>
					<bean:message key="tablebrowser.filter"/>
					<html:text property="whereClause" onkeypress="return doFilterOnEnter(event)"></html:text>
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="tablebrowser.filter"/>" onClick="doFilter(); return false;"  >
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="tablebrowser.saveFilter"/>" onClick="hidePopup(); showPopup('ReferenceSearch.do?clearFilter','')">
					</nobr>
				</c:if>
				<c:if test="${!referenceSearch}">
				<td  align="left">
					<bean:message key="tablebrowser.filter"/>
					<html:text property="whereClause"></html:text>
					<html:submit property="filter" styleClass="ViewButtonStyle">
					<bean:message key="tablebrowser.filter"/></html:submit>
					<html:submit property="clearFilter" styleClass="ViewButtonStyle">
					<bean:message key="tablebrowser.saveFilter"/></html:submit>
				</td>
				</c:if>
				</c:if>
			</tr>
			</table>
			<table width="100%">
			<c:if test="${tableForm.table == null}">
			<tr>
				<td>
					<bean:message key="tablebrowser.noTableSelected"/>
				</td>
			</tr>
			<tr>
				<br>
			</tr>
			<tr>

			<c:set var="colCount" value="5"/>
			<c:set var="rowCount" value="${tableForm.tableListRowCount}"/>
			<c:choose>
				<c:when test="${((tableForm.tableCount > colCount) && (tableForm.tableCount%colCount != 0))}">
					<c:set var="longColLimit" value="${tableForm.tableCount%colCount}"/>
				</c:when>
				<c:otherwise>
					<c:set var="longColLimit" value="0"/>
				</c:otherwise>
			</c:choose>		
			<c:set var="rowNum" value="0"/>
			<c:set var="colNum" value="0"/>
			<c:set var="n" value="0"/>
			
			<c:forEach var="i" begin="0" end="${tableForm.tableCount-1}" varStatus="status">
				<td>
					<html:link page="/tablebrowser/TableView.do?selectedTable=${tableForm.tableNames[n]}">
		        		<c:out value="${tableForm.tableNames[n]}"/>
	        		</html:link>
	        		<c:choose>
						<%-- See if the current column is longer than the others, and figure out the next element based on that --%>
						<c:when test="${colNum < longColLimit}">
							<c:set var="n" value="${n+(rowCount+1)}"/>
						</c:when>
						<c:otherwise>
							<c:set var="n" value="${n+(rowCount)}"/>
						</c:otherwise>
					</c:choose>
				</td>
				<c:set var="colNum" value="${colNum+1}"/>
				<c:if test="${(i+1) % colCount == 0}">
					</tr>
					<tr>
					<c:set var="rowNum" value="${rowNum+1}"/>
					<c:set var="colNum" value="0"/>
					<c:set var="n" value="${rowNum}"/> 
			    </c:if> 
			</c:forEach>

			</tr>
			</c:if>		
		</tbody>
</table>



<c:if test="${tableForm.table != null}">
	<table>
		<tr>
			<td colspan="2" style="width: 100%">
				<tiles:insert page="TableTile.jsp"/>
			</td>
		</tr>
		<tr>
			<td colspan="2">
				<table>
					<tr>
						<td style="width:24px;">
							<c:if test="${tableForm.insertAllowed && !referenceSearch}">
								<html:link page="/tablebrowser/TableView.do?addRow"><img src="<crh-common:url value='/images/add.gif'/>" border="0" alt=""></html:link>
							</c:if>
						</td>
						<td style="width: 24px;">
							<c:if test="${tableForm.pageNr > 0}">
								<c:if test="${referenceSearch}">
									<img src="<crh-common:url value='/images/arrow_left.gif'/>" onclick="hidePopup(); showPopup('ReferenceSearch.do?prev','')"/>
								</c:if>
								<c:if test="${!referenceSearch}">
									<html:link page="${action}.do?prev"><img src="<crh-common:url value='/images/arrow_left.gif'/>" border="0" alt=""></html:link>
								</c:if>
							</c:if>
						</td>
						<td style="width: 24px;">
							<c:if test="${!tableForm.lastPage}">
								<c:if test="${referenceSearch}">
									<img src="<crh-common:url value='/images/arrow_right.gif'/>" onclick="hidePopup(); showPopup('ReferenceSearch.do?next','')"/>
								</c:if>
								<c:if test="${!referenceSearch}">
									<html:link page="${action}.do?next"><img src="<crh-common:url value='/images/arrow_right.gif'/>" border="0" alt=""></html:link>
								</c:if>
							</c:if>
						</td>
					</tr>
				</table>
			</td>
		</tr>
		<c:if test="${!referenceSearch}">
			<tr>
				<td colspan="2" style="padding-top:10px;">
					<div style="float: left;">
						<html:submit property="export" styleClass="ViewButtonStyle"><bean:message key="tablebrowser.csvExport"/></html:submit>
						&nbsp;
					</div>
					<div id="importFormLocation" style="float: left;"/>	
				</td>
			</tr>
		</c:if>
		<c:if test="${referenceSearch}">
			<tr>
				<td colspan="2" class="buttons left">
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				</td>
			</tr>
		</c:if>
	</table>
	<table style="width: 600px;">
		<tr>
			<td>
				<i><c:out value="${tableForm.table.tableInfo.comments}"/></i>
			</td>
		</tr>
	</table>
</c:if>
</html:form>

<%-- 
	https://issues.apache.org/struts/browse/STR-1569
	FormFile is not serializable, that's why a new request-scoped form is created to hold the FormFile object.
	See also https://aww.adnovum.ch/ticket/ADN43669
 --%>
<c:if test="${tableForm.table != null and !referenceSearch and tableForm.insertAllowed}">
	<html:form action="/tablebrowser/FileImport" enctype="multipart/form-data" method="post" styleId="importForm">
		<html:submit property="import" styleClass="ViewButtonStyle">Import CSV</html:submit>
		<html:file name="admin_FileImportForm" property="csvImportFile"/>
	</html:form>

	<script type="text/javascript">
	Ext.onReady(function() {
		var importFormLocation = Ext.get('importFormLocation');
		var importForm = Ext.get('importForm');
		if ( (importForm) && (importFormLocation) ) {
			importForm.setX(importFormLocation.getX());
			importForm.setY(importFormLocation.getY());
		}
	});
	</script>
</c:if>
<%-- END $RCSfile: TableEditor.jsp,v $ --%>
