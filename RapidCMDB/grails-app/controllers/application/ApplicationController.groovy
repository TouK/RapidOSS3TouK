package application

import model.Model
import com.ifountain.rcmdb.domain.ModelUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.runtime.StackTraceUtils

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 26, 2008
* Time: 1:35:56 AM
* To change this template use File | Settings | File Templates.
*/
class ApplicationController {
    def reload = {
        def models = Model.findAllByResourcesWillBeGenerated(true);
        def classLoader = new GroovyClassLoader();
        classLoader.addClasspath ("${System.getProperty("base.dir")}/grails-app/domain");
        models.each{Model model->
            if(model.isGenerated())
            {
                println "creating for ${model.name}"
                try
                {
                    def cls = classLoader.loadClass(model.name);
                    cls.metaClass.addMetaBeanProperty(new MetaBeanProperty("id", Long.class, null, null));
                    cls.metaClass.addMetaBeanProperty(new MetaBeanProperty("version", Long.class, null, null));
                    def domainClass = new DefaultGrailsDomainClass(cls);
                    ModelUtils.generateModelArtefacts (domainClass);
                    model.resourcesWillBeGenerated = false;
                    model.save(flush:true);
                }
                catch(t)
                {
                    log.error("Exception occurred while creating controller, view and operations files of model ${model.name}", StackTraceUtils.deepSanitize(t));
                }
            }
            else
            {
                log.error("Could not create controller, view and operations files of model ${model.name}. Model file does not exist");
            }
        }
        System.setProperty("disable.auto.recompile", "false");
        render(view:"reloading", controller:"application");
    }
}