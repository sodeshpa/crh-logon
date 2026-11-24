<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: TableTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="../includeTLDs.jsp" %>

<c:set var="tableForm" value="${admin_TableViewForm}" scope="request"/>
<c:if test="${referenceSearch}">
	<c:set var="tableForm" value="${admin_ReferenceSearchForm}" scope="request"/>
</c:if>

				<div id="testDiv" class="scrollist" style="width: px;  overflow:auto;">
					<table class="classicblue">
						<tbody>
							<tr>
							<c:if test="${tableForm.updateAllowed || tableForm.deleteAllowed }">
								<th>&nbsp;</th>
							</c:if>
							<c:forEach var="field" items="${tableForm.table.fields}" varStatus="fieldcount">
								<th>
								<c:if test="${referenceSearch}">
										<html:link styleClass="classicblue" href="javascript:hidePopup(); showPopup('ReferenceSearch.do?selectedField=${field.name}','')">
											<c:out value="${field.name}"/>	
										</html:link>
								</c:if>	
								<c:if test="${!referenceSearch}">
										<html:link styleClass="classicblue" page="/tablebrowser/TableView.do?selectedField=${field.name}">	
											<c:out value="${field.name}"/>	
										</html:link>
								</c:if>
								</th>
							</c:forEach>
							</tr>
							<c:forEach var="row" items="${tableForm.rows}" varStatus="status">
							<c:set var="rowclass">zebra1</c:set>
							<c:if test="${status.count % 2 == 0}">
								<c:set var="rowclass">zebra0</c:set>
							</c:if>
							<tr class="<c:out value='${rowclass}'/>">
					
							<c:if test="${(tableForm.updateAllowed || tableForm.deleteAllowed) && !referenceSearch}">
								<td style="width:1px">
									<nobr>
									<c:if test="${tableForm.updateAllowed}">
									<html:link page="/tablebrowser/TableView.do?editRow&rowNr=${status.count - 1}">
									<img src="<crh-common:url value='/images/edit.gif'/>" border="0" alt="">
									</html:link>
									</c:if>
									<c:if test="${tableForm.deleteAllowed}">
									<c:set var="rowKey"></c:set>
									<c:set var="separator"></c:set>
									<c:forEach var="key" items="${tableForm.keyIds}">
										<c:set var="rowKey" value="${rowKey}${separator}${row[key].webValue}"/>
										<c:set var="separator">, </c:set>
									</c:forEach>
									<html:link page="/delete" onclick="deleteRow(${status.count - 1}, '${rowKey}'); return false;">
									<img src="<crh-common:url value='/images/delete.gif'/>" border="0" alt="">
									</html:link>
									</c:if>				
									</nobr>		
								</td>
							</c:if>		
							<c:forEach var="value" items="${row}" varStatus="status">
								<td nowrap="nowrap">
									<c:if test="${referenceSearch && tableForm.table.fields[status.count -1].key}">
										<a href="javascript:<c:out value="${tableForm.setter}"/>('<c:out value="${value.value}"/>', '<c:out value="${row[tableForm.table.refFieldIdx].webValue}"/>');hidePopup();">
									</c:if>
									<c:if test="${value.type.jspRenderer == null}">
										<c:choose>
											<c:when test="${value.webValue == null}">
												<i>NULL</i>
											</c:when>
											<c:otherwise>
												<c:out value="${value.webValue}"/>
											</c:otherwise>
										</c:choose>
									</c:if>
									<c:if test="${value.type.jspRenderer != null}">
										<c:set var="table" scope="request" value="${tableForm.table}"/>
										<c:set var="valueNr" scope="request" value="${status.count - 1}"/>
										<c:set var="row" scope="request" value="${row}"/>
										<c:set var="value" scope="request" value="${value}"/>
										<c:set var="mimeMap" scope="request" value="${tableForm.mimeMap}"/>
										<jsp:include page="<%= ((com.crh.logon.admin.web.beans.ValueBean)pageContext.getAttribute("value")).getType().getJspRenderer() %>"/>
									</c:if>
									<c:if test="${referenceSearch && tableForm.table.fields[status.count -1].key}">
										</a>
									</c:if>
								</td>
							</c:forEach>
							
							</tr>
							</c:forEach>	
						</tbody>
					</table>
				</div>
<%-- END $RCSfile: TableTile.jsp,v $ --%>
