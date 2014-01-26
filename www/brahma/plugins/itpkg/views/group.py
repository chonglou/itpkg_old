__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.views import BaseHandler
from brahma.plugins.itpkg.store import GroupDao
from brahma.plugins.itpkg.forms import InfoForm



class GroupHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        manager = self.current_user['id']
        groups = GroupDao.all(manager)
        if not groups:
            GroupDao.add(manager, "默认组", "默认用户组")
            groups = GroupDao.all(manager)
        self.render("itpkg/group.html", groups=groups)

    @tornado.web.authenticated
    def put(self):
        gid = self.get_argument("id", None)
        form = InfoForm("group", "添加用户组", "/itpkg/group")
        if gid:
            if self.__check_group(gid):
                g = GroupDao.get(gid)
                form.iid.data = g.id
                form.name.data = g.name
                form.details.data = g.details
                form.label = "编辑用户组[%s]" % gid
                self.render_form_widget(form)
                return
        else:
            self.render_form_widget(form)
            return

    @tornado.web.authenticated
    def post(self):
        fm = InfoForm(formdata=self.request.arguments)
        gid = fm.iid.data
        if fm.validate():
            if gid:
                if self.__check_group(gid):
                    GroupDao.set_info(gid, fm.name.data, fm.details.data)
                    self.render_message_widget(ok=True)
                    return
            else:
                GroupDao.add(self.current_user['id'], fm.name.data, fm.details.data)
                self.render_message_widget(ok=True)
                return
        else:
            messages = []
            messages.extend(fm.messages())
            self.render_message_widget(messages=messages)
            return


    def __check_group(self, gid):
        g = GroupDao.get(gid)
        manager = self.current_user['id']
        if g.manager == manager:
            return True
        self.render_message_widget(messages=['没有权限'])
        return False


handlers = [
    (r"/itpkg/group", GroupHandler),
]