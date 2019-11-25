package com.asianwallets.message.utils;
import com.asianwallets.common.utils.ThreadUtil;
import com.asianwallets.message.config.EmailConfig;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;

@Component
@Slf4j
public class SendEmailCommon {

    @Autowired
    private EmailConfig emailConfig;

    @Value("${msg.send.api.simple.url}")
    private String simpleUrl;

    @Async
    public Future<Boolean> send_common(String sendTo, String subject, String html) {
        return ThreadUtil.EXECUTOR_SERVICE.submit(new Callable<Boolean>() {
            @Override
            public Boolean call() throws Exception {
                String apiUser = emailConfig.getApiUser();
                String apiKey = emailConfig.getApiKey();
                HttpPost httpPost = new HttpPost(simpleUrl);
                CloseableHttpClient httpClient = HttpClients.createDefault();
                List<NameValuePair> params = new ArrayList<NameValuePair>();
                params.add(new BasicNameValuePair("apiUser", apiUser));
                params.add(new BasicNameValuePair("apiKey", apiKey));
                params.add(new BasicNameValuePair("to", sendTo));
                params.add(new BasicNameValuePair("from", emailConfig.getEmailFrom()));
                params.add(new BasicNameValuePair("fromName", emailConfig.getFromName()));
                params.add(new BasicNameValuePair("subject", subject));
                params.add(new BasicNameValuePair("html", html));
                try {
                    httpPost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
                    HttpResponse response = httpClient.execute(httpPost);
                    httpPost.releaseConnection();
                    return response.getStatusLine().getStatusCode() == HttpStatus.SC_OK;
                } catch (Exception e) {
                    log.error("发送邮件失败：{}--{}--{}--{}", sendTo, subject, html, e);
                }
                return false;
            }
        });
    }

}
