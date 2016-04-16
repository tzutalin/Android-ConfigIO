/*
*  Copyright (C) 2016 TzuTaLin
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

package com.tzutalin.configio;

import android.support.annotation.Nullable;

import java.util.Set;

/**
 * Created by darrenl on 2016/4/16.
 */
public class XmlConfig extends ConfigIO implements ConfigIO.Writer {
    @Override
    public boolean loadFromFile() {
        return false;
    }

    @Override
    public Writer getWriter() {
        return null;
    }

    @Override
    public Writer putString(String key, @Nullable String value) {
        return null;
    }

    @Override
    public Writer putStringSet(String key, @Nullable Set<String> values) {
        return null;
    }

    @Override
    public Writer putInt(String key, int value) {
        return null;
    }

    @Override
    public Writer putLong(String key, long value) {
        return null;
    }

    @Override
    public Writer putFloat(String key, float value) {
        return null;
    }

    @Override
    public Writer putBoolean(String key, boolean value) {
        return null;
    }

    @Override
    public Writer remove(String key) {
        return null;
    }

    @Override
    public Writer clear() {
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
