__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.views import BaseHandler
from brahma.forms.site import InfoForm, SmtpForm, ContentForm, AdvertForm, ProtocolForm, ValidCodeForm, FriendLinkForm, \
    TimerForm
from brahma.store import SettingDao, UserDao, FriendLinkDao, TaskDao
from brahma.models import State, TaskFlag
from brahma.cache import get_site_info


class AdminHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self, act):
        if self.is_admin():
            if act == "info":
                fmInfo = InfoForm("info", "基本信息", "/personal/site/info")
                fmInfo.title.data = SettingDao.get("site.title")
                fmInfo.domain.data = SettingDao.get("site.domain")
                fmInfo.description.data = SettingDao.get("site.description")
                fmInfo.keywords.data = SettingDao.get("site.keywords")

                fmHelp = ContentForm("help", "帮助文档", "/personal/site/help")
                fmHelp.content.data = SettingDao.get("site.help")

                fmAboutMe = ContentForm("aboutMe", "关于我们", "/personal/site/aboutMe")
                fmAboutMe.content.data = SettingDao.get("site.aboutMe")

                fmProtocol = ProtocolForm("protocol", "注册协议", "/personal/site/protocol")
                fmProtocol.content.data = SettingDao.get("site.protocol")

                self.render("widgets/forms.html", forms=[fmInfo, fmHelp, fmAboutMe, fmProtocol])

            elif act == "friendLink":
                self.render("personal/site/friendLinks.html",
                            links=FriendLinkDao.all())

            elif act == "seo":
                fmGoogle = ValidCodeForm("google", "GOOGLE网站验证", "/personal/site/seo.google")
                fmGoogle.code.data = "google%s.html" % SettingDao.get("site.seo.google")
                fmBaidu = ValidCodeForm("baidu", "百度网站验证", "/personal/site/seo.baidu")
                fmBaidu.code.data = "baidu_verify_%s.html" % SettingDao.get("site.seo.baidu")
                self.render("widgets/forms.html", forms=[fmGoogle, fmBaidu])

            elif act == "smtp":
                fmSmtp = SmtpForm("smtp", "邮件信息", "/personal/site/smtp")
                smtp = SettingDao.get("site.smtp", True)
                fmSmtp.bcc.data = smtp["bcc"]
                fmSmtp.ssl.data = smtp["ssl"]
                fmSmtp.host.data = smtp["host"]
                fmSmtp.username.data = smtp["username"]
                fmSmtp.port.data = smtp["port"]

                self.render_form_widget(form=fmSmtp)
            elif act == "advert":
                fmLeft = AdvertForm("advertLeft", "左侧广告栏", "/personal/site/advert")
                fmLeft.aid.data = "left"
                fmLeft.script.data = SettingDao.get("site.advert.left")

                fmBottom = AdvertForm("advertBottom", "底部广告栏", "/personal/site/advert")
                fmBottom.aid.data = "bottom"
                fmBottom.script.data = SettingDao.get("site.advert.bottom")

                self.render("widgets/forms.html", forms=[fmLeft, fmBottom])
            elif act == "status":
                from brahma.env import start_stamp
                import datetime, psutil, tornado.options, sys, os

                items = list()
                items.append("Python目录：%s" % sys.exec_prefix)
                items.append("数据目录：%s" % tornado.options.options.app_store)
                items.append("CPU：%s%%" % psutil.cpu_percent(1))
                phymem = psutil.phymem_usage()
                items.append("内存：%s%%  %sM/%sM" % (
                    phymem.percent, int(phymem.used / 1024 / 1024), int(phymem.total / 1024 / 1024)))
                items.append("当前时间：%s" % datetime.datetime.now())
                items.append("启动时间：%s" % start_stamp)
                self.render("personal/site/status.html", items=items)

            elif act == "user":
                manager = SettingDao.get("site.manager", True)

                def act(id, state):
                    if id == manager:
                        return None
                    if state == State.DISABLE:
                        return "ENABLE"
                    if state == State.ENABLE:
                        return "DISABLE"
                    return None

                items = [(
                             u.id, u.username, u.flag,
                             u.email if "localhost" not in u.email else None,
                             u.state, u.lastLogin,
                             act(u.id, u.state)) for u in UserDao.all()]
                self.render(
                    "personal/site/user.html",
                    items=items)
            elif act == "task":
                qr = TimerForm("qr", "site.png", "/personal/site/task")
                qr.act.data = TaskFlag.QR
                qr.clock.data = self.__get_clock(TaskFlag.QR)

                sitemap = TimerForm("sitemap", "sitemap.xml", "/personal/site/task")
                sitemap.act.data = TaskFlag.SITEMAP
                sitemap.clock.data = self.__get_clock(TaskFlag.SITEMAP)

                rss = TimerForm("rss", "rss.xml", "/personal/site/task")
                rss.act.data = TaskFlag.RSS
                rss.clock.data = self.__get_clock(TaskFlag.RSS)

                robots = TimerForm("robots", "robots.xml", "/personal/site/task")
                robots.act.data = TaskFlag.ROBOTS
                robots.clock.data = self.__get_clock(TaskFlag.ROBOTS)

                self.render("widgets/forms.html", forms=[qr, robots, sitemap, rss])
            else:
                self.render_message_widget(messages=["错误请求"])

    def __get_clock(self, flag):
        ts = TaskDao.list_by_flag(flag)
        if ts:
            return ts[0].request['clock']
        return None

    def __set_clock(self, flag, clock):
        import datetime

        now = datetime.datetime.now()
        nextRun = datetime.datetime(now.year, now.month, now.day, hour=clock) + datetime.timedelta(days=1)
        from brahma.store import TaskDao

        ts = TaskDao.list_by_flag(flag)
        from brahma.models import Item

        import pickle

        request = pickle.dumps(Item(clock=clock).__dict__)
        from brahma.env import transaction

        if ts:
            @transaction()
            def set_t(cursor=None):

                from brahma.utils.database import update

                update(Item(next_run=nextRun, request=request).update("tasks", id_val=ts[0].id, version=True))(cursor)

            set_t()
        else:
            @transaction()
            def add_t(cursor=None):
                from brahma.utils.database import insert

                insert(
                    Item(next_run=nextRun, flag=flag, request=request).insert("tasks"))(cursor)

            add_t()

    @tornado.web.authenticated
    def post(self, act):
        if self.is_admin():
            if act == "user":
                uid = self.get_argument("uid")
                state = self.get_argument("state")
                if int(uid) != SettingDao.get("site.manager", True):
                    user = UserDao.get_by_id(uid)
                    if user and user.state != State.SUBMIT:
                        if state in ["ENABLE", "DISABLE"]:
                            UserDao.set_state(uid, state)
                            self.log("变更用户状态[%s=>%s]" % (uid, state))
                            self.render_message_widget(ok=True)
                            return
                    self.render_message_widget(messages=["状态不对"])
                else:
                    self.render_message_widget(messages=["不能修改超级管理员"])
            elif act == "advert":
                fm = AdvertForm(formdata=self.request.arguments)
                aid = fm.aid.data
                SettingDao.set("site.advert." + aid, fm.script.data)
                from brahma.cache import get_advert

                get_advert(aid, True)
                self.log("修改广告[%s]脚本" % aid)
                self.render_message_widget(ok=True)
            elif act == "smtp":
                fm = SmtpForm(formdata=self.request.arguments)
                messages = []
                if fm.validate():
                    SettingDao.set("site.smtp", {
                        "host": fm.host.data,
                        "port": fm.port.data,
                        "username": fm.username.data,
                        "password": fm.password.data,
                        "bcc": fm.bcc.data,
                        "ssl": fm.ssl.data,
                    }, True)
                    self.log("修改SMTP信息")
                    self.render_message_widget(ok=True)
                else:
                    messages.extend(fm.messages())
                    self.render_message_widget(messages=messages)
            elif act == "seo.google":
                fm = ValidCodeForm(formdata=self.request.arguments)
                SettingDao.set("site.seo.google", fm.code.data[6:-5])
                self.log("更新google网站验证")
                self.render_message_widget(ok=True)
            elif act == "seo.baidu":
                fm = ValidCodeForm(formdata=self.request.arguments)
                SettingDao.set("site.seo.baidu", fm.code.data[13:-5])
                self.log("更新百度网站验证")
                self.render_message_widget(ok=True)
            elif act == "info":
                fm = InfoForm(formdata=self.request.arguments)
                messages = []
                if fm.validate():
                    for s in ["domain", "title", "keywords", "description"]:
                        SettingDao.set("site." + s, getattr(fm, s).data)
                        get_site_info(s, True)
                    self.log("修改站点信息")
                    self.render_message_widget(ok=True)
                else:
                    messages.append(fm.messages())
                    self.render_message_widget(messages=messages)

            elif act == "help":
                fm = ContentForm(formdata=self.request.arguments)
                SettingDao.set("site.help", fm.content.data)
                get_site_info("help", True)
                self.log("修改帮助文档")
                self.render_message_widget(ok=True)
            elif act == "aboutMe":
                fm = ContentForm(formdata=self.request.arguments)
                SettingDao.set("site.aboutMe", fm.content.data)
                get_site_info("aboutMe", True)
                self.log("修改关于我们")
                self.render_message_widget(ok=True)
            elif act == "protocol":
                fm = ProtocolForm(formdata=self.request.arguments)
                SettingDao.set("site.protocol", fm.content.data)
                get_site_info("protocol", True)
                self.log("修改用户注册协议")
                self.render_message_widget(ok=True)
            elif act == "friendLink":
                fm = FriendLinkForm(formdata=self.request.arguments)

                if fm.validate():
                    if fm.flid.data:
                        FriendLinkDao.set(fm.flid.data, fm.domain.data, fm.name.data, fm.logo.data)
                    else:
                        FriendLinkDao.add(fm.domain.data, fm.name.data, fm.logo.data)
                    self.render_message_widget(ok=True)
                else:
                    messages = []
                    messages.extend(fm.messages())
                    self.render_message_widget(messages=messages)

            elif act == "task":
                fm = TimerForm(formdata=self.request.arguments)
                if fm.act.data in [TaskFlag.QR, TaskFlag.RSS, TaskFlag.SITEMAP, TaskFlag.ROBOTS]:
                    from brahma.jobs import TaskSender

                    getattr(TaskSender, fm.act.data)()
                    self.__set_clock(fm.act.data, fm.clock.data)
                    #Setting.set("site.task.%s" % fm.act.data, fm.clock.data)
                    self.render_message_widget(ok=True)
            else:
                self.render_message_widget(messages=["错误请求"])

    @tornado.web.authenticated
    def put(self, act):
        if self.is_admin():
            if act == "friendLink":
                flid = self.get_argument("id", None)
                form = FriendLinkForm("friendLink", "添加友情链接", "/personal/site/friendLink")
                if flid:
                    form.label = "编辑友情链接[%s]" % flid
                    fl = FriendLinkDao.get(flid)
                    form.flid.data = fl.id
                    form.domain.data = fl.domain
                    form.name.data = fl.name
                    form.logo.data = fl.logo
                self.render_form_widget(form)

    @tornado.web.authenticated
    def delete(self, act):
        if self.is_admin():
            if act.startswith("friendLink/"):
                flid = act[len("friendLink/"):]
                FriendLinkDao.delete(flid)
                self.log("删除友情链接[%s]" % flid)
                self.render_message_widget(ok=True)
            elif act == "cache":
                from beaker.cache import cache_managers

                for c in cache_managers.values():
                    c.clear()

                self.render_message_widget(ok=True)
            else:
                self.render_message_widget(messages=["未知操作"])


class SiteHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        if self.is_admin():
            self.render_ctlbar_widget(act="/personal/site",
                                      items=[
                                          ("info", "基本信息"),
                                          ("friendLink", "友情链接"),
                                          ("smtp", "邮件设置"),
                                          ("seo", "站长工具"),
                                          ("oauth", "OAUTH"),
                                          ("advert", "广告代码"),
                                          ("task", "定时任务"),
                                          ("user", "账户管理"),
                                          ("status", "系统状态"),
                                      ])


handlers = [
    (r"/personal/site", SiteHandler),
    (r"/personal/site/(.*)", AdminHandler),
]