<%--
 Copyright (c)     : AdNovum Informatik AG
 Version Number    : $Revision$
 Date of last edit : $Date$
--%>

<!-- BEGIN $RCSfile: DateEditor.jsp,v $ -->

<%@ include file="../includeTLDs.jsp" %>
<%@ page import="com.crh.logon.utils.time.DateTimeFormat" %>

<script type="text/javascript">

    Ext.onReady(function () {
        // prepare date picker
        var picker = new Ext.form.DateField({
            applyTo: 'extdatepicker',
            fieldLabel: 'date',
            format: '<%= DateTimeFormat.getDateFormat4JS(pageContext.getRequest().getLocale()) %>',
    	 altFormats: '<%= DateTimeFormat.getDateAltFormats4JS(pageContext.getRequest().getLocale()) %>',
         allowBlank: true
     }); 
     
 });
 

</script>

<html:text property="value[${valueNr}]" styleId="extdatepicker" value="${value.webValue}"/>
 