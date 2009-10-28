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
package model;


import com.ifountain.rcmdb.domain.util.ControllerUtils;

class DatasourceNameController {

    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [datasourceNameList: DatasourceName.search("alias:*", params).results]
    }

    def show = {
        def datasourceName = DatasourceName.get([id: params.id])

        if (!datasourceName) {
            flash.message = "DatasourceName not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (datasourceName.class != DatasourceName)
            {
                def controllerName = datasourceName.class.simpleName;
                if (controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0, 1).toLowerCase() + controllerName.substring(1);
                }
                redirect(action: show, controller: controllerName, id: params.id)
            }
            else
            {
                return [datasourceName: datasourceName]
            }
        }
    }

    def delete = {
        def datasourceName = DatasourceName.get(id: params.id)
        if (datasourceName) {
            datasourceName.remove()
            flash.message = "DatasourceName ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "DatasourceName not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def datasourceName = DatasourceName.get([id: params.id])

        if (!datasourceName) {
            flash.message = "DatasourceName not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [datasourceName: datasourceName]
        }
    }


    def update = {
        def datasourceName = DatasourceName.get([id: params.id])
        if (datasourceName) {
            datasourceName.update(ControllerUtils.getClassProperties(params, DatasourceName));
            if (!datasourceName.hasErrors()) {
                flash.message = "DatasourceName ${params.id} updated"
                redirect(action: show, id: datasourceName.id)
            }
            else {
                render(view: 'edit', model: [datasourceName: datasourceName])
            }
        }
        else {
            flash.message = "DatasourceName not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def datasourceName = new DatasourceName()
        datasourceName.properties = params
        return ['datasourceName': datasourceName]
    }

    def save = {
        def datasourceName = DatasourceName.add(ControllerUtils.getClassProperties(params, DatasourceName))
        if (!datasourceName.hasErrors()) {
            flash.message = "DatasourceName ${datasourceName.id} created"
            redirect(action: show, id: datasourceName.id)
        }
        else {
            render(view: 'create', model: [datasourceName: datasourceName])
        }
    }

}