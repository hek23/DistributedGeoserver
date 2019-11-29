from flask import Flask, request, current_app, g
import requests
from requests.auth import HTTPBasicAuth
import json
import os
from datetime import datetime

from werkzeug.utils import secure_filename

app = Flask(__name__)


def initimport(dsName, wsName):
    importer = {
        "import": {
            "targetStore": {
                "dataStore": {
                    "name": dsName
                }
            },
            "targetWorkspace": {
                "workspace": {
                    "name": wsName
                }
            }
        }
    }

    # Init import
    response = requests.post(current_app.config.get("GEOSERVER")+"/rest/imports",
                         data=json.dumps(importer),
                         headers={'Content-Type': 'application/json'}, auth=HTTPBasicAuth(current_app.config.get("GEOSERVERUSR"), current_app.config.get("GEOSERVERPWD")))

    if(response.status_code==201):
        return response.json()['import']['id']
    else:
        return -1


def activateImport(id):
    response = requests.post(current_app.config.get("GEOSERVER")+"/rest/imports/"+str(id)+"/?async=true",
                         headers={'Content-Type': 'application/json'}, auth=HTTPBasicAuth(current_app.config.get("GEOSERVERUSR"), current_app.config.get("GEOSERVERPWD")))
    print(response.text)
    return

def defineDsType(id):

    reset = {
      "dataStore": {
        "name":"postgis"
      }
    }
    r = requests.put(current_app.config.get("GEOSERVER")+"/rest/imports/"+str(id)+"/tasks/0/target", data=json.dumps(reset), auth=HTTPBasicAuth(current_app.config.get("GEOSERVERUSR"), current_app.config.get("GEOSERVERPWD")))

    activateImport(id)


def convertFile(file):
    name = secure_filename(file.filename)
    file.save(name)
    file = open(name, 'rb')
    return file


def importSHP(dsName, wsName, file):
    id = initimport(dsName,wsName)
    if(id<0): #Request failed
        pass
    else:
        file = convertFile(file)
        files = {'file': file}
        r = requests.post(current_app.config.get("GEOSERVER")+"/rest/imports/"+str(id)+"/tasks", files=files, auth=HTTPBasicAuth(current_app.config.get("GEOSERVERUSR"), current_app.config.get("GEOSERVERPWD")))

        file.close()
        os.remove((secure_filename(file.name)))
        if(r.status_code==201):
            defineDsType(id)
        else:
            return


@current_app.route('/')
def hello_world():
    return 'Hello World!'


@current_app.route('/upload', methods=['GET', 'POST'])
def upload():
    if request.method == 'POST':
          f = request.files['file']
          #zipshape = zipfile.ZipFile(f)
          #test = zipshape.namelist()
          #print(test)
          ws = request.form['workspace']
          ds = request.form['datastore']
          importSHP(ds,ws,f)
          return 'file uploaded successfully'
