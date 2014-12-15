module SettingsHelper
  def settings_nav_items
    [
        {
            name: t('links.settings.users.title'),
            url: settings_users_url
        },
        {
            name: t('links.rss_site.list'),
            url: rss_sites_url
        }
    ]
  end
end