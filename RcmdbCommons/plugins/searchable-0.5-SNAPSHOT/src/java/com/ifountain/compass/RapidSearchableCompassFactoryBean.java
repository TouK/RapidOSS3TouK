package com.ifountain.compass;

import org.codehaus.groovy.grails.plugins.searchable.compass.spring.DefaultSearchableCompassFactoryBean;
import org.compass.core.Compass;
import org.compass.core.spi.InternalCompass;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jul 20, 2008
 * Time: 1:40:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class RapidSearchableCompassFactoryBean extends DefaultSearchableCompassFactoryBean
{
    private RapidCompass compass;
    private int batchSize = 100;
    private long trCommitTime = 300000;
    public Object getObject() throws Exception {
        if (compass == null)
        {
            compass = new RapidCompass((InternalCompass)super.getObject(), batchSize, trCommitTime);
        }
        return compass;
    }

    public void setBatchSize(int batchSize) {
        this.batchSize = batchSize;
    }

    public void setTrCommitTime(long trCommitTime) {
        this.trCommitTime = trCommitTime;
    }
}
