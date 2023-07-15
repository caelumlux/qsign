package org.apache.commons.logging.impl;

import org.apache.commons.logging.Log;

public class MiraiLogger implements Log {
    private final net.mamoe.mirai.utils.MiraiLogger logger;
    public MiraiLogger(Class<?> clazz) {
        logger = net.mamoe.mirai.utils.MiraiLogger.Factory.INSTANCE.create(clazz);
    }
    public MiraiLogger(Class<?> clazz, String identify) {
        logger = net.mamoe.mirai.utils.MiraiLogger.Factory.INSTANCE.create(clazz, identify);
    }

    @Override
    public void debug(Object o) {
        logger.debug(String.valueOf(o));
    }

    @Override
    public void debug(Object o, Throwable throwable) {
        logger.debug(String.valueOf(o), throwable);
    }

    @Override
    public void error(Object o) {
        logger.error(String.valueOf(o));
    }

    @Override
    public void error(Object o, Throwable throwable) {
        logger.error(String.valueOf(o), throwable);
    }

    @Override
    public void fatal(Object o) {
        logger.error(String.valueOf(o));
    }

    @Override
    public void fatal(Object o, Throwable throwable) {
        logger.error(String.valueOf(o), throwable);
    }

    @Override
    public void info(Object o) {
        logger.info(String.valueOf(o));
    }

    @Override
    public void info(Object o, Throwable throwable) {
        logger.info(String.valueOf(o), throwable);
    }

    @Override
    public boolean isDebugEnabled() {
        return logger.isDebugEnabled();
    }

    @Override
    public boolean isErrorEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isFatalEnabled() {
        return logger.isErrorEnabled();
    }

    @Override
    public boolean isInfoEnabled() {
        return logger.isInfoEnabled();
    }

    @Override
    public boolean isTraceEnabled() {
        return logger.isVerboseEnabled();
    }

    @Override
    public boolean isWarnEnabled() {
        return logger.isWarningEnabled();
    }

    @Override
    public void trace(Object o) {
        logger.verbose(String.valueOf(o));
    }

    @Override
    public void trace(Object o, Throwable throwable) {
        logger.verbose(String.valueOf(o), throwable);
    }

    @Override
    public void warn(Object o) {
        logger.warning(String.valueOf(o));
    }

    @Override
    public void warn(Object o, Throwable throwable) {
        logger.warning(String.valueOf(o), throwable);
    }
}
