(function ($) {
  'use strict';

  $(function () {
    var $edit_task_comment_button = $('.edit_task_comment_button'),
      $edit_task_comment_content  = $('#edit_task_comment_content'),
      $edit_task_comment_form     = $('#edit_task_comment_form');

    $edit_task_comment_button.on('click', function () {
      var $this = $(this),
        new_action;
      $edit_task_comment_content.val($this.data('content'));

      new_action = $edit_task_comment_form.prop('action').replace(/task_comments[(.)|(\/)]\d*/, 'task_comments/' + $this.data('id'));
      $edit_task_comment_form.prop('action', new_action);
    });

    $edit_task_comment_form.on('submit', function () {
      if ($edit_task_comment_content.val() === '') {
        alert('Comment cannot be blank!');
        return false;
      }
    });
  });
})(jQuery);
