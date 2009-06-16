package com.ifountain.rcmdb.aol;

import net.kano.joscar.ByteBlock;
import net.kano.joscar.OscarTools;
import net.kano.joscar.snac.SnacPacketEvent;
import net.kano.joscar.snac.SnacResponseEvent;
import net.kano.joscar.snac.SnacRequest;
import net.kano.joscar.snac.SnacRequestListener;
import net.kano.joscar.flapcmd.LoginFlapCmd;
import net.kano.joscar.flapcmd.SnacPacket;
import net.kano.joscar.flapcmd.SnacCommand;
import net.kano.joscar.flap.FlapPacketEvent;
import net.kano.joscar.flap.FlapCommand;
import net.kano.joscar.rvcmd.DefaultRvCommandFactory;
import net.kano.joscar.rv.*;
import net.kano.joscar.ratelim.RateLimitingQueueMgr;
import net.kano.joscar.snaccmd.conn.*;
import net.kano.joscar.snaccmd.SnacFamilyInfoFactory;
import net.kano.joscar.snaccmd.icbm.RecvImIcbm;
import net.kano.joscar.snaccmd.icbm.InstantMessage;
import net.kano.joscar.snaccmd.icbm.ParamInfoCmd;

import java.util.Map;
import java.util.HashMap;
import java.util.Arrays;
import java.text.DateFormat;
import java.io.PrintWriter;

import com.ifountain.rcmdb.aol.security.SecureSession;
import com.ifountain.rcmdb.aol.security.SecureSessionException;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 2:32:17 PM
 */
public abstract class BasicConn extends AbstractFlapConn {
    protected final ByteBlock cookie;
    protected boolean sentClientReady = false;

    protected int[] snacFamilies = null;
    protected SnacFamilyInfo[] snacFamilyInfos;
    protected RateLimitingQueueMgr rateMgr = new RateLimitingQueueMgr();
    protected RvProcessor rvProcessor = new RvProcessor(snacProcessor);
    protected RvProcessorListener rvListener = new RvProcessorListener() {
        public void handleNewSession(NewRvSessionEvent event) {
            aolConnection.getLogger().debug("new RV session: " + event.getSession());
            event.getSession().addListener(rvSessionListener);
        }
    };
    protected Map trillianEncSessions = new HashMap();

    protected RvSessionListener rvSessionListener = new RvSessionListener() {
        public void handleRv(RecvRvEvent event) {
        }

        public void handleSnacResponse(RvSnacResponseEvent event) {
            aolConnection.getLogger().debug("got SNAC response for <"
                    + event.getRvSession() + ">: "
                    + event.getSnacCommand());
        }
    };

    { // init
        snacProcessor.setSnacQueueManager(rateMgr);
        rvProcessor.registerRvCmdFactory(new DefaultRvCommandFactory());
        rvProcessor.addListener(rvListener);
    }


