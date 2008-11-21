import auth.Role

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Aug 15, 2008
 * Time: 10:08:59 AM
 * To change this template use File | Settings | File Templates.
 */
class RiForSmartsSecurityFilters {
    def filters = {
        def adminControllers = ["smartsConnectionTemplate", "smartsConnector"];
        adminAuthorization(controller: "(" + adminControllers.join("|") + ")", action: "*") {
            before = {
                accessControl {
                    role(Role.ADMINISTRATOR)
                }

            }
        }
    }
}