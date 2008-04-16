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

jars=`ls -d $RS_HOME/lib/*.jar`
rcmdbjars=`ls -d $RS_HOME/RapidCMDB/lib/*.jar`
jars_str=""
for f in $jars; do
	jars_str=$jars_str:$f;
done

for f in $rcmdbjars; do
	jars_str=$jars_str:$f;
done

export JAVACMD=$JAVA_HOME/bin/java
$JAVACMD -cp $jars_str -Drshome=$RS_HOME com.ifountain.rcmdb.cli.RsBatch $*
