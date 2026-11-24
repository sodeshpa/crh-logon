<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	onPopupReady(function() {
		
		var el = document.getElementById('monthCheckbox0');
		if (!el) {
			el =  document.getElementById('weekCheckbox0');
		}
		if (!el) {
			el = document.getElementById('dayOfMonthCheckbox0');
		}
		if (el && !el.disabled) {
			el.focus();
		}
	});
	
	function saveSelection(){
		a = {"save": ""};
		addFormElements(a, "BilltoSelectionRhythmForm");
	
		showPopup('BilltoSelectionRhythmAction.do', null, a);
	}
	
	function popupTabChoose(tab){
	<%-- Switch Tab --%>
	selectTab("BilltoSelectionRhythmTabs", tab); 
	a = {"setTab":"", "tab": tab};
	addFormElements(a, "BilltoSelectionRhythmForm"); <%-- add form data --%>
	var diva = Ext.get('RhythmTabsDiv');
	diva.load({
		url: 'BilltoSelectionRhythmAction.do', 
		params: a,
		text: '<fmt:message key="dispokarte.texte.exjsloading"/>',
		scripts: true
	});
	}
	
</script>

<div id="RhythmTabsDiv" class="border" style="border-top:none;">
<html:form action="dispokarte/BilltoSelectionRhythmAction.do" onsubmit="return false;" styleId="BilltoSelectionRhythmForm">
<table class="classicblue" style="border:none; width:auto; border-collapse: collapse" width="1060">
<tr>
	<th>
		<fmt:message key="dispokarte.texte.billtoSelectionRhythm"/>
	</th>
</tr>
<tr>
	<td class="popupcontent">
		<table width="1100" style="classicblue" border="0" cellpadding="0" cellspacing="0" id ="BilltoSelectionRhythmTabs" style="margin-top:10px;">
			<tr>
				<td style="width:60px;padding-left:10px"  class="subnaviborder">
					<ul id="subnavigation">
			  			<li>
			  				<a href="javascript:popupTabChoose('TabMonthly')" id='TabMonthly' <c:if test="${order_dispokarte_BilltoSelectionRhythmForm.tab=='TabMonthly'}">class="highlight"</c:if>>
			  					<span class="outer"><span class="inner"><nobr><fmt:message key="dispokarte.texte.monthly"/></nobr></span></span>
			  				</a>
			  			</li>
					</ul> 
				</td>
				<td style="width:80px;padding-left:10px"  class="subnaviborder">
					<ul id="subnavigation">
			  			<li>
			  				<a href="javascript:popupTabChoose('TabWeekly')" id='TabWeekly' <c:if test="${order_dispokarte_BilltoSelectionRhythmForm.tab=='TabWeekly'}">class="highlight"</c:if>>
			  					<span class="outer"><span class="inner"><nobr><fmt:message key="dispokarte.texte.weekly"/></nobr></span></span>
			  				</a>
			  			</li>
					</ul> 
				</td>
				<td style="width:80px;padding-left:10px"  class="subnaviborder">
					<ul id="subnavigation">
			  			<li>
			  				<a href="javascript:popupTabChoose('TabDayOfMonth')" id='TabDayOfMonth' <c:if test="${order_dispokarte_BilltoSelectionRhythmForm.tab=='TabDayOfMonth'}">class="highlight"</c:if>>
			  					<span class="outer"><span class="inner"><nobr><fmt:message key="dispokarte.texte.dayOfMonth"/></nobr></span></span>
			  				</a>
			  			</li>
					</ul> 
				</td>
				<td style="width:860px;padding-left:10px"  class="subnaviborder">
					&nbsp;
				</td>
			</tr>
<%--  	</table>
	</td>
