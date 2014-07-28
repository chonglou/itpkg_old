#!/usr/bin/env python
#coding=utf-8

__author__ = 'letminba@gmail.com'

from flask import Flask, g, request, redirect, url_for, session
from flaskext.principal import Principal, identity_loaded
from logging.handlers import RotatingFileHandler

from webadm.extensions import db, cache

from webadm.views.vpn import vpn_page
from webadm.views.dhcpd import dhcpd_page
from webadm.views.employee import employee_page
from webadm.views.host import host_page
from webadm.views.named import named_page
from webadm.views.personal import personal_page
from webadm.views.site import site_page

from webadm.views.firewall import firewall_page
from webadm.views.firewall.flow_rule import ff_flow_page
from webadm.views.firewall.mac import ff_mac_page
from webadm.views.firewall.dmz import ff_dmz_page
from webadm.views.firewall.lan import ff_lan_page
from webadm.views.firewall.wan import ff_wan_page
from webadm.views.firewall.item import ff_item_page
from webadm.views.firewall.in_rule import ff_in_page
from webadm.views.firewall.out_rule import ff_out_page
from webadm.views.firewall.nat_rule import ff_nat_page
from webadm.views.firewall.mgr import ff_mgr_page

PAGE_MODULES = {
    '/dhcpd': dhcpd_page,
    '/employee': employee_page,
    '/ff_item': ff_item_page,
    '/firewall': firewall_page,
    '/ff_flow': ff_flow_page,
    '/ff_dmz': ff_dmz_page,
    '/ff_lan': ff_lan_page,
    '/ff_wan': ff_wan_page,
    '/ff_in': ff_in_page,
    '/ff_out': ff_out_page,
    '/ff_nat': ff_nat_page,
    '/ff_mgr':ff_mgr_page,
    '/host': host_page,
    '/mac': ff_mac_page,
    '/named': named_page,
    '/personal': personal_page,
    '/site': site_page,
    '/vpn': vpn_page,
    }

def create_app(cfg_file):
    app = Flask(__name__)
    app.config.from_pyfile(cfg_file)
    configure_extensions(app)
    configure_pages(app)
    configure_filter(app)
    configure_logging(app)
    configure_identity(app)
    return app


def configure_filter(app):
    @app.before_request
    def before_request():
        from webadm.extensions import site_id
        from webadm.models.site import Site

        g.site = Site.query.get(site_id)

        if 'who' in session:
            if request.path.find('login') != -1:
                return redirect(url_for('personal_page.index'))
        else:
            if request.path.find('static') == -1 and request.path not in ['/about_me', '/personal/login', '/install']:
                return redirect(url_for('personal_page.login'))


    @app.teardown_request
    def teardown_request(execpt):
        pass


def configure_extensions(app):
    db.init_app(app)
    cache.init_app(app)


def configure_identity(app):
    principal = Principal(app)

    @identity_loaded.connect_via(app)
    def on_identity_loaded(sender, identity):
        pass


def configure_logging(app):
    if not app.debug:
        import logging

        log_handler = RotatingFileHandler(app.config['DEBUG_LOG'], maxBytes=100000, backupCount=10)
        log_handler.setLevel(logging.WARNING)
        for logger in [app.logger, logging.getLogger('sqlalchemy')]:
            logger.addHandler(log_handler)
    pass


def configure_pages(app):
    for k in PAGE_MODULES.keys():
        app.register_blueprint(PAGE_MODULES[k], url_prefix=k)

    from webadm.views.comm import about_me, index, search

    app.add_url_rule('/', 'index', index)
    app.add_url_rule('/about_me', 'about_me', about_me)
    app.add_url_rule('/search', 'search', search, methods=['POST'])
    