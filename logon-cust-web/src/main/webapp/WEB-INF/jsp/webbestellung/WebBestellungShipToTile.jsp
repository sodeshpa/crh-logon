<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: WebBestellungShipToTile.jsp,v $ --%>

<%@ page session="true" %>
<%@ include file="../includeTLDs.jsp" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<%@ taglib tagdir="/WEB-INF/tags" prefix="fmtx" %>
<c:if test="false">
    <%-- for preview in MyEclipse --%>
<link rel="stylesheet" type="text/css" href="../css/crh_weborder_style.css">
</c:if>
<script type="text/javascript">
      function refreshPage(){
            Ext.get('ShipToForm').dom.submit();
       }
</script>

<html:form action="webbestellung/WebBestellungEntry.do" styleId="ShipToForm">
<div class="customer_max">
    <!-------------------------------------->
    <!----- beginn Border-Table   ---------->
    <!-------------------------------------->
    <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px;">
    <tr>
    <td width="10" height="1">&nbsp;</td>
    <td><div style="float:left; margin:0px; padding:0px;"><img src="<crh-common:url value='/images/cccccc.gif'/>" alt="" width="600" height="1" border="0" style="float:left; margin:0px; padding:0px;"></div><div style="clear:both; padding:0px;margin:0px;"></div>
      
    <!-- -------------------------- -->
	<!-- Ship-to / Standort         -->
	<!-- -------------------------- -->
<table class="big_form2" style="border-collapse:collapse;">
      
      <tr style="border-bottom: 2px solid #599ade;">
      <c:choose>
      <c:when test="${empty cust_WebBestellungForm.shiptoList}" >
            <fmt:message key="webbestellung.no.shiptos.available"/>
      </c:when>
      <c:otherwise>
            <!-- Begin Ship-to -->
      <td class="labelblue_bg"><span class="labelbluebig"><fmt:message key="bestellung.boxlabel.lieferort"/><span style="border:0px; margin-left:20px;">
					<html:select onchange="refreshPage()" styleId="compositeId" property="compositeId" style="margin:0px; padding:0px; width:auto;">
						<c:forEach var="entry" items="${cust_WebBestellungForm.shiptoList}">
						<c:set var="showDisplayLongName">false</c:set>
					<c:if test="${!empty cust_WebBestellungForm.multipleSalesAreaShiptos[entry.shiptoSapBpId]}">
					<c:set var="showDisplayLongName">true</c:set>
					</c:if>
						<html:option value="${entry.compositeId}">
							<c:out value="${entry.shiptoName}"/>&nbsp;-&nbsp;<c:out value="${entry.soldtoName}"/>&nbsp;-&nbsp;<c:out value="${entry.street}"/>&nbsp;<c:out value="${entry.houseNumber}"/>&nbsp;<c:out value="${entry.city}"/>
						</html:option>						
						</c:forEach>
					</html:select>
      </span></span>
      </td>
      <td class="labelblue_bg"><span class="labelbluebig"><fmt:message key="fields.shipto.name"/></span></td>
      <td class="labelblue_bg"><span class="labelbluebig"><fmt:message key="bestellung.boxlabel.standort"/></span><br></td>
      </c:otherwise>
      </c:choose>
      </tr>
</table>

	</td>
    </tr>

</table>

  <!-------------------------------------->
  <!--------- End Border Table ----------->
  <!-------------------------------------->
</div>
</html:form>
