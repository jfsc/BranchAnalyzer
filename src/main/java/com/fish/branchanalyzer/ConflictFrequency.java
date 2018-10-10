package com.fish.branchanalyzer;;

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

import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.function.Consumer;

import com.fish.branchanalyzer.structure.Branch;
import org.eclipse.jgit.api.*;
import org.eclipse.jgit.api.errors.CheckoutConflictException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.diff.DiffEntry;
import org.eclipse.jgit.lib.ObjectReader;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevTree;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.revwalk.filter.CommitTimeRevFilter;
import org.eclipse.jgit.revwalk.filter.RevFilter;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.treewalk.AbstractTreeIterator;
import org.eclipse.jgit.treewalk.CanonicalTreeParser;

/**
 * Simple snippet which shows how to retrieve the diffs
 * between two commits
 */
public class ConflictFrequency {

    public static void main(String[] args) throws IOException, GitAPIException {
        FileRepositoryBuilder repositoryBuilder = new FileRepositoryBuilder();
        String repoFolderPath = "{REPOSITORY PATH}/.git";
        String abortMerge="git merge --abort";
        String resetPrevious="git reset --hard HEAD";
        Repository repository = repositoryBuilder.setGitDir(new File(repoFolderPath))
                .readEnvironment() // scan environment GIT_* variables
                .findGitDir() // scan up the file system tree
                .setMustExist(true)
                .build();

            List<Branch> branches = new ArrayList<>();
            Ref checkout;
            System.out.println("Executando estudo ...");
            try (Git git = new Git(repository)) {
                //Obtendo a lista de integrações por branch
                List<Ref> call = git.branchList().call();
                call = git.branchList().setListMode(ListBranchCommand.ListMode.REMOTE).call();
                for (Ref ref : call) {
                    if (!ref.isSymbolic()) {
                        // Alterei para conter apenas a branch develop
                        if (ref.getName().contains("develop")) branches.add(new Branch(ref));
                    }
                }
                Iterator<Branch> itBranches = branches.iterator();

                while(itBranches.hasNext()){
                    Branch bra = itBranches.next();
                    checkout = git.checkout()
                            .setName(bra.getBranchreference().getName()).call();

                    //System.out.println("Merges present in branch: " + bra.getBranchName());
                    int conflitcs = 0;
                    int total;
                    Date since = new SimpleDateFormat("yyyy-MM-dd").parse("2017-09-17");
                    Date until = new SimpleDateFormat("yyyy-MM-dd").parse("2017-09-28");
                    RevFilter between = CommitTimeRevFilter.between(since, until);
                    Iterable<RevCommit> logs = git.log().setRevFilter(RevFilter.ONLY_MERGES).setRevFilter(between).call();

                    for (RevCommit aCommit : logs) {

                        if (aCommit.getParentCount() >= 2 && !bra.getBranchName().equals("HEAD")) {
                            bra.getMerges().add(aCommit);
                            checkout = git.checkout()
                                    .setName(aCommit.getParent(0).getId().getName()).call();
                            MergeResult merge = git.merge().
                                    include(repository.resolve(aCommit.getParent(1).getId().getName())).
                                    setCommit(true).
                                    setFastForward(MergeCommand.FastForwardMode.FF).
                                    //setSquash(false).
                                            setMessage("Merged changes").
                                            call();

                            if(merge.getConflicts()!=null)
                                conflitcs++;
                            rawCMD(repoFolderPath, resetPrevious);

                        }


                    }
                    if (!bra.getBranchName().equals("HEAD")) {
                        System.out.println("BRANCH : " + bra.getBranchName());
                        System.out.println("TOTAL MERGES : " + bra.getMerges().size());
                        // System.out.println("N. AUTORES : autores distintos nas branches #TODO");
                        System.out.println("N. CONFLITOS : " + conflitcs);
                        //System.out.println("N. DENSIDADE : Quantidade de conflitos por merge conflitante#TODO");
                        //System.out.println("TIPOS DE CONFLITOS : porcentagem de cada tipo#TODO");
                    }

                }
            }catch (CheckoutConflictException e) {
                // TODO treat GitAPIException

                e.printStackTrace();
            }catch (GitAPIException e) {
                // TODO treat GitAPIException
                e.printStackTrace();
            }catch (ParseException pe){
                pe.printStackTrace();
            }
    }

    private static void listDiff(Repository repository, Git git, String oldCommit, String newCommit) throws GitAPIException, IOException {
        final List<DiffEntry> diffs = git.diff()
                .setOldTree(prepareTreeParser(repository, oldCommit))
                .setNewTree(prepareTreeParser(repository, newCommit))
                .call();

        System.out.println("Found: " + diffs.size() + " differences");
        for (DiffEntry diff : diffs) {
            System.out.println("Diff: " + diff.getChangeType() + ": " +
                    (diff.getOldPath().equals(diff.getNewPath()) ? diff.getNewPath() : diff.getOldPath() + " -> " + diff.getNewPath()));
        }
    }

    private static AbstractTreeIterator prepareTreeParser(Repository repository, String objectId) throws IOException {
        // from the commit we can build the tree which allows us to construct the TreeParser
        //noinspection Duplicates
        try (RevWalk walk = new RevWalk(repository)) {
            RevCommit commit = walk.parseCommit(repository.resolve(objectId));
            RevTree tree = walk.parseTree(commit.getTree().getId());

            CanonicalTreeParser treeParser = new CanonicalTreeParser();
            try (ObjectReader reader = repository.newObjectReader()) {
                treeParser.reset(reader, tree.getId());
            }

            walk.dispose();

            return treeParser;
        }
    }

    private static void rawCMD(String folder, String command){
        boolean isWindows = System.getProperty("os.name")
                .toLowerCase().startsWith("windows");
        ProcessBuilder builder = new ProcessBuilder();

        try {
            if (isWindows) {
                builder.command("cmd.exe", "/c", "dir");
            } else {
                builder.command("sh", "-c", "cd "+folder +"/../; "+command);
            }
            builder.directory(new File(System.getProperty("user.home")));
            Process process = null;
            process = builder.start();

        int exitCode = process.waitFor();
        assert exitCode == 0;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

}

