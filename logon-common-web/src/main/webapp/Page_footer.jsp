<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: $ --%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://com.crh.logon/tags/fmt.tld" prefix="fmt" %>
<fmt:setBundle basename="LitDict"/>

            </table>
            <!-- End Border Table -->
            <br>
        </div>

        <div class="helpDiv" style="display:none;width:30%;margin-top:50px;margin-left:auto;margin-right:auto;" id="helpDiv">

			<!-- begin Border-Table -->
			<table class="big_form2" cellpadding="0" cellspacing="0" border="0"
				style="padding: 0px; margin: 0px; width: auto;">
				<tr><td>
					<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">
						<tr>
							<th class="classicblue left" style="vertical-align: middle;">
								<fmt:message key="application.help.title.external" />
							</th>
						</tr>
						<tr>
							<td class="label left">
								<fmt:message key="application.help.text.external" />
							</td>
						</tr>
					</table>
				</td></tr>
				<tr><td>
					<table class="classicblue" style="border: none; margin-top: 10px; width: 100%;">
						<tr>
							<th class="classicblue left" style="vertical-align: middle;">
								<fmt:message key="application.help.title.internal" />
							</th>
						</tr>
						<tr>
							<td class="label left">
								<fmt:message key="application.help.text.internal" />
							</td>
						</tr>
					</table>
				</td></tr>
			</table>

        </div>

    </body>
</html>
<%-- END $RCSfile: $ --%>