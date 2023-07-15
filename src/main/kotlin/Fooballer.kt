import com.fasterxml.jackson.annotation.JsonProperty

data class Footballer(
    @JsonProperty("name")
    val name: String,
    @JsonProperty("position")
    val position: String,
    @JsonProperty("age")
    val age: Int,
    @JsonProperty("salary")
    val salary: Long
)
