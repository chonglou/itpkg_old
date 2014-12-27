(function ($) {
  'use strict';

  $(function () {
    var $add_users_form     = $('#add_users_form'),
      $add_users_button     = $('#add_users_button'),
      $project_members      = $('#project_members'),
      $none_project_members = $('#none_project_members');

    $add_users_button.on('click', function (e) {
      e.preventDefault();

      var checked_users_ids = [],
        unchecked_users_ids = [];

      $('.js-user-checkbox').each(function () {
        var $this = $(this);

        if ($this.is(':checked')) {
          checked_users_ids.push($this.val());
        } else {
          unchecked_users_ids.push($this.val());
        }
      });

      $project_members.val(checked_users_ids.toString());
      $none_project_members.val(unchecked_users_ids.toString());

      $add_users_form.trigger('submit');
    });
  });
})(jQuery);
