__author__ = 'zhengjitang@gmail.com'


class EmailHelper:
    def __init__(self):
        self.email = None

    def load(self):
        from brahma.store.site import SettingDao
        smtp = SettingDao.get("site.smtp", encrypt=True)
        if smtp:
            import tornado.options

            self.email = Email(
                smtp['host'],
                smtp['username'],
                smtp['password'],
                port=smtp['port'],
                ssl=smtp['ssl'],
                debug=tornado.options.options.debug,
            )
        else:
            self.email = None

    def send(self, to, title, body, html=True):
        if self.email:
            self.email.send(to, title, body, html)
        else:
            import logging
            logging.error("发送邮件[{%s}]：%s出错"%(to, title))


class Email:
    def __init__(self, host, username, password, port=25, ssl=False, bcc=None, debug=False):
        self.host = host
        self.username = username
        self.password = password
        self.port = port
        self.ssl = ssl
        self.bcc = bcc
        self.debug = debug

    def send(self, to, title, body, html):
        import smtplib
        from email.mime.text import MIMEText
        from email.header import Header
        from email.mime.multipart import MIMEMultipart

        msg = MIMEMultipart()
        msg["Subject"] = Header(title, charset="UTF-8")
        msg["From"] = self.username
        msg["To"] = to

        txt = MIMEText(body, _subtype="html" if html else "plain", _charset="UTF-8")
        msg.attach(txt)

        smtp = smtplib.SMTP_SSL(self.host, self.port) if self.ssl else smtplib.SMTP(self.host, self.port)

        if self.debug:
            smtp.set_debuglevel(1)

        smtp.login(self.username, self.password)
        smtp.sendmail(self.username, to, msg.as_string())
        smtp.quit()

