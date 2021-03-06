package build
/**
 * Created by IntelliJ IDEA.
 * User: fadime
 * Date: Jun 8, 2009
 * Time: 10:14:05 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidInsightUiTestBuild extends Build {
    
    def version = "$env.rapid_insight/ROSSVersion.txt";
    def versionInBuild = "$env.dist_rapid_suite/ROSSVersion.txt";
    def riZipFileName;
    static def buildOption;
    boolean RI_UNIX_OS, RI_WINDOWS_OS

    def setOption(options) {
        if (options != null) {
            buildOption = options;
            RI_UNIX_OS = Boolean.parseBoolean(options.get("RI_UNIX", "false"));
            RI_WINDOWS_OS = Boolean.parseBoolean(options.get("RI_WINDOWS", "true"));
        }
    }

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
        options.put("TEST", "true")
        options.put("JREDIR", "C:/Program Files/Java/jdk1.6.0_04/jre")
        return options;
    }


    static void main(String[] args) {
        RapidInsightUiTestBuild testBuild = new RapidInsightUiTestBuild();
        testBuild.findOs()
        testBuild.build();
    }

    def findOs() {
        def options = getTestOptions();
        setOption(options);
    }


    def build()
    {
        try {
            buildDependentProjects()
            clean();
            setupRi();
            compileUiTestClasses();

            if (RI_UNIX_OS)
                startRIUnix();

            if (RI_WINDOWS_OS)
                startRIWindows();

            def testClassPaths = ["${env.distribution}/uiTestClasses/testUtils"]
             ant.copy(file: "${env.rapid_cmdb_cvs}/devDocs/Test.properties",
                 todir: "${env.rapid_modules}");
            runTest("${env.distribution}/uiTestClasses/tests", testClassPaths, "${env.rapid_modules}/../TestResults")
        }
        finally {
            if (RI_UNIX_OS)
                stopRIUnix();
            if (RI_WINDOWS_OS)
                stopRIWindows();
        }
    }

    def startRIWindows()
    {
        def list = ["./${env.distribution}/RapidServer"]
        File dir = new File("./${env.distribution}/RapidServer/RapidSuite")
        Process p = "./${env.distribution}/RapidServer/RapidSuite/rs.exe -start".execute();
        waitForRI()
    }


    def waitForRI()
    {
        for (int i = 0; i < 50; i++)
        {
            try {
                println("ROSS not started yet")
                def url = new URL("http://localhost:12222/RapidSuite/")
                Thread.sleep(10000);
                def content = url.getText()
                break;
            }
            catch (Throwable e)
            {
                if (i == 49)
                {
                    throw new Exception("ROSS server is not reachable.", e);
                }
            }
        }
    }

    def stopRIWindows()
    {
        def list = ["./${env.distribution}/RapidServer"]
        File dir = new File("./${env.distribution}/RapidServer/RapidSuite")
        Process p = "./${env.distribution}/RapidServer/RapidSuite/rs.exe -stop".execute();
    }

    def startRIUnix()
    {
        Process p = "chmod +x ${env.distribution}/RapidServer/RapidSuite/rs.sh".execute();
        p.consumeProcessOutput(System.out, System.out);
        p.waitFor();

        def list = ["./${env.distribution}/RapidServer"]
        File dir = new File("./${env.distribution}/RapidServer/RapidSuite")

        def envVariables = [];
        System.getenv().each {String key, String value ->
            envVariables.add("${key}=${value}");
        }
        envVariables.add("RS_HOME=..")

        p = "./rs.sh -start".execute(envVariables, dir);

        p.consumeProcessOutput(System.out, System.out);
        p.waitFor();
        waitForRI()
    }

    def stopRIUnix()
    {
        Process p = "chmod +x ${env.distribution}/RapidServer/RapidSuite/rs.sh".execute();
        p.consumeProcessOutput(System.out, System.out);
        p.waitFor();

        def list = ["./${env.distribution}/RapidServer"]
        File dir = new File("./${env.distribution}/RapidServer/RapidSuite")

        def envVariables = [];
        System.getenv().each {String key, String value ->
            envVariables.add("${key}=${value}");
        }
        envVariables.add("RS_HOME=..")

        p = "./rs.sh -stop".execute(envVariables, dir);

        p.consumeProcessOutput(System.out, System.out);
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
                todir: "${env.distribution}/tmpSourceDir/com/ifountain/rcmdb/test/util")

        ant.copy(file: "${env.rapid_cmdb_commons_cvs}/src/groovy/com/ifountain/rcmdb/test/util/SeleniumTestUtils.groovy",
                todir: "${env.distribution}/tmpSourceDir/com/ifountain/rcmdb/test/util")

        ant.copy(file: "${env.rapid_comp_src}/com/ifountain/comp/test/util/CommonTestUtils.java",
                todir: "${env.distribution}/tmpSourceDir/com/ifountain/comp/test/util")
        ant.copy(todir: "${env.distribution}/tmpSourceDir/utils")
        {
            ant.fileset(dir:"${env.rapid_insight}/test/ui/utils");    
        }

        ant.groovyc(destdir: "${env.distribution}/uiTestClasses/testUtils",
                classpathref: "classpath",
                srcdir: "${env.distribution}/tmpSourceDir")
                {
                    ant.javac(source: "1.4", target: "1.4", debug: "on", cleanDestination: false);
                }

        ant.groovyc(destdir: "${env.distribution}/uiTestClasses/tests", srcdir: "${env.rapid_insight}/test/ui") {
            ant.classpath {
                ant.path(refid: "classpath")
                ant.path(location: "${env.distribution}/uiTestClasses/testUtils")
            }
        }

        ant.delete(dir: "${env.distribution}/tmpSourceDir")

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
        riZipFileName = "ROSS_Unix${rapidInsightBuilder.getVersionWithDate()}.zip"
    }



    def clean()
    {
        ant.delete(dir: "${env.rapid_modules}/../RapidServer");
    }


    def runTest(String testClassDir, List classPaths, String outputXmlDir) {
        ant.delete(dir: outputXmlDir);
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
        }
    }


}