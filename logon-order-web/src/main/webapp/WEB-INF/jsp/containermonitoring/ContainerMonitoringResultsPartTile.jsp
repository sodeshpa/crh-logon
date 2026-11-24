<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%><%-- 
BEGIN $RCSfile: BimodalRailcarMonitoringResultsPartTile.jsp,v $ 
--%>
<%@ taglib uri="http://com.crh.logon/tags/order-taglib.tld" prefix="crh-order" %>

<%@ include file="../includeTLDs.jsp" %>
<%@page import="com.crh.logon.persistence.entities.views.ContainerMonitoringEntity" %>

	<table class="classicblue" style="width:100%;margin-top:10px;">
		<tr>
        	<th>
        		<a onclick='setOrderBy("<%=ContainerMonitoringEntity.ATTR_CONTAINER_TERMINAL%>")'><fmt:message key="container.monitoring.container.terminal"/></a>
        	</th>        	
        	<th>
        		<a onclick='setOrderBy("<%=ContainerMonitoringEntity.ATTR_CONTAINER_NAME%>")'><fmt:message key="container.monitoring.container.name"/></a>
        	</th>        	
            <th>
            	<a onclick='setOrderBy("<%=ContainerMonitoringEntity.ATTR_PRODUCTION_PLANT_CD%>")'><fmt:message key="container.monitoring.production.plant"/></a>
            </th> 
            <th>
            	<a onclick='setOrderBy("<%=ContainerMonitoringEntity.ATTR_SAP_MATERIAL_ID%>")'><fmt:message key="container.monitoring.material.qty"/></a>
            </th> 
            <th>
				<a onclick='setOrderBy("<%=ContainerMonitoringEntity.ATTR_LOADING_TIME%>")'><fmt:message key="container.monitoring.loading.time"/></a>
            </th> 
            <th>
            	<a onclick='setOrderBy("<%=ContainerMonitoringEntity.ATTR_ARRIVAL_TIME%>")'><fmt:message key="container.monitoring.arrival.time"/></a>
            </th>
            <th>
            	<a onclick='setOrderBy("<%=ContainerMonitoringEntity.ATTR_STANDING_TIME%>")'><fmt:message key="container.monitoring.standing.time"/></a>
            </th>
       </tr>
      	  
	  <c:set var="zebracount" value="0"/>      	  
		<logic:iterate id="result" name="order_ContainerMonitoringForm" property="resultList"  indexId="i">
		
		<c:set var="zebracount" value="${(zebracount + 1)%2}"/>

		<tr class="zebra<c:out value="${zebracount}" />">
			<td>
				<c:out value="${result.entity.containerTerminal}" />
			</td>
			<td>
            	<c:out value="${result.entity.containerName}"/>
            </td>
            <td>
				<c:out value="${result.prodPlant}"/>
            </td>
            <td>
        		<c:out value="${result.materialAndQty}"/>
            </td> 
      	    <td>
      	    	<c:out value="${result.loadingTime}"/>
      	    </td>
      	    <td>
				<c:out value="${result.arrivalTime}"/>
      	    </td>
      	    <td>
				<c:out value="${result.standingTime}"/>
      	    </td>     	    
        </tr>
        </logic:iterate>
    </table>