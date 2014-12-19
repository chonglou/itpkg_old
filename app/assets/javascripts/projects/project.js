(function ($) {
  'use strict';

  $(function () {

    $('.add-user-link').on('click', function (e) {
      e.preventDefault();

      var $this = $(this),
        project_id = $this.data('project-id'),
        user_id = $this.data('user-id');

      $.ajax({
        url: '/projects/' + project_id + '/add_user',
        type: 'POST',
        data: {user_id: user_id},
        dataType: 'json'
      }).complete(function () {
        $this.find('.glyphicon').removeClass('glyphicon-plus-sign').addClass('glyphicon-ok');
      });
    });

    $('.remove-user-link').on('click', function (e) {
      e.preventDefault();

      var $this = $(this),
        project_id = $this.data('project-id'),
        user_id = $this.data('user-id');

      $.ajax({
        url: '/projects/' + project_id + '/remove_user',
        type: 'POST',
        data: {user_id: user_id},
        dataType: 'json'
      }).complete(function () {
        $this.find('.glyphicon').removeClass('glyphicon-ok').addClass('glyphicon-plus-sign');
      });
    });
  });
})(jQuery);
