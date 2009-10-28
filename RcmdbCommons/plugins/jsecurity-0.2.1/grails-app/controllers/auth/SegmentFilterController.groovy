package auth

import com.ifountain.rcmdb.domain.util.ControllerUtils

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Jul 2, 2009
* Time: 11:34:28 AM
* To change this template use File | Settings | File Templates.
*/
class SegmentFilterController {
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.max) params.max = 10
        [segmentFilterList: SegmentFilter.search("alias:*", params).results]
    }

    def show = {
        def segmentFilter = SegmentFilter.get([id: params.id])

        if (!segmentFilter) {
            flash.message = "SegmentFilter not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (segmentFilter.class != SegmentFilter)
            {
                def controllerName = segmentFilter.class.simpleName;
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
                return [segmentFilter: segmentFilter]
            }
        }
    }

    def delete = {
        def segmentFilter = SegmentFilter.get([id: params.id])
        if (segmentFilter) {
            def groupId = segmentFilter.groupId;
            segmentFilter.remove()
            flash.message = "SegmentFilter ${params.id} deleted"
            redirect(action: "show", controller: "group", id: groupId)
        }
        else {
            flash.message = "SegmentFilter not found with id ${params.id}"
            redirect(action: "list", group: "controller")
        }
    }

    def edit = {
        def segmentFilter = SegmentFilter.get([id: params.id])

        if (!segmentFilter) {
            flash.message = "SegmentFilter not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [segmentFilter: segmentFilter]
        }
    }


    def update = {
        def segmentFilter = SegmentFilter.get([id: params.id])
        if (segmentFilter) {
            segmentFilter.update(ControllerUtils.getClassProperties(params, SegmentFilter));
            if (!segmentFilter.hasErrors()) {
                flash.message = "SegmentFilter ${params.id} updated"
                if (params.targetURI) {
                    redirect(uri: params.targetURI)
                }
                else {
                    redirect(controller: "group", action: "show", id: segmentFilter.groupId)
                }
            }
            else {
                render(view: 'edit', model: [segmentFilter: segmentFilter])
            }
        }
        else {
            flash.message = "SegmentFilter not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def segmentFilter = new SegmentFilter()
        segmentFilter.properties = params
        return ['segmentFilter': segmentFilter]
    }

    def save = {
        def addParams = ControllerUtils.getClassProperties(params, SegmentFilter)
        def segmentFilter = SegmentFilter.add(addParams)
        if (!segmentFilter.hasErrors()) {
            flash.message = "SegmentFilter ${segmentFilter.id} created"
            if (params.targetURI) {
                redirect(uri: params.targetURI)
            }
            else {
                redirect(controller: "group", action: "show", id: segmentFilter.groupId)
            }

        }
        else {
            render(view: 'create', model: [segmentFilter: segmentFilter])
        }
    }

}