#!/usr/bin/env bash

PROG_ROOT="$( cd "$( dirname "${BASH_SOURCE[0]}" )" && pwd )"
DB_DIR="DynamoDB"
DB_DOWNLOAD="https://s3.eu-central-1.amazonaws.com/dynamodb-local-frankfurt/dynamodb_local_latest.zip"
DB_COMPR="dynamodb_local_latest.zip"

# check if DynamoDB directory exists
# prompt user to download DynamoDB if not
if [ ! -d "$PROG_ROOT/$DB_DIR" ]; then
	echo -n "Directory '$DB_DIR' not found. Download DynamoDB now? [Y/n]: "
	read input
	if [ "$input" = "n" ] || [ "$input" = "N" ]; then
		echo "Failed to start database"
		exit 1
	fi

	# download DynamoDB with hash
	echo ""
	echo "Downloading latest DynamoDB release..."
	wget -q --show-progress "$DB_DOWNLOAD"
	wget -q --show-progress "$DB_DOWNLOAD.sha256"
	echo ""

	# verify hash	
	if sha256sum --quiet -c "$DB_COMPR.sha256"; then
		echo "Hash matched!"
	else
		echo "Hash did not match! Failed to start database"
		exit 1
	fi

	# extract into DB_DIR
	echo "Extracting DynamoDB"
	unzip -q "$DB_COMPR" -d "$PROG_ROOT/$DB_DIR"
	# clean up
	rm "$DB_COMPR" "$DB_COMPR.sha256"
	
	echo "Running DynamoDB"
	echo ""
fi

# run DynamoDB
java -Djava.library.path=./DynamoDBLocal_lib -jar "$DB_DIR/DynamoDBLocal.jar"

exit 0