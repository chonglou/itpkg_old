class StoriesController < ApplicationController
  def new
  end

  def show
  end

  def edit
  end

  def update
  end

  def destroy
    story = Story.find params[:id]

    if story.requester_id == current_user.id
      story.destroy
      project = Project.find params[:project_id]
    else
      flash[:alert] = t('labels.in_using')
    end

    redirect_to project_path(project)
  end
end
