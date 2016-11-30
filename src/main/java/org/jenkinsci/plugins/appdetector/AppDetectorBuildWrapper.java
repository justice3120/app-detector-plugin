package org.jenkinsci.plugins.appdetector;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.Result;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ComboBoxModel;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.appdetector.task.AppDetectionTask;
import org.jenkinsci.plugins.appdetector.util.Utils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Extension
public class AppDetectorBuildWrapper extends BuildWrapper {

  private List<AppUsageSetting> usageSettings;

  public AppDetectorBuildWrapper() {
    super();
  }

  @DataBoundConstructor
  public AppDetectorBuildWrapper(List<AppUsageSetting> usageSettings) {
    this.usageSettings = usageSettings;
  }

  @Override
  public Environment setUp(AbstractBuild build, final Launcher launcher, BuildListener listener) {
    final PrintStream logger = listener.getLogger();

    final Map<String, String> buildVars = build.getBuildVariables();

    Node node = build.getBuiltOn();

    if (node == null) {
      logger.println(Messages.GETTING_NODE_FAILED());
      build.setResult(Result.FAILURE);
      return null;
    }

    AppLabelSet allLabels = Utils.getApplicationLabels(node);
    final List<AppLabelAtom> labels = new ArrayList<AppLabelAtom>();

    for (AppUsageSetting setting: usageSettings) {
      String expandedVersion = Utils.expandVariables(buildVars, setting.getVersion());
      AppLabelAtom label = allLabels.getApplicationLabel(setting.getAppName(), expandedVersion);

      if (label == null) {
        logger.println(Messages.APP_NOT_FOUND(setting.getAppName(), expandedVersion, node.getNodeName()));
        build.setResult(Result.NOT_BUILT);
        return null;
      }

      labels.add(label);
    }

    return new Environment() {
      @Override
      public void buildEnvVars(Map<String, String> env) {
        Jenkins hudsonInstance = Jenkins.getInstance();
        if (hudsonInstance != null) {
          DescriptorImpl descriptor = hudsonInstance.getDescriptorByType(DescriptorImpl.class);
          List<AppDetectionSetting> detectionSettings = descriptor.getDetectionSettings();

          for (AppLabelAtom label: labels) {
            String envVarName = null;

            for (AppDetectionSetting setting: detectionSettings) {
              if (label.getApplication().equals(setting.getAppName())) {
                envVarName = setting.getHomeDirVarName();
                break;
              }
            }

            if (envVarName != null && !("".equals(envVarName))) {
              env.put(envVarName, label.getHome());
            }
          }
        }
      }
    };
  }

  public List<AppUsageSetting> getAppUsageSettings() {
    return usageSettings;
  }

  @Extension
  public static final class DescriptorImpl extends BuildWrapperDescriptor {

    private List<AppDetectionSetting> detectionSettings = new ArrayList<AppDetectionSetting>();

    public DescriptorImpl() {
      super(AppDetectorBuildWrapper.class);
      load();
    }

    @Override
    public String getDisplayName() {
      return Messages.JOB_DESCRIPTION();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
      detectionSettings = new ArrayList<AppDetectionSetting>();

      JSONArray settingArray = json.optJSONArray("setting");
      if (settingArray != null) {
        for (Object settingObj: settingArray) {
          JSONObject setting = JSONObject.fromObject(settingObj);
          detectionSettings
              .add(new AppDetectionSetting(
                  setting.getString("appName"),
                  setting.getString("script"),
                  setting.getBoolean("detectOnLinux"),
                  setting.getBoolean("detectOnOsx"),
                  setting.getBoolean("detectOnWindows"),
                  setting.getString("homeDirVarName")
              ));
        }
      } else {
        JSONObject setting = json.optJSONObject("setting");
        if (setting != null) {
          detectionSettings
              .add(new AppDetectionSetting(
                  setting.getString("appName"),
                  setting.getString("script"),
                  setting.getBoolean("detectOnLinux"),
                  setting.getBoolean("detectOnOsx"),
                  setting.getBoolean("detectOnWindows"),
                  setting.getString("homeDirVarName")
              ));
        }
      }

      save();
      return true;
    }

    @Override
    public BuildWrapper newInstance(StaplerRequest req, JSONObject json) throws FormException {
      List<AppUsageSetting> usageSettings = new ArrayList<AppUsageSetting>();

      JSONArray settingArray = json.optJSONArray("setting");
      if (settingArray != null) {
        for (Object settingObj: settingArray) {
          JSONObject setting = JSONObject.fromObject(settingObj);
          usageSettings
              .add(new AppUsageSetting(
                  setting.getString("appName"),
                  setting.getString("appVersion")
              ));
        }
      } else {
        JSONObject setting = json.optJSONObject("setting");
        if (setting != null) {
          usageSettings
              .add(new AppUsageSetting(
                  setting.getString("appName"),
                  setting.getString("appVersion")
              ));
        }
      }

      return new AppDetectorBuildWrapper(usageSettings);
    }

    @Override
    public boolean isApplicable(AbstractProject<?, ?> item) {
      return true;
    }

    public ListBoxModel doFillAppNameItems() {
      ListBoxModel items = new ListBoxModel();
      AppLabelSet labels = Utils.getApplicationLabels();
      for (String appName: labels.getAppNames()) {
        items.add(appName);
      }
      return items;
    }

    public ComboBoxModel doFillAppVersionItems(@QueryParameter("appName") final String appName) {
      AppLabelSet labels = Utils.getApplicationLabels();
      return new ComboBoxModel(labels.getAppVersions(appName));
    }

    public ListBoxModel doFillNodeItems() {
      ListBoxModel items = new ListBoxModel();

      Computer[] allComputers = Utils.getAllComputers();
      for (Computer computer: allComputers) {
        if (computer.isOnline()) {
          items.add(computer.getDisplayName(), computer.getName());
        } else {
          items.add(computer.getDisplayName() + "(offline)", computer.getName());
        }
      }
      return items;
    }

    public FormValidation doTestScript(
        @QueryParameter("script") final String script,
        @QueryParameter("node") final String node,
        @QueryParameter("detectOnLinux") final boolean onLinux,
        @QueryParameter("detectOnOsx") final boolean onOsx,
        @QueryParameter("detectOnWindows") final boolean onWindows) {

      Jenkins jenkins = Jenkins.getInstance();
      AppDetectionSetting setting = new AppDetectionSetting("Test", script, onLinux, onOsx, onWindows, "TEST");

      try {
        Computer computer = jenkins.getComputer(node);
        String result = computer.getChannel().call(new AppDetectionTask(setting));
        return FormValidation.ok(result);
      } catch (Exception e) {
        return FormValidation.error(e, e.getMessage());
      }
    }

    public List<AppDetectionSetting> getDetectionSettings() {
      return detectionSettings;
    }
  }
}
