package datasource
/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Oct 28, 2010
 * Time: 3:38:01 PM
 */

import com.ifountain.rcmdb.domain.util.ControllerUtils
class TcpListeningDatasourceController {
   def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']
    def list = {
        if (!params.sort) params.sort = "name"
        [tcpListeningDatasourceList: TcpListeningDatasource.search("alias:*", params).results]
    }

    def show = {
        def tcpListeningDatasource = TcpListeningDatasource.get([id: params.id])

        if (!tcpListeningDatasource) {
            flash.message = "TcpListeningDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (tcpListeningDatasource.class != TcpListeningDatasource)
            {
                def controllerName = tcpListeningDatasource.class.simpleName;
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
                return [tcpListeningDatasource: tcpListeningDatasource]
            }
        }
    }

    def delete = {
        def tcpListeningDatasource = TcpListeningDatasource.get([id: params.id])
        if (tcpListeningDatasource) {
            tcpListeningDatasource.remove()
            flash.message = "TcpListeningDatasource ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "TcpListeningDatasource not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def tcpListeningDatasource = TcpListeningDatasource.get([id: params.id])

        if (!tcpListeningDatasource) {
            flash.message = "TcpListeningDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [tcpListeningDatasource: tcpListeningDatasource]
        }
    }


    def update = {
        def tcpListeningDatasource = TcpListeningDatasource.get([id: params.id])
        if (tcpListeningDatasource) {
            tcpListeningDatasource.update(ControllerUtils.getClassProperties(params, TcpListeningDatasource));
            if (!tcpListeningDatasource.hasErrors()) {
                flash.message = "TcpListeningDatasource ${params.id} updated"
                redirect(action: show, id: tcpListeningDatasource.id)
            }
            else {
                render(view: 'edit', model: [tcpListeningDatasource: tcpListeningDatasource])
            }
        }
        else {
            flash.message = "TcpListeningDatasource not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def tcpListeningDatasource = new TcpListeningDatasource()
        tcpListeningDatasource.properties = params
        return ['tcpListeningDatasource': tcpListeningDatasource]
    }

    def save = {
        def tcpListeningDatasource = TcpListeningDatasource.add(ControllerUtils.getClassProperties(params, TcpListeningDatasource))
        if (!tcpListeningDatasource.hasErrors()) {
            flash.message = "TcpListeningDatasource ${tcpListeningDatasource.id} created"
            redirect(action: show, id: tcpListeningDatasource.id)
        }
        else {
            render(view: 'create', model: [tcpListeningDatasource: tcpListeningDatasource])
        }
    }

    def addTo = {
        def tcpListeningDatasource = TcpListeningDatasource.get([id: params.id])
        if (!tcpListeningDatasource) {
            flash.message = "TcpListeningDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(tcpListeningDatasource.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [tcpListeningDatasource: tcpListeningDatasource, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: tcpListeningDatasource.id)
            }
        }
    }

    def addRelation = {
        def tcpListeningDatasource = TcpListeningDatasource.get([id: params.id])
        if (!tcpListeningDatasource) {
            flash.message = "TcpListeningDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(tcpListeningDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    tcpListeningDatasource.addRelation(relationMap);
                    if (tcpListeningDatasource.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [tcpListeningDatasource: tcpListeningDatasource, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "TcpListeningDatasource ${params.id} updated"
                        redirect(action: edit, id: tcpListeningDatasource.id)
                    }

                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: addTo, id: params.id, relationName: relationName)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: addTo, id: params.id, relationName: relationName)
            }
        }
    }

    def removeRelation = {
        def tcpListeningDatasource = TcpListeningDatasource.get([id: params.id])
        if (!tcpListeningDatasource) {
            flash.message = "TcpListeningDatasource not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(tcpListeningDatasource.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    tcpListeningDatasource.removeRelation(relationMap);
                    if (tcpListeningDatasource.hasErrors()) {
                        render(view: 'edit', model: [tcpListeningDatasource: tcpListeningDatasource])
                    }
                    else {
                        flash.message = "TcpListeningDatasource ${params.id} updated"
                        redirect(action: edit, id: tcpListeningDatasource.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: tcpListeningDatasource.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: tcpListeningDatasource.id)
            }
        }
    }
}