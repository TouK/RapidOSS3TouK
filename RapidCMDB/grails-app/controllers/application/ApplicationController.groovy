package application

import model.Model
import com.ifountain.rcmdb.domain.ModelUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.runtime.StackTraceUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication
import org.codehaus.groovy.grails.plugins.GrailsPluginManager
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.ArtefactHandler
import org.codehaus.groovy.grails.commons.DomainClassArtefactHandler
import org.codehaus.groovy.grails.compiler.GrailsCompiler
import com.ifountain.grails.RapidGrailsScriptRunner

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
        try
        {
            System.setProperty("disable.system.exit", "true")
            def runner = new RapidGrailsScriptRunner();
            runner.main (["compile"] as String[]);
        }
        catch(t)
        {
            System.setProperty("disable.system.exit", "false")
            log.error("Could not compiled groovy classes", t);
        }
        ArtefactHandler domainArtHandler = null;
        grailsApplication.getArtefactHandlers().each {ArtefactHandler handler->
            if(handler.type == DomainClassArtefactHandler.TYPE)
            {
                domainArtHandler = handler;
            }
        }
        models.each{Model model->
            if(model.isGenerated())
            {
                try
                {
                    def cls = grailsApplication.classLoader.loadClass(model.name);
                    def domainClass = new DefaultGrailsDomainClass (cls);
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