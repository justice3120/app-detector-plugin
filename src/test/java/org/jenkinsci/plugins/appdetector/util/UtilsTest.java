package org.jenkinsci.plugins.appdetector.util;

import hudson.model.Node;
import mockit.Expectations;
import mockit.Mocked;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;
import org.jenkinsci.plugins.appdetector.SoftwareLabelAtom;
import org.jenkinsci.plugins.appdetector.SoftwareLabelSet;

import java.util.Map;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class UtilsTest {
  @Mocked
  private Node node;

  @Mocked
  private SoftwareLabelAtom label;

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
    final Set<SoftwareLabelAtom> assignedLabels = new HashSet<SoftwareLabelAtom>();
    assignedLabels.add(label);

    new Expectations() {
      {
        node.getAssignedLabels();
        result = assignedLabels;
      }
    };

    SoftwareLabelSet labels = Utils.getSoftwareLabels(node);
    assertThat(labels.size(), is(1));
    assertThat(labels, hasItem(label));
  }

  @Test
  public void runExternalCommand() throws Exception {
    String output = Utils.runExternalCommand("echo", "HOGE");
    assertThat(output, is("HOGE\n"));
  }
}
