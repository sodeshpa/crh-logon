<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>

<script type="text/javascript">
	Ext.onReady(function() {

		activeTab = '<c:out value="${admin_WagonDetailForm.activeTab}"/>';
		selectedTab = '<c:out value="${admin_WagonDetailForm.selectedTab}"/>';
		if (activeTab) { 
			document.getElementById(activeTab).className = '';
		}
		if (selectedTab) {
			document.getElementById(selectedTab).className = 'highlight';
		}
	});
</script>
