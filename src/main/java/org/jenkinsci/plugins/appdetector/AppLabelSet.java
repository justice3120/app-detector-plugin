package org.jenkinsci.plugins.appdetector;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class AppLabelSet extends HashSet<AppLabelAtom> {

  /**
   * Returns a Set of xcode version strings contained in this set.
   * @return Xcode version set.
   */
  public Set<String> getXcodeVersions() {
    Set<String> xcodeVersions = new TreeSet<String>();
    for (AppLabelAtom label: this) {
      if ("Xcode".equals(label.getApplication())) {
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
    for (AppLabelAtom label: this) {
      if ("Unity".equals(label.getApplication())) {
        unityVersions.add(label.getVersion());
      }
    }
    return unityVersions;
  }

  /**
   * Returns a application label that matches specified condition, or null if all labels not matched.
   * @param application The name of application such as "Xcode", "Unity".
   * @param version Application version.
   * @return The mached application label or null.
   */
  public AppLabelAtom getApplicationLabel(String application, String version) {
    for (AppLabelAtom label: this) {
      if (label.getApplication().equals(application) && label.getVersion().equals(version)) {
        return label;
      }
    }
    return null;
  }
}
