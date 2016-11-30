package org.jenkinsci.plugins.appdetector;

import hudson.model.labels.LabelAtom;

public class AppLabelAtom extends LabelAtom {

  private static final long serialVersionUID = 1L;

  private String application;
  private String version;
  private String home;

  /**
   * Creates new {@link AppLabelAtom} instance.
   * @param application The name of application such as "Xcode", "Unity".
   * @param version Application version.
   * @param home Application home directory.
   */
  public AppLabelAtom(String application, String version, String home) {
    super(application + "-" + version);
    this.application = application;
    this.version = version;
    this.home = home;
  }

  public String getApplication() {
    return application;
  }

  public String getVersion() {
    return version;
  }

  public String getHome() {
    return home;
  }
}
