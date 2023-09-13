package com.ipharmacare.collect.web.support;


/**
 * 上下文处理基类
 */
public class BaseContextHandler {
    public static final InheritableThreadLocal tokenInfoThreadLocal = new InheritableThreadLocal();

    public static void set(Long uid) {
        tokenInfoThreadLocal.set(uid);
    }

    public static Long get() {
        return (Long) tokenInfoThreadLocal.get();
    }

    public static Long getUid() {
        return get();

    }

    public static void remove() {
        tokenInfoThreadLocal.remove();
    }

}

