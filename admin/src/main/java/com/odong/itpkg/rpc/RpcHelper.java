package com.odong.itpkg.rpc;

import com.odong.itpkg.entity.net.Host;
import com.odong.itpkg.entity.net.Ip;
import com.odong.itpkg.model.Rpc;
import com.odong.itpkg.service.HostService;
import com.odong.itpkg.util.EncryptHelper;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import java.util.HashMap;
import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-31
 * Time: 下午3:47
 */
@Component
public class RpcHelper {

    public  Rpc.Response file(long hostId,String name, String mode, String owner, String...commands){
        return send(hostId, Rpc.Type.FILE, name, mode, owner,commands);
    }
    public  Rpc.Response command(long hostId, String...commands){
        return send(hostId, Rpc.Type.COMMAND, null, null, null,commands);
    }
    public  Rpc.Response heart(long hostId){
        return send(hostId, Rpc.Type.HEART, null, null, null);
    }
    public  Rpc.Response bye(long hostId){
        return send(hostId, Rpc.Type.BYE, null, null, null);
    }

    public synchronized void pop(long hostId){
        clientMap.remove(hostId);
    }

    @PostConstruct
    void init(){
        clientMap = new HashMap<>();
    }
    @PreDestroy
    void destroy(){
        clientMap.clear();
    }

    private synchronized Connection getConnection(long hostId){
        Connection c = clientMap.get(hostId);
        if(c == null){
            Host host = hostService.getHost(hostId);
            Ip wanIp = hostService.getIp(host.getWanIp());
            c = new Connection(wanIp.getId(), host.getRpcPort(),encryptHelper.decode(host.getSignKey()));
            clientMap.put(hostId, c);
        }
        return c;
    }

    private  Rpc.Response send(long hostId, Rpc.Type type, String name, String mode, String owner, String...lines){
        final Rpc.Response.Builder builder = Rpc.Response.newBuilder();
        Connection c = getConnection(hostId);
        final Client client = new Client(c.key);
        client.open(c.host, c.port, new Callback() {
            @Override
            public void execute(Rpc.Response response) {
                for(String s : response.getLinesList()){
                    builder.addLines(client.decode(s));
                }
                builder.addAllLines(response.getLinesList());
                builder.setCode(response.getCode());
                builder.setType(response.getType());
                builder.setCreated(response.getCreated());
            }
        });
        Rpc.Request request;
        switch (type){
            case COMMAND:
                request = client.command(lines);
                break;
            case FILE:
                request = client.file(name, mode, owner, lines);
                break;
            case HEART:
                request  = client.heart();
                break;
            default:
                request = client.bye();
        }
        client.send(request);
        while (builder.getCreated() == 0){
            try{
                Thread.sleep(1000);
            }
            catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        client.close();
        return builder.build();
    }
    private  Map<Long, Connection> clientMap;
    @Resource
    private HostService hostService;
    @Resource
    private EncryptHelper encryptHelper;

    class Connection{
        Connection(String host, int port, String key) {
            this.host = host;
            this.port = port;
            this.key = key;
        }

        private final String host;
        private final int port;
        private final String key;
    }

    public void setEncryptHelper(EncryptHelper encryptHelper) {
        this.encryptHelper = encryptHelper;
    }

    public void setHostService(HostService hostService) {
        this.hostService = hostService;
    }
}
