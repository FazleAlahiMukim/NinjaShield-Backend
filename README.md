# NinjaShield Backend

NinjaShield Backend is the core of the Data Leak Prevention (DLP) system, built using **Spring Boot**. It handles all data processing, policy enforcement, service management, and interactions with the MongoDB database. The backend ensures that sensitive data is monitored, policies are enforced, and notifications are sent in real-time.

## Contributors

- [@fazle]([https://github.com/fazle](https://github.com/FazleAlahiMukim))
- [@tonmoy]([https://github.com/tonmoy](https://github.com/TonmoyDaFulkopi))

## Other Project portions 

[@devicebackend](https://github.com/FazleAlahiMukim/NinjaShield-Backend) Web App Frontend
[@adminbacckend](https://github.com/FazleAlahiMukim/NinjaShield-AdminBackend) backend web-based application & 

## Features

### Policy Management
- Allows administrators to create, update, and delete data protection policies.
- Policies include rules for data classification, file categories, and actions (block, log, warn).
- Supports granular policy enforcement based on the device, data class, and file category.

### Data Classification and Rules Engine
- **Data Classification** system categorizes files based on content and predefined rules.
- A powerful **Rules Engine** evaluates file contents using regular expressions and triggers the appropriate actions.
- Policies can be linked to multiple rules, and each rule can contain multiple conditions that are processed using logical operators.

### Device Management
- Manages multiple devices with unique IDs, each having its own set of policies and monitoring services.
- Supports **service registry**, which tracks the status of different services (e.g., file scanning, clipboard monitoring) running on each device.
- Periodically checks and updates service statuses and triggers necessary actions based on the latest policy settings.

### File Upload Monitoring
- Intercepts file uploads from browsers and checks their content for sensitive information.
- Works in conjunction with the frontend to either block or allow the upload based on the active policies.

### Real-time Notifications
- Provides **real-time notifications** when an action (such as blocking a file upload or detecting sensitive data) is triggered.
- Uses a notification utility to deliver messages to the frontend or directly to the system tray, with options for custom icons and sounds.

### Service Scheduler
- A **Service Scheduler** that runs background tasks at regular intervals, including checking for policy updates and scanning services.
- Ensures that the backend services are always in sync with the latest policy settings.

### MongoDB Integration
- Uses **MongoDB** to store all relevant data, including devices, policies, rules, and service statuses.
- Provides seamless interaction between the backend and the database for creating, reading, updating, and deleting records.

### Security
- Ensures secure interactions between the frontend and backend, leveraging **browser cookies** for session management and authentication.
- Prevents unauthorized access to sensitive data and protects against data leakage.

## Technologies Used

- **Spring Boot**: The main framework for building the backend services.
- **MongoDB**: Used as the database for storing policies, devices, and service data.
- **Java 21**: Utilized for the backend codebase.
- **REST API**: For interaction with the frontend and other services.
- **.NET FRAMEWORK AND C#**: For interacting with the windows kernel

