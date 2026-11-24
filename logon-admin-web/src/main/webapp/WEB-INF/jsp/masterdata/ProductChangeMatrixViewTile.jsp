<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: ProductChangeMatrixViewTile.jsp,v $ --%>
<%@ page session="true"%>

<%@ include file="../includeTLDs.jsp"%>
<%@ taglib uri="http://com.crh.logon/tags/admin-taglib.tld" prefix="crh-admin" %>
<%@page import="com.crh.logon.web.utils.Authorization"%>
<body>
<jsp:include page="../SubTab9Tile.jsp"/>   
<c:set var="form" value="${sessionScope.admin_ProductChangeMatrixViewForm}"/>
<table class="big_form2">
      
      <tr class="classicblue">
      <th class="classicblue"><fmt:message key="tabitem.masterdata"/> / <fmt:message key="tabsubitem.9.4"/></th>
      </tr>
      <tr>
    
      <td>
	<jsp:include page="../Sub4Tab9Tile.jsp"/>
   <table class="big_form2" cellpadding="0" cellspacing="0" border="0"
	style="padding: 0px; margin: 0px; margin-top: 20px;">
   <tr>
   <td>
   <crh-common:authorize function="<%= Authorization.MASTER_DATA_GLOBAL_PRODUCT_CHANGE_MATRIX %>">
	<table class="classicblue" style="width:100%">
		<tr>
        	<th>
        		<fmt:message key="productchangematrixview.columns.material"/>
        	</th>
        	<th>
        		<fmt:message key="productchangematrixview.columns.compat.comment"/>
        	</th>        	
        	<th>
        		<fmt:message key="productchangematrixview.columns.incompat.comment"/>
        	</th>        	
            <th>
            	<fmt:message key="productchangematrixview.columns.threshold.lower"/>
            </th> 
            <th>
            	<fmt:message key="productchangematrixview.columns.threshold.upper"/>
            </th> 
            <th>
				<fmt:message key="productchangematrixview.columns.topmaterial"/>
            </th> 
            <th>
            	<fmt:message key="productchangematrixview.columns.compatrulecomment"/>
            </th>
      	  </tr>
	<c:set var="zebracount" value="0"/>      	  
		<logic:iterate id="compat" name="admin_ProductChangeMatrixViewForm" property="compats"  indexId="i">
		
		<crh-admin:productChangeMatrixTd itemIndex="<%=(Integer)pageContext.getAttribute("i") %>" printTdTag="false">
			<c:set var="zebracount" value="${(zebracount + 1)%2}"/>
		</crh-admin:productChangeMatrixTd>
		<tr class="zebra<c:out value="${zebracount}" />">
			<crh-admin:productChangeMatrixTd itemIndex="<%=(Integer)pageContext.getAttribute("i") %>" printTdTag="true">
        		<c:out value="${compat.fromSapMaterialNo}"/>&nbsp;<c:out value="${compat.fromSapMaterialTxt}"/> 
			</crh-admin:productChangeMatrixTd>
			<crh-admin:productChangeMatrixTd itemIndex="<%=(Integer)pageContext.getAttribute("i") %>" printTdTag="true">
        		<c:out value="${compat.compatComment}"/>
			</crh-admin:productChangeMatrixTd>
            <crh-admin:productChangeMatrixTd itemIndex="<%=(Integer)pageContext.getAttribute("i") %>" printTdTag="true">
            	<c:out value="${compat.incompatComment}"/>
            </crh-admin:productChangeMatrixTd>
            <crh-admin:productChangeMatrixTd itemIndex="<%=(Integer)pageContext.getAttribute("i") %>" printTdTag="true">
            	<c:out value="${compat.insignificantThresholdPerc}"/> %
            </crh-admin:productChangeMatrixTd>
            <crh-admin:productChangeMatrixTd itemIndex="<%=(Integer)pageContext.getAttribute("i") %>" printTdTag="true">
				<c:out value="${compat.forceIdenticalThresholdPerc}"/> %
            </crh-admin:productChangeMatrixTd>
            <td>
        		<c:out value="${compat.toSapMaterialNo}"/>&nbsp;<c:out value="${compat.toSapMaterialName}"/>
            </td> 
      	    <td>
      	    	<c:out value="${compat.matCompatMatComment}"/>
      	    </td>
        </tr>
        </logic:iterate>
        </table>
        </crh-common:authorize>
        
        
      </td>
      </tr>
    </table>

</td>
</tr>
</table>
</body>
</html>
<%-- END $RCSfile: ProductChangeMatrixViewTile.jsp,v $ --%>
