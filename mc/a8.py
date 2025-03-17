def demo_a8(ki, rand):
    """
    Educational demonstration of GSM A8 algorithm concepts.
    This is a simplified version for learning purposes only.

    Args:
        ki (bytes): 128-bit subscriber key
        rand (bytes): 128-bit random challenge

    Returns:
        bytes: 64-bit session key Kc
    """
    from cryptography.hazmat.primitives import hashes
    from cryptography.hazmat.primitives.kdf.pbkdf2 import PBKDF2HMAC
    from cryptography.hazmat.backends import default_backend

    if len(ki) != 16 or len(rand) != 16:
        raise ValueError("Ki and RAND must be 16 bytes (128 bits) each")

    # In actual GSM, COMP128 or similar algorithms would be used
    # This is a simplified demonstration using PBKDF2
    kdf = PBKDF2HMAC(
        algorithm=hashes.SHA256(),
        length=8,  # 64-bit output
        salt=rand,
        iterations=1000,
        backend=default_backend(),
    )

    # Generate session key
    kc = kdf.derive(ki)
    return kc


# Example usage
def demo_usage():
    # Example values (in real systems these would be secret/random)
    ki = b"0123456789ABCDEF"  # 128-bit subscriber key
    rand = b"FEDCBA9876543210"  # 128-bit random challenge

    # Generate session key
    kc = demo_a8(ki, rand)

    print(f"Ki (subscriber key): {ki.hex()}")
    print(f"RAND (random challenge): {rand.hex()}")
    print(f"Kc (session key): {kc.hex()}")


if __name__ == "__main__":
    demo_usage()
