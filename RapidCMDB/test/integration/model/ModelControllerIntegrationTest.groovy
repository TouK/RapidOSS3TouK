package model

import org.codehaus.groovy.grails.commons.GrailsApplication

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
* noted in a separate copyright notice. All rights reserved.
* This file is part of RapidCMDB.
*
* RapidCMDB is free software; you can redistribute it and/or modify
* it under the terms version 2 of the GNU General Public License as
* published by the Free Software Foundation. This program is distributed
* in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
* even the implied warranty of MERCHANTABILITY or FITNESS FOR A
* PARTICULAR PURPOSE. See the GNU General Public License for more
* details.
*
* You should have received a copy of the GNU General Public License
* along with this program; if not, write to the Free Software
* Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307
* USA.
*/
/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Mar 28, 2008
 * Time: 9:07:45 AM
 * To change this template use File | Settings | File Templates.
 */
class ModelControllerIntegrationTest extends GroovyTestCase{
    void testGenerateModel() {
        GrailsApplication.PROJECT_TEST_CLASSES_DIR
        def model = new Model(name:"Model1");
        model.save();
        model.addToModelProperties(new ModelProperty(name:"prop1",type:ModelProperty.stringType, defaultValue:"defaultValue"));
        model.save();

        def mdc = new ModelController();
        mdc.params["id"] = model.id;
        mdc.generate();
        assertEquals("/model/show/" + model.id, mdc.response.redirectedUrl);
        assertNotNull(getClass().getClassLoader().loadClass (model.name));
    }
}