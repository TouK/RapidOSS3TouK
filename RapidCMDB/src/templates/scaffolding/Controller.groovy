<%=packageName ? "import ${packageName}.${className}" : ''%>
class ${className}Controller {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ ${propertyName}List: ${className}.list( params ) ]
    }

    def show = {
        def ${propertyName} = ${className}.get( params.id )

        if(!${propertyName}) {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else { return [ ${propertyName} : ${propertyName} ] }
    }

    def delete = {
        def ${propertyName} = ${className}.get( params.id )
        if(${propertyName}) {
            ${propertyName}.delete()
            flash.message = "${className} \${params.id} deleted"
            redirect(action:list)
        }
        else {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def ${propertyName} = ${className}.get( params.id )

        if(!${propertyName}) {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            return [ ${propertyName} : ${propertyName} ]
        }
    }

    def getClassProperties(params, domainClass)
    {
        def returnedParams = [:]
        params.each{propName, propValue->
            if(!PROPS_TO_BE_EXCLUDED.containsKey(propName))
            {
                def indexOfDot = propName.indexOf(".");
                if(indexOfDot < 0)
                {
                     if(propValue instanceof Map)
                    {
                        if(propValue["id"] != "null")
                        {
                            def id = Long.parseLong(propValue["id"]);
                            def fieldType = domainClass.getDeclaredField(propName).type;
                            returnedParams[propName] = fieldType.metaClass.invokeStaticMethod(fieldType, "get", [id] as Object[])
                        }
                        else
                        {
                            returnedParams[propName] = null;
                        }
                    }
                    else
                    {
                        returnedParams[propName] = propValue;
                    }
                }
            }
        }
        return returnedParams;
    }
    def update = {
        def ${propertyName} = ${className}.get( params.id )
        if(${propertyName}) {
            ${propertyName}.update(getClassProperties(params, ${className}));
            if(!${propertyName}.hasErrors() && ${propertyName}.save()) {
                flash.message = "${className} \${params.id} updated"
                redirect(action:show,id:${propertyName}.id)
            }
            else {
                render(view:'edit',model:[${propertyName}:${propertyName}])
            }
        }
        else {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def ${propertyName} = new ${className}()
        ${propertyName}.properties = params
        return ['${propertyName}':${propertyName}]
    }

    def save = {
        def ${propertyName} = ${className}.add(getClassProperties(params, ${className}))
        if(!${propertyName}.hasErrors() && ${propertyName}.save()) {
            flash.message = "${className} \${${propertyName}.id} created"
            redirect(action:show,id:${propertyName}.id)
        }
        else {
            render(view:'create',model:[${propertyName}:${propertyName}])
        }
    }

    def addTo = {
        def ${propertyName} = ${className}.get( params.id )
        if(!${propertyName}){
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = ${propertyName}.hasMany[relationName];
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [${propertyName}:${propertyName}, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:${propertyName}.id)
            }
        }
    }

    def addRelation = {
        def ${propertyName} = ${className}.get( params.id )
        if(!${propertyName}) {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = ${propertyName}.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      ${propertyName}.addRelation(relationMap);
                      if(${propertyName}.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[${propertyName}:${propertyName}, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "${className} \${params.id} updated"
                          redirect(action:edit,id:${propertyName}.id)
                      }

                }
                else{
                    flash.message = otherClass.getName() + " not found with id \${params.relatedObjectId}"
                    redirect(action:addTo, id:params.id, relationName:relationName)
                }
            }
            else{
                flash.message = "No relation exist with name \${relationName}"
                redirect(action:addTo, id:params.id, relationName:relationName)
            }
        }
    }

    def removeRelation = {
        def ${propertyName} = ${className}.get( params.id )
        if(!${propertyName}) {
            flash.message = "${className} not found with id \${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = ${propertyName}.hasMany[relationName];
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      ${propertyName}.removeRelation(relationMap);
                      if(${propertyName}.hasErrors()){
                          render(view:'edit',model:[${propertyName}:${propertyName}])
                      }
                      else{
                          flash.message = "${className} \${params.id} updated"
                          redirect(action:edit,id:${propertyName}.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id \${params.relatedObjectId}"
                    redirect(action:edit,id:${propertyName}.id)
                }
            }
            else{
                flash.message = "No relation exist with name \${relationName}"
                redirect(action:edit,id:${propertyName}.id)
            }
        }
    }
}