package org.jenkinsci.plugins.appdetector;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;

public class AppLabelSet extends HashSet<AppLabelAtom> {

  /**
   * Returns a Set of the app name strings contained in this set.
   * @return app name set.
   */
  public Set<String> getAppNames() {
    Set<String> appNames = new TreeSet<String>();
    for (AppLabelAtom label: this) {
      appNames.add(label.getApplication());
    }
    return appNames;
  }

  /**
   * Returns a Set of the app version strings contained in this set.
   * @param appName The application name for which you want to obtain the version list.
   * @return app version set.
   */
  public Set<String> getAppVersions(String appName) {
    Set<String> appVersions = new TreeSet<String>();
    for (AppLabelAtom label: this) {
      if (appName.equals(label.getApplication())) {
        appVersions.add(label.getVersion());
      }
    }
    return appVersions;
  }

  /**
   * Returns a application label that matches specified condition,
   * or null if all labels not matched.
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
