/*
* Copyright 2004-2005 the original author or authors.
*
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
*
*      http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
package org.codehaus.groovy.grails.plugins.quartz;

import org.codehaus.groovy.grails.commons.ArtefactHandlerAdapter;

/**
 * @author Marc Palmer (marc@anyware.co.uk)
 */
public class TaskArtefactHandler extends ArtefactHandlerAdapter {

    public static final String TYPE = "Task";


    public TaskArtefactHandler() {
        super(TYPE, GrailsTaskClass.class, DefaultGrailsTaskClass.class, null);
    }

    public boolean isArtefactClass( Class clazz ) {
        if(clazz == null) return false;
        try {
            clazz.getDeclaredMethod( GrailsTaskClassProperty.EXECUTE , new Class[]{});
        } catch (SecurityException e) {
            return false;
        } catch (NoSuchMethodException e) {
            return false;
        }

        return clazz.getName().endsWith(DefaultGrailsTaskClass.JOB);
    }
}
