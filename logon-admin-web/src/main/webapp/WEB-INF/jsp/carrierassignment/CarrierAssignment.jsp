<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
var texts = {
	exjsloading: '<fmt:message key="dispokarte.texte.exjsloading"/>'
};
</script>
<script type="text/javascript" src="<crh-common:url value='/js/carrierassignment/CarrierAssignment.js'/>"></script>

 <!-- beginn Border-Table   -->
    <table cellpadding="0" cellspacing="0" border="0" width="900" style="padding:0px; margin:0px;">
    	<tr>
    		<td width="10" height="1">
    			&nbsp;
    		</td>
    		<td >
    			<div style="float:left; margin:0px; padding:0px;">
    				<img src="<crh-common:url value='/images/cccccc.gif'/>" alt="" width="900" height="1" border="0" style="float:left; margin:0px; padding:0px;">
    			</div>
    			<div style="clear:both; padding:0px;margin:0px;">
    			</div>
    		</td>
    	</tr>
    	<tr>
    		<td>
    		</td>
    		<td>
    			<jsp:include page="../SubTab9Tile.jsp"/> 
				<table class="big_form2">
			    	<tr class="classicblue">
      					<th class="classicblue">
      						<fmt:message key="tabitem.masterdata"/> / <fmt:message key="carrierassignment.title"/>
      					</th>
      				</tr>
					<tr>
						<td>
							<div id="carrierAssignmentDiv">
								<jsp:include page="CarrierAssignmentTable.jsp"/>
							</div>
						</td>
					</tr>
				</table>
			</td>
		</tr>
	</table>
		