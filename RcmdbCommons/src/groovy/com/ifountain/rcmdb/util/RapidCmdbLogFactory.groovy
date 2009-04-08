package com.ifountain.rcmdb.util

import org.apache.log4j.Logger
import org.codehaus.groovy.grails.commons.ControllerArtefactHandler

/**
* Created by IntelliJ IDEA.
* User: admin
* Date: Apr 7, 2009
* Time: 6:28:40 PM
* To change this template use File | Settings | File Templates.
*/
class RapidCmdbLogFactory {
    public static Logger getControllerLogger(String controllerName)
    {
        return Logger.getLogger ("grails.app.${ControllerArtefactHandler.TYPE}.${controllerName}");
    }
}