import org.apache.commons.lang.math.RandomUtils
import org.codehaus.groovy.grails.commons.ApplicationHolder

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 31, 2008
 * Time: 1:34:14 PM
 * To change this template use File | Settings | File Templates.
 */
def static DemoValues = new NetcoolDemoValues();

for(int i=0; i < 100000; i++)
{
    def props = DemoValues.getEventProperties();
    NetcoolEvent.add(props);
}




