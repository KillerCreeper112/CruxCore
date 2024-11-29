
version = "1.0"
plugins {
    alias(libs.plugins.paperweight)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin)
}

repositories{
    maven("https://jitpack.io")
}

dependencies {
    implementation("com.github.Maxlego08:zAuctionHouseV3-API:3.1.4.5")
}

dependencies {
    paperweight.paperDevBundle(libs.versions.paper)
    //Crux Modules
    implementation(files(
        "E:\\Plugins\\Crux2.0\\crux\\CruxMain\\build\\libs\\CruxMain-1.0.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxMenus\\build\\libs\\CruxMenus-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxPotions\\build\\libs\\CruxPotions-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxConfigs\\build\\libs\\CruxConfigs-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxEntities\\build\\libs\\CruxEntities-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxAttributes\\build\\libs\\CruxAttributes-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxEnchants\\build\\libs\\CruxEnchants-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxItems\\build\\libs\\CruxItems-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxBlocks\\build\\libs\\CruxBlocks-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxStructures\\build\\libs\\CruxStructures-1.0-dev-all.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxExternal\\build\\libs\\CruxExternal-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxAdvancements\\build\\libs\\CruxAdvancements-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxGeneration\\build\\libs\\CruxGeneration-1.0.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxWorlds\\build\\libs\\CruxWorlds-1.0.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxStats\\build\\libs\\CruxStats-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxForm\\build\\libs\\CruxForm-1.0-dev.jar",
    ))

    compileOnly(fileTree("libs") {
        include("*.jar")
    })
}
tasks{
    runServer{
        jvmArgs("-Xmx6000M", "-Xms6000M")
    }
}

paperweight.reobfArtifactConfiguration = io.papermc.paperweight.userdev.ReobfArtifactConfiguration.MOJANG_PRODUCTION

allprojects{

    plugins.apply("java")

    repositories {
        mavenCentral()
    }

    tasks.withType<JavaCompile> {
        options.encoding = "UTF-8"
    }

    tasks.withType<Test> {
        systemProperty("file.encoding", "UTF-8")
    }

    tasks.withType<Javadoc>{
        options.encoding = "UTF-8"
    }
}
