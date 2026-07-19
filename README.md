# 🔭 DB Visualizer

A full-stack SQL schema visualization tool that parses `.sql` files and renders interactive ER diagrams with table relationships, column details, and FK mapping.

**Live Demo** → [Db Visualizer](https://db-visualizer-frontend-sage.vercel.app/)

 *(add a screenshot here)*

---

## ✨ Features

- 📂 **Drag & drop** `.sql` file upload
- 🗄️ **Interactive graph** — drag, zoom, pan tables freely
- 🔗 **FK relationship lines** with column labels
- 🎨 **Color-coded tables** by domain (users, orders, payments, etc.)
- 🏷️ **Column details** — PK/FK badges, data types, NOT NULL indicators
- 📊 **Stats bar** — table count, relationship count, dialect detection
- 🌐 **Multi-dialect** — MySQL, PostgreSQL, SQLite, Oracle SQL

---

## 🛠️ Tech Stack

| Layer | Technology |
|---|---|
| Backend | Java 17 · Spring Boot 3 · JSQLParser |
| Frontend | React 18 · React Flow · Vite |
| Deploy | Render (backend) · Vercel (frontend) |
| Container | Docker · Docker Compose |

---

## 🚀 Local Development

### Prerequisites
- Java 17+
- Maven 3.8+
- Node.js 18+

### 1. Clone the repo
```bash
git clone https://github.com/Rushi117108/db-visualizer.git
cd db-visualizer
```

### 2. Run the backend
```bash
cd backend
mvn spring-boot:run
# API running at http://localhost:8080
```

### 3. Run the frontend
```bash
cd frontend
npm install --legacy-peer-deps
npm run dev
# App running at http://localhost:5173
```

### 4. Open the app
Go to `http://localhost:5173`, upload any `.sql` file and the graph renders instantly.

---

## 🐳 Docker (Full Stack)

```bash
docker-compose up --build
# Frontend → http://localhost
# Backend  → http://localhost:8080
```

---

## 📡 API Reference

| Method | Endpoint | Description |
|---|---|---|
| `POST` | `/api/upload` | Upload a `.sql` file (multipart/form-data) |
| `GET` | `/api/health` | Health check |

### Sample Response
```json
{
  "tables": [
    {
      "id": "users",
      "name": "users",
      "columns": [
        { "name": "id", "type": "INT", "primaryKey": true, "foreignKey": false },
        { "name": "email", "type": "VARCHAR(100)", "primaryKey": false, "foreignKey": false }
      ],
      "pkCount": 1,
      "fkCount": 0,
      "columnCount": 2
    }
  ],
  "relationships": [
    {
      "id": "orders_user_id_users_fk",
      "source": "orders",
      "sourceColumn": "user_id",
      "target": "users",
      "targetColumn": "id",
      "cardinality": "MANY_TO_ONE"
    }
  ],
  "dialect": "MySQL",
  "totalTables": 2,
  "totalRelationships": 1
}
```

---

## 📁 Project Structure

```
db-visualizer/
├── backend/                          # Spring Boot
│   ├── src/main/java/com/dbviz/
│   │   ├── controller/
│   │   │   └── SchemaController.java   # REST endpoints
│   │   ├── service/
│   │   │   └── SqlParserService.java   # DDL parsing logic
│   │   └── model/
│   │       ├── ColumnInfo.java
│   │       ├── TableNode.java
│   │       ├── SchemaEdge.java
│   │       └── SchemaGraph.java
│   ├── Dockerfile
│   └── pom.xml
│
├── frontend/                         # React + Vite
│   ├── src/
│   │   ├── components/
│   │   │   ├── TableNode.jsx           # Table card renderer
│   │   │   ├── FileUpload.jsx          # Drag & drop uploader
│   │   │   └── StatsBar.jsx            # Schema stats
│   │   ├── hooks/
│   │   │   └── useSchemaLayout.js      # Graph layout logic
│   │   └── App.jsx
│   ├── Dockerfile
│   └── vite.config.js
│
├── docker-compose.yml
└── README.md
```

---

## 🗺️ Roadmap

- [ ] Click table → highlight connected relationships
- [ ] Java / Spring Boot class diagram visualizer

---

## 🤝 Contributing

Pull requests welcome! Open an issue first to discuss what you'd like to change.

