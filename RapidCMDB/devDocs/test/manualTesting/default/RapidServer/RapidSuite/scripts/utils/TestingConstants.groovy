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
package utils

import java.text.SimpleDateFormat;
class TestingConstants
{
    public static java.text.SimpleDateFormat format = new SimpleDateFormat("yyyy_MM_dd-HH_mm_ss.SSS");
    public static String MEMORY_TEST_RESULTS_ROOT_DIR = "memoryTestResults";
    public static String MEMORY_DUMP_DIR = "${MEMORY_TEST_RESULTS_ROOT_DIR}/dumps";
    public static String MEMORY_HISTOGRAMS_DIR = "${MEMORY_TEST_RESULTS_ROOT_DIR}/histograms";

    public static File getHeapDumpFile()
    {
        return new File("$MEMORY_DUMP_DIR/heap${format.format (new Date())}.bin")
    }
    public static File getHeapHistogramFile()
    {
        return new File("$MEMORY_HISTOGRAMS_DIR/dump${format.format (new Date())}.txt")
    }
}