package org.jenkinsci.plugins.withsoftware.util;

import hudson.model.Computer;
import hudson.model.Node;
import hudson.model.labels.LabelAtom;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.withsoftware.SoftwareLabelAtom;
import org.jenkinsci.plugins.withsoftware.SoftwareLabelSet;
import org.jenkinsci.plugins.withsoftware.task.UnityDetectionTask;
import org.jenkinsci.plugins.withsoftware.task.XcodeDetectionTask;
import org.zeroturnaround.exec.ProcessExecutor;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Utils {
  public static Set<LabelAtom> detectInstalledSoftwares(Computer computer) {
    Set<LabelAtom> softwares = new HashSet<LabelAtom>();
    try {
      if (computer.isUnix()) {
        Set<String> serializedSoftwares = new HashSet<String>();
        serializedSoftwares.addAll(computer.getChannel().call(new XcodeDetectionTask()));
        serializedSoftwares.addAll(computer.getChannel().call(new UnityDetectionTask()));
        for (String softwareString: serializedSoftwares) {
          softwares.add(SoftwareLabelAtom.deserialize(softwareString));
        }
      }
    } catch (Exception e) {
      System.out.println(e.toString());
    }
    return softwares;
  }

  public static SoftwareLabelSet getSoftwareLabels() {
    SoftwareLabelSet softwareLabels = new SoftwareLabelSet();

    List<Node> allNode = Jenkins.getInstance().getNodes();
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
