package org.jenkinsci.plugins.appdetector.task;

import mockit.Expectations;
import mockit.Mocked;
import org.jenkinsci.plugins.appdetector.AppDetectionSetting;
import org.jenkinsci.plugins.appdetector.util.Utils;
import org.junit.*;
import static org.hamcrest.MatcherAssert.*;
import static org.hamcrest.Matchers.*;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import java.util.Scanner;
import java.util.Set;
import java.io.InputStream;

public class AppDetectionTaskTest {

  private AppDetectionTask task;

  @Mocked
  final Utils utils = null;

  @Before
  public void init() {
    InputStream in = this.getClass().getResourceAsStream("test.groovy");
    String script = new Scanner(in, "UTF-8").useDelimiter("\\A").next();
    AppDetectionSetting setting = new AppDetectionSetting("Test", script, false, true, false, "TEST");
    task = new AppDetectionTask(setting);
  }

  @Test
  public void call() throws Exception {

    new Expectations() {{
      Utils.runExternalCommand("uname");
      result = "Darwin\n";
    }};

    JSONArray result = JSONArray.fromObject(task.call());
    JSONObject expectedItem = new JSONObject();
    expectedItem.put("version", "1.0");
    expectedItem.put("home", "/hoge");

    assertThat(result, hasItem(expectedItem));
  }

  @Test
  public void call_whenPlatformNotMatched() throws Exception {

    new Expectations() {{
      Utils.runExternalCommand("uname");
      result = "Linux\n";
    }};

    JSONArray result = JSONArray.fromObject(task.call());

    assertThat(result, is(empty()));
  }
}
