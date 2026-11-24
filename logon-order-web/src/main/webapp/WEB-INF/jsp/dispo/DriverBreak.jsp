<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: DriverBreak.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<html:form action="dispo/DriverBreak.do" onsubmit="return false;" styleId="DriverBreakForm">

<table class="classicblue" style="border:none; width:auto"  class="big_form2">
<tr>
	<th colspan="2">
		<fmt:message key="dispo.texte.editdriverbreak"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
	<table width="100%" class="big_form2" >
		<tr>
			<td colspan='3' class='border'>
				<table width='100%'>
					<tr>
						<td  class='label right'><nobr>
							<fmt:message key="dispo.texte.Driver"/>:
						</td>
						<td colspan="3">
							<c:out value="${order_dispo_DriverBreakForm.driver.user.person.firstname}"/>
							<c:out value="${order_dispo_DriverBreakForm.driver.user.person.name}"/></nobr>							
						</td>
					</tr>
					<tr>
						<td class="label right">
							<fmt:message key="dispo.texte.breakfrom"/>:
						</td>
						<td>
							<html:text styleClass='textfield' style='width:50px' styleId='driverBreakFrom' property="startTime"/>
						</td>
						<td class="label right">
							<fmt:message key="dispo.texte.breakuntil"/>:
						</td>
						<td>
							<html:text styleClass='textfield' style='width:50px' styleId='driverBreakUntil' property="endTime"/>
						</td>
					</tr>
					<tr>
						<td class="label right">
							<fmt:message key="dispo.texte.description"/>:
						</td>
						<td colspan="3">
							<html:textarea styleClass='textfield' style='width:100%;height:100px;' property="driverBreak.description"/>
						</td>
					</tr>
					<tr>
						<td class="label right">
							<fmt:message key="dispo.texte.lunchbreak"/>
						</td>
						<td  colspan="3">
							<html:checkbox property="driverBreak.isLunchBreak"></html:checkbox>
						</td>
					</tr>
					
				</table>
			</td>
		</tr>
		<tr>
			<td colspan="4" class="buttons right">
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>
				<c:if  test="${order_dispo_DriverBreakForm.driverBreakId != null}">  				
					<input type="button" class="ViewButtonStyle" value="<fmt:message key="dispo.texte.deletebreak"/>" onclick="deleteDriverBreak()"/>
				</c:if>   				
				<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.save"/>" onclick="saveDriverBreak()"/>   				
			</td>
		</tr>
	</table>
	</td>
</table>

</html:form>



<script type="text/javascript">

	new Ext.form.TimeField({
	    increment: 30,
	    applyTo : 'driverBreakFrom',
	    format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>'
	    
	});	
	
	new Ext.form.TimeField({
	    increment: 30,
	    applyTo : 'driverBreakUntil',
	    format: '<%= DateTimeFormat.getHourMinuteFormat4JS(pageContext.getRequest().getLocale()) %>'
	    
	});	


	function saveDriverBreak(){	
		a = {save: ''};
		addFormElements(a, 'DriverBreakForm');

		showPopup('DriverBreak.do', null, a);
	};
	
	function deleteDriverBreak(){	
		showPopup('DriverBreak.do', null, {'delete': ''});
	};

	onPopupReady(function() {
		
		var el = document.getElementById('driverBreakFrom');
		if (el) {
			el.focus();
		}
	});
</script>


