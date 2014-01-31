__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.env import cache


class FriendLinkBar(tornado.web.UIModule):
    def render(self):
        @cache.cache("site/friendLinks", expire=3600 * 24)
        def fl():
            from brahma.store.site import FriendLinkDao

            return FriendLinkDao.all()

        return self.render_string("widgets/friendLinkBar.html", items=fl())


class List(tornado.web.UIModule):
    def render(self, label, items):
        return self.render_string("widgets/list.html", label=label, items=items)


class CtlBar(tornado.web.UIModule):
    def embedded_javascript(self):
        return js_ready("""
            function show_tab(href){
                var id = href.substr(1);
                new Ajax(id, "%s/"+id.split('-')[2]);
            }
            $('ul#tab a').click(function(){
                show_tab($(this).attr("href"));
            });

            var last = $('ul#tab a:last');
            last.tab('show');
            show_tab(last.attr('href'));
        """ % self.act)

    def render(self, act, items):
        self.act = act
        return self.render_string("widgets/ctlBar.html", act=act, items=items)


class FallCard(tornado.web.UIModule):
    def embedded_javascript(self):
        return js_ready("""
            $('div#fall-container-card').masonry({
                itemSelector: '.fall-item'
            });
        """)

    def render(self, items):
        return self.render_string("widgets/fallCard.html", items=items)


class FallLink(tornado.web.UIModule):
    def embedded_javascript(self):
        return js_ready("""
            $('div#fall-container-link').masonry({
                itemSelector: '.fall-item'
            });
        """)

    def render(self, items):
        return self.render_string("widgets/fallLink.html", items=items)


class ButtonGroup(tornado.web.UIModule):
    def embedded_javascript(self):
        return """
            $("div.btn-group button").each(function () {
                $(this).click(function () {
                    var ss = $(this).attr("id").split('-');
                    if(ss[1]!="DELETE" || confirm("你确定要删除此项么？")){
                        new Ajax("%s", ss[0], ss[1]);
                    }
                });
            });
        """ % self.div

    def render(self, div, items):
        self.div = div
        return '<div class="btn-group">%s</div>' % ''.join(
            map(lambda item: '<button id="%s-%s" type="button" class="btn btn-%s">%s</button>' % item,
                items))


class Head(tornado.web.UIModule):
    def render(self, title=None):
        from brahma.cache import get_site_info

        return """
            <meta name="description" content="%s"/>
            <meta name="keywords" content="%s"/>
            <meta name="author" content="zhengjitang@gmail.com">
            <title>%s-%s</title>
            """ % (get_site_info("description") or "",
                   get_site_info("keywords") or "",
                   get_site_info("title") or "",
                   title or "")


class TopNav(tornado.web.UIModule):
    def embedded_javascript(self):
        if self.current_user:
            click = """
                if(id.indexOf('logout') < 0){
                    clear_navbar();
                    new Ajax("gl_root", id);
                }
                else{
                    if(window.confirm("您确认要退出系统么？")){
                        window.location.href = id;
                    }
                }
            """
        else:
            click = "new Ajax('gl_root', id);"
        return js_ready(
            """
            $("li#personalBar ul.dropdown-menu a").each(function () {
                $(this).click(function () {
                    var id = $(this).attr("id");
                    %s
                });
            });

            $("a#favorites").click(function () {
                var ctrl = (navigator.userAgent.toLowerCase()).indexOf('mac') != -1 ? 'Command/Cmd' : 'CTRL';

                if (document.all) {
                    window.external.addFavorite(location.href, document.title);

                } else if (window.sidebar) {
                    window.sidebar.addPanel(location.href, document.title, "");
                } else {
                    alert('添加失败\\n您可以尝试通过快捷键' + ctrl + ' + D 加入到收藏夹~')
                }

            });
            """ % click)

    def render(self, index):
        if not index:
            index = "/main"

        @cache.cache("site/topLinks", expire=3600 * 24)
        def get_top_links():
            import tornado.options, importlib

            links = list()
            links.append(("/main", "本站首页"))
            links.extend(map(lambda name: ("/%s/" % name, importlib.import_module("brahma.plugins." + name).NAME),
                             tornado.options.options.app_plugins))
            links.append(("/user", "用户列表"))
            links.append(("/help", "帮助文档"))
            links.append(("/aboutMe", "关于我们"))
            return links

        def get_self_links():
            import tornado.options, importlib

            links = list()
            if self.current_user:
                if self.current_user["admin"]:
                    links.append(("/personal/site", "站点设置"))
                links.append(("/personal/self", "个人信息"))
                links.extend(
                    map(lambda name: ("/personal/" + name, importlib.import_module("brahma.plugins." + name).NAME),
                        tornado.options.options.app_plugins))
                links.append(("/personal/logout", "安全退出"))
            else:
                links.append(("/personal/login", "用户登录"))
                links.append(("/personal/register", "账户注册"))
                links.append(("/personal/active", "账户激活"))
                links.append(("/personal/resetPwd", "重置密码"))

            return links

        from brahma.cache import get_site_info

        return self.render_string("widgets/topNav.html", index=index, title=get_site_info("title"),
                                  selfLinks=get_self_links(), user=self.current_user,
                                  topLinks=get_top_links())


class DatePicker(tornado.web.UIModule):
    def embedded_javascript(self):
        return js_ready(
            """
            var dt = $("div#datePicker");
            dt.datepicker({
                onSelect: function (dateText) {
                    window.open("/calendar/" + dateText.split('-').join('/'));
                }
            });
            dt.datepicker($.datepicker.regional[ "zh-CN" ]);
            """)

    def render(self):
        return "<div id='datePicker'></div>"


