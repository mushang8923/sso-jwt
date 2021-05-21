package com.mubasha.distributed.sso.distributedsecurityuaa.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.springframework.http.HttpStatus;
import org.springframework.web.util.HtmlUtils;

import java.io.IOException;
import java.util.Map;

/**
 * @Description: 序列化异常类
 * @ProjectName: spring-parent
 * @Package: com.yaomy.security.oauth2.exception.BootOAuthExceptionJacksonSerializer
 * @Date: 2019/7/17 15:32
 * @Version: 1.0
 */
public class UserOAuth2ExceptionSerializer extends StdSerializer<UserOAuth2Exception> {

    protected UserOAuth2ExceptionSerializer() {
        super(UserOAuth2Exception.class);
    }
    @Override
    public void serialize(UserOAuth2Exception e, JsonGenerator generator, SerializerProvider serializerProvider) throws IOException {
        generator.writeStartObject();
        generator.writeObjectField("code", 600);
        String message = e.getMessage();
        if (message != null) {
            message = HtmlUtils.htmlEscape(message);
        }
        generator.writeStringField("message", message);
        if (e.getAdditionalInformation()!=null) {
            for (Map.Entry<String, String> entry : e.getAdditionalInformation().entrySet()) {
                String key = entry.getKey();
                String add = entry.getValue();
                generator.writeStringField(key, add);
            }
        }
        generator.writeEndObject();
    }
}
