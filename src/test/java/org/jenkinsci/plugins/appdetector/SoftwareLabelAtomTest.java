package org.jenkinsci.plugins.appdetector;

import org.junit.*;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class AppLabelAtomTest {
  private AppLabelAtom label;

  @Before
  public void init() {
    label =new AppLabelAtom("Xcode", "8.0", "/Applications/Xcode.app");
  }

  @Test
  public void serialize() throws Exception {
    String serialized = AppLabelAtom.serialize(label);
    String expected = "Xcode:8.0:/Applications/Xcode.app";

    assertThat(serialized, is(expected));
  }

  @Test
  public void deserialize() throws Exception {
    String serialized = AppLabelAtom.serialize(label);
    AppLabelAtom deserialized = AppLabelAtom.deserialize(serialized);

    assertThat(deserialized.getApplication(), is("Xcode"));
    assertThat(deserialized.getVersion(), is("8.0"));
    assertThat(deserialized.getHome(), is("/Applications/Xcode.app"));
  }
}
