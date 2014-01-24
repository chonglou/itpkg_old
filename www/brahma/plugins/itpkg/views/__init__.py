__author__ = 'zhengjitang@gmail.com'

import brahma.views


class BaseHandler(brahma.views.BaseHandler):
    def check_router(self, rid):
        from brahma.plugins.itpkg.store import RouterDao

        if self.current_user['id'] == RouterDao.get(rid).manager:
            return True
        self.render_message_widget("没有权限")
        return False


