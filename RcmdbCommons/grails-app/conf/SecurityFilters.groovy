import auth.Role

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: May 22, 2008
 * Time: 10:20:12 AM
 */
class SecurityFilters {
    def filters = {
        // Ensure that all controllers and actions require an authenticated user,
        // except for the "public" controller
        authentication(controller: "*", action: "*") {
            before = {
                // Exclude the "public" controller.
                if (controllerName == "public") return true

                // This just means that the user must be authenticated. He does
                // not need any particular role or permission.
                accessControl {
                    role(Role.ADMINISTRATOR) || role(Role.USER)
                }
            }
        }
        def adminControllers = ["userRoleRel"];
        adminAuthorization(controller: "(" + adminControllers.join("|") + ")", action: "*") {
            before = {
                accessControl {
                    role(Role.ADMINISTRATOR)
                }

            }
        }

        scriptAuthorization(controller: "script", action: "*") {
           before = {
                if (actionName == "run") {
                    accessControl {
                        role(Role.ADMINISTRATOR) || role(Role.USER)
                    }
                }
                else {
                    accessControl {
                        role(Role.ADMINISTRATOR)
                    }
                }
            }
        }
        rsUserAuthorization(controller: "rsUser", action: "*") {
            before = {
                if (actionName == "changePassword") {
                    accessControl {
                        role(Role.ADMINISTRATOR) || role(Role.USER)
                    }
                }
                else {
                    accessControl {
                        role(Role.ADMINISTRATOR)
                    }
                }
            }
        }
    }
}