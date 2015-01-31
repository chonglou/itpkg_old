$(function () {
  Itpkg.active_nav_link(document.URL);

  $('pre code').each(function (i, block) {
    hljs.highlightBlock(block);
  });

  $.ajaxSetup({headers: {'X-CSRF-Token': $('meta[name="csrf-token"]').attr('content')}});

  $("div .datetimepicker").datetimepicker();

  $('.im-friend').on('click', function () {
    $('#chat_box').show();
  });
});


