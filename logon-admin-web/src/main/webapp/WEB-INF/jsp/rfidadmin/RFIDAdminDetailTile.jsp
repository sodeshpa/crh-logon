<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: RFIDAdminDetailTile.jsp,v $ --%>
<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
    <link rel="stylesheet" href="css/crh_style.css" type="text/css">
</c:if>

<script type="text/javascript">
	Ext.onReady(function() {
		document.getElementById('rfidIdentity').focus();
	});

function submitWithAction(action){
	form = Ext.getDom('admin_RFIDAdminForm');
	Ext.query("input[name=formAction]", form)[0].value = action;
	form.submit();
}
</script>
<html:form action="/rfidadmin/RFIDAdmin.do" styleId="admin_RFIDAdminForm">
 <!-- beginn Border-Table   -->
    <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
    	<tr>
    		<td>
    			<jsp:include page="../SubTab9Tile.jsp"/> 
				<table class="big_form2">
				<tr>
				<td>
      				<jsp:include page="../Sub3Tab9Tile.jsp"/>
      				</td>
      				</tr>
			    	<tr class="classicblue">
      					<th class="classicblue">
      						<fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.3"/>/ <fmt:message key="tabsubitem.9.3.1"/>
      					</th>
      				</tr>
      				<tr>
      				<td>
      				
      					<table width="100%" class="classicblue" style="margin-top:4px">
      					<td valign="bottom" colspan="2">
      					<c:choose>
							<c:when test="${empty admin_RFIDAdminForm.rfidTag}">
								<p class="subtitle1"><fmt:message key="rfidadmin.texte.add"/></p>
							</c:when>
							<c:otherwise>
								<p class="subtitle1"><fmt:message key="rfidadmin.texte.edit"/></p>							
							</c:otherwise>
						</c:choose>
						</td>
															<tbody>
															<tr>
																<td class="left" width="75">
																	<span class="label" style="margin-left: 15px"><nobr><fmt:message key="rfidadmin.header.rfidIdentity"/>:</nobr></span>
																</td>
																<td class="left">
																	<html:text styleId="rfidIdentity" property="rfidIdentity" style="width: 200px"/>						
																</td>
																</tr>
																<tr>
																<td class="left" width="75">
																	<span class="label" style="margin-left: 15px"><nobr><fmt:message key="rfidadmin.header.name"/>:</nobr></span>
																</td>
																<td class="left">
																	<html:text styleId="name" property="name" style="width: 200px"/>						
																</td>
																</tr>
																<tr>
																<td class="left" width="75">
																	<span class="label" style="margin-left: 15px"><nobr><fmt:message key="rfidadmin.header.activeFlag"/>:</nobr></span>
																</td>
																<td class="left">
																	<html:checkbox styleId="active" property="active"/>						
																</td>
																</tr>
							</table>
													<table class="big_form2" style="margin-top: 8px;">
														<tr>
															<td class="buttons right">
																<input id="buttonSave" name="save" type="submit" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" style="margin-top: 3px"/>
																<input id="buttonCancel" name="cancel" type="submit" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" style="margin-top: 3px"/>
															</td>
														</tr>
													</table>
								</td>
							</tr>
      				
      				</td>
      				</tr>
				</table>
			</td>
		</tr>
	</table>
	</html:form>
