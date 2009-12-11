/*
* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
package com.ifountain.compass

import com.ifountain.rcmdb.test.util.AbstractSearchableCompassTests
import org.compass.core.Compass
import com.ifountain.rcmdb.test.util.compass.TestCompassFactory
import org.apache.commons.io.FileUtils
import org.codehaus.groovy.grails.commons.GrailsApplication
import org.codehaus.groovy.grails.commons.ApplicationHolder
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.DomainClassMappingHelper
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.CompassClassMapping
import org.apache.lucene.store.RAMDirectory
import org.apache.lucene.store.Directory
import org.apache.lucene.store.FSDirectory
import org.compass.core.config.CompassSettings
import com.ifountain.rcmdb.domain.generation.ModelGenerator
import org.apache.lucene.store.NoLockFactory

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 26, 2008
 * Time: 3:36:31 AM
 * To change this template use File | Settings | File Templates.
 */
class CompositeDirectoryWrapperTest extends  AbstractSearchableCompassTests{
    Compass compass;

    public void setUp() {
        super.setUp()
        FileUtils.deleteDirectory (new File(TestCompassFactory.indexDirectory));
        System.setProperty("mirrorBufferUpperLimit", "128")
        System.setProperty("mirrorBufferLowerLimit", "64")
    }

    public void tearDown() {
        super.tearDown();
        if(compass)
        {
            compass.close();
        }
        System.setProperty("mirrorBufferUpperLimit", "128")
        System.setProperty("mirrorBufferLowerLimit", "64")
    }

    public void testConfig()
    {
        CompassSettings settings = new CompassSettings();
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        System.setProperty("mirrorBufferUpperLimit", "aaa")
        System.setProperty("mirrorBufferLowerLimit", "1")
        try
        {
            provider.configure (settings);
            fail("Should throw exception since mirrorBufferUpperLimit is invalid");
        }
        catch(InvalidMirrorBufferSizeException e)
        {
        }

        System.setProperty("mirrorBufferUpperLimit", "1")
        System.setProperty("mirrorBufferLowerLimit", "aaa")
        try
        {
            provider.configure (settings);
            fail("Should throw exception since mirrorBufferLowerLimit is invalid");
        }
        catch(InvalidMirrorBufferSizeException e)
        {
        }
        System.setProperty("mirrorBufferUpperLimit", "1")
        System.setProperty("mirrorBufferLowerLimit", "5")
        try
        {
            provider.configure (settings);
            fail("Should throw exception sincer mirrorBufferUpperLimit should be greater than mirrorBufferLowerLimit");
        }
        catch(InvalidMirrorBufferSizeException e)
        {
        }
    }

    public void testWrap()
    {
        GrailsApplication application = TestCompassFactory.getGrailsApplication([SubIndexSpecifiedMirrorProviderDomainClass, RamProviderDomainClass, RamProviderChildDomainClassLevel1WithNoStorageType,RamProviderChildDomainClassLevel2WithNoStorageType,FileProviderDomainClass, MirrorProviderDomainClass, NullProviderDomainClass])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];
        DomainClassMappingHelper.getDomainClassMappings().each{CompassClassMapping mapping->
            mappings[mapping.getMappedClass().name] = mapping;
        }
        Directory mainDir = FSDirectory.getDirectory("trial");
        mainDir.setLockFactory (NoLockFactory.getNoLockFactory())
        Directory dir = provider.wrap(mappings[SubIndexSpecifiedMirrorProviderDomainClass.name].subIndex, mainDir);
        assertTrue (dir instanceof MemoryMirrorDirectoryWrapper);
        assertSame (NoLockFactory.getNoLockFactory(), dir.getLockFactory());
        dir = provider.wrap(mappings[RamProviderDomainClass.name].subIndex,  mainDir);
        assertTrue (dir instanceof RAMDirectory);
        assertSame (NoLockFactory.getNoLockFactory(), dir.getLockFactory());
        dir = provider.wrap(mappings[FileProviderDomainClass.name].subIndex,  mainDir);
        assertTrue (dir instanceof FSDirectory);
        assertSame (NoLockFactory.getNoLockFactory(), dir.getLockFactory());
        dir = provider.wrap(mappings[MirrorProviderDomainClass.name].subIndex,  mainDir);
        assertTrue (dir instanceof MemoryMirrorDirectoryWrapper);
        assertSame (NoLockFactory.getNoLockFactory(), dir.getLockFactory());
        dir = provider.wrap(mappings[NullProviderDomainClass.name].subIndex,  mainDir);
        assertTrue (dir instanceof FSDirectory);
        assertSame (NoLockFactory.getNoLockFactory(), dir.getLockFactory());

        //test for the childs of RamProviderDomainClass should both be same as parent since not specified
        dir = provider.wrap(mappings[RamProviderChildDomainClassLevel1WithNoStorageType.name].subIndex,  mainDir);
        assertTrue ("False dir ${dir.class} should be ${RAMDirectory}",dir instanceof RAMDirectory);
        assertSame (NoLockFactory.getNoLockFactory(), dir.getLockFactory());

        dir = provider.wrap(mappings[RamProviderChildDomainClassLevel2WithNoStorageType.name].subIndex,  mainDir);
        assertTrue ("False dir ${dir.class} should be ${RAMDirectory}",dir instanceof RAMDirectory);
        assertSame (NoLockFactory.getNoLockFactory(), dir.getLockFactory());
    }
}

class RamProviderDomainClass{
    static searchable = {
        storageType CompositeDirectoryWrapperProvider.RAM_DIR_TYPE
    }
    static relations = [:]
    Long id
    Long version
    String prop1 = "";
}

class RamProviderChildDomainClassLevel1WithNoStorageType extends RamProviderDomainClass{
    static searchable = {

    }
    static relations = [:]
}

class RamProviderChildDomainClassLevel2WithNoStorageType extends RamProviderChildDomainClassLevel1WithNoStorageType{
    static searchable = {

    }
    static relations = [:]
}

class FileProviderDomainClass{
    static searchable = {
        storageType CompositeDirectoryWrapperProvider.FILE_DIR_TYPE
    }
    static relations = [:]
    Long id
    Long version
    String prop1 = "";
}

class MirrorProviderDomainClass{
    static searchable = {
        storageType CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE
    }
    static relations = [:]
    Long id
    Long version
    String prop1 = "";
}

class SubIndexSpecifiedMirrorProviderDomainClass{
    static searchable = {
        storageType CompositeDirectoryWrapperProvider.MIRRORED_DIR_TYPE
    }
    static relations = [:]
    Long id
    Long version
    String prop1 = "";
}
class NullProviderDomainClass{
    static searchable = {
    }
    static relations = [:]
    Long id
    Long version
    String prop1 = "";
}