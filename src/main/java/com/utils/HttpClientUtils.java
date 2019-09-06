package com.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.conn.PoolingHttpClientConnectionManager;
import org.apache.http.util.EntityUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.UUID;

@Slf4j
@Component
public class HttpClientUtils {

    @Value("${imgPath}")
    private String imgPath;

    private PoolingHttpClientConnectionManager manager = new PoolingHttpClientConnectionManager();

    public HttpClientUtils() {
        //设置最大连接数
        manager.setMaxTotal(200);
        //设置每个主机的并发数
        manager.setDefaultMaxPerRoute(20);
    }

    /**
     * 获取网页具体内容
     */
    public String getContent(String url) {
        // 获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(manager).build();

        // 声明httpGet请求对象
        HttpGet httpGet = new HttpGet(url);
        httpGet.addHeader("user-agent", "Mozilla/5.0");
        // 设置请求参数RequestConfig
        httpGet.setConfig(this.getConfig());

        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            // 使用HttpClient发起请求，返回response
            // 解析response返回数据
            // 如果response。getEntity获取的结果是空，在执行EntityUtils.toString会报错
            // 需要对Entity进行非空的判断
            if (response.getStatusLine().getStatusCode() == 200 && response.getEntity() != null) {
                return EntityUtils.toString(response.getEntity(), "UTF-8");
            }
        } catch (Exception e) {
            log.error("获取网页内容异常", e);
        }
        return null;
    }

    /**
     * 获取图片
     */
    public String getImage(String url) {
        // 获取HttpClient对象
        CloseableHttpClient httpClient = HttpClients.custom().setConnectionManager(manager).build();
        // 声明httpGet请求对象
        HttpGet httpGet = new HttpGet(url);
        // 设置请求参数RequestConfig
        httpGet.setConfig(this.getConfig());
        // 使用HttpClient发起请求，返回response
        try (CloseableHttpResponse response = httpClient.execute(httpGet)) {
            // 解析response下载图片
            if (response.getStatusLine().getStatusCode() == 200) {
                // 获取文件类型
                String extName = url.substring(url.lastIndexOf("."));
                // 使用uuid生成图片名
                String imageName = UUID.randomUUID().toString() + extName;
                // 声明输出的文件
                OutputStream outstream = new FileOutputStream(new File(imgPath + imageName));
                // 使用响应体输出文件
                response.getEntity().writeTo(outstream);
                // 返回生成的图片名
                return imageName;
            }
        } catch (Exception e) {
            log.error("获取图片异常", e);
        }
        return null;
    }

    /**
     * 获取请求参数对象
     */
    @SuppressWarnings("all")
    private RequestConfig getConfig() {
        return RequestConfig.custom()
            .setConnectTimeout(1000)// 设置创建连接的超时时间
            .setConnectionRequestTimeout(500) // 设置获取连接的超时时间
            .setSocketTimeout(10000) // 设置连接的超时时间
            .build();
    }
}

