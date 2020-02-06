#from locust import HttpLocust, TaskSet, task
import json
import random
import requests
import time
from sys import argv
from statistics import mean, stdev 
import concurrent.futures
import numpy as np
import matplotlib.pyplot as plt

geomType = {
    0: "Point",
    1: "LineString",
    2: "MultiLineString",
    3: "Polygon",
    4: "Circle"
}

def newParam():
    polygonCase21 = {
    "layers":[
        #"visor_upload.potencial_hidroelectrico", 
        "visor_upload.sic"
        #"visor_upload.gis_osm_roads_free_1", "visor_upload.gis_osm_buildings_a_free_1"
    ],
    "geometry":
    {
            "type": "Circle",
            "coordinates": [[random.uniform(-8472891.711355217, -7445578.051202448), random.uniform(-7523849.568166468, -1917652.1656185004)]]
            #"coordinates": [[-7920554.006431963, -3851648.4899293995]]  
        },
    "radius":random.uniform(30000,4000000) #Meters (?) At least 3857 says that
        }
    return polygonCase21

def intersection():
    #Generating object. For that generate

    headers = {'content-type': 'application/json'}
    #Start
    startTime = time.time()
    r = requests.post(argv[1]+"/geoprocessing/intersection",
    data= json.dumps(newParam()), 
    headers=headers)
    finishTime = time.time()
    #print(r.json())
    return finishTime - startTime

def query(numTests, id):
    accum = []
    while numTests>0:
        accum.append(intersection())
        numTests = numTests - 1
    #print("Average Time Thread "+ str(threading.get_ident())+ " "+ str(mean(accum)))
    #print("Std Deviation: " + str(threading.get_ident()) +" "+ str(stdev(accum)))
    #return (id, mean(accum), stdev(accum))
    time.sleep(random.uniform(0,5))
    return (id, accum)

def generateTestGraph(numUsers,numTests):
    with concurrent.futures.ThreadPoolExecutor(max_workers=numUsers) as executor:
        results = [executor.submit(query, numTests, i) for i in range(numUsers)]
        suby =[]
        #Inside With: Calls when thread finishes.
        #Outside With: Wait for all threads, then execute
        for f in concurrent.futures.as_completed(results):
            r= f.result()
            suby.extend(r[1])
            print("Average Time Thread "+ str(r[0])+": "+ str(mean(r[1]))+" \n"+ "CV: " + str(stdev(r[1])/mean(r[1])))
        #Now, add to general data set as a group
        #Each graph must have X and Y, with data correlated by index
        #For X Axis, we need to specify that is numUsers users asociated to certain time
        subx = np.full(len(suby), numUsers) #numUsers * numTests
        #print(subx)
        #For Y Axis, is suby (already created)
        #For matplot these must be numpy arrays
        suby = np.array(suby)
        return subx, suby


if __name__ == "__main__":
    maxTestQty = int(argv[2])
    maxUsersQty = int(argv[3])
    actualQueryQty = 10 #Quantity of Queries PER USER
    actualUserQty = 1 #Quantity of concurrent Users
    #Test type(?)
    x = np.empty(0,dtype=int)
    y = np.empty(0,dtype=int)
    color = np.empty(0,dtype=int)
    while (actualQueryQty<=maxTestQty): #Iterate through queryQty, creating layers of info
        while(actualUserQty<=maxUsersQty): #Iterate through UserQty, creating "data"
            print("MaxUsersQ: "+ str(actualUserQty) + " TestsQ: "+str(actualQueryQty))
            newx, newy = generateTestGraph(actualUserQty,actualQueryQty)
            #print(newx)
            x= np.concatenate((x,newx))
            y= np.concatenate((y,newy))
            actualUserQty = actualUserQty + 1
        #As these correspond to one particular query type (shape)
        #And query quantity (color), we set the variables to be applied using the length
        subcolor = np.full(int(actualQueryQty*((maxUsersQty*(maxUsersQty+1))/2)),actualQueryQty,dtype=int)
        #
        color = np.concatenate((color,subcolor))
        actualUserQty = 1 #Reset internal user iterator
        actualQueryQty = actualQueryQty + 10 #Iterator

    fig, ax = plt.subplots()
    scatter = ax.scatter(x, y, c=color, s=np.array(1))

    # produce a legend with the unique colors from the scatter
    legend1 = ax.legend(*scatter.legend_elements(),
                    loc="lower left", title="Classes")
    ax.add_artist(legend1)

    # produce a legend with a cross section of sizes from the scatter
    handles, labels = scatter.legend_elements(prop="sizes", alpha=0.6)
    legend2 = ax.legend(handles, labels, loc="upper right", title="Sizes")

    plt.show()
    

