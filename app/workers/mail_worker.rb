class MailWorker
  include Sidekiq::Worker

  def perform(to, type, options={})
    logger.info "begin mail to #{to}"
    case type
      when 'key_pairs'
        UserMailer.key_pairs(options.fetch 'user_id').deliver
      else
        logger.info "Unknown type #{type}"
    end
    logger.info "end mail to #{to}"
  end
end