class SessionsController < Devise::SessionsController
  after_filter :log_failed_login, only: :new
  before_filter :log_logout, only: :destroy

  def create
    super
    Log.create(user_id: login_user.id, message: t('logs.user.login.success'))
  end

  private
  def log_failed_login
    if failed_login?
      u = login_user
      Log.create(user_id:(u ? u.id : 0), message: t('logs.user.login.fail', login:_login_by))
    end
  end

  def failed_login?
    (options = env['warden.options']) && options[:action] == 'unauthenticated'
  end

  def login_user
    User.find_by('label = :value OR email = :value', {value: _login_by})
  end

  def _login_by
    request.filtered_parameters['user'].fetch('login')
  end

  def log_logout
    Log.create user_id: current_user.id, message: t('logs.user.logout')
  end

end