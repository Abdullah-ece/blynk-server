workflow "Build on push" {
  on = "push"
  resolves = ["Build with maven"]
}

action "Build with maven" {
  uses = "doom369/openjdk11-maven-git-docker@master"
  runs = "mvn clean install -Dmaven.test.skip=true -P qa"
}
