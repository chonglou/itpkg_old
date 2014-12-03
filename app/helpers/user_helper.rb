module UserHelper
  def user_select_options
    User.where('id != ?', current_user.id).select{|u| u.confirmed?}.map{|u| [u.to_s, u.id]}
  end
end