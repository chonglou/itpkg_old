class AddTrackingTimesToStorysAndTasks < ActiveRecord::Migration
  def change
    add_column :stories, :plan_start_time,  :datetime
    add_column :stories, :real_start_time,  :datetime
    add_column :stories, :plan_finish_time, :datetime
    add_column :stories, :real_finish_time, :datetime
    add_column :tasks, :plan_start_time,  :datetime
    add_column :tasks, :real_start_time,  :datetime
    add_column :tasks, :plan_finish_time, :datetime
    add_column :tasks, :real_finish_time, :datetime

    add_column :tasks, :point,    :integer
    add_column :tasks, :status,   :integer, limit: 1

    rename_column :tasks, :level, :priority
  end
end
