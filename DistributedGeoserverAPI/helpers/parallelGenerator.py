#!/usr/bin/python3
# -*- coding: utf-8 -*-
import os 
from flask import g, current_app
from concurrent.futures import ThreadPoolExecutor
from multiprocessing import Pool



def getThreads(x=0):
    if 'threadPool' not in g:
        if(x==0):
            x=min(len(os.sched_getaffinity(0)), current_app.config["NODES"])
        g.threadPool = ThreadPoolExecutor(max_workers=16)
    return g.threadPool

def getProcessorPool(x=0):
    if 'threadPool' not in g:
        if(x==0):
            x=min(len(os.sched_getaffinity(0)), current_app.config["NODES"])
        g.processpool = Pool(x)
    return g.processpool

#def close_db():
#  db = g.pop('sqldb', None)
#  if db is not None:
#    db.close()