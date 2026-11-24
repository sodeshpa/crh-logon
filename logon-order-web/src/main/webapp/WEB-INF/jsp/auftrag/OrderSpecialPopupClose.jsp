<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: OrderSpecialPopupClose.jsp,v $ --%>
<%@ include file="../includeTLDs.jsp"%>

<script type="text/javascript">

	hidePopup();
	hide();
	
	function hide(){
		//at several places the screens have to be reloaded/initialized and so on... define function logonPopupHideCallback() on the caller page if you need to call it when calling hidepopup 
		hidePopupCallFunction();
	}
</script>

<%-- END $RCSfile: OrderSpecialPopupClose.jsp,v $ --%>
