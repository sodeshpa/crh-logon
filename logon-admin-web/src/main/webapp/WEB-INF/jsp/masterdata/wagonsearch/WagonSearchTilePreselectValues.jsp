<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: WagonSearchTilePreselectValues.jsp,v $ --%>
<%@page import="com.crh.logon.admin.web.forms.masterdata.WagonSearchForm"%>
<%@page import="java.util.Map"%>
<%@page import="com.crh.logon.persistence.entities.views.WagonSearchEntity"%>
<%@page import="com.crh.logon.admin.web.beans.WagonSearchBean"%>

	<%
	WagonSearchForm form = (WagonSearchForm)session.getAttribute("admin_WagonSearchForm");
	Map map = form.getSearchBean();
	if (map != null) {%>
		wagonTypeCheckCombo.selectNone();
		orderStatusCheckCombo.selectNone();
		distributionDestCheckComboBox.selectNone();
		wagonInactiveReasonCheckCombo.selectNone();
		<%WagonSearchBean bean = (WagonSearchBean) map.get(WagonSearchEntity.ATTR_WAGON_TYPE_CD);
		if (bean != null && !bean.getId().equals("")){%>
			var selectedValues = '<%=bean.getId()%>'.split(',');
			for (i=0;i<selectedValues.length;i++) {
				wagonTypeCheckCombo.setChecked(selectedValues[i], true);
			}
		<%}%>
		<%bean = (WagonSearchBean) map.get(WagonSearchEntity.ATTR_WAGON_NO);
		if (bean != null && !bean.getId().equals("")){%>
			var defaultData = {
				    wagonId: '<%=bean.getId()%>',
				    wagonNo: '<%=bean.getValue()%>'
			};
			var r = new wagonNoStore.recordType(defaultData, '<%=bean.getId()%>');
			wagonNoStore.insert(0, r);
			wagonNoCombo.preselectValue('<%=bean.getId()%>', false);
		<%}%>
 		<%bean = (WagonSearchBean) map.get(WagonSearchEntity.ATTR_LAST_KNOWN_STATION_ID);
		if (bean != null && !bean.getId().equals("")){%>
			var defaultData = {
				    railwayStationId: '<%=bean.getId()%>',
				    railwayStationName: '<%=bean.getValue()%>'
			};
			var r = new locationStationStore.recordType(defaultData, <%=bean.getId()%>);
			locationStationStore.insert(0, r);
			locationCombo.preselectValue(<%=bean.getId()%>, false);	
		<%}%>
		<%bean = (WagonSearchBean) map.get(WagonSearchEntity.ATTR_WAGON_KEEPER_ID);
		if (bean != null && !bean.getId().equals("")){%>
			var defaultData = {
				    keeperd: '<%=bean.getId()%>',
				    keeperName: '<%=bean.getValue()%>'
			};
			var r = new keeperCombo.store.recordType(defaultData, <%=bean.getId()%>);
			keeperCombo.store.insert(0, r);
			keeperCombo.preselectValue(<%=bean.getId()%>, false);	
		<%}%>
		<%bean = (WagonSearchBean) map.get(WagonSearchEntity.ATTR_ORDER_STATUS_CD);
		if (bean != null && !bean.getId().equals("")){%>
			var selectedValues = '<%=bean.getId()%>'.split(',');
			for (i=0;i<selectedValues.length;i++) {
				orderStatusCheckCombo.setChecked(selectedValues[i], true);
			}
		<%}%>
		<%bean = (WagonSearchBean) map.get(WagonSearchEntity.ATTR_DISTRIBUTION_DEST_CD);
		if (bean != null && !bean.getId().equals("")){%>
			var selectedValues = '<%=bean.getId()%>'.split(',');
			for (i=0;i<selectedValues.length;i++) {
				distributionDestCheckComboBox.setChecked(selectedValues[i], true);
			}
		<%}%>		
		<%bean = (WagonSearchBean) map.get(WagonSearchEntity.ATTR_DEST_RAILWAY_STATION_ID);
		if (bean != null && !bean.getId().equals("")){%>
			var defaultData = {
				    railwayStationId: '<%=bean.getId()%>',
				    railwayStationName: '<%=bean.getValue()%>'
			};
			var r = new keeperCombo.store.recordType(defaultData, <%=bean.getId()%>);
			destRwsCombo.store.insert(0, r);
			destRwsCombo.preselectValue(<%=bean.getId()%>, false);	
		<%}%>
		<%bean = (WagonSearchBean) map.get(WagonSearchEntity.ATTR_WAGON_INACTIVE_REASON_CD);
		if (bean != null && !bean.getId().equals("")){%>
			var selectedValues = '<%=bean.getId()%>'.split(',');
			for (i=0;i<selectedValues.length;i++) {
				wagonInactiveReasonCheckCombo.setChecked(selectedValues[i], true);
			}
		<%}%>	
		<%bean = (WagonSearchBean) map.get(WagonSearchEntity.ATTR_ACTIVE_FLAG);
		if (bean != null && !bean.getId().equals("") && bean.getValue().equals("true")){%>
			wagonInactiveReasonCheckCombo.setChecked('-1', true);
		<%}%>
	<%}%>	

<%-- END $RCSfile: WagonSearchTilePreselectValues.jsp,v $ --%>