package com.fish.branchanalyzer.structure;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.revwalk.RevCommit;

import java.util.ArrayList;

public class Branch {
    /**
     * List of commits.
     *
     * @return the list of commits from a branch
     */
    private ArrayList<RevCommit> commits;

    /**
     * JGIt branch Reference Representing the Branch.
     * -- SETTER --
     * Reference of Branch in JGIT
     *
     * @param branchrference The new value.
     */
    @Getter @Setter
    private Ref branchreference;

    @Getter @Setter
    private ArrayList<RevCommit> merges = new ArrayList<>();


    public Branch(Ref ref) {
        this.branchreference = ref;
    }

    public String getBranchName(){
        String branchRawName = branchreference.getName();
        int lastSlash = branchRawName.lastIndexOf("/");
        return branchRawName.substring(lastSlash+1,branchRawName.length());
    }
}
