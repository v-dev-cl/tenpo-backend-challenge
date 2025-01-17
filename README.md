# API Rest en Spring Boot
Proyecto de prueba para aplicar a Tenpo como Backend Software Engineer.

* Función para sumar 2 números con un porcentaje adicional obtenido desde un servicio externo.
* Función para obtener todas las llamadas API realizadas a la suma y otros puntos finales.
* Límite de tasa establecido a 3 RPM (llamadas por minuto).
* Utiliza Spring Reactive Repository para guardar y recuperar datos.
* Documentación de Swagger (http://localhost:8080/v3/api-docs).
* Para simular errores se utiliza una función aleatoria con un 30% de probabilidad de generar errores al solicitar el porcentaje adicional.

---

# Ejecutando la APP

### Ejecutar el proyecto con solo 1 replica
```
docker-compose up
```

### Ejecutar el proyecto con multiples replicas de Spring App
```
docker swam init
docker stack deploy --compose-file docker-compose.yaml my_stack

### Verify running services
docker service ls
```

---

### Endpoints

#### Endpoint de suma con error aleatorio
```
curl --location 'localhost:8080/calculator/calculate?number1=1&number2=1'
```

Status 200
```
2.2
```

Error Status 429 Rate limit
```
Rate limit exceeded
```

Error Status 500 cuando no logro obtener datos del servicio externo y no hay datos en cache
```
Could not fetch additional data
```

---

#### Endpoint history con el detalle de las llamas a los endpoints, sus params y respuestas o errores (solo se aplico con GET).
```
curl --location 'localhost:8080/endpoint-history?page=0&size=10'
```

Status 200
```json
{
    "content": [
        {
            "id": 1,
            "ip": "/172.27.0.1:33722",
            "endpoint": "/calculator/calculate",
            "parameters": "number1=1&number2=2&",
            "response": "",
            "error": "Could not fetch additional data",
            "created_at": "2025-01-17T19:00:59.386869",
            "update_at": "2025-01-17T19:00:59.386882"
        },
        {
            "id": 2,
            "ip": "/172.27.0.1:57050",
            "endpoint": "/calculator/calculate",
            "parameters": "number1=1&number2=2&",
            "response": "3.3000002",
            "error": null,
            "created_at": "2025-01-17T19:02:13.973207",
            "update_at": "2025-01-17T19:02:13.973233"
        },
        {
            "id": 3,
            "ip": "/172.27.0.1:57050",
            "endpoint": "/calculator/calculate",
            "parameters": "number1=1&number2=1&",
            "response": "2.2",
            "error": null,
            "created_at": "2025-01-17T19:02:17.309775",
            "update_at": "2025-01-17T19:02:17.309791"
        }
    ],
    "pageable": {
        "pageNumber": 0,
        "pageSize": 10,
        "sort": {
            "sorted": false,
            "unsorted": true,
            "empty": true
        },
        "offset": 0,
        "paged": true,
        "unpaged": false
    },
    "totalElements": 3,
    "totalPages": 1,
    "last": true,
    "numberOfElements": 3,
    "size": 10,
    "number": 0,
    "sort": {
        "sorted": false,
        "unsorted": true,
        "empty": true
    },
    "first": true,
    "empty": false
}
```
---

### Explicación técnica de las decisiones tomadas

* El rate limit lo implemente dentro de la app al ser un requerimiento, personalmente no optaría por usar rate limit dentro de la aplicación habiendo gateways o servicios externos que proveen gran cantidad de funciones y seguridad como Cloudflare o el mismo gateway/ingress en K8s, con eso sería posible abstraer la implementacion y obviar el lenguaje en caso de querer cambiar a otro.
* Se utilizó la estructura de archivos en carpetas según el tipo de archivo al ser el más común en proyectos Java, personalmente usaría uno en base a dominios como lo que se puede observar en el framework NestJs que agrupa todo lo relacionado por ejemplo Users/(User Module, User Controller, User Service, etc).
* Se utilizó un WebFilter para obtener los datos de los requests y respuestas para el caso de agregar más endpoints.

---

### Referencias
Principales sitios web usados:

* [Baeldung spring tutorials](https://www.baeldung.com/)
* [Medium Pageable requests](https://medium.com/@davidpetro/spring-webflux-and-pageable-be55104c234f)
* [Medium Rate Limit with redis](https://medium.com/@mbanaee61/efficient-rate-limiting-in-reactive-spring-boot-applications-with-redis-and-junit-testing-20675e73104a)

