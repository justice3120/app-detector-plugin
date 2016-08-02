package org.jenkinsci.plugins.withsoftware.task;

import org.jenkinsci.plugins.withsoftware.SoftwareLabelAtom;
import org.jenkinsci.plugins.withsoftware.util.Utils;

import java.util.HashSet;
import java.util.Set;

public final class UnityDetectionTask extends SoftwareDetectionTask {

  @Override
  public Set<String> call() throws Exception {
    Set<String> unityList = new HashSet<String>();
    String appDir = "/Applications/";

    if (! isMac()) {
      return unityList;
    }

    String[] unityPathList = Utils
        .runExternalCommand("ls", appDir, "|", "egrep", "^Unity").split("\n");

    for (String path: unityPathList) {
      String version = Utils
          .runExternalCommand("/usr/libexec/PlistBuddy", "-c", "Print :CFBundleVersion",
              appDir + path + "/Unity.app/Contents/Info.plist").split("\n")[0];

      String unityHome = appDir + path + "/Unity.app";
      SoftwareLabelAtom label = new SoftwareLabelAtom("Unity", version, unityHome);
      unityList.add(SoftwareLabelAtom.serialize(label));
    }

    return unityList;
  }
}
