import com.ifountain.rcmdb.domain.util.ControllerUtils;
import grails.converters.XML

class TopoMapController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: ['POST', 'GET'], save: ['POST', 'GET'], update: ['POST', 'GET']]

    def list = {
        if(!params.max) params.max = 10
        def topoMaps = TopoMap.list(params);
        withFormat {
            html topoMapList: topoMaps
            xml {render topoMaps as XML}
        }
    }

    def show = {
        def topoMap = TopoMap.get([id:params.id])

        if(!topoMap) {
            addError("default.object.not.found", [TopoMap.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {errorsToXml(errors)}
            }
        }
        else {
            withFormat {
                html {render(view: "show", model: [topoMap: topoMap])}
                xml {render topoMap as XML}
            }
        }
    }

    def delete = {
        def topoMap = TopoMap.get( [id:params.id])
        if(topoMap) {
            def username =  session.username;
            def mapName = topoMap.mapName;
            def edges = EdgeNode.list().findAll {
                it.mapName == mapName && it.username == username;
            };
            edges.each(){
                it.remove();
            }
            def devices = topoMap.consistOfDevices;
            topoMap.remove();
            devices.each {
                it.remove();
            }
            withFormat {
                html {
                    flash.message = "TopoMap ${params.id} deleted"
                    redirect(action: list)
                }
                xml {render(text: ControllerUtils.convertSuccessToXml("TopoMap ${topoMap.id} deleted"), contentType: "text/xml")}
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

    def edit = {
        def topoMap = TopoMap.get( [id:params.id] )

        if(!topoMap) {
            addError("default.object.not.found", [TopoMap.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: list)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
        else {
            withFormat {
                html {
                    return [topoMap: topoMap]
                }
                xml {
                    def userName = session.username;
                    def mapGroups = MapGroup.list().findAll {
                        it.username == userName
                    };
                    render(contentType: 'text/xml') {
                        Edit {
                            id(topoMap.id)
                            name(topoMap.mapName)
                            username(topoMap.username)
                            group {
                                mapGroups.each {
                                    if (it.groupName == topoMap.group.groupName) {
                                        option(selected: "true", it.groupName)
                                    }
                                    else {
                                        option(it.groupName)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }


    def update = {
        def topoMap = TopoMap.get( [id:params.id] )
        if(topoMap) {
            if (params.groupName == "")
            {
                params.groupName = "Default";
            }
            def group = MapGroup.get(groupName: params.groupName, username: session.username);
            if (group == null)
            {
                group = MapGroup.add(name: params.groupName, username: session.username);
            }
            params["group"] = ["id": group.id];
            topoMap.update(ControllerUtils.getClassProperties(params, TopoMap));
            if (!topoMap.hasErrors()) {
                withFormat {
                    html {
                        flash.message = "TopoMap ${params.id} updated"
                        redirect(action: show, id: topoMap.id)
                    }
                    xml {render(text: ControllerUtils.convertSuccessToXml("TopoMap ${topoMap.id} updated"), contentType: "text/xml")}
                }

            }
            else {
                withFormat {
                    html {
                        render(view: 'edit', model: [topoMap: topoMap])
                    }
                    xml {render(text: errorsToXml(topoMap.errors), contentType: "text/xml")}
                }
            }
        }
        else {
            addError("default.object.not.found", [TopoMap.class.name, params.id]);
            withFormat {
                html {
                    flash.errors = errors;
                    redirect(action: edit, id: params.id)
                }
                xml {render(text: errorsToXml(errors), contentType: "text/xml")}
            }
        }
    }

    def create = {
        withFormat {
            html {
                def topoMap = new TopoMap()
                topoMap.properties = params
                return ['topoMap': topoMap]
            }
            xml {
                def userName = session.username;
                def mapGroups = MapGroup.list().findAll {
                    it.username == userName
                };
                render(contentType: 'text/xml') {
                    Create {
                        group {
                            mapGroups.each {
                                option(it.name)
                            }
                        }
                    }
                }
            }
        }
    }

    def save = {
        params["username"] = session.username
        if (params.groupName == "")
        {
            params.groupName = "Default";
        }
        def group = MapGroup.get(groupName: params.groupName, username: session.username);
        if (group == null)
        {
            group = MapGroup.add(groupName: params.groupName, username: session.username);
        }
        params["group"] = ["id": group.id];
        def topoMap = TopoMap.add(ControllerUtils.getClassProperties(params, TopoMap))
        if (!topoMap.hasErrors()) {
            withFormat {
                html {
                    flash.message = "TopoMap ${topoMap.id} created"
                    redirect(action: show, id: topoMap.id)
                }
                xml {render(text: ControllerUtils.convertSuccessToXml("TopoMap ${topoMap.id} created"), contentType: "text/xml")}
            }

        }
        else {
            withFormat {
                html {
                    render(view: 'create', model: [topoMap: topoMap])
                }
                xml {render(text: errorsToXml(topoMap.errors), contentType: "text/xml")}
            }

        }
    }

    def addTo = {
        def topoMap = TopoMap.get( [id:params.id] )
        if(!topoMap){
            flash.message = "TopoMap not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = topoMap.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [topoMap:topoMap, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:topoMap.id)
            }
        }
    }

    def addRelation = {
        def topoMap = TopoMap.get( [id:params.id] )
        if(!topoMap) {
            flash.message = "TopoMap not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = topoMap.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      topoMap.addRelation(relationMap);
                      if(topoMap.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[topoMap:topoMap, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "TopoMap ${params.id} updated"
                          redirect(action:edit,id:topoMap.id)
                      }

                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:addTo, id:params.id, relationName:relationName)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:addTo, id:params.id, relationName:relationName)
            }
        }
    }

    def removeRelation = {
        def topoMap = TopoMap.get( [id:params.id] )
        if(!topoMap) {
            flash.message = "TopoMap not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = topoMap.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      topoMap.removeRelation(relationMap);
                      if(topoMap.hasErrors()){
                          render(view:'edit',model:[topoMap:topoMap])
                      }
                      else{
                          flash.message = "TopoMap ${params.id} updated"
                          redirect(action:edit,id:topoMap.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:topoMap.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:topoMap.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("TopoMap")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action:list)
            } catch (t)
            {
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                 redirect(action:list)
            }
        }
        else
        {
            flash.message = "Model currently not loaded by application. You should reload application."
            redirect(action:list)
        }
    }
}