package org.jenkinsci.plugins.withsoftware.model;

public class Software {
  private String name;
  private String version;

  public Software(String name, String version) {
    this.name = name;
    this.version = version;
  }

  public String getName() {
    return name;
  }

  public String getVersion() {
    return version;
  }
}
