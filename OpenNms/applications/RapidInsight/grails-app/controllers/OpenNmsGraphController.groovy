import com.ifountain.rcmdb.domain.util.ControllerUtils;
import com.ifountain.rcmdb.domain.util.DomainClassUtils;

import datasource.HttpDatasource;
import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.httpclient.util.ParameterParser;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.util.Date;
class OpenNmsGraphController {
    def final static PROPS_TO_BE_EXCLUDED = ["id":"id","_action_Update":"_action_Update","controller":"controller", "action":"action"]
    def index = { redirect(action:list,params:params) }

    // the delete, save and update actions only accept POST requests
    def allowedMethods = [delete:'POST', save:'POST', update:'POST']

    def list = {
        if(!params.max) params.max = 10
        [ openNmsGraphList: OpenNmsGraph.list( params ) ]
    }

    def show = {
        def openNmsGraph = OpenNmsGraph.get([id:params.id])

        if(!openNmsGraph) {
            flash.message = "OpenNmsGraph not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            if(openNmsGraph.class != OpenNmsGraph)
            {
                def controllerName = openNmsGraph.class.simpleName;
                if(controllerName.length() == 1)
                {
                    controllerName = controllerName.toLowerCase();
                }
                else
                {
                    controllerName = controllerName.substring(0,1).toLowerCase()+controllerName.substring(1);
                }
                redirect(action:show, controller:controllerName, id:params.id)
            }
            else
            {
                return [ openNmsGraph : openNmsGraph ]
            }
        }
    }

    def delete = {
        def openNmsGraph = OpenNmsGraph.get( [id:params.id])
        if(openNmsGraph) {
            try{
                openNmsGraph.remove()
                flash.message = "OpenNmsGraph ${params.id} deleted"
                redirect(action:list)
            }
            catch(e){
                addError("default.couldnot.delete", [OpenNmsGraph, openNmsGraph])
                flash.errors = this.errors;
                redirect(action:show, id:openNmsGraph.id)
            }

        }
        else {
            flash.message = "OpenNmsGraph not found with id ${params.id}"
            redirect(action:list)
        }
    }

    def edit = {
        def openNmsGraph = OpenNmsGraph.get( [id:params.id] )

        if(!openNmsGraph) {
            flash.message = "OpenNmsGraph not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            return [ openNmsGraph : openNmsGraph ]
        }
    }


    def update = {
        def openNmsGraph = OpenNmsGraph.get( [id:params.id] )
        if(openNmsGraph) {
            openNmsGraph.update(ControllerUtils.getClassProperties(params, OpenNmsGraph));
            if(!openNmsGraph.hasErrors()) {
                flash.message = "OpenNmsGraph ${params.id} updated"
                redirect(action:show,id:openNmsGraph.id)
            }
            else {
                render(view:'edit',model:[openNmsGraph:openNmsGraph])
            }
        }
        else {
            flash.message = "OpenNmsGraph not found with id ${params.id}"
            redirect(action:edit,id:params.id)
        }
    }

    def create = {
        def openNmsGraph = new OpenNmsGraph()
        openNmsGraph.properties = params
        return ['openNmsGraph':openNmsGraph]
    }

    def save = {
        def openNmsGraph = OpenNmsGraph.add(ControllerUtils.getClassProperties(params, OpenNmsGraph))
        if(!openNmsGraph.hasErrors()) {
            flash.message = "OpenNmsGraph ${openNmsGraph.id} created"
            redirect(action:show,id:openNmsGraph.id)
        }
        else {
            render(view:'create',model:[openNmsGraph:openNmsGraph])
        }
    }

    def addTo = {
        def openNmsGraph = OpenNmsGraph.get( [id:params.id] )
        if(!openNmsGraph){
            flash.message = "OpenNmsGraph not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            if(relationName){
                def otherClass = DomainClassUtils.getStaticMapVariable(OpenNmsGraph, "relations")[relationName].type;
                def relatedObjectList = [];
                if(otherClass){
                    relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                }
                return [openNmsGraph:openNmsGraph, relationName:relationName, relatedObjectList:relatedObjectList]
            }
            else{
               flash.message = "No relation name specified for add relation action"
               redirect(action:edit,id:openNmsGraph.id)
            }
        }
    }



    def addRelation = {
        def openNmsGraph = OpenNmsGraph.get( [id:params.id] )
        if(!openNmsGraph) {
            flash.message = "OpenNmsGraph not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;
            def otherClass = DomainClassUtils.getStaticMapVariable(OpenNmsGraph, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNmsGraph.addRelation(relationMap);
                      if(openNmsGraph.hasErrors()){
                          def relatedObjectList = otherClass.metaClass.invokeStaticMethod(otherClass, "list");
                          render(view:'addTo',model:[openNmsGraph:openNmsGraph, relationName:relationName, relatedObjectList:relatedObjectList])
                      }
                      else{
                          flash.message = "OpenNmsGraph ${params.id} updated"
                          redirect(action:edit,id:openNmsGraph.id)
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
        def openNmsGraph = OpenNmsGraph.get( [id:params.id] )
        if(!openNmsGraph) {
            flash.message = "OpenNmsGraph not found with id ${params.id}"
            redirect(action:list)
        }
        else {
            def relationName = params.relationName;

            def otherClass = com.ifountain.rcmdb.domain.util.DomainClassUtils.getStaticMapVariable(OpenNmsGraph, "relations")[relationName].type;
            if(otherClass){
                def res = otherClass.metaClass.invokeStaticMethod(otherClass, "get", params.relatedObjectId.toLong());
                if(res){
                      def relationMap = [:];
                      relationMap[relationName] = res;
                      openNmsGraph.removeRelation(relationMap);
                      if(openNmsGraph.hasErrors()){
                          render(view:'edit',model:[openNmsGraph:openNmsGraph])
                      }
                      else{
                          flash.message = "OpenNmsGraph ${params.id} updated"
                          redirect(action:edit,id:openNmsGraph.id)
                      }
                }
                else{
                    flash.message = otherClass.getName() + " not found with id ${params.relatedObjectId}"
                    redirect(action:edit,id:openNmsGraph.id)
                }
            }
            else{
                flash.message = "No relation exist with name ${relationName}"
                redirect(action:edit,id:openNmsGraph.id)
            }
        }
    }

    def reloadOperations = {
        def modelClass = grailsApplication.getClassForName("OpenNmsGraph")
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

    def viewImage={
        //response.setHeader("Content-disposition", "attachment; filename=${photo.name}")
        //response.contentType = photo.fileType //'image/jpeg' will do too
        //response.outputStream << photo.file //'myphoto.jpg' will do too

        def openNmsGraphDs=HttpDatasource.get(name:"openNmsHttpDs");
        openNmsGraphDs.doGetRequest("j_acegi_security_check", ["j_username":"admin","j_password":"admin"]);

        def graph=OpenNmsGraph.get(id:params.id);
        def image=new BufferedImage(100,100,BufferedImage.TYPE_INT_RGB )
        if(graph!=null)
        {
            def url=graph.url;
            def queryParams=new ParameterParser().parse(URIUtil.getQuery(url),'&' as char);
            def params=[:]
            for(param in queryParams)
            {
            	params[param.name]=param.value;
            }
            long end=new Date().getTime();
            long start=end-(24*60*60*1000);
            
            params["start"]=String.valueOf(start);
            params["end"]=String.valueOf(end);
            image=openNmsGraphDs.adapter.getImage(url,params);
        }

        response.contentType="image/png";

        ImageIO.write(image, "png", response.outputStream);
        response.outputStream.flush()
        return;

    }
}