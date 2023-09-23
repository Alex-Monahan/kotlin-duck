import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.databind.module.SimpleModule
import com.fasterxml.jackson.databind.ser.std.StdSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import java.sql.DriverManager
import java.sql.Statement
import java.util.*

class SqlArraySerializer : StdSerializer<java.sql.Array>(java.sql.Array::class.java) {
    override fun serialize(value: java.sql.Array, gen: JsonGenerator, provider: SerializerProvider) {
        gen.writeObject(value.array)
    }
}

fun main(args: Array<String>) {


    val ro_prop = Properties()
    ro_prop.setProperty("duckdb.read_only", "true")
    //val conn = DriverManager.getConnection("jdbc:duckdb:test0.8.1.db", ro_prop)
    val conn = DriverManager.getConnection("jdbc:duckdb:test0.9.0.db", ro_prop)

    val stmt: Statement = conn.createStatement()


    val mapper = jacksonObjectMapper();
    val module = SimpleModule();
    module.addSerializer(SqlArraySerializer());
    mapper.registerModule(module);

    var results = mutableListOf<Any>()
    var json: String?

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