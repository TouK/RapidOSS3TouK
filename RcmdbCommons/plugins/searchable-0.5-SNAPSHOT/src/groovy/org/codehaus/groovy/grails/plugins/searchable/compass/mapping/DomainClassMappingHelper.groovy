package org.codehaus.groovy.grails.plugins.searchable.compass.mapping

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass
import org.codehaus.groovy.grails.plugins.searchable.compass.converter.DefaultCompassConverterLookupHelper
import org.compass.core.converter.ConverterLookup
import org.compass.core.converter.DefaultConverterLookup

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 26, 2008
 * Time: 2:45:28 AM
 * To change this template use File | Settings | File Templates.
 */
class DomainClassMappingHelper {
    public static List getDomainClassMappings()
    {
        List domainClasses = ApplicationHolder.application.getDomainClasses();
        List allClassMappings = new ArrayList();
        domainClasses.each{GrailsDomainClass domainCLass->
            CompositeSearchableGrailsDomainClassCompassClassMapper mapper = SearchableGrailsDomainClassCompassClassMapperFactory.getDefaultSearchableGrailsDomainClassCompassClassMapper([],[:], new DefaultConverterLookup());
            allClassMappings.add(mapper.getCompassClassMapping(domainCLass, domainClasses));
        }
        CompassMappingUtils.resolveSubIndexes (allClassMappings);
        return allClassMappings;
    }
}