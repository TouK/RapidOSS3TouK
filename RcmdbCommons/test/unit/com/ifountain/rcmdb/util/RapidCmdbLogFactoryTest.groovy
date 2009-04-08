package com.ifountain.rcmdb.util

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 8, 2009
* Time: 8:38:56 AM
* To change this template use File | Settings | File Templates.
*/
class RapidCmdbLogFactoryTest extends RapidCmdbTestCase{
    public void testGetControllerLogger()
    {
        String controllerName = "controller1";
        Logger logger = RapidCmdbLogFactory.getControllerLogger (controllerName);
        assertEquals ("grails.app.${ControllerArtefactHandler.TYPE}.${controllerName}", logger.getName());
    }
}