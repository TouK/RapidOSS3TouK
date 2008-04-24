package com.ifountain.rcmdb.domain

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter
import org.codehaus.groovy.grails.commons.ArtefactInfo
import org.codehaus.groovy.grails.commons.GrailsClass

/**
* Created by IntelliJ IDEA.
* User: mustafa
* Date: Apr 25, 2008
* Time: 1:02:51 AM
* To change this template use File | Settings | File Templates.
*/
class OperationsArtefactHandler extends ArtefactHandlerAdapter{
    public static final String TYPE = "DomainOperations"
    private GrailsClass[] operationClasses;
    public OperationsArtefactHandler() {
        super(TYPE, DefaultOperationClass.class, DefaultOperationClass.class, DefaultOperationClass.OPERATIONS); //To change body of overridden methods use File | Settings | File Templates.
    }

    public void initialize(ArtefactInfo artefacts) {
        operationClasses = artefacts.getGrailsClasses();
        println "inited ${operationClasses}"
    }

}