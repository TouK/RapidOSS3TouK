#!/bin/bash

if [ "$JAVA_HOME" == "" ]; then
	echo "JAVA_HOME is not defined."
	exit 0
fi

if [ ! -f "$JAVA_HOME/bin/java" ]; then
	echo "java executable doesn't exists."
	exit 0
fi

if [ "$RS_HOME" == "" ]; then
	echo "RS_HOME is not defined."
	exit 0
fi

export GRAILS_HOME=$RS_HOME

if [ -f "$RS_HOME/temp/projects" ]
then 
	rm -r $RS_HOME/temp/projects
fi
. $GRAILS_HOME/bin/startGrails
JAVA_OPTS=" -Xmx512m -Ddisable.auto.recompile=true -Dserver.port=12222 -Dgrails.work.dir=$RS_HOME/temp -Dgroovy.sanitized.stacktraces=groovy.,org.codehaus.groovy.,java.,javax.,sun.,gjdk.groovy.,org.springframework.,org.mortbay.,net.sf., -Dgroovy.full.stacktrace=false $JAVA_OPTS"
startGrails com.ifountain.grails.RapidGrailsScriptRunner  "$@"
