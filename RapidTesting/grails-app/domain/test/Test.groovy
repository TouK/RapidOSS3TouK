package test;
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 17, 2008
 * Time: 10:28:44 AM
 * To change this template use File | Settings | File Templates.
 */
class Test
{
    String name;
    TestSuite suite;
    Class testClass;
    static transients = ["testClass"]
    static hasMany = [testCases:TestCase];
}