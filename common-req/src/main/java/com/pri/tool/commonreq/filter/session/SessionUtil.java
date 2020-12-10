package com.pri.tool.commonreq.filter.session;

import com.pri.tool.commonreq.filter.bean.PublicParam;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * @author wang.song
 * @date 2020-12-09 18:12
 * @Desc
 */
public class SessionUtil {
    /**
     * 专用
     */
    /**
     * 一个线程Map，用来存放线程和其对应的变量副本
     */
    private static Map threadMap = Collections.synchronizedMap(new HashMap());

    public static void set(Object object) {
        if (null == object) {
            return;
        }
        threadMap.put(Thread.currentThread(), object);
    }

    /**
     * 静态同步对象，用于同步块创建
     */
    public static Object synObject = new Object();

    public static PublicParam get() {
        Thread currentThread = Thread.currentThread();
        Object obj = threadMap.get(currentThread);
        synchronized (synObject) {
            if (obj == null && !threadMap.containsKey(currentThread)) {
                obj = initialValue();
                threadMap.put(currentThread, obj);
            }
        }
        PublicParam publicParam = (PublicParam) obj;
        return publicParam;
    }

    public static void remove() {
        threadMap.remove(Thread.currentThread());
    }

    private static Object initialValue() {
        return null;
    }
}
