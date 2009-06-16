package com.ifountain.rcmdb.aol;

import net.kano.joscar.flap.*;
import net.kano.joscar.snac.*;
import net.kano.joscar.flapcmd.DefaultFlapCmdFactory;
import net.kano.joscar.flapcmd.SnacCommand;
import net.kano.joscar.snaccmd.DefaultClientFactoryList;
import net.kano.joscar.net.ClientConnListener;
import net.kano.joscar.net.ClientConnEvent;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 2:19:32 PM
 */
public abstract class AbstractFlapConn extends ClientFlapConn {
    protected IAolConnection aolConnection;
    protected ClientSnacProcessor snacProcessor
            = new ClientSnacProcessor(getFlapProcessor());

    { // init
        getFlapProcessor().setFlapCmdFactory(new DefaultFlapCmdFactory());
        snacProcessor.addPreprocessor(new FamilyVersionPreprocessor());
        snacProcessor.getCmdFactoryMgr().setDefaultFactoryList(
                new DefaultClientFactoryList());

        addConnListener(new ClientConnListener() {
            public void stateChanged(ClientConnEvent e) {
                handleStateChange(e);
            }
        });
        getFlapProcessor().addPacketListener(new FlapPacketListener() {
            public void handleFlapPacket(FlapPacketEvent e) {
                AbstractFlapConn.this.handleFlapPacket(e);
            }
        });
        getFlapProcessor().addExceptionHandler(new FlapExceptionHandler() {
            public void handleException(FlapExceptionEvent event) {
                aolConnection.getLogger().warn(event.getType() + " FLAP ERROR: " + event.getException().getMessage(), event.getException());
            }
        });
        snacProcessor.addPacketListener(new SnacPacketListener() {
            public void handleSnacPacket(SnacPacketEvent e) {
                AbstractFlapConn.this.handleSnacPacket(e);
            }
        });
    }

    protected SnacRequestListener genericReqListener
            = new SnacRequestAdapter() {
        public void handleResponse(SnacResponseEvent e) {
            handleSnacResponse(e);
        }
    };


    public AbstractFlapConn(String host, int port, IAolConnection aolConn) {
        super(host, port);
        this.aolConnection = aolConn;
    }


    public SnacRequestListener getGenericReqListener() {
        return genericReqListener;
    }

    public ClientSnacProcessor getSnacProcessor() {
        return snacProcessor;
    }

    public IAolConnection getAolConnection() {
        return aolConnection;
    }

    public void sendRequest(SnacRequest req) {
        if (!req.hasListeners()) req.addListener(genericReqListener);
        snacProcessor.sendSnac(req);
    }

    SnacRequest request(SnacCommand cmd) {
        return request(cmd, null);
    }

    SnacRequest request(SnacCommand cmd, SnacRequestListener listener) {
        SnacRequest req = new SnacRequest(cmd, listener);
        sendRequest(req);
        return req;
    }

    protected abstract void handleStateChange(ClientConnEvent e);

    protected abstract void handleFlapPacket(FlapPacketEvent e);

    protected abstract void handleSnacPacket(SnacPacketEvent e);

    protected abstract void handleSnacResponse(SnacResponseEvent e);

}
