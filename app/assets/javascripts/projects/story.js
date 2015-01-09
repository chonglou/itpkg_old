(function ($) {
  'use strict';

  function getTags(project_id, type) {
    var tags = [];

    $.ajax({
      url: '/projects/' + project_id + '/' + type,
      dataType: 'json',
      async: false
    })
      .done(function (data) {
        tags = $.map(data, function (tag) {
          return {id: tag.id, text: tag.name};
        });
      });

    return tags;
  }

  $(function () {
    var project_id = $('#new_story, #edit_story').data('project-id'),
      story_types,
      story_tags;

    story_types = getTags(project_id, 'story_types');
    story_tags = getTags(project_id, 'story_tags');

    $('#story_story_type_ids').select2({
      tags: story_types,
      multiple: true,
      maxLength: 10
    });

    $('#story_story_tag_ids').select2({
      tags: story_tags,
      multiple: true,
      maxLength: 10
    });

    $('.js-story-list').hover(
      function () {
        $(this).addClass('active');
      },
      function () {
        $(this).removeClass('active');
      }
    );
  });
})(jQuery);
