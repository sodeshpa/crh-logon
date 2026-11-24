#!/usr/local/bin/perl -w

use strict;
use warnings;
use File::Basename;
use Cwd 'abs_path';
use Digest::SHA1 qw(sha1 sha1_hex sha1_base64);

my $argNum = $#ARGV + 1;

if ($argNum != 6 ) {
	print "\nUsage - 1: testUserUpsertSqlGenerator.pl user_name first_name last_name email pda_user_no.\nuser_name: MUST BE THE ADNOVUM LOGIN NAME! REASON: CONFIG IS GENERATED FOR THAT! EMAIL ADDRESS MUST BE THE ADNOVUM EMAIL ADDRESS.\n\n";
	exit;
}

my $loginprefix = $ARGV[0];
my $first = $ARGV[1];
my $last = $ARGV[2];
my $email = $ARGV[3];
my $instance = $ARGV[4];
my $pdauserno = $ARGV[5];
my $persistenceFolderPath;

print  "\nFOR ADNOVUM TESTDATA ONLY NEVER RUN FROM PRODUCTIVE PATCH! COPY THE GENERATED CONTENT TO TESTDATA USER SQL. Will add new user with this data:\nuser login prefix=$loginprefix , first name=$first, last name=$last, email=$email adnovum loginname (used for adnovum.dev.instance)=$instance.\n\n";


sub getLogonPersistencePath{
	#source code is located in logon/logon-persistence/etc/

	#relative path of current perl file
	my $filePathRelative = __FILE__;	

	#We know this file's location in relation the logon-persistence folder.
	my $currentFileFolderAbsolute = dirname(abs_path($filePathRelative));	

	my @folders = split('/',$currentFileFolderAbsolute);
	my $returnLogonPersistencePath="";	
	for (my $i=0; $i <= $#folders-1; $i++) {		
		$returnLogonPersistencePath .= "$folders[$i]/";
	}	

	return $returnLogonPersistencePath;
}



sub appendConfigCSV{
	my $CSVPath = $_[0]. 'etc/csv/testdata/config.csv';
	my $CSVFileHandle;
	open($CSVFileHandle,'>>',$CSVPath) or die "Could not open file $CSVFileHandle";

	print $CSVFileHandle 'mail.test.to;                                            String;  null; null; "adnovum.dev.',"$instance",'"; false; "',"$email",'"; "Email addresses to use in case test mode is enabled."',"\n";

	close $CSVFileHandle;
}

