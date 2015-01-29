class Projects::DocumentsController < ApplicationController
  before_action :authenticate_user!
  before_action :_can_project?

  def viewer
    @document = Document.find params[:document_id]
    if _can_view?
      f = Mongoid::GridFS[@document.avatar.path]
      send_data f.data, type: f.content_type, disposition: :inline
    else
      render status: 404
    end
  end

  def download
    @document = Document.find params[:document_id]
    if _can_view?
      f = Mongoid::GridFS[@document.avatar.path]
      send_data f.data, type: f.content_type, filename: @document.name
    else
      render status: 404
    end
  end

  def index
    @documents = Document.select(:id, :name, :size, :updated_at).order(id: :desc).where('project_id = ? AND status = ?', @project.id, Document.statuses[:project]).page(params[:page])
    @items = @documents.map { |d| {cols: [d.name, d.size_s, d.updated_at], url: project_document_path(d.id, project_id: @project.id)} }
    @buttons=[
        {label: t('links.project.document.create', name: @project.name), url: new_project_document_path, style: 'primary'},
        {label: t('links.project.show', name: @project.name), url: project_path(@project), style: 'warning'}
    ]
  end

  def show
    @document = Document.find params[:id]
    if _can_view?
      @buttons=[

          {label: t('links.project.document.download', name: @document.name), url: project_document_download_path(document_id: @document.id, project_id: @project.id), style: 'success'},
          {label: t('links.project.document.list'), url: project_documents_path, style: 'warning'}
      ]
      if _can_edit?
        @buttons << {label: t('links.project.document.edit', name: @document.name), url: edit_project_document_path, style: 'primary'}
      end
    else
      render status: 404
    end
  end

  def create
    files = params.fetch(:files).map do |tf|
      doc = Document.new project_id: @project.id,
                         name: tf.original_filename, ext: _file_ext(tf.original_filename),
                         size: tf.size, details: ''
      doc.avatar= tf
      if doc.save
        current_user.add_role :creator, doc
        {
            id: doc.id,
            name: doc.name,
            size: doc.size,
            url: project_document_path(doc.id, project_id: @project.id),
            deleteUrl: project_document_path(doc.id, project_id: @project.id),
            deleteType: 'DELETE'
        }
      else
        {
            name: tf.original_filename,
            error: doc.errors.fetch(:avatar).join('<br>')
        }
      end
    end

    render json: {files: files}

  end

  def edit
    @document = Document.find params[:id]
    if _can_edit?
      render 'edit'
    else
      render status: 404
    end
  end

  def update
    @document = Document.find params[:id]
    if _can_edit?
      if @document.update(params.require(:document).permit(:name, :details))
        redirect_to project_document_path(@document.id, project_id: @project.id)
      else
        render 'edit'
      end
    else
      render status: 404
    end
  end

  def destroy
    #files=[]
    @document = Document.find params[:id]
    if _can_edit?
      Document.destroy params[:id]
      #files << {d.name => true}
    end
    redirect_to project_documents_path(project_id: @project)
    #render json: {files: files}
  end

  private
  def _can_edit?
    current_user.is_creator_of?(@document)
  end

  def _can_view?
    @document.project?
  end

  def _can_project?
    @project = Project.find params[:project_id]
    current_user.is_member_of?(@project) || current_user.is_creator_of?(@project)
  end

  def _file_ext(name)
    i = name.rindex('.')
    name[i+1, name.size].downcase if i
  end
end
