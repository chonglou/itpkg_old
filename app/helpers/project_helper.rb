module ProjectHelper
  def status_select_options
    %w(submit processing finish reject done)
  end

  def priority_select_options
    %w(immediately high normal low ignore)
  end

  def point_select_options
    (1..10).to_a
  end

  def story_action_button(story)
    text = link = style = disabled = nil

    case story.status
      when 'submit'
        text = t('buttons.start')
        link = project_story_update_status_path(story.project, story, id: story.id, status: :processing)
        style = 'btn btn-default'
      when 'processing'
        text = t('buttons.finish')
        link = project_story_update_status_path(story.project, story, id: story.id, status: :finish)
        style = 'btn btn-primary'
      when 'finish'
        button_accept = button_to t('buttons.accept'),
                                  project_story_update_status_path(story.project, story, id: story.id, status: :done),
                                  class: 'btn btn-success pull-left'
        button_reject = button_to t('buttons.reject'),
                                  project_story_update_status_path(story.project, story, id: story.id, status: :reject),
                                  class: 'btn btn-danger'

        return [button_accept, button_reject]
      when 'reject'
        text = t('buttons.restart')
        link = project_story_update_status_path(story.project, story, id: story.id, status: :processing)
        style = 'btn btn-default'
      when 'done'
        text     = t('buttons.complete')
        style    = 'btn btn-default'
        disabled = true
      else return []
    end

    button = button_to text, link, class: style, disabled: disabled
    [button]
  end

  def task_action_button(task)
    text = link = style = disabled = nil
    story = task.story

    case task.status
      when 'submit'
        text = t('buttons.start')
        link = project_story_task_update_status_path(story.project, story, task, id: task.id, status: :processing)
        style = 'btn btn-default'
      when 'processing'
        text = t('buttons.finish')
        link = project_story_task_update_status_path(story.project, story, task, id: task.id,status: :finish)
        style = 'btn btn-primary'
      when 'finish'
        button_accept = button_to t('buttons.accept'),
                                  project_story_task_update_status_path(story.project, story, task, id: task.id, status: :done),
                                  class: 'btn btn-success pull-left'
        button_reject = button_to t('buttons.reject'),
                                  project_story_task_update_status_path(story.project, story, task, id: task.id, status: :reject),
                                  class: 'btn btn-danger'

        return [button_accept, button_reject]
      when 'reject'
        text = t('buttons.restart')
        link = project_story_task_update_status_path(story.project, story, task, id: task.id, status: :processing)
        style = 'btn btn-default'
      when 'done'
        text     = t('buttons.complete')
        style    = 'btn btn-default'
        disabled = true
      else return []
    end

    button = button_to text, link, class: style, disabled: disabled
    [button]
  end

  def feedback_action_button(feedback)
    text = link = style = disabled = nil

    case feedback.status
      when 'submit'
        text = t('buttons.start')
        link = project_feedback_update_status_path(feedback.project, feedback, id: feedback.id, status: :processing)
        style = 'btn btn-default'
      when 'processing'
        text = t('buttons.finish')
        link = project_feedback_update_status_path(feedback.project, feedback, id: feedback.id, status: :done)
        style = 'btn btn-primary'
        disabled = current_user != feedback.user
      when 'done'
        text     = t('buttons.complete')
        style    = 'btn btn-default'
        disabled = true
      else return
    end

    button_to text, link, class: style, disabled: disabled
  end
end