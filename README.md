# AWS_Image_reKognition_pipeline_JAVA
# AWS Rekognition Project

This project demonstrates an AWS Rekognition pipeline using two EC2 instances for Car Recognition and Text Recognition tasks. The project can be run manually on two machines or automated using Ansible across multiple EC2 instances.

## Project Breakdown

The project was broken down into a series of tasks to be completed in stages, as follows:

1. **Planning and Initial Setup**:
    - We identified the tools, libraries, and services required, such as AWS Rekognition, SQS, and S3, and prepared the environment by setting up EC2 instances, IAM roles, and Java projects.
    - Next, we defined the workflow where Machine 1 would handle Car Recognition and Machine 2 would handle Text Recognition. Each machine processes images from an S3 bucket, and the flow of information between machines is handled via SQS.

2. **Manual Execution on Two Machines**:
    - We performed the initial tests manually by running the JAR files on separate EC2 machines to confirm that the pipeline worked as expected.

3. **Automation with Ansible**:
    - Finally, we automated the execution using Ansible, allowing the tasks to be triggered and executed across EC2 instances with ease, facilitating reproducibility and scaling.

---

## Running the Project with 2 Machines

### Prerequisites
- IAM Role for EC2 with permissions for SQS, S3, and Rekognition
- AWS EC2 instances (2 machines)
- AWS key pair for SSH access
- Pre-configured Java and Maven on the EC2 machines
- JAR files for Car Recognition and Text Recognition

### Steps to Run

1. **Task 1: Car Recognition (Machine 1)**
    - SSH into Machine 1 using your key:
      ```bash
      ssh -i your-key.pem ec2-user@<machine1-public-ip>
      ```
    - Clone the repository and navigate to the project directory:
      ```bash
      git clone <repository-url>
      cd AWS-directory
      ```
    - Build the project and run the Car Recognition JAR:
      ```bash
      mvn clean package
      java -jar Car-Rekognition.jar
      ```

2. **Task 2: Text Recognition (Machine 2)**
    - SSH into Machine 2:
      ```bash
      ssh -i your-key.pem ec2-user@<machine2-public-ip>
      ```
    - Clone the repository and navigate to the project directory:
      ```bash
      git clone <repository-url>
      cd AWS-directory
      ```
    - Build the project and run the Text Recognition JAR:
      ```bash
      mvn clean package
      java -jar Text-Rekognition.jar
      ```

---

## Running the Project with Ansible

### Prerequisites
- 3 EC2 machines (1 control node and 2 worker nodes)
- Java and Maven set up on all three machines
- Ansible set up on all three machines
- AWS key configuration on all three machines
- Private IP addresses of the worker nodes

### Steps to Run

1. **Task 1: Build the Project**
    - SSH into the control node and clone the repository:
      ```bash
      ssh -i your-key.pem ec2-user@<control-node-public-ip>
      git clone <repository-url>
      cd AWS-directory
      ```
    - Build the project:
      ```bash
      mvn clean package
      ```

2. **Task 2: Configure and Run Ansible**
    - Enter the private IPs of the worker nodes into the `inventory.ini` file under the `[worker_nodes]` section.

    - Run the Ansible playbook:
      ```bash
      ansible-playbook -i inventory.ini ansible.yaml
      ```

3. **Task 3: Wait for Completion**
    - Wait for the execution to complete. This may take some time.
    - Once complete, check the output file in the same directory for the results.

---

## Additional Notes

- Ensure that all IAM roles are properly configured with necessary permissions for SQS, S3, and Rekognition services.
- Ensure that the EC2 instances are appropriately sized for the workload, especially if processing large datasets.
- Be sure to monitor the logs for both machines to verify that the services are running as expected.
