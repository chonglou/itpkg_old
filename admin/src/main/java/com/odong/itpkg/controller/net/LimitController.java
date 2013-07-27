package com.odong.itpkg.controller.net;

import com.odong.itpkg.model.SessionItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:29
 */
@Controller
@RequestMapping(value = "/net/limit")
@SessionAttributes(SessionItem.KEY)
public class LimitController {


}
