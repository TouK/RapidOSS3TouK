
/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 22, 2008
 * Time: 5:07:01 PM
 * To change this template use File | Settings | File Templates.
 */

RsTopologyObject.list().each{
    it.remove();
}
RsEvent.list().each{
    it.remove();
}
RsHistoricalEvent.list().each{
    it.remove();
}
RsManagementSystem.list().each{
    it.remove();
}