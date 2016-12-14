#!/usr/bin/perl -w

use strict;
use DBI;

my $vf = './src/com/ccdev/famtree/Macro.java';
my $htmf = './view/index.html';
my $dbf = './resources/META-INF/persistence.xml';

if (! -f $vf) {
	print " Can not find version file\n";
	exit;
}

my $info = `cvs update -d 2>/dev/null`;

print "====>$info<====\n";
if ($info=~/M\s+src\//g) {
	print "Some thing is on modifying in src,Please check in first!\n";
	exit;
}

if ($info=~/M\s+view\//g) {
	print "Some thing is on modifying in view,Please check in first!\n";
	exit;
}

my $persist = `cat $dbf`;

if (!($persist=~/value=\"update\"/ig)) {
	print "$dbf :error!\n";
	exit;
}

my $s = `cat $vf`;

if (!($s=~/static final public String version = \"(\d)\.(\d+)\";/)) {
	print "Unkown version format!\n";
	exit;
}

if (!defined($1) || !defined($2)) {
	print "version error!\n";
	exit;
}
my $major=$1;
my $minor=$2;

if (!($s=~/static final public boolean PASSWORD_UNCHECK = [true|false]/)) {
	print "Can not enable password!\n";
	exit;
}


$minor = sprintf("%03d",$minor);
print "Last vesrion: $major\.$minor!\n";
$minor=$minor+1;
$minor = sprintf("%03d",$minor);
print "New vesrion: $major\.$minor!\n";
$s=~s/static final public boolean PASSWORD_UNCHECK = true/static final public boolean PASSWORD_UNCHECK = false/g;
if ($s=~s/static final public String version = \"(\d)\.(\d+)\";/static final public String version = \"$major\.$minor\";/g) {
	open FH,">$vf";
	print FH $s;
	close(FH);
}

my $htm = `cat $htmf`;

if ($htm=~s/ext-all-debug.js/ext-all.js/g) {
	open FH,">$htmf";
	print FH $htm;
	close(FH);
}

my $a =`ant arch`;
#my $a =`ant`;
print "===>$a<===\n";
if ($s=~s/static final public boolean PASSWORD_UNCHECK = false/static final public boolean PASSWORD_UNCHECK = true/g) {
	open FH,">$vf";
	print FH $s;
	close(FH);
}
if ($htm=~s/ext-all.js/ext-all-debug.js/g) {
	open FH,">$htmf";
	print FH $htm;
	close(FH);
}
`cvs commit -f -m \"increase version number\" $vf`;
`cvs tag 'v$major$minor'`;
exit;


