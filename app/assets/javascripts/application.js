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
//= require bootstrap-sass/assets/javascripts/bootstrap-sprockets
//= require angular/angular
//= require holderjs/holder
//= require moment/moment
//= require moment/locale/zh-cn
//= require bootstrap3-datetimepicker/build/js/bootstrap-datetimepicker.min.js
//= require highlightjs/highlight.pack
//= require highcharts/highcharts
//= require highcharts/highcharts-more
//= require select2-ng/select2
//= require select2-ng/select2_locale_zh-CN


//= require blueimp-load-image/js/load-image.all.min
//= require blueimp-canvas-to-blob/js/canvas-to-blob.min
//= require jquery-file-upload/js/vendor/jquery.ui.widget
//= require jquery-file-upload/js/jquery.iframe-transport
//= require jquery-file-upload/js/jquery.fileupload
//= require jquery-file-upload/js/jquery.fileupload-process
//= require jquery-file-upload/js/jquery.fileupload-image
//= require jquery-file-upload/js/jquery.fileupload-audio
//= require jquery-file-upload/js/jquery.fileupload-video
//= require jquery-file-upload/js/jquery.fileupload-validate
//= require jquery-file-upload/js/jquery.fileupload-angular
//= require im

//= require widgets

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
