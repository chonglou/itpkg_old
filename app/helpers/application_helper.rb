require 'brahma/factory'

module ApplicationHelper
  include BrahmaBodhi::ApplicationHelper
  include ShareHelper

  def nav_links
    links = {'/main' => '本站首页'}
    if current_user
      links['/personal'] = '用户中心'
    end
    links['/projects'] = '项目管理'
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
      label = "欢迎你, #{session.fetch :username}"
      links={
          personal_path => '个人中心',
          brahma_bodhi.personal_logout_path => '安全退出'
      }
    else
      label = '注册/登录'
      links={
          Brahma::Factory.instance.oauth2.authorize_url => 'BRAHMA通行证'
      }
    end
    puts '#'*80
    {label: label, links: links}
  end

  def tag_links
    links = {}
    BrahmaBodhi::FriendLink.all.each { |fl| links["http://#{fl.domain}"] = fl.name }
    links
  end

end
