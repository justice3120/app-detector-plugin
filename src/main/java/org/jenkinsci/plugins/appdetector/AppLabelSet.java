package org.jenkinsci.plugins.appdetector;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
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
   * Returns the version list sorted in DESC.
   * @param appName The application name for which you want to obtain the version list.
   * @return The version list sorted in DESC
   */
  public List<String> getSortedAppVersions(String appName) {
    List<String> versionList
        = new ArrayList<String>(getAppVersions(appName));
    Collections.sort(versionList, new VersionComparator());
    Collections.reverse(versionList);
    return versionList;
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

  private static class VersionComparator implements Comparator<String>, Serializable {
    private static final long serialVersionUID = 1L;

    public int compare(String verString1, String verString2) {
      String[] verArray1 = verString1.split("\\.");
      String[] verArray2 = verString2.split("\\.");

      for (int i = 0; i < verArray1.length; i++) {
        try {
          try {
            int num1 = Integer.parseInt(verArray1[i]);
            int num2 = Integer.parseInt(verArray2[i]);

            if (! (num1 == num2)) {
              return (num1 - num2);
            }
          } catch (NumberFormatException e) {
            String num1 = verArray1[i];
            String num2 = verArray2[i];

            if (! num1.equals(num2)) {
              return num1.compareTo(num2);
            }
          }
        } catch (ArrayIndexOutOfBoundsException e) {
          return 1;
        }
      }

      return (verArray1.length - verArray2.length);
    }
  }
}
