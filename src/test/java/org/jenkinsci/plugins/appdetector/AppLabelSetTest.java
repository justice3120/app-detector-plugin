package org.jenkinsci.plugins.appdetector;

import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

import java.util.Set;

public class AppLabelSetTest {
  private AppLabelSet labels;

  @Before
  public void init() {
    labels = new AppLabelSet();
    labels.add(new AppLabelAtom("Xcode", "8.0", "/Applications/Xcode.app"));
    labels.add(new AppLabelAtom("Xcode", "7.3.1", "/"));
    labels.add(new AppLabelAtom("Unity", "5.4.0b24", "/"));
    labels.add(new AppLabelAtom("Unity", "5.3.5f1", "/"));
  }

  @Test
  public void getAppVersions() throws Exception {
    Set<String> xcodeVersions = labels.getAppVersions("Xcode");

    assertThat(xcodeVersions, hasItems("8.0", "7.3.1"));
    assertThat(xcodeVersions, not(anyOf(hasItems("5.4.0b24"), hasItems("5.3.5f1"))));
  }

  @Test
  public void getApplicationLabel() throws Exception {
    AppLabelAtom label = labels.getApplicationLabel("Xcode", "8.0");

    assertThat(label, notNullValue());
    assertThat(label.getApplication(), is("Xcode"));
    assertThat(label.getVersion(), is("8.0"));
    assertThat(label.getHome(), is("/Applications/Xcode.app"));
  }
}
