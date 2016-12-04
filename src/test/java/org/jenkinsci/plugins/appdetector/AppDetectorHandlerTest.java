package org.jenkinsci.plugins.appdetector;

import org.junit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import mockit.Expectations;
import mockit.Mocked;
import hudson.matrix.Combination;
import hudson.matrix.MatrixConfiguration;
import hudson.model.Action;
import hudson.model.Descriptor;
import hudson.model.StringParameterValue;
import hudson.model.ParametersAction;
import hudson.model.ParameterValue;
import hudson.model.Project;
import hudson.model.StringParameterValue;
import hudson.model.Label;
import hudson.model.labels.LabelAssignmentAction;
import hudson.model.labels.LabelAtom;
import hudson.tasks.BuildWrapper;
import hudson.util.DescribableList;

import java.util.Arrays;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.io.PrintStream;

public class AppDetectorHandlerTest {
  private AppDetectorHandler handler;

  @Mocked
  private Project task;

  @Mocked
  private MatrixConfiguration matrixTask;

  @Mocked
  private AppDetectorBuildWrapper bw;

  private List<Action> actions;

  @Before
  public void init() {
    actions = new ArrayList<Action>();
    handler = new AppDetectorHandler();
  }

  @Test
  public void shouldSchedule() throws Exception {
    new Expectations() {
      {
        task.getBuildWrappersList();
        result = new DescribableList<BuildWrapper, Descriptor<BuildWrapper>>() {
          {
            add(bw);
          }
        };

        task.getAssignedLabel();
        result = null;

        bw.getAppUsageSettings();
        result = new ArrayList<>(Arrays.asList(new AppUsageSetting("Xcode", "8.0")));
      }
    };
    LabelAssignmentAction actualAction = null;
    boolean result = handler.shouldSchedule(task, actions);

    for (Action action: actions) {
      if (action instanceof LabelAssignmentAction) {
        actualAction = (LabelAssignmentAction) action;
        break;
      }
    }

    assertThat(result, is(true));
    assertThat(actualAction, is(notNullValue()));

    Label expectedLabel = new LabelAtom("Xcode-8.0");
    assertThat(actualAction.getAssignedLabel(task), is(expectedLabel));
  }

  @Test
  public void shouldScheduleWhenMultipleActionAssigned() throws Exception {
    new Expectations() {
      {
        task.getBuildWrappersList();
        result = new DescribableList<BuildWrapper, Descriptor<BuildWrapper>>() {
          {
            add(bw);
          }
        };

        task.getAssignedLabel();
        result = null;

        bw.getAppUsageSettings();
        result = new ArrayList<>(Arrays.asList(
            new AppUsageSetting("Xcode", "8.0"), new AppUsageSetting("Unity", "5.3.6f1")));
      }
    };
    LabelAssignmentAction actualAction = null;
    boolean result = handler.shouldSchedule(task, actions);

    for (Action action: actions) {
      if (action instanceof LabelAssignmentAction) {
        actualAction = (LabelAssignmentAction) action;
        break;
      }
    }

    assertThat(result, is(true));
    assertThat(actualAction, is(notNullValue()));

    Label expectedLabel = new LabelAtom("Xcode-8.0");
    expectedLabel = expectedLabel.and(new LabelAtom("Unity-5.3.6f1"));
    assertThat(actualAction.getAssignedLabel(task), is(expectedLabel));
  }

  @Test
  public void shouldScheduleWhenProjectLabelAssigned() throws Exception {
    new Expectations() {
      {
        task.getBuildWrappersList();
        result = new DescribableList<BuildWrapper, Descriptor<BuildWrapper>>() {
          {
            add(bw);
          }
        };

        task.getAssignedLabel();
        result = new LabelAtom("master");

        bw.getAppUsageSettings();
        result = new ArrayList<>(Arrays.asList(new AppUsageSetting("Xcode", "8.0")));
      }
    };
    LabelAssignmentAction actualAction = null;
    boolean result = handler.shouldSchedule(task, actions);

    for (Action action: actions) {
      if (action instanceof LabelAssignmentAction) {
        actualAction = (LabelAssignmentAction) action;
        break;
      }
    }

    assertThat(result, is(true));
    assertThat(actualAction, is(notNullValue()));

    Label expectedLabel = new LabelAtom("Xcode-8.0");
    expectedLabel = expectedLabel.and(new LabelAtom("master"));
    assertThat(actualAction.getAssignedLabel(task), is(expectedLabel));
  }

  @Test
  public void shouldScheduleWhenBuildParamaterGiven() throws Exception {
    new Expectations() {
      {
        task.getBuildWrappersList();
        result = new DescribableList<BuildWrapper, Descriptor<BuildWrapper>>() {
          {
            add(bw);
          }
        };

        task.getAssignedLabel();
        result = null;

        bw.getAppUsageSettings();
        result = new ArrayList<>(Arrays.asList(new AppUsageSetting("Xcode", "$XCODE_VERSION")));
      }
    };
    actions.add(new ParametersAction(new StringParameterValue("XCODE_VERSION", "8.0")));

    LabelAssignmentAction actualAction = null;
    boolean result = handler.shouldSchedule(task, actions);

    for (Action action: actions) {
      if (action instanceof LabelAssignmentAction) {
        if (! (action instanceof ParametersAction)) {
          actualAction = (LabelAssignmentAction) action;
          break;
        }
      }
    }

    assertThat(result, is(true));
    assertThat(actualAction, is(notNullValue()));

    Label expectedLabel = new LabelAtom("Xcode-8.0");
    assertThat(actualAction.getAssignedLabel(task), is(expectedLabel));
  }

  @Test
  public void shouldScheduleWhenMatrixParamaterGiven() throws Exception {
    new Expectations() {
      {
        matrixTask.getBuildWrappersList();
        result = new DescribableList<BuildWrapper, Descriptor<BuildWrapper>>() {
          {
            add(bw);
          }
        };

        matrixTask.getAssignedLabel();
        result = null;

        matrixTask.getCombination();
        result = new Combination(new HashMap<String, String>() {
          {
            put("XCODE_VERSION", "8.0");
          }
        });

        bw.getAppUsageSettings();
        result = new ArrayList<>(Arrays.asList(new AppUsageSetting("Xcode", "$XCODE_VERSION")));
      }
    };
    LabelAssignmentAction actualAction = null;
    boolean result = handler.shouldSchedule(matrixTask, actions);

    for (Action action: actions) {
      if (action instanceof LabelAssignmentAction) {
        actualAction = (LabelAssignmentAction) action;
        break;
      }
    }

    assertThat(result, is(true));
    assertThat(actualAction, is(notNullValue()));

    Label expectedLabel = new LabelAtom("Xcode-8.0");
    assertThat(actualAction.getAssignedLabel(matrixTask), is(expectedLabel));
  }
}
