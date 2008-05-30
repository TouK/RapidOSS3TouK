package script

import com.ifountain.rcmdb.scripting.ScriptManager
import org.apache.tools.ant.taskdefs.optional.junit.XMLJUnitResultFormatter
import org.apache.tools.ant.taskdefs.optional.junit.JUnitTest
import junit.framework.TestResult
import junit.framework.Test;

class ScriptController {
    public static final String SUCCESSFULLY_CREATED = "Script created";
    public static final String SCRIPT_DOESNOT_EXIST = "Script does not exist";
    def scaffold = CmdbScript;
    def save = {
        def script = new CmdbScript(params)
        if (script.save() && !script.hasErrors()) {

            ScriptManager.getInstance().addScript(script.name);
            flash.message = SUCCESSFULLY_CREATED
            redirect(action: show, controller: 'script', id: script.id)
        }
        else {
            render(view: 'create', controller: 'script', model: [cmdbScript: script])
        }
    }

    def reload = {
        def script = CmdbScript.findByName(params.id);
        if (script)
        {
            try
            {
                script.reload();
                flash.message = "Script reloaded successfully.";
                redirect(action: show, controller: 'script', id: script.id);
            }
            catch (t)
            {
                flash.message = "Exception occurred while reloading. Reason : ${t.getMessage()}";
                redirect(action: show, controller: 'script', id: script.id);
            }

        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action: list, controller: 'script');
        }
    }
    def run =
    {
        def script = CmdbScript.findByName(params.id);
        if (script)
        {
            def bindings = ["params": params]
            try
            {
                def result = ScriptManager.getInstance().runScript(script.name, bindings);
                render(text: String.valueOf(result), contentType: "text/html", encoding: "UTF-8");
            }
            catch (t)
            {
                log.warn("", org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                render(text: t.toString(), contentType: "text/html", encoding: "UTF-8");
            }

        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action: list, controller: 'script');
        }
    }

    def test =
    {
        def testDir = "test/reports"
        new AntBuilder().mkdir(dir: testDir);
        def script = CmdbScript.findByName(params.id);
        if (script)
        {
            def bindings = ["params": params]
            def renderText = "";
            new File("${testDir}/TEST-${script.name}.xml").withOutputStream {xmlOut ->
                def savedOut = System.out
                def savedErr = System.err
                try {
                    def outBytes = new ByteArrayOutputStream()
                    def errBytes = new ByteArrayOutputStream()
                    System.out = new PrintStream(outBytes)
                    System.err = new PrintStream(errBytes)
                    def xmlOutput = new XMLJUnitResultFormatter(output: xmlOut)
                    def junitTest = new JUnitTest(script.name)
                    xmlOutput.startTestSuite(junitTest)
                    def errorCount = 0;
                    def start = System.currentTimeMillis();
                    try
                    {
                        def result = ScriptManager.getInstance().runScript(script.name, bindings);
                        junitTest.setRunTime(System.currentTimeMillis() - start)
                        renderText = String.valueOf(result);
                    }
                    catch (Throwable t)
                    {
                        junitTest.setRunTime(System.currentTimeMillis() - start)
                        xmlOutput.addError(new MockScriptTest(script.name), org.codehaus.groovy.runtime.StackTraceUtils.deepSanitize(t));
                        renderText = t.toString();
                        errorCount = 1;
                    }
                    junitTest.setCounts(1, 0, errorCount);
                    def outString = outBytes.toString()
                    def errString = errBytes.toString()
                    xmlOutput.setSystemOutput(outString)
                    xmlOutput.setSystemError(errString)
                    xmlOutput.endTestSuite(junitTest)
                }
                catch(Throwable t2){
                   renderText = t2.toString(); 
                }
                finally {
                    System.out = savedOut
                    System.err = savedErr
                }
            }
            render(text: renderText, contentType: "text/html", encoding: "UTF-8");

        }
        else
        {
            flash.message = SCRIPT_DOESNOT_EXIST
            redirect(action: list, controller: 'script');
        }
    }
}

class MockScriptTest implements Test{
    def name;
    public MockScriptTest(String name){
        this.name = name;
    }
	public int countTestCases() {
		return 0;
	}
	public void run(TestResult arg0) {
	}

	public String getName() {
		return name;
	}
	public String name() {
		return name;
	}
}
