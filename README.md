# 🎓 SAARTHI - AI-Powered Dropout Prevention System

> **"A stitch in time saves nine."** > SAARTHI is a smart monitoring tool designed to predict, prevent, and intervene in student dropout cases before it's too late.

![Java](https://img.shields.io/badge/Java-17-orange) ![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green) ![Vaadin](https://img.shields.io/badge/Frontend-Vaadin_Flow-blue) ![Status](https://img.shields.io/badge/Status-Hackathon_Ready-red)

---

## 🚀 The Problem
In India, millions of students drop out of school due to **financial constraints, academic pressure, and lack of awareness** about government support. Teachers often realize this too late, when the student has already stopped attending.

## 💡 The Solution
**SAARTHI** (meaning "Charioteer" or "Guide") is a comprehensive dashboard for Principals and Teachers that uses **Smart Logic** to identify "At-Risk" students early. It doesn't just flag problems—it suggests solutions.

### 🌟 Key Features

#### 🏫 For Principals (Admin View)
* **Risk Meter:** Visual analytics showing exactly how many students are at Critical, High, or Medium risk.
* **Teacher Monitoring:** Track teacher activity and last login times to ensure accountability.
* **Intervention Log:** A centralized hub to review complaints sent by teachers and mark them as "Resolved" once action is taken.
* **Govt Schemes Library:** A built-in database of government scholarships (e.g., Pre-Matric, PM YASASWI) to help financially weak students.

#### 👩‍🏫 For Teachers (Class View)
* **Smart Risk Analysis:** Automatically calculates risk based on Marks (Periodic/Half Yearly/Annual), Attendance, and Stress Score.
* **Critical Alerts:** Automatically flags students who fail both **Half Yearly & Annual** exams as "Critical".
* **Actionable Interventions:**
    * 📲 **SMS Alert:** Send instant pre-configured alerts to parents.
    * 📅 **Counseling:** Schedule counselor meetings directly from the dashboard.
    * 🎓 **Scheme Recommendation:** Auto-suggests scholarships based on the student's gender and family income.

---

## 🛠️ Tech Stack
* **Backend:** Java 17, Spring Boot
* **Frontend:** Vaadin Flow (Java-based UI)
* **Database:** H2 In-Memory (for easy demo setup) / PostgreSQL (Production)
* **Deployment:** Docker & Render.com
* **Tools:** Maven, Git

---

## ☁️ Deployment (Docker)
This project includes a `Dockerfile` optimized for **Render.com**.

1.  Push code to GitHub.
2.  Connect repository to Render.
3.  Select **Docker Runtime**.
4.  Deploy! 🚀

---

## 🔮 Future Roadmap
* [ ] Integration with WhatsApp API for real-time parent notifications.
* [ ] AI Model integration to predict "Hidden Dropouts" using behavioral patterns.
* [ ] Regional Language Support (Hindi, Odia, etc.) for rural accessibility.

---

Made with ❤️ by **Ankit Kumar Panda & Team** for **TRITHON 2026**