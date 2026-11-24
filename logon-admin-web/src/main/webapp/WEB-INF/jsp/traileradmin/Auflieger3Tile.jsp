<%@ include file="../includeTLDs.jsp" %>
<script type="text/javascript" src="<crh-common:url value='/js/logon-gmap-common.js'/>"></script>
<script type="text/javascript">
GMapAPILoader.onAPIReady( function() {

	load_map('<c:out value="${admin_TrailerAdminForm.lastPosition[0]}"/>', '<c:out value="${admin_TrailerAdminForm.lastPosition[1]}"/>');
});

Ext.onReady(function(){
		
	GMapAPILoader.load();	
});
</script>
<table width="100%">
<tr>
<td align="center">      
                       <div id="map" style="height:320px;width:320px;padding-bottom:10px;overflow:hidden" class="border">map</div>
                       </td>
                       </tr>
                       <tr>
                       <td>
                       <span class="label"><br>
                          <fmt:message key="roadmasterdata.fields.coordinates"/>
                       </span>
                         <br>
                          <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="margin-top:4px; padding:0px;">
                            <colgroup>
                              <col width="1%">
                              <col width="48%">
                              <col width="49%">
                            </colgroup>
                            <tr>
                              <td valign="top"  class="formtitle" style="white-space:nowrap;border:0px; vertical-align:middle; color:#FF1100; font-weight:bold;padding:0px;"><fmt:message key="roadmasterdata.fields.lastknown"/></td>
                              <td><input disabled type="text" class="bordertextfield" style="width:100%;" value="<c:out value="${admin_TrailerAdminForm.lastPosition[0]}"/>" size="4" maxlength="20" id="lastPosition0" name="lastPosition0"></td>
                              <td ><input disabled type="text" class="bordertextfield" style="width:100%;" value="<c:out value="${admin_TrailerAdminForm.lastPosition[1]}"/>" size="4" maxlength="20" id="lastPosition1" name="lastPosition1"></td>
                            </tr>
                          </table>
                          <br>
                          </td>
                          </tr>
                          </table>
                      