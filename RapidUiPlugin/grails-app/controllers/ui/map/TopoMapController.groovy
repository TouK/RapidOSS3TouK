/* 
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package ui.map
import grails.converters.XML

class TopoMapController {
    def index = {}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: ['POST', 'GET']]


    def delete = {
        def topoMap = TopoMap.get( [id:params.id])
        if(topoMap) {
            def username =  session.username;
            def mapName = topoMap.mapName;
            topoMap.remove();
            withFormat {
                html {
                    flash.message = "TopoMap ${params.id} deleted"
                    redirect(action: list)
                }
                xml {render(text: com.ifountain.rcmdb.domain.util.ControllerUtils.convertSuccessToXml("TopoMap ${topoMap.id} deleted"), contentType: "text/xml")}
            }
        }
        else {
            addError("default.object.not.found", [TopoMap.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

}