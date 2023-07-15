package org.apache.commons.logging.impl;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogConfigurationException;
import org.apache.commons.logging.LogFactory;

import java.util.Hashtable;

public class LogFactoryImpl extends LogFactory {
    protected Hashtable<String, Object> attributes = new Hashtable<>();
    protected Hashtable<String, MiraiLogger> instances = new Hashtable<>();

    public Object getAttribute(String name) {
        return this.attributes.get(name);
    }

    public String[] getAttributeNames() {
        return this.attributes.keySet().toArray(new String[0]);
    }

    @Override
    public Log getInstance(Class aClass) throws LogConfigurationException {
        String name = aClass.getName();
        MiraiLogger logger = instances.get(name);
        if (logger == null) {
            logger = new MiraiLogger(aClass);
            instances.put(name, logger);
        }
        return logger;
    }

    @Override
    public Log getInstance(String s) throws LogConfigurationException {
        MiraiLogger logger = instances.get(s);
        if (logger == null) {
            logger = new MiraiLogger(this.getClass(), s);
            instances.put(s, logger);
        }
        return logger;
    }

    public void release() {
        this.instances.clear();
    }

    public void removeAttribute(String name) {
        this.attributes.remove(name);
    }

    public void setAttribute(String name, Object value) {
        if (value == null) {
            this.attributes.remove(name);
        } else {
            this.attributes.put(name, value);
        }
    }
}
