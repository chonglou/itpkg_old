__author__ = 'zhengjitang@gmail.com'


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
        import smtplib, logging
        from email.mime.text import MIMEText
        from email.header import Header
        from email.mime.multipart import MIMEMultipart

        try:
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
        except smtplib.SMTPException:
            logging.exception("发送邮件出错")

