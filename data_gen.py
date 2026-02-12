import pandas as pd
import numpy as np

def create_dataset(num_samples=10000):
    np.random.seed(42)
    attendance = np.random.normal(70, 15, num_samples)
    attendance = np.clip(attendance, 0, 100)
    math_score = (attendance * 0.6) + np.random.normal(20, 10, num_samples)
    math_score = np.clip(math_score, 0, 100)  
    parent_income = np.random.exponential(15000, num_samples) + 2000
    has_internet = np.random.choice([0, 1], size=num_samples, p=[0.7, 0.3])
    parent_education = np.random.choice([0, 1, 2], size=num_samples, p=[0.5, 0.3, 0.2]) 
    term1 = (100 - attendance) * 0.5
    term2 = (100 - math_score) * 0.3
    term3 = np.where(parent_income < 5000, 20, 0) # Fixed line
    term4 = np.where(has_internet == 0, 10, 0)    # Fixed line  
    risk_factors = term1 + term2 + term3 + term4             
    dropout_target = np.where(risk_factors > 50, 1, 0)
    df = pd.DataFrame({
        'Attendance': attendance,
        'Math_Score': math_score,
        'Parent_Income': parent_income,
        'Has_Internet': has_internet,
        'Parent_Education': parent_education,
        'Dropout_Risk': dropout_target
    })
    return df
if __name__ == "__main__":
    df = create_dataset()
    df.to_csv('student_data.csv', index=False)
    print("✅ Dataset generated successfully: student_data.csv")