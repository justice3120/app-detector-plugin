with-sofrware-plugin
====================

# About
XcodeとUnityのバージョンを指定するだけで、よろしくやってくれるJenkinsプラグイン。

## やっていること
 - Jenkinsマスター及びスレーブがオンラインになったタイミングで、それぞでのノードにXcode/Unityがインストールされているか調べ、されていれば対応するバージョンとともにノードにラベルを付加する。
   - インストールされているか調べる方法は、[install-unity](https://github.com/sttz/install-unity)/[xcode-install](https://github.com/neonichu/xcode-install)と同等。
 - BuildWrapperにバージョンが指定された場合、タスクに対応するラベルをLabelAssignmentActionとして付加する。(「実行するノードを制限」が指定された場合と同じ状態にする)
 - ビルドの環境変数に指定されたバージョンのXcode/Unityのホームディレクトリーを設定する。
   - Xcode: DEVELOPER_DIR
   - Unity: UNITY_HOME
