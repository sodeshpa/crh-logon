<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: AufliegerCarrierTile.jsp,v $ --%>
<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.admin.web.actions.traileradmin.TrailerAdminAction;"%>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<script type="text/javascript">
function selectCarrier(){
    Ext.get('admin_TrailerAdminForm').dom.submit();
}
</script>

<div class="customer_max">
    
    <!-- beginn Border-Table   -->
<jsp:include page="../SubTab9Tile.jsp"/> 
                
<!-- Begin table 3 rows -->
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.5"/></th>
      </tr>
      <tr>
    
      <td>
            <!-- Beginn Submenu 2-2-1-1 -->
<jsp:include page="../Sub5Tab9Tile.jsp"/>   
       		  
       		  <div class="subform" style="border-top:0px;">
       		    
                
                <!-- Beginn Search Table -->
                
                        
  <html:form action="traileradmin/TrailerAdmin.do" styleId="admin_TrailerAdminForm">
                   <table cellpadding="0" cellspacing="0" border="0" class="big_form2" style="padding:0px;">
                  	<colgroup>
                    <col width="20%">
                    <col width="80%">
                    </colgroup>
                          
                            <tr>
                              <td valign="top" style="border:0px;"><span class="labelblue"><fmt:message key="roadmasterdata.fields.carrierchoose"/></span><br>
                              
                                  <table class="noborder" cellpadding="0" cellspacing="0" border="0">
                                      <tr>
                                          <td valign="bottom" style="white-space:nowrap;border:0px; padding-right: 4px;"><p class="formtitle"><fmt:message key="roadmasterdata.fields.choose"/></p></td>
                                          <td style="white-space:nowrap;border:0px;"><span class="content" style="border:0px;">
					<html:select onchange="selectCarrier()" styleId="carrierId" property="carrierId" style="margin:0px; padding:0px; width:auto;">
					<html:option value="">Select carrier...</html:option>											
						<c:forEach var="entry" items="${admin_TrailerAdminForm.carrierList}">
						<html:option value="${entry.carrierId}"><c:out value="${entry.displayName}"/></html:option>						
						</c:forEach>
					</html:select>
                                          </span>
                                          </td>
                                      </tr>
                                  </table>  
                                                            
                               </td>
                              <td style="border:0px;"><span class="labelblue"><fmt:message key="roadmasterdata.fields.address"/></span><br>
                              <c:out value="${admin_TrailerAdminForm.carrier.sapVendor.name1}"/><br>
                              <c:out value="${admin_TrailerAdminForm.carrier.sapVendor.street}"/> <c:out value="${admin_TrailerAdminForm.carrier.sapVendor.houseNumber}"/><br>
                              <c:out value="${admin_TrailerAdminForm.carrier.sapVendor.city}"/>
                              </td>
                            </tr>
                  </table>
<input type="hidden" name="inputAction" id="inputAction" value="<%=TrailerAdminAction.FORWARD_SELECT_CARRIER %>"/>

</html:form>
</div>
</td>
</tr>
</table>
</div>
<%-- END $RCSfile: AufliegerCarrierTile.jsp,v $ --%>
