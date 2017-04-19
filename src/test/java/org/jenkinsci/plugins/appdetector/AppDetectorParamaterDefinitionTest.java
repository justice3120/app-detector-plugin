package org.jenkinsci.plugins.appdetector;

import mockit.Expectations;
import mockit.Mocked;
import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.contains;
import static org.junit.Assert.*;
import org.jenkinsci.plugins.appdetector.util.Utils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class AppDetectorParamaterDefinitionTest {
  private AppDetectorParamaterDefinition param;

  @Mocked
  final Utils utils = null;

  @Mocked
  private AppLabelSet labels;

  @Before
  public void init() {
    param = new AppDetectorParamaterDefinition("test", "test", "");
  }

  @Test
  public void getSortedVersionList() throws Exception {

    new Expectations() {{
      Utils.getApplicationLabels();
      result = labels;

      labels.getSortedAppVersions("test");
      result = new ArrayList<String>() {
        {
          add("8.3.1b");
          add("8.3");
          add("8");
        }
      };
    }};

    List<String> actual = param.getSortedVersionList();
    String[] expected = {"8.3.1b", "8.3", "8"};
    assertThat(actual, is(contains(expected)));
  }
}
