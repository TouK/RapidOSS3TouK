package com.ifountain.rcmdb.tcp.datasource

import com.ifountain.core.datasource.BaseListeningAdapter
import com.ifountain.rcmdb.tcp.connection.TcpListeningConnectionImpl
import java.util.concurrent.Executors
import org.apache.log4j.Logger
import org.jboss.netty.bootstrap.ServerBootstrap
import org.jboss.netty.buffer.ChannelBuffer
import org.jboss.netty.buffer.ChannelBuffers
import org.jboss.netty.channel.*
import org.jboss.netty.channel.socket.nio.NioServerSocketChannelFactory
import org.jboss.netty.handler.codec.frame.DelimiterBasedFrameDecoder
import org.jboss.netty.handler.codec.frame.Delimiters
import org.jboss.netty.handler.codec.string.StringDecoder
import org.jboss.netty.handler.codec.string.StringEncoder

/**
* Created by Sezgin Kucukkaraaslan
* Date: Oct 27, 2010
* Time: 6:06:46 PM
*/
class TcpListeningAdapter extends BaseListeningAdapter {
    public static final String ENTRY = "entry";
    private String endOfEntry;
    private boolean isSecure = false;
    private int maxFrameLength = Math.pow(2, 15);
    protected boolean _running = false;
    protected Thread entryProcessorThread;
    private Channel channel;
    private ChannelFactory factory;


    private List entryBuffer = Collections.synchronizedList(new ArrayList());
    private Object entryWaitingLock = new Object();

    public TcpListeningAdapter(String connectionName, Logger logger) {
        super(connectionName, 0, logger);
        this.endOfEntry = endOfEntry;
        this.isSecure = isSecure;
    }


    public Object _update(Observable o, Object arg) {
        return arg;
    }

    protected void _subscribe() {
        _running = true;
        String host = ((TcpListeningConnectionImpl) getConnection()).getHost();
        Long port = ((TcpListeningConnectionImpl) getConnection()).getPort();
        entryProcessorThread = Thread.start({
            try {
                while (_running) {
                    Map entry = null;
                    synchronized (entryWaitingLock) {
                        if (entryBuffer.isEmpty()) {
                            logger.debug(getLogPrefix() + "Queue is empty, waiting...");
                            entryWaitingLock.wait();
                        }
                        entry = (Map) entryBuffer.remove(0);
                        logger.debug(getLogPrefix() + "Processing entry " + entry.get(ENTRY));
                    }
                    update(null, entry);
                }
            }
            catch (InterruptedException e) {
                logger.info(getLogPrefix() + "Entry processor thread stopped.");
            }
        })
        factory = new NioServerSocketChannelFactory(Executors.newCachedThreadPool(), Executors.newCachedThreadPool())
        ServerBootstrap bootstrap = new ServerBootstrap(factory);
        bootstrap.setPipelineFactory(new TcpListenerPipelineFactory(this))
        channel = bootstrap.bind(new InetSocketAddress(host, port.intValue()));
    }

    protected void _unsubscribe() {
        _running = false;
        if (channel != null) {
            long timeout = ((TcpListeningConnectionImpl) getConnection()).getTimeout()
            try {
                channel.close().awaitUninterruptibly(timeout);
            }
            catch (e) {
                logger.warn(getLogPrefix() + "Could not close channel.", e);
            }
        }
        if (factory != null) {
            factory.releaseExternalResources();
        }
        if (entryProcessorThread != null) {
            if (entryProcessorThread.isAlive()) {
                entryProcessorThread.interrupt();
                logger.debug(getLogPrefix() + "Interrupted entry processor thread. Waiting for trap processor entry to die.");
                try {
                    entryProcessorThread.join();
                }
                catch (InterruptedException e) {
                    logger.warn(getLogPrefix() + "InterruptedException occured during entryProcessorThread.join .");
                }
                logger.debug(getLogPrefix() + "Entry processor thread died.");
            } else {
                logger.debug(getLogPrefix() + "Entry processor is not alive. No need to interrupt.");
            }
        }
        logger.info(getLogPrefix() + "Closed.");
    }

    protected void addEntryToBuffer(String entry) {
        synchronized (entryWaitingLock) {
            def entryMap = [:]
            entryMap[ENTRY] = entry
            entryBuffer.add(entryMap);
            entryWaitingLock.notifyAll();
        }
    }

    public void clearBuffer() {
        synchronized (entryWaitingLock) {
            entryBuffer.clear();
        }
    }

    public int getMaxFrameLength() {
        return maxFrameLength;
    }

    public void setMaxFrameLength(int i) {
        maxFrameLength = i;
    }

    public String getEndOfEntry() {
        return endOfEntry;
    }

    public boolean isSecure() {
        return isSecure;
    }

    public void setEndOfEntry(String endOfEntry) {
        this.endOfEntry = endOfEntry;
    }

    public void setIsSecure(boolean isSecure) {
        this.isSecure = isSecure;
    }

    protected Channel getChannel() {
        return channel;
    }

    public String getLogPrefix() {
        return "[TcpListeningAdapter]: ";
    }

}

private class TcpListenerPipelineFactory implements ChannelPipelineFactory {
    private TcpListeningAdapter adapter;

    public TcpListenerPipelineFactory(TcpListeningAdapter adapter) {
        this.adapter = adapter;
    }

    public ChannelPipeline getPipeline() {
        ChannelPipeline pipeline = Channels.pipeline();
        String endOfEntry = adapter.getEndOfEntry();
        def delimiters;
        if (endOfEntry == null) {
            delimiters = Delimiters.lineDelimiter();
        }
        else {
            delimiters = [ChannelBuffers.wrappedBuffer(endOfEntry.getBytes())] as ChannelBuffer[];
        }
        pipeline.addLast("framer", new DelimiterBasedFrameDecoder(adapter.getMaxFrameLength(), delimiters));
        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        pipeline.addLast("handler", new TcpListenerEntryHandler(adapter));
        return pipeline;
    }
}

private class TcpListenerEntryHandler extends SimpleChannelUpstreamHandler {
    private TcpListeningAdapter adapter;
    public TcpListenerEntryHandler(TcpListeningAdapter adapter) {
        this.adapter = adapter;
    }
    public void messageReceived(ChannelHandlerContext channelHandlerContext, MessageEvent event) {
        String entry = (String) event.getMessage();
        adapter.addEntryToBuffer(entry);
    }

}