package org.ifountain.grails.domain;

import org.codehaus.groovy.grails.commons.*;
import groovy.lang.Closure;
import groovy.lang.GroovyObject;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Mar 18, 2008
 * Time: 12:50:42 AM
 * To change this template use File | Settings | File Templates.
 */
public class RapidDomainArtefactHandler extends ArtefactHandlerAdapter{
    public static final String TYPE = "Domain";

    public RapidDomainArtefactHandler() {

        super(TYPE, GrailsDomainClass.class, DefaultGrailsDomainClass.class, null);
        System.out.println("ART");
    }


    public GrailsClass newArtefactClass(Class artefactClass) {
        System.out.println("Returning domain class");
        return new DefaultGrailsDomainClass(artefactClass);
    }

    /**
     * Sets up the relationships between the domain classes, this has to be done after
     * the intial creation to avoid looping
     */
    public void initialize(ArtefactInfo artefacts) {
        System.out.println("initializing");
        log.debug("Configuring domain class relationships");
        GrailsDomainConfigurationUtil.configureDomainClassRelationships(
            artefacts.getGrailsClasses(),
            artefacts.getGrailsClassesByName());
    }

    public boolean isArtefactClass( Class clazz ) {
         System.out.println("Is art:"+clazz);
        return isDomainClass(clazz);

    }

    public static boolean isDomainClass(Class clazz) {
        // its not a closure
        if(clazz == null)return false;
        if(Closure.class.isAssignableFrom(clazz)) {
            return false;
        }
        Class testClass = clazz;
        boolean result = false;
        while(testClass!=null&&!testClass.equals(GroovyObject.class)&&!testClass.equals(Object.class)) {
            try {
                // make sure the identify and version field exist
                testClass.getDeclaredField( GrailsDomainClassProperty.IDENTITY );
                testClass.getDeclaredField( GrailsDomainClassProperty.VERSION );

                // passes all conditions return true
                result = true;
                break;
            } catch (SecurityException e) {
                // ignore
            } catch (NoSuchFieldException e) {
                // ignore
            }
            testClass = testClass.getSuperclass();
        }
        return result;
    }
}
