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
  public void getXcodeVersions() throws Exception {
    Set<String> xcodeVersions = labels.getXcodeVersions();

    assertThat(xcodeVersions, hasItems("8.0", "7.3.1"));
    assertThat(xcodeVersions, not(anyOf(hasItems("5.4.0b24"), hasItems("5.3.5f1"))));
  }

  @Test
  public void getUnityVersions() throws Exception {
    Set<String> unityVersions = labels.getUnityVersions();

    assertThat(unityVersions, hasItems("5.4.0b24", "5.3.5f1"));
    assertThat(unityVersions, not(anyOf(hasItems("8.0"), hasItems("7.3.1"))));
  }

  @Test
  public void getSoftwareLabel() throws Exception {
    AppLabelAtom label = labels.getSoftwareLabel("Xcode", "8.0");

    assertThat(label, notNullValue());
    assertThat(label.getSoftware(), is("Xcode"));
    assertThat(label.getVersion(), is("8.0"));
    assertThat(label.getHome(), is("/Applications/Xcode.app"));
  }
}
