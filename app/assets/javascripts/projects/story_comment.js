(function ($) {
  'use strict';

  $(function () {
    var $edit_story_comment_button = $('.edit_story_comment_button'),
      $edit_story_comment_content  = $('#edit_story_comment_content'),
      $edit_story_comment_form     = $('#edit_story_comment_form');

    $edit_story_comment_button.on('click', function () {
      var $this = $(this),
        new_action;
      $edit_story_comment_content.val($this.data('content'));

      new_action = $edit_story_comment_form.prop('action').replace(/story_comments\/\d*/, 'story_comments/' + $this.data('id'));
      $edit_story_comment_form.prop('action', new_action);
    });
  });
})(jQuery);