sub appendUserToCSV{
	my $CSVPath = $_[0] . 'etc/csv/testdata/user_person_driver_role.csv';
	my $CSVFileHandle;
	open($CSVFileHandle,'>>',$CSVPath) or die "Could not open file $CSVFileHandle";

		my $pda_user = $pdauserno;
		my $pda_pin = "SHA-1:" . sha1_base64(1234) . "=";

	if($argNum == 6){
		print "Adding new admin user.\n";
		print $CSVFileHandle ";$last",' (Logon Admin)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Logon Admin)@EA.CRH.NET;1;',"$loginprefix",' Logon Admin',";1234;;;;;;;;;en;;;;;;;1;;;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;1","\n";
		print $CSVFileHandle ";$last",' (User Admin)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(User Admin)@EA.CRH.NET;1;',"$loginprefix",' User Admin',";1234;;;;;;;;;en;;;;;;;1;;;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;2;2","\n";
		print $CSVFileHandle ";$last",' (Sales Admin)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Sales Admin)@EA.CRH.NET;1;',"$loginprefix",' Sales Admin',";1234;;;;;;;;;en;;;;;;;1;;;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;3;3","\n";
		print $CSVFileHandle ";$last",' (Road Admin)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Rail Admin)(Road Admin)@EA.CRH.NET;1;',"$loginprefix",' Road Admin',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;4;4","\n";
		print $CSVFileHandle ";$last",' (Rail Admin)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Call Center Agent)@EA.CRH.NET;1;',"$loginprefix",' Rail Admin',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;5;5","\n";
		print $CSVFileHandle ";$last",' (Call Center Agent)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Scheduling Road)@EA.CRH.NET;1;',"$loginprefix",' Order Entry',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;6;6","\n";
		print $CSVFileHandle ";$last",' (Scheduling Rail Display)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Scheduling Rail Display)@EA.CRH.NET;1;',"$loginprefix",' Scheduling Rail Display',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;10;10","\n";
		print $CSVFileHandle ";$last",' (Scheduling Rail TC OFF)',";$first;;;$email",';1;;;;2;;ADN82495;',"$loginprefix",'(Scheduling Rail TC OFF)@EA.CRH.NET;1;',"$loginprefix",' Scheduling Rail (TC OFF)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;8;8","\n";
		print $CSVFileHandle ";$last",' (Scheduling Rail TC ON)',";$first;;;$email",';1;;;;2;;ADN83275;',"$loginprefix",'(Scheduling Rail TC ON)@EA.CRH.NET;1;',"$loginprefix",' Scheduling Rail (TC ON)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;8;8","\n";
		print $CSVFileHandle ";$last",' (Schedule And Load Road)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Schedule And Load Road)@EA.CRH.NET;1;',"$loginprefix",' Schedule And Load Road',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;9;9","\n";
		print $CSVFileHandle ";$last",' (Schedule And Load Rail)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Schedule And Load Rail)@EA.CRH.NET;1;',"$loginprefix",' Schedule And Load Rail',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;10;10","\n";
		print $CSVFileHandle ";$last",' (Customer ADN Zumdorf)',";$first;;;$email",';1;;;;1;ADN73031;;',"$loginprefix",'(Customer ADN Zumdorf)@EA.CRH.NET;1;',"$loginprefix",' Customer (ADN Zumdorf)',";1234;","$pda_user","1;;;;1234;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;12;12","\n";
		print $CSVFileHandle ";$last",' (Customer ADN Albula)',";$first;;;$email",';1;;;;1;ADN73161;;',"$loginprefix",'(Customer ADN Albula)@EA.CRH.NET;1;',"$loginprefix",' Customer (ADN Albula)',";1234;","$pda_user","2;;;;1234;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;12;12","\n";
		print $CSVFileHandle ";$last",' (Customer ADN Untervaz)',";$first;;;$email",';1;;;;1;ADN800119;;',"$loginprefix",'(Customer ADN Untervaz)@EA.CRH.NET;1;',"$loginprefix",' Customer (ADN Untervaz)',";1234;","$pda_user","3;;;;1234;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;12;12","\n";
		print $CSVFileHandle ";$last",' (Driver)',";$first;;;$email",';1;;;;2;;ADNTEST -1;',"$loginprefix",'(Driver)@EA.CRH.NET;1;',"$loginprefix",' Driver',";1234;$pda_user;;1234;;1234;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;13;13","\n";
		print $CSVFileHandle ";$last",' (Pool Manager)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Pool Manager)@EA.CRH.NET;1;',"$loginprefix",' Pool Manager',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;16;16","\n";
		print $CSVFileHandle ";$last",' (Scheduling Comm Carrier 1)',";$first;;;$email",';1;;;;2;;1 (C);',"$loginprefix",'(Scheduling Comm Carrier 1)@EA.CRH.NET;1;',"$loginprefix",' Scheduling (Comm Carrier 1)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;7;7","\n";
		print $CSVFileHandle ";$last",' (Scheduling Comm Carrier 2)',";$first;;;$email",';1;;;;2;;(3) C;',"$loginprefix",'(Scheduling Comm Carrier 2)@EA.CRH.NET;1;',"$loginprefix",' Scheduling (Comm Carrier 2)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;7;7","\n";
		print $CSVFileHandle ";$last",' (Scheduling Exec Carrier 1)',";$first;;;$email",';1;;;;2;;(2) E;',"$loginprefix",'(Scheduling Exec Carrier 1)@EA.CRH.NET;1;',"$loginprefix",' Scheduling (Exec Carrier 1)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;7;7","\n";
		print $CSVFileHandle ";$last",' (Scheduling Exec Carrier 2)',";$first;;;$email",';1;;;;2;;(4) E;',"$loginprefix",'(Scheduling Exec Carrier 2)@EA.CRH.NET;1;',"$loginprefix",' Scheduling (Exec Carrier 2)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;7;7","\n";
		print $CSVFileHandle ";$last",' (Scheduling Mix Carrier 1)',";$first;;;$email",';1;;;;2;;(5) M;',"$loginprefix",'(Scheduling Mix Carrier 1)@EA.CRH.NET;1;',"$loginprefix",' Scheduling (Mix Carrier 1)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;7;7","\n";
		print $CSVFileHandle ";$last",' (Scheduling Mix Carrier 2)',";$first;;;$email",';1;;;;2;;6 (M);',"$loginprefix",'(Scheduling Mix Carrier 2)@EA.CRH.NET;1;',"$loginprefix",' Scheduling (Mix Carrier 2)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;7;7","\n";
		print $CSVFileHandle ";$last",' (Scheduling ADTEST -1)',";$first;;;$email",';1;;;;2;;ADNTEST -1;',"$loginprefix",'(Scheduling ADTEST -1)@EA.CRH.NET;1;',"$loginprefix",' Scheduling ADNTEST -1)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;7;7","\n";
		print $CSVFileHandle ";$last",' (Scheduling ADTEST -2)',";$first;;;$email",';1;;;;2;;ADNTEST -2;',"$loginprefix",'(Scheduling ADTEST -2)@EA.CRH.NET;1;',"$loginprefix",' Scheduling ADNTEST -2)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;1;0;0;0;0;0;0;0;0;0;0;0;0;0;0;7;7","\n";
		print $CSVFileHandle ";$last",' (Management)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Management)@EA.CRH.NET;1;',"$loginprefix",' Management',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;0;18;18","\n";
		print $CSVFileHandle ";$last",' (Sch. Disp. Comm Carrier 1)',";$first;;;$email",';1;;;;2;;1 (C);',"$loginprefix",'(Sch. Disp. Comm Carrier 1)@EA.CRH.NET;1;',"$loginprefix",' Sch. Disp. (Comm Carrier 1)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;20;20","\n";
		print $CSVFileHandle ";$last",' (Sch. Disp.Comm Carrier 2)',";$first;;;$email",';1;;;;2;;(3) C;',"$loginprefix",'(Sch. Disp.Comm Carrier 2)@EA.CRH.NET;1;',"$loginprefix",' Sch. Disp.(Comm Carrier 2)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;20;20","\n";
		print $CSVFileHandle ";$last",' (Sch. Disp.Exec Carrier 1)',";$first;;;$email",';1;;;;2;;(2) E;',"$loginprefix",'(Sch. Disp.Exec Carrier 1)@EA.CRH.NET;1;',"$loginprefix",' Sch. Disp.(Exec Carrier 1)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;20;20","\n";
		print $CSVFileHandle ";$last",' (Sch. Disp. Exec Carrier 2)',";$first;;;$email",';1;;;;2;;(4) E;',"$loginprefix",'(Sch. Disp. Exec Carrier 2)@EA.CRH.NET;1;',"$loginprefix",' Sch. Disp.(Exec Carrier 2)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;20;20","\n";
		print $CSVFileHandle ";$last",' (Sch. Disp. Mix Carrier 1)',";$first;;;$email",';1;;;;2;;(5) M;',"$loginprefix",'(Sch. Disp. Mix Carrier 1)@EA.CRH.NET;1;',"$loginprefix",' Sch. Disp.(Mix Carrier 1)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;20;20","\n";
		print $CSVFileHandle ";$last",' (Sch. Disp. Mix Carrier 2)',";$first;;;$email",';1;;;;2;;6 (M);',"$loginprefix",'(Sch. Disp. Mix Carrier 2)@EA.CRH.NET;1;',"$loginprefix",' Sch. Disp.  (Mix Carrier 2)',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;20;20","\n";
		print $CSVFileHandle ";$last",' (Sch. Disp. ADNTEST -1)',";$first;;;$email",';1;;;;2;;ADNTEST -1;',"$loginprefix",'(Sch. Disp. ADNTEST -1)@EA.CRH.NET;1;',"$loginprefix",' Sch. Disp. ADNTEST -1 Display',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;20;20","\n";
		print $CSVFileHandle ";$last",' (Sch. Disp. ADNTEST -2)',";$first;;;$email",';1;;;;2;;ADNTEST -2;',"$loginprefix",'(Sch. Disp. ADNTEST -2)@EA.CRH.NET;1;',"$loginprefix",' Sch. Disp. ADNTEST -2 Display',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;0;20;20","\n";
		print $CSVFileHandle ";$last",' (Rail Maintenance)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Rail Maintenance)@EA.CRH.NET;1;',"$loginprefix",' Rail Maintenance',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;0;21;21","\n";
		print $CSVFileHandle ";$last",' (External Scheduling)',";$first;;;$email",';1;;;;2;;ADNEXTERN;',"$loginprefix",'(External Scheduling)@EA.CRH.NET;1;',"$loginprefix",' External Scheduling',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;0;22;22","\n";
		print $CSVFileHandle ";$last",' (Lite Logon Admin)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Lite Logon Admin)@EA.CRH.NET;1;',"$loginprefix",' Lite Logon Admin',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;0;23;23","\n";
		print $CSVFileHandle ";$last",' (Lite User Admin)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Lite User Admin)@EA.CRH.NET;1;',"$loginprefix",' Lite User Admin',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;0;24;24","\n";
		print $CSVFileHandle ";$last",' (Simple Vehicle Admin)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Simple Vehicle Admin)@EA.CRH.NET;1;',"$loginprefix",' Simple Vehicle Admin',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;0;25;25","\n";
		print $CSVFileHandle ";$last",' (Del. Time. Adjustment)',";$first;;;$email",';1;;;;0;;;',"$loginprefix",'(Del. Time. Adjustment)@EA.CRH.NET;1;',"$loginprefix",' Delivery Time Adjustment',";1234;;;;;;;;;en;;;;;;;1;;;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;0;1;26;26","\n";

		print "Generated users. Default pda pin: 1234 default password: 1234.";

	} else {
		print "Provide all arguments. See usage: perl testUserU...";

	}
	
	close $CSVFileHandle;
}

