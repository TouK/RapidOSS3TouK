package com.ifountain.snmp.datasource;

import com.ifountain.snmp.util.RSnmpConstants;
import com.ifountain.snmp.connection.SnmpConnectionImpl;
import com.ifountain.core.datasource.BaseListeningAdapter;
import org.apache.log4j.Logger;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.snmp4j.*;
import org.snmp4j.security.*;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.util.ThreadPool;
import org.snmp4j.util.MultiThreadedMessageDispatcher;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;

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
public class SnmpListeningAdapter extends BaseListeningAdapter implements CommandResponder {
    private Snmp snmp;
    private TransportMapping transport;
    private Address targetAddress;
    private List trapBuffer = Collections.synchronizedList(new ArrayList());
    private Object trapWaitingLock = new Object();
    protected TrapProcessThread trapProcessorThread;
    private int numDispatcherThreads = 2;
    protected boolean _running=true;


    public SnmpListeningAdapter(String connectionName, Logger logger) {
        super(connectionName, 0, logger);
    }

    public void _subscribe() throws Exception {
        _running=true;
        String host = ((SnmpConnectionImpl) getConnection()).getHost();
        Long port = ((SnmpConnectionImpl) getConnection()).getPort();
        logger.debug(getLogPrefix() + "Starting..");
        logger.debug(getLogPrefix() + "parsing address udp:" + host + "/" + port);
        targetAddress = GenericAddress.parse("udp:" + host + "/" + port);
        if (targetAddress == null || !targetAddress.isValid()) {
            throw new Exception("Invalid address " + host + "/" + port);
        }
        transport = new DefaultUdpTransportMapping((UdpAddress) targetAddress);
        ThreadPool threadPool = ThreadPool.create("DispatcherPool", numDispatcherThreads);
        OctetString localEngineID = new OctetString(MPv3.createLocalEngineID());
        MessageDispatcher mtDispatcher = new MultiThreadedMessageDispatcher(threadPool, new MessageDispatcherImpl());
        mtDispatcher.addMessageProcessingModel(new MPv1());
        mtDispatcher.addMessageProcessingModel(new MPv2c());
        mtDispatcher.addMessageProcessingModel(new MPv3(localEngineID.getValue()));
        SecurityProtocols.getInstance().addDefaultProtocols();
        SecurityProtocols.getInstance().addPrivacyProtocol(new Priv3DES());
        logger.debug(getLogPrefix() + "Starting snmp session");
        snmp = new Snmp(mtDispatcher, transport);
        logger.info(getLogPrefix() + "Successfully started snmp session");
        USM usm = new USM(SecurityProtocols.getInstance(), localEngineID, 0);
        SecurityModels.getInstance().addSecurityModel(usm);
        snmp.getUSM().addUser(new OctetString(), new UsmUser(new OctetString(),
                null,
                null,
                null,
                null));
        snmp.addCommandResponder(this);
        logger.debug(getLogPrefix() + "Starting trap processor thread");
        trapProcessorThread = new TrapProcessThread();
        trapProcessorThread.start();
        transport.listen();
        logger.info(getLogPrefix() + "Snmp session is listening");

        logger.info(getLogPrefix() + "Started.");

    }

    public void _unsubscribe() {
        logger.debug(getLogPrefix() + "Closing snmp session");
        _running=false;
        if (snmp != null) {
            try {
                snmp.removeCommandResponder(this);
                logger.debug(getLogPrefix() + "Closing snmp session.");
                snmp.close();                
                logger.info(getLogPrefix() + "Snmp session successfully closed.");
            } catch (IOException e) {
                logger.warn(getLogPrefix() + "Error occured during snmp session _unsubscribe. Exception: " + e.getMessage());
            }
        }
        if (trapProcessorThread != null) {
            if (trapProcessorThread.isAlive()) {
                trapProcessorThread.interrupt();
                logger.debug(getLogPrefix() + "Interrupted trap processor thread. Waiting for trap processor thread to die.");
                try {
                    trapProcessorThread.join();
                }
                catch (InterruptedException e) {
                    logger.warn(getLogPrefix() + "InterruptedException occured during trapProcessorThread.join .");
                }
                logger.debug(getLogPrefix() + "Trap processor thread died.");
            } else {
                logger.debug(getLogPrefix() + "Trap processor is not alive. No need to interrupt.");
            }
        }
        logger.info(getLogPrefix() + "Closed.");
    }

    public void processPdu(CommandResponderEvent event) {

        PDU pdu = event.getPDU();
        logger.debug(getLogPrefix() + "Pdu " + pdu + " received..");
        if (pdu.getType() == PDU.TRAP || pdu.getType() == PDU.V1TRAP) {

            Trap trap = null;
            if (pdu instanceof PDUv1) {
                logger.debug(getLogPrefix() + "Pdu is version1 trap");
                PDUv1 version1Pdu = (PDUv1) pdu;
                trap = new Trap(version1Pdu);
            } else {
                logger.debug(getLogPrefix() + "Pdu is version2 trap");
                try {
                    logger.debug(getLogPrefix() + "Validating version2 trap");
                    trap = new Trap(pdu, ((IpAddress) event.getPeerAddress()).getInetAddress());
                    logger.info(getLogPrefix() + "Version2 trap successfully constructed.");
                } catch (Exception e) {
                    logger.warn(getLogPrefix() + "Could not process trap " + pdu + ". Reason: " + e.getMessage());
                }
            }
            if (trap != null) {
                logger.debug(getLogPrefix() + "Adding trap to buffer.");
                addTrapToBuffer(trap.toMap());
            }
        } else {
            logger.info(getLogPrefix() + "Ignored non trap pdu: " + pdu);
        }
    }

    public String getLogPrefix() {
        return "[SnmpListeningAdapter]: ";
    }

    protected void addTrapToBuffer(Map trap) {
        synchronized (trapWaitingLock) {
            trapBuffer.add(trap);
            trapWaitingLock.notifyAll();
        }
    }

    public void clearBuffer() {
        synchronized (trapWaitingLock) {
            trapBuffer.clear();
        }
    }

    public Object _update(Observable o, Object arg) {
        return arg;
    }


    class TrapProcessThread extends Thread {
        public void run() {
            try {
                while (_running) {
                    Map trap = null;
                    synchronized (trapWaitingLock) {
                        if (trapBuffer.isEmpty()) {
                            logger.debug(getLogPrefix() + "Queue is empty, waiting...");
                            trapWaitingLock.wait();
                        }
                        trap = (Map) trapBuffer.remove(0);
                        logger.debug(getLogPrefix() + "Processing trap from " + trap.get(RSnmpConstants.AGENT));
                    }
                    update(null,trap);
                }
            }
            catch (InterruptedException e) {
                logger.info(getLogPrefix() + "Trap processor thread stopped.");
            }

        }
    }
}
