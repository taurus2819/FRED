#!/bin/bash
dir=$1
# Delete from the given directory temporary map image files more than 5 minutes old
find $dir -maxdepth 1 -mmin +5 -name '*locmap*.png' -delete
