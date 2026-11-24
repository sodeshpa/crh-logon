#!/bin/sh

. ./testclient.properties
sh ./soapui/bin/testrunner.sh -h $TESTCLIENT_TARGET_URL -a -fresults project.xml $@
