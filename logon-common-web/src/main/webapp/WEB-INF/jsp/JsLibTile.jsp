<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>
<!-- BEGIN $RCSfile: JsLibTile.jsp,v $ -->
<%@ include file="includeTLDs.jsp" %>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page import="com.crh.logon.constants.Constant"%>
		<script type="text/javascript"
				src="<crh-common:url value="/js/litdict/LitDict_${pageContext.request.locale.language}.js"/>"></script>
		<% if (ConfigBeanProvider.getConfigBean().getAppMonitoringAppdynamicsKey() != null) { %>
			<script>
				window["adrum-app-key"] = "<%= ConfigBeanProvider.getConfigBean().getAppMonitoringAppdynamicsKey() %>";
				window["adrum-app-beacon-url-https"] = "<%= ConfigBeanProvider.getConfigBean().getAppMonitoringAppdynamicsBeaconUrlHttps() %>";
				window['adrum-start-time'] = new Date().getTime();
			</script>
			<script type="text/javascript" src="<crh-common:url value="/js/appdynamics/adrum.js"/>"></script>
		<% } %>

        <link rel="stylesheet" type="text/css" href="<crh-common:url value='/css/ext-all.css'/>"/>
		<script type="text/javascript">
			var logonBuildVersion = '<%= ConfigBeanProvider.getConfigBean().getLogonProperties().getBuildVersion() %>';
			var gMapApiBaseUrl = '<%= ConfigBeanProvider.getConfigBean().getGmapApiBaseUrl() %>';
			var gmapApiKey =  '<%= Constant.GMAPS_API_CRH_PRIVATE_KEY %>';
			var updaterIntermediateMsgText = '<fmt:message key="message.still_loading"/>';
			var loadingMsgText = '<fmt:message key="extjs.loading"/>';
		</script>

        <script type="text/javascript" src="<crh-common:url value="/js/adapter/ext/ext-base.js"/>"></script>
        <script type="text/javascript" src="<crh-common:url value="/js/ext-all.js"/>"></script>
	    <script type="text/javascript" src="<crh-common:url value="/js/logon.js"/>"></script>

		<%-- Ext.ux (user extension) components --%>
        <script type="text/javascript" src="<crh-common:url value="/js/ext-ux/Ext.ux.window.MessageWindow.js"/>"></script>

        <%-- Proxy object for DWR --%>
        <script type="text/javascript" src="<crh-common:url value="/js/ext-data-dwrproxy.js"/>"></script>
        <script type="text/javascript" src="<crh-common:url value="/js/ext-data-dwrproxy-for-pager.js"/>"></script>
        
        <%-- DWR services --%>
        <script type="text/javascript" src="<c:url value="/dwr/engine.js"/>"></script> <%-- engine.js is not cached (yet) --%>
        <script type="text/javascript" src="<crh-common:url value="/dwr/util.js"/>"></script>
        
        <%-- http://extjs.com/learn/Ext_FAQ#My_code_links_to_extjs.com.2Fs.gif --%>
        <script type="text/javascript">
            Ext.BLANK_IMAGE_URL = '<crh-common:url value="/images/default/s.gif"/>';
        </script>

        <%-- DWR Error / Warning / Loading handlers --%>
		<script type="text/javascript">

			loadingMessage = loadingMsgText;

            if(Ext.isChrome===true){
                var chromeDatePickerCSS = ".x-date-picker {border: 1px solid #1b376c;border-top:0 none;background:#fff;position:relative;width: 185px;}";
                Ext.util.CSS.createStyleSheet(chromeDatePickerCSS,'chromeDatePickerStyle');
            }
			
			customeDwrEngineExceptionHandler = null;
	        dwr.engine.setPreHook(function() {
	        	if (Logon && Logon.message) {
	            	Logon.message.showLoading(loadingMessage);
	            }
	        });
	
	        dwr.engine.setPostHook(function() {
	        	if (Logon && Logon.message) {	        
        			Logon.message.hideLoading();
        		}
	        });

            dwr.engine.setTextHtmlHandler(function(res) {
                if (res.responseText.indexOf('LOGIN_PAGE' + '474c6ecd1b22f9236780b5bb2d3b9944') > -1) {
                    window.location = '<c:out value="${pageContext.request.contextPath}" />/LoginInactivity.jsp';
                }
            }); 

	        Ext.Ajax.on('beforerequest', function(conn, opts) {
	            Logon.message.showLoading(loadingMessage);
	       	}, this);
	       	
	        Ext.Ajax.on('requestcomplete', function(conn, res, opts) {
        		Logon.message.hideLoading();
                if (res.responseText.indexOf('LOGIN_PAGE' + '474c6ecd1b22f9236780b5bb2d3b9944') > -1) {
                    window.location = '<c:out value="${pageContext.request.contextPath}" />/LoginInactivity.jsp';
                }
	       	}, this);

	    	Ext.getBody().on('unload', function() { Logon.message.showLoading(loadingMessage); });
			
			dwr.engine.setErrorHandler(function(err, exc, c) {
<% if (ConfigBeanProvider.getConfigBean().getLogonProperties().getDevmode()) { %>
// 				if (typeof console !== undefined) {
// 					console.log(err);
// 					console.log(exc);
// 				}
				var errorDiv = Ext.get('errorDiv');
				if (errorDiv !== undefined && errorDiv !== null) {
					if (exc.message == 'response is not defined') {
						errorTplNullRes.overwrite(errorDiv, 
							{ 
								datetime: (new Date().format('y.m.d H:i'))
							}
						);			
					}
					else {
						var errorMsg = '\"' + err + '\" <fmt:message key="fatalerror.helpmsg"/>';
						if (exc.id) {
							errorMsg += ' <fmt:message key="fatalerror.ref"/>: ' + exc.id;
						}
						
						errorTpl.overwrite(errorDiv, 
							{ 
								error: errorMsg,
								exception: exc, 
								datetime: (new Date().format('y.m.d H:i'))
							}
						);
					}
				}
<% } %>
				var errorMsg = '\"' + ((exc.errorDesc)?exc.errorDesc:err) + '\" <fmt:message key="fatalerror.helpmsg"/>';
				if (exc.id) {
					errorMsg += ' <fmt:message key="fatalerror.ref"/>: ' + exc.id;
				}
				Logon.message.error(errorMsg);
				if (customeDwrEngineExceptionHandler) {
					customeDwrEngineExceptionHandler(err, exc, c);
				}
			});
			function setCustomeDwrEngineExceptionHandler(f) {

				if ( (customeDwrEngineExceptionHandler) && (f) ) {

					Logon.message.error('\"' + '<fmt:message key="fatalerror.unknownerror"/>' + '\" <fmt:message key="fatalerror.helpmsg"/> <fmt:message key="fatalerror.ref"/>: ' + 'exchandler-already-defined'); 
				}
				customeDwrEngineExceptionHandler = f;
			}
		</script>
    