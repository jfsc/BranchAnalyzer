Conflict Scenarios

Jose Fernando - jfsc@cin.ufpe.br
Vinicius Garcia - vcg@cin.ufpe.br

# Installation
Getting code
```bash
git clone https://github.com/jfsc/jgitconflict.git 
```
Open on intelliJ

#  Running study
Open file src/main/java/com/fish/branchanalyzer/ConflictFrequency.java 
```java
public static void main(String[] args) throws IOException, GitAPIException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        String repoFolderPath = "{REPOSITORY PATH}/.git";
...
```
Choose the intervall in the same file
```java
...
 RevWalk walk = new RevWalk(repository);
            Date since = new SimpleDateFormat("yyyy-MM-dd").parse("2017-09-17");
            Date until = new SimpleDateFormat("yyyy-MM-dd").parse("2017-09-28");
```
Finally, build and run.
