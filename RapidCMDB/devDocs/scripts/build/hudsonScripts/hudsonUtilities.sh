#! /bin/sh

tagSvn() {
    tagname=$1
    taglist="svn list http://dev.ifountain.org/repos/os/tags"
    if [ "$taglist" != "${taglist#*$tagname}" ]
      then
         svn delete -m "Deleting obsolete tag" http://dev.ifountain.org/repos/os/tags/$tagname
    fi

    svn mkdir -m "Creating tag $tagname" http://dev.ifountain.org/repos/os/tags/$tagname
    svn copy http://dev.ifountain.org/repos/os/ThirdParty http://dev.ifountain.org/repos/os/tags/$tagname -m "Tagging ThirdParty"
    svn copy http://dev.ifountain.org/repos/os/RapidModules http://dev.ifountain.org/repos/os/tags/$tagname -m "Tagging RapidModules"
    svn copy http://dev.ifountain.org/repos/os/LicencedJars http://dev.ifountain.org/repos/os/tags/$tagname -m "Tagging LicencedJars"
    svn copy http://dev.ifountain.org/repos/os/Netcool http://dev.ifountain.org/repos/os/tags/$tagname -m "Tagging Netcool"
    svn copy http://dev.ifountain.org/repos/os/Smarts http://dev.ifountain.org/repos/os/tags/$tagname -m "Tagging Smarts"
    svn copy http://dev.ifountain.org/repos/os/Hyperic http://dev.ifountain.org/repos/os/tags/$tagname -m "Tagging Hyperic"
    svn copy http://dev.ifountain.org/repos/os/Apg http://dev.ifountain.org/repos/os/tags/$tagname -m "Tagging Apg"
    svn copy http://dev.ifountain.org/repos/os/OpenNms http://dev.ifountain.org/repos/os/tags/$tagname -m "Tagging OpenNms"
}

tagSvnForRapidBrowser() {
    tagname=$1
    taglist=`svn list file:///var/www/svn/os/tags`
    if [ "$taglist" != "${taglist#*$tagname}" ]
      then
         svn delete -m "Deleting obsolete tag" file:///var/www/svn/os/tags/$tagname
    fi

    svn mkdir -m "Creating tag $tagname" file:///var/www/svn/os/tags/$tagname
    svn copy file:///var/www/svn/os/ThirdParty file:///var/www/svn/os/tags/$tagname -m "Tagging ThirdParty"
    svn copy file:///var/www/svn/os/LicencedJars file:///var/www/svn/os/tags/$tagname -m "Tagging LicencedJars"
    svn copy file:///var/www/svn/os/SmartsBrowser file:///var/www/svn/os/tags/$tagname -m "Tagging SmartsBrowser"
}

checkOutTag() {
   LATEST_TAG_FILE="latesttag.txt"
    if [ -f $LATEST_TAG_FILE ]
     then
        TAG_NAME=`cat $LATEST_TAG_FILE`
     else
        echo "$LATEST_TAG_FILE file could not be found !!!!!!!"
        exit 1
    fi
    rm -rf RapidModules
    rm -rf ThirdParty
    rm -rf LicencedJars
    rm -rf Netcool
    rm -rf Smarts
    rm -rf Hyperic
    rm -rf Apg
    rm -rf OpenNms
    svn checkout file:///var/www/svn/os/tags/$TAG_NAME/RapidModules ./RapidModules
    svn checkout file:///var/www/svn/os/tags/$TAG_NAME/ThirdParty ./ThirdParty
    svn checkout file:///var/www/svn/os/tags/$TAG_NAME/LicencedJars ./LicencedJars
    svn checkout file:///var/www/svn/os/tags/$TAG_NAME/Netcool ./Netcool
    svn checkout file:///var/www/svn/os/tags/$TAG_NAME/Smarts ./Smarts
    svn checkout file:///var/www/svn/os/tags/$TAG_NAME/Hyperic ./Hyperic
    svn checkout file:///var/www/svn/os/tags/$TAG_NAME/Apg ./Apg
    svn checkout file:///var/www/svn/os/tags/$TAG_NAME/OpenNms ./OpenNms
}

