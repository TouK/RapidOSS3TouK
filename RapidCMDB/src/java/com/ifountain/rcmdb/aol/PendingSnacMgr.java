package com.ifountain.rcmdb.aol;

import net.kano.joscar.snac.SnacRequest;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 3:16:49 PM
 */
public class PendingSnacMgr {
    protected Map snacs = new HashMap();

    public boolean isPending(int familyCode) {
        Integer family = new Integer(familyCode);

        return snacs.containsKey(family);
    }

    public void add(SnacRequest request) {
        Integer family = new Integer(request.getCommand().getFamily());

        List pending = (List) snacs.get(family);

        pending.add(request);
    }

    public SnacRequest[] getPending(int familyCode) {
        Integer family = new Integer(familyCode);

        List pending = (List) snacs.get(family);

        return (SnacRequest[]) pending.toArray(new SnacRequest[0]);
    }

    public void setPending(int familyCode, boolean pending) {
        Integer family = new Integer(familyCode);

        if (pending) snacs.put(family, new ArrayList());
        else snacs.remove(family);
    }
}
