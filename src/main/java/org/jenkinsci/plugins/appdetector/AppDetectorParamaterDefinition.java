package org.jenkinsci.plugins.appdetector;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.appdetector.util.Utils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import java.util.ArrayList;
import java.util.List;

public class AppDetectorParamaterDefinition extends SimpleParameterDefinition {

  private final String appName;
  private List<String> choices;
  private final String defaultValue;

  /**
   * Creates new {@link AppDetectorParamaterDefinition} instance.
   * @param name Name.
   * @param appName The name of application such as "Xcode", "Unity".
   * @param description Description.
   */
  //@DataBoundConstructor
  public AppDetectorParamaterDefinition(String name, String appName, String description) {
    super(name, description);
    this.appName = appName;
    this.choices = getSortedVersionList();
    defaultValue = null;
  }

  /**
   * Creates new {@link AppDetectorParamaterDefinition} instance.
   * @param name Name.
   * @param appName The name of application such as "Xcode", "Unity".
   * @param defaultValue Default version of this application.
   * @param description Description.
   */
  @DataBoundConstructor
  public AppDetectorParamaterDefinition(String name, String appName, String defaultValue,
      String description) {
    super(name, description);
    this.appName = appName;
    this.choices = getSortedVersionList();
    this.defaultValue = defaultValue;
  }

  @Override
  public ParameterDefinition copyWithDefaultValue(ParameterValue defaultValue) {
    if (defaultValue instanceof StringParameterValue) {
      StringParameterValue value = (StringParameterValue) defaultValue;
      return new AppDetectorParamaterDefinition(getName(), getAppName(), value.value,
          getDescription());
    } else {
      return this;
    }
  }

  @Exported
  public String getAppName() {
    return appName;
  }

  @Exported
  public String getDefaultValue() {
    return defaultValue;
  }

  /**
   * Returns the version list sorted in DESC.
   * @return The version list sorted in DESC
   */
  public List<String> getSortedVersionList() {
    return Utils.getApplicationLabels().getSortedAppVersions(appName);
  }

  @Override
  public StringParameterValue getDefaultParameterValue() {
    // Update option to current version list
    choices = new ArrayList<String>(Utils.getApplicationLabels().getSortedAppVersions(appName));
    if (choices.contains(defaultValue)) {
      return new StringParameterValue(getName(), defaultValue, getDescription());
    } else {
      return new StringParameterValue(getName(), choices.get(0), getDescription());
    }
  }

  private StringParameterValue checkValue(StringParameterValue value) {
    // Update option to current version list
    choices = new ArrayList<String>(Utils.getApplicationLabels().getSortedAppVersions(appName));
    if (!choices.contains(value.value)) {
      throw new IllegalArgumentException("Illegal choice for parameter " + getName() + ": "
          + value.value);
    }
    return value;
  }

  @Override
  public ParameterValue createValue(StaplerRequest req, JSONObject jo) {
    StringParameterValue value = req.bindJSON(StringParameterValue.class, jo);
    value.setDescription(getDescription());
    return checkValue(value);
  }

  public StringParameterValue createValue(String value) {
    return checkValue(new StringParameterValue(getName(), value, getDescription()));
  }

  @Extension
  public static class DescriptorImpl extends ParameterDescriptor {
    @Override
    public String getDisplayName() {
      return Messages.AppDetectorParamaterDefinition_DisplayName();
    }

    /**
     * Fill in the value of the select element of the application name in Jenkins' Web view.
     * @return List of application names.
     */
    public ListBoxModel doFillAppNameItems() {
      ListBoxModel items = new ListBoxModel();
      AppLabelSet labels = Utils.getApplicationLabels();
      for (String appName: labels.getAppNames()) {
        items.add(appName);
      }
      return items;
    }

    public ComboBoxModel doFillDefaultValueItems(@QueryParameter("appName") final String appName) {
      AppLabelSet labels = Utils.getApplicationLabels();
      return new ComboBoxModel(labels.getSortedAppVersions(appName));
    }
  }
}
