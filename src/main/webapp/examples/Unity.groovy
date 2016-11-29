def unityList = [];
def appDir = "/Applications/";

def unityPathList = runExternalCommand("ls", appDir).split("\n").findAll { it.startsWith("Unity")};

unityPathList.each {
  def version = runExternalCommand("/usr/libexec/PlistBuddy", "-c", "Print :CFBundleVersion", appDir + it + "/Unity.app/Contents/Info.plist").split("\n")[0];
  def unityHome = appDir + it + "/Unity.app";
  unityList.add([version: version, home: unityHome]);
}

return JsonOutput.prettyPrint(JsonOutput.toJson(unityList));
