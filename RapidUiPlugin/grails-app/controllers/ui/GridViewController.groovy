package ui

import java.util.regex.Pattern
import java.util.regex.Matcher
import grails.converters.deep.XML
import com.ifountain.rcmdb.domain.util.ControllerUtils;

/**
* Created by IntelliJ IDEA.
* User: Sezgin Kucukkaraaslan
* Date: Sep 22, 2008
* Time: 2:50:18 PM
*/
class GridViewController {
    private static final Pattern COLUMN_PROPERTY_PATTERN = Pattern.compile("column(\\d+)(attributeName|header|width)");
    def index = {redirect(action: list, params: params)}

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete: 'POST', save: 'POST', update: 'POST']

    def list = {
        def gridViews = GridView.search("username:\"${session.username}\"", params).results;
        withFormat {
            xml {
                render(contentType: 'text/xml') {
                    Views {
                        for (view in gridViews) {
                            View(id:view.id, name:view.name, defaultSortColumn:view.defaultSortColumn, sortOrder: view.sortOrder){
                                view.gridColumns.each{GridColumn gridColumn ->
                                   Column(attributeName:gridColumn.attributeName, header:gridColumn.header, width:gridColumn.width, columnIndex:gridColumn.columnIndex);
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    def show = {
        def gridView = GridView.get([id: params.id])

        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            if (gridView.class != GridView)
            {
                def controllerName = gridView.class.name;
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
                return [gridView: gridView]
            }
        }
    }

    def delete = {
        def gridView = GridView.get([id: params.id]);
        if (gridView) {
            gridView.remove()
            withFormat {
                html {
                    flash.message = "GridView ${params.id} deleted"
                    redirect(action: list)
                }
                xml {render(text: ControllerUtils.convertSuccessToXml("GridView ${gridView.id} deleted"), contentType: "text/xml")}
            }
        }
        else {
            addError("default.object.not.found", [GridView.class.name, params.id ? params.id : params.name]);
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
        def gridView = GridView.get([id: params.id])

        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            return [gridView: gridView]
        }
    }


    def update = {
        def gridView = GridView.get([id: params.id])
        if (gridView) {
            gridView.update(ControllerUtils.getClassProperties(params, GridView));
            if (!gridView.hasErrors()) {
                flash.message = "GridView ${params.id} updated"
                redirect(action: show, id: gridView.id)
            }
            else {
                render(view: 'edit', model: [gridView: gridView])
            }
        }
        else {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: edit, id: params.id)
        }
    }

    def create = {
        def gridView = new GridView()
        gridView.properties = params
        return ['gridView': gridView]
    }

    def save = {
        def gridView = GridView.add(ControllerUtils.getClassProperties(params, GridView))
        if (!gridView.hasErrors()) {
            flash.message = "GridView ${gridView.id} created"
            redirect(action: show, id: gridView.id)
        }
        else {
            render(view: 'create', model: [gridView: gridView])
        }
    }

    def add = {
        params.username = session.username;
        def gridView = GridView.add([name: params.name, username: session.username, defaultSortColumn: params.defaultSortColumn, sortOrder:params.sortOrder]);
        if (!gridView.hasErrors()) {
            gridView.gridColumns.each {
                it.remove();
            }
            def columns = [:];
            params.each {key, value ->
                Matcher matcher = COLUMN_PROPERTY_PATTERN.matcher(key);
                if (matcher.matches())
                {
                    int order = Integer.parseInt(matcher.group(1));
                    String propertyName = matcher.group(2);
                    def column = columns.get(order);
                    if (column == null)
                    {
                        column = [columnIndex: order];
                        columns.put(order, column);
                    }
                    column."${propertyName}" = value;
                }
            }
            columns.each {index, columnMap ->
                columnMap.put("gridView", gridView);
                GridColumn.add(columnMap);
            }
            withFormat {
                xml {render(text: ControllerUtils.convertSuccessToXml("GridView ${gridView.id} created"), contentType: "text/xml")}
            }

        }
        else {
            withFormat {
                xml {render(text: errorsToXml(gridView.errors), contentType: "text/xml")}
            }
        }
    }

    def addTo = {
        def gridView = GridView.get([id: params.id])
        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            if (relationName) {
                def otherClass = gridView.hasMany[relationName];
                def relatedObjectList = [];
                if (otherClass) {
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [gridView: gridView, relationName: relationName, relatedObjectList: relatedObjectList]
            }
            else {
                flash.message = "No relation name specified for add relation action"
                redirect(action: edit, id: gridView.id)
            }
        }
    }

    def addRelation = {
        def gridView = GridView.get([id: params.id])
        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = gridView.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    gridView.addRelation(relationMap);
                    if (gridView.hasErrors()) {
                        def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                        render(view: 'addTo', model: [gridView: gridView, relationName: relationName, relatedObjectList: relatedObjectList])
                    }
                    else {
                        flash.message = "GridView ${params.id} updated"
                        redirect(action: edit, id: gridView.id)
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
        def gridView = GridView.get([id: params.id])
        if (!gridView) {
            flash.message = "GridView not found with id ${params.id}"
            redirect(action: list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = gridView.hasMany[relationName];
            if (otherClass) {
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if (res) {
                    def relationMap = [:];
                    relationMap[relationName] = res;
                    gridView.removeRelation(relationMap);
                    if (gridView.hasErrors()) {
                        render(view: 'edit', model: [gridView: gridView])
                    }
                    else {
                        flash.message = "GridView ${params.id} updated"
                        redirect(action: edit, id: gridView.id)
                    }
                }
                else {
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action: edit, id: gridView.id)
                }
            }
            else {
                flash.message = "No relation exist with name ${relationName}"
                redirect(action: edit, id: gridView.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("GridView")
        if (modelClass)
        {
            try
            {

                modelClass.metaClass.invokeStaticMethod(modelClass, "reloadOperations", [] as Object[]);
                flash.message = "Model operations reloaded"
                redirect(action: list)
            } catch (t)
            {
                flash.message = "Exception occurred while reloading model operations Reason:${t.toString()}"
                redirect(action: list)
            }
        }
        else
        {
            flash.message = "Model currently not loaded by application. You should reload application."
            redirect(action: list)
        }
    }
}