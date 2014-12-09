module Itpkg
  module HistoryService
    module_function
    def backup(type, id, content={})
      History.create url:"#{type}://#{id}", data:content.to_json
    end
    def recent(type, id, size=1)
      History.where(url:"#{type}://#{id}").order(id:desc).limit(size)
    end
  end
end
