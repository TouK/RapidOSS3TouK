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
KILL_TIMEOUT=120
MAX_MEMORY_SIZE="512"				#max heap size for JVM

JAVA_OPTS=" -Xmx"$MAX_MEMORY_SIZE"m -Ddisable.auto.recompile=true -Dserver.port=12223 -Dgrails.work.dir=$RS_HOME/temp/modeler -Dgrails.env=production -Dgroovy.sanitized.stacktraces=groovy.,org.codehaus.groovy.,java.,javax.,sun.,gjdk.groovy.,org.springframework.,org.mortbay.,net.sf., -Dgroovy.full.stacktrace=false -Drs.modeler=true $JAVA_OPTS"

PIDDIR=$RS_HOME/bin/pids  ##pid directory
PIDFILE=$PIDDIR/rsmodelerpid ##pid file

pid=""

start() {
 #
 # check to see whether pid directory exists
 #
 if [ ! -d $PIDDIR ]
 then
   `mkdir $PIDDIR`
 fi

 getpid
  if [ "X$pid" = "X" ]
     then
     rm -f logs/RapidServerOut.log
     rm -f logs/RapidServerErr.log
     if [ ! -d logs ]
      then
       mkdir logs
      fi
   	##starts the RS service
   	. $GRAILS_HOME/bin/startGrails run-app
	startGrails com.ifountain.grails.RapidGrailsScriptRunner run-app > logs/RapidServerOut.log  2> logs/RapidServerErr.log &
   echo $! >> $PIDFILE

 else
  echo "AlreadyRunning"
 fi
}
testApp() {
   	. $GRAILS_HOME/bin/startGrails test-app
	startGrails com.ifountain.grails.RapidGrailsScriptRunner test-app
}
stop(){

 if test -d $PIDDIR
 then
  if test -f $PIDFILE
  then
   PID=`/bin/cat $PIDFILE`
  else
   echo "Error : Process ID file deleted OR service is not running"
   exit
  fi
 else
  echo "Error : Unable to open process id directory"
  exit
 fi

 cycle_kill $PID
 rm -f $PIDFILE
}

cycle_kill()
{
  processId=$1
  returnValue=0
  kill -HUP $processId
  stopped=0;
  for (( i=0 ;  i<KILL_TIMEOUT ;  i++ ))
  do
    foundPid=`ps -p $processId | grep $processId | grep -v grep | awk '{print $1}' | tail -1`
    if [ "X$foundPid" = "X$processId" ]
    then
      sleep 1
    else
      stopped=1
      break
    fi
  done
  if [ $stopped -eq 0 ]
  then
    kill -9 $processId
  fi
}

getpid(){

 if [ -f $PIDFILE ]
 then
  if [ -r $PIDFILE ]
  then
   pid=`cat $PIDFILE`
   if [ "X$pid" != "X" ]
   then

    # Verify that a process with this pid is still running.
    pid=`ps -p $pid | grep $pid | grep -v grep | awk '{print $1}' | tail -1`

    if [ "X$pid" = "X" ]
    then
     # This is a stale pid file.
     rm -f $PIDFILE
     #echo "Removed stale pid file: $PIDFILE"
    fi
   fi
   else
    echo "Error : Cannot read $PIDFILE."
   exit 1
  fi
 fi

}
status(){
 getpid
 if [ "X$pid" = "X" ]
     then
         echo "NotRunning"
          exit 1
    else
         echo "Running ($pid)"
         exit 0
     fi
}

case "$1" in

     '-start')
      start
  ;;
  
   '-test')
      testApp
  ;;

     '-stop')
         stop
  ;;
 '-restart')
  stop
  start
  ;;
 '-status')
  status
  ;;

    *)
        echo "usage :: rs.sh -start | -stop "
        exit 1
        ;;
esac



