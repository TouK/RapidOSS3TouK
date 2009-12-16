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
SimpleDateFormat sdf = new SimpleDateFormat("yyyy_MM_dd_HH_mm_ss");
def currentTime=sdf.format(Calendar.getInstance().getTime());


def pid=java.lang.management.ManagementFactory.getRuntimeMXBean().getName().split("@")[0];
def javaDir = System.getenv("JAVA_HOME")+"/bin/";
def cmd = "${javaDir}jstack -l ${pid}"


def process = "${cmd}".execute()
def output = process.in.text

def folder=new File("web-app/memoryResults/jvmStacks");
if(!folder.exists())
{
	folder.mkdirs();
}

def file=new File("${folder.path}/${currentTime}.txt");
file.setText(output);


return "Stack file created at ${file.getAbsolutePath()}"