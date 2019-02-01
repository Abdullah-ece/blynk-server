workflow "Build on push" {
  on = "push"
  resolves = ["Build with maven"]
}

action "Build with maven" {
  uses = "LucaFeger/action-maven-cli@aed8a1fd96b459b9a0be4b42a5863843cc70724e"
  args = "-Dmaven.test.skip=true -P qa"
}

workflow "New workflow 1" {
  on = "push"
}
