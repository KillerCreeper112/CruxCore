
version = "1.0"
plugins {
    alias(libs.plugins.paperweight)
    alias(libs.plugins.runPaper)
    alias(libs.plugins.shadow)
    alias(libs.plugins.kotlin)
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
        "E:\\Plugins\\Crux2.0\\crux\\CruxConfigs\\build\\libs\\CruxConfigs-1.0-dev.jar",
        "E:\\Plugins\\Crux2.0\\crux\\CruxStructures\\build\\libs\\CruxStructures-1.0-dev.jar",
    ))

    compileOnly(files("D:\\EssentialsX-2.21.0-dev+100-b392f03.jar"))
    compileOnly(files("D:\\EssentialsXChat-2.21.0-dev+102-fcf6e64.jar"))

    /*compileOnly(fileTree("libs") {
        include("*.jar")
    })*/
}
tasks{
    runServer{
        jvmArgs("-Xmx4000M", "-Xms4000M")
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
