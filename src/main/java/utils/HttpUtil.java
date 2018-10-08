package utils;


import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.serializer.SerializerFeature;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.exception.ExceptionUtils;
import org.apache.http.Consts;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.TrustStrategy;
import org.apache.http.util.CharsetUtils;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.*;

/**
 * @Author ：yaxuSong
 * @Description:
 * @Date: 13:42 2018/3/21
 * @Modified by:
 */
@Slf4j
public class HttpUtil {

        /**
         * 默认的编码格式
         */
        private static final String DEFAULT_CHARSET = "UTF-8";

        /**
         * time out 时间
         */
        private static final int HTTP_CONNECTION_TIMEOUT = 10000;

        /**
         * http连接超时 单位 ms
         */
        private static final int HTTP_SOCKET_TIMEOUT = 120000;

        private static BasicCookieStore cookieStore = new BasicCookieStore();

        private static String userSessionId = StringUtils.EMPTY;

        /**
         * 发送Get 请求
         *
         * @param url
         * @param params
         * @param headers
         * @return
         * @throws Exception
         */
        public static String get(String url, Map<String, String> params, Map<String, String> headers) throws Exception {
            log.info("url=" + url + ",param=" + params + ",header=" + headers);

            CloseableHttpClient httpClient = getHttpClient();

            RequestBuilder builder = RequestBuilder.get(url);
            if (params != null && !params.isEmpty()) {
                // 有值
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    builder.addParameter(key, params.get(key));
                }
            }

            if (headers != null && !headers.isEmpty()) {
                Set<String> keys = headers.keySet();
                for (String key : keys) {
                    builder.addHeader(key, params.get(key));
                }
            }

            //加用户验证信息
            addUserSessionId(builder);

