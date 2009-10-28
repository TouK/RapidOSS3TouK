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
package model

import com.ifountain.rcmdb.util.RapidCMDBConstants
import com.ifountain.rcmdb.domain.util.ControllerUtils;
class ModelPropertyController {

    def index = {redirect(action: list, params: params)}
    def list = {
        if (!params.max) params.max = 10
        [modelPropertyList: ModelProperty.search("alias:*", params).results]
    }

    def edit = {
        def modelProperty = ModelProperty.get([id: params.id])

        if (!modelProperty) {
            flash.message = "ModelProperty not found with id \${params.id}"
            redirect(action: list)
        }
        else {
            return [modelProperty: modelProperty]
        }
    }

    def create = {
        def modelProperty = new ModelProperty()
        if (params["model.id"] != null) {
            def model = Model.get([id: params["model.id"]]);
            modelProperty.model = model;
        }
        return ['modelProperty': modelProperty]
    }

    def show = {
        def modelProperty = ModelProperty.get(id: params.id)
        if (!modelProperty) {
            flash.message = "ModelProperty not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelProperty: modelProperty]}
    }
    def save = {
        if (params["datasource.id"] != null && params["datasource.id"] != "null") {
            def datasourceName = DatasourceName.get(id: params["datasource.id"]);
            def currentModel = Model.get(id: params["model.id"]);
            def tempModel = currentModel;
            def modelDatasource = null;
            while (tempModel != null && !modelDatasource) {
                tempModel.datasources.each
                {
                    if (it.datasource.name == datasourceName.name)
                    {
                        modelDatasource = it;
                        return;
                    }
                }
                tempModel = tempModel.parentModel;
            }


            if (!modelDatasource) {
                def isMaster = false;
                if (datasourceName.name == RapidCMDBConstants.RCMDB) {
                    isMaster = true;
                }
                modelDatasource = ModelDatasource.add(model: currentModel, datasource: datasourceName);
            }
            params["propertyDatasource.id"] = "" + modelDatasource.id;
            params["propertyDatasource"] = ["id": modelDatasource.id];
        }
        else
        {
            params["propertyDatasource.id"] = "null";
            params["propertyDatasource"] = ["id": "null"];
        }
        def modelProperty = ModelProperty.add(ControllerUtils.getClassProperties(params, ModelProperty))
        if (!modelProperty.hasErrors()) {
            flash.message = "ModelProperty ${modelProperty} created"
            redirect(action: show, controller: 'model', id: modelProperty.model?.id)
        }
        else {
            render(view: 'create', model: [modelProperty: modelProperty])
        }
    }

    def delete = {
        def modelProperty = ModelProperty.get(id: params.id)
        if (modelProperty) {
            def modelId = modelProperty.model?.id;
            def modelPropertyName = modelProperty.toString();
            modelProperty.remove()
            flash.message = "Property ${modelPropertyName} deleted"
            redirect(action: show, controller: 'model', id: modelId)
        }
        else {
            flash.message = "Property not found"
            redirect(action: list)
        }
    }

    def update = {
        def modelProperty = ModelProperty.get(id: params.id)
        if (modelProperty) {
            if (params["datasource.id"] != null && params["datasource.id"] != "null") {
                def datasourceName = DatasourceName.get(id: params["datasource.id"]);
                def currentModel = Model.get(id: params["model.id"]);
                def tempModel = currentModel;
                def modelDatasource = null;
                while (tempModel != null && !modelDatasource) {
                    tempModel.datasources.each
                    {
                        if (it.datasource.name == datasourceName.name)
                        {
                            modelDatasource = it;
                            return;
                        }
                    }
                    tempModel = tempModel.parentModel;
                }


                if (!modelDatasource) {
                    def isMaster = false;
                    if (datasourceName.name == RapidCMDBConstants.RCMDB) {
                        isMaster = true;
                    }
                    modelDatasource = ModelDatasource.add(model: currentModel, datasource: datasourceName);
                }
                params["propertyDatasource"] = ["id": modelDatasource.id];
            }
            else
            {
                params["propertyDatasource"] = ["id": "null"];
            }
            modelProperty.update(ControllerUtils.getClassProperties(params, ModelProperty));
            if (!modelProperty.hasErrors()) {
                flash.message = "ModelProperty ${modelProperty} updated"
                redirect(action: "show", controller: 'model', id: _getModelId(modelProperty))
            }
            else {
                render(view: 'edit', model: [modelProperty: modelProperty])
            }
        }
        else {
            flash.message = "ModelProperty not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def _getModelId = {modelProperty ->
        return modelProperty.model?.id;
    }
}
