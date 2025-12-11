// SPDX-License-Identifier: Apache-2.0
package org.gradlex.maven.plugin.development.internal;

import org.gradle.api.logging.Logger;

public final class MavenLoggerAdapter
        implements org.codehaus.plexus.logging.Logger, org.apache.maven.plugin.logging.Log {

    private final Logger delegate;

    public MavenLoggerAdapter(Logger delegate) {
        this.delegate = delegate;
    }

    @Override
    public String getName() {
        return delegate.getName();
    }

    @Override
    public int getThreshold() {
        // not supported
        return 0;
    }

    @Override
    public void setThreshold(int threshold) {
        // not supported
    }

    @Override
    public org.codehaus.plexus.logging.Logger getChildLogger(String name) {
        // not supported
        return this;
    }

    @Override
    public boolean isDebugEnabled() {
        return delegate.isDebugEnabled();
    }

    @Override
    public void debug(CharSequence content) {
        debug(content.toString());
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
        debug(content.toString(), error);
    }

    @Override
    public void debug(Throwable error) {
        delegate.debug("", error);
    }

    @Override
    public void debug(String message) {
        delegate.debug(message);
    }

    @Override
    public void debug(String message, Throwable throwable) {
        delegate.debug(message, throwable);
    }

    @Override
    public boolean isInfoEnabled() {
        return delegate.isInfoEnabled();
    }

    @Override
    public void info(String message) {
        delegate.info(message);
    }

    @Override
    public void info(String message, Throwable throwable) {
        delegate.info(message, throwable);
    }

    @Override
    public void info(CharSequence content) {
        info(content.toString());
    }

    @Override
    public void info(CharSequence content, Throwable error) {
        info(content.toString(), error);
    }

    @Override
    public void info(Throwable error) {
        info("", error);
    }

    @Override
    public boolean isWarnEnabled() {
        return delegate.isWarnEnabled();
    }

    @Override
    public void warn(String message) {
        delegate.warn(message);
    }

    @Override
    public void warn(String message, Throwable throwable) {
        delegate.warn(message, throwable);
    }

    @Override
    public void warn(CharSequence content) {
        warn(content.toString());
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
        warn(content.toString(), error);
    }

    @Override
    public void warn(Throwable error) {
        warn("", error);
    }

    @Override
    public boolean isErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void error(String message) {
        delegate.error(message);
    }

    @Override
    public void error(String message, Throwable throwable) {
        delegate.error(message, throwable);
    }

    @Override
    public void error(CharSequence content) {
        error(content.toString());
    }

    @Override
    public void error(CharSequence content, Throwable error) {
        error(content.toString(), error);
    }

    @Override
    public void error(Throwable error) {
        error("", error);
    }

    @Override
    public boolean isFatalErrorEnabled() {
        return delegate.isErrorEnabled();
    }

    @Override
    public void fatalError(String message) {
        delegate.error(message);
    }

    @Override
    public void fatalError(String message, Throwable throwable) {
        delegate.error(message, throwable);
    }
}
