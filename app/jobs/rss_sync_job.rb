require 'rss'
require 'open-uri'

class RssSyncJob < ActiveJob::Base
  queue_as :default

  def perform(id=nil)
    if id
      _parse RssSite.find(id)
    else
      RssSite.all.each {|rs| _parse rs}
    end

  end

  private
  def _parse(rs)
    logger.info "Fetch From #{rs.url}"
    open(rs.url) do |rss|
      feed = RSS::Parser.parse(rss)
      feed.items.each do |item|
        unless RssItem.find_by(link: item.link)
          logger.info "GET #{item.title}"
          RssItem.create title: item.title, link: item.link, created: item.pubDate, body: (item.content_encoded || item.description)
        end
      end
      rs.update title: feed.channel.title, last_sync: Time.now
    end
  end
end
