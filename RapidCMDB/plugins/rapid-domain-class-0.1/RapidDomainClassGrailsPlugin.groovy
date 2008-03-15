import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.grails.compiler.injection.GrailsDomainClassInjector
import org.codehaus.groovy.grails.commons.GrailsClassUtils

class RapidDomainClassGrailsPlugin {
    def version = 0.1
    def dependsOn = [:]

    def doWithSpring = {
    }

    def doWithApplicationContext = { applicationContext ->
    }

    def doWithWebDescriptor = { xml ->

    }

    def doWithDynamicMethods = { ctx ->
        println ctx.getBeansOfType(GrailsDomainClassInjector.class);
        for (dc in application.domainClasses) {
            //    registerDynamicMethods(dc, application, ctx)
            MetaClass mc = dc.metaClass
            if(dc.metaClass.hasProperty(dc, "sources"))
            {
                println dc.getName();

                def sources = GrailsClassUtils.getStaticPropertyValue (dc.clazz, "sources");
                if(sources)
                {
                    sources.each{key,value->
                        println key;
                        println value;
                    }
//                    for(source in sources)
//                    {
//                        def propName = source.name;
//                        println propName;
//                        def getter = GCU.getGetterName(propName)
//                        def setter = GCU.getSetterName(propName)
//
//                        mc.'$getter' = { String name->
//                            println "1";
//                            return "a";
//                        }
//                        mc.'$setter' = {String name, Object value ->
//                            println "2";
//                        }
//                    }
                }
            }
        }
    }

    def onChange = { event ->
    }

    def onApplicationChange = { event ->
    }
}
