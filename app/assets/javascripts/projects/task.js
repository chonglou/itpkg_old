(function ($) {
  'use strict';

  function add_new_task_to_table(data) {
    var $tasks          = $('#tasks'),
      $new_row          = $('#task_row_template').clone().removeClass('hide'),
      $new_row_els      = $new_row.find('td'),
      $delete_button    = $new_row.find('.btn-danger'),
      new_task_id       = 'tasks/' + data.id,
      href;

    $new_row_els.eq(0).text(data.id);
    $new_row_els.eq(1).text(data.details);

    href = $delete_button.prop('href').replace('tasks/0', new_task_id);
    $delete_button.prop('href', href);

    $tasks.append($new_row);
  }

  function close_add_task_form(create_task_form, new_task) {
    create_task_form.hide();
    new_task.show();
  }

  $(function () {
    var $new_task        = $('#new_task'),
      $task_form_filed   = $('#task_form_filed'),
      $task_details      = $('#details'),
      $cancel_task       = $('#cancel_task'),
      $create_task_form  = $('#create_task_form'),
      $task_form         = $('#task_form'),
      $edit_task_form    = $('#edit_task_form'),
      $edit_task_details = $('#edit_task_details');

    $new_task.on('click', function (e) {
      e.preventDefault();

      $(this).hide();
      $task_details.val('');
      $task_form_filed.removeClass('has-error');
      $create_task_form.show();
    });

    $task_form.on('ajax:beforeSend', function () {
      if ($task_details.val() === '') {
        $task_form_filed.addClass('has-error');
        alert('Details cannot be blank!');
        return false;
      }
    });

    $task_form.on('ajax:success', function (xhr, data) {
      add_new_task_to_table(data);

      close_add_task_form($create_task_form, $new_task);
    });

    $cancel_task.on('click', function (e) {
      e.preventDefault();

      close_add_task_form($create_task_form, $new_task);
    });

    $('#tasks').on('click', '.edit_task', function (e) {
      e.preventDefault();

      var $this = $(this),
        task_id = $this.parent().siblings('.task_id').text().trim(),
        task_details = $this.parent().siblings('.task_details'),
        new_action;

      $edit_task_details.val(task_details.text().trim());
      $('#task_id').val(task_id);
      new_action = $edit_task_form.prop('action').replace(/tasks\/\d*/, 'tasks/' + task_id);
      $edit_task_form.prop('action', new_action);
    });

    $edit_task_form.on('submit', function () {
      if ($('#edit_task_details').val() === '') {
        alert('Details cannot be blank!');
        return false;
      }
    });
  });
})(jQuery);
