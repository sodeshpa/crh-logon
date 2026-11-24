#!/bin/bash
# If SYNCHOST is passed as command-line argument, rsync operates via SSH.
# If SYNCFAST=true is set in environment, existing traget directories are skipped.

SYNCHOST=$1  # e.g. adnsync
SYNCURL=
[ -z "$SYNCHOST"  ] || SYNCURL="$USER@$SYNCHOST:"

sync() {
	src=${1%/} # src must be without trailing slash
	dst=$2     # if not defined, derive from src
	[ -n "$dst" ] || dst=/local`dirname $src`
	# fast lane
	if [ "$SYNCFAST" = "true" -a -d "/local$src" ]; then
		echo "INFO: /local$src already exists and SYNCFAST=true is set"
	else
		mkdir -p $dst
		cmd="/usr/bin/rsync -az --exclude=mpository --exclude=.svn --exclude=.git $SYNCURL$src $dst"
		echo "##### $cmd"
		$cmd
	fi
}

# lh-logon static project dependencies based on file build/all.properties

sync /share/install/adnant/1.8.16.1
sync /share/install/adnpkg/1.2.0.0
sync /share/install/adnpkg/1.2.0.0/adnpkg
sync /share/install/appweb3/current
sync /share/install/appweb3/current/appweb3
sync /share/install/coverman/1.2.2.0
sync /share/install/gdi/1.0.24.0
sync /share/install/gdi-ddl/1.1.0.0
sync /share/java/jdk/1.8.0_131_x64
sync /share/java/jdk/1.8.0_152_x64
sync /share/java/jpository/adnovum/adnant/1.8.10.0
sync /share/java/jpository/adnovum/adnant/1.8.10.0/adnant/dist
sync /share/java/jpository/adnovum/adnant/1.8.10.0/adnant/dist/aal
sync /share/java/jpository/adnovum/adnsonar/current
sync /share/java/jpository/adnovum/adnsonar/current/adnsonar
sync /share/java/jpository/adnovum/coverman/1.2.2.0/coverman/build/dist
sync /share/java/jpository/adnovum/espace2/2.7.0.0/espace2/dist
sync /share/java/jpository/adnovum/gdi/1.0.24.0/gdi/dist
sync /share/java/jpository/adnovum/gdi-ddl/1.1.0.0/gdi-ddl/dist
sync /share/java/jpository/adnovum/gdi-jpa/1.4.6.0/gdi-jpa/dist
sync /share/java/jpository/adnovum/jcan-commons/1.0.2.0
sync /share/java/jpository/adnovum/jcan-commons/1.0.2.0/jcan/commons/dist
sync /share/java/jpository/adnovum/jcan-log/1.8.1.0
sync /share/java/jpository/adnovum/jcan-log/1.8.1.0/jcan-log/jcan-log-master/dist
sync /share/java/jpository/adnovum/jcan-optrace/1.0.2.0
sync /share/java/jpository/adnovum/jcan-optrace/1.0.2.0/jcan-optrace/dist
sync /share/java/jpository/adnovum/jcan-print/1.2.4.0
sync /share/java/jpository/adnovum/jcan-print/1.2.4.0/jcan-print/dist
sync /share/java/jpository/adnovum/jcan-sectoken/1.2.5.0
sync /share/java/jpository/adnovum/jcan-sectoken/1.2.5.0/jcan-sectoken/dist
sync /share/java/jpository/adnovum/jcan-test/1.4.1.3
sync /share/java/jpository/adnovum/jcan-test/1.4.1.3/jcan-test
sync /share/java/jpository/adnovum/safran/0.6.0.0/safran/dist
sync /share/java/jpository/apache/axis2/1.6.1
sync /share/java/jpository/apache/commons-beanutils/1.7.0
sync /share/java/jpository/apache/commons-cli/1.2
sync /share/java/jpository/apache/commons-io/2.4
sync /share/java/jpository/apache/commons-net/2.0
sync /share/java/jpository/apache/ddlutils/1.0
sync /share/java/jpository/apache/ftpserver/1.0.2
sync /share/java/jpository/apache/maven/3.0.4/dist-bin
sync /share/java/jpository/apache/poi/3.8
sync /share/java/jpository/apache/tomcat/7.0.42
sync /share/java/jpository/apache/xerces/2.9.1
sync /share/java/jpository/apache/xmlbeans/2.4.0/dist-bin
sync /share/java/jpository/aspose/words/13.9.0.0/lib
sync /share/java/jpository/codehaus/groovy/1.6.3
sync /share/java/jpository/codehaus/groovy/1.6.3/dist-bin/embeddable
sync /share/java/jpository/directwebremoting/dwr/2.0.5
sync /share/java/jpository/ext/extjs/2.3
sync /share/java/jpository/googlecode/selenium/2.37.0
sync /share/java/jpository/ibm/icu4j/51.2/dist-bin
sync /share/java/jpository/jakarta/commons-collections/2.1
sync /share/java/jpository/jakarta/commons-dbcp/1.2.1
sync /share/java/jpository/jakarta/commons-lang/2.3
sync /share/java/jpository/jakarta/commons-pool/1.2
sync /share/java/jpository/jakarta/commons-validator/1.1.3
sync /share/java/jpository/jakarta/log4j/1.2.16
sync /share/java/jpository/jakarta/oro/2.0.8
sync /share/java/jpository/jakarta/struts/1.2.9-PATCH-REQUTIL
sync /share/java/jpository/jakarta/taglibs-standard/1.0.6
sync /share/java/jpository/java/javamail/1.4
sync /share/java/jpository/java/jaxb/2.2
sync /share/java/jpository/java/jaxb-interface-insertion
sync /share/java/jpository/java/jaxb-interface-insertion/20100624
sync /share/java/jpository/java-net/hyperjaxb3/0.3
sync /share/java/jpository/java/servlet/2.3
sync /share/java/jpository/jboss/jboss-negotiation/2.0.3.SP3-brew
sync /share/java/jpository/jboss/jboss-web-native/2.0.6-win32/dist-bin
sync /share/java/jpository/mozilla/qae-firefox-profile/24.0.0.0/dist-bin
sync /share/java/jpository/opensource/jboss/4.2.3
sync /share/java/jpository/opensource/jdom/1.0
sync /share/java/jpository/opensource/slf4j/1.6.1
sync /share/java/jpository/oracle/jdbc/11.2.0.1.0
sync /share/java/jpository/ostermiller/utils/1.07.00
sync /share/java/jpository/sourceforge/flexjson/1.6/dist-bin/
sync /share/java/jpository/sourceforge/grinder/3.11
sync /share/java/jpository/sourceforge/hibernate-tools/3.2.4.CR1
sync /share/java/jpository/sourceforge/hsqldb/1.8.0.0
sync /share/java/jpository/sourceforge/jfreechart/1.0.11
sync /share/java/jpository/sourceforge/jsch/0.1.50/
sync /share/java/jpository/sourceforge/opencsv/1.8
sync /share/java/jpository/sourceforge/soapui/4.5.1/dist-bin/
sync /share/java/jpository/sourceforge/spring/1.2.5
sync /share/java/jpository/sun/j2ee/1.5.2
sync /share/java/jpository/yahoo/yui-compressor/2.4.2/dist-bin
sync /share/java/mpository/com/pd4ml
sync /share/java/mpository/org/hibernate/hibernate-core/4.2.1.Final
sync /share/java/mpository/org/hibernate/javax/persistence/hibernate-jpa-2.0-api/1.0.1.Final
sync /share/sw/RedHat/jboss_eap/6.4.10/jboss-eap-6.4.10.zip
sync /share/sw/RedHat/jboss_eap/6.4.14/jboss-eap-6.4.14.zip
sync /share/sw/RedHat/jboss_eap/extensions/rh-sso/rh-sso-7.0.0-saml-eap6-adapter.zip
sync /share/xpository/oracle/instantclient/11.2.0.3
sync /share/java/jpository/jakarta/struts/1.2.9-PATCH-REQUTIL-JAVA5
sync /share/java/jpository/jakarta/struts/1.2.9-PATCH-REQUTIL
sync /share/java/mpository/org/hamcrest/hamcrest-core/1.3
sync /share/java/mpository/org/hibernate/hibernate-entitymanager/4.2.1.Final
sync /share/java/jpository/adnovum/jcan/commons/1.0.2.0
sync /share/java/jpository/adnovum/jcan-commons/1.0.2.0
sync /share/java/mpository/junit/junit/4.11
sync /share/java/mpository/org/mockito/mockito-all/1.9.5

 required for version 3.3
sync /share/install/adnant/1.8.7.2
sync /share/java/jdk/1.7.0_80_x64
sync /share/java/jpository/adnovum/jcan-log/1.7.9.0


CRH specific stuff
sync /share/java/mpository/commons-beanutils/commons-beanutils/1.9.3
sync /share/java/jpository/adnovum/adnant/1.8.17.0
sync /share/java/mpository/commons-collections/commons-collections/3.2.2
sync /share/java/jpository/jakarta/commons-codec/1.3
sync /share/java/mpository/joda-time/joda-time/2.3
sync /share/java/jpository/aspose/words/13.9.0.0
sync /share/java/jpository/adnovum/jcan-print/1.2.0.0
sync /share/sw/RedHat/jboss_eap/6.1.1
sync /share/java/mpository/junit/junit/4.12
sync /share/java/jpository/googlecode/selenium/2.53.0
sync /share/app/mozilla/firefox/24.0_x64
sync /share/java/jdk/1.8.0_92_x64
