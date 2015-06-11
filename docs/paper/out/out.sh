#!/bin/sh
set -x
pandoc --toc --template=./ohnosequences.report.tex ../paper.md --smart -s --biblatex --bibliography ../refs.bib -o paper.tex
xelatex paper.tex
biber paper
xelatex paper.tex
