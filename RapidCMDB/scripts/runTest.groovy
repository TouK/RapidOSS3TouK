import junit.framework.TestCase
import junit.framework.TestSuite
import org.codehaus.groovy.grails.support.GrailsTestSuite
import org.codehaus.groovy.grails.commons.ApplicationHolder
import junit.framework.TestResult

def testClassLoader = new GroovyClassLoader(this.class.classLoader);
testClassLoader.addClasspath (System.getProperty("base.dir") + "/test/unit");
testClassLoader.addClasspath (System.getProperty("base.dir") + "/test/integration");

Class cls = testClassLoader.loadClass (params.name);
def suite = new TestSuite();
suite.addTest(new GrailsTestSuite(ApplicationHolder.application.parentContext, cls))
def res = new TestResult();
println "a"
try
{
    println "b"
suite.run (res);
println "c"
}
catch(Throwable t)
{
    println "d"
    t.printStackTrace();    
}
println "e"
res.failures().each {failure->
    println "f"
    println failure;
    println "g"
}
println "h"
println res.wasSuccessful();