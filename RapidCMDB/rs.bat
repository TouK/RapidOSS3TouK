@if "%DEBUG%" == "" @echo off

@rem 
@rem $Revision: 2770 $ $Date: 2005-08-29 10:49:42 +0000 (Mon, 29 Aug 2005) $
@rem 

@rem Set local scope for the variables with windows NT shell
if "%OS%"=="Windows_NT" setlocal

:begin
set GRAILS_HOME=%RS_HOME%
CALL "%GRAILS_HOME%\bin\startGrails.bat" "%GRAILS_HOME%" org.codehaus.groovy.grails.cli.GrailsScriptRunner  %* -Dserver.port=12222