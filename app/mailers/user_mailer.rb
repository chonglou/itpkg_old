class UserMailer < ActionMailer::Base
  def key_pairs(user_id)
    u = User.find user_id
    attachments['id_rsa'] = u.ssh_key.private_key
    attachments['id_rsa.pub'] = u.ssh_key.public_key
    mail( to:u.email, subject: t('mails.key_pairs.subject')) if u
  end
end
