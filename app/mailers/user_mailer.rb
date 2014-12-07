require 'json'

class UserMailer < ActionMailer::Base
  default from: ENV['ITPKG_MAILER_SENDER']

  def log(user_id, subject, message)
    u = User.find user_id
    mail(to: u.email, subject: subject, message: message).deliver
  end

  def key_pairs(user_id)
    u = User.find user_id
    attachments['id_rsa'] = u.ssh_key.private_key
    attachments['id_rsa.pub'] = u.ssh_key.public_key
    mail(to: u.email, subject: t('mails.key_pairs.subject')).deliver
  end

  def remove_from_repository(repository_id, user_id)
    u = User.find user_id
    r = Repository.find repository_id
    @name = r.name
    mail(to: u.email, subject: t('mails.remove_from_repository.subject', name: @name)).deliver
  end

  def confirm(c_id)
    c = Confirmation.find c_id
    @token = c.token
    @extra = JSON.parse c.extra
    @deadline = c.deadline
    mail(to: c.user.email, subject: c.subject).deliver
  end

  def git_commit(to, logs)

  end
end
