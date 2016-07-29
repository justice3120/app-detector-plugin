package org.jenkinsci.plugins.withsoftware;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class SoftwareLabelSet extends HashSet<SoftwareLabelAtom> {

  public Set<String> getXcodeVersions() {
    Set<String> xcodeVersions = new TreeSet<String>();
    for (SoftwareLabelAtom label: this) {
      if ("Xcode".equals(label.getSoftware())) {
        xcodeVersions.add(label.getVersion());
      }
    }
    return xcodeVersions;
  }

  public Set<String> getUnityVersions() {
    Set<String> unityVersions = new TreeSet<String>();
    for (SoftwareLabelAtom label: this) {
      if ("Unity".equals(label.getSoftware())) {
        unityVersions.add(label.getVersion());
      }
    }
    return unityVersions;
  }

  public SoftwareLabelAtom getSoftwareLabel(String software, String version) {
    for (SoftwareLabelAtom label: this) {
      if (label.getSoftware().equals(software) && label.getVersion().equals(version)) {
        return label;
      }
    }
    return null;
  }
}
