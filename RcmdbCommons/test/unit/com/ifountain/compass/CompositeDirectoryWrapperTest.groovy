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

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Oct 26, 2008
 * Time: 3:36:31 AM
 * To change this template use File | Settings | File Templates.
 */
class CompositeDirectoryWrapperProviderTest extends  AbstractSearchableCompassTests{
    Compass compass;

    protected void setUp() {
        super.setUp()
        FileUtils.deleteDirectory (new File(TestCompassFactory.indexDirectory));
        System.setProperty("mirrorBufferUpperLimit", "128")
        System.setProperty("mirrorBufferLowerLimit", "64")
    }

    protected void tearDown() {
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
        GrailsApplication application = TestCompassFactory.getGrailsApplication([SubIndexSpecifiedMirrorProviderDomainClass, RamProviderDomainClass, FileProviderDomainClass, MirrorProviderDomainClass, NullProviderDomainClass])
        ApplicationHolder.application = application;
        CompositeDirectoryWrapperProvider provider = new CompositeDirectoryWrapperProvider();
        Map mappings = [:];
        DomainClassMappingHelper.getDomainClassMappings().each{CompassClassMapping mapping->
            mappings[mapping.getMappedClass().name] = mapping;            
        }
        Directory mainDir = FSDirectory.getDirectory("trial");
        Directory dir = provider.wrap(mappings[SubIndexSpecifiedMirrorProviderDomainClass.name].subIndex, mainDir);
        assertTrue (dir instanceof MemoryMirrorDirectoryWrapper);
        dir = provider.wrap(mappings[RamProviderDomainClass.name].subIndex,  mainDir);
        assertTrue (dir instanceof RAMDirectory);
        dir = provider.wrap(mappings[FileProviderDomainClass.name].subIndex,  mainDir);
        assertTrue (dir instanceof FSDirectory);
        dir = provider.wrap(mappings[MirrorProviderDomainClass.name].subIndex,  mainDir);
        assertTrue (dir instanceof MemoryMirrorDirectoryWrapper);
        dir = provider.wrap(mappings[NullProviderDomainClass.name].subIndex,  mainDir);
        assertTrue (dir instanceof FSDirectory);
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