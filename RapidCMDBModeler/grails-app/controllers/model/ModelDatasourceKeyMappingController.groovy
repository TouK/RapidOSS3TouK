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
class ModelDatasourceKeyMappingController {

    def index = {redirect(action: list, params: params)}
    def list = {
        if (!params.max) params.max = 10
        [modelDatasourceKeyMappingList: ModelDatasourceKeyMapping.search("alias:*", params).results]
    }

    def edit = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get([id: params.id])

        if (!modelDatasourceKeyMapping) {
            flash.message = "ModelDatasourceKeyMapping not found with id \${params.id}"
            redirect(action: list)
        }
        else {
            return [modelDatasourceKeyMapping: modelDatasourceKeyMapping]
        }
    }

    def create = {
        def modelDatasourceKeyMapping = new ModelDatasourceKeyMapping()
        modelDatasourceKeyMapping.properties = params
        return ['modelDatasourceKeyMapping': modelDatasourceKeyMapping]
    }
    def show = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get(id: params.id)
        if (!modelDatasourceKeyMapping) {
            flash.message = "ModelDatasourceKeyMapping not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelDatasourceKeyMapping: modelDatasourceKeyMapping]}
    }

    def save = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.add(ControllerUtils.getClassProperties(params, ModelDatasourceKeyMapping))
        if (!modelDatasourceKeyMapping.hasErrors()) {
            flash.message = "ModelDatasourceKeyMapping ${modelDatasourceKeyMapping} created"
            redirect(action: show, controller: 'modelDatasource', id: modelDatasourceKeyMapping.datasource?.id)
        }
        else {
            render(view: 'create', model: [modelDatasourceKeyMapping: modelDatasourceKeyMapping])
        }
    }

    def delete = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get(id: params.id)
        if (modelDatasourceKeyMapping) {
            def modelDatasourceId = modelDatasourceKeyMapping.datasource?.id;
            def keyMappingName = modelDatasourceKeyMapping.toString();
            modelDatasourceKeyMapping.remove()
            flash.message = "ModelDatasourceKeyMapping ${keyMappingName} deleted"
            redirect(action: show, controller: 'modelDatasource', id: modelDatasourceId)
        }
        else {
            flash.message = "ModelDatasourceKeyMapping not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def update = {
        def modelDatasourceKeyMapping = ModelDatasourceKeyMapping.get(id: params.id)
        if (modelDatasourceKeyMapping) {
            modelDatasourceKeyMapping.update(ControllerUtils.getClassProperties(params, ModelDatasourceKeyMapping));
            if (!modelDatasourceKeyMapping.hasErrors()) {
                def modelDatasourceId = modelDatasourceKeyMapping.datasource?.id;
                flash.message = "ModelDatasourceKeyMapping ${modelDatasourceKeyMapping} updated"
                redirect(action: "show", controller: 'modelDatasource', id: modelDatasourceId)
            }
            else {
                render(view: 'edit', model: [modelDatasourceKeyMapping: modelDatasourceKeyMapping])
            }
        }
        else {
            flash.message = "ModelDatasourceKeyMapping not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }
}
