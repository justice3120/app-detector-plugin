def xcodeList = [];

def xcodePathList = ["/usr/bin/mdfind", "kMDItemCFBundleIdentifier == 'com.apple.dt.Xcode'"].execute().text.split("\n");

xcodePathList.each {
  def version = ["env", "DEVELOPER_DIR=" + it, "/usr/bin/xcodebuild", "-version"].execute().text.split("\n")[0].split(" ")[1];
  xcodeList.add([version: version, home: it]);
}

return JsonOutput.prettyPrint(JsonOutput.toJson(xcodeList));
