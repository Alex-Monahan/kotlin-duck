import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.sql.DriverManager
import java.sql.Statement
import java.util.*
import java.nio.file.Files
import java.nio.file.Paths
import java.io.BufferedReader
import java.io.InputStreamReader


fun main(args: Array<String>) {

    // CREATE TABLE test(a INTEGER, b VARCHAR[], c MAP(VARCHAR, INTEGER)); 
    // INSERT INTO "test"(a,b,c) VALUES (1,'[hello, world]','{a=1, b=2}'), (2,'[foo, bar, baz]','{c=3}'), (3,'[x]','{y=9, z=-1}');

    // CREATE TABLE test as select 1::int as a, '[hello, world]'::varchar[] as b, '{a=1, b=2}'::map(varchar, integer) as c;

    // Need to recreate the test db since the JDBC driver I'm testing with is a different DuckDB version
    // val path = Paths.get("test2.db")
    // val result = Files.deleteIfExists(path)
    // println("deleteIfExists result " + result)

    // val path2 = Paths.get("test2.db.wal")
    // val result2 = Files.deleteIfExists(path2)
    // println("deleteIfExists result " + result2)

    // val process = Runtime.getRuntime().exec("""/Users/alex/Documents/DuckDB/duckdb_motherduck/build/release/duckdb test2.db -c "CREATE OR REPLACE TABLE test as select 1::int as a, '[hello, world]'::varchar[] as b, '{a=1, b=2}'::map(varchar, integer) as c" """)

    // val reader = BufferedReader(InputStreamReader(process.inputStream))

    // val line = reader.readText();
    // println(line)

    // val exitVal = process.waitFor()

    // println("exitVal: " + exitVal)

    // /Users/alex/Documents/DuckDB/duckdb_motherduck/build/release/duckdb test2.db -c "CREATE OR REPLACE TABLE test as select 1::int as a, '[hello, world]'::varchar[] as b, '{a=1, b=2}'::map(varchar, integer) as c"

    val ro_prop = Properties()
    ro_prop.setProperty("duckdb.read_only", "true") // Since we are creating the DB, we can't be read only anymore
    val conn = DriverManager.getConnection("jdbc:duckdb:test2.db", ro_prop)

    val stmt: Statement = conn.createStatement()


    val mapper = jacksonObjectMapper()
    var results = mutableListOf<Any>()
    var json: String?

    // val stmt1: Statement = conn.createStatement()
    // var create_sql: String?
    // var insert_sql: String?

    // Load the database
    // create_sql = "CREATE TABLE test(a INTEGER, b VARCHAR[], c MAP(VARCHAR, INTEGER))"
    // create_sql = "CREATE TABLE test as select 1::int as a, '[hello, world]'::varchar[] as b, '{a=1, b=2}'::map(varchar, integer) as c"
    // stmt1.execute(create_sql)

    // insert_sql = "INSERT INTO test (a,b,c) VALUES (1,'[hello, world]','{a=1, b=2}'), (2,'[foo, bar, baz]','{c=3}'), (3,'[x]','{y=9, z=-1}')"
    // stmt.execute(insert_sql)

    /////////////// get rows of first column - integer only
    stmt.executeQuery("SELECT a FROM test").use { rs ->
        while (rs.next()) {
            val row = (1..rs.metaData.columnCount).map { rs.getObject(it) }
            results.add(row)
        }
    }

    // WORKS! write json using jackson
    json = mapper.writeValueAsString(results)
    println(json)
    assert(json.equals("[[1],[2],[3]]"))


    /////////////// get map data
    results.clear()
    stmt.executeQuery("SELECT a, c FROM test").use { rs ->
        while (rs.next()) {
            val row = (1..rs.metaData.columnCount).map { rs.getObject(it) }
            results.add(row)
        }
    }

    // DOESN'T BREAK, BUT INCORRECT VALUE write json using jackson
    //   the map items are returned as strings:
    //   [[1,"{a=1, b=2}"],[2,"{c=3}"],[3,"{y=9, z=-1}"]]
    json = mapper.writeValueAsString(results)
    println(json)


    /////////////// get array data
    results.clear()
    stmt.executeQuery("SELECT a, b FROM test").use { rs ->
        while (rs.next()) {
            val row = (1..rs.metaData.columnCount).map { rs.getObject(it) }
            results.add(row)
        }
    }

    // FAILS! write json using jackson
    //   Exception in thread "main" com.fasterxml.jackson.databind.JsonMappingException:
    //   Unimplemented method 'getResultSet' (through reference chain: java.util.ArrayList[0]->java.util.ArrayList[1]->org.duckdb.DuckDBArray["resultSet"])
    json = mapper.writeValueAsString(results)
    println(json)




    println("==== complete ====")
}