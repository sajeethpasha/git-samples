import subprocess
import random
import string
from datetime import datetime

# Generate a random commit message
def generate_random_commit_message(length=10):
    # Generate a random string of letters and digits
    letters_and_digits = string.ascii_letters + string.digits
    random_part = ''.join(random.choice(letters_and_digits) for i in range(length))
    
    # Get the current timestamp
    timestamp = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
    
    # Combine random string with timestamp
    commit_message = f"{random_part} - {timestamp}"
    
    return commit_message

# Function to run git commands
def run_git_command(command):
    result = subprocess.run(command, capture_output=True, text=True)
    if result.returncode != 0:
        print(f"Error running command: {' '.join(command)}")
        print(result.stderr)
    else:
        print(result.stdout)

# Git commands
def git_add_commit_push():
    # Git add
    run_git_command(['git', 'add', '.'])

    # Commit with a random message combined with a timestamp
    commit_message = generate_random_commit_message()
    run_git_command(['git', 'commit', '-m', commit_message])

    # Push to the current branch
    run_git_command(['git', 'push'])

if __name__ == "__main__":
    git_add_commit_push()
