#!/usr/bin/python3
# -*- coding: utf-8 -*-

from redis import Redis
from flask import current_app, g

def getRedis():
  #g is a context, could give performance issues
  if 'redis' not in g:
    g.redis = Redis(host= current_app.config["REDISHOST"], port =current_app.config["REDISPORT"], db=current_app.config["REDISDB"])
  return g.redis

#Register methods for close
def init_app(app):
  #app.teardown_appcontext(close_db)
  #Define appContext for import modules that require flask_app
  with app.app_context():
    g.sqldb = getRedis()

def saveObject(object):
  return

def loadObject(key):
  return
  
