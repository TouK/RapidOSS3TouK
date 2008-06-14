package com.ifountain.rcmdb.domain.method
/**
 * Created by IntelliJ IDEA.
 * User: mustafa seker
 * Date: Jun 14, 2008
 * Time: 4:37:27 AM
 * To change this template use File | Settings | File Templates.
 */
class EventTriggeringUtils {
    static final String ONLOAD_EVENT = 'onLoad'
    static final String BEFORE_INSERT_EVENT = 'beforeInsert'
    static final String BEFORE_UPDATE_EVENT = 'beforeUpdate'
    static final String BEFORE_DELETE_EVENT = 'beforeDelete'
    public static void triggerEvent(entity, event) {
        if(entity.metaClass.hasProperty(entity, event)) {
             def callable = entity."$event"
             callable.delegate = entity
             callable.call()
         }
    }
}