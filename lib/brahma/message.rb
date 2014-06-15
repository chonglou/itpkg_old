require 'json'
module Brahma

  module Request
    module_function

    def shell(lines, user=nil)
      {user: user, lines: lines, crated: Time.now}.to_json
    end

    def file(name, lines, owner=nil, mode=nil)
      {name: name, lines: lines, owner: owner, mode: mode, crated: Time.now}.to_json
    end

    def sql(db, lines)
      {db: db, lines: lines, crated: Time.now}.to_json
    end

  end

  module Response
    module_function

    def parse(json)
      j = JSON.parse(json)
      [j.fetch('stdout'), j.fetch('stderr')]
    end
  end
end