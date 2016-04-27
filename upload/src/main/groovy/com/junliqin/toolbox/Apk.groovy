package com.junliqin.toolbox

class Apk {
    String name
    File file
    String version
    String build
    String changelog
    File icon

    Apk(){

    }

    @Override
    String toString() {
        return " " + name + "-" + version + "-" + build;
    }

    static Apk parse(FirExtension firExt, Object variant, File apkFile) {
        Apk apk = new Apk();
        apk.file = apkFile
        apk.name = firExt.appName
        apk.version = variant.versionName
        apk.build = variant.versionCode
        apk.changelog = firExt.changeLog
        apk.icon = firExt.iconFile

        return apk;

    }

    public HashMap<String, Object> getParams() {
        HashMap<String, Object> params = new HashMap<String, Object>()
        if(version != null) {
            params.put("x:version", version)
        }
        if(build != null) {
            params.put("x:build", build)
        }
        if(changelog != null) {
            params.put("x:changelog", changelog)
        }
        if (name != null) {
            params.put("x:name", name)
        }
        return params
    }

}
