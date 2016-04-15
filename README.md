# Android-ConfigIO

Not done

### Usage
```java
        File sdcard = Environment.getExternalStorageDirectory();
        String targetPath = sdcard.getAbsolutePath() + File.separator + "config.json";

        ConfigIO configIO = new ConfigIO(getApplicationContext());
        configIO.init(targetPath);

        // Write
        ConfigIO.Writer writer = configIO.getWriter();
        writer.putString("test", "123");
        writer.putBoolean("test2", true);

        // Read

        String test_str = configIO.getString("test", "default_str");
        boolean test_bool = configIO.getBoolean("test2", false);
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