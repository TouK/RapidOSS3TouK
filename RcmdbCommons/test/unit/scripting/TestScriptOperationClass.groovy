package scripting
/**
 * Created by IntelliJ IDEA.
 * User: admin
 * Date: Feb 9, 2009
 * Time: 6:27:27 PM
 * To change this template use File | Settings | File Templates.
 */
class TestScriptOperationClass extends com.ifountain.rcmdb.scripting.AbstractScriptOperation {
    String test;
    def injectedFunction()
    {
        input.fromInjectedFunction="injectedHello";
    }
    def injectedFunction2(param)
    {
        input.fromInjectedFunctionParam=param;
    }
}


