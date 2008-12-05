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
import java.text.SimpleDateFormat
import utils.TestingConstants;


Runtime.getRuntime().gc();
Runtime.getRuntime().gc();


def total = Runtime.getRuntime().totalMemory() / Math.pow(2,20);
def free = Runtime.getRuntime().freeMemory() / Math.pow(2,20);
SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
def currentTime=sdf.format(Calendar.getInstance().getTime());

    

def used = total - free;

def file = new File("${TestingConstants.MEMORY_TEST_RESULTS_ROOT_DIR}/memory.txt");
def line = "Time: "+currentTime +"\tTotal: " +total + "\tFree: " + free + "\tUsed: " + used + "\n";
file.append(line);
return line;