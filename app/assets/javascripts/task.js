(function ($) {
  'use strict';

  function add_new_task_to_table(data) {
    var $tasks          = $('#tasks'),
      $new_row          = $('#task_row_template').clone().removeClass('hide'),
      $new_row_els      = $new_row.find('td'),
      $new_action_links = $new_row.find('a'),
      new_task_id       = 'tasks/' + data.id;

    $new_row_els.eq(0).text(data.id);
    $new_row_els.eq(1).text(data.details);

    $new_action_links.each(function () {
      var href = $(this).prop('href').replace('tasks/0', new_task_id);
      $(this).prop('href', href);
    });

    $tasks.append($new_row);
  }

  function close_add_task_form(create_task_form, new_task) {
    create_task_form.hide();
    new_task.show();
  }

  $(function () {
    var $new_task       = $('#new_task'),
      $task_form_filed  = $('#task_form_filed'),
      $task_details     = $('#details'),
      $cancel_task      = $('#cancel_task'),
      $create_task_form = $('#create_task_form'),
      $task_form        = $('#task_form');

    $new_task.on('click', function (e) {
      e.preventDefault();

      $(this).hide();
      $task_details.val('');
      $create_task_form.show();
    });

    $task_form.on('ajax:beforeSend', function () {
      if ($task_details.val() === '') {
        $task_form_filed.addClass('has-error');
        alert('Details cannot be blank!');
        return false;
      }
    });

    $task_form.on('ajax:success', function (xhr, data, status) {
      add_new_task_to_table(data);

      close_add_task_form($create_task_form, $new_task);
    });

    $cancel_task.on('click', function (e) {
      e.preventDefault();

      close_add_task_form($create_task_form, $new_task);
    });


  });
})(jQuery);
