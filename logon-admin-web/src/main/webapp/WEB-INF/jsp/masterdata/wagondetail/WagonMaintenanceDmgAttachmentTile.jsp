<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%@ page session="true"%>
<%@ include file="../../includeTLDs.jsp"%>
<%@page import="com.crh.logon.constants.Constant"%>

<c:set var="MIME_TYPE_PDF"><%=Constant.MIME_TYPE_PDF %></c:set>
<c:set var="MIME_TYPE_DOC"><%=Constant.MIME_TYPE_DOC %></c:set>
<c:set var="MIME_TYPE_XLS"><%=Constant.MIME_TYPE_XLS %></c:set>


<c:set var="ORDER_REP_ATTACHMENT_TYPE_EMAIL"><%=Constant.WAGON_MAINT_ATTACHMENT_TYPE_EMAIL %></c:set>

<c:set var="list" value="${admin_WagonDetailForm.wagonDetailMaintenance.emailAttachments}"/>
<c:set var="noattachment"><fmt:message key="wagondetail.maint.email.no.attachment" /></c:set>
<c:set var="attType"><%=Constant.WAGON_MAINT_ATTACHMENT_TYPE_EMAIL %></c:set>

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
                                <a href="/logon/order/blob?keyValue=<c:out value="${entry.attachmentId}"/>&entity=WagonRepAttEntity&keyName=attachmentId" target="_blank">
							</c:if>
                                <c:out value="${entry.attachmentName}"/>
							<c:if test="${!empty entry.attachmentId }">
								</a>                                
							</c:if>
							<c:if test="${empty entry.attachmentId }">							
								&nbsp;<i><fmt:message key="wagondetail.maint.attachment.unsaved" /></i>
							</c:if>								
                                </td><td>
                                <c:if test="${!empty entry.attachmentId }">
                                	 <a onclick="javascript:removeWagonMaintAttachment('<c:out value="${entry.attachmentId}"/>','<c:out value="${attType}"/>')"><img src="<crh-common:url value='/images/delete2.png'/>" width="16" height="16" border="0"></a>
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
