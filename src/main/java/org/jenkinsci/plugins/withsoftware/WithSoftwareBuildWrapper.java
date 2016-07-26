package org.jenkinsci.plugins.withsoftware;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.model.BuildListener;
import hudson.tasks.BuildWrapper;
import hudson.tasks.BuildWrapperDescriptor;
import hudson.util.ComboBoxModel;
import hudson.util.ListBoxModel;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.jenkinsci.plugins.withsoftware.model.Software;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class WithSoftwareBuildWrapper extends BuildWrapper {

  private List<Software> softwareList;

  @DataBoundConstructor
  public WithSoftwareBuildWrapper(List<Software> softwareList) {
    this.softwareList = softwareList;
  }

  @Override
  public Environment setUp(AbstractBuild build, final Launcher launcher, BuildListener listener) {
    return new Environment() {
      @Override
      public void buildEnvVars(Map<String, String> env) {
        env.put("DEVELOPER_DIR", "hoge");
        env.put("UNITY_DIR", "fuga");
      }
    };
  }

  public List<Software> getSoftwareList() {
    return softwareList;
  }

  @Extension
  public static final class DescriptorImpl extends BuildWrapperDescriptor {

    public DescriptorImpl() {
      super(WithSoftwareBuildWrapper.class);
      load();
    }

    @Override
    public String getDisplayName() {
      return Messages.JOB_DESCRIPTION();
    }

    @Override
    public boolean configure(StaplerRequest req, JSONObject json) throws FormException {
      save();
      return true;
    }

    @Override
    public BuildWrapper newInstance(StaplerRequest req, JSONObject json) throws FormException {
      List<Software> softwareList = new ArrayList<Software>();

      JSONArray softwareArray = json.optJSONArray("software");
      if (softwareArray != null) {
        for (Object softwareObj: softwareArray) {
          JSONObject software = JSONObject.fromObject(softwareObj);
          softwareList
              .add(new Software(software.getString("softwareName"), software.getString("softwareVersion")));
        }
      } else {
        JSONObject software = json.optJSONObject("software");
        if (software != null) {
          softwareList
              .add(new Software(software.getString("softwareName"), software.getString("softwareVersion")));
        }
      }
      return new WithSoftwareBuildWrapper(softwareList);
    }

    @Override
    public boolean isApplicable(AbstractProject<?, ?> item) {
      return true;
    }

    public ListBoxModel doFillSoftwareNameItems() {
      ListBoxModel items = new ListBoxModel();
      items.add("Xcode");
      items.add("Unity");
      return items;
    }

    public ComboBoxModel doFillSoftwareVersionItems(@QueryParameter String softwareName) {
      ComboBoxModel items = new ComboBoxModel();
      return items;
    }
  }
}
