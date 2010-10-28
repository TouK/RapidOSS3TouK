package com.ifountain.rcmdb.tcp.datasource

import com.ifountain.core.test.util.RapidCoreTestCase
import com.ifountain.core.connection.ConnectionParam
import com.ifountain.rcmdb.tcp.connection.TcpListeningConnectionImpl
import com.ifountain.comp.test.util.logging.TestLogUtils
import com.ifountain.core.test.util.DatasourceTestUtils
import org.jboss.netty.channel.ChannelException
import com.ifountain.rcmdb.test.util.ClosureWaitAction
import com.ifountain.comp.test.util.CommonTestUtils

/**
* Created by Sezgin Kucukkaraaslan
* Date: Oct 28, 2010
* Time: 1:34:02 PM
*/
class TcpListeningAdapterTest extends RapidCoreTestCase {
    private static final String TCP_TEST_CONNECTION_NAME = "tcpConn";
    TcpListeningAdapter adapter;
    private BufferedOutputStream os;
    private BufferedReader br;
    private Socket socket;
    protected void setUp() {
        super.setUp();
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam("127.0.0.1", 9999L));
        adapter = new TcpListeningAdapter(TCP_TEST_CONNECTION_NAME, TestLogUtils.log)
    }

    protected void tearDown() {
        closeIO();
        if (adapter != null) {
            adapter.unsubscribe();
        }
        super.tearDown();
    }

    public void testOpenThrowsExceptionIfHostNameIsInvalid() throws Exception {
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam("invalidHost", 9999L));
        try {
            adapter.subscribe();
        }
        catch (ChannelException e) {
            assertEquals("Failed to bind to: invalidHost:9999", e.getMessage());
        }
    }

    public void testOpenThrowsExceptionIfPortIsInvalid() throws Exception {
        DatasourceTestUtils.getParamSupplier().setParam(getConnectionParam("127.0.0.1", -1L));
        try {
            adapter.subscribe();
        }
        catch (IllegalArgumentException e) {
            assertEquals("port out of range:-1", e.getMessage());
        }
    }

    public void testSuccessfulOpen() throws Exception {
        adapter.subscribe();
        assertTrue(adapter.isSubscribed());
        assertTrue(adapter.entryProcessorThread.isAlive());
        assertTrue(adapter.getChannel().isOpen())
    }

    public void testUnsubscribe() throws Exception {
        adapter.subscribe();
        assertTrue(adapter.isSubscribed());
        assertTrue(adapter.entryProcessorThread.isAlive());
        assertTrue(adapter.getChannel().isOpen())
        adapter.unsubscribe();
        assertFalse(adapter.isSubscribed());
        assertFalse(adapter.entryProcessorThread.isAlive());
        assertFalse(adapter.getChannel().isOpen())
    }

    public void testCloseWithoutInterruptException() throws Exception {
        final MockTcpObserverImplWithoutInterruptException tcpProcessor1 = new MockTcpObserverImplWithoutInterruptException();

        adapter.addObserver(tcpProcessor1);
        adapter.subscribe();
        assertTrue(adapter.isSubscribed());
        assertTrue(adapter.entryProcessorThread.isAlive());

        adapter.addEntryToBuffer("entry1");
        adapter.unsubscribe();
        assertFalse(adapter.isSubscribed());
        assertFalse(adapter.entryProcessorThread.isAlive());

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, tcpProcessor1.entries.size());
            assertEquals("entry1", tcpProcessor1.entries[0][TcpListeningAdapter.ENTRY]);
        }));
        assertNotNull(tcpProcessor1.lastException);
        assertTrue(tcpProcessor1.lastException instanceof InterruptedException);

        //should do resubscribe
        final MockTcpObserverImpl tcpProcessor2 = new MockTcpObserverImpl();

        adapter.addObserver(tcpProcessor2);
        adapter.subscribe();
        assertTrue(adapter.isSubscribed());
        assertTrue(adapter.entryProcessorThread.isAlive());

        adapter.addEntryToBuffer("entry2");

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, tcpProcessor2.entries.size());
            assertEquals("entry2", tcpProcessor2.entries[0][TcpListeningAdapter.ENTRY]);
        }));

        adapter.unsubscribe();
        assertFalse(adapter.isSubscribed());
        assertFalse(adapter.entryProcessorThread.isAlive());
    }

    public void testAdapterSendsEntriesToAllSubscribers() throws Exception {
        final MockTcpObserverImpl tcpProcessor1 = new MockTcpObserverImpl();
        final MockTcpObserverImpl tcpProcessor2 = new MockTcpObserverImpl();
        final MockTcpObserverImpl tcpProcessor3 = new MockTcpObserverImpl();
        adapter.addObserver(tcpProcessor1);
        adapter.addObserver(tcpProcessor2);
        adapter.addObserver(tcpProcessor3);
        adapter.subscribe();
        adapter.addEntryToBuffer("myEntry");
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, tcpProcessor1.entries.size());
            assertEquals(1, tcpProcessor2.entries.size());
            assertEquals(1, tcpProcessor3.entries.size());
            assertEquals("myEntry", tcpProcessor1.entries[0][TcpListeningAdapter.ENTRY]);
            assertEquals("myEntry", tcpProcessor2.entries[0][TcpListeningAdapter.ENTRY]);
            assertEquals("myEntry", tcpProcessor3.entries[0][TcpListeningAdapter.ENTRY]);
        }));
    }

    public void testAdapterWaitsIfBufferIsEmpty() throws Exception {
        final MockTcpObserverImpl observer = new MockTcpObserverImpl();
        adapter.addObserver(observer);
        adapter.subscribe();
        adapter.addEntryToBuffer("entry1");
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(1, observer.entries.size());
        }));
        Thread.sleep(300);
        adapter.addEntryToBuffer("entry2");
        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, observer.entries.size());
        }));
    }

    public void testAdapterWithNewLineAsEndOfEntry() {
        final MockTcpObserverImpl observer = new MockTcpObserverImpl();
        adapter.addObserver(observer);
        adapter.subscribe();

        openConnectionToPort(9999);

        os.write("entry1\n".getBytes())
        os.flush();
        os.write("entry2\n".getBytes())
        os.flush();
        os.write("entry3".getBytes())
        os.flush();

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(2, observer.entries.size())
            assertEquals("entry1", observer.entries[0][TcpListeningAdapter.ENTRY])
            assertEquals("entry2", observer.entries[1][TcpListeningAdapter.ENTRY])
        }))

        Thread.sleep(500);
        assertEquals(2, observer.entries.size())
        os.write("lastPartOfEntry3\n".getBytes())
        os.flush();

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(3, observer.entries.size())
            assertEquals("entry3lastPartOfEntry3", observer.entries[2][TcpListeningAdapter.ENTRY])
        }))

    }

    public void testAdapterWithEndOfEntryConfigured() {
        String endOfEntry = "<END>"
        final MockTcpObserverImpl observer = new MockTcpObserverImpl();
        adapter.setEndOfEntry(endOfEntry)
        adapter.addObserver(observer);
        adapter.subscribe();

        openConnectionToPort(9999);

        os.write("entry1${endOfEntry}".getBytes())
        os.flush();
        os.write("entry2${endOfEntry}".getBytes())
        os.flush();
        os.write("entry3${endOfEntry}".getBytes())
        os.flush();

        CommonTestUtils.waitFor(new ClosureWaitAction({
            assertEquals(3, observer.entries.size())
            assertEquals("entry1", observer.entries[0][TcpListeningAdapter.ENTRY])
            assertEquals("entry2", observer.entries[1][TcpListeningAdapter.ENTRY])
            assertEquals("entry3", observer.entries[2][TcpListeningAdapter.ENTRY])
        }))

    }

    private void openConnectionToPort(int portNumber) throws Exception
    {
        SocketAddress local = new InetSocketAddress("localhost", portNumber);
        int connection_fail_count = 0;
        boolean repeat = true;
        while (repeat) {
            try
            {
                socket = new Socket();
                socket.setKeepAlive(true);
                socket.connect(local);
                socket.setKeepAlive(true);
                repeat = false;
            }
            catch (IOException e)
            {
                repeat = true;
                CommonTestUtils.wait(100);
                connection_fail_count++;
                if (connection_fail_count == 20)
                {
                    throw new Exception("Connection to adapter can not be established");
                }
            }
        }
        os = new BufferedOutputStream(socket.getOutputStream());
        br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
    }

    private void closeIO() {
        if (os != null)
            os.close();
        if (br != null)
            br.close();
        if (socket != null)
            socket.close();
    }

    public static ConnectionParam getConnectionParam(String host, Long port) {
        Map<String, Object> otherParams = new HashMap<String, Object>();
        otherParams.put(TcpListeningConnectionImpl.HOST, host);
        otherParams.put(TcpListeningConnectionImpl.PORT, port);
        return new ConnectionParam(TCP_TEST_CONNECTION_NAME, TcpListeningConnectionImpl.class.getName(), otherParams, 1, 1000, 6000);
    }
}

class MockTcpObserverImplWithoutInterruptException implements Observer {
    def entries = [];
    def lastException = null;

    public void update(Observable o, Object entry) {
        try {
            Thread.sleep(2000);
        }
        catch (InterruptedException e)
        {
            this.lastException = e;
        }
        entries.add(entry);
    }
}

class MockTcpObserverImpl implements Observer {
    def entries = [];
    public void update(Observable o, Object entry) {
        entries.add(entry);
    }
}
