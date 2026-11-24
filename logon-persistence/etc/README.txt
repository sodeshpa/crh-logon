README is for 
testUserUpsertSqlGenerator.sh 
testUserUpsertSqlGenerator.pl

Purpose: 
Generate users for new adnovum team members. All kind with default password, real life scenario setting. Example: customer type user, scheduler type user, special use case user.

See also testdata/user_person_driver_role.csv
https://aww.adnovum.ch/confluence/display/CrhPegasus/How+to+add+new+users+to+Logon

To add new ADN team member:
1. delete meaningful content of the TESTdata/user_person_driver_role.csv (from row 6)
2. add the new team member to the testUserUpsertSqlGenerator.sh 
3. run testUserUpsertSqlGenerator.sh press no when the script asks you want to build
-> at this point testdata/user_person_driver_role.csv is re-generated and contains all the existing and the new team member's users
4. build in logon-persistence this will re-generate @TESTdata/insert_user_person_driver_role.sql
5. call the part referring to the new member on all adnovum dbs in order that the new member is created there

What to commit:
-all changes in testdata/user_person_driver_role.csv
-only the new user's settings in testdata/config.csv

The shell script is using the perl script testUserUpsertSqlGenerator.pl.

Usage (if not using the .sh script - can be used separately)
emoke@adnlt101:/local/projects/logon/logon-persistence/etc> perl testUserUpsertSqlGenerator.pl Orsie Orsie Alapi orsolya.alapi@adnovum.hu orsie -90
emoke@adnlt101:/local/projects/logon/logon-persistence/etc> perl testUserUpsertSqlGenerator.pl Emoke Emoke Bardoczy emoke@adnovum.hu emoke -88

perl testUserUpsertSqlGenerator.pl [login prefix] [first] [last] [real adn email address] [real instance name] [a unique pda number (negative number to avoid collisions) - choose the next available]
This will generate testdata user automatically executed on load prod dump and in the NB.

See also:
LOGON-8214 and it's linked tickets + confluence. Pay special attention: there is a difference in loading users to production and to adnovum integration. testdata folder contains only testdata.

Simple rule:
users added to csv residing in testdata folder -> will only exist on INT (testUserUpsertSqlGenerator) -> use for Adnovum INT to make sure a new member has all the users
users addded to csv residing in initialdata -> possibly loaded to production, for which Crh approval is necessary -> do not add anybody there unless Crh approved it

WARNING: DO NOT include the output in productive patches. There are users existing in production, but those reside in the initialdata/user_person_driver_role.csv. These generators are putting output to testdata/user_person_driver_role.csv, which is supposed to be run only at Adn.
As written before, please read.
https://aww.adnovum.ch/confluence/display/CrhPegasus/How+to+add+new+users+to+Logon
