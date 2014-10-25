module FormsHelper
  # A custom FormBuilder class for Rails forms with Twitter Bootstrap 3
  class BootstrapFormBuilder < ActionView::Helpers::FormBuilder

    # ActionView::Helpers::FormHelper and ActionView::Helpers::FormBuilder
    # methods each have different args. This hash stores the names of the args
    # and the methods that have those args in order to DRY up the method aliases
    # defined below.
    METHOD_NAMES_FOR_ARG_SETS = {

        'method,options={}' => %w{
        date_field datetime_field datetime_local_field email_field file_field
         hidden_field number_field password_field phone_field range_field
        search_field telephone_field text_area text_field time_field url_field
      },

        # Removing checkboxes for now - the error field causes alignment issues
        # "method,options={},checked_val='1',unchecked_val='0'" => %w{ check_box },

        "method,collection,value_method,text_method,options={},html_options={},&block" => %w{
        collection_check_boxes collection_radio_buttons
      },

        "method,collection,value_method,text_method,options={},html_options={}" => %w{
        collection_select
      },

        "method,options={},html_options={}" => %w{date_select datetime_select time_select},

        "method,tag_value,options={}" => %w{ radio_button },

        "method,choices,options={},html_options={}" => %w{ select }
    }

    for args_set, method_names in METHOD_NAMES_FOR_ARG_SETS

      html_class_in = args_set.index("html_options") ? 'html_options' : 'options'
      args_without_defaults = args_set.gsub(/\=[^,]+/, '')
      for method_name in method_names

        method_str = <<-RUBY
          def #{method_name}_with_error_field(#{args_set})
            update_options_with_form_control_class! #{html_class_in}
            content = #{method_name}_without_error_field(#{args_without_defaults})
            append_error_field_to_content!(content, method, options)
          end
        RUBY

        # Subtracting 2 from the __LINE__ var for accurate error messages
        class_eval(method_str, __FILE__, __LINE__-2)
        alias_method_chain method_name, :error_field
      end

    end

    # Creates a div wrapper with class 'form-group'
    def group(options ={}, &block)
      @template.form_group(options, &block)
    end

    # Shows the error messages for :base if any
    def error_messages
      @template.error_messages_for(object)
    end

    # Creates a div wrapepr with class 'input-group'
    def input_group(options={}, &block)
      @template.input_group(options, &block)
    end

    # Creates a fieldset tag
    def fieldset(&block)
      @template.form_fieldset(&block)
    end

    # Creates a legend tag
    def legend(content, options ={})
      @template.form_legend(content, options)
    end

    # Creates a div with class 'text-error' for displaying error messages
    # on specific fields
    def error_field(attribute, options = {})
      return unless object.errors[attribute]
      update_options_with_class!(options, 'text-error')
      content = object.errors[attribute].first
      @template.content_tag(:div, content, options)
    end

    # Creates a small tag with class 'help-block' for displaying helpful
    # text under a form input
    def help_text(method, content, options = {})
      return if object.errors[method].any?
      update_options_with_class!(options, 'help-block')
      @template.content_tag(:small, content, options)
    end

    # Creates a div wrapper with class 'checkbox'
    def checkbox(&block)
      @template.form_checkbox(&block)
    end

    # Creates a submit button with class 'btn btn-primary'
    def submit(name, options ={})
      update_options_with_class!(options, 'btn btn-primary')
      super(name, options) << " " # add a space to the end
    end

    # Creates a cancel button to go back to a previous page.
    def cancel(path, edit_path = nil, options ={})
      options[:class] ||= ''
      options[:class] << "btn btn-default"
      options[:data] = {confirm: "Cancel without saving?"}

      if edit_path && object.persisted?
        path = edit_path
      end
      @template.link_to('cancel', path, options)
    end

    private

    # Add the error_field div to each field by default
    def append_error_field_to_content!(content, method, options)
      unless object.errors[method].any? || options[:error_field] == false
        content << error_field(method)
      end
      content
    end

    # Update an options hash with class 'form-control'
    def update_options_with_form_control_class!(options)
      update_options_with_class!(options, 'form-control')
    end

    # Update an options hash with a :class
    def update_options_with_class!(options, klass)
      @template.update_options_with_class!(options, klass)
    end

  end

  # Use this instead of form_for
  def bootstrap_form(record, options = {}, &block)
    options.update(builder: BootstrapFormBuilder)
    form_for(record, options, &block)
  end

  # Creates a div wrapper with class 'input-group'
  def input_group(options={}, &block)
    content = capture(&block)
    update_options_with_class!(options, 'input-group')
    content_tag(:div, content, options)
  end

  # Creates a div wrapper with class 'form-group'
  def form_group(options ={}, &block)
    content = capture(&block)
    update_options_with_class!(options, 'form-group')
    content_tag(:div, content, options)
  end


  def form_fieldset(&block)
    content_tag(:fieldset, capture(&block))
  end

  # Creates a legend tag
  def form_legend(content, options ={})
    content_tag(:legend, content, options)
  end

  # Creates a div wrapper with class 'input-group'
  def form_input_group(&block)
    content_tag(:div, capture(&block), class: "input-group")
  end

  # Creates a div wrapper with class 'checkbox'
  def form_checkbox(&block)
    content_tag(:div, capture(&block), class: "checkbox")
  end

  # Show the error messages on :base for a record
  def error_messages_for(object)
    if object.errors[:base].any?
      message = object.errors[:base].first
      %{<div class="text-danger">#{message}</div>}.html_safe
    end
  end

  # Update an options hash with a given :class value
  def update_options_with_class!(options, klass)
    options[:class] ||= ''
    options[:class] << " #{klass}"
    options
  end
end
