package org.jenkinsci.plugins.withsoftware;

import hudson.Extension;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.model.Label;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Project;
import hudson.model.Queue;
import hudson.model.labels.LabelAssignmentAction;
import hudson.model.labels.LabelAtom;
import hudson.model.queue.SubTask;
import hudson.tasks.BuildWrapper;
import org.jenkinsci.plugins.withsoftware.util.Utils;

import java.util.List;
import java.util.HashMap;
import java.util.Map;

@Extension(ordinal=-100)
public class WithSoftwareHandler extends Queue.QueueDecisionHandler {
  @Override
  public boolean shouldSchedule(Queue.Task p, List<Action> actions) {
    if (p instanceof Project) {
      List<BuildWrapper> buildWapperList = ((Project)p).getBuildWrappersList();

      for (BuildWrapper bw: buildWapperList) {
        if (bw instanceof WithSoftwareBuildWrapper) {
          String xcodeVersion = ((WithSoftwareBuildWrapper)bw).getXcodeVersion();
          String unityVersion = ((WithSoftwareBuildWrapper)bw).getUnityVersion();

          final Map<String, String> buildVars = getBuildVariablesFromActions(actions);

          xcodeVersion = Utils.expandVariables(buildVars, xcodeVersion);
          unityVersion = Utils.expandVariables(buildVars, unityVersion);

          if (xcodeVersion != null) {
            actions.add(new SoftwareLabelAssignmentAction("Xcode-" + xcodeVersion));
          }
          if (unityVersion != null) {
            actions.add(new SoftwareLabelAssignmentAction("Unity-" + unityVersion));
          }
        }
      }
    }
    return true;
  }

  private Map<String, String> getBuildVariablesFromActions(List<Action> actions) {
    Map<String, String> buildVars = new HashMap<String, String>();
    for (Action action: actions) {
      if (action instanceof ParametersAction) {
        for (ParameterValue param: ((ParametersAction)action).getParameters()) {
          buildVars.put(param.getName(), param.getValue().toString());
        }
      }
    }
    return buildVars;
  }

  private static class SoftwareLabelAssignmentAction implements LabelAssignmentAction {
    private String label;

    public SoftwareLabelAssignmentAction(String label) {
      this.label = label;
    }

    public Label getAssignedLabel(SubTask task) {
      return new LabelAtom(label);
    }

    public String getIconFileName() {
      return null;
    }

    public String getDisplayName() {
      return null;
    }

    public String getUrlName() {
      return null;
    }
  }
}
