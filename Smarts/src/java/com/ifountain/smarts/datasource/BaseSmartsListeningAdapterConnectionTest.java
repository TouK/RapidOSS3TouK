package com.ifountain.smarts.datasource;

/**
 * Created by IntelliJ IDEA.
 * User: iFountain
 * Date: Nov 18, 2008
 * Time: 5:03:19 PM
 * To change this template use File | Settings | File Templates.
 */


import java.util.*;

import com.ifountain.comp.test.util.threads.TestAction;
import com.ifountain.comp.test.util.threads.TestActionExecutorThread;
import com.ifountain.comp.test.util.logging.TestLogUtils;
import com.ifountain.core.connection.ConnectionManager;
import com.ifountain.core.connection.ConnectionParam;
import com.ifountain.core.connection.IConnection;
import com.ifountain.core.connection.exception.ConnectionException;
import com.ifountain.core.connection.mocks.MockConnectionImpl;
import com.ifountain.core.connection.mocks.NotConnectedConnection;
import com.ifountain.core.datasource.mocks.MockBaseListeningAdapter;
import com.ifountain.core.datasource.mocks.MockConnectionParameterSupplierImpl;
import com.ifountain.core.test.util.RapidCoreTestCase;
import com.ifountain.smarts.connection.SmartsConnectionImpl;
import com.ifountain.smarts.util.params.SmartsSubscribeParameters;
import com.ifountain.smarts.util.DataFromObservable;
import com.ifountain.smarts.test.util.SmartsTestUtils;
import com.ifountain.smarts.test.util.SmartsTestConstants;
import com.smarts.repos.MR_PropertyNameValue;
import com.smarts.repos.MR_PropertyChoice;
import com.smarts.repos.MR_Choice;
import com.smarts.remote.SmObserverEvent;
import com.smarts.remote.SmRemoteDomainManager;
import org.apache.log4j.Logger;
import com.ifountain.smarts.connection.mocks.SmartsConnectionImplMock;

public class BaseSmartsListeningAdapterConnectionTest extends RapidCoreTestCase{

    private MockConnectionParameterSupplierImpl connectionParameterSupplier;
    private ConnectionParam param;
    private String connectionName;
    private BaseSmartsListeningAdapterImpl smartsAdapter;
    LinkedList receivedObjects;
    int numberOfDiscconnectionMessages = 0;


    @Override
    protected void setUp() throws Exception {
        super.setUp();
        connectionParameterSupplier = new MockConnectionParameterSupplierImpl();
        ConnectionManager.setParamSupplier(connectionParameterSupplier);
        connectionName = "ds1";
    }
    public void testDisconnectedByCheckerWhenConnectionIsDisconnected() throws Exception
    {
        createConnectionParam(connectionName);
        smartsAdapter = new BaseSmartsListeningAdapterImpl(connectionName,0, TestLogUtils.log, null);
        smartsAdapter.subscribe();

        SmartsConnectionImplMock connection=(SmartsConnectionImplMock)smartsAdapter.getConnection();
        assertTrue(connection.isConnectedOnce());
        SmartsConnectionImplMock.isConnected=false;
        Thread.sleep(5000);
        assertFalse(connection.isConnectedOnce());



    }
    private void createConnectionParam(String connName)
    {
        param = SmartsTestUtils.getConnectionParam(SmartsTestConstants.SMARTS_SAM_CONNECTION_TYPE);
        param.setConnectionClass(SmartsConnectionImplMock.class.getName());
        param.setConnectionName(connName);
        connectionParameterSupplier.setParam(param);
    }

   

    class BaseSmartsListeningAdapterImpl extends BaseSmartsListeningAdapter {
        public BaseSmartsListeningAdapterImpl(String connectionName, long reconnectInterval, Logger logger, SmartsSubscribeParameters[] subscribeParams) {
            super(connectionName, reconnectInterval, logger, subscribeParams);

        }

        @Override
        public Object processIncomingData(DataFromObservable data) {
            MR_PropertyNameValue[] nameValuePairs = convertIncomingDataToMR_PropertyNameValue(data);
            switch (data.getEventType()) {
                case SmObserverEvent.INSTANCE_CREATE: {
                    receivedObjects.add(createObject(CREATE, nameValuePairs));
                    break;
                }
                case SmObserverEvent.INSTANCE_DELETE: {
                    receivedObjects.add(createObject(DELETE, nameValuePairs));
                    break;
                }
                case SmObserverEvent.ATTRIBUTE_CHANGE: {
                    receivedObjects.add(createObject(CHANGE, nameValuePairs));
                    break;
                }
                default:
                    break;
            }
            return null;
        }

        protected void getExistingObjects(SmartsSubscribeParameters[] parameters) throws Exception {

        }

        @Override
        protected void subscribeTo() throws Exception {
            SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
            domainManager.topologySubscribe();
            if (subscribeParams != null) {
                SmartsSubscribeParameters param = subscribeParams[0];
                MR_PropertyChoice choice = new MR_PropertyChoice(param.getClassName(),
                        param.getInstanceName(), param.getParameter(0), MR_PropertyChoice.STICKY | MR_PropertyChoice.EXPAND_SUBCLASSES);
                domainManager.propertySubscribeAll(choice, 1);
            }
        }

        @Override
        protected void unsubscribeFrom() throws Exception {
            SmRemoteDomainManager domainManager = ((SmartsConnectionImpl) getConnection()).getDomainManager();
            domainManager.propertyUnsubscribeAll(new MR_PropertyChoice(".*", ".*", ".*", MR_Choice.EXPAND_SUBCLASSES));
            domainManager.topologyUnsubscribe();
        }

        @Override
        protected void disconnectDetected() throws Exception {
            numberOfDiscconnectionMessages++;
        }

        public void setExitingObjectsRetrieved(boolean existingObjectsRetrieved) {
            this.existingObjectsRetrieved = existingObjectsRetrieved;
        }

        public void setSubscribed(boolean isSubscribed){
            this.isSubscribed = isSubscribed;
        }
        public void setObserverCreated(boolean isObserverCreated){
            this.isObserverCreated = isObserverCreated;
        }
        protected IConnection getConnection() {
            return connection;
        }
    }

}