sub generateSQL{
	#I. change the working directory, we are only interested in logon-persistence!
	my $folderChangeSuccessState = chdir $_[0];
	
	
	#II. Calling adnant clean dist in the current working directory, no need to rebuild the whole project.
	my $adnantSuccessState = system("/opt/adnhome/scripts/ant.sh clean dist");	
}

sub filterGeneratedSQL{

	#The working directory is already set. We know relatively where we want to put the filtered file.

	my $filteredSQLFileHandle;	
	open (unfilteredSqlFileHandle, './build/generated/gdi/csv2sql/initialdata/insert_user_person_driver_role.sql');
	
	#Copy header section.
		while (<unfilteredSqlFileHandle>) {
		chomp;
		
		if($_ =~  /show errors;/){
			print $filteredSQLFileHandle "$_\n";
			last;
		} else{
			print $filteredSQLFileHandle "$_\n";
		}	
	}

	#Filter for the currently added user only.
		while (<unfilteredSqlFileHandle>) {
		chomp;

		if($_ =~ /prompt/  && $_ =~ /$loginprefix/){
			print $filteredSQLFileHandle "\n";
			print $filteredSQLFileHandle "$_\n";
			
			#copy the next line too
			my $nextline = <unfilteredSqlFileHandle>;
			print $filteredSQLFileHandle "$nextline\n";
		}

		if($_ =~  /commit;/){
			print $filteredSQLFileHandle "$_\n";
			last;
		} 
	}

	#Copy footer section.
	while (<unfilteredSqlFileHandle>) {
		chomp;		
		print $filteredSQLFileHandle "$_\n";
	}

	close (unfilteredSqlFileHandle); 
	close ($filteredSQLFileHandle); 
}

