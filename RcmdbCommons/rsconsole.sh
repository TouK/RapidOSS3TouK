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
. $GRAILS_HOME/bin/startGrails
startGrails com.ifountain.grails.RapidGrailsScriptRunner  "$@" -Dgrails.work.dir=$RS_HOME/temp
