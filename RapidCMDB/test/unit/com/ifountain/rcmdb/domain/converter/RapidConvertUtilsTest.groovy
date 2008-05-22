package com.ifountain.rcmdb.domain.converter

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.beanutils.converters.LongConverter

/**
* Created by IntelliJ IDEA.
* User: Administrator
* Date: May 22, 2008
* Time: 3:13:30 PM
* To change this template use File | Settings | File Templates.
*/
class RapidConvertUtilsTest extends RapidCmdbTestCase{
      public void testGetInstance()
      {
          assertSame (RapidConvertUtils.getInstance(), RapidConvertUtils.getInstance());
          assertNotNull (RapidConvertUtils.getInstance().lookup(String));
      }

      public void testKeepInstancesCreatedInOtherThreads()
      {
          RapidConvertUtilsAdderThread t = new RapidConvertUtilsAdderThread(); 
          t.start();
          t.join();
          assertTrue (RapidConvertUtils.getInstance().lookup(Long) instanceof LongConverter);
      }
}

class RapidConvertUtilsAdderThread extends Thread
{
      public void run()
      {
        RapidConvertUtils.getInstance().register (new LongConverter(), Long.class);
      }
}