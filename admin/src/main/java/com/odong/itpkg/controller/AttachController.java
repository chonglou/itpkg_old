package com.odong.itpkg.controller;

import com.odong.itpkg.model.SessionItem;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.SessionAttributes;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-30
 * Time: 下午3:57
 */
@Controller("c.attach")
@RequestMapping(value = "/attach")
@SessionAttributes(SessionItem.KEY)
public class AttachController {
}
