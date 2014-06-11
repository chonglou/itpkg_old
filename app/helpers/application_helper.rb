require 'brahma/factory'

module ApplicationHelper
  include BrahmaBodhi::ApplicationHelper

  def nav_links
    links = {'/main' => '本站首页'}
    if current_user
      links['/personal'] = '用户中心'
    end
    links['/firewall'] = '防火墙'
    links['/cdn'] = 'CDN服务'
    links['/vpn'] = 'VPN服务'
    links['/dns'] = 'DNS服务'
    links['/monitor'] = '监控服务'
    links['/email'] = '邮件服务'
    links['/about_me']='关于我们'
    links
  end

  def personal_links
    if current_user
      {'/personal' => '个人中心', '/core/personal/logout' => '安全退出'}
    else
      auth = Brahma::Factory.instance.oauth2
      state = auth.state
      oauth2_state! state
      {
          auth.authorization(['info'], state) => 'BRAHMA通行证'
      }
    end
  end

  def tag_links
    links = {}
    BrahmaBodhi::FriendLink.all.each { |fl| links["http://#{fl.domain}"] = fl.name }
    links
  end

end
