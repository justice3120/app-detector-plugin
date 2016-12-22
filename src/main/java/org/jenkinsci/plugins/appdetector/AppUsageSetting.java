package org.jenkinsci.plugins.appdetector;

public class AppUsageSetting {
  private String appName;
  private String version;

  public AppUsageSetting(String appName, String version) {
    this.appName = appName;
    this.version = version;
  }

  public String getAppName() {
    return appName;
  }

  public String getVersion() {
    return version;
  }
}
