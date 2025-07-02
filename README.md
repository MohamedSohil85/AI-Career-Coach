# 🧠 AI Career Coach App

An AI-powered Career Coach application built with **Java**, **Spring Boot**, **PostgreSQL**, **LangChain**, and **LLaMA**, containerized using **Docker**. This app helps job seekers optimize their resumes, generate cover letters, and receive personalized career advice powered by large language models.

---

## ✨ Features

- ✅ **ATS-Friendly Resume Check**  
  Analyze and optimize resumes to ensure they meet Applicant Tracking System (ATS) standards.

- 📄 **Cover Letter Generator**  
  Automatically generate a professional cover letter using:
    - Job Title
    - Job Description
    - Resume Content

- 🧭 **Career Advisor**  
  Get AI-driven career insights and recommendations based on your resume and job goals.

- 🎤 **Mock Interview Simulator**  
  Practice interviews with AI-generated questions and evaluate answers based on the target job description and resume content.

---

## 🛠️ Tech Stack

- **Backend:** Java, Spring Boot
- **AI & NLP:** LLaMA (via LangChain)
  -**LLM Interface:** LangChain + Custom Prompts
- **Database:** PostgreSQL
- **Containerization:** Docker
- **APIs:** RESTful services

---

## 🚀 Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose
- PostgreSQL (if not using Docker for DB)
- LLaMA model setup (locally or via API)

### Installation

```bash
# Clone the repository
git clone https://github.com/MohamedSohil85/AI-Career-Coach.git
cd ai-career-coach

# Start application
docker-compose up --build
