import pandas as pd
import joblib
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score

def train_model():
    # Load the new 2000-row dataset
    df = pd.read_csv('student_data.csv')
    
    # IMPORTANT: Drop ID columns and Teacher_ID before training
    X = df.drop(['Dropout_Risk', 'Student_ID', 'Teacher_ID'], axis=1) 
    y = df['Dropout_Risk']
    
    # 80/20 Split
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    # Initialize Random Forest with the judges' suggested depth
    model = RandomForestClassifier(n_estimators=100, max_depth=5, random_state=42)
    model.fit(X_train, y_train)
    
    # Validate
        # Validate
    train_predictions = model.predict(X_train)
    test_predictions = model.predict(X_test)
    
    train_acc = accuracy_score(y_train, train_predictions)
    test_acc = accuracy_score(y_test, test_predictions)
    
    # Save the updated "Brain"
    joblib.dump(model, 'model.pkl')
    
    print(f"--- Training Results ---")
    print(f"Training Accuracy: {train_acc:.4f}")
    print(f"Testing Accuracy:  {test_acc:.4f}")
    print(f"Gap: {(train_acc - test_acc):.4f}")

    
    # Save the updated "Brain"
    joblib.dump(model, 'model.pkl')

if __name__ == "__main__":
    train_model()