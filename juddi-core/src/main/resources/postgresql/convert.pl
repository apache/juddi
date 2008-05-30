#!/usr/bin/perl
# Convert the create_database.sql script into something that can be
# piped into psql
#

open (CREATE, "create_database.sql");
open (OUTPUT, ">prefixless_database.sql");
while ($line = <CREATE>) {
	$line =~ s|^--||;
	$line =~ s|\${prefix}||;
	print OUTPUT $line;	
}
close (CREATE);
close (OUTPUT);

