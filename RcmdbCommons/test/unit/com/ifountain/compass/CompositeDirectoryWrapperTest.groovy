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
    }

    protected void tearDown() {
        super.tearDown();
        if(compass)
        {
            compass.close();
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
        dirType "ram"
    }
    static relations = [:]
    Long id
    Long version
    String prop1 = "";
}

class FileProviderDomainClass{
    static searchable = {
        dirType "file"
    }
    static relations = [:]
    Long id
    Long version
    String prop1 = "";
}

class MirrorProviderDomainClass{
    static searchable = {
        dirType "mirror"
    }
    static relations = [:]
    Long id
    Long version
    String prop1 = "";
}

class SubIndexSpecifiedMirrorProviderDomainClass{
    static searchable = {
        dirType "mirror"
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