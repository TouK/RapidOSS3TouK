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
class ModelRelationController {
    def index = {redirect(action: list, params: params)}
    def list = {
        if (!params.max) params.max = 10
        [modelRelationList: ModelRelation.search("alias:*", params).results]
    }

    def edit = {
        def modelRelation = ModelRelation.get([id: params.id])

        if (!modelRelation) {
            flash.message = "ModelRelation not found with id \${params.id}"
            redirect(action: list)
        }
        else {
            return [modelRelation: modelRelation]
        }
    }


    def show = {
        def modelRelation = ModelRelation.get(id: params.id)
        if (!modelRelation) {
            flash.message = "ModelRelation not found with id ${params.id}"
            redirect(action: list)
        }
        else {return [modelRelation: modelRelation]}
    }

    def save = {
        def cardinalities = params["cardinality"]?.split("To");
        if (cardinalities != null) {
            params["firstCardinality"] = cardinalities[0];
            params["secondCardinality"] = cardinalities[1];
        }
        def modelRelation = ModelRelation.add(ControllerUtils.getClassProperties(params, ModelRelation))
        if (!modelRelation.hasErrors()) {
            flash.message = "Relation created"
            redirect(action: show, controller: 'model', id: modelRelation.firstModel?.id)
        }
        else {
            render(view: 'create', model: [modelRelation: modelRelation, 'modelId': params["firstModel.id"]])
        }
    }
    def update = {
        def cardinalities = params["cardinality"]?.split("To");
        if (cardinalities != null) {
            params["firstCardinality"] = cardinalities[0];
            params["secondCardinality"] = cardinalities[1];
        }
        def modelRelation = ModelRelation.get(id: params.id)
        if (modelRelation) {
            def isReverse = false;
            def redirectModelId = modelRelation.firstModel?.id;
            if (params["reverse"] != null) {
                isReverse = true;
                params["firstCardinality"] = cardinalities[1];
                params["secondCardinality"] = cardinalities[0];
                redirectModelId = modelRelation.secondModel?.id;

            }
            modelRelation.update(ControllerUtils.getClassProperties(params, ModelRelation));
            if (!modelRelation.hasErrors()) {
                flash.message = "ModelRelation ${params.id} updated"
                redirect(action: show, controller: 'model', id: redirectModelId)
            }
            else {
                render(view: 'edit', model: [modelRelation: modelRelation], 'isReverse': isReverse)
            }
        }
        else {
            flash.message = "ModelRelation not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def delete = {
        def modelRelation = ModelRelation.get(id: params.id)
        if (modelRelation) {
            def modelId = modelRelation.firstModel?.id;
            modelRelation.remove()
            flash.message = "Relation deleted"
            redirect(action: show, controller: 'model', id: modelId)
        }
        else {
            flash.message = "Relation not found."
            redirect(action: list)
        }
    }

    def create = {
        def modelRelation = new ModelRelation(ControllerUtils.getClassProperties(params, ModelRelation))
        return ['modelRelation': modelRelation]
    }
}
