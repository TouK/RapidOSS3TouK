import org.codehaus.groovy.grails.commons.GrailsClassUtils as GCU

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
                                def isNull = false;
                                keyConfiguration.each{key,value->
                                    def nameInDs = key;
                                    if(value && value.nameInDs)
                                    {
                                        nameInDs = value.nameInDs;    
                                    }
                                    def keyValue =   delegate.getProperty(key);
                                    if(keyValue)
                                    {
                                        keys[nameInDs] = keyValue;
                                    }
                                    else
                                    {
                                        isNull = true;
                                        return;
                                    }
                                }
                                if(isNull&& keys.size() > 0)
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

                        return "";
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
