from flask import Flask, request, jsonify
import joblib
import numpy as np

app = Flask(__name__)
model = joblib.load('model.pkl')

@app.route('/predict', methods=['POST'])
def predict():
    try:
        data = request.get_json()
        
        # Extract features in the EXACT order they were trained
        features = [
            data['Attendance'],
            data['Math_Score'],
            data['Parent_Income'],
            data['Has_Internet'],
            data['Parent_Education']
        ]
        
        prediction = model.predict([features])
        # Return 1 for High Risk, 0 for Low Risk
        return jsonify({'risk_level': int(prediction[0])})
    
    except Exception as e:
        return jsonify({'error': str(e)}), 400

if __name__ == "__main__":
    app.run(port=5000, debug=True)