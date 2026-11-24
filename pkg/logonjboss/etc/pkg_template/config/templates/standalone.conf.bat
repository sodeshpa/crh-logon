set "JAVA_OPTS=-server"
set "JAVA_OPTS=%JAVA_OPTS% -Xms4096m -Xmx4096m -XX:-TieredCompilation -XX:NewRatio=2 -XX:SurvivorRatio=6 -XX:TargetSurvivorRatio=90 -XX:+UseParNewGC -XX:+UseConcMarkSweepGC -XX:+UseCompressedOops -verbose:gc -XX:+UseGCLogFileRotation -XX:GCLogFileSize=2M -XX:NumberOfGCLogFiles=10 -Xloggc:..\standalone\log\gc.log -XX:+PrintGCDateStamps -XX:+PrintGCTimeStamps -XX:+PrintGCDetails -XX:+PrintTenuringDistribution -XX:+CMSClassUnloadingEnabled"
set "JAVA_OPTS=%JAVA_OPTS% -Djava.net.preferIPv4Stack=true -Djboss.modules.system.pkgs=_JBOSSPKGS_ -Djava.awt.headless=true"
set "JAVA_OPTS=%JAVA_OPTS% -Djboss.server.default.config=standalone.xml {{ JAVAAGENT_INSTALL_DIR }} {{ JAVAAGENT }}"
