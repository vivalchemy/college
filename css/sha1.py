import hashlib
import time


def compute_sha1_hash(input_string: str) -> str:
    sha1 = hashlib.sha1()
    sha1.update(input_string.encode())
    return sha1.hexdigest()


def main():
    # Read input message from message.txt
    try:
        with open("message.txt", "r", encoding="utf-8") as file:
            message = file.read().strip()  # Strip leading/trailing whitespace
    except FileNotFoundError:
        print("Error: message.txt not found.")
        return

    # Start measuring execution time
    start_time = time.time()

    # Compute SHA-1 hash
    hash_value = compute_sha1_hash(message)

    # End time measurement
    end_time = time.time()

    # Calculate execution time in microseconds
    execution_time_us = (end_time - start_time) * 1_000_000

    # Write hash to hash.txt
    with open("hash.txt", "w", encoding="utf-8") as file:
        file.write(hash_value)

    # Output results
    print(f"SHA-1 Hash: {hash_value}")
    print(f"Time taken: {execution_time_us:.2f} µs")


if __name__ == "__main__":
    main()
