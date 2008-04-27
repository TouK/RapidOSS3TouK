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
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector

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
        GrailsAwareClassLoader gcl = new GrailsAwareClassLoader();
        gcl.addClasspath (System.getProperty("base.dir")+"/grails-app/domain");
        gcl.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);
        models.each{Model model->
            if(model.isGenerated())
            {
                try
                {
                    def cls = gcl.loadClass(model.name);
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