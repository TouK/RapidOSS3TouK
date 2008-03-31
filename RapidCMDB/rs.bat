@if "%DEBUG%" == "" @echo off

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

:begin
set GRAILS_HOME=%RS_HOME%
rm -r %RS_HOME%\temp
CALL "%GRAILS_HOME%\bin\startGrails.bat" "%GRAILS_HOME%" org.codehaus.groovy.grails.cli.GrailsScriptRunner  %* -Dserver.port=12222 -Dgrails.work.dir=%RS_HOME%\temp