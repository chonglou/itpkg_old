module Itpkg
  class SearchClient
    def initialize
      cfg = Itpkg::ELACSICSEARCH_CFG
      @index = cfg.fetch 'index'
      @client = Elasticsearch::Client.new url:cfg.fetch('url'), log:cfg.fetch('log')
    end
    def insert(table, columns={})
      @client.index index: @index, type: table, body: columns
    end
  end
end