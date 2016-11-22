package org.jenkinsci.plugins.appdetector;

import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class SoftwareLabelAtomTest {
  private SoftwareLabelAtom label;

  @Before
  public void init() {
    label =new SoftwareLabelAtom("Xcode", "8.0", "/Applications/Xcode.app");
  }

  @Test
  public void serialize() throws Exception {
    String serialized = SoftwareLabelAtom.serialize(label);
    String expected = "Xcode:8.0:/Applications/Xcode.app";

    assertThat(serialized, is(expected));
  }

  @Test
  public void deserialize() throws Exception {
    String serialized = SoftwareLabelAtom.serialize(label);
    SoftwareLabelAtom deserialized = SoftwareLabelAtom.deserialize(serialized);

    assertThat(deserialized.getSoftware(), is("Xcode"));
    assertThat(deserialized.getVersion(), is("8.0"));
    assertThat(deserialized.getHome(), is("/Applications/Xcode.app"));
  }
}
