import csv
import glob

filteredLogs = []
fieldNames = ["msg_id", "event", "address", "mode", "phase", "type", "msg_size", "time"]
logFileNames = glob.glob("logs/*.csv")
acceptedTypes = ["auth_search_begin", "auth_search_end", "unauth_search_begin", "unauth_search_end", 
"partial_sign_transcript_begin", "partial_sign_transcript_end", "verify_last_proof_begin", "verify_last_proof_end",
"verify_begin", "verify_end", "sign_begin", "sign_end", "partial_sign_begin", "partial_sign_end", "reconstruct_begin", "reconstruct_end",
"auth_route_search_begin", "auth_route_search_end", "search_path_length"]

i = 0
for logFileName in logFileNames:
    print("Filtering log file " + str(i+1) + '/' + str(len(logFileNames)))
    with open(logFileName) as file:
        reader = csv.DictReader(file, delimiter=';')
        for row in reader:
            if row["type"] in acceptedTypes:
                filteredLogs.append(row)
    i += 1

print("Sorting " + str(len(filteredLogs)) + " entries by time...")
filteredLogs = sorted(filteredLogs, key=lambda x: int(x["time"]))

print("Merging...")
with open("filtered_logs.csv", "w", newline="") as file:
    writer = csv.DictWriter(file, fieldNames)
    writer.writeheader()
    writer.writerows(filteredLogs)
