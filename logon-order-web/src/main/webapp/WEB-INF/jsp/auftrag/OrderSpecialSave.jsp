<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderSpecialSave.jsp,v $ --%>

<%@ page session="true"%>
<%@ include file="../includeTLDs.jsp"%>
<script type="text/javascript">

		var button = Ext.getDom('SpecialCancelButton');
		button.className = 'ViewButtonStyle';
		button.disabled = false;
		button = Ext.getDom('SpecialSaveButton');
		button.className = 'ViewButtonStyle';
		button.disabled = false;
		checkTransmitEnabled();

</script>
<%-- END $RCSfile: OrderSpecialSave.jsp,v $ --%>
