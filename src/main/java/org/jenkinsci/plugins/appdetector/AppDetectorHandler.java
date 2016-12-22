package org.jenkinsci.plugins.appdetector;

import hudson.Extension;
import hudson.matrix.Combination;
import hudson.matrix.MatrixConfiguration;
import hudson.model.Action;
import hudson.model.Label;
import hudson.model.ParameterValue;
import hudson.model.ParametersAction;
import hudson.model.Project;
import hudson.model.Queue;
import hudson.model.labels.LabelAssignmentAction;
import hudson.model.labels.LabelAtom;
import hudson.model.queue.SubTask;
import hudson.tasks.BuildWrapper;
import org.jenkinsci.plugins.appdetector.util.Utils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

@Extension(ordinal = -100)
public class AppDetectorHandler extends Queue.QueueDecisionHandler {
  @Override
  public boolean shouldSchedule(Queue.Task task, List<Action> actions) {
    if (task instanceof Project) {
      List<BuildWrapper> buildWapperList = ((Project)task).getBuildWrappersList();

      for (BuildWrapper bw: buildWapperList) {
        if (bw instanceof AppDetectorBuildWrapper) {
          List<AppUsageSetting> settings = ((AppDetectorBuildWrapper)bw).getAppUsageSettings();

          if (settings.isEmpty()) {
            return true;
          }

          final Map<String, String> buildVars = new TreeMap<String, String>();

          if (task instanceof MatrixConfiguration) {
            Combination combination = ((MatrixConfiguration)task).getCombination();
            buildVars.putAll(combination);
          }

          buildVars.putAll(getBuildVariablesFromActions(actions));

          ApplicationLabelAssignmentAction action = new ApplicationLabelAssignmentAction();

          for (AppUsageSetting setting: settings) {
            String expandedVersion = Utils.expandVariables(buildVars, setting.getVersion());

            action.add(setting.getAppName() + "-" + expandedVersion);
          }
          actions.add(action);
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

  private static class ApplicationLabelAssignmentAction implements LabelAssignmentAction {
    private Label label;

    public ApplicationLabelAssignmentAction() {
      this.label = null;
    }

    public Label getAssignedLabel(SubTask task) {
      Label taskLabel = task.getAssignedLabel();

      if (taskLabel != null) {
        return label.and(taskLabel);
      }

      return label;
    }

    public void add(String labelString) {
      if (label == null) {
        label = new LabelAtom(labelString);
      } else {
        label = label.and(new LabelAtom(labelString));
      }
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