runTestBuildAndJavaTests(){
  runTestBuild
  cd RapidModules/
  groovy RapidCMDB/devDocs/scripts/build/SmartsModuleTest
  groovy RapidCMDB/devDocs/scripts/build/CoreModuleTest
  groovy RapidCMDB/devDocs/scripts/build/CompModuleTest
  cd $WORKSPACE
}
runTestBuild() {

    rm -rf TestResults/
    mkdir TestResults
    if [ -d ManualTestResults ];
    then
       echo "Copying ManualTestResults to TestResults"
       cp -rf ManualTestResults/ TestResults/
    fi

    cd RapidModules/
    optionsFile=test.options
    rm -f $optionsFile
    echo RI_UNIX=false >> $optionsFile
    echo RI_WINDOWS=true >> $optionsFile
    echo RCMDB_UNIX=true >> $optionsFile
    echo RCMDB_WINDOWS=false >> $optionsFile
    echo MODELER=false >> $optionsFile
    echo SAMPLE1=false >> $optionsFile
    echo SAMPLE2=false >> $optionsFile
    echo ZIP=false>> $optionsFile
    echo APG=true>> $optionsFile
    echo OPENNMS=true >> $optionsFile
    echo HYPERIC=true >> $optionsFile
    echo NETCOOL=true >> $optionsFile
    echo SMARTS=true >> $optionsFile
    echo JIRA=true >> $optionsFile
    echo E_WINDOWS=false >> $optionsFile
    echo E_UNIX=false >> $optionsFile
    echo TEST=true >> $optionsFile
    echo JREDIR=/usr/java/jdk1.6.0_04/jre >> $optionsFile
    groovy RapidCMDB/devDocs/scripts/build/RapidInsightBuild $optionsFile
    cp $WORKSPACE/LicencedJars/lib/jdbc/*.jar $WORKSPACE/Distribution/RapidServer/lib
    cp $WORKSPACE/LicencedJars/lib/smarts/*.jar $WORKSPACE/Distribution/RapidServer/lib
    cd $WORKSPACE
}

runTestBuildAndJavaTestsForRCMDB() {
    rm -rf TestResults/
    mkdir TestResults
    cd RapidModules/
    optionsFile=test.options
    rm -f $optionsFile
    echo RCMDB_UNIX=false >> $optionsFile
    echo RCMDB_WINDOWS=true >> $optionsFile
    echo MODELER=false >> $optionsFile
    echo SAMPLE1=false >> $optionsFile
    echo SAMPLE2=false >> $optionsFile
    echo ZIP=false>> $optionsFile
    echo TEST=true >> $optionsFile
    echo JREDIR=/usr/java/jdk1.6.0_04/jre >> $optionsFile
    groovy RapidCMDB/devDocs/scripts/build/RapidCmdbBuild $optionsFile
    cp $WORKSPACE/LicencedJars/lib/jdbc/*.jar $WORKSPACE/Distribution/RapidServer/lib
    groovy RapidCMDB/devDocs/scripts/build/CoreModuleTest
    groovy RapidCMDB/devDocs/scripts/build/CompModuleTest
    cd $WORKSPACE
}

runGrailsTests() {
#    cp $WORKSPACE/RapidModules/RapidCMDB/devDocs/groovy-starter-for-unit-tests.conf  $WORKSPACE/Distribution/RapidServer/conf/groovy-starter.conf

#    cd $WORKSPACE/Distribution/RapidServer/Modeler/
#    cp $WORKSPACE/RapidModules/RapidCMDB/devDocs/RCMDBTest.properties .
#    chmod +x *.sh
#    rm -rf $WORKSPACE/Distribution/RapidServer/temp
#    ./rsmodeler.sh -testUnit
#    sleep 5
#    rm -r test/reports/TESTS-TestSuites.xml
#    if [ ! -d $WORKSPACE/TestResults/Modeler ]
#      then
#        mkdir $WORKSPACE/TestResults/Modeler
#    fi
#    mv test/reports/*.xml $WORKSPACE/TestResults/Modeler

#    sed -i "s/MAX_MEMORY_SIZE="512"/MAX_MEMORY_SIZE="1024"/g" rsmodeler.sh
#    cp $WORKSPACE/RapidModules/RapidCMDB/devDocs/groovy-starter-for-integration-tests.conf  $WORKSPACE/Distribution/RapidServer/conf/groovy-starter.conf
#    rm -rf $WORKSPACE/Distribution/RapidServer/temp
#    rm -rf $WORKSPACE/Distribution/RapidServer/Modeler/test/unit/*
#    ./rsmodeler.sh -testIntegration

#    sleep 5
#    rm -r test/reports/TESTS-TestSuites.xml
#    if [ ! -d $WORKSPACE/TestResults/Modeler ]
#      then
#        mkdir $WORKSPACE/TestResults/Modeler
#    fi
#    mv test/reports/*.xml $WORKSPACE/TestResults/Modeler

#    cd ../RapidSuite

    cd $WORKSPACE/Distribution/RapidServer/RapidSuite
    cp $WORKSPACE/RapidModules/RapidCMDB/devDocs/RCMDBTest.properties .
    chmod +x rs.sh

    sed -i "s/-Xmx512m/-Xmx1024m/g" rs.sh
    cp $WORKSPACE/RapidModules/RapidCMDB/devDocs/groovy-starter-for-unit-tests.conf  $WORKSPACE/Distribution/RapidServer/conf/groovy-starter.conf
    rm -rf $WORKSPACE/Distribution/RapidServer/temp
    ./rs.sh -testUnit
    rm -r test/reports/TESTS-TestSuites.xml
    if [ ! -d $WORKSPACE/TestResults/RapidSuite ]
      then
        mkdir $WORKSPACE/TestResults/RapidSuite
    fi
    mv test/reports/*.xml  $WORKSPACE/TestResults/RapidSuite
    cp $WORKSPACE/RapidModules/RapidCMDB/devDocs/groovy-starter-for-integration-tests.conf  $WORKSPACE/Distribution/RapidServer/conf/groovy-starter.conf
    rm -rf $WORKSPACE/Distribution/RapidServer/temp
    rm -rf $WORKSPACE/Distribution/RapidServer/RapidSuite/test/unit/*
    ./rs.sh -testIntegration
    rm -r test/reports/TESTS-TestSuites.xml
    if [ ! -d $WORKSPACE/TestResults/RapidSuite ]
      then
        mkdir $WORKSPACE/TestResults/RapidSuite
    fi
    mv test/reports/*.xml  $WORKSPACE/TestResults/RapidSuite
    cd $WORKSPACE
}

generateTestDomainClasses() {

    scriptFileName1="Sample3Setup"
    scriptFileName2="Sample1Setup"
    cp $WORKSPACE/RapidModules/RapidCMDBModeler/scripts/$scriptFileName1.groovy $WORKSPACE/Distribution/RapidServer/Modeler/scripts
    cp $WORKSPACE/RapidModules/RapidCMDBModeler/scripts/$scriptFileName2.groovy $WORKSPACE/Distribution/RapidServer/Modeler/scripts
    cp $WORKSPACE/RapidModules/RapidCMDBModeler/scripts/ModelHelper.groovy $WORKSPACE/Distribution/RapidServer/Modeler/scripts

    rsbatchInput=input.txt
    echo  "/script/save?name=$scriptFileName1" >> $rsbatchInput
    echo  "/script/save?name=$scriptFileName2" >> $rsbatchInput
    echo  "/script/run/$scriptFileName1" >> $rsbatchInput
    echo  "/script/run/$scriptFileName2" >> $rsbatchInput

    #for file in $(find RapidCMDB/scripts -name *Test.groovy)
    #do
    #     filename=${file#RapidCMDB/scripts/*}
    #     echo "$filename"
    #     cp RapidCMDB/scripts/$filename ../Distribution/RapidServer/RapidSuite/scripts
    #     echo "/RapidSuite/script/save?name=$filename" >> $rsbatchInput
    #     echo  "/RapidSuite/script/test?id=$filename" >> $rsbatchInput
    #done

    mv $rsbatchInput $WORKSPACE/Distribution/RapidServer/Modeler

    cd $WORKSPACE/Distribution/RapidServer/Modeler/
    export RS_HOME=$1
    chmod +x *.sh

    sed -i "s/-Dserver.port=12223/-Dserver.port=9999/g" rsmodeler.sh


    ./rsmodeler.sh -start
    dataDir=data
    loopcount=0
    until [ -d $dataDir ]
    do
       if [ $loopcount -gt 300 ]
         then
            ./rsmodeler.sh -stop
            exit 1
       fi
       sleep 1
       loopcount=$(expr $loopcount + 1)
    done
    sleep 20

    cd ../bin
    chmod +x rsbatch.sh
    ./rsbatch.sh -commandfile Modeler/$rsbatchInput -host localhost -port 9999 -username rsadmin -password changeme -application Modeler
    cd ../Modeler

    sleep 2
    ./rsmodeler.sh -stop

    if [ -d test/reports ]
     then
       mv test/reports/*.xml $WORKSPACE/TestResults
    fi

    cd ..
    generatedModelsDir=RapidSuite/generatedModels
    if [ -d $generatedModelsDir ]
      then
          mv $generatedModelsDir/grails-app/domain/*.groovy RapidSuite/grails-app/domain
      else
          echo "Test models couldnot be generated!!!!"
    fi

    rm -rf $WORKSPACE/Distribution/RapidServer/Modeler/data
    cd $WORKSPACE
}

compileBuildFiles() {
    rm -rf $GROOVY_HOME/build
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/Env.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/Parent.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/Build.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/Test.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/SmartsModuleBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/HypericBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/ApgBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/OpenNmsBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/NetcoolModuleBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/JiraPluginBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidInsightPluginBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidCompBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidCoreBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidExtBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidUiPluginBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidCmdbBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidInsightBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/SmartsModuleTest.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/CoreModuleTest.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/CompModuleTest.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidBrowserBuild.groovy
    groovyc -d $GROOVY_HOME/build RapidModules/RapidCMDB/devDocs/scripts/build/RapidInsightUiTestBuild.groovy

}