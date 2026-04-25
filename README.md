# plurb

> Reputation-weighted community platform where your voice is earned.

plurb is a self-hosted monolithic community platform built with Spring Boot. Phase 1 (Panorama) ships a per-user markdown blog. Future phases add reputation-weighted discussion, the Been system, and more.

**Live:** [plurb.org](https://plurb.org) — currently running Panorama (blog only)

---

## Stack

- **Backend:** Java 21, Spring Boot 4, Spring Security, Spring Data JPA
- **Database:** PostgreSQL 18, Flyway
- **Frontend:** Thymeleaf SSR, highlight.js
- **Infra:** nginx, systemd, RackNerd VPS

---

## Modules

### Panorama (v0.1 — current)
Per-user markdown blog with:
- Public profile page with tabbed About / Posts view
- Markdown rendering with code syntax highlighting
- Post tagging and series support (series management UI coming in v0.2)
- Cover image and post description
- Editor dashboard — create, edit, publish, delete posts
- About page editable in markdown
- Password change
- Multi-tenant schema (single author for now, registration blocked by design)

### Planned
- **Forum** — reputation-weighted discussion threads
- **Been** — contribution-based reputation system
- **Auth** — user registration with invite or open depending on policy

---

## Getting Started

### Prerequisites
- Java 21
- PostgreSQL
- Gradle

### Local Setup

**1. Create the database:**
```bash
sudo -u postgres psql
CREATE DATABASE plurb;
CREATE USER plurb_user WITH PASSWORD 'yourpassword';
GRANT ALL PRIVILEGES ON DATABASE plurb TO plurb_user;
GRANT ALL ON SCHEMA public TO plurb_user;
\q
```

**2. Set environment variables:**
```bash
export DB_USERNAME=plurb_user
export DB_PASSWORD=yourpassword
```

**3. Run:**
```bash
./gradlew bootRun
```

Flyway will apply migrations automatically. A seed user is created on first run — change the default password immediately in `DataInitializer.java` before running.

**4. Open:**
- `http://localhost:8080/` — index
- `http://localhost:8080/login` — login

---

## Deployment

Built as a single jar, deployed behind nginx on a VPS with systemd.

```bash
./gradlew bootJar
scp build/libs/plurb-*.jar user@yourserver:/opt/plurb/plurb.jar
sudo systemctl restart plurb
```

See deploy notes in the project wiki for full nginx and systemd config.

---

## License

- **Source code:** [AGPL-3.0](https://www.gnu.org/licenses/agpl-3.0.html)
- **Content:** [CC BY 4.0](https://creativecommons.org/licenses/by/4.0/)

---

*Made by [Benny Chen](https://github.com/chen-benny)*
