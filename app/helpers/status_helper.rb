module StatusHelper
  def status_nav_items
    [
        {
            name: t('links.status.versions.title'),
            url: status_versions_path
        },
        {
            name: t('links.status.workers.title'),
            url: status_workers_path
        },
        {
            name: t('links.status.logs.title'),
            url: status_logs_path
        }
    ]
  end
end