#!/usr/bin/perl -w

my $old = shift;
my $new = shift;

list_dir("resources");

my $src = "src/com/fortinet/sample";
list_dir($src);

my $view = "view/js";
list_dir($view);

modify_file("d.pl");
modify_file("build.xml");
modify_file("view/index.html");
modify_file("view/build.xml");

$old = uc $old;
$new = uc $new;
modify_file("resources/META-INF/persistence.xml");

sub list_dir {
	my $dir = shift;
	print "\n---$dir---\n";
	my @fls = <$dir/*>;
	foreach $f (@fls) {
		if(-f $f) {
			print $f."\n";
			modify_file($f);
		} else {
			if(-d $f){list_dir($f);}
		}
	}
}

sub modify_file {
	my $f = shift;
	open FH, "<$f";
	my @lines = <FH>;
	close(FH);
	
	my $output = "";
	foreach $line (@lines) {
		my $s = $line;
		if($s=~s/$old/$new/g) {
			$output .= $s;
		} else {
			$output .= $line;
		}
	}
#	print $output;
	open FH, ">$f";
	print FH $output;
	close(FH);
}