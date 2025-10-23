<p align="center">
    <img src="https://atelierlocal-bucket1.s3.eu-west-3.amazonaws.com/logos/atelier-local-logo5_white.png" alt="Atelier Local logo">
</p>
<center>
<h1>Portfolio Project</h1>
<em>Atelier Local</em>
</center>

## 📖 Project Overview

Our projet "Atelier Local" is a web platform designed to connect communities with local artisans and small businesses in a more human, interactive, and collaborative way.
Its main objective is to make local know-how more accessible while offering users a simple, reliable, and engaging experience when searching for services.
This project is still in development.

## 🚀 Main features

By the end of this development phase, the functionnalities are:

- **Authentication system** with assigned roles: User, Artisan, Admin,
- **Post a request**: single need or multiple needs at once,
- **Lightweight current requests list** for artisans to browse,
- **Lightweight artisans list** to explore available artisans with filters,
- **Artisan response system** via a private messenger for each user,
- **Artisan profile page** including:
  - Bio / description,
  - Category,
  - Gallery of a few works,
  - “Contact me” button,
- **Simple admin panel** for content moderation,
- **Advanced design** for a modern and intuitive UI.

## 🧩 The next steps

+ Improve front (icons for categories, footer's pages, ...)
+ Client's page,
+ Mailing system,
+ Mapping system,
+ Recommendation system,
+ Collaborative portfolio (improvement of Artisans's gallery),
+ Commercial system and deployment,

## 🔧 Tech Stack

- **Frontend**: React 19, Next.js 15, Tailwind CSS 3,
- **Backend**: Java 21 - Springboot 3,
- **Database**: PostgreSQL 16,
- **External API**: LocationIQ,
- **Other Tools**: Framer Motion for animations, CloudFront / AWS S3 for image hosting, Docker.

## 🛠 How to Run

1️⃣ Clone the repository:

```bash
git clone https://github.com/Proser-V/portfolio-project.git
```

2️⃣ Run locally with Docker:

1. Make sure Docker is installed on your system  
   - [Download Docker Desktop](https://www.docker.com/products/docker-desktop/) (Windows / macOS)  
   - or install Docker Engine on Linux (`sudo apt install docker docker-compose`)

2. Start Docker (Docker Desktop or Docker daemon)

3. In the project root, build and run all containers:
```bash
docker-compose up --build
```

4️⃣ Access to the app:

[Home page](http://localhost:3000/)

The backend API is also available for testing at:
[Swagger](http://localhost:8080/swagger-ui/index.html)

## 📄 License

Private use - All rigths reserved.

## 🤝 Authors

+ Quentin Lataste : [github.com/loufi84](https://github.com/loufi84)
+ Valentin Dumont : [github.com/Proser-V](https://github.com/Proser-V)
