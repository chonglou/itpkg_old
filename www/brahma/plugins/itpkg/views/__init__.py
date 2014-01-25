__author__ = 'zhengjitang@gmail.com'

import brahma.views


class BaseHandler(brahma.views.BaseHandler):
    def check_state(self, rid):
        from brahma.web import Message
        from brahma.plugins.itpkg.store import RouterDao
        r = RouterDao.get(rid)
        messages = []
        if self.current_user['id'] == r.manager:
            if r.state == 'ENABLE':
                return True
            elif r.state == "SUBMIT":
                messages.append("没有初始化")
            else:
                messages.append("状态不对")
        else:
            messages.append("没有权限")
        self.render_message_widget(messages=messages)
        return False

    def check_manager(self, rid):
        from brahma.web import Message
        from brahma.plugins.itpkg.store import RouterDao

        if self.current_user['id'] == RouterDao.get(rid).manager:
            return True
        self.render_message_widget(messages="没有权限")
        return False


