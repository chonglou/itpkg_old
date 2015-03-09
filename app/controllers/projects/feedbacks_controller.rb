require 'itpkg/services/site'

class Projects::FeedbacksController < ApplicationController
  before_action :prepare_project
  before_action :check_user_authority, only: [:index, :show, :destroy]

  def index
    @buttons = [
      {label: t('links.feedback.project'), url: project_path(@project), style: 'primary'}
    ]

    @feedbacks = @project.feedbacks.select(&:active?)
  end

  def show
    @buttons = [
      {label: t('links.feedback.project'), url: project_path(@project), style: 'primary'}
    ]

    @feedback = Feedback.includes(:user).where(id: params[:id], active: true).first
  end

  def new
    @feedback = Feedback.new
  end

  def create
    @feedback = @project.feedbacks.build(feedback_params.merge({status: 0, active: 1}))

    if @feedback.save
      redirect_to project_feedback_success_path(@project, @feedback)
    else
      render :new
    end
  end

  def update_status
    @feedback = Feedback.includes(:user).where(id: params[:id], active: true).first
    @feedback.update(status: params[:status])
    @feedback.update(user: current_user) if params[:status] == 'processing'

    redirect_to :back
  end

  def success
  end

  def destroy
    feedback = Feedback.find_by_id(params[:id])

    feedback.destroy if feedback.present?

    redirect_to project_feedbacks_path(@project)
  end

  private
  def prepare_project
    @project = Project.where(id: params[:project_id], active: true).first
  end

  def check_user_authority
    unless current_user.is_member_of? @project
      flash[:alert] = t('message.unauthorized')
      redirect_to :back
    end
  end

  def feedback_params
    params.require(:feedback).permit(:name, :email, :phone_number, :content)
  end

end
