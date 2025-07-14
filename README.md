# Headless CMS 📰

A modern Spring Boot–based Headless CMS supporting role-based access control, subscription-restricted content, JWT/OAuth2 authentication, and media management using MinIO. Designed with a Dockerized infrastructure for rapid development and deployment.

---

## 🚀 Features

- **Post Management**
    - Create, read, update, and delete posts.
    - Each post can have multiple associated media files (images, videos, etc.).

- **Media Handling**
    - Upload and store media files using MinIO (S3-compatible object storage).
    - Link multiple media items to a single post.

- **Authentication & Authorization**
    - Secure endpoints with JWT (JSON Web Token) and OAuth2 login.
    - Two roles: `ROLE_USER` and `ROLE_ADMIN`.
    - Admins can manage content, users, and media.
    - Users can view public and subscribed content.

- **Subscription Access**
    - Posts can be marked as subscription-only.
    - Only users with a valid subscription can access restricted content.

- **Dockerized Infrastructure**
    - Uses Docker Compose to orchestrate:
        - Spring Boot app
        - PostgreSQL (database)
        - Redis (caching)
        - MinIO (media storage)

---

## 🧑‍💻 Getting Started

### Prerequisites

- Java 17+
- Docker & Docker Compose

### Clone & Run

```bash
git clone https://github.com/PouryaT/headless-cms.git
cd headless-cms
docker-compose up --build
