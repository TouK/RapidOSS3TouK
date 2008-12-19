package com.ifountain.compass.index;

import org.apache.lucene.index.IndexCommitPoint;
import org.apache.lucene.store.Directory;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 19, 2008
 * Time: 10:54:35 AM
 * To change this template use File | Settings | File Templates.
 */
public interface IndexSnapshotAction {
    public void execute(IndexCommitPoint commitPoint, Directory indexDir);
}
