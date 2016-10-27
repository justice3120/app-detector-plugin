package org.jenkinsci.plugins.withsoftware;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class SoftwareLabelSet extends HashSet<SoftwareLabelAtom> {

  /**
   * Returns a Set of xcode version strings contained in this set.
   * @return Xcode version set.
   */
  public Set<String> getXcodeVersions() {
    Set<String> xcodeVersions = new TreeSet<String>();
    for (SoftwareLabelAtom label: this) {
      if ("Xcode".equals(label.getSoftware())) {
        xcodeVersions.add(label.getVersion());
      }
    }
    return xcodeVersions;
  }

  /**
  * Returns a Set of unity version strings contained in this set.
  * @return Unity version set.
   */
  public Set<String> getUnityVersions() {
    Set<String> unityVersions = new TreeSet<String>();
    for (SoftwareLabelAtom label: this) {
      if ("Unity".equals(label.getSoftware())) {
        unityVersions.add(label.getVersion());
      }
    }
    return unityVersions;
  }

  /**
   * Returns a software label that matches specified condition, or null if all labels not matched.
   * @param software The name of software such as "Xcode", "Unity".
   * @param version Software version.
   * @return The mached software label or null.
   */
  public SoftwareLabelAtom getSoftwareLabel(String software, String version) {
    for (SoftwareLabelAtom label: this) {
      if (label.getSoftware().equals(software) && label.getVersion().equals(version)) {
        return label;
      }
    }
    return null;
  }
}
