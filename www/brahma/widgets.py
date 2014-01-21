__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.env import cache


class List(tornado.web.UIModule):
    def render(self, label, items):
        return self.render_string("widgets/list.html", label=label, items=items)


class CtlBar(tornado.web.UIModule):
    def embedded_javascript(self):
        return js_ready("""
            function show_tab(href){
                var id = href.substr(1);
                new Ajax(id, "%s/"+id);
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
            <title>%s</title>
            """ % (get_site_info("description") or "", get_site_info("keywords") or "", get_site_info("title") or "")


class TopNav(tornado.web.UIModule):
    def embedded_javascript(self):
        if self.current_user:
            click = """
                if(id.indexOf('logout') < 0){
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
            links.append(("/help", "帮助文档"))
            links.append(("/aboutMe", "关于我们"))
            return links

        def get_self_links():
            import tornado.options, importlib

            links = list()
            if self.current_user:
                if self.current_user["admin"]:
                    links.append(("/personal/site", "站点参数"))
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
                maxSpeed: 0.08,
                minSpeed: 0.01,
                shape: "sphere"
            }, 'tags')) {
                $("div#tagCanvasContainer").hide();
            }
        """)

    def render(self, tags=list()):
        return self.render_string("widgets/tagCloud.html", tags=tags)


class QrCode(tornado.web.UIModule):
    def embedded_css(self):
        return ".qrCode{width: 250px}"

    def render(self):
        return """
            <div>
                <a href="/main" target="_blank">
                    <img class="qrCode" src="/site.png" alt="二维码" title="手机扫描二维码获得更多信息"/>
                </a>
            </div>
        """


class Advert(tornado.web.UIModule):
    def embedded_javascript(self):
        from brahma.cache import get_advert

        return get_advert(self.name)

    def render(self, name):
        self.name = name
        return "<div id='advert-%s'></div>" % name


class NavBar(tornado.web.UIModule):
    def embedded_javascript(self):
        if self.ajax:
            return ("""
                $("li#navBar a").each(function () {
                    $(this).click(function () {
                        new Ajax($(this).attr("id"));
                    });
                });
            """ % self.ajax)

    def render(self, items=list(), ajax=False):
        self.ajax = ajax
        return self.render_string("widgets/navBar.html", navBars=items, ajax=ajax)


class Form(tornado.web.UIModule):
    def embedded_javascript(self):
        from brahma.web import HtmlField

        editors = []
        for f in self.form:
            if isinstance(f, HtmlField):
                editors.append("UE.getEditor('fm-%s-%s');" %(self.form.fid, f.id))
        return """
        %s
        function reset_field(fmId) {
            $('form#fm-' + fmId + '')[0].reset();
        }
        function reload_captcha(fmId) {
            $("img#fm-img-" + fmId + "-captcha").attr("src", "/captcha?_=" + Math.random());
        }
        %s
        """ % (" ".join(editors), js_ready("""
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
                                //console.log($(this).attr('id')+"\\t"+$(this).attr('type'));
                                data[fid] = $(this).val();
                        }
                    });
                    new Ajax(
                            "fm-msg-"+fmId,
                            $("input#fm-act-"+fmId).val(),
                            "POST",
                            data,
                            undefined,
                           %s
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
            "false" if self.form.captcha else "true",
            """
            $("img#fm-img-"+fmId+"-captcha").click(function(){
            reload_captcha(fmId);
            });
            """ if self.form.captcha else "",)
        ))

    def render(self, form):
        self.form = form
        return self.render_string("widgets/form.html", form=form)


class Message(tornado.web.UIModule):
    def embedded_javascript(self):
        return js_ready("""
            $('div#msgModal').modal({keyboard: false});
            %s
        """ % ("""
            $("button#msgModal-btn-ok").click(function(){
               window.location.href="%s";
            });
        """ % self.goto if self.goto else ""))

    def render(self, msg):
        self.goto = msg.goto
        return self.render_string("widgets/message.html", msg=msg)


def js_ready(script):
    return "$(document).ready(function () {%s});" % script
