#!/usr/bin/perl -w

# Needed packages
use Getopt::Std;
use strict "vars";
use IO::Socket::INET;

# Global variables
my $VERBOSE = 0;
my $DEBUG = 0;

#####################
# handle flags and arguments
# Example: c == "-c", c: == "-c argument"
my $opt_string = 'hvd';
getopts( "$opt_string", \my %opt ) or usage() and exit 1;

# print help message if -h is invoked
if ( $opt{'h'} ){
    usage();
    exit 0;
}

$VERBOSE = 1 if $opt{'v'};
$DEBUG = 1 if $opt{'d'};

# main program content                                                                                                                       

$| = 1;

my $socket;
my $server_bind_port = 80;
my $server_bind_address = '127.0.0.1';
my $client_name = "VANGELIS";

$socket = IO::Socket::INET->new(
   PeerHost => $server_bind_address,
   PeerPort => $server_bind_port,
   Proto => 'tcp',
   ) or die "An error occured while trying to create socket: $!\n";

print "Connected to server at $server_bind_address:$server_bind_port\n";

my ($data_send, $data_received);

while(1){
   $data_received = <$socket>;
   chomp($data_received);
   print $data_received . "\n";
   print "Type the message you want to send to the server (type quit to exit): ";
   $data_send = <STDIN>;
   chomp($data_send);
   if($data_send eq "quit" or $data_send eq "exit"){
	$socket->close();
	exit(0);
   }
   print $socket "$client_name: $data_send\n";
}

#####################
# Helper routines

sub usage {
    # prints the correct use of this script
    print "Usage:\n";
    print "-h    Usage\n";
    print "-v    Verbose\n";
    print "-d    Debug\n";
}

sub verbose {
    print $_[0] if ( $VERBOSE );
}

sub debug {
    print $_[0] if ( $DEBUG );
}
