package build
/**
 * Created by IntelliJ IDEA.
 * User: fadime
 * Date: Jun 8, 2009
 * Time: 10:14:05 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidInsightUiTestBuild extends Build {
    def version = "$env.rapid_insight/RIVersion.txt";
    def versionInBuild = "$env.dist_rapid_suite/RIVersion.txt";
    def riZipFileName;



    static def getTestOptions() {
        Properties options = new Properties();
        options.put("RI_UNIX", "true")
        options.put("RI_WINDOWS", "false")
        options.put("RCMDB_UNIX", "true")
        options.put("RCMDB_WINDOWS", "false")
        options.put("MODELER", "false")
        options.put("OPENNMS", "false")
        options.put("JIRA", "false")
        options.put("APG", "false")
        options.put("NETCOOL", "false")
        options.put("SMARTS", "false")
        options.put("HYPERIC", "false")
        options.put("E_WINDOWS", "false")
        options.put("E_UNIX", "false")
        options.put("ZIP", "true")
        options.put("TEST", "false")
        options.put("JREDIR", "C:/Program Files/Java/jdk1.6.0_04/jre")
        return options;
    }

    static void main(String[] args) {
        RapidInsightUiTestBuild testBuild = new RapidInsightUiTestBuild();
        testBuild.build();
    }

    def build()
    {
        try {

             //startSeleniumServer();
             buildDependentProjects()
              clean();
              setupRi();
            compileUiTestClasses();

            //stopSeleniumServer();
            //startt()
            startRI();

            // def testClassPaths = ["${env.distribution}/uiTestClasses/testUtils"]
            // runTest("${env.distribution}/uiTestClasses/tests", testClassPaths, "${env.distribution}/uiTestResults","testResults")
        }
        finally {
            // stopRI()
            //  stopSeleniumServer()
        }
    }

    def startSeleniumServer()
    {
        ant.java(jar: "${env.third_party}/lib/selenium/selenium-server.jar", fork: "true",
                spawn: "true", jvm: "/usr/java/jdk1.6.0_04/jre/bin/java")
    }

    def stopSeleniumServer()
    {
        ant.target(name: "stop-server") {
            ant.get(taskname: "selenium-shutdown", src: "http://localhost:4444/selenium-server/driver/?cmd=shutDown",
                    dest: "result.txt", ignoreerrors: "true")
            ant.echo(taskname: "selenium-shutdown", message: "DGF Errors during shutdown are expected")
        }
    }


    def startt()
    {
        //  Runtime.getRuntime().exec("C:/Documents and Settings/fadime/Desktop/RapidServer/RapidSuite/rs.exe -start");
        // String[] test = new String[1]
        //   test[0]  ="C:/Documents and Settings/fadime/Desktop/RapidServer/RapidSuite"
        def list = ["./${env.distribution}/RapidServer"]
        Process p = "C:/Documents and Settings/fadime/Desktop/RapidServer/RapidSuite/rs.exe -start".execute(list, null);
        p.waitFor()

        for (int i = 0; i < 0; i++)
        {
            println("u")
            try {
                def url = new URL("http://localhost:12222/RapidSuite")
                p.sleep(60000)
                def content = url.getText()
                break;
            }
            catch (ConnectException e)
            {
                if (i == 5)
                {
                    throw e;
                }
            }
        }
    }


    def startRI()
    {
        Process p = "chmod +x ${env.distribution}/RapidServer/RapidSuite/rs.sh".execute();
        p.consumeProcessOutput(System.out, System.out);
        p.waitFor();

        def list = ["./${env.distribution}/RapidServer"]
        File dir = new File("${env.distribution}/RapidServer/RapidSuite")

        p = "./${env.distribution}/RapidServer/RapidSuite/rs.sh -start".execute(list, dir);

        p.consumeProcessOutput(System.out, System.out);
        p.waitFor();

        for (int i = 0; i < 0; i++)
        {
            println("u")
            try {
                def url = new URL("http://localhost:12222/RapidSuite")
                p.sleep(60000)
                def content = url.getText()
                break;
            }
            catch (ConnectException e)
            {
                if (i == 0)
                {
                    throw e;
                }
            }
        }

    }

    def stopRI()
    {
        Process p = "chmod +x ${env.distribution}/RapidServer/RapidSuite/rs.sh".execute();
        p.consumeProcessOutput();
        p.consumeProcessErrorStream(System.out);
        p.waitFor();
        p = "./${env.distribution}/RapidServer/RapidSuite/rs.sh -stop".execute();
        p.consumeProcessOutput();
        p.consumeProcessErrorStream(System.out);
        p.waitFor();
    }

    def compileUiTestClasses()
    {
        ant.taskdef(name: "groovyc", classname: "org.codehaus.groovy.ant.Groovyc");
        def destDir = "${env.distribution}/uiTestClasses";
        ant.delete(dir: destDir);
        ant.mkdir(dir: destDir);
        ant.mkdir(dir: "${destDir}/testUtils");
        ant.mkdir(dir: "${destDir}/tests");

        ant.copy(file: "${env.rapid_cmdb_commons_cvs}/src/groovy/com/ifountain/rcmdb/test/util/SeleniumTestCase.groovy",
                todir: "${env.distribution}/case")

        ant.groovyc(destdir: "${env.distribution}/uiTestClasses/testUtils",
                classpathref: "classpath",
                srcdir: "${env.distribution}/case");


        ant.groovyc(destdir: "${env.distribution}/uiTestClasses/tests", srcdir: "${env.rapid_cmdb_commons_cvs}/test/ui") {
            ant.classpath {
                ant.path(refid: "classpath")
                ant.path(location: "${env.distribution}/uiTestClasses/testUtils")
            }
        }
        ant.delete(dir: "${env.distribution}/case")

    }

    def setupRi()
    {
        ant.unzip(src: "${env.distribution}/${riZipFileName}", dest: "${env.distribution}");
    }

    def buildDependentProjects()
    {
        clean()
        def options = getTestOptions();
        RapidInsightBuild rapidInsightBuilder = new RapidInsightBuild();
        rapidInsightBuilder.setOptions(options);
        rapidInsightBuilder.build();
        riZipFileName = "RI_Unix${rapidInsightBuilder.getVersionWithDate()}.zip"
    }



    def clean()
    {
        ant.delete(dir: "${env.distribution}/RapidServer");
    }

    def runTest(String testClassDir, List classPaths, String outputXmlDir, String outputXmlFile) {
        //ant.echo(message: "Running all tests for test class " + testClass + " and will output xml results to " + outputXmlDir + "/" + outputXmlFile);
        // ant.delete(dir: outputXmlDir);
        ant.mkdir(dir: outputXmlDir);
        ant.junit(printsummary: "yes", haltonfailure: "no", fork: "false", showoutput: "true") {
            ant.classpath
            {
                ant.path(refid: "classpath");
                ant.path(location: "${testClassDir}");
                classPaths.each {classPathEntry ->
                    ant.path(location: classPathEntry);
                }
            }
            ant.formatter(type: "xml");
            ant.batchtest(fork: "false", todir: outputXmlDir) {
                ant.fileset(dir: "${testClassDir}")
                        {
                            ant.include(name: "**/*Test.class");
                        }
            }

            ant.formatter(type: "xml");
        }
        //   ant.junitreport( todir:"${env.distribution}/uiTestResults"){
        //         ant.fileset(dir:"${env.distribution}/uiTestResults") {
        //                       ant.include(name:"TEST-*.xml");
        //                  }
        //          ant.report(format:"frames",todir:"${env.distribution}/uiTestResults/html")
        // }
        ant.junit(printsummary: "yes", haltonfailure: "no") {
            ant.batchtest(fork: "yes", todir: "${env.distribution}/uiTestResults") {
                ant.fileset(dir: "${env.distribution}/uiTestResults") {
                    ant.include(name: "TEST-*.xml");
                }
            }
        }

    }

}