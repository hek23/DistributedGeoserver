from os import sched_getaffinity

from numpy import array, array_split
from flask import current_app
from concurrent.futures import as_completed
from shapely.geometry import shape, mapping

from helpers.parallelGenerator import getThreads
from helpers.formatter import formatShape

#Intersect two shapes or one and a list of them formatted.
#Is recursive
#If shape2 is empty list, returns
def intersectShapes(shapeList):
    if(len(shapeList) == 1):
        #Recursive finish
        return shapeList[0]
    #Before parallelize, order shapes, using polygon quantity
    #shapeList = [shape1,shape2]
    shapeList.sort(key = len)
    #The smallest shape will be used as a constant for comparison
    #splitting the other between threads
    smallShape = shapeList.pop(0)
    polygonList = array(shapeList.pop(0))
    num = min(len(sched_getaffinity(0)), current_app.config["THREADS"])

    polygonList = array_split(polygonList,num)

    #MULTITHREAD START
    executor = getThreads()
    processedLayers = []
    for polygonSet in polygonList:
        processedResult = executor.submit(intersect, polygonSet, smallShape)
        processedLayers.append(processedResult)
    #At processedLayers will be a list of objects like 
    # {attributes: [], polygons:[]} that need to be joined
    results = []
    for res in as_completed(processedLayers):
        results.extend(res.result())

    shapeList.append(results)
    return intersectShapes(shapeList)

#Shape1 and Shape2 are list of {attributes: [], polygons:[]} Return same format
def intersect(shape1, shape2):
    res = []
    #Each polygon in each shape
    for polygon1 in shape1:    
        for polygon2 in shape2:
            #Transform to object
            r1 = shape(polygon1['polygons'])
            r2 = shape(polygon2['polygons'])
            #Calculate intersection
            result = r1.intersection(r2)
            #If is not empty, add to result
            if(not(result.is_empty)):
                res.append({"attributes": {**polygon1['attributes'], **polygon2['attributes']}, "polygons": mapping(result)})
    return res      

def processIntersection(layersName, polygon):
    layerSet = []
    #Get layers info (uses Threads for paralell query)
    executor = getThreads(len(layersName))
    for layer in layersName:
        formattedLayer = executor.submit(formatShape, layer,polygon)
        layerSet.append(formattedLayer)
    results = []
    for res in as_completed(layerSet):
        results.append(res.result())
    #Call till layerSet is empty
    polygons = intersectShapes(results)
    return polygons
