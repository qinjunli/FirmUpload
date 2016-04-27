package com.junliqin.toolbox

import com.alibaba.fastjson.JSON
import com.alibaba.fastjson.JSONObject
import org.apache.http.HttpResponse
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpPost
import org.apache.http.entity.StringEntity
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.util.EntityUtils
import org.gradle.api.DefaultTask
import org.gradle.api.tasks.TaskAction

class FirTask extends DefaultTask {
    private final String API_END_POINT = "http://api.fir.im/"

//    @InputFile
    File inputFile

    Object variant;


    Apk inputApk;

    @TaskAction
    public void upload() {
        String token = project.fir.userToken
        String bundleId = project.fir.bundleId

        FirExtension firExt = project.fir

        println("begin parse apk, variant is " + variant + " input file is " + inputFile.getAbsolutePath())
        inputApk = Apk.parse(firExt,variant, inputFile)
        println("end parse apk")
        JSONObject infoObj = getAppInfo(token, bundleId)

        if (infoObj != null && infoObj.containsKey("cert")) {
            JSONObject certObj = infoObj.getJSONObject("cert")
            if (certObj.containsKey("binary") && inputApk.file != null) {
                JSONObject  binaryObj = certObj.getJSONObject("binary")
                String up_url = binaryObj.getString("upload_url")
                String up_token = binaryObj.getString("token")
                String up_key = binaryObj.getString("key")
                JSONObject reusltobj = uploadApk(up_url, up_key, up_token)
                println("apk上传结果:" + reusltobj)
            }

            if (certObj.containsKey("icon") && inputApk.icon != null) {
                JSONObject iconObj = certObj.getJSONObject("icon")
                String up_url = iconObj.getString("upload_url")
                String up_token = iconObj.getString("token")
                String up_key = iconObj.getString("key")
                JSONObject reusltobj = uploadIcon(up_url, up_key, up_token)
                println("icon上传结果:" + reusltobj)
            }
        }
    }

//    private JSONObject getAppInfo(String endpoint, String userToken, String bundleId) {
//        OkHttpClient client = new OkHttpClient();
////        client.interceptors().add(new LoggingInterceptor())
//
//        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
//
//        JSONObject jsonObject = new JSONObject();
//        jsonObject.put("api_token", userToken)
//        jsonObject.put("type", "android")
//        jsonObject.put("bundle_id", bundleId)
//
//        RequestBody body = RequestBody.create(JSONType, jsonObject.toJSONString())
//        println("getAppInfo request body: " + jsonObject.toJSONString())
//        println("getAppInfo request url: " + endpoint)
//
//        HttpUrl url = HttpUrl.parse("http://api.fir.im/apps/helo")
//        Request request = new Request.Builder()
//                .url(url)
//                .header("Content-Type","application/json;charset=utf-8")
//                .post(body)
//                .build()
//        println("request headers is  " + request.headers())
//        Response response = client.newCall(request).execute()
//        println("reponse header is  " + response.headers())
//        println("reponse staus code  is  " + response.code())
//        if (response == null || response.body() == null) return null
//        String is = response.body().string()
//        println("getAppinfo result:" + is)
//        JSONObject json = JSON.parseObject(is)
//        return json
//    }


    private JSONObject getAppInfo(String userToken, String bundleId) {
        HttpClient client = new DefaultHttpClient()
        HttpPost post = new HttpPost('http://api.fir.im/apps')
        post.setHeader('Content-Type', 'application/json')
        post.setEntity(new StringEntity("{\"type\":\"android\", \"bundle_id\":\"${bundleId}\", \"api_token\":\"${userToken}\"}"))
        HttpResponse response = client.execute(post)
        JSONObject json = JSON.parseObject(EntityUtils.toString(response.entity))
        return json
    }

//    private JSONObject uploadApk(String url, String key, String token) {
//        println("upload apk to " + url)
//        OkHttpClient client = new OkHttpClient();
////        client.setConnectTimeout(10, TimeUnit.SECONDS);
////        client.setReadTimeout(60, TimeUnit.SECONDS);
//        MediaType JSONType = MediaType.parse("application/json; charset=utf-8");
////        MultipartBuilder build = new MultipartBuilder().type(MultipartBuilder.FORM)
////        build.addFormDataPart("key", key)
////        build.addFormDataPart("token", token)
////        build.addFormDataPart("file",
////                apk.file.name,
////                RequestBody.create(
////                        MediaType.parse("application/vnd.android.package-archive"),
////                        apk.file)
////        )
//
////        JSONObject jsonObject = new JSONObject();
////        jsonObject.put("key", key);
////        jsonObject.put("token", token);
////        jsonObject.put("file", inputApk.file.name);
//
//        HashMap<String, Object> params = inputApk.getParams()
//        params.put("key", key)
//        params.put("token", token)
////        for (String k : params.keySet()) {
////            println("add part key: " + k + " value: " + params.get(k))
//////            build.addFormDataPart(k, params.get(k))
////            jsonObject.put(k, params.get(k))
////        }
//        RequestBody body = RequestBody.create(JSONType, jsonObject.toJSONString())
//        println("request body string: " + jsonObject.toJSONString())
//        Request request = new Request.Builder().url(url).post(body).build()
//
//        Response response = client.newCall(request).execute()
//        if (response == null || response.body() == null) return null
//        String is = response.body().string()
//        println("upload result:" + is)
//        JSONObject json = JSON.parseObject(is)
//        return json
//    }

    private JSONObject uploadApk(String url, String key, String token) {
        HashMap<String, Object> params = inputApk.getParams()
        params.put("key", key)
        params.put("token", token)
        if(inputApk.file != null){
            params.put("file", inputApk.file)
        }


        String is = uploadFile(url, params)
        println("upload result:" + is)
        JSONObject json = JSON.parseObject(is)
        return json
    }

    private JSONObject uploadIcon(String url, String key, String token) {
        HashMap<String, Object> params = inputApk.getParams()
        params.put("key", key)
        params.put("token", token)
        if(inputApk.icon != null){
            params.put("file", inputApk.icon)
        }

        String is = uploadFile(url, params)
        println("upload result:" + is)
        JSONObject json = JSON.parseObject(is)
        return json
    }

    static String uploadFile(String url, HashMap<String, Object> params) {
        HttpClient client = new DefaultHttpClient()
        println("upload file url : " + url)
        HttpPost post = new HttpPost(url)
        SimpleMultipartEntity entity = new SimpleMultipartEntity()

        String fileKey
        File fileValue
        params.each { key, value ->
            if (value instanceof File) {
                fileKey = key
                fileValue = value
            } else {
                entity.addPart(key, value as String)
            }
        }
        if (fileKey && fileValue) {
            entity.addPart(fileKey, fileValue, true)
        }
        post.setEntity(entity);
        HttpResponse response = client.execute(post);
        return EntityUtils.toString(response.entity)
    }


}