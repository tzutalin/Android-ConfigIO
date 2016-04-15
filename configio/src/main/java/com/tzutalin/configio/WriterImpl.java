package com.tzutalin.configio;

import android.support.annotation.Nullable;

import java.util.Set;

/**
 * Created by darrenl on 2016/4/15.
 * Package only
 */
class WriterImpl implements ConfigIO.Writer {
    @Override
    public ConfigIO.Writer putString(String key, @Nullable String value) {
        return null;
    }

    @Override
    public ConfigIO.Writer putStringSet(String key, @Nullable Set<String> values) {
        return null;
    }

    @Override
    public ConfigIO.Writer putInt(String key, int value) {
        return null;
    }

    @Override
    public ConfigIO.Writer putLong(String key, long value) {
        return null;
    }

    @Override
    public ConfigIO.Writer putFloat(String key, float value) {
        return null;
    }

    @Override
    public ConfigIO.Writer putBoolean(String key, boolean value) {
        return null;
    }

    @Override
    public ConfigIO.Writer remove(String key) {
        return null;
    }

    @Override
    public ConfigIO.Writer clear() {
        return null;
    }

    @Override
    public boolean commit() {
        return false;
    }

    @Override
    public void apply() {

    }
}
