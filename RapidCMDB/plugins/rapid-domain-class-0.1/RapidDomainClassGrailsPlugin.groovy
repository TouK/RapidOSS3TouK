import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU
import org.codehaus.groovy.runtime.InvokerHelper
import org.codehaus.groovy.grails.compiler.injection.GrailsDomainClassInjector
import org.codehaus.groovy.grails.commons.GrailsClassUtils
import org.codehaus.groovy.grails.commons.metaclass.PropertyAccessInterceptor
import org.codehaus.groovy.grails.commons.metaclass.AbstractDynamicProperty
import org.codehaus.groovy.runtime.metaclass.ClosureMetaMethod

class RapidDomainClassGrailsPlugin {
    def version = 0.1
    def dependsOn = [:]
    def loadAfter = ['hibernate']
    def doWithSpring = {
//        println application.domainClasses;
    }

    def doWithApplicationContext = { applicationContext ->

    }

    def doWithWebDescriptor = { xml ->


    }

    def doWithDynamicMethods = { ctx ->
        for (dc in application.domainClasses) {
            MetaClass mc = dc.metaClass

            if(dc.metaClass.hasProperty(dc, "propertyConfiguration") && dc.metaClass.hasProperty(dc, "dataSources"))
            {
                def dataSources = GCU.getStaticPropertyValue (dc.clazz, "dataSources");
                def propertyConfiguration = GCU.getStaticPropertyValue (dc.clazz, "propertyConfiguration");
                mc.setProperty = {String name, Object value->
                    def propertyConfig = propertyConfiguration[name];
                    if(!propertyConfig)
                    {
                        mc.getMetaProperty(name).setProperty(delegate, value);
                    }
                };

                mc.getProperty = {String name->
                    def propertyConfig = propertyConfiguration[name];
                    if(!propertyConfig)
                    {
                        return mc.getMetaProperty(name).getProperty(delegate);
                    }
                    else
                    {
                        def datasourceName =  propertyConfig.datasource;
                        if(datasourceName)
                        {
                            def propertyDatasource = BaseDatasource.findByName(datasourceName);
                            if(propertyDatasource)
                            {
                                def datasourceConfig = dataSources[propertyConfig.datasource];
                                def keyConfiguration = datasourceConfig.keys;
                                def keys = [:];
                                keyConfiguration.each{key,value->
                                    def nameInDs = key;
                                    if(value && value.nameInDs)
                                    {
                                        nameInDs = value.nameInDs;    
                                    }
                                    keys[nameInDs] =   delegate.getProperty(key);
                                }
                                if(keys.size() > 0)
                                {
                                    def propName = name;
                                    if(propertyConfig.nameInDs)
                                    {
                                        propName = propertyConfig.nameInDs;
                                    }
                                    return propertyDatasource.getProperty (keys, propName);
                                }
                            }
                        }
                    }
                };
            }
        }
    }

    def onChange = { event ->
    }

    def onApplicationChange = { event ->
    }
}
