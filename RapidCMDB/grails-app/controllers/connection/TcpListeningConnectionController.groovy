package connection
/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Oct 28, 2010
 * Time: 3:36:23 PM
 */

import com.ifountain.rcmdb.domain.util.ControllerUtils

class TcpListeningConnectionController {
   def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        if (!params.sort) params.sort = "name"
        [tcpListeningConnectionList: TcpListeningConnection.search("alias:*", params).results]
    }

    def show = {
        def tcpListeningConnection = TcpListeningConnection.get([id: params.id])

        if (!tcpListeningConnection) {
            flash.message = "TcpListeningConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (tcpListeningConnection.class != TcpListeningConnection)
            {
                def controllerName = tcpListeningConnection.class.simpleName;
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
                return [tcpListeningConnection: tcpListeningConnection]
            }
        }
    }

    def delete = {
        def tcpListeningConnection = TcpListeningConnection.get([id: params.id])
        if (tcpListeningConnection) {
            tcpListeningConnection.remove()
            flash.message = "TcpListeningConnection ${params.id} deleted"
            redirect(action: list)
        }
        else {
            flash.message = "TcpListeningConnection not found with id ${params.id}"
            redirect(action: list)
        }
    }

    def edit = {
        def tcpListeningConnection = TcpListeningConnection.get([id: params.id])

        if (!tcpListeningConnection) {
            flash.message = "TcpListeningConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [tcpListeningConnection: tcpListeningConnection]
        }
    }


    def update = {
        def tcpListeningConnection = TcpListeningConnection.get([id: params.id])
        if (tcpListeningConnection) {
            tcpListeningConnection.update(ControllerUtils.getClassProperties(params, TcpListeningConnection));
            if (!tcpListeningConnection.hasErrors()) {
                flash.message = "TcpListeningConnection ${params.id} updated"
                redirect(action: show, id: tcpListeningConnection.id)
            }
            else {
                render(view: 'edit', model: [tcpListeningConnection: tcpListeningConnection])
            }
        }
        else {
            flash.message = "TcpListeningConnection not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def tcpListeningConnection = new TcpListeningConnection()
        tcpListeningConnection.properties = params
        return ['tcpListeningConnection': tcpListeningConnection]
    }

    def save = {
        def tcpListeningConnection = TcpListeningConnection.add(ControllerUtils.getClassProperties(params, TcpListeningConnection))
        if (!tcpListeningConnection.hasErrors()) {
            flash.message = "TcpListeningConnection ${tcpListeningConnection.id} created"
            redirect(action: show, id: tcpListeningConnection.id)
        }
        else {
            render(view: 'create', model: [tcpListeningConnection: tcpListeningConnection])
        }
    }

    def addTo = {
        def tcpListeningConnection = TcpListeningConnection.get([id: params.id])
        if (!tcpListeningConnection) {
            flash.message = "TcpListeningConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(tcpListeningConnection.class, "relations")[relationName].type;
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [tcpListeningConnection: tcpListeningConnection, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: tcpListeningConnection.id)
            }
        }
    }

    def addRelation = {
        def tcpListeningConnection = TcpListeningConnection.get([id: params.id])
        if (!tcpListeningConnection) {
            flash.message = "TcpListeningConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(tcpListeningConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    tcpListeningConnection.addRelation(relationMap);
                    if (tcpListeningConnection.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [tcpListeningConnection: tcpListeningConnection, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "TcpListeningConnection ${params.id} updated"
                        redirect(action: edit, id: tcpListeningConnection.id)
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
        def tcpListeningConnection = TcpListeningConnection.get([id: params.id])
        if (!tcpListeningConnection) {
            flash.message = "TcpListeningConnection not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(tcpListeningConnection.class, "relations")[relationName].type;
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    tcpListeningConnection.removeRelation(relationMap);
                    if (tcpListeningConnection.hasErrors()) {
                        render(view: 'edit', model: [tcpListeningConnection: tcpListeningConnection])
                    }
                    else {
                        flash.message = "TcpListeningConnection ${params.id} updated"
                        redirect(action: edit, id: tcpListeningConnection.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: tcpListeningConnection.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: tcpListeningConnection.id)
            }
        }
    }
}