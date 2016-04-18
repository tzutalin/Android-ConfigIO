# Android-ConfigIO

A small library for creating, accessing, and modifying configuration file with Xml and Json format

### Features

* Read/Write configruation with json format

* Read/Write configuration with xml format

* Support Rxjava (Not done)

### Usage
```java
File sdcard = Environment.getExternalStorageDirectory();
String targetPath = sdcard.getAbsolutePath() + File.separator + "config.json";

ConfigIO configIO = ConfigIO.newInstance(targetPath);
// === Write ===
ConfigIO.Writer writer = configIO.getWriter();
writer.putString("test_str", "12345678");
writer.putBoolean("test_bool", true);
writer.putInt("test_int", 10);
writer.putFloat("test_float", 0.5f);
writer.putLong("test_long", 100000000L);
// Blocking method
writer.commit();
// writer.apply() will save file async
//writer.apply();

// === Read ====
// It will load config from the file
configIO.loadFromFile();
String test_str = configIO.getString("test_str", "default_str");
boolean test_bool = configIO.getBoolean("test_bool", false);
int test_int = configIO.getInt("test_int", 0);
float test_float = configIO.getFloat("test_float", 0);
long test_long = configIO.getLong("test_long", 0);

// It will load config from the file
configIO.loadFromFile();
String test_str = configIO.getString("test_str", "default_str");
boolean test_bool = configIO.getBoolean("test_bool", false);
float test_float = configIO.getFloat("test_float", 0);
long test_long = configIO.getLong("test_long", 0);

```

Save as /sdcard/config.json
```json
{
   "test_str":"12345678",
   "test_int":10,
   "test_long":100000000,
   "test_bool":true,
   "test_float":0.5,
}
```

If you would like to read and write in external storage:

    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>

    <uses-permission android:name="android.permission.READ_EXTERNAL_STORAGE"/>

### Binary
```
	repositories {
		maven {
			url 'https://dl.bintray.com/tzutalin/maven'
		}
	}

	dependencies {
		compile 'com.tzutalin.configio:configio:1.0.2'
	}
```

### LICNESE
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
