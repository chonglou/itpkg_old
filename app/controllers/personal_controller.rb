require 'itpkg/utils/encryptor'

class PersonalController < ApplicationController
  layout 'tabbed'
  before_action :authenticate_user!

  def contact
    case request.method
      when 'GET'
        @contact = current_user.contact || Contact.new
      when 'POST'
        @contact = current_user.contact
        kv = params.require(:contact).permit(:logo, :username, :qq, :wechat, :phone, :fax, :address, :weibo, :linkedin, :facebook, :skype, :others)
        if @contact
          @contact.update kv
        else
          @contact = Contact.new kv
          @contact.user_id = current_user.id
          @contact.save
        end
        redirect_to personal_contact_path
      else
        render status: 404
    end
  end


  def logs
    @logs = Log.where(user_id: current_user.id).order(id: :desc).page(params[:page])
  end

  def generate_keys
    key = current_user.ssh_key
    keys = Linux::Git.key_pairs current_user.label
    if key
      key.update keys
    else
      keys[:user_id] = current_user.id
      SshKey.create keys
    end
    Itpkg::GitAdminWorker.perform_async
    UserMailer.delay.key_pairs current_user.id
    flash[:notice] = t('labels.success')
    redirect_to personal_public_key_path
  end

  # def mail_box
  #   case request.method
  #     when 'GET'
  #       mb = current_user.settings.mail_box
  #       @mail_box =  mb ? Itpkg::Encryptor.decode(mb) : {
  #           smtp_host:"smtp.#{ENV['ITPKG_DOMAIN']}",
  #           smtp_port:'25',
  #           imap_host:"imap.#{ENV['ITPKG_DOMAIN']}",
  #           imap_port:'143',
  #           username:current_user.email,
  #           password:nil
  #       }
  #       render 'mail_box',layout:'personal/self'
  #     when 'POST'
  #       current_user.settings.mail_box = Itpkg::Encryptor.encode(params.permit(:smtp_host,:smtp_port, :imap_host, :imap_port,:username,:password))
  #       redirect_to personal_mail_box_path
  #     else
  #       render status:404
  #   end
  # end

  def public_key
    case request.method
      when 'GET'

      when 'POST'
        key = current_user.ssh_key
        if key
          key.update public_key: params[:public_key]
        else
          SshKey.create user_id: current_user.id, public_key: params[:public_key], private_key: 'NULL'
        end
        Itpkg::GitAdminWorker.perform_async
        flash[:notice] = t('labels.success')
        redirect_to edit_user_registration_path
      else
        render status: 404
    end
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
    if current_user.has_role?(:admin) || current_user.has_role?(:ops)
      @items << {
          url: monitor_nodes_path,
          logo: 'flat/256/lifeline6.png',
          label: t('links.monitor_node.list')
      }
      @items << {
          url: logging_nodes_path,
          logo: 'flat/256/log2.png',
          label: t('links.logging_node.list')
      }
    end
    if current_user.has_role?(:admin)
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
          url: status_versions_path,
          logo: 'flat/256/house129.png',
          label: t('links.status.versions.title')
      }
      @items << {
          url: settings_users_path,
          logo: 'flat/256/settings48.png',
          label: t('links.settings.title')
      }
    end
    @items << {
        url: edit_user_registration_path,
        logo: 'flat/256/male80.png',
        label: t('links.personal.info')
    }
    @items << {
        url: wikis_path(name: 'help'),
        logo: 'flat/256/edit26.png',
        label: t('links.wiki.list')
    }
    @items << {
        url: rss_path,
        logo: 'flat/256/rss47.png',
        label: t('links.rss_item.list')
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
    render 'index', layout:'application'

  end


end
