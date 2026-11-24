<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="MIME_TYPE_PDF"><%=Constant.MIME_TYPE_PDF %></c:set>
<c:set var="MIME_TYPE_DOC"><%=Constant.MIME_TYPE_DOC %></c:set>
<c:set var="MIME_TYPE_XLS"><%=Constant.MIME_TYPE_XLS %></c:set>


<c:set var="ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION %></c:set>
<c:set var="ORDER_REP_ATTACHMENT_TYPE_INVOICE"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE %></c:set>
<c:set var="ORDER_REP_ATTACHMENT_TYPE_OFFER"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER %></c:set>
<c:set var="ORDER_REP_ATTACHMENT_TYPE_EMAIL"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL %></c:set>

<c:set var="noattachment"></c:set>
<c:choose>
<c:when test="${attachmentTypeDiv == ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION}">
	<c:set var="list" value="${order_auftrag_OrderDetailForm.repBean.documentationAttachments}"/>
	<c:set var="noattachment"><fmt:message key="orderdetail.repair.documentation.no.attachment" /></c:set>
	<c:set var="attType"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_DOCUMENTATION %></c:set>
</c:when>
<c:when test="${attachmentTypeDiv == ORDER_REP_ATTACHMENT_TYPE_INVOICE}">
	<c:set var="list" value="${order_auftrag_OrderDetailForm.repBean.invoiceAttachments}"/>
	<c:set var="noattachment"><fmt:message key="orderdetail.repair.invoice.no.attachment" /></c:set>
	<c:set var="attType"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_INVOICE %></c:set>
</c:when>
<c:when test="${attachmentTypeDiv == ORDER_REP_ATTACHMENT_TYPE_OFFER}">
	<c:set var="list" value="${order_auftrag_OrderDetailForm.repBean.offerAttachments}"/>
	<c:set var="noattachment"><fmt:message key="orderdetail.repair.offer.no.attachment" /></c:set>
	<c:set var="attType"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_OFFER %></c:set>	
</c:when>
<c:when test="${attachmentTypeDiv == ORDER_REP_ATTACHMENT_TYPE_EMAIL}">
	<c:set var="list" value="${order_auftrag_OrderDetailForm.repBean.emailAttachments}"/>
	<c:set var="noattachment"><fmt:message key="orderdetail.repair.email.no.attachment" /></c:set>
	<c:set var="attType"><%=Constant.ORDER_REP_ATTACHMENT_TYPE_EMAIL %></c:set>
</c:when>
</c:choose>
		<table class="classic" style="border: none">
                            <tbody>
	<c:set var="zebracount" value="0"/>
		<c:set var="rowPresent">false</c:set>
	<c:forEach var="entry" varStatus="status" items="${list}">
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
		<c:set var="rowPresent">true</c:set>	
                              <tr class="zebra<c:out value="${zebracount}" />" >
                                <td>
                              <c:choose>
								<c:when test="${entry.dataMimeType.mimeTypeCd == MIME_TYPE_PDF }">                                
                                <img src="<crh-common:url value='/images/img_pdf_icon.png'/>">
								</c:when>
								<c:when test="${entry.dataMimeType.mimeTypeCd == MIME_TYPE_DOC }">                                
                                <img src="<crh-common:url value='/images/WordIcon.gif'/>">
								</c:when>
								<c:when test="${entry.dataMimeType.mimeTypeCd == MIME_TYPE_XLS }">                                
                                <img src="<crh-common:url value='/images/ExcelIcon.gif'/>">
								</c:when>				
								<c:otherwise>
									&nbsp;
								</c:otherwise>
							</c:choose>
							<c:if test="${!empty entry.attachmentId }">
                                <a href="/logon/order/blob?keyValue=<c:out value="${entry.attachmentId}"/>&entity=OrderRepAttEntity&keyName=attachmentId" target="_blank">
							</c:if>
                                <c:out value="${entry.attachmentName}"/>
							<c:if test="${!empty entry.attachmentId }">
								</a>                                
							</c:if>
							<c:if test="${empty entry.attachmentId }">							
								&nbsp;<i><fmt:message key="orderdetail.repair.attachment.unsaved" /></i>
							</c:if>								
                                </td><td>
                                <c:if test="${!empty entry.attachmentId }">
                                	 <a onclick="javascript:removeOrderRepAttachment('<c:out value="${entry.attachmentId}"/>','<c:out value="${attType}"/>')"><img src="<crh-common:url value='/images/delete2.png'/>" width="16" height="16" border="0"></a>
                                </c:if>
                                </td>
                              </tr>	
	</c:forEach>
		<c:if test="${!rowPresent}">
			<tr>
			<td>
			<c:out value="${noattachment }"/>
			</td><td></td>
			</tr>
		</c:if>	                   
                              
                            </tbody>
                          </table>
