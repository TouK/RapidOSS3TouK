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
    private static Object globalSanapshotLock = new Object();
    private static boolean takingSnapshot = false;
    public static List<WrapperIndexDeletionPolicy> getPolicies() {
        synchronized (createdPolicies)
        {
            return new ArrayList<WrapperIndexDeletionPolicy>(createdPolicies.values());
        }
    }

    public static void clearPolicies() {
        synchronized (createdPolicies)
        {
            createdPolicies.clear();
        }
    }

    public static void takeGlobalSnapshot(IndexSnapshotAction snapshotAction) throws IOException
    {
        List indexPointList = new ArrayList();
        List policies = getPolicies();
        List snapShotPolicies = new ArrayList();
        try
        {
            synchronized (globalSanapshotLock)
            {
                takingSnapshot = true;
                for(int i=0; i < policies.size(); i++)
                {
                    WrapperIndexDeletionPolicy policy = (WrapperIndexDeletionPolicy)policies.get(i);
                    IndexCommitPoint commitPoint = policy.getWrappedPolicy().snapshot();
                    indexPointList.add(commitPoint);
                    snapShotPolicies.add(policy);
                }
                takingSnapshot = false;
                globalSanapshotLock.notifyAll();
            }
            for(int i=0; i < indexPointList.size(); i++)
            {
                IndexCommitPoint commitPoint = (IndexCommitPoint)indexPointList.get(i);
                WrapperIndexDeletionPolicy policy = (WrapperIndexDeletionPolicy)snapShotPolicies.get(i);
                snapshotAction.execute(commitPoint, policy.dir);
            }
        }finally{
            for(int i=0; i < snapShotPolicies.size(); i++)
            {
                WrapperIndexDeletionPolicy policy = (WrapperIndexDeletionPolicy)snapShotPolicies.get(i);
                policy.getWrappedPolicy().release();
            }
        }
    }

    private SnapshotIndexDeletionPolicy policy;

    public void onInit(List list) throws IOException {
        checkGlobalSnapshotLock();
        policy.onInit(list);
    }

    private static void checkGlobalSnapshotLock()
    {
        try
        {
            synchronized (globalSanapshotLock)
            {
                if(takingSnapshot)
                {
                    globalSanapshotLock.wait();
                }
        }
        }
        catch(InterruptedException e)
        {
        }   
    }

    public void onCommit(List list) throws IOException {
        checkGlobalSnapshotLock();
        policy.onCommit(list);
    }

    public SnapshotIndexDeletionPolicy getWrappedPolicy()
    {
        return policy;
    }
    private void snapshot(IndexSnapshotAction snapshotAction) throws IOException {
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
                 policy = new SnapshotIndexDeletionPolicy(new KeepOnlyLastCommitDeletionPolicy());
            }
            createdPolicies.put(directory, this);
        }
    }
}
