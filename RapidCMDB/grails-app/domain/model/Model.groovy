package model

import org.codehaus.groovy.grails.commons.GrailsApplication
import java.lang.reflect.Method
import com.ifountain.core.domain.annotations.CmdbOperation

class Model {
    String name;
    Boolean resourcesWillBeGenerated = false;
    def grailsApplication
    Model parentModel;
    static transients = ['generated','modelFile','controllerFile','controllerGenerated']
    static hasMany = [modelProperties:ModelProperty, datasources:ModelDatasource, fromRelations:ModelRelation, toRelations:ModelRelation];
    static mappedBy = [fromRelations:'firstModel', toRelations:'secondModel']     
    static constraints = {
        name(blank:false, unique:true, validator:{val, obj ->
            def firstChar = val.charAt(0);
            if(!(firstChar >= 65 && firstChar <= 90)){
                return ['model.name.lowercased'];
            }
        });
        parentModel(nullable:true);
        resourcesWillBeGenerated(nullable:true);
    }


    public boolean isGenerated()
    {
        return getModelFile().exists();
    }
    public boolean isControllerGenerated()
    {
        return getControllerFile().exists();
    }

    def getModelFile()
    {
        return new File(System.getProperty("base.dir", ".")+"/grails-app/domain/${name}.groovy");
    }
    def getControllerFile()
    {
        return new File(System.getProperty("base.dir", ".")+"/grails-app/controllers/${name}Controller.groovy");
    }

    def getOperations()
    {
        def operations = [];
        if(name)
        {
            Class cls = grailsApplication.getClassForName(name)
            if(cls)
            {
                def methods = cls.getDeclaredMethods();
                methods.each{domainMethod->
                    def annotation = domainMethod.getAnnotation(CmdbOperation.class)
                    if(annotation)
                    {
                        operations += [name:domainMethod.getName(), description:annotation.description()];
                        return;
                    }
                }
            }
        }
        return operations;
    }
        
    String toString(){
        return "$name";
    }

}
