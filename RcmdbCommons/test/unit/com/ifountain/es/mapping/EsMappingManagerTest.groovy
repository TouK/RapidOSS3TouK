package com.ifountain.es.mapping

import com.ifountain.comp.test.util.RCompTestCase
import com.ifountain.es.test.util.MockMappingProvider

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 26, 2010
 * Time: 1:29:56 PM
 */
class EsMappingManagerTest extends RCompTestCase {

  public void testEsMappingManagerWarnsListenersWhenMappingsAreLoaded() throws Exception {
    MockEsMappingListener listener = new MockEsMappingListener();
    EsMappingManager.getInstance().addListener(listener);
    EsMappingManager.getInstance().setMappingProvider(new MockMappingProvider());
    EsMappingManager.getInstance().load();
    assertTrue(listener.isMappingChanged);
  }
}

class MockEsMappingListener implements EsMappingListener {
  boolean isMappingChanged = false;

  void mappingChanged() {
    isMappingChanged = true;
  }
}
