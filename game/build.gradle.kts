plugins {
    id("java-library")
    id("kotlin")
}

java {
    sourceCompatibility = JavaVersion.VERSION_1_7
    targetCompatibility = JavaVersion.VERSION_1_7
}

dependencies {
    implementation(Dependencies.coroutines_core)
    testImplementation(Dependencies.junit)
    testImplementation(Dependencies.kotlin_coroutine_test)
    testImplementation(Dependencies.mockito_kotlin)
}