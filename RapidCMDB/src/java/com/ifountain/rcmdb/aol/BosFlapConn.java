package com.ifountain.rcmdb.aol;

import net.kano.joscar.ssiitem.SsiItemObjectFactory;
import net.kano.joscar.ssiitem.DefaultSsiItemObjFactory;
import net.kano.joscar.ssiitem.SsiItemObj;
import net.kano.joscar.ByteBlock;
import net.kano.joscar.snaccmd.conn.*;
import net.kano.joscar.snaccmd.icbm.*;
import net.kano.joscar.snaccmd.loc.LocRightsRequest;
import net.kano.joscar.snaccmd.loc.LocRightsCmd;
import net.kano.joscar.snaccmd.loc.SetInfoCmd;
import net.kano.joscar.snaccmd.loc.UserInfoCmd;
import net.kano.joscar.snaccmd.ssi.*;
import net.kano.joscar.snaccmd.*;
import net.kano.joscar.snaccmd.error.SnacError;
import net.kano.joscar.flapcmd.SnacCommand;
import net.kano.joscar.snac.SnacPacketEvent;
import net.kano.joscar.snac.SnacResponseEvent;
import net.kano.joscar.flap.ClientFlapConn;
import net.kano.joscar.flap.FlapPacketEvent;
import net.kano.joscar.net.ClientConnEvent;

import java.security.cert.*;
import java.security.NoSuchProviderException;
import java.io.ByteArrayInputStream;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 2:42:55 PM
 */
public class BosFlapConn extends BasicConn {
    protected SsiItemObjectFactory itemFactory = new DefaultSsiItemObjFactory();


    public BosFlapConn(String host, int port, IAolConnection aolConn,
                       ByteBlock cookie) {
        super(host, port, aolConn, cookie);
    }


    protected void handleStateChange(ClientConnEvent e) {
        if (e.getNewState() == ClientFlapConn.STATE_NOT_CONNECTED || e.getNewState() == ClientFlapConn.STATE_FAILED) {
            try {
                aolConnection.unRegisterSnacFamilies(this);
            }
            catch (Throwable e1) {
            }
            aolConnection.disconnectDetected(String.valueOf(e.getReason()));
        }
        aolConnection.getLogger().debug("main connection state changed from "
                + e.getOldState() + " to " + e.getNewState() + ": "
                + e.getReason());
    }

    protected void handleFlapPacket(FlapPacketEvent e) {
        super.handleFlapPacket(e);
    }

    protected void handleSnacPacket(SnacPacketEvent e) {
        super.handleSnacPacket(e);

        SnacCommand cmd = e.getSnacCommand();
        if (cmd instanceof ServerReadyCmd) {
            request(new ParamInfoRequest());
            request(new LocRightsRequest());
            request(new SsiRightsRequest());
            request(new SsiDataRequest());
        }
    }

