# StormGate Development Rule

##Rules when working with git:
1. When starting to work on an issue, you must create a separate branch. For example if the issue is EC-1
then you should create branch EC-1.
2. When writing message for commit, the message should start with branch or issue name. For example if the branch
is EC-1 then commit's message should be "EC-1 bla bla....".
3. When merging feature branch to master, if the branch has many commits, you should use "git merge --no-ff".