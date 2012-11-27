#!/bin/bash

# This script only works on linux and is aim to generate the PDF slides from our deck.js slides

rm target -rf
mkdir target
mkdir target/pdfs
mkdir target/printable-pdfs
arch=$(uname -m)

if [ "$arch" == 'x86_64' ]
then
    echo "### downloading phantomjs for 64bits"
    wget http://phantomjs.googlecode.com/files/phantomjs-1.7.0-linux-x86_64.tar.bz2
    tar -jxvf phantomjs-1.7.0-linux-x86_64.tar.bz2
    mv phantomjs-1.7.0-linux-x86_64 target/phantomjs
    rm phantomjs-1.7.0-linux-x86_64.tar.bz2
else
    echo "### downloading phantomjs for 32bits"
    wget http://phantomjs.googlecode.com/files/phantomjs-1.7.0-linux-i686.tar.bz2
    tar -jxvf phantomjs-1.7.0-linux-i686.tar.bz2
    mv phantomjs-1.7.0-linux-i686 target/phantomjs
    rm phantomjs-1.7.0-linux-i686.tar.bz2
fi

echo "### copy deck2pdf next to phantomjs"
cp deck2pdf/*.js target/phantomjs/bin

for i in *.html; do
 echo "### generating slides for $i"
 cd target/phantomjs/bin
 ./phantomjs deck2png.js "../../../$i"
 ./phantomjs png2pdf.js
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

