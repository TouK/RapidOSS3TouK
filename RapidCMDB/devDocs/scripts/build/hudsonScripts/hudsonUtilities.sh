#!/bin/bash
compileBuildFiles()
{
    rm -rf $GROOVY_HOME/build
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/Env.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/Parent.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/Build.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/Test.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/SmartsModuleBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/NetcoolModuleBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidCompBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidCoreBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidExtBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidUiPluginBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidCmdbBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidInsightForNetcoolBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/SmartsModuleTest.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/CoreModuleTest.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/CompModuleTest.groovy
}