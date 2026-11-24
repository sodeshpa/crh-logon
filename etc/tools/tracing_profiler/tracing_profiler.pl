#!/usr/bin/perl -w
#
# Script description goes here

use strict;
use Getopt::Std;
use Time::Local;

# Global variables
my $verbose;
my $outfile;
my $maxlevel;
my $startmethod;
my $summaryonly;

# Debug info display routine
sub debug
{
  my $msg = $_[0];
  if (defined $.) {
    print STDERR "$.: $msg\n" if $verbose;
  } else {
    print STDERR "$msg\n" if $verbose;
  }
}


# Help display routine
sub usage
{
  print <<EOT;
Usage: parse_tracing.pl [-l xx] [-m method] < inputfile

Reads from stdin. The -l option indicates the maximal level to process
(0 = only top level).
The -m options lets you denote the method name at which processing should start
EOT
  exit(1);
}

# Process options
my %options = ();
getopts("hvo:l:m:s", \%options);
$outfile = defined($options{'o'}) ? $options{'o'} : "-";
$verbose = defined($options{'v'}) ? 1 : 0;
$maxlevel = defined($options{'l'}) ? $options{'l'} : 999999;
$startmethod = defined($options{'m'}) ? $options{'m'} : undef;
$summaryonly = defined($options{'s'}) ? 1 : 0;
if (defined($options{'h'})) { usage; }

my @stack = ();   # Stack for method names
my @tsstack = (); # Stack for timestamps
my %timepermethod = ();
my %callspermethod = ();

sub timesValueDescending {
    $timepermethod{$b} <=> $timepermethod{$a};
}


my $lvlspc = 4;  # Number of spaces to indent per level

# Main code here
while (<>)
{
    if (/(\d\d:\d\d:\d\d,\d\d\d) ([A-Z]+) +\[[^]]+\] \[([^]]+)\] (.*)$/) {
        my ($ts, $level, $thread, $entry) = ($1,$2,$3, $4);
#        print ("$ts: $level in thread $thread\n");
#        printf "Rest: %s\n", substr($entry, 0, 20);

        my ($h,$m,$s,$ms) = $ts =~ m/(\d\d):(\d\d):(\d\d),(\d\d\d)/;
        my $seconds = timelocal($s, $m, $h, 1,1,1);
        my $msts = $seconds * 1000 + $ms;


        if ($entry =~ /([A-Z][A-Za-z0-9_]*)\@0x([0-9a-f]+)\.([^(]+)\(/) {
            my ($class, $addr, $method) = ($1, $2, $3);
            my $fqn = $class . "." . $method;
            my $level = scalar(@stack);

            # Do not start stacking if wrong start method
            unless (@stack) {
                if ($startmethod and $method ne $startmethod) {
                    debug("Start method set to $startmethod but this method is $method; skipping");
                    next;
                }
            }

            push (@stack, $fqn);
            push (@tsstack, $msts);

            if ($level > $maxlevel) {
                next;
                # Don't print or count for statistics if level too big
            }

            print " " x ($level * $lvlspc) unless $summaryonly;
            if ($callspermethod{$fqn}) {
                my $c = $callspermethod{$fqn};
                $callspermethod{$fqn} = $c + 1;
            }
            else {
                $callspermethod{$fqn} = 1;
            }

            print "Entering $fqn\n" unless $summaryonly;
        }
        elsif ($entry =~ /^} = /) {

            # If the stack is empty, we've reached the level of the method we
            # started, so we skip processing.
            next unless @stack;

            my $fqn = pop @stack;
            my $startts = pop @tsstack;
            my $dt = $msts - $startts;
            my $level = scalar(@stack);

            if ($level > $maxlevel) {
                next;
            }

            unless ($summaryonly) {
                print " " x ($level * $lvlspc);
                print "Leaving $fqn (dT = $dt ms)\n";
            }

            if ($timepermethod{$fqn}) {
                my $dtsum = $timepermethod{$fqn};
                $timepermethod{$fqn} = $dtsum + $dt;
            }
            else {
                $timepermethod{$fqn} = $dt;
            }

            # If the stack is empty now, we stop because we don't want to process the top level method several times.
            last unless @stack;
        }
    }
}

print "\n\n" unless $summaryonly;
print "Summary:\n==================\n";
foreach my $mth (sort timesValueDescending (keys(%timepermethod))) {
    printf "%5dms / %4d calls: %s\n", $timepermethod{$mth},
    $callspermethod{$mth}, $mth;
}
