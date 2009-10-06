package com.ifountain.rcmdb.domain

import com.ifountain.compass.index.IndexSnapshotAction
import org.apache.log4j.Logger
import org.apache.lucene.index.IndexCommitPoint
import org.apache.lucene.store.Directory
import org.apache.lucene.store.IndexInput
import org.compass.core.CompassQuery
import org.compass.core.lucene.engine.LuceneSearchEngineFactory
import org.compass.core.spi.InternalCompass

/**
 * Created by IntelliJ IDEA.
 * User: mustafa sener
 * Date: Dec 19, 2008
 * Time: 11:11:01 AM
 * To change this template use File | Settings | File Templates.
 */
class BackupAction implements IndexSnapshotAction {

    Logger logger;
    private Map directoryToSubindexMap = [:]
    private String destinationDirectory;
    public BackupAction(InternalCompass compass, List domainClasses, String destinationDirectory)
    {
        this(compass, domainClasses, destinationDirectory, Logger.getLogger(BackupAction.class));
    }
    public BackupAction(InternalCompass compass, List domainClasses, String destinationDirectory, Logger logger)
    {
        this.logger = logger;
        logger.warn("Initialized a backup action");
        domainClasses.each {domainClass ->
            def session = compass.openSession();
            def tr = session.beginTransaction()
            def instance = domainClass.newInstance()
            instance.id = -1;
            session.save(instance);
            CompassQuery query = session.queryBuilder().queryString("id:\\-1").toQuery();
            def res = query.hits().resource(0)
            session.delete(res);
            tr.commit();
            session.close();
        }
        this.destinationDirectory = new File(destinationDirectory).getCanonicalPath();
        LuceneSearchEngineFactory serachEngineFactory = (LuceneSearchEngineFactory) compass.getSearchEngineFactory();
        String[] subIndexes = serachEngineFactory.getLuceneIndexManager().getStore().getSubIndexes();
        for (int i = 0; i < subIndexes.length; i++)
        {
            def subIndex = subIndexes[i];
            directoryToSubindexMap.put(serachEngineFactory.getLuceneIndexManager().getStore().openDirectory(subIndex), subIndex);
        }
    }
    public void execute(IndexCommitPoint commitPoint, Directory indexDir) {

        Collection<String> fileNames = commitPoint.getFileNames();
        String subIndex = directoryToSubindexMap.get(indexDir);
        def dirDestFilePath = destinationDirectory + "/" + subIndex + "/"
        logger.warn("Executing backup action for ${subIndex} copying ${fileNames} to ${dirDestFilePath}");
        for (Iterator<String> it = fileNames.iterator(); it.hasNext();) {
            String fileName = it.next();
            backupFile(indexDir, fileName, dirDestFilePath + fileName);
        }
        logger.warn("Backup action completed for ${subIndex}");
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
            } catch (Throwable t)
            {
            }
            try
            {
                fout.close();
            } catch (Throwable t)
            {
            }
        }
    }
}