module LoggingNodesHelper
  def logging_nodes_nav_items
    [
        {
            name: t('links.logging_item.list'),
            url: logging_items_url
        },
        {
            name: t('links.logging_node.list'),
            url: logging_nodes_url
        },
        {
            name: t('links.logging_search.quick'),
            url: new_logging_search_url
        },
        {
            name: t('links.logging_search.list'),
            url: logging_searches_url
        }
    ]
  end
end