package application

import model.Model
import com.ifountain.rcmdb.domain.ModelUtils
import org.codehaus.groovy.grails.commons.DefaultGrailsDomainClass
import org.codehaus.groovy.runtime.StackTraceUtils
import org.codehaus.groovy.grails.compiler.injection.GrailsAwareClassLoader
import org.codehaus.groovy.grails.compiler.injection.ClassInjector
import org.codehaus.groovy.grails.compiler.injection.DefaultGrailsDomainClassInjector
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 26, 2008
* Time: 1:35:56 AM
* To change this template use File | Settings | File Templates.
*/
class ApplicationController {
    public static final String RESTART_APPLICATION = "restart.application"
    def reload = {
        def models = Model.findAllByResourcesWillBeGenerated(true);
        GrailsAwareClassLoader gcl = new GrailsAwareClassLoader(Thread.currentThread().getContextClassLoader().parent);
        gcl.setShouldRecompile (true);
        String baseDirectory = System.getProperty("base.dir")
        gcl.addClasspath (baseDirectory+"/grails-app/domain");
        gcl.setClassInjectors([new DefaultGrailsDomainClassInjector()] as ClassInjector[]);
        models.each{Model model->
            try
            {
                def cls = gcl.loadClass(model.name);
                def domainClass = new DefaultGrailsDomainClass (cls);
                ModelUtils.generateModelArtefacts (domainClass, baseDirectory);
                model.resourcesWillBeGenerated = false;
                model.save(flush:true);
            }
            catch(t)
            {
                log.error("Exception occurred while creating controller, view and operations files of model ${model.name}", StackTraceUtils.deepSanitize(t));
            }
        }
        render(view:"reloading", controller:"application");
        GroovyPagesTemplateEngine.pageCache.clear();
        System.setProperty(RESTART_APPLICATION, "true");
    }
}