package org.jenkinsci.plugins.withsoftware;

import hudson.model.labels.LabelAtom;

public class SoftwareLabelAtom extends LabelAtom {

  private static final long serialVersionUID = 1L;

  private String software;
  private String version;
  private String home;

  public SoftwareLabelAtom(String software, String version, String home) {
    super(software + "-" + version);
    this.software = software;
    this.version = version;
    this.home = home;
  }

  public static String serialize(SoftwareLabelAtom label) {
    String serialized = label.getSoftware() + ":" + label.getVersion() + ":" + label.getHome();
    return serialized;
  }

  public static SoftwareLabelAtom deserialize(String serialized) {
    String[] list = serialized.split(":");
    if (list.length != 3) {
      return null;
    }
    SoftwareLabelAtom label = new SoftwareLabelAtom(list[0], list[1], list[2]);
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
