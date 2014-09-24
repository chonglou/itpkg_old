module ApplicationHelper
  include BrahmaBodhi::ApplicationHelper
  def hot_bars
    []
  end

  def nav_links
    {}
  end

  def personal_bar
    if current_user
      label = "#{t('web.label.welcome')}, #{session.fetch :username}"
      links={
          personal_path => t('web.title.personal'),
          brahma_bodhi.personal_logout_path => t('web.link.logout')
      }
    else
      label = t('web.label.register_or_login')
      links={
          Brahma::Factory.instance.oauth2.authorize_url => t('web.link.login')
      }
    end
    {label: label, links: links}
  end

  def tag_links
    {}
  end
end
