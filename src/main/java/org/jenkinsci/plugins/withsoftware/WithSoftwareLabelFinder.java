package org.jenkinsci.plugins.withsoftware;

import hudson.Extension;
import hudson.model.Computer;
import hudson.model.LabelFinder;
import hudson.model.Node;
import hudson.model.TaskListener;
import hudson.model.labels.LabelAtom;
import hudson.slaves.ComputerListener;
import jenkins.model.Jenkins;
import org.jenkinsci.plugins.withsoftware.task.UnityDetectionTask;
import org.jenkinsci.plugins.withsoftware.task.XcodeDetectionTask;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.LogManager;
import java.util.logging.Logger;

@Extension
public class WithSoftwareLabelFinder extends LabelFinder {

  private final Map<Node, Set<LabelAtom>> cashedLabels = new ConcurrentHashMap<Node, Set<LabelAtom>>();

  @Override
  public Collection<LabelAtom> findLabels(Node node) {
    Computer computer = node.toComputer();
    if(computer == null || node.getChannel()==null)
        return Collections.emptyList();

    Set<LabelAtom> softwares = cashedLabels.get(node);
    if(softwares == null || softwares.isEmpty()) return Collections.emptyList();

    return softwares;
  }

  @Extension
  public static class WithSoftwareComputerListener extends ComputerListener {

    @Override
    public void onOnline(Computer c, TaskListener taskListener) {
      Set<LabelAtom> softwares = detectInstalledSoftwares(c);
      if (!softwares.isEmpty()) {
          finder().cashedLabels.put(c.getNode(), softwares);
      } else {
          finder().cashedLabels.remove(c.getNode());
      }
    }

    @Override
    public void onConfigurationChange(){
      WithSoftwareLabelFinder finder = finder();

      Set<Node> cachedNodes = new HashSet<Node>(finder.cashedLabels.keySet());

      Jenkins jenkins = Jenkins.getInstance();

      if (jenkins == null) {
        return;
      }

      List<Node> realNodes = jenkins.getNodes();
      for(Node node: cachedNodes){
        if(!realNodes.contains(node)){
            finder.cashedLabels.remove(node);
        }
      }
    }

    private WithSoftwareLabelFinder finder() {
        return LabelFinder.all().get(WithSoftwareLabelFinder.class);
    }

    private Set<LabelAtom> detectInstalledSoftwares(Computer computer) {
      Logger logger = LogManager.getLogManager().getLogger("hudson.WebAppMain");

      Set<LabelAtom> softwares = new HashSet<LabelAtom>();
      Boolean isUnix = computer.isUnix();

      //This computer seems offline. So, skip detection.
      if (isUnix == null) {
        return softwares;
      }

      try {
        if (isUnix) {
          Set<String> serializedSoftwares = new HashSet<String>();
          serializedSoftwares.addAll(computer.getChannel().call(new XcodeDetectionTask()));
          serializedSoftwares.addAll(computer.getChannel().call(new UnityDetectionTask()));
          for (String softwareString: serializedSoftwares) {
            softwares.add(SoftwareLabelAtom.deserialize(softwareString));
          }
        }
      } catch (Exception e) {
        logger.warning(Messages.DETECTING_SOFTOWARE_INSTLLATION_FAILED(computer.getDisplayName()));
      }
      return softwares;
    }
  }
}
