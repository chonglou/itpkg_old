class UserMailer < ActionMailer::Base
  def key_pairs(user_id)
    u = User.find user_id
    #todo
    mail( to:u.email, subject: t('mails.key_pairs.subject')) if u
  end
end
