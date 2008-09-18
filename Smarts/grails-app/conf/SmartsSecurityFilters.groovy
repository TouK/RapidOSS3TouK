import auth.Role

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 19, 2008
 * Time: 9:42:07 AM
 * To change this template use File | Settings | File Templates.
 */
class SmartsSecurityFilters {
     def filters = {
        def adminControllers = ["smartsConnection", "smartsTopologyDatasource", "smartsNotificationDatasource"];
        adminAuthorization(controller: "(" + adminControllers.join("|") + ")", action: "*") {
            before = {
                accessControl {
                    role(Role.ADMINISTRATOR)
                }

            }
        }
    }
}