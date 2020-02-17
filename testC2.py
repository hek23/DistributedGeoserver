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
import matplotlib.colors as mcolors

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

def query(numTests, numUsers):
    accum = []
    while numTests>0:
        accum.append(intersection())
        numTests = numTests - 1
    return mean(accum)

def generateTestGraph(numUsers,numTests):
    with concurrent.futures.ThreadPoolExecutor(max_workers=numUsers) as executor:
        results = [executor.submit(query, numTests, numUsers) for i in range(numUsers)]
        suby =[]
        for f in concurrent.futures.as_completed(results):
            r= f.result()
            suby.append(r)
        if(len(suby)==1):
            suby.append(r)
        return mean(suby)

def graphNusers(maxTestQty, maxUsersQty, jumpQueries,initialQuery):
    actualQueryQty = initialQuery #Quantity of Queries PER USER
    actualUserQty = 1 #Quantity of concurrent Users
    log = open("log.log", "w")
    x = np.arange(initialQuery,maxTestQty+1,jumpQueries,dtype=int)
    #X is number of tests 
    #Y is time
    #Z is concurrent Users at the time
    #Is need to generate all Y with X, at a given Z (each Z will generate a graph)
    times = np.empty(0)
    vari = np.empty(0)
    colors = mcolors.CSS4_COLORS
    print("Max. Queries per user: {}".format(maxTestQty))
    log.write("Max. Queries per user: {}".format(maxTestQty)+'\n')
    print("Max. Users concurrent: {}".format(maxUsersQty))
    log.write("Max. Users concurrent: {}".format(maxUsersQty)+'\n')
    print("Jump Q factor: {}".format(jumpQueries))
    log.write("Jump Q factor: {}".format(jumpQueries)+'\n')
    print("Initial Queries: {}".format(initialQuery))
    log.write("Initial Queries: {}".format(initialQuery)+'\n')
    while(actualUserQty<=maxUsersQty): 
        print("Quering {} Users".format(actualUserQty))
        log.write("Quering {} Users".format(actualUserQty)+'\n')
        y = np.empty(0,dtype=float)
        while(actualQueryQty<=maxTestQty):
            result = generateTestGraph(actualUserQty,actualQueryQty)
            y= np.append(y,result[0])
            print("{} Users, {} queries, Mean: {}".format(actualUserQty, actualQueryQty, result))
            log.write("{} Users, {} queries, Mean: {}".format(actualUserQty, actualQueryQty, result)+'\n')
            actualQueryQty = actualQueryQty + jumpQueries
        #Save Y
        times=np.append(times,y)
        actualQueryQty = initialQuery
        style = '^C{}:'.format(actualUserQty%10)
        #plt.scatter(x, y, label='data')
        plt.plot(x, y, style, label="{} users".format(actualUserQty))
        plt.xlabel('Queries')
        plt.ylabel('Time [s]')
        #plt.title('Fitting primes')
        actualUserQty = actualUserQty + 1
    plt.legend()
    np.save("times",times)
    np.save("queries", x)
    np.save("users", np.arange(1,maxUsersQty+1))
    plt.savefig('Graph.png')
    log.close()
    return 0

def graphXUsers(maxUsers,numTests):
    x = np.arange(1,maxUsers+1,1,dtype=int)
    y = np.empty(0,dtype=float)
    for i in x:
        print(str(i) + " users\n")
        y = np.append(y,generateTestGraph(i,numTests))
    np.savetxt("graphY.gz", y, delimiter=',')
    np.savetxt("graphX.gz", x, delimiter=',')
    plt.plot(x, y, '^C2:')
    plt.xlabel('Users')
    plt.ylabel('Time [s]')
    return 0


if __name__ == "__main__":
    maxTestQty = int(argv[2])
    maxUsersQty = int(argv[3])
    if(len(argv)>4):
        jumpQueries = int(argv[4])
        initialQuery = int(argv[5])
    #graphNusers(maxTestQty, maxUsersQty, jumpQueries,initialQuery)
    graphXUsers(maxUsersQty,maxTestQty)
    plt.show()    




