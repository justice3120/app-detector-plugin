package org.jenkinsci.plugins.appdetector.util;

import hudson.model.Computer;
import hudson.model.Node;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.jenkinsci.plugins.appdetector.AppLabelAtom;
import org.jenkinsci.plugins.appdetector.AppLabelSet;
import jenkins.model.Jenkins;

import java.util.Arrays;
import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UtilsTest {
  @Mocked
  private Jenkins jenkins;

  @Mocked
  private Computer computer;

  @Mocked
  private Node node;

  @Mocked
  private AppLabelAtom label;

  @Test
  public void expandVariablesWithBuildVarToken() throws Exception {
    Map<String,String> buildVers = new HashMap<String,String>();
    buildVers.put("hoge", "8.0");
    String token = "${hoge}";

    String expanded = Utils.expandVariables(buildVers, token);

    assertThat(expanded, is("8.0"));
  }

  @Test
  public void expandVariablesWithPlainToken() {
    Map<String,String> buildVers = new HashMap<String,String>();
    buildVers.put("hoge", "8.0");
    String token = "hoge";

    String expanded = Utils.expandVariables(buildVers, token);

    assertThat(expanded, is("hoge"));
  }

  @Test
  public void getAllComputers() throws Exception {

    new Expectations() {{
      Jenkins.getInstance();
      result = jenkins;

      jenkins.getComputers();
      result = new Computer[] { computer };
    }};

    Computer[] allComputers = Utils.getAllComputers();
    assertThat(allComputers.length, is(1));
    assertThat(Arrays.asList(allComputers), hasItem(computer));
  }

  @Test
  public void getApplicationLabels() throws Exception {
    final Set<AppLabelAtom> assignedLabels = new HashSet<AppLabelAtom>();
    assignedLabels.add(label);

    new Expectations() {{
      Jenkins.getInstance();
      result = jenkins;

      jenkins.getComputers();
      result = new Computer[] { computer };

      computer.getNode();
      result = node;

      node.getAssignedLabels();
      result = assignedLabels;
    }};

    AppLabelSet labels = Utils.getApplicationLabels();
    assertThat(labels.size(), is(1));
    assertThat(labels, hasItem(label));
  }

  @Test
  public void getApplicationLabelsWithNode() throws Exception {
    final Set<AppLabelAtom> assignedLabels = new HashSet<AppLabelAtom>();
    assignedLabels.add(label);

    new Expectations() {
      {
        node.getAssignedLabels();
        result = assignedLabels;
      }
    };

    AppLabelSet labels = Utils.getApplicationLabels(node);
    assertThat(labels.size(), is(1));
    assertThat(labels, hasItem(label));
  }

  @Test
  public void runExternalCommand() throws Exception {
    String output = Utils.runExternalCommand("echo", "HOGE");
    assertThat(output, is("HOGE\n"));
  }
}
