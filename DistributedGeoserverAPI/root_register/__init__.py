from flask import Flask
import os
from flask_cors import CORS
#App initialization
app = Flask(__name__)
CORS(app, resources={r"/*": {"origins": "*"}}, supports_credentials=True)
  
app.config.from_pyfile('../config/default.py')

#Init DB Connections
from helpers import psqlConnector,redisManager

#Register Databases in app
psqlConnector.init_app(app)
redisManager.init_app(app)

#Define appContext for import modules that require flask_app
with app.app_context():
  #Import Controllers, routes, etc
  from controllers import *
