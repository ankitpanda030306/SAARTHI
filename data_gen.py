import pandas as pd
import numpy as np

def create_dataset(num_students=2000, num_teachers=250):
    np.random.seed(42)
    
    # --- 1. Generate Teacher Records ---
    teacher_ids = [f"TCH_{100 + i}" for i in range(num_teachers)]
    teachers_df = pd.DataFrame({
        'Teacher_ID': teacher_ids,
        'Experience_Years': np.random.randint(1, 30, num_teachers),
        'Workload_Score': np.random.randint(1, 10, num_teachers),
        'Department': np.random.choice(['Math', 'Science', 'Arts', 'Languages'], num_teachers)
    })
    
    # --- 2. Generate Student Records ---
    assigned_teachers = np.random.choice(teacher_ids, size=num_students)
    attendance = np.random.normal(70, 15, num_students)
    attendance = np.clip(attendance, 0, 100)
    
    math_score = (attendance * 0.6) + np.random.normal(20, 10, num_students)
    math_score = np.clip(math_score, 0, 100)  
    
    parent_income = np.random.exponential(15000, num_students) + 2000
    has_internet = np.random.choice([0, 1], size=num_students, p=[0.7, 0.3])
    parent_education = np.random.choice([0, 1, 2], size=num_students, p=[0.5, 0.3, 0.2]) 
    
    # Risk Logic (The "Why")
    term1 = (100 - attendance) * 0.5
    term2 = (100 - math_score) * 0.3
    term3 = np.where(parent_income < 5000, 20, 0)
    term4 = np.where(has_internet == 0, 10, 0)   
    
    risk_factors = term1 + term2 + term3 + term4             
    dropout_target = np.where(risk_factors > 50, 1, 0)
    
    students_df = pd.DataFrame({
        'Student_ID': [f"STU_{1000 + i}" for i in range(num_students)],
        'Attendance': attendance.round(2),
        'Math_Score': math_score.round(2),
        'Parent_Income': parent_income.round(2),
        'Has_Internet': has_internet,
        'Parent_Education': parent_education,
        'Teacher_ID': assigned_teachers,
        'Dropout_Risk': dropout_target
    })
    
    return students_df, teachers_df

if __name__ == "__main__":
    df_students, df_teachers = create_dataset()
    df_students.to_csv('student_data.csv', index=False)
    df_teachers.to_csv('teacher_data.csv', index=False)
    print(f"✅ Generated 2000 Students and 250 Teachers.")