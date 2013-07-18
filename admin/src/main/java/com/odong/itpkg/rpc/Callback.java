package com.odong.itpkg.rpc;

import com.odong.itpkg.model.Rpc;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 下午10:00
 */
public interface Callback {
    void execute(Rpc.Response response);
}
