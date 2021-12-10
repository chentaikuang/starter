package com.xiaochen.starter.dingtalk.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.xiaochen.starter.dingtalk.common.CommonConst;
import com.xiaochen.starter.dingtalk.config.DingtalkAppProperties;
import com.xiaochen.starter.dingtalk.model.DingtalkApp;
import com.xiaochen.starter.dingtalk.util.IPUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.*;

import static com.xiaochen.starter.dingtalk.common.CommonConst.CHARSET_NAME;

@Slf4j
public class DingtalkService {

    /**
     * @param title   标题
     * @param content 内容
     */
    public void send(String title, String content, String... dingtalkKey) {
        send(title, content, null, dingtalkKey);
    }

    /**
     * @param title        标题
     * @param content      内容
     * @param throwable    异常
     * @param dingtalkName 钉钉name
     */
    public void send(String title, String content, Throwable throwable, String... dingtalkName) {
        try {
            MarkdownMessage markdownMessage = buildMsg(title, content, throwable);
            postReq(markdownMessage, dingtalkName);
        } catch (Exception e) {
            log.error(CommonConst.TWO_TIPS, "消息通知异常", e);
        }
    }

    private void postReq(MarkdownMessage message, String... dingtalkName) {
        try {
            HttpClient httpclient = HttpClientBuilder.create().build();
            String reqUrl = appendReqUrl((dingtalkName == null || dingtalkName.length == 0) ? "" : dingtalkName[0]);
            HttpPost httppost = new HttpPost(reqUrl);
            httppost.addHeader("Content-Type", "application/json; charset=utf-8");
            StringEntity stringEntity = new StringEntity(message.toJsonString(), "utf-8");
            httppost.setEntity(stringEntity);
            HttpResponse response = httpclient.execute(httppost);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                String result = EntityUtils.toString(response.getEntity());
                JSONObject obj = JSONObject.parseObject(result);
                Integer errcode = obj.getInteger("errcode");
                if (null == errcode || errcode.intValue() != 0) {
                    log.warn("ErrCode:{},ErrMsg:{}", obj.getString("errcode"), obj.getString("errmsg"));
                }
            }
        } catch (Exception e) {
            log.error("发送消息异常：{}", e.getMessage(), e);
        }
    }

    private String appendReqUrl(String appName) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        Long timestamp = System.currentTimeMillis();
        DingtalkApp dingtalkApp = chooseDingtalk(appName);
        String sign = convertSign(timestamp, dingtalkApp.getSecret());
        StringBuffer stringBuffer = new StringBuffer(CommonConst.DING_TALK_REQ_URL)
                .append(dingtalkApp.getToken())
                .append("&timestamp=")
                .append(timestamp)
                .append("&sign=").append(sign);
        log.debug("url:\n" + stringBuffer.toString());
        return stringBuffer.toString();
    }


    private DingtalkApp chooseDingtalk(String dingtalkKey) {
        if (StringUtils.isNotBlank(dingtalkKey) && DingtalkAppProperties.dingtalkAppMap.containsKey(dingtalkKey)) {
            return DingtalkAppProperties.dingtalkAppMap.get(dingtalkKey);
        }

        Optional<DingtalkApp> dingtalkAppOptional = DingtalkAppProperties.dingtalkAppMap.values().stream().findFirst();
        DingtalkApp dingtalkApp = dingtalkAppOptional.get();
        log.warn("未找到钉钉[Key=" + dingtalkKey + "] 选择默认 -> {}", dingtalkApp.getName());
        return dingtalkApp;
    }

    private String convertSign(Long timestamp, String secret) throws NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException {
        String stringToSign = timestamp + "\n" + secret;
        Mac mac = Mac.getInstance(CommonConst.HMAC_SHA_256);
        mac.init(new SecretKeySpec(secret.getBytes(CHARSET_NAME), CommonConst.HMAC_SHA_256));
        byte[] signData = mac.doFinal(stringToSign.getBytes(CHARSET_NAME));
        String sign = URLEncoder.encode(new String(Base64.encodeBase64(signData)), CHARSET_NAME);
        log.debug(CommonConst.TWO_TIPS, "sign", sign);
        return sign;
    }

    private MarkdownMessage buildMsg(String title, String content, Throwable throwable) {
        MarkdownMessage message = new MarkdownMessage();
        title = StringUtils.isBlank(title) ? "钉钉通知消息" : title;
        message.setTitle(title);
        message.add(MarkdownMessage.getHeaderText(6, title));
        message.add(MarkdownMessage.getHeaderText(4, content));
        String address = "机器IP：" + IPUtil.get();
        message.add(MarkdownMessage.getHeaderText(6, address));
        String sendDate = "发送时间：" + curDate();
        message.add(MarkdownMessage.getHeaderText(6, sendDate));
        convertThrowableStack(throwable, message);
        return message;
    }

    private String curDate() {
        return DateFormatUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss");
    }

    //转换异常堆栈信息
    private void convertThrowableStack(Throwable t, MarkdownMessage message) {
        if (t != null) {

            List<StackTraceElement> list = new LinkedList<>();
            StackTraceElement[] stackTrace = t.getStackTrace();
            int i = 0;
            while (i < stackTrace.length) {
                list.add(stackTrace[i]);
                if (i++ > 4) {
                    break;
                }
            }
            boolean first = true;
            for (StackTraceElement element : list) {
                if (first) {
                    message.add(MarkdownMessage.getReferenceText(t.getClass().getTypeName() + "|" + t.getMessage()));
                    first = false;
                }
                message.add(MarkdownMessage.getReferenceText(element.toString()));
            }
        }
    }

    @Data
    private static class MarkdownMessage {
        public static final int HEADER_TYPE_6 = 6;
        public static final int HEADER_TYPE_1 = 1;
        private String title;
        private List<String> items = new ArrayList<String>();

        //内容换行
        public static String getReferenceText(String text) {
            return "> " + text;
        }

        //添加告警内容项
        public void add(String text) {
            items.add(text);
        }

        /**
         * 设置消息头
         *
         * @param headerType 钉钉模板的消息头类型
         * @param text
         * @return
         */
        private static String getHeaderText(int headerType, String text) {
            if (headerType < HEADER_TYPE_1 || headerType > HEADER_TYPE_6) {
                throw new IllegalArgumentException("headerType should be in [1, 6]");
            }
            StringBuffer numbers = new StringBuffer();
            for (int i = 0; i < headerType; i++) {
                numbers.append("#");
            }
            return numbers + " " + text;
        }

        //消息内容转换JSON
        public String toJsonString() {
            Map<String, Object> result = new HashMap<String, Object>();
            result.put("msgtype", "markdown");

            Map<String, Object> markdown = new HashMap<String, Object>();
            markdown.put("title", title);

            StringBuffer markdownText = new StringBuffer();
            for (String item : items) {
                markdownText.append(item + "\n");
            }
            markdown.put("text", markdownText.toString());
            result.put("markdown", markdown);

            Map<String, Object> atMap = new HashMap<String, Object>();
            atMap.put("isAtAll", true);
            result.put("at", atMap);
            log.debug(JSON.toJSONString(result));

            return JSON.toJSONString(result);
        }
    }
}
