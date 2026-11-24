<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	hidePopup();

	Ext.get('buttonSearch').dom.click();
	if (materialStore.lastOptions){
		materialStore.reload(materialStore.lastOptions);
	}
</script>
