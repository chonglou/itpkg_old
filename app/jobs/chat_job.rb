class ChatJob < ActiveJob::Base
  queue_as :default

  def perform(from, to, body, type, created)
    begin
      ChatMessage.create from: from.split('/').first, to: to, body: body, flag: ChatMessage.flags[type.to_sym], created: created

        #logger.info "ChatMessage.new from: from, to: to, body: body, flag: ChatMessage.flags[type.to_sym], created: created"
    rescue => e
      logger.error e
    end

  end

end
