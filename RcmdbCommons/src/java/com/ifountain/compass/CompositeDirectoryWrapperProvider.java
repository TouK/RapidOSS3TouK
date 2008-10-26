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

import java.io.IOException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.List;

import org.compass.core.lucene.engine.store.wrapper.*;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.RAMDirectory;
import org.compass.core.CompassException;
import org.compass.core.config.CompassConfigurable;
import org.compass.core.config.CompassSettings;
import org.compass.core.engine.SearchEngineException;
import org.compass.core.util.concurrent.SingleThreadThreadFactory;
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.DomainClassMappingHelper;
import org.codehaus.groovy.grails.plugins.searchable.compass.mapping.CompassClassMapping;

/**
 * Wraps a Lucene {@link Directory} with {@link MemoryMirrorDirectoryWrapper}.
 *
 * @author kimchy
 * @see MemoryMirrorDirectoryWrapper
 */
public class CompositeDirectoryWrapperProvider implements DirectoryWrapperProvider, CompassConfigurable {

    private long awaitTermination;
    private long maxNumberOfUnProcessedBytes = (long)Math.pow(2, 27);
    private long minNumberOfUnProcessedBytes = (long)Math.pow(2, 26);
    public void configure(CompassSettings settings) throws CompassException {
        awaitTermination = settings.getSettingAsLong("awaitTermination", 5);
        try
        {
            maxNumberOfUnProcessedBytes = (long)(Long.parseLong(System.getProperty("maxNumberOfUnProcessedBytes", "128"))* Math.pow(2, 20));
        }catch(NumberFormatException e)
        {
        }
        try
        {
            minNumberOfUnProcessedBytes = (long)(Long.parseLong(System.getProperty("minNumberOfUnProcessedBytes", "64"))* Math.pow(2, 20));
        }catch(NumberFormatException e)
        {
        }

    }

    public Directory wrap(String subIndex, Directory dir) throws SearchEngineException {
        List allClassMappings = DomainClassMappingHelper.getDomainClassMappings();
        try {
            String dirType = null;
            for(int i=0; i < allClassMappings.size(); i++)
            {
                if(subIndex.equals(((CompassClassMapping)allClassMappings.get(i)).getSubIndex()))
                {
                    dirType = ((CompassClassMapping)allClassMappings.get(i)).getDirType();
                    break;
                }
            }

            if(dirType == null)
            {
                return dir;    
            }
            else if(dirType.equalsIgnoreCase("ram"))
            {
                return new RAMDirectory(dir);
            }
            else  if(dirType.equalsIgnoreCase("mirror"))
            {
                return new MemoryMirrorDirectoryWrapper(dir, awaitTermination, maxNumberOfUnProcessedBytes, minNumberOfUnProcessedBytes, doCreateExecutorService());
            }
            else
            {
                return dir;
            }
        } catch (IOException e) {
            throw new SearchEngineException("Failed to wrap directory [" + dir + "] with async memory wrapper", e);
        }
    }

    protected ExecutorService doCreateExecutorService() {
        return Executors.newSingleThreadExecutor(new SingleThreadThreadFactory("AsyncMirror Directory Wrapper", false));
    }
}
