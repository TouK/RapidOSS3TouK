package com.ifountain.snmp.datasource;

import org.snmp4j.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.GenericAddress;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.util.*;

/* All content copyright (C) 2004-2008 iFountain, LLC., except as may otherwise be
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
/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Apr 11, 2008
 * Time: 9:58:10 AM
 */
public class SnmpListeningAdapter implements CommandResponder {
    private Snmp snmp;
    private Address targetAddress;
    private String host;
    private int port;
    private boolean isOpen = false;
    private List trapProcessors = Collections.synchronizedList(new ArrayList());
    private List trapBuffer = Collections.synchronizedList(new ArrayList());
    private Logger logger;
    private Object trapWaitingLock = new Object();
    private TrapProcessThread trapProcessorThread;


    public SnmpListeningAdapter(String host, int port, Logger logger) {
        this.host = host;
        this.port = port;
        this.logger = logger;
    }

    public void open() throws Exception {
        if (!isOpen) {
            targetAddress = GenericAddress.parse("udp:" + host + "/" + port);
            if (targetAddress == null || !targetAddress.isValid()) {
                throw new Exception("Invalid address " + host + "/" + port);
            }
            TransportMapping transport = new DefaultUdpTransportMapping();
            snmp = new Snmp(transport);
            snmp.listen();
            trapProcessorThread = new TrapProcessThread();
            trapProcessorThread.start();
            snmp.addNotificationListener(targetAddress, this);
            isOpen = true;
        }
    }

    public void close() {
        if (snmp != null) {
            try {
                snmp.removeNotificationListener(targetAddress);
                snmp.close();
            } catch (IOException e) {
            }
        }
        if(trapProcessorThread != null)
        {
        	if(trapProcessorThread.isAlive())
            {
        		trapProcessorThread.interrupt();
        		logger.debug(getLogPrefix() + "Interrupted trap processor thread. Waiting for trap processor thread to die.");
        		try {
        			trapProcessorThread.join();
        		} catch (InterruptedException e) {
        		}
        		logger.debug(getLogPrefix() + "Trap processor thread died.");
            }
        	else
        	{
        		logger.debug(getLogPrefix() + "Trap processor is not alive. No need to interrupt.");
        	}
        }
        isOpen = false;
    }

    public void processPdu(CommandResponderEvent event) {
        Map trap = new HashMap();
        PDU pdu = event.getPDU();
        trap.put("pdu", pdu);
        addTrapToBuffer(trap);
    }

    public boolean isOpen() {
        return isOpen;
    }

    public void addTrapProcessor(SnmpTrapProcessor processor) {
        trapProcessors.add(processor);
    }

    public void removeTrapProcessor(SnmpTrapProcessor processor) {
        trapProcessors.remove(processor);
    }

    public void removeAllTrapProcessors() {
        trapProcessors.clear();
    }

    public String getLogPrefix() {
        return "[SnmpListeningAdapter " + host + ":" + port + "]: ";
    }

    private void addTrapToBuffer(Map trap) {
        synchronized (trapWaitingLock) {
            trapBuffer.add(trap);
            trapWaitingLock.notifyAll();
        }
    }

    public void clearBuffer(){
         synchronized (trapWaitingLock) {
            trapBuffer.clear();
        }
    }

    class TrapProcessThread extends Thread {
        public void run() {
            try {
                while (true) {
                    Map trap = null;
                    synchronized (trapWaitingLock) {
                        if (trapBuffer.isEmpty()) {
                            logger.debug(getLogPrefix() + "Queue is empty, waiting...");
                            trapWaitingLock.wait();
                        }
                        trap = (Map) trapBuffer.remove(0);
                    }
                    for (Iterator iterator = trapProcessors.iterator(); iterator.hasNext();) {
                        SnmpTrapProcessor snmpTrapProcessor = (SnmpTrapProcessor) iterator.next();
                        snmpTrapProcessor.processTrap(trap);
                    }
                }
            }
            catch (InterruptedException e) {
                logger.info(getLogPrefix() + "Trap processor thread stopped.");
            }

        }
    }
}