    protected void handleSnacResponse(SnacResponseEvent e) {
        super.handleSnacResponse(e);

        SnacCommand cmd = e.getSnacCommand();

        if (cmd instanceof LocRightsCmd) {
            try {
                Certificate cert = aolConnection.getSecureSession().getMyCertificate();
                CertificateInfo certInfo;
                if (cert == null) {
                    certInfo = null;
                } else {
                    byte[] encoded = cert.getEncoded();
                    certInfo = new CertificateInfo(
                            ByteBlock.wrap(encoded));
                }
                request(new SetInfoCmd(new InfoData("yo",
                        null, new CapabilityBlock[]{
                        CapabilityBlock.BLOCK_CHAT,
                        CapabilityBlock.BLOCK_DIRECTIM,
                        CapabilityBlock.BLOCK_FILE_GET,
                        CapabilityBlock.BLOCK_FILE_SEND,
                        CapabilityBlock.BLOCK_GAMES,
                        CapabilityBlock.BLOCK_GAMES2,
                        CapabilityBlock.BLOCK_ICON,
                        CapabilityBlock.BLOCK_SENDBUDDYLIST,
                        CapabilityBlock.BLOCK_TRILLIANCRYPT,
                        CapabilityBlock.BLOCK_VOICE,
                        CapabilityBlock.BLOCK_ADDINS,
                        CapabilityBlock.BLOCK_ICQCOMPATIBLE,
                        CapabilityBlock.BLOCK_SHORTCAPS,
                        CapabilityBlock.BLOCK_ENCRYPTION,
//                        CapabilityBlock.BLOCK_SOMETHING,
                }, certInfo)));
            } catch (CertificateEncodingException e1) {
                e1.printStackTrace();
            }
            request(new SetEncryptionInfoCmd(new ExtraInfoBlock[]{
                    new ExtraInfoBlock(0x0402, new ExtraInfoData(
                            ExtraInfoData.FLAG_HASH_PRESENT,
                            CertificateInfo.HASHA_DEFAULT)),
                    new ExtraInfoBlock(0x0403, new ExtraInfoData(
                            ExtraInfoData.FLAG_HASH_PRESENT,
                            CertificateInfo.HASHB_DEFAULT)),
            }));
            request(new MyInfoRequest());

        } else if (cmd instanceof ParamInfoCmd) {
            ParamInfoCmd pic = (ParamInfoCmd) cmd;

            ParamInfo info = pic.getParamInfo();

            request(new SetParamInfoCmd(new ParamInfo(0,
                    info.getFlags() | ParamInfo.FLAG_TYPING_NOTIFICATION, 8000,
                    info.getMaxSenderWarning(), info.getMaxReceiverWarning(),
                    0)));

        } else if (cmd instanceof YourInfoCmd) {
            YourInfoCmd yic = (YourInfoCmd) cmd;
            FullUserInfo info = yic.getUserInfo();

            aolConnection.getLogger().debug("got my user info: " + info);

        } else if (cmd instanceof UserInfoCmd) {
            UserInfoCmd uic = (UserInfoCmd) cmd;

            String sn = uic.getUserInfo().getScreenname();
            aolConnection.getLogger().debug("user info for " + sn + ": "
                    + uic.getInfoData());

            CertificateInfo certInfo = uic.getInfoData().getCertificateInfo();
            if (certInfo != null) {
                ByteBlock certData = certInfo.getCommonCertData();

                try {
                    CertificateFactory factory
                            = CertificateFactory.getInstance("X.509", "BC");
                    ByteArrayInputStream stream
                            = new ByteArrayInputStream(certData.toByteArray());
                    X509Certificate cert = (X509Certificate)
                            factory.generateCertificate(stream);

                    aolConnection.getSecureSession().setCert(sn, cert);

                    X509Certificate x = cert;
                    aolConnection.getLogger().debug("got certificate for " + sn + ": "
                            + x.getSubjectX500Principal().getName());


                } catch (CertificateException e1) {
                    e1.printStackTrace();
                } catch (NoSuchProviderException e1) {
                    e1.printStackTrace();
                }
            }

        } else if (cmd instanceof SsiDataCmd) {
            SsiDataCmd sdc = (SsiDataCmd) cmd;

            SsiItem[] items = sdc.getItems();
            for (int i = 0; i < items.length; i++) {
                SsiItemObj obj = itemFactory.getItemObj(items[i]);
                aolConnection.getLogger().debug("- " + (obj == null ? (Object) items[i]
                        : (Object) obj));
            }

            if (sdc.getLastModDate() != 0) {
                aolConnection.getLogger().debug("done with SSI");
                request(new ActivateSsiCmd());
                clientReady();
            }
        } else if (cmd instanceof MessageAck) {
            aolConnection.messageSentSuccessfully();
        } else if (cmd instanceof SnacError) {
            SnacError err = (SnacError) cmd;
            aolConnection.messageFailed(err.toString());
        } 
    }

}