</tr>
</table>   --%>
			<tr>
				<td colspan="4">
				<div class="scrollist" style="height: 300px; width: 100%;">
				<c:choose>
				<c:when test="${order_dispokarte_BilltoSelectionRhythmForm.tab == 'TabMonthly'}"> 
					<table width="100%" class="classicblue" style="margin-top:4px">
						<tr>
							<th style="width:200px"></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.jan"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.feb"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.mar"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.apr"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.mai"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.jun"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.jul"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.aug"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.sep"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.okt"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.nov"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.dez"/></th>
							<th>&nbsp;</th>
						</tr>
						<c:forEach var="billto" varStatus="st2" items="${order_dispokarte_DispokarteForm.currSalesAreaBean.shiptoSarea.billtos}">
							<tr>
								<td style="width:200px">
									<c:out value="${billto.billtoSapBp.name1}"/> <c:out value="${billto.billtoSapBp.name2}"/> (<c:out value="${billto.billtoSapBp.sapId}"/>)
								</td>
								<c:forEach var="cb" begin="1" end="12" step="1" varStatus="stat">
									<td>
										<html:checkbox property="monthCheckbox[${((st2.count - 1) * 12) + (stat.count - 1)}]" value="true" styleId="monthCheckbox${((st2.count - 1) * 12) + (stat.count - 1)}" onclick="adjustCheckboxes('monthCheckbox', ${((st2.count - 1) * 12) + (stat.count - 1)}, ${(stat.count - 1)})"/> 
									</td>
								</c:forEach>
								<td>&nbsp;</td>
							</tr>
						</c:forEach>
							<td></td>
					</table>
				</c:when>
				<c:when test="${order_dispokarte_BilltoSelectionRhythmForm.tab == 'TabWeekly'}">  
					<table width="100%" class="classicblue" style="margin-top:4px">
						<tr>
							<th style="width:200px"></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.mo"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.di"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.mi"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.do"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.fr"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.sa"/></th>
							<th style="width:30px"><fmt:message key="dispokarte.texte.so"/></th>
							<th>&nbsp;</th>
						</tr>
						<c:forEach var="billto" varStatus="st2" items="${order_dispokarte_DispokarteForm.currSalesAreaBean.shiptoSarea.billtos}">
							<tr>
								<td style="width:200px">
									<c:out value="${billto.billtoSapBp.name1}"/> <c:out value="${billto.billtoSapBp.name2}"/> (<c:out value="${billto.billtoSapBp.sapId}"/>)
								</td>
								<c:forEach var="cb" begin="1" end="7" step="1" varStatus="stat">
									<td>
			 							<html:checkbox property="weekCheckbox[${((st2.count - 1)*7) + (stat.count - 1)}]" value="true" styleId="weekCheckbox${((st2.count - 1)*7) + (stat.count - 1)}" onclick="adjustCheckboxes('weekCheckbox', ${((st2.count - 1)*7) + (stat.count - 1)}, ${(stat.count - 1)})"/>  
									</td>
								</c:forEach>
								<td>&nbsp;</td>
							</tr>
						</c:forEach>
					</table>
				</c:when>
				<c:when test="${order_dispokarte_BilltoSelectionRhythmForm.tab == 'TabDayOfMonth'}">  
					<table width="100%" class="classicblue" style="margin-top:4px">
						<tr>
							<th style="width:150px"></th>
							<c:forEach var="cb" begin="1" end="31" step="1" varStatus="stat">
								<th style="width:30px"><c:out value="${stat.count}" /></th>
							</c:forEach>
						</tr>
						<c:forEach var="billto" varStatus="st2" items="${order_dispokarte_DispokarteForm.currSalesAreaBean.shiptoSarea.billtos}">
							<tr>
								<td style="width:150px">
									<c:out value="${billto.billtoSapBp.name1}"/> <c:out value="${billto.billtoSapBp.name2}"/> (<c:out value="${billto.billtoSapBp.sapId}"/>)
								</td>
								<c:forEach var="cb" begin="1" end="31" step="1" varStatus="stat">
									<td>
		 								<html:checkbox property="dayOfMonthCheckbox[${((st2.count - 1) * 31) + (stat.count - 1)}]" value="true" styleId="dayOfMonthCheckbox${((st2.count - 1) * 31) + (stat.count - 1)}" onclick="adjustCheckboxes('dayOfMonthCheckbox', ${((st2.count - 1) * 31) + (stat.count - 1)}, ${(stat.count - 1)})"/> 
									</td>
								</c:forEach>
							</tr>
						</c:forEach>
					</table>
			</c:when>
			</c:choose> 
			</div>
			<table width="100%">
				<tr>
					<td class="buttons right" align="right">
 						<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.cancel"/>" onclick="hidePopup()"/>   				
						<input type="button" class="ViewButtonStyle" value="<fmt:message key="buttons.hinzufuegen"/>" onclick="saveSelection()"/>     				
					</td>
				</tr>
			</table>
		</td>
	</tr>
</table>
</html:form>
</div>

<script type="text/javascript">
  	initCheckboxes();
  	
  	function initCheckboxes() {
		var cboxName = null;
		var multiplier;
		
		if ( document.getElementById('monthCheckbox0') != null ) {
			var cboxName = 'monthCheckbox';
  			var multiplier = 12;
  		}
  		else if ( document.getElementById('weekCheckbox0') != null ) {
			var cboxName = 'weekCheckbox';
  			var multiplier = 7;
  		} 
  		else if ( document.getElementById('dayOfMonthCheckbox0') != null ) {
			var cboxName = 'dayOfMonthCheckbox';
  			var multiplier = 31;
  		}
  		else {
  			return;
  		}
		for (var colIdx = 0; colIdx < multiplier; colIdx++) {
			var selectedCBInCol = getSelectedCBNumInColumn( colIdx, multiplier, cboxName); 
			if ( selectedCBInCol > -1) {
				adjustCheckboxes( cboxName, selectedCBInCol, colIdx);
			}
		}
  	}
  	
  	function getSelectedCBNumInColumn( colNum, multiplier, cboxName) {
  		var result = -1;
  		for (var i=0; true; i++) {
			var cbox = document.getElementById(cboxName + (i*multiplier + colNum) );
			if ( cbox == null ) {
				break;
			}
			if ( cbox.checked == true) {
				result = (i*multiplier + colNum);
				break;
			}
  		}
  		return result;
  	}
  	
  	function adjustCheckboxes( cboxName, cbNum, numInRow ) {
  		var multiplier;
  		if ( cboxName == 'monthCheckbox' ) {
  			multiplier = 12;
  		}
  		else if ( cboxName == 'weekCheckbox' ) {
  			multiplier = 7;
  		}
  		else if ( cboxName == 'dayOfMonthCheckbox' ) {
  			multiplier = 31;
  		}
  	
		var referenceCb = document.getElementById(cboxName + cbNum);
		
		for (var i=0; true; i++) {
			if ( i*multiplier + numInRow == cbNum ) {
				continue;
			}
			var cbox = document.getElementById(cboxName + (i*multiplier + numInRow) );
			if ( cbox == null ) {
				break;
			}
   			cbox.disabled = ( referenceCb.checked ) ? true : false;
   			if ( cbox.disabled == true ) {
   				cbox.checked = false;
   			}
   		}
  	}
</script>
