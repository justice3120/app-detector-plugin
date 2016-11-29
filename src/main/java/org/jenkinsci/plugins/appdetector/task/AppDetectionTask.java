package org.jenkinsci.plugins.appdetector.task;

import groovy.lang.GroovyShell;
import jenkins.security.MasterToSlaveCallable;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.runtime.RuntimeServices;
import org.apache.velocity.runtime.RuntimeSingleton;
import org.apache.velocity.runtime.parser.ParseException;
import org.apache.velocity.runtime.parser.node.SimpleNode;
import org.jenkinsci.plugins.appdetector.AppDetectionSetting;
import org.jenkinsci.plugins.appdetector.AppLabelAtom;
import org.jenkinsci.plugins.appdetector.util.Utils;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;

import java.util.Scanner;
import java.util.Set;
import java.util.List;
import java.util.HashSet;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.InputStream;
import java.net.URL;

public class AppDetectionTask extends MasterToSlaveCallable<String, Exception> {
  private String appName;
  private String scriptString;
  private boolean onLinux;
  private boolean onOsx;
  private boolean onWindows;

  public AppDetectionTask(AppDetectionSetting setting) {
    this.appName = setting.getAppName();
    this.scriptString = setting.getScript();
    this.onLinux = setting.getOnLinux();
    this.onOsx = setting.getOnOsx();
    this.onWindows = setting.getOnWindows();
  }

  @Override
  public String call() throws Exception {
    String result = "[]";
    String platform = getPlatform();

    if ("osx".equals(platform)) {
      if (! onOsx) {
        return result;
      }
    } else if ("linux".equals(platform)){
      if (! onLinux) {
        return result;
      }
    } else {
      return result;
    }

    String templateString = loadTemplateFile();

    StringWriter writer = new StringWriter();
    VelocityContext context = new VelocityContext();
    context.put("platform", platform);
    context.put("scriptBody", scriptString);

    Velocity.evaluate(context, writer, "", templateString);

    GroovyShell shell = new GroovyShell(this.getClass().getClassLoader());
    groovy.lang.Script script = shell.parse(writer.toString());
    result = (String) script.run();

    return result;
  }

  private String getPlatform() {
    try {
      String uname = Utils.runExternalCommand("uname").replace("\n","");
      if ("Darwin".equals(uname)) {
        return "osx";
      }
      return "linux";
    } catch (Exception e) {
      return "windows";
    }
  }

  private String loadTemplateFile() throws Exception {
    URL templateUrl = AppDetectionTask.class.getResource("template.groovy.vm");
    InputStream in = templateUrl.openConnection().getInputStream();
    String templateString = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
    return templateString;
  }
}
