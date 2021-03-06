package com.mail.smtp.mta.handler;

import com.mail.smtp.data.ResponseData;
import com.mail.smtp.data.SmtpData;
import com.mail.smtp.exception.SmtpException;
import com.mail.smtp.mta.authentication.CheckAuth;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.timeout.ReadTimeoutException;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RequiredArgsConstructor
public class AuthHandler extends ChannelInboundHandlerAdapter
{
    @Getter @Setter
    private String userId;
    @Getter @Setter
    private String userPass;
    @NonNull
    @Getter
    private final SmtpData smtpData;
    @NonNull
    @Getter
    private final ChannelHandler channelHandler;
    @NonNull
    @Getter
    private final CheckAuth checkAuth;

    protected boolean auth()
    {
        return this.checkAuth.checkAuth(userId, userPass, smtpData.getRandomUID());
    }

    protected void replaceBaseHandler(ChannelHandlerContext ctx)
    {
        ChannelPipeline cp = ctx.pipeline();
        cp.replace("authhandler", "basehandler", channelHandler);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
    {
        if( cause instanceof ReadTimeoutException )
            log.error("[{}] exception occured, read timed out", smtpData.getRandomUID());
        else if( cause instanceof SmtpException )
        {
            log.error("[{}] exception occured, error code : {}, message : {}",
                    smtpData.getRandomUID(), ( (SmtpException) cause ).getErrorCode(), cause.getMessage());
            //ctx.writeAndFlush(( (SmtpException) cause ).getResponse());
            ctx.writeAndFlush(new ResponseData(smtpData.getRandomUID(), ((SmtpException) cause).getResponse()));
        }
        else
            log.error("[{}] exception occured, {}", smtpData.getRandomUID(), cause.getMessage());

        if( log.isTraceEnabled() )
            cause.printStackTrace();
        ctx.close();
        //MDC.clear();
    }

    @Override
    public void handlerRemoved(ChannelHandlerContext ctx)
    {
        log.trace("[{}] auth handler channel removed", smtpData.getRandomUID());
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx)
    {
        log.trace("[{}] auth handler channel unregistered", smtpData.getRandomUID());
        replaceBaseHandler(ctx);
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx)
    {
        log.trace("[{}] auth handler channel active", smtpData.getRandomUID());
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx)
    {
        log.trace("[{}] auth handler channel registered", smtpData.getRandomUID());
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx)
    {
        log.trace("[{}] auth handler channel inactive", smtpData.getRandomUID());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt)
    {
        log.trace("[{}] auth handler channel user event triggered", smtpData.getRandomUID());
    }
}
