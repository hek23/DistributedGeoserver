#!/usr/bin/python3
# -*- coding: utf-8 -*-
from flask import current_app, g
import psycopg2
import os

def get_db():
  #g is a context, could give performance issues
  if 'sqldb' not in g:
    g.sqldb = psycopg2.connect(current_app.config["CONN_STRING"])
  return g.sqldb

def close_db(e=None):
  db = g.pop('sqldb', None)
  if db is not None:
    db.close()

#Register methods for close
def init_app(app):
  app.teardown_appcontext(close_db)
  #Define appContext for import modules that require flask_app
  with app.app_context():
    g.sqldb = get_db()

def getCursor():
  return get_db().cursor()
