/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 2:23:36 PM
 * To change this template use File | Settings | File Templates.
 */
import datasource.EmailDatasource;

def datasource=EmailDatasource.get(name:"emailds");


datasource.sendEmail("","","","");
