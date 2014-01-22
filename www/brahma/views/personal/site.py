__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.views import BaseHandler
from brahma.forms.site import InfoForm,SmtpForm,ContentForm
from brahma.store.site import SettingDao

class AdminHandler(BaseHandler):
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
                fmProtocol = ContentForm("protocol", "注册协议", "/personal/site/protocol")
                fmProtocol.content.data = SettingDao.get("site.protocol")
                self.render("personal/site/info.html", forms=[fmInfo, fmHelp, fmAboutMe, fmProtocol])
            elif act == "smtp":
                fmInfo = SmtpForm("smtp", "邮件信息", "/personal/site/smtp")
                self.render_form_widget(form=fmInfo)


    def post(self, act):
        if self.is_admin():
            pass



class SiteHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        if self.is_admin():
            return self.render_ctlbar_widget(act="/personal/site",
                                             items=[
                                                 ("info", "基本信息"),
                                                 ("smtp", "邮件设置"),
                                                 ("user", "账户管理"),
                                                 ("logs", "日志列表"),
                                                 ("status", "当前状态"),
                                             ])


handlers = [
    (r"/personal/site", SiteHandler),
    (r"/personal/site/(.*)", AdminHandler),
]