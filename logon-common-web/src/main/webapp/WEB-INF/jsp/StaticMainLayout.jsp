<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<%-- BEGIN $RCSfile: MainLayout.jsp,v $ --%>

<%@page import="com.crh.logon.utils.AuthUtils"%>
<%@ include file="includeTLDs.jsp" %>

<%@ page session="true" %>

<%@page import="com.crh.logon.web.actions.ActionSupport"%>
<%@page import="com.crh.logon.web.utils.UserProvider"%>
<%@page import="com.crh.logon.persistence.entities.admin.UserEntity"%>
<%@page import="com.crh.logon.web.utils.ConfigBeanProvider"%>
<%@page import="com.crh.logon.web.utils.ServiceLocatorProvider"%>

<c:set var="HomeAction" scope="page"><%= ActionSupport.getHomeAction(request.getContextPath()) %></c:set>
<c:set var="commonDatePattern" value="dd/MM/yyyy" scope="request"/> 
<c:set var="commonTimePattern" value="HH:mm" scope="request"/> 

<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html:html> 
    <head>
        <title><%=ConfigBeanProvider.getConfigBean().getAppSystemBrowserwindowtitle() %></title>
        <meta name="robots" content="all">
        <meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
        <%-- FIXME, probably not the contextpath what has to be checked--%>
        <%  String contextPath = ((HttpServletRequest)pageContext.getRequest()).getContextPath();
            if ( !contextPath.contains("cust")){
        %>
        <link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_style.css"/>">
        <%}else{
            %>
        <link rel="stylesheet" type="text/css" href="<crh-common:url value="/css/crh_weborder_style.css"/>">
        <%} %>
        <tiles:insert attribute="jslib"/>
        <tiles:insert attribute="extra_header"/>
        <script type="text/javascript">
        
			Ext.namespace('Logon', 'Logon.util');
			
	        Ext.onReady(function() {
	        
				Ext.QuickTips.init();
				Ext.ux.window.MessageWindow.prototype.shadow = false;
				
		        <c:if test="${not empty LITDICT_INFO_FLAG}">
		        	
		        	cleanButtonLabels = function() {
			        	var btnList = Ext.query('button,input[@type=button],input[@type=submit]');
						for (var i = 0; i < btnList.length; i++) {
						    var val = btnList[i].value;
						    if (val.indexOf('<span ext:qtitle') > -1) {
						        btnList[i].value = val.substring(val.indexOf('>>') + 2, val.indexOf('</span>'));
						    }
						}
		        	}
		        	
		        	cleanButtonLabels();
		        
					Ext.TaskMgr.start({
					    run: cleanButtonLabels,
					    interval: 10000
					});
				</c:if>
				
				Logon.contextPath = '<%=contextPath%>'; 
				
			    Logon.message = function() {
			
			        var loadingMsg = Ext.get('loading-msg');
			        var loadingMsgCt = Ext.get('loading-msg-ct');
			        var loadingMsgLabel = Ext.get('loading-msg-label');
			        var loadingMsgCounter = 0;
					var msgGroup = new Ext.ux.window.MessageWindowGroup({groupId: 1, zseed: 200000});
			    	var msgTypes = {
						'success' : { bg: '#CCEECC', border: '#99BB99', title: '<fmt:message key="messagewindow.title.success"/>', window: null, displayed: false, msgStack: new Array()},
						'info' : { bg: '#B3C2DC', border: '#768AAC', title: '<fmt:message key="messagewindow.title.info"/>', window: null, displayed: false, msgStack: new Array()},
						'warning' : { bg: '#FFEEAA', border: '#CCBB99', title: '<fmt:message key="messagewindow.title.warning"/>', window: null, displayed: false, msgStack: new Array()},
						'error' : { bg: '#FFCCCC', border: '#CC9999', title: '<fmt:message key="messagewindow.title.error"/>', window: null, displayed: false, msgStack: new Array()}
					}
					
					var wndStack = new Array();
					
					var getNextY = function(idx) {
						var y = 10;
						for (var i=0; i<idx; i++) {
							y += wndStack[i].getSize().height + 10;
						}
						return y;
					}
					
	        		// --- Readjust the Y coordinate of the windows (needed when e.g. one is deleted in the middle, or resized ---
			    	var readjustPositions = function() {
		        		for (var i=0; i<wndStack.length; i++) {
		        			var x = 0; 
		        			var y = getNextY(i);
		        			wndStack[i].alignTo(document, 't-t', [x, y]);
		        		}			        		
			    	};
			    	
			    	// --- Using classic javascript event handling. (Attaching the scroll handler for the window object in the "Ext" way causes errors...see LOGON-5476 --- 
			    	// --- Furthermore, 500ms delay is used to prevent repetitive repositioning in case of smooth scrolling ---
				    var windowScrollEnabled = true;
			    	var windowScrollHandlerFn = function() {
				    	loadingMsgCt.alignTo.defer(100, loadingMsgCt, [document, 'tl']);
				    	readjustPositions();
				    	windowScrollEnabled = true;
			    	};
					var addWindowScrollHandler = function(handler) {
						if (window.addEventListener) {
							window.addEventListener("scroll", handler, false);
						}
						else if (window.attachEvent) {
							window.attachEvent('onscroll', handler);
						}
						else {
							window['onscroll'] = handler;
						}
					};
					addWindowScrollHandler(function() {
						if (!windowScrollEnabled) {
							return;
						}
						windowScrollEnabled = false; 
						return setTimeout(windowScrollHandlerFn, 500);
					});
			    
			        var showMessageWindow = function(msgType, msgHtml, wndTitle) {
		        		msgTypes[msgType].msgStack.push(msgHtml);
			        	if (!msgTypes[msgType].displayed) {
				    		w = new Ext.ux.window.MessageWindow({
				    			title: (Ext.isEmpty(wndTitle) ? msgTypes[msgType].title : wndTitle),
				    			autoDestroy: true, // default = true
				                autoHeight: true,
				    			autoHide: (msgType == 'success'),
				                bodyStyle: 'text-align: left; border: 1px solid ' + msgTypes[msgType].border + '; background-color: ' + msgTypes[msgType].bg,
				                closable: true,
				                help: false,
				    			html: msgHtml,
				    			iconCls: 'x-icon-' + msgType,
				    			manager: msgGroup,
				                origin: {
				                	increment: false,
				                	forceY: getNextY.createCallback(wndStack.length),
				                    pos: "t-t",//position to align to (see {@link Ext.Element#alignTo} for more details defaults to "br-br").
				                    offX: 0, //amount to offset horizontally (-20 by default)
				                    offY: 10, //amount to offset vertically (-20 by default)
				                    spaY: 10    //vertical spacing between adjacent messages
				                },
				                pinOnClick: false,
				                showFx: {
				                    align: 't',
				                    delay: 0,
				                    duration: 0.7,
				                    callback: function() {
								        if (this.autoHide) {
								           if (this.pinState === 'unpin') {
								                this.task.delay(this.hideFx.delay);
								            }
								        }
						        		if (wndStack.length > 1) { 
						        			readjustPositions();
						        		}
				                    },
				                    mode: 'standard',// null,'standard','custom',or default ghost
				                    useProxy: false // default is false to hide window instead
				                },
				                hideFx: {
				                    delay: 5000,
				                    duration: 0.4,
				                    //remove:true,
				                    callback: function() {
										msgTypes[msgType].displayed = false;
										msgTypes[msgType].msgStack = [];
										wndStack.remove(this);                  
						        		readjustPositions();
				                    },
				                    mode: 'standard',// null,'standard','custom',or default ghost
				                    useProxy: false // default is false to hide window instead
				                },
				                width: 450 // optional (can also set minWidth which = 200 by default)
				    		});
				    		
			    			w.toFront();	            
		            		w.show(Ext.getDoc());
			    			msgTypes[msgType].displayed = true;
				    		msgTypes[msgType].window = w;
			    			wndStack.push(w);
				    		
			        	}
			        	else {
			        		// --- Appending the message into an existing message box ---
			        		msgTypes[msgType].window.setMessage(msgTypes[msgType].msgStack.join('<br/><hr>'));
			        		readjustPositions();
			        	}
			    	}

					return {
				        info: function(msgHtml, wndTitle) {
				        	showMessageWindow('info', msgHtml, wndTitle);
						},
				        success: function(msgHtml, wndTitle) {
				        	showMessageWindow('success', msgHtml, wndTitle);
						},
				        warning: function(msgHtml, wndTitle) {
				        	showMessageWindow('warning', msgHtml, wndTitle);
						},
				        error: function(msgHtml, wndTitle) {
				        	showMessageWindow('error', msgHtml, wndTitle);
						},
			            showLoading: function(msg) {
					        loadingMsgCounter++;
							loadingMsgLabel.update(msg || window.loadingMessage);
							if (!loadingMsg.isVisible()) { 
								//loadingMsg.slideIn('t', { duration: 0.5 });
								loadingMsg.show();
							}
							window.loadingMessage = 'Loading...';
			            },
			            hideLoading: function() {
					        loadingMsgCounter--;
							if (loadingMsg.isVisible() && loadingMsgCounter == 0) { 
								//loadingMsg.slideOut('t', { duration: 0.5 });
								loadingMsg.hide();
							}
			            }
					}
					
			    }();        
			    
		        <%-- 
				var invalidFieldWrappers = new Array();
				function addExtJsInvalidFieldMark(fieldId, message, msgTarget, isResize) {
					// TODO: gergely finish the impl.
				}  
		        <c:forEach var="mark" varStatus="status" items="${EXTJS_INVALID_MARKS}">
		            addExtJsInvalidFieldMark('<c:out value="${mark.fieldId}"/>', '<fmt:message key="${mark.titleKey}"/>', '<c:out value="${mark.msgTarget}"/>', <c:out value="${mark.resize}"/>);
		        </c:forEach>
		        --%>
		        
		    });
        </script>
    </head>
    <body>
    
		<div id="loading-msg-ct"><div id="loading-msg"><table cellpadding="0" cellspacing="0" align="right"><tr><td id="loading-td-left">&nbsp;</td><td id="loading-td-spinner"></td><td id="loading-td-label"><div id="loading-msg-label"></div></td><td id="loading-td-right">&nbsp;</td></tr></table></div></div>
		
        <div id="grayout" class="grayout"></div>
        <div id="logonPopup" class="popup border"></div>
        <div id="grayout2" class="grayout2"></div>        
        <div id="logonPopup2" class="popup border"></div>        
        <table valign="top" align="center" border="0" cellpadding="0" cellspacing="0" width="100%">
          <tbody>
            <tr>
              <td class="applicationheader"> 
                <table align="center" border="0" cellpadding="0" cellspacing="0" width="100%" class="navigation">
                 <tbody>
                  <tr>
                    <td class="applicationheader" width="10">
                    </td>
                    <td style="padding-left:10px" valign="bottom" width="100%">
