// This is a manifest file that'll be compiled into application.js, which will include all the files
// listed below.
//
// Any JavaScript/Coffee file within this directory, lib/assets/javascripts, vendor/assets/javascripts,
// or vendor/assets/javascripts of plugins, if any, can be referenced here using a relative path.
//
// It's not advisable to add code directly here, but if you do, it'll appear at the bottom of the
// compiled file.
//
// Read Sprockets README (https://github.com/sstephenson/sprockets#sprockets-directives) for details
// about supported directives.
//
//= require jquery
//= require jquery_ujs
//= require turbolinks
//= require holder
//= require moment
//= require moment/zh-cn
//= require bootstrap-sprockets
//= require bootstrap-datetimepicker
//= require angular
//= require angular-animate
//= require angular-resource
//= require highcharts
//= require highcharts/highcharts-more
//= require highlight-8.4
//= require jstree-3.0.8
//= require jquery-ui/widget
//= require jQuery-File-Upload-9.8.1/load-image.all.min
//= require jQuery-File-Upload-9.8.1/canvas-to-blob.min
//= require jQuery-File-Upload-9.8.1/jquery.iframe-transport
//= require jQuery-File-Upload-9.8.1/jquery.fileupload.js
//= require jQuery-File-Upload-9.8.1/jquery.fileupload-process.js
//= require jQuery-File-Upload-9.8.1/jquery.fileupload-image.js
//= require jQuery-File-Upload-9.8.1/jquery.fileupload-audio.js
//= require jQuery-File-Upload-9.8.1/jquery.fileupload-video.js
//= require jQuery-File-Upload-9.8.1/jquery.fileupload-validate.js
//= require jQuery-File-Upload-9.8.1/jquery.fileupload-angular.js
//= require widgets
//= require select2
//= require select2_locale_zh-CN


function swap_select_items(from, to){

    $("select#"+from+" :selected").map(function (i, el) {
        $("select#"+to).append("<option value='" + $(el).val() + "'>" + $(el).text() + "</option>");
        $("select#"+from+" option[value='" + $(el).val() + "']").remove();
    });
}

(function ($) {
  'use strict';

  $(function () {
    $('[data-toggle="tooltip"]').tooltip();

    $(document).on('click', '.clickable-td', function () {
      window.location = $(this).parent().data('link');
    });
  });

})(jQuery);
