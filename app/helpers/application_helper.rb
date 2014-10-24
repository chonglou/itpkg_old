module ApplicationHelper
  def nav_links
    links = {root_path => t('links.home')}
    if current_user
      links['111'] = t('links.personal')
    end
    links[about_me_path]=t('web.title.about_me')
    links

  end

   def personal_bar
    if current_user
      label = "#{t('labels.welcome')}, #{current_user.email}"
      links={
          '111' => t('links.personal'),
          '222' => t('links.logout')
      }
    else
      label = t('labels.register_or_signin')
      links={
          '111' => t('links.login')
      }
    end
    {label: label, links: links}
  end

end
