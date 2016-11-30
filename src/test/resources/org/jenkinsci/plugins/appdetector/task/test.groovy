def applist = [];

applist.add([version: "1.0", home: "/hoge"]);

return JsonOutput.prettyPrint(JsonOutput.toJson(applist));
