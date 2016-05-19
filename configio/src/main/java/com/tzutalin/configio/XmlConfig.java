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

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Xml;

import com.tzutalin.configio.utils.FastXmlSerializer;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Created by darrenl on 2016/4/16.
 */

/**
 * {@hide}
 */
class XmlConfig extends ConfigIO implements ConfigIO.Writer {
    private static final String TAG = XmlConfig.class.getSimpleName();
    private Set<String> mDeleteKeySet = new HashSet<>();
    private boolean mbLoadToMemory = false;
    private Thread mThread;

    public XmlConfig(String path) {
        super(path);
    }

    @Override
    public boolean loadFromFile() {
        // If it has loaed to memory, return true directly
        if (mbLoadToMemory == true) {
            return true;
        }

        if (TextUtils.isEmpty(mTargetPath)) {
            throw new IllegalAccessError("Empty file path");
        }

        if (new File(mTargetPath).exists()) {
            File file = new File(mTargetPath);
            if (file.canRead()) {
                BufferedInputStream str = null;
                try {
                    str = new BufferedInputStream(  new FileInputStream(file), 16 * 1024);
                    Map map = readMapXml(str);
                    map.putAll(mMap);
                    mMap = map;
                    // Print log
                    dumpMap();
                    mbLoadToMemory = true;
                } catch (XmlPullParserException e) {
                    Log.w(TAG, "getSharedPreferences", e);
                } catch (FileNotFoundException e) {
                    Log.w(TAG, "getSharedPreferences", e);
                } catch (IOException e) {
                    Log.w(TAG, "getSharedPreferences", e);
                } finally {
                    try {
                        str.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        return mbLoadToMemory;
    }

    @Override
    public Writer getWriter() {
        return this;
    }

    @Override
    public ConfigIO.Writer putString(@NonNull String key, @Nullable String value) {
        mMap.put(key, value);
        return this;
    }


    //@Override
    public ConfigIO.Writer putStringSet(@NonNull String key, @Nullable Set<String> values) {
        mMap.put(key, values);
        return this;
    }

    @Override
    public ConfigIO.Writer putInt(@NonNull String key, int value) {
        mMap.put(key, value);
        return this;
    }

    @Override
    public ConfigIO.Writer putLong(@NonNull String key, long value) {
        mMap.put(key, value);
        return this;
    }

    @Override
    public ConfigIO.Writer putFloat(@NonNull String key, float value) {
        mMap.put(key, value);
        return this;
    }

    @Override
    public ConfigIO.Writer putDouble(@NonNull String key, double value) {
        mMap.put(key, value);
        return this;
    }

    @Override
    public ConfigIO.Writer putBoolean(@NonNull String key, boolean value) {
        mMap.put(key, value);
        return this;
    }

    @Override
    public ConfigIO.Writer remove(@NonNull String key) {
        mMap.remove(key);

        // Add to delete set when the user didn't call loadFromFile first
        if (mbLoadToMemory == false) {
            mDeleteKeySet.add(key);
        }
        return this;
    }

    @Override
    public ConfigIO.Writer clear() {
        mMap.clear();
        if (mbLoadToMemory == false) {
            mDeleteKeySet.addAll(mMap.keySet());
        }
        return this;
    }

    @Override
    public boolean commit() {
        return save();
    }

    @Override
    public void apply() {
        // TODO: Should be better
        // Use only one thread to do it even call apply() many times
        if (mThread == null) {
            synchronized (XmlConfig.this) {
                mThread = new Thread(new Runnable() {
                    @Override
                    public void run() {
                        save();

                        synchronized (XmlConfig.this) {
                            mThread = null;
                        }
                    }
                });
            }
            mThread.start();
        }
    }

    private boolean save() {
        // If  the file exists, load it first
        if (new File(mTargetPath).exists()) {
            loadFromFile();
        }

        // Delete keys if the user removes keys but didn't call loadFromFile first
        if (mMap != null && mMap.size() != 0 && mDeleteKeySet.size() != 0) {
            for (String key : mDeleteKeySet) {
                mMap.remove(key);
            }
        }

        // Save to target path
        if (mMap != null && mMap.size() != 0) {
            Log.d(TAG, "save : " + mTargetPath);
            FileOutputStream str = null;
            File file = new File(mTargetPath);
            try {
                str = new FileOutputStream(file);
                writeMapXml(mMap, str);
                return true;
            } catch (XmlPullParserException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (str != null) {
                    try {
                        str.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }

    /**
     * Flatten a Map into an output stream of XML file.
     *
     * @param val The map to be flattened.
     * @param out Where to write the XML data.
     */
    private static final void writeMapXml(Map val, OutputStream out)
            throws XmlPullParserException, java.io.IOException {
        XmlSerializer serializer = new FastXmlSerializer();
        serializer.setOutput(out, StandardCharsets.UTF_8.name());
        serializer.startDocument(null, true);
        serializer.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);
        writeMapXml(val, serializer);
        serializer.endDocument();
    }

    private static final void writeMapXml(Map val, XmlSerializer out) throws XmlPullParserException, java.io.IOException {

        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }

        out.startTag(null, "map");

        Set s = val.entrySet();
        Iterator i = s.iterator();

        while (i.hasNext()) {
            Map.Entry e = (Map.Entry) i.next();
            writeValueXml(e.getValue(), (String) e.getKey(), out);
        }

        out.endTag(null, "map");
    }


    private static final void writeValueXml(Object v, String name, XmlSerializer out) throws XmlPullParserException, java.io.IOException {
        String typeStr;
        if (v == null) {
            out.startTag(null, "null");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.endTag(null, "null");
            return;
        } else if (v instanceof String) {
            out.startTag(null, "string");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.text(v.toString());
            out.endTag(null, "string");
            return;
        } else if (v instanceof Integer) {
            typeStr = "int";
        } else if (v instanceof Long) {
            typeStr = "long";
        } else if (v instanceof Float) {
            typeStr = "float";
        } else if (v instanceof Double) {
            typeStr = "double";
        } else if (v instanceof Boolean) {
            typeStr = "boolean";
        } else if (v instanceof String[]) {
            writeStringArrayXml((String[]) v, name, out);
            return;
        } else if (v instanceof CharSequence) {
            // XXX This is to allow us to at least write something if
            // we encounter styled text...  but it means we will drop all
            // of the styling information. :(
            out.startTag(null, "string");
            if (name != null) {
                out.attribute(null, "name", name);
            }
            out.text(v.toString());
            out.endTag(null, "string");
            return;
        } else {
            throw new RuntimeException("writeValueXml: unable to write value " + v);
        }

        out.startTag(null, typeStr);
        if (name != null) {
            out.attribute(null, "name", name);
        }
        out.attribute(null, "value", v.toString());
        out.endTag(null, typeStr);
    }

    private static void writeStringArrayXml(String[] val, String name, XmlSerializer out) throws IOException {

        if (val == null) {
            out.startTag(null, "null");
            out.endTag(null, "null");
            return;
        }

        out.startTag(null, "string-array");
        if (name != null) {
            out.attribute(null, "name", name);
        }

        final int N = val.length;
        out.attribute(null, "num", Integer.toString(N));

        for (int i = 0; i < N; i++) {
            out.startTag(null, "item");
            out.attribute(null, "value", val[i]);
            out.endTag(null, "item");
        }

        out.endTag(null, "string-array");
    }


    @SuppressWarnings("unchecked")
    private static final HashMap<String, ?> readMapXml(InputStream in)
            throws XmlPullParserException, java.io.IOException {
        XmlPullParser parser = Xml.newPullParser();
        parser.setInput(in, StandardCharsets.UTF_8.name());
        return (HashMap<String, ?>) readValueXml(parser, new String[1]);
    }

    private static final Object readValueXml(XmlPullParser parser, String[] name)
            throws XmlPullParserException, java.io.IOException {
        int eventType = parser.getEventType();
        do {
            if (eventType == parser.START_TAG) {
                return readThisValueXml(parser, name, false);
            } else if (eventType == parser.END_TAG) {
                throw new XmlPullParserException(
                        "Unexpected end tag at: " + parser.getName());
            } else if (eventType == parser.TEXT) {
                throw new XmlPullParserException(
                        "Unexpected text: " + parser.getText());
            }
            eventType = parser.next();
        } while (eventType != parser.END_DOCUMENT);

        throw new XmlPullParserException(
                "Unexpected end of document");
    }

    private static final Object readThisValueXml(XmlPullParser parser, String[] name, boolean arrayMap)
            throws XmlPullParserException, java.io.IOException {
        final String valueName = parser.getAttributeValue(null, "name");
        final String tagName = parser.getName();

        Object res;

        if (tagName.equals("null")) {
            res = null;
        } else if (tagName.equals("string")) {
            String value = "";
            int eventType;
            while ((eventType = parser.next()) != parser.END_DOCUMENT) {
                if (eventType == parser.END_TAG) {
                    if (parser.getName().equals("string")) {
                        name[0] = valueName;
                        //System.out.println("Returning value for " + valueName + ": " + value);
                        return value;
                    }
                    throw new XmlPullParserException(
                            "Unexpected end tag in <string>: " + parser.getName());
                } else if (eventType == parser.TEXT) {
                    value += parser.getText();
                } else if (eventType == parser.START_TAG) {
                    throw new XmlPullParserException(
                            "Unexpected start tag in <string>: " + parser.getName());
                }
            }
            throw new XmlPullParserException(
                    "Unexpected end of document in <string>");
        } else if ((res = readThisPrimitiveValueXml(parser, tagName)) != null) {
            // Ok
        } else if (tagName.equals("string-array")) {
            res = readThisStringArrayXml(parser, "string-array", name);
            name[0] = valueName;
            return res;
        } else if (tagName.equals("map")) {
            parser.next();
            res = arrayMap
                    ? readThisArrayMapXml(parser, "map", name)
                    : readThisMapXml(parser, "map", name);
            name[0] = valueName;
            return res;
        } else {
            throw new XmlPullParserException("Unknown tag: " + tagName);
        }

        // Skip through to end tag.
        int eventType;
        while ((eventType = parser.next()) != parser.END_DOCUMENT) {
            if (eventType == parser.END_TAG) {
                if (parser.getName().equals(tagName)) {
                    name[0] = valueName;
                    //System.out.println("Returning value for " + valueName + ": " + res);
                    return res;
                }
                throw new XmlPullParserException(
                        "Unexpected end tag in <" + tagName + ">: " + parser.getName());
            } else if (eventType == parser.TEXT) {
                throw new XmlPullParserException(
                        "Unexpected text in <" + tagName + ">: " + parser.getName());
            } else if (eventType == parser.START_TAG) {
                throw new XmlPullParserException(
                        "Unexpected start tag in <" + tagName + ">: " + parser.getName());
            }
        }
        throw new XmlPullParserException(
                "Unexpected end of document in <" + tagName + ">");
    }

    private static final Object readThisPrimitiveValueXml(XmlPullParser parser, String tagName)
            throws XmlPullParserException, java.io.IOException {
        try {
            if (tagName.equals("int")) {
                return Integer.parseInt(parser.getAttributeValue(null, "value"));
            } else if (tagName.equals("long")) {
                return Long.valueOf(parser.getAttributeValue(null, "value"));
            } else if (tagName.equals("float")) {
                return new Float(parser.getAttributeValue(null, "value"));
            } else if (tagName.equals("double")) {
                return new Double(parser.getAttributeValue(null, "value"));
            } else if (tagName.equals("boolean")) {
                return Boolean.valueOf(parser.getAttributeValue(null, "value"));
            } else {
                return null;
            }
        } catch (NullPointerException e) {
            throw new XmlPullParserException("Need value attribute in <" + tagName + ">");
        } catch (NumberFormatException e) {
            throw new XmlPullParserException(
                    "Not a number in value attribute in <" + tagName + ">");
        }
    }

    private static final String[] readThisStringArrayXml(XmlPullParser parser, String endTag,
                                                         String[] name) throws XmlPullParserException, java.io.IOException {

        int num;
        try {
            num = Integer.parseInt(parser.getAttributeValue(null, "num"));
        } catch (NullPointerException e) {
            throw new XmlPullParserException("Need num attribute in string-array");
        } catch (NumberFormatException e) {
            throw new XmlPullParserException("Not a number in num attribute in string-array");
        }
        parser.next();

        String[] array = new String[num];
        int i = 0;

        int eventType = parser.getEventType();
        do {
            if (eventType == parser.START_TAG) {
                if (parser.getName().equals("item")) {
                    try {
                        array[i] = parser.getAttributeValue(null, "value");
                    } catch (NullPointerException e) {
                        throw new XmlPullParserException("Need value attribute in item");
                    } catch (NumberFormatException e) {
                        throw new XmlPullParserException("Not a number in value attribute in item");
                    }
                } else {
                    throw new XmlPullParserException("Expected item tag at: " + parser.getName());
                }
            } else if (eventType == parser.END_TAG) {
                if (parser.getName().equals(endTag)) {
                    return array;
                } else if (parser.getName().equals("item")) {
                    i++;
                } else {
                    throw new XmlPullParserException("Expected " + endTag + " end tag at: " +
                            parser.getName());
                }
            }
            eventType = parser.next();
        } while (eventType != parser.END_DOCUMENT);

        throw new XmlPullParserException("Document ended before " + endTag + " end tag");
    }

    private static final ArrayMap<String, ?> readThisArrayMapXml(XmlPullParser parser, String endTag,
                                                                 String[] name)
            throws XmlPullParserException, java.io.IOException {
        ArrayMap<String, Object> map = new ArrayMap<>();

        int eventType = parser.getEventType();
        do {
            if (eventType == parser.START_TAG) {
                Object val = readThisValueXml(parser, name, true);
                map.put(name[0], val);
            } else if (eventType == parser.END_TAG) {
                if (parser.getName().equals(endTag)) {
                    return map;
                }
                throw new XmlPullParserException(
                        "Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != parser.END_DOCUMENT);

        throw new XmlPullParserException(
                "Document ended before " + endTag + " end tag");
    }


    public static final HashMap<String, ?> readThisMapXml(XmlPullParser parser, String endTag,
                                                          String[] name)
            throws XmlPullParserException, java.io.IOException {
        HashMap<String, Object> map = new HashMap<String, Object>();

        int eventType = parser.getEventType();
        do {
            if (eventType == parser.START_TAG) {
                Object val = readThisValueXml(parser, name, false);
                map.put(name[0], val);
            } else if (eventType == parser.END_TAG) {
                if (parser.getName().equals(endTag)) {
                    return map;
                }
                throw new XmlPullParserException(
                        "Expected " + endTag + " end tag at: " + parser.getName());
            }
            eventType = parser.next();
        } while (eventType != parser.END_DOCUMENT);

        throw new XmlPullParserException(
                "Document ended before " + endTag + " end tag");
    }

}
