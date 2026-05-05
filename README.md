# 🏠 Smart Home Backend System

Backend application for a **Smart Home Management System** designed to control, monitor, and analyze IoT devices in real time.

The system provides RESTful APIs for authentication, home and device management, MQTT-based IoT integration, telemetry processing, and energy consumption analytics.

---

## 🚀 Features

- 🔐 JWT-based authentication and authorization  
- 🏡 Home, room, and device management  
- ⚡ Real-time device control via REST API  
- 📡 MQTT integration with IoT devices (Shelly Plug)  
- 📊 Real-time telemetry processing  
- 💾 Energy consumption history storage  
- 📅 Daily, weekly, and monthly energy statistics  
- 💰 Energy cost estimation  
- 🚨 Energy anomaly detection with email notifications  
- 🧊 3D room layout data support  
- 🐳 Docker-based deployment  

---

## 🛠 Tech Stack

| Technology | Purpose |
|-----------|--------|
| Java 21 | Backend programming language |
| Spring Boot | Backend framework |
| Spring Security | Authentication & authorization |
| JWT | Stateless authentication |
| PostgreSQL | Database |
| Spring Data JPA | ORM & data persistence |
| Flyway | Database migrations |
| MQTT (Mosquitto) | IoT communication |
| Docker | Containerization |
| GitHub Actions | CI/CD |

---

## 📡 API Overview

| Module | Endpoint | Description |
|--------|---------|-------------|
| Auth | `/api/auth` | Login & registration |
| Dashboard | `/api/dashboard` | Summary statistics |
| Homes | `/api/homes` | Home management |
| Rooms | `/api/rooms` | Room management |
| Devices | `/api/devices` | Device CRUD operations |
| Device States | `/api/device-states` | Device control |
| Device Energy | `/api/device-energy` | Energy analytics |
| Device Connections | `/api/device-connections` | IoT configuration |
| Energy Alerts | `/api/energy-alerts` | Anomaly detection |

---

## ⚙️ Getting Started

### 📥 Clone the Repository

```bash
git clone <repository-url>
cd smart-home-backend

```
## ⚙️ Getting Started

### 🐳 Start Dependencies (PostgreSQL + MQTT)

```bash
docker compose up -d postgres mqtt
```


CI/CD

The project uses GitHub Actions:

Build Docker image
Push to Docker Hub
Deploy to server via SSH


Author

Sezim Orozobekova
Ala-Too International University
Computer Science Departmen
