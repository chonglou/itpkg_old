require 'itpkg/linux/git'

class PersonalController < ApplicationController
  before_action :authenticate_user!

  def generate_keys
    key = current_user.ssh_key
    keys = Linux::Git.key_pairs current_user.label
    if key
      key.update keys
    else
      keys[:user_id] = current_user.id
      SshKey.create keys
    end
    GitAdminWorker.perform_async
    UserMailer.delay.key_pairs current_user.id
    flash[:notice] = t('labels.success')
    redirect_to edit_user_registration_path
  end

  def update_public_key
    key = current_user.ssh_key
    if key
      key.update public_key: params[:public_key]
    else
      SshKey.create user_id: current_user.id, public_key: params[:public_key], private_key: 'NULL'
    end
    GitAdminWorker.perform_async
    flash[:notice] = t('labels.success')
    redirect_to edit_user_registration_path
  end

  def index
    @items=[
        {
            url: projects_path,
            logo: 'flat/256/businessman3.png',
            label: t('links.project.list')
        },
        {
            url: repositories_path,
            logo: 'flat/256/three128.png',
            label: t('links.repository.list')
        }
    ]
    if admin?
      @items << {
          url: nodes_path,
          logo: 'flat/256/hosting.png',
          label: t('links.node.list')
      }
      @items << {
          url: node_types_path,
          logo: 'flat/256/black398.png',
          label: t('links.node_type.list')
      }

      @items << {
          url: email_path,
          logo: 'flat/256/black218.png',
          label: t('links.email')
      }
      @items << {
          url: vpn_path,
          logo: 'logo/openvpn.png',
          label: t('links.vpn')
      }
      @items << {
          url: dns_path,
          logo: 'flat/256/internet5.png',
          label: t('links.dns')
      }
      @items << {
          url: settings_path,
          logo: 'flat/256/settings48.png',
          label: t('links.settings')
      }
    end
    @items << {
        url: edit_user_registration_path,
        logo: 'flat/256/male80.png',
        label: t('links.personal.info')
    }
    @items << {
        url: document_show_path(name: 'help'),
        logo: 'flat/256/help.png',
        label: t('links.help')
    }
    @items << {
        url: document_show_path(name: 'about_us'),
        logo: 'flat/256/call37.png',
        label: t('links.about_us')
    }

  end
end
