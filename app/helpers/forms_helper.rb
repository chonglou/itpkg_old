module FormsHelper
  def bootstrap_form(record, options = {}, &block)
    options.update(builder: BootstrapFormBuilder)

    options[:html] ||= {role: 'form'}
    options[:html][:class] ||= ''
    options[:html][:class] << ' form-horizontal'

    form_for(record, options, &block)
  end

  class BootstrapFormBuilder < ActionView::Helpers::FormBuilder
    alias :super_text_field :text_field

    def error_messages
      unless object.errors.empty?
        content = <<HTML
<div class='panel panel-danger'>
  <div class='panel-heading'>#{I18n.t 'labels.fail'}</div>
  <ul class='list-group'>
    #{object.errors.map { |k, v| "<li class='list-group-item'>#{k.capitalize} #{v}</li>" }.join('')}
  </ul>
</div>
HTML
        content.html_safe
      end
    end

    def fieldset(&block)
      @template.content_tag(:fieldset, @template.capture(&block))
    end

    def group(options={}, &block)
      content = @template.capture(&block)
      update_options_with_class! options, 'form-group'
      @template.content_tag :div, content, options
    end

    def legend(content, options ={})
      @template.content_tag(:legend, content, options)
    end

    def button_group(&block)
      @template.content_tag(:div, @template.content_tag(:div, @template.capture(&block), class: 'col-sm-offset-2 col-sm-10', style: 'margin-top: 20px;'), class: 'form-group')
    end

    def submit(name, options ={})
      update_options_with_class!(options, 'btn btn-primary')
      super(name, options)
    end

    def reset(name, options={})
      options[:type] = :reset
      update_options_with_class!(options, 'btn btn-default')
      @template.button_tag(name, options)
    end

    def back(name, options={})
      update_options_with_class!(options, 'btn btn-info')
      @template.link_to name, :back, options
    end

    def label(name, options={})

      if options[:no_style]
        options.delete :no_style
        update_options_with_style! options, ' padding-left:10px;'
      else
        update_options_with_class!(options, 'col-sm-2 control-label')

      end
      super name, options
    end


    def check_box_group(&block)
      @template.content_tag(:div, class: 'col-sm-offset-2 col-sm-10') do
        @template.content_tag(:div, @template.capture(&block), class: 'checkbox')
      end
    end

    def check_box(name, options={})
      update_options_with_style! options, 'margin-left:-10px;'
      super name, options
    end


    def text_field(name, options={})
      update_options_with_class! options, 'form-control'
      input_div(super(name, options), 9)
    end

    def name_field(name, options={})
      update_options_with_class! options, 'form-control'
      input_div(super_text_field(name, options), 4)
    end

    def email_field(name, options={})
      update_options_with_class! options, 'form-control'
      input_div(super(name, options), 6)
    end

    def password_field(name, options={})
      update_options_with_class! options, 'form-control'
      input_div(super(name, options), 7)
    end

    def date_picker(name, options={})
      update_options_with_class! options, 'form-control'
      options['data-provide']='datepicker'
      options['data-date-format'] = 'yyyy-mm-dd'
      options['data-date-language'] = I18n.locale

      c1 = super_text_field(name, options)
      c2 = "<span class='input-group-addon'><i class='glyphicon glyphicon-th'></i></span>"
      @template.content_tag(:div, @template.content_tag(:div, c1+c2.html_safe, class: 'input-group date'), class: 'col-sm-4')
    end


    private
    def input_div(content, size)
      @template.content_tag :div, content, class: "col-sm-#{size}"
    end

    def update_options_with_class!(options, klass)
      options[:class] ||= ''
      options[:class] << " #{klass}"
      options
    end

    def update_options_with_style!(options, style)
      options[:style] ||= ''
      options[:style] << " #{style}"
      options
    end

  end


end
