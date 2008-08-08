#! /bin/sh
compileBuildFiles() {
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

runTestBuildAndJavaTests() {
    rm -rf TestResults/
    mkdir TestResults
    cd RapidModules/
    groovy RapidCMDB/devDocs/scripts/build/RapidCmdbBuild testBuild
    cp $WORKSPACE/LicencedJars/lib/jdbc/*.jar $WORKSPACE/Distribution/RapidServer/lib
    cp $WORKSPACE/LicencedJars/lib/smarts/*.jar $WORKSPACE/Distribution/RapidServer/lib
    groovy RapidCMDB/devDocs/scripts/build/SmartsModuleTest
    groovy RapidCMDB/devDocs/scripts/build/CoreModuleTest
    groovy RapidCMDB/devDocs/scripts/build/CompModuleTest
    cd $WORKSPACE
}

runGrailsTests() {

    cd $WORKSPACE/Distribution/RapidServer/Modeler/
    cp $WORKSPACE/RapidModules/RapidCMDB/devDocs/RCMDBTest.properties .
    ./rsmodeler.sh -test
    rm -r test/reports/TESTS-TestSuites.xml
    if [ ! -d $WORKSPACE/TestResults/Modeler ]
      then
        mkdir $WORKSPACE/TestResults/Modeler
    fi
    mv test/reports/*.xml $WORKSPACE/TestResults/Modeler

    cd ../RapidSuite
    cp $WORKSPACE/RapidModules/RapidCMDB/devDocs/RCMDBTest.properties .
    chmod +x rs.sh
    ./rs.sh -test
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
    cp $WORKSPACE/RapidCMDBModeler/scripts/$scriptFileName1.groovy $WORKSPACE/Distribution/RapidServer/Modeler/scripts
    cp $WORKSPACE/RapidCMDBModeler/scripts/$scriptFileName2.groovy $WORKSPACE/Distribution/RapidServer/Modeler/scripts
    cp $WORKSPACE/RapidCMDBModeler/scripts/ModelHelper.groovy $WORKSPACE/Distribution/RapidServer/Modeler/scripts

    rsbatchInput=input.txt
    echo  "/Modeler/script/save?name=$scriptFileName1" >> $rsbatchInput
    echo  "/Modeler/script/save?name=$scriptFileName2" >> $rsbatchInput
    echo  "/Modeler/script/run/$scriptFileName1" >> $rsbatchInput
    echo  "/Modeler/script/run/$scriptFileName2" >> $rsbatchInput

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
    ./rsbatch.sh -commandfile Modeler/$rsbatchInput -host localhost -port 9999 -username rsadmin -password changeme
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