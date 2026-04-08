# =============================================
# EC2 Data — MySQL 8
# =============================================
FROM mysql:8.0

# Variables de entorno para MySQL.
# IMPORTANTE: en AWS estas se pasan como variables
# de entorno del contenedor, NO hardcodeadas aquí.
# Este archivo sirve solo como referencia de valores por defecto.
ENV MYSQL_ROOT_PASSWORD=${MYSQL_ROOT_PASSWORD:-rootpass}
ENV MYSQL_DATABASE=${MYSQL_DATABASE:-tasksdb}
ENV MYSQL_USER=${MYSQL_USER:-taskuser}
ENV MYSQL_PASSWORD=${MYSQL_PASSWORD:-taskpass}

# Script de inicialización: se ejecuta la primera vez
# que el contenedor arranca y la base de datos está vacía.
COPY init.sql /docker-entrypoint-initdb.d/init.sql

# Puerto estándar de MySQL
EXPOSE 3306

# Configuración de MySQL: optimizada para un contenedor pequeño
COPY my.cnf /etc/mysql/conf.d/my.cnf
