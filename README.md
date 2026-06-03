# 🌸 Student Reflection AI — Backend

> A secure Spring Boot REST API powering the Student Reflection AI platform — an AI-powered wellness platform for students with real-time sentiment analysis, emotion classification, and personalized Google Gemini AI insights.

[![Java](https://img.shields.io/badge/Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![Spring Boot](https://img.shields.io/badge/Spring_Boot-6DB33F?style=for-the-badge&logo=spring&logoColor=white)](https://spring.io/projects/spring-boot)
[![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=for-the-badge&logo=mysql&logoColor=white)](https://www.mysql.com/)
[![Gemini AI](https://img.shields.io/badge/Google_Gemini-4285F4?style=for-the-badge&logo=google&logoColor=white)](https://aistudio.google.com/)
[![JWT](https://img.shields.io/badge/JWT-000000?style=for-the-badge&logo=jsonwebtokens&logoColor=white)](https://jwt.io/)

---

## ✨ Overview

This is the backend REST API for Student Reflection AI — a full-stack wellness platform that helps students track their mental health, emotions, and academic performance using real AI.

The backend handles secure JWT authentication, stores reflection data in MySQL, and orchestrates Google Gemini 2.5 AI calls to generate personalized emotional insights, sentiment analysis, burnout warnings, and study suggestions — all in a single optimized API call.

---

## 🎯 Features

### 🔐 Authentication & Security
- JWT token-based authentication with Spring Security
- BCrypt password encoding
- Stateless session management
- Protected endpoints — all reflection data is user-specific
- CORS configured for frontend integration

### 📝 Reflection Management
- Save daily reflections with mood and productivity data
- Retrieve all reflections per user ordered by date
- Per-user data isolation — users only see their own entries

### 🧠 Google Gemini 2.5 AI Integration
- Single optimized Gemini API call per reflection — no rate limit issues
- Personalized empathetic AI insight generation
- Real-time sentiment analysis — Positive / Neutral / Negative
- Emotion classification — Joy, Calm, Sadness, Fear, Anger, Stress
- Stress level detection — Low / Medium / High
- Sinhala/English language detection using Unicode character ranges
- Bilingual AI responses when Sinhala is detected

### 🤖 AI Insights Engine
- 🔥 Burnout warning — analyzes patterns across last 7 entries
- 💡 Personalized study suggestions — tailored to reflection history
- 🌟 Weekly motivational summary — AI-written encouragement
- 📈 Productivity insight — weekly performance analysis
- 🔮 Study pattern prediction — predicts future stress proactively

---

## 🛠️ Tech Stack

| Technology | Purpose |
|---|---|
| Java 17 | Core programming language |
| Spring Boot 4 | Application framework |
| Spring Security 7 | Authentication and authorization |
| JWT (jjwt 0.12.6) | Token-based auth |
| Spring Data JPA | Database ORM layer |
| Hibernate 7 | JPA implementation |
| MySQL 8 | Relational database |
| Spring WebFlux | Non-blocking HTTP client for Gemini API |
| Maven | Dependency management |

---

## 📂 Project Structure

```plaintext
src/main/java/com/pubudini/studentreflection/
│
├── StudentreflectionApplication.java   # Main entry point
│
├── config/
│   └── SecurityConfig.java             # JWT + CORS security config
│
├── controller/
│   ├── AuthController.java             # POST /api/auth/register, /login
│   ├── ReflectionController.java       # GET/POST /api/reflections
│   └── InsightController.java          # GET /api/insights/*
│
├── dto/
│   ├── RegisterRequest.java            # Registration request body
│   ├── LoginRequest.java               # Login request body
│   └── ReflectionRequest.java          # Reflection save request body
│
├── entity/
│   ├── User.java                       # User entity
│   └── Reflection.java                 # Reflection entity with AI fields
│
├── repository/
│   ├── UserRepository.java             # User data access
│   └── ReflectionRepository.java       # Reflection data access
│
├── security/
│   ├── JwtUtil.java                    # JWT generation and validation
│   ├── JwtAuthFilter.java              # JWT request filter
│   └── CustomUserDetailsService.java   # Spring Security user loader
│
└── service/
    ├── UserService.java                # Auth business logic
    ├── ReflectionService.java          # Reflection + AI orchestration
    ├── ClaudeService.java              # Gemini API integration
    └── InsightService.java             # AI insights engine
```

---

## 🗄️ Database Schema

```plaintext
users
├── id (PRIMARY KEY)
├── email (UNIQUE)
└── password (BCrypt encoded)

reflections
├── id (PRIMARY KEY)
├── user_id (FOREIGN KEY → users)
├── reflection (TEXT)
├── mood
├── productivity
├── ai_insight (TEXT)
├── sentiment
├── emotion
├── stress_level
├── detected_language
└── created_at
```

---

## 🚀 Getting Started

### Prerequisites
- Java 17 or higher
- MySQL 8.0
- Maven
- Google Gemini API key (free at [aistudio.google.com](https://aistudio.google.com/apikey))

### 1. Clone the repository
```bash
git clone https://github.com/pubudini-rathnayake/student-reflection-ai-backend.git
```

### 2. Navigate to the project folder
```bash
cd student-reflection-ai-backend/studentreflection
```

### 3. Create the database
```sql
CREATE DATABASE IF NOT EXISTS student_reflection_db;
```

### 4. Set up application.properties
Copy the example config file:
```bash
cp src/main/resources/application.properties.example src/main/resources/application.properties
```

Then fill in your values:
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/student_reflection_db
spring.datasource.username=your_mysql_username
spring.datasource.password=your_mysql_password

jwt.secret=your_long_random_jwt_secret_here

gemini.api.key=your_gemini_api_key_here
```

### 5. Run the application
```bash
./mvnw spring-boot:run
```

### 6. Backend is running at
http://localhost:8080

> ⚠️ Never commit your real `application.properties` — it is already in `.gitignore` for security.

---

## 🔌 API Endpoints

### Auth
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/auth/register` | Register new user | ❌ |
| POST | `/api/auth/login` | Login and get JWT token | ❌ |

### Reflections
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| POST | `/api/reflections` | Save reflection + trigger AI | ✅ |
| GET | `/api/reflections` | Get all user reflections | ✅ |

### AI Insights
| Method | Endpoint | Description | Auth Required |
|---|---|---|---|
| GET | `/api/insights/burnout` | Get burnout warning | ✅ |
| GET | `/api/insights/suggestions` | Get study suggestions | ✅ |
| GET | `/api/insights/weekly-summary` | Get weekly summary | ✅ |
| GET | `/api/insights/productivity` | Get productivity insight | ✅ |
| GET | `/api/insights/prediction` | Get study prediction | ✅ |

---

## 🔗 Frontend Repository

This backend powers a React frontend. See the frontend repo for UI setup:

[![Frontend Repo](https://img.shields.io/badge/Frontend_Repository-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/pubudini-rathnayake/student-reflection-ai-frontend)

---

## 👩‍💻 Author

**Pubudini Rathnayake**
ICT Undergraduate | Specializing in Artificial Intelligence | Full-Stack Developer

[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/pubudini-rathnayake-388b062b7)
[![GitHub](https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white)](https://github.com/pubudini-rathnayake)

---

## 📜 License

This project is licensed under the MIT License.
