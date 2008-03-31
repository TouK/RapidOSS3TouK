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
. "%GRAILS_HOME%\bin\startGrails" "%GRAILS_HOME%" org.codehaus.groovy.grails.cli.GrailsScriptRunner  %* -Dserver.port=12222