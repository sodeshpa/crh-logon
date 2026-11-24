<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://struts.apache.org/tags-html-el" prefix="html" %>
<!--  <%-- Struts needs this, fond no better way to do it --%> <html:form action="auftrag/AuftragBearbeitung.do">-->
<html:select styleId="selectedProduct" onchange="loadOrders()" multiple="true" size="4" property="selectedProduct" style="margin:0px 0px 10px 0px;padding:0px;width:130px">
	<c:forEach var="entry" items="${order_auftrag_AuftragBearbeitungForm.selectableProductList}">
		<html:option value="${entry.sapMaterialId}"><c:out value="${entry.txt}"/></html:option>
	</c:forEach>            
</html:select>
                              <input type="button" class="SmallButtonStyle"  onclick="selectProductAll(false)" value=" keine ">&nbsp;&nbsp;<input type="button"  onclick="selectProductAll(true)" class="SmallButtonStyle" value=" alle ">
<!--</html:form>-->