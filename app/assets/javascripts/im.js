//= require jquery_ujs

(function ($) {
  'use strict';

  $(function () {
    $(document).on('click', '.im-friend', function () {
      var url = $(this).data('url'),
        new_window = window.open(url, '', 'width=500,height=535');

      new_window.focus();
    });
  });
})(jQuery);
