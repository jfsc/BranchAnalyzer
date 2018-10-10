package com.fish.branchanalyzer.tests.helper;

/*
   Copyright 2013, 2014 Dominik Stadler

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
 */

import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;

import java.io.File;
import java.io.IOException;


public class CookbookHelper {

    public static Repository openJGitCookbookRepository() throws IOException {
        FileRepositoryBuilder builder = new FileRepositoryBuilder();
        return builder
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .build();
    }

    public static Repository createNewRepository() throws IOException {
        // prepare a new folder
        //File localPath = File.createTempFile("TestGitRepository", "");
        File localPath = new File("/Users/fernando.fish/Development/gitexperiments");
        System.out.println(localPath);
        if(!localPath.delete()) {
            throw new IOException("Could not delete temporary file " + localPath);
        }

        // create the directory
        Repository repository = FileRepositoryBuilder.create(new File(localPath, ".git"));
        repository.create();

        return repository;
    }

    public static Repository createNewRepositoryWithFile() throws IOException {
        Repository repo = createNewRepository();
        String dir = System.getProperty("user.dir");

        File source = new File(dir+ "/src/test/java/com/fish/branchanalyzer/tests/helper/data/Pojo.java");
        System.out.println(dir+ "/src/test/java/com/fish/branchanalyzer/tests/helper/data/Pojo.java");
        File dest = new File(repo.getDirectory().getParent());
        try {
            Git git = new Git(repo);
            FileUtils.copyFileToDirectory(source, dest);
            // run the add
            git.add()
                    .addFilepattern("Pojo.java")
                    .call();

            // and then commit the changes
            git.commit()
                    .setMessage("Pojo added for experimets")
                    .call();

        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException("Could not copy temporary file to " + dest);
        }catch (Exception e) {
            e.printStackTrace();
        }

        return repo;
    }
}
