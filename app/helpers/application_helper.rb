module ApplicationHelper
  def nav_links
    links = {home_path => t('links.home')}
    if current_user
      links[personal_path] = t('links.personal.self')
    end
    links[document_show_path(name: 'help')]=t('links.help')
    links[document_show_path(name: 'about_us')]=t('links.about_us')
    links

  end

  def personal_bar
    if current_user
      label = t('labels.welcome', username: current_user.email)
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


end
