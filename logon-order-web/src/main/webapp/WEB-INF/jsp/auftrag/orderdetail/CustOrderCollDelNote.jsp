<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon. web.utils.Authorization"%>

<c:set var="org.apache.struts.taglib.html.BEAN" scope="page"
	value="${order_auftrag_OrderDetailForm}" />
	
<!-- Delivery note -->
<div style="width:305px; margin-top:4px;" class="subform"><span
	class="label"><fmt:message
	key="orderdetail.material.label.coll.del.note" /> </span> 
	<c:if test="${!isMaterialDisabled && !isDelNoteDisabled}" >
		<a href="javascript:generateCollPdf();" ><img src="<crh-common:url value='/images/img_pdf_create.png'/>" alt="<fmt:message key="auftragbearbeitung.alt.generatepdf"/>"> </a>
	</c:if>
	<div style="font-size: 12px; margin-top: 4px; height: 44px;"
		class="subformlist">
		<table cellspacing="0" cellpadding="0" border="0" style="width: 100%;" class="noborder">
			<colgroup>
				<col width="1%" />
				<col width="99%" />
			</colgroup>
			<tbody>
				<c:set var="counter" value="1" />
				<c:forEach var="entity" varStatus="_status"
					items="${order_auftrag_OrderDetailForm.custOrderTabBean.collDeliveryNoteMap}">
					<tr class="zebra<c:out value="${counter % 2}" />">
						<td valign="middle"><a
							href="<%=request.getContextPath() %>/blob?keyValue=<c:out value="${entity.key}" />&entity=OutputdocEntity&keyName=outputdocId"
							target="_blank"><img src="<crh-common:url value='/images/img_pdf_icon.png'/>"
							width="16" height="16" alt="pdf"></a></td>
						<td><c:out value="${entity.value}" /></td>
					</tr>
					<c:set var="counter" value="${counter + 1}" />
				</c:forEach>
			</tbody>
		</table>
	</div>
</div>
