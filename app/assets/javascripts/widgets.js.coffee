active_nav_link = (uri)->
  $("ul#nav_bar li a").each (index, element)=>
    #if uri.indexOf($(element).attr('href').split('?')[0]) == 0
    if $(element).attr('href') == uri
      $(element).parent().addClass('active')
    return
  return

#-------------初始化设置-------------------
$.ajaxSetup headers: {'X-CSRF-Token': $('meta[name="csrf-token"]').attr('content')}

namespace = (target, name, block) ->
  [target, name, block] = [(if typeof exports isnt 'undefined' then exports else window), arguments ...] if arguments.length < 3
  top = target
  target = target[item] or= {} for item in name.split '.'
  block target, top
  return

namespace 'Itpkg', (exports) ->
  exports.active_nav_link = active_nav_link
  return