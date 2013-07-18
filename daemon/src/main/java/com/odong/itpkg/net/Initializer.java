package com.odong.itpkg.net;

import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.util.EncryptHelper;
import com.odong.itpkg.util.JsonHelper;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.protobuf.ProtobufDecoder;
import io.netty.handler.codec.protobuf.ProtobufEncoder;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-16
 * Time: 下午2:14
 */
public class Initializer extends ChannelInitializer<SocketChannel> {
    public Initializer(JsonHelper jsonHelper, EncryptHelper encryptHelper, int signLength) {
        super();
        this.jsonHelper = jsonHelper;
        this.encryptHelper = encryptHelper;
        this.signLength = signLength;
    }

    @Override
    protected void initChannel(SocketChannel socketChannel) throws Exception {
        ChannelPipeline pipeline = socketChannel.pipeline();
        //pipeline.addLast("deflater", ZlibCodecFactory.newZlibEncoder(ZlibWrapper.GZIP));
        //pipeline.addLast("inflater", ZlibCodecFactory.newZlibDecoder(ZlibWrapper.GZIP));

        pipeline.addLast("frameDecoder", new LengthFieldBasedFrameDecoder(1048576, 0, 4, 0, 4));
        pipeline.addLast("protobufDecoder", new ProtobufDecoder(Rpc.Request.getDefaultInstance()));

        pipeline.addLast("frameEncoder", new LengthFieldPrepender(4));
        pipeline.addLast("protobufEncoder", new ProtobufEncoder());

        pipeline.addLast("handler", new Handler(jsonHelper, encryptHelper, signLength));
    }

    private JsonHelper jsonHelper;
    private EncryptHelper encryptHelper;
    private int signLength;
}
