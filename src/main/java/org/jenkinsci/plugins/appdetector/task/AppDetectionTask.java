package org.jenkinsci.plugins.appdetector.task;

import groovy.lang.GroovyShell;
import jenkins.security.MasterToSlaveCallable;
import org.jenkinsci.plugins.appdetector.AppDetectionSetting;
import org.jenkinsci.plugins.appdetector.AppLabelAtom;
import org.jenkinsci.plugins.appdetector.util.Utils;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import java.util.Set;
import java.util.List;
import java.util.HashSet;

public class AppDetectionTask extends MasterToSlaveCallable<Set<String>, Exception> {
  private String appName;
  private String scriptString;
  private boolean onLinux;
  private boolean onOsx;
  private boolean onWindows;

  String DEAFULT_SCRIPT_HEADER = "import groovy.json.*\n"
      + "import static org.jenkinsci.plugins.appdetector.util.Utils.runExternalCommand\n";

  public AppDetectionTask(AppDetectionSetting setting) {
    this.appName = setting.getAppName();
    this.scriptString = setting.getScript();
    this.onLinux = setting.getOnLinux();
    this.onOsx = setting.getOnOsx();
    this.onWindows = setting.getOnWindows();
  }

  @Override
  public Set<String> call() throws Exception {
    Set<String> appList = new HashSet<String>();

    if (isMac()) {
      if (! onOsx) {
        return appList;
      }
    } else {
      if (! onLinux) {
        return appList;
      }
    }

    GroovyShell shell = new GroovyShell(this.getClass().getClassLoader());
    groovy.lang.Script script = shell.parse(DEAFULT_SCRIPT_HEADER + scriptString);
    JSONArray appVersions = JSONArray.fromObject((String) script.run());

    for (Object appInfo: appVersions) {
      JSONObject info = JSONObject.fromObject(appInfo);
      appList.add(appName + ":" + info.getString("version") + ":" + info.getString("home"));
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
