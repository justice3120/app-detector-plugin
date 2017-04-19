def unityList = [];
def appDir = "/Applications/";

def unityPathList = ["ls", appDir].execute().text.split("\n").findAll { it.startsWith("Unity")};

unityPathList.each {
  def version = ["/usr/libexec/PlistBuddy", "-c", "Print :CFBundleVersion", appDir + it + "/Unity.app/Contents/Info.plist"].execute().text.split("\n")[0];
  def unityHome = appDir + it + "/Unity.app";
  unityList.add([version: version, home: unityHome]);
}

return JsonOutput.prettyPrint(JsonOutput.toJson(unityList));
