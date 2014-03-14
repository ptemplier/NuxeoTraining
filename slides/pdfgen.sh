#!/bin/bash

# This script is aim to generate the PDF slides from our deck.js slides. It's working on Linux and Mac OSX
ARCH=$(uname -m)
SNAME=$(uname -s)
PHANTOMJS_VERSION=1.9.0

rm -rf target
mkdir target
mkdir target/pdfs
mkdir target/printable-pdfs

if [ "$SNAME" == "Darwin" ]; then
    echo "### downloading phantomjs for OSX"
    TARGET=phantomjs-$PHANTOMJS_VERSION-macosx
    FILE=$TARGET.zip
    CMD=unzip
elif [ "$ARCH" == 'x86_64' ]; then
    echo "### downloading phantomjs for 64bits"
    TARGET=phantomjs-$PHANTOMJS_VERSION-linux-x86_64
    FILE=$TARGET.tar.bz2
    CMD='tar -jxvf'
else
    echo "### downloading phantomjs for 32bits"
    TARGET=phantomjs-$PHANTOMJS_VERSION-linux-i686
    FILE=$TARGET.tar.bz2
    CMD='tar -jxvf'
fi

wget http://phantomjs.googlecode.com/files/$FILE
$CMD $FILE
mv $TARGET target/phantomjs
rm $FILE

echo "### copy deck2pdf next to phantomjs"
cp deck2pdf/*.js target/phantomjs/bin

for i in **/*.html; do
 echo "### generating slides for $i"
 cd target/phantomjs/bin
 ./phantomjs deck2png.js "../../../$i"
 ./phantomjs png2pdf.js
 parentDir=$(dirname "../../pdfs/$i.pdf")
 mkdir -p $parentDir
 mv output.pdf "../../pdfs/$i.pdf"
 cd ../../../
done;


echo "### copy deck2printable-pdf next to phantomjs"
cp deck2printable-pdf/*.js target/phantomjs/bin

for i in *.html; do
 echo "### generating printable slides for $i"
 cd target/phantomjs/bin
 ./phantomjs deck2printable-pdf.js "../../../$i" "../../printable-pdfs/$i.pdf"
 cd ../../../
done;

