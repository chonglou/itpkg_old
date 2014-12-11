module PersonalHelper
  def personal_nav_items
    [
        {
            name: t('links.personal.password'),
            url: edit_user_registration_url
        },
        {
            name: t('links.personal.public_key'),
            url: personal_public_key_url
        },
        {
            name: t('links.personal.logs'),
            url: personal_logs_url
        },
        {
            name: t('links.personal.mail_box'),
            url: personal_mail_box_url
        },
    ]
  end

end