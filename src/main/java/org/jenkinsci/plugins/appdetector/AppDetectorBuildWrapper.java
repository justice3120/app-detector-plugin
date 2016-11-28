package org.jenkinsci.plugins.appdetector;

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.model.Result;
import jenkins.model.Jenkins;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ListBoxModel;
import hudson.util.ComboBoxModel;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.jenkinsci.plugins.appdetector.util.Utils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

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

    public ComboBoxModel doFillAppVersionItems(@QueryParameter String appName) {
      AppLabelSet labels = Utils.getApplicationLabels();
      return new ComboBoxModel(labels.getAppVersions(appName));
    }

    public List<AppDetectionSetting> getDetectionSettings() {
      return detectionSettings;
    }
  }
}
