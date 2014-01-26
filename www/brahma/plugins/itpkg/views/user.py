__author__ = 'zhengjitang@gmail.com'

import tornado.web
from brahma.views import BaseHandler
from brahma.plugins.itpkg.store import UserDao
from brahma.plugins.itpkg.forms import InfoForm




class UserHandler(BaseHandler):
    @tornado.web.authenticated
    def get(self):
        manager = self.current_user['id']
        users=UserDao.all(manager)
        if not users:
            UserDao.add(manager, "默认用户", "")
            users = UserDao.all(manager)
        self.render("itpkg/user.html", users=users)

    @tornado.web.authenticated
    def put(self):
        uid = self.get_argument("id", None)
        form = InfoForm("user", "添加用户", "/itpkg/user")
        if uid:
            if self.__check_user(uid):
                u = UserDao.get(uid)
                form.iid.data = u.id
                form.name.data = u.name
                form.details.data = u.details
                form.label = "编辑用户组[%s]" % uid
                self.render_form_widget(form)
                return
        else:
            self.render_form_widget(form)
            return

    @tornado.web.authenticated
    def post(self):
        fm = InfoForm(formdata=self.request.arguments)
        uid = fm.iid.data
        if fm.validate():
            if uid:
                if self.__check_user(uid):
                    UserDao.set_info(uid, fm.name.data, fm.details.data)
                    self.render_message_widget(ok=True)
                    return
            else:
                UserDao.add(self.current_user['id'], fm.name.data, fm.details.data)
                self.render_message_widget(ok=True)
                return
        else:
            messages = []
            messages.extend(fm.messages())
            self.render_message_widget(messages=messages)
            return


    def __check_user(self, uid):
        u = UserDao.get(uid)
        manager = self.current_user['id']
        if u.manager == manager:
            return True
        self.render_message_widget(messages=['没有权限'])
        return False


handlers = [
    (r"/itpkg/user", UserHandler),
]