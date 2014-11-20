class BackupWorker
  include Sidekiq::Worker

  def perform
    tmp = "#{Rails.root}/tmp/storage/backups"
    unless Dir.exist?(tmp)
      FileUtils.mkpath tmp
    end
    cfg = Rails.configuration.database_configuration[Rails.env]

    case cfg['adapter']
      when 'mysql2'
        logger.info `mysqldump --opt --host=#{cfg['host']} --user=#{cfg['username']} --password="#{cfg['password']}" #{cfg['database']} | gzip > #{tmp}/#{Time.now.strftime '%Y%m%d%H%M%S%3N'}.sql.gz`
      else
        logger.info 'Not support to backup'
    end

  end

end