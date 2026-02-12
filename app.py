from flask import Flask, request, jsonify
import pickle
import numpy as np

app = Flask(__name__)

model = None
with open('model.pkl', 'rb') as f:
    model = pickle.load(f)

@app.route('/predict', methods=['POST'])
def predict_dropout():
    try:
        data = request.get_json()
        
        features = np.array([[
            data.get('Attendance'),
            data.get('Math_Score'),
            data.get('Parent_Income'),
            data.get('Has_Internet'),
            data.get('Parent_Education')
        ]])
        
        prediction = model.predict(features)[0]
        probability = model.predict_proba(features)[0][1]
        
        risk_level = 'Low'
        if probability > 0.7:
            risk_level = 'High'
        elif probability > 0.4:
            risk_level = 'Medium'

        return jsonify({
            'status': 'success',
            'prediction': int(prediction),
            'probability': float(probability),
            'risk_level': risk_level
        })

    except Exception as e:
        return jsonify({'status': 'error', 'message': str(e)}), 400

if __name__ == '__main__':
    app.run(host='0.0.0.0', port=5000)