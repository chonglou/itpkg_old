module Brahma::Web
  class Response
    attr_reader :ok, :data, :created

    def initialize(ok=false)
      @ok=ok
      @data = []
      @created = Time.now
    end

    def add(message)
      @data << message
    end

    def to_h
      {ok: @ok, data: @data, created: @created}
    end
  end
  class Form < Response
    attr_reader :id, :captcha, :action, :scroll, :fields, :buttons
    attr_accessor :label

    def initialize(id, label, action, captcha=false, scroll=false)
      super false
      @id=id
      @label = label
      @action=action
      @captcha = captcha
      @scroll = scroll
      @fields = []
      @buttons=[]
    end

    def add(item)
        if item.is_a?(String) || item.is_a?(Time) ||  item.is_a?(Fixnum) || item.is_a?(Array) ||  item.is_a?(Hash)
          @data << item
        elsif item.is_a?(Button)
          @buttons << item
        elsif item.is_a?(Field)
          @fields << item
        else
          @data << item.to_s
      end
    end


    def to_h
      h = super.to_h
      h[:id] = @id
      h[:label]=@label
      h[:action] = @action
      h[:scroll] = @scroll
      f_h = []
      b_h = []
      @fields.each { |f| f_h << f.to_h }
      @buttons.each { |b| b_h << b.to_h }
      h[:fields] = f_h
      h[:buttons] = b_h
      h
    end

  end
  class Field
    attr_reader :id, :type

    def initialize(id, type)
      @id=id
      @type=type
    end

    def to_h
      {id: id, type: type}
    end
  end

  class TextField < Field
    attr_reader :label, :value

    def initialize(id, label, value)
      super id, 'text'
      @label = label
      @value = value
    end

    def to_h
      h = super.to_h
      h[:label] = @label
      h[:value] = @value
      h
    end
  end
  class TextAreaField <Field

  end
  class RadioField < Field

  end
  class HtmlField <Field

  end
  class HiddenField <Field

  end
  class SelectField<Field

  end
  class CheckboxField<Field

  end
  class Button

  end

end