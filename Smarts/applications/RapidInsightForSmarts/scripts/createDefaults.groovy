import script.CmdbScript
import datasource.SmartsModel

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jul 7, 2008
 * Time: 5:29:58 PM
 * To change this template use File | Settings | File Templates.
 */

CmdbScript.addScript(name: "modelCreator");
CmdbScript.addScript(name: "getDevices");
CmdbScript.addScript(name: "removeAll");
if(SmartsModel.get(name:"Notification") == null)
{
    CmdbScript.runScript("modelCreator", [web:[:]]);   
}