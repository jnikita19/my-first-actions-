# app.py

# This file contains fake secrets for Gitleaks testing.
# In a real application, these should NEVER be hardcoded.
# They are here solely to demonstrate Gitleaks detection.

# Fake AWS Credentials
AWS_ACCESS_KEY_ID = "AKIAIOSFODNN7EXAMPLE"
AWS_SECRET_ACCESS_KEY = "wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY"

# Fake GitHub Personal Access Token
GITHUB_TOKEN = "ghp_abcdefghijklmnopqrstuvwxyz0123456789ABCDEF"

# Fake Generic API Key
API_KEY = "sk_live_XXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXXX"

# Fake Database Password
DB_PASSWORD = "mySuperSecureDbPassword123!"

def add(a, b):
    """
    Adds two numbers and prints them.
    This function is unrelated to the fake secrets above.
    """
    print(f"DEBUG: Using AWS Key ID: {AWS_ACCESS_KEY_ID}") # Example of using a "secret"
    return a + b

if __name__ == "__main__":
    print("The sum is:", add(10, 5))
    print(f"GitHub Token in use: {GITHUB_TOKEN}")
