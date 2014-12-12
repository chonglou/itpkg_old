module MailBoxesHelper

  def mail_boxes_top_nav_items
    [
        {
            name: t('links.mail_box.list'),
            url: mail_boxes_url(label: 'INBOX')
        },
        {
            name: t('links.mail_box.compose'),
            url: new_mail_box_url
        },
        {
            name: t('links.mail_box.setting'),
            url: mail_boxes_password_url
        }
    ]
  end
end