package org.jenkinsci.plugins.appdetector.util;

import hudson.model.Node;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.jenkinsci.plugins.appdetector.AppLabelAtom;
import org.jenkinsci.plugins.appdetector.AppLabelSet;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UtilsTest {
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
  public void getSoftwareLabelsWithNode() throws Exception {
    final Set<AppLabelAtom> assignedLabels = new HashSet<AppLabelAtom>();
    assignedLabels.add(label);

    new Expectations() {
      {
        node.getAssignedLabels();
        result = assignedLabels;
      }
    };

    AppLabelSet labels = Utils.getSoftwareLabels(node);
    assertThat(labels.size(), is(1));
    assertThat(labels, hasItem(label));
  }

  @Test
  public void runExternalCommand() throws Exception {
    String output = Utils.runExternalCommand("echo", "HOGE");
    assertThat(output, is("HOGE\n"));
  }
}
