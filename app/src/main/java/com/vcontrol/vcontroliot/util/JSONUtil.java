package com.vcontrol.vcontroliot.util;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by JimCharles on 2016/12/13.
 */

public class JSONUtil
{

    private static final String TAG = "JSONUtil";

    /**
     * 获取json内容
     * @param  url
     * @return JSONArray
     * @throws JSONException
     */
    public static JSONObject getJSON(String url) throws JSONException, Exception {

        return new JSONObject(getRequest(url));
    }

    /**
     * 向api发送get请求，返回从后台取得的信息。
     *
     * @param url
     * @return String
     */
    protected static String getRequest(String url) throws Exception {
        return getRequest(url, new DefaultHttpClient(new BasicHttpParams()));
    }

    /**
     * 向api发送get请求，返回从后台取得的信息。
     *
     * @param url
     * @param client
     * @return String
     */
    protected static String getRequest(String url, DefaultHttpClient client) throws Exception {
        String result = null;
        int statusCode = 0;
        HttpGet getMethod = new HttpGet(url);
        Log.d(TAG, "do the getRequest,url="+url+"");
        try {
            //getMethod.setHeader("User-Agent", USER_AGENT);

            HttpResponse httpResponse = client.execute(getMethod);
            //statusCode == 200 正常
            statusCode = httpResponse.getStatusLine().getStatusCode();
            Log.d(TAG, "statuscode = "+statusCode);
            //处理返回的httpResponse信息
            result = retrieveInputStream(httpResponse.getEntity());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            throw new Exception(e);
        } finally {
            getMethod.abort();
        }
        return result;
    }

    /**
     * 处理httpResponse信息,返回String
     *
     * @param httpEntity
     * @return String
     */
    protected static String retrieveInputStream(HttpEntity httpEntity) {

        int length = (int) httpEntity.getContentLength();
        //the number of bytes of the content, or a negative number if unknown. If the content length is known but exceeds Long.MAX_VALUE, a negative number is returned.
        //length==-1，下面这句报错，println needs a message
        if (length < 0) length = 10000;
        StringBuffer stringBuffer = new StringBuffer(length);
        try {
            InputStreamReader inputStreamReader = new InputStreamReader(httpEntity.getContent(), HTTP.UTF_8);
            char buffer[] = new char[length];
            int count;
            while ((count = inputStreamReader.read(buffer, 0, length - 1)) > 0) {
                stringBuffer.append(buffer, 0, count);
            }
        } catch (IllegalStateException | IOException e) {
            Log.e(TAG, e.getMessage());
        }
        return stringBuffer.toString();
    }

    public static String parseIatResult(String json) {
        StringBuffer ret = new StringBuffer() ;
        try {
            JSONTokener tokener = new JSONTokener(json) ;
            JSONObject joResult = new JSONObject(tokener) ;

            JSONArray words = joResult.getJSONArray("ws" );
            for (int i = 0; i < words.length(); i++) {
                // 转写结果词，默认使用第一个结果
                JSONArray items = words.getJSONObject(i).getJSONArray("cw" );
                JSONObject obj = items.getJSONObject(0 );
                ret.append(obj.getString("w" ));
//                  如果需要多候选结果，解析数组其他字段
//                 for(int j = 0; j < items.length(); j++)
//                 {
//                      JSONObject obj = items.getJSONObject(j);
//                      ret.append(obj.getString("w"));
//                 }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ret.toString();
    }

    public static String parseGrammarResult(String json) {
        StringBuffer ret = new StringBuffer() ;
        try {
            JSONTokener tokener = new JSONTokener(json) ;
            JSONObject joResult = new JSONObject(tokener) ;

            JSONArray words = joResult.getJSONArray("ws" );
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw" );
                for (int j = 0; j < items.length() ; j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if (obj.getString("w").contains( "nomatch"))
                    {
                        ret.append( "没有匹配结果.") ;
                        return ret.toString();
                    }
                    ret.append( "【结果】" + obj.getString("w" ));
                    ret.append("【置信度】 " + obj.getInt("sc" ));
                    ret.append("\n ");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            ret.append(" 没有匹配结果 .");
        }
        return ret.toString();
    }

    public static String parseLocalGrammarResult(String json) {
        StringBuffer ret = new StringBuffer() ;
        try {
            JSONTokener tokener = new JSONTokener(json) ;
            JSONObject joResult = new JSONObject(tokener) ;

            JSONArray words = joResult.getJSONArray("ws" );
            for (int i = 0; i < words.length(); i++) {
                JSONArray items = words.getJSONObject(i).getJSONArray("cw" );
                for (int j = 0; j < items.length() ; j++)
                {
                    JSONObject obj = items.getJSONObject(j);
                    if (obj.getString("w").contains( "nomatch"))
                    {
                        ret.append( "没有匹配结果.") ;
                        return ret.toString();
                    }
                    ret.append( "【结果】" + obj.getString("w" ));
                    ret.append("\n ");
                }
            }
            ret.append("【置信度】 " + joResult.optInt("sc" ));

        } catch (Exception e) {
            e.printStackTrace();
            ret.append(" 没有匹配结果 .");
        }
        return ret.toString();
    }
}