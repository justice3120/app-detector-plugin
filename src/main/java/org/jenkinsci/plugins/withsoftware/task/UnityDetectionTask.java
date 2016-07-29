package org.jenkinsci.plugins.withsoftware.task;

import hudson.remoting.Callable;
import jenkins.security.MasterToSlaveCallable;
import org.jenkinsci.plugins.withsoftware.SoftwareLabelAtom;
import org.jenkinsci.plugins.withsoftware.util.Utils;

import java.util.HashSet;
import java.util.Set;

public final class UnityDetectionTask extends MasterToSlaveCallable<Set<String>, Exception> {

  @Override
  public Set<String> call() throws Exception {
    Set<String> unityList = new HashSet<String>();
    String appDir = "/Applications/";

    String[] unityPathList = Utils
        .runExternalCommand("ls", appDir, "|", "egrep", "^Unity").split("\n");

    for (String unityPath: unityPathList) {
      String version = Utils
          .runExternalCommand("/usr/libexec/PlistBuddy", "-c", "Print :CFBundleVersion",
              appDir + unityPath + "/Unity.app/Contents/Info.plist").split("\n")[0];

      String unityHome = appDir + unityPath + "/Unity.app";
      unityList.add(SoftwareLabelAtom.serialize(new SoftwareLabelAtom("Unity", version, unityHome)));
    }

    return unityList;
  }
}
