package com.ifountain.rcmdb.domain

import com.ifountain.compass.index.IndexSnapshotAction
import org.apache.lucene.index.IndexCommitPoint
import org.apache.lucene.store.Directory
import org.apache.lucene.store.IndexInput
import org.compass.core.impl.DefaultCompass
import org.compass.core.lucene.engine.LuceneSearchEngineFactory

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 19, 2008
 * Time: 11:11:01 AM
 * To change this template use File | Settings | File Templates.
 */
class BackupAction implements IndexSnapshotAction{

    private Map directoryToSubindexMap = [:]
    private String destinationDirectory;
    public BackupAction(DefaultCompass compass, String destinationDirectory)
    {
        this.destinationDirectory = new File(destinationDirectory).getCanonicalPath();
        LuceneSearchEngineFactory serachEngineFactory = (LuceneSearchEngineFactory)compass.getSearchEngineFactory();
        String[] subIndexes = serachEngineFactory.getLuceneIndexManager().getStore().getSubIndexes();
        for(int i=0; i < subIndexes.length; i++)
        {
            def subIndex = subIndexes[i];
            directoryToSubindexMap.put(serachEngineFactory.getLuceneIndexManager().getStore().openDirectory(subIndex), subIndex);
        }
    }
    public void execute(IndexCommitPoint commitPoint, Directory indexDir) {
        Collection<String> fileNames = commitPoint.getFileNames();
        def dirDestFilePath = destinationDirectory+"/"+directoryToSubindexMap.get(indexDir)+"/"
        for (Iterator<String> it = fileNames.iterator(); it.hasNext();) {
            String fileName = it.next();
            backupFile(indexDir, fileName, dirDestFilePath+fileName);
        }
    }

    private void backupFile(Directory dir, String fileName, String backupFilePath) throws IOException {
        byte[] buffer = new byte[4096];
        IndexInput input = dir.openInput(fileName);
        File backupFile = new File(backupFilePath);
        backupFile.parentFile.mkdirs();
        FileOutputStream fout = new FileOutputStream(backupFile.getPath());
        try {
            long size = dir.fileLength(fileName);
            long bytesLeft = size;
            while (bytesLeft > 0) {
                final int numToRead;
                if (bytesLeft < buffer.length)
                    numToRead = (int) bytesLeft;
                else
                    numToRead = buffer.length;
                input.readBytes(buffer, 0, numToRead, false);
                fout.write(buffer, 0, numToRead);
                bytesLeft -= numToRead;
            }

        } finally {
            try
            {
                input.close();
            }catch(Throwable t)
            {
            }
            try
            {
                fout.close();
            }catch(Throwable t)
            {
            }
        }
    }
}