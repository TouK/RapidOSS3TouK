package com.ifountain.es.mapping

import com.ifountain.comp.test.util.RCompTestCase
import com.ifountain.es.test.util.MockMappingProvider

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 26, 2010
 * Time: 1:29:56 PM
 */
class EsMappingManagerTest extends RCompTestCase {

  public void setUp() {
    super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
    EsMappingManager.destroy();
  }

  public void tearDown() {
    super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    EsMappingManager.destroy();
  }

  public void testLoad() {
    Map<String, TypeMapping> expectedMappings = [type1: new TypeMapping("type1", "index1")];
    MockMappingProvider provider = new MockMappingProvider();
    provider.setMappings(expectedMappings);

    EsMappingManager.getInstance().setMappingProvider(provider);
    EsMappingManager.getInstance().load();
    Map<String, TypeMapping> mappings = EsMappingManager.getInstance().getTypeMappings();
    assertEquals(expectedMappings.size(), mappings.size());
    assertSame(expectedMappings.type1, mappings.type1);
    try {
      mappings.put("type2", null);
      fail("Should throw exception since mappings map is unmodifyable");
    }
    catch (UnsupportedOperationException e) {
    }
  }

  public void testLoadThrowsExceptionIfNoProviderSpecified() {
    try {
      EsMappingManager.getInstance().load();
      fail("Should throw exception since no provider specified");
    }
    catch (MappingException e) {
      MappingException expectedEx = MappingException.noProviderSpecified();
      assertEquals (expectedEx.toString(), e.toString());
    }
  }

  public void testReLoadThrowsExceptionIfNoProviderSpecified() {
    try {
      EsMappingManager.getInstance().reload();
      fail("Should throw exception since no provider specified");
    }
    catch (MappingException e) {
      MappingException expectedEx = MappingException.noProviderSpecified();
      assertEquals (expectedEx.toString(), e.toString());
    }
  }

  public void testLoadWillReturnOldMappingsThrowsException() {
    Map<String, TypeMapping> expectedMappings = [type1: new TypeMapping("type1", "index1")];
    MockMappingProvider provider = new MockMappingProvider();
    provider.setMappings(expectedMappings);

    EsMappingManager.getInstance().setMappingProvider(provider);
    EsMappingManager.getInstance().load();

    Map<String, TypeMapping> expectedMappings2 = [type2: new TypeMapping("type2", "index1")];
    provider.setMappings(expectedMappings2);    
    MappingException expectedException = new MappingException();
    provider.setExceptionToBeThrown (expectedException);

    try {
      EsMappingManager.getInstance().load();
      fail("Should throw exception");
    }
    catch (MappingException e) {
      assertSame (expectedException, e);
    }
    assertSame (expectedMappings.type1, EsMappingManager.getInstance().getTypeMappings().type1);
  }

  
  public void testReLoadWillReturnOldMappingsThrowsException() {
    Map<String, TypeMapping> expectedMappings = [type1: new TypeMapping("type1", "index1")];
    MockMappingProvider provider = new MockMappingProvider();
    provider.setMappings(expectedMappings);

    EsMappingManager.getInstance().setMappingProvider(provider);
    EsMappingManager.getInstance().load();

    Map<String, TypeMapping> expectedMappings2 = [type2: new TypeMapping("type2", "index1")];
    provider.setMappings(expectedMappings2);
    MappingException expectedException = new MappingException();
    provider.setExceptionToBeThrown (expectedException);

    try {
      EsMappingManager.getInstance().reload();
      fail("Should throw exception");
    }
    catch (MappingException e) {
      assertSame (expectedException, e);
    }
    assertSame (expectedMappings.type1, EsMappingManager.getInstance().getTypeMappings().type1);
  }

  public void testIfLoadThrowsExceptionExistingMappingsWillNotChange(){
    Map<String, TypeMapping> expectedMappings = [type1: new TypeMapping("type1", "index1")];
    MockMappingProvider provider = new MockMappingProvider();
    provider.setMappings(expectedMappings);

    EsMappingManager.getInstance().setMappingProvider(provider);
    EsMappingManager.getInstance().load();
    Map<String, TypeMapping> mappings = EsMappingManager.getInstance().getTypeMappings();
    assertEquals(expectedMappings.size(), mappings.size());
    assertSame(expectedMappings.type1, mappings.type1);
    try {
      mappings.put("type2", null);
      fail("Should throw exception since mappings map is unmodifyable");
    }
    catch (UnsupportedOperationException e) {
    }
  }



  public void testReload() {
    Map<String, TypeMapping> expectedMappings = [type1: new TypeMapping("type1", "index1")];
    MockMappingProvider provider = new MockMappingProvider();
    provider.setReloadMappings(expectedMappings);

    EsMappingManager.getInstance().setMappingProvider(provider);
    EsMappingManager.getInstance().reload();
    Map<String, TypeMapping> mappings = EsMappingManager.getInstance().getTypeMappings();
    assertEquals(expectedMappings.size(), mappings.size());
    assertSame(expectedMappings.type1, mappings.type1);
    try {
      mappings.put("type2", null);
      fail("Should throw exception since mappings map is unmodifyable");
    }
    catch (UnsupportedOperationException e) {
    }
  }

  public void testEsMappingManagerWarnsListenersWhenMappingsAreLoaded() throws Exception {
    MockEsMappingListener listener = new MockEsMappingListener();
    EsMappingManager.getInstance().addListener(listener);
    EsMappingManager.getInstance().setMappingProvider(new MockMappingProvider());
    EsMappingManager.getInstance().load();
    assertTrue(listener.isMappingChanged);
  }

  public void testEsMappingManagerWarnsListenersWhenMappingsAreReLoaded() throws Exception {
    MockEsMappingListener listener = new MockEsMappingListener();
    EsMappingManager.getInstance().addListener(listener);
    EsMappingManager.getInstance().setMappingProvider(new MockMappingProvider());
    EsMappingManager.getInstance().reload();
    assertTrue(listener.isMappingChanged);
  }
}

class MockEsMappingListener implements EsMappingListener {
  boolean isMappingChanged = false;

  void mappingChanged() {
    isMappingChanged = true;
  }
}
