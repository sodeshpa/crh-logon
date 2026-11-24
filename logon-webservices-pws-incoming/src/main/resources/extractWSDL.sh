#!/bin/bash

if [ $# -lt 3 ]
then
    echo "Usage: extractWSDL.sh SCHEMA_LOCATION PORT_NAME SERVICE_NAME [SOAP_ADDRESS_BASE]" 1>&2
    exit -1
fi

schemaLocation="$1"
portName="$2"
serviceName="$3"
soapAddress=${4:-https://logon.crh.com/logon/webservices-pws-incoming/services/${serviceName}}

targetNamespace=`sed -n '/targetNamespace/s/.*targetNamespace=\"\(.*\)\".*/\1/p' ${schemaLocation}`

echo '<?xml version="1.0" encoding="UTF-8"?>'
echo '<definitions'
echo "    targetNamespace=\"${targetNamespace}\""
echo "    xmlns:tns=\"${targetNamespace}\""
echo '    xmlns:xsd="http://www.w3.org/2001/XMLSchema"'
echo '    xmlns:soap="http://schemas.xmlsoap.org/wsdl/soap/"'
echo '    xmlns="http://schemas.xmlsoap.org/wsdl/">'
echo
echo '    <types>'
echo "        <xsd:schema elementFormDefault=\"qualified\" targetNamespace=\"${targetNamespace}\">"
echo "            <xsd:include schemaLocation=\"${schemaLocation}\"/>"
echo '        </xsd:schema>'
echo '    </types>'
echo
egrep "^    <xsd:element name=" ${schemaLocation} |\
    sed 's/^    <xsd:element name=\"\(.*\)\">/    <message name=\"\1\">\n        <part name=\"\1\" element=\"tns:\1\"\/>\n    <\/message>/'
echo
echo "    <portType name=\"${portName}\">"
egrep "^    <xsd:element name=".*Request xsd/LogonLDSInterface_services.xsd |\
    sed 's/^    <xsd:element name=\"\(.*\)Request\">/        <operation name=\"\1\">\n            <input message=\"tns:\1Request\"\/>\n            <output message=\"tns:\1\Response\"\/>\n        <\/operation>/'
echo '    </portType>'
echo
echo "    <binding name=\"${portName}\" type=\"tns:${portName}\">"
echo '        <soap:binding style="document" transport="http://schemas.xmlsoap.org/soap/http"/>'
egrep "^    <xsd:element name=".*Request xsd/LogonLDSInterface_services.xsd |\
    sed 's/^    <xsd:element name=\"\(.*\)Request\">/        <operation name=\"\1\">\n            <soap:operation soapAction=\"\1\"\/>\n            <input>\n                <soap:body use=\"literal\" \/>\n            <\/input>\n            <output>\n                <soap:body use=\"literal\" \/>\n            <\/output>\n        <\/operation>/'
echo '    </binding>'
echo
echo "    <service name=\"${serviceName}\">"
echo "        <port name=\"${portName}\" binding=\"tns:${portName}\">"
echo "            <soap:address location=\"${soapAddress}\"/>"
echo '        </port>'
echo '    </service>'
echo
echo '</definitions>'
