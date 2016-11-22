package org.jenkinsci.plugins.appdetector;

import hudson.Extension;
import hudson.Launcher;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.model.Node;
import hudson.model.Result;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ComboBoxModel;
import net.sf.json.JSONObject;
import net.sf.json.JSONArray;
import org.jenkinsci.plugins.appdetector.util.Utils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;

@Extension
public class AppDetectorBuildWrapper extends BuildWrapper {

  private String xcodeVersion;
  private String unityVersion;

  public AppDetectorBuildWrapper() {
    super();
  }

  @DataBoundConstructor
  public AppDetectorBuildWrapper(String xcodeVersion, String unityVersion) {
    this.xcodeVersion = xcodeVersion;
    this.unityVersion = unityVersion;
  }

  @Override
  public Environment setUp(AbstractBuild build, final Launcher launcher, BuildListener listener) {
    final PrintStream logger = listener.getLogger();

    final Map<String, String> buildVars = build.getBuildVariables();

    String xcodeVersion = Utils.expandVariables(buildVars, this.xcodeVersion);
    String unityVersion = Utils.expandVariables(buildVars, this.unityVersion);

    Node node = build.getBuiltOn();

    if (node == null) {
      logger.println(Messages.GETTING_NODE_FAILED());
      build.setResult(Result.FAILURE);
      return null;
    }

    AppLabelSet labels = Utils.getApplicationLabels(node);

    final AppLabelAtom xcodeLabel = labels.getApplicationLabel("Xcode", xcodeVersion);
    final AppLabelAtom unityLabel = labels.getApplicationLabel("Unity", unityVersion);

    if (xcodeVersion != null && xcodeLabel == null) {
      logger.println(Messages.XCODE_NOT_FOUND());
      build.setResult(Result.NOT_BUILT);
      return null;
    }

    if (unityVersion != null && unityLabel == null) {
      logger.println(Messages.UUNITY_NOT_FOUND());
      build.setResult(Result.NOT_BUILT);
      return null;
    }

    return new Environment() {
      @Override
      public void buildEnvVars(Map<String, String> env) {
        if (xcodeLabel != null) {
          env.put("DEVELOPER_DIR", xcodeLabel.getHome());
        }

        if (unityLabel != null) {
          env.put("UNITY_HOME", unityLabel.getHome());
        }
      }
    };
  }

  public String getXcodeVersion() {
    return xcodeVersion;
  }

  public String getUnityVersion() {
    return unityVersion;
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
              .add(new AppDetectionSetting(setting.getString("appName"), setting.getString("script")));
        }
      } else {
        JSONObject setting = json.optJSONObject("setting");
        if (setting != null) {
          detectionSettings
              .add(new AppDetectionSetting(setting.getString("appName"), setting.getString("script")));
        }
      }

      save();
      return true;
    }

    @Override
    public BuildWrapper newInstance(StaplerRequest req, JSONObject json) throws FormException {
      String xcodeVersion = Util.fixEmptyAndTrim(json.getString("xcodeVersion"));
      String unityVersion = Util.fixEmptyAndTrim(json.getString("unityVersion"));

      return new AppDetectorBuildWrapper(xcodeVersion, unityVersion);
    }

    @Override
    public boolean isApplicable(AbstractProject<?, ?> item) {
      return true;
    }

    public ComboBoxModel doFillXcodeVersionItems() {
      AppLabelSet labels = Utils.getApplicationLabels();
      return new ComboBoxModel(labels.getXcodeVersions());
    }

    public ComboBoxModel doFillUnityVersionItems() {
      AppLabelSet labels = Utils.getApplicationLabels();
      return new ComboBoxModel(labels.getUnityVersions());
    }

    public List<AppDetectionSetting> getDetectionSettings() {
      return detectionSettings;
    }
  }
}
