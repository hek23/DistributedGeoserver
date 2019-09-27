from flask import Flask
import os

#App initialization
app = Flask(__name__)

app.config.from_pyfile('../config/default.py')

#Init DB Connections
from helpers import psqlConnector

#Register Databases in app
psqlConnector.init_app(app)
#Define appContext for import modules that require flask_app
with app.app_context():
  #Import Controllers, routes, etc
  from controllers import *
