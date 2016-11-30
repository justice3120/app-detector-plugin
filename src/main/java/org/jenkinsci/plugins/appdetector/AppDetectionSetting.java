package org.jenkinsci.plugins.appdetector;

public class AppDetectionSetting {
  private String appName;
  private String script;
  private boolean onLinux;
  private boolean onOsx;
  private boolean onWindows;
  private String homeDirVarName;

  /**
   * Construct an AppDetectionSetting Object.
   * @param String appName Application name to be detected.
   * @param String script Groovy script used to detect applications.
   * @param boolean onLinux Whether to perform detection on Linux.
   * @param boolean onOsx Whether to perform detection on Mac.
   * @param boolean onWindows Whether to perform detection on Windows.
   * @param String homeDirVarName Environment variable name to bind the application's home directory.
   */
  public AppDetectionSetting(String appName, String script, boolean onLinux, boolean onOsx,
      boolean onWindows, String homeDirVarName) {

    this.appName = appName;
    this.script = script;
    this.onLinux = onLinux;
    this.onOsx = onOsx;
    this.onWindows = onWindows;
    this.homeDirVarName = homeDirVarName;
  }

  public String getAppName() {
    return appName;
  }

  public String getScript() {
    return script;
  }

  public boolean getOnLinux() {
    return onLinux;
  }

  public boolean getOnOsx() {
    return onOsx;
  }

  public boolean getOnWindows() {
    return onWindows;
  }

  public String getHomeDirVarName() {
    return homeDirVarName;
  }
}
