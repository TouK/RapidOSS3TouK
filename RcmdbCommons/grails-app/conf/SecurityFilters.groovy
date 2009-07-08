import auth.Role
import script.CmdbScript

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
        authentication(uri: "/**") {
            before = {
                return accessControl {
                    return role(Role.ADMINISTRATOR) || role(Role.USER)
                }
            }
        }
        def adminControllers = ["group"];
        adminAuthorization(controller: "(" + adminControllers.join("|") + ")", action: "*") {
            before = {
                return accessControl {
                    return role(Role.ADMINISTRATOR)
                }

            }
        }

        scriptAuthorization(controller: "script", action: "*") {
           before = {
                if (actionName == "run" || actionName == "remove" || actionName == "update") {
                    CmdbScript script = null;
                    try{
                        script = CmdbScript.get(id:Long.parseLong(params.id));
                    }
                    catch(Throwable t)
                    {
                        script = CmdbScript.get(name:params.id);
                    }
                    if(script)
                    {
                        return accessControl {
                            def isOk = script.enabledForAllGroups;
                            if(!script.enabledForAllGroups)
                            {
                                script.allowedGroups.each{
                                    isOk = isOk || group(it.name)
                                }
                            }
                            return role(Role.ADMINISTRATOR) || isOk && role(Role.USER);
                        }
                    }
                    else
                    {
                        return accessControl{
                            return role(Role.ADMINISTRATOR);
                        }
                    }
                }
                else {
                    return accessControl {
                        return role(Role.ADMINISTRATOR)
                    }
                }
            }
        }
        rsUserAuthorization(controller: "rsUser", action: "*") {
            before = {
                if (actionName == "changeProfile" ) {
                    return accessControl {
                        return role(Role.ADMINISTRATOR) || role(Role.USER)
                    }
                }
                else {
                    return accessControl {
                        return role(Role.ADMINISTRATOR)
                    }
                }
            }
        }
    }
}