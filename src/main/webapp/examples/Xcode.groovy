def xcodeList = [];

def xcodePathList = runExternalCommand("/usr/bin/mdfind", "kMDItemCFBundleIdentifier == 'com.apple.dt.Xcode'").split("\n");

xcodePathList.each {
  def version = runExternalCommand("env", "DEVELOPER_DIR=" + it, "/usr/bin/xcodebuild", "-version").split("\n")[0].split(" ")[1];
  xcodeList.add([version: version, home: it]);
}

return JsonOutput.prettyPrint(JsonOutput.toJson(xcodeList));
