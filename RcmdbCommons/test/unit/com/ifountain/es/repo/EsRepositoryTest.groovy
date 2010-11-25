package com.ifountain.es.repo

import com.ifountain.core.test.util.RapidCoreTestCase

/**
 * Created by Sezgin Kucukkaraaslan
 * Date: Nov 25, 2010
 * Time: 2:21:23 PM
 */
class EsRepositoryTest extends RapidCoreTestCase {

  protected void setUp() {
    super.setUp();
  }

  protected void tearDown() {
    super.tearDown();
  }

  public void testEsRepositoryIsSingleton() throws Exception {
    EsRepository repo1 = EsRepository.getInstance();
    EsRepository repo2 = EsRepository.getInstance();
    assertSame(repo1, repo2);
  }

}
