#!/bin/sh
set -e

mkdir -p /data/db
for f in /opt/app/db/*.json; do
  name=$(basename "$f")
  if [ ! -f "/data/db/$name" ]; then
    cp "$f" "/data/db/$name"
  fi
done

exec java -cp "/opt/app/app.jar:/opt/app/lib/*" mephi.Main "$@"
