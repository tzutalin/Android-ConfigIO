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
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by darrenl on 2016/4/15.
 * Package only
 */
class JsonConfig extends ConfigIO implements ConfigIO.Writer {
    private static final String TAG = JsonConfig.class.getSimpleName();

    private JsonConfig() {
        super();
    }

    public JsonConfig(String path) {
        super(path);
    }

    @Override
    public ConfigIO.Writer putString(String key, @Nullable String value) {
        mMap.put(key, value);
        return this;
    }


    @Override
    public boolean loadFromFile() {
        if (TextUtils.isEmpty(mTargetPath)) {
            throw new IllegalAccessError("Empty file path ");
        }

        try {
            File f = new File(mTargetPath);
            FileInputStream is = new FileInputStream(f);
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            String response = new String(buffer);
            JSONObject jsonObj = new JSONObject(response);
            Map map = toMap(jsonObj);
            mMap.putAll(map);
            // Print log
            dumpMap();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    @Override
    public Writer getWriter() {
        return this;
    }


    @Override
    public ConfigIO.Writer putStringSet(String key, @Nullable Set<String> values) {
        mMap.put(key, values);
        return this;
    }

    @Override
    public ConfigIO.Writer putInt(String key, int value) {
        mMap.put(key, value);
        return this;
    }

    @Override
    public ConfigIO.Writer putLong(String key, long value) {
        mMap.put(key, value);
        return this;
    }

    @Override
    public ConfigIO.Writer putFloat(String key, float value) {
        mMap.put(key, value);
        return this;
    }

    @Override
    public ConfigIO.Writer putBoolean(String key, boolean value) {
        mMap.put(key, value);
        return this;
    }

    @Override
    public ConfigIO.Writer remove(String key) {
        mMap.remove(key);
        return this;
    }

    @Override
    public ConfigIO.Writer clear() {
        mMap.clear();
        return this;
    }

    @Override
    public boolean commit() {
        if (mMap != null && mMap.size() != 0) {
            JSONObject obj = new JSONObject(mMap);
            return save(mTargetPath, obj.toString());
        }
        return false;
    }

    @Override
    public void apply() {
        JSONObject obj = new JSONObject(mMap);
        save(mTargetPath, obj.toString());
    }

    private Map<String, Object> jsonToMap(JSONObject json) throws JSONException {
        Map<String, Object> retMap = new HashMap<String, Object>();

        if (json != JSONObject.NULL) {
            retMap = toMap(json);
        }
        return retMap;
    }

    private Map<String, Object> toMap(JSONObject object) throws JSONException {
        Map<String, Object> map = new HashMap<String, Object>();

        Iterator<String> keysItr = object.keys();
        while (keysItr.hasNext()) {
            String key = keysItr.next();
            Object value = object.get(key);

            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            map.put(key, value);
        }
        return map;
    }

    private List<Object> toList(JSONArray array) throws JSONException {
        List<Object> list = new ArrayList<Object>();
        for (int i = 0; i < array.length(); i++) {
            Object value = array.get(i);
            if (value instanceof JSONArray) {
                value = toList((JSONArray) value);
            } else if (value instanceof JSONObject) {
                value = toMap((JSONObject) value);
            }
            list.add(value);
        }
        return list;
    }

    private boolean save(String path, String jsonStr) {
        Log.d(TAG, "save : " + path + " json:" + jsonStr);
        try {
            FileWriter file = new FileWriter(path);
            file.write(jsonStr);
            file.flush();
            file.close();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

}
