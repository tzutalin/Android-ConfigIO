# Android-ConfigIO

A small and encapsulation library for creating, accessing, and modifying configuration file with Xml and Json format

[![Build Status](https://travis-ci.org/tzutalin/Android-ConfigIO.png)](https://travis-ci.org/tzutalin/Android-ConfigIO)
[ ![Download](https://api.bintray.com/packages/tzutalin/maven/Android-ConfigIO/images/download.svg) ](https://bintray.com/tzutalin/maven/Android-ConfigIO/_latestVersion)

### Features

* Read/Write configruation with json format

* Read/Write configuration with xml format

* Rx support

## Usage

### Binary / Import to your build.gradle
```
	repositories {
		maven {
			url 'https://dl.bintray.com/tzutalin/maven'
		}
	}

	dependencies {
		compile 'com.tzutalin.configio:configio:1.0.3'
	}
```

### Use in code

If you would like to read and write in external storage, you need to decalre permissions in Manifest:

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

Sample

```java
// Assign the target path
File sdcard = Environment.getExternalStorageDirectory();
String targetPath = sdcard.getAbsolutePath() + File.separator + "config.json";

// Instantiate ConfigIO
ConfigIO configIO = ConfigIO.newInstance(targetPath);
// === Write ===
ConfigIO.Writer writer = configIO.getWriter();
writer.putString("test_str", "12345678")
      .putBoolean("test_bool", true)
      .putInt("test_int", 10)
      .putFloat("test_float", 0.5f)
      .putLong("test_long", 100000000L);
// Blocking method. You can use writer.apply() to save it async
writer.commit();

// === Read ====
// It will load config from the file
configIO.loadFromFile();
String test_str = configIO.getString("test_str", "default_str");
boolean test_bool = configIO.getBoolean("test_bool", false);
int test_int = configIO.getInt("test_int", 0);
float test_float = configIO.getFloat("test_float", 0);
long test_long = configIO.getLong("test_long", 0);
```

Result: It will save to /sdcard/config.json
```json
{
   "test_str":"12345678",
   "test_int":10,
   "test_long":100000000,
   "test_bool":true,
   "test_float":0.5,
}
```

Rx support
```
String targetPath = sdcard.getAbsolutePath() + File.separator + "config.xml";
final ConfigIO configIO = ConfigIO.newInstance(targetPath);
// Using RxJava to subscribe on io thread
configIO.loadFromFileWithRx().subscribeOn(Schedulers.io()).subscribe(new Action1<Boolean>() {
    @Override
    public void call(Boolean isSuccess) {
         String test_str = configIO.getString("test_str", "default_str");
         boolean test_bool = configIO.getBoolean("test_bool", false);
         int test_int = configIO.getInt("test_int", 0);
         float test_float = configIO.getFloat("test_float", 0);
         long test_long = configIO.getLong("test_long", 0);
    }
 });

```

For more example, you can check the [sample code](https://github.com/tzutalin/Android-ConfigIO/blob/master/app/src/main/java/com/tzutalin/example/MainActivity.java#L67)

## LICNESE
Copyright 2016 Tzutalin

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

   http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
