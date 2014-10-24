module ApplicationHelper
  def nav_links
    links = {root_path => t('links.home')}
    if current_user
      links['111'] = t('links.personal')
    end
    links[help_path]=t('links.help')
    links[about_us_path]=t('links.about_us')
    links

  end

   def personal_bar
    if current_user
      label = "#{t('labels.welcome')}, #{current_user.email}"
      links={
          '111' => t('links.personal.self'),
          '222' => t('links.personal.logout')
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
