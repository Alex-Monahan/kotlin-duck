To build a local copy of DuckDB driver, check out git@github.com:duckdb/duckdb.git and run:
```
GEN=ninja BUILD_ICU=1 BUILD_JDBC=1 BUILD_JEMALLOC=1 BUILD_JSON=1  make
```

To install the driver into local Maven repository:
```
mvn install:install-file -Dfile=build/release/tools/jdbc/duckdb_jdbc.jar -DgroupId=org.duckdb  -DartifactId=duckdb_jdbc -Dversion=0.9.0-patched -Dpackaging=jar
```

To use the newly installed local Jar:
 - go to `build.gradle.kts`, comment out `implementation("org.duckdb:duckdb_jdbc:0.8.1")` and uncomment `implementation("org.duckdb:duckdb_jdbc:0.9.0-patched")`
 - in Main.kt, use `test0.9.0.db` as the test database to connect to



