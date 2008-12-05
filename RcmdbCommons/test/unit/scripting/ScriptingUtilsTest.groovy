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
package scripting

import com.ifountain.rcmdb.test.util.RapidCmdbTestCase
import org.apache.commons.io.FileUtils
import com.ifountain.rcmdb.scripting.ScriptingUtils

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Nov 6, 2008
 * Time: 5:36:22 PM
 * To change this template use File | Settings | File Templates.
 */
class ScriptingUtilsTest extends RapidCmdbTestCase
{
    String baseDir = "../testOutput";
    protected void setUp() {
        super.setUp();    //To change body of overridden methods use File | Settings | File Templates.
        File baseDirFile = new File(baseDir);
        FileUtils.deleteDirectory (baseDirFile);
        baseDirFile.mkdirs()
    }

    protected void tearDown() {
        super.tearDown();    //To change body of overridden methods use File | Settings | File Templates.
    }

    public void testGetStartupScripts()
    {
        File startupScriptFile = new File(baseDir+"/"+ScriptingUtils.STARTUP_SCRIPT_DIR+"/Group1StartupScripts.groovy");
        startupScriptFile.parentFile.mkdirs();
        startupScriptFile.setText ("""
        class Group1StartupScripts{
            static def scripts = ["script1.groovy", "script2.groovy"]
        }
        """)
        startupScriptFile = new File(baseDir+"/"+ScriptingUtils.STARTUP_SCRIPT_DIR+"/Exceptional1StartupScripts.groovy");
        startupScriptFile.setText ("""
        class Exceptional1StartupScripts
        """)
        startupScriptFile = new File(baseDir+"/"+ScriptingUtils.STARTUP_SCRIPT_DIR+"/Group2StartupScripts.groovy");
        startupScriptFile.setText ("""
        class Group2StartupScripts{
            static def scripts = ["script3.groovy", "script4.groovy"]
        }
        """)
        startupScriptFile = new File(baseDir+"/"+ScriptingUtils.STARTUP_SCRIPT_DIR+"/Exceptional2StartupScripts.groovy");
        startupScriptFile.setText ("""
        class Exceptional2StartupScripts{                                                        
        }
        """)


        GroovyClassLoader classLoader = new GroovyClassLoader();
        classLoader.addClasspath (startupScriptFile.getParentFile().getAbsolutePath());
        def startupScriptFileList = ScriptingUtils.getStartupScriptList(baseDir, classLoader)
        assertEquals (4, startupScriptFileList.size());
        assertTrue (startupScriptFileList.contains("script1.groovy"));
        assertTrue (startupScriptFileList.contains("script2.groovy"));
        assertTrue (startupScriptFileList.contains("script3.groovy"));
        assertTrue (startupScriptFileList.contains("script4.groovy"));
    }

    public void testGetStartupScriptsWithNoStartupScriptFileConfigDirectory()
    {
        GroovyClassLoader classLoader = new GroovyClassLoader();
        classLoader.addClasspath (baseDir);
        List startupScriptFileList = ScriptingUtils.getStartupScriptList(baseDir, classLoader)
        assertTrue (startupScriptFileList.isEmpty());
    }
    public void testGetStartupScriptsWithNoStartupScriptFile()
    {
        new File(baseDir+"/"+ScriptingUtils.STARTUP_SCRIPT_DIR).mkdirs();
        GroovyClassLoader classLoader = new GroovyClassLoader();
        classLoader.addClasspath (baseDir);
        List startupScriptFileList = ScriptingUtils.getStartupScriptList(baseDir, classLoader)
        assertTrue (startupScriptFileList.isEmpty());
    }

    public void testGetStartupScriptsWithWrongFileExtension()
    {
        File startupScriptFile = new File(baseDir+"/"+ScriptingUtils.STARTUP_SCRIPT_DIR+"/Group1StartupScripts.groovy");
        startupScriptFile.parentFile.mkdirs();
        startupScriptFile.setText ("""
        class Group1StartupScripts{
            static def scripts = ["script1.groovy", "script2.groovy"]
        }
        """)
        startupScriptFile = new File(baseDir+"/"+ScriptingUtils.STARTUP_SCRIPT_DIR+"/AnotherClass.groovy");
        startupScriptFile.setText ("""
        class AnotherClass{
            static def scripts = ["script3.groovy", "script4.groovy"]
        }
        """)
        GroovyClassLoader classLoader = new GroovyClassLoader();
        classLoader.addClasspath (startupScriptFile.getParentFile().getAbsolutePath());
        List startupScriptFileList = ScriptingUtils.getStartupScriptList(baseDir, classLoader)
        assertEquals (2, startupScriptFileList.size());
        assertTrue (startupScriptFileList.contains("script1.groovy"));
        assertTrue (startupScriptFileList.contains("script2.groovy"));
    }

}