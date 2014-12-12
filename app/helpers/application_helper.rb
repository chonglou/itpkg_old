require 'digest/md5'
require 'itpkg/services/site'
require 'itpkg/utils/string_helper'

module ApplicationHelper
  def nav_links
    links = {www_home_url => t('links.home')}
    if current_user
      links[personal_url] = t('links.personal.self')
    end
    links[document_show_url(name: 'help')]=t('links.help')
    links[document_show_url(name: 'about_us')]=t('links.about_us')
    links

  end

  def personal_bar
    user = current_user
    if user
      label = t('labels.welcome', username: user.email, logo: email2logo(user.email, 18))
      links={
          personal_path => t('links.personal.self'),
          edit_user_registration_path => t('links.personal.info'),
          destroy_user_session_path => t('links.personal.logout')
      }
    else
      label = t('labels.register_or_login')
      links={
          new_user_session_path => t('links.personal.login'),
          new_user_registration_path => t('links.personal.register'),
          new_user_password_path => t('links.personal.reset_password'),
          new_user_confirmation_path => t('links.personal.active'),
          new_user_unlock_path => t('links.personal.unlock')
      }
    end
    {label: label, links: links}
  end


  def email2logo(email, size=nil)
    "http#{'s' if Rails.env.production?}://www.gravatar.com/avatar/#{Digest::MD5.hexdigest email.downcase}#{"?s=#{size}" if size}"
  end

  def filename2type(name)
    ext = File.extname(name)[1..-1]
    ext.downcase if ext
  end

  def md2html(md)
    Itpkg::StringHelper.md2html md
  end

  def google_viewer(url)
    "http://docs.google.com/viewer?url=#{CGI.escape url}"
  end

  def js_viewer(path)
    "/ViewerJS/#..#{path}"
  end
end
