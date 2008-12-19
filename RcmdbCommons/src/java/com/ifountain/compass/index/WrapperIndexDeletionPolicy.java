package com.ifountain.compass.index;

import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.KeepOnlyLastCommitDeletionPolicy;
import org.apache.lucene.index.SnapshotDeletionPolicy;
import org.apache.lucene.index.IndexCommitPoint;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.IndexInput;
import org.compass.core.lucene.engine.indexdeletionpolicy.DirectoryConfigurable;
import org.compass.core.lucene.engine.LuceneSearchEngineFactory;
import org.compass.core.Compass;
import org.compass.core.impl.DefaultCompass;

import java.io.IOException;
import java.io.FileOutputStream;
import java.util.*;

import groovy.lang.Closure;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 18, 2008
 * Time: 4:43:01 PM
 * To change this template use File | Settings | File Templates.
 */
public class WrapperIndexDeletionPolicy implements IndexDeletionPolicy, DirectoryConfigurable {
    private static Map<Directory, WrapperIndexDeletionPolicy> createdPolicies = new HashMap<Directory, WrapperIndexDeletionPolicy>();
    private Directory dir;

    public static List<WrapperIndexDeletionPolicy> getPolicies() {
        return new ArrayList<WrapperIndexDeletionPolicy>(createdPolicies.values());
    }
    public static void clearPolicies() {
        createdPolicies.clear();
    }

    private SnapshotDeletionPolicy policy;

    public void onInit(List list) throws IOException {
        policy.onInit(list);
    }

    public void onCommit(List list) throws IOException {
        policy.onCommit(list);
    }

    public SnapshotDeletionPolicy getWrappedPolicy()
    {
        return policy;
    }
    public void snapshot(IndexSnapshotAction snapshotAction) throws IOException {
        try {

            IndexCommitPoint commit = policy.snapshot();
            snapshotAction.execute(commit, dir);
        } finally {
            policy.release();
        }
    }

    public void setDirectory(Directory directory) {
        this.dir = directory;
        synchronized (createdPolicies)
        {
            WrapperIndexDeletionPolicy oldPolicy = createdPolicies.get(dir);
            if(oldPolicy != null)
            {
                policy = oldPolicy.getWrappedPolicy();
            }
            else
            {
                 policy = new SnapshotDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());
            }
        }
        createdPolicies.put(directory, this);
    }
}