    protected DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT);
    protected PrintWriter imLogger = null;

    public BasicConn(String host, int port, IAolConnection aolConn,
                     ByteBlock cookie) {
        super(host, port, aolConn);
        this.cookie = cookie;
    }


    protected void handleFlapPacket(FlapPacketEvent e) {
        FlapCommand cmd = e.getFlapCommand();

        if (cmd instanceof LoginFlapCmd) {
            getFlapProcessor().sendFlap(new LoginFlapCmd(cookie));
        } else {
            aolConnection.getLogger().debug("got FLAP command on channel 0x"
                    + Integer.toHexString(e.getFlapPacket().getChannel())
                    + ": " + cmd);
        }
    }

    protected void handleSnacPacket(SnacPacketEvent e) {
        SnacPacket packet = e.getSnacPacket();
        aolConnection.getLogger().debug("got snac packet type "
                + Integer.toHexString(packet.getFamily()) + "/"
                + Integer.toHexString(packet.getCommand()) + ": "
                + e.getSnacCommand());

        SnacCommand cmd = e.getSnacCommand();
        if (cmd instanceof ServerReadyCmd) {
            ServerReadyCmd src = (ServerReadyCmd) cmd;

            setSnacFamilies(src.getSnacFamilies());

            SnacFamilyInfo[] familyInfos = SnacFamilyInfoFactory
                    .getDefaultFamilyInfos(src.getSnacFamilies());

            setSnacFamilyInfos(familyInfos);

            aolConnection.registerSnacFamilies(this);

            request(new ClientVersionsCmd(familyInfos));
            request(new RateInfoRequest());

        } else if (cmd instanceof RecvImIcbm) {
            RecvImIcbm icbm = (RecvImIcbm) cmd;

            String sn = icbm.getSenderInfo().getScreenname();
            InstantMessage message = icbm.getMessage();
            String msg = null;
            if (message.isEncrypted()) {
                ByteBlock encData = message.getEncryptedData();
                aolConnection.getLogger().debug("got [" + encData.getLength() + "]");

                SecureSession secureSession = aolConnection.getSecureSession();
                if (secureSession.hasCert(sn)) {
                    try {
                        msg = secureSession.decodeEncryptedIM(sn, encData);
                    } catch (SecureSessionException e1) {
                        e1.printStackTrace();
                    }
                } else {
                    aolConnection.getLogger().debug(sn + " tried sending an encrypted "
                            + "message, but I don't have his/her certificate "
                            + " - try typing 'getcertinfo " + sn + "'");
                }

            } else {
                msg = OscarTools.stripHtml(message.getMessage());
            }
            if (sn.equalsIgnoreCase("AOL System Msg")) {
                aolConnection.getLogger().debug(sn + " Received System Message replies with 1");
                try {
                    aolConnection.sendIM(sn, "1");
                }
                catch (Exception e1) {
                }
                aolConnection.getLogger().debug(sn + " Received System Message replied with 1");
            } else {
                aolConnection.messageRecieved(sn, msg);
            }

        } else if (cmd instanceof RateChange) {
            RateChange rc = (RateChange) cmd;

            aolConnection.getLogger().debug("rate change: current avg is "
                    + rc.getRateInfo().getCurrentAvg());
        }
    }

    protected void handleSnacResponse(SnacResponseEvent e) {
        SnacPacket packet = e.getSnacPacket();
        aolConnection.getLogger().debug("got snac response type "
                + Integer.toHexString(packet.getFamily()) + "/"
                + Integer.toHexString(packet.getCommand()) + ": "
                + e.getSnacCommand());

        SnacCommand cmd = e.getSnacCommand();

        if (cmd instanceof RateInfoCmd) {
            RateInfoCmd ric = (RateInfoCmd) cmd;

            RateClassInfo[] rateClasses = ric.getRateClassInfos();

            int[] classes = new int[rateClasses.length];
            for (int i = 0; i < rateClasses.length; i++) {
                classes[i] = rateClasses[i].getRateClass();
            }

            request(new RateAck(classes));
        }
        else if (cmd instanceof ParamInfoCmd) {
            aolConnection.serverAlive();
        }
    }

    public int[] getSnacFamilies() {
        return snacFamilies;
    }

    protected void setSnacFamilies(int[] families) {
        this.snacFamilies = families.clone();
        Arrays.sort(snacFamilies);
    }

    protected void setSnacFamilyInfos(SnacFamilyInfo[] infos) {
        snacFamilyInfos = infos;
    }

    protected boolean supportsFamily(int family) {
        return Arrays.binarySearch(snacFamilies, family) >= 0;
    }

    protected void clientReady() {
        if (!sentClientReady) {
            sentClientReady = true;
            request(new ClientReadyCmd(snacFamilyInfos));
            aolConnection.clientReady();
        }
    }

    protected SnacRequest request(SnacCommand cmd,
                                  SnacRequestListener listener) {
        SnacRequest req = new SnacRequest(cmd, listener);

        handleReq(req);

        return req;
    }

    private void handleReq(SnacRequest request) {
        int family = request.getCommand().getFamily();
        if (snacFamilies == null || supportsFamily(family)) {
            // this connection supports this snac, so we'll send it here
            sendRequest(request);
        } else {
            try {
                aolConnection.handleRequest(request);
            }
            catch (InterruptedException e) {
            }
        }
    }

}
