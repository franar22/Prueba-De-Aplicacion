# Gestor de Tareas — Proyecto Full Stack

## Estructura del proyecto

```
project/
├── backend/                  ← Spring Boot (EC2 Back)
│   ├── src/main/java/com/app/tasks/
│   │   ├── TasksApiApplication.java
│   │   ├── model/Task.java
│   │   ├── repository/TaskRepository.java
│   │   ├── service/TaskService.java
│   │   ├── controller/TaskController.java
│   │   └── config/
│   │       ├── CorsConfig.java
│   │       └── GlobalExceptionHandler.java
│   ├── src/main/resources/application.properties
│   ├── pom.xml
│   └── Dockerfile
│
├── frontend/                 ← HTML + Nginx (EC2 Front)
│   ├── index.html
│   ├── Dockerfile
│   └── nginx.conf
│
├── database/                 ← MySQL (EC2 Data)
│   ├── Dockerfile
│   ├── init.sql
│   └── my.cnf
│
├── docker-compose.yml        ← Para desarrollo local
└── README.md
```

## Endpoints de la API

| Método | URL | Descripción |
|--------|-----|-------------|
| GET    | /api/tasks            | Listar todas las tareas |
| GET    | /api/tasks?status=X   | Filtrar por PENDIENTE / EN_PROGRESO / COMPLETADA |
| GET    | /api/tasks/{id}       | Obtener una tarea |
| POST   | /api/tasks            | Crear tarea |
| PUT    | /api/tasks/{id}       | Actualizar tarea |
| DELETE | /api/tasks/{id}       | Eliminar tarea |
| GET    | /api/tasks/health     | Estado del servicio |

## Desarrollo local (todo junto)

```bash
# Requiere Docker y Docker Compose instalados
docker-compose up --build

# Front:    http://localhost       (Nginx)
# Back API: http://localhost:3000  (Spring Boot)
# MySQL:    localhost:3306
```

## Despliegue en AWS

### EC2 Data (subred privada — 10.0.2.0/24)

```bash
# 1. Instalar Docker
sudo yum update -y
sudo yum install docker -y
sudo systemctl start docker
sudo systemctl enable docker
sudo usermod -aG docker ec2-user

# 2. Subir carpeta /database al servidor
scp -i tu-key.pem -r ./database ec2-user@<IP-PRIVADA-DATA>:~/

# 3. Construir y levantar MySQL
cd ~/database
docker build -t tasks-db .
docker run -d \
  --name tasks-db \
  --restart unless-stopped \
  -e MYSQL_ROOT_PASSWORD=rootpass \
  -e MYSQL_DATABASE=tasksdb \
  -e MYSQL_USER=taskuser \
  -e MYSQL_PASSWORD=taskpass \
  -v mysql_data:/var/lib/mysql \
  -p 3306:3306 \
  tasks-db
```

### EC2 Back (subred privada — 10.0.2.0/24)

```bash
# 1. Instalar Docker (igual que arriba)

# 2. Subir carpeta /backend
scp -i tu-key.pem -r ./backend ec2-user@<IP-PRIVADA-BACK>:~/

# 3. Construir y levantar Spring Boot
cd ~/backend
docker build -t tasks-api .
docker run -d \
  --name tasks-api \
  --restart unless-stopped \
  -e DB_HOST=<IP-PRIVADA-EC2-DATA> \
  -e DB_PORT=3306 \
  -e DB_NAME=tasksdb \
  -e DB_USER=taskuser \
  -e DB_PASSWORD=taskpass \
  -e ALLOWED_ORIGINS=http://<IP-PUBLICA-EC2-FRONT> \
  -p 3000:3000 \
  tasks-api
```

### EC2 Front (subred pública)

```bash
# 1. Instalar Docker (igual que arriba)

# 2. Editar index.html: cambiar la URL de la API
# Busca la línea:  const API = ...
# Cámbiala por:   const API = 'http://<IP-PRIVADA-EC2-BACK>:3000';

# 3. Subir carpeta /frontend
scp -i tu-key.pem -r ./frontend ec2-user@<IP-PUBLICA-FRONT>:~/

# 4. Construir y levantar Nginx
cd ~/frontend
docker build -t tasks-front .
docker run -d \
  --name tasks-front \
  --restart unless-stopped \
  -p 80:80 \
  tasks-front
```

## Security Groups necesarios en AWS

| SG | Regla de entrada | Desde |
|----|-----------------|-------|
| SG-Front | Puerto 80 (HTTP)  | 0.0.0.0/0 (internet) |
| SG-Front | Puerto 22 (SSH)   | Tu IP |
| SG-Back  | Puerto 3000       | SG-Front |
| SG-Back  | Puerto 22 (SSH)   | SG-Front (bastion) |
| SG-Data  | Puerto 3306       | SG-Back |

## Variables de entorno del Back

| Variable | Valor por defecto | Descripción |
|----------|------------------|-------------|
| DB_HOST  | db | Host de MySQL (IP privada del EC2 Data en AWS) |
| DB_PORT  | 3306 | Puerto MySQL |
| DB_NAME  | tasksdb | Nombre de la base de datos |
| DB_USER  | taskuser | Usuario MySQL |
| DB_PASSWORD | taskpass | Contraseña MySQL |
| ALLOWED_ORIGINS | * | Origen permitido para CORS (IP pública del Front) |
