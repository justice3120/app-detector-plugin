package org.jenkinsci.plugins.appdetector.task;

import groovy.lang.GroovyShell;
import jenkins.security.MasterToSlaveCallable;
import org.jenkinsci.plugins.appdetector.SoftwareLabelAtom;
import org.jenkinsci.plugins.appdetector.util.Utils;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

public class AppDetectionTask extends MasterToSlaveCallable<Set<String>, Exception> {
  private String appName;
  private String scriptString;

  public AppDetectionTask(String appName, String scriptString) {
    this.appName = appName;
    this.scriptString = scriptString;
  }

  @Override
  public Set<String> call() throws Exception {
    Set<String> appList = new HashSet<String>();

    if (! isMac()) {
      return appList;
    }

    GroovyShell shell = new GroovyShell(this.getClass().getClassLoader());
    groovy.lang.Script script = shell.parse(scriptString);
    List<String> scriptResult = (List<String>) script.run();

    for (String appInfo: scriptResult) {
      appList.add(appName + "," + appInfo);
    }

    return appList;
  }

  private boolean isMac() {
    try {
      String uname = Utils.runExternalCommand("uname").replace("\n","");
      return "Darwin".equals(uname);
    } catch (Exception e) {
      return false;
    }
  }
}
