from os import sched_getaffinity
from json import loads
from flask import current_app, jsonify
from concurrent.futures import as_completed
from numpy import array, array_split

from helpers.parallelGenerator import getThreads
from helpers.cropper import cropByPolygon
from root_register import app as globalApp

# Return array of objects {"attributes": {}, "polygons": {}}
def formatShape(layer,polygon):
    with globalApp.app_context():
        results = []
        information_layer, result = cropByPolygon(layer, polygon)
        layer = layer.split(".")[-1]
        for row in result:
            #Each result will be one object
            attr = {}
            for attri in range(len(information_layer)):
                attr.update({layer +"." +information_layer[attri][0]: row[attri]}) # attr
            results.append({"polygons": loads(row[-1]), "attributes": attr})
        return results


#Reformats to {attributes:[], polygons:[]}. Must be corresponding
def resultFormatter(objectList):
    #As indexes must match, a controlled and limited access to
    #final data containers are done
    #Distribute the objectList on "even" parts
    threadCount = min(len(sched_getaffinity(0)), current_app.config["THREADS"])
    #Transform list to array, for array_split use
    objectList = array_split(array(objectList),threadCount)
    futureRes = []
    executor = getThreads()
    #Now, each element from objectList is ready to work parallel
    for partialList in objectList:
        splitted = executor.submit(splitter, partialList)
        futureRes.append(splitted)
    #We care order, so add to results, only if finished
    polygons = []
    attributes = []
    for res in as_completed(futureRes):
        attributes.extend(res.result()[0])
        polygons.extend(res.result()[1])
    return jsonify({"attributes":attributes, "polygons": polygons})

def splitter(partialList):
    return [[obj["attributes"] for obj in partialList],[obj["polygons"] for obj in partialList]]