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
import com.sun.tools.attach.VirtualMachine
import com.sun.tools.attach.VirtualMachineDescriptor
import javax.management.remote.JMXConnectorFactory
import javax.management.remote.JMXConnector
import javax.management.remote.JMXServiceURL
import javax.management.MBeanServerConnection
import java.lang.management.RuntimeMXBean
import java.lang.management.ManagementFactory
import java.lang.management.ThreadMXBean

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Jun 26, 2008
 * Time: 5:03:26 PM
 * To change this template use File | Settings | File Templates.
 */
VirtualMachine.list().each{VirtualMachineDescriptor desc->
    VirtualMachine vm = VirtualMachine.attach(desc.id());

    def add = VirtualMachine.attach(desc.id()).getAgentProperties().get("com.sun.management.jmxremote.localConnectorAddress")
    /*String agent = vm.getSystemProperties().getProperty("java.home") +
                        File.separator + "lib" + File.separator + "management-agent.jar";
                vm.loadAgent(agent);
    connectorAddress =
                        vm.getAgentProperties().getProperty("com.sun.management.jmxremote.localConnectorAddress");*/
    JMXConnector connector = JMXConnectorFactory.connect(new JMXServiceURL(add))

    MBeanServerConnection remote =
                connector.getMBeanServerConnection();
    RuntimeMXBean remoteRuntime =
                ManagementFactory.newPlatformMXBeanProxy(
                    remote,
                    ManagementFactory.RUNTIME_MXBEAN_NAME,
                    RuntimeMXBean.class);
    System.out.println("Target VM is: "+remoteRuntime.getName());
        System.out.println("Started since: "+remoteRuntime.getUptime());
        System.out.println("With Classpath: "+remoteRuntime.getClassPath());
        System.out.println("And args: "+remoteRuntime.getInputArguments());
    println add;

}
