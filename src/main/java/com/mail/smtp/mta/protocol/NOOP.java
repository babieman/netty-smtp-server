package com.mail.smtp.mta.protocol;

import com.mail.smtp.data.ResponseData;
import com.mail.smtp.data.SmtpData;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NOOP
{
    public void process(ChannelHandlerContext ctx, SmtpData smtpData)
    {
        //ctx.write("250 OK\r\n");
        ctx.write(new ResponseData(smtpData.getRandomUID(), "250 OK\r\n"));
    }
}
