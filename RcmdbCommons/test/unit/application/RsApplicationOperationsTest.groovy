package application

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 5, 2008
 * Time: 9:24:49 AM
 * To change this template use File | Settings | File Templates.
 */

public class RsApplicationOperationsTest extends RapidCmdbTestCase
{
  public void testGetApplicationInfo()
  {
    String baseDir = "../testoutput";
    def licenceFile = new File("${baseDir}/../${RsApplicationOperations.ENTERPRISE_LICENCE_FILE}");
    licenceFile.delete();
    def baseDirFile = new File(baseDir);
    if(baseDirFile.exists())
    {
      org.apache.commons.io.FileUtils.deleteDirectory(baseDirFile);
    }
    baseDirFile.mkdirs();
    System.setProperty("base.dir", baseDir);
    try
    {
      File versionFile1 = new File("${baseDir}/RIVersion.txt");
      File versionFile2 = new File("${baseDir}/RCMDBVersion.txt");
      File invalidContentVersionFile = new File("${baseDir}/InvalidContentVersion.txt");
      File invalidVersionFile = new File("${baseDir}/Invalidversion.txt");
      File anotherFile = new File("${baseDir}/anotherfile.txt");
      versionFile1.append("Version: 3.0\n");
      versionFile1.append("Build: 3.0.1\n");

      versionFile2.append("Version: 3.0\n");
      versionFile2.append("Build: 3.01\n");

      invalidContentVersionFile.setText("invaliddata")
      invalidVersionFile.setText("invaliddata")
      anotherFile.setText("invaliddata")


      Map appInfo = RsApplicationOperations.applicationInfo();
      assertEquals(2, appInfo.size())
      assertEquals(3, appInfo.RI.size())
      assertEquals("3.0", appInfo.RI.veRsion);
      assertEquals("3.0.1", appInfo.RI.Build);
      assertEquals(RsApplicationOperations.COMMUNITY_PRODUCT, appInfo.RI[RsApplicationOperations.PRODUCT_TYPE]);

      assertEquals(2, appInfo.RCMDB.size())
      assertEquals("3.0", appInfo.RCMDB.veRsion);
      assertEquals("3.01", appInfo.RCMDB.buiLD);


      licenceFile.setText("licence")

      appInfo = RsApplicationOperations.applicationInfo();
      assertEquals(RsApplicationOperations.ENTERPRISE_PRODUCT, appInfo.RI[RsApplicationOperations.PRODUCT_TYPE]);
    }
    finally{
      licenceFile.delete();
    }
  }
}