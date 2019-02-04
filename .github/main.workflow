workflow "Build on push" {
  on = "push"
  resolves = ["Build with maven"]
}

action "Build with maven" {
  uses = "docker://adoptopenjdk/maven-openjdk11:latest"
  runs = "mvn clean install -Dmaven.test.skip=true -P qa"
}
