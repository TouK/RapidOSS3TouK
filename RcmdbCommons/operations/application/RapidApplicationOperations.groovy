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
package application

import com.ifountain.comp.utils.CaseInsensitiveMap
import com.ifountain.compass.index.WrapperIndexDeletionPolicy
import com.ifountain.rcmdb.domain.BackupAction
import com.ifountain.rcmdb.domain.FullExportImportUtility
import com.ifountain.rcmdb.domain.statistics.OperationStatistics
import java.text.SimpleDateFormat
import org.apache.commons.io.FileUtils
import org.apache.commons.io.filefilter.FalseFileFilter
import org.apache.commons.io.filefilter.SuffixFileFilter
import org.apache.commons.lang.StringUtils
import org.codehaus.groovy.grails.plugins.PluginManagerHolder
import org.codehaus.groovy.grails.web.context.ServletContextHolder
import org.codehaus.groovy.grails.web.pages.GroovyPagesTemplateEngine
import org.codehaus.groovy.grails.web.servlet.GrailsApplicationAttributes
import org.compass.core.spi.InternalCompass
import org.codehaus.groovy.grails.commons.ApplicationHolder
import com.ifountain.rcmdb.transaction.RapidCmdbTransactionManager


/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 13, 2008
 * Time: 4:10:27 PM
 * To change this template use File | Settings | File Templates.
 */
class RapidApplicationOperations extends com.ifountain.rcmdb.domain.operation.AbstractDomainOperation{
    public static final String VERSION_FILE_SUFFIX = "Version.txt";
    public static final String ENTERPRISE_LICENCE_FILE = "IFountain End User License Agreement.pdf";
    public static final String COMMUNITY_PRODUCT = "community";
    public static final String ENTERPRISE_PRODUCT = "enterprise";
    public static final String PRODUCT_TYPE = "productType";
    public static String getCompassStatistics()
    {
        return OperationStatistics.getInstance().getGlobalStatistics();
    }

    public static void resetCompassStatistics()
    {
        OperationStatistics.getInstance().reset();
    }
    public static executeBatch(Closure closure)
    {
        RapidCmdbTransactionManager.executeWithGlobalTransaction(closure);        
    }
    public static Map applicationInfo()
    {
      def appInfo = new CaseInsensitiveMap()
      def files = FileUtils.listFiles(new File(System.getProperty("base.dir")), new SuffixFileFilter(VERSION_FILE_SUFFIX), new FalseFileFilter());
      files.each{File versionFile->
        String productName = StringUtils.substringBefore(versionFile.getName(), VERSION_FILE_SUFFIX);
        List lines = versionFile.readLines();
        def productInfo = new CaseInsensitiveMap()
        boolean isValid = true;
        lines.each{String line->
          def parts = line.split(":", -1);
          if(parts.length == 2)
          {
            String infoType = parts[0].trim().toLowerCase()
            String infoValue = parts[1].trim()
            productInfo[infoType] = infoValue;
          }
          else
          {
            isValid = false;
            return;
          }
        }
        if(isValid)
        {
          appInfo[productName] = productInfo;
        }
      }
      if(appInfo.RI != null)
      {
        def lincenceFile = new File("${System.getProperty("base.dir")}/../${ENTERPRISE_LICENCE_FILE}")
        if(lincenceFile.exists())
        {
          appInfo.RI[PRODUCT_TYPE] = ENTERPRISE_PRODUCT;
        }
        else
        {
          appInfo.RI[PRODUCT_TYPE] = COMMUNITY_PRODUCT;
        }
      }
      return appInfo;
    }

    public static void backup()
    {
        SimpleDateFormat df = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
        String directory = "backup/${df.format(new Date())}".toString();
        backup (directory);
    }

    public static void backup(String directory)
    {
        InternalCompass c = ServletContextHolder.getServletContext().getAttribute(GrailsApplicationAttributes.APPLICATION_CONTEXT).getBean("compass");
        List domainClasses = ApplicationHolder.application.getDomainClasses().clazz;
        BackupAction action = new BackupAction(c, domainClasses, directory, getLogger());
        WrapperIndexDeletionPolicy.takeGlobalSnapshot(action); 
    }

    def static reloadControllers(){
        PluginManagerHolder.getPluginManager().getGrailsPlugin("controllers").checkForChanges()
    }

    def static reloadFilters(){
        PluginManagerHolder.getPluginManager().getGrailsPlugin("filters").checkForChanges()
    }

    def static reloadViewsAndControllers(){
        reloadControllers();
        reloadViews()
    }

    def static reloadViews(){
        GroovyPagesTemplateEngine.pageCache.clear();
    }

    public static def getUtility(utilityName)
    {
        return RapidApplicationOperations.class.classLoader.loadClass (utilityName).newInstance();
    }

    public static def fullExport(CONFIG)
    {
        def fullExportUtility=new FullExportImportUtility(getLogger());
        fullExportUtility.fullExport(CONFIG);
    }
    public static def fullImport(CONFIG)
    {
        def fullExportUtility=new FullExportImportUtility(getLogger());
        fullExportUtility.fullImport(CONFIG);
    }
    public static def getModelClass(String modelName)
    {
        def modelClass=ApplicationHolder.application.getDomainClass(modelName);
        if(modelClass)
        {
            return modelClass.clazz;
        }
        else
        {
            throw new Exception("Model Class ${modelName} does not exist");
        }
    }
}