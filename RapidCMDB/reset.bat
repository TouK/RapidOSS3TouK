@if "%DEBUG%" == "" @echo off

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

if "%JAVA_HOME%"=="" (
  echo JAVA_HOME is not defined.
  goto:end
)

if "%RS_HOME%"=="" (
  echo RS_HOME is not defined.
  goto:end
)

:begin
set GRAILS_HOME=%RS_HOME%
rmdir /s /q %RS_HOME%\temp\projects
set JAVA_OPTS=%JAVA_OPTS% -Xmx512m
CALL "%GRAILS_HOME%\bin\startGrails.bat" "%GRAILS_HOME%" com.ifountain.grails.RapidGrailsScriptRunner  %* -Dserver.port=12222 -Dgrails.work.dir=%RS_HOME%\temp -Ddisable.auto.recompile=true -Dgroovy.sanitized.stacktraces="groovy.,org.codehaus.groovy.,java.,javax.,sun.,gjdk.groovy.,org.springframework.,org.mortbay.,net.sf.," -Dgroovy.full.stacktrace=false -Dgrails.env=reset

:end