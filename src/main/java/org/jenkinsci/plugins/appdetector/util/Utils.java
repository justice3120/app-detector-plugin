package org.jenkinsci.plugins.appdetector.util;


import hudson.Util;
import hudson.model.Node;
import hudson.model.labels.LabelAtom;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.appdetector.SoftwareLabelAtom;
import org.jenkinsci.plugins.appdetector.SoftwareLabelSet;
import org.zeroturnaround.exec.ProcessExecutor;

import java.util.HashMap;
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

  /**
   * Getting software labels from all nodes that connected to jenkins.
   * @return all of software labels.
   */
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

  /**
   * Getting software labels from specified node.
   * @param node The target node.
   * @return Software labels that assigned to given node.
   */
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

  /**
   * Executing external command, and get output.
   * @param command The list containing the program and its arguments.
   * @return output string.
   * @throws Exception Fail to execute command.
   */
  public static String runExternalCommand(String... command) throws Exception {
    String output = new ProcessExecutor().command(command).readOutput(true).execute().outputUTF8();
    if (output == null) {
      output = "";
    }

    return output;
  }

}
