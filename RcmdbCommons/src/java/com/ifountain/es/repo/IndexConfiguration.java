package com.ifountain.es.repo;

import com.ifountain.comp.config.ConfigurationBean;

/**
 * Created by IntelliJ IDEA.
 * User: mustafa
 * Date: Nov 30, 2010
 * Time: 11:13:03 AM
 * To change this template use File | Settings | File Templates.
 */
public class IndexConfiguration extends ConfigurationBean{

    private String name;
    private int shardCount = 2;
    private int replicaCount = 0;
    
    public IndexConfiguration(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getShardCount() {
        return shardCount;
    }

    public void setShardCount(int shardCount) {
        this.shardCount = shardCount;
    }

    public int getReplicaCount() {
        return replicaCount;
    }

    public void setReplicaCount(int replicaCount) {
        this.replicaCount = replicaCount;
    }
}
