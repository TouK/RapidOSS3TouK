package com.ifountain.compass;

import org.codehaus.groovy.grails.plugins.searchable.compass.spring.DefaultSearchableCompassFactoryBean;
import org.compass.core.Compass;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 20, 2008
 * Time: 1:40:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidSearchableCompassFactoryBean extends DefaultSearchableCompassFactoryBean
{
    private Compass compass;
    public Object getObject() throws Exception {
        if (compass == null) {
            compass = (Compass)super.getObject();
        }
        return compass;
    }
}
