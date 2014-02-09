__author__ = 'zhengjitang@gmail.com'

import brahma.views
from brahma.plugins.itpkg.store import RouterDao
from brahma.models import State


class BaseHandler(brahma.views.BaseHandler):
    def check_state(self, rid):
        row = RouterDao.get_manager_state(rid)
        messages = []
        if row and self.current_user['id'] == row[0]:
            if row[1] == State.ENABLE:
                return True
            elif row[1] == State.SUBMIT:
                messages.append("没有初始化")
            else:
                messages.append("状态不对")
        else:
            messages.append("没有权限")

        self.render_message_widget(messages=messages)
        return False

    def check_manager(self, rid):
        from brahma.plugins.itpkg.store import RouterDao

        row = RouterDao.get_manager_state(rid)
        if row and self.current_user['id'] == row[0]:
            return True
        self.render_message_widget(messages=["没有权限"])
        return False