            CloseableHttpResponse response = null;
            try {
                builder.setCharset(CharsetUtils.get(DEFAULT_CHARSET));
                RequestConfig config = RequestConfig.custom().setSocketTimeout(HTTP_SOCKET_TIMEOUT).setConnectTimeout(HTTP_CONNECTION_TIMEOUT).build();
                builder.setConfig(config);

                response = httpClient.execute(builder.build());
                handlerStatus(response);

                HttpEntity entity = response.getEntity();
                showCookie();

                return EntityUtils.toString(entity, DEFAULT_CHARSET);
            } catch (Exception e) {
                throw new Exception("http get fail -->"+ ExceptionUtils.getMessage(e),e);
            } finally {
                close(httpClient, response);
            }
        }

        public static void addUserSessionIdVertify(RequestBuilder builder, String serviceName) {
            String timeStr = TimeUtil.getDefaultTimeStr(new Date());
            int  maxNum = 62;
            int i;
            int count = 0;
            char[] str = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K',
                    'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W',
                    'X', 'Y', 'Z','a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
                    'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
                    'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
            StringBuffer pwd = new StringBuffer("");
            Random r = new Random();
            while(count < 8){
                i = Math.abs(r.nextInt(maxNum));
                if (i >= 0 && i < str.length) {
                    pwd.append(str[i]);
                    count ++;
                }
            }
            builder.addHeader("mvTrackId", timeStr+"_"+serviceName+"_bijiajia_"+pwd.toString());
        }

        private static void addUserSessionId(RequestBuilder builder) {
            if(StringUtils.isNotBlank(userSessionId)){
                log.debug("user session id" + userSessionId);
                builder.addHeader("sessionId", userSessionId);
//                builder.addHeader("mvTrackId", "20180718105632_IdNamePhoneCmccCheck_xxKeji_sa23jhfu");
            }
        }

        /**
         * 简单的GET请求
         *
         * @param url
         * @return
         * @throws Exception
         */
        public static String get(String url) throws Exception {
            try {
                return get(url, null);
            } catch (Exception e) {
                throw new Exception("http get fail", e);
            }
        }

        /**
         * 不带Head 的GET请求
         *
         * @return 返回类型:
         * @throws Exception
         * @description 功能描述: get 请求
         */
        public static String get(String url, Map<String, String> params) throws Exception {
            try {
                return get(url, params, null);
            } catch (Exception e) {
                throw new Exception("http get fail", e);
            }
        }

        /**
         * 发送请求
         *
         * @param url
         * @return
         * @throws Exception
         */
        public static String post(String url) throws Exception {
            Map<String, String> params = new HashMap<String, String>();
            return post(url, params);
        }


    public static String postYiMeiSms(String url, String data,String appId) throws Exception {
        log.info("url=" + url + ",data=" + data);

        CloseableHttpClient httpClient = getHttpClient();

        RequestBuilder builder = RequestBuilder.post(url);

        List<NameValuePair> paramsa=new ArrayList<NameValuePair>();

        //加用户验证信息
        builder.addHeader("appId", appId);
        CloseableHttpResponse response = null;
        try {
            builder.setCharset(CharsetUtils.get(DEFAULT_CHARSET));
            RequestConfig config = RequestConfig.custom().setSocketTimeout(HTTP_SOCKET_TIMEOUT).setConnectTimeout(HTTP_CONNECTION_TIMEOUT).build();
            builder.setConfig(config);
            builder.setEntity(new StringEntity(data, Consts.UTF_8));
            response = httpClient.execute(builder.build());
            handlerStatus(response);
            log.debug("http post rsult :" + response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            showCookie();
            String result= EntityUtils.toString(entity, DEFAULT_CHARSET);
            return result;
        } catch (Exception e) {

            throw new Exception("http post fail -->"+ ExceptionUtils.getMessage(e),e);
        } finally {
            close(httpClient, response);
        }
    }
        /**
         * @return 返回类型:
         * @throws Exception
         * @description 功能描述: POST 请求
         */
        public static String post(String url, Map<String, String> params) throws Exception {
            log.info("url=" + url + ",param=" + params);

            CloseableHttpClient httpClient = getHttpClient();

            RequestBuilder builder = RequestBuilder.post(url);

            List<NameValuePair> paramsa=new ArrayList<NameValuePair>();

            if (params != null && !params.isEmpty()) {
                // 有值
                Set<String> keys = params.keySet();
                for (String key : keys) {
                    paramsa.add(new BasicNameValuePair(key,params.get(key)));

                }
            }
            //加用户验证信息
            addUserSessionId(builder);

            CloseableHttpResponse response = null;
            try {
                builder.setCharset(CharsetUtils.get(DEFAULT_CHARSET));
                RequestConfig config = RequestConfig.custom().setSocketTimeout(HTTP_SOCKET_TIMEOUT).setConnectTimeout(HTTP_CONNECTION_TIMEOUT).build();
                builder.setConfig(config);
                builder.setEntity(new UrlEncodedFormEntity(paramsa, Consts.UTF_8));


                response = httpClient.execute(builder.build());
                handlerStatus(response);
                log.debug("http post rsult :" + response.getStatusLine().getStatusCode());

                HttpEntity entity = response.getEntity();
                showCookie();
                String result= EntityUtils.toString(entity, DEFAULT_CHARSET);
                return result;
            } catch (Exception e) {

                throw new Exception("http post fail -->"+ ExceptionUtils.getMessage(e),e);
            } finally {
                close(httpClient, response);
            }
        }


    public static SSLContext configureHttpClient()
    {
        try
        {
            SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, new TrustStrategy()
            {
                // 信任所有
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException{
                    return true;
                }
            }).build();

            return sslContext;

        }catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }


    private static CloseableHttpClient getHttpSSLClient() {
        SSLConnectionSocketFactory socketFactory = new SSLConnectionSocketFactory(configureHttpClient(), new HostnameVerifier() {

            public boolean verify(String s, SSLSession sslSession) {
                return true;
            }
        });

        CloseableHttpClient httpClient = HttpClientBuilder.create().setSSLSocketFactory(socketFactory).setDefaultCookieStore(cookieStore).build();
        return httpClient;
    }
    /**
     * @return 返回类型:
     * @throws Exception
     * @description 功能描述: POST 请求
     */
    public static String postString(String url,String bodyStr,String serviceName) throws Exception {
        log.info("url=" + url + ",bodyStr=" + bodyStr);

        CloseableHttpClient httpClient = getHttpSSLClient();

        RequestBuilder builder = RequestBuilder.post(url);

        //加请求头验证信息
        addUserSessionIdVertify(builder, serviceName);

        CloseableHttpResponse response = null;
        try {
            builder.setCharset(CharsetUtils.get(DEFAULT_CHARSET));
            RequestConfig config = RequestConfig.custom().setSocketTimeout(HTTP_SOCKET_TIMEOUT).setConnectTimeout(HTTP_CONNECTION_TIMEOUT).build();
            builder.setConfig(config);
            builder.setEntity(new StringEntity(bodyStr, Consts.UTF_8));


            response = httpClient.execute(builder.build());
            handlerStatus(response);
            log.debug("http post rsult :" + response.getStatusLine().getStatusCode());

            HttpEntity entity = response.getEntity();
            showCookie();
            String result= EntityUtils.toString(entity, DEFAULT_CHARSET);
            return result;
        } catch (Exception e) {

            throw new Exception("http post fail -->"+ ExceptionUtils.getMessage(e),e);
        } finally {
            close(httpClient, response);
        }
    }

        /**
         * @return 返回类型:
         * @throws Exception
         * @description 功能描述: POST 请求
         */
        public static String postJSON(String url, JSONObject json) throws Exception {

            if (url == null || json == null) {
                log.error("url is empty");
                throw new IllegalArgumentException("url or json can not be null");
            }

            Map<String, String> params = new HashMap<String, String>();

            Iterator<String> ite = json.keySet().iterator();
            String key = StringUtils.EMPTY;
            while (ite.hasNext()) {
                key = ite.next();
                Object obj = json.get(key);

                if (obj instanceof JSONArray) {
                    params.put(key, String.valueOf(obj));
                } else {
                    params.put(key, String.valueOf(obj));
                }
            }
            return post(url, params);
        }

        /**
         * Status 进行处理d
         *
         * @param response
         * @throws Exception
         */
        private static void handlerStatus(CloseableHttpResponse response) throws Exception {
            int statusCode = response.getStatusLine().getStatusCode();
            log.debug("http get result :" + statusCode);
            if (statusCode == HttpStatus.SC_OK) {
                log.debug("response success, ok!");
            }else if (statusCode == HttpStatus.SC_MOVED_PERMANENTLY) {
                log.error("user not has permission");
            } else if (statusCode == HttpStatus.SC_UNAUTHORIZED) {
                throw new Exception("user is not login");
            } else {
                try {
                    String content = EntityUtils.toString(response.getEntity());
                    //报500错误的时候，控制台，可以看到错误信息
                    log.info(content);
                    log.debug("error page --> " + content);
                } catch (Exception e) {
                    log.debug("http error " + ExceptionUtils.getRootCauseMessage(e));
                }

                throw new Exception(String.valueOf(statusCode));
            }
        }

        /**
         * 上传媒体文件
         *
         * @param url
         * @param file
         * @return
         * @throws Exception
         */
        public static String upload(String url, File file) throws Exception {


            CloseableHttpClient httpClient = getHttpClient();
            CloseableHttpResponse response = null;
            try {
                FileBody fileBody = new FileBody(file);
                HttpEntity entity = MultipartEntityBuilder.create().setMode(HttpMultipartMode.BROWSER_COMPATIBLE).addPart("files", fileBody).setCharset(CharsetUtils.get(DEFAULT_CHARSET)).build();

                RequestBuilder builder = RequestBuilder.post(url);
                builder.setEntity(entity);

                builder.setCharset(CharsetUtils.get(DEFAULT_CHARSET));
                RequestConfig config = RequestConfig.custom().setSocketTimeout(HTTP_SOCKET_TIMEOUT).setConnectTimeout(HTTP_CONNECTION_TIMEOUT).build();
                builder.setConfig(config);

                //加用户验证信息
                addUserSessionId(builder);

                response = httpClient.execute(builder.build());

                log.info("http post rsult :" + response.getStatusLine().getStatusCode());

                HttpEntity responseEntity = response.getEntity();
                showCookie();
                String result= EntityUtils.toString(responseEntity, DEFAULT_CHARSET);
                return result;

            } catch (Exception e) {
                throw new Exception("http upload fail", e);
            } finally {
                close(httpClient, response);
            }
        }

        /**
         * 下载文件
         *
         * @param url
         * @param saveFilePath
         * @throws Exception
         */
        public static void download(String url, String saveFilePath) throws Exception {

            CloseableHttpClient httpClient = getHttpClient();
            CloseableHttpResponse response = null;

            OutputStream out = null;
            InputStream in = null;
            try {
                RequestBuilder builder = RequestBuilder.get(url).setCharset(CharsetUtils.get(DEFAULT_CHARSET));
                //加用户验证信息
                addUserSessionId(builder);

                response = httpClient.execute(builder.build());

                handlerStatus(response);
                HttpEntity entity = response.getEntity();

                File file = new File(saveFilePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                in = entity.getContent();
                out = new FileOutputStream(file);

                byte[] buffer = new byte[4096];
                int readLength = 0;
                while ((readLength = in.read(buffer)) > 0) {
                    byte[] bytes = new byte[readLength];
                    System.arraycopy(buffer, 0, bytes, 0, readLength);
                    out.write(bytes);
                }

                out.flush();
            } catch (Exception e) {
                throw new Exception("http download fail", e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }

                    if (in != null) {
                        in.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                close(httpClient, response);
            }
        }

        /**
         * 下载文件
         * @param url
         * @param saveFilePath
         * @param params
         * @throws Exception
         */
        public static void download(String url, String saveFilePath,Map<String, String> params) throws Exception {

            CloseableHttpClient httpClient = getHttpClient();
            CloseableHttpResponse response = null;

            OutputStream out = null;
            InputStream in = null;
            try {
                RequestBuilder builder = RequestBuilder.get(url).setCharset(CharsetUtils.get(DEFAULT_CHARSET));
                if (params != null && !params.isEmpty()) {
                    // 有值
                    Set<String> keys = params.keySet();
                    for (String key : keys) {
                        builder.addParameter(key, params.get(key));
                    }
                }
                //加用户验证信息
                addUserSessionId(builder);

                response = httpClient.execute(builder.build());

                handlerStatus(response);
                HttpEntity entity = response.getEntity();

                File file = new File(saveFilePath);
                if (!file.exists()) {
                    file.createNewFile();
                }
                in = entity.getContent();
                out = new FileOutputStream(file);

                byte[] buffer = new byte[4096];
                int readLength = 0;
                while ((readLength = in.read(buffer)) > 0) {
                    byte[] bytes = new byte[readLength];
                    System.arraycopy(buffer, 0, bytes, 0, readLength);
                    out.write(bytes);
                }

                out.flush();
            } catch (Exception e) {
                throw new Exception("http download fail", e);
            } finally {
                try {
                    if (out != null) {
                        out.close();
                    }

                    if (in != null) {
                        in.close();
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                close(httpClient, response);
            }
        }

        /**
         * 关闭连接
         *
         * @param httpClient
         * @param response
         */
        private static void close(CloseableHttpClient httpClient, CloseableHttpResponse response) {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private static CloseableHttpClient getHttpClient() {
            CloseableHttpClient httpClient = HttpClientBuilder.create().setDefaultCookieStore(cookieStore).build();
            return httpClient;
        }

        private static void showCookie() {
            List<Cookie> cookies = cookieStore.getCookies();
            if (cookies.isEmpty()) {
                log.info("http get not has cookie");
            } else {
                for (int i = 0; i < cookies.size(); i++) {
                    log.debug("- " + cookies.get(i).toString());
                }
            }
        }

        public static void setUserSessionId(String userSessionId) {
            HttpUtil.userSessionId = userSessionId;
        }

        public static void outJSON(HttpServletResponse response, Object obj) throws Exception {
            response.setContentType("application/json; charset=utf-8");
            PrintWriter writer = response.getWriter();
            writer.print(JSONObject.toJSONString(obj, SerializerFeature.WriteMapNullValue,
                    SerializerFeature.WriteDateUseDateFormat));
            writer.close();
            response.flushBuffer();
        }
}
