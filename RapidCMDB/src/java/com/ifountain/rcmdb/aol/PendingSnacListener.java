package com.ifountain.rcmdb.aol;

import net.kano.joscar.snac.SnacRequest;

/**
 * Created by IntelliJ IDEA.
 * User: Sezgin Kucukkaraaslan
 * Date: Jun 11, 2009
 * Time: 3:16:23 PM
 */
public interface PendingSnacListener {
    void dequeueSnacs(SnacRequest[] pending);
}
