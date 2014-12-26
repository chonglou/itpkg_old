class UpdateTeamworkRelatedModels < ActiveRecord::Migration
  def change
    add_column :projects,       :active, :boolean, default: true
    add_column :stories,        :active, :boolean, default: true
    add_column :tasks,          :active, :boolean, default: true
    add_column :story_comments, :active, :boolean, default: true
    add_column :task_comments,  :active, :boolean, default: true
  end
end
