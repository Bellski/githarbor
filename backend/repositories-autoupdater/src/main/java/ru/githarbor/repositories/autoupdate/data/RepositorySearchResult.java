package ru.githarbor.repositories.autoupdate.data;

public class RepositorySearchResult {
    public class PageInfo {
        public boolean hasNextPage;
        public String endCursor;
    }

    public class Repository {
        public String nameWithOwner;
        public Branch branch;
    }

    public class Branch {
        public String name;
        public BranchTarget target;
    }

    public class BranchTarget {
        public String committedDate;
    }

    public PageInfo pageInfo;
    public Repository[] repositories;
}
