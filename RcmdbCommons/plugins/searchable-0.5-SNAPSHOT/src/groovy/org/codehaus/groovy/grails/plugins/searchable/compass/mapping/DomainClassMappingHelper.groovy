package org.codehaus.groovy.grails.plugins.searchable.compass.mapping

import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsDomainClass

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
            CompositeSearchableGrailsDomainClassCompassClassMapper mapper = SearchableGrailsDomainClassCompassClassMapperFactory.getDefaultSearchableGrailsDomainClassCompassClassMapper([],[:]);
            allClassMappings.add(mapper.getCompassClassMapping(domainCLass, domainClasses));
        }
        CompassMappingUtils.resolveSubIndexes (allClassMappings);
        return allClassMappings;
    }
}