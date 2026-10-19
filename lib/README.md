# Project Dependencies (lib/)

This directory contains the required third-party JARs used for compilation and runtime.

## 1. MySQL Connector/J (`mysql-connector-j-8.3.0.jar`)
- **Purpose**: Official MySQL JDBC Type-4 driver used for establishing database connections from Java Servlets and DAOs.
- **Runtime Location**: Placed in `src/main/webapp/WEB-INF/lib/` so Tomcat's WebAppClassLoader can load `com.mysql.cj.jdbc.Driver`.
- **Download Link**: [MySQL Connector/J Official Downloads](https://dev.mysql.com/downloads/connector/j/)

## 2. Servlet API (`servlet-api.jar`)
- **Purpose**: Provides `javax.servlet` and `javax.servlet.http` interface definitions (`HttpServlet`, `HttpServletRequest`, `HttpServletResponse`, `HttpSession`, `Filter`).
- **Compilation Only**: Provided by Apache Tomcat 9 (`<TOMCAT_HOME>/lib/servlet-api.jar`).
- **Important**: This JAR must **NOT** be copied into `WEB-INF/lib` because Tomcat's common classloader already provides it. Bundling it inside `WEB-INF/lib` would cause `ClassCastException` or classloader conflicts.

## 3. JSP API (`jsp-api.jar`)
- **Purpose**: Provides `javax.servlet.jsp` interfaces.
- **Provided by**: Apache Tomcat 9 (`<TOMCAT_HOME>/lib/jsp-api.jar`).

## Compilation Command:
```cmd
javac -cp "lib/*" -d src/main/webapp/WEB-INF/classes src/main/java/com/career/**/*.java
```
