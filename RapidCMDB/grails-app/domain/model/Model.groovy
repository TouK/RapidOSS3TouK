package model

import org.codehaus.groovy.grails.commons.GrailsApplication
import java.lang.reflect.Method
import com.ifountain.core.domain.annotations.CmdbOperation
import com.ifountain.rcmdb.domain.ModelUtils
import org.codehaus.groovy.grails.commons.ConfigurationHolder

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
            def invalidNames = ConfigurationHolder.config.flatten().get("rapidcmdb.invalid.names")
            if(invalidNames.contains(val.toLowerCase()))
            {
                return ['model.name.invalid'];                
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
    def getOperationsFile()
    {
        return new File(System.getProperty("base.dir", ".")+"/operations/${name}${ModelUtils.OPERATIONS_CLASS_EXTENSION}.groovy");
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
    
    def xml(){
    	
    	def model = {
    		model{
			name(name)
			def parentModelName = null;
			if(parentModel != null) parentModelName = parentModel.name;
			parent(parentModelName)
			properties(){
				for(modelProperty in modelProperties){
					out << modelProperty.xml();
				}
			}
			datasources(){
				for(datasource in datasources){
					out << datasource.xml();
				}
			}
			fromRelations(){
				for(fromRelation in fromRelations){
					out << fromRelation.xml();
				}
			}			
		}
	}
	
	return model;
    }     

}
