import io
import csv
import glob

class Recorder:
    recordedParams = {}
    params = {}

    def createParameter(self, paramName):
        self.params[paramName] = []

    def createRecordedParameter(self, paramName):
        self.recordedParams[paramName] = {}

    def startRecording(self, paramName, id, value):
        if id in self.recordedParams[paramName]:
            return
        self.recordedParams[paramName][id] = (value, value)

    def finalizeRecording(self, paramName, id, value):
        if id not in self.recordedParams[paramName]:
            return
        initialValue = self.recordedParams[paramName][id][0]
        self.recordedParams[paramName][id] = (initialValue, value)
    
    def record(self, paramName, value):
        self.params[paramName].append(value)
    
    def getAverage(self, paramName):
        return sum(self.params[paramName])/len(self.params[paramName])
    
    def getRecordedAverage(self, paramName):
        deltas = sorted([(k, v[1] - v[0]) for k, v in self.recordedParams[paramName].items()], key=lambda x: x[1], reverse=True)
        ds = ()
        return sum([x[1] for x in deltas])/len(deltas)

recorder = Recorder()
recorder.createRecordedParameter("auth_delay")
recorder.createRecordedParameter("unauth_delay")
recorder.createRecordedParameter("route_delay")

def nextSequentialIndex(address, indexMap):
    if address not in indexMap:
        indexMap[address] = 0
        return 0
    indexMap[address] = indexMap[address] + 1
    return indexMap[address]

def lastSequentialIndex(address, indexMap):
    if address not in indexMap:
        return -1
    return indexMap[address]

with open("merged_logs.csv") as file:
    reader = csv.DictReader(file, delimiter=',')
    for row in reader:
        address = row["address"]
        time = float(row["time"])
        if row["type"] == "auth_search_begin":
            recorder.startRecording("auth_delay", address + row["msg_id"], time)
        elif row["type"] == "auth_search_end":
            recorder.finalizeRecording("auth_delay", address + row["msg_id"], time)
        elif row["type"] == "unauth_search_begin":
            recorder.startRecording("unauth_delay", address + row["msg_id"], time)
        elif row["type"] == "unauth_search_end":
            recorder.finalizeRecording("unauth_delay", address + row["msg_id"], time)
        elif row["type"] == "auth_route_search_begin":
            recorder.startRecording("route_delay", address + row["msg_id"], time)
        elif row["type"] == "auth_route_search_end":
            recorder.finalizeRecording("route_delay", address + row["msg_id"], time)

auth_delay = recorder.getRecordedAverage("auth_delay")
unauth_delay = recorder.getRecordedAverage("unauth_delay")
    
print("Avg auth delay: " + str(auth_delay))
print("Avg unauth delay: " + str(unauth_delay))
print("Factor of increase: " + str(auth_delay/unauth_delay))
print("Avg route delay: " + str(recorder.getRecordedAverage("route_delay")))
