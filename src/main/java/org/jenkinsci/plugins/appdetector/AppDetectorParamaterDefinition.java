package org.jenkinsci.plugins.appdetector;

import hudson.Extension;
import hudson.model.ParameterDefinition;
import hudson.model.ParameterValue;
import hudson.model.SimpleParameterDefinition;
import hudson.model.StringParameterValue;
import hudson.util.ListBoxModel;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.appdetector.util.Utils;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;
import org.kohsuke.stapler.export.Exported;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
  @DataBoundConstructor
  public AppDetectorParamaterDefinition(String name, String appName, String description) {
    super(name, description);
    this.appName = appName;
    this.choices = getSortedVersionList();
    defaultValue = null;
  }

  private AppDetectorParamaterDefinition(String name, String appName, String defaultValue,
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

  /**
   * Returns the version list sorted in DESC.
   * @return The version list sorted in DESC
   */
  public List<String> getSortedVersionList() {
    List<String> versionList = new ArrayList<String>(Utils.getApplicationLabels().getAppVersions(appName));
    Collections.sort(versionList, new VersionComparator());
    Collections.reverse(versionList);
    return versionList;
  }

  @Override
  public StringParameterValue getDefaultParameterValue() {
    return new StringParameterValue(getName(), defaultValue == null ? choices.get(0) : defaultValue,
        getDescription());
  }

  private StringParameterValue checkValue(StringParameterValue value) {
    // Update option to current version list
    choices = new ArrayList<String>(Utils.getApplicationLabels().getAppVersions(appName));
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
  }

  private static class VersionComparator implements Comparator<String>, Serializable {
    private static final long serialVersionUID = 1L;

    public int compare(String vString1, String vString2) {
      String[] vArray1 = vString1.split("\\.");
      String[] vArray2 = vString2.split("\\.");

      for (int i = 0; i < vArray1.length; i++) {
        try {
          try {
            int num1 = Integer.parseInt(vArray1[i]);
            int num2 = Integer.parseInt(vArray2[i]);

            if (! (num1 == num2)) {
                return (num1 - num2);
            }
          } catch (NumberFormatException e) {
            String num1 = vArray1[i];
            String num2 = vArray2[i];

            if (! num1.equals(num2)) {
                return num1.compareTo(num2);
            }
          }
        } catch (ArrayIndexOutOfBoundsException e) {
            return 1;
        }
      }

      return (vArray1.length - vArray2.length);
    }
  }
}
