require_relative '../constants'
require_relative '../utils/encryptor'

module Itpkg

  module TranslationService
    module_function

    def create(flag, lang)
      t = Translation.create flag: flag
      t.update lang => yield(t.id)
    end

    def delete(flag, id, lang)
      t = Translation.find_by flag: flag, lang => id
      yield id
      t.update lang => nil
      can_d = true
      Itpkg::LOCALES.each do |l|
        if t.send(l)
          can_d = false
        end
      end
      t.destroy if can_d
    end

    def translate(flag, id, from, to, yes, no)
      t = Translation.find_by flag: flag, from => id
      id = t.send to
      if id
        yes.call id
      else
        o = no.call t.id
        t.update to => o.id
        o
      end
    end

  end


  module SiteService
    module_function

    def version
      v = "#{Rails.root}/REVISION"
      if  File.exist?(v)
        "#{File.open(v, 'r') { |f| f.read.strip }}-#{File.basename Rails.root}"
      end
    end
  end
end