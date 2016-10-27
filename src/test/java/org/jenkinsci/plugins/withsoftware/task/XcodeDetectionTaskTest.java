package org.jenkinsci.plugins.withsoftware.task;

import mockit.Expectations;
import mockit.Mocked;
import org.jenkinsci.plugins.withsoftware.util.Utils;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Set;

public class XcodeDetectionTaskTest {

  private XcodeDetectionTask task;

  @Mocked
  final Utils utils = null;

  @Before
  public void init() {
    task = new XcodeDetectionTask();
  }

  @Test
  public void call() throws Exception {

    new Expectations() {{
      Utils.runExternalCommand("uname");
      result = "Darwin\n";

      Utils.runExternalCommand("/usr/bin/mdfind", "kMDItemCFBundleIdentifier == 'com.apple.dt.Xcode'");
      result = "/Applications/Xcode.app\n";

      Utils.runExternalCommand("env", "DEVELOPER_DIR=/Applications/Xcode.app", "/usr/bin/xcodebuild", "-version");
      result = "Xcode 7.3.1\nBuild version 7D1014\n";
    }};

    Set<String> labels = task.call();
    String expectedItem = "Xcode:7.3.1:/Applications/Xcode.app";

    assertThat(labels, hasItem(expectedItem));
  }
}
