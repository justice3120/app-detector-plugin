def unityList = [];
def appDir = "C:\\Program Files";

def unityPathList = ["cmd", "/c", "dir", appDir, "/A:d", "/b"].execute().text.split("\r\n").findAll { it.startsWith("Unity")};

unityPathList.each {
  def version = it.minus("Unity");
  def unityHome = appDir + "\\" + it;
  unityList.add([version: version, home: unityHome]);
}

return JsonOutput.prettyPrint(JsonOutput.toJson(unityList));
