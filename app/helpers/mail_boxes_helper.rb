module MailBoxesHelper
  def mail_boxes_nav_items
    [
        {
            name: t('links.mail_box.inbox'),
            url: mail_boxes_url(label: :inbox)
        },
        {
            name: t('links.mail_box.outbox'),
            url: mail_boxes_url(label: :outbox)
        },
        {
            name: t('links.mail_box.drafts'),
            url: mail_boxes_url(label: :draft)
        },
        {
            name: t('links.mail_box.spam'),
            url: mail_boxes_url(label: :spam)
        },
        {
            name: t('links.mail_box.trash'),
            url: mail_boxes_url(label: :trash)
        },
    ]
  end
end