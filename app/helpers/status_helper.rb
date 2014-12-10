module StatusHelper
  def status_nav_items
    [
        {
            name: t('links.status.versions.title'),
            url: status_versions_url
        },
        {
            name: t('links.status.workers.title'),
            url: status_workers_url
        },
        {
            name: t('links.status.logs.title'),
            url: status_logs_url
        },
        {
            name: t('links.status.users.title'),
            url: status_users_url
        }
    ]
  end
end