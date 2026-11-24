<%@ page session="true" %>
<%@page import="com.crh.logon.constants.Constant"%>
<%@ include file="../includeTLDs.jsp" %>
<script type="text/javascript">

	function selectJob(){
		Ext.get('admin_CronJobsForm').dom.submit();
	}

</script>


<jsp:include page="../SubTab11Tile.jsp" />

<!-- Begin table 3 rows -->
<html:form action="test/CronJobs.do" styleId="admin_CronJobsForm">
<table class="big_form2">

	<tr class="classicblue">
		<th class="classicblue"><fmt:message key="tabitem.test" />
		/ <fmt:message key="tabsubitem.11.4" /></th>
	</tr>
	
	
	<tr style="width:40%;" >
	<table>
		<tr>
		<td>
			<b>
			&nbsp &nbsp Start cron jobs: &nbsp &nbsp &nbsp &nbsp
			</b>
		</td>
		<td style="white-space: nowrap; border: 0px;">
			<span class="content" style="border: 0px;"> 
				<html:select styleId="selectedJob" property="selectedJob" style="margin:0px; padding:0px; width:auto;">
					<html:option value="<%=Constant.INVALID_OBJECT_ID_STR%>">
						select job
					</html:option>
					<c:forEach var="entry"
						items="${admin_CronJobsForm.cronJobsList}">
						<html:option value="${entry}">
							<c:out value="${entry}" />
						</html:option>
					</c:forEach>
				</html:select>
			</span>
		</td>
		<td>
		</td>
		<td>
		<button id="runCronJob" type="button" onclick="selectJob();"> run job </button>
		</td>
		
		</tr>
	</table>
		
	</tr>
</table>
</html:form>
