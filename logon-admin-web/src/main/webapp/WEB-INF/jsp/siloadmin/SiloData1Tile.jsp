<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.admin.web.actions.siloadmin.SiloAdminAction"%>
<%@page import="com.crh.logon.admin.web.forms.siloadmin.SiloAdminForm"%>
<%@ page import="com.crh.logon.taglib.fallbacklogic.DefaultFallBackLogicTag" %>
                          <%@page import="com.crh.logon.persistence.entities.refdata.SiloTypeTxEntity"%>
<%@ page import="com.crh.logon.admin.web.forms.AdminRFIDPopupForm" %>
					
<%@page import="com.crh.logon.persistence.entities.common.ShiptoSiloEntity"%>
<%@page import="com.crh.logon.persistence.entities.common.ShiptoEntity"%><p class="subtitle1"><fmt:message key="silomasterdata.labels.masterdata"/></p>
                    <div class="subform" style="margin:4px 0px 4px 0px;padding:0px;">
                      <table border="0" cellspacing="0" cellpadding="0" class="noborder" style="height:160px; width:100%">
                        <colgroup>
                        <col width="20%">
                        <col width="40%">
                        <col width="30%">
                        </colgroup>
                        <tr>
						  <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="silomasterdata.fields.status"/></td>
                          <td colspan="2" class="content"  style="border:0px;"><input type="checkbox" value="1" name="inputStatus" id="inputStatus" <c:if test="${admin_SiloAdminForm.inputStatus}">checked</c:if>/></td>                        
                        </tr>
                        <tr>
                          <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="silomasterdata.fields.siloname"/></td>
                          <td colspan="2"  class="content"  style="white-space:nowrap;border:0px;"><input type="text" name="inputSiloName" id="inputSiloName" class="bordertextfield"   style="width:100%;" maxlength="50" value='<c:out value="${admin_SiloAdminForm.inputSiloName}"/>'/></td>
                        </tr>
                        <tr>
                          <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="silomasterdata.fields.siloart"/></td>
                          <td colspan="2" class="content" style="white-space:nowrap;border:0px;">
					<select onchange="" name="inputSiloType" id="inputSiloType" style="width:auto;">
					<c:forEach var="entry" items="${admin_SiloAdminForm.siloTypeList}">
					<%String entrySiloTypeCd = ((SiloTypeTxEntity)pageContext.getAttribute("entry")).getSiloTypeTxPK().getSiloTypeBs().getSiloTypeCd().toString();%>
					<option value="<%=entrySiloTypeCd%>" <%=( 
							((SiloAdminForm)session.getAttribute("admin_SiloAdminForm")).getInputSiloType() != null&&  
							entrySiloTypeCd.equals(((SiloAdminForm)session.getAttribute("admin_SiloAdminForm")).getInputSiloType().toString() )
							) ? "selected" : ""%> ><c:out value="${entry.txt}"/></option>
					</c:forEach></select></td>
                        </tr>
                        <tr>
                          <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="silomasterdata.fields.volumenm3"/></td>
                          <td colspan="2"  class="content" style="white-space:nowrap;border:0px;"><input type="text" name="inputMaxVolumeM3" id="inputMaxVolumeM3" value='<c:out value="${admin_SiloAdminForm.inputMaxVolumeM3}"/>' style="width:100%;" maxlength="8" class="bordertextfield" />
                        </tr>
                        <tr>
                         <td valign="bottom" class="label" style="border:0px;"><fmt:message key="silomasterdata.fields.volument"/></td>
                         <td colspan="2" class="content" style="white-space:nowrap;border:0px;"><input type="text" name="inputMaxVolumeT" id="inputMaxVolumeT" value='<c:out value="${admin_SiloAdminForm.inputMaxVolumeT}"/>' class="bordertextfield" style="width:100%;" maxlength="4" class="bordertextfield" /></td>                       
                        </tr>
                        <tr>
	<%		
	ShiptoSiloEntity silo = new ShiptoSiloEntity();
	String unloadingTime = ((SiloAdminForm)request.getSession().getAttribute("admin_SiloAdminForm")).getInputUnloadingTime();
	pageContext.setAttribute("unloadingTime",unloadingTime );
	silo.setShipto(((SiloAdminForm)request.getSession().getAttribute("admin_SiloAdminForm")).getShipTo());
	pageContext.setAttribute("silo",silo );
	%>                    
	<crh-common:defaultFallback propertyValue = "<%=(String)pageContext.getAttribute("unloadingTime")%>" propertyName="<%=DefaultFallBackLogicTag.PROPERTY_DEFAULT_SILO_UNLOADING_TIME_MIN %>" propertyInputFieldName="inputUnloadingTime" propertyCheckboxName="unloadingTimeCheckbox" objectName="silo">
			<td>
			<span class="label" style="white-space:nowrap;border:0px;"><fmt:message key="silomasterdata.fields.loadingtime"/></span>
			</td>
			<td>
			   <span class="content" style="white-space:nowrap;border:0px;">
			   <crh-common:defaultFallbackInputField maxLength="10" styleClass="bordertextfield" style="width:100%;"/>
				</span>
			</td>			
			<td>
				<span class="label" style="white-space:nowrap;border:0px;">
				<fmt:message key="dispokarte.texte.entladezeitflag"/>
				</span>
			 	<span class="content" style="white-space:nowrap;border:0px;">
				<crh-common:defaultFallbackCheckbox/>
				</span>
			</td>
	</crh-common:defaultFallback>
                        </tr>
                        <tr>
                          <td valign="bottom" class="label" style="white-space:nowrap;border:0px;"><fmt:message key="silomasterdata.fields.product"/></td>
                          <td colspan="2"  class="content"  style="white-space:nowrap;border:0px;"><c:if test="${!empty admin_SiloAdminForm.materialTxt}"><c:out value="${admin_SiloAdminForm.materialTxt}"/></c:if> <c:if test="${empty admin_SiloAdminForm.materialTxt}">-</c:if></td>
                        </tr>                        
                      </table>
					</div>
                                        
                    <br>
                    <div class="subform" style="margin:4px 0px 0px 0px;padding:0px; border:0px;">
                      <table border="0" cellspacing="0" cellpadding="0" class="noborder" style="width:100%; padding:0px;">
                        <colgroup>
                        <col width="30%">
                        <col width="70%">
                        </colgroup>
                        <tr>
                          <td valign="bottom" class="label" style="white-space:nowrap;border:0px; padding:0px;"><fmt:message key="silomasterdata.fields.siloassignment"/></td>
                          <td class="content" style="border:0px; text-align:right"><input onclick='showPopup("SiloAdmin.do?inputAction=<%=SiloAdminAction.FORWARD_SHIPTO_POPUP %>")' type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.change"/>" alt="aufheben">&nbsp;
                          <input onclick="removeShipto();" type="button" class="SmallButtonStyle" value="<fmt:message key="buttons.resolve"/>" alt="aufheben"></td>
                        </tr>
                        <tr>
                          <td colspan="2" valign="bottom" class="label" style="white-space:nowrap;border:0px;"><span class="content">
                            <c:out value="${admin_SiloAdminForm.shipTo.shiptoSapBp.name1}"/>
                          </span></td>
                          </tr>
                      </table>
                      <br>
                  </div>
                                      
                    <span class="label"><fmt:message key="silomasterdata.fields.rfid"/></span><br>
                      <!-- beginn RFID -->
                          <div class="subformlist" id="<%=AdminRFIDPopupForm.NAME_OF_HTML_DIV_TO_RELOAD%>" style="clear:both;font-size:12px; margin:0px 4px 0px 4px;">
<jsp:include page="../AdminRfidsTile.jsp"/>    
                          </div>
                          <div align="right">
                            <input onclick="javascript:updatenameShowRFIDPopup();" type="button" class="SmallButtonStyle" value=" <fmt:message key="buttons.add"/> ">
                          </div>
                      <!-- End RFID -->
                          <div style="padding:4px;"> <span class="label"><fmt:message key="silomasterdata.fields.comment"/></span><br>
                              <textarea id="inputComment" name="inputComment" rows="5"  onkeypress="return checkMaxLength(event, this, 199)" class="textfield"  cols="1" style="width: 100%;" maxlength="200"><c:out value="${admin_SiloAdminForm.inputComment}" /></textarea>
                          </div>

