#!/usr/bin/python3
# -*- coding: utf-8 -*-
from root_register import app

if __name__ == "__main__":
    # insert production server deployment code
    app.run(host='0.0.0.0', port=int(app.config["PORT"]), use_reloader=False)
    #app.run(use_reloader=True)

