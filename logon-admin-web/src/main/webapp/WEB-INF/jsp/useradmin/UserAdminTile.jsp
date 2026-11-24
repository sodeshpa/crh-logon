<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: UserAdminTile.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>
  
<script type="text/javascript" src="<crh-common:url value='/js/extjshelper/executeUpdate.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/useradmin/useradmin-util.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/useradmin/DDView.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/js/ext-ux/Ext.ux.TwinCombo.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/UserAdminAjaxService.js'/>"></script>
<script type="text/javascript" src="<crh-common:url value='/dwr/interface/AdminRFIDPopupAjaxService.js'/>"></script>

<script type="text/javascript"> 
executeUpdate.init('<fmt:message key="executeupdate.updatedText"/>', '<fmt:message key="executeupdate.exceptionTitle"/>', '<fmt:message key="executeupdate.updateButtonText"/>');
useradmin.init('<fmt:message key="shippingpoint.text.exjsloading"/>', '<fmt:message key="executeupdate.question.optimisticlock"/>');

var searchArea;

function hideDetailsDiv() {
	Ext.DomHelper.overwrite(Ext.getDom('user-details-div'), "");
}

function cancel(){
	hideDetailsDiv();
	searchArea.searchUsers(true);
}


function doResetSearch(){
	document.getElementsByName("searchFirstNameOrName")[0].value = "";
	document.getElementsByName("searchHolspaceOrExtLogin")[0].value = "";
	document.getElementsByName("searchCustomerOrCarrier")[0].value = "";
	document.getElementsByName("searchStatus")[0].checked = true;
	document.getElementsByName("searchSelRoleId")[0].selectedIndex = 0;
	searchArea.searchUsers(false);
}



	Ext.onReady(function(){
		//Search Grid
		<jsp:include page="UserAdminSearchGrid.jsp"/>
		
		Ext.get('searchUser').on('click', function() {
			searchArea.searchUsers(true);
		});
		Ext.get('newUser').on('click', function() {
			if (grid != null) {
				grid.hide();
			}
			useradmin.submitWithActionToDiv('newUser', 'admin_UserAdminForm', '');
		});
	<c:choose>
	<c:when test="${admin_UserAdminForm.searchState == admin_UserAdminForm.searchStateWithFilter}">
			searchArea.searchUsers(true);
		</c:when>
		<c:otherwise>
		searchArea.searchUsers(false);
		</c:otherwise>
	</c:choose>

	});
</script>


<div class="customer_max">
    <input type="hidden" name="inputAction" id="inputAction" value=""/>
    <!-- beginn Border-Table   -->
    <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
    <tr>
<jsp:include page="../SubTab10Tile.jsp"/>   

<!-- Begin table 3 rows -->
<table class="big_form2">
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.admin"/> / <fmt:message key="tabsubitem.10.1"/></th>
      </tr>
      <tr>
      <td>
            <!-- Beginn Submenu -->
<jsp:include page="../Sub1Tab10Tile.jsp"/>  

    <!-- beginn Border-Table   -->
    <table width="100%">
    <tr>
