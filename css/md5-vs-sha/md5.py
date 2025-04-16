import hashlib
import time

# Read input message from message.txt
with open("message.txt", "r") as file:
    str2hash = file.read().strip()

# Start time
start_time = time.time()

# Encoding message and hashing with MD5
result = hashlib.md5(str2hash.encode())

# End time
end_time = time.time()

# Calculate execution time in microseconds
execution_time_us = (end_time - start_time) * 1_000_000  # Convert to microseconds

# Printing the equivalent hexadecimal value
hash_value = result.hexdigest()
print("The MD5 of hash is:", hash_value)

# Write hash to hash.txt
with open("hash.txt", "w") as file:
    file.write(hash_value)

# Printing execution time in microseconds
print(f"Time taken to compute hash: {execution_time_us:.2f} µs")
