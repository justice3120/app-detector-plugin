package org.jenkinsci.plugins.appdetector;

import hudson.model.labels.LabelAtom;

public class AppLabelAtom extends LabelAtom {

  private static final long serialVersionUID = 1L;

  private String software;
  private String version;
  private String home;

  /**
   * Creates new {@link AppLabelAtom} instance.
   * @param software The name of software such as "Xcode", "Unity".
   * @param version Software version.
   * @param home Software home directory.
   */
  public AppLabelAtom(String software, String version, String home) {
    super(software + "-" + version);
    this.software = software;
    this.version = version;
    this.home = home;
  }

  /**
   * Serializing given label object to string.
   * @param label A label object to serialize.
   * @return Serialized string.
   */
  public static String serialize(AppLabelAtom label) {
    String serialized = label.getSoftware() + ":" + label.getVersion() + ":" + label.getHome();
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

  public String getSoftware() {
    return software;
  }

  public String getVersion() {
    return version;
  }

  public String getHome() {
    return home;
  }
}
