<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../includeTLDs.jsp"%>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="MIME_TYPE_PDF"><%=Constant.MIME_TYPE_PDF %></c:set>
<c:set var="MIME_TYPE_DOC"><%=Constant.MIME_TYPE_DOC %></c:set>
<c:set var="MIME_TYPE_XLS"><%=Constant.MIME_TYPE_XLS %></c:set>

		<table class="classic" style="border: none">
                            <tbody>
	<c:set var="zebracount" value="0"/>
		<c:set var="rowPresent">false</c:set>
	<c:forEach var="entry" varStatus="status" items="${order_auftrag_OrderSpecialForm.orderRep.orderRepAttachments}">
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
                                <!--<a href="/logon/order/blob?keyValue=<c:out value="${entry.attachmentId}"/>&entity=OrderRepAttEntity&keyName=attachmentId" target="_blank">-->
                                <c:out value="${entry.attachmentName}"/>
                                <!--</a>-->
                                </td><td>
                                <a onclick="javascript:removeOrderRepAttachment('<c:out value="${status.count-1}"/>')"><img src="<crh-common:url value='/images/delete2.png'/>" width="16" height="16" border="0"></a>
                                </td>
                              </tr>	
	</c:forEach>
		<c:if test="${!rowPresent}">
			<tr>
			<td>
			<fmt:message key="orderdetail.repair.email.no.attachment" />
			</td><td></td>
			</tr>
		</c:if>	                   
                              
                            </tbody>
                          </table>
