#!/usr/bin/python3
# -*- coding: utf-8 -*-
from root_register import app
import os

app.run(host='0.0.0.0', port=int(app.config["PORT"]))