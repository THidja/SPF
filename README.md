awk -v name="$NAME" -F'"' '/"name":/ {if ($4 ~ "\\y" name "\\y") print $4}'
