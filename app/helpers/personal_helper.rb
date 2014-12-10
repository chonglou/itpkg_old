module PersonalHelper
  def personal_nav_items
    [
        {
            name: t('titles.personal.edit_profile'),
            url: edit_user_registration_url
        },
        {
            name: t('links.user.public_key'),
            url: personal_public_key_url
        },
        {
            name: t('links.user.logs'),
            url: personal_logs_url
        },
    ]
  end

end