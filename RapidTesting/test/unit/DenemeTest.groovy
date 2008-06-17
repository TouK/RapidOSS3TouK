/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 16, 2008
 * Time: 5:11:18 PM
 * To change this template use File | Settings | File Templates.
 */
class DenemeTest extends GroovyTestCase{
    public void test1()
    {
        def controller = new DenemeController();
        assertTrue(controller.show().contains("sezgin"));
    }
    public void test2()
    {
        def controller = new DenemeController();
        assertTrue(controller.show().contains("sezgin"));
    }
    public void test3()
    {
        def controller = new DenemeController();
        assertTrue(controller.show().contains("sezgin"));
    }
    public void test4()
    {
        def controller = new DenemeController();
        assertTrue(controller.show().contains("sezgin"));
    }
}