<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: CloseEditAssignment.jsp,v $ --%>

<%@ include file="../includeTLDs.jsp" %>

<script type="text/javascript">
	hidePopup();
	<c:if test="${refreshAssignments}">
		refreshAssignments();
	</c:if>
</script>
