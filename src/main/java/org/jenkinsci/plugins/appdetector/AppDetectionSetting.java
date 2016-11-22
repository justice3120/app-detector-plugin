package org.jenkinsci.plugins.appdetector;

public class AppDetectionSetting {
  private String appName;
  private String script;

  public AppDetectionSetting(String appName, String script) {
    this.appName = appName;
    this.script = script;
  }

  public String getAppName() {
    return appName;
  }

  public String getScript() {
    return script;
  }
}
