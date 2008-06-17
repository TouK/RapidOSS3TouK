/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 17, 2008
 * Time: 10:35:38 AM
 * To change this template use File | Settings | File Templates.
 */
class Deneme3Test extends GroovyTestCase{
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