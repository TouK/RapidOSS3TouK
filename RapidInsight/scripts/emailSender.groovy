/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Dec 23, 2008
 * Time: 2:23:36 PM
 * To change this template use File | Settings | File Templates.
 */

 def datasource=EmailDatasource.get(name:"emailds");

 datasource.sendMail(from,to,subject,body);