<table border="0" width="100%" cellpadding="0" cellspacing="0">
<tr>
<td valign="top" height="30">
			  	  <tiles:insert attribute="header"/>
</td>
</tr>
<tr>
<td>
                      <!-- NAVIGATION -->
                      <tiles:insert attribute="navigation"/>
</td>
</tr>
</table>
                    </td>
                   	<td width="61" align="middle" valign="middle" class="applicationheader"><img src="<crh-common:url value='/images/img_logo_logon.gif'/>" alt="LOGON" width="51" height="42" style="margin: 10px 6px 0px 0px">
                    </td>
                  </tr>
                 </tbody>
                </table>

        <div class="customer_max">
            <!-- begin Border-Table   -->
            <table cellpadding="0" cellspacing="0" border="0" width="100%" style="padding:0px; margin:0px">
            <tr>
            <td width="5">&nbsp;</td>
            <td>
        
            <tiles:insert attribute="errors" />
    
            <!-- START CONTENT -->
            <tiles:insert attribute="body" />
            <!-- END CONTENT -->
            
            </td>
            <td width="5">&nbsp;</td>
            </tr>
            </table>
            <!-- End Border Table -->
            <br>        
        </div>
        
    </body>
</html:html>

<%-- END $RCSfile: MainLayout.jsp,v $ --%>
