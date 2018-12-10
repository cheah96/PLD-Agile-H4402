#!/bin/bash

echo '<?xml version="1.0" encoding="UTF-8" standalone="no"?>'
echo '<demandeDeLivraisons>'
echo '<entrepot adresse="190951407" heureDepart="8:0:0"/>'
for i in $(cat grandPlan.xml | cut -d '"' -f 2 | less | grep -v '<' | shuf | head -n $1)
do
    echo "<livraison adresse=\"$i\" duree=\"$((RANDOM%360))\"/>"
done
echo '</demandeDeLivraisons>'
