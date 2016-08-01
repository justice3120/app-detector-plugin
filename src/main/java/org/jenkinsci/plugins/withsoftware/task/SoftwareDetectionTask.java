package org.jenkinsci.plugins.withsoftware.task;

import jenkins.security.MasterToSlaveCallable;
import org.jenkinsci.plugins.withsoftware.util.Utils;

import java.util.Set;

public abstract class SoftwareDetectionTask extends MasterToSlaveCallable<Set<String>, Exception> {
  protected boolean isMac() {
    try {
      String uname = Utils.runExternalCommand("uname").replace("\n","");
      return "Darwin".equals(uname);
    } catch (Exception e) {
      return false;
    }
  }
}
