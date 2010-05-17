/*
 * Copyright 2004-2006 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.ifountain.compass;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.CompassClassMapping;
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.DomainClassMappingHelper;
import org.compass.core.CompassException;
import org.compass.core.config.CompassConfigurable;
import org.compass.core.config.CompassSettings;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.lucene.engine.store.wrapper.DirectoryWrapperProvider;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Wraps a Lucene {@link Directory} with {@link MemoryMirrorDirectoryWrapper}.
 *
 * @author kimchy
 * @see MemoryMirrorDirectoryWrapper
 */
public class CompositeDirectoryWrapperProvider implements DirectoryWrapperProvider, CompassConfigurable {
    private static final Log log = LogFactory.getLog(CompositeDirectoryWrapperProvider.class);
    private static String logPrefix="[CompositeDirectoryWrapperProvider] : ";
    public static String FILE_DIR_TYPE = "File";
    public static String RAM_DIR_TYPE = "Memory";
    public static String MIRRORED_DIR_TYPE = "FileAndMemory";
    private long awaitTermination;
    private long maxNumberOfUnProcessedBytes = (long)Math.pow(2, 27);
    private long minNumberOfUnProcessedBytes = (long)Math.pow(2, 26);
    public void configure(CompassSettings settings) throws CompassException {
        awaitTermination = settings.getSettingAsLong("awaitTermination", 5);
        try
        {
            maxNumberOfUnProcessedBytes = (long)(Long.parseLong(System.getProperty("mirrorBufferUpperLimit", "128"))* Math.pow(2, 20));
        }catch(NumberFormatException e)
        {
            throw new InvalidMirrorBufferSizeException("mirrorBufferUpperLimit", System.getProperty("mirrorBufferUpperLimit"), e.getMessage());
        }
        try
        {
            minNumberOfUnProcessedBytes = (long)(Long.parseLong(System.getProperty("mirrorBufferLowerLimit", "64"))* Math.pow(2, 20));
        }catch(NumberFormatException e)
        {
            throw new InvalidMirrorBufferSizeException("mirrorBufferLowerLimit", System.getProperty("mirrorBufferLowerLimit"), e.getMessage());
        }
        if(minNumberOfUnProcessedBytes >= maxNumberOfUnProcessedBytes)
        {
            throw new InvalidMirrorBufferSizeException("mirrorBufferLowerLimit", System.getProperty("mirrorBufferLowerLimit"), "mirrorBufferUpperLimit should be greater than mirrorBufferLowerLimit");
        }

    }

    public Directory wrap(String subIndex, Directory dir) throws SearchEngineException {
        String subLogPrefix=logPrefix+subIndex+" : ";

        List allClassMappings = DomainClassMappingHelper.getDomainClassMappings();
        try {
            log.info(subLogPrefix+"Checking storageType");
            
            String storageType = null;
            CompassClassMapping  classMapping=getClassMappingForSubIndex(allClassMappings,subIndex);
            if(classMapping != null)
            {
                storageType=classMapping.getStorageType();
            }
            log.info(subLogPrefix+"storageType in classMapping is "+storageType);

            if(storageType == null)
            {
                //search the top parent class and find storage type of it
                if(classMapping!=null)
                {
                    log.info(subLogPrefix+"Seaching superclasses becasue storageType is "+storageType);
                    Class superClass=classMapping.getMappedClassSuperClass();
                    while(superClass!=null)
                    {
                        log.info(subLogPrefix+"Seaching superclass "+superClass.getName());
                        CompassClassMapping  superClassMapping=getClassMappingForMappedClass(allClassMappings,superClass);
                        if(superClassMapping != null)
                        {
                            storageType=superClassMapping.getStorageType();
                            if(storageType==null)   //if not found search another top
                            {
                                superClass=superClassMapping.getMappedClassSuperClass();
                            }
                            else //if found stop search
                            {
                                log.info(subLogPrefix+"Found storageType "+storageType+" in superClass "+superClass.getName());
                                superClass=null;
                            }
                        }
                    }
                }
                //if no storageType found in parents, then return dir
                if(storageType==null)
                {
                    log.info(subLogPrefix+"storageType can not be found in supercclasses, using default- FILE_TYPE (given dir) ");
                    return dir;
                }
            }

            if(storageType.equalsIgnoreCase(RAM_DIR_TYPE))
            {
                log.info(subLogPrefix+"Creating  storageType "+storageType);
                RAMDirectory ramdir = new RAMDirectory(dir);
                ramdir.setLockFactory(dir.getLockFactory());
                return ramdir;
            }
            else  if(storageType.equalsIgnoreCase(MIRRORED_DIR_TYPE))
            {
                log.info(subLogPrefix+"Creating  storageType "+storageType);
                return new MemoryMirrorDirectoryWrapper(dir, awaitTermination, maxNumberOfUnProcessedBytes, minNumberOfUnProcessedBytes, doCreateExecutorService());
            }
            else if(storageType.equalsIgnoreCase(FILE_DIR_TYPE))
            {
                log.info(subLogPrefix+"Creating  storageType "+storageType);
                return dir;
            }
            else
            {
                log.warn(subLogPrefix+"StorageType is unknown using default- FILE_TYPE (given dir)");
                return dir;
            }
            
        } catch (IOException e) {
            throw new SearchEngineException("Failed to wrap directory [" + dir + "] with async memory wrapper", e);
        }
    }

    private CompassClassMapping getClassMappingForSubIndex(List allClassMappings,String subIndex)
    {
        CompassClassMapping classMapping=null;
        for(int i=0; i < allClassMappings.size(); i++)
        {
            if(subIndex.equals(((CompassClassMapping)allClassMappings.get(i)).getSubIndex()))
            {
                //storageType = ((CompassClassMapping)allClassMappings.get(i)).getStorageType();
                classMapping=((CompassClassMapping)allClassMappings.get(i));
                break;
            }
        }
        return classMapping;
    }
    private CompassClassMapping getClassMappingForMappedClass(List allClassMappings,Class mappedClass)
    {
        CompassClassMapping classMapping=null;
        for(int i=0; i < allClassMappings.size(); i++)
        {
            if(mappedClass.equals(((CompassClassMapping)allClassMappings.get(i)).getMappedClass()))
            {
                //storageType = ((CompassClassMapping)allClassMappings.get(i)).getStorageType();
                classMapping=((CompassClassMapping)allClassMappings.get(i));
                break;
            }
        }
        return classMapping;
    }
    protected ExecutorService doCreateExecutorService() {
        return Executors.newSingleThreadExecutor();
    }
}
