# Code Contributions and Code Reviews Feedback

#### Focused Commits

Grade: Good

Feedback: The repository has a good number of commits, though at this point in the project there could've been more. Not every team member has code-related commits. A lot of the commits also affect a big numbeer of files. In the future you should strive for commits that only affect a small number of files and aggregate a coherent change to the system. Most commit messages are concise one liners, which clearly summarize the change, but the style of writing the commit messages is inconsistent between team members.


#### Isolation

Grade: Very Good

Feedback: The amount of successfully integrated MRs is good and they have an appropiate degree of focus (e.g. they are not convoluted or too big). Individual features are isolated into their own branches (e.g. "edit-cards"), but some branches should be rethought about (e.g. instead of "dev-sprint1", you could consider just using a "dev" branch for all sprints). 


#### Reviewability

Grade: Excellent

Feedback: Your MRs always have an appropriate focus that becomes clear from the title and the description. They cover a small number of related commits, and there aren't MRs with unresolved merge conflicts that have been left open for a while. Good job on this! 


#### Code Reviews

Grade: Excellent

Feedback: MRs receive a timely review and do not stay open for too long. Code reviews are an actual discussion with a back and forth of questions and answers, and the comments are constructive and goal oriented. Keep it up!  


#### Build Server

Grade: Very Good

Feedback: The build duration is reasonable (e.g. < 3 mins) and you have set the 10+ appropriate checkstyle rules. It would've been an "Excellent" if not for the fact that quite frequently the builds fail. However, it's good that they are fixed quickly, and usually in the next build.
