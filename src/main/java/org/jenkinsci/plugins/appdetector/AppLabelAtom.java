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

  /**
   * Serializing given label object to string.
   * @param label A label object to serialize.
   * @return Serialized string.
   */
  public static String serialize(AppLabelAtom label) {
    String serialized = label.getApplication() + ":" + label.getVersion() + ":" + label.getHome();
    return serialized;
  }

  /**
   * Deserializing given string to label object.
   * @param serialized A string to deserialize.
   * @return Deserialized label object.
   */
  public static AppLabelAtom deserialize(String serialized) {
    String[] list = serialized.split(":");
    if (list.length != 3) {
      return null;
    }
    AppLabelAtom label = new AppLabelAtom(list[0], list[1], list[2]);
    return label;
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
