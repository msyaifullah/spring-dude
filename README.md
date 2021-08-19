# Getting Started

### Reference Documentation
For further reference, please consider the following sections:

sample file env : "env_file" 
```
MYSQL_HOST=192.168.99.106
MYSQL_USERNAME=root
MYSQL_PASSWORD=changeMe123
REDIS_HOST=192.168.99.106
REDIS_PORT=6379
MYSQL_HOST_STG=192.168.99.106
MYSQL_USERNAME_STG=root
MYSQL_PASSWORD_STG=changeMe123
MYSQL_HOST_PRD=192.168.99.106
MYSQL_USERNAME_PRD=root
MYSQL_PASSWORD_PRD=changeMe123
```

command for building images
```
$docker build -t testing-egg:123 . --build-arg BUILD_ENV=development
$docker run --env-file env_file -p 8080:8080 testing-egg:123 --network=host
```