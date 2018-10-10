package com.fish.branchanalyzer.tests;

import com.fish.branchanalyzer.tests.helper.CookbookHelper;
import org.apache.commons.io.FileUtils;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.*;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.junit.Before;
import  org.junit.Test;

import java.io.File;
import java.io.IOException;

import static org.junit.Assert.*;

public class ConflictFrequencyTest {
    private Repository repo;

    @Before
    public void setUp() throws Exception {
        repo = CookbookHelper.createNewRepositoryWithFile();

    }

    @Test
    public void listModifiedFilesFrom2BRanchesTest(){
        try (Git git = new Git(repo)) {
            File repoPathPlace = repo.getWorkTree();
            String dir = System.getProperty("user.dir");
            File source = new File(dir+ "/src/test/java/com/fish/branchanalyzer/tests/helper/data/Pojo2.java");
            File dest = new File(repo.getDirectory().getParent()+"/Pojo.java");

            // run the create branch
            git.branchCreate()
                    .setName("devopsbranch")
                    .call();
            // check out branch "devopsbranch"
            Ref checkout = git.checkout().setName("devopsbranch").call();
            System.out.println("Result of checking out the branch: " + checkout);
            FileUtils.copyFile(source,dest);
            // run the add
            git.add()
                    .addFilepattern("Pojo.java")
                    .call();

            // and then commit the changes
            git.commit()
                    .setMessage("Pojo alterated added")
                    .call();

        } catch (AbortedByHookException e) {
            e.printStackTrace();
        } catch (WrongRepositoryStateException e) {
            e.printStackTrace();
        } catch (ConcurrentRefUpdateException e) {
            e.printStackTrace();
        } catch (NoHeadException e) {
            e.printStackTrace();
        } catch (UnmergedPathsException e) {
            e.printStackTrace();
        } catch (NoFilepatternException e) {
            e.printStackTrace();
        } catch (NoMessageException e) {
            e.printStackTrace();
        } catch (GitAPIException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}