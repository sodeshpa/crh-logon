#!/usr/bin/env perl
use strict;
use warnings;

use Text::ParseWords;

sub trim($);

if ($#ARGV != 0) {
    printf "\nUsage: config.pl <config.csv>\n";
    exit;
}

my $config_file = $ARGV[0];

open(my $in, "<", $config_file) or die "Cannot open $config_file: $!";

while (<$in>) {
    my $line = $_;
    if (/^#/) {
        $line =~ s/[\s;"]+$//;
        print "$line\n";
    }
    else {
        $line =~ s/'/\\'/g;
        my @tokens = parse_line(";", 1, $line);
        if (@tokens < 8 or (@tokens > 8 and $tokens[8])) {
            print;
        }
        else {
            foreach (@tokens) {
                s/\\'/'/g;
            }

            my ($key, $type, $min, $max, $instance, $sensitive, $value, $description) = @tokens;

            $key = trim($key) . ";";

            $type = trim($type) . ";";

            $min = trim($min) . ";";
            $max = trim($max) . ";";
            $instance = trim($instance);
            $instance = "\"$instance\"" unless $instance =~ /null/;
            $instance .= ";";
            $sensitive = lc(trim($sensitive) . ";");
            $value = trim($value);
            $value = "\"$value\"" unless $value =~ /null/;
            $value .= ";";
            $description = trim($description);
            $description = "\"$description\"";

            printf("%-56s %-8s %-5s %-5s %-17s %-6s %-18s %s\n", $key, $type, $min, $max, $instance, $sensitive, $value, $description);
        }
    }
}

sub trim($)
{
    my $string = shift;
    $string =~ s/^[\s"]+//;
    $string =~ s/[\s"]+$//;
    return $string;
}


=head1 NAME

B<config.pl> - pretty prints config.csv to stdout

=head1 SYNOPSIS

config.pl <config.csv>

=head1 DESCRIPTION

Pretty prints input config file (CSV format) to standard output. It converts the original format to
standardized format to simplify diffing with repository versions of e.g. logon-persistence/etc/csv/refdata/config.csv

=cut
