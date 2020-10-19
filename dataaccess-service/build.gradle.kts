val ktor_version : String by project
val klaxon_version : String by project

dependencies {
    implementation("io.ktor:ktor-client-cio:$ktor_version")
    implementation("io.ktor:ktor-client-json:$ktor_version")
    implementation("io.ktor:ktor-client-jackson:$ktor_version")
    implementation("com.beust:klaxon:$klaxon_version")
}

