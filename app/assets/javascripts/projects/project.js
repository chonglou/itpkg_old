(function ($) {
  'use strict';

  function initializePagination() {
    $('.user-info').hide();
    $('.user_page_0').show();
    $('.js-page-number:first').parent().addClass('active');

    $('.js-page-number').on('click', function (e) {
      e.preventDefault();
      var $this = $(this),
        target_users = $this.data('page');

      $this.parent().addClass('active').siblings().removeClass('active');
      $('.user-info').hide();
      $(target_users).show();
    });

    $('#prev_page').on('click', function (e) {
      e.preventDefault();

      var $this = $(this),
        parent = $this.parent(),
        active_page = parent.siblings('.active'),
        prev_page = active_page.prev(),
        prev_page_child = prev_page.children().first();

      if (prev_page_child.attr('id') !== 'prev_page') {
        prev_page_child.trigger('click');
      }
    });

    $('#next_page').on('click', function (e) {
      e.preventDefault();

      var $this = $(this),
        parent = $this.parent(),
        active_page = parent.siblings('.active'),
        next_page = active_page.next(),
        next_page_child = next_page.children().first();

      if (next_page_child.attr('id') !== 'next_page') {
        next_page_child.trigger('click');
      }
    });
  }

  function initializeAddUsersForm() {
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

    $add_users_form.on('ajax:beforeSend', function (xhr) {
      xhr.setRequestHeader('X-CSRF-Token', $('meta[name="csrf-token"]').attr('content'));
    });
  }

  window.initializeAddUsers = function () {
    initializePagination();
    initializeAddUsersForm();
  };

  $(function () {
    ZeroClipboard.config({swfPath: 'https://cdnjs.cloudflare.com/ajax/libs/zeroclipboard/2.2.0/ZeroClipboard.swf'});
    var copy_button = $('.js-copy-button');

    copy_button.on('click', function () {
      ZeroClipboard.setData('text/plain', $(this).prev().val());
    });

    copy_button.hover(
      function () {
        $(this).children('i').tooltip('show');
      },
      function () {
        $(this).children('i').tooltip('hide');
      }
    );

  });
})(jQuery);
