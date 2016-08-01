package org.jenkinsci.plugins.withsoftware.util;

import hudson.EnvVars;
import hudson.Util;
import hudson.model.AbstractBuild;
import hudson.model.BuildListener;
import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.labels.LabelAtom;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.withsoftware.SoftwareLabelAtom;
import org.jenkinsci.plugins.withsoftware.SoftwareLabelSet;
import org.zeroturnaround.exec.ProcessExecutor;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class Utils {

  /**
   * Expands the variable in the given string to its value in the variables available to this build.
   *
   * @param buildVars  Map of the build-specific variables.
   * @param token  The token which may or may not contain variables in the format <tt>${foo}</tt>.
   * @return  The given token, with applicable variable expansions done.
   */
  public static String expandVariables(Map<String,String> buildVars, String token) {

    final Map<String,String> vars = new HashMap<String,String>();
    if (buildVars != null) {
      // Build-specific variables, if any, take priority over environment variables
      vars.putAll(buildVars);
    }

    String result = Util.fixEmptyAndTrim(token);
    if (result != null) {
      result = Util.replaceMacro(result, vars);
    }
    return Util.fixEmptyAndTrim(result);
  }

  public static SoftwareLabelSet getSoftwareLabels() {
    SoftwareLabelSet softwareLabels = new SoftwareLabelSet();

    Jenkins jenkins = Jenkins.getInstance();

    if (jenkins == null) {
      return softwareLabels;
    }

    List<Node> allNode = jenkins.getNodes();
    for (Node node: allNode) {
      softwareLabels.addAll(getSoftwareLabels(node));
    }
    return softwareLabels;
  }

  public static SoftwareLabelSet getSoftwareLabels(Node node) {
    SoftwareLabelSet softwareLabels = new SoftwareLabelSet();

    Set<LabelAtom> allLabels = node.getAssignedLabels();
    for (LabelAtom label: allLabels) {
      if (label instanceof SoftwareLabelAtom) {
        softwareLabels.add((SoftwareLabelAtom)label);
      }
    }

    return softwareLabels;
  }

  public static String runExternalCommand(String... command) throws Exception {
    String output = new ProcessExecutor().command(command).readOutput(true).execute().outputUTF8();
    if (output == null) {
      output = "";
    }

    return output;
  }

}
