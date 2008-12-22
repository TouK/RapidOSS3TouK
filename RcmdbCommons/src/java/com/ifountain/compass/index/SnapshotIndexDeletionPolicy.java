package com.ifountain.compass.index;

import org.apache.lucene.index.IndexDeletionPolicy;
import org.apache.lucene.index.IndexCommitPoint;

import java.util.Collection;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;


public class SnapshotIndexDeletionPolicy implements IndexDeletionPolicy {

  private IndexCommitPoint lastCommit;
  private IndexDeletionPolicy primary;
  private String snapshot;

  public SnapshotIndexDeletionPolicy(IndexDeletionPolicy primary) {
    this.primary = primary;
  }

  public synchronized void onInit(List commits) throws IOException {
    primary.onInit(wrapCommits(commits));
    lastCommit = (IndexCommitPoint) commits.get(commits.size()-1);
  }

  public synchronized void onCommit(List commits) throws IOException {
    primary.onCommit(wrapCommits(commits));
    lastCommit = (IndexCommitPoint) commits.get(commits.size()-1);
  }

  /** Take a snapshot of the most recent commit to the
   *  index.  You must call release() to free this snapshot.
   *  Note that while the snapshot is held, the files it
   *  references will not be deleted, which will consume
   *  additional disk space in your index. If you take a
   *  snapshot at a particularly bad time (say just before
   *  you call optimize()) then in the worst case this could
   *  consume an extra 1X of your total index size, until
   *  you release the snapshot. */
  public synchronized IndexCommitPoint snapshot() {
    if (snapshot == null)
      snapshot = lastCommit.getSegmentsFileName();
    else
      throw new IllegalStateException("snapshot is already set; please call release() first");
    return lastCommit;
  }

  /** Release the currently held snapshot. */
  public synchronized void release() {
    if (snapshot != null)
      snapshot = null;
    else
      throw new IllegalStateException("snapshot was not set; please call snapshot() first");
  }

  private class MyCommitPoint implements IndexCommitPoint {
    IndexCommitPoint cp;
    MyCommitPoint(IndexCommitPoint cp) {
      this.cp = cp;
    }
    public String getSegmentsFileName() {
      return cp.getSegmentsFileName();
    }
    public Collection getFileNames() throws IOException {
      return cp.getFileNames();
    }
    public void delete() {
      synchronized(SnapshotIndexDeletionPolicy.this) {
        // Suppress the delete request if this commit point is
        // our current snapshot.
        if (snapshot == null || !snapshot.equals(getSegmentsFileName()))
        {
          cp.delete();
        }
      }
    }
  }

  private List wrapCommits(List commits) {
    final int count = commits.size();
    List myCommits = new ArrayList(count);
    for(int i=0;i<count;i++)
      myCommits.add(new MyCommitPoint((IndexCommitPoint) commits.get(i)));
    return myCommits;
  }
}
