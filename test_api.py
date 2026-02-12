import requests
import json
url = 'http://127.0.0.1:5000/predict'
payload = {
    "Attendance": 35.0,        
    "Math_Score": 30.0,        
    "Parent_Income": 4000,     
    "Has_Internet": 0,         
    "Parent_Education": 0      
}
try:
    response = requests.post(url, json=payload)
    print("Server Response:\n", json.dumps(response.json(), indent=2))
except Exception as e:
    print("Error:", e)