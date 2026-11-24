<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	hidePopup();

	Ext.get('buttonSearch').dom.click();
	if (stationToCombo.lastOptions){
		stationToCombo.reload(stationToCombo.lastOptions);
	}
</script>
