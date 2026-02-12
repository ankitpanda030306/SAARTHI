import pandas as pd
import pickle
from sklearn.model_selection import train_test_split
from sklearn.ensemble import RandomForestClassifier
from sklearn.metrics import accuracy_score

def train():
    df = pd.read_csv('student_data.csv')
    
    X = df.drop('Dropout_Risk', axis=1)
    y = df['Dropout_Risk']
    
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.2, random_state=42)
    
    clf = RandomForestClassifier(n_estimators=100, max_depth=10, random_state=42)
    clf.fit(X_train, y_train)
    
    preds = clf.predict(X_test)
    acc = accuracy_score(y_test, preds)
    print(f"Model trained. Accuracy: {acc:.4f}")
    
    with open('model.pkl', 'wb') as f:
        pickle.dump(clf, f)

if __name__ == "__main__":
    train()