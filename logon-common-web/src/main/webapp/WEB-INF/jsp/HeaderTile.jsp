<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: HeaderTile.jsp,v $ --%>

<%@ page session="true" %>

<%@ include file="includeTLDs.jsp" %>

<%@page import="com.crh.logon.web.utils.UserProvider"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page import="com.crh.logon.web.utils.ConfigUtil"%>
<%@ page import="com.crh.logon.business.services.utils.TimeZone" %>

<%
	request.setAttribute("user", UserProvider.getUser());
	request.setAttribute("role", UserProvider.getRole());
%>

<style type="text/css">
div.infoheader {
	color: <%= ConfigBeanProvider.getConfigBean().getAppDefaultHeaderColor() %>;
}
</style>

                      <table width="100%">
                          <tr>
                            <td valign="top" class="applicationheader">
                            <nobr>
                                <div class="infoheader" style="text-align:left;"> 
                                    <logic:present name="org.apache.struts.action.ERROR" scope="request">
                                    	<c:out value="${sessionScope.FatalErrorForm.errorId}" /> | 
                                    </logic:present>
	                                <%
	                                	out.println(ConfigUtil.getApplicationString());
	                                %>
									<span>&nbsp;|&nbsp;</span>
										<%
											java.util.Date now = new java.util.Date();

											TimeZone timezone = UserProvider.getTimezone();
											out.println(timezone.fmtAsDetailedDateTime(now));

										%>
                                    <span>&nbsp;|&nbsp;</span>
	                                    <c:out value="${user.person.firstname}" />
	                                    <c:out value="${user.person.name}" />
	                                    (<%=request.getUserPrincipal().getName() %>)
                                    <span>&nbsp;|&nbsp;</span>
                                        <c:out value="${role.roleName}" />
                                </div>
                              </nobr>
                            </td>
                            <td valign="top" class="applicationheader">
                                <nobr>
                                <div class="infoheader" style="text-align:right;">
                                    <a href="javascript:showPopup('<%=request.getContextPath() %>/Help.do')"><fmt:message key="application.help"/></a>
                                </div>
                                </nobr>
                            </td>
                            <td align="right" valign="top" class="applicationheader">
                                <nobr>
                                <div class="infoheader" style="text-align:right;">
                                    <a href="<%=request.getContextPath() %>/Logout.do"><fmt:message key="application.logout.label"/></a>                                    
                                </div>
                                </nobr>
                            </td>
                            <td width="10" align="right" valign="top" class="applicationheader">
                              &nbsp;
                            </td>
                          </tr>
                      </table>
<%-- END $RCSfile: HeaderTile.jsp,v $ --%>
