import datasource.RCMDBDatasource
import com.ifountain.domain.MockModel
import model.Model
import com.ifountain.domain.ModelGeneratorTest
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.DefaultGrailsApplication

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
 * User: mustafa
 * Date: Mar 31, 2008
 * Time: 2:24:54 AM
 * To change this template use File | Settings | File Templates.
 */
class BootStrapTest extends GroovyTestCase{

    RCMDBDatasource datasource;
    RCMDBDatasource saveCalledFor;
    protected void setUp() {
        super.setUp(); //To change body of overridden methods use File | Settings | File Templates.
        datasource = null;
        saveCalledFor = null;
        RCMDBDatasource.metaClass.static.findByName = {String name->
            return datasource;
        }

        RCMDBDatasource.metaClass.save = {->
            saveCalledFor = delegate;
        }
    }

    protected void tearDown() {
        super.tearDown(); //To change body of overridden methods use File | Settings | File Templates.
        GroovySystem.metaClassRegistry.removeMetaClass(RCMDBDatasource);
        GroovySystem.metaClassRegistry.removeMetaClass(Model);
    }
    
    public void testInitInsertRCMDBDatasource()
    {
        def bStrap = new BootStrap();
        bStrap.init();
        assertEquals (RapidCMDBConstants.RCMDB, saveCalledFor.name);

        datasource =  saveCalledFor;
        saveCalledFor =null;
        bStrap = new BootStrap();
        bStrap.init();

        assertNull(saveCalledFor);
    }

    public void testGeneratesAllModelResourcesIfGenerateAllIsTrue()
    {
        def model = new Model(name:"Model1");
        fail("should be implemented");
//        try
//        {
//            def application = [:];
//            application["getDomainClasses"] = [["class":["name":model.name]]];
//            ApplicationHolder.setApplication (application as DefaultGrailsApplication);
//            Model.metaClass.static.findByNamel = {String name->
//                return model;
//            }
//            def bStrap = new BootStrap();
//            bStrap.init();
//            assertFalse (model.getModelFile().exists());
//
//            model.generateAll = true;
//            bStrap = new BootStrap();
//            bStrap.init();
//            assertTrue (model.getModelFile().exists());
//        }
//        finally
//        {
//            model.getModelFile().delete();
//        }
    }
}