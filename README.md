# KCAU Online Voting System 🗳️

## 📌 Project Overview

The **KCAU Online Voting System** is a mobile-first Android application developed to digitize the student electoral process at KCA University.

The current student election process relies on a delegate-based and physically conducted voting model, which:
- Limits direct student participation
- Disrupts academic activities
- Reduces transparency
- Results in low voter turnout

This project introduces a secure, transparent, and accessible **online voting platform** that enables students to vote directly for SAKU leadership from any location.

---

## 🎯 Objectives

- Enable direct student participation in elections
- Increase voter turnout
- Improve transparency in election processes
- Eliminate academic disruption during voting
- Ensure secure authentication and vote integrity
- Provide real-time result computation

---

## 🏗️ System Architecture

The system follows a **Client–Server Architecture**:

- **Client Application:** Android (Java)
- **Backend Services:** Firebase Authentication + Cloud Firestore
- **Cloud Hosting:** Firebase
- **Security Layer:** HTTPS (TLS) + Firebase Security Rules

---

## 🛠️ Technologies Used

| Category | Technology | Purpose |
|-----------|------------|----------|
| Programming Language | Java | Android app development |
| IDE | Android Studio | Development & debugging |
| Database | Firebase Firestore | Real-time cloud storage |
| Authentication | Firebase Authentication | Secure login |
| Hosting | Firebase Hosting | Deployment |
| UI Design | Figma / Canva | Interface design |
| Testing | Android Emulator / Physical Devices | System testing |

---

## 🔐 Core Functional Modules

### 1️⃣ User Authentication Module
- Login using Registration Number and Password
- Firebase credential verification
- Role-based access control (Voter/Admin)
- Account lock after multiple failed login attempts

---

### 2️⃣ Election Management Module (Admin)
- Create and configure elections
- Define positions
- Register candidates
- Set election timelines
- Publish active elections

---

### 3️⃣ Voting Module
- Display available elections
- Allow candidate selection per position
- Enforce one-vote-per-user rule
- Store votes anonymously
- Provide vote confirmation message

---

### 4️⃣ Result Computation Module
- Real-time vote aggregation
- Automatic updates via Firestore listeners
- Display percentages and turnout statistics
- Restricted access until official release

---

### 5️⃣ System Administration Module
- Manage user roles
- Monitor activity logs
- Approve candidates
- Publish final results
- Maintain audit trails

---

## 📥 System Inputs

- Student registration credentials
- Election metadata (title, dates, positions)
- Candidate details (name, photo, manifesto)

---

## 📤 System Outputs

- Vote confirmation message
- Live election progress updates
- Final election results
- Participation reports

---

## 📱 User Interface Overview

### 🔑 Login Screen
- Registration number field
- Password field
- Login button
- Password recovery option

### 🏠 Voter Dashboard
- Active elections list
- View candidates option
- Vote now option
- Logout feature

### 🗳️ Voting Screen
- Position title display
- Candidate selection (radio buttons)
- Submit vote button
- Confirmation dialog

### 📊 Results Screen
- Visual vote representation (bar graph)
- Vote percentages
- Total votes and turnout statistics

---

## ⚠️ System Limitations

- Requires internet connectivity
- Android-only in initial release
- No advanced end-to-end cryptographic verification (Phase 1)
- Dependent on Firebase service availability

---

## 🚀 Development Methodology

The system follows the **Incremental Development Model**:

1. Requirement Analysis  
2. System Design  
3. Module Development  
4. Integration & Testing  
5. Deployment  
6. Documentation  

---

## 📅 Project Milestones

| Phase | Description |
|--------|------------|
| Requirement Analysis | Define system requirements |
| System Design | Architecture & UI design |
| Implementation | Develop modules |
| Testing | Validate functionality & security |
| Deployment | Host on Firebase |
| Documentation | Final report & user manual |

---

## 🔒 Security & Privacy Considerations

- HTTPS encrypted communication
- Secure authentication via Firebase
- Anonymous vote storage
- Role-based access control
- Activity logging for transparency

---

## 📌 Future Improvements

- iOS and Web support
- Single Sign-On (SSO) integration
- Advanced cryptographic vote verification
- Biometric authentication
- Offline caching improvements

---

## 📄 License

This project is developed for academic purposes as part of a Bachelor of Science in Information Technology final year project.
