<%@ page session="true"%>
	
<%@ include file="../includeTLDs.jsp"%>
<logic:present name="org.apache.struts.action.ACTION_MESSAGE" scope="request">
<html:messages id="msg" name="org.apache.struts.action.ACTION_MESSAGE">
<%-- This is a tile, which is called in case some error happened when saving the LKW Unavailability entry.
The lkw popup's content is re-set to the value recorded before sending the save call, and the input fields has to be set
to the sooner values.--%>
<script>
Ext.onReady(function(){
    Logon.message.warning('<c:out value="${pageScope.msg}"/>');
	document.getElementById('logonPopup').innerHTML = lkwPopupInner;
	var idField = document.getElementById("inputTruckUnavailabilityId");
	row = idField.parentNode.parentNode;
	replaceRow(row);
	vals.setBackValues();
});
</script>
</html:messages>
</logic:present>
