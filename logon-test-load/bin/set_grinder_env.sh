#!/bin/bash

GRINDERPATH='/share/java/jpository/sourceforge/grinder/3.11'
GRINDERPROPERTIES=etc/grinder.properties
CLASSPATH=$GRINDERPATH'/dist-bin/lib/*:build/staging/lib/*'
JAVA_HOME=/share/java/jdk/1.6.0_24
PATH=$JAVA_HOME/bin:$PATH
export CLASSPATH PATH GRINDERPROPERTIES
