<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript" src="<crh-common:url value='/dwr/interface/ManualPoolAssignmentAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.CheckColumn.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.valid.ValidationMessages.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.EditableList.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.grid.PagingToolbar.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.data.OriginalRecordStore.js'/>"></script>

<jsp:include page="../SubTab7Tile.jsp"/> 

<html:form action="bestellung/TruckPlanningEntry.do" styleId="truckPlanningForm">
<table class="big_form2">
    <tr class="classicblue">
        <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.7.3"/></th>
    </tr>
    <tr>
        <td>
            <div id="divChartLoadMask">
                <div id="divChart" style="width: 100%; height: 100%">
                	<jsp:include page="../bestellung/Chart.jsp" flush="true">
                        <jsp:param name="filterPage" value="truckPlanning" />
                        <jsp:param name="imgWidth" value="900" />
                        <jsp:param name="imgHeight" value="700" />
                        <jsp:param name="checkboxWidth" value="120" />
                    </jsp:include>
                </div> 
            </div>
        </td>
    </tr>
</table>
</html:form>