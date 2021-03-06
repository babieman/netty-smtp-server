/*
tbl_maillist 테이블에 insert 시 mail 에 대한 정보를 JSON 으로 변환함.
 */

package com.mail.smtp.converter;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mail.smtp.data.MailAttribute;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.AttributeConverter;

@Slf4j
public class MailAttributeConverter implements AttributeConverter< MailAttribute, String>
{
    private static final ObjectMapper objectMapper = new ObjectMapper()
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

    @Override
    public String convertToDatabaseColumn(MailAttribute mailAttribute)
    {
        String jsonData = "";
        try
        {
            jsonData = objectMapper.writeValueAsString(mailAttribute);
        }
        catch( JsonProcessingException e )
        {
            log.error("fail to convert mail attribute to json, {}", e.getMessage());
            if( log.isTraceEnabled())
                e.printStackTrace();
        }

        return jsonData;
    }

    @Override
    public MailAttribute convertToEntityAttribute(String s)
    {
        MailAttribute mailAttribute = null;
        try
        {
            mailAttribute = objectMapper.readValue(s, MailAttribute.class);
        }
        catch( JsonProcessingException e )
        {
            log.error("fail to convert json to mail attribute, {}", e.getMessage());
            if( log.isTraceEnabled() )
                e.printStackTrace();
        }

        return mailAttribute;
    }
}
