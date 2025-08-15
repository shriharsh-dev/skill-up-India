# Use an official Python runtime as a parent image
FROM python:3.13-slim

# Set the working directory in the container
WORKDIR /app

# Copy the dependencies file to the working directory
COPY requirements.txt .

# Install any needed packages specified in requirements.txt
# --no-cache-dir: Disables the cache to keep the image size down
# -r: Installs from the given requirements file.
RUN pip install --no-cache-dir -r requirements.txt

# Copy the rest of the application's code to the working directory
COPY . .

# Expose port 8000 to the outside world
EXPOSE 8000

# Command to run the application
# gunicorn is a production-ready WSGI server.
# -w 4: Spawns 4 worker processes.
# -k uvicorn.workers.UvicornWorker: Uses Uvicorn to handle the async events.
# main:app: The FastAPI app instance.
# --bind 0.0.0.0:8000: Binds to all network interfaces on port 8000.
CMD ["gunicorn", "-w", "4", "-k", "uvicorn.workers.UvicornWorker", "main:app", "--bind", "0.0.0.0:8000"]