class TagCloud(tornado.web.UIModule):
    def embedded_javascript(self):
        return js_ready("""
            if (!$('#tagCanvas').tagcanvas({
                textColour: '#00f',
                textHeight: 25,
                outlineColour: '#ff9999',
                outlineThickness: 3,
                pulsateTo: 0.2,
                pulsateTime: 0.5,
                wheelZoom: false,
                reverse: true,
                decel: 0.9,
                depth: 0.8,
                weight: true,
                maxSpeed: 0.08,
                minSpeed: 0.01,
                shape: "sphere"
            }, 'tags')) {
                $("div#tagCanvasContainer").hide();
            }
        """)

    def render(self):
        @cache.cache("tagCloud", expire=3600 * 24)
        def tags():
            import importlib, tornado.options
            from brahma.store.site import SettingDao

            items = list()
            items.append(("/main", SettingDao.get("site.title")))
            for p in tornado.options.options.app_plugins:
                items.extend(importlib.import_module("brahma.plugins." + p).tags())
            return items

        return self.render_string("widgets/tagCloud.html", tags=tags())


class QrCode(tornado.web.UIModule):
    def embedded_css(self):
        return ".qrCode{width: 250px}"

    def render(self):
        return self.render_string("widgets/qrCode.html")


class Advert(tornado.web.UIModule):
    def render(self, name):
        from brahma.cache import get_advert

        return "<div id='advert-%s'>%s</div>" % (name, get_advert(name))


class NavBar(tornado.web.UIModule):
    def embedded_javascript(self):

        return """
                function clear_navbar(){
                    $("div#navBar li").each(function () {
                        $(this).removeClass('active');
                    });
                }
                %s
                """ % js_ready("""
                $("div#navBar a").each(function () {
                    $(this).click(function () {
                        if($(this).attr('href') == '#'){
                            clear_navbar();
                            $(this).parent().addClass('active');
                            new Ajax("gl_root", $(this).attr("id"));
                        }
                    });
                });
                """)

    def render(self):
        import importlib, tornado.options
        from brahma.web import NavBar
        from brahma.store.site import SettingDao

        navbars = list()
        for p in tornado.options.options.app_plugins:
            nb = importlib.import_module("brahma.plugins." + p).navbar(
                self.current_user['id'] if self.current_user else None)
            if nb:
                navbars.append(nb)

        @cache.cache("nav/cal", expire=3600 * 24)
        def nv_cal():
            from brahma.utils.time import last_months

            cal = NavBar("归档列表")
            init = SettingDao.get("site.init")
            cal.items.extend(
                map(lambda dt: (dt.strftime("/calendar/%Y/%m"), dt.strftime("%Y年%m月")), last_months(init, 5)))
            return cal

        navbars.append(nv_cal())
        return self.render_string("widgets/navBar.html", navBars=navbars)


class Form(tornado.web.UIModule):
    def embedded_javascript(self):
        return """
        function reset_field(fmId) {
            $('form#fm-' + fmId + '')[0].reset();
        }
        function reload_captcha(fmId) {
            $("img#fm-img-" + fmId + "-captcha").attr("src", "/captcha?_=" + Math.random());
        }
        %s
        """ % (js_ready("""

             $("form[id^='fm-']").each(function () {
                var fmId = $(this).attr('id').split('-')[1];

                $("a#fm-btn-" + fmId + "-submit").click(function () {
                    var id = "[id^='fm-"+fmId+"-']";
                    var data = {};
                    $(id).each(function () {
                        var fid = $(this).attr('id').split('-')[2];

                        switch ($(this).attr('type')) {
                            case "checkbox":
                                data[fid] = $(this).is(":checked");
                                break;
                            case "password":
                                data[fid] = $(this).val();
                                $(this).val("");
                                break;
                            default:
                                data[fid] = $(this).val();
                        }
                    });
                    $("[id^='fm-html-"+fmId+"-']").each(function(){
                        var id = $(this).attr('id');
                        data[id.split('-')[3]] = UE.getEditor(id).getContent();
                    });
                    $("[id^='fm-select-"+fmId+"-']").each(function(){
                        data[$(this).attr('id').split('-')[3]] = $(this).val();
                    });
                    $("[name^='fm-radio-"+fmId+"-']:checked").each(function(){
                        data[$(this).attr('name').split('-')[3]] = $(this).val();
                    });

                    new Ajax(
                            "fm-msg-"+fmId,
                            $("input#fm-act-"+fmId).val(),
                            "POST",
                            data,
                            undefined,
                           false
                    );
                    reload_captcha(fmId);
                });
                $("a#fm-btn-"+fmId+"-reset").click(function(){
                    reset_field(fmId);
                });
                %s
                reset_field(fmId);
            });
        """ % (
            """
            $("img#fm-img-"+fmId+"-captcha").click(function(){
            reload_captcha(fmId);
            });
            """ if self.captcha else "",)
        ))

    def render(self, form):
        self.captcha = form.captcha
        return self.render_string("widgets/form.html", form=form)


class Message(tornado.web.UIModule):
    def embedded_javascript(self):
        return js_ready("""
            $('div#msgModal%s').modal({keyboard: false});
            %s
        """ % (self.mid, """
            $("button#msgModal%s-btn-ok").click(function(){
               window.location.href="%s";
            });
        """ % (self.mid, self.goto) if self.goto else ""))

    def render(self, msg):
        import uuid

        self.goto = msg.goto
        self.mid = uuid.uuid4().hex
        return self.render_string("widgets/message.html", id=self.mid, msg=msg)


def js_ready(script):
    return "$(document).ready(function () {%s});" % script
