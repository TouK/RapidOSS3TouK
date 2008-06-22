/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 17, 2008
 * Time: 3:06:45 PM
 * To change this template use File | Settings | File Templates.
 */
class BerkayTest extends GroovyTestCase{
    public void testBerkaySurnameIsSener()
    {
        def cont = new DenemeController();
        cont.show();
        println cont.response.contentAsString
        assertEquals ("mollamustafaoglu", "mollamustafaoglu");
    }

     public void testBerkaySurnameIsSener2()
    {
        assertEquals ("mollamustafaoglu", "mollamustafaoglu");
    }
}