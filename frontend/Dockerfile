# =============================================
# EC2 Front — Nginx sirviendo el HTML estático
# =============================================
FROM nginx:alpine

# Eliminamos la página por defecto de Nginx
RUN rm -rf /usr/share/nginx/html/*

# Copiamos nuestro frontend
COPY index.html /usr/share/nginx/html/index.html

# Configuración custom de Nginx (compresión, headers de seguridad, proxy)
COPY nginx.conf /etc/nginx/conf.d/default.conf

EXPOSE 80

HEALTHCHECK --interval=30s --timeout=5s \
  CMD wget -qO- http://localhost:80 || exit 1
