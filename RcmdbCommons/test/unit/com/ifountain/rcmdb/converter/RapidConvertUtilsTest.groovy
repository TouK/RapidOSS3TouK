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