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
class ModelDatasourceController {

    def index = {redirect(action: list, params: params)}
    def list = {
        if (!params.max) params.max = 10
        [modelDatasourceList: ModelDatasource.search("alias:*", params).results]
    }

    def edit = {
        def modelDatasource = ModelDatasource.get([id: params.id])

        if (!modelDatasource) {
            flash.message = "ModelDatasource not found with id \${params.id}"
            redirect(action: list)
        }
        else {
            return [modelDatasource: modelDatasource]
        }
    }

    def create = {
        def modelDatasource = new ModelDatasource(ControllerUtils.getClassProperties(params, ModelDatasource))
        return ['modelDatasource': modelDatasource]
    }
    def show = {
        def keyMappingSortProp = params.keyMappingSortProp != null ? params.keyMappingSortProp : "property"
        def keyMappingSortOrder = params.keyMappingSortOrder != null ? params.keyMappingSortOrder : "asc"
        def modelDatasource = ModelDatasource.get(id: params.id)
        if (!modelDatasource) {
            flash.message = "ModelDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelDatasource: modelDatasource, keyMappingSortOrder: keyMappingSortOrder, keyMappingSortProp: keyMappingSortProp]}
    }


    def save = {
        def modelDatasource = ModelDatasource.add(ControllerUtils.getClassProperties(params, ModelDatasource));
        if (!modelDatasource.hasErrors()) {
            flash.message = "ModelDatasource ${modelDatasource} created"
            redirect(action: "show", controller: 'model', id: _getModelId(modelDatasource))
        }
        else {
            _render(view: 'create', model: [modelDatasource: modelDatasource])
        }
    }

    def delete = {
        def modelDatasource = ModelDatasource.get(id: params.id)
        if (modelDatasource) {
            def modelId = _getModelId(modelDatasource);
            def modelDatasourceName = modelDatasource.toString();
            modelDatasource.remove()
            flash.message = "ModelDatasource ${modelDatasourceName} deleted"
            redirect(action: "show", controller: 'model', id: modelId)
        }
        else {
            flash.message = "ModelDatasource not found with id ${params.id}"
            redirect(action: "list", controller: 'modelDatasource')
        }
    }

    def update = {
        def modelDatasource = ModelDatasource.get(id: params.id)
        if (modelDatasource) {
            modelDatasource.update(ControllerUtils.getClassProperties(params, ModelDatasource));
            if (!modelDatasource.hasErrors()) {
                flash.message = "ModelDatasource ${modelDatasource} updated"
                redirect(action: "show", controller: 'model', id: _getModelId(modelDatasource))
            }
            else {
                render(view: 'edit', model: [modelDatasource: modelDatasource])
            }
        }
        else {
            flash.message = "ModelDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def _render = {Map params ->
        def view = params.view;
        def model = params.model;
        render(view: view, model: model);
    }
    def _getModelId = {modelDatasource ->
        return modelDatasource.model?.id;
    }
}
