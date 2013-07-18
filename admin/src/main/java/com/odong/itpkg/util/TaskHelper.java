package com.odong.itpkg.util;

import java.util.List;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-17
 * Time: 下午7:40
 */
public interface TaskHelper {
    String addFile(long host, String name, String mode, List<String> commands);

    String addCommand(long host, List<String> commands);

    String addHeart(long host, int space);

    String execute(String id);
}
