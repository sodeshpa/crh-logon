<%@ include file="../includeTLDs.jsp" %>

<table style="width: 100%;">
    <colgroup>
        <col width="10%">
        <col width="9%">
        <col width="9%">
        <col width="9%">
        <col width="9%">
        <col width="9%">
        <col width="9%">
        <col width="9%">
        <col width="9%">
        <col width="18%">
    </colgroup>
    <tr>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.date"/>
        </td>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.pool"/>
        </td>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.execCarrier"/>
        </td>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.truckType"/>
        </td>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.deliveryType"/>
        </td>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.transCat"/>
        </td>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.companycode"/>
        </td>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.plant"/>
        </td>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.shippingPoint"/>
        </td>
        <td class="tinylabel" rowspan="2">
            <table cellspacing="0" cellpadding="0" style="margin: 0px; padding:0px; border: 0px;">
                <tr>
                    <td>
                        <input id="buttonRefresh" type="button" class="SmallButtonStyle" value="Refresh" />
                    </td>
                </tr>
            </table>
        </td>
    </tr>
    <tr>
        <td>
            <input type="text" id="selectedDayDat" />                          
        </td>
        <td>
            <input type="text" id="poolId" />                          
        </td>
        <td>
            <input type="text" id="carrierIds" />                                                   
        </td>
        <td>
            <input type="text" id="truckTypesIds" />
        </td>
        <td>
            <input type="text" id="deliveryTypeIds" />  
        </td>
        <td>
            <input type="text" id="transCatId" />  
        </td>
        <td>
            <input type="text" id="companycode" /> 
        </td>
        <td>
            <input type="text" id="plantId" />                              
        </td>
        <td>
            <input type="text" id="shippingPointIds" />                                                          
        </td>
    </tr>
    <tr>
        <td class="tinylabel">
            <fmt:message key="chartsearch.main.field.productType"/>
        </td>
        <td>
            <fmt:message key="chartsearch.main.field.wagonTrspCat" />
        </td>
        <td>
            <fmt:message key="chartsearch.main.field.wagonType" />
        </td>
        <td>
            <fmt:message key="chartsearch.main.field.chartType" />
        </td>
        <td colspan="6">&nbsp;</td>
    </tr>
    <tr>
        <td>
            <input type="text" id="productTypeIds" />                                                                                  
        </td>
        <td>
            <input type="text" id="wagonTrspCat" /> 
        </td>
        <td>
            <input type="text" id="wagonType" /> 
        </td>
        <td>
            <input type="text" id="chartType" /> 
        </td>
        <td colspan="6">&nbsp;</td>
    </tr>
</table>