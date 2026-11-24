<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: DispocardViewTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@ page import="com.crh.logon.admin.web.ajax.DispocardViewAjaxService"%>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/DispocardViewAjaxService'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/logon-validation.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.CheckComboBox.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.form.PeriodSelector.js'/>"></script>

<body>

<jsp:include page="../SubTab9Tile.jsp"/> 

<script type="text/javascript">
    Ext.onReady(function(){
    
        // --- Filters ---
        <jsp:include page="DispocardSearchFiltersInc.jsp" />      
    
        // --- Result list ---
       <jsp:include page="DispocardSearchResultGridInc.jsp" />
    
        // --- Initial data load ---
        dispocardSearchStore.setDefaultSort('<c:out value="${defaultSort}"/>', '<c:out value="${defaultSortDir}"/>');
        searchArea.searchDefaults();
        
        // --- Buttons ---   
        Ext.get('buttonPreview').on('click', searchArea.searchDefaults.createDelegate(searchArea, []));
        
    	Ext.get('buttonExport').on('click', function() {
    		var url = 'DispocardView.do?export=true';
    		var crits = searchArea.getSearchCrits();
    
    		if (crits.sapMaterialId) {
    			url += '&sapMaterialId=' + crits.sapMaterialId;
    		}
    		if (crits.deliveryTypeCdList) {
    			url += '&deliveryTypeCdList=' + crits.deliveryTypeCdList;
    		}
    		if (crits.poolId) {
    			url += '&poolId=' + crits.poolId;
    		}
    		if (crits.commCarrierId) {
    			url += '&carrierId=' + crits.commCarrierId;
    		}
    		if (crits.plantCd) {
    			url += '&plantCd=' + crits.plantCd;
    		}
    
    		window.open(url);
    	});
    });
    
</script>
<table class="big_form2">
    <tr class="classicblue">
        <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.8"/></th>
    </tr>
    <tr>
        <td>
            <table id="searchTable" cellpadding="0" cellspacing="0" class="big_form2" style="margin: 0px; border-collapse: collapse;">
                <tr>
                    <td class="border" style="background-color: F9F9F9; padding: 2px 0 0 10px;">
                        <div id="divSimpleSearch">
                            <table style="width: 100%;">
                                <colgroup>
                                    <col width="16%">
                                    <col width="14%">
                                    <col width="14%">
                                    <col width="14%">
                                    <col width="14%">
                                    <col width="28%">
                                </colgroup>
                                <tr>
                                    <td class="tinylabel">
                                        <fmt:message key="dispocardsearch.main.field.produkt"/>
                                    </td>
                                    <td class="tinylabel">
                                        <fmt:message key="dispocardsearch.main.field.pool"/>
                                    </td>
                                    <td class="tinylabel">
                                        <fmt:message key="dispocardsearch.main.field.carrier"/>
                                    </td>
                                    <td class="tinylabel">
                                        <fmt:message key="dispocardsearch.main.field.plant"/>
                                    </td>
                                    <td class="tinylabel">
                                        <fmt:message key="dispocardsearch.main.field.delivery.type"/>
                                    </td>
                                    <td rowspan="2" class="tinylabel">
                                        <table cellspacing="0" cellpadding="0" style="margin: 0px; padding:0px; border: 0px;">
                                            <tr>
                                                <td>
                                                    <input id="buttonPreview" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.preview"/>"/><br/>
                                                </td>
                                                <td>
                                                    <input id="buttonExport" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.export"/>"/><br/>
                                                </td>
                                            </tr>
                                        </table>
                                    </td>
                                </tr>
                                <tr>
                            	   <td>
                            	       <input type="text" id="sapMatCombo"/>                       
                            	   </td>
                            	   <td>
                            	       <input type="text" id="poolId"/>                        
                                   </td>
                                   <td>
                                        <input type="text" id="carrierId"/>                     
                                    </td>
                                    <td>
                                        <input type="text" id="plantCd"/>
                                    </td>
                                    <td>
                                        <input type="text" id="deliveryTypeCd"/>
                                    </td>
                                </tr>
                            </table>
                        </div>
                    </td>
                </tr>
            </table>

            <div id="divSearchResults" style="margin-top: 2px"/>

        </td>
    </tr>
</table>
<%-- END $RCSfile: DispocardViewTile.jsp,v $ --%>
