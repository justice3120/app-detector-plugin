Application Detector Plugin
====================

This plugin provides the following functions.

 - Detects an application and its versions installed in slaves, by using a groovy script.
 - Enables you to execute builds by specifying the detected application and its version.

## Usage
First, You need to register the detection setting on the Jenkins global setting page, as follows.

![](/readme/global_config.png)

And restart Jenkins, or reconnect slaves to reflect the detection setting.

**NOTE: Detection setting is not reflected until disconnecting the node and connecting it again.**

Then, you can specfiy some applications and versions at job setting.
![](/readme/job_config.png)

Or, Select it runtime by using "Choice Application Version" build parameter.
![](/readme/build_parameter.png)


### About Detection Script
- Script MUST return a JSON string of the form:
```json
[
    {
      "version": "Application Version",
      "home": "Home Directory of This Version"
    },
]
```
- You can use the '[cmd, arg1, arg2 ...].execute().text' method to execute external commands and get output.  
- And you can use the 'platform' variable to determine the platform in the script. ("windows", "linux", or "osx" will be stored)

#### Sample
Here is some sample scripts.

- [Unity](/src/main/webapp/examples/Unity.groovy)
- [Unity(on Windows)](/src/main/webapp/examples/UnityOnWindows.groovy)
- [Xcode](/src/main/webapp/examples/Xcode.groovy)

## Licence

[MIT](https://github.com/tcnksm/tool/blob/master/LICENCE)

## Author

[justice3120](https://github.com/justice3120)
