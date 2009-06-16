package com.ifountain.rcmdb.aol;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 3:17:49 PM
 */

import net.kano.joscar.snac.SnacRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class SnacManager {
    protected Map conns = new HashMap();
    protected PendingSnacMgr pendingSnacs = new PendingSnacMgr();
    protected List listeners = new ArrayList();
    protected Map supportedFamilies = new IdentityHashMap();

    public SnacManager() {
    }

    public SnacManager(PendingSnacListener listener) {
        addListener(listener);
    }

    public void register(BasicConn conn) {
        int[] families = conn.getSnacFamilies();
        supportedFamilies.put(conn, families);

        for (int i = 0; i < families.length; i++) {
            int familyCode = families[i];
            Integer family = new Integer(familyCode);

            List handlers = (List) conns.get(family);

            if (handlers == null) {
                handlers = new LinkedList();
                conns.put(family, handlers);
            }

            if (!handlers.contains(conn)) handlers.add(conn);
        }
    }

    public void dequeueSnacs(BasicConn conn) {
        int[] infos = (int[]) supportedFamilies.get(conn);

        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                int familyCode = infos[i];
                if (pendingSnacs.isPending(familyCode)) {
                    dequeueSnacs(familyCode);
                }
            }
        }
    }

    protected void dequeueSnacs(int familyCode) {
        SnacRequest[] pending = pendingSnacs.getPending(familyCode);

        pendingSnacs.setPending(familyCode, false);

        for (Iterator it = listeners.iterator(); it.hasNext();) {
            PendingSnacListener listener = (PendingSnacListener) it.next();

            listener.dequeueSnacs(pending);
        }
    }

    public void unregister(BasicConn conn) {
        for (Iterator it = conns.values().iterator(); it.hasNext();) {
            List handlers = (List) it.next();

            handlers.remove(conn);
        }
    }

    public BasicConn getConn(int familyCode) {
        Integer family = new Integer(familyCode);

        List handlers = (List) conns.get(family);

        if (handlers == null || handlers.size() == 0) return null;

        return (BasicConn) handlers.get(0);
    }


    public boolean isPending(int familyCode) {
        return pendingSnacs.isPending(familyCode);
    }

    public void addRequest(SnacRequest request) {
        pendingSnacs.add(request);
    }

    public void addListener(PendingSnacListener l) {
        if (!listeners.contains(l)) listeners.add(l);
    }

    public void removeListener(PendingSnacListener l) {
        listeners.remove(l);
    }

    public void setPending(int family, boolean pending) {
        pendingSnacs.setPending(family, pending);
    }

}
