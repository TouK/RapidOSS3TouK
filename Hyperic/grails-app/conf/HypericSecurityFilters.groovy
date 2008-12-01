import auth.Role

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Nov 21, 2008
* Time: 11:47:09 AM
*/
class HypericSecurityFilters {
    def filters = {
        def adminControllers = ["hypericConnection", "hypericDatasource"];
        adminAuthorization(controller: "(" + adminControllers.join("|") + ")", action: "*") {
            before = {
                accessControl {
                    role(Role.ADMINISTRATOR)
                }

            }
        }
    }
}