#STEP 1 - get logon-persistence folder path.
$persistenceFolderPath = getLogonPersistencePath();

#STEP 2 - Append the csv files with the new user.
appendConfigCSV($persistenceFolderPath);
appendUserToCSV($persistenceFolderPath);

print "$loginprefix is added to the .csv files.\n";
print "Do you want to generate the upsert SQL code right now?(y/n)\n";
my $willRunSqlGeneration = <STDIN>;
chomp $willRunSqlGeneration;

if($willRunSqlGeneration eq 'y'){
	#STEP 3 - Generate the SQL code ased on the csv files.
	generateSQL($persistenceFolderPath);

	#STEP 4 - Filter the generated SQL code, to only include the added the user.
	filterGeneratedSQL($persistenceFolderPath);

	print "\n";
	print "\n";
	print "------------------------------------------------------------------------\n";
	print "\n";
	print "The generated SQl source code can be found here:\n";
	print "$persistenceFolderPath","build/generated/gdi/csv2sql/testdata/insert_user_person_driver_role.sql\n";
	print "!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!USE ONLY FOR TESTDATA USERS. REASON: USERS INSERTED BY SCRIPTS IN THE DB HAVE TO BE ACKNOWLEDGED BY CRH.!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n";
	print "\n";
	print "-----------------------------------------------------------------------\n";
	print "\n";
}
else{
	print "The SQL code can be generated later by calling 'adnant clean dist' in logon-persistence/\n";
}
