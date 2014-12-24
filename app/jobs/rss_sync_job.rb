require 'rss'
require 'open-uri'
module Itpkg
  class RssSyncJob < ActiveJob::Base
    queue_as :default

    def perform
      RssSite.all.each do |rs|
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
  end
end