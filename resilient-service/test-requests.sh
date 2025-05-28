#!/bin/bash

# Function to make a single request
make_request() {
    curl -X POST \
         -H "Content-Type: application/json" \
         -d '{"data":"test-'$1'"}' \
         http://localhost:8080/api/process
    echo "\nRequest $1 completed"
}

# Launch 10 parallel requests
for i in {1..10}; do
    make_request $i &
done

# Wait for all background processes to complete
wait

echo "All requests completed"