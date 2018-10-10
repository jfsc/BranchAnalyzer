This tool is part of contributions resulting of research about use of branches in DevOps environments from [ASSERT lab](http://assertlab.com/). 

Jose Fernando - jfsc@cin.ufpe.br

Vinicius Garcia - vcg@cin.ufpe.br

# Context
Branches are historically used to help develop teams on coordinating their parallel development through version control systems tools (ex.Git). BranchAnalyzer help developers to retrieve the frequency of textual conflicts by branch. 

# Objective
Retrieve the frequency of textual conflicts by branch. 

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


# References

[1] Bird, Christian, et al. "The promises and perils of mining git." Mining Software Repositories, 2009. MSR'09. 6th IEEE International Working Conference on. IEEE, 2009. [Link](http://cs.queensu.ca/~ahmed/home/teaching/CISC880/F10/papers/MiningGit_MSR2009.pdf).

[2] Eclipse. (2018). JGIT. Retrieved October 9, 2018, from https://www.eclipse.org/jgit/

[3] Accioly, P. R. G. (2018). UNDERSTANDING COLLABORATION CONFLICTS CHARACTERISTICS.
