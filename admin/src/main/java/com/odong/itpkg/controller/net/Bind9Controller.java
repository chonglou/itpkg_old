package com.odong.itpkg.controller.net;

import com.odong.itpkg.model.SessionItem;
import com.odong.portal.web.form.Form;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * Created with IntelliJ IDEA.
 * User: flamen
 * Date: 13-7-18
 * Time: 上午11:27
 */
@Controller
@RequestMapping(value = "/net/bind9")
@SessionAttributes(SessionItem.KEY)
public class Bind9Controller {
    @RequestMapping(value = "/", method = RequestMethod.GET)
    String getIndex(Map<String,Object> map,@ModelAttribute(SessionItem.KEY) SessionItem si){
        //map.put("zones", )
        return "net/bind9";
    }
    @RequestMapping(value = "/add/zone", method = RequestMethod.GET)
    @ResponseBody
    Form getAddZone(){
        return null;
    }

}