<!--    <td width="10" height="1">&nbsp;</td>-->
    <td>
       			 <!-- Beginn Benutzersuche -->
		<html:form action="/useradmin/UserAdmin.do" styleId="admin_UserAdminForm">
		<html:hidden property="inputAction" value=""/>
		<html:hidden property="updateUserStatusValue" value=""/>
		<div id="user-search-filters-panel">
         <table cellpadding="0" cellspacing="0" border="0" class="big_form2" style="padding:0px;">
             <tbody>
              	<tr>
                  <td colspan="2" style="border:0px;"><span class="labelblue"><fmt:message key="useradmin.texte.userSearch"/></span><br>
                      <table class="noborder" cellpadding="0" cellspacing="0" border="0" style="margin-top:4px;">
                      		<tr>
                          		<td valign="top" style="white-space:nowrap;border:0px; padding-right: 4px; vertical-align:top;"><span class="formtitle"><fmt:message key="useradmin.texte.filter"/>:</span></td>
                        		<td width="100%">
			                        <div class="subform" style="margin:0px; padding: 4px;">
            		                  <table cellpadding="0" cellspacing="0" border="0">
                    		              <tr>
                            		         <td class="formtitle" style="padding:0px; margin:0px;" height="10"><fmt:message key="useradmin.field.firstNameOrName"/></td>
                                    		 <td class="formtitle" style="padding:0px; margin:0px;" height="10"><fmt:message key="useradmin.field.holspaceOrExtLogin"/></td>
											 <td class="formtitle" style="padding:0px; margin:0px;" height="10"><fmt:message key="useradmin.field.customerOrCarrier"/></td>
                                      		 <td class="formtitle" style="padding:0px; margin:0px;"><fmt:message key="useradmin.texte.roles"/></td>
                                    		 <td class="formtitle" style="text-align:center;padding:0px; margin:0px;"><nobr><fmt:message key="useradmin.texte.activeonly"/></nobr></td>
                                      		 <td width="100%" rowspan="2" class="formtitle" style="padding:0px; margin:0px; vertical-align:bottom" valign="bottom">
                                        	 	<div align="left" style="float:left; vertical-align:bottom; padding:0px 0px 0px 10px; width:auto;"><input id="searchUser" tabindex="6" name="searchUser" type="submit" class="ViewButtonStyle" value="<fmt:message key="useradmin.button.search"/>"></div>
                                        	 	<div align="left" style="float:left; vertical-align:bottom; padding:0px 0px 0px 10px; width:auto;"><input id="resetSearchUser" tabindex="7" name="resetSearchUser" type="button" class="ViewButtonStyle" value="<fmt:message key="useradmin.button.resetSearch"/>"></div>
											 	<div align="left" style="float:left; vertical-align:bottom; padding:0px 0px 0px 10px; width:auto;"><input id="newUser" tabindex="8" type="button" class="ViewButtonStyle" value="<fmt:message key="useradmin.button.newUser"/>"></div>
                                      		 </td>
                                  		</tr>
                                  		<tr>
								          <td><html:text styleId="searchFirstNameOrName" tabindex="1" name="admin_UserAdminForm" property="searchFirstNameOrName" maxlength="40" size="10" style="width:170px" styleClass="textField"/></td>
								          <td><html:text styleId="searchHolspaceOrExtLogin" tabindex="2" name="admin_UserAdminForm" property="searchHolspaceOrExtLogin" maxlength="40" size="10" style="width:170px" styleClass="textField"/></td>
								          <td><html:text styleId="searchCustomerOrCarrier" tabindex="3" name="admin_UserAdminForm" property="searchCustomerOrCarrier" maxlength="40" size="10" style="width:170px" styleClass="textField"/></td>          
								          <td>
					                      	<html:select styleId="searcRole" tabindex="4" property="searchSelRoleId" style="margin:0px; padding:0px; width:auto;" >
								         		<html:optionsCollection name="admin_UserAdminForm" property="roleList" value="roleId" label="roleName"/>
								       		</html:select>
								          </td>
         								  <td align="center" style="text-align:center; vertical-align:middle;padding:0px 16px 0px 16px;"><html:checkbox styleId="searchActive" tabindex="5" name="admin_UserAdminForm" property="searchStatus"/></td>
                             		    </tr>
                          			</table>
                        		</div>
                        </td>
                      </tr>
                      </table>
	                </td>
                </tr>
              </tbody>
        </table>
		</div>
		</html:form>
				
		<div id="user-search-results-div" style="width:100%">
		</div>
		<div id="user-details-div" style="width:100%">
		</div>
</td>
</tr>
</table>
            </td>
      
      
      </tr>
</table>
<br>





  </td>
  <td width="10">&nbsp;</td>
  </tr>
  </table>
  <!-- End Border Table -->
	    
    
    
    <br>
</div>

<tiles:insert page="PageSetupTile.jsp">
	<tiles:put name="focus" value="searchFirstName"/>
</tiles:insert>

<%-- END $RCSfile: UserAdminTile.jsp,v $ --%>
